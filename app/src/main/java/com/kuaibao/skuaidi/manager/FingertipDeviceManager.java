package com.kuaibao.skuaidi.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kaicom.android.app.KaicomJNI;

public class FingertipDeviceManager {
	
	public static final String FINGERTIP_DEVICE_MODEL = "ZD5100";
	public static final String FINGERTIP_DEVICE_KERNEL_VERSION = "3.4.0-perf-g5b50675";
	private static FingertipDeviceScanReceiver mReceiver;
	private static IntentFilter mFilter;

	public static String getModel() {
		return model;
	}

	private static final String model = Build.MODEL;
	
	
	private static class FingertipDeviceScanReceiver extends BroadcastReceiver{

		private Handler mHandler;
		public FingertipDeviceScanReceiver(Handler handler){
			mHandler = handler;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {

			final String lAction = intent.getAction();

			if ("com.sim.action.SIMSCAN".equals(lAction)) {
				final String lValue = intent.getStringExtra("value");
				////Log.i(TAG, "scan result -- " + lValue);
				final Message lMsg = Message.obtain(mHandler, 0, lValue);
				lMsg.sendToTarget();
			} else if ("com.yto.action.GET_SCANDATA".equals(lAction)) {
				byte[] bs = intent.getByteArrayExtra("data");
				String lValue = new String(bs);
				final Message lMsg = Message.obtain(mHandler, 0, lValue);
				lMsg.sendToTarget();
			} 
//			else if(BarcodeScan.ACTION_BAR_SCAN.equals(lAction)){
//				final String lValue = intent
//						.getStringExtra(BarcodeScan.EXTRA_SCAN_DATA);
//				final Message m = Message.obtain(mHandler, 0, lValue);
//				m.sendToTarget();
//			} 
			else if(lAction.equals("com.idatachina.SCANKEYEVENT")){
				String key_action = intent.getStringExtra("action");
				String key_code = intent.getStringExtra("code");
				if (key_code.compareTo("139") == 0|| key_code.compareTo("140") == 0 
					|| key_code.compareTo("141") == 0 || key_code.compareTo("KEYCODE_F9") == 0 
					|| key_code.compareTo("KEYCODE_F10") == 0 || key_code.compareTo("KEYCODE_F11") == 0){
					final Message m = Message.obtain(mHandler, 300, key_action);
					m.sendToTarget();
				}
			}
			else if(lAction.equals("action_barcode_broadcast")){
				String strBarcode = intent.getExtras().getString("key_barcode_string");
				final Message m = Message.obtain(mHandler, 0, strBarcode);
				m.sendToTarget();
			}
			else if(lAction.equals("com.yto.action.GET_SCANDATA")){
				byte[] bs = intent.getByteArrayExtra("data");
				String lValue = new String(bs);
				final Message lMsg = Message.obtain(mHandler, 0, lValue);
				lMsg.sendToTarget();
			}
			// sendOrderedBroadcast(intent, receiverPermission)
			this.abortBroadcast();
		}
		
	}
	
	
	public static void registerReceiver(Context context,Handler handler){
		mReceiver = new FingertipDeviceScanReceiver(handler);
		mFilter = new IntentFilter();
		mFilter.addAction("com.sim.action.SIMSCAN");
		mFilter.addAction("com.yto.action.GET_SCANDATA");
		//mFilter.addAction(BarcodeScan.ACTION_BAR_SCAN);
		mFilter.addAction("com.idatachina.SCANKEYEVENT");
		mFilter.addAction("action_barcode_broadcast");
		mFilter.addAction("com.yto.action.GET_SCANDATA");
		mFilter.setPriority(500);
		context.registerReceiver(mReceiver, mFilter);
	}
	
	
	public static void unregisterReceiver(Context context){
		context.unregisterReceiver(mReceiver);
		mReceiver = null;
		mFilter = null;
	}
	
	/**
	 * 获取系统内核版本
	 * @return
	 */
	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		//System.out.println("kernelVersion : "+kernelVersion);
		return kernelVersion;
	}
	
	
	public static boolean isSpecialEquipment(){
		KLog.i("kb","build Model:--->"+model);
		return 	model.equals("HT101") ||
				model.equals(FINGERTIP_DEVICE_MODEL) ||
				model.equals("Android TD") ||
				model.equals("Android") ||
				model.equals("S5") ||
				model.equals("S7") ||
				KaicomJNI.isKaicom() == true;
	}
	
}
