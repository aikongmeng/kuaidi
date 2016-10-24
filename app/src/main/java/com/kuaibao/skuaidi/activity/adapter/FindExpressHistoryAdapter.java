package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ExpressHistory;
import com.kuaibao.skuaidi.util.ExpressFirm;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FindExpressHistoryAdapter extends BaseAdapter {
	Context context;
	List<ExpressHistory> expressHistories;

	public FindExpressHistoryAdapter(Context context, List<ExpressHistory> expressHistories) {
		super();
		this.context = context;
		this.expressHistories = expressHistories;
	}

	@Override
	public int getCount() {
		return expressHistories.size();
	}

	@Override
	public ExpressHistory getItem(int position) {
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
		holder.find_time = (TextView) convertView.findViewById(R.id.find_time);
		holder.express_sign = (ImageView) convertView.findViewById(R.id.express_sign);
		holder.iv_findexpresshistory_item_express.setImageResource(ExpressFirm.getExpressIcon(SkuaidiSpf.getLoginUser().getExpressNo()));

		if (getItem(position).getRemarks() == null || getItem(position).getRemarks().equals("")) {
			holder.tv_findexpresshistory_item_order.setText(getItem(position).getDeliverNo());
		} else {
			holder.tv_findexpresshistory_item_order.setText("备注：" + getItem(position).getRemarks());
		}

		String str_record = getItem(position).getRecord().toString();
		if (!Utility.isEmpty(str_record)) {
			//Log.i("logi", "str_record:   " + str_record);
			// 把字符串中的时间部分去掉-只显示除时间部分以外内容
			// int length = 0;
			// String str[] = str_record.split(" ");
			// for (int i = 0; i < str.length; i++) {
			// length = str[0].length() + str[1].length();
			// }
			// str_record = str_record.substring(length + 2,
			// str_record.length());

			if (str_record.length() > 20) {
				str_record = str_record.substring(20);
				holder.tv_findexpresshistory_item_remarks.setText(str_record);
			} else {
				holder.tv_findexpresshistory_item_remarks.setText("");
			}
		} else {
			holder.tv_findexpresshistory_item_remarks.setText("");
		}
		if (getItem(position).getFirstTime() != null && !getItem(position).getFirstTime().equals("")) {
			holder.find_time.setText(getItem(position).getFirstTime());
		} else {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			holder.find_time.setText(sdf.format(date));
		}
		if (getItem(position).getStatus().equals("已签收")) {
			holder.express_sign.setImageResource(R.drawable.pop_choose_icon);
			// holder.tv_findexpresshistory_item_status.setText("已签收");
		} else if (getItem(position).getStatus().equals("true")) {
			// holder.tv_findexpresshistory_item_status.setText("未签收");
		} else {
			/*
			 * holder.tv_findexpresshistory_item_status.setText(getItem(position)
			 * .getStatus()); //System.out.println("单号 " +
			 * getItem(position).getDeliverNo() + "状态" +
			 * getItem(position).getStatus());
			 */
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_findexpresshistory_item_express, express_sign;
		TextView tv_findexpresshistory_item_order, tv_findexpresshistory_item_remarks, find_time, tv_findexpresshistory_item_status;

	}

}
