package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baoyz.actionsheet.ActionSheet;
import com.example.com.example.base.BaseActivity;
import com.example.sean.liontest1.R;
import com.example.util.UserStatus;

/**
 * Created by Administrator on 2016/3/11.
 */
public class MySetting extends BaseActivity {

	Button exit_btn;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mysetting);
		setStatusBar();
		initView();
	}

	private void initView() {
		initTopBarForLeft("设置");
		exit_btn = (Button)findViewById(R.id.exit_btn);
		exit_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				ActionSheet.createBuilder(MySetting.this,getSupportFragmentManager())
						.setCancelButtonTitle("取消")
						.setOtherButtonTitles("确定")
						.setCancelableOnTouchOutside(true)
						.setListener(new ActionSheet.ActionSheetListener() {
							@Override
							public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

							}

							@Override
							public void onOtherButtonClick(ActionSheet actionSheet, int index) {
								if (index == 0) {

									UserStatus.deleteLoginedUser();
									Intent intent = new Intent(MySetting.this, LogUI.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}
							}
						}).show();
			}
		});
	}
}
