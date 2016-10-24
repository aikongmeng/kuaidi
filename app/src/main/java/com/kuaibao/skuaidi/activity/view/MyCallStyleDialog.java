package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class MyCallStyleDialog extends Dialog {
	
	public interface OnMyCallStyleDialogListener{
		void click(View v);
	}

	private Context context;
	private OnMyCallStyleDialogListener listener;
	private TextView tv_dialog_call_common;
	private TextView tv_dialog_call_net;
	
	public MyCallStyleDialog(Context context, OnMyCallStyleDialogListener listener) {
		super(context, R.style.Dialog);
		this.context= context;
		this.listener=listener;
	}

	private View.OnTouchListener onTouchListener=new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(context.getResources()
						.getColor(R.color.item_bg_hover));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(context.getResources().getColor(
						R.color.white));
			}
			return false;
		}
	};
	
	private View.OnClickListener clickListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.click(v);
				dismiss();
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_mycall_style);
		tv_dialog_call_common=(TextView) findViewById(R.id.tv_dialog_call_common);
		tv_dialog_call_net=(TextView) findViewById(R.id.tv_dialog_call_net);
		tv_dialog_call_common.setOnClickListener(clickListener);
		tv_dialog_call_net.setOnClickListener(clickListener);
		tv_dialog_call_common.setOnTouchListener(onTouchListener);
		tv_dialog_call_net.setOnTouchListener(onTouchListener);
	}

}
