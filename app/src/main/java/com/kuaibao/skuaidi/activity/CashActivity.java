package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.MyFundsAccount;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.payali.AuthResult;
import com.kuaibao.skuaidi.popup.SlideFromBottomPopup;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.texthelp.TextMarquee;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 提现界面
 * 
 * @author 顾冬冬
 * 
 */
public class CashActivity extends SkuaiDiBaseActivity {
	private final int PAY_WX = 0X1001;
	private final int PAY_ALI = 0X1002;
	private final int PAY_BAIDU = 0x1003;
	public static final int REQUEST_BIND_BAIDU = 0x1004;
	public static final int RESULT_BIND_PAYTYPE = 0X1005;
	public static int CASH_ACTIVITY = 2;
	private static final int SDK_AUTH_FLAG = 1;


	@BindView(R.id.tv_title_des) TextView title;
	@BindView(R.id.et_input_money) EditText etInputMoney;// 提现输入框
	@BindView(R.id.llALiCash) RelativeLayout llALiCash;//支付宝提现按钮
	@BindView(R.id.llWXCash) RelativeLayout llWXCash;//微信提现按钮
	@BindView(R.id.llBaiDuCash) LinearLayout llBaiDuCash;// 百度提现按钮
	@BindView(R.id.tip_ali) TextMarquee tipAli;// 阿里滚动提示控件
	@BindView(R.id.tip_wx) TextMarquee tipWx;// 微信滚动提示控件

	private Context context;
	private Intent mIntent = null;

	private String baiduAccount;// 百度账号
	private String alipayName = "";// 支付宝真实姓名
	private String alipayAccount = "";// 支付宝账户
	private String wxpayOpenId = "";// 微信账户ID
	private String wxpay_name = "";// 获取微信支付账户昵称
	private String withdrawable_money = null;// 可提现金额
	
	protected Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SDK_AUTH_FLAG: 
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				// 支付宝返回此次授权结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				//String resultInfo = authResult.getResult();

