package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;

/**
 * @author a13 查快递模块，用于弹出派件员信息的dialog
 */
public class HurryOrderMenuDialog extends Dialog {
public static boolean flag = false;
	
	public HurryOrderMenuDialog(Context context, int theme) {
		super(context, theme);
	}

	public HurryOrderMenuDialog(Context context,boolean flag) {
		super(context);
		HurryOrderMenuDialog.flag = flag;
	}
	
	public static class Builder {
		private Context context;
	
		
		private DialogInterface.OnClickListener messageButtonClickListener;
		private DialogInterface.OnClickListener callButtonClickListener;
		private DialogInterface.OnClickListener cancelButtOnClickListener;
		//发短信
		private LinearLayout ll_hurry_message;
		//取消
		private LinearLayout ll_hurry_cancel;
		//打电话
		private LinearLayout ll_hurry_call;
		
		public Builder(Context context) {
			this.context = context;
		}


		public Builder setMessageButton(
				DialogInterface.OnClickListener listener) {
			this.messageButtonClickListener = listener;
			return this;
		}
		public Builder setCancelButton(
				DialogInterface.OnClickListener listener) {
			this.cancelButtOnClickListener = listener;
			return this;
		}
		
		public Builder setCallButton(DialogInterface.OnClickListener listener) {
			this.callButtonClickListener = listener;
			return this;
		}
		

		public HurryOrderMenuDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final HurryOrderMenuDialog dialog = new HurryOrderMenuDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_hurryorder_menu, null);
			
			
			ll_hurry_message = (LinearLayout) layout.findViewById(R.id.ll_hurry_message);
			if (flag==false) {
				ll_hurry_message.setVisibility(View.GONE);
			}
			
			ll_hurry_call = (LinearLayout) layout.findViewById(R.id.ll_hurry_call);
			ll_hurry_cancel = (LinearLayout) layout.findViewById(R.id.ll_hurry_cancel);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			Window window=dialog.getWindow();//设置dialog显示位置
			window.setGravity(Gravity.BOTTOM);
			
			//设置宽度的屏幕一样
			window.getDecorView().setPadding(0, 0, 0, 0);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
				
			
			ll_hurry_message.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					messageButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_NEUTRAL);
				}
			});

			
			ll_hurry_call.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					callButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_NEUTRAL);
				}
			});
			ll_hurry_cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					cancelButtOnClickListener.onClick(dialog, BUTTON_NEUTRAL);
					
				}
			});
			return dialog;
		}
		public View GetCall() {
			return ll_hurry_message;
		}
	}
}
