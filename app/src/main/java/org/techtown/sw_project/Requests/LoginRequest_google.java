package org.techtown.sw_project.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest_google extends StringRequest {

    final static private String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Login_google.php"; //호스팅 주소 + php

    private Map<String, String> map;

    public LoginRequest_google(String nickname, Response.Listener<String> listener) { //문자형태로 보낸다는 뜻
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("nickname", nickname);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;

    }
}