package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.LatticePoint;

import java.util.ArrayList;

public class AreaListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<LatticePoint> list;

	public AreaListAdapter(Context context, ArrayList<LatticePoint> list) {
		this.context = context;
		this.list = list;
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_area_list, parent, false);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		holder.tv_name.setText(list.get(position).getIndex_shop_name());

		holder.tv_tel.setText(list.get(position).getContact_tel());
		holder.tv_address.setText(list.get(position).getAddress());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_name;
		TextView tv_tel;
		TextView tv_address;

	}

}
