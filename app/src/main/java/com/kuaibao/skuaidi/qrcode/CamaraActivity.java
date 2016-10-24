package com.kuaibao.skuaidi.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.TakePicImageView;
import com.kuaibao.skuaidi.camara.CameraHandler;
import com.kuaibao.skuaidi.camara.CameraInterface;
import com.kuaibao.skuaidi.camara.CameraInterface.CamOpenOverCallback;
import com.kuaibao.skuaidi.camara.CameraInterface.PictrueTakeFishCallBack;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

/**
 * 
 * 拍照基类
 * 
 * @author xy
 * 
 */
public class CamaraActivity extends RxRetrofitBaseActivity implements CamOpenOverCallback, PictrueTakeFishCallBack, Callback,
		OnClickListener {

	private static final String TAG = "skuaidi camera";
	protected ImageView iv_applyToAll;

	SurfaceView surfaceView = null;
	ImageView shutterBtn;
	TakePicImageView maskView = null;
	View close, commit, iv_preview, right_view;
	protected TextView picSize;
	private Context context;
	protected TextView tv_waybill_num;
	private boolean hasSurface;

	float previewRate = -1f;
	int DST_CENTER_RECT_WIDTH = 200; // 单位是dip
	int DST_CENTER_RECT_HEIGHT = 200;// 单位是dip

	Point rectPictureSize = null;
	private boolean isLightOn = false;// 闪光灯是否打开
	private CameraHandler cameraHandler;
	public final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				hasSurface = true;
				if (cameraHandler == null) {
					cameraHandler = new CameraHandler((SurfaceHolder) msg.obj, previewRate);
				}
				break;

			default:
				break;
			}
		}
	};

	private static final Handler autoFocusHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	Thread openThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// openThread.start();

		context = this;
		setContentView(R.layout.camera_activity);
		initUI();
		initViewParams();
		initAnimation();
		shutterBtn.setOnClickListener(this);
		close.setOnClickListener(this);
		commit.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	boolean isRestart = false;

	@Override
	protected void onRestart() {
		super.onRestart();
		isRestart = true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			openThread = new Thread(new Runnable() {

				@Override
				public void run() {
					CameraInterface.getInstance().doOpenCamera(CamaraActivity.this, CamaraActivity.this, context);
				}
			});
			openThread.start();
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	@SuppressWarnings("unchecked")
	private void initUI() {
		surfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
		right_view = findViewById(R.id.rl_right_takeOper);
		shutterBtn = (ImageView) findViewById(R.id.btn_shutter);
		iv_applyToAll = (ImageView) findViewById(R.id.btn_all);
		maskView = (TakePicImageView) findViewById(R.id.view_mask);
		close = findViewById(R.id.camera_close);
		commit = findViewById(R.id.camera_commit);
		iv_preview = findViewById(R.id.iv_preview);
		picSize = (TextView) findViewById(R.id.tv_picSize);
		tv_waybill_num = (TextView) findViewById(R.id.tv_wayBill_num);

	}

	private void initViewParams() {
		android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) tv_waybill_num
				.getLayoutParams();
		layoutParams.setMargins(0, 20, right_view.getWidth() / 2, 0);
		tv_waybill_num.setLayoutParams(layoutParams);
	}

	public void cameraHasOpened() {
		SurfaceHolder holder = surfaceView.getHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
		if (maskView != null) {
			Rect screenCenterRect = createCenterScreenRect(DisplayUtil.dip2px(DST_CENTER_RECT_WIDTH),
					DisplayUtil.dip2px(DST_CENTER_RECT_HEIGHT));
			maskView.setCenterRect(screenCenterRect);
		}
	}

	/**
	 * 生成拍照后图片的中间矩形的宽度和高度
	 * 
	 * @param w
	 *            屏幕上的矩形宽度，单位px
	 * @param h
	 *            屏幕上的矩形高度，单位px
	 * @return
	 */
	private Point createCenterPictureRect(int w, int h) {

		int wScreen = DisplayUtil.getScreenMetrics().x;
		int hScreen = DisplayUtil.getScreenMetrics().y;
		int wSavePicture = CameraInterface.getInstance().doGetPrictureSize().y; // 因为图片旋转了，所以此处宽高换位
		int hSavePicture = CameraInterface.getInstance().doGetPrictureSize().x; // 因为图片旋转了，所以此处宽高换位
		float wRate = (float) (wSavePicture) / (float) (wScreen);
		float hRate = (float) (hSavePicture) / (float) (hScreen);
		float rate = (wRate <= hRate) ? wRate : hRate;// 也可以按照最小比率计算

		int wRectPicture = (int) (w * wRate);
		int hRectPicture = (int) (h * hRate);
		return new Point(wRectPicture, hRectPicture);

	}

	/**
	 * 生成屏幕中间的矩形
	 * 
	 * @param w
	 *            目标矩形的宽度,单位px
	 * @param h
	 *            目标矩形的高度,单位px
	 * @return
	 */
	private Rect createCenterScreenRect(int w, int h) {

		int x1;
		int y1;
		int x2;
		int y2;
		if (SkuaidiSpf.getLoginUser().getExpressNo().equals("zt")) {
			// 矩阵生成在屏幕中间 且为正方形
			// x1 = DisplayUtil.getScreenMetrics(this).x / 2 - w / 2;
			// y1 = DisplayUtil.getScreenMetrics(this).y / 2 - h / 2;
			// x2 = x1 + w;
			// y2 = y1 + h;

			x1 = DisplayUtil.dip2px(25);
			y1 = DisplayUtil.dip2px(75);
			x2 = DisplayUtil.getScreenMetrics().x - x1;
			y2 = DisplayUtil.getScreenMetrics().y / 2;
			return new Rect(x1, y1, x2, y2);
		}
		// x1 = DisplayUtil.getScreenMetrics(this).x / 2 - w / 2;
		// x2 = x1 + w;
		// y1 = DisplayUtil.getScreenMetrics(this).y / 2 - h;
		// y2 = y1 + h;
		return new Rect(0, 0, DisplayUtil.getScreenMetrics().x, DisplayUtil.getScreenMetrics().y);
	}

	@Override
	public void onPictrueTakeFish(final Bitmap bitmap, String picPath) {
		// iv_preview.setBackgroundDrawable(new
		// BitmapDrawable(ImageUtil.decodeFile(picPath)));
		// iv_preview.startAnimation(animationSet);
	}

	protected void closeCameraActivity() {

	}

	protected void cameraCommit() {

	}

	AnimationSet animationSet = new AnimationSet(true);
	AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
	ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_PARENT, 0.95f,
			Animation.RELATIVE_TO_PARENT, 0.05f);

	private void initAnimation() {
		iv_preview.setVisibility(View.VISIBLE);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setDuration(1500);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				iv_preview.setVisibility(View.GONE);
				onTakeAnimationEnd(animation);
			}
		});
	}

	protected void onTakeAnimationEnd(Animation animation) {

	}

	protected void closeDriver() {
		CameraInterface.getInstance().doStopCamera(surfaceView.getHolder(), this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeDriver();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void open(View view) {
		//Log.i("iii", "闪光灯");
		try {
			CameraInterface.getInstance().flash();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageView iv_flashlight = (ImageView) findViewById(R.id.iv_flashlight);
		if (!isLightOn) {
			iv_flashlight.setImageResource(R.drawable.icon_sign_flashlight_opened);
			isLightOn = true;
		} else {
			iv_flashlight.setImageResource(R.drawable.icon_sign_flashlight_closed);
			isLightOn = false;
		}
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		openThread = new Thread() {
			@Override
			public void run() {
				//Log.d("cameraActivitySurface", "created");
				CameraInterface.getInstance().doOpenCamera(CamaraActivity.this, CamaraActivity.this, context);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = holder;
				handler.sendMessage(msg);
			}
		};
		openThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.d("cameraActivitySurface", "destroyed");
		hasSurface = false;
		openThread.interrupt();
		closeDriver();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_shutter:
			action();
			break;
		case R.id.camera_close:
			closeDriver();
			closeCameraActivity();
			finish();
			break;
		case R.id.camera_commit:
			cameraCommit();
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 点击拍照按钮之后
	 */
	protected void action() {
		if (CameraInterface.getInstance().isCameraOpen() == false) {
			UtilToolkit.showToast("相机未打开");
			return;
		}
		if (rectPictureSize == null) {
			rectPictureSize = createCenterPictureRect(
					DisplayUtil.getScreenMetrics().x - DisplayUtil.dip2px(50),
					DisplayUtil.getScreenMetrics().y / 2 - DisplayUtil.dip2px(75));
		}
		CameraInterface.getInstance().doTakePicture(rectPictureSize.x, rectPictureSize.y);
	}

}
