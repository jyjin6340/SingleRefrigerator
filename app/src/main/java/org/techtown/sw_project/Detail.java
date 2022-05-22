package org.techtown.sw_project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.sw_project.Requests.CommentRequest;
import org.techtown.sw_project.Requests.CommentRequest_rm;
import org.techtown.sw_project.Requests.CommentlistRequest;
import org.techtown.sw_project.Requests.MyinfoRequest_get;
import org.techtown.sw_project.Requests.Myrecipe_delete;
import org.techtown.sw_project.Requests.RecipeRequest_b_add;
import org.techtown.sw_project.Requests.RecipeRequest_b_rm;
import org.techtown.sw_project.Requests.RecipeRequest_l_add;
import org.techtown.sw_project.Requests.RecipeRequest_l_rm;
import org.techtown.sw_project.Requests.RecipeRequest_liked_num;
import org.techtown.sw_project.Requests.RecipeRequest_recipe;
import org.techtown.sw_project.Requests.RecipeRequest_recipe_check;
import org.techtown.sw_project.Requests.RecipeRequest_recipe_ing;
import org.techtown.sw_project.Requests.RecipeRequest_ustate_b;
import org.techtown.sw_project.Requests.RecipeRequest_ustate_l;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Detail extends Activity {
    String UserName, UserId;
    String writer_name, recipe_name, required_ing, selected_ing, howtomake;
    Integer liked_number, recipe_id, vegan, Uname_exist, writer_id;
    Integer liked, bookmark;
    TextView text_recipename, text_recipenamebar, writer, recipe1, recipe2, recipe3, like_num;
    ImageView image_vegan, button_like, button_bookmark;
    Button comment_button, edit_button, remove_button;

    EditText Comment;
    ArrayList<String> Recipe_ing_name = new ArrayList<>();
    ArrayList<String> Recipe_ing_amount = new ArrayList<>();
    ArrayList<Integer> Recipe_ing_required = new ArrayList<>();

    ListView listView = null;
    CommentAdapter adapter = null;
    ArrayList<Comment> commentlist = new ArrayList<Comment>();
    String content, userid, commentid, username;

    ImageButton back_button;
    AlertDialog dialog;
    Boolean editclicked = false;

    final int REQUEST_CODE = 1102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SharedPreferences auto = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

        UserId = auto.getString("Id",null);
        UserName = auto.getString("Name", null);

        Intent intent = getIntent();
        recipe_id = intent.getIntExtra("id", 0);

        image_vegan = findViewById(R.id.image_vegan);
        image_vegan.setVisibility(View.GONE);

        button_like = findViewById(R.id.button_like);
        like_num = findViewById(R.id.like_num);
        button_bookmark = findViewById(R.id.button_bookmark);

        edit_button = findViewById(R.id.button_edit);
        edit_button.setVisibility(View.GONE);
        remove_button = findViewById(R.id.button_delete);
        remove_button.setVisibility(View.GONE);

        back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });


///////////////////////수정 버튼 클릭 시 //////////////////////
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editclicked = true;
                Intent intent = new Intent(Detail.this, My_Detail_edit.class);
                intent.putExtra("id", recipe_id);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

///////////////////////삭제 버튼 클릭 시 //////////////////////
        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alt_bld=new AlertDialog.Builder(view.getContext());
                alt_bld.setMessage("레시피를 삭제하시겠습니까?").setCancelable(false)
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int id) {

                                        Response.Listener<String> responseListener_myrecipe_rm = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    boolean success = jsonObject.getBoolean("success");

                                                    if (success) {//성공시
                                                        return;
                                                    } else {//실패시
                                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        };
                                        Myrecipe_delete Myrecipe_delete_ = new Myrecipe_delete(recipe_id.toString(),UserId,responseListener_myrecipe_rm);
                                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                        queue.add(Myrecipe_delete_);

                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert= alt_bld.create();
                alert.setTitle("레시피 삭제");
                alert.show();
            }
        });

