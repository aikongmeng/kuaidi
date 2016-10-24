package com.kuaibao.skuaidi.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtility {
	
	/**
	 * 将bitmap保存到本地
	 * @param bitmap 
	 * @param path 路径
	 * @param picName 图片名字
	 */
	public static void saveBitmapInLocal(Bitmap bitmap ,String path,String picName){
		File file = new File(Environment.getExternalStorageDirectory().getPath()+path,picName+".jpg");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException  e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
