package com.kuaibao.skuaidi.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Follower;

import java.util.ArrayList;
import java.util.List;

public class FollowerAdapter extends BaseAdapter {
	private List<Follower> followers;
	private boolean isChoiceModel = false;// true,消息群发中的选择模式

	public FollowerAdapter(List<Follower> followers) {
		this.followers = followers;
	}

	@Override
	public int getCount() {
		return followers.size();
	}

	@Override
	public Object getItem(int position) {
		return followers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void changeDataset(List<Follower> followers) {
		this.followers = followers;
		this.notifyDataSetChanged();
	}

	public void changeMode(boolean isChoiceModel) {
		this.isChoiceModel = isChoiceModel;
		this.notifyDataSetChanged();
	}

	public List<Follower> getList() {
		return followers;
	}

	public void clearSelected() {
		for (int i = 0, j = followers.size(); i < j; i++) {
			followers.get(i).setSelected(false);
		}
		this.notifyDataSetChanged();
	}
	public List<Follower> getCheckedList(){
		List<Follower> selectedList=new ArrayList<Follower>();
		for (int i = 0, j = followers.size(); i < j; i++) {
			if(followers.get(i).isSelected()){
				selectedList.add(followers.get(i));
			}
		}
		return selectedList;
	}

	/**
	 * 全选
	 */
	public void selectAll() {
		for (int i = 0, j = followers.size(); i < j; i++) {
			followers.get(i).setSelected(true);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_follower_list, parent, false);

			holder.iv_accountType = (ImageView) convertView.findViewById(R.id.img_order_icon);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.iv_selector = (ImageView) convertView.findViewById(R.id.iv_selector);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (isChoiceModel) {
			holder.iv_selector.setVisibility(View.VISIBLE);
			if (followers.get(position).isSelected()) {
				holder.iv_selector.setImageResource(R.drawable.batch_add_checked);
			} else {
				holder.iv_selector.setImageResource(R.drawable.select_edit_identity);
			}
		} else {
			holder.iv_selector.setVisibility(View.GONE);
		}
		holder.tv_content.setText(followers.get(position).getName());
		String type = followers.get(position).getType();
		if ("c".equals(type)) {
			holder.iv_accountType.setImageResource(R.drawable.icon_kuaidi);
		} else if ("alipay".equals(type)) {
			holder.iv_accountType.setImageResource(R.drawable.icon_alipay);
		} else {
			holder.iv_accountType.setImageResource(R.drawable.icon_weixin);
		}

		return convertView;
	}

	public static class ViewHolder {
		public TextView tv_content;
		public ImageView iv_accountType;
		public ImageView iv_selector;

	}

}
