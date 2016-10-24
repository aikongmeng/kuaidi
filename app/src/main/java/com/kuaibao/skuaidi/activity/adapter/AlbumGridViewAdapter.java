package com.kuaibao.skuaidi.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.AlbumImageObj;
import com.kuaibao.skuaidi.util.BitmapCache;

import java.util.ArrayList;

/**
 * 这个是显示相册里的所有图片时用的适配器
 *
 */
public class AlbumGridViewAdapter extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	private Context mContext;
	private ArrayList<AlbumImageObj> dataList;
	private ArrayList<AlbumImageObj> selectedDataList;
	private DisplayMetrics dm;
	BitmapCache cache;
	public AlbumGridViewAdapter(Context c, ArrayList<AlbumImageObj> dataList,
			ArrayList<AlbumImageObj> selectedDataList) {
		mContext = c;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	public int getCount() {
		return dataList.size()+1;
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
		public ToggleButton toggleButton;
		public ImageView choosetoggle;
		public TextView textView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if(position == 0){
			convertView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_take_imageview, parent, false);
			convertView.setOnClickListener(listener);
		}else{
			ViewHolder viewHolder;
			if (convertView == null || !(convertView instanceof RelativeLayout)) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.plugin_camera_select_imageview, parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.image_view);
				viewHolder.toggleButton = (ToggleButton) convertView
						.findViewById(R.id.toggle_button);
				viewHolder.choosetoggle = (ImageView) convertView
						.findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65));
//			lp.setMargins(50, 0, 50,0);
//			viewHolder.imageView.setLayoutParams(lp);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

//			ImageManager2.from(mContext).displayImage(viewHolder.imageView,
//					path, Res.getDrawableID("plugin_camera_camera_default"), 100, 100);
			    AlbumImageObj item = dataList.get(position-1);
				viewHolder.imageView.setTag(item.imagePath);
				cache.displayBitmap(viewHolder.imageView, item.thumbPath, item.imagePath,
						callback);
			viewHolder.toggleButton.setTag(position);
			viewHolder.choosetoggle.setTag(position);
			viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
			if (selectedDataList.contains(dataList.get(position-1))) {
				viewHolder.toggleButton.setChecked(true);
				viewHolder.choosetoggle.setBackgroundResource(R.drawable.icon_guestbook_select_pic_2);
			} else {
				viewHolder.toggleButton.setChecked(false);
				viewHolder.choosetoggle.setBackgroundResource(R.drawable.icon_guestbook_select_pic_1);
			}
		}
		return convertView;

	}

	private class ToggleClickListener implements OnClickListener{
		ImageView chooseBt;
		public ToggleClickListener(ImageView choosebt){
			this.chooseBt = choosebt;
		}

		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null
						&& position <= dataList.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(),chooseBt);
				}
			}
		}
	}


	private OnItemChoosedClickListener mOnItemClickListener;
	private OnClickListener listener;

	public void setOnItemChoosedClickListener(OnItemChoosedClickListener l) {
		mOnItemClickListener = l;
	}

	public void setOnClickListener(OnClickListener l){
		listener = l;
	}

	public interface OnItemChoosedClickListener {
		public void onItemClick(ToggleButton view, int position,
								boolean isChecked, ImageView chooseBt);
	}

}
