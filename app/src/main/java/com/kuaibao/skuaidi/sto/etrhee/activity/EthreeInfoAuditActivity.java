package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EthreeInfoAuditActivity extends RxRetrofitBaseActivity {
	private List<String> deliver_list = new ArrayList<String>();
	private int deliver_index = 1;
	private int deliver_again = 0;
	private TextView tv_title;
	private LinearLayout ll_deliver_one;
	private TextView tv_deliver_one;
	private LinearLayout ll_deliver_two;
	private TextView tv_deliver_two;
	private LinearLayout ll_deliver_three;
	private TextView tv_deliver_three;
	private LinearLayout ll_deliver_four;
	private TextView tv_deliver_four;
	private LinearLayout ll_deliver_five;
	private TextView tv_deliver_five;
	private Button bt_deliver;
	private TextView tv_deliver_hint;
	private ImageView iv_camera;
	private Bitmap bitmap;
	private File file;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.ethreeinfoaudit);

		getControl();

		setData();
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
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Constants.TYPE_DELIVER) {
			String decodestr = data.getStringExtra("decodestr");
			if (decodehas(decodestr)) {
				if (deliver_index == 1 || deliver_again == 1) {
					tv_deliver_one.setText(decodestr);
					if (deliver_again == 1) {
						deliver_list.set(0, decodestr);
					} else {
						tv_deliver_hint.setVisibility(View.VISIBLE);
						tv_deliver_hint.setText("你还需扫描4个单号,就可以完成了啦~");
						ll_deliver_one.setVisibility(View.VISIBLE);
						tv_deliver_hint.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 2 || deliver_again == 2) {
					tv_deliver_two.setText(decodestr);
					if (deliver_again == 2) {
						deliver_list.set(1, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描3个单号,就可以完成了啦~");
						ll_deliver_two.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 3 || deliver_again == 3) {
					tv_deliver_three.setText(decodestr);
					if (deliver_again == 3) {
						deliver_list.set(2, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描2个单号,就可以完成了啦~");
						ll_deliver_three.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 4 || deliver_again == 4) {
					tv_deliver_four.setText(decodestr);
					if (deliver_again == 4) {
						deliver_list.set(3, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描1个单号,就可以完成了啦~");
						ll_deliver_four.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 5 || deliver_again == 5) {
					tv_deliver_five.setText(decodestr);
					if (deliver_again == 5) {
						deliver_list.set(4, decodestr);
					} else {
						tv_deliver_hint.setText("恭喜,派件单号已扫描完成!");
						tv_deliver_hint.setTextColor(getResources().getColor(
								R.color.text_yes2));
						ll_deliver_five.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						bt_deliver.setText("提交");
					}
				}
			} else {
				UtilToolkit.showToast("该单号已存在!");
			}
		} else if (requestCode == Constants.PHOTO_REQUEST_TAKEPHOTO) {
			startPhotoZoom(Uri.fromFile(file), ((int) getResources()
					.getDisplayMetrics().density) * 220);
		} else if (requestCode == Constants.PHOTO_REQUEST_CUT) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					bitmap = bundle.getParcelable("data");
					iv_camera.setImageBitmap(bitmap);
					if (iv_camera.getVisibility() == View.GONE) {
						iv_camera.setVisibility(View.VISIBLE);
						bt_deliver.setEnabled(true);
					}
				}
			}
		}
	}

	public void getControl() {
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		ll_deliver_one = (LinearLayout) findViewById(R.id.ll_ethreeaudit_deliver1);
		tv_deliver_one = (TextView) findViewById(R.id.tv_ethreeaudit_deliver1);
		ll_deliver_two = (LinearLayout) findViewById(R.id.ll_ethreeaudit_deliver2);
		tv_deliver_two = (TextView) findViewById(R.id.tv_ethreeaudit_deliver2);
		ll_deliver_three = (LinearLayout) findViewById(R.id.ll_ethreeaudit_deliver3);
		tv_deliver_three = (TextView) findViewById(R.id.tv_ethreeaudit_deliver3);
		ll_deliver_four = (LinearLayout) findViewById(R.id.ll_ethreeaudit_deliver4);
		tv_deliver_four = (TextView) findViewById(R.id.tv_ethreeaudit_deliver4);
		ll_deliver_five = (LinearLayout) findViewById(R.id.ll_ethreeaudit_deliver5);
		tv_deliver_five = (TextView) findViewById(R.id.tv_ethreeaudit_deliver5);
		bt_deliver = (Button) findViewById(R.id.bt_ethreeaudit_deliver);
		tv_deliver_hint = (TextView) findViewById(R.id.tv_ethreeaudit_hint_deliver);
		iv_camera = (ImageView) findViewById(R.id.iv_ethreeinfoaudit_camera);
	}

	public void setData() {
		tv_title.setText("审核E3个人信息");
	}

	// 派件扫描
	public void deliverscan(View view) {
		if (bt_deliver.getText().toString().equals("提交")) {
			String no = "";
			for (int i = 0; i < deliver_list.size(); i++) {
				no += "|" + deliver_list.get(i);
			}
			no = no.substring(1);
			Bitmap bitmap1 =null ;// BitmapFactory.decodeFile(Constants.TEMP_HEADER_PATH + "e3audit.jpg");
			JSONObject data = new JSONObject();
			UserInfo userInfo=SkuaidiSpf.getLoginUser();
			try {
				data.put("pname", "androids");
				data.put("sname", "express.add");
				data.put("brand", userInfo.getExpressNo());
				data.put("no", no);
				data.put("id", userInfo.getUserId());
				data.put("pic", StringUtil.bitmapToString(bitmap1, 10));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			showProgressDialog("");//EthreeInfoAuditActivity.this,"");
			HttpHelper httpHelper = new HttpHelper(
					new OnResultListener() {

						@Override
						public void onSuccess(String result, String sname) {
							dismissProgressDialog();//EthreeInfoAuditActivity.this);
							try {
								JSONObject json = new JSONObject(result);
								JSONObject data = json.getJSONObject("data");
								String status = data.getString("status");
								if (status.equals("unaudited")
										|| status.equals("reject")) {
									// 未通过
									UtilToolkit.showToast("审核失败,请扫描自己的派件单号");
								} else if (status.equals("apply")
										|| status.equals("pass")) {
									// 通过
									String name = data.optString("name");
									String wangdian = data
											.optString("wangdian");
									if (!name.equals("")) {
										SharedPreferences sp = getSharedPreferences(
												"config", MODE_PRIVATE);
										Editor editor = sp.edit();
										editor.putString("user_e3_name", name);
										editor.putString("user_e3_brandname",
												wangdian);
										editor.commit();
										UtilToolkit.showToast("审核通过");
									} else {
										UtilToolkit.showToast("你已提交过审核");
									}
									finish();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								UtilToolkit.showToast("审核失败");
							}
						}

						@Override
						public void onFail(String result, JSONObject data_fail, String code) {
							dismissProgressDialog();//EthreeInfoAuditActivity.this);
						}
					},handler);
			httpHelper.getPart(data);
		} else {
			deliver_again = 0;
			Intent intent = new Intent();
			intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
			intent.setClass(this, CaptureActivity.class);
			startActivityForResult(intent, 1);
		}
	}

	// 重新扫描
	public void deliver_scan1(View view) {
		deliver_again = 1;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	// 重新扫描
	public void deliver_scan2(View view) {
		deliver_again = 2;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	// 重新扫描
	public void deliver_scan3(View view) {
		deliver_again = 3;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	// 重新扫描
	public void deliver_scan4(View view) {
		deliver_again = 4;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	// 重新扫描
	public void deliver_scan5(View view) {
		deliver_again = 5;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	// 相机
//	public void camera(View view) {
//		File fileName = new File(Constants.TEMP_HEADER_PATH);
//		if (!fileName.exists()) {
//			fileName.mkdirs();
//		}
//		file = new File(fileName, "e3audit.jpg");
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//		startActivityForResult(intent, Constants.PHOTO_REQUEST_TAKEPHOTO);
//	}

	// 裁剪
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Constants.PHOTO_REQUEST_CUT);
	}

	public void back(View view) {
		finish();
	}

	// 判断单号是否重复
	public boolean decodehas(String decode) {
		for (int i = 0; i < deliver_list.size(); i++) {
			if (deliver_list.get(i).equals(decode)) {
				return false;
			}
		}
		return true;
	}
}
