package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.PickAndSendMapActivity;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒋健 我的二维码名片
 * 
 */
@SuppressLint("HandlerLeak")
public class QrcodeCardActivity extends SkuaiDiBaseActivity {
	private ImageView iv;
	private Button bt_share;
	private Button bt1;
	private Button bt2;
	private Button bt3;
	private Button bt4;
	private Bitmap bitmap;
	private String url;
	private TextView tv_title_des;
	private TextView tv_more_user_name;
	private TextView tv_fangwen_num,tv_shoucang_num,tv_description;
	private ProgressDialog progressDialog;
	
	//private CircularImage ci_user_photo;//头像
	//private File headerFile;
	private Context context;
	private ImageView imv_defalt_head;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.TYPE_AUDIT_SUCCESS:
				String status_result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(status_result);
					JSONObject response = json.getJSONObject("response");
					JSONObject body = response.getJSONObject("body");
					JSONObject desc = body.getJSONObject("desc");
					String status = desc.getString("status");
					if (status.equals("pass")) {
						File file = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/skuaidi/qrcodecard");
						if (!file.exists()) {
							file.mkdirs();
						}
						File file_bitmap = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/skuaidi/qrcodecard/", "qrcodecard_"
								+ SkuaidiSpf.getLoginUser().getUserId()
								+ ".png");
						try {
							FileOutputStream out = new FileOutputStream(
									file_bitmap);
							bitmap.compress(CompressFormat.PNG, 100, out);
							out.flush();
							out.close();
							Toast.makeText(QrcodeCardActivity.this,
									"图片已保存至/sdcard/skuaidi/qrcodecard/文件夹",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
						}
					} else {
						UtilToolkit.showToast(
								"抱歉！你还没有通过审核，请通过审核后下载二维码");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			case Constants.SHORT_URL_SUCCESS:
				progressDialog.dismiss();
				String result = msg.obj.toString();
				bt_share.setVisibility(View.VISIBLE);
				bt1.setVisibility(View.VISIBLE);
				bt2.setVisibility(View.VISIBLE);
				bt3.setVisibility(View.VISIBLE);
				bt4.setVisibility(View.VISIBLE);
				try {
					JSONObject json = new JSONObject(result);
					JSONObject response = json.getJSONObject("response");
					JSONObject body = response.getJSONObject("body");
					JSONObject url_result = body.getJSONObject("result");
					url = url_result.getString("short_url");
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					bitmap = BitmapUtil.Create2DCode(context,url);
					iv.setImageBitmap(bitmap);
				} catch (WriterException e1) {
					e1.printStackTrace();
				}
				break;

			case Constants.SHORT_URL_FAIL:
				progressDialog.dismiss();
				bt_share.setVisibility(View.VISIBLE);
				bt1.setVisibility(View.VISIBLE);
				bt2.setVisibility(View.VISIBLE);
				bt3.setVisibility(View.VISIBLE);
				bt4.setVisibility(View.VISIBLE);
				try {
					bitmap = BitmapUtil.Create2DCode(context,url);
					iv.setImageBitmap(bitmap);
				} catch (WriterException e) {
					e.printStackTrace();
				}
				break;

			case Constants.TYPE_AUDIT_FAIL:
				UtilToolkit.showToast( "数据异常");
				break;

			case Constants.PARSE_VISIT_CARD_OK://二维码名片访问量与收藏量
				//设置访问量和收藏量
				tv_fangwen_num.setText(SkuaidiSpf.getFangwen(context));
				tv_shoucang_num.setText(SkuaidiSpf.getShoucang(context));
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.qrcodecard);
		
		context = this;
		
		findView();


		if(Utility.isNetworkConnected() == true){
			progressDialog = ProgressDialog
					.show(QrcodeCardActivity.this, "", "加载中");
			progressDialog.setCancelable(true);	
		}else {
			UtilToolkit.showToast("请设置网络");
		}
		
