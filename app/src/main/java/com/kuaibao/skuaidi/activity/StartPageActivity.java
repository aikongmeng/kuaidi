package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StartPageActivity extends RxRetrofitBaseActivity {

	//private static final String TAG = MainActivity.class.getSimpleName();
	private WebView webview;
	private Button clearCache;
	private Activity activity;
	private WebChromeClient wcc = null;
	private static final String APP_CACAHE_DIRNAME = "/webcache";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.start_page_activity);

		activity = this;
		webview = (WebView) findViewById(R.id.webview);

		clearCache = (Button) findViewById(R.id.clearCache);
		clearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearWebViewCache();
			}
		});
		setWebviewTitle();
		webView();
	}

	/**
	 * @Title: setWebviewTitle
	 * @Description: 获取url的title设置在标题上
	 * @param
	 * @return void
	 */
	private void setWebviewTitle() {
		wcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				// tv_title_des.setText(title);
			}

		};
	}

	private void webView() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networInfo = cm.getActiveNetworkInfo();

		webview.loadUrl("http://m.kuaidihelp.com/app/anime");
		WebSettings webSettings = webview.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);// 设置支持javascript脚本
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
		webSettings.setSupportZoom(false);// 支持缩放
		webSettings.setGeolocationEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);// 提高渲染的优先级

		if (networInfo == null || !networInfo.isAvailable()) {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		// 缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT，无网络时，使用LOAD_CACHE_ELSE_NETWORK
		// if (Utility.isNetworkConnected() == true) {
		// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//
		// 缓存模式【根据cache-control决定是否从网络上取数据。】
		// }else{
		// webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//
		// 缓存模式【只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。】
		// }

		webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
		 */
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSettings.setUseWideViewPort(true);
		// 开启 DOM storage API 功能 【最重要的方法，一定要设置，这就是地图出不来的主要原因】
		webSettings.setDomStorageEnabled(true);
		// 开启 database storage API 功能 【启用数据库】
		webSettings.setDatabaseEnabled(true);
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();

		String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
		// 设置数据库缓存路径
		webSettings.setDatabasePath(cacheDirPath);
		// 设置 Application Caches 缓存目录
		webSettings.setAppCachePath(cacheDirPath);
		// 开启 Application Caches 功能
		webSettings.setAppCacheEnabled(true);
		// 启用地理定位
		webSettings.setGeolocationEnabled(true);
		// 设置定位的数据库路径
		webSettings.setGeolocationDatabasePath(dir);

		/**
		 * 如果需要监视加载进度的，需要创建一个自己的WebChromeClient类，
		 * 并重载方法onProgressChanged，再webview.setWebChromeClient(new
		 * MyWebChromeClient())即可
		 */
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle("Loading...");
				activity.setProgress(progress * 100);
				if (progress == 100)
					activity.setTitle(R.string.app_name);
			}
		});

		webview.setWebChromeClient(wcc);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { // Handle
																												// the
																												// error
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.substring(0, 4).equals("tel:")) {// 如果字符串从第一个字符到第4个字符是“tel：”说明是拨打电话手机号
					final String telPhone = url.substring(4, url.length());// 获取"tel:"字符串后面的手机所有字符
					SkuaidiDialog dialog = new SkuaidiDialog(activity);
					dialog.setTitle("提示");
					dialog.setContent("你确认要向" + telPhone + "拨打电话吗？");
					dialog.setPositionButtonTitle("取消");
					dialog.setNegativeButtonTitle("确认");
					dialog.isUseEditText(false);
					dialog.showDialog();
					dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
						@Override
						public void onClick() {
							// 拨打电话
//							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telPhone));
//							startActivity(intent);
							AcitivityTransUtil.showChooseTeleTypeDialog(StartPageActivity.this, "", telPhone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
						}
					});
				} else if (url.substring(0, 4).equals("sms:")) {// 如果字符串第一个字符到第4个字符是“sms:”说明是发送短信的手机号
					final String smsPhone = url.substring(4, url.length());// 获取"sms:"字符串后面的手机所有字符
					// 发送短信
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + smsPhone));
					activity.startActivity(sendIntent);
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
	}

