package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Deliver;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author a16 催件短信
 */
public class HurryOrderActivity extends SkuaiDiBaseActivity {

	private TextView tv_title_des, send_expressNo, send_name, send_address,
			send_phone, tv_send_message; // 提示
	private Button bt_title_more;
	private Context context;
	Deliver deliver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.hurryorder_mode);
		context = this;
		deliver = (Deliver) SKuaidiApplication.getInstance().onReceiveMsg(
				"HurryOrderActivity", "deliver_info");
		getControl();
		
	}

	private void getControl() {
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		tv_title_des.setText("催件短信");
		bt_title_more.setText("发送");
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setOnClickListener(new MyClickListener());
		send_expressNo = (TextView) findViewById(R.id.send_expressNo);
		send_name = (TextView) findViewById(R.id.send_name);
		send_address = (TextView) findViewById(R.id.send_address);
		send_phone = (TextView) findViewById(R.id.send_phone);
		tv_send_message = (TextView) findViewById(R.id.tv_send_message);
		if (deliver != null) {
			send_address.setText(send_address.getText()
					+ deliver.getRecipient_address());
			send_expressNo.setText(send_expressNo.getText()
					+ deliver.getExpress_no());
			
			if (deliver.getRecipient_name() == null
					|| deliver.getRecipient_name().equals("无信息")) {
				deliver.setRecipient_name("");
			}
			send_name
					.setText(send_name.getText() + deliver.getRecipient_name());
			send_phone.setText(send_phone.getText()
					+ deliver.getRecipient_phone());
		}
	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 发短信
			case R.id.bt_title_more:

				// 调接口，发短信
				if (Utility.isNetworkConnected() == false) {
					SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(
							context);
					dialog.setTitleGray("提示");
					dialog.setTitleSkinColor("main_color");
					dialog.setContentGray("您没有连接网络，是否进行设置？");
					dialog.setPositionButtonTextGray("设置");
					dialog.setNegativeButtonTextGray("取消");
					dialog.showDialogGray(bt_title_more);
					dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									android.provider.Settings.ACTION_WIFI_SETTINGS);
							startActivity(intent);
						}
					});
				} else {
					Toast.makeText(context, "短信已发送", Toast.LENGTH_LONG).show();
					JSONObject object = new JSONObject();
					try {
						object.put("sname", "remind.notice");
						object.put("ywy_phone", deliver.getCourier_phone());
						object.put("exp_no", deliver.getExpress_no());
						//Log.i("iii", "kkkkkkkkkk" + deliver.getCourier_phone());
						object.put("rec_name", deliver.getRecipient_name());
						object.put("rec_phone", deliver.getRecipient_phone());
						object.put("rec_address",
								deliver.getRecipient_address());

						httpInterfaceRequest(object, false,
								INTERFACE_VERSION_NEW);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				break;

			default:
				break;
			}

		}

	}

	public void back(View view) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		Toast.makeText(context, "短信已发送", Toast.LENGTH_LONG).show();
		//Log.i("iii", "chengong返回的数据===" + result);
		finish();

	}

	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

		//Log.i("iii", "shibai返回的数据===" + result);

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname,
			String msg, JSONObject result) {
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
