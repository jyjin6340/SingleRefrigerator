package org.techtown.sw_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.sw_project.Fragments.Fragment_home;
import org.techtown.sw_project.Fragments.Fragment_mypage;
import org.techtown.sw_project.Fragments.Fragment_recipe;
import org.techtown.sw_project.Fragments.Fragment_refrige;

public class MainActivity extends AppCompatActivity {

    Fragment_refrige fragment1;    //냉장고 tab
    Fragment_home fragment2;    //꿀팁 tab
    Fragment_recipe fragment3;    //홈 tab
    Fragment_mypage fragment4;    //마이페이지 tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment_refrige();
        fragment2 = new Fragment_home();
        fragment3 = new Fragment_recipe();
        fragment4 = new Fragment_mypage();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
        bottomNavigation.setSelectedItemId(R.id.tab2);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
                        //Toast.makeText(getApplicationContext(), "첫 번째 탭 선택됨", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment1).commit();
                        return true;

                    case R.id.tab2:
                        //Toast.makeText(getApplicationContext(), "두 번째 탭 선택됨", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment2).commit();
                        return true;

                    case R.id.tab3:
                        //Toast.makeText(getApplicationContext(), "세 번째 탭 선택됨", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment3).commit();
                        return true;

                    case R.id.tab4:
                        //Toast.makeText(getApplicationContext(), "네 번째 탭 선택됨", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment4).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fragment1 = new Fragment_refrige();
        fragment2 = new Fragment_home();
        fragment3 = new Fragment_recipe();
        fragment4 = new Fragment_mypage();



        if (resultCode == RESULT_OK) {
            if(requestCode == 100){
                //Toast.makeText(getApplicationContext(), "request code 400", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
                bottomNavigation.setSelectedItemId(R.id.tab1);
            }
            else if(requestCode == 200){
                //Toast.makeText(getApplicationContext(), "request code 400", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
                bottomNavigation.setSelectedItemId(R.id.tab2);
            }
            else if(requestCode == 300){
                //Toast.makeText(getApplicationContext(), "request code 300", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
                bottomNavigation.setSelectedItemId(R.id.tab3);
            }
            else if(requestCode == 400){
                //Toast.makeText(getApplicationContext(), "request code 400", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment4).commit();
                BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
                bottomNavigation.setSelectedItemId(R.id.tab4);
            }
        }
    }

}