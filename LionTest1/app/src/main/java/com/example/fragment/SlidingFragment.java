package com.example.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.adapter.SlidingAdater;
import com.example.com.example.base.BaseActivity;
import com.example.entity.SuccessResponse;
import com.example.model.User;
import com.example.sean.liontest1.R;
import com.example.ui.AboutUs;
import com.example.ui.AddHuoDong;
import com.example.ui.FanKui;
import com.example.ui.GaoDeSearch;
import com.example.ui.LogUI;
import com.example.ui.MySetting;
import com.example.ui.Profile;
import com.example.ui.SouSuoQiYe;
import com.example.util.NetworkSingleton;
import com.example.util.UserStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/3/8.
 */
public class SlidingFragment extends Fragment {


    private View mView;
    private Context context;
    private List<String> list;
    Button click_to_login;
    TextView name_tv;
    CircleImageView header_imageView;

    ProgressDialog progressDialog;

    public SlidingFragment() {
        super();
    }

    public SlidingFragment(Context context) {
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            initView(inflater, container);
        }
        return mView;
    }

    protected void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.slide_main, container, false);
        list = new ArrayList<>();
        list.add("商家地图");
        list.add("添加活动");
        list.add("个人中心");
        list.add("搜索企业");
        list.add("设置");
        list.add("反馈");
        list.add("关于");

        click_to_login = (Button) mView.findViewById(R.id.slide_click_login);
        name_tv = (TextView) mView.findViewById(R.id.slide_name);

        click_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 进入登录界面
                Intent intent = new Intent(context, LogUI.class);
                startActivity(intent);
            }
        });

        header_imageView = (CircleImageView) mView.findViewById(R.id.slide_icon);

        // 检查当前是否有用户登录
        checkUser();

        ListView myListView = (ListView) mView.findViewById(R.id.slide_listview);
        myListView.setAdapter(new SlidingAdater(context, list));
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Class<?> cls = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://商家地图
                        cls = GaoDeSearch.class;
                        break;
                    case 1://添加活动

                        if (UserStatus.hasUserLogined() == false) {
                            Toast.makeText(context.getApplicationContext(), "您还没有登录", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("正在加载...");
                            progressDialog.setCancelable(false);

                            String url = BaseActivity.URL_PREFIX + "check_user_wether_has_company.php";
                            RequestParams params = new RequestParams();
                            params.put("user_id", UserStatus.getLoginedUser().getUserId());

                            final Gson gson = new Gson();

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(url, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    String response = new String(bytes);
                                    // 首先是获取code
                                    String resArray[] = response.split("code");
                                    String codeStr = resArray[1].substring(3, 6);
                                    int code = Integer.parseInt(codeStr);
                                    if (code == 201) { // 出错了
                                        Toast.makeText(context.getApplicationContext(), "加载失败", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        return;
                                    } else if (code == 200) {
                                        // 解析JSON
                                        Type type = new TypeToken<SuccessResponse<String>>() {
                                        }.getType();
                                        SuccessResponse<String> successResponse = gson.fromJson(response, type);

                                        if (successResponse.getData().equals("0")) {
                                            // 没有公司
                                            Toast.makeText(context.getApplicationContext(), "请到个人中心添加公司", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        } else if (successResponse.getData().equals("1")) {
                                            // 有公司，进入添加活动界面
                                            Intent intent = new Intent(context, AddHuoDong.class);
                                            startActivity(intent);
                                        }

                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                    Toast.makeText(context.getApplicationContext(), "加载失败", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    return;
                                }
                            });
                        }

                        // cls = AddHuoDong.class;
                        break;
                    case 2://个人中心
                        // 首先检查是否有用户登录
                        if (UserStatus.hasUserLogined() == false) {
                            // 进入到登录页面
                            Toast.makeText(context.getApplicationContext(), "您还没有登录", Toast.LENGTH_LONG).show();
                            // naviToLoginActivity();
                            return;
                        }
                        cls = Profile.class;
                        break;
                    case 3://搜索企业
                        cls = SouSuoQiYe.class;
                        break;
                    case 4://设置
                        cls = MySetting.class;
                        break;
                    case 5://反馈
                        cls = FanKui.class;
                        break;
                    case 6://关于
                        cls = AboutUs.class;
                        break;
                }

                if (position != 1) {
                    Intent intent = new Intent(context, cls);
                    startActivity(intent);
                }

                // Toast.makeText(context, list.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView icon = (ImageView) mView.findViewById(R.id.slide_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView name_tv = (TextView) mView.findViewById(R.id.slide_name);
        name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void checkUser() {
        // 判断当前是否有用户登录
        // 首先判断是否有用户登录了
        List<User> users = User.findWithQuery(User.class, "Select * from User");
        if (users.size() == 0) {
            // 当前咩有用户登录，进入主界面
            name_tv.setVisibility(View.INVISIBLE);
            click_to_login.setVisibility(View.VISIBLE);
        } else {
            Log.i("deeplee", "有用户登录");
            name_tv.setVisibility(View.VISIBLE);
            click_to_login.setVisibility(View.INVISIBLE);
            name_tv.setText(users.get(0).getName());
            updateUserHeader(users.get(0));
        }
    }

    private void updateUserHeader(User user) {
        Log.i("deeplee", user.getHeader());
        ImageRequest request = new ImageRequest(user.getHeader(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                header_imageView.setImageBitmap(response);
                Log.i("deeplee", "获取了头像");
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
               // Log.i("deeplee", error.getMessage());
            }
        });

        NetworkSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }

    private void naviToLoginActivity() {
        Intent intent = new Intent(context, LogUI.class);
        context.startActivity(intent);
    }
}
