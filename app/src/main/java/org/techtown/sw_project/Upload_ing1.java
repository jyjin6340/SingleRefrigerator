//Upload_ing1.java

package org.techtown.sw_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.HomeRequest_ing;

import java.util.ArrayList;

public class Upload_ing1 extends Activity {
    Button submit_button;
    GridView gridview = null;
    GridViewAdapter2 adapter = null;
    String Selected; ImageView back_button;
    TextView text_selected; EditText text_amount;
    ArrayList<String> ingredient_list = new ArrayList<>();
    ArrayList<Bitmap> ingredient_list_img = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload_ing1);

        ArrayList<String> existlist = getIntent().getStringArrayListExtra("list");

        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gridview = findViewById(R.id.gridview3);
        adapter = new GridViewAdapter2();

        Response.Listener<String> responseListener_ing = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ingredient_list.clear();
                ingredient_list_img.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("All_ing");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Ingredient_name = item.getString("All_ing_name");
                        Bitmap bitmap_ =StringToBitmap(item.getString("All_ing_image"));

                        ingredient_list.add(Ingredient_name);
                        ingredient_list_img.add(bitmap_);
                        //Toast.makeText(getContext().getApplicationContext(), Ingredient_name, Toast.LENGTH_SHORT).show();
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        adapter.addItem(new Upload_ing1.Ingredient2(ingredient_list.get(i),ingredient_list_img.get(i),false));
                    }
                    for(int i=0; i<existlist.size(); i++) {
                        Log.e("list", existlist.get(i));
                        adapter.removeItem(existlist.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        HomeRequest_ing HomeRequest_ing_ = new HomeRequest_ing(responseListener_ing);
        RequestQueue queue = Volley.newRequestQueue(Upload_ing1.this);
        queue.add(HomeRequest_ing_);

        gridview.setAdapter(adapter);
        gridview.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        text_selected = findViewById(R.id.text_selected);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity().getApplicationContext(),"clicked", Toast.LENGTH_SHORT).show();
                //adapter.notifyDataSetChanged();
                //gridview.setAdapter(adapter);
                Selected = adapter.getItemName(i);
                //Toast.makeText(getApplicationContext(),Selected+" Clicked", Toast.LENGTH_SHORT).show();
                text_selected.setText(Selected);
            }
        });

        text_amount = findViewById(R.id.text_amount);

        submit_button = findViewById(R.id.button_add_ing1);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Ingred = text_selected.getText().toString();
                String Amount = text_amount.getText().toString();
                if(Ingred.length()==0){
                    Toast.makeText(getApplicationContext(),"재료를 선택해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Amount.length() == 0){
                    Toast.makeText(getApplicationContext(),"양을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("name", Ingred);
                intent.putExtra("amount", Amount);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    class GridViewAdapter2 extends BaseAdapter {
        ArrayList<Ingredient2> items = new ArrayList<Ingredient2>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Ingredient2 item) {
            items.add(item);
        }

        public void removeItem(String item) {
            for(int i=0; i<items.size(); i++){
                if(item.equals(items.get(i).getName())) {
                    items.remove(items.get(i));
                    break;
                }
            }
        }
        public void changeItemSelect(int position){
            if(items.get(position).getSelected()==true)
                items.get(position).setSelected(false);
            else items.get(position).setSelected(true);
            return;
        }

        public boolean getSelect(int position){
            return items.get(position).getSelected();
        }
        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getItemName(int position) {
            return items.get(position).getName();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Ingredient2 ingredient = items.get(position);

            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_list_item, viewGroup, false);
            Boolean isSel = ingredient.getSelected();

            LinearLayout background = (LinearLayout) convertView.findViewById(R.id.gridview_background);

            if(isSel == true){
                //tv_name.setBackgroundColor(Color.GREEN);
                background.setBackground(getResources().getDrawable(R.drawable.gridview_round));
            } else {
                //background.setBackgroundColor(Color.WHITE);
                background.setBackgroundColor(Color.alpha(255));
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);

            tv_name.setText(ingredient.getName());
            iv_icon.setImageBitmap(ingredient.getResId());

            return convertView;  //뷰 객체 반환
        }
    }


    public class Ingredient2 {
        /* 아이템의 정보를 담기 위한 클래스 */

        String name;
        Bitmap resId;
        boolean isSelected;

        public Ingredient2(String name, Bitmap resId, boolean isSelected) {
            this.name = name;
            this.resId = resId;
            this.isSelected = isSelected;
        }
        public boolean getSelected() { return isSelected; }
        public void setSelected(boolean isSelected) { this.isSelected = isSelected; }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getResId() { return resId; }
        public void setResId(Bitmap resId) {
            this.resId = resId;
        }
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE ) {
            return false;
        }
        return true;
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