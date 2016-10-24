package com.kuaibao.skuaidi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * 
 * @author 顾冬冬
 * toast 在屏幕中间弹出
 * 用于：
 * 1.发短信界面语音识别成功以后显示号码
 * 2.云呼界面语音识别成功以后显示号码
 * 3.手机号码输入界面语音识别成功以后显示号码
 *
 */
@SuppressLint("NewApi")
public class ToastHelper {
	public static final int LENGTH_LONG = 3500;
	public static final int LENGTH_SHORT = 2000;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private View toastView;
	private Context mContext;
	private Handler mHandler;
	private String mToastContent = "";
	private int duration = 0;
	private int animStyleId = android.R.style.Animation_Toast;

	private final Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			removeView();
		}
	};

	private ToastHelper(Context context) {
		Context ctx = context.getApplicationContext();
		if (ctx == null) {
			ctx = context;
		}
		this.mContext = ctx;
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		init();
	}

	private void init() {
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mWindowParams.alpha = 1.0f;
		mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.gravity = Gravity.CENTER;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mWindowParams.setTitle("ToastHelper");
		mWindowParams.packageName = mContext.getPackageName();
		mWindowParams.windowAnimations = animStyleId;
//		mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 5;
//		mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels;
	}

	private View getDefaultToastView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layout = inflater.inflate(R.layout.view_custom_toast, null);
		
		ImageView imageView = (ImageView) layout.findViewById(R.id.imageview);
		TextView textView = (TextView) layout.findViewById(R.id.textview);
		
		textView.setText(mToastContent);
		textView.setFocusable(false);
		textView.setClickable(false);
		textView.setFocusableInTouchMode(false);
		
		

//		TextView view = new TextView(mContext);
//		view.setText(mToastContent);
//		view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
//		view.setFocusable(false);
//		view.setClickable(false);
//		view.setFocusableInTouchMode(false);
//		view.setTextColor(android.graphics.Color.WHITE);
//		Drawable drawable = mContext.getResources().getDrawable(android.R.drawable.toast_frame);
//
//		if (Build.VERSION.SDK_INT < 16) {
//			view.setBackgroundDrawable(drawable);
//		} else {
//			view.setBackground(drawable);
//		}
		return layout;
	}

	public void show() {
		removeView();
		if (toastView == null) {
			toastView = getDefaultToastView();
		}
		mWindowParams.gravity = android.support.v4.view.GravityCompat.getAbsoluteGravity(Gravity.CENTER,
				android.support.v4.view.ViewCompat.getLayoutDirection(toastView));
		removeView();
		mWindowManager.addView(toastView, mWindowParams);
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mHandler.postDelayed(timerRunnable, duration);
	}

	public void removeView() {
		if (toastView != null && toastView.getParent() != null) {
			mWindowManager.removeView(toastView);
			mHandler.removeCallbacks(timerRunnable);
		}
	}

	/**
	 * @param context
	 * @param content
	 * @param duration
	 * @return
	 */
	public static ToastHelper makeText(Context context, String content, int duration) {
		ToastHelper helper = new ToastHelper(context);
		helper.setDuration(duration);
		helper.setContent(content);
		helper.setAnimation(R.style.popUpWindowEnterExit);
		return helper;
	}
	
	/**
	 * showToast:显示Toast
	 * @param context 上下文
	 * @param content 需要显示的消息内容
	 *
	 * @author 顾冬冬
	 */
	public static void showToast(Context context,String content){
		makeText(context, content, LENGTH_LONG).show();
	}

	/**
	 * @param context
	 * @param strId
	 * @param duration
	 * @return
	 */
	public static ToastHelper makeText(Context context, int strId, int duration) {
		ToastHelper helper = new ToastHelper(context);
		helper.setDuration(duration);
		helper.setContent(context.getString(strId));
		return helper;
	}

	public ToastHelper setContent(String content) {
		this.mToastContent = content;
		return this;
	}

	public ToastHelper setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	public ToastHelper setAnimation(int animStyleId) {
		this.animStyleId = animStyleId;
		mWindowParams.windowAnimations = this.animStyleId;
		return this;
	}

	/**
	 * custom view
	 * 
	 * @param view
	 */
	public ToastHelper setView(View view) {
		this.toastView = view;
		return this;
	}
}
