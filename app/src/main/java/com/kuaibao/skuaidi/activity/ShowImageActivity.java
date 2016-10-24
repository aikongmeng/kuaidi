package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.RealNameInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ShowImageActivity extends RxRetrofitBaseActivity {

	private Context mContext;
	private RealNameInfo realNameinfoObj;
	private ApiWrapper apiWrapper;
	private Subscription subscription;
	private Map<String ,String> recordContent;

	private String money, time, order,buyer_methor,buyer_openid,order_number;

	@BindView(R.id.tv_title_des)
	TextView tv_title_desc;
	@BindView(R.id.iv_title_back)
	SkuaidiImageView iv_title_back;
	@BindView(R.id.tv_name)
	EditText tv_name;
	@BindView(R.id.tv_sex)
	EditText tv_sex;
	@BindView(R.id.tv_minzu)
	EditText tv_minzu;
	@BindView(R.id.tv_birthday)
	TextView tv_birthday;
	@BindView(R.id.tv_no)
	TextView tv_no;
	@BindView(R.id.tv_address)
	EditText tv_address;
	@BindView(R.id.save)
	TextView save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.show_image_activity);
		mContext = this;
		ButterKnife.bind(this);
		apiWrapper = new ApiWrapper();
		initView();
	}

	private void initView() {
		tv_title_desc.setText("身份信息");

		realNameinfoObj = (RealNameInfo) getIntent().getSerializableExtra("realNameInfo");
		recordContent = (Map<String, String>) getIntent().getSerializableExtra("recordContent");
		if (null != recordContent) {
			money = recordContent.get("money");
			time = recordContent.get("time");
			order = recordContent.get("order");
			buyer_methor = recordContent.get("buyer_methor");
			buyer_openid = recordContent.get("buyer_openid");
			order_number = recordContent.get("order_number");
		}

		if (null != realNameinfoObj) {
			tv_name.setText(realNameinfoObj.getName());
			tv_sex.setText(realNameinfoObj.getSex());
			tv_minzu.setText(realNameinfoObj.getNation());
			tv_birthday.setText(realNameinfoObj.getBorn());
			tv_address.setText(realNameinfoObj.getAddress());
			tv_no.setText(realNameinfoObj.getIdno());
		}
	}

	@OnClick({R.id.iv_title_back,R.id.save})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.save:
			if (!TextUtils.isEmpty(getIntent().getStringExtra("comeType")) && getIntent().getStringExtra("comeType").equals("RealNameInputLocalActivity")) {

				realNameinfoObj.setName(tv_name.getText().toString());
				realNameinfoObj.setSex(tv_sex.getText().toString());
				realNameinfoObj.setNation(tv_minzu.getText().toString());
				realNameinfoObj.setAddress(tv_address.getText().toString());

				MessageEvent messageEvent = new MessageEvent(Constants.EVENT_BUS_TYPE_10005, "");
				Intent intent = new Intent();
				intent.putExtra("realNameInfoObj", realNameinfoObj);
				intent.putExtra("imageStr",getIntent().getStringExtra("imagePath"));
				messageEvent.putIntent(intent);
				EventBus.getDefault().post(messageEvent);

				finish();
			}else {
				getServiceIDCardImgUrl();
			}
			break;
		default:
			break;
		}
	}


	/**保存实名寄递身份证识别信息**/
	private void idCardSaveInfo(final String image){
		Map<String, String> realNameInfo_map = new HashMap<>();
		realNameInfo_map.put("name", TextUtils.isEmpty(realNameinfoObj.getName()) ? "" : realNameinfoObj.getName());
		realNameInfo_map.put("sex", TextUtils.isEmpty(realNameinfoObj.getSex()) ? "" : realNameinfoObj.getSex());
		realNameInfo_map.put("nation", TextUtils.isEmpty(realNameinfoObj.getNation()) ? "" : realNameinfoObj.getNation());
		realNameInfo_map.put("born", TextUtils.isEmpty(realNameinfoObj.getBorn()) ? "" : realNameinfoObj.getBorn());
		realNameInfo_map.put("address", TextUtils.isEmpty(realNameinfoObj.getAddress()) ? "" : realNameinfoObj.getAddress());
		realNameInfo_map.put("idno", TextUtils.isEmpty(realNameinfoObj.getIdno()) ? "" : realNameinfoObj.getIdno());
		if (!Utility.isEmpty(buyer_methor)) {
			if ("alipay".equals(buyer_methor)) {
				realNameInfo_map.put("alipay_openid", TextUtils.isEmpty(buyer_openid) ? "" : buyer_openid);
			}else {
				realNameInfo_map.put("weixin_openid", TextUtils.isEmpty(buyer_openid) ? "" : buyer_openid);
			}
		}
		realNameInfo_map.put("portrait","");
		realNameInfo_map.put("profile",image);
		realNameInfo_map.put("source","app_s_android");
		subscription = apiWrapper.saveIdCardInfo(realNameInfo_map)
				.subscribe(newSubscriber(new Action1<JSONObject>() {
					@Override
					public void call(JSONObject string) {
						billDetailIndex(image);
					}
				}));
		mCompositeSubscription.add(subscription);
	}

	/**保存实名寄递记录**/
	private void billDetailIndex(String image){
		Map<String,String> params = new HashMap<>();
		params.put("waybill_no",TextUtils.isEmpty(order) ? "" : order);// 运单号
		params.put("scan_time",TextUtils.isEmpty(time) ? "" : time);// 扫描时间
		params.put("money",TextUtils.isEmpty(money) ? "" : money);// 包裹价格
		params.put("idno",TextUtils.isEmpty(realNameinfoObj.getIdno()) ? "" : realNameinfoObj.getIdno());// 身份证号
		params.put("name",TextUtils.isEmpty(realNameinfoObj.getName()) ? "" : realNameinfoObj.getName());// 寄件用户姓名
		params.put("address",TextUtils.isEmpty(realNameinfoObj.getAddress()) ? "" : realNameinfoObj.getAddress());// 住址
		params.put("latitude","");// 投递包裹时纬度
		params.put("longitude","");// 投递包裹时经度
		params.put("image",image);// 包裹图片地址
		params.put("order_no",TextUtils.isEmpty(order_number) ?  "" : order_number);// 收款订单号
		params.put("version","1.0");

		subscription = apiWrapper.billDetailIndex(params).subscribe(newSubscriber(new Action1<String>() {
			@Override
			public void call(String string) {
//				dismissProgressDialog();
//				setResult(Constants.RESULT_GETREALNAME_SUCCESS);
				MessageEvent messageEvent = new MessageEvent(Constants.EVENT_BUS_TYPE_1007,"");
				EventBus.getDefault().post(messageEvent);
				finish();
			}
		}));
		mCompositeSubscription.add(subscription);

	}

	/**保存身份证识别后照片并返回照片在服务器中地址**/
	private void getServiceIDCardImgUrl(){
		showProgressDialog("");//this,"请稍候...");
		subscription = apiWrapper.uploadImgData(getIntent().getStringExtra("imagePath"),".jpg").subscribe(newSubscriber(new Action1<JSONObject>() {

			@Override
			public void call(JSONObject jsonData) {
				if(jsonData!=null && !TextUtils.isEmpty(jsonData.getString("src"))){
					idCardSaveInfo(jsonData.getString("src"));
				}else{
					UtilToolkit.showToast("提交失败,请重试");
				}
			}
		}));
		mCompositeSubscription.add(subscription);
	}
}
