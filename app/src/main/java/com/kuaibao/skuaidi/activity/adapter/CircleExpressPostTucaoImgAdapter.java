package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.BitmapUtil;

import java.io.IOException;
import java.util.List;

public class CircleExpressPostTucaoImgAdapter extends BaseAdapter {

	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	private Context context;
	private Bitmap mbitmap;

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

	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public CircleExpressPostTucaoImgAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return (BitmapUtil.getBmp(context).size() + 1);
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public class ViewHolder {
		public ImageView image;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.shop_goods_image_item, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grid_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == BitmapUtil.getBmp(context).size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
					R.drawable.icon_addpic_unfocused));
			if (position == 9) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			holder.image.setImageBitmap(BitmapUtil.getBmp(context).get(position));
		}
		return convertView;
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
