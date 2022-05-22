package org.techtown.sw_project;

import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Fragments.Fragment_refrige;
import org.techtown.sw_project.Requests.FridgeRequest_add;
import org.techtown.sw_project.Requests.FridgeRequest_add_list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fridge_add extends Activity {

    GridView gridview = null;
    GridViewAdapter adapter = null;

    Button add_finish;
    ImageButton back_button;
    String UserId;

    ArrayList<String> add_ingredient_list = new ArrayList<>();
    ArrayList<Integer> add_ingredient_list_id = new ArrayList<>();
    ArrayList<Bitmap> add_ingredient_list_img = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fridge_add);

        SharedPreferences auto = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        UserId=auto.getString("Id",null);

        gridview = findViewById(R.id.gridview2);
        adapter = new GridViewAdapter();

        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gridview.setAdapter(adapter);
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        Response.Listener<String> responseListener3 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                add_ingredient_list.clear();
                add_ingredient_list_id.clear();
                add_ingredient_list_img.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Fridge_ing_name");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Ingredient_name = item.getString("All_ing_name");
                        int Ingredient_id = Integer.valueOf(item.getString("All_ing_id"));
                        Bitmap bitmap_ =StringToBitmap(item.getString("All_ing_image"));
                        add_ingredient_list.add(Ingredient_name);
                        add_ingredient_list_id.add(Ingredient_id);
                        add_ingredient_list_img.add(bitmap_);
                        //Toast.makeText(getContext().getApplicationContext(), Ingredient_name, Toast.LENGTH_SHORT).show();
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        adapter.addItem(new Fridge_add.Ingredient(add_ingredient_list.get(i),add_ingredient_list_id.get(i),add_ingredient_list_img.get(i),false));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Fridge_add.this, "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        FridgeRequest_add_list FridgeRequest_add_list_ = new FridgeRequest_add_list(UserId, responseListener3);
        RequestQueue queue = Volley.newRequestQueue(Fridge_add.this);
        queue.add(FridgeRequest_add_list_);

        ArrayList<Integer> ing_add = new ArrayList<Integer>();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.changeItemSelect(i);
                adapter.notifyDataSetChanged();
                if(adapter.getSelect(i) == TRUE) {
                    ing_add.add(Integer.valueOf(adapter.get_ing_num(i)));
                }
                else {
                    ing_add.remove(Integer.valueOf(adapter.get_ing_num(i)));
                }
            }
        });

        add_finish=findViewById(R.id.Button_fridge_add_finish);
        add_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<ing_add.size();i++)
                {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {//성공시
                                    setResult(RESULT_OK);
                                    finish();

                                    return;
                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    FridgeRequest_add fridgeRequest_add_ = new FridgeRequest_add(UserId,ing_add.get(i).toString(),responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(fridgeRequest_add_);
                }
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE ) {
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    class GridViewAdapter extends BaseAdapter {
        ArrayList<Fridge_add.Ingredient> items = new ArrayList<Fridge_add.Ingredient>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Fridge_add.Ingredient item) {
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
            final Fridge_add.Ingredient ingredient = items.get(position);

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
            } else {
                background.setBackgroundColor(Color.alpha(255));
            }

            tv_name.setText(ingredient.getName());
            iv_icon.setImageBitmap(ingredient.getResId());

            //} else {
            //    View view = new View(context);
            //    view = (View) convertView;
            //}
            return convertView;  //뷰 객체 반환v
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
