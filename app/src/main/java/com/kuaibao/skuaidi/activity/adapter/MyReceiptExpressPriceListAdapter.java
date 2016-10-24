package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class MyReceiptExpressPriceListAdapter extends BaseAdapter {
	private Context context;

	private List<ShopInfoImg> shopInfoImgs;

	@SuppressLint("UseSparseArrays")
	public MyReceiptExpressPriceListAdapter(Context context,
			List<ShopInfoImg> shopInfoImgs) {
		this.context = context;
		this.shopInfoImgs = shopInfoImgs;
	}

	@Override
	public int getCount() {
		return shopInfoImgs == null ? 0 : shopInfoImgs.size();
	}

	@Override
	public ShopInfoImg getItem(int position) {
		return shopInfoImgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private ImageView imageView;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context, R.layout.outside_detail_griditem,
				null);
		ShopInfoImg info = getItem(position);
		imageView = (ImageView) view.findViewById(R.id.iv_icon);
//		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//				.cacheInMemory(true).cacheOnDisk(true)
//				.bitmapConfig(Config.RGB_565).build();
//		ImageLoader.getInstance().displayImage(info.getPhotoURL(), imageView,
//				imageOptions);
		GlideUtil.GlideUrlToImg(context,info.getPhotoURL(),imageView);
		if (info.isChecked() == false) {
			view.findViewById(R.id.iv_checked_status).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.iv_checked_status).setVisibility(View.VISIBLE);
		}
		return view;
	}
	
	
	public List<ShopInfoImg> getShopInfoImgs(){
		return shopInfoImgs;
	}
	
	public ArrayList<String> getImageUrls(){
		ArrayList<String> urls = new ArrayList<String>();
		for (int i = 0; i < shopInfoImgs.size(); i++) {
			urls.add(shopInfoImgs.get(i).getPhotoURL());
		}
		return urls;
	}
	
	
	public void chooseState(int index) {
		getItem(index).setChecked(
				getItem(index).isChecked() == true ? false : true);
		notifyDataSetChanged();
	}


	private Bitmap drawableToBitmap(Drawable drawable) {
		/*
		 * Drawable转化为Bitmap
		 */
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

}
