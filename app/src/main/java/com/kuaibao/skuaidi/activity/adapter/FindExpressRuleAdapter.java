package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.AllInterface;

import java.util.List;

public class FindExpressRuleAdapter extends ArrayAdapter<String> {

	private String ordernumber;
	private Context context;
	private boolean flag;
	private String notify_findexpress;
	private String expresswuliu;
	private String lock_express;

	public FindExpressRuleAdapter(Context context, List<String> objects,
			String ordernumber, boolean flag) {
		super(context, 0, objects);
		this.context = context;
		this.ordernumber = ordernumber;
		this.flag = flag;
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		notify_findexpress = sp.getString("notify_findexpress", "");
		expresswuliu = sp.getString("expresswuliu", "");
		lock_express = sp.getString("lockexpress", "");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (flag && getItem(position).equals("选择其他快递公司")) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.select_brand_item, null);
		} else {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.findexpress_rule_item, null);
			TextView tv_remark = (TextView) convertView
					.findViewById(R.id.tv_findexpress_rule_item_remark);
			TextView tv_time = (TextView) convertView
					.findViewById(R.id.tv_findexpress_rule_item_time);
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_item);
			ImageView iv_signed = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_item_signed);
			ImageView iv_bg = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_bg_item);
			ImageView iv_notify = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_notify_item);
			ImageView iv_soso = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_item_soso);
			TextView tv_md5 = (TextView) convertView
					.findViewById(R.id.tv_findexpress_rule_item_md5);
			ImageView iv_lock = (ImageView) convertView
					.findViewById(R.id.iv_findexpress_rule_item_lock);
			String str = getItem(position);
			if (flag) {
				iv.setVisibility(View.GONE);
				iv_bg.setVisibility(View.GONE);
				iv_notify.setVisibility(View.GONE);
				tv_md5.setVisibility(View.GONE);
				tv_time.setVisibility(View.GONE);
				iv_soso.setVisibility(View.VISIBLE);
				iv_lock.setVisibility(View.VISIBLE);
			}
			String code = "";
			if (ordernumber.equals("historyrule")) {
				String remark = "";
				if (!str.substring(0, str.indexOf("-remark-")).equals("")) {
					remark = str.substring(0, str.indexOf("-remark-"));
				}
				String time = str.substring(str.indexOf("-time-") + 6);
				if (time.indexOf("-") != -1) {
					time = time.substring(time.indexOf("-") + 1,
							time.lastIndexOf(":"));
				}
				tv_time.setText(time);
				str = str.substring(str.indexOf("-remark-") + 8,
						str.indexOf("-time-"));
				String status = str.substring(str.lastIndexOf("-") + 1);
				if (status.equals("signed") || status.equals("签收")) {
					iv_signed.setVisibility(View.VISIBLE);
				}
				code = str.substring(0, str.indexOf("-"));
				String order_number = str.substring(str.indexOf("-") + 1,
						str.lastIndexOf("-"));
				if (!remark.equals("")) {
					tv_remark.setText(remark);
				} else {
					tv_remark.setText(AllInterface.getExpressNoStr(code) + "  "
							+ order_number);
				}
				if (expresswuliu.indexOf(code + "-no-" + order_number
						+ "-wuliu-") != -1) {
					String wuliu = expresswuliu.substring(expresswuliu
							.indexOf(code + "-no-" + order_number + "-wuliu-"));
					wuliu = wuliu.substring(0, wuliu.indexOf("/") + 1);
					wuliu = wuliu.substring(wuliu.indexOf("-wuliu-") + 7,
							wuliu.indexOf("/"));
					if (!wuliu.equals("")) {
						tv_md5.setText(wuliu);
					}
				}
				iv.setTag(code + "_" + order_number);
				if (notify_findexpress.indexOf(code + "-" + order_number) != -1) {
					iv_notify.setVisibility(View.VISIBLE);
				}
				try {
					if (code.equals("fedexInter")) {
						code = "fedexinter";
					}
					int value = R.drawable.class.getDeclaredField(
							"icon_" + code).getInt(null);
					iv.setImageResource(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				code = str;
				if (lock_express.indexOf(code) == -1) {
					iv_lock.setImageDrawable(getContext().getResources()
							.getDrawable(R.drawable.icon_lock_on));
				} else {
					iv_lock.setImageDrawable(getContext().getResources()
							.getDrawable(R.drawable.icon_lock_off));
				}
				iv.setTag(code + "_" + ordernumber);
				tv_remark.setText(AllInterface.getExpressNoStr(code) + "  "
						+ ordernumber);
				if (position == 0) {
					iv_soso.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.icon_rule_soso_hover));
					tv_remark.setTextColor(context.getResources().getColor(
							R.color.findexpress_view_rule_blue));
				}
			}
		}
		return convertView;
	}
}
