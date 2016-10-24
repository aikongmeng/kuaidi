package com.kuaibao.skuaidi.manager;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;

/**
 * 手机管理类
 * 		用来获取手机的各种信息与状态
 * 		模式:single
 * @author xy
 *
 */
public class SkuaidiTelPhoneManager {
	
	private TelephonyManager mTelephonyManager;
	private static SkuaidiTelPhoneManager mInstanse;
	private Context context;
	
	private SkuaidiTelPhoneManager(Context context){
		this.context = context;
		mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static synchronized SkuaidiTelPhoneManager getInstanse(Context context){
		if(null == mInstanse){
			mInstanse = new SkuaidiTelPhoneManager(context);
		}
		return mInstanse;
	}
	
	/**
	 * 获取本机号码
	 * @return
	 */
	public String getLine1Number(){
		return mTelephonyManager.getLine1Number();
	}
	
	/**
	 * 获取手机型号
	 * @return
	 */
	public String getPhoneModel(){
		return Build.MODEL;
	}
	
	/**
	 * 获取手机品牌
	 * @return
	 */
	public String getPhoneBrand(){
		return Build.BRAND;
	}
	
	/**
	 * 获取手机IME号
	 * @return
	 */
	public String getPhoneIME(){
		return mTelephonyManager.getDeviceId();
	}
	
	/**
	 * 获取手机ID（可当做唯一生别码）
	 * 在设备第一次启动的时候随机生成并在设备的整个生命周期中不变。（如果重新进行出厂设置可能会改变）   
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getAndroidId(){
		return System.getString(context.getContentResolver(), System.ANDROID_ID);
	}
	
	/**
	 * 获取手机设备信息
	 * @return
	 */
	public String getPhoneDevice(){
		return Build.DEVICE;
	}
	
	/**
	 * 获取手机adroid版本
	 * @return
	 */
	public String getSDKVersion(){
		return Build.VERSION.SDK;
	}
	
	/**
	 * 获取手机android版本号
	 * @return
	 */
	public int getSDKVersionInt(){
		return Build.VERSION.SDK_INT;
	}
}
