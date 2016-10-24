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

public class CommonDialogListMenu implements OnClickListener{
	
	private Context mContext;
	private Display display;
	private LinearLayout view;
	private Dialog dialog;
	
	private TextView call_normal;
	private TextView call_net;
	private TextView cancel;
	//private ProgressBar pb;
	//private LinearLayout ll_dialog_visible;
	private OnDialogMenuClickListener onclickListener;
	public CommonDialogListMenu(Context context){
		this.mContext = context;
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	public CommonDialogListMenu builder(){
		// 获取Dialog布局
		view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.common_dialog_list_menu, null);
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
	public CommonDialogListMenu setCancleable(boolean cancleable){
		dialog.setCancelable(cancleable);
		return this;
	}
	public CommonDialogListMenu serOnTouchOutSide(boolean cancelOutSide){
		dialog.setCanceledOnTouchOutside(cancelOutSide);
		return this;
	}
	private void initView(){
		call_normal = (TextView) view.findViewById(R.id.call_normal_phone);
		call_net = (TextView) view.findViewById(R.id.call_net_phone);
		cancel = (TextView) view.findViewById(R.id.cancel_call);
		//pb=(ProgressBar) view.findViewById(R.id.dialog_pb);
		//ll_dialog_visible=(LinearLayout) view.findViewById(R.id.ll_dialog_visible);
//		cancel.setVisibility(View.GONE);
		call_normal.setOnClickListener(this);
		call_net.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	public void setCallNormalText(String text){
		this.call_normal.setText(text);
	}
	public void setCancelVisible(boolean visible){
		this.cancel.setVisibility(visible?View.VISIBLE:View.GONE);
	}
	public void setCallNormalVisibility(boolean visible){
		this.call_normal.setVisibility(visible?View.VISIBLE:View.GONE);
	}
	public void setCallNetVisibility(boolean visible){
		this.call_net.setVisibility(visible?View.VISIBLE:View.GONE);
	}
	public void setCancelText(String text){
		this.cancel.setText(text);
	}
	public void setCallNetText(String text){
		this.call_net.setText(text);
	}
	
//	public void showPbShow(){
//		if(pb!=null && pb.getVisibility()==View.GONE){
//			pb.setVisibility(View.VISIBLE);
//		}
//		if(ll_dialog_visible!=null && ll_dialog_visible.getVisibility()==View.VISIBLE){
//			ll_dialog_visible.setVisibility(View.GONE);
//		}
//	}
//	public void hidePb(){
//		if(pb!=null && pb.getVisibility()==View.VISIBLE){
//			pb.setVisibility(View.GONE);
//		}
//	}
	
	public CommonDialogListMenu addClickListener(OnDialogMenuClickListener onclickListener){
		this.onclickListener = onclickListener;
		return this;
	}
	
	
	
	public interface OnDialogMenuClickListener{
		void callNormal();
		void callNet();
		void cancel();
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
		case R.id.cancel_call:
			dimissDialog();
			onclickListener.cancel();
			break;
		case R.id.call_normal_phone:
			dimissDialog();
			onclickListener.callNormal();
			break;
		case R.id.call_net_phone:
			dimissDialog();
			//showPbShow();
			onclickListener.callNet();
			//getMoneyForCall();
			break;
		default:
			break;
		}
	}
}
