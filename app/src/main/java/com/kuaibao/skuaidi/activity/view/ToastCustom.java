package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName: ToastCustom 
 * @Description: 用于在屏幕中央显示类似TOAST样式的提示-比正常TOAST要大
 * @author 顾冬冬
 * @date 2015-12-3 下午2:12:16
 */
public class ToastCustom extends PopupWindow {
private final int TIMER_TASK = 0x1001;
	
	View view;
	int w = 0;
	int h = 0;
	int width = 0;
	int height = 0;
	
	private Timer mTimer = null;
	private MyTimeTask task = null;
	private int stayTime = 0;// 停留时间
	private int time = 0;// 计时器
	private View viewShowDropDown = null;
	private TextView tvShowText = null;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIMER_TASK:
				time++;
				if(time == stayTime){
					if(null != mTimer){
						mTimer.purge();
						mTimer.cancel();
						mTimer = null;
					}
					if(null != task){
						task.cancel();
						task = null;
					}
					endAnimation();
				}
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 * @param context TODO
	 * @param context TODO
	 * @param context
	 * @param stayTime pop停留时间
	 * @param v 用来将pop显示在此view下
	 * @param notifyTxt 通知内容
	 * @param onClickListener
	 */
	public ToastCustom(Context context, int stayTime, View v){
		super(context);
		this.stayTime = stayTime;
		this.viewShowDropDown = v;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.toast_custom_pop, null);
		tvShowText = (TextView) view.findViewById(R.id.tvShowText);
		
		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(false);
		ColorDrawable cd = new ColorDrawable(00000000);
		this.setBackgroundDrawable(cd);
		w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		width = view.getMeasuredWidth();
		height = view.getMeasuredHeight();
		
	}
	
	public void startAnimation(){
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		view.startAnimation(alphaAnimation);
		
		mTimer = new Timer();
		task = new MyTimeTask();
		mTimer.schedule(task, 0,1000);
	}
	
	public class MyTimeTask extends TimerTask{
		
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = TIMER_TASK;
			mHandler.sendMessage(msg);
		}
	}
	
	@SuppressLint("NewApi")
	public void endAnimation(){
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(1000);
		view.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			// 动画结束
			@Override
			public void onAnimationEnd(Animation animation) {
				dismiss();
			}
		});
	}
	
	public void show(String show_text){
		tvShowText.setText(show_text);
		time = 0;
		startAnimation();
//		showAsDropDown(viewShowDropDown, 0, 20);
		showAtLocation(viewShowDropDown, Gravity.CENTER, 0, 0);
	}
}
