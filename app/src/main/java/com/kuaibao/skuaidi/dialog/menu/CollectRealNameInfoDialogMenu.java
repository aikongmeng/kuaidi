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
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * 类名: CollectRealNameInfoDialogMenu <br/> 
 * 方法: TODO  <br/> 
 * 原理: 采集实名信息菜单
 * 时间: 2016-2-15 上午9:50:35 <br/> 
 * 
 * 作者： 顾冬冬 
 * 版本：
 */
public class CollectRealNameInfoDialogMenu implements OnClickListener{
	
	private Context mContext;
	private Display display;
	private View view;
	private Dialog dialog;
	
	private TextView take_photo;
	private TextView manual_input;
	private TextView cancel;
	
	private onClickListener onclickListener;

	public CollectRealNameInfoDialogMenu(Context context){
		this.mContext = context;
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	public CollectRealNameInfoDialogMenu builder(){
		
		// 获取Dialog布局
		view = LayoutInflater.from(mContext).inflate(R.layout.collect_realname_info_dialog_menu, null);

		// 设置Dialog最小宽度为屏幕宽度
		view.setMinimumWidth(display.getWidth());
		initView();
		// 定义Dialog布局和参数
		dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		return this;
	}
	
	/**
	 * 设置是否自动隐藏菜单功能
	 * @param cancleable
	 * @return
	 */
	public CollectRealNameInfoDialogMenu setCancleable(boolean cancleable){
		dialog.setCancelable(cancleable);
		return this;
	}
	
	private void initView(){
		take_photo = (TextView) view.findViewById(R.id.take_photo);
		manual_input = (TextView) view.findViewById(R.id.manual_input);
		cancel = (TextView) view.findViewById(R.id.cancel);
//		cancel.setVisibility(View.GONE);
		
		take_photo.setOnClickListener(this);
		manual_input.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
	public CollectRealNameInfoDialogMenu addClickListener(onClickListener onclickListener){
		this.onclickListener = onclickListener;
		return this;
	}
	
	public interface onClickListener{
		void takePhoto();
		void manuInput();
		void cancel();
	}
	
	public void show(){
		dialog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			onclickListener.cancel();
			dialog.dismiss();
			break;
		case R.id.take_photo:
			onclickListener.takePhoto();
			dialog.dismiss();
			break;
		case R.id.manual_input:
			onclickListener.manuInput();
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

}
