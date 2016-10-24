package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;

import java.util.List;

public class SkuaidiBigPopAboutCheckList {
	private PopupWindow popupWindow;
	private Context context;
	private View v;
	private List<String> titleList;
	private ItemOnclickListener itemOnclickListener;
	private MengOnclickListener mengOnclickListener;
	public SkuaidiBigPopAboutCheckList(Context context,View v,List<String> titleList){
		this.context = context;
		this.v = v;
		this.titleList = titleList;
		initPop();
	}
	public SkuaidiBigPopAboutCheckList(Context context,View v,List<String> titleList,MengOnclickListener mengOnclickListener){
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
				R.layout.check_list_pop_layout, null);
		
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
	
	public void setTitleList(List<String> titleList){
		this.titleList = titleList;
	}
	
	public void showPop(){
		if(!isShowPopOnTopCenter){
			android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) listView.getLayoutParams();
			params.rightMargin = 30;
			listView.setLayoutParams(params);
		}
		//popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAsDropDown(v,0,3);
	}
	private boolean isShowPopOnTopCenter = false;
	public void showPopOnTopCenter(){
		isShowPopOnTopCenter = true;
		showPop();
	}
	public void notifyDataSetChanged(List<String> titleList,View v){
		this.titleList = titleList;
		this.v = v;
		adapter.notifyDataSetChanged();
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
		
		ViewHolder holder;
		@Override
		public View getView(final int position, View convertView, final ViewGroup parentView) {
			holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				if(android.os.Build.VERSION.SDK_INT>=17||isShowPopOnTopCenter){
					convertView = LayoutInflater.from(context).inflate(
							R.layout.big_check_list_pop_item, null);
					holder.item_content = (TextView) convertView.findViewById(R.id.big_item_content);
					if(!isShowPopOnTopCenter){
						android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.item_content.getLayoutParams();
						if(Build.VERSION.SDK_INT >=17){
							params.removeRule(android.widget.RelativeLayout.CENTER_HORIZONTAL);
							params.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
						}else{
							params.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT,0);
						}
						holder.item_content.setLayoutParams(params);
					}else{
						//System.out.println("居中");
					}
					//holder.split_line = convertView.findViewById(R.id.big_split_line);
				}else if(android.os.Build.VERSION.SDK_INT<17&&!isShowPopOnTopCenter){
					convertView = LayoutInflater.from(context).inflate(
							R.layout.check_list_item1, null);
					holder.item_content = (TextView) convertView.findViewById(R.id.big_item_content);
					//holder.split_line = convertView.findViewById(R.id.big_split_line);
				}
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if(getCount()!=1){
//				if(position == 0){
//					holder.item_content.setBackgroundResource(R.drawable.selector_popu_bg);
//					//holder.split_line.setVisibility(View.VISIBLE);
//				}else if(position == getCount()-1){
//					holder.item_content.setBackgroundResource(R.drawable.selector_popu_bg);
//					//holder.split_line.setVisibility(View.GONE);
//				}else{
					holder.item_content.setBackgroundResource(R.drawable.selector_popu_bg);
					//holder.split_line.setVisibility(View.VISIBLE);
				//}
			}else{
				holder.item_content.setBackgroundResource(R.drawable.selector_check_list_single);
				//holder.split_line.setVisibility(View.GONE);
			}
			
			
			if(SkuaidiBigAboutCheckListManager.getChecks(context)!=null
					&&SkuaidiBigAboutCheckListManager.getChecks(context).get(SkuaidiBigPopAboutCheckList.this.v.getTag())!=null
					&&!KuaiBaoStringUtilToolkit.isEmpty(SkuaidiBigAboutCheckListManager.getChecks(context).get(SkuaidiBigPopAboutCheckList.this.v.getTag()).get(position))){
				//System.out.println("map  : "+SkuaidiBigAboutCheckListManager.getChecks(context).get((Integer)SkuaidiBigPopAboutCheckList.this.v.getTag()));
				holder.item_content.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			    Drawable nav_up=context.getResources().getDrawable(SkuaidiSkinManager.getSkinResId("select_deliver_gou_cu"));  
			    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());  
			    holder.item_content.setCompoundDrawables(null, null, nav_up, null);  
			}else{
				holder.item_content.setTextColor(context.getResources().getColorStateList(R.color.text_black));
				holder.item_content.setCompoundDrawables(null, null, null, null);
			}
			
			holder.item_content.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SkuaidiBigAboutCheckListManager.setCheck(context, (Integer)SkuaidiBigPopAboutCheckList.this.v.getTag(), position,getItem(position));
					holder.item_content.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
				    Drawable nav_up=context.getResources().getDrawable(SkuaidiSkinManager.getSkinResId("select_deliver_gou_cu"));  
				    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());  
				    holder.item_content.setCompoundDrawables(null, null, nav_up, null);  
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
		//View split_line;
	}
}
