package com.kuaibao.skuaidi.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.SortModel;

import java.util.List;

public abstract class SkuaidiBaseAdapter extends BaseAdapter {
	
	private List<Object> objects;
	private Context context;
	public SkuaidiBaseAdapter(Context context,List<Object> list){
		objects = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<Object> getList(){
		return objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.base_adapter_item, null);
			LinearLayout layout = (LinearLayout) convertView;
			View view  = initViewHolder();
			layout.addView(view);
			holder = convertView;
			convertView.setTag(holder);
			
		}else{
			holder = (View) convertView.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			holder.findViewById(R.id.catalog).setVisibility(View.VISIBLE);
			((TextView)holder.findViewById(R.id.catalog)).setText(((SortModel)objects.get(position)).getSortLetters());
		}else{
			holder.findViewById(R.id.catalog).setVisibility(View.GONE);
		}
		initConvertView(position,holder,viewGroup);
		//System.out.println(position);
		return convertView;
	}
	

	public void freshDatas(List<Object> list){
		objects = list;
		notifyDataSetChanged();
	}
	
	protected abstract View initViewHolder();
	protected abstract void initConvertView(int position, View holder, ViewGroup viewGroup);
	
	public int getSectionForPosition(int position) {
		return ((SortModel)objects.get(position)).getSortLetters().charAt(0);
	}
	
	public int getPositionForSection(int section) {
		int count = getCount();
		for (int i = 0; i < count; i++) {
			String sortStr = ((SortModel)objects.get(i)).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}
	
	@SuppressWarnings("unused")
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
	
}