///////////////////////좋아요 개수 불러오기//////////////////////
        Response.Listener<String> responseListener_ustate_l = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {//성공시
                        liked = Integer.valueOf(jsonObject.getString("suc"));
                        liked_number= Integer.valueOf(jsonObject.getString("liked_number"));
                        like_num.setText(liked_number.toString());

                        if(liked==1){
                            button_like.setImageResource(R.drawable.heart_fill);
                        }
                        return;
                    } else {//실패시
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        RecipeRequest_ustate_l RecipeRequest_ustate_l_ = new RecipeRequest_ustate_l(recipe_id.toString(),UserId,responseListener_ustate_l);
        RequestQueue queue_ustate_l = Volley.newRequestQueue(getApplicationContext());
        queue_ustate_l.add(RecipeRequest_ustate_l_);

///////////////////////좋아요 버튼 클릭 시 //////////////////////
        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserName == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                    dialog = builder.setMessage("로그인 후 좋아요가 가능합니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(liked == 1){     //좋아요 취소
                    Response.Listener<String> responseListener_l_rm = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {//성공시
                                    liked = 0;
                                    button_like.setImageResource(R.drawable.heart_empty);
                                    liked_number--;
                                    like_num.setText(liked_number.toString());
                                    return;
                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    RecipeRequest_l_rm RecipeRequest_l_rm_ = new RecipeRequest_l_rm(recipe_id.toString(),UserId.toString(),responseListener_l_rm);
                    RequestQueue queue_l_rm = Volley.newRequestQueue(getApplicationContext());
                    queue_l_rm.add(RecipeRequest_l_rm_);
                }
                else{       //좋아요 추가
                    Response.Listener<String> responseListener_l_add = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    liked = 1;
                                    button_like.setImageResource(R.drawable.heart_fill);

                                    Response.Listener<String> responseListener_l_num = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                boolean success = jsonObject.getBoolean("success");

                                                if (success) {//성공시
                                                    liked_number= Integer.valueOf(jsonObject.getString("liked_number"));
                                                    like_num.setText(liked_number.toString());

                                                    return;
                                                } else {//실패시
                                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getApplicationContext(), "좋아요 숫자 가져오기 예외 1", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    };
                                    RecipeRequest_liked_num RecipeRequest_liked_num_ = new RecipeRequest_liked_num(recipe_id.toString(),responseListener_l_num);
                                    RequestQueue queue_l_num = Volley.newRequestQueue(getApplicationContext());
                                    queue_l_num.add(RecipeRequest_liked_num_);
                                    return;
                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    RecipeRequest_l_add RecipeRequest_l_add_ = new RecipeRequest_l_add(recipe_id.toString(),UserId.toString(),responseListener_l_add);
                    RequestQueue queue_l_add = Volley.newRequestQueue(getApplicationContext());
                    queue_l_add.add(RecipeRequest_l_add_);
                }

            }
        });

///////////////////////북마크 정보 불러오기 //////////////////////
        Response.Listener<String> responseListener_ustate_b = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {//성공시
                        bookmark = Integer.valueOf(jsonObject.getString("suc"));

                        if(bookmark==1){
                            button_bookmark.setImageResource(R.drawable.bookmark_fill);
                        }
                        return;
                    } else {//실패시
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        RecipeRequest_ustate_b RecipeRequest_ustate_b_ = new RecipeRequest_ustate_b(recipe_id.toString(),UserId,responseListener_ustate_b);
        RequestQueue queue_ustate_b = Volley.newRequestQueue(getApplicationContext());
        queue_ustate_b.add(RecipeRequest_ustate_b_);

///////////////////////북마크 버튼 클릭 시 //////////////////////
        button_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserName == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                    dialog = builder.setMessage("로그인 후 북마크가 가능합니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                if(bookmark==1){    //북마크 취소
                    Response.Listener<String> responseListener_b_rm = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    bookmark=0;
                                    button_bookmark.setImageResource(R.drawable.bookmark_empty);
                                    return;
                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    RecipeRequest_b_rm RecipeRequest_b_rm_ = new RecipeRequest_b_rm(recipe_id.toString(),UserId.toString(),responseListener_b_rm);
                    RequestQueue queue_b_rm = Volley.newRequestQueue(getApplicationContext());
                    queue_b_rm.add(RecipeRequest_b_rm_);
                }
                else{       //북마크 추가
                    Response.Listener<String> responseListener_b_add = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    bookmark=1;
                                    button_bookmark.setImageResource(R.drawable.bookmark_fill);
                                    return;
                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    RecipeRequest_b_add RecipeRequest_b_add_ = new RecipeRequest_b_add(recipe_id.toString(),UserId.toString(),responseListener_b_add);
                    RequestQueue queue_b_add = Volley.newRequestQueue(getApplicationContext());
                    queue_b_add.add(RecipeRequest_b_add_);
                }
            }
        });

