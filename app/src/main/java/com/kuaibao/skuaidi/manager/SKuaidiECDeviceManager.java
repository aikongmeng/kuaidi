package com.kuaibao.skuaidi.manager;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.YTXConst;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.socks.library.KLog;
import com.yuntongxun.ecsdk.CallStatisticsInfo;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;

import org.greenrobot.eventbus.EventBus;

//import //KLog.LogUtils;

public class SKuaidiECDeviceManager {
	public static final int EC_EVENT_STATE_0= 0x6001;
	public static final int EC_EVENT_STATE_1= 0x6002;
	public static final int EC_EVENT_STATE_2= 0x6003;
	public static void loginYTXByNormal(String userId){
		//KLog.d("LoginYTX@@@@@@@@@@@@");
		    //设置注册参数、设置通知回调监听
		    // 构建注册所需要的参数信息
		    ECInitParams params = ECInitParams.createParams();
		    //自定义登录方式：
		    //测试阶段Userid可以填写手机
		    params.setUserid(userId); 
		    params.setAppKey(YTXConst.APP_ID);
		    params.setToken(YTXConst.APP_TOKEN);
		    // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
		    params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
		    // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
		    // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
		    // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
		    params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
		   
//		    //voip账号+voip密码方式：
		    //params.setUserid(voip);
		    //params.setPwd(passward);
		    //params.setAppKey(YTXConst.APP_ID);
		    //params.setToken(YTXConst.APP_TOKEN);
//		    // 设置登陆验证模式（是否验证密码）PASSWORD_AUTH-密码登录方式
		    //params.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
//		    // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
//		    // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
//		    // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
		    //params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
		    
		    // 如果是v5.1.8r开始版本建议使用ECDevice.setOnDeviceConnectListener（new ECDevice.OnECDeviceConnectListener()）
		    ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener(){

				@Override
				public void onConnect() {
					//Log.d("kb","onConnect");
				}

				@Override
				public void onConnectState(ECDevice.ECConnectState state, ECError error) {
					//Log.d("kb","ECConnectState:--->"+error.errorCode+";msg:---->"+error.errorMsg);
					KLog.d("kb","ECConnectState:--->"+error.errorCode+";msg:---->"+error.errorMsg);
					if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
		                   if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
		                	   //UtilToolkit.showToast("账号异地登陆");
		                	   EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_0,"账号异地登陆"));
		                   }else{
		                    //连接状态失败
		                	 //UtilToolkit.showToast("云通讯连接失败");
		                	 EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_0,"云通讯连接失败"));
		                   }
		                   SKuaidiApplication.getInstance().setLoginSuccess(false);
		                   //EventBus.getDefault().post(new MessageEvent(0,"云通讯连接失败"));
		                  return ;
		                }else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
		                    // 登陆成功
							KLog.d("kb","登陆成功");
		                	SKuaidiApplication.getInstance().setLoginSuccess(true);
		                	EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_1,"云通讯登录成功"));
		                }
				}

				@Override
				public void onDisconnect(ECError arg0) {
					KLog.d("kb",arg0.errorMsg);
					SKuaidiApplication.getInstance().setLoginSuccess(false);
					KLog.d("kb","云通讯连接断开");
					EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_2,"云通讯连接断开"));
				}});

		     // 如果是v5.1.8r开始版本建议使用ECDevice.setOnChatReceiveListener(new OnChatReceiveListener())
		    // 设置VOIP 自定义铃声路径
	        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
	        if(setupManager != null) {
	        	// 目前支持下面三种路径查找方式
	        	// 1、如果是assets目录则设置为前缀[assets://]
	        	setupManager.setInComingRingUrl(true, "assets://phonering.mp3");
	        	setupManager.setOutGoingRingUrl(true, "assets://phonering.mp3");
	        	setupManager.setBusyRingTone(true, "assets://playend.mp3");
	            // 2、如果是raw目录则设置为前缀[raw://]
	            // 3、如果是SDCard目录则设置为前缀[file://]
	        }
		    //验证参数是否正确，注册SDK
		    if(params.validate()) {
		       // 判断注册参数是否正确
				KLog.d("kb","登陆参数:AppKey--->"+params.getAppKey()+";Token:--->"+params.getToken()+";UserId:--->"+params.getUserId());
		        ECDevice.login(params);
		    }else{
				KLog.d("kb","YTX登录参数不合法!");
		    }
	}
