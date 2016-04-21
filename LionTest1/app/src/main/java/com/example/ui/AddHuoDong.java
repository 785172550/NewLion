package com.example.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.com.example.base.BaseActivity;
import com.example.entity.SuccessResponse;
import com.example.sean.liontest1.R;
import com.example.util.NetworkSingleton;
import com.example.util.PhotoUtil;
import com.example.util.UploadUtil;
import com.example.util.UserStatus;
import com.example.view.HeaderLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2016/3/12.
 */
public class AddHuoDong extends BaseActivity implements UploadUtil.OnUploadProcessListener {
    private RichEditor mEditor;
    private ImageView cover_image;
    private EditText themem_et, time_et;

    private String cover_image_uri = "";

    private String cover_image_address = "";

    private ProgressDialog progressDialog;

    private Boolean flag = false;

    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                // 插入图片
                mEditor.insertImage(msg.getData().getString("image_address"), "activity_images");

                progressDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_huodong);
        // 沉浸式状态栏
        setStatusBar();
        initview();
    }

    private void initview() {
        initTopBarForBothWithText("发布活动", "发布", new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick(View v) {
                // 发布活动
                // 首先检查资料是痘填写完整
                if (cover_image_address.isEmpty() || themem_et.getText().toString().isEmpty() ||
                        time_et.getText().toString().isEmpty() || mEditor.getHtml().isEmpty()) {
                    ShowToast("请将信息填写完整");
                } else {
                    // 发起项目
                    launchProject();
                }
            }
        }, R.drawable.base_action_bar_back_bg_selector, new HeaderLayout.onLeftImageButtonClickListener() {
            @Override
            public void onClick() {
                AddHuoDong.this.finish();
            }
        });

        cover_image = (ImageView) findViewById(R.id.cover_image);
        themem_et = (EditText) findViewById(R.id.activity_theme_et);
        time_et = (EditText) findViewById(R.id.activity_time_et);

        mEditor = (RichEditor) findViewById(R.id.editor);

        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("介绍您的活动...");
        mEditor.setAlignLeft();

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                // mPreview.setText(text);
            }
        });

        mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


            }
        });

        time_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从图库中选择图片，然后上传到服务器，然后在显示
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 1);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出对话框，让用户输入链接
            }
        });


        // 封面图片点击事件
        cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 2) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                Cursor c = cr.query(uri, null, null, null, null);
                c.moveToFirst();
                //这是获取的图片保存在sdcard中的位置
                cover_image_uri = PhotoUtil.getSmallBitmap(c.getString(c.getColumnIndex("_data")), 360, 480);
                Bitmap bitmap = PhotoUtil.getBitmap(cover_image_uri, 360, 480);
                cover_image.setImageBitmap(bitmap);

                // 上传到服务器
                // 弹出Loading对话框
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                }
                progressDialog.setMessage("正在上传图片...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // 表示当前上传的是封面图片
                flag = true;

                // 上传图片
                String fileKey = "file";
                String requestURL = URL_PREFIX + "accept_activity_image.php";
                UploadUtil uploadUtil = UploadUtil.getInstance();
                uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

                uploadUtil.uploadFile(cover_image_uri, fileKey, requestURL, null);

            } else if (requestCode == 1) {
                // 弹出Loading对话框，上传到服务器
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                Cursor c = cr.query(uri, null, null, null, null);
                c.moveToFirst();
                String image_uri = PhotoUtil.getSmallBitmap(c.getString(c.getColumnIndex("_data")), 360, 480);

                // 弹出Loading对话框
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                }
                progressDialog.setMessage("正在上传图片...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // 表示当前上传的是详情里面的照片
                flag = false;

                // 上传图片
                String fileKey = "file";
                String requestURL = URL_PREFIX + "accept_activity_image.php";
                UploadUtil uploadUtil = UploadUtil.getInstance();
                uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

                uploadUtil.uploadFile(image_uri, fileKey, requestURL, null);
            }
        }
    }

    private void showDatePickerDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View date_picker_view = inflater.inflate(R.layout.dialog_date_picker, null);
        final DatePicker datePicker = (DatePicker) date_picker_view.findViewById(R.id.date_picker);

        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle("选择活动时间");
        dialog.setContentView(date_picker_view);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取Date
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                time_et.setText(date);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * 上传服务器响应回调
     */
    @Override
    public void onUploadDone(int responseCode, String message) {
        ShowLog("上传完成");
        ShowLog("返回信息：" + message);

        // 删除本地保存的文件
        PhotoUtil.deleteTempFile(cover_image_uri);

        // 解析json数据
        // 首先是获取code
        String resArray[] = message.split("code");
        ShowLog(resArray[1]);
        String codeStr = resArray[1].substring(3, 6);
        int code = Integer.parseInt(codeStr);
        if (code == 201) { // 出错了
            ShowToast("上传图片失败");
            progressDialog.dismiss();
        } else if (code == 200) { // 上传成功
            Gson gson = new Gson();
            Type type = new TypeToken<SuccessResponse<String>>() {
            }.getType();
            String response = message.subSequence(5, message.length()).toString();
            ShowLog(response);
            SuccessResponse<String> successResponse = gson.fromJson(response, type);
            final String image_address = URL_PREFIX + successResponse.getData();

            // 当前是上传封面照片
            if (flag) {

                cover_image_address = image_address;
                progressDialog.dismiss();

            } else { // 上传详情里面的照片
                Message message1 = new Message();
                message1.what = 1;
                Bundle data = new Bundle();
                data.putString("image_address", image_address);
                message1.setData(data);
                myHandler.sendMessage(message1);
            }
        }

    }

    /*
     * 上传进度
     */
    @Override
    public void onUploadProcess(int uploadSize) {
        ShowLog("进度: " + uploadSize);
    }

    @Override
    public void initUpload(int fileSize) {
        ShowLog("初始：" + fileSize);
    }

    private void launchProject() {
        // 发起请求
        String tag_register_req = "launch_project_req";
        String url = URL_PREFIX + "publish_project.php";

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("正在发起...");

        final Gson gson = new Gson();

        StringRequest launch_project_req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 获取Code
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
                            }
                        } else if (code == 200) {  // 发布成功
                            // 解析JSON
                            Type type = new TypeToken<SuccessResponse<String>>() {
                            }.getType();
                            SuccessResponse<String> succResponse = gson.fromJson(response, type);
                            if (succResponse.getCode() == 200) {
                                // 注册成功
                                ShowToast(R.string.register_success);
                                AddHuoDong.this.finish();
                            }
                        }

                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowLog(error.getMessage());
                        ShowToast("发起活动出错，请重试");
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("title", themem_et.getText().toString());
                map.put("time", time_et.getText().toString());
                map.put("launcher_id", UserStatus.getLoginedUser().getUserId() + "");
                map.put("cover_image", cover_image_address);
                map.put("details_page", mEditor.getHtml());
                return map;
            }
        };

        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(launch_project_req);

    }
}
