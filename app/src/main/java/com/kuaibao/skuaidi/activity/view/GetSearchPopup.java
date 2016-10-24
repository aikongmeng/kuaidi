package com.kuaibao.skuaidi.activity.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

public class GetSearchPopup extends PopupWindow {

	View view;
	TextView tv_1, tv_2, tv_3, tv_4,tv_5;
	ImageView iv_all,iv_time,iv_phone,iv_add_numbers,iv_unread;

	public GetSearchPopup(Activity context,
			android.view.View.OnClickListener itemsOnClick2,int state) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.pop_search, null);
		tv_1 = (TextView) view.findViewById(R.id.all);
		tv_2 = (TextView) view.findViewById(R.id.by_time);
		tv_3 = (TextView) view.findViewById(R.id.by_phone);
		tv_4 = (TextView) view.findViewById(R.id.by_add_numbers);
		tv_5 = (TextView) view.findViewById(R.id.by_unread);
		
		iv_all = (ImageView) view.findViewById(R.id.iv_all);
		iv_time = (ImageView) view.findViewById(R.id.iv_time);
		iv_phone = (ImageView) view.findViewById(R.id.iv_phone);
		iv_add_numbers = (ImageView) view.findViewById(R.id.iv_add_numbers);
		iv_unread = (ImageView) view.findViewById(R.id.iv_unread);
		
		// 设置按钮监听
		tv_1.setOnClickListener(itemsOnClick2);
		tv_2.setOnClickListener(itemsOnClick2);
		tv_3.setOnClickListener(itemsOnClick2);
		tv_4.setOnClickListener(itemsOnClick2);
		tv_5.setOnClickListener(itemsOnClick2);
		
		
		
		int checkedColor = SkuaidiSkinManager.getTextColor("main_color");
		
		if(state == 1){
			tv_1.setTextColor(checkedColor);
			iv_all.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_all.setVisibility(View.VISIBLE);
		}else if(state==2){
			tv_2.setTextColor(checkedColor);
			iv_time.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_time.setVisibility(View.VISIBLE);
		}else if(state==3){
			tv_3.setTextColor(checkedColor);
			iv_phone.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_phone.setVisibility(View.VISIBLE);
		}else if(state==4){
			tv_4.setTextColor(checkedColor);
			iv_add_numbers.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_add_numbers.setVisibility(View.VISIBLE);
		}else if(state==5){
			tv_5.setTextColor(checkedColor);
			iv_unread.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_unread.setVisibility(View.VISIBLE);
		}
		
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

		view.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});

	}

}
