package com.kuaibao.skuaidi.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ExpressRemarksDialog;

public class MannulInputDeliverNoDialog extends Dialog {

	public MannulInputDeliverNoDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MannulInputDeliverNoDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public MannulInputDeliverNoDialog(Context context) {
		super(context);
	}
	
	public static class Builder{
		private Context context;
		private String deliverno="";
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener closeButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		private EditText et_deliver_no;
		private String hint = "";
		private TextView title;
		
		public Builder(Context context) {
			this.context = context;
		}
		
		public String getDeliverNo(){
			return deliverno;
		}
		
		/**
		 * 设置输入框提示文本
		 */
		public void setEditTextHint(String hint){
			this.hint = hint;
			
		}
		public TextView getTileContent() {
			return title;
		}
		
		public Builder setPositiveButton(DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}
		
		public Builder setCloseButton(DialogInterface.OnClickListener listener) {
			this.closeButtonClickListener = listener;
			return this;
		}
		
		public Builder setNegativeButton(DialogInterface.OnClickListener listener){
			this.negativeButtonClickListener=listener;
			return this;
		}
		
		public ExpressRemarksDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ExpressRemarksDialog dialog = new ExpressRemarksDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_mannul_input, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			et_deliver_no = (EditText) layout.findViewById(R.id.et_deliver_no);
			title = (TextView) layout.findViewById(R.id.title);
			et_deliver_no.setInputType(InputType.TYPE_CLASS_NUMBER);
			if(!TextUtils.isEmpty(hint)){
				et_deliver_no.setHint(hint);
			}
			
			final Button btn_positive = (Button) layout.findViewById(R.id.btn_positive);
			final Button btn_negative=(Button) layout.findViewById(R.id.btn_negative);
			final Button btn_close = (Button) layout.findViewById(R.id.btn_close);
			if (et_deliver_no.getText().toString().equals("")) {
				btn_close.setBackgroundDrawable(null);
			}else {
				btn_close.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_close));
			}
			btn_positive.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					deliverno=et_deliver_no.getText().toString();
					positiveButtonClickListener.onClick(
							dialog,
							DialogInterface.BUTTON_POSITIVE);
				}
			});
			et_deliver_no.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					if (et_deliver_no.getText().toString().equals("")) {
						btn_close.setBackgroundDrawable(null);
					}else {
						btn_close.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_close));
					}
				}
			});
			
			btn_close.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					et_deliver_no.setText("");
					closeButtonClickListener.onClick(
							dialog,
							DialogInterface.BUTTON_NEUTRAL);
				}
			});
			btn_close.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						btn_close.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_close_hover));
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						btn_close.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_close));
					}
					return false;
				}
			});
			btn_negative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
				}
			});
			return dialog;
		}
	}

}
