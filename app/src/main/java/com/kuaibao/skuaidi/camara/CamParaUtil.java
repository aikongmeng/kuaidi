package com.kuaibao.skuaidi.camara;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {
	private static final String TAG = "skuaidi camera";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CamParaUtil myCamPara = null;

	private CamParaUtil() {

	}

	public static CamParaUtil getInstance() {
		if (myCamPara == null) {
			myCamPara = new CamParaUtil();
			return myCamPara;
		} else {
			return myCamPara;
		}
	}

	public Size getMaxPropPreviewSize(List<Camera.Size> list) {
		Collections.sort(list, sizeComparator);
		return list.get(list.size() - 1);
	}

	public Size getMaxPropPictureSize(List<Camera.Size> list) {
		Collections.sort(list, sizeComparator);
		return list.get(list.size() - 1);
	}

	public Size getPropPreviewSize(List<Camera.Size> list, float th,
			int minHeight) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.height >= minHeight) && equalRate(s, th)) {
				//Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i = 0;// 如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public Size getPropPictureSize(List<Camera.Size> list, float th,
			int minHeight) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.height >= minHeight) && equalRate(s, th)) {
				//Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i = 0;// 如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate) {
		float r = (float) (s.width) / (float) (s.height);
		return Math.abs(r - rate) <= 0.03;
	}

	public class CameraSizeComparator implements Comparator<Camera.Size> {
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}

	/**
	 * 打印支持的previewSizes
	 * 
	 * @param params
	 */
	public void printSupportPreviewSize(Camera.Parameters params) {
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for (int i = 0; i < previewSizes.size(); i++) {
			Size size = previewSizes.get(i);
			//Log.i(TAG, "previewSizes:width = " + size.width + " height = "+ size.height);
		}

	}

	/**
	 * 打印支持的pictureSizes
	 * 
	 * @param params
	 */
	public void printSupportPictureSize(Camera.Parameters params) {
		List<Size> pictureSizes = params.getSupportedPictureSizes();
		for (int i = 0; i < pictureSizes.size(); i++) {
			Size size = pictureSizes.get(i);
			//Log.i(TAG, "pictureSizes:width = " + size.width + " height = "+ size.height);
		}
	}

	/**
	 * 打印支持的聚焦模式
	 * 
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params) {
		List<String> focusModes = params.getSupportedFocusModes();
		for (String mode : focusModes) {
			//Log.i(TAG, "focusModes--" + mode);
		}
	}

	public Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
}
