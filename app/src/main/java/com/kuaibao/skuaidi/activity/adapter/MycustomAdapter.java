package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;

import java.util.HashMap;
import java.util.List;


/**
 * 我的客户列表Adapter
 * @author xy
 *
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale","NewApi" })
public class MycustomAdapter extends BaseAdapter {
	public static final int LOAD_TYPE_ACQUIESCENCE = 0;
	public static final int LOAD_TYPE_CHECKITEM = 1;
	public static final int LOAD_TYPE_HIDETOP = 2;
	private int loadType = LOAD_TYPE_ACQUIESCENCE;
	private MycustomAdapterCallback callback;
	private List<MyCustom> objects;
	private Context context;
	private View view;
	private CustomerMgTools tools;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();

	public void updateListView(List<MyCustom> list,int loadType){
		this.loadType = loadType;
		this.objects = list;
		notifyDataSetChanged();
	}
	public void updateListView(List<MyCustom> list){
		this.objects.clear();
		this.objects.addAll(list);
		notifyDataSetChanged();
	}
	public MycustomAdapter(Context context, List<MyCustom> objects,
			MycustomAdapterCallback callback,int loadType) {
		this.loadType = loadType;
		this.callback = callback;
		this.objects = objects;
		this.context = context.getApplicationContext();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (position == 0 && loadType == LOAD_TYPE_ACQUIESCENCE) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.mycus_top_item, null);

			convertView.findViewById(R.id.who_collect).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							tools.whoCollection();
						}
					});
			convertView.findViewById(R.id.sync_customer).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					UMShareManager.onEvent(context, "customer_manager_syncCustomer", "customer_manager", "客户管理:同步通讯录");
					tools.asyncDatas();
				}
			});
			
			convertView.findViewById(R.id.baned_recorder).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							tools.banedRecord();
						}
					});
			
			
			view = convertView;
		} else {

			ViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listitem_mycustom, null);
				holder.cataLog = (TextView) convertView
						.findViewById(R.id.catalog);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_listitem_mycustom_time);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_listitem_mycustom_name);
				holder.tv_call = (TextView) convertView
						.findViewById(R.id.tv_listitem_mycustom_call);	
				holder.tv_address = (TextView) convertView
						.findViewById(R.id.tv_listitem_mycustom_address);
				holder.iv_call = (ImageView) convertView
						.findViewById(R.id.iv_listitem_mycustom_call);
				holder.iv_message = (ImageView) convertView
						.findViewById(R.id.iv_listitem_mycustom_message);
				holder.iv_multiselect_icon = (ImageView) convertView
						.findViewById(R.id.iv_multiselect_icon);
				holder.iv_call.setOnClickListener(mListener);
				holder.iv_message.setOnClickListener(mListener);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int pos;
			MyCustom custom;
			if(loadType == LOAD_TYPE_ACQUIESCENCE){
				pos = position-1;
				custom = objects.get(pos);
			}else{
				pos = position;
				custom = objects.get(pos);
				if(loadType == LOAD_TYPE_CHECKITEM){
					holder.iv_multiselect_icon.setVisibility(View.VISIBLE);
					
					if(!custom.isChecked()){
						holder.iv_multiselect_icon.setImageResource(R.drawable.icon_ethreescan_fail);
					}
					else{
						holder.iv_multiselect_icon.setImageResource(R.drawable.icon_ethreescan_success);
					}
					holder.iv_call.setVisibility(View.GONE);
					holder.iv_message.setVisibility(View.GONE);
				}
				
			}
			
			//根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(pos);
			
			//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if(pos == getPositionForSection(section)){
				holder.cataLog.setVisibility(View.VISIBLE);
				holder.cataLog.setText(custom.getSortLetters());
			}else{
				holder.cataLog.setVisibility(View.GONE);
			}
			holder.tv_name.setText(KuaiBaoStringUtilToolkit.isEmpty(custom.getName())?"客户":custom.getName());
			holder.tv_call.setText(custom.getPhone());
			HashMap<String,String> sa=new HashMap<String, String>();
			sa.put("callerName", KuaiBaoStringUtilToolkit.isEmpty(custom.getName())?"":custom.getName());
			sa.put("phone", custom.getPhone());
			holder.iv_call.setTag(sa);
			holder.iv_call.setImageResource(R.drawable.icon_phone);
			holder.iv_message.setTag(custom.getPhone());
			holder.iv_message.setImageResource(R.drawable.icon_msg);
		}
		return convertView;
	}

	public static class ViewHolder {
		TextView tv_time, tv_name, tv_call, tv_address, cataLog;
		ImageView iv_call, iv_message,iv_multiselect_icon;
	}

	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (callback != null) {
				callback.onClick(arg0);
			}
		}
	};

	public interface MycustomAdapterCallback {
		void onClick(View v);
	}
	
	public int getCheckedCount(){
		int checkedCount = 0;
		for (int i = 0; i < objects.size(); i++) {
			if(objects.get(i).isChecked()){
				checkedCount = checkedCount+1;
			}
		}
		return checkedCount;
	}
	
	@Override
	public int getCount() {
		if(loadType != LOAD_TYPE_ACQUIESCENCE){
			return objects.size();
		}
		return objects.size() + 1;
	}
	public List<MyCustom> getCustomList(){
		return objects;
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		if (arg0 == 0&&loadType==LOAD_TYPE_ACQUIESCENCE) {
			return view;
		}else if(loadType!=LOAD_TYPE_ACQUIESCENCE){
			return objects.get(arg0);
		}
		return objects.get(arg0-1);
		
	}

	public void setCustomerMgTools(CustomerMgTools tools){
		this.tools = tools;
	}
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if(objects==null||objects.get(position)==null||objects.get(position).getSortLetters()==null)
			return 0;
		return objects.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		
		int count;
		if(loadType == LOAD_TYPE_ACQUIESCENCE){
			count = getCount()-1;
		}else{
			count = getCount();
		}
		
		for (int i = 0; i < count; i++) {
			String sortStr = objects.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}
	/**
	 * 	根据此Adapter的特殊性重写SIDEBAR点击获取位置POSItion
	 */
		public int getPositionForSection1(int section) {
			for (int i = 0; i < getCount()-1; i++) {
				String sortStr = objects.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i+1;
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

	public interface CustomerMgTools{
		public void asyncDatas();
		public void whoCollection();
		public void banedRecord();
	}
}
