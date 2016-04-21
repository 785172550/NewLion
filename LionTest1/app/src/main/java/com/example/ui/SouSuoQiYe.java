package com.example.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.adapter.CompanyListAdapter;
import com.example.com.example.base.BaseActivity;
import com.example.entity.Company;
import com.example.entity.Project;
import com.example.entity.SuccessResponse;
import com.example.sean.liontest1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/9.
 * 搜索企业
 */
public class SouSuoQiYe extends BaseActivity {

	ImageButton back_btn;
	EditText search_et;
	ListView company_list;

    RelativeLayout suggest_layout;

	List<Company> list;
	private CompanyListAdapter adapter;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sousuo);
		setStatusBar();
		initview();
	}

	private void initview() {

		//初始化热门行业
		initGridview1();
		//初始化热门行业
		initGridview2();

        back_btn = (ImageButton) findViewById(R.id.header_ib_imagebutton);
        search_et = (EditText) findViewById(R.id.search_qiye_et);
        company_list = (ListView) findViewById(R.id.search_list);

        suggest_layout = (RelativeLayout)findViewById(R.id.suggest_layout);

        final List<Project> list = new ArrayList<>();
        adapter = new CompanyListAdapter(SouSuoQiYe.this, list);
        company_list.setAdapter(adapter);

        search_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == i && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (!search_et.getText().toString().isEmpty()) {
                        search();
                    }
                    return true;
                }
                return false;
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SouSuoQiYe.this.finish();
            }
        });
	}

    private void search() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }

        dialog.setMessage("正在搜索...");
        dialog.show();

        // 发起请求
        String url = URL_PREFIX + "search_company.php";

        RequestParams params = new RequestParams();
        params.put("search_text", search_et.getText().toString());

        final Gson gson = new Gson();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // 响应成功
                String response = new String(bytes);
                String resArray[] = response.split("code");
                ShowLog(resArray[1]);
                String codeStr = resArray[1].substring(3, 6);
                int code = Integer.parseInt(codeStr);
                if (code == 201) { // 出错了
                    ShowToast("搜索失败，请重试");
                } else if (code == 200) {
                    if (response.length() > 50) {
                        Type type = new TypeToken<SuccessResponse<Company[]>>() {
                        }.getType();
                        SuccessResponse<Company[]> succResponse = gson.fromJson(response, type);

                        ShowLog("长度为：" + succResponse.getData().length);
                        List<Company> data = Arrays.asList(succResponse.getData());
                        list = data;
                        adapter.updateListData(data);

                        showListView();

                    } else {
                        ShowToast("搜索数据为空");
                    }
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ShowToast("搜索失败，请重试");
                dialog.dismiss();
            }
        });
    }

    public void showListView() {
        company_list.setVisibility(View.VISIBLE);
        suggest_layout.setVisibility(View.INVISIBLE);
    }

	private void initGridview1() {
		//热门行业
		int[] icon1 =
				{R.mipmap.hu_lian_wang, R.mipmap.jiao_yu,
						R.mipmap.fang_di_chan,R.mipmap.can_yin,
						R.mipmap.jin_rong,R.mipmap.qi_che
						,R.mipmap.kuai_xiao,R.mipmap.geng_duo};
		String[] icon1name = {"互联网","教育","房地产","餐饮","金融","汽车","快消","更多"};
		GridView gridView1 = (GridView) findViewById(R.id.grid1);
		List<Map<String,Object>> mlist = new ArrayList();
		for(int i=0;i<icon1.length;i++){
			Map<String,Object> t = new HashMap<String,Object>();
			t.put("image",icon1[i]);
			t.put("text", icon1name[i]);
			mlist.add(t);
		}
		SimpleAdapter grid1adapter = new SimpleAdapter(SouSuoQiYe.this,mlist,
				R.layout.grid_item,new String[]{"image","text"},new int[]{R.id.icon_im1,R.id.text_tv1});
		gridView1.setAdapter(grid1adapter);
		gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}


	private void initGridview2() {
		int[] icon2 =
				{R.mipmap.qi_ye1,R.mipmap.qi_ye2,R.mipmap.qi_ye3,R.mipmap.qi_ye4,R.mipmap.qi_ye5,
						R.mipmap.qi_ye6,R.mipmap.qi_ye7,R.mipmap.qi_ye8,R.mipmap.qi_ye9,
						R.mipmap.qi_ye10,R.mipmap.qi_ye11,R.mipmap.qi_ye12};

		GridView gridView2 = (GridView) findViewById(R.id.grid2);
		List<Map<String,Object>> mlist2 = new ArrayList();
		for(int i=0;i<icon2.length;i++){
			Map t = new HashMap();
			t.put("image",icon2[i]);
			mlist2.add(t);
		}
		SimpleAdapter grid2adapter = new SimpleAdapter(this,mlist2,R.layout.grid2_item,
				new String[]{"image"},new int[]{R.id.icon_im2});
		gridView2.setAdapter(grid2adapter);
		gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}
}
