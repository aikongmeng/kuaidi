package com.kuaibao.skuaidi.activity.view;

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

public class GetSMSProgressBar {
	
	private Context mContext = null;
	private Progress mProgress = null;
	private ProgressBar mProgressbar = null;// 进度条
	private TextView proportion,uploadedNum;// 显示进度数据
	
	public GetSMSProgressBar(Context context){
		this.mContext =  context;
		mProgress = new Progress(mContext);
	}
	
	
	private class Progress extends PopupWindow{
		
		public Progress(Context context){
			super(context);
			init();
		}
		private void init(){
			View layout = LayoutInflater.from(mContext).inflate(R.layout.progress_layout2, null);
			mProgressbar = (ProgressBar) layout.findViewById(R.id.view_progressbar);
			proportion = (TextView) layout.findViewById(R.id.proportion);
			uploadedNum = (TextView) layout.findViewById(R.id.uploadedNum);
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
		return mProgressbar.getMax();
	}
	
	public void setMax(int max){
		mProgressbar.setMax(max);
	}
	
	public int getUploadedNum(){
		return mProgressbar.getProgress();
	}
	
	public double getUploadedPercentage(){
		double uploaded = mProgressbar.getProgress();
		double max = mProgressbar.getMax();
		return uploaded/max;
	}
	public int getProgress(){
		return mProgressbar.getProgress();
	}
	
	public boolean isShowing(){
		return mProgress.isShowing();
	}
	
	public void setProgress(int progress){
		mProgressbar.setProgress(progress);
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
		
		proportion.setText(s+"."+s1+"%");
		uploadedNum.setText(getProgress()+"/"+getMax());
	}
	
}
