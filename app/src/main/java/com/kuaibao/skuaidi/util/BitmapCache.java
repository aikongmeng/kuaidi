package com.kuaibao.skuaidi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kuaibao.skuaidi.activity.AlbumSystemActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class BitmapCache extends RxRetrofitBaseActivity {
	public Handler handler = new Handler();
	public final String TAG = getClass().getSimpleName();//获取类的类型
	public HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();//图片缓存
	
	/**
	 * 将图片存放到缓存
	 * @param path
	 * @param bmp
	 */
	public void put(String path,Bitmap bmp){
		if(!TextUtils.isEmpty(path) && bmp!=null){
			imageCache.put(path,new SoftReference<Bitmap>(bmp));
		}
	}
	
	
	public void displayBitmap(final ImageView imageView,final String thumbPath,final String sourcePath,final ImageCallback callback){
		if(TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)){
			return;
		}
		
		final String path;
		final boolean isthumbPath;
		if(!TextUtils.isEmpty(thumbPath)){
			path = thumbPath;
			isthumbPath = true;
		}else if(!TextUtils.isEmpty(sourcePath)){
			path = sourcePath;
			isthumbPath = false;
		}else {
			return ;
		}
		
		if(imageCache.containsKey(path)){
			SoftReference<Bitmap> reference = imageCache.get(path);
			Bitmap bmp = reference.get();
			if(bmp!=null){
				if(callback != null){
					callback.imageLoad(imageView, bmp, sourcePath);
				}
				imageView.setImageBitmap(bmp);
				return ;
			}
		}
		imageView.setImageBitmap(null);
		
		new Thread() {
			Bitmap thumb;

			public void run() {

				try {
					if (isthumbPath) {
						thumb = BitmapFactory.decodeFile(thumbPath);
						if (thumb == null) {
							thumb = revitionImageSize(sourcePath);						
						}						
					} else {
						thumb = revitionImageSize(sourcePath);											
					}
				} catch (Exception e) {	
					
				}
				if (thumb == null) {
					thumb = AlbumSystemActivity.bitmap;
				}
				//Log.e(TAG, "-------thumb------"+thumb);
				put(path, thumb);

				if (callback != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.imageLoad(imageView, thumb, sourcePath);
						}
					});
				}
			}
		}.start();
	}
	
	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
	/**
	 * 接口
	 * @author gudd
	 *
	 */
	public interface ImageCallback{
		void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);//...不定长的数组
	}
}
