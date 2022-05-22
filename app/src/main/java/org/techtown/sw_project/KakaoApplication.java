package org.techtown.sw_project;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this,"266b9b767f1c7a24883ddc61a55ddea8");

    }
}