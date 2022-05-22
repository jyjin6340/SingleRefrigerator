//Mybmark.java

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
import org.techtown.sw_project.Requests.MypageRequest_mybmark;

import java.util.ArrayList;

public class Mybmark extends AppCompatActivity {
    String UserId;
    ListView listView;
    ArrayList<Mybmark.Info_b> InfoList = new ArrayList<Mybmark.Info_b>();
    ArrayList<String> RecipeList = new ArrayList<String>();
    ImageButton back_button;
    TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybmark);
        SharedPreferences auto = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

        UserId = auto.getString("Id",null);

        nothing = findViewById(R.id.textview_nothing);
        nothing.setVisibility(View.GONE);

        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = findViewById(R.id.listView_mybmark);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Mybmark.this, R.layout.list_textview, RecipeList);
        listView.setAdapter(adapter);

        Response.Listener<String> responseListener_mybmark = new Response.Listener<String>() {
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
                    JSONArray jsonArray = jsonObject.getJSONArray("Mybmark");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String name = item.getString("Recipe_name");
                        int id = Integer.valueOf(item.getString("Recipe_id"));
                        InfoList.add(new Mybmark.Info_b(id, name));
                        RecipeList.add(name);
//                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        MypageRequest_mybmark MypageRequest_mybmark_ = new MypageRequest_mybmark(UserId, responseListener_mybmark);
        RequestQueue queue = Volley.newRequestQueue(Mybmark.this);
        queue.add(MypageRequest_mybmark_);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Mybmark.this, Detail.class);
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