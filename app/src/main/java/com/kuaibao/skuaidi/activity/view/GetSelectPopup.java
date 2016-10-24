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
import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class GetSelectPopup extends PopupWindow {

	View view;
	TextView nofinish, finish, all,back;
	ImageView iv_nofinish, iv_finish, iv_all,iv_back;

	public GetSelectPopup(Activity context,
			android.view.View.OnClickListener itemsOnClick2) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.pop_select, null);
		nofinish = (TextView) view.findViewById(R.id.nofinish);
		finish = (TextView) view.findViewById(R.id.finish);
		back = (TextView) view.findViewById(R.id.back);
		
		all = (TextView) view.findViewById(R.id.all);
		iv_nofinish = (ImageView) view.findViewById(R.id.iv_nofinish);
		iv_finish = (ImageView) view.findViewById(R.id.iv_finish);
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_all = (ImageView) view.findViewById(R.id.iv_all);
		
		int checkedColor = SkuaidiSkinManager.getTextColor("main_color");
		
		if (SkuaidiSpf.getOrderIsChoose(context).equals("unfinished")) {
			iv_nofinish.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_nofinish.setVisibility(View.VISIBLE);
			nofinish.setTextColor(checkedColor);
		} else if (SkuaidiSpf.getOrderIsChoose(context).equals("finished")) {
			iv_finish.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_finish.setVisibility(View.VISIBLE);
			finish.setTextColor(checkedColor);
		} else if(SkuaidiSpf.getOrderIsChoose(context).equals("back")){
			iv_back.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_back.setVisibility(View.VISIBLE);
			back.setTextColor(checkedColor);
		} else if (SkuaidiSpf.getOrderIsChoose(context).equals("")) {
			iv_all.setImageResource(SkuaidiSkinManager.getSkinResId("pop_choose_icon"));
			iv_all.setVisibility(View.VISIBLE);
			all.setTextColor(checkedColor);
		}

		// 设置按钮监听
		nofinish.setOnClickListener(itemsOnClick2);
		finish.setOnClickListener(itemsOnClick2);
		back.setOnClickListener(itemsOnClick2);
		all.setOnClickListener(itemsOnClick2);

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
