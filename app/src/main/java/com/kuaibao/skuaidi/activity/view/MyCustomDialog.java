package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class MyCustomDialog extends Dialog {
	
	public static final int DIALOG_TYPE_ACQUIESCENCE = 0;
	public static final int DIALOG_TYPE_GROUP = 1;
	
	private int loadType = DIALOG_TYPE_ACQUIESCENCE;
	private Context context;
	private OnMyCustomDialogListener mycustomDialogListener;
	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mycustomDialogListener != null) {
				mycustomDialogListener.click(v);
				dismiss();
			}
		}
	};

	private TextView tv_update;
	private TextView tv_cancel;
	private TextView tv_delete;
	private View splitLine;
	
	public MyCustomDialog(Context context,int loadType,
			OnMyCustomDialogListener mycustomDialogListener) {
		super(context, R.style.Dialog);
		this.context = context;
		this.loadType = loadType;
		this.mycustomDialogListener = mycustomDialogListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_mycustom);
		tv_update = (TextView) findViewById(R.id.tv_dialog_mycustom_update);
		tv_cancel = (TextView) findViewById(R.id.tv_dialog_mycustom_cancel);
		tv_delete = (TextView) findViewById(R.id.tv_dialog_mycustom_group_del);
		splitLine = findViewById(R.id.view_split_line);
		if(loadType!=DIALOG_TYPE_GROUP){
			tv_delete.setVisibility(View.GONE);
			splitLine.setVisibility(View.GONE);
		}
		tv_update.setOnClickListener(clickListener);
		tv_cancel.setOnClickListener(clickListener);
		tv_delete.setOnClickListener(clickListener);
		tv_update.setOnTouchListener(mListener);
		tv_cancel.setOnTouchListener(mListener);
		tv_delete.setOnTouchListener(mListener);
	}

	OnTouchListener mListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
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

	public interface OnMyCustomDialogListener {
		void click(View v);
	}
}
