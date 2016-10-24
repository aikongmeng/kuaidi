package com.kuaibao.skuaidi.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.SaveImgMenuDialog;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.util.AlbumFileUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * 单张图片显示Fragment
 * 
 * @author gudd
 */
public class CircleExpressGridItemFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	private int position;
	private Bitmap save_bitmap;

	/**
	 * 
	 * @param imageUrl单张图片路径
	 * @param shopInfoImgs图片集合
	 * @param position当前图片下标
	 * @return
	 */
	public static CircleExpressGridItemFragment newInstance(String imageUrl, int position) {

		final CircleExpressGridItemFragment f = new CircleExpressGridItemFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		args.putInt("position", position);
		f.setArguments(args);
		return f;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
		position = getArguments() != null ? getArguments().getInt("position") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.circle_express_grid_image_item_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);

		// 点击图片上面关闭界面
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				if (getActivity() != null)
					getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mImageUrl != null && mImageUrl.contains("http://upload")) {
			// ImageLoader 下载显示图片
			//Log.i("iiii", ">>>>>>>>>>url" + mImageUrl);
			// save_bitmap=ImageLoader.getInstance().loadImageSync(mImageUrl);
			/*
			 * InputStream is=AlbumFileUtils.getStreamFromURL(mImageUrl);
			 * save_bitmap=InputStream2Bitmap(is);
			 *///Log.i("iiii", ">>>>>>>>>>save_bitmap" + save_bitmap);
			//GlideUtil.GlideUrlToImg(getActivity(),mImageUrl,mImageView);
			Glide.with(this).load(mImageUrl).asBitmap()
					.placeholder(R.drawable.ic_loading)
					.error(R.drawable.ic_fail)
					.format(DecodeFormat.PREFER_RGB_565)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(new SimpleTarget<Bitmap>() {
				@Override
				public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
					mImageView.setImageBitmap(resource);
					mAttacher.update();
				}
			});
//			ImageLoader.getInstance().displayImage(mImageUrl, mImageView, new SimpleImageLoadingListener() {
//				@Override
//				public void onLoadingStarted(String imageUri, View view) {
//					progressBar.setVisibility(View.VISIBLE);
//				}
//
//				@Override
//				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//					String message = "";
//					if (failReason == null) {
//						return;
//					}
//					switch (failReason.getType()) {
//					case IO_ERROR:
//						message = "下载错误";
//						break;
//					case DECODING_ERROR:
//						message = "图片无法显示";
//						break;
//					case NETWORK_DENIED:
//						message = "网络有问题，无法下载";
//						break;
//					case OUT_OF_MEMORY:
//						message = "图片太大无法显示";
//						break;
//					case UNKNOWN:
//						message = "未知的错误";
//						break;
//					}
//					// Toast.makeText(getActivity(), message,
//					// Toast.LENGTH_SHORT).show();
//					progressBar.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//					save_bitmap = loadedImage;
//					progressBar.setVisibility(View.GONE);
//					mAttacher.update();
//				}
//			});
		} else {// 显示本地图片
			List<ShopInfoImg> shopInfoImgs = (List<ShopInfoImg>) SKuaidiApplication.getInstance().onReceiveMsg(
					"PhotoShowActivity", "ShopInfos");// 获取网络上的图片
			mImageView.setImageBitmap(shopInfoImgs.get(position).getBitmap());

		}
		// 长按弹出保存图片的菜单
		mAttacher.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				final SaveImgMenuDialog.Builder savebuilder = new SaveImgMenuDialog.Builder(getActivity());
				savebuilder.setSaveButton(new OnClickListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true) {

							Date date = new Date();
							AlbumFileUtils.saveBitmap(save_bitmap, date.getTime() + "");
							Toast.makeText(getActivity(), "保存成功", 0).show();
							dialog.dismiss();
						} else {
							Toast.makeText(getActivity(), "请插入手机内存卡！", 0).show();
						}
					}
				});
				// 取消
				savebuilder.setCancelButton(new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();

					}
				});
				savebuilder.create().show();
				return false;
			}
		});

	}

	public Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);

	}

}
