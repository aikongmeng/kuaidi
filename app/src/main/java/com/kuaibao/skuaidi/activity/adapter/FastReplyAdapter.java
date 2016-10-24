package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class FastReplyAdapter extends BaseExpandableListAdapter {
	private class ReplyItem{
		String parent;
		List<String> children;
	}
	
	List<ReplyItem> replyList;
	private Context context;
	
	public FastReplyAdapter(Context context,int replyType) {
		super();
		this.context = context;
		replyList=new ArrayList<FastReplyAdapter.ReplyItem>();
		if (replyType==Constants.TYPE_REPLY_CUSTOMER) {
			String[] group={"XX小时内上门取件","下午来取件","今天来不及了，明天取件"};
			String[][] child={{"半小时内上门取件","一小时内上门取件","两小时内上门取件"},{},{},{}};
			for (int groupPosition = 0; groupPosition < group.length; groupPosition++) {
				ReplyItem replyItem=new ReplyItem();
				replyItem.parent=group[groupPosition];
				List<String> children=new ArrayList<String>();
				for (int childPosition = 0; childPosition < child[groupPosition].length; childPosition++) {
					children.add(child[groupPosition][childPosition]);
				}
				replyItem.children=children;
				replyList.add(replyItem);
			}
			
		}else if (replyType==Constants.TYPE_REPLY_SERVER) {
			String[] group={"太忙了，来不及取件","今天我请假","不是我的负责区域","地址不详，客户联系不上"};
			for (int groupPosition = 0; groupPosition < group.length; groupPosition++) {
				ReplyItem replyItem=new ReplyItem();
				replyItem.parent=group[groupPosition];
				List<String> children=new ArrayList<String>();
				replyItem.children=children;
				replyList.add(replyItem);
			}
		}
	}

	@Override
	public String getChild(int groupPosition, int childPosition) {
		return replyList.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LinearLayout child_layout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.child_layout, null);
		TextView tv=(TextView) child_layout.findViewById(R.id.tv_child);
		tv.setText(getChild(groupPosition,childPosition).toString());
		return child_layout;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return replyList.get(groupPosition).children.size();
	}

	@Override
	public String getGroup(int groupPosition) {
		return replyList.get(groupPosition).parent;
	}

	@Override
	public int getGroupCount() {
		return replyList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		RelativeLayout group_layout=(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.group_layout, null);
		TextView tv_group=(TextView) group_layout.findViewById(R.id.tv_group);
		ImageView imv_isexpandable=(ImageView) group_layout.findViewById(R.id.imv_isexpandable);
		if (getChildrenCount(groupPosition)<1) {
			imv_isexpandable.setVisibility(View.GONE);
		}else {
			if (isExpanded) {
				imv_isexpandable.setImageResource(R.drawable.icon_jiantou_down);
			}else {
				imv_isexpandable.setImageResource(R.drawable.icon_jiantou_up);
			}
		}
		
		tv_group.setText(this.getGroup(groupPosition).toString());
		return group_layout;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	

}
