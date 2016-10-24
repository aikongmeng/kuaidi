package com.kuaibao.skuaidi.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.iData.ledcontroll.LedControll;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.manager.FingertipDeviceManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.lhzw.barcodehelper.BarcodeHelper;
import com.socks.library.KLog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import kaicom.android.app.KaicomJNI;
import kaicom.android.app.KaicomJNI.ScanCallBack;

/**
 * 定制设备扫描基类
 * @author xy
 *
 */
@SuppressLint("HandlerLeak")
public abstract class SpecialEquipmentScanActivity extends SkuaiDiBaseActivity
		implements ScanCallBack{

	// 扫描结果
	protected static final int SCAN_RESULT = 0xad;
	protected static final int START_SCAN = 0xdd;
	private KaicomJNI jni;
	private int LaserOntime;
	//private BarcodeScan mBarcodeScan;
	private HardwareControll barControll;
	//private PowerManager pm;
	public static LedControll ledC;
	private int flag = 0;
	private boolean isSingle = true;
	Vibrator vm;
	private Intent intentService = new Intent("com.hyipc.core.service.barcode.BarcodeService2D");
	public static final String KEY_ACTION = "KEY_ACTION";
	private BarcodeHelper mBarcode;
	private BarcodeScanCallback mScanCallback;
	private BarcodeErrorCallback mErrorCallback;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0||msg.what == SCAN_RESULT){
				final String lValue = (String) msg.obj;
				if(!TextUtils.isEmpty(lValue)){
					onScanResult(lValue);
					if(msg.what == SCAN_RESULT){
						startScan();
					}
				}else{
					playErrorSounds(0);
					if(msg.what == SCAN_RESULT){
						startScan();
					}
				}
			}else if(msg.what == 100){
				barControll.set_power(0x04);
				vm.vibrate(70);
				ledC.SetGreenLed(false);
			}else if(msg.what == 300){
				String key_action = (String) msg.obj;
				if (isSingle){
					if (key_action.equals("down") ){
						barControll.scan_start();
					}
					else if (key_action.equals("up")){
						barControll.scan_stop();
					}
				}
				else{
					if (key_action.equals("down")){
						if (flag%2 == 0){
							barControll.scan_start();
						}
						else{
							barControll.scan_stop();
						}
						flag ++;
					}
					
				}
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		// 屏幕保持唤醒状态
		initSound();
		if(KaicomJNI.isKaicom() == true){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			jni = KaicomJNI.getInstance(this);
			jni.setmScanCB(this);
			jni.SetScannerOn();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startScan();
		}
//		else if(Build.MODEL.equals("C40")){
//			mBarcodeScan = new BarcodeScan(this);
//			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		}
		else if(Build.MODEL.equals("Android TD")||Build.MODEL.equals("Android")){
			barControll = new HardwareControll(mHandler);
			vm = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
			ledC = new LedControll(this);
			acquireWakeLock();
		}else if(Build.MODEL.equals("HT101")){
			// open device
			new OpenDeviceTask().execute();
			KLog.i("kb","openDeviceTask");
			// init callback
			mScanCallback = new BarcodeScanCallback();
			mErrorCallback = new BarcodeErrorCallback();
		}
		/*
		 * LaserOntime =
		 * Integer.valueOf(FilePreference.getInstance(this).getLaserOnTime());
		 * if((LaserOntime <= 25) && (LaserOntime > 0)){
		 * jni.SetScannerTimerOut(Integer
		 * .valueOf(FilePreference.getInstance(this).getLaserOnTime())); }
		 */
	}
	/**
	 * Open Device AsyncTask
	 */
	public class OpenDeviceTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("");//SpecialEquipmentScanActivity.this,"初始化设备...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... parameters) {
			try {
				mBarcode = BarcodeHelper.getBarcodeHelper();
				KLog.i("kb","mBarcode is null? "+ (mBarcode==null?"true":"false"));
			} catch (Exception e) {
				// TODO handle exception
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result[]) {
			dismissProgressDialog();//SpecialEquipmentScanActivity.this);
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(Build.MODEL.equals("HT101")){
			if (keyCode == BarcodeHelper.KEYCODE_SCAN) {
				if (event.getRepeatCount() == 0) {
					mBarcode.stopScan();
					KLog.i("kb","mBarcode.stop scan");
				}
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onScanResults(String str) {
		// TODO Auto-generated method stub

		if (str == null) {
			//System.out.println("##Scan timeout!!");
			return;
		}
		Message msg = mHandler.obtainMessage();
		msg.what = SCAN_RESULT;
		msg.obj = str;
		mHandler.sendMessageDelayed(msg, 200);
		// handle.sendMessageDelayed(msg, 20);
		// handle.sendMessage(msg);
		/*
		 * try { Thread.sleep(50); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(jni!=null){
			jni.SetScannerStop();
			jni.SetScannerOff();
			jni=null;
		}
		mHandler = null;
		//mBarcodeScan = null;
		//pm = null;
		if(barControll!=null){
			barControll.singleCmd();
			barControll.Close();
			barControll=null;
		}
		// release barcode
		if(mBarcode!=null){
			mBarcode.release();
			mBarcode=null;
		}
		if(soundPool!=null){
			soundPool.release();
			soundPool=null;
		}
		if(am!=null){
			am=null;
		}
		releaseWakeLock() ;
		super.onDestroy();
		// jni.scannerOFF();
		// jni.setScannerType(ScannerJNI.SCANNER_CLOSE);

	}

	@Override
	public void onScanResults(String str, int type) {
		// TODO Auto-generated method stub
		//System.out.println("type:" + type + "  str:" + str);
		/*
		 * Message msg = new Message();
		 * 
		 * msg.what = type; msg.obj = str; handle.sendMessageDelayed(msg, 20);
		 */
	}

	/**
	 * 开始扫描
	 */
	public void startScan() {
		// jni.scannerON();
		// jni.SetScannerOn();
		if(jni!=null)
			jni.SetScannerStart();
	}

	public void scanOn() {
		if(jni!=null)
			jni.SetScannerOn();
	}

	public void scanOff() {
		if(jni!=null)
			jni.SetScannerOff();
	}

	/**
	 * 继续扫描
	 */
	/*
	 * public void contiuneScan() { jni.setScannerPar(ScannerJNI.CONTINUE_SCAN);
	 * }
	 */

	/**
	 * 判断是否正在扫描
	 * 
	 * @return
	 */
	public boolean isTrigger() {
		if(jni==null)
			return false;
		else
			return jni.GetScannerIsScanning();
		// return isTrigger;
	}

	/**
	 * 停止扫描
	 */
	public void stopScan() {
		if(jni!=null)
			jni.SetScannerStop();
	}

	/**
	 * 重置扫描
	 */
	/*
	 * public void trigerScan() { jni.scannerOFF(); jni.scannerON(); }
	 */
	/**
	 * 销毁
	 */
	/*
	 * @Override protected void onDestroy() { jni.scannerOFF();
	 * jni.setScannerType(ScannerJNI.SCANNER_CLOSE); super.onDestroy(); }
	 */
	/*
	 * @Override public void onScanResults(String str) { isTrigger = false; }
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(KaicomJNI.isKaicom()==true){
			try {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getRepeatCount() == 0) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BUTTON_L1:
					case KeyEvent.KEYCODE_BUTTON_R1:
					case KeyEvent.KEYCODE_BUTTON_MODE:
						startScan();
						break;
					case KeyEvent.KEYCODE_DEL:
						stopScan();
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
	
			}
		}
//		else if(Build.MODEL.equals("C40")){
//			switch (keyCode) {
//
//			case KeyEvent.KEYCODE_MUTE:
//			case KeyEvent.KEYCODE_CTRL_LEFT:
//			case KeyEvent.KEYCODE_VOLUME_UP:
//			case KeyEvent.KEYCODE_VOLUME_DOWN:
//				// scanning
//				if (event.getRepeatCount() == 0) {
//					mBarcodeScan.scanning();
//				}
//				return true;
//			}

//		}
		else if(Build.MODEL.equals("HT101")){
			if (keyCode == BarcodeHelper.KEYCODE_SCAN) {
				if (event.getRepeatCount() == 0) {
					if(mBarcode!=null){
						// set callback
						mBarcode.setScanCallback(mScanCallback);
						mBarcode.setErrorCallback(mErrorCallback);
						mBarcode.startScan();
						KLog.i("kb","mBarcode.startScan");
					}
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(FingertipDeviceManager.isSpecialEquipment()&&KaicomJNI.isKaicom() == false && !Build.MODEL.equals("HT101")){
			
			if(Build.MODEL.equals("S5")){
				intentService.putExtra(KEY_ACTION, "INIT");
				this.startService(intentService);
			}
			
			FingertipDeviceManager.registerReceiver(this, mHandler);
//			if(mBarcodeScan!=null){
//				mBarcodeScan.open();
//			}
			if(barControll!=null){
				barControll.Open();	
				isSingle = false;
				barControll.continueCmd();
				barControll.set_power(0x03);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				barControll.set_power(0x04);
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(FingertipDeviceManager.isSpecialEquipment()&&KaicomJNI.isKaicom() == false && !Build.MODEL.equals("HT101")){
			if(Build.MODEL.equals("S5")){
				intentService.putExtra(KEY_ACTION, "POWER=OFF");
				this.startService(intentService);
			}
			FingertipDeviceManager.unregisterReceiver(this);
		}
	}
	
	protected void onScanResult(String code){
		
	}
	
	@Override
	public void finish() {
		super.finish();
//		if(mBarcodeScan!=null&&pm!=null){
//			mBarcodeScan.scannerContinusousModeShutdown();
//			pm.setScanningGunPowerOnroOff(0);
//		}
	}
	@Override
	protected void onStart() {
		super.onStart();
//		if(mBarcodeScan!=null&&pm!=null){
//			mBarcodeScan.setScannerContinuousMode();
//			pm.setScanningGunPowerOnroOff(1);
//		}
	}
	@Override
	protected void onStop() {
		super.onStop();
//		if(mBarcodeScan!=null){
//			mBarcodeScan.close();
//		}
	}
	
	
	private WakeLock wakeLock = null;  
	private void acquireWakeLock()  
    {  
        if (null == wakeLock)  
        {  
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);  
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");  
            if (null != wakeLock)  
            {  
            	//Log.i("123", "get lock");
                wakeLock.acquire();  
            }  
        }  
    }
	private void releaseWakeLock()  
    {  
        if (null != wakeLock)  
        {  
        	//Log.i("123", "release lock");
            wakeLock.release();  
            wakeLock = null;  
        }  
    }	
	
	public void appendWriteData(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// =声音==================================
		private SoundPool soundPool;
		private HashMap<Integer, Integer> spMap;
		private AudioManager am = null;
		private float volumnRatio1 = 0;

		// 播放声音
		public void playSounds(int number) {
			int sound = 1; // 默认正确声音
			soundPool.play(spMap.get(sound), volumnRatio1, volumnRatio1, 1, number, 1);
		}

		// 播放错误声音
		public void playErrorSounds(int number) {
			int sound = 2; // 错误声音文件
			soundPool.play(spMap.get(sound), volumnRatio1, volumnRatio1, 1, number, 1);
		}

		// -- 初始化 --
		private void initSound() {
			// TODO Auto-generated method stub
			soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
			spMap = new HashMap<Integer, Integer>();
			spMap.put(1, soundPool.load(this, R.raw.scan, 1));
			spMap.put(2, soundPool.load(this, R.raw.wrong, 1));
			try {
				am = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
				float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
				volumnRatio1 = audioCurrentVolumn / audioMaxVolumn;
			} catch (Exception e) {
				// GlobalFunc.showErrorScreen(this, "初始化声音时发生错误:" + e.getMessage(),
				// null);

				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String errorLog = sw.toString();

				// Error//KLog.writeErrorLog(TAG+":"+errorLog);
			}
		}

	public class BarcodeScanCallback implements BarcodeHelper.ScanCallback {
		public void onScanResult(byte[] data) {
			KLog.i("kb","onScanResult：---->");
			if (data != null && !data.equals("")) {
				try {
					String result = new String(data, "GB2312");
					KLog.i("kb","HT101扫描码:--->"+result);
					SpecialEquipmentScanActivity.this.onScanResult(result);
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public class BarcodeErrorCallback implements BarcodeHelper.ErrorCallback {
		public void onError(int error) {
			switch (error) {
				case BarcodeHelper.BARCODE_ERROR_SCAN_TIMEOUT:
					UtilToolkit.showToast("扫描超时");
					break;
			}
		}
	}

}
