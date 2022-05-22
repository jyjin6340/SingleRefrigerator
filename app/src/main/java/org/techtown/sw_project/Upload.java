package org.techtown.sw_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.NicknameRequest;
import org.techtown.sw_project.Requests.RecipeIDRequest;
import org.techtown.sw_project.Requests.UploadRequest_ingred;
import org.techtown.sw_project.Requests.UploadRequest_recipe;

import java.util.ArrayList;
import java.util.List;

public class Upload extends AppCompatActivity {
    private Context mContext;

    Button Add1_button, Add2_button, Upload_button;
    ImageButton Back_button;
    EditText Rname, Rcontent;
    CheckBox vegancheck;
    ArrayList<String> requiredname = new ArrayList<>(), requiredamount = new ArrayList<>();
    ArrayList<String> selectedname = new ArrayList<>(), selectedamount = new ArrayList<>();
    ListView listview1, listview2;
    ScrollView scrollView;
    CustomChoiceListViewAdapter adapter1 = new CustomChoiceListViewAdapter(), adapter2 = new CustomChoiceListViewAdapter();

    int Isvegan = 0;
    String UserId, RecipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        UserId = getIntent().getStringExtra("ID");

        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }); //뒤로가기 버튼

        Rname = findViewById(R.id.text_recipename); //레시피 이름

        vegancheck = findViewById(R.id.checkBox_vegan);
        vegancheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vegancheck.isChecked())
                    Isvegan = 1;
                else
                    Isvegan = 0;
            }
        }); //비건 여부 체크박스


        ArrayList<String> del_list1_name = new ArrayList<>(), del_list1_amount = new ArrayList<>();
        listview1 = (ListView)findViewById(R.id.list_ing1);
        listview1.setAdapter(adapter1);
        for(int i=0; i<requiredname.size(); i++){
            adapter1.addItem(requiredname.get(i), requiredamount.get(i));
        }
        scrollView = findViewById(R.id.upload_scrollview);
        listview1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listview1.isItemChecked(i) == true) {
                    //Toast.makeText(getApplicationContext(), requiredname.get(i) + " checked", Toast.LENGTH_SHORT).show();
                    del_list1_name.add(requiredname.get(i));
                    del_list1_amount.add(requiredamount.get(i));
                }
                else {
                    //Toast.makeText(getApplicationContext(), requiredname.get(i) + " unchecked", Toast.LENGTH_SHORT).show();
                    del_list1_name.remove(requiredname.get(i));
                    del_list1_amount.remove(requiredamount.get(i));
                }
            }
        });

        Button del_button1 = findViewById(R.id.button_del1);
        del_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for(int i=0; i<requiredname.size(); i++)
                   // Toast.makeText(getApplicationContext(), "sel : "+requiredname.get(i)+" "+requiredamount.get(i), Toast.LENGTH_SHORT).show();

                for(int i=0; i<del_list1_name.size(); i++){
                    int idx = requiredname.indexOf(del_list1_name.get(i));
                    adapter1.removeItem(idx);
                    requiredname.remove(del_list1_name.get(i));
                    requiredamount.remove(del_list1_amount.get(i));
                }
                del_list1_name.clear();
                del_list1_amount.clear();
                for(int i=0; i<requiredname.size(); i++)
                    listview1.setItemChecked(i,false);
                adapter1.notifyDataSetChanged();
            }
        });

        Add1_button = findViewById(R.id.button_add1);
        Add1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Upload.this, Upload_ing1.class);
                intent.putExtra("list", requiredname);
                startActivityResult.launch(intent);
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent intent = result.getData();
                                String tempIngred = intent.getStringExtra("name");
                                String tempAmount = intent.getStringExtra("amount");
                                //Toast.makeText(getApplicationContext(), tempIngred+" , "+tempAmount, Toast.LENGTH_SHORT).show();
                                adapter1.addItem(tempIngred, tempAmount);
                                requiredname.add(tempIngred);
                                requiredamount.add(tempAmount);
                                adapter1.notifyDataSetChanged();
                            } else {
                                //Toast.makeText(getApplicationContext(), "예외 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        });



        ArrayList<String> del_list2_name = new ArrayList<>(), del_list2_amount = new ArrayList<>();
        listview2 = (ListView)findViewById(R.id.list_ing2);
        listview2.setAdapter(adapter2);
        for(int i=0; i<selectedname.size(); i++){
            adapter2.addItem(selectedname.get(i), selectedamount.get(i));
        }
        listview2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listview2.isItemChecked(i) == true) {
                    //Toast.makeText(getApplicationContext(), selectedname.get(i) + " checked", Toast.LENGTH_SHORT).show();
                    del_list2_name.add(selectedname.get(i));
                    del_list2_amount.add(selectedamount.get(i));
                }
                else {
                    //Toast.makeText(getApplicationContext(), selectedname.get(i) + " unchecked", Toast.LENGTH_SHORT).show();
                    del_list2_name.remove(selectedname.get(i));
                    del_list2_amount.remove(selectedamount.get(i));
                }
            }
        });

        Button del_button2 = findViewById(R.id.button_del2);
        del_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<selectedname.size(); i++)
                    Toast.makeText(getApplicationContext(), "sel : "+selectedname.get(i)+" "+selectedamount.get(i), Toast.LENGTH_SHORT).show();

                for(int i=0; i<del_list2_name.size(); i++){
                    int idx = selectedname.indexOf(del_list2_name.get(i));
                    adapter2.removeItem(idx);
                    selectedname.remove(del_list2_name.get(i));
                    selectedamount.remove(del_list2_amount.get(i));
                }
                del_list2_name.clear();
                del_list2_amount.clear();
                for(int i=0; i<selectedname.size(); i++)
                    listview2.setItemChecked(i,false);
                adapter2.notifyDataSetChanged();
            }
        });

        Add2_button = findViewById(R.id.button_add2);
        Add2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Upload.this, Upload_ing2.class);
                startActivityResult.launch(intent);
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent intent = result.getData();
                                ArrayList<String> newnameArray = intent.getStringArrayListExtra("name");
                                ArrayList<String> newamountArray = intent.getStringArrayListExtra("amount");
                                for(int i=0; i<newnameArray.size(); i++) {
                                    adapter2.addItem(newnameArray.get(i), newamountArray.get(i));
                                    selectedname.add(newnameArray.get(i));
                                    selectedamount.add(newamountArray.get(i));
                                }
                                adapter2.notifyDataSetChanged();
                            } else {
                                //Toast.makeText(getApplicationContext(), "예외 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        });

        Rcontent = findViewById(R.id.text_recipecontent);

        Upload_button = findViewById(R.id.button_upload);
        Upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Rname : 요리이름
                //Isvegan : 비건여부
                //필수재료 리스트 : requiredname/requiredamount
                //선택재료 리스트 : selectedname/selectedamount
                //Rcontent : 내용

                if(Rname.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "요리 이름을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                    //요리이름 필수
                    return;
                }
                if(Rcontent.getText().toString().length() == 0){
                    //만드는 방법 필수
                    Toast.makeText(getApplicationContext(), "만드는 방법을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(requiredname.size()==0){
                    //필수재료 최소 1개
                    Toast.makeText(getApplicationContext(), "재료를 입력해야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                //String vegan = Integer.toString(Isvegan);
                //Toast.makeText(getApplicationContext(), Rname.getText().toString()+" "+vegan+"\n"+Rcontent.getText().toString(), Toast.LENGTH_SHORT).show();
                //for(int i=0; i<requiredname.size(); i++)
                //    Toast.makeText(getApplicationContext(), requiredname.get(i)+" "+requiredamount.get(i), Toast.LENGTH_SHORT).show();
                //for(int i=0; i<selectedname.size(); i++)
                //    Toast.makeText(getApplicationContext(), selectedname.get(i)+" "+selectedamount.get(i), Toast.LENGTH_SHORT).show();

                //REQUEST : Recipe에 Upload하고 Recipe id 받아와서 Recipe_ingredient에 순서대로 Upload

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {  //성공 시 Recipe id 얻어오고 ingredient 입력.
                                Response.Listener<String> responseListener = new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");

                                            if (success) {  //성공 시 Recipe id 얻어오고 ingredient 입력.
                                                RecipeID = jsonObject.getString("id");
                                                //Toast.makeText(getApplicationContext(), "RECIPE ID : " + RecipeID, Toast.LENGTH_SHORT).show();
                                                // RecipeID : RecipeID

                                                for(int i=0; i<requiredname.size(); i++){
                                                    int finalI = i;
                                                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                                                        @Override
                                                        public void onResponse(String response){
                                                            try{
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                boolean success = jsonObject.getBoolean("success");

                                                                if(success){
                                                                    if(selectedname.size()==0)
                                                                    { setResult(RESULT_OK);
                                                                    finish();
                                                                    }
                                                                    //Toast.makeText(getApplicationContext(), RecipeID+" "+requiredname.get(finalI)+" "+requiredamount.get(finalI), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "필수재료 입력 실패", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                                return;
                                                            }
                                                        }
                                                    };
                                                    UploadRequest_ingred uploadRequest_ingred = new UploadRequest_ingred(RecipeID, requiredname.get(i), requiredamount.get(i), "1", responseListener);
                                                    RequestQueue queue = Volley.newRequestQueue(Upload.this);
                                                    queue.add(uploadRequest_ingred);
                                                }
                                                for(int i=0; i<selectedname.size(); i++){
                                                    int finalI = i;
                                                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                                                        @Override
                                                        public void onResponse(String response){
                                                            try{
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                boolean success = jsonObject.getBoolean("success");

                                                                if(success){
                                                                    if(finalI == selectedname.size()-1)
                                                                    {
                                                                        setResult(RESULT_OK);
                                                                        finish();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "선택재료 입력 실패", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                                return;
                                                            }
                                                        }
                                                    };
                                                    UploadRequest_ingred uploadRequest_ingred = new UploadRequest_ingred(RecipeID, selectedname.get(i), selectedamount.get(i), "0", responseListener);
                                                    RequestQueue queue = Volley.newRequestQueue(Upload.this);
                                                    queue.add(uploadRequest_ingred);
                                                }
                                                return;
                                            } else {    //실패 시
                                                Toast.makeText(getApplicationContext(), "Recipe id 얻어오기 실패", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                };
                                //서버로 요청
                                RecipeIDRequest recipeIDRequest = new RecipeIDRequest(Rname.getText().toString(), Rcontent.getText().toString(), responseListener);
                                RequestQueue queue = Volley.newRequestQueue(Upload.this);
                                queue.add(recipeIDRequest);
                                return;
                            } else {    //실패 시
                                Toast.makeText(getApplicationContext(), "Recipe 입력 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                //서버로 요청
                UploadRequest_recipe uploadRequest_recipe = new UploadRequest_recipe(Rname.getText().toString(), Rcontent.getText().toString(), UserId, Integer.toString(Isvegan), responseListener);
                RequestQueue queue = Volley.newRequestQueue(Upload.this);
                queue.add(uploadRequest_recipe);
            }
        });
    }
}
