package com.kuaibao.skuaidi.activity.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kuaibao.skuaidi.R;

public class ChooseBusinessPop extends PopupWindow {

	View view;
	RelativeLayout rl_tongcheng, rl_guonei, rl_guoji,
	rl_other;

	public ChooseBusinessPop(Activity content,android.view.View.OnClickListener onClickListener){
		super(content);
		LayoutInflater layoutInflater =  (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.pop_select_business, null);
		
		rl_tongcheng = (RelativeLayout) view.findViewById(R.id.rl_tongcheng);
		rl_guonei = (RelativeLayout) view.findViewById(R.id.rl_guonei);
		rl_guoji = (RelativeLayout) view.findViewById(R.id.rl_guoji);
		rl_other = (RelativeLayout) view.findViewById(R.id.rl_other);
		
		rl_tongcheng.setOnClickListener(onClickListener);
		rl_guonei.setOnClickListener(onClickListener);
		rl_guoji.setOnClickListener(onClickListener);
		rl_other.setOnClickListener(onClickListener);
		
		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
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
