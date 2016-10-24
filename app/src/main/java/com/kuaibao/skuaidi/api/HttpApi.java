package com.kuaibao.skuaidi.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class HttpApi {

	private static final int ASYNCHRONOUS_PROCESSING_FINISH = 1;
	private Context context;
	protected static final int INTERFACE_VERSION_OLD = 1;
	protected static final int INTERFACE_VERSION_NEW = 2;
	private static final int NETWORK_DOWN = 3;
	private static final int NETWORK_UP = 4;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ASYNCHRONOUS_PROCESSING_FINISH:
				((OnAsynchronous) (msg.obj)).onProcessingFinish();
				break;
//			case SKuaidiSMSBroadcastListener.SMS_SEND_SUCCESS:
//				OnSMSSendSuccess();
//				break;
//			case SKuaidiSMSBroadcastListener.SMS_SEND_FAIL:
//				OnSMSSendFail();
//				break;
			case NETWORK_DOWN:
				onNetWorkChanged(false);
				break;
			case NETWORK_UP:
				onNetWorkChanged(true);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 服务器接口请求
	 * 
	 * @param data
	 *            需要传到服务器的Json
	 *            需要传到服务器的cookie 不需要则传入NULL
	 */
	public void httpInterfaceRequest(JSONObject data, boolean isOffLineProcessing, int interfaceVersion) {
		if (interfaceVersion == INTERFACE_VERSION_OLD) {
			request(data, isOffLineProcessing);
		} else if (interfaceVersion == INTERFACE_VERSION_NEW) {
			requestNewInterface(data, isOffLineProcessing);
		} else if (interfaceVersion == HttpHelper.SERVICE_V1) {
			requestV3(data);
		}
	}
	
	
	/**
	 * 正式请求接口 当session失效时会后台重新登录获取session再次请求
	 * 
	 * @param data
	 * @param isOffLineProcessing
	 */

	private void request(final JSONObject data, final boolean isOffLineProcessing) {
		try {
			data.put("pname", "androids");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
			@Override
			public void onSuccess(String result, String sname) {
				//Log.i("result", result);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					String msg = json.getString("msg");
					if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//						SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//
//							@Override
//							public void todo() {
//								request(data, isOffLineProcessing);
//							}
//
//							@Override
//							public void faild() {
//
//							}
//						});
						BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
							@Override
							public void onReLoginSuccess() {
								request(data, isOffLineProcessing);
							}
						});
					} else if (code.equals("0")) {

						onRequestSuccess(sname, msg, result, "");// 请求成功

					} else {
						onRequestOldInterFaceFail(code, sname, msg, json.optJSONObject("data"));// 请求异常
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				if (result.contains("<!DOCTYPE html>")) {
					result = "网络无连接";
				}
				JSONObject json;
				String code1 = Constants.SPACE;
				try {
					json = new JSONObject(result);
					code1 = json.getString("code");
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					onRequestOldInterFaceFail(code1, data.optString("sname"), result, null);
				}
			}
		}, handler);
		httpHelper.getPart(data, Utility.getSession_id(context));
	}
	
	private void requestNewInterface(final JSONObject data, final boolean isOffLineProcessing) {
		try {
			data.put("pname", "androids");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HttpHelper httpHelper = new HttpHelper(new HttpHelper.OnResultListener() {
			@Override
			public void onSuccess(String result, String sname) {

				String msg = "";
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					msg = json.getString("msg");
					if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//						SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//							/**
//							 * 再次请求
//							 */
//							@Override
//							public void todo() {
//								requestNewInterface(data, isOffLineProcessing);
//							}
//
//							@Override
//							public void faild() {
//
//							}
//						});
						BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
							@Override
							public void onReLoginSuccess() {
								requestNewInterface(data, isOffLineProcessing);
							}
						});
					} else if (code.equals("0")) {
						JSONObject resultData = json.optJSONObject("data");
						if (resultData == null) {
							onRequestFail("", sname, json.toString(), data.optString("act"), null);
							return;
						}
						String act = resultData.optString("act");
						if ("scan.to.qf".equals(act)) {// 全峰不支持批量上传
							onRequestSuccess(sname, msg, resultData.toString(), act);// 请求成功
							return;
						}
						String status = resultData.getString("status");
						if (status.equals("success")) {
							String datas = resultData.optString("result");
							onRequestSuccess(sname, msg, datas, act);// 请求成功
						} else if (status.equals("fail")) {
							if ("scan.zt.verify".equals(sname)) {
								onRequestFail("", sname, resultData.toString(), act, null);
							} else {
								info = resultData.optString("confirm");
								String desc = resultData.optString("desc");
								if (Utility.isEmpty(desc)) {
									JSONObject result1 = resultData.optJSONObject("result");
									if (result1 != null) {
										desc = result1.optString("retStr");
									}
								}
								onRequestFail("", sname, desc, act, null);
							}
						}
					} else {
						onRequestFail("", sname, msg, data.optString("act"), null);// 请求异常
					}
				} catch (JSONException e) {
					onRequestFail("", sname, result, data.optString("act"), null);// 请求异常
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {

				if (result.contains("<!DOCTYPE html>")) {
					result = "网络无连接";
				}
				onRequestFail("", data.optString("sname"), result, data.optString("act"), null);
			}
		}, handler);
		httpHelper.getPart(data, Utility.getSession_id(SKuaidiApplication.getInstance()));
	}


	public void requestV2(final JSONObject data) {
		try {
			data.put("pname", "androids");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
			@Override
			public void onSuccess(String result, String sname) {
				KLog.d("接口sname:--->" + sname + ";接口返回Result:--->" + result);
				String msg = "";
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					msg = json.getString("msg");
					if (code.equals("0")) {
						onRequestSuccess(sname, msg, json.optString("data"), data.optString("act"));
					} else if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//						SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//							/**
//							 * 再次请求
//							 */
//							@Override
//							public void todo() {
//								requestV2(data);
//							}
//
//							@Override
//							public void faild() {
//
//							}
//						});
						BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
							@Override
							public void onReLoginSuccess() {
								requestV2(data);
							}
						});
					} else {
						try {
							onFail(msg, json.optJSONObject("data"), code);
						} catch (Exception e) {
							onFail(msg, null, code);
						}
					}

				} catch (Exception e) {
					onFail(result, null, "");
				}

			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				if (result.contains("<!DOCTYPE html>")) {
					result = "请连接网络";
				}
				onRequestFail(code, data.optString("sname"), result, data.optString("act"), data_fail==null? "":data_fail.toString());
			}
		}, handler);
		httpHelper.getPart(data, Utility.getSession_id(SKuaidiApplication.getInstance()));
	}
	
	

	private void requestV3(final JSONObject data) {
		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
			@Override
			public void onSuccess(String result, String sname) {
				String msg = "";
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					msg = json.getString("msg");
					if (code.equals("0")) {
						onRequestSuccess(sname, msg, json.optString("data"), data.optString("act"));
					} else if (code.equals("1011")) {
//						SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//							/**
//							 * 再次请求
//							 */
//							@Override
//							public void todo() {
//								requestV3(data);
//							}
//
//							@Override
//							public void faild() {
//
//							}
//						});
						BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
							@Override
							public void onReLoginSuccess() {
								requestV3(data);
							}
						});
					} else {
						onFail(msg, json.optJSONObject("data"), "");
					}

				} catch (Exception e) {
					onFail(result, null, "");
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {

				if (result.contains("<!DOCTYPE html>")) {
					result = "网络无连接";
				}
				onRequestFail("", data.optString("sname"), result, data.optString("act"), null);
			}
		}, handler);
		httpHelper.getPartV3(data);
	}

	String info;

	/**
	 * 
	 * 异步处理接口
	 * 
	 * @author pc
	 * 
	 */
	public interface OnAsynchronous {
		void onAsynchronousFunction();// 异步处理

		void onProcessingFinish();// 异步处理完成
	}

	/**
	 * 服务器接口请求成功
	 * 
	 *            Json返回code
	 */
	protected abstract void onRequestSuccess(String sname, String msg, String result, String act);

	/**
	 * 服务器接口请求失败
	 * @param code
	 * @param result
	 *            失败信息
	 * @param data_fail 可能包含的数据
	 */
	protected abstract void onRequestFail(String code, String sname, String result, String act, String data_fail);

	/**
	 * 服务器老版本接口请求失败
	 * 
	 * @param sname
	 * @param msg
	 * @param result
	 */

	protected abstract void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result);

	/**
	 * 短信发送成功
	 */
	protected void OnSMSSendSuccess() {

	}

	/**
	 * 短信发送失败
	 */
	protected void OnSMSSendFail() {

	}

	protected String getAnotherInfo() {
		return info;
	}

	protected void onNetWorkChanged(boolean isNetWorkUp) {
	}

}
