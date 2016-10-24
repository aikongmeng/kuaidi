package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.kuaibao.skuaidi.R;

public class SelectSendTimePop extends PopupWindow {

	private View view;
	
	private Button btn_deselect_sendTime;// 取消发送
	private Button btn_exchange_sendTime;// 重置发送时间
	
	
	public SelectSendTimePop(Context context,OnClickListener onClickListener){
		super(context);
		
		view = View.inflate(context, R.layout.select_sendtime_pop, null);
		
		btn_deselect_sendTime = (Button) view.findViewById(R.id.btn_deselect_sendTime);
		btn_exchange_sendTime = (Button) view.findViewById(R.id.btn_exchange_sendTime);
		
		btn_deselect_sendTime.setOnClickListener(onClickListener);
		btn_exchange_sendTime.setOnClickListener(onClickListener);
		
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
