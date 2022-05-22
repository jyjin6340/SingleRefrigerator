package org.techtown.sw_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.RegisterRequest;
import org.techtown.sw_project.Requests.UploadRequest_tip;

public class Upload_tip extends AppCompatActivity {
    ImageButton Back_button;
    EditText Tipname, Tipcontent;
    String UserId;
    Button Upload_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipupload);

        UserId = getIntent().getStringExtra("ID");

        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }); //뒤로가기 버튼

        Tipname = findViewById(R.id.text_tipname); //팁 이름
        Tipcontent= findViewById(R.id.text_tipcontent); //팁 내용
        Upload_button= findViewById(R.id.button_tipupload); //팁 업로드
        Upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Tipname.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "팁 제목을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                    //요리이름 필수
                    return;
                }
                if(Tipcontent.getText().toString().length() == 0){
                    //만드는 방법 필수
                    Toast.makeText(getApplicationContext(), "팁 상세내용을 입력해야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<String> responseListener_tipupload = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//성공시
                                setResult(RESULT_OK);
                                finish();
                                //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
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
                UploadRequest_tip uploadRequest_tip = new UploadRequest_tip(Tipname.getText().toString(), Tipcontent.getText().toString(), UserId, responseListener_tipupload);
                RequestQueue queue = Volley.newRequestQueue(Upload_tip.this);
                queue.add(uploadRequest_tip);
            }
        });

    }
}