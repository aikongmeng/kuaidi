package com.kuaibao.skuaidi.activity.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class DeleteDialog extends Dialog {
	private Context c;
	private String title;
	private OnCustomDialogListener customDialogListener;
	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (customDialogListener != null) {
				customDialogListener.click(v);
			}
		}
	};


	private TextView tv_remark;
	private TextView tv_copy;
	private TextView tv_delete;
	public DeleteDialog(Context context, String title, String message,
			OnCustomDialogListener customDialogListener) {
		super(context,R.style.Dialog);
		this.c = context;
		this.title = title;
		this.customDialogListener = customDialogListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_detele);
		TextView tv_title = (TextView) findViewById(R.id.dialog_detele_title);
		tv_title.setText(title);
		tv_remark = (TextView) findViewById(R.id.dialog_remark);
		tv_copy = (TextView) findViewById(R.id.dialog_copy);
		tv_delete = (TextView) findViewById(R.id.dialog_detele);
		tv_remark.setOnClickListener(clickListener);
		tv_copy.setOnClickListener(clickListener);
		tv_delete.setOnClickListener(clickListener);
		tv_remark.setOnTouchListener(mListener);
		tv_copy.setOnTouchListener(mListener);
	}

	OnTouchListener mListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(c.getResources().getColor(R.color.findexpress_view_bg));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			return false;
		}
	};
	
	public interface OnCustomDialogListener {
		void click(View v);
	}
}
