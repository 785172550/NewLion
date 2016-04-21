package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.com.example.base.BaseActivity;
import com.example.model.User;
import com.example.sean.liontest1.MainActivity;
import com.example.sean.liontest1.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    // 定时操作
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            checkHasUserLoginedOrProfiled();
        }
    };

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log);
        setContentView(R.layout.activity_splash);

        timer = new Timer();
        timer.schedule(task, 2000);
    }

    private void checkHasUserLoginedOrProfiled() {

        // 首先判断是否有用户登录了
        List<User> users = User.findWithQuery(User.class, "Select * from User");
        if (users.size() == 0) {
            // 当前咩有用户登录，进入主界面
            naviToMainActivity();
            ShowLog("当前没有用户登录");

        } else  {
            // 判断用户的资料是否填写完整
            if (users.get(0).getName().isEmpty() || users.get(0).getName() == null) {
                // 没有填写完整，进入资料填写界面
                naviToPersonUI();
                // naviToMainActivity();
                ShowLog("有用户登录，但是资料没有填写完整");
            } else  {
                // 用户资料已经填写完整
                naviToMainActivity();
                ShowLog("有用户登录，且资料已经填写完整");
            }
        }
    }

    private void naviToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    private void naviToPersonUI() {
        Intent intent = new Intent(SplashActivity.this, PersonalUI.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}
