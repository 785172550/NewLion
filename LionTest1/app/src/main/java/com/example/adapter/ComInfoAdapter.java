package com.example.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.example.base.BaseListAdapter;
import com.example.sean.liontest1.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/14.
 */
public class ComInfoAdapter extends BaseListAdapter {

    List<Map<String, String>> data;

	public ComInfoAdapter(Context context, List list) {
		super(context, list);
        this.data = list;
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.cominfo_list_item,null);

		AbsListView.LayoutParams pl = new
				AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150);
		convertView.setLayoutParams(pl);

		TextView tv1 = (TextView) convertView.findViewById(R.id.cominfo_tv1);
        Map<String, String> map = (Map<String, String>)list.get(position);
		tv1.setText(map.get("1"));

		TextView tv2 = (TextView) convertView.findViewById(R.id.cominfo_details);
        tv2.setText(map.get("2"));

		return convertView;
	}

    public void updateList(List list) {
        this.data = list;
        this.notifyDataSetInvalidated();
    }
}
