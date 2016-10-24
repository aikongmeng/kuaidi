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

public class ChooseIdentityPop extends PopupWindow {

	View view;
	RelativeLayout rl_kuaidiyuan, rl_daishoudian, rl_wangdiankefu,
			rl_wangdianjingli, rl_paotuiren, rl_other_identity;

	public ChooseIdentityPop(Activity content,android.view.View.OnClickListener onClickListener){
		super(content);
		LayoutInflater layoutInflater =  (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.pop_select_identity, null);
		
		rl_kuaidiyuan = (RelativeLayout) view.findViewById(R.id.rl_kuaidiyuan);
		rl_daishoudian = (RelativeLayout) view.findViewById(R.id.rl_daishoudian);
		rl_wangdiankefu = (RelativeLayout) view.findViewById(R.id.rl_wangdiankefu);
		rl_wangdianjingli = (RelativeLayout) view.findViewById(R.id.rl_wangdianjingli);
		rl_paotuiren = (RelativeLayout) view.findViewById(R.id.rl_paotuiren);
		rl_other_identity = (RelativeLayout) view.findViewById(R.id.rl_other_identity);
		
		rl_kuaidiyuan.setOnClickListener(onClickListener);
		rl_daishoudian.setOnClickListener(onClickListener);
		rl_wangdiankefu.setOnClickListener(onClickListener);
		rl_wangdianjingli.setOnClickListener(onClickListener);
		rl_paotuiren.setOnClickListener(onClickListener);
		rl_other_identity.setOnClickListener(onClickListener);
		
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.FILL_PARENT);
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
