package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class NotifyDetailSendYunhuMenuDialog implements OnClickListener {

	private Context mContext = null;
	private Display display = null;
	private Dialog dialog = null;
	private View view = null;
	private BtnOnClickListener btnOnClickListener = null;
	
	private RelativeLayout rl_sendTime = null;// 定时发短信
	private RelativeLayout rlSendMsg = null;// 发短信给今日未接收号码
	private RelativeLayout rlSendCloudVoice = null;// 重呼今日未接号码
	private RelativeLayout rlOneKeyImportPhone = null;// 一键导入手机号码
	private RelativeLayout rl_yunhu_setting = null;//云呼雨点手机号码设置
	private TextView yunhuSettingState;
	private ImageView ivCloseMenu= null;// 关闭菜单按钮 
	private ImageView cb_gun_scan = null;// 客户接听云呼允许接收短信开关
	private ViewGroup rlAutoSendMsg = null;// 失败自动补发短信
	
	
	public NotifyDetailSendYunhuMenuDialog(Context context,BtnOnClickListener onClickListener){
		this.mContext = context;
		this.btnOnClickListener = onClickListener;
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	public NotifyDetailSendYunhuMenuDialog builder(){
		// 获取Dialog布局
		view = LayoutInflater.from(mContext).inflate(R.layout.notify_detail_send_yunhu_menu, null);
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
	
	private void initView(){
		rl_sendTime = (RelativeLayout) view.findViewById(R.id.rl_sendTime);
		rlSendMsg = (RelativeLayout) view.findViewById(R.id.rlSendMsg);
		rlSendCloudVoice = (RelativeLayout) view.findViewById(R.id.rlSendCloudVoice);
		rlOneKeyImportPhone = (RelativeLayout) view.findViewById(R.id.rlOneKeyImportPhone);
		rl_yunhu_setting = (RelativeLayout) view.findViewById(R.id.rl_yunhu_setting);
		yunhuSettingState = (TextView) rl_yunhu_setting.findViewById(R.id.tv_yunhu_setting_state);
		ivCloseMenu = (ImageView) view.findViewById(R.id.ivCloseMenu);
		cb_gun_scan = (ImageView) view.findViewById(R.id.cb_gun_scan);
		rlAutoSendMsg = (ViewGroup) view.findViewById(R.id.rlAutoSendMsg);// 失败自动补发短信-设有点击事件
		ivCloseMenu.setOnClickListener(this);
		rl_sendTime.setOnClickListener(this);
		rlSendMsg.setOnClickListener(this);
		cb_gun_scan.setOnClickListener(this);
		rlSendCloudVoice.setOnClickListener(this);
		rlOneKeyImportPhone.setOnClickListener(this);
		rl_yunhu_setting.setOnClickListener(this);
		rlAutoSendMsg.setOnClickListener(this);
		
		if (SkuaidiSpf.getWhetherCanReceiveMSG(mContext) == true) {
			cb_gun_scan.setBackgroundResource(R.drawable.icon_push_open);
		}else{
			cb_gun_scan.setBackgroundResource(R.drawable.icon_push_close);
		}
	}
	
	
	public NotifyDetailSendYunhuMenuDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}
	
	public void show(){
		dialog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlAutoSendMsg:// 云呼失败自动补发短信
			btnOnClickListener.autoSendMsg(v);
			dialog.dismiss();
			break;
		case R.id.ivCloseMenu:// 关闭菜单
			dialog.dismiss();
			break;
		case R.id.cb_gun_scan:
			if(SkuaidiSpf.getWhetherCanReceiveMSG(mContext) == true){
				SkuaidiSpf.saveWhetherCanReceiveMSG(mContext, false);
				cb_gun_scan.setBackgroundResource(R.drawable.icon_push_close);
			}else{
				SkuaidiSpf.saveWhetherCanReceiveMSG(mContext, true);
				cb_gun_scan.setBackgroundResource(R.drawable.icon_push_open);
			}
			break;
		case R.id.rl_sendTime:
			btnOnClickListener.sendTime(v);
			dialog.dismiss();
			break;
		case R.id.rlSendMsg:
			btnOnClickListener.sendMsg();
			dialog.dismiss();
			break;
		case R.id.rlSendCloudVoice:
			btnOnClickListener.sendCloudVoice();
			dialog.dismiss();
			break;
		case R.id.rlOneKeyImportPhone:
			btnOnClickListener.oneKeyImportPhone();
			dialog.dismiss();
			break;
		case R.id.rl_yunhu_setting:
			btnOnClickListener.yunhuOutPhoneNumSetting();
			dialog.dismiss();
			break;
		default:
			break;
		}
	}
	
	public void modifyYuhuSettingState(String state){
		yunhuSettingState.setText(state);
	}
	
	public interface BtnOnClickListener{
		/** 云呼失败自动补发短信 **/
		void autoSendMsg(View view);
		/**定时发送**/
		void sendTime(View v);
		/**短信今日未接**/
		void sendMsg();
		/**重呼今日未接**/
		void sendCloudVoice();
		/**批量导入手机号码**/
		void oneKeyImportPhone();
		/**去电显示号码设置**/
		void yunhuOutPhoneNumSetting();
	}

}
