
package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.com.example.base.BaseActivity;
import com.example.com.example.base.BaseListAdapter;
import com.example.entity.Project;
import com.example.entity.SuccessResponse;
import com.example.sean.liontest1.R;
import com.example.ui.ProjectDetailsActivity;
import com.example.util.NetworkSingleton;
import com.example.util.UserStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/7.
 */
public class MainListAdapter extends BaseListAdapter {

    List<Project> data;
    Context context;

    public MainListAdapter(Context context, List list) {
        super(context, list);
        this.data = list;
        this.context = context;
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_list_item, null);
            holder = new ViewHolder();

            holder.time_btn = (Button) convertView.findViewById(R.id.time_btn);
            holder.launcher_btn = (Button) convertView.findViewById(R.id.launcher_btn);
            holder.fav_btn = (Button) convertView.findViewById(R.id.fav_btn);
            holder.comment_btn = (Button) convertView.findViewById(R.id.comment_btn);
            holder.time_tv = (TextView) convertView.findViewById(R.id.acticity_time_tv);
            holder.launcher_tv = (TextView) convertView.findViewById(R.id.activity_launcher_tv);
            holder.fav_tv = (TextView) convertView.findViewById(R.id.activity_fav_tv);
            holder.comment_tv = (TextView) convertView.findViewById(R.id.acticity_comment_tv);
            holder.cover_image = (ImageView) convertView.findViewById(R.id.cover_image);
            holder.title_tv = (TextView) convertView.findViewById(R.id.activity_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Project project = data.get(position);
        holder.title_tv.setText(project.getTitle());

        // 加载封面照片
        final ImageRequest request = new ImageRequest(project.getCover_image(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.cover_image.setImageBitmap(response);

            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        });
        NetworkSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(request);

//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(project.getCover_image(), new BinaryHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);//解析图片
//                holder.cover_image.setImageBitmap(b);
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
//            }
//        });

        holder.time_tv.setText(project.getTime());
        holder.launcher_tv.setText(project.getName());
        holder.fav_tv.setText(project.getFavorite() + "");

        holder.cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("project", data.get(position));
                context.startActivity(intent);
            }
        });

        // 点赞
        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("deeplee", "正在发起请求");

                if (!UserStatus.hasUserLogined()) {
                    ShowToast("您还没有登录");
                    return;
                }

                // 发起请求
                String tag_fav_req = "fav_req";
                String url = BaseActivity.URL_PREFIX + "add_fav.php";

                final Gson gson = new Gson();
                StringRequest fav_req = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.i("deeplee", response);

                                // 首先是获取code
                                String resArray[] = response.split("code");
                                String codeStr = resArray[1].substring(3, 6);
                                int code = Integer.parseInt(codeStr);
                                if (code == 201) { // 出错了
                                    // 获取type的值
                                    String typeStr = response.substring(9, 12);
                                    int type = Integer.parseInt(typeStr);
                                    if (type == 100 || type == 101) {
                                        Toast.makeText(context, "点赞失败，请重试", Toast.LENGTH_LONG).show();
                                    } else if (type == 102) {
                                        Toast.makeText(context, "您已经赞过了", Toast.LENGTH_LONG).show();
                                    }
                                } else if (code == 200) { // 正确的返回

                                    // 解析JSON
                                    Type type = new TypeToken<SuccessResponse<String>>() {
                                    }.getType();
                                    SuccessResponse<String> successResponse = gson.fromJson(response, type);
                                    project.setFavorite(project.getFavorite() + 1);
                                    // 点赞成功
                                    holder.fav_tv.setText(project.getFavorite() + "");
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("deeplee", error.getMessage());
                                Toast.makeText(context, "点赞失败，请重试", Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        com.example.model.User user = UserStatus.getLoginedUser();

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("user_id", "" + user.getUserId());
                        map.put("project_id", "" + project.getId());
                        return map;
                    }
                };

                NetworkSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(fav_req);
            }
        });

        return convertView;
    }

    public void updateListData(List list) {
        this.data = list;
        this.list = list;
        this.notifyDataSetInvalidated();
    }

    static class ViewHolder {
        Button time_btn, launcher_btn, fav_btn, comment_btn;
        TextView time_tv, launcher_tv, fav_tv, comment_tv, title_tv;
        ImageView cover_image;
    }
}

