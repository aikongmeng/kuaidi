package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class GetChooseCirclePop extends PopupWindow {
	View view;

	RelativeLayout ll_all;
	RelativeLayout ll_thevoice_of_the_customer;
	RelativeLayout ll_my;
	RelativeLayout ll_search;
	TextView tv_text1;
	TextView tv_text2;
	TextView tv_text3;
	TextView tv_text4;
	ImageView iv_gou1;
	ImageView iv_gou2;
	ImageView iv_gou3;
	ImageView iv_gou4;

	public GetChooseCirclePop(Context context, String postMark,
			OnClickListener onClickListener) {
		super(context);

		view = View.inflate(context, R.layout.pop_choose_circle, null);

		ll_all = (RelativeLayout) view.findViewById(R.id.ll_all);
		ll_thevoice_of_the_customer = (RelativeLayout) view.findViewById(R.id.ll_thevoice_of_the_customer);
		ll_my = (RelativeLayout) view.findViewById(R.id.ll_my);
		ll_search = (RelativeLayout) view.findViewById(R.id.ll_search);

		tv_text1 = (TextView) view.findViewById(R.id.tv_text1);
		tv_text2 = (TextView) view.findViewById(R.id.tv_text2);
		tv_text3 = (TextView) view.findViewById(R.id.tv_text3);
		tv_text4 = (TextView) view.findViewById(R.id.tv_text4);

		iv_gou1 = (ImageView) view.findViewById(R.id.iv_gou1);
		iv_gou2 = (ImageView) view.findViewById(R.id.iv_gou2);
		iv_gou3 = (ImageView) view.findViewById(R.id.iv_gou3);
		iv_gou4 = (ImageView) view.findViewById(R.id.iv_gou4);
		
		if (postMark.equals("all")) {
			iv_gou1.setVisibility(View.VISIBLE);
		}else if(postMark.equals("customer")){
			iv_gou4.setVisibility(View.VISIBLE);
		}else if (postMark.equals("my")) {
			iv_gou2.setVisibility(View.VISIBLE);
		}else {
			iv_gou3.setVisibility(View.VISIBLE);
		}

		view.setOnClickListener(onClickListener);
		ll_all.setOnClickListener(onClickListener);
		ll_thevoice_of_the_customer.setOnClickListener(onClickListener);
		ll_my.setOnClickListener(onClickListener);
		ll_search.setOnClickListener(onClickListener);

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

	}

}
