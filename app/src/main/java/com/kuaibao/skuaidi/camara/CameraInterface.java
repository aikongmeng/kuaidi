package com.kuaibao.skuaidi.camara;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;

import com.kuaibao.skuaidi.manager.SkuaidiIMGCompressManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class CameraInterface {
	private static final String TAG = "skuaidi camera";
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	private static final int TEN_DESIRED_ZOOM = 27;
	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private static CameraInterface mCameraInterface;
	private PictrueTakeFishCallBack takeFishCallBack;
	private final boolean useOneShotPreviewCallback;
	private boolean isTakeFinish = true;
	private boolean isCameraOpen = false;
	private Point screenResolution;
	private Point cameraResolution;
	private int previewFormat;
	private String previewFormatString;
	private Context context;
	private com.kuaibao.skuaidi.camara.AutoFocusCallback autoFocusCallback;

	private static float cutPercentage;

	private static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
	static {
		int sdkInt;
		try {
			sdkInt = Integer.parseInt(Build.VERSION.SDK);
		} catch (NumberFormatException nfe) {
			// Just to be safe
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	public interface CamOpenOverCallback {
		void cameraHasOpened();
	}

	public interface PictrueTakeFishCallBack {
		void onPictrueTakeFish(Bitmap bitmap, String bitMapPath);
	}

	private CameraInterface() {
		useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3;
		autoFocusCallback = new com.kuaibao.skuaidi.camara.AutoFocusCallback();
	}

	private static class CameraInterfaceHolder {
		private static final CameraInterface mCamera = new CameraInterface();

	}

	public static final CameraInterface getInstance() {
		return CameraInterfaceHolder.mCamera;
	}

	/*
	 * public static CameraInterface getInstance() { if (mCameraInterface ==
	 * null) { synchronized (CameraInterface.class) { mCameraInterface = new
	 * CameraInterface(); } }
	 * 
	 * return mCameraInterface; }
	 */

	/*
	 * public static synchronized CameraInterface getInstance() { if
	 * (mCameraInterface == null) { mCameraInterface = new CameraInterface(); }
	 * 
	 * return mCameraInterface; }
	 */

	/**
	 * 打开Camera
	 * 
	 * @param callback
	 */
	public void doOpenCamera(CamOpenOverCallback callback, PictrueTakeFishCallBack takeFishCallBack, Context context) {
		// Log.i(TAG, "Camera open....");
		try {
			this.context = context;
			mCamera = Camera.open();
			isCameraOpen = true;
		} catch (Exception e) {
		}
		// Log.i(TAG, "Camera open over....");
		callback.cameraHasOpened();
		this.takeFishCallBack = takeFishCallBack;
	}

	/**
	 * 使用Surfaceview开启预览
	 * 
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate) {
		// Log.i(TAG, "doStartPreview...");
		if (isPreviewing && mCamera != null) {
			try {
				mCamera.startPreview();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(null);
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initCamera(previewRate);
		}

	}

	public void setIsCameraOpen(boolean isOpen) {
		isCameraOpen = isOpen;
	}

	public boolean isCameraOpen() {
		return isCameraOpen;
	}

	/**
	 * 使用TextureView预览Camera
	 * 
	 * @param surface
	 * @param previewRate
	 */
	@SuppressLint("NewApi")
	public void doStartPreview(SurfaceTexture surface, float previewRate) {
		// Log.i(TAG, "doStartPreview...");
		if (isPreviewing) {
			mCamera.stopPreview();
			return;
		}
		if (mCamera != null) {
			try {
				mCamera.setPreviewTexture(surface);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initCamera(previewRate);
		}

	}

	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera(SurfaceHolder holder, Callback callback) {
		if (null != mCamera) {
			if (!useOneShotPreviewCallback) {
				mCamera.setPreviewCallback(null);
			}
			holder.removeCallback(callback);
			mCamera.stopPreview();
			isPreviewing = false;
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;
			isCameraOpen = false;
		}
	}

	public void requestAutoFocus(Handler handler, int message) {
		if (mCamera != null && isPreviewing) {
			autoFocusCallback.setHandler(handler, message);
			// //Log.d(TAG, "Requesting auto-focus callback");
			try {
				mCamera.autoFocus(autoFocusCallback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 拍照
	 */
	public void doTakePicture() {
		if (isPreviewing && (mCamera != null)) {
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}

	int DST_RECT_WIDTH, DST_RECT_HEIGHT;

	public void doTakePicture(int w, int h) {
		if (isPreviewing && (mCamera != null) && isTakeFinish == true) {
			isTakeFinish = false;
			// Log.i(TAG, "矩形拍照尺寸:width = " + w + " h = " + h);
			DST_RECT_WIDTH = w;
			DST_RECT_HEIGHT = h;
			// 系统不支持的PictureSize会导致mRectJpegPictureCallback不能回调 ?
			mCamera.takePicture(mShutterCallback, null, mRectJpegPictureCallback);
		}
	}

	public Point doGetPrictureSize() {
		Size s;
		Parameters parameters = mCamera.getParameters();
		s = parameters.getPictureSize();
		if (s != null)
			return new Point(s.width, s.height);
		else
			return new Point(2560, 4480);
	}

	private void initCamera(float previewRate) {
		if (mCamera != null) {
			// initFromCameraParameters(mCamera);
			// setDesiredCameraParameters(mCamera);
			mParams = mCamera.getParameters();
			String flatten = mParams.flatten();

			// Log.d("flatten", flatten);

			mParams.setPictureFormat(PixelFormat.JPEG);
			List<Size> pictureSizes = mParams.getSupportedPictureSizes();
			Size size2 = getCurrentScreenSize(pictureSizes, 1);
			mParams.setPictureSize(size2.width, size2.height);
			mCamera.setDisplayOrientation(90);
			mCamera.setParameters(mParams);
			mCamera.startPreview();// 开启预览
			isPreviewing = true;
			mPreviwRate = previewRate;
			mParams = mCamera.getParameters(); // 重新get一次

			// Log.i(TAG,"最终设置:PreviewSize--With = " +
			// mParams.getPreviewSize().width + "Height = "+
			// mParams.getPreviewSize().height);
			// Log.i(TAG,"最终设置:PictureSize--With = " +
			// mParams.getPictureSize().width + "Height = "+
			// mParams.getPictureSize().height);
		}
	}

	/* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
	ShutterCallback mShutterCallback = new ShutterCallback()
	// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{
		public void onShutter() {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback()
	// 拍摄的未压缩原数据的回调,可以为null
	{

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myRawCallback:onPictureTaken...");

		}
	};
	/**
	 * 常规拍照
	 */
	PictureCallback mJpegPictureCallback = new PictureCallback()
	// 对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				// 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
				// 90)失效。
				// 图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
				String path = FileUtil.saveBitmap(rotaBitmap);
				takeFishCallBack.onPictrueTakeFish(b, path);
			}
			// 再次进入预览
			mCamera.startPreview();
			isPreviewing = true;
		}
	};

	/**
	 * 拍摄指定区域的Rect
	 */
	PictureCallback mRectJpegPictureCallback = new PictureCallback()
	// 对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap b = null;
			if (null != data) {
				try {
					b = BitmapFactory.decodeByteArray(data, 0, data.length);
				} catch (java.lang.OutOfMemoryError e) {
					b = SkuaidiIMGCompressManager.getImageFromData(data, DisplayUtil.getScreenMetrics().x,
							DisplayUtil.getScreenMetrics().y);
				}
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
				if (E3SysManager.BRAND_ZT.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {

					cutPercentage = (768 - DST_RECT_WIDTH) / 2 / 768;

					// 注释部分为屏幕正中
					// int x = rotaBitmap.getWidth() / 2 - DST_RECT_WIDTH / 2;
					// int y = rotaBitmap.getHeight()/2 - DST_RECT_HEIGHT/2;
					int x = (rotaBitmap.getWidth() - DST_RECT_WIDTH) / 2;

					// int x = (int) (rotaBitmap.getWidth()*cutPercentage);
					int y = (int) (x * 2.2);
					Bitmap rectBitmap = null;
					try {
						rectBitmap = Bitmap.createBitmap(rotaBitmap, x, y, DST_RECT_WIDTH, DST_RECT_HEIGHT);// 裁剪图片
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (rectBitmap == null) {
						rectBitmap = rotaBitmap;
					} else {
						if (rotaBitmap != null) {
							rotaBitmap.recycle();
							rotaBitmap = null;
						}
					}
					String path = FileUtil.saveBitmap(rectBitmap);// 保存
					Bitmap tempMap = Utility.comPressImage(path);// 压缩

					if (tempMap != null) {
						tempMap = Utility.compressImage(tempMap, 5);
					}
					if (tempMap != null) {
						FileUtil.saveBitmap(tempMap, path);// 重新保存
						rectBitmap = tempMap;
					}
					takeFishCallBack.onPictrueTakeFish(rectBitmap, path);
					if (rectBitmap != null) {
						rectBitmap.recycle();
						rectBitmap = null;
					}
					if (rotaBitmap != null) {
						rotaBitmap.recycle();
						rotaBitmap = null;
					}
					if (tempMap != null) {
						tempMap.recycle();
						tempMap = null;
					}
				} else {
					rotaBitmap = Utility.imageZoom(rotaBitmap, 10);
					takeFishCallBack.onPictrueTakeFish(rotaBitmap, FileUtil.saveBitmap(rotaBitmap));
					if (rotaBitmap != null) {
						rotaBitmap.recycle();
						rotaBitmap = null;
					}
				}
			}
			// 再次进入预览
			if (null != mCamera) {
				mCamera.startPreview();
				isPreviewing = true;
			} else {
				isPreviewing = false;
			}
			isTakeFinish = true;
		}
	};

	private void getFocusMode() {
		CamParaUtil.getInstance().printSupportFocusMode(mParams);
		List<String> focusModes = mParams.getSupportedFocusModes();
		if (focusModes.contains("continuous-picture")) {
			// System.out.println("picture");
			mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else if (focusModes.contains("continuous-video")) {// 根据系统相机支持的自动对焦模式匹配自动对焦模式
			// System.out.println("video");
			mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		} else if (focusModes.contains("macro")) {
			// System.out.println("macro");
			mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		} else if (focusModes.contains("infinity")) {
			// System.out.println("infinity");
			mParams.setFocusMode("infinity");
		} else if (focusModes.contains("continuous-auto")) {
			// System.out.println("continuous-auto");
			mParams.setFocusMode("continuous-auto");
		} else if (focusModes.contains("auto")) {
			// System.out.println("auto");
			mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
	}

	public boolean flash() {
		boolean isFlashOn = false;

		if (mCamera != null) {
			mParams = mCamera.getParameters();
			if (mParams.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
				mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
				isFlashOn = true;
			} else {
				mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
				isFlashOn = false;
			}

			mCamera.setParameters(mParams);
		}

		return isFlashOn;
	}

	/**
	 * 获得最接近频幕宽度的尺寸
	 * 
	 * @param sizeList
	 * @param n
	 *            放大几倍 （>0)
	 * @return
	 */
	private Size getCurrentScreenSize(List<Size> sizeList, int n) {
		if (sizeList != null && sizeList.size() > 0) {
			int screenWidth = context.getResources().getDisplayMetrics().widthPixels * n;
			int[] arry = new int[sizeList.size()];
			int temp = 0;
			for (Size size : sizeList) {
				arry[temp++] = Math.abs(size.width - screenWidth);
			}
			temp = 0;
			int index = 0;
			for (int i = 0; i < arry.length; i++) {
				if (i == 0) {
					temp = arry[i];
					index = 0;
				} else {
					if (arry[i] < temp) {
						index = i;
						temp = arry[i];
					}
				}
			}
			return sizeList.get(index);
		}
		return null;
	}

	void initFromCameraParameters(Camera camera) {
		mParams = camera.getParameters();
		previewFormat = mParams.getPreviewFormat();
		previewFormatString = mParams.get("preview-format");
		// Log.d(TAG, "Default preview format: " + previewFormat + '/' +
		// previewFormatString);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenResolution = new Point(display.getWidth(), display.getHeight());
		// Log.d(TAG, "Screen resolution: " + screenResolution);
		cameraResolution = getCameraResolution(mParams, screenResolution);
		// Log.d(TAG, "Camera resolution: " + screenResolution);
	}

	/**
	 * Sets the camera up to take preview images which are used for both preview
	 * and decoding. We detect the preview format here so that
	 * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
	 * In the future we may want to force YUV420SP as it's the smallest, and the
	 * planar Y can be used for barcode scanning without a copy in some cases.
	 */
	private void setDesiredCameraParameters(Camera camera) {
		mParams = camera.getParameters();
		mParams.setPictureFormat(PixelFormat.JPEG);
		// Log.d(TAG, "Setting preview size: " + cameraResolution);
		mParams.setPreviewSize(cameraResolution.x, cameraResolution.y);
		setFlash(mParams);
		setZoom(mParams);
		// setSharpness(parameters);
		camera.setParameters(mParams);
		camera.setDisplayOrientation(90);
	}

	private Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			// Log.d(TAG, "preview-size-values parameter: " +
			// previewSizeValueString);
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
		}

		if (cameraResolution == null) {
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
		}

		return cameraResolution;
	}

	private Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				// Log.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newX;
			int newY;
			try {
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				// Log.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	private int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
		int tenBestValue = 0;
		for (String stringValue : COMMA_PATTERN.split(stringValues)) {
			stringValue = stringValue.trim();
			double value;
			try {
				value = Double.parseDouble(stringValue);
			} catch (NumberFormatException nfe) {
				return tenDesiredZoom;
			}
			int tenValue = (int) (10.0 * value);
			if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
				tenBestValue = tenValue;
			}
		}
		return tenBestValue;
	}

	private void setFlash(Camera.Parameters parameters) {
		// FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
		// And this is a hack-hack to work around a different value on the
		// Behold II
		// Restrict Behold II check to Cupcake, per Samsung's advice
		// if (Build.MODEL.contains("Behold II") &&
		// CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
		if (Build.MODEL.contains("Behold II") && SDK_INT == 3) { // 3 = Cupcake
			parameters.set("flash-value", 1);
		} else {
			parameters.set("flash-value", 2);
		}
		// This is the standard setting to turn the flash off that all devices
		// should honor.
		parameters.set("flash-mode", "off");
	}

	private void setZoom(Camera.Parameters parameters) {

		String zoomSupportedString = parameters.get("zoom-supported");
		if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
			return;
		}

		int tenDesiredZoom = TEN_DESIRED_ZOOM;

		String maxZoomString = parameters.get("max-zoom");
		if (maxZoomString != null) {
			try {
				int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				// Log.w(TAG, "Bad max-zoom: " + maxZoomString);
			}
		}

		String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
		if (takingPictureZoomMaxString != null) {
			try {
				int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				// Log.w(TAG, "Bad taking-picture-zoom-max: " +
				// takingPictureZoomMaxString);
			}
		}

		String motZoomValuesString = parameters.get("mot-zoom-values");
		if (motZoomValuesString != null) {
			tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
		}

		String motZoomStepString = parameters.get("mot-zoom-step");
		if (motZoomStepString != null) {
			try {
				double motZoomStep = Double.parseDouble(motZoomStepString.trim());
				int tenZoomStep = (int) (10.0 * motZoomStep);
				if (tenZoomStep > 1) {
					tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
				}
			} catch (NumberFormatException nfe) {
				// continue
			}
		}

		// Set zoom. This helps encourage the user to pull back.
		// Some devices like the Behold have a zoom parameter
		if (maxZoomString != null || motZoomValuesString != null) {
			parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
		}

		// Most devices, like the Hero, appear to expose this zoom parameter.
		// It takes on values like "27" which appears to mean 2.7x zoom
		if (takingPictureZoomMaxString != null) {
			parameters.set("taking-picture-zoom", tenDesiredZoom);
		}
	}
}
