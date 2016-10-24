package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.kuaibao.skuaidi.R;
//旋转图片
public class RotateLoadView extends View{
	
	public RotateLoadView(Context context) {
		super(context);
		show();
	}
	
	public RotateLoadView(Context context,AttributeSet attrs){
	       super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_findexpress_load)).getBitmap(), 0, 0, null);
	}
	
	public void show(){
		//图片大小
		////Log.i("iii", getResources().getDrawable(R.drawable.icon_findexpress_load).getMinimumWidth()/getResources().getDisplayMetrics().density+"");
		Animation animation = new RotateAnimation(0f, 359f, getResources().getDrawable(R.drawable.icon_findexpress_load).getMinimumWidth()/2.0f, getResources().getDrawable(R.drawable.icon_findexpress_load).getMinimumHeight()/2.0f);
		animation.setDuration(1000);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());
		startAnimation(animation);
		this.invalidate();
	}
}
