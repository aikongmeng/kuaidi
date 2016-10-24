package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.List;

public class SkuaidiPopOfCenterCheckList {
	private PopupWindow popupWindow;
	private Context context;
	private View v;
	private List<String> titleList;
	private ItemOnclickListener itemOnclickListener;
	private MengOnclickListener mengOnclickListener;
	public SkuaidiPopOfCenterCheckList(Context context,View v,List<String> titleList){
		this.context = context;
		this.v = v;
		this.titleList = titleList;
		initPop();
	}
	public SkuaidiPopOfCenterCheckList(Context context,View v,List<String> titleList,MengOnclickListener mengOnclickListener){
		this.mengOnclickListener = mengOnclickListener;
		this.context = context;
		this.v = v;
		this.titleList = titleList;
		initPop();
	}
	private ListView listView;
	private CheckListAdapter adapter;
	private void initPop(){
		popupWindow = new PopupWindow(context);
		View layout = LayoutInflater.from(context).inflate(
				R.layout.check_list_pop_center_layout, null);
		
		listView = (ListView) layout.findViewById(R.id.lv_checkList);
		 adapter = new CheckListAdapter();
		listView.setAdapter(adapter);
		
		// 点击界面隐藏popupwindow
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mengOnclickListener!=null){
					mengOnclickListener.onClick();
				}
				popupWindow.dismiss();
			}
		});
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);
	}
	
	public boolean isShowing(){
		return popupWindow.isShowing();
	}
	
	public void showPop(){
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
	}
	public void dismiss(){
		popupWindow.dismiss();
	}

	private class CheckListAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return titleList.size();
		}

		@Override
		public String getItem(int arg0) {
			return titleList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		
		@Override
		public View getView(final int position, View convertView, final ViewGroup parentView) {
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.pop_check_list_item, null);
				holder.split_line = convertView.findViewById(R.id.split_line);
				holder.item_content = (TextView) convertView.findViewById(R.id.item_content);
				holder.body = convertView.findViewById(R.id.item_center_body);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if(getCount()!=1){
				if(position == 0){
					holder.item_content.setBackgroundResource(R.drawable.selector_pop_top);
					holder.split_line.setVisibility(View.VISIBLE);
				}else if(position == getCount()-1){
					holder.item_content.setBackgroundResource(R.drawable.selector_pop_bottom);
					holder.split_line.setVisibility(View.GONE);
				}else{
					holder.item_content.setBackgroundResource(R.drawable.selector_popu_bg);
					holder.split_line.setVisibility(View.VISIBLE);
				}
				
			}else{
				holder.item_content.setBackgroundResource(R.drawable.selector_check_list_single);
				holder.split_line.setVisibility(View.GONE);
			}
			holder.item_content.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(itemOnclickListener!=null){
						itemOnclickListener.onClick(position);
					}
					popupWindow.dismiss();
				}
			});
			holder.item_content.setText(getItem(position));
			return convertView;
		}
		
	}
	
	public void setItemOnclickListener(ItemOnclickListener itemOnclickListener){
		this.itemOnclickListener = itemOnclickListener;
	}
	
	public void setMengOnclickListener(MengOnclickListener mengOnclickListener){
		this.mengOnclickListener = mengOnclickListener;
	}
	
	
	public interface MengOnclickListener{
		void onClick();
	}
	
	public interface ItemOnclickListener{
		void onClick(int position);
	}
	private class ViewHolder{
		TextView item_content;
		View body,split_line;
	}
}
