package com.kuaibao.skuaidi.zxing.ui;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CollectionAddExpressNoActivity;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.json.entry.MakeCollectionDescParameter;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.zxing.camera.CameraManager;
import com.kuaibao.skuaidi.zxing.decoding.CaptureActivityHandler;
import com.kuaibao.skuaidi.zxing.decoding.InactivityTimer;
import com.kuaibao.skuaidi.zxing.decoding.RGBLuminanceSource;
import com.kuaibao.skuaidi.zxing.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * @ClassName: PayScanQrcodeActivity
 * @Description: 二维码扫描界面
 * @author 顾冬冬
 * @date 2015-9-21 下午3:42:49
 * 
 */
public class PayScanQrcodeActivity extends SkuaiDiBaseActivity implements Callback, OnClickListener {
	// 获取收款结果，总次数
	private static final int REQUEST_TIEMS = 60 * 20 * 2;
	private static final String MICRO_PAY = "MicroPay";
	// 扫码支付【扫对方】，用于判断是否要开通微信支付的参数：VERSION="V1"
	private static final String VERSION = "v1";
	// 扫一扫收款，上传二维码
	private static final String PAYMENT = "payment.order";
	/** 获取收款结果接口 */
	private static final String PAYMENT_GET_ORDER_STATUS = "payment.getOrderStatus";
	// 扫一扫收款
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;

