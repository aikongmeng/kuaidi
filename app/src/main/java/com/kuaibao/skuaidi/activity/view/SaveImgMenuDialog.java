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
 * @author hh 快递圈 保存图片的dialog
 */
public class SaveImgMenuDialog extends Dialog {
public static boolean flag = false;
	
	public SaveImgMenuDialog(Context context, int theme) {
		super(context, theme);
	}

	public SaveImgMenuDialog(Context context,boolean flag) {
		super(context);
		SaveImgMenuDialog.flag = flag;
	}
	
	public static class Builder {
		private Context context;
	
		
		private DialogInterface.OnClickListener saveimgButtonClickListener;
		private DialogInterface.OnClickListener cancelButtOnClickListener;
		//保存图片
		private LinearLayout ll_save_img;
		//取消
		private LinearLayout ll_hurry_cancel;
		
		public Builder(Context context) {
			this.context = context;
		}


		public Builder setSaveButton(
				DialogInterface.OnClickListener listener) {
			this.saveimgButtonClickListener = listener;
			return this;
		}
		public Builder setCancelButton(
				DialogInterface.OnClickListener listener) {
			this.cancelButtOnClickListener = listener;
			return this;
		}
		

		public SaveImgMenuDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final SaveImgMenuDialog dialog = new SaveImgMenuDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_saveimg_menu, null);
			
			
			ll_save_img = (LinearLayout) layout.findViewById(R.id.ll_save_img);
			
			
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
				
			
			ll_save_img.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					saveimgButtonClickListener.onClick(dialog,
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
			return ll_save_img;
		}
	}
}
