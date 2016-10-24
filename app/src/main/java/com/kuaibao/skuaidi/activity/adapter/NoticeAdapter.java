package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NoticeInfo;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeAdapter extends BaseAdapter {

	Context context;
	List<NoticeInfo> notices;
	LayoutInflater inflater;
	String messageTimeResponse;

	public NoticeAdapter(Context context, List<NoticeInfo> notices, int unRead) {
		super();
		this.context = context;
		this.notices = notices;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return notices.size();
	}

	@Override
	public NoticeInfo getItem(int position) {
		return notices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "SimpleDateFormat", "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.listitem_notice, null);
		holder.clock_icon = (ImageView) convertView.findViewById(R.id.clock_icon);
		holder.tv_notice_type = (TextView) convertView.findViewById(R.id.tv_notice_type);
		holder.tv_notice_content = (TextView) convertView.findViewById(R.id.tv_notice_content);
		holder.tv_time = (TextView) convertView.findViewById(R.id.tv_notice_time);
		holder.iv_isnew = (ImageView) convertView.findViewById(R.id.iv_isnew);
		holder.tv_notice_top = (TextView) convertView.findViewById(R.id.tv_notice_top);
		holder.view2 = convertView.findViewById(R.id.view2);
		holder.rl_time = (RelativeLayout) convertView.findViewById(R.id.rl_time);
		holder.show_time = (TextView) convertView.findViewById(R.id.show_time);
		convertView.setTag(holder);
		NoticeInfo info = getItem(position);
		if (info.getUnRead() == 0) {
			holder.iv_isnew.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_notify));
		} else {
			holder.iv_isnew.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_notify_gray));
		}

		holder.tv_notice_type.setText(info.getType());
		holder.tv_notice_content.setText(info.getContent());

		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
		if (position != 0) {
			long indexTime;
			indexTime = getItem(position - 1).getCreatTime() * 1000L;
			messageTimeResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(indexTime);
		}

		String messagetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getCreatTime() * 1000);
		if (info.getIs_top() == 1) {
			// 消息置頂
			holder.tv_notice_top.setVisibility(View.VISIBLE);
		} else if (now.substring(0, 10).equals(messagetime.substring(0, 10))) {
			holder.show_time.setText("今天");
			holder.show_time.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
			holder.clock_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_clock));
			holder.tv_time.setText(messagetime.substring(10, 16));
		} else if (now.substring(0, 8).equals(messagetime.substring(0, 8))
				&& (Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(messagetime.substring(8, 10)) == 1)) {
			holder.show_time.setText("昨天");
			holder.tv_time.setText(messagetime.substring(10, 16));
		} else {
			holder.show_time.setText(messagetime.substring(0, 10));
			holder.tv_time.setText(messagetime.substring(10, 16));
		}

		// 时间一起的留言放在一起~
		if (info.getIs_top() == 1) {
			holder.rl_time.setVisibility(View.GONE);
			holder.view2.setVisibility(View.GONE);
			holder.tv_time.setText(messagetime.substring(0, 10));
		} else if (position != 0 && messagetime.substring(0, 10).equals(messageTimeResponse.substring(0, 10))) {
			holder.rl_time.setVisibility(View.GONE);
			holder.view2.setVisibility(View.GONE);
		} else {
			holder.rl_time.setVisibility(View.VISIBLE);
			holder.view2.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView clock_icon;
		TextView tv_notice_content, tv_time, tv_notice_type, tv_notice_top;
		ImageView iv_isnew;
		RelativeLayout rl_time;// 时间显示区
		TextView show_time;// 显示日期
		View view2;
	}

}