		KuaidiApi.getshorturl(this, handler,
				"http://m.kuaidihelp.com/wduser/page?cm_phone="
						+ SkuaidiSpf.getLoginUser().getPhoneNumber());
	}

	@Override
	protected void onStart() {
		super.onStart();
		setData();
	}

	private void setData(){
		//设置用户名字
		tv_more_user_name.setText(SkuaidiSpf.getLoginUser().getUserName());
		//设置用户头像
//		headerFile = new File(Constants.HEADER_PATH, "counterman_"
//				+ SkuaidiSpf.getLoginUser().getUserId() + ".jpg");
//		if (headerFile.exists()) {
//			ci_user_photo.setVisibility(View.VISIBLE);
//			Bitmap headerBm = BitmapFactory.decodeFile(Constants.HEADER_PATH
//					+ "counterman_"
//					+ SkuaidiSpf.getLoginUser().getUserId() + ".jpg");
//
//			ci_user_photo.setImageBitmap(headerBm);
//		} else {
//			ci_user_photo.setVisibility(View.GONE);
//		}
		String headUrl=Constants.URL_HEADER_ROOT+ "counterman_" + SkuaidiSpf.getLoginUser().getUserId() + ".jpg";
		GlideUtil.GlideCircleImg(QrcodeCardActivity.this,
				headUrl,imv_defalt_head,
				R.drawable.icon_yonghu,
				R.drawable.icon_yonghu);
		//设置访问量和收藏量
		tv_fangwen_num.setText(SkuaidiSpf.getFangwen(context));
		tv_shoucang_num.setText(SkuaidiSpf.getShoucang(context));
	}

	private void findView() {
		url = "http://m.kuaidihelp.com/wduser/page?cm_phone="
				+ SkuaidiSpf.getLoginUser()
						.getPhoneNumber();
		iv = (ImageView) findViewById(R.id.iv_qrcodecard);
		bt_share = (Button) findViewById(R.id.bt_title_more);
		bt1 = (Button) findViewById(R.id.bt_qrcodecard1);
		bt2 = (Button) findViewById(R.id.bt_qrcodecard2);
		bt3 = (Button) findViewById(R.id.bt_qrcodecard3);
		bt4 = (Button) findViewById(R.id.bt_qrcodecard4);
		tv_more_user_name = (TextView) findViewById(R.id.tv_more_user_name);
		tv_fangwen_num = (TextView) findViewById(R.id.tv_fangwen_num);
		tv_shoucang_num = (TextView) findViewById(R.id.tv_shoucang_num);
		tv_description = (TextView) findViewById(R.id.tv_description);
		imv_defalt_head = (ImageView) findViewById(R.id.imv_defalt_head);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_description.setText(Utility.getUnderLineSpan(context, "支付及补贴活动说明", 0, 9));
		tv_title_des.setText("我的二维码名片");
		bt_share.setText("分享");

		bt_share.setOnClickListener(new MyOnClickListener());
		tv_description.setOnClickListener(new MyOnClickListener());
	}


	public void back(View view) {
		finish();
	}

	
	/**
	 * 我的服务说明click事件
	 * @param view
	 */
	public void service_description(View view){
		UMShareManager.onEvent(context, "qrcodeCard_theServiceDescription", "qrcodeCard", "我的服务说明");
		Intent intent = new Intent();
		intent.setClass(this, ServiceStateActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 添加派送范围click事件
	 * @param view
	 */
	public void delivery_range(View view){
		UMShareManager.onEvent(context, "qrcodeCard_sendingRange", "qrcodeCard", "我的取派范围");
		Intent intent = new Intent();
		intent.setClass(this, PickAndSendMapActivity.class);
		startActivity(intent);
	}

	public void qrcodeinfo(View view) {
		UMShareManager.onEvent(context, "qrcodeCard_qrcodeinfo", "qrcodeCard", "预览二维码名片");
		Intent intent = new Intent();
		intent.putExtra("url", url);
		intent.setClass(this, LoadQrcodeCardActivity.class);
		startActivity(intent);
	}

	public void download_qrcode(View view) {
		UMShareManager.onEvent(context, "qrcodeCard_download_qrcode", "qrcodeCard", "下载二维码");
		// 审核状态
		KuaidiApi.getAuditstatus(this, handler);
	}

//	public Bitmap Create2DCode(String str) throws WriterException {
//		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//		BitMatrix matrix = new MultiFormatWriter().encode(str,
//				BarcodeFormat.QR_CODE, (int) (getResources()
//						.getDisplayMetrics().density) * 300,
//				(int) (getResources().getDisplayMetrics().density) * 300);
//		int width = matrix.getWidth();
//		int height = matrix.getHeight();
//		// 二维矩阵转为一维像素数组,也就是一直横着排了
//		int[] pixels = new int[width * height];
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				if (matrix.get(x, y)) {
//					pixels[y * width + x] = 0xff000000;
//				} else {
//					pixels[y * width + x] = 0xffffffff;
//				}
//
//			}
//		}
//
//		Bitmap bitmap = Bitmap.createBitmap(width, height,
//				Bitmap.Config.ARGB_8888);
//		// 通过像素数组生成bitmap,具体参考api
//		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bitmap;
//	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		KuaidiApi.getQrcodeCardFangwenShoucang(context, handler);// 读取用户二维码收藏访问量
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_description:
				UMShareManager.onEvent(context, "qrcodeCard_huodongDescription", "qrcodeCard", "支付及补贴活动说明");
				Intent mIntent = new Intent(context,HuodongDescriptionActivity.class);
				mIntent.putExtra("url", Constants.ACTIVITY_EXPLAIN);
				context.startActivity(mIntent);
				break;
			case R.id.bt_title_more:
				UMShareManager.onEvent(context, "qrcodeCard_share", "qrcodeCard", "二维码名片分享");
				String title = "亲，扫二维码，就可给我下单，在线支付快递费立减5元！";
				String shareText = "我正在使用高端大气上档次的快递员专属二维码名片，不用打电话，不用发短信，直接扫下面的二维码，或者戳"
						+ url
						+ " ，就可以给我下订单，还可以和我在线沟通。下载“快递员”App，创建属于你自己的二维码名片 http://ckd.so/2";
				String targetUrl = url;
				Map<String, String> shareTexts = new HashMap<String, String>();
				shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
				
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText);
				openShare(title, shareTexts, targetUrl,R.drawable.share);
				break;
			default:
				break;
			}
			
		}
		
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		
	}


	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if(code.equals("7") && null != result){
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
