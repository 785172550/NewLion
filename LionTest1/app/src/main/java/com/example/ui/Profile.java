package com.example.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.com.example.base.BaseActivity;
import com.example.model.User;
import com.example.sean.liontest1.R;
import com.example.util.NetworkSingleton;
import com.example.util.UserStatus;

/**
 * Created by Administrator on 2016/3/9.
 *  个人资料界面
 */
public class Profile extends BaseActivity{

    String srcPath;
    ImageView icon_view;
    LinearLayout genren_ll;
    LinearLayout gongsi_ll;
    TextView profile_name_et;

    Boolean flag = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_profile);
        setStatusBar();
        initTopBarForLeft("个人中心");
        initview();
    }

    private void initview() {

        icon_view = (ImageView) findViewById(R.id.imageView);

        genren_ll = (LinearLayout) findViewById(R.id.pro_lay1);
        genren_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genren_intent = new Intent(Profile.this, PersonalUI.class);
                genren_intent.putExtra("update_or_complete", true);
                startActivity(genren_intent);
            }
        });
        gongsi_ll = (LinearLayout) findViewById(R.id.pro_lay2);
        gongsi_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gongsi_intent = new Intent(Profile.this,ComInfo.class);
                startActivity(gongsi_intent);
            }
        });

        profile_name_et = (TextView)findViewById(R.id.profile_name_et);
        showUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 2) {
            Uri uri = data.getData();
            icon_view.setImageURI(uri);
            ContentResolver cr = this.getContentResolver();
            Cursor c = cr.query(uri, null, null, null, null);
            c.moveToFirst();
            //这是获取的图片保存在sdcard中的位置
            srcPath = c.getString(c.getColumnIndex("_data"));
            System.out.println(srcPath+"----------保存路径2");
        }
    }


    private void showUserInfo() {
        User user = UserStatus.getLoginedUser();
        ImageRequest request = new ImageRequest(user.getHeader(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                icon_view.setImageBitmap(response);
                Log.i("deeplee", "获取了头像");
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("deeplee", error.getMessage());
            }
        });

        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        profile_name_et.setText(user.getName());
    }
}
