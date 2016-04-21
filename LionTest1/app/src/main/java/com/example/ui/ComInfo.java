package com.example.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.example.adapter.ComInfoAdapter;
import com.example.com.example.base.BaseActivity;
import com.example.entity.Company;
import com.example.entity.SuccessResponse;
import com.example.model.User;
import com.example.sean.liontest1.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/7.
 */
public class ComInfo extends BaseActivity {

    //公司信息填写列表
    ListView ComInfo_listview;
    List<Map<String, String>> mlist;
    ComInfoAdapter comInfoAdapter;
    ArrayList<String> photoArray;

	private EditText etName;//拓展View内容
	private InputMethodManager imm;
	private AlertView mAlertViewExt;//窗口拓展例子

    private String selected_position_lat = "";  // 选择的地点的纬度
    private String selected_positios_long = ""; // 选择的地点的经度

    private ProgressDialog progressDialog;

    private Boolean hasCompanyFlag = false; // 标志用户是否已经添加了公司信息

    private Company company = null;

    private int selected_industry = -1;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                comInfoAdapter.updateList(mlist);
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_cominfo);
        setStatusBar();
        initview();
    }

    private void initview() {

        initTopBarForBothWithText("公司信息", "保存", new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 先判断用户信息有没有填写完整
                        for (Map<String, String> item : mlist) {
                            if (item.get("2").isEmpty()) {
                                ShowToast("请将信息填写完整");
                                return;
                            }
                        }
                        save();
                    }
                },
                R.drawable.base_action_bar_back_bg_selector, new HeaderLayout.onLeftImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        ComInfo.this.finish();
                    }
                });

        mlist = new ArrayList<Map<String, String>>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("1", "公司名称");
        map1.put("2", "");
        mlist.add(map1);

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("1", "公司地址");
        map2.put("2", "");
        mlist.add(map2);

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("1", "经营范围");
        map3.put("2", "");
        mlist.add(map3);

        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("1", "所属行业");
        map4.put("2", "");
        mlist.add(map4);

        Map<String, String> map5 = new HashMap<String, String>();
        map5.put("1", "简介");
        map5.put("2", "");
        mlist.add(map5);

        Map<String, String> map6 = new HashMap<>();
        map6.put("1", "联系方式");
        map6.put("2", "");
        mlist.add(map6);

        Map<String, String> map7 = new HashMap<String, String>();
        map7.put("1", "企业图片展示");
        map7.put("2", "");
        mlist.add(map7);

        ComInfo_listview = (ListView) findViewById(R.id.cominfo_listview);
        comInfoAdapter = new ComInfoAdapter(this, mlist);
        ComInfo_listview.setAdapter(comInfoAdapter);
        ComInfo_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 2 || position == 4 || position == 5) {
                    showInputDialog(position);
                } else if (position == 1) { // 进入地图选择公司地址
                    Intent sintent = new Intent(ComInfo.this,GaoDeSearchComInfo.class);
                    startActivityForResult(sintent, 2);
                } else if (position == 3) { // 选择行业
                    showSelectDialog(position, BUSINESS_SCOPE);
                } else if (position == 6) { // 选择展示图片

                    Intent choose_intent = new Intent(ComInfo.this,ImageChoose.class);
                    startActivityForResult(choose_intent, 1);
                }
            }
        });


        // 向服务器发起请求，判断当前用户有没有添加公司信息
        checkUserCompanyInfo();
    }

    private void checkUserCompanyInfo() {
        // 发起请求
        // 首先上传图片
        String url = URL_PREFIX + "company_of_user.php";
        // 设置请求参数
        RequestParams params = new RequestParams();
        User user = UserStatus.getLoginedUser();
        params.put("user_id", "" + user.getUserId());

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("正在加载公司信息...");
        progressDialog.show();

        final Gson gson = new Gson();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String response = new String(bytes);
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
						// 请求失败
						ShowToast("获取公司信息失败，请重试");
					} else if (type == 102) {
						// 没有公司信息
						ShowLog("没有公司信息");
						hasCompanyFlag = false;

						ShowLog("Deeplee: " + hasCompanyFlag);
					}

					progressDialog.dismiss();

				} else if (code == 200) {
					// 获取到了公司信息
					// 解析JSON
					Type type = new TypeToken<SuccessResponse<Company>>() {
					}.getType();
					SuccessResponse<Company> successResponse = gson.fromJson(response, type);
					company = successResponse.getData();
					// 刷新界面
					mlist.get(0).put("2", company.getCompany_name());
					mlist.get(1).put("2", company.getAddress_longitude() + ";" + company.getAddress_latitude());
					mlist.get(2).put("2", company.getBusiness_scope());
					mlist.get(3).put("2", BUSINESS_SCOPE[company.getIndustry()]);
					mlist.get(4).put("2", company.getIntroduction());
					mlist.get(5).put("2", company.getContact());

					selected_positios_long = company.getAddress_longitude();
					selected_position_lat = company.getAddress_latitude();
					selected_industry = company.getIndustry();

					String[] showPhotoArray = company.getShow_photo().split(";");

					mlist.get(6).put("2", "已选择" + showPhotoArray.length + "张照片");

					hasCompanyFlag = true;

					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
					progressDialog.dismiss();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				// 获取公司信息失败，请重试
				ShowToast("获取公司信息失败，请重试");
				progressDialog.dismiss();
			}
		});
    }

	private void showInputDialog(final int position){

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form,null);
		etName = (EditText) extView.findViewById(R.id.etName);
        etName.setText(mlist.get(position).get("2"));

        if (position == 5) {
            etName.setInputType(InputType.TYPE_CLASS_PHONE);
        }

		etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focus) {
				//输入框出来则往上移动
				boolean isOpen = imm.isActive();
				mAlertViewExt.setMarginBottom(isOpen && focus ? 200 : 0);
				System.out.println(isOpen);
			}
		});

		RelativeLayout.LayoutParams lp = new
				RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(8,0,8,0);
        etName.setLayoutParams(lp);

		mAlertViewExt = new AlertView("请输入", null, "取消",
				null, new String[]{"确定"}, this, AlertView.Style.Alert,
				new com.bigkoo.alertview.OnItemClickListener(){
			@Override
			public void onItemClick(Object o, int i) {
				closeKeyboard();
				if(o == mAlertViewExt && i != AlertView.CANCELPOSITION){
					String intput = etName.getText().toString();
					if(intput.isEmpty()){
						Toast.makeText(ComInfo.this, "啥都没填呢", Toast.LENGTH_SHORT).show();
					}
					else{
						mlist.get(position).put("2", intput);
						// 更新ListView
						comInfoAdapter.updateList(mlist);
					}
					return;
				}
			}
		});
		mAlertViewExt.setCancelable(true);
		mAlertViewExt.addExtView(extView);
		mAlertViewExt.show();
	}

  /*  private void showInputDialog(final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View input_view = inflater.inflate(R.layout.dialog_input, null);
        final EditText input_et = (EditText) input_view.findViewById(R.id.input_et);
        input_et.setText(mlist.get(position).get("2"));

        if (position == 5) {
            input_et.setInputType(InputType.TYPE_CLASS_PHONE);
        }

        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle("请输入")
                .setContentView(input_view)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                        mlist.get(position).put("2", input_et.getText().toString());
                        // 更新ListView
                        comInfoAdapter.updateList(mlist);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                    }
                });
        materialDialog.show();
    }*/

	private void showSelectDialog(final int position, String[] data){
		new AlertView("请选择", null, "取消", null, data, this, AlertView.Style.ActionSheet,
				new com.bigkoo.alertview.OnItemClickListener() {
					@Override
					public void onItemClick(Object o, int i) {
						if(i != AlertView.CANCELPOSITION){
							mlist.get(position).put("2", BUSINESS_SCOPE[i]);
							selected_industry = i;
							// 更新ListView
							comInfoAdapter.updateList(mlist);
						}
					}
				}).setCancelable(true).show();
	}

