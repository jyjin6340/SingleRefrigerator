//Fridge_recommend.java

package org.techtown.sw_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.MyinfoRequest_get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Fridge_recommend extends Activity {

    ImageButton button_back;
    ArrayList<String> refrige_ingredient = new ArrayList<>();
    ArrayList<String> recipelist0 = new ArrayList<>();
    ArrayList<Fridge_recommend.Info2> infolist0 = new ArrayList<>();
    recommendAdapter adapter = null;
    TextView nothing;

    final int REQUEST_CODE = 1111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recommend_recipe);

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nothing=findViewById(R.id.textview_nothing);
        nothing.setVisibility(View.GONE);

        refrige_ingredient = getIntent().getStringArrayListExtra("input");

        ListView recommend_list = findViewById(R.id.listView_recommend);
        adapter = new recommendAdapter();
        recommend_list.setAdapter(adapter);

        recommend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //list 내 레시피가 클릭되었을 경우 새로운 view에서 상세레시피 띄워줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Fridge_recommend.this, Detail.class);
                int recipeid = infolist0.get(position).getId();
                intent.putExtra("id", recipeid);
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
        params.put("list", refrige_ingredient);

        RequestQueue queue2 = Volley.newRequestQueue(Fridge_recommend.this);
        String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Search_bylist.php"; //호스팅 주소 + php

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("res", response.toString());
                //Log.e("length", Integer.toString(response.length()));
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(Integer.toString(i));
                        String name = String.valueOf(jsonObject.get("Recipe_name"));
                        int id = Integer.valueOf(String.valueOf(jsonObject.get("Recipe_id")));
                        int count = Integer.valueOf(String.valueOf(jsonObject.get("Blowcount")));
                        int hitcount = Integer.valueOf(String.valueOf(jsonObject.get("Hitcount")));

                        infolist0.add(new Fridge_recommend.Info2(id, name, count, hitcount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //정렬
                Collections.sort(infolist0, new Comparator<Fridge_recommend.Info2>() {
                    @Override
                    public int compare(Fridge_recommend.Info2 i1, Fridge_recommend.Info2 i2) {
                        if(i1.getCount() == i2.getCount())
                            return i2.getHitcount() - i1.getHitcount();
                        return i1.getCount() - i2.getCount();
                    }
                });
                for(int i = 0; i < infolist0.size(); i++){
                    adapter.addInfo2(infolist0.get(i));
                    recipelist0.add(infolist0.get(i).getName());
                }
                if(infolist0.size()==0)
                {
                    recommend_list.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
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
        queue2.add(request);

    }

    public class Info2 {
        /* 아이템의 정보를 담기 위한 클래스 */

        int id;
        String name;
        int count;
        int hitcount;

        public Info2(int id, String name, int count, int hitcount) {
            this.id = id;
            this.name = name;
            this.count = count;
            this.hitcount = hitcount;
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

        public int getHitcount() {
            return hitcount;
        }

        public void setHitcount(int count) {
            this.hitcount = hitcount;
        }
    }


    class recommendAdapter extends BaseAdapter {
        ArrayList<Info2> reclist = new ArrayList<Info2>();

        @Override
        public int getCount() {
            return reclist.size();
        }

        public void addInfo2(Info2 info){
            reclist.add(info);
        }

        @Override
        public Object getItem(int position) {
            return reclist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return reclist.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Info2 info2 = reclist.get(position);

            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_recommend, viewGroup, false);

            TextView recipename = (TextView) convertView.findViewById(R.id.text_name);
            TextView hitcount = (TextView) convertView.findViewById(R.id.text_hit);
            TextView blowcount = (TextView) convertView.findViewById(R.id.text_blow);

            ImageView Hit = (ImageView) convertView.findViewById(R.id.image_hit);
            ImageView Blow = (ImageView) convertView.findViewById(R.id.image_blow);
            Hit.setVisibility(View.GONE);


            recipename.setText(info2.getName());
            hitcount.setText(Integer.toString(info2.getHitcount()));
            hitcount.setVisibility(View.GONE);
            blowcount.setText(Integer.toString(info2.getCount()));
            if(info2.getCount() == 0) {
                Hit.setVisibility(View.VISIBLE);
                hitcount.setVisibility(View.VISIBLE);
                blowcount.setVisibility(View.GONE);
                Blow.setVisibility(View.GONE);
            }

            return convertView;  //뷰 객체 반환
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Intent intent = getIntent();
            finish();
            overridePendingTransition(R.anim.anim_slide_down_enter, R.anim.anim_slide_down_exit);
            startActivity(intent);
        }
    }
}