///////////////////////레시피 정보 불러옴 //////////////////////
        Response.Listener<String> responseListener_recipe_check = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {//성공시
                        Uname_exist = Integer.valueOf(jsonObject.getString("Exi"));

                        if(Uname_exist==0){     //탈퇴한 사용자인 경우
                            writer = findViewById(R.id.writer);
                            writer.setText("알수없음");

                            recipe_name = jsonObject.getString("Recipe_name");
                            howtomake = jsonObject.getString("Recipe_content");
                            vegan = Integer.valueOf(jsonObject.getString("Recipe_vegan"));

                            if(vegan==1)
                            {
                                image_vegan.setImageResource(R.drawable.vegan);
                                image_vegan.setVisibility(View.VISIBLE);
                            }

                            text_recipename = findViewById(R.id.recipename);
                            text_recipename.setText(recipe_name);
                            text_recipenamebar = findViewById(R.id.recipename_bar);
                            text_recipenamebar.setText(recipe_name);

                            recipe3 = findViewById(R.id.recipe3);
                            recipe3.setText(howtomake);
                        }
                        else{           //사용자가 존재하는 경우
                            Response.Listener<String> responseListener_recipe = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success");

                                        if (success) {//성공시
                                            recipe_name = jsonObject.getString("Recipe_name");
                                            howtomake = jsonObject.getString("Recipe_content");
                                            vegan = Integer.valueOf(jsonObject.getString("Recipe_vegan"));
                                            writer_name = jsonObject.getString("User_name");
                                            writer_id=Integer.valueOf(jsonObject.getString("Recipe_userid"));

                                            text_recipename = findViewById(R.id.recipename);
                                            text_recipename.setText(recipe_name);
                                            text_recipenamebar = findViewById(R.id.recipename_bar);
                                            text_recipenamebar.setText(recipe_name);

                                            recipe3 = findViewById(R.id.recipe3);
                                            recipe3.setText(howtomake);

                                            writer = findViewById(R.id.writer);
                                            writer.setText(writer_name);
                                            if(writer_id==Integer.valueOf(UserId)){
                                                edit_button.setVisibility(View.VISIBLE);
                                                remove_button.setVisibility(View.VISIBLE);
                                            }

                                            if(vegan == 1){
                                                image_vegan.setImageResource(R.drawable.vegan);
                                                image_vegan.setVisibility(View.VISIBLE);
                                            }

                                            return;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            };
                            RecipeRequest_recipe RecipeRequest_recipe_ = new RecipeRequest_recipe(recipe_id.toString(),responseListener_recipe);
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(RecipeRequest_recipe_);
                        }

                        return;
                    } else {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        RecipeRequest_recipe_check RecipeRequest_recipe_check_ = new RecipeRequest_recipe_check(recipe_id.toString(),responseListener_recipe_check);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(RecipeRequest_recipe_check_);

///////////////////////레시피 재료 정보 불러옴 //////////////////////
        Response.Listener<String> responseListener_recipe_ing = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Recipe_ing_name.clear();
                Recipe_ing_amount.clear();
                Recipe_ing_required.clear();
                required_ing="";
                selected_ing="";

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Recipe_ing");
                    int selnum = 0;
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String Recipe_ing_name_2 = item.getString("Recipe_ing_name");
                        String Recipe_ing_amount_2 = item.getString("Recipe_ing_amount");
                        int Recipe_ing_required_2 = Integer.valueOf(item.getString("Recipe_ing_required"));
                        Recipe_ing_name.add(Recipe_ing_name_2);
                        Recipe_ing_amount.add(Recipe_ing_amount_2);
                        Recipe_ing_required.add(Recipe_ing_required_2);

                        if(Recipe_ing_required.get(i)==1){
                            required_ing=required_ing+Recipe_ing_name.get(i)+": "+Recipe_ing_amount.get(i)+"\n";
                        }
                        else{
                            selnum++;
                            selected_ing=selected_ing+Recipe_ing_name.get(i)+": "+Recipe_ing_amount.get(i)+"\n";
                        }
                    }
                    if(selnum == 0){        //선택재료 없는 경우
                        LinearLayout selLayout = findViewById(R.id.layout_select);
                        TextView selText = findViewById(R.id.recipe2);
                        selLayout.setVisibility(View.GONE);
                        selText.setVisibility(View.GONE);
                    }
                    recipe1 = findViewById(R.id.recipe1);
                    recipe1.setText(required_ing);
                    recipe2 = findViewById(R.id.recipe2);
                    recipe2.setText(selected_ing);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        RecipeRequest_recipe_ing RecipeRequest_recipe_ing_ = new RecipeRequest_recipe_ing(recipe_id.toString(),responseListener_recipe_ing);
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        queue2.add(RecipeRequest_recipe_ing_);

