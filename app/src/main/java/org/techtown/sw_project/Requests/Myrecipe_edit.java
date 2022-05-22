package org.techtown.sw_project.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Myrecipe_edit extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Myrecipe_ed.php"; //호스팅 주소 + php

    private Map<String, String> map;

    public Myrecipe_edit(String recipe_id,String name, String content, String userid, String vegan, Response.Listener<String> listener) { //문자형태로 보낸다는 뜻
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("recipe_id", recipe_id);
        map.put("name", name);
        map.put("content", content);
        map.put("userid", userid);
        map.put("vegan", vegan);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;

    }
}