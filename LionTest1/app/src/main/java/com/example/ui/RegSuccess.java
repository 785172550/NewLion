package com.example.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.com.example.base.BaseActivity;
import com.example.sean.liontest1.R;

/**
 * Created by Administrator on 2016/3/4.
 * 注册成功后的界面
 */
public class RegSuccess extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_regsuccess);
        Button gonext = (Button) findViewById(R.id.go_next);

        gonext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