	boolean isopen = false;// 闪光开关标记
	private ImageView mButtonBack = null;// 返回按钮
	private TextView tv_flashlight_top = null;// 闪光灯开关
	private TextView tv_desc;// 显示消息
	private String money;
	private int getResultTimes = 0;
	private String create_time;
	private String order_number;
	// 获取支付结果的计算器
	private Timer timer;

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if (getResultTimes >= REQUEST_TIEMS) {
				this.cancel();
			} else {
				getPaymentStatus();
				getResultTimes++;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.pay_scan_qrcode_activity);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);

		// 相机初始化
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		mButtonBack = (ImageView) findViewById(R.id.iv_title_back);
		tv_flashlight_top = (TextView) findViewById(R.id.tv_flashlight_top);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		mButtonBack.setOnClickListener(this);
		tv_flashlight_top.setOnClickListener(this);

		// View contentView =
		// LayoutInflater.from(this).inflate(R.layout.pop_item, null);
		// final PopupWindow pop = new PopupWindow(contentView,
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// pop.setOutsideTouchable(true);
		/*
		 * ImageButton more_key = (ImageButton) findViewById(R.id.more_key);
		 * 
		 * 调用本地图库中保存的二维码
		 * 
		 * 
		 * more_key.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // pop.showAsDropDown(v);
		 * Intent mIntent1 = new Intent(); if(Build.VERSION.SDK_INT < 19){
		 * mIntent1.setAction(Intent.ACTION_GET_CONTENT);} else{
		 * 
		 * 针对于Android 4.4 及以上使用。
		 * 
		 * // mIntent1.setAction(Intent.ACTION_OPEN_DOCUMENT); }
		 * mIntent1.addCategory(Intent.CATEGORY_OPENABLE);
		 * mIntent1.setType("image/*");
		 * PayScanQrcodeActivity.this.startActivityForResult(mIntent1,1); } });
		 */
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		money = getIntent().getStringExtra("money");
		if (MakeCollectionsActivity.activitys != null) {// 订单收款
			MakeCollectionsActivity.activitys.add(this);
		}
		order_number = getIntent().getStringExtra("orderID");
		getPayDesc();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				if (cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();

				showProgressDialog("");//PayScanQrcodeActivity.this,"正在扫描...");
				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result.getText();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "Scan failed!";
							mHandler.sendMessage(m);
						}
					}
				}).start();

				break;

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissProgressDialog();//PayScanQrcodeActivity.this);
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				httpScanQrcode(money, (String) msg.obj);
				break;
			case PARSE_BARCODE_FAIL:
				Toast.makeText(PayScanQrcodeActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;

			}
		}

	};

	/*
	 * 
	 * 解析手机自带的二维码图片
	 */

	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		task.cancel();
		if (MakeCollectionsActivity.activitys != null)
			MakeCollectionsActivity.activitys.clear();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		// playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(PayScanQrcodeActivity.this, "逗比，你扫的二维码坏了", Toast.LENGTH_SHORT).show();
		} else {
			if (TextUtils.isEmpty(resultString)) {
				Toast.makeText(PayScanQrcodeActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
				return;
			}
			httpScanQrcode(money, resultString);
		}
	}

	/**
	 * @Title: httpScanQrcode
	 * @Description: 如果pay_method为：ScanCode则为扫描二维码收款调用 接口，否则为刷卡支付调用接口
	 * @param @param auth_code扫描授权码
	 * @return void
	 */
	private void httpScanQrcode(String money, String auth_code) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", PAYMENT);
			data.put("pay_method", MICRO_PAY);
			if (TextUtils.isEmpty(order_number)) {
				data.put("pay_type", "d");
			} else {
				data.put("order_number", order_number);
			}
			data.put("auth_code", auth_code);
			data.put("money", money);
			data.put("version", VERSION);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	// private void playBeepSoundAndVibrate() {
	// if (playBeep && mediaPlayer != null) {
	// mediaPlayer.start();
	// }
	// if (vibrate) {
	// Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	// vibrator.vibrate(VIBRATE_DURATION);
	// }
	// }

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	private String photo_path;
	private Bitmap scanBitmap;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.tv_flashlight_top:
			CameraManager.get().flash();
			if (isopen) {// 设置为暗
				Drawable drawable = getResources().getDrawable(R.drawable.icon_sign_flashlight_closed);
				// 这一步必须要做，否则不会显示。
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tv_flashlight_top.setCompoundDrawables(drawable, null, null, null);
				tv_flashlight_top.setText("打开闪光灯");
				isopen = false;
			} else {// 设置为亮
				Drawable drawable = getResources().getDrawable(R.drawable.icon_sign_flashlight_opened);
				// 这一步必须要做，否则不会显示。
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tv_flashlight_top.setCompoundDrawables(drawable, null, null, null);
				tv_flashlight_top.setText("关闭闪光灯");
				isopen = true;
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 获取收款结果
	 */
	private void getPaymentStatus() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", PAYMENT_GET_ORDER_STATUS);
			data.put("order_number", order_number);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**调用信息仅当刷卡支付时使用，返回微信支付的一些消息**/
	private void getPayDesc(){
		JSONObject data = new JSONObject();
		try{
			data.put("sname","payment/getPayDesc");
		}catch(Exception e){
			e.printStackTrace();
		}
		httpInterfaceRequest(data,false,HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (PAYMENT.equals(sname)) {
			create_time = result.optString("create_time");
			if (TextUtils.isEmpty(order_number)) {
				order_number = result.optString("order_number");
			}
			timer = new Timer(true);
			timer.schedule(task, 0, 3 * 1000);

		} else if (PAYMENT_GET_ORDER_STATUS.equals(sname) && getResultTimes != REQUEST_TIEMS) {
			if ("SUCCESS".equals(result.optString("status"))) {
				getResultTimes = REQUEST_TIEMS;
				Intent intent = new Intent(this, CollectionAddExpressNoActivity.class);
				if(result.optJSONObject("real_info")!=null){
					intent.putExtra("instruction", result.optJSONObject("real_info").optString("instruction"));
				}
				intent.putExtra("money", money);
				intent.putExtra("desc", result.optString("desc"));
				intent.putExtra("create_time", create_time);
				intent.putExtra("order_number", order_number);
				intent.putExtra("isContinuous", false);
				intent.putExtra("qrcodetype", Constants.TYPE_COLLECTION);
				startActivity(intent);
				finish();
			} else if ("FAIL".equals(result.optString("status"))) {
				getResultTimes = REQUEST_TIEMS;
				UtilToolkit.showToast( result.optString("desc"));
				finish();
			}
		} else if("payment/getPayDesc".equals(sname)){
			MakeCollectionDescParameter mcp = JSON.parseObject(result.toString(),MakeCollectionDescParameter.class);
			tv_desc.setText(mcp.getDesc());
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (result == null) {
			return;
		}
		UtilToolkit.showToast( result);
		if (PAYMENT_GET_ORDER_STATUS.equals(sname)) {
			getResultTimes = REQUEST_TIEMS;
			finish();
		} else if (PAYMENT.equals(sname)) {
			getResultTimes = REQUEST_TIEMS;
			finish();
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}
}