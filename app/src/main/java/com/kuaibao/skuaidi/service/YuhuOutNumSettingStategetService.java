package com.kuaibao.skuaidi.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class YuhuOutNumSettingStategetService extends Service {
	
	private MyBinder mBinder;
	private Timer mTimer;
	private stateGetTimerTask mTimerTask;
	private static final long DELAY = 5000;
	private static final long PERIOD = 5000;
	public static final long MAX_RUNTIME = 1000*60*30;
	private Callback mCallback;
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mBinder = new MyBinder();
		mTimer = new Timer();
		mTimerTask = new stateGetTimerTask();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		cancelStateGet();
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCallback!=null){
			mCallback.onServiceOver();
		}
		destroy();
	}
	
	public void startStateGet(){
		mTimer.schedule(mTimerTask, DELAY, PERIOD);
	}
	
	public void cancelStateGet(){
		if(mTimer!=null){
			mTimer.cancel();
			mTimerTask.cancel();
		}
		if (mCallback != null){
			mCallback.onServiceOver();
		}
		runCount = 0;
	}
	
	private void destroy(){
		mTimer = null;
		mTimerTask = null;
		mBinder = null;
		mCallback = null;
		runCount = 0;
	}
	
	public class MyBinder extends Binder{
		
		public YuhuOutNumSettingStategetService getService(){
			return YuhuOutNumSettingStategetService.this;
		}
	} 
	
	public void setCallback(Callback callback){
		mCallback = callback;
	}
	
	
	public interface Callback{
		void onServiceDoing();
		void onServiceOver();
	}
	int runCount;
	private class stateGetTimerTask extends TimerTask{

		@Override
		public void run() {
			runCount = runCount+1;
			if(runCount*5000>=MAX_RUNTIME){
				cancelStateGet();
				return;
			}
			if(mCallback!=null){
				mCallback.onServiceDoing();
			}
		}
		
	}
	
}
