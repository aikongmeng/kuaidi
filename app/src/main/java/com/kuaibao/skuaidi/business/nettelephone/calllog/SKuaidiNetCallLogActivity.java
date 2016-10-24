package com.kuaibao.skuaidi.business.nettelephone.calllog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.AndroidSegmentedControlView;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.AndroidSegmentedControlView.OnSelectionChangedListener;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.LocalCallLogFragment;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.NetCallLogFragment;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragmentActivity;
import com.kuaibao.skuaidi.service.AudioOnLinePlayService;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class SKuaidiNetCallLogActivity extends RxRetrofitBaseFragmentActivity implements OnSelectionChangedListener{

	@BindView(R.id.iv_title_back)
	ImageView ivBack;
	@BindView(R.id.segment_control)
	AndroidSegmentedControlView segmentControl;
	@BindView(R.id.bt_title_more_iv)
	SkuaidiImageView more;
	@BindView(R.id.tv_go_call)
	TextView tv_go_call;
	
	private Fragment mFragment;
	private LocalCallLogFragment localCallLogFragment;
	private NetCallLogFragment netCallLogFragment;
	private int openIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skuaidi_net_call_log);
		initData();
		Intent intent=new Intent(this, AudioOnLinePlayService.class);
		startService(intent);
		KLog.i("kb","start AudioOnliePlayService ****");
	}
	
	private void initData(){
		openIndex=getIntent().getIntExtra("open_index", 1);
		try {
			segmentControl.setDefaultSelection(openIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		showFragment(openIndex);
		segmentControl.setSelectedTextColor(DynamicSkinChangeManager.getTextColorSkin());
		segmentControl.setOnSelectionChangedListener(this);
	}

	@OnClick({R.id.tv_go_call,R.id.iv_title_back,R.id.bt_title_more_iv})
	public void onClick(View view){
		switch(view.getId()){
			case R.id.iv_title_back:
			case R.id.tv_go_call:
				finish();
				break;
			case R.id.bt_title_more_iv:
				if(mFragment instanceof LocalCallLogFragment){
					((LocalCallLogFragment)mFragment).openPop(more);
				}
				break;
		}
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
	public void newSelection(String identifier, String value) {
		if("普通电话".equals(value)){
			showFragment(0);
		}else if("网络电话".equals(value)){
			showFragment(1);
		}
	}
	
	public void switchContent(Fragment to,int index) {
		if (mFragment == null) {
				mFragment = new Fragment();
		}
		if (mFragment != to) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (!to.isAdded()) { 
				transaction.hide(mFragment).add(R.id.realtabcontent, to).commit(); 
			} else {
				transaction.hide(mFragment).show(to).commit();
			}
			mFragment = to;
			switchRightMenu(index);
		}
	}
	public void showFragment(int index){
		if(index==0){
			if (localCallLogFragment == null) {
				localCallLogFragment = LocalCallLogFragment.newInstance();
			}
			switchContent(localCallLogFragment,index);
		}else if(index==1){
			if (netCallLogFragment == null) {
				netCallLogFragment = NetCallLogFragment.newInstance();
			}
			switchContent(netCallLogFragment,index);
		}
	}
	
	public void switchRightMenu(int index){
		if(index==0){
			more.setVisibility(View.VISIBLE);
		}else if(index==1){
			more.setVisibility(View.GONE);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getCurrentFragmentType(){
		if(mFragment!=null && mFragment instanceof LocalCallLogFragment){
			return 0;
		}else{
			return 1;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessageEvent event=new MessageEvent(0XF3,"stop service");
		EventBus.getDefault().post(event);
	}
}
