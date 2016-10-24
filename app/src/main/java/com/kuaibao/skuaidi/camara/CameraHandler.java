package com.kuaibao.skuaidi.camara;


import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

public class CameraHandler extends Handler {
	
	private static final String TAG = CameraHandler.class.getSimpleName();
	public static final int AUTO_FOCUS = 1;
	public static final int RESTART_PREVIEW = 2;
	public static final int TAKEN_SUCCESS = 3;
	public static final int TAKEN_FAILED = 4;
	public static final int LAUNCH_PRODUCT_QUERY = 5;
	public static final int RETURN_TAKEN_RESULT = 6;
	

	private State state;
	private enum State {
		PREVIEW, SUCCESS, DONE
	}
	
	public CameraHandler(SurfaceHolder holder,float previewRate){
		state = State.SUCCESS;
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
		restartPreview();
	}
	
	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case AUTO_FOCUS:
			if (state == State.PREVIEW) {
				CameraInterface.getInstance().requestAutoFocus(this, AUTO_FOCUS);
			}
			break;
		case RESTART_PREVIEW:
			//Log.d(TAG, "Got restart preview message");
			restartPreview();
			break;

		}
	}

	public void restartPreview() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraInterface.getInstance().requestAutoFocus(this, AUTO_FOCUS);
		}
	}

}
