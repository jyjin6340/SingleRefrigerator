package org.techtown.sw_project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.Myrecipe_delete;
import org.techtown.sw_project.Requests.Myrecipe_edit;
import org.techtown.sw_project.Requests.Myrecipe_edit_ing_rm;
import org.techtown.sw_project.Requests.RecipeIDRequest;
import org.techtown.sw_project.Requests.RecipeRequest_recipe;
import org.techtown.sw_project.Requests.RecipeRequest_recipe_ing;
import org.techtown.sw_project.Requests.UploadRequest_ingred;
import org.techtown.sw_project.Requests.UploadRequest_recipe;

import java.util.ArrayList;

public class My_Detail_edit extends AppCompatActivity {
    String UserName, UserId;
    String recipe_name,howtomake;
    Integer recipe_id;
    EditText text_recipename,recipe3;
    CheckBox vegancheck_edit;
    Button Edit_finish,Add1_button,Add2_button;
    ImageButton Back_button;
    int Isvegan = 0;

    ArrayList<String> Recipe_ing_name = new ArrayList<>();
    ArrayList<String> Recipe_ing_amount = new ArrayList<>();
    ArrayList<Integer> Recipe_ing_required = new ArrayList<>();
    ArrayList<String> del_list1_name = new ArrayList<>(), del_list1_amount = new ArrayList<>();

