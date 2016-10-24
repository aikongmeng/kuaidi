package com.kuaibao.skuaidi.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

public class NotifyImageShowActivity extends SkuaiDiBaseActivity {

	private ImageView iv_image_show;
	private String from = "";
	private Bitmap imageBitmap;
	private String imagePath = "";

	private Bitmap image;
	private int degree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.notify_image_show_activity);

		initView();
		initData();
	}

	private void initView() {
		iv_image_show = (ImageView) findViewById(R.id.iv_image_show);
	}

	private void initData() {
		from = getIntent().getStringExtra("from");
		if (from.equals("notifyDetailActivity_iv_forensics")) {
			imageBitmap = (Bitmap) SKuaidiApplication.getInstance().onReceiveMsg("NotifyDetailActivity", "bitmap_show");
			iv_image_show.setImageBitmap(imageBitmap);
		} else if (from.equals("liuyanDetail_iv_image_content_send")) {
			imagePath = getIntent().getStringExtra("image");
			// 将图片转成bitmap
			image = BitmapUtil.getLoacalBitmap(imagePath);
			degree = BitmapUtil.readPictureDegree(imagePath);
			imageBitmap = BitmapUtil.rotaingImageView(degree, image);
			iv_image_show.setImageBitmap(imageBitmap);
			
			
		}else if(from.equals("liuyanDetail_iv_image_content_receive")){
			imagePath = getIntent().getStringExtra("image");
//			DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
//					.bitmapConfig(Config.RGB_565).build();
//			ImageLoader.getInstance().displayImage(imagePath, iv_image_show, imageOptions);
			GlideUtil.GlideUrlToImg(NotifyImageShowActivity.this,imagePath, iv_image_show);
		}
		// Bitmap imageBitmap;
		// imageBitmap = getIntent().getParcelableExtra("image");
		iv_image_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if (code.equals("7") && null != result) {
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
