package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneContactsAdapter extends ArrayAdapter<String> {
	private boolean isBatchAdd = false;
	private Map<String,MyCustom> customs = new HashMap<String,MyCustom>();
	private ViewHolder holder;
	private List<String> objects;
	public PhoneContactsAdapter(Context context, List<String> objects) {
		super(context, 0, objects);
		this.objects = objects;
		refreshCustomList();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_phonecontacts, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_phonecontacts_name);
			holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_phonecontacts_tel);
			holder.batch_add_icon = (ImageView) convertView.findViewById(R.id.batch_add_icon);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String str = getItem(position);
		holder.tv_name.setText(str.substring(0, str.indexOf("-contact-")));
		holder.tv_tel.setText(str.substring(str.indexOf("-contact-") + 9));
		if(isBatchAdd == true){
			holder.batch_add_icon.setImageResource(R.drawable.batch_add_checked);
			holder.batch_add_icon.setVisibility(View.VISIBLE);
			if(customs.get(position+"")!=null){
				holder.batch_add_icon.setImageResource(R.drawable.batch_add_checked);
			}else{
				holder.batch_add_icon.setImageResource(R.drawable.batch_add_cancel);
			}
			holder.batch_add_icon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(customs.get(position+"")!=null){
						((ImageView)v.findViewById(R.id.batch_add_icon)).setImageResource(R.drawable.batch_add_cancel);
						customs.remove(position+"");
					}else{
						((ImageView)v.findViewById(R.id.batch_add_icon)).setImageResource(R.drawable.batch_add_checked);
						MyCustom cus = new MyCustom();
						cus.setName(str.substring(0, str.indexOf("-contact-")));
						cus.setPhone(str.substring(str.indexOf("-contact-") + 9).replaceAll(" ", ""));
						customs.put(position+"",cus);
					}
				}
			});
		}else{
			holder.batch_add_icon.setVisibility(View.GONE);
		}
		return convertView;
	}

	public static class ViewHolder {
		TextView tv_name, tv_tel;
		ImageView batch_add_icon;
	}
	
	public void isBatchAdd(boolean isBatchAdd){
		this.isBatchAdd = isBatchAdd;
		refreshCustomList();
		notifyDataSetChanged();
	}
	public MyCustom getCus(int position){
		return customs.get(position+"");
	}
	public void removeCus(int position){
		customs.remove(position+"");
	}
	public void addCus(String key,MyCustom cus){
		customs.put(key, cus);
	}
	public List<MyCustom> getMyCustoms(){
		List<MyCustom> list = new ArrayList<MyCustom>();
		for (String key : customs.keySet()) {
			MyCustom custom = customs.get(key);
			boolean isHave = SkuaidiNewDB.getInstance().isHaveCustomer1(custom.getPhone());
			if(!isHave){
				list.add(customs.get(key));
			}
		}
		return list;
	}
	
	private void refreshCustomList(){
		for (int i = 0; i < objects.size(); i++) {
			MyCustom cus = new MyCustom();
			String string = objects.get(i);
			cus.setName(TextUtils.isEmpty(string.substring(0, objects.get(i).indexOf("-contact-")))?string.substring(objects.get(i).indexOf("-contact-") + 9).replaceAll(" ", ""):string.substring(0, objects.get(i).indexOf("-contact-")));
			cus.setPhone(objects.get(i).substring(objects.get(i).indexOf("-contact-") + 9).replaceAll(" ", ""));
			customs.put(i+"",cus);
			
		}
	}
}
