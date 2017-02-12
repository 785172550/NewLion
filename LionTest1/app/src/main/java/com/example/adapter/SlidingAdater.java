package com.example.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.example.base.BaseListAdapter;
import com.example.sean.liontest1.R;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 *
 */
public class SlidingAdater extends BaseListAdapter{

	int iconId[] = {
			R.drawable.slide_globe_selector,
			R.drawable.huodong_selector,
			R.drawable.profile_selector,
			R.drawable.sousuo_selector,
			R.drawable.mysetting_selector,
			R.drawable.fan_kui_selector,
			R.drawable.myabout_selector
	};
	public SlidingAdater(Context context){
		super();
		this.mContext = context;
	}
	public SlidingAdater(Context context, List list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = mInflater.inflate(R.layout.slide_list_item,null);
		}

		//设置每个 item 高度
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 90);
		convertView.setLayoutParams(lp);

		ImageView iv_item = (ImageView) convertView.findViewById(R.id.iv_item);
		TextView tv = (TextView) convertView.findViewById(R.id.name_item);
		tv.setText(list.get(position).toString());
		iv_item.setImageResource(iconId[position]);

		return convertView;
	}
}
