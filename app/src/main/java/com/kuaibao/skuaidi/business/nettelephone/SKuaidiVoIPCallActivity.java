package com.kuaibao.skuaidi.business.nettelephone;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.util.CallFailReason;
import com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils;
import com.kuaibao.skuaidi.business.nettelephone.util.VoIPCallHelper;
import com.kuaibao.skuaidi.business.nettelephone.widget.ECCallControlUILayout;
import com.kuaibao.skuaidi.business.nettelephone.widget.ECCallHeadUILayout;
import com.kuaibao.skuaidi.business.nettelephone.widget.ECCallHeadUILayout.OnSendDTMFDelegate;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.SKuaidiECDeviceManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.BlurUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VideoRatio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import butterknife.BindView;
import gen.greendao.bean.INetCallInfo;
import gen.greendao.dao.INetCallInfoDao;

public class SKuaidiVoIPCallActivity extends RxRetrofitBaseActivity implements VoIPCallHelper.OnCallEventNotifyListener,ECCallControlUILayout.OnCallControlDelegate,OnSendDTMFDelegate{
	/**昵称*/
	public static final String EXTRA_CALL_NAME = "con.yuntongxun.ecdemo.VoIP_CALL_NAME";
	/**通话号码*/
	public static final String EXTRA_CALL_NUMBER = "con.yuntongxun.ecdemo.VoIP_CALL_NUMBER";
	/**呼入方或者呼出方*/
	public static final String EXTRA_OUTGOING_CALL = "con.yuntongxun.ecdemo.VoIP_OUTGOING_CALL";

	/**通话昵称*/
	protected String mCallName;
	/**通话号码*/
	protected String mCallNumber;

	/**是否是直拨*/
	private boolean isDirectCall = true;

	/**呼叫唯一标识号*/
	protected String mCallId;

	@BindView(R.id.call_header_ll)
	ECCallHeadUILayout mCallHeaderView;
	@BindView(R.id.call_control_ll)
	ECCallControlUILayout mCallControlUIView;
	@BindView(R.id.voip_layout_parent)
	LinearLayout parent;


