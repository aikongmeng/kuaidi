package com.kuaibao.skuaidi.activity.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class NotifyShowPhoneAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private TextView show_name;
	private TextView show_phone;
	private List<MyCustom> cuss;// 联系人集合
	Context context;
	
	
	public NotifyShowPhoneAdapter(Context context,List<MyCustom> cuss){
		super();
		this.context = context;
		this.cuss = cuss;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(cuss!=null &&!cuss.isEmpty() ){
			return cuss.size();
		}else {
			return 0;
		}
	}

	@Override
	public MyCustom getItem(int position) {
		return cuss.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.pop_search_phonenum_item, null);
		show_name = (TextView) convertView.findViewById(R.id.show_name);
		show_phone = (TextView) convertView.findViewById(R.id.show_phone);
		if(Utility.isEmpty(getItem(position).getName())){
			show_name.setText("客户");
		}else{
			show_name.setText(cuss.get(position).getName());
		}
		String phone = cuss.get(position).getPhone();
		String str = "";
		if(phone.length() <11){
			show_phone.setText(phone);
		}else if(phone.length() >12){
			str = phone.substring(0, phone.length()-11)+"  "+phone.substring(phone.length()-11, phone.length()-8)+" - "+phone.substring(phone.length()-8, phone.length()-4)+" - "+phone.substring(phone.length()-4);
			show_phone.setText(str);
		}else{
			if(phone.length() == 11){
				if(phone.indexOf("02") ==  0){
					str = phone.substring(0, 3)+" - "+ phone.substring(3);
				}else {
					str = phone.substring(0, 3)+" - "+phone.substring(3, 7)+" - "+phone.substring(7);
				}
			}else{
				str = phone.substring(0, 4)+" - "+phone.substring(4);
			}
			show_phone.setText(str);
		}
		
		return convertView;
	}
	
	
	public void freshData(List<MyCustom> cacheData){
		cuss = cacheData;
		notifyDataSetChanged();
	}
	
	
}
