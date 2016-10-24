package com.kuaibao.skuaidi.activity.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CashActivity;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.MyFundsAccount;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.texthelp.TextMarquee;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 钱包
 * @author 顾冬冬
 * create by 2015-11-23 上午10:59:33
 */
public class WalletMainActivity extends SkuaiDiBaseActivity {

	@BindView(R.id.tv_more) TextView more;
	@BindView(R.id.tv_title_des) TextView tvTitleDes;// 标题
	@BindView(R.id.tvcomsumption_money) TextView tvComsumptionMoney;// 消费--(冻结金额+剩余金额)
	@BindView(R.id.tv_accountBalance) TextView tv_accountBalance;
	@BindView(R.id.tv_canWithdrawCashBlance) TextView tvCanWithdrawCashBlance;
	@BindView(R.id.wallet_desc) ViewGroup walletDesc;
	@BindView(R.id.tip1) TextMarquee tips1;
	@BindView(R.id.tip2) TextMarquee tips2;

	private Context context;// 上下文
	private Intent mIntent = null;

	private String avail_money = "0.00";// 可使用金额
	private String baidu_account;// 百度账户

	private boolean isAgree = false;
	private int MY_ACCOUNT_ACTIVITY = 0;

	protected Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.ZHIFU_GETINFO_SUCCESS:// 获取账户余额成功
//				MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//				EventBus.getDefault().post(m);
				MyFundsAccount myFundsAccount = (MyFundsAccount) msg.obj;
				if (null != myFundsAccount) {
					avail_money = myFundsAccount.getAvail_money();// 可提现额
					String withdrawable_money = myFundsAccount.getWithdrawable_money();// 可提现金额
					baidu_account = myFundsAccount.getBaidu_account();// 获取百度账户
					String alipay_account = myFundsAccount.getAlipay_account();// 获取支付宝账户
					String wxpay_openid = myFundsAccount.getWxpay_openid();// 获取微信支付账户ID
					BigDecimal availMoney = new BigDecimal(avail_money);
					BigDecimal withdrawableMoney = new BigDecimal(withdrawable_money);
					BigDecimal total = availMoney.add(withdrawableMoney);
					String total_money = total.toString();// 总余额
					tv_accountBalance.setText(String.valueOf(Utility.formatMoney(total_money)+"元"));
					tvComsumptionMoney.setText(String.valueOf(Utility.formatMoney(avail_money)+"元"));
					tvCanWithdrawCashBlance.setText(String.valueOf(Utility.formatMoney(withdrawable_money)+"元"));// 可提现
					if(!Utility.isEmpty(myFundsAccount.getCash_desc_info_isShow()) && "TRUE".equals(myFundsAccount.getCash_desc_info_isShow())){
						tips1.setText(myFundsAccount.getCash_desc_info_desc());
					}else{
						tips1.setText("");
					}
					if(!Utility.isEmpty(myFundsAccount.getAvail_desc_info_isShow()) && "TRUE".equals(myFundsAccount.getAvail_desc_info_isShow())){
						tips2.setText(myFundsAccount.getAvail_desc_info_desc());
					}else{
						tips2.setText("");
					}
					if((!Utility.isEmpty(myFundsAccount.getCash_desc_info_isShow()) && "TRUE".equals(myFundsAccount.getCash_desc_info_isShow()))||
							(!Utility.isEmpty(myFundsAccount.getAvail_desc_info_isShow()) && "TRUE".equals(myFundsAccount.getAvail_desc_info_isShow()))){
						walletDesc.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.my_account_activity);
		context = this;
		ButterKnife.bind(this);
		initData();
	}

	// 初始化数据
	private void initData() {
		tvTitleDes.setText("钱包");
		more.setVisibility(View.VISIBLE);
		more.setText("资金明细");

		if (avail_money.equals("0.00")) {
			tv_accountBalance.setText("...");
		} else {
			SpannableString after = Utility.getSizeSpanUseDip(context, avail_money, avail_money.length() - 2, avail_money.length(), 18);
			tv_accountBalance.setText(String.valueOf(after+"元"));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == MY_ACCOUNT_ACTIVITY) {
//			if (resultCode == BillDetailActivity.BILL_DETAIL_ACTIVITY) {
//				mark = "desc";
//			}
//		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		isAgree = SkuaidiSpf.getIagree_tixian(context);
		/*
		 * // 如果是从个人中心进来则重新刷新一下数据 if (Utility.isEmpty(mark) &&
		 * !mark.equals("desc")) { request(); }
		 */
		getAccount();
	}

	/**
	 * 接口】获取资金账户信息
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
	 * 提现
	 * 
	 */
	public void withdrawCash() {
		// 判断是否阅读过提现说明
		if (isAgree) {
			// 进入提现界面
			mIntent = new Intent(context, CashActivity.class);
			startActivityForResult(mIntent, MY_ACCOUNT_ACTIVITY);
		} else {// 没有阅读过提现说明
				// 进入绑定百度账号界面
			if (KuaiBaoStringUtilToolkit.isEmpty(baidu_account)) {
				SkuaidiSpf.saveIagree_tixian(context, false);
			}
			mIntent = new Intent(context, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "tixian");
			startActivityForResult(mIntent, MY_ACCOUNT_ACTIVITY);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@OnClick({R.id.iv_title_back,R.id.tv_topUp,R.id.tv_withdrawCash,R.id.tv_walletFAQ,R.id.tv_more})
	public void onClick(View view){
		switch (view.getId()){
			case R.id.iv_title_back:
				finish();
				break;
			case R.id.tv_topUp:// 充值按钮
				UMShareManager.onEvent(context, "myAccount_recharge", "myAccount", "我的资金账户：充值");
				mIntent = new Intent(context, WebViewActivity.class);
				mIntent.putExtra("fromwhere", "chongzhi");
				startActivity(mIntent);
				break;
			case R.id.tv_withdrawCash:// 提现按钮
				UMShareManager.onEvent(context, "myAccount_withDrawCash", "myAccount", "我的资金账户：提现");
				withdrawCash();
				break;
			case R.id.tv_walletFAQ:// 帮助
				UMShareManager.onEvent(context, "myAccount_btn_help", "myAccount", "我的资金账户：帮助");
				mIntent = new Intent(context, WalletFAQActivity.class);
				startActivity(mIntent);
				break;
			case R.id.tv_more:
				UMShareManager.onEvent(context, "myAccount_Financial_Detail", "myAccount", "我的资金账户：资金明细");
				mIntent = new Intent(context, FundDetailsActivity.class);
				startActivity(mIntent);
				break;
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		dismissProgressDialog();
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			Message message = new Message();
			if (sname.equals("withdraw.account")) {
				JsonXmlParser.paseMyFundsAccount2(context, handler, result);
			} else if (sname.equals("user/account")) {
				MyFundsAccount myFundsAccount = JsonXmlParser.paseUserAccount(result);
				message.what = Constants.ZHIFU_GETINFO_SUCCESS;
				message.obj = myFundsAccount;
			}
			handler.sendMessage(message);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		dismissProgressDialog();
		if (!msg.equals("")) {
			UtilToolkit.showToast(msg);
		}
		if (Utility.isNetworkConnected()) {
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
