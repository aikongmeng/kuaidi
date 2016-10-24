package com.kuaibao.skuaidi.dialog.menu;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class DynamicCommDialog implements OnClickListener{
	
	private Context mContext;
	private Display display;
	private LinearLayout view;
	private Dialog dialog;
	
	private TextView tv_copy;
	private OnDialogMenuClickListener onclickListener;
	public DynamicCommDialog(Context context){
		this.mContext = context;
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	public DynamicCommDialog builder(){
		// 获取Dialog布局
		view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.common_dialog_list_menu2, null);
		// 设置Dialog最小宽度为屏幕宽度
		//view.setMinimumWidth(display.getWidth());
		initView();
		// 定义Dialog布局和参数
		dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle2);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		//lp.x = 0;
		//lp.y = 0;
        // lp.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.6
         lp.width = (int) (display.getWidth() * 0.5); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
		return this;
	}
	
	/**
	 * 设置是否自动隐藏菜单功能
	 * @param cancleable
	 * @return
	 */
	public DynamicCommDialog setCancleable(boolean cancleable){
		dialog.setCancelable(cancleable);
		return this;
	}
	public DynamicCommDialog serOnTouchOutSide(boolean cancelOutSide){
		dialog.setCanceledOnTouchOutside(cancelOutSide);
		return this;
	}
	private void initView(){
		tv_copy = (TextView) view.findViewById(R.id.copy_tv);
		tv_copy.setOnClickListener(this);
	}

	public DynamicCommDialog addClickListener(OnDialogMenuClickListener onclickListener){
		this.onclickListener = onclickListener;
		return this;
	}
	
	
	
	public interface OnDialogMenuClickListener{
		void copy();
	}
	
	public void showDialog(){
		if(!dialog.isShowing())
		dialog.show();
	}
	public void dimissDialog(){
		if(dialog.isShowing())
			dialog.dismiss();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.copy_tv:
			dimissDialog();
			onclickListener.copy();
			break;
		default:
			break;
		}
	}
}
