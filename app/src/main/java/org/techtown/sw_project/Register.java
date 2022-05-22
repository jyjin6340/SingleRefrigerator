package org.techtown.sw_project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
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
import org.techtown.sw_project.Requests.NicknameRequest;
import org.techtown.sw_project.Requests.RegisterRequest;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Register extends AppCompatActivity {

    Button Register_button, NickCK_button, Emailcheck_button, Code_confirm_button;
    ImageButton Back_button;
    EditText Nickname, Email, Passwd, PasswdCK, Certification_code_user;
    String Certification_code_2;

    Boolean NickCK = false; //NickCK_button 눌러서 중복확인 되면 true로 바꾸고 가입버튼 눌렀을 때 false면 중복확인 해주세요 띄우기.
    int Certification_True=0;

    AlertDialog dialog;
    static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });     //뒤로가기

        Nickname = findViewById(R.id.text_nickname);
        Nickname.addTextChangedListener(new TextWatcher() {     //Password 일치하는지 체크
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                NickCK=false;
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Email = findViewById(R.id.text_email);
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Certification_True = 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Certification_code_user = findViewById(R.id.certification_code);
        Passwd = findViewById(R.id.text_passwd);
        PasswdCK = findViewById(R.id.text_passwdcheck);

        NickCK_button = findViewById(R.id.button_nickcheck);
        NickCK_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = Nickname.getText().toString();
                if(UserName.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("닉네임을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                String InputNickname = Nickname.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//성공시 닉네임이 존재하는 것
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("이미 존재하는 닉네임입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                return;
                            } else {//실패시 닉네임이 존재하지 않는 것
                                NickCK = true;
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("사용 가능한 닉네임입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
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
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(nicknameRequest);
            }
        });

        Emailcheck_button = findViewById(R.id.button_email_certification);
        Emailcheck_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Email.getText().length()==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("닉네임을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                pd = new ProgressDialog(Register.this);
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

                            if (success) {
                                pd.show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("이미 존재하는 이메일입니다.").setPositiveButton("확인", null).create();
                                pd.hide();
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
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(emailRequest);
            }

        });

        Code_confirm_button = findViewById(R.id.button_code_confirm);
        Code_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((Certification_code_user.getText().toString()).equals(Certification_code_2)){
                    Certification_True=1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("인증이 완료되었습니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("인증 코드를 다시 확인해주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                }
            }
        });

        Register_button = findViewById(R.id.button_register);
        Register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = Nickname.getText().toString();
                String Userpw = Passwd.getText().toString();
                String Useremail = Email.getText().toString();
                String PassCK = PasswdCK.getText().toString();
                if(Username.equals("") || Useremail.equals("") || Userpw.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("정보를 모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(Certification_True==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("이메일 인증을 해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(!NickCK){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("닉네임 중복 확인을 해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(Userpw.length()<6){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("비밀번호는 6자리 이상이어야 합니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(!Userpw.equals(PassCK)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
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
                                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
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
                RegisterRequest registerRequest = new RegisterRequest(Username, Userpw, Useremail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(registerRequest);
            }
        });
    }
}