package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.List;

public class CollectionRecordsAdapter extends BaseAdapter {
	private Context context;
	List<CollectionRecords> list;
	private String now;

	public CollectionRecordsAdapter(Context context, List<CollectionRecords> list) {
		this.context = context;
		this.list = list;
		now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_collection_records, parent, false);
			holder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			TextPaint tp = holder.tv_amount.getPaint();
			tp.setFakeBoldText(true);//字体加粗
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.ll_date=(LinearLayout)convertView.findViewById(R.id.ll_date);
			holder.iv_resource = (ImageView) convertView.findViewById(R.id.iv_resource);
			holder.tv_resource = (TextView) convertView.findViewById(R.id.tv_resource);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String time = list.get(position).getAvail_time();
		holder.tv_amount.setText("+" + list.get(position).getMoney());
//		holder.tv_resource.setText(list.get(position).getTran_msg());
		if("支付宝".equals(list.get(position).getTran_msg())){
			holder.iv_resource.setImageResource(R.drawable.icon_shoukuan_alipay);
			holder.tv_resource.setText("支付宝收款");
		}else if("微信".equals(list.get(position).getTran_msg())){
			holder.iv_resource.setImageResource(R.drawable.icon_shoukuan_wechat);
			holder.tv_resource.setText("微信收款");
		}else{
			holder.iv_resource.setImageResource(R.drawable.icon_shoukuan_geren);
			if (!Utility.isEmpty(list.get(position).getIsAccounting()) && list.get(position).getIsAccounting().equals("1")){
				holder.tv_resource.setText("现金收款");
			}else {
				holder.tv_resource.setText(list.get(position).getTran_msg());
			}
		}
		try {
			holder.tv_time.setText(time.substring(11,16));
			if (!TextUtils.isEmpty(time) && now.substring(0, 10).equals(time.substring(0, 10))) {
				holder.tv_date.setText("今天 ");
			} else if (!TextUtils.isEmpty(time) && now.substring(0, 8).equals(time.substring(0, 8))
					&& Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(time.substring(8, 10)) == 1) {
				holder.tv_date.setText("昨天");
			} else {
				if (!TextUtils.isEmpty(time)) {
					holder.tv_date.setText(time.substring(0, 10));
				}

			}

			if (position != 0 && !TextUtils.isEmpty(time)
					&& time.substring(0, 10).equals(list.get(position - 1).getAvail_time().substring(0, 10))) {
				holder.ll_date.setVisibility(View.GONE);
			} else {
				holder.ll_date.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		LinearLayout ll_date;
		TextView tv_amount, tv_time, tv_date, tv_resource;
		ImageView iv_resource;
	}

}
