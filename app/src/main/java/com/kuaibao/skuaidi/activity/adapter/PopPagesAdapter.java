package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class PopPagesAdapter extends BaseAdapter {
	private Context context;
	private int totalPages;
	private int presentPage;
	LayoutInflater inflater;

	public PopPagesAdapter(Context context, int totalPages, int presentPage) {
		this.context = context;
		this.totalPages = totalPages;
		this.presentPage = presentPage;
		inflater = LayoutInflater.from(context);
	}
	
	public void setPresentPage(int presentPage){
		this.presentPage=presentPage;
	}
	
	public int getPresentPage(){
		return presentPage;
	}

	public int getCount() {
		return totalPages;
	}

	public Object getItem(int position) {
		return position + 1;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_page, null);
			holder.tv_page = (TextView) convertView
					.findViewById(R.id.tv_page_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_page.setText("第" + (position + 1) + "页");
		
		holder.tv_page.setTextColor(Color.BLACK);
		if (position == 0) {
			holder.tv_page
					.setBackgroundResource(R.drawable.shape_top_white_with_stroke);
		} else if (position+1 == getCount()) {
			holder.tv_page
					.setBackgroundResource(R.drawable.shape_bottom_white_with_stroke);
		} else {
			holder.tv_page
					.setBackgroundResource(R.drawable.shape_white_no_radius_with_stroke);
		}

		holder.tv_page.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					holder.tv_page.setTextColor(Color.WHITE);
					if (position == 0) {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_top_blue_with_stroke);
					} else if (position+1 == getCount()) {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_bottom_blue_with_stroke);
					} else {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_blue_no_radius_with_stroke);
					}
				} else {
					holder.tv_page.setTextColor(Color.BLACK);
					if (position == 0) {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_top_white_with_stroke);
					} else if (position+1 == getCount()) {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_bottom_white_with_stroke);
					} else {
						holder.tv_page
								.setBackgroundResource(R.drawable.shape_white_no_radius_with_stroke);
					}

				}
				return false;
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_page;
	}
}
