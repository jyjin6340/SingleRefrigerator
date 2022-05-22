package org.techtown.sw_project.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UploadRequest_tip extends StringRequest {

    final static private String URL = "http:ec2-3-39-106-112.ap-northeast-2.compute.amazonaws.com/Upload_tip.php"; //호스팅 주소 + php

    private Map<String, String> map;

    public UploadRequest_tip(String name, String content, String userid, Response.Listener<String> listener) { //문자형태로 보낸다는 뜻
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("name", name);
        map.put("content", content);
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;

    }
}