				String resultStatus = authResult.getResultStatus();
				//Log.i("logi", "authResult"+authResult);
				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {// 业务处理成功，会返回authCode
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则用户使用的支付账户为该授权账户
//					Toast.makeText(context, "授权成功\n" + authResult.getAlipayOpenId(), Toast.LENGTH_SHORT).show();
					//Log.i("logi", authResult.getAuthCode());
					authBind("alipay", authResult.getAuthCode());
				} else if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "1005")) {// 账户已冻结，如有疑问，请联系支付宝技术支持
					UtilToolkit.showToast("您的账户已被冻结，如有疑问，请联系支付宝客服");
				} else if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "713")) {// userId不能转换为openId，请联系支付宝技术支持
					UtilToolkit.showToast("系统异常，请稍候再试");
				} else if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "202")) {// 系统异常，请联系支付宝技术支持
					UtilToolkit.showToast("支付宝系统异常");
				} else if (TextUtils.equals(resultStatus, "4000")) {// 系统异常
					UtilToolkit.showToast("系统异常，请稍候再试");
				} else if (TextUtils.equals(resultStatus, "6001")) {// 用户中途取消
					UtilToolkit.showToast("操作取消");
				} else if (TextUtils.equals(resultStatus, "6002")) {// 网络连接出错
					UtilToolkit.showToast("网络连接错误");
				} else {
					UtilToolkit.showToast("授权失败");
				}
				break;
			case Constants.ZHIFU_GETINFO_SUCCESS:// 获取账户余额成功
				//System.out.println("getinfo_success");
				MyFundsAccount myFundsAccount = (MyFundsAccount) msg.obj;
				if (null != myFundsAccount) {
					String avail_money = myFundsAccount.getAvail_money();// 可提现额  // 可使用
					withdrawable_money = myFundsAccount.getWithdrawable_money();// 可提现金额
					baiduAccount = myFundsAccount.getBaidu_account();// 获取百度账户
					alipayName = myFundsAccount.getAlipay_name();// 获取支付宝账户真实姓名
					alipayAccount = myFundsAccount.getAlipay_account();// 获取支付宝账户
					wxpayOpenId = myFundsAccount.getWxpay_openid();// 获取微信支付账户ID
					wxpay_name = myFundsAccount.getWxpay_name();// 获取微信支付账户昵称
					BigDecimal availMoney = new BigDecimal(avail_money);
					BigDecimal withdrawableMoney = new BigDecimal(withdrawable_money);
					BigDecimal total = availMoney.add(withdrawableMoney);
//					total_money = total.toString();
					
					if (!Utility.isEmpty(alipayAccount) && !Utility.isEmpty(alipayName)) {
						tipAli.setText(String.valueOf("账户："+alipayAccount+" ,姓名："+alipayName));
					}else if(Utility.isEmpty(alipayAccount) && Utility.isEmpty(alipayName)){
						tipAli.setText("暂未绑定账号");
					}else if(!Utility.isEmpty(alipayAccount)){
						tipAli.setText(String.valueOf("账户："+alipayAccount));
					}else{
						tipAli.setText(String.valueOf("姓名："+alipayName));
					}
					
					if (!Utility.isEmpty(wxpay_name)) {
						tipWx.setText(String.valueOf("姓名："+wxpay_name));
					}else{
						tipWx.setText("暂未绑定账号");
					}
				}
				break;
			
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.cash_activity);
		context = this;
		ButterKnife.bind(this);
		initView();
		myListener();
	}

	// 初始化界面
	private void initView() {
//		tvTishi = (TextView) findViewById(R.id.tvTishi);
		tipAli = (TextMarquee) findViewById(R.id.tip_ali);
		tipWx = (TextMarquee) findViewById(R.id.tip_wx);

		SpannableString ss;
		ss = new SpannableString("请输入提现金额");
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置hint
		etInputMoney.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失

		title.setText("提现");
	}

	private void myListener() {
		// *************控件edittext只能输入到小数点后两位**************
		etInputMoney.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
						etInputMoney.setText(s);
						etInputMoney.setSelection(s.length());
					}
				}
				if (s.toString().trim().equals(".")) {
					s = "0" + s;
					etInputMoney.setText(s);
					etInputMoney.setSelection(2);
				}

				if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						etInputMoney.setText(s.subSequence(0, 1));
						etInputMoney.setSelection(1);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	/**
	 * 设置*号颜色为橙色 .gudd
	 * @return style
	 */
	@SuppressWarnings("unused")
	private SpannableStringBuilder setXingColor(String text) {
		SpannableStringBuilder style = new SpannableStringBuilder(text);
		style.setSpan(new ForegroundColorSpan(Color.rgb(243, 152, 0)), 0, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		getAccount();// 获取资金账户
		widthdrawCheck();// 获取可提现金额
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void showBottomPop(){
		SlideFromBottomPopup sliderPop=new SlideFromBottomPopup(CashActivity.this);
		sliderPop.setPopClick(new SlideFromBottomPopup.OnBottomPopClick() {
			@Override
			public void onClickFirstPop() {
				if (!Utility.isEmpty(alipayAccount)) {// 已经绑定支付宝
					SkuaidiDialog skuaidiDialog = new SkuaidiDialog(CashActivity.this);
					skuaidiDialog.setTitle("提示");
					skuaidiDialog.setContent("是否解绑支付宝?\n账户："+alipayAccount+"\n姓名："+alipayName);
					skuaidiDialog.isUseEditText(false);
					skuaidiDialog.setPositionButtonTitle("解绑");
					skuaidiDialog.setNegativeButtonTitle("取消");
					skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
						@Override
						public void onClick(View v) {
							JSONObject data = new JSONObject();
							try {
								data.put("sname", "withdraw/unbinding");
								data.put("type", "alipay");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
						}
					});
					skuaidiDialog.showDialog();
				} else {
					UtilToolkit.showToast("请先绑定支付宝账号");
					//alipayAuth();// 绑定支付宝
				}
			}
		});
		sliderPop.showPopupWindow();
	}

	@OnClick({R.id.tv_more,R.id.iv_title_back,R.id.llALiCash,R.id.llWXCash,R.id.llBaiDuCash,R.id.tvTishi})
	public void onClick(View view){
		String money;// 输入的提现金额
		switch (view.getId()){
			case R.id.tv_more:
				showBottomPop();
				break;
			case R.id.iv_title_back:// 返回按钮
				setResult(CASH_ACTIVITY);
				finish();
				break;
			case R.id.llWXCash:
				UMShareManager.onEvent(context, "CashByWX", "CashActivity", "提现界面：微信提现");
				money = etInputMoney.getText().toString();
				if (!Utility.isEmpty(money)) {
					if (!Utility.isEmpty(wxpayOpenId)) {
						showComfirmPop(llBaiDuCash, money, PAY_WX);
					} else {
						authBind("wxpay", "");
					}
				} else {
					UtilToolkit.showToast("请输入提现金额");
				}
				break;
			case R.id.llALiCash:
				UMShareManager.onEvent(context, "CashByAliPay", "CashActivity", "提现界面：支付宝提现");
				if (!Utility.isEmpty(alipayAccount)) {// 已经绑定支付宝
					money = etInputMoney.getText().toString();
					if (!Utility.isEmpty(money)) {
						showComfirmPop(llBaiDuCash, money, PAY_ALI);
					} else {
						UtilToolkit.showToast("请输入提现金额");
					}
				} else {
					UtilToolkit.showToast("绑定支付宝账号");
					alipayAuth();// 绑定支付宝
				}
				break;
			case R.id.llBaiDuCash:
				UMShareManager.onEvent(context, "CashByBaiDu", "CashActivity", "提现界面：百度提现");
				money = etInputMoney.getText().toString();
				if (!Utility.isEmpty(money)) {
					if (!Utility.isEmpty(baiduAccount)) {// 已经绑定百度账户
						showComfirmPop(llBaiDuCash, money, PAY_BAIDU);
					} else {
						mIntent = new Intent(context, WebViewActivity.class);
						mIntent.putExtra("fromwhere", "cash_baidu");// 百度提现
						startActivityForResult(mIntent, REQUEST_BIND_BAIDU);
						finish();
					}
				} else {
					UtilToolkit.showToast("请输入提现金额");
				}
				break;
			case R.id.tvTishi://提示
				UMShareManager.onEvent(context, "myAccount_withDrawCashDescription", "myAccount", "我的资金账户：提现说明");
				mIntent = new Intent(context, WebViewActivity.class);
				mIntent.putExtra("fromwhere", "problemCash");
				startActivity(mIntent);
				break;
		}
	}

	/**
	 * 显示确认金额和支付方式对话框
	 */
	private void showComfirmPop(View v, String pay_money, final int pay_type) {
		SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
		dialog.setTitleGray("温馨提示");
		SpannableStringBuilder style = null;
		String text;
		if (pay_type == PAY_WX) {
			text = "是否确认用微信\n姓名："+wxpay_name+"\n提现" + pay_money + "元？";
			int startIndex = text.indexOf("提现") + 2;
			style = new SpannableStringBuilder(text);
			style.setSpan(new ForegroundColorSpan(Utility.getColor(context,R.color.red)), startIndex, text.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		} else if (pay_type == PAY_ALI) {
			if (!Utility.isEmpty(alipayAccount)) {// 已经绑定支付宝
				text = "是否确认用支付宝\n账号:"+alipayAccount+"\n姓名："+alipayName+"\n提现" + pay_money + "元？";
				int startIndex = text.indexOf("提现") + 2;
				style = new SpannableStringBuilder(text);
				style.setSpan(new ForegroundColorSpan(Utility.getColor(context,R.color.red)), startIndex, text.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			} else {
				UtilToolkit.showToast("请先绑定支付宝账号");
				alipayAuth();// 绑定支付宝
			}
		} else if (pay_type == PAY_BAIDU) {
			text = "是否确认使用百度账号（" + baiduAccount + "）提现" + pay_money + "元？";
			int startIndex = text.indexOf("提现") + 2;
			style = new SpannableStringBuilder(text);
			style.setSpan(new ForegroundColorSpan(Utility.getColor(context,R.color.red)), startIndex, text.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		dialog.setContentGray(style);
		dialog.setNegativeButtonTextGray("取消");
		dialog.setPositionButtonTextGray("确认");
		dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

			@Override
			public void onClick(View v) {
				if (pay_type == PAY_WX) {
					cashForWX();
				} else if (pay_type == PAY_ALI) {
					cashForAli();
				} else if (pay_type == PAY_BAIDU) {
					cashForBaidu();
				}
			}
		});
		dialog.showDialogGray(v);
	}
	
	/**
	 * 【接口】获取本次可提现金额
	 */
	public void widthdrawCheck(){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "withdraw/check");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * 【接口】获取资金账户信息
	 */
	public void getAccount() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "user/account");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}
	
	/**
	 * 微信提现
	 */
	private void cashForWX() {
		if (!Utility.isEmpty(wxpayOpenId)) {
			cash("wxpay");
		} else {
			authBind("wxpay", "");
		}
	}

	/**
	 * 支付宝提现
	 */
	private void cashForAli() {
		if (!Utility.isEmpty(alipayAccount)) {// 已经绑定支付宝
			cash("alipay");
		} else {
			alipayAuth();// 绑定支付宝
		}
	}

	/**
	 * 百度提现
	 */
	private void cashForBaidu() {
		if (!Utility.isEmpty(baiduAccount)) {// 已经绑定百度账户
			cash("baidu");
		} else {
			mIntent = new Intent(context, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "cash_baidu");// 百度提现
			startActivityForResult(mIntent, REQUEST_BIND_BAIDU);
		}
	}

	/**
	 * 提现方式
	 */
	private void cash(String cash_type) {
		String cash = etInputMoney.getText().toString();
		float tmoney = Float.parseFloat(withdrawable_money);

		if (cash.equals("0")) {
			UtilToolkit.showToast("请输入正确金额");
		} else {
			float tixiane_f = Float.parseFloat(cash);
			if (tixiane_f <= tmoney) {
				cashWithDraw(cash, cash_type);
				showProgressDialog( "请稍候...");
			} else {
				UtilToolkit.showToast("提现失败，余额不足");
			}
		}
	}

	/**
	 * 提现接口
	 */
	private void cashWithDraw(String money, String cash_type) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "withdraw");
			data.put("money", money);
			data.put("type", cash_type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_BIND_BAIDU && resultCode == RESULT_BIND_PAYTYPE) {
			
			finish();
		}
	}

	/**
	 * 支付宝绑定功能
	 */
	private void alipayAuth() {
		showProgressDialog("获取授权中...");
		final ApiWrapper apiWrapper=new ApiWrapper();
		Subscription mSubscription=apiWrapper.aliPayAuth().subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
			@Override
			public void call(final com.alibaba.fastjson.JSONObject jsonObject) {
				if(jsonObject!=null && jsonObject.containsKey("sign")){
					Runnable authRunnable = new Runnable() {
						@Override
						public void run() {
							// 构造AuthTask 对象
							AuthTask authTask = new AuthTask(CashActivity.this);
							// 调用授权接口，获取授权结果
							Map<String, String> result = authTask.authV2(jsonObject.getString("sign"),true);
							Message msg = new Message();
							msg.what = SDK_AUTH_FLAG;
							msg.obj = result;
							handler.sendMessage(msg);
						}
					};
					// 必须异步调用
					Thread authThread = new Thread(authRunnable);
					authThread.start();
				}else{
					UtilToolkit.showToast("授权失败");
				}
			}
		}));
		mCompositeSubscription.add(mSubscription);
		// 授权信息
