package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author ln 
 * 版本相关的操作
 */
public class VersionUtil {

	/**
	 * @param context
	 * @return 当前版本号
	 */
	public static String getCurrentVersion(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo;
			packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e1) {
			return "版本号未知";
		}
	}

	/**
	 * @param context
	 * @param currentVersion 当前软件版本
	 * @param newVersion 已有新版本
	 * @return 是否需要更新
	 * 判定是否有新版本
	 */
	public static boolean isNeedUpdate(Context context,String currentVersion,String newVersion) {
		return !(newVersion.equals(currentVersion) || newVersion.equals(""));

	}
	

}
