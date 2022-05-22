//Fragment_home.java

package org.techtown.sw_project.Fragments;

import static java.lang.Boolean.TRUE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import androidx.fragment.app.Fragment;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Detail;
import org.techtown.sw_project.R;
import org.techtown.sw_project.Requests.HomeRequest_ing;
import org.techtown.sw_project.SearchBylist;

import java.util.ArrayList;

public class Fragment_home extends Fragment {

    ImageButton search2;    //전체 레시피 검색 버튼

    GridView gridview = null;
    GridViewAdapter adapter = null;

    ArrayList<String> ingredient_list = new ArrayList<>();
    ArrayList<Bitmap> ingredient_list_img = new ArrayList<>();

    ListView recipe_list;
    ArrayList<String> RecipeList = new ArrayList<String>(); //랜덤 레시피 이름 저장
    ArrayList<Info_r> InfoList = new ArrayList<Info_r>();
    Random rand = new Random();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        gridview = v.findViewById(R.id.gridview2);
        adapter = new GridViewAdapter();

        gridview.setAdapter(adapter);
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        search2=v.findViewById(R.id.Button_search2);
        search2.setVisibility(View.INVISIBLE);

        Response.Listener<String> responseListener_ing = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ingredient_list.clear();
                ingredient_list_img.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("All_ing");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Ingredient_name = item.getString("All_ing_name");
                        Bitmap bitmap_ =StringToBitmap(item.getString("All_ing_image"));

                        ingredient_list.add(Ingredient_name);
                        ingredient_list_img.add(bitmap_);
                        //Toast.makeText(getContext().getApplicationContext(), Ingredient_name, Toast.LENGTH_SHORT).show();
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        adapter.addItem(new Fragment_home.Ingredient(ingredient_list.get(i),ingredient_list_img.get(i),false));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        HomeRequest_ing HomeRequest_ing_ = new HomeRequest_ing(responseListener_ing);
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        queue.add(HomeRequest_ing_);

        ArrayList<String> ing_search = new ArrayList<String>();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity().getApplicationContext(),"clicked", Toast.LENGTH_SHORT).show();
                adapter.changeItemSelect(i);
                adapter.notifyDataSetChanged();
                //gridview.setAdapter(adapter);
                if(adapter.getSelect(i) == TRUE) {
                    //Toast.makeText(getActivity().getApplicationContext(), adapter.getItemName(i) + " checked" + adapter.getSelect(i), Toast.LENGTH_SHORT).show();
                    ing_search.add(adapter.getItemName(i));
                }
                else {
                    //Toast.makeText(getActivity().getApplicationContext(), adapter.getItemName(i) + " unchecked" + adapter.getSelect(i), Toast.LENGTH_SHORT).show();
                    ing_search.remove(adapter.getItemName(i));
                }
            }
        });

        search2 = v.findViewById(R.id.Button_search2);
        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getActivity().getApplicationContext(), SearchBylist.class);
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchBylist.class);
                intent.putExtra("input", ing_search);             //input이라는 이름으로 Search_recipe에 검색어 전달
                startActivity(intent);
            }
        });

////////////////////////////////////
        recipe_list = v.findViewById(R.id.recipe_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.list_textview, RecipeList);
        adapter.clear();
        recipe_list.setAdapter(adapter);
        InfoList.clear();
        RecipeList.clear();


        HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
        RequestQueue queue3 = Volley.newRequestQueue(getContext().getApplicationContext());
        String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Home_recommend.php"; //호스팅 주소 + php

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(Integer.toString(i));
                        String name = String.valueOf(jsonObject.get("Recipe_name"));
                        int id = Integer.valueOf(String.valueOf(jsonObject.get("Recipe_id")));
                        InfoList.add(new Fragment_home.Info_r(id, name));
                        RecipeList.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());
                //handle errors
            }
        });
        queue3.add(request);

        recipe_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Detail.class);
                int recipeid = InfoList.get(position).getId();
                intent.putExtra("id", recipeid);
                getActivity().startActivityForResult(intent, 200);
                getActivity().overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        return v;
    }
    public class Info_r {

        int id;
        String name;

        public Info_r(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId(){ return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) {
            this.name = name;
        }
    }

    public class GridViewAdapter extends BaseAdapter {
        ArrayList<Ingredient> items = new ArrayList<Ingredient>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Ingredient item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getItemName(int position) {
            return items.get(position).getName();
        }

        public void changeItemSelect(int position){
            if(items.get(position).getSelected()==true)
                items.get(position).setSelected(false);
            else items.get(position).setSelected(true);
            return;
        }

        public boolean getSelect(int position){
            return items.get(position).getSelected();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Ingredient ingredient = items.get(position);

            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_list_item, viewGroup, false);

            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            Boolean isSel = ingredient.getSelected();

            LinearLayout background = (LinearLayout) convertView.findViewById(R.id.gridview_background);

            if(isSel == true){
                //tv_name.setBackgroundColor(Color.GREEN);
                background.setBackground(getResources().getDrawable(R.drawable.gridview_round));
                search2.setVisibility(View.VISIBLE);
            } else {
                background.setBackgroundColor(Color.alpha(255));
            }

            tv_name.setText(ingredient.getName());
            iv_icon.setImageBitmap(ingredient.getResId());

            //} else {
            //    View view = new View(context);
            //    view = (View) convertView;
            //}
            return convertView;  //뷰 객체 반환
        }
    }

    public class Ingredient {
        /* 아이템의 정보를 담기 위한 클래스 */

        String name;
        Bitmap resId;
        boolean isSelected;

        public Ingredient(String name, Bitmap resId, boolean isSelected) {
            this.name = name;
            this.resId = resId;
            this.isSelected = isSelected;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public boolean getSelected() { return isSelected; }
        public void setSelected(boolean isSelected) { this.isSelected = isSelected; }

        public Bitmap getResId() { return resId; }
        public void setResId(Bitmap resId) {
            this.resId = resId;
        }
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}