package org.techtown.sw_project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.EmailRequest;
import org.techtown.sw_project.Requests.MyinfoRequest_get;
import org.techtown.sw_project.Requests.MyinfoRequest_update;
import org.techtown.sw_project.Requests.NicknameRequest;
import org.techtown.sw_project.Requests.QuitRequest;

import java.io.ByteArrayOutputStream;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Myinfo_social extends Activity {

    //EditText text_name;
    Button button_modify, button_quit, button_nickcheck;
    ImageButton button_back;
    String UserId, name, email, passwd;
    TextView text_name, text_email;
    AlertDialog dialog;
    ImageView User_profile;
    Boolean NickCK = true;
    static final int getimagesetting=1001;//for request intent
    String Profile_edit="";

    Bitmap bitmap_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_social);

        UserId = getIntent().getStringExtra("id");

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        User_profile=findViewById(R.id.profile);
        button_modify = findViewById(R.id.button_modify);
        button_quit = findViewById(R.id.button_quit);
        text_name = findViewById(R.id.myinfo_name);
        text_email = findViewById(R.id.myinfo_email_social);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {//성공시
                        name = jsonObject.getString("name");
                        text_name.setText(name);
                        email = jsonObject.getString("email");
                        text_email.setText(email);
                        passwd = jsonObject.getString("passwd");
                        bitmap_ =StringToBitmap(jsonObject.getString("User_profile"));
                        if(bitmap_!=null)
                            User_profile.setImageBitmap(bitmap_);
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
        MyinfoRequest_get myinfoRequest_get = new MyinfoRequest_get(UserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(myinfoRequest_get);

        User_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SetImageActivity.class);
                startActivityForResult(intent, getimagesetting);
            }
        });

        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLoginEdit = auto.edit();

        button_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Profile_edit=="")
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap_.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    byte[] bytes = baos.toByteArray();

                    Profile_edit = Base64.encodeToString(bytes, Base64.NO_WRAP);
                }
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//성공시
                                Toast.makeText(getApplicationContext(), "회원정보 수정 완료", Toast.LENGTH_SHORT).show();
                                //그냥 finish만 하면 다시 마이페이지로 돌아가는데, 닉네임 바뀌지 않은 상태의 마이페이지임..
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
                MyinfoRequest_update myinfoRequest_update = new MyinfoRequest_update(UserId, name, passwd, email, Profile_edit, responseListener);
                //Toast.makeText(getApplicationContext(), UserId+" "+newname+" "+newpasswd+" "+newemail, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(Myinfo_social.this);
                queue.add(myinfoRequest_update);
            }
        });

        button_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo_social.this);
                builder.setMessage("정말로 탈퇴하시겠습니까?").setCancelable(false);
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                Response.Listener<String> responseListener = new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");

                                            if (success) {//성공시 닉네임이 존재하는 것
                                                autoLoginEdit.clear();
                                                autoLoginEdit.commit();

                                                setResult(RESULT_OK);
                                                finish();
                                                return;
                                            } else {//실패시 닉네임이 존재하지 않는 것

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
                                QuitRequest quitRequest = new QuitRequest(UserId, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(Myinfo_social.this);
                                queue.add(quitRequest);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("회원 탈퇴");
                alert.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==getimagesetting){   //if image change

            if(resultCode==RESULT_OK){
                Bitmap selPhoto = null;
                selPhoto=(Bitmap) data.getParcelableExtra("bitmap");
                User_profile.setImageBitmap(selPhoto);//썸네일

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selPhoto.compress(Bitmap.CompressFormat.PNG, 50, baos);
                byte[] bytes = baos.toByteArray();

                Profile_edit = Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        }
    }

}