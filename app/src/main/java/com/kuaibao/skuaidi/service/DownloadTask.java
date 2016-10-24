package com.kuaibao.skuaidi.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;

import com.kuaibao.skuaidi.business.nettelephone.calllog.utils.NewPlayer;
import com.kuaibao.skuaidi.util.Constants;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTask {
	// 下載文件
	public static void downloadFile(final Handler handler,
			String urlRecordRoot, final String RecordPath, final String fileName) {
		
		String url = urlRecordRoot + fileName;
		String localAddr = Constants.ROOT+"/skuaidi/voice"+fileName;
		//System.out.println(url);
		File f = new File(RecordPath);
		if(!f.exists()){
			f.mkdirs();
		}
		
		long size = 0;// 文件大小 
		final File file = new File(RecordPath, fileName);
		try {
			if(file.exists()){// 判断文件是否存在
				if(file.isFile()){// 判断是否是文件
					FileInputStream fis = new FileInputStream(file);
					size = fis.available();// 获取文件大小
					fis.close();// 关闭输入流
					if(size<=0){// 如果文件大小小于0
						file.delete();// 删除文件
					}
					downloadFile(url,localAddr);
				}
			}else {
//				file.getParentFile().mkdirs();
				try {
					file.createNewFile();
					downloadFile(url,localAddr);
					/**
					 * 网络地址 和保存的文件路径
					 */
//					FinalHttp finalHttp = new FinalHttp();
//					finalHttp.download(url, file.getAbsolutePath(),
//							new AjaxCallBack<File>() {
//						@Override
//						public void onFailure(Throwable t, int errorNo,
//								String strMsg) {
//							super.onFailure(t, errorNo, strMsg);
//							//System.out.println("gudd 下载失败");
//						}
//						@Override
//						public void onSuccess(File t) {
//							if (handler != null) {
//								Message msg = new Message();
//								msg.what = Constants.DOWNLOAD_OK;
//								handler.sendMessage(msg);
//							}
//							//System.out.println("gudd 下载成功");
//							super.onSuccess(t);
//						}
//							});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		if (file.exists() && f.length() < 1) {
//			file.delete();
//		}
		

	}
	
	public static void downloadFile(String downloadLink,String localAddress){
		try {
			FinalHttp fh = new FinalHttp();
			HttpHandler<File> handler = fh.download(downloadLink, localAddress, new AjaxCallBack<File>() {
				@Override
				public void onStart() {
					super.onStart();
					//System.out.println("gudd 开始下载");
				}
				@Override
				public void onLoading(long count, long current) {
					super.onLoading(count, current);
				}
				@Override
				public void onSuccess(File t) {
					super.onSuccess(t);
					//System.out.println("gudd 下载成功");
				}
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					//System.out.println("gudd 开始失败");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @param handler
	 * @param urlRecordRoot
	 *            图片网址
	 * @param RecordPath
	 *            本地存储地址
	 * @param fileName
	 *            文件名
	 */
	public static void downloadImage(final Handler handler, String urlRecordRoot,final String RecordPath,final String fileName){
		Bitmap mBitmap = null;
		String url = urlRecordRoot+fileName;
		final File file = new File(RecordPath, fileName);
		
		try {
			URL url2 = new URL(url);
				HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.connect();
			InputStream in = urlConnection.getInputStream();
			mBitmap = BitmapFactory.decodeStream(in);
			FileOutputStream out = new FileOutputStream(file.getPath());
			mBitmap.compress(Bitmap.CompressFormat.JPEG	, 100, out);
			out.flush();
			out.close();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * ------------------------------------播放录音--------------------------
	 **/
	public static void playLocalVoice(String localPath) {
		NewPlayer.getInstance().playUrlOrPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/skuaidi/voice/" +localPath,0);
	}
	/**停止播放**/
	public static void stopPlayLocalVoice(){
		NewPlayer.stop();
		NewPlayer.releaseMediaPlayer();
		NewPlayer.destoryInstance();
	}
}
