package com.example.ui;

import android.os.Bundle;

import com.example.com.example.base.BaseActivity;
import com.example.sean.liontest1.R;

/**
 * Created by Administrator on 2016/3/14.
 *
 */
public class AboutUs extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_about_us);
		setStatusBar();
		initview();
	}

	private void initview() {

		initTopBarForLeft("关于我们");
	}
}
