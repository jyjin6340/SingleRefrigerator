package org.techtown.sw_project;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class Appinfo extends AppCompatActivity {

    TextView iconTextView, fontTextView, deleteiconTextView;
    Button ossButton;
    ImageButton back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);


        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iconTextView = findViewById(R.id.text_veganicon);
        iconTextView.setPaintFlags(iconTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fontTextView = findViewById(R.id.text_font);
        fontTextView.setPaintFlags(fontTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        deleteiconTextView = findViewById(R.id.text_deleteicon);
        deleteiconTextView.setPaintFlags(deleteiconTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        iconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/free-icons/vegan"));
                startActivity(intent);
            }
        });

        fontTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bingfont.co.kr/license.html"));
                startActivity(intent);
            }
        });

        deleteiconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/free-icons/delete"));
                startActivity(intent);
            }
        });

        ossButton = findViewById(R.id.button_oss);
        ossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Appinfo.this, OssLicensesMenuActivity.class);
                startActivity(intent);
            }
        });
    }


}