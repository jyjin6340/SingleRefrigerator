package org.techtown.sw_project.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Appinfo;
import org.techtown.sw_project.Login;
import org.techtown.sw_project.Myinfo;
import org.techtown.sw_project.Myinfo_social;
import org.techtown.sw_project.R;
import org.techtown.sw_project.Requests.MyinfoRequest_get;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/*
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
 */
public class Fragment_mypage extends Fragment {
    String UserPW, UserEmail, UserId, UserName;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth ;
    ImageView User_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        UserId=auto.getString("Id",null);
        UserName = auto.getString("Name", null);
        UserEmail = auto.getString("Email", null);
        UserPW = auto.getString("PW", null);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);


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
        if(UserName == null) {      //로그인 되지 않은 상태
            View v = inflater.inflate(R.layout.fragment_mypage_bflogin, container, false);

            Button Logreg = v.findViewById(R.id.button_log_reg);
            Logreg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                    getActivity().startActivityForResult(intent, 400);
                }
            });
            return v;
        }
        else{
            if(UserPW == null) {    //소셜 로그인 된 상태
                View v = inflater.inflate(R.layout.fragment_mypage, container,false);
                TextView Welcome;
                ImageButton Myinfo, Logout, Myrecipe, Mylike, Mybmark;
                Button Appinfo;

                Welcome = v.findViewById(R.id.text_welcome);
                Welcome.setText(UserName+"님, 환영합니다!");
                Myinfo = v.findViewById(R.id.button_myinfo);
                Logout = v.findViewById(R.id.button_logout);
                Myrecipe = v.findViewById(R.id.button_myrecipe);
                Mylike = v.findViewById(R.id.button_mylike);
                Mybmark = v.findViewById(R.id.button_mybmark);
                Appinfo = v.findViewById(R.id.button_servinfo);
                User_profile=v.findViewById(R.id.profile);

                Myinfo.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), Myinfo_social.class);
                        intent.putExtra("id", UserId);
                        getActivity().startActivityForResult(intent, 400);
                    }
                });

                Logout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                updateKakaoLoginUi();
                                return null;
                            }
                        });
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Logout.class);
                        getActivity().startActivityForResult(intent, 400);
                    }
                });
                updateKakaoLoginUi();
                Myrecipe.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Myrecipe.class);
                        startActivity(intent);
                    }
                });

                Mylike.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Mylike.class);
                        startActivity(intent);
                    }
                });

                Mybmark.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Mybmark.class);
                        startActivity(intent);
                    }
                });

                Appinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), Appinfo.class);
                        startActivity(intent);
                    }
                });
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시

                                Bitmap bitmap_ =StringToBitmap(jsonObject.getString("User_profile"));
                                if(bitmap_!=null)
                                    User_profile.setImageBitmap(bitmap_);
                                return;
                            } else {//실패시
                                Toast.makeText(getContext().getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext().getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                MyinfoRequest_get myinfoRequest_get = new MyinfoRequest_get(UserId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                queue.add(myinfoRequest_get);
                //Toast.makeText(getActivity().getApplicationContext(), UserId+" "+UserName, Toast.LENGTH_SHORT).show();
                return v;
            }


            else{        //일반 로그인 된 상태
                View v = inflater.inflate(R.layout.fragment_mypage, container,false);
                TextView Welcome;
                ImageButton Myinfo, Logout, Myrecipe, Mylike, Mybmark;
                Button Appinfo;

                Welcome = v.findViewById(R.id.text_welcome);
                Welcome.setText(UserName+"님, 환영합니다!");
                Myinfo = v.findViewById(R.id.button_myinfo);
                Logout = v.findViewById(R.id.button_logout);
                Myrecipe = v.findViewById(R.id.button_myrecipe);
                Mylike = v.findViewById(R.id.button_mylike);
                Mybmark = v.findViewById(R.id.button_mybmark);
                Appinfo = v.findViewById(R.id.button_servinfo);
                User_profile=v.findViewById(R.id.profile);

                Myinfo.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), Myinfo.class);
                        intent.putExtra("id", UserId);
                        getActivity().startActivityForResult(intent, 400);
                    }
                });

                Logout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                updateKakaoLoginUi();
                                return null;
                            }
                        });
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Logout.class);
                        getActivity().startActivityForResult(intent, 400);
                    }
                });
                updateKakaoLoginUi();
                Myrecipe.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Myrecipe.class);
                        startActivity(intent);
                    }
                });

                Mylike.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Mylike.class);
                        startActivity(intent);
                    }
                });

                Mybmark.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Mybmark.class);
                        startActivity(intent);
                    }
                });

                Appinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), org.techtown.sw_project.Appinfo.class);
                        startActivity(intent);
                    }
                });

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시

                                Bitmap bitmap_ =StringToBitmap(jsonObject.getString("User_profile"));
                                if(bitmap_!=null)
                                    User_profile.setImageBitmap(bitmap_);
                                return;
                            } else {//실패시
                                Toast.makeText(getContext().getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext().getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                MyinfoRequest_get myinfoRequest_get = new MyinfoRequest_get(UserId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
                queue.add(myinfoRequest_get);
                //Toast.makeText(getActivity().getApplicationContext(), UserId+" "+UserName, Toast.LENGTH_SHORT).show();
                return v;
            }
        }
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private  void updateKakaoLoginUi(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있으면
                if (user!=null){

                }else {

                }
                return null;
            }
        });
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
}