    ArrayList<String> requiredname = new ArrayList<>(), requiredamount = new ArrayList<>();
    ArrayList<String> selectedname = new ArrayList<>(), selectedamount = new ArrayList<>();
    ListView listview1, listview2;
    ScrollView scrollView;
    CustomChoiceListViewAdapter adapter1 = new CustomChoiceListViewAdapter(), adapter2 = new CustomChoiceListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail_edit);

        SharedPreferences auto = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

        UserId=auto.getString("Id",null);
        UserName = auto.getString("Name", null);

        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        recipe_id = intent.getIntExtra("id", 0);

        vegancheck_edit = findViewById(R.id.checkBox_vegan_edit);
        vegancheck_edit.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vegancheck_edit.isChecked())
                    Isvegan = 1;
                else
                    Isvegan = 0;
            }
        });

        listview1 = (ListView)findViewById(R.id.list_ing1_edit);
        listview1.setAdapter(adapter1);

        scrollView = findViewById(R.id.my_detail_edit_scrollview);
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

        Button del_button1 = findViewById(R.id.button_del1_edit);
        del_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for(int i=0; i<requiredname.size(); i++)
                    //Toast.makeText(getApplicationContext(), "sel : "+requiredname.get(i)+" "+requiredamount.get(i), Toast.LENGTH_SHORT).show();

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

        Add1_button = findViewById(R.id.button_add1_edit);
        Add1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(My_Detail_edit.this, Upload_ing1.class);
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
                                Toast.makeText(getApplicationContext(), "예외 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        });

        ArrayList<String> del_list2_name = new ArrayList<>(), del_list2_amount = new ArrayList<>();
        listview2 = (ListView)findViewById(R.id.list_ing2_edit);
        listview2.setAdapter(adapter2);

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

        Button del_button2 = findViewById(R.id.button_del2_edit);
        del_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for(int i=0; i<selectedname.size(); i++)
                    //Toast.makeText(getApplicationContext(), "sel : "+selectedname.get(i)+" "+selectedamount.get(i), Toast.LENGTH_SHORT).show();

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

        Add2_button = findViewById(R.id.button_add2_edit);
        Add2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(My_Detail_edit.this, Upload_ing2.class);
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
                                Toast.makeText(getApplicationContext(), "예외 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        });

        Edit_finish=findViewById(R.id.button_edit_finish);
        Edit_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alt_bld=new AlertDialog.Builder(view.getContext());
                alt_bld.setMessage("레시피 수정을 완료하시겠습니까?").setCancelable(false)
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int id) {

                                        if(text_recipename.getText().toString().length() == 0){
                                            Toast.makeText(getApplicationContext(), "요리 이름을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                                            //요리이름 필수
                                            return;
                                        }
                                        if(recipe3.getText().toString().length() == 0){
                                            //만드는 방법 필수
                                            Toast.makeText(getApplicationContext(), "만드는 방법을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if(requiredname.size()==0){
                                            //필수재료 최소 1개
                                            Toast.makeText(getApplicationContext(), "재료를 입력해야 합니다", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Response.Listener<String> responseListener_edit = new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    boolean success = jsonObject.getBoolean("success");

                                                    if (success) {  //성공 시 ingredient 일단 다 지워

                                                        Response.Listener<String> responseListener_ing_rm = new Response.Listener<String>()
                                                        {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    boolean success = jsonObject.getBoolean("success");

                                                                    if (success) {  //성공 시 ingredient 넣어

                                                                        for(int i=0; i<requiredname.size(); i++){
                                                                            int finalI = i;
                                                                            Response.Listener<String> responseListener = new Response.Listener<String>(){
                                                                                @Override
                                                                                public void onResponse(String response){
                                                                                    try{
                                                                                        JSONObject jsonObject = new JSONObject(response);
                                                                                        boolean success = jsonObject.getBoolean("success");

                                                                                        if(success){
                                                                                            if(selectedname.size()==0) {
                                                                                                Intent intent = new Intent();
                                                                                                setResult(RESULT_OK, intent);
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
                                                                            UploadRequest_ingred uploadRequest_ingred = new UploadRequest_ingred(recipe_id.toString(), requiredname.get(i), requiredamount.get(i), "1", responseListener);
                                                                            RequestQueue queue = Volley.newRequestQueue(My_Detail_edit.this);
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
                                                                                            if(finalI == selectedname.size()-1) {
                                                                                                Intent intent = new Intent();
                                                                                                setResult(RESULT_OK, intent);
                                                                                                finish();
                                                                                            }
                                                                                            Intent intent = new Intent();
                                                                                            setResult(RESULT_OK, intent);
                                                                                            finish();
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
                                                                            UploadRequest_ingred uploadRequest_ingred = new UploadRequest_ingred(recipe_id.toString(), selectedname.get(i), selectedamount.get(i), "0", responseListener);
                                                                            RequestQueue queue = Volley.newRequestQueue(My_Detail_edit.this);
                                                                            queue.add(uploadRequest_ingred);
                                                                        }

                                                                        return;

                                                                    } else {    //실패 시
                                                                        Toast.makeText(getApplicationContext(), "재료 삭제 실패", Toast.LENGTH_SHORT).show();
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
                                                        Myrecipe_edit_ing_rm Myrecipe_edit_ing_rm_ = new Myrecipe_edit_ing_rm(recipe_id.toString(), responseListener_ing_rm);
                                                        RequestQueue queue = Volley.newRequestQueue(My_Detail_edit.this);
                                                        queue.add(Myrecipe_edit_ing_rm_);

                                                    } else {    //실패 시
                                                        Toast.makeText(getApplicationContext(), "Recipe 업데이트 실패", Toast.LENGTH_SHORT).show();
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
                                        Myrecipe_edit Myrecipe_edit_ = new Myrecipe_edit(recipe_id.toString(),text_recipename.getText().toString(), recipe3.getText().toString(), UserId, Integer.toString(Isvegan), responseListener_edit);
                                        RequestQueue queue = Volley.newRequestQueue(My_Detail_edit.this);
                                        queue.add(Myrecipe_edit_);


                                        return;

                                    }
                                })
                        .setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert= alt_bld.create();
                alert.setTitle("레시피 수정");
                alert.show();
            }
        });

        Response.Listener<String> responseListener_recipe = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {//성공시
                        recipe_name = jsonObject.getString("Recipe_name");
                        howtomake = jsonObject.getString("Recipe_content");
                        Isvegan=Integer.valueOf(jsonObject.getString("Recipe_vegan"));

                        if(Isvegan==1)
                            vegancheck_edit.setChecked(true);
                        else
                            vegancheck_edit.setChecked(false);

                        text_recipename=findViewById(R.id.recipename_edit);
                        text_recipename.setText(recipe_name);

                        recipe3 = findViewById(R.id.recipe3_edit);
                        recipe3.setText(howtomake);


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
        RecipeRequest_recipe RecipeRequest_recipe_ = new RecipeRequest_recipe(recipe_id.toString(),responseListener_recipe);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(RecipeRequest_recipe_);

        Response.Listener<String> responseListener_recipe_ing = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Recipe_ing_name.clear();
                Recipe_ing_amount.clear();
                Recipe_ing_required.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Recipe_ing");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Recipe_ing_name_2 = item.getString("Recipe_ing_name");
                        String Recipe_ing_amount_2 = item.getString("Recipe_ing_amount");
                        int Recipe_ing_required_2 = Integer.valueOf(item.getString("Recipe_ing_required"));
                        Recipe_ing_name.add(Recipe_ing_name_2);
                        Recipe_ing_amount.add(Recipe_ing_amount_2);
                        Recipe_ing_required.add(Recipe_ing_required_2);

                        if(Recipe_ing_required.get(i)==1)
                        {
                            requiredname.add(Recipe_ing_name.get(i));
                            requiredamount.add(Recipe_ing_amount.get(i));
                        }
                        else
                        {
                            selectedname.add(Recipe_ing_name.get(i));
                            selectedamount.add(Recipe_ing_amount.get(i));
                        }

                    }
                    for(int i=0; i<requiredname.size(); i++){
                        adapter1.addItem(requiredname.get(i), requiredamount.get(i));
                    }
                    for(int i=0; i<selectedname.size(); i++){
                        adapter2.addItem(selectedname.get(i), selectedamount.get(i));
                    }
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };

        RecipeRequest_recipe_ing RecipeRequest_recipe_ing_ = new RecipeRequest_recipe_ing(recipe_id.toString(),responseListener_recipe_ing);
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        queue2.add(RecipeRequest_recipe_ing_);


    }
}