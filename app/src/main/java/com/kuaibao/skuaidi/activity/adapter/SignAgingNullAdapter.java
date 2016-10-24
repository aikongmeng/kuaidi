package com.kuaibao.skuaidi.activity.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class SignAgingNullAdapter extends BaseAdapter {
	private Context context;
	private String str[];
	private TextView tv_sign_notice;

	public SignAgingNullAdapter(Context context, String str[]) {
		this.context = context;
		this.str = str;

	}

	@Override
	public int getCount() {
		return str.length;
	}

	@Override
	public String getItem(int position) {
		return str[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup ViewGroup) {

		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.sign_aging_item_null, null);
			tv_sign_notice = (TextView) convertView
					.findViewById(R.id.tv_sign_notice);
			// ViewTouchDelegate.expandViewTouchDelegate(holder.check_pic, 6, 6,
			// 6, 6);// 扩大点击区域
			AbsListView.LayoutParams lp=new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			convertView.setLayoutParams(lp);
			tv_sign_notice.setText(str[position].toString());
		}

		return convertView;

	}

}
