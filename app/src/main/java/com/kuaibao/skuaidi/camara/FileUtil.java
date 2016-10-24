package com.kuaibao.skuaidi.camara;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	// 图片存放目录
	private static final String SKUAIDI_PIC = "/skuaidi/pic/";
	private static final String TAG = "skuaidi camera";

	/**
	 * 初始化保存路径
	 * 
	 * @return
	 */
	private static String initPath() {
		String picPath = Constants.ROOT + "/skuaidi/pic/e3_waybills/";
		File f = new File(picPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return picPath;
	}

	/**
	 * 保存Bitmap到sdcard
	 * 
	 * @param b
	 */
	public static String saveBitmap(Bitmap b) {
		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + dataTake + ".jpg";
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(jpegName);
			bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			UtilToolkit.showToast("图片压缩存储成功....");
			return jpegName;
		} catch (IOException e) {
			UtilToolkit.showToast("图片压缩存储失败....");
			e.printStackTrace();
			return "";
		} finally {

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存Bitmap到sdcard
	 * 
	 * @param b
	 */
	public static String saveBitmap(Bitmap b, String path) {
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(path);
			bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			return path;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int getBitmapsize(Bitmap bitmap) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
			return bitmap.getAllocationByteCount();
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();

	}

	public static boolean existSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static File mkPicdir() {
		String filePath = Environment.getExternalStorageDirectory().getPath() + SKUAIDI_PIC;// 设置图片存放目录
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static void imageCopy(String oldPath, String newPath) {
		File old = new File(oldPath);
		File newImage = new File(newPath);
		// newImage.mkdirs();
		FileInputStream imageInput = null;
		FileOutputStream imageOutput = null;
		try {
			if (!old.exists()) {
				// System.out.println("the file is not exists");
			} else {
				imageInput = new FileInputStream(old);

				if (!newImage.exists()) {
					// newImage.mkdir();
					newImage.createNewFile();

				}
				imageOutput = new FileOutputStream(newImage);

				byte[] temp = new byte[1000];
				int size = imageInput.read(temp);
				while (size != -1) {
					imageOutput.write(temp);
					size = imageInput.read(temp);
				}
				// System.out.println("the File Copy is success!");
			}
		} catch (FileNotFoundException fileNot) {
			// System.out.println("the File Copy is Failed!");
			fileNot.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// System.out.println("the File Copy is Failed!");
		} finally {
			try {
				imageInput.close();
				imageOutput.close();

			} catch (Exception ex) {
			}

		}
	}

}
