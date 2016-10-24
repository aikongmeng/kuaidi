package com.kuaibao.skuaidi.activity.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.AlbumSystemObj;
import com.kuaibao.skuaidi.util.BitmapCache;
import com.kuaibao.skuaidi.util.BitmapCache.ImageCallback;

import java.util.List;

public class AlbumSystemAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	Activity activity;
	BitmapCache bitmapCache;

	// 相册列表集
	List<AlbumSystemObj> albumList;

	public AlbumSystemAdapter(Activity activity, List<AlbumSystemObj> list) {
		this.activity = activity;
		this.albumList = list;
		bitmapCache = new BitmapCache();
	}

	ImageCallback imageCallback = new ImageCallback() {

		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				} else {
					//Log.e(TAG, "callback, bmp not match");
				}
			} else {
				//Log.e(TAG, "callback, bmp null");
			}

		}
	};

	@Override
	public int getCount() {
		int count = 0;
		if(albumList != null){
			count = albumList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup parent) {
		Holder holder;
		if(arg1 == null){
			holder = new Holder();
			arg1 = View.inflate(activity, R.layout.album_system_item, null);
			holder.iv_album = (ImageView) arg1.findViewById(R.id.iv_album);
			holder.tv_album_name = (TextView) arg1.findViewById(R.id.tv_album_name);
			holder.tv_albumImae_num = (TextView) arg1.findViewById(R.id.tv_albumImae_num);
			arg1.setTag(holder);
		}else {
			holder = (Holder) arg1.getTag();
		}
		AlbumSystemObj albumSystemObj = albumList.get(position);
		holder.tv_albumImae_num.setText("("+albumSystemObj.count+")");// 相册里面的照片数量
		holder.tv_album_name.setText(albumSystemObj.albumName);// 相册名字
		if(albumSystemObj.albumImageList!= null && albumSystemObj.albumImageList.size() > 0){
			String thumbPath = albumSystemObj.albumImageList.get(0).thumbPath;
			String sourcePath = albumSystemObj.albumImageList.get(0).imagePath;
			holder.iv_album.setTag(sourcePath);
			bitmapCache.displayBitmap(holder.iv_album, thumbPath, sourcePath, imageCallback);
		} else {
			holder.iv_album.setImageBitmap(null);
		}
		return arg1;
	}
	
	class Holder{
		private ImageView iv_album;
		private TextView tv_album_name;
		private TextView tv_albumImae_num;
	}

}
