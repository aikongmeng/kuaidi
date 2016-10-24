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
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.BitmapUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopGoodsImageAdapter extends BaseAdapter {

	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	private Context context;
	private Bitmap mbitmap;
	private List<ShopInfoImg> shopInfoImgs;// 商品下载后的文件名称和ID

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				bitmaps.clear();
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

	public ShopGoodsImageAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
//		return (BitmapUtil.getBmp(context).size() + 1);
		if(shopInfoImgs!=null){
			return shopInfoImgs.size()+1;
		}
		return 1;
		
	}

	public ShopInfoImg getItem(int arg0) {
		if(shopInfoImgs != null){
			return shopInfoImgs.get(arg0);
		}
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

	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.shop_goods_image_item, parent,false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grid_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position == getCount()-1){
			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_addpic_unfocused));
			if (position == 9) {
				holder.image.setVisibility(View.GONE);
			}
		}else{
			if(getItem(position) !=  null){
				//判断图片路径是不是以http://开头的，如果是就从网络上下载图片，否则直接显示本地图片
				String imageUrl = getItem(position).getPhotoName();
				if(imageUrl!=null && imageUrl.contains("http://upload")){
//					DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();
//					ImageLoader.getInstance().displayImage(imageUrl, holder.image, imageOptions);
					GlideUtil.GlideUrlToImg(context,imageUrl,holder.image);
				}else{
					holder.image.setImageBitmap(getItem(position).getBitmap());
				}
				
//				Bitmap bitmap=ImageLoader.getInstance().loadImageSync(imageUrl, imageOptions);
//				bitmaps.add(bitmap);
			}
//			holder.image.setImageBitmap(BitmapUtil.getBmp(context).get(position));
		}
		
//		if (position == BitmapUtil.getBmp(context).size()) {
//			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_addpic_unfocused));
//			if (position == 9) {
//				holder.image.setVisibility(View.GONE);
//			}
//		} else {
//			holder.image.setImageBitmap(BitmapUtil.getBmp(context).get(position));
//		}
		return convertView;
	}
	
	/**
	 * 返回所有缓存中的bitmap
	 * @return
	 */
	public List<Bitmap> getBitmap(){
		return bitmaps;
	}
	
	public void loadLocBitmap(List<String> bitmaps){
		for (int i = 0; i < bitmaps.size(); i++) {
			try {
				BitmapUtil.getBmp(context).add(BitmapUtil.revitionImageSize(bitmaps.get(i)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setImageLoaderData(List<ShopInfoImg> shopInfoImgs){
		this.shopInfoImgs = shopInfoImgs;
		notifyDataSetChanged();
	}
	
	public void reLoad(){
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
	}
}
