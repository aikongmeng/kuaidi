package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.EditTextWithTitle;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全峰发件录单
 * 
 * @author a4
 * 
 */
public class OrderTypeInActivity extends SkuaiDiBaseActivity {
	// private EditTextWithTitle et_send_site;
	// private EditTextWithTitle et_type_in_person;
	// private EditTextWithTitle et_register_site;
	// private EditTextWithTitle et_dispatch_site;
	private EditTextWithTitle et_person_send;
	private EditTextWithTitle et_address_send;
	private EditTextWithTitle et_phone_send;
	private EditTextWithTitle et_receiver;
	private EditTextWithTitle et_address_receiver;
	private EditTextWithTitle et_phone_receiver;
	private EditTextWithTitle et_weight;
	private EditTextWithTitle et_freight;
	private EditTextWithTitle et_topayment;
	/** 寄件网点 */
	private String send_site;
	/** 录入人 */
	private String type_in_person;
	/** 录入网点 */
	private String register_site;
	/** 派件网点 */
	private String dispatch_site;
	private String person_send;
	private String address_send;
	private String phone_send;
	private String receiver;
	private String address_receiver;
	private String phone_receiver;
	private String settlement_weight;
	private String freight;
	private String topayment;
	private Context context;
	protected boolean isRequesting = false;
	private String orderNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_order_type_in);
		context = this;
		getOrderInfo();
		findView();
	}

	private void getOrderInfo() {
		NotifyInfo info = (NotifyInfo) getIntent().getSerializableExtra("NotifyInfo");
		orderNumber = info.getExpress_number();
	}

	private void findView() {

		// et_send_site = (EditTextWithTitle) findViewById(R.id.send_site);
		// et_type_in_person = (EditTextWithTitle)
		// findViewById(R.id.type_in_person);
		// et_register_site = (EditTextWithTitle)
		// findViewById(R.id.register_site);
		// et_dispatch_site = (EditTextWithTitle)
		// findViewById(R.id.dispatch_site);
		et_person_send = (EditTextWithTitle) findViewById(R.id.person_send);
		et_address_send = (EditTextWithTitle) findViewById(R.id.address_send);
		et_phone_send = (EditTextWithTitle) findViewById(R.id.phone_send);
		et_receiver = (EditTextWithTitle) findViewById(R.id.receiver);
		et_address_receiver = (EditTextWithTitle) findViewById(R.id.address_receiver);
		et_phone_receiver = (EditTextWithTitle) findViewById(R.id.phone_receiver);
		et_weight = (EditTextWithTitle) findViewById(R.id.weight);
		et_freight = (EditTextWithTitle) findViewById(R.id.freight);
		et_topayment = (EditTextWithTitle) findViewById(R.id.topayment);

		TextView tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_title.setText(orderNumber);

		Button btn_save = (Button) findViewById(R.id.bt_title_more);
		btn_save.setVisibility(View.VISIBLE);
		btn_save.setText("上传");

//		List<CourierReviewInfo> list;
//		list = finalDb.findAllByWhere(CourierReviewInfo.class, "courierPhone = '"
//				+ SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
		//if (list != null && list.size() != 0) {
			CourierReviewInfo reviewInfo = E3SysManager.getReviewInfo();

			// et_send_site.setContent(reviewInfo.getCourierLatticePoint());
			// et_register_site.setContent(reviewInfo.getCourierLatticePoint());
			// et_type_in_person.setContent(reviewInfo.getCourierName());
			if(reviewInfo!=null){
				send_site = reviewInfo.getCourierLatticePoint();
				type_in_person = reviewInfo.getCourierName();
			}
			register_site = send_site;

		//}

		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// send_site = et_send_site.getContent();
				// type_in_person = et_type_in_person.getContent();
				// register_site = et_register_site.getContent();
				// dispatch_site = et_dispatch_site.getContent();
				UMShareManager.onEvent(context, "upload_order", "order_typein", "录单：上传订单信息");
				person_send = et_person_send.getContent();
				address_send = et_address_send.getContent();
				phone_send = et_phone_send.getContent();
				receiver = et_receiver.getContent();
				address_receiver = et_address_receiver.getContent();
				phone_receiver = et_phone_receiver.getContent();
				settlement_weight = et_weight.getContent();
				freight = et_freight.getContent();
				topayment = et_topayment.getContent();

				if (!Utility.isNetworkConnected()) {// 无网络
					UtilToolkit.showToast("请检查网络设置！");
					return;
				}
				if (isRequesting) {
					UtilToolkit.showToast("正在上传，请勿重复提交！");
					return;
				}
				uploadOrder();

			}

			private void uploadOrder() {
				isRequesting = true;

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());
				String send_date = formatter.format(curDate);

				JSONObject json = new JSONObject();

				try {
					json.put("BILL_CODE", orderNumber);// 运单编号
					json.put("SEND_DATE", send_date);// 寄件日期
					json.put("SEND_SITE", send_site);// 寄件网点
					json.put("REGISTER_MAN", type_in_person);// 录入人 可以为空
					json.put("REGISTER_SITE", register_site);// 录入网点

					json.put("ACCEPT_MAN", receiver);// 收件人
					json.put("ACCEPT_MAN_PHONE", phone_receiver);// 收件人电话
					json.put("ACCEPT_MAN_ADDRESS", address_receiver);// 收件人地址

					json.put("SETTLEMENT_WEIGHT", settlement_weight);// 结算重量
					json.put("TOPAYMENT", topayment);// 到付款
					json.put("FREIGHT", freight);// 运费

					json.put("SEND_MAN", person_send);// 寄件人
					json.put("SEND_MAN_PHONE", phone_send);// 寄件人电话
					json.put("SEND_MAN_ADDRESS", address_send);// 寄件人地址

					json.put("TAKE_PIECE_EMPLOYEE", "");// 取件员
					json.put("PIECE_NUMBER", "");// 件数
					json.put("DESTINATION", "");// 目的地
					json.put("DISPATCH_SITE", "");// 派件网点
					json.put("GOODS_PAYMENT", "");// 代收货款
					json.put("CUSTOMER_NAME", "");// 客户名称
					json.put("SEND_MAN_COMPANY", "");// 寄件人公司
					json.put("ACCEPT_MAN_COMPANY", "");// 收件人公司
					json.put("REMARK", "");// 备注
					json.put("BL_RETURN_BILL", "");// 回单标识
					json.put("INSURANCE", "");// 保险费
					json.put("R_BILLCODE", "");// 回单编号
					json.put("BILL_CODE_SUB", "");// 子单号
					json.put("SEND_PROVINCE", "");// 寄件省份
					json.put("SEND_CITY_NAME", "");// 寄件城市
					json.put("SEND_COUNTY_NAME", "");// 寄件区县
					json.put("REC_PROVINCE", "");// 收件省份
					json.put("REC_CITY_NAME", "");// 收件城市
					json.put("REC_COUNTY_NAME", "");// 收件区县

					TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
					String imei = tm.getDeviceId();
					String only_code = Utility.getOnlyCode();

					JSONObject datas = new JSONObject();
					datas.put("dev_id", only_code);
					datas.put("dev_imei", imei);
					datas.put("sname", "qf.handle");
					datas.put("act", "send");
					datas.put("data", json);
					httpInterfaceRequest(datas, false, INTERFACE_VERSION_NEW);

				} catch (Exception e) {
					isRequesting = false;
					e.printStackTrace();
					//Log.w("iii", "data can't cast to jsonObject");
				}

			}
		});

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("qf.handle".equals(sname) && "send".equals(act)) {
			isRequesting = false;
			if (result != null)
				UtilToolkit.showToast(result.optString("retStr"));
			finish();

		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if ("qf.handle".equals(sname) && "send".equals(act)) {
			UtilToolkit.showToast(result);
			isRequesting = false;

		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	public void back(View view) {
		finish();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();

		}

		return super.onKeyDown(keyCode, event);

	}

}
