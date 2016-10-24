package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ToastCustom;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.VASInfo;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: VASAddDetailActivity
 * @Description: 添加增值业务详情页
 * @author 顾冬冬
 * @date 2015-11-25 下午8:47:57
 */
public class VASAddDetailActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private Context mContext = null;
	private Intent mIntent = null;
	private ToastCustom toast = null;

	// title
	private SkuaidiImageView back = null;// 返回
	private TextView title = null;// 标题

	// body
	private EditText etBusinessName = null;// 业务名称
	private EditText etPrice = null;// 业务价格
	private EditText etEditDetail = null;// 业务详细
	private Button btnSubmit = null;// 提交按钮

	// 参数
	private int fromEvent = 0;
	private String vasItemId = "";
	private String fromActivity = "";// 从哪个界面而来
	private VASInfo vasInfo = null;
	private String Name = "";// 从列表进来时候保存的名称
	private String Price = "";// 同上（价格）
	private String Detail = "";// 同上（详细）
	private String deliveryPrice = "";// 送货上门费用
	private String deliveryBusinessName = "送货上门";// 送货上门业务名称
	private String deliveryEditDetail = "非电梯房4层以上可以送货上，送到4层5元，每增加一层加1元。\n\n无需送货上门请自提";// 送货上门详情
	private String deliveryCallBusinessName = "送货前联系";// 送货上门提前联系业务名称
	private String deliveryCallEditDetail = "如需送货前联系并在固定时间内送达指定地点需20元。";// 送货上门提前联系详情
	private String deliveryTimingBusinessName = "指定时间送货";// 指定送货时间业务名称
	private String deliveryTimingEditDetail = "晚上20：00-21：00送货10元，21：00后送货需20元，23：00后不送货。";// 指定送货时间详情

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.vas_add_detail_activity);
		mContext = this;
		
		initView();
		getData();
		setData();
		setListener();
	}

	private void initView() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);

		etBusinessName = (EditText) findViewById(R.id.etBusinessName);
		etPrice = (EditText) findViewById(R.id.etPrice);
		etEditDetail = (EditText) findViewById(R.id.etEditDetail);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		back.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);

		btnSubmit.setEnabled(false);

	}

	private void getData() {
		toast = new ToastCustom( mContext, 5, title);
		
		fromEvent = getIntent().getIntExtra("fromEvent", 0);
		fromActivity = getIntent().getStringExtra("fromActivity");
		vasInfo = (VASInfo) getIntent().getSerializableExtra("vasinfo");
	}

	private void setData() {
		if(!Utility.isEmpty(fromActivity) && fromActivity.equals("modifyVasInfo")){
			title.setText("编辑增值业务");
			Name = vasInfo.getVasName();
			Detail = vasInfo.getVasDescription();
			Price = vasInfo.getVasPrice();
			vasItemId = vasInfo.getId();
			
			etBusinessName.setText(Name);
			etPrice.setText(Price);
			etEditDetail.setText(Detail);
			if(!Utility.isEmpty(vasInfo.getVasPrice())){
				btnSubmit.setEnabled(true);
				btnSubmit.setBackgroundResource(R.drawable.selector_base_green_qianse1);
			}
		}else{
			title.setText("添加增值业务");
			if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
				etBusinessName.setHint("送货上门(点击编辑，限6字)");
				etPrice.setHint("请输入参考价格");
				etEditDetail.setHint("非电梯房4层以上可以送货上，送到4层5元，每增加一层加1元。\n\n无需送货上门请自提");
			} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
				etBusinessName.setHint("送货前联系(点击编辑，限6字)");
				etPrice.setHint("请输入参考价格");
				etEditDetail.setHint("如需送货前联系并在固定时间内送达指定地点需20元。");
			} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
				etBusinessName.setHint("指定时间送货(点击编辑，限6字)");
				etPrice.setHint("请输入参考价格");
				etEditDetail.setHint("晚上20：00-21：00送货10元，21：00后送货需20元，23：00后不送货。");
			}
		}
		etBusinessName.setHintTextColor(mContext.getResources().getColor(R.color.gray_3));
		etPrice.setHintTextColor(mContext.getResources().getColor(R.color.gray_3));
		etEditDetail.setHintTextColor(mContext.getResources().getColor(R.color.gray_3));
	}

	private void setListener() {

		etBusinessName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!Utility.isEmpty(arg0.toString())) {
					if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
						deliveryBusinessName = etBusinessName.getText().toString();
					} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
						deliveryCallBusinessName = etBusinessName.getText().toString();
					} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
						deliveryTimingBusinessName = etBusinessName.getText().toString();
					} else {
						Name = etBusinessName.getText().toString();
					}
				}
			}
		});
		etPrice.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
						etPrice.setText(s);
						etPrice.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					etPrice.setText(s);
					etPrice.setSelection(2);
				}

				if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						etPrice.setText(s.subSequence(0, 1));
						etPrice.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				deliveryPrice = etPrice.getText().toString();
				Price = etPrice.getText().toString();
				if (Utility.isEmpty(deliveryPrice)) {
					btnSubmit.setEnabled(false);
					btnSubmit.setBackgroundResource(R.drawable.shape_btn_gray1);
				} else {
					btnSubmit.setEnabled(true);
					btnSubmit.setBackgroundResource(R.drawable.selector_base_green_qianse1);
				}
			}
		});


		etEditDetail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!Utility.isEmpty(etEditDetail.getText().toString())) {
					if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
						deliveryEditDetail = etEditDetail.getText().toString();
					} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
						deliveryCallEditDetail = etEditDetail.getText().toString();
					} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
						deliveryTimingEditDetail = etEditDetail.getText().toString();
					} else {
						Detail = etEditDetail.getText().toString();
					}
				}
			}
		});

		etBusinessName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					if (TextUtils.isEmpty(etBusinessName.getText())) {
						if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
							if (!TextUtils.isEmpty(deliveryBusinessName))
								etBusinessName.setText(deliveryBusinessName);
						} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
							if (!TextUtils.isEmpty(deliveryCallBusinessName))
								etBusinessName.setText(deliveryCallBusinessName);
						} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
							if (!TextUtils.isEmpty(deliveryTimingBusinessName))
								etBusinessName.setText(deliveryTimingBusinessName);
						}
						etBusinessName.setTextColor(mContext.getResources().getColor(R.color.gray_1));
					}
				}
			}
		});

		etEditDetail.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					if (TextUtils.isEmpty(etEditDetail.getText())) {
						if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
							if (!TextUtils.isEmpty(deliveryEditDetail))
								etEditDetail.setText(deliveryEditDetail);
						} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
							if (!TextUtils.isEmpty(deliveryCallEditDetail))
								etEditDetail.setText(deliveryCallEditDetail);
						} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
							if (!TextUtils.isEmpty(deliveryTimingEditDetail))
								etEditDetail.setText(deliveryTimingEditDetail);
						}
						etEditDetail.setTextColor(mContext.getResources().getColor(R.color.gray_1));
					}
				}

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.btnSubmit:
			if(Utility.isNetworkConnected() == true){
				btnSubmit.setEnabled(false);
				if(!Utility.isEmpty(fromActivity) && fromActivity.equals("modifyVasInfo")){
					addVAS(Name, Detail, Price,vasItemId);
				}else{
					if (fromEvent == VASAddActivity.DELIVERY) {// 送货上门
						addVAS(deliveryBusinessName, deliveryEditDetail, deliveryPrice,"");
					} else if (fromEvent == VASAddActivity.DELIVERY_CALL) {// 送货前联系
						addVAS(deliveryCallBusinessName, deliveryCallEditDetail, deliveryPrice,"");
					} else if (fromEvent == VASAddActivity.DELIVERY_TIMING) {// 指定时间送货
						addVAS(deliveryTimingBusinessName, deliveryTimingEditDetail, deliveryPrice,"");
					}
				}
				
			}else{
				UtilToolkit.showToast("网络错误，请设置网络");
			}

			break;
		default:
			break;
		}
	}

	/**
	 * @Title: addVAS
	 * @Description:增值业务的添加/修改
	 * @param title 增值业务名称
	 * @param description 增值业务详细
	 * @param price 增值业务价格
	 * @author: 顾冬冬
	 * @return void
	 */
	private void addVAS(String title, String description, String price,String id) {
		JSONObject data = new JSONObject();
		try {
			if(!Utility.isEmpty(fromActivity) && fromActivity.equals("modifyVasInfo")){
				data.put("sname", "vas/modify");
				data.put("id",id);
			}else{
				data.put("sname", "vas/add");
			}
			data.put("title", title);
			data.put("description", description);
			data.put("exes", price);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}
	

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		btnSubmit.setEnabled(true);
//		if(Utility.isEmpty(result)){
			if(!Utility.isEmpty(sname) && "vas/add".equals(sname)){
//				UtilToolkit.showToast("提交成功");
				ToastHelper.showToast(mContext, "提交成功");
				finish();
			}else if(!Utility.isEmpty(sname) && "vas/modify".equals(sname)){
				vasInfo.setVasName(Name);
				vasInfo.setVasDescription(Detail);
				vasInfo.setVasPrice(Price);
				mIntent = new Intent();
				mIntent.putExtra("vasInfo", vasInfo);
				setResult(VASActivity.RESULT_VAS_EDIT, mIntent);
				ToastHelper.showToast(mContext,"修改成功");
//				UtilToolkit.showToast("修改成功");
				finish();
			}
//		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		btnSubmit.setEnabled(true);
		if(!Utility.isEmpty(result)){
			UtilToolkit.showToast(result);
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		// TODO Auto-generated method stub

	}

}
