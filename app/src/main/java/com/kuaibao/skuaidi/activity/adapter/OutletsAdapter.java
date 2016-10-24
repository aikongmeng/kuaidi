package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Outlets;

import java.util.ArrayList;

public class OutletsAdapter extends BaseAdapter {

	private ArrayList<Outlets> outletsList;
	private Context context;
	private String shortName;

	public OutletsAdapter(ArrayList<Outlets> outletsList, Context context) {
		if (outletsList != null)
			this.outletsList = outletsList;
		else
			this.outletsList = new ArrayList<Outlets>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return outletsList.size();
	}

	@Override
	public Object getItem(int position) {
		return outletsList.get(position);
	}

	public void clearData() {
		outletsList.clear();
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.outlets_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SpannableStringBuilder style;
		int start;
		if (shortName.matches("[0-9]+")) {
			style = new SpannableStringBuilder(outletsList.get(position).getOutletsCode());
			start = outletsList.get(position).getOutletsCode().indexOf(shortName);
		} else {
			style = new SpannableStringBuilder(outletsList.get(position).getOutletsName());
			start = outletsList.get(position).getOutletsName().indexOf(shortName);

		}
		if(start>=0){
			try {

				style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.default_green_2)), start,
						start + shortName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			} catch (Exception e) {
				e.printStackTrace();
				//Log.w("iii", e.getMessage());
			}
		}

		if (shortName.matches("[0-9]+")) {
			holder.tv_code.setText(style);
			holder.tv_name.setText(outletsList.get(position).getOutletsName());
		} else {
			holder.tv_name.setText(style);
			holder.tv_code.setText(outletsList.get(position).getOutletsCode());
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_name;
		TextView tv_code;
	}

}