///////////////////////댓글 입력 //////////////////////
        comment_button = findViewById(R.id.button4);
        Comment = findViewById(R.id.text_comment);
        String recipe_Str = String.valueOf(recipe_id);
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserName == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                    dialog = builder.setMessage("로그인 후 댓글 작성이 가능합니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                String Inputcomment = Comment.getText().toString();
                if(Inputcomment.equals("")){        //입력한 댓글 없는 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                    AlertDialog dialog = builder.setMessage("댓글을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener_comment = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
//                                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();

                            } else {//실패시
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };
                //서버로 요청
                CommentRequest commentRequest = new CommentRequest(recipe_Str, UserId, Inputcomment, responseListener_comment);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(commentRequest);
            }
        });

        listView = findViewById(R.id.comment_list);
        adapter = new CommentAdapter();

///////////////////////댓글 정보 불러옴 //////////////////////
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Comment");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        content = item.getString("Comment_content");
                        userid = item.getString("Comment_user");
                        commentid = item.getString("Comment_id");
                        username = item.getString("User_name");

                        commentlist.add(new Comment(Integer.valueOf(commentid), username, content, Integer.valueOf(userid)));
                    }
                    Collections.sort(commentlist, new CommentComparator());
                    //Toast.makeText(getApplicationContext(), "comment list 크기 : "+Integer.toString(commentlist.size()), Toast.LENGTH_SHORT).show();
                    for(int j=0; j<commentlist.size(); j++)
                        adapter.addComment(commentlist.get(j));
                    listView.setAdapter(adapter);
                    setListViewHeightBasedOnItems(listView);

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "comment 예외 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        CommentlistRequest CommentlistRequest_ = new CommentlistRequest(recipe_Str, responseListener);
        RequestQueue queue3 = Volley.newRequestQueue(getApplicationContext());
        queue3.add(CommentlistRequest_);
    }

    class CommentComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment c1, Comment c2){
            if(c1.getId() > c2.getId())
                return 1;
            else if(c1.getId() < c2.getId())
                return -1;
            return 0;
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }
    }

    class CommentAdapter extends BaseAdapter {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        @Override
        public int getCount() {
            return comments.size();
        }

        public void addComment(Comment comment) {
            comments.add(comment);
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return comments.get(position).getId();
        }

        public String getItemName(int position) {
            return comments.get(position).getName();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Comment comment = comments.get(position);

            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_comment, viewGroup, false);

            TextView username = (TextView) convertView.findViewById(R.id.text_user);
            TextView content = (TextView) convertView.findViewById(R.id.text_content);
            ImageView delete = (ImageView) convertView.findViewById(R.id.button_delete);
            ImageView User_profile = (ImageView) convertView.findViewById(R.id.profile_comment);

            SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
            UserId = auto.getString("Id", null);
            //Toast.makeText(getApplicationContext(), "uid "+UserId+" cid "+Integer.toString(comments.get(position).getUid()), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            MyinfoRequest_get myinfoRequest_get = new MyinfoRequest_get(Integer.toString(comments.get(position).getUid()), responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(myinfoRequest_get);

            if(!Integer.toString(comments.get(position).getUid()).equals(UserId))
                delete.setVisibility(View.GONE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                    builder.setMessage("정말로 삭제하시겠습니까?").setCancelable(false);
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        int commentid = comments.get(position).getId();
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            Response.Listener<String> responseListener = new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success");

                                        if (success) {
                                            comments.remove(position);
                                            adapter.notifyDataSetChanged();
                                            return;
                                        } else {

                                            return;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            };
                            //서버로 요청
                            CommentRequest_rm commentRequest_rm = new CommentRequest_rm(Integer.toString(commentid), responseListener);
                            RequestQueue queue = Volley.newRequestQueue(Detail.this);
                            queue.add(commentRequest_rm);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("댓글 삭제");
                    alert.show();
                }
            });

            username.setText(comment.getName());
            Log.e("comment.getContent()", comment.getContent());
            content.setText(comment.getContent());

            return convertView;  //뷰 객체 반환
        }
    }

    public class Comment {

        int id;
        String name;
        String content;
        int uid;

        public Comment(int id, String name, String content, int uid) {
            this.id= id;
            this.name = name;
            this.content = content;
            this.uid = uid;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getContent(){
            return content;
        }
        public void setContent(String content){
            this.content = content;
        }

        public int getUid(){
            return uid;
        }
        public void setUid(int uid){
            this.uid = uid;
        }

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

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Response.Listener<String> responseListener_ustate_l = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {//성공시
                            liked = Integer.valueOf(jsonObject.getString("suc"));
                            liked_number= Integer.valueOf(jsonObject.getString("liked_number"));
                            like_num.setText(liked_number.toString());

                            if(liked==1){
                                button_like.setImageResource(R.drawable.heart_fill);
                            }
                            return;
                        } else {//실패시
                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            RecipeRequest_ustate_l RecipeRequest_ustate_l_ = new RecipeRequest_ustate_l(recipe_id.toString(),UserId,responseListener_ustate_l);
            RequestQueue queue_ustate_l = Volley.newRequestQueue(getApplicationContext());
            queue_ustate_l.add(RecipeRequest_ustate_l_);

///////////////////////좋아요 버튼 클릭 시 //////////////////////
            button_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserName == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                        dialog = builder.setMessage("로그인 후 좋아요가 가능합니다.").setPositiveButton("확인", null).create();
                        dialog.show();
                        return;
                    }
                    if(liked == 1){     //좋아요 취소
                        Response.Listener<String> responseListener_l_rm = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {//성공시
                                        liked = 0;
                                        button_like.setImageResource(R.drawable.heart_empty);
                                        liked_number--;
                                        like_num.setText(liked_number.toString());
                                        return;
                                    } else {//실패시
                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        };
                        RecipeRequest_l_rm RecipeRequest_l_rm_ = new RecipeRequest_l_rm(recipe_id.toString(),UserId.toString(),responseListener_l_rm);
                        RequestQueue queue_l_rm = Volley.newRequestQueue(getApplicationContext());
                        queue_l_rm.add(RecipeRequest_l_rm_);
                    }
                    else{       //좋아요 추가
                        Response.Listener<String> responseListener_l_add = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        liked = 1;
                                        button_like.setImageResource(R.drawable.heart_fill);

                                        Response.Listener<String> responseListener_l_num = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    boolean success = jsonObject.getBoolean("success");

                                                    if (success) {//성공시
                                                        liked_number= Integer.valueOf(jsonObject.getString("liked_number"));
                                                        like_num.setText(liked_number.toString());

                                                        return;
                                                    } else {//실패시
                                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "좋아요 숫자 가져오기 예외 1", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        };
                                        RecipeRequest_liked_num RecipeRequest_liked_num_ = new RecipeRequest_liked_num(recipe_id.toString(),responseListener_l_num);
                                        RequestQueue queue_l_num = Volley.newRequestQueue(getApplicationContext());
                                        queue_l_num.add(RecipeRequest_liked_num_);
                                        return;
                                    } else {//실패시
                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        };
                        RecipeRequest_l_add RecipeRequest_l_add_ = new RecipeRequest_l_add(recipe_id.toString(),UserId.toString(),responseListener_l_add);
                        RequestQueue queue_l_add = Volley.newRequestQueue(getApplicationContext());
                        queue_l_add.add(RecipeRequest_l_add_);
                    }

                }
            });

