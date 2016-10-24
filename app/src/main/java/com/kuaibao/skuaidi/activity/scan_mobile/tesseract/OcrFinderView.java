package com.kuaibao.skuaidi.activity.scan_mobile.tesseract;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.scan_mobile.camera.CameraManager;
import com.kuaibao.skuaidi.util.Utility;

public class OcrFinderView extends View {

	private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128,
			64};
	private static final long ANIMATION_DELAY = 80L;
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 20;
	private static final int POINT_SIZE = 6;

	private final Paint paint;
	private final int maskColor;
	private final int resultColor;
	private final int laserColor;
	private final int resultPointColor;
	private int fourCorners;// 扫描框四的角的颜色
	private int scannerAlpha;
	private CameraManager cameraManager;
	private Bitmap resultBitmap;

	/** 四个绿色边角对应的长度*/
	private int ScreenRate;
	/** 手机的屏幕密度[像素比例]*/
	private static float density;
	/** 四个绿色边角对应的宽度 */
	private static final int CORNER_WIDTH = 5;
	/** 字体距离扫描框下面的距离 */
	private static final int TEXT_PADDING_TOP = 30;


	public OcrFinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (10 * density);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		fourCorners = Utility.getColor(context,R.color.default_green_2);
		scannerAlpha = 0;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// 画扫描框边上的角，总共8个部分
		paint.setColor(fourCorners);
		canvas.drawRect(frame.left, frame.top-10, frame.left + ScreenRate, frame.top-10 + CORNER_WIDTH, paint);// 左上横
		canvas.drawRect(frame.left, frame.top-10, frame.left + CORNER_WIDTH, frame.top-10 + ScreenRate, paint);// 左上竖
		canvas.drawRect(frame.right+1 - ScreenRate, frame.top-10, frame.right+1, frame.top-10 + CORNER_WIDTH, paint);// 右上横
		canvas.drawRect(frame.right+1 - CORNER_WIDTH, frame.top-10, frame.right+1, frame.top-10 + ScreenRate, paint);// 右上竖
		canvas.drawRect(frame.left, frame.bottom+1 - CORNER_WIDTH, frame.left + ScreenRate, frame.bottom+1, paint);// 左下横
		canvas.drawRect(frame.left, frame.bottom+1 - ScreenRate, frame.left + CORNER_WIDTH, frame.bottom+1, paint);// 左下竖
		canvas.drawRect(frame.right+1 - ScreenRate, frame.bottom+1 - CORNER_WIDTH, frame.right+1, frame.bottom+1, paint);// 右下横
		canvas.drawRect(frame.right+1 - CORNER_WIDTH, frame.bottom+1 - ScreenRate, frame.right+1, frame.bottom+1, paint);// 右下竖

		// 画透明黑色阴影
	    paint.setColor(resultBitmap != null ? resultColor : maskColor);
	    canvas.drawRect(0, 0, width, frame.top-10, paint);
	    canvas.drawRect(0, frame.top-10, frame.left, frame.bottom + 1, paint);
	    canvas.drawRect(frame.right + 1, frame.top-10, width, frame.bottom + 1, paint);
	    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		// 扫描框下方的字
		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(12 * density);
		textPaint.setTypeface(Typeface.create("System", Typeface.NORMAL));

		// 文字自动换行
		StaticLayout layout = new StaticLayout("请将扫描框对准面单上的手机号（限电子面单)", textPaint, frame.width() + frame.left,
				StaticLayout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
		canvas.save();
		textPaint.setTextAlign(Paint.Align.LEFT);
		canvas.translate(frame.left - frame.left/2, frame.bottom + (float) TEXT_PADDING_TOP * density);// 起始位置【左|上】
		layout.draw(canvas);
		canvas.restore();

	    if (resultBitmap != null) {
	        // Draw the opaque result bitmap over the scanning rectangle
	        paint.setAlpha(CURRENT_POINT_OPACITY);
	        canvas.drawBitmap(resultBitmap, null, frame, paint);
	      } else {

	        // 画扫描框中间的横线
	        /*paint.setColor(laserColor);
	        paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
	        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
	        int middle = frame.height() / 2 + frame.top;
	        canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);*/
	        
	        Rect previewFrame = cameraManager.getFramingRectInPreview();
	        float scaleX = frame.width() / (float) previewFrame.width();
	        float scaleY = frame.height() / (float) previewFrame.height();


	        postInvalidateDelayed(ANIMATION_DELAY,
	                              frame.left - POINT_SIZE,
	                              frame.top - POINT_SIZE,
	                              frame.right + POINT_SIZE,
	                              frame.bottom + POINT_SIZE);
	      }
	}
	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}
}
