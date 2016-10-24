package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class BitmapUtil {
	private static Map<Class<?>, Integer> max = new HashMap<Class<?>, Integer>();
	public static boolean act_bool = true;
	private static Map<Class<?>, List<Bitmap>> bmp = new HashMap<Class<?>, List<Bitmap>>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	private static Map<Class<?>, List<String>> drr = new HashMap<Class<?>, List<String>>();// 保存图片地址
	private static Map<Class<?>, List<String>> imgId = new HashMap<Class<?>, List<String>>();// 保存图片ID
	private static Map<Class<?>, List<String>> imgBackUps = new HashMap<Class<?>, List<String>>();// 备份删掉的图片ID
	private static Context fromContext;

	public static int getMax(Context context) {
		if (max.get(context.getClass()) == null) {
			max.put(context.getClass(), 0);
		}
		return max.get(context.getClass());
	}

	public static void setMax(Context context, int mMax) {
		max.put(context.getClass(), mMax);
	}

	public static List<Bitmap> getBmp(Context context) {
		if (bmp.get(context.getClass()) == null) {
			bmp.put(context.getClass(), new ArrayList<Bitmap>());
		}
		return bmp.get(context.getClass());
	}

	public static void setBmp(Context context, List<Bitmap> childBmp) {
		bmp.put(context.getClass(), childBmp);
	}

	public static List<String> getDrr(Context context) {
		if (null == drr.get(context.getClass())) {
			drr.put(context.getClass(), new ArrayList<String>());
		}
		return drr.get(context.getClass());
	}

	public static void setDrr(Context context, List<String> childDrr) {
		drr.put(context.getClass(), childDrr);
	}

	public static List<String> getImgId(Context context) {
		if (imgId.get(context.getClass()) == null) {
			imgId.put(context.getClass(), new ArrayList<String>());
		}
		return imgId.get(context.getClass());
	}

	public static void setImgId(Context context, List<String> childImgId) {
		imgId.put(context.getClass(), childImgId);
	}

	public static List<String> getImgBackUps(Context context) {
		if (imgBackUps.get(context.getClass()) == null) {
			imgBackUps.put(context.getClass(), new ArrayList<String>());
		}
		return imgBackUps.get(context.getClass());
	}

	public static void setBackUps(Context context, List<String> childBackUps) {
		imgBackUps.put(context.getClass(), childBackUps);
	}

	public static void setFromContext(Context context) {
		fromContext = context;
	}

	public static Context getFromContext() {
		return fromContext;
	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public static void removeBitMap(int index) {
		bmp.remove(index);
	}

	/**
	 * 生成二维码（返回bitmap）
	 */
	public static Bitmap Create2DCode(Context context, String str) throws WriterException {

		Hashtable<EncodeHintType, Comparable> hints = new Hashtable<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, 1);// 设置二给码的白边-分1，2，3，4级，1最小4最大
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, (int) (context.getResources().getDisplayMetrics().density) * 300, (int) (context.getResources().getDisplayMetrics().density) * 300, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;// 设置为黑色码
				} else {
					pixels[y * width + x] = 0xffffffff;// 设置为白色底
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 将本地路径上的图片转换成bitmap
	 * 
	 * @param path
	 *            图片本地路径
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);

			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将bitmap图片保存到本地
	 * 
	 * @param bitmap
	 * @return 返回图片路径
	 */
	public static String saveBitmap2Local(Bitmap bitmap) {
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/shopImage/";
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File imageFile = new File(file, System.currentTimeMillis() + ".jpg");
		String imagePath = imageFile.getPath();
		//System.out.println("gudd imagePath    " + imagePath);
		try {
			imageFile.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(imageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imagePath;// 返回图片路径
	}

	/**
	 * 旋转图片
	 * 
	 * @param degree
	 *            旋转的角度
	 * @param bitmap
	 *            需要旋转的图片
	 * @return resizedBitmap 旋转以后的图片
	 */
	public static Bitmap rotaingImageView(int degree, Bitmap bitmap) {
		Bitmap resizedBitmap = null;
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		//System.out.println("angle2=" + degree);
		// 创建新的图片
		if (!Utility.isEmpty(bitmap)) {
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return resizedBitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 将sd卡上的文件转换成bigmap--并将图片设置为指定的长宽
	 * 
	 * @param path
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap convertToBitmap(String path, int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// opts.inSampleSize = 1;// 设为1才是原始大小
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	/**
	 * 将sd卡上的文件转换成bigmap--图片为原始尺寸
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap convertToBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = 3;// 设置图片尺寸是原图的3分之1，如果 为1的话则是代表原图
		return BitmapFactory.decodeFile(path, opts);
	}

}
