package com.kuaibao.skuaidi.sortlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MyExpressBrandEntry;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<MyExpressBrandEntry> list = null;
	private Context mContext;
	private int count = 0;
	
	public SortAdapter(Context mContext, List<MyExpressBrandEntry> list,int commonExpressCount) {
		this.mContext = mContext;
		this.list = list;
		this.count = commonExpressCount;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<MyExpressBrandEntry> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public MyExpressBrandEntry getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final MyExpressBrandEntry mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.sort_adapter_item, null);
			viewHolder.line = view.findViewById(R.id.line);
			viewHolder.line1 = view.findViewById(R.id.line1);
			viewHolder.line3 = view.findViewById(R.id.line3);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);// 内容
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);// 字母
			viewHolder.express_shop_icon = (ImageView) view.findViewById(R.id.express_shop_icon);// 快递ICON
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		if(position <=count-1){
			if(position == 0){
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText("常用");
				viewHolder.line.setVisibility(View.GONE);
				viewHolder.line1.setVisibility(View.VISIBLE);
			}else{
				if(position != count-1){
					viewHolder.tvLetter.setVisibility(View.GONE);
					viewHolder.line.setVisibility(View.GONE);
					viewHolder.line1.setVisibility(View.GONE);
					viewHolder.line3.setVisibility(View.VISIBLE);
				}else{
					viewHolder.tvLetter.setVisibility(View.GONE);
					viewHolder.line.setVisibility(View.GONE);
					viewHolder.line1.setVisibility(View.GONE);
					viewHolder.line3.setVisibility(View.GONE);
				}
				
			}
			
		}else {
			//根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if(position == getPositionForSection(section)){
				viewHolder.line.setVisibility(View.VISIBLE);
				viewHolder.line1.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getSortLetters());
			}else{
				viewHolder.line.setVisibility(View.GONE);
				viewHolder.line1.setVisibility(View.GONE);
				viewHolder.tvLetter.setVisibility(View.GONE);
			}
			
			if(position+1 < list.size()){
				if(position+1 == getSectionForPosition(position+1)){
					viewHolder.line3.setVisibility(View.GONE);
				}else{
					viewHolder.line3.setVisibility(View.VISIBLE);
				}
			}else{
				viewHolder.line3.setVisibility(View.GONE);
			}
		}
		
		String expressCompany=getItem(position).getExpressName();// 获取物流公司名称
		viewHolder.tvTitle.setText(expressCompany);
		String expressCompanyForShort = "icon_"+getItem(position).getExpressCode();// 获取物流公司简称
		
		viewHolder.express_shop_icon.setBackgroundResource(SkuaidiSkinManager.getSkinResId(expressCompanyForShort));
		return view;

	}
	


	final static class ViewHolder {
		View line = null;
		View line1 = null;
		View line3 = null;
		TextView tvLetter;
		TextView tvTitle;
		ImageView express_shop_icon;// icon[公司]
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = count; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();	
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}