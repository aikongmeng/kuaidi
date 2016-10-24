package com.kuaibao.skuaidi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 开机
 * @author xy
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2 = new Intent(context, OnlineService.class);
		context.startService(intent2);
	}
}
