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
import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class GetNotifyDetailPopup extends PopupWindow {

	View view;
	TextView tv_lan, tv_pai, tv_qian, tv_ques;
	ImageView iv_lan, iv_pai, iv_qian, iv_ques;

	public GetNotifyDetailPopup(Activity context,
			android.view.View.OnClickListener itemsOnClick2) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.pop_notifydetail, null);
		tv_lan = (TextView) view.findViewById(R.id.tv_lan);
		tv_pai = (TextView) view.findViewById(R.id.tv_pai);
		tv_qian = (TextView) view.findViewById(R.id.tv_qian);
		tv_ques = (TextView) view.findViewById(R.id.tv_ques);
		iv_lan = (ImageView) view.findViewById(R.id.iv_lan);
		iv_pai = (ImageView) view.findViewById(R.id.iv_pai);
		iv_qian = (ImageView) view.findViewById(R.id.iv_qian);
		iv_ques = (ImageView) view.findViewById(R.id.iv_ques);
		if (SkuaidiSpf.getNotifyIsChoose(context).equals("tv_lan")) {
			iv_lan.setVisibility(View.VISIBLE);
			tv_lan.setTextColor(context.getResources().getColor(
					R.color.title_bg));
		} else if (SkuaidiSpf.getNotifyIsChoose(context).equals("tv_pai")) {
			iv_pai.setVisibility(View.VISIBLE);
			tv_pai.setTextColor(context.getResources().getColor(
					R.color.title_bg));
		} else if (SkuaidiSpf.getNotifyIsChoose(context).equals("tv_qian")) {
			iv_qian.setVisibility(View.VISIBLE);
			tv_qian.setTextColor(context.getResources().getColor(
					R.color.title_bg));
		} else if (SkuaidiSpf.getNotifyIsChoose(context).equals("tv_ques")) {
			iv_ques.setVisibility(View.VISIBLE);
			tv_ques.setTextColor(context.getResources().getColor(
					R.color.title_bg));
		}

		// 设置按钮监听
		tv_lan.setOnClickListener(itemsOnClick2);
		tv_pai.setOnClickListener(itemsOnClick2);
		tv_qian.setOnClickListener(itemsOnClick2);
		tv_ques.setOnClickListener(itemsOnClick2);

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