/*    private void showSelectDialog(final int position, String[] data) {
        SingleChoiceDialog.Builder builder = new SingleChoiceDialog.Builder();
        SingleChoiceDialog dialog = builder
                .setTitle("请选择")
                .setData(data, 0)
                .setOnItemSelectedListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mlist.get(position).put("2", BUSINESS_SCOPE[i]);
                        selected_industry = i;
                        // 更新ListView
                        comInfoAdapter.updateList(mlist);
                    }
                }).create();
        dialog.show(getSupportFragmentManager(), "TAG");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShowLog("返回了");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {
                photoArray = data.getStringArrayListExtra("photos");
                mlist.get(6).put("2", "已选择" + photoArray.size() + "张图片");
                // 更新ListView
                comInfoAdapter.updateList(mlist);
            } else if (requestCode == 2) {
                ShowLog("收到了地址");
                selected_position_lat = data.getStringExtra("selected_position_lat");
                selected_positios_long = data.getStringExtra("selected_positios_long");
                mlist.get(1).put("2", selected_positios_long + ";" + selected_position_lat);
                comInfoAdapter.updateList(mlist);
            }
        }
    }

    public void save() {
        ShowLog("点击了");
        // 发起请求
        // 首先上传图片
        String url = URL_PREFIX + "accept_multi_image.php";
        // 设置请求参数
        RequestParams params = new RequestParams();
        try {
            for (int i = 0; i < photoArray.size(); i++) {
                File file = new File(photoArray.get(i));
                params.put("file" + i, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }

        progressDialog.setMessage("正在保存...");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String response = new String(bytes);
				Gson gson = new Gson();
				// 首先是获取code
				String resArray[] = response.split("code");
				ShowLog(resArray[1]);
				String codeStr = resArray[1].substring(3, 6);
				int code = Integer.parseInt(codeStr);
				if (code == 201) { // 出错了
					ShowLog("保存失败，请重试");
				} else if (code == 200) {
					// 上传成功
					// 解析JSON
					Type type = new TypeToken<SuccessResponse<String>>() {
					}.getType();
					SuccessResponse<String> successResponse = gson.fromJson(response, type);
					// 获取data数据，也就是图片的地址
					if (!hasCompanyFlag) {
						// 没有公司，新插入公司
						insertCompanyInfo(successResponse.getData());
					} else {
						// 有公司，更新公司信息
						updateCompanyInfo(successResponse.getData());
					}
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				ShowLog("保存失败，请重试");
			}
		});

    }

    // 插入公司信息
    public void insertCompanyInfo(String photos) {
        ShowLog("插入新的公司");
        String url = URL_PREFIX + "add_company.php";
        // 设置请求参数
        RequestParams params = new RequestParams();
        params.put("user_id", UserStatus.getLoginedUser().getUserId());
        params.put("company_name", mlist.get(0).get("2"));
        params.put("address_longitude", selected_positios_long);
        params.put("address_latitude", selected_position_lat);
        params.put("business_scope", mlist.get(2).get("2"));
        params.put("industry", selected_industry);
        params.put("show_photo", photos);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        params.put("create_time", date);
        params.put("introduction", mlist.get(4).get("2"));
        params.put("contact", mlist.get(5).get("2"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // 首先是获取code
                String response = new String(bytes);
                ShowLog("deeplee" + response);
                String resArray[] = response.split("code");
                ShowLog(resArray[1]);
                String codeStr = resArray[1].substring(3, 6);
                int code = Integer.parseInt(codeStr);
                if (code == 201) { // 出错了
                    // 获取type的值
                    ShowToast("保存失败，请重试");
                    progressDialog.dismiss();

                } else if (code == 200) {
                    // 保存成功
                    ShowToast("保存成功");
                    progressDialog.dismiss();
                    ComInfo.this.finish();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ShowToast("保存失败，请重试");
                progressDialog.dismiss();
            }
        });
    }

    // 更新公司信息
    public void updateCompanyInfo(String photos) {
        ShowLog("更新公司信息");
        String url = URL_PREFIX + "update_company_info.php";
        // 设置请求参数
        RequestParams params = new RequestParams();
        params.put("company_id", company.getId());
        params.put("company_name", mlist.get(0).get("2"));
        params.put("address_longitude", selected_positios_long);
        params.put("address_latitude", selected_position_lat);
        params.put("business_scope", mlist.get(2).get("2"));
        params.put("industry", selected_industry);
        params.put("show_photo", (photos == null || photos.isEmpty()) ? company.getShow_photo() : photos);
        params.put("introduction", mlist.get(4).get("2"));
        params.put("contact", mlist.get(5).get("2"));

        ShowLog("所属行业：" + selected_industry);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // 首先是获取code
                String response = new String(bytes);
                ShowLog("deeplee" + response);
                String resArray[] = response.split("code");
                ShowLog(resArray[1]);
                String codeStr = resArray[1].substring(3, 6);
                int code = Integer.parseInt(codeStr);
                if (code == 201) { // 出错了
                    // 获取type的值
                    ShowToast("保存失败，请重试");
                    progressDialog.dismiss();

                } else if (code == 200) {
                    // 保存成功
                    ShowToast("保存成功");
                    progressDialog.dismiss();
                    ComInfo.this.finish();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ShowToast("保存失败，请重试");
                progressDialog.dismiss();
            }
        });

    }

	private void closeKeyboard() {
		//关闭软键盘
		imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
		//恢复位置
		mAlertViewExt.setMarginBottom(0);
	}
}
