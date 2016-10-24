package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.util.ExpressFirm;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.List;

public class FindExpressAdapter extends BaseAdapter {
	Context context;
	List<NotifyInfo> expressHistories;

	public FindExpressAdapter(Context context, List<NotifyInfo> expressHistories) {
		super();
		this.context = context;
		this.expressHistories = expressHistories;
	}

	@Override
	public int getCount() {
		return expressHistories.size();
	}

	@Override
	public NotifyInfo getItem(int position) {
		return expressHistories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(R.layout.findexpress_history_item, null);
		holder.iv_findexpresshistory_item_express = (ImageView) convertView.findViewById(R.id.iv_findexpresshistory_item_express);
		holder.tv_findexpresshistory_item_order = (TextView) convertView.findViewById(R.id.tv_findexpresshistory_item_order);
		holder.tv_findexpresshistory_item_remarks = (TextView) convertView.findViewById(R.id.tv_findexpresshistory_item_remarks);
		holder.tv_findexpresshistory_item_status = (ImageView) convertView.findViewById(R.id.express_sign);

		holder.iv_findexpresshistory_item_express.setImageResource(ExpressFirm.getExpressIcon(SkuaidiSpf.getLoginUser().getExpressNo()));
		holder.tv_findexpresshistory_item_order.setText(getItem(position).getExpress_number());
		if (getItem(position).getRemarks() == null || getItem(position).getRemarks().equals("")) {
			holder.tv_findexpresshistory_item_remarks.setText("备注：");
		} else {
			holder.tv_findexpresshistory_item_remarks.setText("备注：" + getItem(position).getRemarks());
		}
		if (getItem(position).getStatus() != null && !getItem(position).getStatus().equals("")) {
			if (getItem(position).getStatus().equals("false")) {

			} else if (getItem(position).getStatus().equals("true")) {
				holder.tv_findexpresshistory_item_status.setBackgroundResource(R.drawable.pop_choose_icon);
			}
		} else {
			//Log.i("test", "status===kong" + getItem(position).getStatus());
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_findexpresshistory_item_express, tv_findexpresshistory_item_status;
		TextView tv_findexpresshistory_item_order, tv_findexpresshistory_item_remarks;

	}

}
