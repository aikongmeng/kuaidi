package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseModelDragListAdapter<T> extends BaseAdapter {
	
	protected Context context;
	protected List<T> replyModels;
	
//	private TextView drag_list_item_title;// 模板标题
//	private TextView drag_list_item_text;// 模板内容显示
	
	public BaseModelDragListAdapter(Context context, List<T> replyModels){
		this.context = context;
		this.replyModels = replyModels;
	}

	@Override
	public int getCount() {
		return replyModels.size();
	}

	@Override
	public Object getItem(int position) {
		return replyModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	
	public void remove(int position){
		replyModels.remove(position);
	}
	
	public void remove(T replyModel){
		replyModels.remove(replyModel);
	}
	
	public void insert(T replyModel,int position){
		replyModels.add(position, replyModel);
	}
	
	public List<T> getModels(){
		return replyModels;
	}
	
	
}
