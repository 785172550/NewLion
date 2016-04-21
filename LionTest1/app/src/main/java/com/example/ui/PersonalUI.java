package com.example.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.actionsheet.ActionSheet;
import com.example.com.example.base.BaseActivity;
import com.example.entity.SuccessResponse;
import com.example.model.User;
import com.example.sean.liontest1.MainActivity;
import com.example.sean.liontest1.R;
import com.example.util.NetworkSingleton;
import com.example.util.PhotoUtil;
import com.example.util.UserStatus;
import com.example.view.HeaderLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/4.
 * 资料填写界面
 */
public class PersonalUI extends BaseActivity {

	ImageView icon_view = null;
	EditText name_et, sex_et, address_et, contact_et, service_et;
	String srcPath;
	int selected_sex = 0;

	Boolean flag = false;
	Boolean update_or_complete = false;

	String header_address = "";

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_personal_ui);
		setStatusBar();
		initview();
	}

	protected void initview() {
//        initTopBarForBoth("基本资料", R.drawable.submit_ok, new HeaderLayout.onRightImageButtonClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
		initTopBarForBothWithText("基本资料", "提交", new HeaderLayout.onRightImageButtonClickListener() {
			@Override
			public void onClick(View v) {
//                Intent intent = new Intent(PersonalUI.this, MainActivity.class);
//                startActivity(intent);

				submit();
			}
		}, R.drawable.base_action_bar_back_bg_selector, new HeaderLayout.onLeftImageButtonClickListener() {
			@Override
			public void onClick() {
				PersonalUI.this.finish();
			}
		});

		icon_view = (ImageView) findViewById(R.id.img_tou);
		icon_view.setOnClickListener(new View.OnClickListener() {
			//
			@Override
			public void onClick(View v) {

				Intent local = new Intent();
				local.setType("image/*");
				//local.setAction(Intent.ACTION_GET_CONTENT)
				/*
				*	这里用是 Intent.ACTION_PICK
				*	原来的Intent.ACTION_GET_CONTENT 在Android4.4 之后不能返回图片的路径
				*	会报空指针异常
				 */
				local.setAction(Intent.ACTION_PICK);
				startActivityForResult(local, 2);
			}
		});

		name_et = (EditText) findViewById(R.id.name_edt);
		sex_et = (EditText) findViewById(R.id.gen_edt);
		address_et = (EditText) findViewById(R.id.adr_edt);
		contact_et = (EditText)findViewById(R.id.contact_edt);
		service_et = (EditText) findViewById(R.id.dui_edt);
//        sex_et.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SingleChoiceDialog.Builder builder = new SingleChoiceDialog.Builder();
//                SingleChoiceDialog dialog = builder
//                        .setTitle("选择性别")
//                        .setData(new String[]{"男", "女"}, selected_sex)// 设置单选列表的数据和监听
//                        .setOnItemSelectedListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(DialogInterface dialog, int position) {
//                                if (position == 0) {
//                                    sex_et.setText("男");
//                                } else {
//                                    sex_et.setText("女");
//                                }
//                                selected_sex = position;
//                                flag = true;
//                                dialog.dismiss();
//                            }
//                        })
//                        .create();
//                dialog.setCancelable(true);
//                dialog.show(getSupportFragmentManager(), "SEX");

		sex_et.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				ActionSheet.createBuilder(PersonalUI.this, getSupportFragmentManager())
						.setCancelButtonTitle("取消")
						.setOtherButtonTitles("男","女")
						.setCancelableOnTouchOutside(true)
						.setListener(new ActionSheet.ActionSheetListener() {
							@Override
							public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

							}

							@Override
							public void onOtherButtonClick(ActionSheet actionSheet, int position) {
								if (position == 0) {
									sex_et.setText("男");
								} else {
									sex_et.setText("女");
								}
								selected_sex = position;
								flag = true;
							}
						}).show();
			}
		});


		update_or_complete = getIntent().getBooleanExtra("update_or_complete", false);
		if (update_or_complete) {
			// 更新信息，此时是修改用户信息，不是完成用户信息
			updateUserInfo();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == 2) {

			try{
				Uri uri = data.getData();
				System.out.println("----------------"+uri);
				// icon_view.setImageURI(uri);
				ContentResolver cr = this.getContentResolver();
				Cursor c = cr.query(uri,null, null, null, null);
				c.moveToFirst();
				//这是获取的图片保存在sdcard中的位置
				srcPath = c.getString(c.getColumnIndex("_data"));

			}catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println(srcPath + "----------保存路径2");
			// 保存图片位置
			header_address = PhotoUtil.getSmallBitmap(srcPath, 360, 480);
			icon_view.setImageBitmap(PhotoUtil.getBitmap(header_address, 360, 480));
		}
	}

	public void submit() {
		// 首先判断资料是否填写完整
		if (!flag || name_et.getText().toString().isEmpty() || address_et.getText().toString().isEmpty() ||
				contact_et.getText().toString().isEmpty() || service_et.getText().toString().isEmpty()) {
			ShowToast("请将资料填写完整");
			return;
		} else {
			// 提交用户资料
			// 首先上传用户头像
			if (dialog == null) {
				dialog = new ProgressDialog(PersonalUI.this);
			}
			dialog.setMessage("正在提交...");
			dialog.setCancelable(false);
			dialog.show();
			ShowLog("提交");

			// 如果是true的话，就是更新用户信息
			if (update_or_complete && header_address.isEmpty()) {
				// 没有改头像
				User user = UserStatus.getLoginedUser();
				completeUserInfo(user.getHeader());
			} else  {
				final String fileKey = "file";
				String requestURL = URL_PREFIX + "accept_image.php";

				RequestParams params = new RequestParams();
				try {
					params.put(fileKey, new File(header_address));
				} catch (Exception e) {
					e.printStackTrace();
				}

				AsyncHttpClient client = new AsyncHttpClient();
				client.post(requestURL, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						ShowLog("上传完成");

						// 上传完成之后删除保存的文件
						PhotoUtil.deleteTempFile(header_address);

						String response = new String(bytes);
						ShowLog(response);

						if (response.length() < 40) {
							ShowToast("头像上传失败");
							dialog.dismiss();
						} else {
							// 解析json数据
							Gson gson = new Gson();
							Type type = new TypeToken<SuccessResponse<String>>() {
							}.getType();
							SuccessResponse<String> successResponse = gson.fromJson(response, type);
							final String headerAddress = URL_PREFIX + successResponse.getData();

							completeUserInfo(headerAddress);
						}
					}

					@Override
					public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
						ShowToast("上传失败");
						dialog.dismiss();
					}
				});

			}
		}

	}

	private void naviToMainActivity() {
		Intent intent = new Intent(PersonalUI.this, MainActivity.class);
		startActivity(intent);
		PersonalUI.this.finish();
	}

	private void completeUserInfo(final String headerAddress) {
		// 请求，完成用户信息
		String tag_complete_info_req = "complete_info_req";
		String url = URL_PREFIX + "complete_user_info.php";
		StringRequest complete_user_info_request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// 提交成功
				// 更新本地保存的用户信息
				List<User> users = User.findWithQuery(User.class, "Select * from User");
				users.get(0).setHeader(headerAddress);
				users.get(0).setName(name_et.getText().toString());
				users.get(0).setSex(selected_sex);
				users.get(0).setAddress(address_et.getText().toString());
				users.get(0).setContact(contact_et.getText().toString());
				users.get(0).setService_team(service_et.getText().toString());
				users.get(0).save();

				// 进入主界面
				naviToMainActivity();
				dialog.dismiss();
				PersonalUI.this.finish();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ShowToast("提交失败，请重试");
				dialog.dismiss();
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				// 获取userId
				List<User> users = User.findWithQuery(User.class, "Select * from User");

				map.put("userId", users.get(0).getUserId() + "");
				map.put("header", headerAddress);
				map.put("name", name_et.getText().toString());
				map.put("sex", selected_sex + "");
				map.put("address", address_et.getText().toString());
				map.put("contact", contact_et.getText().toString());
				map.put("service_team", service_et.getText().toString());
				return map;
			}
		};

		// 加入RequestQueue
		NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(complete_user_info_request);
	}

	private void updateUserInfo() {
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

		name_et.setText(user.getName());
		selected_sex = user.getSex();
		if (selected_sex == 0) {
			sex_et.setText("男");
		} else {
			sex_et.setText("女");
		}
		flag = true;
		address_et.setText(user.getAddress());
		contact_et.setText(user.getContact());
		service_et.setText(user.getService_team());
	}
}