//		String info = getAuthInfo();
//		// 对授权信息做签名
//		String sign = sign(info);
//		try {
//			// 仅需对sign 做URL编码
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		// 完整的符合支付宝授权规范的参数信息
//		final String authInfo = info + "&sign=\"" + sign + "\"&" + getSignType();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 获取支付宝授权信息
	 */
//	public String getAuthInfo() {
//		String app_id = "2015092500324812";
//		String pid = AliPay.PARTNER;// 商户号
//		String target_id = SkuaidiSpf.getLoginUser().getUserId();
//		// 服务接口名称， 固定值
//		String authInfo = "apiname=\"com.alipay.account.auth\"";
//		// 商户签约拿到的app_id，如：2013081700024223
//		authInfo += "&app_id=" + "\"" + app_id + "\"";
//		// 商户类型标识， 固定值
//		authInfo += "&app_name=\"mc\"";
//		// 授权类型，授权常量值为"AUTHACCOUNT", 登录常量值为"LOGIN"
//		authInfo += "&auth_type=\"AUTHACCOUNT\"";
//		// 业务类型， 固定值
//		authInfo += "&biz_type=\"openservice\"";
//		// 商户签约拿到的pid，如：2088102123816631
//		authInfo += "&pid=" + "\"" + pid + "\"";
//		// 产品码， 固定值
//		authInfo += "&product_id=\"WAP_FAST_LOGIN\"";
//		// 授权范围， 固定值
//		authInfo += "&scope=\"kuaijie\"";
//		// 商户标识该次用户授权请求的ID，该值在商户端应保持唯一，如：kkkkk091125
//		authInfo += "&target_id=" + "\"" + target_id + "\"";
//		// 签名时间戳
//		authInfo += "&sign_date=" + "\"" + getSignDate() + "\"";
//		return authInfo;
//
//	}

	/**
	 * sign the order info. 对授权信息进行签名
	 * 
	 * @param content 待签名授权信息
	 */
