package org.techtown.sw_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.HoneyTipRequest;
import org.techtown.sw_project.Requests.Mytip_update;

public class My_tip_edit extends AppCompatActivity {
    Integer tip_id;
    String ing_name, howto, tipuser;
    TextView text_ingname,tip;
    ImageButton button_back;
    Button modify;
    AlertDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_edit);

        Intent intent = getIntent();
        tip_id = intent.getIntExtra("id",0);
        text_ingname = findViewById(R.id.text_tipname); //팁 이름
        tip= findViewById(R.id.text_tipcontent); //팁 내용
        modify=findViewById(R.id.button_tipmodify); //팁 수정버튼

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Response.Listener<String> responseListener_tip = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {//성공시
                        ing_name = jsonObject.getString("Tip_name");
                        howto= jsonObject.getString("Tip_detail");
                        tipuser= jsonObject.getString("Tip_userid");

                        text_ingname.setText(ing_name);
                        tip.setText(howto);

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
        HoneyTipRequest honeyTipRequest = new HoneyTipRequest(tip_id.toString(),responseListener_tip);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(honeyTipRequest);

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = text_ingname.getText().toString();
                String newtip = tip.getText().toString();
                if(newname == "" || newtip == ""){
                    AlertDialog.Builder builder = new AlertDialog.Builder(My_tip_edit.this);
                    dialog = builder.setMessage("모든 정보를 입력해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
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
                //서버로 요청
                Mytip_update mytip_update = new Mytip_update(tip_id.toString(), newname, newtip,responseListener);
                RequestQueue queue = Volley.newRequestQueue(My_tip_edit.this);
                queue.add(mytip_update);
            }
        });

    }
}