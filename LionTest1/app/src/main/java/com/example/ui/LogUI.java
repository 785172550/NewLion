package com.example.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.com.example.base.BaseActivity;
import com.example.entity.SuccessResponse;
import com.example.entity.User;
import com.example.sean.liontest1.MainActivity;
import com.example.sean.liontest1.R;
import com.example.util.NetworkSingleton;
import com.example.util.UserStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/4.
 * 登录界面
 */
public class LogUI extends BaseActivity {

    EditText username_et, psw_et;
    Button login_btn, forget_btn, reg_btn;
    AVLoadingIndicatorView avLoadingIndicatorView;
    Animation scale_animation;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log);
        initview();
    }

    protected void initview() {

        username_et = (EditText) findViewById(R.id.uname_edt);
        psw_et = (EditText) findViewById(R.id.pawd_edt);
        login_btn = (Button) findViewById(R.id.log_btn);
        forget_btn = (Button) findViewById(R.id.forget_btn);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        //用户名 密码
        String uname, pawd = null;

        scale_animation = AnimationUtils.loadAnimation(LogUI.this, R.anim.login_btn_scale_animation);

        //注册监听器
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 首先检查是否为空
                if (username_et.getText().toString().isEmpty() || psw_et.getText().toString().isEmpty()) {
                    ShowToast("用户名或密码不能为空");
                    return;
                }

                login();
            }
        });
        forget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入注册界面
                Intent regIntent = new Intent(LogUI.this, RegUI.class);
                LogUI.this.startActivity(regIntent);
            }
        });

    }

    protected void login() {
        // login_btn.setVisibility(View.INVISIBLE);

        // 发起请求
        String tag_register_req = "login_req";
        String url = URL_PREFIX + "login.php";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        final Gson gson = new Gson();

        StringRequest login_req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShowLog(response);

                        // 首先是获取code
                        String resArray[] = response.split("code");
                        ShowLog(resArray[1]);
                        String codeStr = resArray[1].substring(3, 6);
                        int code = Integer.parseInt(codeStr);
                        if (code == 201) { // 出错了
                            // 获取type的值
                            String typeStr = response.substring(9, 12);
                            int type = Integer.parseInt(typeStr);
                            if (type == 100 || type == 101) {
                                ShowToast(R.string.database_operation_error);
                            } else if (type == 102) {
                                ShowToast(R.string.username_or_password_error);
                            }
                        } else if (code == 200) { // 正确的返回

                            ShowLog("测试");

                            // 解析JSON
                            Type type = new TypeToken<SuccessResponse<User>>() {
                            }.getType();
                            SuccessResponse<User> successResponse = gson.fromJson(response, type);

                            ShowToast(R.string.login_success);
                            // 想数据库中插入数据
                            insertIntoDatabase(successResponse);

                            // 首先判断用户资料是否填写完整
                            com.example.model.User user = UserStatus.getLoginedUser();
                            if (user.getName().isEmpty()) {
                                naviToPersonUI();
                            } else {
                                naviToMainActivity();
                            }

                            LogUI.this.finish();
                        }

                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowLog("登录失败，请重试");
                        pDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username_et.getText().toString());
                map.put("password", md5(psw_et.getText().toString()));
                return map;
            }
        };

        // 加入RequestQueue
        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(login_req);


//		login_btn.startAnimation(scale_animation);
//		scale_animation.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				avLoadingIndicatorView.setVisibility(View.VISIBLE);
//				login_btn.setClickable(false);
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//
//			}
//		});

        //成功跳到下一个界面，不成功返回原来，toast提示密码用户名错误
        /*if(登录不成功){
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
			login_btn.clearAnimation();
			login_btn.setClickable(true);

		}*/

    }

    private void insertIntoDatabase(SuccessResponse<User> successResponse) {
        com.example.model.User user = new com.example.model.User(successResponse.getData().getId(), successResponse.getData().getUsername(),
                successResponse.getData().getPassword(), successResponse.getData().getHeader(), successResponse.getData().getName(),
                successResponse.getData().getSex(), successResponse.getData().getAddress(), successResponse.getData().getContact(),
                successResponse.getData().getService_team(),
                successResponse.getData().getCreate_time(), successResponse.getData().getUpdate_time());

        user.save();
    }

    private void naviToMainActivity() {
        Intent intent = new Intent(LogUI.this, MainActivity.class);
        startActivity(intent);
    }

    private void naviToPersonUI() {
        Intent intent = new Intent(LogUI.this, PersonalUI.class);
        startActivity(intent);
    }
}