//	public String sign(String content) {
//		return SignUtils.sign(content, AliPay.RSA_PRIVATE);
//	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
//	public String getSignType() {
//		return "sign_type=\"RSA\"";
//	}

	/**
	 * get the sign time. 获取当前签名时间
	 * 
	 */
//	public String getSignDate() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//		return format.format(new Date());
//	}

	/**
	 * 提现方式的绑定授权接口
	 * @param auth_type 授权类型
	 * @param auth_code 授权码
	 */
	private void authBind(String auth_type, String auth_code) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "auth_binding");
			data.put("auth_type", auth_type);
			data.put("auth_code", auth_code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		dismissProgressDialog();
		Message message = new Message();
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			String url = "";
			if (TextUtils.equals(sname, "auth_binding")) {
				url = result.optString("request_url");
			}
			if (!Utility.isEmpty(url)) {
				mIntent = new Intent(context, WebViewActivity.class);
				mIntent.putExtra("fromwhere", "cash_alipay");
				mIntent.putExtra("url", url);
				startActivity(mIntent);
			}
		}

		switch (sname){
			case "withdraw":
				UtilToolkit.showToast(msg);
				finish();
				break;
			case "user/account":
				MyFundsAccount myFundsAccount = JsonXmlParser.paseUserAccount(result);
				message.what = Constants.ZHIFU_GETINFO_SUCCESS;
				message.obj = myFundsAccount;
				break;
			case "withdraw/check":
				if (result != null) {
					withdrawable_money = result.optString("withdrawable_money");
					SpannableString ss = new SpannableString("最多可提现" + withdrawable_money + "元");
					AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
					ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					// 设置hint
					etInputMoney.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
				}
				break;
			case "withdraw/unbinding":
				UtilToolkit.showToast(msg);
				// 解绑成功
				tipAli.setText("暂未绑定账号");
				alipayAccount="";
				break;

		}
		if (handler != null)
			handler.sendMessage(message);


	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (code.equals("7") && null != result) {
			try {
				String desc = result.optString("desc");
				UtilToolkit.showToast(desc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if(!Utility.isEmpty(msg)){
				UtilToolkit.showToast(msg);
			}
		}
	}

}
