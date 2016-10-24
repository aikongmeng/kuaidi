package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.DeliveryFees;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryFeesAdapter extends BaseAdapter {
	private Context context;
	private List<DeliveryFees> mList;
	private String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

	public DeliveryFeesAdapter(Context context, List<DeliveryFees> list) {
		this.context = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_delivery_fees, parent, false);
			holder.ll_date = (LinearLayout) convertView.findViewById(R.id.ll_date);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeliveryFees fees = mList.get(position);
		String scan_date = fees.getScan_date();
		Utility.setTimeDate(scan_date, holder.tv_date);

		if (position != 0 && !TextUtils.isEmpty(scan_date)
				&& scan_date.substring(0, 10).equals(mList.get(position - 1).getScan_date().substring(0, 10))) {
			holder.ll_date.setVisibility(View.GONE);
		} else {
			holder.ll_date.setVisibility(View.VISIBLE);
		}

		holder.tv_money.setText(fees.getAward_money());
		holder.tv_number.setText(fees.getWaybill_no());
		try {
			holder.tv_time.setText(scan_date.subSequence(10, scan_date.length()));
		} catch (Exception e) {
			//Log.w("iii", "DeliveryFeesAdapter,时间格式异常");
			e.printStackTrace();
		}

		return convertView;
	}

	class ViewHolder {
		TextView tv_number;
		TextView tv_money;
		TextView tv_time;
		TextView tv_date;
		LinearLayout ll_date;
	}

}
