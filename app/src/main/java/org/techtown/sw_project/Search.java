package org.techtown.sw_project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.SearchRequest;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    ListView listView;
    CheckBox checkBox;
    String keyword;
    ImageButton button_back;
    TextView text_keyword, nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = findViewById(R.id.listView_search);
        checkBox = findViewById(R.id.CheckBox_Vegan_search);
        nothing = findViewById(R.id.textview_nothing);
        nothing.setVisibility(View.GONE);

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Intent intent = getIntent();
        keyword = getIntent().getStringExtra("keyword");
        text_keyword=findViewById(R.id.textView_searchkey);
        text_keyword.setText(keyword);

        ArrayList<Info> InfoList = new ArrayList<Info>(), NotveganInfoList = new ArrayList<Info>();
        ArrayList<String> RecipeList = new ArrayList<String>(), NotveganRecipeList = new ArrayList<String>();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Search.this, android.R.layout.simple_list_item_1, RecipeList);
        listView.setAdapter(adapter);
        //받아와야 하는 정보 : 레시피 이름, 아이디, 비건여부

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("INFO");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        int id = Integer.valueOf(item.getString("Recipe_id"));
                        String name = item.getString("Recipe_name");
                        int vegan = Integer.valueOf(item.getString("Recipe_vegan"));

                        InfoList.add(new Info(id, name, vegan));
                        RecipeList.add(name);
                    }
                    adapter.notifyDataSetChanged();
                    if(jsonArray.length()==0) {
                        listView.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        SearchRequest searchRequest_ = new SearchRequest(keyword, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Search.this);
        queue.add(searchRequest_);

        checkBox.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {   //비건만 뽑아야 함 ..
                    NotveganInfoList.clear();   //비건리스트 초기화
                    NotveganRecipeList.clear();
                    for (int i = 0; i < InfoList.size(); i++) {
                        if (InfoList.get(i).getVegan() == 0) {  //비건이 아닌 것을 찾아 notveganlist에 넣음
                            NotveganInfoList.add(InfoList.get(i));
                            NotveganRecipeList.add(RecipeList.get(i));
                        }
                    }
                    for (int i = 0; i < NotveganInfoList.size(); i++) {   //비건이 아닌 것을 adapter와 연결된 list에서 지움
                        InfoList.remove(NotveganInfoList.get(i));
                        RecipeList.remove(NotveganRecipeList.get(i));
                    }
                    adapter.notifyDataSetChanged();

                } else {    //비건 체크했다가 다시 체크 푼 경우 .. veganlist에 있는 레시피를 list에 다시 추가해줌
                    for (int i = 0; i < NotveganInfoList.size(); i++) {
                        InfoList.add(NotveganInfoList.get(i));
                        RecipeList.add(NotveganRecipeList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //list 내 레시피가 클릭되었을 경우 새로운 view에서 상세레시피 띄워줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Search.this, Detail.class);
                int recipeid = InfoList.get(position).getId();
                intent.putExtra("id", recipeid);
                startActivityResult.launch(intent);
                overridePendingTransition(R.anim.anim_slide_up_enter, R.anim.anim_slide_maintain);
            }
            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent intent = getIntent();
                            finish();
                            overridePendingTransition(R.anim.anim_slide_down_enter, R.anim.anim_slide_down_exit);
                            startActivity(intent);
                        }
                    }
            );
        });
    }


    public class Info {
        /* 아이템의 정보를 담기 위한 클래스 */

        int id;
        String name;
        int vegan;

        public Info(int id, String name, int vegan) {
            this.id = id;
            this.name = name;
            this.vegan = vegan;
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

        public int getVegan() {
            return vegan;
        }

        public void setVegan(int vegan) {
            this.vegan = vegan;
        }
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}