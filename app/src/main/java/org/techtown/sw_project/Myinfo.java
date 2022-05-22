package org.techtown.sw_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.EmailRequest;
import org.techtown.sw_project.Requests.FridgeRequest_remove;
import org.techtown.sw_project.Requests.MyinfoRequest_get;
import org.techtown.sw_project.Requests.MyinfoRequest_update;
import org.techtown.sw_project.Requests.NicknameRequest;
import org.techtown.sw_project.Requests.QuitRequest;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Myinfo extends Activity {

    EditText text_name, text_email, text_passwd, text_passwdcheck, text_certify;
    Button button_modify, button_quit, button_nickcheck, button_sendmail, button_certify;
    ImageButton button_back;
    String UserId, name, email, passwd;
    TextView textView;
    AlertDialog dialog;
    //CircleImageView User_profile;
    ImageView User_profile;
    String Certification_code_2;
    Boolean NickCK = true, CertifyCK = true;
    Bitmap bitmap_;

    static ProgressDialog pd;
    static final int getimagesetting=1001;//for request intent
    String Profile_edit="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

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
        text_email = findViewById(R.id.myinfo_email);
        text_passwd = findViewById(R.id.myinfo_passwd);
        text_passwdcheck = findViewById(R.id.myinfo_passwdcheck);

        text_certify = findViewById(R.id.myinfo_certify);
        textView = findViewById(R.id.textView_certify);
        button_nickcheck = findViewById(R.id.button_nickname);
        button_sendmail = findViewById(R.id.button_sendmail);
        button_certify = findViewById(R.id.button_certify);

        text_certify.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        button_nickcheck.setVisibility(View.GONE);
        button_sendmail.setVisibility(View.GONE);
        button_certify.setVisibility(View.GONE);

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
                        text_passwd.setText(passwd);
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
                    //Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        MyinfoRequest_get myinfoRequest_get = new MyinfoRequest_get(UserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(myinfoRequest_get);

        /////////////////////////////////////////////
        User_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SetImageActivity.class);
                startActivityForResult(intent, getimagesetting);
            }
        });

        text_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!text_name.getText().toString().equals(name)){
                    button_nickcheck.setVisibility(View.VISIBLE);
                    NickCK = false;
                }
                else {
                    button_nickcheck.setVisibility(View.GONE);
                    NickCK = true;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        button_nickcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = text_name.getText().toString();
                if(UserName.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("닉네임을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                String InputNickname = text_name.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//성공시 닉네임이 존재하는 것
                                AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                                dialog = builder.setMessage("이미 존재하는 닉네임입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                return;
                            } else {//실패시 닉네임이 존재하지 않는 것
                                NickCK = true;
                                AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                                dialog = builder.setMessage("사용 가능한 닉네임입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                button_nickcheck.setVisibility(View.GONE);
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
                NicknameRequest nicknameRequest = new NicknameRequest(InputNickname, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Myinfo.this);
                queue.add(nicknameRequest);
            }
        });

        text_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!text_email.getText().toString().equals(email)){
                    button_sendmail.setVisibility(View.VISIBLE);
                    CertifyCK = false;
                }
                else {
                    button_sendmail.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    text_certify.setVisibility(View.GONE);
                    button_certify.setVisibility(View.GONE);
                    CertifyCK = true;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        button_sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(Myinfo.this);
                pd.setMessage("전송 중입니다.");
                pd.show();
                //layout_certify.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                text_certify.setVisibility(View.VISIBLE);
                button_certify.setVisibility(View.VISIBLE);
                String UserEmail = text_email.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                                dialog = builder.setMessage("이미 존재하는 이메일입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                return;
                            } else {        //실패 시 이메일 보내기 가능
                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                        .permitDiskReads()
                                        .permitDiskWrites()
                                        .permitNetwork().build());

                                int Certification_code = (int) (Math.random() * 10000000);; //본문 내용
                                Certification_code_2 = String.valueOf(Certification_code);
                                try {
                                    GMailSender gMailSender = new GMailSender("rememberus320@gmail.com", "qmoncouchrzhjacu"); //GMailSender.sendMail(제목, 본문내용, 받는사람);
                                    gMailSender.sendMail("싱냉 인증메일 입니다", Certification_code_2, UserEmail);
                                    pd.hide();
                                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                                } catch (SendFailedException e) {
                                    pd.hide();
                                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (MessagingException e) {
                                    pd.hide();
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace(); }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                //서버로 요청
                EmailRequest emailRequest = new EmailRequest(UserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Myinfo.this);
                queue.add(emailRequest);
            }
        });

        button_certify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((text_certify.getText().toString()).equals(Certification_code_2)){
                    CertifyCK = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("인증이 완료되었습니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("인증 코드를 다시 확인해주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                }
            }
        });

        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLoginEdit = auto.edit();

        button_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = text_name.getText().toString();
                String newemail = text_email.getText().toString();
                String newpasswd = text_passwd.getText().toString();

                if(Profile_edit=="")
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap_.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    byte[] bytes = baos.toByteArray();

                    Profile_edit = Base64.encodeToString(bytes, Base64.NO_WRAP);
                }
                if(NickCK == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("닉네임 중복 확인을 해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(CertifyCK == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("이메일 인증을 해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(!text_passwdcheck.getText().toString().equals(text_passwd.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
                    dialog = builder.setMessage("비밀번호가 일치하지 않습니다").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(newname == "" || newemail == "" || newpasswd == ""){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
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
                                Toast.makeText(getApplicationContext(), "회원정보 수정 완료", Toast.LENGTH_SHORT).show();
                                //그냥 finish만 하면 다시 마이페이지로 돌아가는데, 닉네임 바뀌지 않은 상태의 마이페이지임..

                                autoLoginEdit.putString("Name", newname);
                                autoLoginEdit.putString("PW", newpasswd);
                                autoLoginEdit.putString("Email", newemail);
                                autoLoginEdit.commit();

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
                MyinfoRequest_update myinfoRequest_update = new MyinfoRequest_update(UserId, newname, newpasswd, newemail,Profile_edit,responseListener);
                //Toast.makeText(getApplicationContext(), UserId+" "+newname+" "+newpasswd+" "+newemail, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(Myinfo.this);
                queue.add(myinfoRequest_update);
            }
        });

        button_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Myinfo.this);
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
                                RequestQueue queue = Volley.newRequestQueue(Myinfo.this);
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
        if(requestCode==getimagesetting){	//if image change

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