package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysMainActivity;

import java.util.HashMap;
import java.util.List;

public class BusinessMenuAdapter extends BaseAdapter {
	HashMap<String, Integer> redCircleVisblePositions = new HashMap<>();

	private List<Integer> images;
	private List<String> description;
	private String centerAdUrl;
	private LayoutInflater layoutInflater;
	private Context context;
	private String company;
	private String courierNO;
	private int responseCode;
	private SharedPreferences sp;

	public int tag;//判断当前是否位于首页

	private final int TYPE_HTML = 0;

	private final int TYPE_ITEM =1 ;
	private boolean refreshWebVie=true;
	public BusinessMenuAdapter(final Context context, String company, String courierNO) {
		this.context = context;
		this.company = company;
		this.courierNO = courierNO;
		layoutInflater = LayoutInflater.from(context);
		sp= context.getSharedPreferences("config", Context.MODE_PRIVATE);

	}

	@Override
	public int getCount() {
		if(tag==1){
			return images.size()+1;
		}else{
			return images.size();
		}
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return (position == 4 && tag==1) ? TYPE_HTML : TYPE_ITEM ;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	Holder holder;

	public WebView webView;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//如果渲染显示的位置position=4，则显示h5页面并设置属性，否则，显示条目类型
			if(position>4 && tag==1){
				position-=1;
			}
			holder = null;
			if (convertView == null || convertView instanceof RelativeLayout) {
				holder = new Holder();
				convertView = layoutInflater.inflate(R.layout.business_menu_gidview_item, null);
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
				holder.iv_red_icon = (ImageView) convertView.findViewById(R.id.iv_red_icon);
				holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			convertView.findViewById(R.id.rl_container).setVisibility(View.GONE);
			convertView.findViewById(R.id.rl_business_item).setVisibility(View.VISIBLE);
			int toUploadCount = E3OrderDAO.getOrderCount(company, courierNO);
			if (context instanceof EThreeSysMainActivity) {
				if (toUploadCount > 0 && "数据上传".equals(description.get(position))) {
					holder.tv_count.setVisibility(TextView.VISIBLE);
					holder.tv_count.setText("" + toUploadCount);
				} else {
					holder.tv_count.setVisibility(TextView.GONE);
				}
			}
			
			holder.iv_icon.setImageResource(images.get(position));
			holder.tv_des.setText(description.get(position));
			holder.tv_des.setTextColor(context.getResources().getColor(R.color.business_gridview_menu_textcolor));
			if (redCircleVisblePositions.get(description.get(position))!=null&&redCircleVisblePositions.get(description.get(position))!=0) {
				holder.iv_red_icon.setVisibility(View.VISIBLE);
			} else {
				holder.iv_red_icon.setVisibility(View.GONE);
			}
		return convertView;
	}

	class Holder {
		ImageView iv_icon;
		TextView tv_des;
		ImageView iv_red_icon;
		TextView tv_count;
	}

	public void setImages(List<Integer> images) {
		this.images = images;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}


	/**
	 *
	 * @param map 功能名称与小红点状态的键值对
	 *
     */
	public  void notifyRedCircleChanged(HashMap map){
		redCircleVisblePositions.clear();
		redCircleVisblePositions.putAll(map);
		notifyDataSetChanged();
	}
	
	public void setCenterAdUrl(String centerAdUrl){
		this.centerAdUrl=centerAdUrl;
	}
}
