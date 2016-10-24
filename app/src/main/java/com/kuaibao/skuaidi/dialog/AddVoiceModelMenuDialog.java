package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * @Description 添加录音功能dialog
 * @author 顾冬冬
 * @CreateDate 2015-8-20
 */
public class AddVoiceModelMenuDialog implements OnClickListener {

	private Context mContext = null;
	private Display display = null;
	private Dialog dialog = null;
	private View view = null;
	
	private onBtnClickListener onBtnClickListener = null;
	
	private TextView record_tag = null;// 提示状态
	private ImageView iv_voice_anim_left = null;// 左边录音动画图片
	private ImageView iv_voice_anim_right = null;// 右边录音动画图片
	private TextView record_time = null;// 录音时间
	private TextView tvRestartRecord = null;// 重录文字 
	private TextView tvTryPlay = null;// 试听文字
	private ImageView ivRestartRecord = null;// 重录按钮 
	private ImageView ivRecording = null;// 正在录音按钮
	private ImageView ivTryPlay = null;// 试听按钮
	private TextView tv_cancel = null;// 取消按钮 
	private TextView tv_ok = null;// 确认按钮 
	private RelativeLayout rlRestartRecord = null;// 重录按钮 
	private RelativeLayout rlRecord = null;// 录音按钮 
	private RelativeLayout rlTryPlay = null;// 试听按钮 
	
	public AddVoiceModelMenuDialog(Context context){
		this.mContext = context;
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	public AddVoiceModelMenuDialog builder(){
		// 获取Dialog布局
		view = LayoutInflater.from(mContext).inflate(R.layout.add_voice_model_bottom_record_menu, null);
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
		
		dialog.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				onBtnClickListener.dialogOnkeyListener(dialog, keyCode, event);
				return false;
			}
		});
		
		return this;
	}
	
	private void initView() {
		record_tag = (TextView) view.findViewById(R.id.record_tag);// 提示状态
		iv_voice_anim_left = (ImageView) view.findViewById(R.id.iv_voice_anim_left);// 左边录音动画图片
		iv_voice_anim_right = (ImageView) view.findViewById(R.id.iv_voice_anim_right);// 右边录音动画图片
		record_time = (TextView) view.findViewById(R.id.record_time);// 录音时间
		tvRestartRecord = (TextView) view.findViewById(R.id.tvRestartRecord);// 重录文字 
		tvTryPlay = (TextView) view.findViewById(R.id.tvTryPlay);// 试听文字
		ivRestartRecord = (ImageView) view.findViewById(R.id.ivRestartRecord);// 重录按钮 
		ivRecording = (ImageView) view.findViewById(R.id.ivRecording);// 正在录音按钮
		ivTryPlay = (ImageView) view.findViewById(R.id.ivTryPlay);// 试听按钮
		tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);// 取消按钮 
		tv_ok = (TextView) view.findViewById(R.id.tv_ok);// 确认按钮 
		rlRecord = (RelativeLayout) view.findViewById(R.id.rlRecord);
		rlRestartRecord = (RelativeLayout) view.findViewById(R.id.rlRestartRecord);
		rlTryPlay = (RelativeLayout) view.findViewById(R.id.rlTryPlay);
		
		rlRestartRecord.setOnClickListener(this);
		rlRecord.setOnClickListener(this);
		rlTryPlay.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
	}
	
	public AddVoiceModelMenuDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}
	
	public AddVoiceModelMenuDialog setOnBtnClickListener(onBtnClickListener onBtnClickListener){
		this.onBtnClickListener = onBtnClickListener;
		return this;
	}
	
	public void show(){
		dialog.show();
	}
	
	
	
	public boolean getDialogIsShow(){
		return dialog.isShowing();
	}
	
	public interface onBtnClickListener{
		/**重新录音**/
		void restartRecord();
		/**停止录音**/
		void recording();
		/**试听录音**/
		void tryPlay();
		/**录音取消**/
		void recordCancel();
		/**录音完成**/
		void recordOk();
		/**dialog按键监听**/
		void dialogOnkeyListener(DialogInterface dialog, int keyCode, KeyEvent event);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlRestartRecord:// 重新录音
			onBtnClickListener.restartRecord();
			break;
		case R.id.rlRecord:// 录音按钮 
			onBtnClickListener.recording();
			break;
		case R.id.rlTryPlay:// 试听按钮 
			onBtnClickListener.tryPlay();
			break;
		case R.id.tv_cancel:// 录音取消
			onBtnClickListener.recordCancel();
			dialog.dismiss();
			break;
		case R.id.tv_ok:// 录音完成
			onBtnClickListener.recordOk();
			dialog.dismiss();
			break;
		default:
			break;
		}
	}
	/**录音状态**/
	public View getRecord_tag(){
		return record_tag;
	}
	/**右动画图片**/
	public View getIvVoiceAnimLeft(){
		return iv_voice_anim_left;
	}
	/**左动画图片**/
	public View getIvVoiceAnimRight(){
		return iv_voice_anim_right;
	}
	/**录音时间文本框**/
	public View getRecordTime(){
		return record_time;
	}
	/**重录提示文字 **/
	public View getTvRestartRecord(){
		return tvRestartRecord;
	}
	/**试听文字**/
	public View getTvTryPlay(){
		return tvTryPlay;
	}
	/**重录按钮**/
	public View getIvRestartRecord(){
		return ivRestartRecord;
	}
	/**正在录音按钮**/
	public View getIvRecording(){
		return ivRecording;
	}
	/**试听按钮**/
	public View getIvTryPlay(){
		return ivTryPlay;
	}
	/**确认按钮**/
	public View getTvOk(){
		return tv_ok;
	}
	/**重新录音按钮区域**/
	public View getRlRecord(){
		return rlRecord;
	}
	/**录音按钮区域**/
	public View getRlRestartRecord(){
		return rlRestartRecord;
	}
	/**试听按钮区域**/
	public View getRlTryPlay(){
		return rlTryPlay;
	}
}