///////////////////////북마크 정보 불러오기 //////////////////////
            Response.Listener<String> responseListener_ustate_b = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {//성공시
                            bookmark = Integer.valueOf(jsonObject.getString("suc"));

                            if(bookmark==1){
                                button_bookmark.setImageResource(R.drawable.bookmark_fill);
                            }
                            return;
                        } else {//실패시
                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            RecipeRequest_ustate_b RecipeRequest_ustate_b_ = new RecipeRequest_ustate_b(recipe_id.toString(),UserId,responseListener_ustate_b);
            RequestQueue queue_ustate_b = Volley.newRequestQueue(getApplicationContext());
            queue_ustate_b.add(RecipeRequest_ustate_b_);

///////////////////////북마크 버튼 클릭 시 //////////////////////
            button_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserName == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                        dialog = builder.setMessage("로그인 후 북마크가 가능합니다.").setPositiveButton("확인", null).create();
                        dialog.show();
                        return;
                    }
                    if(bookmark==1){    //북마크 취소
                        Response.Listener<String> responseListener_b_rm = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        bookmark=0;
                                        button_bookmark.setImageResource(R.drawable.bookmark_empty);
                                        return;
                                    } else {//실패시
                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        };
                        RecipeRequest_b_rm RecipeRequest_b_rm_ = new RecipeRequest_b_rm(recipe_id.toString(),UserId.toString(),responseListener_b_rm);
                        RequestQueue queue_b_rm = Volley.newRequestQueue(getApplicationContext());
                        queue_b_rm.add(RecipeRequest_b_rm_);
                    }
                    else{       //북마크 추가
                        Response.Listener<String> responseListener_b_add = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        bookmark=1;
                                        button_bookmark.setImageResource(R.drawable.bookmark_fill);
                                        return;
                                    } else {//실패시
                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        };
                        RecipeRequest_b_add RecipeRequest_b_add_ = new RecipeRequest_b_add(recipe_id.toString(),UserId.toString(),responseListener_b_add);
                        RequestQueue queue_b_add = Volley.newRequestQueue(getApplicationContext());
                        queue_b_add.add(RecipeRequest_b_add_);
                    }
                }
            });

