package com.kuaibao.skuaidi.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.business.nettelephone.SKuaidiVoIPCallActivity;
import com.kuaibao.skuaidi.business.nettelephone.YTXConst;
import com.kuaibao.skuaidi.business.nettelephone.util.Encryption;
import com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.menu.CommonDialogListMenu;
import com.kuaibao.skuaidi.dialog.menu.CommonDialogListMenu.OnDialogMenuClickListener;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragmentActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AcitivityTransUtil {

	public static final int NORMAI_CALL_DIALOG=0;//通常情况下的对话框
	public static final int LIUYAN_CALL_DIALOG=1;//留言的对话框
public static void showChooseTeleTypeDialog(final Activity activity,final String callName,final String norphone,final int type,final String... params){
	final String phoneFormat=AllInterface.formatCall(norphone);
	CommonDialogListMenu dialog=new CommonDialogListMenu(activity).builder().addClickListener(new OnDialogMenuClickListener() {
		@Override
		public void cancel() {
			if(LIUYAN_CALL_DIALOG==type && "visible".equals(params[0])){
					Intent intent = new Intent(activity, SendMSGActivity.class);
					intent.putExtra("liuyanPhone", norphone);
					activity.startActivityForResult(intent, 333);
			}
		}
		
		@Override
		public void callNormal() {
			UMShareManager.onEvent(SKuaidiApplication.getContext(), "nornalcall_count", "NormalCallCount", "1");
			Intent intent = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phoneFormat));
			activity.startActivity(intent);
		}
		
		@Override
		public void callNet() {
			UMShareManager.onEvent(SKuaidiApplication.getContext(), "netcall_count", "NetCallCount", "1");
			final JSONObject data = new JSONObject();
			try {
				data.put("pname", "androids");
				data.put("sname", "netcall/netcall_prep");
				data.put("from_app", "1");
				String tag= NetTeleUtils.getMyTelePrefer(SkuaidiSpf.getLoginUser().getPhoneNumber());
				if("0".equals(tag)){
					data.put("call_back","0");
				}else if("1".equals(tag)){
					data.put("call_back","1");
				}else if("2".equals(tag)){
					if(NetTeleUtils.isWifi(activity.getApplicationContext())){
						data.put("call_back","0");
					}else{
						data.put("call_back","1");
					}
				}
				if(LIUYAN_CALL_DIALOG==type){
					data.put("msg_id", params[1]);
					data.put("msg_type", params[2]);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(activity instanceof SkuaiDiBaseActivity){
				((SkuaiDiBaseActivity)activity).showProgressDialog("");
			}else if(activity instanceof RxRetrofitBaseActivity){
				((RxRetrofitBaseActivity)activity).showProgressDialog("");
			}else if(activity instanceof RxRetrofitBaseFragmentActivity){
				((RxRetrofitBaseFragmentActivity)activity).showProgressDialog("");
			}
			HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
				@Override
				public void onSuccess(String result, String sname) {
					if(activity instanceof SkuaiDiBaseActivity){
						((SkuaiDiBaseActivity)activity).dismissProgressDialog();
					}else if(activity instanceof RxRetrofitBaseActivity){
						((RxRetrofitBaseActivity)activity).dismissProgressDialog();
					}else if(activity instanceof RxRetrofitBaseFragmentActivity){
						((RxRetrofitBaseFragmentActivity)activity).dismissProgressDialog();
					}
					String msg = "";
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						msg = json.getString("msg");
						if (code.equals("0")) {
							JSONObject jsonresult =json.optJSONObject("data");
							if("netcall/netcall_prep"==sname){
								String token = null;
								String disNum = null;
								int time;
								String voip=null;
								String passward=null;
								voip=jsonresult.optString("acc","");
								passward=jsonresult.optString("pwd","");
								disNum=jsonresult.optString("disNum","");
								time=Integer.parseInt(jsonresult.optString("avail_time","0"));
								if(TextUtils.isEmpty(voip) || TextUtils.isEmpty(passward)){
									token=jsonresult.optString("token","");
									if(!TextUtils.isEmpty(token)){
										if(!SkuaidiSpf.getAudioPermission()){
											showNoPermissionDialog(activity);
											return;
										}
										Intent callAction = new Intent(activity , SKuaidiVoIPCallActivity.class);
								        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NAME , callName);
								        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NUMBER , phoneFormat);
								        callAction.putExtra(ECDevice.CALLTYPE ,  ECVoIPCallManager.CallType.DIRECT);
								        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_OUTGOING_CALL , true);
								        callAction.putExtra("avail_time", time);
								        callAction.putExtra("disNum",disNum);
								        callAction.putExtra("token",token);
								        activity.startActivity(callAction);
									}else{
										UtilToolkit.showToast("连接断开,请尝试重新登录");
									}
								}else{
									try {
										String decodeStr = Encryption.desEncrypt(YTXConst.SECRET_KEY,YTXConst.IV,voip);
										//Log.i("kb", "decodeStr解密:--->"+decodeStr);
										if(!TextUtils.isEmpty(decodeStr)){
											if(!SkuaidiSpf.getAudioPermission()){
												showNoPermissionDialog(activity);
												return;
											}
											Intent callAction = new Intent(activity , SKuaidiVoIPCallActivity.class);
									        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NAME , callName);
									        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NUMBER , phoneFormat);
									        callAction.putExtra(ECDevice.CALLTYPE ,  ECVoIPCallManager.CallType.DIRECT);
									        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_OUTGOING_CALL , true);
									        callAction.putExtra("avail_time", time);
									        callAction.putExtra("disNum",disNum);
									        callAction.putExtra("voip",decodeStr.length()>14?decodeStr.substring(0,14):decodeStr);
									        callAction.putExtra("passward",passward);
									        activity.startActivity(callAction);
										}else{
											UtilToolkit.showToast("连接断开,请尝试重新登录");
										}
									} catch (Exception e1) {
										e1.printStackTrace();
										UtilToolkit.showToast("连接断开,请尝试重新登录");
									}
								}
							}
						}else{
								onFail(msg, json.optJSONObject("data"), code);
						}
					} catch (JSONException e) {
						onFail("token获取失败", null, "");
					}
				}
				@Override
				public void onFail(String result, JSONObject data_fail, String code) {
					if(activity instanceof SkuaiDiBaseActivity){
						((SkuaiDiBaseActivity)activity).dismissProgressDialog();
					}else if(activity instanceof RxRetrofitBaseActivity){
						((RxRetrofitBaseActivity)activity).dismissProgressDialog();
					}else if(activity instanceof RxRetrofitBaseFragmentActivity){
						((RxRetrofitBaseFragmentActivity)activity).dismissProgressDialog();
					}
					if("10001".equals(code)){
						SkuaidiDialog skuaidiDialog = new SkuaidiDialog(activity);
						skuaidiDialog.setTitle("提示");
						skuaidiDialog.setContent("账户余额不足\n请充值后再继续操作");
						skuaidiDialog.isUseEditText(false);
						skuaidiDialog.setPositionButtonTitle("充值");
						skuaidiDialog.setNegativeButtonTitle("取消");
						skuaidiDialog.setPosionClickListener(new PositonButtonOnclickListener() {
							@Override
							public void onClick(View v) {
								Intent mIntent = new Intent(activity, TopUpActivity.class);
								activity.startActivity(mIntent);
							}
						});
						skuaidiDialog.showDialog();
					}else{
						if(activity instanceof SkuaiDiBaseActivity){
							((SkuaiDiBaseActivity)activity).dismissProgressDialog();
						}else if(activity instanceof RxRetrofitBaseActivity){
							((RxRetrofitBaseActivity)activity).dismissProgressDialog();
						}else if(activity instanceof RxRetrofitBaseFragmentActivity){
							((RxRetrofitBaseFragmentActivity)activity).dismissProgressDialog();
						}
						if (result.contains("<!DOCTYPE html>")) {
							result = "网络无连接";
						}
						//Log.i("iii", "请求失败：方法名-->" + data.optJSONObject("sname") + "\n" + "返回数据-->" + result);
						UtilToolkit.showToast( result);
					}
				}
			}, new Handler());
			httpHelper.getPartV3(data);
		}
		
	}).setCancleable(true).serOnTouchOutSide(true);
	if(LIUYAN_CALL_DIALOG==type && "visible".equals(params[0])){
		dialog.setCancelText("发短信");
		dialog.setCancelVisible(true);
	}
	dialog.showDialog();
}

	private static void showNoPermissionDialog(final Activity activity){
		SkuaidiDialog skuaidiDialog = new SkuaidiDialog(activity);
		skuaidiDialog.setTitle("提示");
		skuaidiDialog.setContent("麦克风无声音，可能是录音权限被禁。请到手机的设置-应用-快递员-权限管理-录音-设为允许");
		skuaidiDialog.isUseEditText(false);
		skuaidiDialog.setPositionButtonTitle("去设置");
		skuaidiDialog.setNegativeButtonTitle("取消");
		skuaidiDialog.setPosionClickListener(new PositonButtonOnclickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intent);
				activity.finish();
				//android.os.Process.killProcess(android.os.Process.myPid());
				SKuaidiApplication.getInstance().exit();
				//System.exit(0);
			}
		});
		skuaidiDialog.showDialog();
	}
}
