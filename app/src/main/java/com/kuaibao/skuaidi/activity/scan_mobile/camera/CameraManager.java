/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuaibao.skuaidi.activity.scan_mobile.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

//	private static final int MIN_FRAME_WIDTH = 240;
//	private static final int MIN_FRAME_HEIGHT = 240;
//	private static final int MAX_FRAME_WIDTH = 600;
//	private static final int MAX_FRAME_HEIGHT = 400;

    private static CameraManager cameraManager;

    private final Context context;
    private final CameraConfigurationManager configManager;
    private Camera camera;
    private AutoFocusManager autoFocusManager;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    //	private int requestedFramingRectWidth;
//	private int requestedFramingRectHeight;
    private Activity activity;

    /**
     * Preview frames are delivered here, which we pass on to the registered
     * handler. Make sure to clear the handler so it will only receive one
     * message.
     */
    private final PreviewCallback previewCallback;

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param context The Activity which wants to use the camera.
     */
    public static void init(Context context, Activity activity) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context, activity);
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    public CameraManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager);
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames
     *               into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
//        Camera theCamera = camera;
        if (camera == null) {
            camera = Camera.open();// new OpenCameraManager().build().open();
            // open(theCamera);
            if (camera == null) {
                throw new IOException();
            }
            camera.setDisplayOrientation(90);
//            camera = theCamera;
        }

        camera.setPreviewDisplay(holder);

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(camera);
            /*if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
				setManualFramingRect(requestedFramingRectWidth,requestedFramingRectHeight);//*****************************
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}*/
        }

        Parameters parameters = camera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save these, temporarily
        try {
            configManager.setDesiredCameraParameters(camera, false);
        } catch (RuntimeException re) {
            Log.w(TAG,"Camera rejected parameters. Setting only minimal safe-mode parameters");
            Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            // Reset:
            if (parametersFlattened != null) {
                parameters = camera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    camera.setParameters(parameters);
                    configManager.setDesiredCameraParameters(camera, true);
                } catch (RuntimeException re2) {
                    Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }

    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
            // Make sure to clear these each time we close the camera, so that
            // any scanning rect
            // requested by intent is forgotten.
//            framingRect = null;
            framingRectInPreview = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     * 将相机硬件开始绘制预览帧到屏幕上
     */
    public synchronized void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(context, camera);
            autoFocusManager.manager = this;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public synchronized void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    private Handler handler = null;
    private int message = -1;
    private Camera.PreviewCallback pre = null;

    /**
     * A single preview frame will be returned to the handler supplied. The data
     * will arrive as byte[] in the message.obj field, with width and height
     * encoded as message.arg1 and message.arg2, respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public synchronized void requestPreviewFrame(Handler handler, int message,
                                                 Camera.PreviewCallback pre) {
        this.handler = handler;
        this.message = message;
        Log.d(TAG, "set hanld and mesg is " + message);
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            // theCamera.setOneShotPreviewCallback(previewCallback);
            this.pre = pre;
            camera.setOneShotPreviewCallback(pre);
        }
    }

    public boolean flash() {
        boolean isFlashOn = false;
        if (camera == null) {
            camera = Camera.open();
        }
        if (camera != null && camera.getParameters() != null) {
            Parameters parameter = camera.getParameters();
            if (Parameters.FLASH_MODE_OFF.equals(parameter.getFlashMode())) {
                parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
                isFlashOn = true;
            } else {
                parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
                isFlashOn = false;
            }

            try {
                camera.setParameters(parameter);
            } catch (RuntimeException e) {
                //Log.d("iii", "闪光灯打开失败");
                e.printStackTrace();
            }
        }
        return isFlashOn;
    }

    public synchronized void requestPreviewFrame() {
        if (handler != null) {
            Camera theCamera = camera;
            Log.d(TAG, "set hanld and mesg is " + message);
            if (theCamera != null && previewing) {
                previewCallback.setHandler(handler, message);
                theCamera.setOneShotPreviewCallback(pre);
            }
        }
    }

    /**
     * 计算扫描匡的位置 Calculates the framing rect which the UI should draw to show the
     * user where to place the barcode. This target helps with alignment as well
     * as forces the user to hold the device far enough away to ensure the image
     * will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates. 扫描框的矩形
     */
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            float denysity = context.getResources().getDisplayMetrics().density;// 屏幕密度
            int width = screenResolution.x /5*3;
//            int width = screenResolution.x - (int) (200 * denysity);
//			height = (int)(25*denysity);
//			width = 600;
//            int height = 150;
            int height = width/8+20;
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (int) (100 * denysity);
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
        return framingRect;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview
     * frame, not UI / screen.
     */
    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();

            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

}