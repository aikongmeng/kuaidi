package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
/**
 * anroid.location  API 定位
 * @author wangqiang
 *
 */
public class MyLocationManager {
	private Handler handler;

	private Location currentBestLocation = null;
	//单次定位绑定的单号
	private String expressNumber;


	public MyLocationManager(Handler handler) {
		this.handler = handler;
	}

	public void setExpressNumber(String expressNumber){
		this.expressNumber = expressNumber;
	}
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(this);
			if (isBetterLocation(location, currentBestLocation)) {
				currentBestLocation = location;
				Message msg = handler.obtainMessage();
				msg.what = TWO_MINUTES;
				Bundle bundle = new Bundle();
				bundle.putString("expressNumber", expressNumber);
				msg.setData(bundle);
				msg.obj = location;
				handler.sendMessage(msg);
			}
		}
	};

	LocationManager locationManager;

	public Location getLocation(Activity activity) {

		// 获取所有可用的位置提供器

		Criteria criteria = new Criteria();
		// 查询精度：高

		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 是否查询海拨：否
		criteria.setAltitudeRequired(false);
		// 是否查询方位角 : 否
		criteria.setBearingRequired(false);
		// 是否允许付费：是
		criteria.setCostAllowed(false);
		// 电量要求：低
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		// 获取地理位置管理器
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		// 获取所有可用的位置提供器
		String provider = locationManager.getBestProvider(criteria, true);

		String locationProvider = null;
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetworkEnabled) {
			openGPS(activity);
		}
		if (!TextUtils.isEmpty(provider)) {
			if (provider.contains(LocationManager.NETWORK_PROVIDER)) {
				// 如果是Network
				locationProvider = LocationManager.NETWORK_PROVIDER;
			} else if (provider.contains(LocationManager.GPS_PROVIDER)) {
				// 如果是GPS
				locationProvider = LocationManager.GPS_PROVIDER;
			}
			if (!TextUtils.isEmpty(locationProvider)) {
				currentBestLocation = locationManager.getLastKnownLocation(locationProvider);
			}
		}
		if (!TextUtils.isEmpty(locationProvider))
			locationManager.requestLocationUpdates(locationProvider, TWO_MINUTES, 0, locationListener);
		return currentBestLocation;
	}

	/**
	 * 打开GPS
	 * 
	 * @param context
	 */
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

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		boolean isEquals = location.getLatitude() == currentBestLocation.getLatitude()
				&& location.getLongitude() == currentBestLocation.getLongitude();
		if (isEquals) {
			return false;

		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location,
		// use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public void removeUpdates() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}

	}
}