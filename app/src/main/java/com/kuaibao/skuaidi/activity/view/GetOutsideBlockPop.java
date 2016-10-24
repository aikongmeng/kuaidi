package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kuaibao.skuaidi.R;

public class GetOutsideBlockPop extends PopupWindow {

	View view;
	LinearLayout ll_rob_territory;//抢地盘
	LinearLayout ll_other_shop;//其它店铺
	LinearLayout ll_invite_friends;//邀请朋友
	LinearLayout ll_invite_colleague;//邀请同事
	
	public GetOutsideBlockPop(Context context,OnClickListener onClickListener){
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		view  = inflater.inflate(R.layout.pop_outside_block_more, null);
		ll_rob_territory = (LinearLayout) view.findViewById(R.id.ll_rob_territory);
		ll_other_shop = (LinearLayout) view.findViewById(R.id.ll_other_shop);
		ll_invite_friends = (LinearLayout) view.findViewById(R.id.ll_invite_friends);
		ll_invite_colleague = (LinearLayout) view.findViewById(R.id.ll_invite_colleague);
		
		ll_rob_territory.setOnClickListener(onClickListener);
		ll_other_shop.setOnClickListener(onClickListener);
		ll_invite_friends.setOnClickListener(onClickListener);
		ll_invite_colleague.setOnClickListener(onClickListener);
		
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
