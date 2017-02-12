package com.example.com.example.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.sean.liontest1.CustomApplcation;
import com.example.sean.liontest1.R;
import com.example.util.StatusBarUtils;
import com.example.view.HeaderLayout;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BaseActivity extends FragmentActivity {

    public static String URL_PREFIX = "http://182.92.158.167/Sunshine_server/";

    public static String[] BUSINESS_SCOPE = new String[] {
        "互联网", "金融", "电子商务", "餐饮", "房地产", "创业", "其他"
    };

    protected HeaderLayout mHeaderLayout;

    CustomApplcation mApplication;
    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        mApplication = CustomApplcation.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    Toast mToast;

    /**
     * String
     *
     * @param text
     */
    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    /**
     * int
     *
     * @param resId
     */
    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }


    /**
     * 打Log
     * ShowLog
     *
     * @return void
     * @throws
     */
    public void ShowLog(String msg) {
        Log.i("life", msg);
    }


    /**
     * 只有title
     *
     * @throws
     * @Title: initTopBarLayoutByTitle
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        //findViewById(R.id.content);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 初始化标题栏-带左右按钮
     *
     * @return void
     * @throws
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId, String text,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
                listener);
    }

    /**
     * 左边默认是返回键
     *
     * @param titleName
     * @param rightDrawableId
     * @param listener
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
    }

    /**
     * 初始化标题栏-带左右按钮
     *
     * @param titleName
     * @param rightDrawableId 右边按钮 Drawable 资源
     * @param listener1       右边按钮监听器
     * @param leftDrawableId  左边按钮 Drawable 资源
     * @param listener2       左边按钮监听器
     */
    public void initTopBarForBoth(String titleName,
                                  int rightDrawableId, HeaderLayout.onRightImageButtonClickListener listener1,
                                  int leftDrawableId, HeaderLayout.onLeftImageButtonClickListener listener2) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, leftDrawableId,
                listener2);
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener1);
    }

    public void initTopBarForBothWithText(String titleName,
                                          CharSequence text, HeaderLayout.onRightImageButtonClickListener listener1,
                                          int leftDrawableId, HeaderLayout.onLeftImageButtonClickListener listener2) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, leftDrawableId,
                listener2);
        mHeaderLayout.setTitleAndRightButtonText(titleName, text, listener1);
    }

    /**
     * 只有左边按钮和 Title
     * initTopBarLayout
     *
     * @throws
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
    }

    // 左边按钮的点击事件
    public class OnLeftButtonClickListener implements
            HeaderLayout.onLeftImageButtonClickListener {

        public void onClick() {
            finish();
        }
    }

    // 字符串MD5加密
    public static String md5(String string) {

        byte[] hash;

        try {

            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Huh, MD5 should be supported?", e);

        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException("Huh, UTF-8 should be supported?", e);

        }


        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {

            if ((b & 0xFF) < 0x10) hex.append("0");

            hex.append(Integer.toHexString(b & 0xFF));

        }

        return hex.toString();

    }
}
