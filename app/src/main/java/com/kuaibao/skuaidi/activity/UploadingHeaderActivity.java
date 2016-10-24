package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 罗娜
 * 上传头像
 *
 */
public class UploadingHeaderActivity extends RxRetrofitBaseActivity {
	Context context;
	LinearLayout ll_upload_header;
	Button btn_upload_photo,btn_upload_camera,btn_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.uploadingheader);
		context=this;
		
		getControl();
		setListener();
	}

	private void setListener() {
		ll_upload_header.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				return false;
			}
		});
		
		btn_upload_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_UPLOAD_FROM_PHOTO);
				finish();
			}
		});
		
		btn_upload_camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_UPLOAD_FROM_CAMERA);
				finish();
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getControl() {
		ll_upload_header=(LinearLayout) findViewById(R.id.ll_upload_header);
		btn_upload_photo=(Button) findViewById(R.id.btn_upload_photo);
		btn_upload_camera=(Button) findViewById(R.id.btn_upload_camera);
		btn_cancel=(Button) findViewById(R.id.btn_cancel);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	

}
