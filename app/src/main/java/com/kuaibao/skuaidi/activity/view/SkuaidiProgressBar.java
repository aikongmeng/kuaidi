package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressLint("SimpleDateFormat")
public class SkuaidiProgressBar  {
	
	private Context context;
	private ProgressBar mProgressBar;
	private Progress mProgress;
	private TextView title,phone,type,date,duration,percentage,uploadedNum;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
	public SkuaidiProgressBar(Context context){
		this.context = context;
		mProgress = new Progress(context); 
	}
	
	private class Progress extends PopupWindow{
		
		public Progress(Context context){
			super(context);
			init();
		}
		private void init(){
			View layout = LayoutInflater.from(context).inflate(R.layout.progress_layout, null);
			mProgressBar = (ProgressBar) layout.findViewById(R.id.view_progressbar);
			title = (TextView) layout.findViewById(R.id.tv_title);
			phone = (TextView) layout.findViewById(R.id.tv_phone);
			type = (TextView) layout.findViewById(R.id.tv_type);
			date = (TextView) layout.findViewById(R.id.tv_date);
			duration = (TextView) layout.findViewById(R.id.tv_duration);
			percentage = (TextView) layout.findViewById(R.id.tv_progress_percentage);
			uploadedNum = (TextView) layout.findViewById(R.id.tv_upload_number);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setContentView(layout);
			setBackgroundDrawable(new ColorDrawable());
		}
	}
	
	public void show(View parent){
		mProgress.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}
	
	public void dismiss(){
		mProgress.dismiss();
	}
	
	public int getMax(){
		return mProgressBar.getMax();
	}
	
	public void setMax(int max){
		mProgressBar.setMax(max);
	}
	
	public int getUploadedNum(){
		return mProgressBar.getProgress();
	}
	
	public double getUploadedPercentage(){
		double uploaded = mProgressBar.getProgress();
		double max = mProgressBar.getMax();
		return uploaded/max;
	}
	
	public void setPhone(String phoneNum){
		phone.setText("号码："+phoneNum);
	}
	
	public void setTitle(String title){
		this.title.setText(title);
	}
	
	public void setType(int type){
		if(type == 0){
			this.type.setText("类型：呼入");
		}else if(type == 1){
			this.type.setText("类型：拨出");
		}
	}
	
	public void setDate(long date){
		this.date.setText("时间："+format.format(new Date(date)));
	}
	
	public void setDuration(long duration){
		this.duration.setText("时长："+format2.format(duration - 28800000));
	}
	
	public int getProgress(){
		return mProgressBar.getProgress();
	}
	
	public boolean isShowing(){
		return mProgress.isShowing();
	}
	
	public void setProgress(int progress){
		mProgressBar.setProgress(progress);
		String s = getUploadedPercentage()*100+"";
		String s1 = "";
		//System.out.println("s :"+s);
		if(s.indexOf(".")!=-1){
			s1 = s.split("\\u002E")[1];
			s = s.split("\\u002E")[0];
			
			if(s1.length()>2){
				s1 = s1.substring(0, 2);
			}
		}
		
		percentage.setText(s+"."+s1+"%");
		uploadedNum.setText(getProgress()+"/"+getMax());
	}
	
	public void setSecondaryProgress(int secondaryProgress){
		mProgressBar.setSecondaryProgress(secondaryProgress);
	}
}
