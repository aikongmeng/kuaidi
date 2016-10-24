package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ClickItem;
import com.kuaibao.skuaidi.entry.WuliuItem;

import java.util.Collections;
import java.util.List;

public class TrachResultAdapter extends BaseAdapter {
	private boolean flag = false;
	LayoutInflater inflater;

	Context context;
	List<WuliuItem> wuliuItems;
	String clicks;
	Handler handler;
	String isException;
	WuliuItem wuliuItem;
	ClickItem clickItem;

	public TrachResultAdapter(Context context, Handler handler, boolean flag,
			List<WuliuItem> wuliuItems) {
		super();
		this.context = context;
		Collections.reverse(wuliuItems);
		this.wuliuItems = wuliuItems;
		this.handler = handler;
		inflater = LayoutInflater.from(context);
		wuliuItem = new WuliuItem();
		this.flag = flag;
	}

	@Override
	public int getCount() {

		return wuliuItems.size();
	}

	@Override
	public WuliuItem getItem(int position) {
		return wuliuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(context).inflate(
				R.layout.trach_result_item, null);

		TextView tv_time = (TextView) convertView
				.findViewById(R.id.tv_trach_result_time);
		TextView tv_date = (TextView) convertView
				.findViewById(R.id.tv_trach_result_date);
		TextView tv_detail = (TextView) convertView
				.findViewById(R.id.tv_trach_result_detail);
		ImageView iv = (ImageView) convertView
				.findViewById(R.id.iv_trach_resultstate);

		if (position == 0) {
			tv_time.setTextColor(context.getResources().getColor(
					R.color.text_black));
			tv_date.setTextColor(context.getResources().getColor(
					R.color.text_black));
			tv_detail.setTextColor(context.getResources().getColor(
					R.color.text_black));
			tv_detail.setTextSize(16);
			// 异常情况
			if (flag) {
				iv.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_result_exception));
			} else {
				iv.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_success));
			}
		}
		wuliuItem = getItem(position);

		if (wuliuItem.getTime().substring(0, 5) != null) {
			tv_time.setText(wuliuItem.getTime().substring(0, 5));
		} else {
			tv_time.setText("");
		}
		tv_date.setText(wuliuItem.getDate().replaceAll("-", "."));
		// 如： 上海虹桥公司的派件员兰XX正在派件 上海虹桥公司为蓝色的
		// if (wuliuItem.getClicks() != null) {
		// holder.tv_detail.setMovementMethod(new LinkMovementMethod());
		// SpannableStringBuilder style = new SpannableStringBuilder(
		// wuliuItem.getDetail());
		// for (int i = 0; i < wuliuItem.getClicks().size(); i++) {
		// final ClickItem clickItem = wuliuItem.getClicks().get(i);
		//
		// style.setSpan(new ClickableSpan() {
		//
		// @Override
		// public void updateDrawState(TextPaint ds) {
		// super.updateDrawState(ds);
		// ds.setColor(Color.parseColor("#3a9af9"));
		// ds.setUnderlineText(false);
		// }
		//
		// @Override
		// public void onClick(View widget) {
		// Message msg = new Message();
		// msg.what = Constants.GET_BRNCH_INFO;
		// msg.obj = clickItem;
		// handler.sendMessage(msg);
		//
		// }
		// }, clickItem.getStart(), clickItem.getEnd(),
		// Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		// }
		// holder.tv_detail.setText(style);
		// } else {
		tv_detail.setText(wuliuItem.getDetail());
		// }

		return convertView;
	}

}
