package org.techtown.sw_project.Fragments;

import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import org.techtown.sw_project.Fridge_add;
import org.techtown.sw_project.Fridge_recommend;
import org.techtown.sw_project.Fridge_remove;
import org.techtown.sw_project.Login;
import org.techtown.sw_project.MainActivity;
import org.techtown.sw_project.R;
import org.techtown.sw_project.Requests.FridgeRequest_remove;
//import org.techtown.sw_project.Recommend_recipe;
import org.techtown.sw_project.Requests.FridgeRequest_list;
import org.techtown.sw_project.Requests.RecipeRequest_recom;
import org.techtown.sw_project.Requests.RecipeRequest_recom_num;
import org.techtown.sw_project.Search;
import org.techtown.sw_project.SearchBylist;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fragment_refrige extends Fragment {
    Button ingredient_add, ingredient_remove;
    String UserPW, UserEmail,UserName, UserId;
    ArrayList<String> refrige_ingredient = new ArrayList<>();
    ArrayList<Integer> refrige_ingredient_id = new ArrayList<>();
    ArrayList<Bitmap> refrige_ingredient_img = new ArrayList<>();
    ArrayList<Integer> ing_remove = new ArrayList<Integer>();
    ArrayList<Integer> recipe_id_sort = new ArrayList<>();
    ArrayList<Fragment_refrige.Info_b2> recipeList = new ArrayList<Fragment_refrige.Info_b2>();
    ImageButton recommendButton; //레시피 추천 버튼
    GridView gridview = null;
    GridViewAdapter adapter = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

        UserId = auto.getString("Id",null);
        UserName = auto.getString("Name", null);
        UserEmail = auto.getString("Email", null);
        UserPW = auto.getString("PW", null);

        refrige_ingredient.clear();
        refrige_ingredient_id.clear();
        ing_remove.clear();

        if(UserName == null) {
            MyAlertDialogFragment newDialogFragment = MyAlertDialogFragment.newInstance("로그인이 필요합니다.");
            newDialogFragment.show(getActivity().getFragmentManager(), "dialog");

            View v = inflater.inflate(R.layout.fragment_mypage_bflogin, container, false);
            Button Logreg = v.findViewById(R.id.button_log_reg);
            Logreg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                    getActivity().startActivityForResult(intent, 100);
                }
            });
            return v;
        }

        View v = inflater.inflate(R.layout.fragment_refrige, container, false);

        gridview = v.findViewById(R.id.gridview3);

        adapter = new GridViewAdapter();
        gridview.setAdapter(adapter);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("fridge ing", response);
                refrige_ingredient.clear();
                refrige_ingredient_id.clear();
                refrige_ingredient_img.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Fridge_ing_name");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Ingredient_name = item.getString("All_ing_name");
                        int Ingredient_id = Integer.valueOf(item.getString("All_ing_id"));
                        Bitmap bitmap_ =StringToBitmap(item.getString("All_ing_image"));
                        refrige_ingredient.add(Ingredient_name);
                        refrige_ingredient_id.add(Ingredient_id);
                        refrige_ingredient_img.add(bitmap_);
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        adapter.addItem(new Fragment_refrige.Ingredient(refrige_ingredient.get(i),refrige_ingredient_id.get(i),refrige_ingredient_img.get(i),false));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext().getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        FridgeRequest_list fridgeRequest_list_ = new FridgeRequest_list(UserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        queue.add(fridgeRequest_list_);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.changeItemSelect(i);
                adapter.notifyDataSetChanged();
                if(adapter.getSelect(i) == TRUE) {
                    ing_remove.add(Integer.valueOf(adapter.get_ing_num(i)));
                }
                else {
                    ing_remove.remove(Integer.valueOf(adapter.get_ing_num(i)));
                }
            }
        });

        ingredient_add = v.findViewById(R.id.ingredient_add);
        ingredient_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Fridge_add.class);
                getActivity().startActivityForResult(intent, 100);
            }
        });

        ingredient_remove = v.findViewById(R.id.ingredient_remove);
        ingredient_remove.setVisibility(View.INVISIBLE);
        ingredient_remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view){
                AlertDialog.Builder alt_bld=new AlertDialog.Builder(view.getContext());
                alt_bld.setMessage("선택한 재료를 삭제하시겠습니까?").setCancelable(false);
                alt_bld.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        for(int i=0;i<ing_remove.size();i++)
                        {
                            //Toast.makeText(getContext().getApplicationContext(), ing_remove.get(i).toString(), Toast.LENGTH_SHORT).show();
                            Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success");
                                        if (success) {//성공시
                                            //Toast.makeText(getContext().getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {//실패시
                                            //Toast.makeText(getContext().getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getContext().getApplicationContext(), "삭제 예외", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            };
                            FridgeRequest_remove fridgeRequest_remove_ = new FridgeRequest_remove(UserId,ing_remove.get(i).toString(),responseListener2);
                            RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                            queue.add(fridgeRequest_remove_);
                        }
                        Intent intent = new Intent(getActivity().getApplicationContext(), Fridge_remove.class);
                        getActivity().startActivityForResult(intent, 100);
                    }
                });
                alt_bld.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert= alt_bld.create();
                alert.setTitle("재료 삭제");
                alert.show();
            }
        });





        recommendButton = v.findViewById(R.id.recommend_recipe);
        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Fridge_recommend.class);
                intent.putExtra("input", refrige_ingredient);             //input이라는 이름으로 Search_recipe에 검색어 전달
                startActivity(intent);
            }
        });

        return v;

    }

    public void onCreate(){

    }

    public class Info2 {
        /* 아이템의 정보를 담기 위한 클래스 */

        int id;
        String name;
        int count;

        public Info2(int id, String name, int count) {
            this.id = id;
            this.name = name;
            this.count = count;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public class Info_b2 {

        int id;

        public Info_b2(int id) {
            this.id = id;
        }

        public int getId(){ return id; }
        public void setId(int id) { this.id = id; }

    }

    class GridViewAdapter extends BaseAdapter {
        ArrayList<Fragment_refrige.Ingredient> items = new ArrayList<Fragment_refrige.Ingredient>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Fragment_refrige.Ingredient item) {
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

        public int get_ing_num(int position) {
            return items.get(position).geting_num();
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
            final Fragment_refrige.Ingredient ingredient = items.get(position);

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
                ingredient_remove.setVisibility(View.VISIBLE);
            } else {
                //background.setBackgroundColor(Color.WHITE);
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
        int ing_num;
        Bitmap resId;
        boolean isSelected;

        public Ingredient(String name, int ing_num, Bitmap resId, boolean isSelected) {
            this.name = name;
            this.ing_num = ing_num;
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

        public int geting_num() { return ing_num; }
        public void seting_num(int ing_num) {
            this.ing_num = ing_num;
        }
    }

    public static class MyAlertDialogFragment extends DialogFragment{

        public static MyAlertDialogFragment newInstance(String title){

            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);

            String title = getArguments().getString("title");
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("MyLog", "확인 버튼이 눌림");
                        }
                    })
                    .create();
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