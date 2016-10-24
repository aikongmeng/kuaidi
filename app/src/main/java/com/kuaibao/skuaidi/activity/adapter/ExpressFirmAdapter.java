package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.List;

public class ExpressFirmAdapter extends ArrayAdapter<String>{

	private List<String> listTag = null;
	public ExpressFirmAdapter(Context context, List<String> objects,
			List<String> tags) {
		super(context, 0, objects);
		listTag = tags;
	}
	
	@Override
	public boolean isEnabled(int position) {
		//分类标签不可点击
		return !listTag.contains(getItem(position));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (listTag.contains(getItem(position))) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_expressfirm_tag, null);
		}else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_expressfirm, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.group_list_item_text);
		ImageView iv = (ImageView) convertView.findViewById(R.id.iv_selectexpressfirm);
		String str = getItem(position);
		if (str.indexOf("-") == -1) {
			tv.setText(str);
		}else {
			tv.setText(str.substring(0, str.indexOf("-")));
			String code = str.substring(str.indexOf("-")+1);
			tv.setTag(code);
			try {
				int value = R.drawable.class.getDeclaredField("icon_"+code).getInt(null);
				iv.setImageResource(value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return convertView;
	}

}
