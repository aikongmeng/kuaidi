package com.kuaibao.skuaidi.camara;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.kuaibao.skuaidi.application.SKuaidiApplication;


/**
 * 
 * 像素转换器
 * @author xy
 *
 */
public class DisplayUtil {
	private static final String TAG = "skuadi";
	/**
	 * dip转px
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue){            
		final float scale = SKuaidiApplication.getContext().getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}     
	
	/**
	 * px转dip
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue){                
		final float scale = SKuaidiApplication.getContext().getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	
	/**
	 * 获取屏幕宽度和高度，单位为px
	 * @return
	 */
	public static Point getScreenMetrics(){
		DisplayMetrics dm =SKuaidiApplication.getContext().getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		//Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);
		
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	/**
	 * 获取屏幕长宽比
	 * @return
	 */
	public static float getScreenRate(){
		Point P = getScreenMetrics();
		float H = P.y;
		float W = P.x;
		return (H/W);
	}
}
