package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.BranchInfo;

import java.util.List;

public class BranchAdapter extends BaseAdapter {
	
	Context context;
	List<BranchInfo> overareas;
	LayoutInflater inflater;
	
	public BranchAdapter(Context context, List<BranchInfo> overareas) {
		super();
		this.context = context;
		this.overareas = overareas;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return overareas.size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		return overareas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.listitem_branch, null);
			holder.tv_branch_name=(TextView) convertView.findViewById(R.id.tv_branch_name);
			holder.tv_branch_address=(TextView) convertView.findViewById(R.id.tv_branch_address);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		
		if (position==overareas.size()) {
			holder.tv_branch_name.setText("其他网点");
			holder.tv_branch_address.setText("");
		}else {
			BranchInfo overarea= overareas.get(position);
			holder.tv_branch_name.setText(overarea.getIndexShopName());
			holder.tv_branch_address.setText(overarea.getAddress_detail());
		}
		
		
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_branch_name,tv_branch_address;
	}

}
