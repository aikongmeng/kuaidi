package com.kuaibao.skuaidi.activity.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

/**
 * 
 * 钱包常见问题说明页
 * @author xy
 *
 */
public class WalletFAQActivity extends RxRetrofitBaseActivity implements OnClickListener{
	
	private Context mContext;
	private Intent mIntent ;
	private TextView title;
	private SkuaidiImageView back; 
	
	private ViewGroup desc_balance_cash_comsumpation,problem_topUp,problem_consumpation,problem_cash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.wallet_faq_layout);
		initView();
	}
	
	private void initView(){
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);
		desc_balance_cash_comsumpation = (ViewGroup) findViewById(R.id.desc_balance_cash_comsumpation);
		problem_topUp = (ViewGroup) findViewById(R.id.problem_topUp);
		problem_consumpation = (ViewGroup) findViewById(R.id.problem_consumpation);
		problem_cash = (ViewGroup) findViewById(R.id.problem_cash);
		
		back.setOnClickListener(this);
		desc_balance_cash_comsumpation.setOnClickListener(this);
		problem_topUp.setOnClickListener(this);
		problem_consumpation.setOnClickListener(this);
		problem_cash.setOnClickListener(this);
		
		title.setText("帮助");
	}
	
	
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.desc_balance_cash_comsumpation:// 余额，提现，消费-说明
			UMShareManager.onEvent(mContext, "WalletFAQ_Desc_All", "walletFAQ", "常见问题：余额，提现，消费-说明");
			mIntent = new Intent(mContext, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "descAll");
			startActivity(mIntent);
			break;
		case R.id.problem_topUp:// 充值说明
			UMShareManager.onEvent(mContext, "myAccount_rechargeDescription", "myAccount", "我的资金账户：充值说明");
			mIntent = new Intent(mContext, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "problemTopUp");
			startActivity(mIntent);
			break;
		case R.id.problem_consumpation:// 消费说明
			UMShareManager.onEvent(mContext, "WalletFAQ_Problem_comsumpation", "walletFAQ", "常见问题：消费问题");
			mIntent = new Intent(mContext, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "problemConSumpation");
			startActivity(mIntent);
			break;
		case R.id.problem_cash:// 提现说明
			UMShareManager.onEvent(mContext, "myAccount_withDrawCashDescription", "myAccount", "我的资金账户：提现说明");
			mIntent = new Intent(mContext, WebViewActivity.class);
			mIntent.putExtra("fromwhere", "problemCash");
			startActivity(mIntent);
			break;

		default:
			break;
		}
		
	}
	
}
