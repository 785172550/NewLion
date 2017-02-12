package com.example.sean.liontest1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.adapter.MainListAdapter;
import com.example.com.example.base.BaseActivity;
import com.example.entity.Project;
import com.example.entity.SuccessResponse;
import com.example.fragment.SlidingFragment;
import com.example.ui.SearchProjectActivity;
import com.example.util.NetworkSingleton;
import com.example.view.HeaderLayout;
import com.example.view.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements XListView.IXListViewListener{

	private static SlidingMenu slidingMenu;
	private XListView mListView;
	RelativeLayout r_zhezhao;
	private MainListAdapter adapter;

    List<Project> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setStatusBar();
		initTopBarForBoth("聚合宝",
				R.mipmap.search_btn,
				new HeaderLayout.onRightImageButtonClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SearchProjectActivity.class);
				startActivity(intent);
			}
		},
				R.mipmap.list,new HeaderLayout.onLeftImageButtonClickListener() {

			@Override
			public void onClick() {
				//左边list按钮
				slidingMenu.showMenu();} });

		r_zhezhao = (RelativeLayout) findViewById(R.id.r_zhezhao);
		r_zhezhao.setAlpha(0);
		//下拉刷新 上滑加载更多
		initXlistview();
		//侧滑
		initSlidingMenu();


	}

	public void initSlidingMenu(){

		slidingMenu = new SlidingMenu(MainActivity.this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		//值越大，侧滑越窄
		slidingMenu.setBehindOffset(200);
		slidingMenu.setBehindScrollScale(1);
		slidingMenu.setShadowDrawable(R.drawable.xlistview_arrow);
		slidingMenu.setFadeDegree(1);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slide_layout);

		// 设置专场动画效果
		slidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
				//主界面遮罩效果
				r_zhezhao.setAlpha(percentOpen < 0.5 ? 0 : (float) (percentOpen * 255));
			}
		});

	/*	slidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});*/

		android.support.v4.app.Fragment slideFragment = new SlidingFragment(MainActivity.this);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frag_slide, slideFragment).commit();

	}

	public void initXlistview(){

		mListView = (XListView) findViewById(R.id.main_xlistview);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		// mListView.pullRefreshing();
		mListView.setDividerHeight(0);

		final List<Project> list = new ArrayList<>();
        adapter = new MainListAdapter(MainActivity.this, list);
		mListView.setAdapter(adapter);
//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ShowLog("Clicked");
//
//			}
//		});
        queryProjectData(true);
	}


	private Handler handler = new Handler();

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
//		handler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				ShowToast("刷新完成!");
//				mListView.stopRefresh();
//			}
//		}, 1000);

        // 刷新
        queryProjectData(false);
	}

	@Override
	public void onLoadMore() {

	}

	public static void showslide(){
		slidingMenu.showMenu();
	}

    // 向服务器请求数据
    public void queryProjectData(Boolean flag) {
        // 发起请求
        String tag_register_req = "query_project_req";
        String url = URL_PREFIX + "project_all.php";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("正在加载...");
        if (flag) {
            pDialog.show();
        }
        final Gson gson = new Gson();
        StringRequest query_project_req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShowLog(response);
                        // 响应成功
                        String resArray[] = response.split("code");
                        ShowLog(resArray[1]);
                        String codeStr = resArray[1].substring(3, 6);
                        int code = Integer.parseInt(codeStr);
                        if (code == 201) { // 出错了
                            ShowToast("获取活动数据失败，请刷新重试");
                            pDialog.dismiss();
                            mListView.stopRefresh();
                        } else if (code == 200) {
                            // 获取数据成功
                            // 解析JSON
                            ShowLog("数据获取成功");
                            if (response.length() > 50) {
                                Type type = new TypeToken<SuccessResponse<Project[]>>() {
                                }.getType();
                                SuccessResponse<Project[]> succResponse = gson.fromJson(response, type);

                                ShowLog("长度为：" + succResponse.getData().length);
                                List<Project> data  = Arrays.asList(succResponse.getData());
								list = data;
                                adapter.updateListData(data);
								ShowToast("刷新完成!");
                            }
                        }

                        pDialog.dismiss();
                        mListView.stopRefresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
						if(error.getMessage()!= null)
                        ShowLog(error.getMessage());
                        ShowToast("获取活动数据失败，请刷新重试");
                        pDialog.dismiss();
                        mListView.stopRefresh();
                    }
                });

        NetworkSingleton.getInstance(this).addToRequestQueue(query_project_req);
    }
}
