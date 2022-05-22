package org.techtown.sw_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.EmailRequest;
import org.techtown.sw_project.Requests.PasswdRequest;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Find_passwd extends AppCompatActivity {
    Button Pd_Change_button, Emailcheck, Code_confirm;
    ImageButton Back_button;
    EditText Email, Passwd, PasswdCK, Certification_code_user;
    AlertDialog dialog;
    String Certification_code_2;
    int Certification_True=0;

    static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_passwd);
        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });     //뒤로가기


        Email = findViewById(R.id.text_email);
        Certification_code_user = findViewById(R.id.certification_code);
        Passwd = findViewById(R.id.text_passwd);
        PasswdCK = findViewById(R.id.text_passwdcheck);

        Certification_code_user.setVisibility(View.GONE);
        Passwd.setVisibility(View.GONE);
        PasswdCK.setVisibility(View.GONE);

        Emailcheck = findViewById(R.id.button_email_certification);
        Code_confirm = findViewById(R.id.button_code_confirm);
        Pd_Change_button = findViewById(R.id.button_register);

        Code_confirm.setVisibility(View.GONE);
        Pd_Change_button.setVisibility(View.GONE);

        Emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(Find_passwd.this);
                pd.setMessage("전송 중입니다.");
                pd.show();
                String UserEmail = Email.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (!success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Find_passwd.this);
                                pd.hide();
                                dialog = builder.setMessage("존재하지 않는 이메일입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                return;
                            } else {        //성공 시 이메일 보내기 가능
                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                        .permitDiskReads()
                                        .permitDiskWrites()
                                        .permitNetwork().build());

                                int Certification_code = (int) (Math.random() * 10000000);; //본문 내용
                                Certification_code_2 = String.valueOf(Certification_code);

                                try {
                                    GMailSender gMailSender = new GMailSender("rememberus320@gmail.com", "qmoncouchrzhjacu"); //GMailSender.sendMail(제목, 본문내용, 받는사람);
                                    gMailSender.sendMail("싱냉 인증메일 입니다", Certification_code_2, UserEmail);
                                    Certification_code_user.setVisibility(View.VISIBLE);
                                    Code_confirm.setVisibility(View.VISIBLE);
                                    pd.hide();
                                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                                } catch (SendFailedException e) {
                                    pd.hide();
                                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (MessagingException e) {
                                    Log.e("message", e.toString());
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
                RequestQueue queue = Volley.newRequestQueue(Find_passwd.this);
                queue.add(emailRequest);
            }

        });


        Code_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((Certification_code_user.getText().toString()).equals(Certification_code_2)){
                    Certification_True=1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Find_passwd.this);
                    dialog = builder.setMessage("인증이 완료되었습니다.").setPositiveButton("확인", null).create();
                    dialog.show();

                    Passwd.setVisibility(View.VISIBLE);
                    PasswdCK.setVisibility(View.VISIBLE);
                    Pd_Change_button.setVisibility(View.VISIBLE);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Find_passwd.this);
                    dialog = builder.setMessage("인증 코드를 다시 확인해주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                }
            }
        });

        Pd_Change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Userpw = Passwd.getText().toString();
                String Useremail = Email.getText().toString();
                String PassCK = PasswdCK.getText().toString();
                if(Certification_True==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Find_passwd.this);
                    dialog = builder.setMessage("이메일 인증을 해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(!Userpw.equals(PassCK)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Find_passwd.this);
                    dialog = builder.setMessage("비밀번호가 일치하지 않습니다.").setNegativeButton("확인", null).create();
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
                                Toast.makeText(getApplicationContext(), "비밀번호 변경 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Find_passwd.this, Login.class); //로그인 객체로 가기
                                startActivity(intent);
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
                PasswdRequest passwdRequest = new PasswdRequest(Userpw, Useremail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Find_passwd.this);
                queue.add(passwdRequest);
            }
        });
    }
}