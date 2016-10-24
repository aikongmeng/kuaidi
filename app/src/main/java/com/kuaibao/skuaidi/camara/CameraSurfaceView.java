package com.kuaibao.skuaidi.camara;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kuaibao.skuaidi.camara.CameraInterface.CamOpenOverCallback;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,CamOpenOverCallback {
	private static final String TAG = "skuaidi camera";
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;
	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "surfaceCreated...");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "surfaceDestroyed...");
		CameraInterface.getInstance().doStopCamera(holder,this);
	}
	public SurfaceHolder getSurfaceHolder(){
		return mSurfaceHolder;
	}

	@Override
	public void cameraHasOpened() {
		
	}
	
}
