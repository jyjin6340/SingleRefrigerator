//Login.java

package org.techtown.sw_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.TokenManager;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.LoginRequest;
import org.techtown.sw_project.Requests.LoginRequest_social;
import org.techtown.sw_project.Requests.RegisterRequest_social;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Login extends AppCompatActivity implements View.OnClickListener{

    Button Login, Register, Find_Passwd;
    ImageButton kakao_login, Back_button;
    EditText textMail, textPW;
    AlertDialog dialog;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;
    private static final String TAG = "Login";
    SignInButton google_log;
    String kakao_email, kakao_nickname,kakao_token;

    private static final String TAG2 = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Back_button = findViewById(R.id.button_back);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textMail = findViewById(R.id.input_email);
        textPW = findViewById(R.id.input_passwd);

        kakao_login=findViewById(R.id.kakao_login);
        Function2<OAuthToken, Throwable, Unit> callback = new  Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                if(oAuthToken != null) {

                }
                if (throwable != null) {

                }
                updateKakaoLoginUi();
                return null;
            }
        };
        kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(Login.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(Login.this, callback);
                }else {
                    UserApiClient.getInstance().loginWithKakaoAccount(Login.this, callback);
                }
            }
        });
        updateKakaoLoginUi();

        Login = findViewById(R.id.button_login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Lemail = textMail.getText().toString();
                String Lpw = textPW.getText().toString();

                if(Lemail.equals("") || Lpw.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    dialog = builder.setMessage("로그인 정보를 입력하세요!").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시

                                String UserId = jsonObject.getString("id");
                                String UserName = jsonObject.getString("name");

                                SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLoginEdit = auto.edit();
                                autoLoginEdit.putString("Id", UserId);
                                autoLoginEdit.putString("Name", UserName);
                                autoLoginEdit.putString("PW", Lpw);
                                autoLoginEdit.putString("Email", Lemail);
                                autoLoginEdit.commit();
                                setResult(RESULT_OK);
                                finish();

                                //Toast.makeText(getApplicationContext(), "UserId : " + mg, Toast.LENGTH_SHORT).show();
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
                LoginRequest loginRequest = new LoginRequest(Lemail, Lpw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);
            }
        });

        Register = findViewById(R.id.button_gotoreg);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        Find_Passwd = findViewById(R.id.button_findpw);
        Find_Passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Find_passwd.class);
                startActivity(intent);
            }
        });

        google_log = findViewById(R.id.google_login);
        google_log.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Login.this, gso);

        // 기존에 로그인 했던 계정을 확인한다.
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(Login.this);

        // 로그인 되있는 경우 (토큰으로 로그인 처리)
        if (gsa != null && gsa.getId() != null) {

        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();


                Response.Listener<String> responseListener_googlereg = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//성공시
                                Response.Listener<String> responseListener_googlelog = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) {//성공시
                                                String UserId = jsonObject.getString("id");
                                                String Name = jsonObject.getString("name");
                                                String email = jsonObject.getString("email");

                                                SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor autoLoginEdit = auto.edit();
                                                autoLoginEdit.putString("Id", UserId);
                                                autoLoginEdit.putString("Name", Name);
                                                autoLoginEdit.putString("PW", null);
                                                autoLoginEdit.putString("Email", email);
                                                autoLoginEdit.commit();

                                                setResult(RESULT_OK);
                                                finish();


                                            } else {//실패시
                                                Toast.makeText(getApplicationContext(), "구글로그인 실패", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "구글로그인 예외", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                };
                                LoginRequest_social LoginRequest_social_ = new LoginRequest_social(personEmail, personName, responseListener_googlelog);
                                RequestQueue queue = Volley.newRequestQueue(Login.this);
                                queue.add(LoginRequest_social_);

                            } else {//실패시
                                Toast.makeText(getApplicationContext(), "구글로그인 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                RegisterRequest_social registerRequest_social = new RegisterRequest_social(personName, personId, personEmail, responseListener_googlereg);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(registerRequest_social);

            }
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            //GoogleSignInAccount account = task.getResult(ApiException.class);
            //firebaseAuthWithGoogle(account);
            handleSignInResult(task);
        }
    }
    private  void updateKakaoLoginUi(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있으면
                if (user!=null){

                    kakao_email=user.getKakaoAccount().getEmail();
                    kakao_nickname=user.getKakaoAccount().getProfile().getNickname();
                    kakao_token= TokenManager.getInstance().getToken().getAccessToken();

                    Log.d(TAG2,"Email: " + user.getKakaoAccount().getEmail());
                    Log.d(TAG2,"Nickname: " + user.getKakaoAccount().getProfile().getNickname());
                    Log.d(TAG2,"토근 정보...?: " + TokenManager.getInstance().getToken().getAccessToken());

                    Response.Listener<String> responseListener_kakao = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {//성공시

                                    Response.Listener<String> responseListener_kakao2 = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                boolean success = jsonObject.getBoolean("success");
                                                if (success) {//성공시
                                                    String UserId = jsonObject.getString("id");
                                                    String Name = jsonObject.getString("name");
                                                    String email = jsonObject.getString("email");
                                                    //Toast.makeText(getApplicationContext(), "UserId : " + UserId +" "+UserName, Toast.LENGTH_SHORT).show();

                                                    SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                                    SharedPreferences.Editor autoLoginEdit = auto.edit();
                                                    autoLoginEdit.putString("Id", UserId);
                                                    autoLoginEdit.putString("Name", Name);
                                                    autoLoginEdit.putString("PW", null);
                                                    autoLoginEdit.putString("Email", email);
                                                    autoLoginEdit.commit();

                                                    setResult(RESULT_OK);
                                                    finish();


                                                } else {//실패시
                                                    Toast.makeText(getApplicationContext(), "카카오로그인2실패", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getApplicationContext(), "카카오로그인2예외", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    };
                                    LoginRequest_social LoginRequest_social_ = new LoginRequest_social(kakao_email, kakao_nickname, responseListener_kakao2);
                                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                                    queue.add(LoginRequest_social_);

                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "카카오회원가입실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    RegisterRequest_social RegisterRequest_social_ = new RegisterRequest_social(kakao_nickname,kakao_token,kakao_email, responseListener_kakao);
                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                    queue.add(RegisterRequest_social_);

                    kakao_login.setVisibility(View.GONE);
                }else {

                    kakao_login.setVisibility(View.VISIBLE);
                }
                return null;
            }
        });
    }
}