///////////////////////레시피 정보 불러옴 //////////////////////
            Response.Listener<String> responseListener_recipe_check = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {//성공시
                            Uname_exist = Integer.valueOf(jsonObject.getString("Exi"));

                            if(Uname_exist==0){     //탈퇴한 사용자인 경우
                                writer = findViewById(R.id.writer);
                                writer.setText("알수없음");

                                recipe_name = jsonObject.getString("Recipe_name");
                                howtomake = jsonObject.getString("Recipe_content");
                                vegan = Integer.valueOf(jsonObject.getString("Recipe_vegan"));

                                if(vegan==1)
                                {
                                    image_vegan.setImageResource(R.drawable.vegan);
                                    image_vegan.setVisibility(View.VISIBLE);
                                }

                                text_recipename = findViewById(R.id.recipename);
                                text_recipename.setText(recipe_name);
                                text_recipenamebar = findViewById(R.id.recipename_bar);
                                text_recipenamebar.setText(recipe_name);

                                recipe3 = findViewById(R.id.recipe3);
                                recipe3.setText(howtomake);
                            }
                            else{           //사용자가 존재하는 경우
                                Response.Listener<String> responseListener_recipe = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");

                                            if (success) {//성공시
                                                recipe_name = jsonObject.getString("Recipe_name");
                                                howtomake = jsonObject.getString("Recipe_content");
                                                vegan = Integer.valueOf(jsonObject.getString("Recipe_vegan"));
                                                writer_name = jsonObject.getString("User_name");

                                                text_recipename = findViewById(R.id.recipename);
                                                text_recipename.setText(recipe_name);
                                                text_recipenamebar = findViewById(R.id.recipename_bar);
                                                text_recipenamebar.setText(recipe_name);

                                                recipe3 = findViewById(R.id.recipe3);
                                                recipe3.setText(howtomake);

                                                writer = findViewById(R.id.writer);
                                                writer.setText(writer_name);
                                                if(writer_name.equals(UserName)){
                                                    edit_button.setVisibility(View.VISIBLE);
                                                    remove_button.setVisibility(View.VISIBLE);
                                                }

                                                if(vegan == 1){
                                                    image_vegan.setImageResource(R.drawable.vegan);
                                                    image_vegan.setVisibility(View.VISIBLE);
                                                }

                                                return;
                                            } else {
                                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                };
                                RecipeRequest_recipe RecipeRequest_recipe_ = new RecipeRequest_recipe(recipe_id.toString(),responseListener_recipe);
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                queue.add(RecipeRequest_recipe_);
                            }

                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            RecipeRequest_recipe_check RecipeRequest_recipe_check_ = new RecipeRequest_recipe_check(recipe_id.toString(),responseListener_recipe_check);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(RecipeRequest_recipe_check_);

///////////////////////레시피 재료 정보 불러옴 //////////////////////
            Response.Listener<String> responseListener_recipe_ing = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Recipe_ing_name.clear();
                    Recipe_ing_amount.clear();
                    Recipe_ing_required.clear();
                    required_ing="";
                    selected_ing="";

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Recipe_ing");
                        int selnum = 0;
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject item = jsonArray.getJSONObject(i);
                            String Recipe_ing_name_2 = item.getString("Recipe_ing_name");
                            String Recipe_ing_amount_2 = item.getString("Recipe_ing_amount");
                            int Recipe_ing_required_2 = Integer.valueOf(item.getString("Recipe_ing_required"));
                            Recipe_ing_name.add(Recipe_ing_name_2);
                            Recipe_ing_amount.add(Recipe_ing_amount_2);
                            Recipe_ing_required.add(Recipe_ing_required_2);

                            if(Recipe_ing_required.get(i)==1){
                                required_ing=required_ing+Recipe_ing_name.get(i)+": "+Recipe_ing_amount.get(i)+"\n";
                            }
                            else{
                                selnum++;
                                selected_ing=selected_ing+Recipe_ing_name.get(i)+": "+Recipe_ing_amount.get(i)+"\n";
                            }
                        }
                        if(selnum == 0){        //선택재료 없는 경우
                            LinearLayout selLayout = findViewById(R.id.layout_select);
                            TextView selText = findViewById(R.id.recipe2);
                            selLayout.setVisibility(View.GONE);
                            selText.setVisibility(View.GONE);
                        }
                        recipe1 = findViewById(R.id.recipe1);
                        recipe1.setText(required_ing);
                        recipe2 = findViewById(R.id.recipe2);
                        recipe2.setText(selected_ing);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            };
            RecipeRequest_recipe_ing RecipeRequest_recipe_ing_ = new RecipeRequest_recipe_ing(recipe_id.toString(),responseListener_recipe_ing);
            RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
            queue2.add(RecipeRequest_recipe_ing_);

