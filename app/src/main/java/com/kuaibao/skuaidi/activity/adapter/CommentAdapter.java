package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.OverAreaComment;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<OverAreaComment> list;

	public CommentAdapter(Context context, ArrayList<OverAreaComment> list) {
		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
			holder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

//		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
//				.bitmapConfig(Config.RGB_565).build();
//		ImageLoader.getInstance().displayImage(list.get(position).getFace(), holder.iv_portrait, imageOptions);
		GlideUtil.GlideUrlToImg(context,list.get(position).getFace(),holder.iv_portrait);
		holder.tv_name.setText(list.get(position).getWduser_name());
		holder.tv_comment.setText(list.get(position).getContent());
		holder.tv_date.setText(list.get(position).getTimestamp());

		return convertView;
	}

	private static class ViewHolder {
		ImageView iv_portrait;
		TextView tv_name;
		TextView tv_comment;
		TextView tv_date;

	}

}
