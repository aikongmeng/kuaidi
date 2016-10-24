package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 用于在底部作为通知显示的pop
 * @author 顾冬冬
 *
 */
public class NotifyBroadCast extends PopupWindow {

	private final int TIMER_TASK = 0x1001;
	
	View view;
	int w = 0;
	int h = 0;
	int width = 0;
	int height = 0;
	
	public Timer mTimer = null;
	public MyTimeTask task = null;
	public Context context;
	private int stayTime = 0;// 停留时间
	private int time = 0;// 计时器
	private View viewShowDropDown = null;
	private TextView tvBroadCast = null;
	
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
	 * @param context
	 * @param stayTime pop停留时间
	 * @param v 用来将pop显示在此view下
	 * @param notifyTxt 通知内容
	 * @param onClickListener
	 */
	public NotifyBroadCast(Context context,int stayTime,View v,String notifyTxt,OnClickListener onClickListener){
		super(context);
		this.context = context;
		this.stayTime = stayTime;
		this.viewShowDropDown = v;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.notify_broadcast, null);
		tvBroadCast = (TextView) view.findViewById(R.id.tvBroadCast);
		tvBroadCast.setText(notifyTxt);
		
		tvBroadCast.setOnClickListener(onClickListener);
		
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
		startAnimation();
	}
	
	public void startAnimation(){
		
		TranslateAnimation traAnimation = new TranslateAnimation(0, 0, height, 0);
		traAnimation.setDuration(500);
		view.startAnimation(traAnimation);
		
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
		TranslateAnimation traAnimation = new TranslateAnimation(0, 0, 0, height);
		traAnimation.setDuration(500);
		view.startAnimation(traAnimation);
		traAnimation.setAnimationListener(new AnimationListener() {
			
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
	
	public void show(){
		if(viewShowDropDown!=null && viewShowDropDown.getVisibility()==View.VISIBLE){
			viewShowDropDown.postDelayed(new Runnable() {

				@Override
				public void run() {
					Activity activity = (Activity) context;
					if (!activity.isFinishing()) {
						showAtLocation(viewShowDropDown, Gravity.BOTTOM, 0, 20);
					}
				}
			},500);
		}
	}
}
