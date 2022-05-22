package org.techtown.sw_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SearchBylist extends AppCompatActivity {
    ArrayList<String> user_ing_list = new ArrayList<>();     //사용자가 가진 재료 리스트
    private RequestQueue queue;

    ListView listview, listview2;
    ArrayAdapter<String> adapter, adapter2;
    ArrayList<String> recipelist0 = new ArrayList<>();
    ArrayList<String> recipelist1 = new ArrayList<>();
    ArrayList<Info2> infolist0 = new ArrayList<>(), infolist1 = new ArrayList<>();

    ImageButton back_button;
    TextView nothing1, nothing2;

    final int REQUEST_CODE = 1103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bylist);

        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nothing1 = findViewById(R.id.textview_nothing);
        nothing2 = findViewById(R.id.textview_nothing2);
        nothing1.setVisibility(View.GONE);
        nothing2.setVisibility(View.GONE);

        listview = findViewById(R.id.listview_hit0);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipelist0);
        listview.setAdapter(adapter);

        listview2 = findViewById(R.id.listview_hit1);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipelist1);
        listview2.setAdapter(adapter2);

        user_ing_list = getIntent().getStringArrayListExtra("input");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //list 내 레시피가 클릭되었을 경우 새로운 view에서 상세레시피 띄워줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //item이라는 변수들에 선택된 레시피의 이름, 걸리는 시간, 방법 저장
                Intent intent = new Intent(SearchBylist.this, Detail.class);
                int recipeid = infolist0.get(position).getId();
                //Toast.makeText(getApplicationContext(), "선택된 레시피 : " + Integer.toString(recipeid), Toast.LENGTH_SHORT).show();
                //각 변수의 값들을 Detail_recipe에 전달
                intent.putExtra("id", recipeid);
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //list 내 레시피가 클릭되었을 경우 새로운 view에서 상세레시피 띄워줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //item이라는 변수들에 선택된 레시피의 이름, 걸리는 시간, 방법 저장
                Intent intent = new Intent(SearchBylist.this, Detail.class);
                int recipeid = infolist1.get(position).getId();
                //Toast.makeText(getApplicationContext(), "선택된 레시피 : " + Integer.toString(recipeid), Toast.LENGTH_SHORT).show();
                //각 변수의 값들을 Detail_recipe에 전달
                intent.putExtra("id", recipeid);
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
        params.put("list", user_ing_list);
        for(int i=0; i<user_ing_list.size(); i++)
            Log.e("list", user_ing_list.get(i));
        queue = Volley.newRequestQueue(this);
        String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Search_bylist.php"; //호스팅 주소 + php

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("res", response.toString());
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(Integer.toString(i));
                        String name = String.valueOf(jsonObject.get("Recipe_name"));
                        int id = Integer.valueOf(String.valueOf(jsonObject.get("Recipe_id")));
                        int count = Integer.valueOf(String.valueOf(jsonObject.get("Blowcount")));
                        //Log.e("output ", name+" "+Integer.toString(id)+" "+Integer.toString(count));
                        if(count == 0)
                            infolist0.add(new Info2(id, name, count));
                        else
                            infolist1.add(new Info2(id, name, count));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //정렬
                Collections.sort(infolist1, new Comparator<Info2>() {
                    @Override
                    public int compare(Info2 i1, Info2 i2) {
                        if(i1.getCount() == i2.getCount())
                            return i1.getId() - i2.getId();
                        return i1.getCount() - i2.getCount();
                    }
                });
                for(int i = 0; i< infolist0.size(); i++){
                    recipelist0.add(infolist0.get(i).getName());
                }
                adapter.notifyDataSetChanged();
                if(infolist0.size()==0){
                    listview.setVisibility(View.GONE);
                    nothing1.setVisibility(View.VISIBLE);
                }
                for(int i = 0; i< infolist1.size(); i++){
                    recipelist1.add(infolist1.get(i).getName());
                }
                adapter2.notifyDataSetChanged();
                if(infolist1.size()==0){
                    listview2.setVisibility(View.GONE);
                    nothing2.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("error", error.getMessage());
                //handle errors
            }
        });
        queue.add(request);
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