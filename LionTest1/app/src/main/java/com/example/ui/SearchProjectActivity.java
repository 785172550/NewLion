package com.example.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.adapter.MainListAdapter;
import com.example.com.example.base.BaseActivity;
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
import java.util.List;

public class SearchProjectActivity extends BaseActivity {

    ImageButton back_btn;
    EditText search_et;
    ListView project_list;

    List<Project> list;
    private MainListAdapter adapter;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_project);
        setStatusBar();

        initView();
    }

    private void initView() {
        back_btn = (ImageButton) findViewById(R.id.header_ib_imagebutton);
        search_et = (EditText) findViewById(R.id.search_project_et);
        project_list = (ListView) findViewById(R.id.project_search_list);

        final List<Project> list = new ArrayList<>();
        adapter = new MainListAdapter(SearchProjectActivity.this, list);
        project_list.setAdapter(adapter);

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
                SearchProjectActivity.this.finish();
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
        String url = URL_PREFIX + "search_project.php";

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
                        Type type = new TypeToken<SuccessResponse<Project[]>>() {
                        }.getType();
                        SuccessResponse<Project[]> succResponse = gson.fromJson(response, type);

                        ShowLog("长度为：" + succResponse.getData().length);
                        List<Project> data = Arrays.asList(succResponse.getData());
                        list = data;
                        adapter.updateListData(data);
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

}
