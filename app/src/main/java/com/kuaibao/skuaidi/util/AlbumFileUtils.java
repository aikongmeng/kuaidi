package com.kuaibao.skuaidi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AlbumFileUtils {

	public static String SDPATH = Constants.ROOT + "/skuaidi/pic/CirclePic/";

	/**
	 * bitmap 保存到本地SDPATH
	 * 
	 * @param bm
	 * @param picName
	 */
	public static void saveBitmap(Bitmap bm, String picName) {
		//Log.i("iiii", "保存图片");
		try {
			if (!isFileExist()) {
				createSDDir();
			}
			File f = new File(SDPATH, picName + ".JPEG");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			//Log.i("iiii", "已经保存");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取网络图片
	 * 
	 * @param imageURL
	 * @return
	 */
	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;

	}

	/**
	 * 将InputStream转换成Bitmap
	 * 
	 * @param is
	 * @return
	 */
	public Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);

	}

	public static File createSDDir() throws IOException {
		File dir = new File(SDPATH);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			//System.out.println("createSDDir:" + dir.getAbsolutePath());
			//System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist() {
		File file = new File(SDPATH);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * bitmap保存到本地
	 * 
	 * @param bitmap
	 * @return String fileName 文件名
	 */
	public static String SavePicInLocal(Bitmap bitmap) {
		String fileName = "";
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null; // 字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
			String saveDir = SDPATH;
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdir(); // 创建文件夹
			}
			fileName = saveDir + System.currentTimeMillis() + ".jpg";
			File file = new File(fileName);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();// 创建文件
				//Log.e("PicDir", file.getPath());
			}
			// 将字节数组写入到刚创建的图片文件中
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}

}