public static void loginYTXByPassward(String voip,String passward){
	//KLog.d("LoginYTX@@@@@@@@@@@@");
	    //设置注册参数、设置通知回调监听
	    // 构建注册所需要的参数信息
	    ECInitParams params = ECInitParams.createParams();
	    //自定义登录方式：
	    //测试阶段Userid可以填写手机
	    //params.setUserid(userId); 
	    //params.setAppKey(YTXConst.APP_ID);
	    //params.setToken(YTXConst.APP_TOKEN);
	    // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
	    //params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
	    // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
	    // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
	    // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
	    //params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
	   
//	    //voip账号+voip密码方式：
	    params.setUserid(voip);
	    params.setPwd(passward);
	    params.setAppKey(YTXConst.APP_ID);
	    params.setToken(YTXConst.APP_TOKEN);
//	    // 设置登陆验证模式（是否验证密码）PASSWORD_AUTH-密码登录方式
	    params.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
//	    // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
//	    // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
//	    // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
	    params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
	    
	    // 如果是v5.1.8r开始版本建议使用ECDevice.setOnDeviceConnectListener（new ECDevice.OnECDeviceConnectListener()）
	    ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener(){

			@Override
			public void onConnect() {
				//Log.d("kb","onConnect");
			}

			@Override
			public void onConnectState(ECDevice.ECConnectState state, ECError error) {
				KLog.d("kb","ECConnectState:--->"+error.errorCode+";msg:---->"+error.errorMsg);
				if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
	                   if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
	                	   //UtilToolkit.showToast("账号异地登陆");
	                	   EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_0,"账号异地登陆"));
	                   }else{
	                    //连接状态失败
	                	 //UtilToolkit.showToast("云通讯连接失败");
	                	 EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_0,"云通讯连接失败"));
	                   }
	                   SKuaidiApplication.getInstance().setLoginSuccess(false);
	                   //EventBus.getDefault().post(new MessageEvent(0,"云通讯连接失败"));
	                  return ;
	                }else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
	                    // 登陆成功
						KLog.d("kb","登陆成功");
	                	SKuaidiApplication.getInstance().setLoginSuccess(true);
	                	EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_1,"云通讯登录成功"));
	                }
			}

			@Override
			public void onDisconnect(ECError arg0) {
				KLog.d("kb",arg0.errorMsg);
				SKuaidiApplication.getInstance().setLoginSuccess(false);
				KLog.d("kb","云通讯连接断开");
				EventBus.getDefault().post(new MessageEvent(EC_EVENT_STATE_2,"云通讯连接断开"));
			}});

	     // 如果是v5.1.8r开始版本建议使用ECDevice.setOnChatReceiveListener(new OnChatReceiveListener())
	    // 设置VOIP 自定义铃声路径
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        if(setupManager != null) {
        	// 目前支持下面三种路径查找方式
        	// 1、如果是assets目录则设置为前缀[assets://]
        	setupManager.setInComingRingUrl(true, "assets://phonering.mp3");
        	setupManager.setOutGoingRingUrl(true, "assets://phonering.mp3");
        	setupManager.setBusyRingTone(true, "assets://playend.mp3");
            // 2、如果是raw目录则设置为前缀[raw://]
            // 3、如果是SDCard目录则设置为前缀[file://]
        }
	    //验证参数是否正确，注册SDK
	    if(params.validate()) {
	       // 判断注册参数是否正确
			KLog.d("kb","登陆参数:AppKey--->"+params.getAppKey()+";Token:--->"+params.getToken()+";UserId:--->"+params.getUserId()+";Password:---->"+params.getPwd());
	        ECDevice.login(params);
	    }else{
			KLog.d("kb","YTX登录参数不合法!");
	    }
}
public static void logoutYTXForNext(){
	if(SKuaidiApplication.getInstance().isLoginSuccess()){
		ECDevice.logout(new ECDevice.OnLogoutListener() {
		    @Override
		    public void onLogout() {
		        // SDK 回调通知当前登出成功
		        // 这里可以做一些（与云通讯IM相关的）应用资源的释放工作
		        // 如（关闭数据库，释放界面资源和跳转等）
		    	//ECDevice.unInitial();
		    	SKuaidiApplication.getInstance().setLoginSuccess(false);
		    }
		});
	}
}

public static CallStatisticsInfo getCallDetailById(String callId){
	//获取音频通话信息
	return ECDevice.getECVoIPSetupManager().getCallStatistics(callId, false);
}

}
