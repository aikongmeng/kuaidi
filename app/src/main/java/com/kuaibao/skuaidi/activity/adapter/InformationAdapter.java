package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.InformationInfo;

import java.util.List;

public class InformationAdapter extends BaseAdapter {

	private Context mContext;
	private List<InformationInfo> infos;

	public InformationAdapter(Context context, List<InformationInfo> list) {
		this.mContext = context;
		this.infos = list;
	}
	
	/**更新列表数据**/
	public void UpdateList(List<InformationInfo> list){
		this.infos = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		InformationInfo info = new InformationInfo();
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.information_item, null);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_information_title);
			holder.tv_description = (TextView) convertView.findViewById(R.id.tv_information_description);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_information_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		info = getItem(position);

		holder.tv_title.setText(info.getTitle());
		holder.tv_description.setText(info.getDescription());
		holder.tv_time.setText(info.getTime().substring(0, 16));
		holder.tv_title.setTag(info.getUrl());
		return convertView;
	}

	public static class ViewHolder {
		TextView tv_title;
		TextView tv_description;
		TextView tv_time;
	}

	@Override
	public int getCount() {
		
		return infos.size();
	}

	@Override
	public InformationInfo getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
