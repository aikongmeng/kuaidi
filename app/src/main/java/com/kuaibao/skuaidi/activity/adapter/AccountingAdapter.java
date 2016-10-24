package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class AccountingAdapter extends BaseAdapter {
	private Context mContext;
	private List<NotifyInfo> mList;
	private TextView tv_orderNo_name;

	public AccountingAdapter(Context context,List<NotifyInfo> infos) {
		mList = infos;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public NotifyInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.item_account_adapter, null);
		tv_orderNo_name = (TextView) convertView.findViewById(R.id.tv_orderNo_name);
		if(!Utility.isEmpty(getItem(position).getExpress_number())){
			tv_orderNo_name.setText(getItem(position).getExpress_number());
		}
		return convertView;
	}

}
