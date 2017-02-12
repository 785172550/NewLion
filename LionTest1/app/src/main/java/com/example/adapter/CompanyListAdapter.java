package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.com.example.base.BaseListAdapter;
import com.example.entity.Company;
import com.example.sean.liontest1.R;
import com.example.ui.ComShow;
import com.example.util.NetworkSingleton;

import java.util.List;

/**
 * Created by Deep on 16/3/19.
 */
public class CompanyListAdapter extends BaseListAdapter {

    List<Company> data;
    Context context;

    public CompanyListAdapter(Context context, List list) {
        super(context, list);
        this.data = list;
        this.context = context;
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.company_list_item, null);
        }

        holder.show_photo = (ImageView)convertView.findViewById(R.id.company_show_iv);
        holder.company_name_tv = (TextView)convertView.findViewById(R.id.company_name);
        holder.company_user_name_tv = (TextView)convertView.findViewById(R.id.compnay_user_name);

        Company company = data.get(position);
        // 异步加载图片
        holder.company_name_tv.setText(company.getCompany_name());
        holder.company_user_name_tv.setText(company.getName());

        String[] photoArray = company.getShow_photo().split(";");

        ImageRequest request = new ImageRequest(photoArray[0], new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.show_photo.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComShow.class);
                intent.putExtra("company", data.get(position));
                context.startActivity(intent);
            }
        });

        NetworkSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(request);

        return convertView;
    }

    public void updateListData(List list) {
        this.data = list;
        this.list = list;
        this.notifyDataSetInvalidated();
    }

    class ViewHolder {
        ImageView show_photo;
        TextView company_name_tv, company_user_name_tv;
    }
}
