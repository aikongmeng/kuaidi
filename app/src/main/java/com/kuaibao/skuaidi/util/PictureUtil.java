package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureUtil {


	/**
	 * 把bitmap转换成String
	 * 
	 * @param filePath
	 * @return
	 */
	public static String bitmapToString(String filePath) {

		Bitmap bm = getSmallBitmap(filePath);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();
		
		return Base64.encodeToString(b, Base64.DEFAULT);
		
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(filePath, options);
	}
	//等比缩放图片
	public static  Bitmap getimage(String srcPath) {
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();
	        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
	        newOpts.inJustDecodeBounds = true;
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
	        newOpts.inJustDecodeBounds = false;
	        int w = newOpts.outWidth;
	        int h = newOpts.outHeight;
	        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	        float hh = 800f;//这里设置高度为800f
	        float ww = 480f;//这里设置宽度为480f
	        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	        int be = 1;//be=1表示不缩放
	        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
	            be = (int) (newOpts.outWidth / ww);
	        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
	            be = (int) (newOpts.outHeight / hh);
	        }
	        if (be <= 0)
	            be = 1;
	        newOpts.inSampleSize = be;//设置缩放比例
	        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	        return bitmap;//compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	    }
	 public static Bitmap compressImage(Bitmap pic,File save){
		//压缩图片
			//pic为bitmap类型的图片资源
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			ByteArrayInputStream isBm = null;
			  pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
			        int options = 100; 
			        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
			            baos.reset();//重置baos即清空baos 
			            pic.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中 
			            options -= 10;//每次都减少10 
			        } 
//			        File save = new File(CBuildConfig.picUrlStr+uuid);
//			  // 父文件夹不存在
//			  if (!save.getParentFile().exists()) {
//			   // 创建文件夹
//			   save.getParentFile().mkdirs();
//			  }
			  
//			  if(save.exists()){
//			   save.delete();
//			   save =  new File(CBuildConfig.picUrlStr+uuid);
//			  }
			        try {
			   baos.writeTo(new FileOutputStream(save));
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
			        try {
			   isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中 
			   pic = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
			  } catch (OutOfMemoryError e) {
				  e.printStackTrace();
			  }
			        finally{
			        	if(baos!=null){
			        		try {
								baos.close();
								baos=null;
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        	if(isBm!=null){
			        		try {
								isBm.close();
								isBm=null;
							} catch (IOException e) {
								e.printStackTrace();
							}
			        	}
			        }
			        return pic;
	 }
	
	
	/**
	 * 根据路径删除图片
	 * 
	 * @param path
	 */
	public static void deleteTempFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}
	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getAlbumDir() {
		File dir = new File(
				Environment.getExternalStorageDirectory(),
				getAlbumName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	/**
	 * 获取保存 隐患检查的图片文件夹名称
	 * 
	 * @return
	 */
	public static String getAlbumName() {
		return "yiqi";
	}
}