	private int totalTime=0;
	private int time=0;
	private String voipAccount;
	private String passward;
	private String disNum;
	private String token;
	private String callIsLog;
	private String teleType="0";
	private UserInfo mUserInfo;
	private INetCallInfoDao mINetCallInfoDao;
	private INetCallInfo callInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skuaidi_vo_ipcall);
		EventBus.getDefault().register(this);
		try{
			setBackgroundblur();
		}catch (Exception e){
			e.printStackTrace();
		}
		mUserInfo=SkuaidiSpf.getLoginUser();
		mINetCallInfoDao=SKuaidiApplication.getInstance().getDaoSession().getINetCallInfoDao();
		time=getIntent().getIntExtra("avail_time", 0);
		voipAccount=TextUtils.isEmpty(getIntent().getStringExtra("voip"))?"":getIntent().getStringExtra("voip");
		passward=TextUtils.isEmpty(getIntent().getStringExtra("passward"))?"":getIntent().getStringExtra("passward");
		token=TextUtils.isEmpty(getIntent().getStringExtra("token"))?"":getIntent().getStringExtra("token");
		disNum=TextUtils.isEmpty(getIntent().getStringExtra("disNum"))?mUserInfo.getPhoneNumber():getIntent().getStringExtra("disNum");
		mCallName = getIntent().getStringExtra(EXTRA_CALL_NAME);
		mCallNumber = getIntent().getStringExtra(EXTRA_CALL_NUMBER);
		//Log.i("kb", "通话参数:--->voip："+voipAccount+";passward: "+passward+";token: "+token+";"+ "time: "+time+";diNum: "+disNum+";mCallName: "+mCallName+";mCallNumber: "+mCallNumber);
		if(TextUtils.isEmpty(voipAccount) || TextUtils.isEmpty(passward)){
			if(TextUtils.isEmpty(token)){
				finish();
				return;
			}
		}
		if(time>0){
			callIsLog=getIntent().hasExtra("callIsLog")?getIntent().getStringExtra("callIsLog"):"normalLog";
			totalTime=time*60;
			teleType= NetTeleUtils.getMyTelePrefer(SkuaidiSpf.getLoginUser().getPhoneNumber());
			if("0".equals(teleType)){
				isDirectCall=true;
			}else if("1".equals(teleType)){
				isDirectCall=false;
			}else if("2".equals(teleType)){
				if(NetTeleUtils.isWifi(getApplicationContext())){
					isDirectCall=true;
				}else{
					isDirectCall=false;
				}
			}
			umengAnaliseCallPersonNum();
			initCall();
			doLoginYTX(token,voipAccount,passward);
		}else{
			UtilToolkit.showToast("余额不足");
			mCallControlUIView.setReleaseButtonEnable(false);
			finishActivity();
		}
	}

	private void umengAnaliseCallPersonNum(){
			if((System.currentTimeMillis()-SkuaidiSpf.getLastTotalTime())>24*60*60*1000){
				UMShareManager.onEvent(SKuaidiApplication.getContext(), "netcall_usecount_everyday", "NetCallPersion", "1");
				SkuaidiSpf.setLastTotalTime(System.currentTimeMillis());
			}
	}
	private static final ECDevice.InitListener initListener=new ECDevice.InitListener(){
		@Override
		public void onInitialized() {
			// SDK已经初始化成功
			KLog.d("kb","SDK初始化成功");
			if(TextUtils.isEmpty(voip_1) || TextUtils.isEmpty(password_1)){
				if(!TextUtils.isEmpty(token_1)){
					SKuaidiECDeviceManager.loginYTXByNormal(token_1);
				}
			}else{
				SKuaidiECDeviceManager.loginYTXByPassward(voip_1,password_1);
			}
		}
		@Override
		public void onError(Exception exception) {
			// SDK 初始化失败,可能有如下原因造成
			// 1、可能SDK已经处于初始化状态
			// 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
			//    或者未配置服务属性android:exported="false";
			// 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
			//    Android Build.VERSION.SDK_INT 以及以上版本）
			UtilToolkit.showToast(exception.getMessage());
			KLog.d("kb",exception.getMessage());
		}
	};
	private static String token_1,voip_1,password_1;
	private void doLoginYTX(final String token,final String voip,final String passward){
		// 判断SDK是否已经初始化，如果已经初始化则可以直接调用登陆接口
		// 没有初始化则先进行初始化SDK，然后调用登录接口注册SDK
		if(!ECDevice.isInitialized()) {
			token_1=token;
			voip_1=voip;
			password_1=passward;
			ECDevice.initial(SKuaidiApplication.getContext(),initListener);
		}else{
			if(TextUtils.isEmpty(voip) || TextUtils.isEmpty(passward)){
				if(!TextUtils.isEmpty(token)){
					SKuaidiECDeviceManager.loginYTXByNormal(token);
				}
			}else{
				SKuaidiECDeviceManager.loginYTXByPassward(voip,passward);
			}
		}
	}
	private void setBackgroundblur(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int  Phone_width  = dm.widthPixels ;
		int Phone_height = dm.heightPixels ;
		//获取WallpaperManager 壁纸管理器
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(this); // 获取壁纸管理器
		// 获取当前壁纸
		Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		//将Drawable,转成Bitmap
		Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
		Bitmap pbm;
		if(bm.getWidth()>Phone_width ){
			if(bm.getHeight()>Phone_height){
				//截取相应屏幕的Bitmap,截取壁纸第一屏
				pbm = Bitmap.createBitmap(bm, 0, 0, Phone_width, Phone_height);
			}else{
				pbm = Bitmap.createBitmap(bm, 0, 0, Phone_width, bm.getHeight());
			}
		}else{
			if(bm.getHeight()>Phone_height){
				//截取相应屏幕的Bitmap,截取壁纸第一屏
				pbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), Phone_height);
			}else{
				pbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());
			}
		}
		if(pbm==null){
			return;
		}
		final Bitmap blurBmp = BlurUtil.fastblur(SKuaidiVoIPCallActivity.this, BlurUtil.small(pbm,0.2f,0.2f), 10);//0-25，表示模糊值
		final Drawable newBitmapDrawable = new BitmapDrawable(BlurUtil.big(blurBmp,5f,5f)); // 将Bitmap转换为Drawable
		parent.post(new Runnable()  //调用UI线程
		{
			@Override
			public void run()
			{
				parent.setBackgroundDrawable(newBitmapDrawable);//设置背景
			}
		});
	}

	@Subscribe
	public void onEvent(MessageEvent event){
		if(event.type==SKuaidiECDeviceManager.EC_EVENT_STATE_0){
			UtilToolkit.showToast(event.message);
		}else if(event.type==SKuaidiECDeviceManager.EC_EVENT_STATE_1){
			if(SKuaidiApplication.getInstance().isLoginSuccess()){
				//if(!hasMakeCalled)
				makeCall();
			}
		}else if(event.type==SKuaidiECDeviceManager.EC_EVENT_STATE_2){
			//Log.e("kb", "云通讯连接断开");
			//UtilToolkit.showToast(event.message);
		}
	}

	private void umengAnaliseCountCall(){
		UMShareManager.onEvent(SKuaidiApplication.getContext(), "netcall_callcount", "NetCallTotalCount", "1");
	}
	private void makeCall(){
		if(isDirectCall){
			umengAnaliseCountCall();
			mCallId = VoIPCallHelper.makeCall(ECVoIPCallManager.CallType.DIRECT, mCallNumber,disNum);
			if (TextUtils.isEmpty(mCallId)) {
				UtilToolkit.showToast("无法连接服务器，请稍后再试");
				return;
			}
			initNetCallInfo();
		}else{
			VoIPCallHelper.makeCallBack(disNum,mCallNumber);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		VoIPCallHelper.setOnCallEventNotifyListener(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
	}

	private void finishActivity(){
		mCallHeaderView.postDelayed(new Runnable() {
			@Override
			public void run() {
				setResult(RESULT_OK);
				finish();
			}
		}, 1000);
	}

	private void initCall() {
		initView();
		// 处理呼叫逻辑
		if (TextUtils.isEmpty(mCallNumber)) {
			UtilToolkit.showToast("呼叫号码为空");
			mCallControlUIView.setReleaseButtonEnable(false);
			finishActivity();
			return;
		}
		if(isDirectCall){
			mCallHeaderView.setCallTextMsg("normalLog".equals(callIsLog)?"正在拨号...":"正在无痕拨号...\n本次通话不产生通话记录");
		}else{
			mCallHeaderView.setCallTextMsg("normalLog".equals(callIsLog)?"正在回拨...":"正在无痕回拨...\n本次通话不产生通话记录");
		}
	}

	private void initNetCallInfo(){
		callInfo=new INetCallInfo();
		long currentTime=System.currentTimeMillis();
		callInfo.setCallDate(currentTime);
		callInfo.setCreate_time(UtilityTime.getDateTimeByMillisecond2(currentTime,UtilityTime.YYYY_MM_DD_HH_MM_SS));
		callInfo.setCalled_name(mCallName);
		callInfo.setCalled(mCallNumber);
		callInfo.setCallType(YTXConst.OUTGOING_CALL);
		callInfo.setCallState(YTXConst.CALLING_WAITING);
		callInfo.setNclid(mCallId);
		callInfo.setUserId(mUserInfo.getUserId());
		callInfo.setDisNum(disNum);
		mINetCallInfoDao.insertOrReplace(callInfo);
	}
	private void initView(){
		mCallControlUIView.setOnCallControlDelegate(this);
		mCallHeaderView.setBackVisible(!isDirectCall);
		if(TextUtils.isEmpty(mCallName)){
			mCallHeaderView.setCallName(mCallNumber);
		}else{
			mCallHeaderView.setCallName(mCallName);
		}
		mCallHeaderView.setCallNumber(mCallNumber);
		mCallHeaderView.setCalling(false);

		ECCallControlUILayout.CallLayout callLayout = isDirectCall ? ECCallControlUILayout.CallLayout.OUTGOING : ECCallControlUILayout.CallLayout.HUIBO;
		mCallControlUIView.setCallDirect(callLayout);

		mCallHeaderView.setSendDTMFDelegate(this);
		mCallHeaderView.getmCallTime().setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (SystemClock.elapsedRealtime()-chronometer.getBase()>totalTime*1000) {
					chronometer.stop();
					UtilToolkit.showToast("余额不足，结束通话");
					VoIPCallHelper.releaseCall(mCallId);
				}
			}
		});
	}

	/**
	 * 是否需要做界面更新
	 * @param callId
	 * @return
	 */
	protected boolean needNotify(String callId) {
		return !(isFinishing() || !isEqualsCall(callId));
	}

	/**
	 * 收到的VoIP通话事件通知是否与当前通话界面相符
	 * @return 是否正在进行的VoIP通话
	 */
	protected boolean isEqualsCall(String callId) {
		return (!TextUtils.isEmpty(callId) && callId.equals(mCallId));
	}

	@Override
	public void onCallProceeding(String callId) {
		if(mCallHeaderView == null || !needNotify(callId)) {
			return ;
		}
		KLog.d("onUICallProceeding:: call id " + callId);
		mCallHeaderView.setCallTextMsg("正在呼叫,请稍后...");
		//hasMakeCalled=true;
	}
	@Override
	public void onMakeCallback(ECError error, String caller, String called) {
		KLog.e("kb" , "make callback error [" + error.toString() + " ]");
		if(error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
			// 回拨呼叫成功,等待来电
			mCallHeaderView.setCallTextMsg("回拨呼叫成功");
		}else{
			mCallHeaderView.setCallTextMsg("回拨呼叫失败,请稍后重试");
		}
	}
	@Override
	public void onCallAlerting(String callId) {
		if(!needNotify(callId) || mCallHeaderView == null) {
			return ;
		}
		//KLog.d("onUICallAlerting:: call id " + callId);
		mCallHeaderView.setCallTextMsg("等待对方接听...");
		mCallControlUIView.setCallDirect(ECCallControlUILayout.CallLayout.ALERTING);
	}
	@Override
	public void onCallAnswered(String callId) {
		if(!needNotify(callId)|| mCallHeaderView == null) {
			return ;
		}
		//KLog.d("onUICallAnswered:: call id " + callId);
		mCallHeaderView.setCalling(true);
		INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
		iNetCallInfo.setCallState(YTXConst.CALLING_SUCCESS);
		mINetCallInfoDao.update(iNetCallInfo);
		mCallHeaderView.setCallTextMsg("通话中...");
		umengAnaliseCountCallSuccess();
	}
	private void umengAnaliseCountCallSuccess(){
		UMShareManager.onEvent(SKuaidiApplication.getContext(), "netcall_callcount_success", "NetCallCountSuccess", "1");
	}
	@Override
	public void onMakeCallFailed(String callId, int reason) {
		if(mCallHeaderView == null || !needNotify(callId)) {
			return ;
		}
		KLog.e("kb","onUIMakeCallFailed:: call id " + callId + " ,reason " + reason);
		mCallHeaderView.setCalling(false);
		mCallHeaderView.setCallTextMsg(CallFailReason.getCallFailReason(reason));

		if(reason == SdkErrorCode.REMOTE_CALL_BUSY || reason == SdkErrorCode.REMOTE_CALL_DECLINED){
			INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
			iNetCallInfo.setCallState(YTXConst.CALLING_UNLINE);
			mINetCallInfoDao.update(iNetCallInfo);
		}else{
			INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
			iNetCallInfo.setCallState(YTXConst.CALLING_FAIL);
			mINetCallInfoDao.update(iNetCallInfo);
			VoIPCallHelper.releaseCall(mCallId);
		}
	}
	@Override
	public void onCallReleased(String callId) {
		if(mCallHeaderView == null || !needNotify(callId)) {
			return ;
		}
		//KLog.d("onUICallReleased:: call id " + callId);
		mCallHeaderView.setCalling(false);
		mCallHeaderView.setCallTextMsg("通话结束\n通话时间以后台统计为准");
		mCallControlUIView.setControlEnable(false);
		if(callInfo!=null && callInfo.getCallState()==YTXConst.CALLING_SUCCESS){
			INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
			iNetCallInfo.setCallState(YTXConst.CALLING_COMMIT);
			iNetCallInfo.setTalkDuration((int)(SystemClock.elapsedRealtime()-mCallHeaderView.getmCallTime().getBase())/1000);
			mINetCallInfoDao.update(iNetCallInfo);
			umengAnalisePersonFee(callInfo.getTalkDuration());
		}else if(callInfo!=null && callInfo.getCallState()==YTXConst.CALLING_WAITING){
			INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
			iNetCallInfo.setCallState(YTXConst.CALLING_FAIL);
			mINetCallInfoDao.update(iNetCallInfo);
		}
		mCallControlUIView.setReleaseButtonEnable(false);
		finishActivity();
	}

	private void umengAnalisePersonFee(long callTime){
		long m=callTime/1000;
		int min=(int)Math.ceil(m/(float)60.0);
		float money=0.06F*min;
		HashMap<String, String> map = new HashMap<>();
		map.put("netcall_fee", String.valueOf(money));
		MobclickAgent.onEvent(SKuaidiApplication.getContext(),"netcall_fee_person", map);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isDirectCall){
				VoIPCallHelper.releaseCall(mCallId);
			}else{
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onVideoRatioChanged(VideoRatio videoRatio) {

	}
	@Override
	public void sendDTMF(char c) {
		ECDevice.getECVoIPCallManager().sendDTMF(mCallId, c);
	}

	@Override
	public void onClickBackBotton() {
		finish();
	}

	@Override
	public void onViewAccept(ECCallControlUILayout controlPanelView, ImageButton view) {
		if(controlPanelView != null) {
			controlPanelView.setControlEnable(false);
		}
		VoIPCallHelper.acceptCall(mCallId);
		mCallControlUIView.setCallDirect(ECCallControlUILayout.CallLayout.INCALL);
		mCallHeaderView.setCallTextMsg("正在接听...");
	}
	@Override
	public void onViewReject(ECCallControlUILayout controlPanelView, ImageButton view) {
		if(controlPanelView != null) {
			controlPanelView.setControlEnable(false);
		}
		VoIPCallHelper.rejectCall(mCallId);
	}
	@Override
	public void onViewRelease(ECCallControlUILayout controlPanelView, ImageButton view) {
		if(controlPanelView != null) {
			controlPanelView.setControlEnable(false);
		}
		VoIPCallHelper.releaseCall(mCallId);
		if(callInfo!=null && callInfo.getCallState()!=YTXConst.CALLING_SUCCESS){
			if(callInfo.getCallState()==YTXConst.CALLING_WAITING){
				INetCallInfo iNetCallInfo=mINetCallInfoDao.load(mCallId);
				iNetCallInfo.setCallState(YTXConst.CALLING_FAIL);
				mINetCallInfoDao.update(iNetCallInfo);
			}
			mCallControlUIView.setReleaseButtonEnable(false);
			finishActivity();
		}
	}
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		SKuaidiECDeviceManager.logoutYTXForNext();
		VoIPCallHelper.setOnCallEventNotifyListener(null);

		BitmapDrawable bd = (BitmapDrawable)parent.getBackground();
		parent.setBackgroundResource(0);
		bd.setCallback(null);
		bd.getBitmap().recycle();
		System.gc();
		super.onDestroy();
	}
}
