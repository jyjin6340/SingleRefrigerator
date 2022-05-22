package org.techtown.sw_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.MypageRequest_mylike;
import org.techtown.sw_project.Requests.MypageRequest_myrecipe;

import java.util.ArrayList;

public class Myrecipe extends AppCompatActivity {
    String UserId;
    ListView listView;
    ArrayList<Myrecipe.Info_b> InfoList = new ArrayList<Myrecipe.Info_b>();
    ArrayList<String> RecipeList = new ArrayList<String>();
    ImageButton button_back;
    TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrecipe);
        SharedPreferences auto = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

        UserId = auto.getString("Id",null);

        nothing = findViewById(R.id.textview_nothing);
        nothing.setVisibility(View.GONE);

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = findViewById(R.id.listView_myrecipe);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Myrecipe.this, R.layout.list_textview, RecipeList);
        listView.setAdapter(adapter);

        Response.Listener<String> responseListener_myrecipe = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                if(response.length()==0){
                    listView.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("MyRecipe");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String name = item.getString("Recipe_name");
                        int id = Integer.valueOf(item.getString("Recipe_id"));
                        InfoList.add(new Myrecipe.Info_b(id, name));
                        RecipeList.add(name);
                        //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        MypageRequest_myrecipe MypageRequest_myrecipe_ = new MypageRequest_myrecipe(UserId, responseListener_myrecipe);
        RequestQueue queue = Volley.newRequestQueue(Myrecipe.this);
        queue.add(MypageRequest_myrecipe_);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Myrecipe.this, Detail.class);
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

    public class Info_b {

        int id;
        String name;

        public Info_b(int id, String name) {
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
