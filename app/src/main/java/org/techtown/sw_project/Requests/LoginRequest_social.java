package org.techtown.sw_project.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest_social extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Login_social.php"; //호스팅 주소 + php
    private Map<String, String> map;
    public LoginRequest_social(String email, String name, Response.Listener<String> listener) { //문자형태로 보낸다는 뜻
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
        map.put("name", name);
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}