package org.techtown.sw_project.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Detail;
import org.techtown.sw_project.R;
import org.techtown.sw_project.Requests.RecipeRequest_popular;
import org.techtown.sw_project.Requests.RecipeRequest_recent;
import org.techtown.sw_project.Requests.RecipeRequest_tip;
import org.techtown.sw_project.Search;
import org.techtown.sw_project.SearchTip;
import org.techtown.sw_project.Upload;
import org.techtown.sw_project.Upload_tip;

import java.util.ArrayList;

public class Fragment_recipe extends Fragment {   //레시피 tab에서 나올 화면
    String UserId, UserName;
    AlertDialog dialog;
    ImageButton uploadbutton,button_recent,button_popular,button_honeytip, tipupload;
    ListView listView1, listView2;
    LinearLayout layout_recipe, layout_tip,layout_button_recipe, layout_button_mylike, layout_button_tip;
    ArrayList<Info_r> InfoList = new ArrayList<Info_r>();
    ArrayList<String> RecipeList = new ArrayList<String>();
    ArrayList<String> TipList = new ArrayList<String>();
    EditText editText;          //전체 레시피 검색 입력어
    ImageButton search1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);
        SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        UserName = auto.getString("Name", null);
        UserId = auto.getString("Id", null);
        InfoList.clear();
        RecipeList.clear();
        search1 = v.findViewById(R.id.Button_search1);
        editText = v.findViewById(R.id.editText_search);

        layout_button_recipe = v.findViewById(R.id.layout_button_recent);
        layout_button_mylike = v.findViewById(R.id.layout_button_mylike);
        layout_button_tip = v.findViewById(R.id.layout_button_honeytip);

        search1.setOnClickListener(new View.OnClickListener(){
            //전체 레시피 검색 버튼이 click될 경우
            @Override
            public void onClick(View v){
                String input = editText.getText().toString();   //입력받은 검색어를 string으로 바꿈
                if(input.length()==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("검색어를 입력해주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), Search.class);
                intent.putExtra("keyword", input);             //input이라는 이름으로 Search_recipe에 검색어 전달
                startActivity(intent);
                //Toast.makeText(getActivity().getApplicationContext(), input, Toast.LENGTH_LONG).show();
            }
        });

        listView1 = v.findViewById(R.id.listView_recipe);
        listView2 = v.findViewById(R.id.listView_tip);
        layout_recipe = v.findViewById(R.id.layout_recipe);
        layout_tip = v.findViewById(R.id.layout_recipe2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.list_textview, RecipeList);
        adapter.clear();
        listView1.setAdapter(adapter);

        ColorMatrix matrix = new ColorMatrix(); matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        button_recent = v.findViewById(R.id.button_recent);
        button_popular = v.findViewById(R.id.button_popular);
        button_honeytip = v.findViewById(R.id.button_honeytip);

        button_popular.setColorFilter(filter);
        button_honeytip.setColorFilter(filter);

        Response.Listener<String> responseListener_recent = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Recipe_recent");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String name = item.getString("Recipe_name");
                        int id = Integer.valueOf(item.getString("Recipe_id"));
                        InfoList.add(new Info_r(id, name));
                        RecipeList.add(name);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        RecipeRequest_recent RecipeRequest_recent_ = new RecipeRequest_recent(responseListener_recent);
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        queue.add(RecipeRequest_recent_);

        button_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoList.clear();
                RecipeList.clear();
                layout_tip.setVisibility(View.GONE); // 숨기기
                layout_recipe.setVisibility(View.VISIBLE); // 보이기

                button_recent.setColorFilter(null);
                button_popular.setColorFilter(filter);
                button_honeytip.setColorFilter(filter);

                Response.Listener<String> responseListener_recent = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Recipe_recent");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject item = jsonArray.getJSONObject(i);
                                String name = item.getString("Recipe_name");
                                int id = Integer.valueOf(item.getString("Recipe_id"));
                                InfoList.add(new Info_r(id, name));
                                RecipeList.add(name);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                };
                RecipeRequest_recent RecipeRequest_recent_ = new RecipeRequest_recent(responseListener_recent);
                RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                queue.add(RecipeRequest_recent_);
            }
        });


        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoList.clear();
                RecipeList.clear();
                layout_tip.setVisibility(View.GONE); // 숨기기
                layout_recipe.setVisibility(View.VISIBLE); // 보이기

                button_popular.setColorFilter(null);
                button_recent.setColorFilter(filter);
                button_honeytip.setColorFilter(filter);

                Response.Listener<String> responseListener_popular = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Recipe_popular");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject item = jsonArray.getJSONObject(i);
                                String name = item.getString("Recipe_name");
                                int id = Integer.valueOf(item.getString("Recipe_id"));
                                InfoList.add(new Info_r(id, name));
                                RecipeList.add(name);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                };
                RecipeRequest_popular RecipeRequest_popular_ = new RecipeRequest_popular(responseListener_popular);
                RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                queue.add(RecipeRequest_popular_);
            }
        });

        ArrayAdapter<String> adapter_tip = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.list_textview, TipList);
        adapter.clear();
        listView2.setAdapter(adapter_tip);

        button_honeytip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoList.clear();
                TipList.clear();
                layout_recipe.setVisibility(View.GONE); // 숨기기
                layout_tip.setVisibility(View.VISIBLE); // 보이기

                button_honeytip.setColorFilter(null);
                button_popular.setColorFilter(filter);
                button_recent.setColorFilter(filter);

                Response.Listener<String> responseListener_tip = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Tip");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject item = jsonArray.getJSONObject(i);
                                String name = item.getString("Tip_name");
                                int id = Integer.valueOf(item.getString("Tip_id"));
                                InfoList.add(new Info_r(id, name));
                                TipList.add(name);
                            }
                            adapter_tip.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                };
                RecipeRequest_tip RecipeRequest_tip_ = new RecipeRequest_tip(responseListener_tip);
                RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                queue.add(RecipeRequest_tip_);
            }
        });

        uploadbutton = v.findViewById(R.id.button_gotoupload);
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserName == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("로그인 후 업로드가 가능합니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                else {
                    Intent intent = new Intent(getActivity().getApplicationContext(),  Upload.class);
                    intent.putExtra("ID", UserId);
                    getActivity().startActivityForResult(intent, 300);
                }
            }
        });
        layout_tip.setVisibility(View.GONE);


        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Detail.class);
                int recipeid = InfoList.get(position).getId();
                intent.putExtra("id", recipeid);
                getActivity().startActivityForResult(intent, 300);
                getActivity().overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity().getApplicationContext(), SearchTip.class);
                int tipid = InfoList.get(position).getId();
                intent.putExtra("id", tipid);
                getActivity().startActivityForResult(intent, 300);
                getActivity().overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
        });

        tipupload = v.findViewById(R.id.button_uploadtip);
        tipupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserName == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("로그인 후 업로드가 가능합니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                else {
                    Intent intent = new Intent(getActivity().getApplicationContext(),  Upload_tip.class);
                    intent.putExtra("ID", UserId);
                    getActivity().startActivityForResult(intent, 300);
                }
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
}