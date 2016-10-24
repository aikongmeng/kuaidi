package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;

import java.util.List;

public class CollectionContinuousScanAdapter extends BaseAdapter {
	private List<NotifyInfo> list;
	private Context context;
	private String type;

	public CollectionContinuousScanAdapter(Context context, Handler handler, List<NotifyInfo> list, String type) {
		this.list = list;
		this.context = context;
		this.type=type;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.capture_list_collection, parent, false);
			holder.iv_capture_del = (ImageView) convertView.findViewById(R.id.iv_capture_del);
			holder.tv_capture_order = (TextView) convertView.findViewById(R.id.tv_capture_order);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_capture_order.setText(list.get(position).getExpress_number());
		holder.iv_capture_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Message msg = new Message();
				E3_order order = new E3_order();
				order.setCompany(((CaptureActivity) context).company);
				try {
					order.setOrder_number(list.get(position).getExpress_number());
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					return;
				}
				order.setCourier_job_no(((CaptureActivity) context).courierNO);
				order.setType(E3SysManager.scanToTypeMap.get(type));

				list.remove(position);
				notifyDataSetChanged();

			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_capture_order;
		ImageView iv_capture_del;
	}
}
