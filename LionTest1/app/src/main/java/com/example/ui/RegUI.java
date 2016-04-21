package com.example.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.com.example.base.BaseActivity;
import com.example.entity.SuccessResponse;
import com.example.sean.liontest1.R;
import com.example.util.NetworkSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/11.
 */
public class RegUI extends BaseActivity {

    private Button reg_sure;
    private EditText usr_name;
    private EditText password1;
    private EditText password2;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_reg_ui);
        // 沉浸式状态栏
        setStatusBar();
        initview();
    }

    private void initview() {
        initTopBarForLeft("注册");
        reg_sure = (Button) findViewById(R.id.reg_sure);
        usr_name = (EditText) findViewById(R.id.user_name_edt);
        password1 = (EditText) findViewById(R.id.password_edt1);
        password2 = (EditText) findViewById(R.id.password_edt2);

        reg_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usr_name.getText().toString().isEmpty() || password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
                    ShowToast("用户名或密码不能为空");
                    return;
                }

                if (!password1.getText().toString().equals(password2.getText().toString())) {
                    ShowToast(R.string.password_not_consistent);
                    return;
                }

                rigister();
            }
        });
    }

    protected void rigister() {
        // 发起请求
        String tag_register_req = "register_req";
        String url = URL_PREFIX + "register.php";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        final Gson gson = new Gson();

        StringRequest register_req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // ShowToast(response);
                        // 解析JSON

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
                                ShowToast(R.string.duplicate_registe);
                            }
                        } else if (code == 200) {
                            // 解析JSON
                            Type type = new TypeToken<SuccessResponse<String>>() {
                            }.getType();
                            SuccessResponse<String> succResponse = gson.fromJson(response, type);
                            if (succResponse.getCode() == 200) {
                                // 注册成功
                                ShowToast(R.string.register_success);
                                RegUI.this.finish();
                            }
                        }

                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowLog("注册失败，请重试");
                        pDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", usr_name.getText().toString());
                map.put("password", md5(password1.getText().toString()));
                map.put("create_time", date);
                return map;
            }
        };

        // 加入RequestQueue
        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(register_req);
    }

}
