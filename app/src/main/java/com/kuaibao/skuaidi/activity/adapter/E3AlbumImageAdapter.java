package com.kuaibao.skuaidi.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.AlbumImageObj;
import com.kuaibao.skuaidi.sto.etrhee.activity.E3ProofAddImgActivity;
import com.kuaibao.skuaidi.util.BitmapCache;
import com.kuaibao.skuaidi.util.BitmapCache.ImageCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class E3AlbumImageAdapter extends BaseAdapter {

	Activity activity;
	List<AlbumImageObj> albumImageObjs;
	Handler handler;
	BitmapCache bitmapCache;
	TextCallback textCallback = null;
	int selectedTotal = 0;
	public Map<String, String> imagePathMap = new HashMap<String, String>();
	public Map<String, Bitmap> path_bitmap = new HashMap<String, Bitmap>();
	// public List<ShopInfoImg> shopInfoImgs = new ArrayList<ShopInfoImg>();
	private Context fromContext;
	private String from = "";
	private static final int MAX_COUNT = 3;

	public E3AlbumImageAdapter(Activity activity, List<AlbumImageObj> albumImageObjs, Handler handler,
			Context fromContext, String from) {
		this.activity = activity;
		this.from = from;
		this.albumImageObjs = albumImageObjs;
		this.handler = handler;
		this.fromContext = fromContext;
		bitmapCache = new BitmapCache();
	}

	ImageCallback callback = new ImageCallback() {

		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				} else {
					//Log.e("gudd", "callback, bmp not match");
				}
			} else {
				//Log.e("gudd", "callback, bmp null");
			}
		}
	};

	@Override
	public int getCount() {
		int count = 0;
		if (albumImageObjs != null) {
			count = albumImageObjs.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(activity, R.layout.album_image_item, null);
			holder.isselected = (ImageView) convertView.findViewById(R.id.isselected);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final AlbumImageObj albumImageItem = albumImageObjs.get(position);
		holder.iv_image.setTag(albumImageItem.imagePath);
		bitmapCache.displayBitmap(holder.iv_image, albumImageItem.thumbPath, albumImageItem.imagePath, callback);
		// 判断是否选中图片
		if (albumImageItem.isSelect) {
			holder.isselected.setImageResource(R.drawable.pic_selected);// 如果选中的话设置选中图片
		} else {
			holder.isselected.setImageResource(-1);// 如果没选中的话取消设置图片
		}

		holder.iv_image.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				String imagePath = albumImageObjs.get(position).imagePath;// 获取点击图片的路径

				if ((E3ProofAddImgActivity.drr.size() + selectedTotal) < MAX_COUNT) {
					albumImageItem.isSelect = !albumImageItem.isSelect;
					if (albumImageItem.isSelect) {// true
						holder.isselected.setImageResource(R.drawable.pic_selected);
						selectedTotal++;
						if (textCallback != null)
							textCallback.onListener(selectedTotal);
						imagePathMap.put(imagePath, imagePath);

					} else if (!albumImageItem.isSelect) {// false
						holder.isselected.setImageResource(-1);
						selectedTotal--;
						if (textCallback != null)
							textCallback.onListener(selectedTotal);
						imagePathMap.remove(imagePath);
					}
				} else {
					if (albumImageItem.isSelect == true) {

						albumImageItem.isSelect = !albumImageItem.isSelect;
						holder.isselected.setImageResource(-1);
						imagePathMap.remove(imagePath);
						selectedTotal--;
						if (textCallback != null)
							textCallback.onListener(selectedTotal);
					} else {
						Message message = Message.obtain(handler, 0);
						message.sendToTarget();
					}
				}
			}
		});

		return convertView;
	}

	class Holder {
		private ImageView iv_image;
		private ImageView isselected;
	}

	public interface TextCallback {
		void onListener(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textCallback = listener;
	}

}
