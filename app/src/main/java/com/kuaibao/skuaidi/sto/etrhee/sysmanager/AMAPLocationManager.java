/*package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

*//**
 * 高德 API 定位
 * 需要高德最新2.X 的SDK,新版本与老版本并不兼容
 * 
 * @author wangqiang
 * 
 *//*
public class AMAPLocationManager implements AMapLocationListener {
	private Handler handler;

	// 单次定位绑定的单号
	private String expressNumber;
	private AMapLocationClient mlocationClient;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public AMAPLocationManager(Handler handler) {
		this.handler = handler;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}

	LocationManager locationManager;

	public AMapLocation getLocation(Activity activity) {
		// 初始化定位，
		mlocationClient = new AMapLocationClient(SKuaidiApplication.getContext());
		// 初始化定位参数
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
		mLocationOption.setOnceLocation(true);
		// 设置定位模式为低功耗定位
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置定位回调监听
		mlocationClient.setLocationListener(this);
		// 设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		mlocationClient.startLocation();
		return mlocationClient.getLastKnownLocation();
	}

	*//**
	 * 打开GPS
	 * 
	 * @param context
	 *//*
	public final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public void removeUpdates() {
		if (mlocationClient != null) {
			mlocationClient.onDestroy();
		}

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		mlocationClient.onDestroy();
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				// 定位成功回调信息，设置相关消息
				// 经纬度
				amapLocation.getLatitude();
				amapLocation.getLongitude();
				// 精度
				String.valueOf(amapLocation.getAccuracy());
				// 定位时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date(amapLocation.getTime());
				df.format(date);
				// 地址，如果option中设置isNeedAddress为false，则没有此结果
				amapLocation.getAddress();
				// 国家
				amapLocation.getCountry();
				// 省
				if (amapLocation.getProvince() == null) {
				} else {
					amapLocation.getProvince();
				}
				// 城市
				amapLocation.getCity();
				// 区
				amapLocation.getDistrict();
				// 街道
				amapLocation.getRoad();
				// 城市编码
				amapLocation.getCityCode();

				amapLocation.getAdCode();
				// 定位类型
				amapLocation.getLocationType();
				Message msg = handler.obtainMessage();
				msg.what = TWO_MINUTES;
				Bundle bundle = new Bundle();
				bundle.putString("expressNumber", expressNumber);
				msg.setData(bundle);
				msg.obj = amapLocation;
				handler.sendMessage(msg);
			} else {
				//Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo() + ", locationDetail:" + amapLocation.getLocationDetail());
			}
		}

	}
}*/