package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.sto.etrhee.activity.E3ProofAddImgActivity.CheckableImage;
import com.kuaibao.skuaidi.util.BitmapUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class E3ProofAddImgAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	private String from;
	private int max;
	private int selectedCount = 0;
	private ArrayList<CheckableImage> imageList = new ArrayList<CheckableImage>();
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public E3ProofAddImgAdapter(Context context, ArrayList<CheckableImage> imageList, String from, int max) {
		this.context = context;
		this.from = from;
		this.max = max;
		inflater = LayoutInflater.from(context);
		this.imageList = imageList;
	}

	public void notifyDatas(ArrayList<CheckableImage> imageList) {
		this.imageList = imageList;
		notifyDataSetChanged();
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public int getSelextedCount() {
		selectedCount = 0;
		for (int i = 0; i < imageList.size(); i++) {
			if (imageList.get(i).isSelected()) {
				selectedCount++;
			}

		}
		return selectedCount;
	}

	@Override
	public int getCount() {
		if ("view".equals(from)) {
			return imageList.size();
		} else {
			return imageList.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.e3_add_image_item, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grid_image);
			holder.iv_checked_status = (ImageView) convertView.findViewById(R.id.iv_checked_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == imageList.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
					R.drawable.icon_addpic_unfocused));
			if (position == 3) {
				holder.image.setVisibility(View.GONE);
			}

			holder.iv_checked_status.setVisibility(View.GONE);
			holder.iv_checked_status.setImageResource(-1);// 如果没选中的话取消设置图片
		} else {
			holder.image.setImageBitmap(imageList.get(position).getBitmap());
			if (imageList.get(position).isSelected()) {
				holder.iv_checked_status.setVisibility(View.VISIBLE);
				holder.iv_checked_status.setImageResource(R.drawable.pic_selected);// 如果选中的话设置选中图片
			} else {
				holder.iv_checked_status.setVisibility(View.GONE);
				holder.iv_checked_status.setImageResource(-1);// 如果没选中的话取消设置图片
			}
		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image, iv_checked_status;
	}

	public void loadLocBitmap(List<String> bitmaps) {
		for (int i = 0; i < bitmaps.size(); i++) {
			try {
				BitmapUtil.getBmp(context).add(BitmapUtil.revitionImageSize(bitmaps.get(i)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void reLoad() {
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
	}

}
