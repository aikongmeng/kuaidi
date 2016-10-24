package com.kuaibao.skuaidi.base.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.progressbar.CustomProgressDialog;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;



/**
 * fragment基类   封装大多数抽象函数以及通用函数
 * @author xy
 *
 */
@SuppressLint("HandlerLeak")
public abstract class SkuaidiBaseFragment extends Fragment {
	private static final int ASYNCHRONOUS_PROCESSING_FINISH = 1;
	protected static final int INTERFACE_VERSION_OLD = 1;
	protected static final int INTERFACE_VERSION_NEW = 2;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ASYNCHRONOUS_PROCESSING_FINISH:
				((OnAsynchronous)(msg.obj)).onProcessingFinish();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	/**
	 * 服务器接口请求
	 * @param data 需要传到服务器的Json
	 * @param cookie 需要传到服务器的cookie  不需要则传入NULL
	 */
	protected void httpInterfaceRequest(JSONObject data,boolean isOffLineProcessing,int interfaceVersion){
		if(interfaceVersion == INTERFACE_VERSION_OLD){
			request(data,isOffLineProcessing);
		}else{
			requestNewInterface(data,isOffLineProcessing);
		}
	}
	
	
	/**
	 * 正式请求接口
	 * 当session失效时会后台重新登录获取session再次请求
	 * @param data
	 * @param cookie
	 * @param isOffLineProcessing
	 */
	private void request(final JSONObject data,final boolean isOffLineProcessing){
		try {
			data.put("pname", "androids");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(Utility.isNetworkConnected()==false && isOffLineProcessing == true){
			return;
		}
		HttpHelper httpHelper = new HttpHelper(
				new OnResultListener() {
					@Override
					public void onSuccess(String result, String sname) {
						try {
							JSONObject json = new JSONObject(result);
							String code = json.getString("code");
							String msg = json.getString("msg");
							if(code.equals("1103")||code.equals("5")||code.equals("6")||code.equals("401")){//SESSION失效
								BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
									@Override
									public void onReLoginSuccess() {
										request(data, isOffLineProcessing);
									}
								});
							}else if(code.equals("0")){
								onRequestSucess(sname,json);//请求成功
							}else{
								onRequestOldInterFaceFail(sname,msg,json.optJSONObject("data"));//请求异常
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(String result, JSONObject data_fail, String code) {
						onRequestOldInterFaceFail(data.optString("sname"),result,null);
					}
				},handler);
		try{
			httpHelper.getPart(data, Utility.getSession_id(getContext()));
		}catch(java.lang.NullPointerException e){
		}
	}
	
	private void requestNewInterface(final JSONObject data,final boolean isOffLineProcessing){
		if(Utility.isNetworkConnected()==false && isOffLineProcessing == true){
			//NetWorkService.setOutOfLineRequestData(data,cookie);
			return;
		}
		HttpHelper httpHelper = new HttpHelper(
				new OnResultListener() {
					@Override
					public void onSuccess(String result, String sname) {
						//Log.i("result", result);
						
						try {
							JSONObject json = new JSONObject(result);
							String code = json.getString("code");
							String msg = json.getString("msg");
							
							if(code.equals("1103")||code.equals("5")||code.equals("6")||code.equals("401")){//SESSION失效
//								SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//
//									@Override
//									public void todo() {
//										request(data, isOffLineProcessing);
//									}
//
//									@Override
//									public void faild() {
//
//									}
//								});
								BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
									@Override
									public void onReLoginSuccess() {
										request(data, isOffLineProcessing);
									}
								});
							}else if(code.equals("0")){
								JSONObject resultData = json.optJSONObject("data");
						if (resultData != null) {
							String status = resultData.optString("status");
							if (status.equals("success")) {
								JSONObject datas = resultData
										.optJSONObject("result");
								onRequestSucess(sname, datas);// 请求成功
							} else if (status.equals("fail")) {
								onRequestFail(sname,
										resultData.optString("desc"), "");
							}

						}
					} else {
								onRequestFail(sname,msg, "");//请求异常
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(String result, JSONObject data_fail, String code) {
						onRequestFail(data.optString("sname"),result, "");
					}
				},handler);
		try{
			httpHelper.getPart(data, Utility.getSession_id(getContext()));
		}catch(java.lang.NullPointerException e){
			
		}
	}

	/**
	 * 
	 * 异步处理接口
	 * @author pc
	 *
	 */
	public interface OnAsynchronous{
		void onProcessingFinish();//异步处理完成
	}
	
	/**
	 * 服务器接口请求成功
	 * @param code  Json返回code
	 */
	protected abstract void onRequestSucess(String sname,JSONObject result);

	/**
	 * 服务器接口请求失败  
	 * @param result 失败信息
	 * @param data_fail TODO
	 */
	protected abstract void onRequestFail(String sname,String result, String data_fail);
	
	/**
	 * 服务器老版本接口请求失败
	 * @param sname
	 * @param msg
	 * @param result
	 */
	protected abstract void onRequestOldInterFaceFail(String sname,String msg,JSONObject result);

	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	  /**
     * 分享
     * @param title : 标题
     * @param shareText : 分享内容
     * @param targetUrl :点击内容跳转的链接URL
     */
    public void openShare(String title,Map<String,String> shareTexts,String targetUrl,int drawableId){
    	UMShareManager.openShare(getActivity(), title, shareTexts, targetUrl, drawableId);
    }
	private CustomProgressDialog mProgressDialog;

	public void showProgressDialog(String msg){
		if(mProgressDialog==null){
			mProgressDialog = new CustomProgressDialog(getActivity());
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(true);
		}
		if(TextUtils.isEmpty(msg)){
			msg="加载中...";
		}
		mProgressDialog.setMessage(msg);
		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	public void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}