///////////////////////댓글 입력 //////////////////////
            comment_button = findViewById(R.id.button4);
            Comment = findViewById(R.id.text_comment);
            String recipe_Str = String.valueOf(recipe_id);
            comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserName == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                        dialog = builder.setMessage("로그인 후 댓글 작성이 가능합니다.").setPositiveButton("확인", null).create();
                        dialog.show();
                        return;
                    }
                    String Inputcomment = Comment.getText().toString();
                    if(Inputcomment.equals("")){        //입력한 댓글 없는 경우
                        AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                        AlertDialog dialog = builder.setMessage("댓글을 입력하세요.").setPositiveButton("확인", null).create();
                        dialog.show();
                        return;
                    }
                    Response.Listener<String> responseListener_comment = new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {//성공시
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();

                                } else {//실패시
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    };
                    //서버로 요청
                    CommentRequest commentRequest = new CommentRequest(recipe_Str, UserId, Inputcomment, responseListener_comment);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(commentRequest);
                }
            });

            listView = findViewById(R.id.comment_list);
            adapter = new CommentAdapter();

///////////////////////댓글 정보 불러옴 //////////////////////
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Comment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            content = item.getString("Comment_content");
                            userid = item.getString("Comment_user");
                            commentid = item.getString("Comment_id");
                            username = item.getString("User_name");

                            commentlist.add(new Comment(Integer.valueOf(commentid), username, content, Integer.valueOf(userid)));
                        }
                        Collections.sort(commentlist, new CommentComparator());
                        //Toast.makeText(getApplicationContext(), "comment list 크기 : "+Integer.toString(commentlist.size()), Toast.LENGTH_SHORT).show();
                        for(int j=0; j<commentlist.size(); j++)
                            adapter.addComment(commentlist.get(j));
                        listView.setAdapter(adapter);
                        setListViewHeightBasedOnItems(listView);

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "comment 예외 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            CommentlistRequest CommentlistRequest_ = new CommentlistRequest(recipe_Str, responseListener);
            RequestQueue queue3 = Volley.newRequestQueue(getApplicationContext());
            queue3.add(CommentlistRequest_);

            back_button = findViewById(R.id.button_back);
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.anim_slide_maintain, R.anim.anim_slide_down_exit);
    }
}