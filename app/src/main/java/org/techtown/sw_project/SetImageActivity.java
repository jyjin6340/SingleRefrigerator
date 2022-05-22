package org.techtown.sw_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SetImageActivity extends Activity implements OnClickListener {

    static final int camera=2001;
    static final int gallery=2002;
    Button Cancle,Camera,Choose_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_img);
        init();

    }

    private void init() {
        Camera=findViewById(R.id.camerapopBtn);
        Choose_photo=findViewById(R.id.gallerypopBtn);
        Cancle=findViewById(R.id.ClosepopBtn);

        Camera.setOnClickListener(this);
        Choose_photo.setOnClickListener(this);
        Cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch(v.getId()){
            case R.id.camerapopBtn:
                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, camera);
                break;
            case R.id.gallerypopBtn:

                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/-");
                startActivityForResult(intent, gallery);
                break;

            case R.id.ClosepopBtn:
                setResult(RESULT_CANCELED, intent);
                Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                break;
        }

    }
    @SuppressLint("NewApi")
    private Bitmap resize(Bitmap bm){

        Configuration config=getResources().getConfiguration();
		if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);

        return bm;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent=new Intent();
        Bitmap bm;
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case camera:
                    bm=(Bitmap) data.getExtras().get("data");
                    bm=resize(bm);
                    intent.putExtra("bitmap",bm);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case gallery:

                    try {
                        bm = Images.Media.getBitmap( getContentResolver(), data.getData());
                        bm=resize(bm);
                        intent.putExtra("bitmap",bm);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch(OutOfMemoryError e){
                        Toast.makeText(getApplicationContext(), "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                default:
                    setResult(RESULT_CANCELED, intent);
                    finish();
                    break;

            }
        }else{
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }



}