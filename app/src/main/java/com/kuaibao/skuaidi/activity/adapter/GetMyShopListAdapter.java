package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ShopInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.Constants;

import java.util.List;

public class GetMyShopListAdapter extends BaseAdapter {
	private List<ShopInfo> shopInfos;// 我的店铺集合
	private ShopInfo shopInfo;// 店铺单条信息
	private Context context;
	private LayoutInflater inflater;

	TextView tv_shop_name;// 我的店铺名称
	TextView tv_warning;// 更新店铺信息失败-警告
	TextView tv_shop_type;// 我的店铺类型
	ImageView iv_shop_logo;// 我的店铺logo
	TextView tv_shop_address;// 我的店铺地址
	TextView tv_money;// 我的资费标准
	TextView tv_num;// 我的服务次数
	LinearLayout ll_shop_name;// 没有图片的时候显示的logo的底部的文字区域
	TextView tv_name;// 店铺名称--用于显示在店铺logo上方

	public GetMyShopListAdapter(Context context, List<ShopInfo> shopInfos) {
		this.context = context;
		this.shopInfos = shopInfos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return shopInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return shopInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.my_shop_item, null);
		tv_shop_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
		tv_warning = (TextView) convertView.findViewById(R.id.tv_warning);
		tv_shop_type = (TextView) convertView.findViewById(R.id.tv_shop_type);
		iv_shop_logo = (ImageView) convertView.findViewById(R.id.iv_shop_logo);
		tv_shop_address = (TextView) convertView
				.findViewById(R.id.tv_shop_address);
		tv_money = (TextView) convertView.findViewById(R.id.tv_money);
		tv_num = (TextView) convertView.findViewById(R.id.tv_num);
		ll_shop_name = (LinearLayout) convertView.findViewById(R.id.ll_shop);
		tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		shopInfo = shopInfos.get(position);
		tv_shop_name.setText(shopInfo.getShop_name());// 设置店铺名称
		tv_shop_name.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		Drawable left1 = context.getResources().getDrawable(SkuaidiSkinManager.getSkinResId("shop_name_icon"));
		Drawable right1 = context.getResources().getDrawable(R.drawable.finish_outside_jiantou_right);
		left1.setBounds(0, 0, left1.getMinimumWidth(),left1.getMinimumHeight());
		right1.setBounds(0, 0, right1.getMinimumWidth(), right1.getMinimumHeight());
		tv_shop_name.setCompoundDrawables(left1, null, right1, null);
		
		// *************设置店铺更新失败警告*************

		// ====================

		// *************设置店铺更新失败警告*************
		tv_shop_type.setText(shopInfo.getShop_type());// 设置店铺类型
		// *************设置店铺LOGO*************
		if (!shopInfo.getShop_logo().equals("")) {
			ll_shop_name.setVisibility(View.GONE);
			ll_shop_name.getBackground().setAlpha(60);
			String imageUrl = Constants.URL_MY_SHOP_IMG_ROOT + "thumb."
					+ shopInfo.getShop_logo();
			// 从网络加载图片
//			DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//					.cacheInMemory(true).cacheOnDisk(true)
//					.bitmapConfig(Config.RGB_565).build();
//			ImageLoader.getInstance().displayImage(imageUrl, iv_shop_logo,
//					imageOptions);
			GlideUtil.GlideUrlToImg(context,imageUrl,iv_shop_logo);
		} else {
			ll_shop_name.getBackground().setAlpha(153);// alpha值从0~255，这里取255的60%
			tv_name.setText(shopInfo.getShop_name());
		}
		// *************设置店铺LOGO*************
		tv_shop_address.setText(shopInfo.getShop_address());// 设置店铺地址

		if (shopInfo.getRevenue_demands().equals("1")) {
			tv_money.setText("5元+5%（手续费）");// 设置店铺跑腿资费
		} else if (shopInfo.getRevenue_demands().equals("2")) {
			tv_money.setText("10元");// 设置店铺跑腿资费
		}
		tv_num.setText(shopInfo.getService_times());// 设置店铺服务次数
		return convertView;
	}
}
