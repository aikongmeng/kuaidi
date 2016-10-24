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

public class E3InfoNumberAdapter extends BaseAdapter {
	public boolean hasUnusual = false;
	private String type = "";
	private List<NotifyInfo> infos;
	private Context context;

	public E3InfoNumberAdapter(Context context, List<NotifyInfo> objects, String type) {
		this.context = context;
		this.type = type;
		this.infos = objects;

	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.e3_number_item, null);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_listitem_ethreeinfoscan_order);
			holder.tv_status = (TextView) convertView.findViewById(R.id.tv_waybill_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_number.setText(infos.get(position).getExpress_number());
		if(!Utility.isEmpty(infos.get(position).getStatus())){
			holder.tv_status.setVisibility(View.VISIBLE);
			holder.tv_status.setText(infos.get(position).getStatus());
		}
		return convertView;
	}

	class ViewHolder {
		private TextView tv_number;
		private TextView tv_status;
	}
}