//	class MyWebClient extends WebViewClient {
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			view.loadUrl(url);
//			return true;
//		}
//
//		@Override
//		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//			if (!"get".equals(request.getMethod().toLowerCase())) {
//				return super.shouldInterceptRequest(view, request);
//			}
//			String url = request.getUrl().toString();
//			// todo:计算url的hash
//			String md5URL = YUtils.md5(url);
//			// 读取缓存的html页面
//			File file = new File(appCacheDir + File.separator + md5URL);
//			if (file.exists()) {
//
//				FileInputStream fileInputStream = null;
//				try {
//					fileInputStream = new FileInputStream(file);
//					//Log.e(">>>>>>>>>", "读缓存");
//					return new WebResourceResponse(YUtils.readBlock(fileInputStream), YUtils.readBlock(fileInputStream), fileInputStream);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//			try {
//				URL uri = new URL(url);
//				URLConnection connection = uri.openConnection();
//				InputStream uristream = connection.getInputStream();
//				String cache = connection.getHeaderField("Ddbuild-Cache");
//				String contentType = connection.getContentType();
//				// text/html; charset=utf-8
//				String mimeType = "";
//				String encoding = "";
//				if (contentType != null && !"".equals(contentType)) {
//					if (contentType.indexOf(";") != -1) {
//						String[] args = contentType.split(";");
//						mimeType = args[0];
//						String[] args2 = args[1].trim().split("=");
//						if (args.length == 2 && args2[0].trim().toLowerCase().equals("charset")) {
//							encoding = args2[1].trim();
//						} else {
//							encoding = "utf-8";
//						}
//					} else {
//						mimeType = contentType;
//						encoding = "utf-8";
//					}
//				}
//				if ("1".equals(cache)) {
//					// todo:缓存uristream
//					FileOutputStream output = new FileOutputStream(file);
//					int read_len;
//					byte[] buffer = new byte[1024];
//					YUtils.writeBlock(output, mimeType);
//					YUtils.writeBlock(output, encoding);
//					while ((read_len = uristream.read(buffer)) > 0) {
//						output.write(buffer, 0, read_len);
//					}
//					output.close();
//					uristream.close();
//					FileInputStream fileInputStream = new FileInputStream(file);
//					YUtils.readBlock(fileInputStream);
//					YUtils.readBlock(fileInputStream);
//					//Log.e(">>>>>>>>>", "读缓存");
//					return new WebResourceResponse(mimeType, encoding, fileInputStream);
//				} else {
//					//Log.e(">>>>>>>>>", "网络加载");
//					return new WebResourceResponse(mimeType, encoding, uristream);
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//	} 
	// 这里面读写操作比较多，还有截取那两个属性的字符串稍微有点麻烦

	/**
	 * * int转byte * by黄海杰 at：2015-10-29 16:15:06 *
	 * 
	 * @param iSource
	 *            *
	 * @param iArrayLen
	 *            *
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/** * byte转int * by黄海杰 at：2015-10-29 16:14:37 * @param bRefArr * @return */
	// 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
	public static int toInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	/**
	 * * 写入JS相关文件 * by黄海杰 at:2015-10-29 16:14:01 * @param output * @param str
	 */
	public static void writeBlock(OutputStream output, String str) {
		try {
			byte[] buffer = str.getBytes("utf-8");
			int len = buffer.length;
			byte[] len_buffer = toByteArray(len, 4);
			output.write(len_buffer);
			output.write(buffer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** * 读取JS相关文件 * by黄海杰 at:2015-10-29 16:14:19 * @param input * @return */
	public static String readBlock(InputStream input) {
		try {
			byte[] len_buffer = new byte[4];
			input.read(len_buffer);
			int len = toInt(len_buffer);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int read_len = 0;
			byte[] buffer = new byte[len];
			while ((read_len = input.read(buffer)) > 0) {
				len -= read_len;
				output.write(buffer, 0, read_len);
				if (len <= 0) {
					break;
				}
			}
			buffer = output.toByteArray();
			output.close();
			return new String(buffer, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	} 
	// 为了加密我们的html我们把url转成md5

	/** * 字符串转MD5 * by黄海杰 at:2015-10-29 16:15:32 * @param string * @return */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

//	}}}}

	/**
	 * 如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身， 如果希望浏览的网
	 * 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 清除WebView缓存
	 */
	public void clearWebViewCache() {

		// 清理Webview缓存数据库
		try {
			deleteDatabase("webview.db");
			deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WebView 缓存文件
		File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
		//Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

		File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
		//Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

		// 删除webview 缓存目录
		if (webviewCacheDir.exists()) {
			deleteFile(webviewCacheDir);
		}
		// 删除webview 缓存 缓存目录
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}
	}

	/**
	 * 递归删除 文件/文件夹
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {

		//Log.i(TAG, "delete file path=" + file.getAbsolutePath());

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			//Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
		}
	}
}
