package com.kuaibao.skuaidi.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.kuaibao.skuaidi.activity.PhotoShowActivity;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.entry.DeliverNoHistory;
import com.kuaibao.skuaidi.entry.NoticeInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.MD5;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ln 本类负责从服务器取得数据
 */
@SuppressLint("SimpleDateFormat")
public class KuaidiApi {
	public final static int VERSION = 8;

	// 登陆注册***********************************************************************************
	/**
	 * 用于在接口返回session失效的时候自动登录
	 * 
	 * @author gudd
	 * @created 2015/1/15
	 * @param context
	 * @param handler
	 */
	// public static void Login(final Context context, final Handler handler) {
	// JSONObject data = new JSONObject();
	// try {
	// data.put("pname", "androids");
	// data.put("sname", "wduser.login");
	// data.put("wduname", SkuaidiSpf.getLoginUser(context)
	// .getPhoneNumber());// 用户名-手机号
	// data.put("wdupwd", SkuaidiSpf.getLoginUser(context).getPwd());// 登录密码
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// HttpHelper httpHelper = new HttpHelper(context, new OnResultListener() {
	// @Override
	// public void onSuccess(String result, String sname) {
	// try {
	// JSONObject json = new JSONObject(result);
	// JSONObject body = json.getJSONObject("data");
	// String code = json.getString("code");
	// if (code.equals("0")) {
	// UserInfo user = new UserInfo();
	// Header[] h = (Header[]) SKuaidiApplication
	// .getInstance().onReceiveMsg("login",
	// "login_res_header");
	//
	// for (int i = 0; i < h.length; i++) {
	// //Log.i("header_1111",
	// h[i].getName() + "||||" + h[i].getValue());
	// if (h[i].getName().equals("Set-Cookie")) {
	// user.setSession_id(h[i].getValue().substring(
	// 11, 46));
	// break;
	// }
	// }
	// user.setUserName(body.getString("realname"));
	// user.setArea(body.getString("area"));
	// user.setBranch(body.getString("index_shop_name"));
	// user.setIndexShopId(body.getString("index_shop_id"));
	// user.setExpressNo(body.getString("brand"));
	// user.setUserId(body.getString("user_id"));
	//
	// String user_phoneNumber = SkuaidiSpf.getLoginUser(
	// context).getPhoneNumber();
	// String user_pwd = SkuaidiSpf.getLoginUser(context)
	// .getPwd();
	// SkuaidiSpf.saveLoginInfo(context, user.getSession_id(),
	// user_phoneNumber, user.getArea(),
	// user.getExpressNo(), user.getBranch(),
	// user.getIndexShopId(), user.getUserName(),
	// user.getUserId(), user_pwd, true);
	// SkuaidiSpf.setLastLoginName(context, user_phoneNumber);
	// SkuaidiSpf.setIsLogin(true);
	// } else {
	// UtilToolkit.showToast("登录时间已过期，请退出重新登录");
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// UtilToolkit.showToast("登录时间已过期，请退出重新登录");
	// }
	// }
	//
	// @Override
	// public void onFail(String result) {
	// }
	// }, handler);
	// Map<String, String> head = new HashMap<String, String>();
	// httpHelper.getPart(data, head);
	// }

	/**
	 * 请求验证码
	 * 
	 * @param context
	 * @param handler
	 * @param phoneNumber
	 *            手机号
	 * @param forget
	 *            "forget"表示修改密码时调用
	 */
	public static void getCheckInfo(final Context context, final Handler handler, String phoneNumber, String forget) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + phoneNumber + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			String str = "";

			if (forget.equals("forget")) {
				str = "<forget_password>1</forget_password>";
			}

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.register" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<from_channel>" + "android-s" + "</from_channel>")
					.append("<user_name>" + phoneNumber + "</user_name>" + str + "</body></request>");
			// //System.out.println("请求验证码：" + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String checkCodeResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						 //System.out.println("请求验证码：" + checkCodeResult);
						KLog.json(checkCodeResult);
						Message msg = new Message();
						msg.what = Constants.CHECKCODE_GET_OK;
						msg.obj = checkCodeResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.CHECKCODE_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * @param context
	 * @param handler
	 * @param phoneNumber
	 * @param checkCode
	 *            检查验证码是否正确
	 */
	public static void checkCode(final Context context, final Handler handler, String phoneNumber, String checkCode,
			String forget) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + phoneNumber + "," + checkCode + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			String str = "";

			if (forget.equals("forget")) {
				str = "<forget_password>1</forget_password>";
			}

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.register" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_name>" + phoneNumber + "</user_name>")
					.append("<verify_code>" + checkCode + "</verify_code>" + str + "</body></request>");

			//System.out.println("gudd  request :" + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String checkCodeVerifyResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						//System.out.println("gudd  result :" + checkCodeVerifyResult);
						JSONObject json = new JSONObject(checkCodeVerifyResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("register_status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							String userid = body.get("user_id").toString();
							UserInfo userInfo = new UserInfo();
							userInfo.setUserId(userid);
							msg.what = Constants.CHECKCODE_VERIFY_SUCCESS;
						} else {
							msg.what = Constants.CHECKCODE_VERIFY_FAILD;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.CHECKCODE_VERIFY_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 发送注册请求
	 * 
	 * @param context
	 * @param handler
	 * @param phoneNumber
	 *            手机号
	 * @param checkCode
	 *            验证码
	 * @param passport
	 *            密码
	 * @param realname
	 *            真实姓名
	 * @param area
	 *            快递员所属地区
	 * @param brand
	 *            品牌缩写
	 * @param indexShopName
	 *            网点名称
	 * @param indexShopId
	 *            网点Id
	 * @param forget
	 *            表示是否是修改密码
	 */
	public static void Regist(final Context context, final Handler handler, String phoneNumber, String checkCode,
			String passport, String realname, String area, String brand, String indexShopName, String indexShopId,
			String forget) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + phoneNumber + "," + checkCode + "," + passport + ","
					+ realname + "," + area + "," + brand + "," + indexShopName + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			String str = "";

			if (forget.equals("forget")) {
				str = "<forget_password>1</forget_password>";
			} else {
				str = "<realname>" + realname + "</realname>" + "<area>" + area + "</area>" + "<brand>" + brand
						+ "</brand>" + "<index_shop_name>" + indexShopName + "</index_shop_name>" + "<index_shop_id>"
						+ indexShopId + "</index_shop_id>";
			}

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.register" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_name>" + phoneNumber + "</user_name>")
					.append("<verify_code>" + checkCode + "</verify_code>")
					.append("<passport>" + passport + "</passport>").append(str + "</body></request>");

			// //System.out.println(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String registResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
								WSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.REGIST_GET_OK;
						msg.obj = registResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.REGIST_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 发送登录请求
	 * 
	 * @param context
	 * @param handler
	 * @param phoneNumber
	 *            用户名即手机号
	 * @param pwd
	 *            密码
	 * 
	 */
//	public static void login(final Context context, final Handler handler, String phoneNumber, String pwd) {
//		if (!Utility.isNetworkConnected()) {
//			Message msg = new Message();
//			msg.what = Constants.NETWORK_FAILED;
//			handler.sendMessage(msg);
//		} else {
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			String date = sDateFormat.format(new java.util.Date());
//
//			String partner_name = "androids";
//			String token = new MD5().toMD5(partner_name + "," + phoneNumber + "," + pwd + ","
//					+ "bac500a42230c8d7d1820f1f1fa9b578");
//
//			final String targetNameSpace = "urn:kuaidihelp_dts";
//			final String Method = "exec";
//			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
//			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
//			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
//					.append("<service_name>" + "wduser.login" + "</service_name>")
//					.append("<partner_name>" + partner_name + "</partner_name>")
//					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
//					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
//					.append("<user_name>" + phoneNumber + "</user_name>")
//					.append("<user_pwd>" + pwd + "</user_pwd>" + "</body></request>");
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						String loginResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
//								WSDL, soapAction, request);
//						//System.out.println(loginResult);
//						Message msg = new Message();
//						msg.what = Constants.LOGIN_GET_OK;
//						msg.obj = loginResult;
//						handler.sendMessage(msg);
//					} catch (Exception e) {
//						Message msg = new Message();
//						msg.what = Constants.LOGIN_GET_FAILD;
//						handler.sendMessage(msg);
//					}
//				}
//			}).start();
//		}
//	}

	// 登陆注册***********************************************************************************

	// 个人信息***********************************************************************************

	public static void isAcceptInvite(final Context context, final Handler handler, int isAccept) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());
			String token = new MD5().toMD5("");

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.migrate" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<is_accepted>" + isAccept + "</is_accepted>")
					.append("<username>" + SkuaidiSpf.getLoginUser().getPhoneNumber()
							+ "</username></body></request>");

			// //System.out.println(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String isAcceptResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						// //System.out.println(modifyUserInfoResult);

						JSONObject json = new JSONObject(isAcceptResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("update_status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.ACCEPT_SUCCESS;
						} else {
							msg.what = Constants.ACCEPT_FAILED;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.ACCEPT_FAILED;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}

	}

	/**
	 * 快递员修改个人信息 类型1：上传id+mobile请求验证码 为默认type值 类型2：上传id+mobile+验证码 对验证码进行验证
	 * 类型3:上传id+mobile+验证码+其他信息 修改快递员个人信息
	 * 
	 * @param context
	 * @param handler
	 * @param mobile
	 *            手机号
	 * @param verifyCode
	 *            验证码
	 * @param realName
	 *            姓名
	 * @param area
	 *            地区
	 * @param brand
	 *            品牌缩写
	 * @param indexShopName
	 *            网点名称
	 * @param indexShopId
	 *            网点ID
	 * @param type
	 *            见本注释3——5行
	 * @param cckvc 用来做为接口端是否判断验证码的参数
	 *            ，客户端用于人工注册时填写个人信息资料时候调用接口，接口再判断验证码的依据
	 */
	public static void modifyUserInfo(final Context context, final Handler handler, String mobile, String verifyCode,
			String realName, String area, String brand, String indexShopName, String indexShopId, final int type,
			String user_id, String cckvc) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());
			String token = new MD5().toMD5("");
			String str = "";

			if (type == 2) {
				str = "<verify_code>" + verifyCode + "</verify_code>";
			} else if (type == 3) {
				str = "<verify_code>" + verifyCode + "</verify_code>";
				if (!realName.equals("")) {
					str += "<realname>" + realName + "</realname>";
				}
				if (!indexShopName.equals("")) {
					str += "<area>" + area + "</area>" + "<brand>" + brand + "</brand>" + "<index_shop_name>"
							+ indexShopName + "</index_shop_name>" + "<index_shop_id>" + indexShopId
							+ "</index_shop_id>";
				}
			}

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.update" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<tbl_wd_user_id>" + user_id + "</tbl_wd_user_id>").append("<cckvc>" + cckvc + "</cckvc>")
					.append("<username>" + mobile + "</username>" + str + "</body></request>");

			//System.out.println("gudd modify request" + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String modifyUserInfoResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						//System.out.println("gudd modify result " + modifyUserInfoResult);

						JSONObject json = new JSONObject(modifyUserInfoResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("update_status").toString();
						Message msg = new Message();
						msg.arg1 = type;
						// //System.out.println(status);
						if (status.equals("success")) {
							msg.what = Constants.CHECKCODE_VERIFY_SUCCESS;
						} else {
							msg.what = Constants.CHECKCODE_VERIFY_FAILD;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.CHECKCODE_VERIFY_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}

	}

	/**
	 * 添加取派范围或服务说明
	 * 
	 * @param context
	 * @param handler
	 * @param contenttype
	 *            用来标识是取派范围接口，还是服务范围接口
	 * @param action
	 * @param content
	 * @param type
	 *            标识是提交还是读取 ：1是提交，2是读取
	 */
	public static void addpickup(final Context context, final Handler handler, String contenttype, String action,
			String content, final int type) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String partner_name = "androids";
			String date = sDateFormat.format(new Date());
			String token = new MD5().toMD5(partner_name + "," + contenttype + "," + action + "," + content + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");
			String str;
			if (content.equals("")) {
				str = "";
			} else {
				str = "<content>" + "<![CDATA[" + content + "]]>" + "</content>";
			}

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wduser.commmod" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<id>" + SkuaidiSpf.getLoginUser().getUserId() + "</id>")
					.append("<type>" + contenttype + "</type>")
					.append("<action>" + action + "</action>" + str + "</body></request>");

			//System.out.println("取派范围" + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String RangeResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
								WSDL, soapAction, request);
						//System.out.println("取派范围" + RangeResult);
						Message msg = new Message();
						msg.what = Constants.RANGE_GET_OK;
						msg.obj = RangeResult;
						msg.arg1 = type;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.RANGE_GET_FAILD;
						msg.arg1 = type;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}

	}

	/**
	 * 上传头像
	 * 
	 * @param context
	 * @param handler
	 * @param headerStream
	 *            头像转为流
	 */
	public static void uploadHeader(final Context context, final Handler handler, String headerStream) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "image.upload" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + "" + "</token></header><body>")
					.append("<image_prefix>" + "counterman_" + SkuaidiSpf.getLoginUser().getUserId()
							+ "</image_prefix>").append("<stream>" + headerStream + "</stream></body></request>");

			//System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String uploadHeaderResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						//System.out.println("上传头像：" + uploadHeaderResult);
						JSONObject json = new JSONObject(uploadHeaderResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						} else {
							msg.what = Constants.FAILED;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 根据手机号获取用户信息（用于快递员信息审核）
	 * 
	 * @param context
	 * @param handler
	 * @param mobile
	 *            手机号即用户名
	 */
	public static void getUserInfo(final Context context, final Handler handler, String mobile) {
		final String targetNameSpace = "urn:kuaidihelp_dts";
		final String method = "exec";
		final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
		final String soapAction = "urn:kuaidihelp_dts#dts#exec";
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new Date());

		final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
				.append("<service_name>" + "cm.info.get" + "</service_name>")
				.append("<partner_name>" + "androids" + "</partner_name>")
				.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
				.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
				.append("<cm_phone>" + mobile + "</cm_phone></body></request>");

		//System.out.println("注册2 request：" + request);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String getUserInfoResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, method,
							WSDL, soapAction, request);
					//System.out.println("注册2：" + getUserInfoResult);
					Message msg = new Message();
					msg.what = Constants.USER_INFO_GET_OK;
					msg.obj = getUserInfoResult;
					handler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = Constants.USER_INFO_GET_FAILED;
					handler.sendMessage(msg);
				}
			}
		}).start();

	}

	/**
	 * 向服务器发送位置信息，表明快递员在线
	 * 
	 * @param context
	 * @param handler
	 * @param longitude
	 * @param latitude
	 */
	public static void sendOnlineInfo(final Context context, final Handler handler, double longitude, double latitude) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "counterman.online" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + "" + "</token></header><body>")
					.append("<longitude>" + longitude + "</longitude>")
					.append("<latitude>" + latitude + "</latitude>")
					.append("<wd_user_id>" + SkuaidiSpf.getLoginUser().getUserId()
							+ "</wd_user_id></body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String sendOnlineInfoResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
					} catch (Exception e) {
						//System.out.println("在线信息失败！");
					}
				}
			}).start();
		}
	}

	// 个人信息***********************************************************************************

	// 派件模块***********************************************************************************

	/**
	 * 获取派件列表
	 * 
	 * @param context
	 * @param handler
	 * @param deliverNo
	 *            单号：可为空，空时表示获取所有列表
	 * @param status
	 *            筛选状态
	 * @param pageNum
	 * @param pageSize
	 */
	public static void getDeliveryList(final Context context, final Handler handler, String deliverNo, String status,
			int pageNum, int pageSize) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			String str = "";

			if (deliverNo != null) {
				str += "<deliver_no>" + deliverNo + "</deliver_no>";
			}

			if (status != null) {
				str += "<status>" + status + "</status>";

			}

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "delivery.list" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<cm_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</cm_id>" + str)
					.append("<page_num>" + pageNum + "</page_num>")
					.append("<page_size>" + pageSize + "</page_size></body></request>");

			//System.out.println("request interface  " + request);
			//System.out.println("page  " + pageNum + "   pagesize  " + pageSize);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String getDeliveryListResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						//System.out.println("派件" + getDeliveryListResult);

						JSONObject json = new JSONObject(getDeliveryListResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							//System.out.println("sss");
							msg.what = Constants.DELIVERY_LIST_GET_SUCCESS;
							msg.arg1 = body.getInt("total_page");
							msg.obj = body;
						} else {
							msg.what = Constants.DELIVERY_LIST_GET_FAILED;
							msg.obj = body.get("desc").toString();
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.DELIVERY_LIST_GET_FAILED;
						handler.sendMessage(msg);

					}
				}
			}).start();

		}
	}

	/**
	 * 派件模块，根据运单号获取客户手机号（目前支持中通）
	 * 
	 * @param context
	 * @param handler
	 * @param deliverNo
	 *            运单号
	 */
	public static void getNotifyPhone(final Context context, final Handler handler, String deliverNo) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "express_contact" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + "" + "</token></header><body>")
					.append("<deliver_no>" + deliverNo + "</deliver_no>")
					.append("<express_company>" + SkuaidiSpf.getLoginUser().getExpressNo()
							+ "</express_company></body></request>");
			// //System.out.println("我的请求信息是："+request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String getNotifyPhoneResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						//System.out.println("免费通知客户：" + getNotifyPhoneResult);
						Message msg = new Message();
						msg.what = Constants.NOTIFY_PHONE_GET_OK;
						msg.obj = getNotifyPhoneResult;
						handler.sendMessage(msg);
					} catch (Exception e) {

					}
				}
			}).start();

		}
	}

	/**
	 * 派件留言列表
	 * 
	 * @param context
	 * @param handler
	 * @param deliverNo
	 *            运单号
	 * @param type
	 *            类型，使用4不在分派件留言类型
	 */
	public static void getLiuyanListfromDeliverNoAct(final Context context, final Handler handler, String deliverNo,
			int type) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + deliverNo + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "waybill.list" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<brand>" + SkuaidiSpf.getLoginUser().getExpressNo() + "</brand>")
					.append("<order_number>" + deliverNo + "</order_number>")
					.append("<get_type>" + type + "</get_type>" + "</body></request>");

			// //System.out.println("留言中心：" + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String liuyanListfromdeliverNoResult = WebServiceHelper.getInstance().getPart(context,
								targetNameSpace, Method, WSDL, soapAction, request);

						// System.out
						// .println("留言" + liuyanListfromdeliverNoResult);
						Message msg = new Message();
						msg.what = Constants.LIUYANLIST_GET_OK;
						msg.obj = liuyanListfromdeliverNoResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.LIUYANLIST_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	public static void NotifyUsers(final Context context, final Handler handler, String content, int type,
			String batchOrderInfo, String picturestr) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());
			// if(picturestr==null)

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "inform.user" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<brand>" + SkuaidiSpf.getLoginUser().getExpressNo() + "</brand>")
					.append("<content>" + content + "</content>").append("<inform_type>" + type + "</inform_type>")
					.append("<batchOrderInfo>" + batchOrderInfo + "</batchOrderInfo>")
					.append("<pic>" + picturestr + "</pic>").append("<app_loc_id>" + "" + "</app_loc_id>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id></body></request>");
			//System.out.println("发送派件通知" + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String noticeUserResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						//System.out.println("照片返回：             " + noticeUserResult);

						Message msg = new Message();
						msg.what = Constants.SUCCESS;
						msg.obj = noticeUserResult;
						handler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	// 派件模块***********************************************************************************
	// 订单***********************************************************************************
	/**
	 * 获取订单列表
	 * 
	 * @param context
	 * @param handler
	 * @param userName
	 *            用户名
	 * @param requestDate
	 *            上次请求时间，0表示获取所有
	 * 
	 */
	// *****************************此接口没有使用*****************************
	public static void getOrder(final Context context, final Handler handler, String order_state, String userName,
			int page_num, int page_size, String requestDate) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + userName + "," + requestDate + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "wdorder.news" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<order_state>" + order_state + "</order_state>")
					.append("<user_name>" + userName + "</user_name>").append("<page_num>" + page_num + "</page_num>")
					.append("<page_size>" + page_size + "</page_size>")
					.append("<time>" + requestDate + "</time>" + "</body></request>");

			// //System.out.println("订单" + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String orderResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
								WSDL, soapAction, request);
						//System.out.println("订单" + orderResult);
						Message msg = new Message();
						msg.what = Constants.ORDER_GET_OK;
						if (orderResult.indexOf("%order") == -1) {
							msg.obj = orderResult + "%order";
						} else {
							msg.obj = orderResult;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.ORDER_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 删除订单（支持删除多个单号）
	 * 
	 * @param context
	 * @param handler
	 * @param deleteIds
	 *            Id列表
	 * 
	 */
	public static void deleteOrder(final Context context, final Handler handler, String deleteIds) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + deleteIds + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "order.del" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<user_role>3</user_role>")
					.append("<delids>" + deleteIds + "</delids>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>"
							+ "</body></request>");

			// //System.out.println(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String deleteOrderResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						JSONObject json = new JSONObject(deleteOrderResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						} else {
							msg.what = Constants.FAILED;
							msg.obj = body.get("desc").toString();
						}
						handler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 订单模块，上传运单号标识订单完成
	 * 
	 * @param context
	 * @param handler
	 * @param orderNO
	 *            订单号
	 * @param deliverNo
	 *            运单号
	 */
	public static void uploadDeliverNo(final Context context, final Handler handler, String orderNO, String deliverNo) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "order.set_express" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + "" + "</token></header><body>")
					.append("<order_number>" + orderNO + "</order_number>")
					.append("<express_number>" + deliverNo + "</express_number>")
					.append("<counterman_id>" + SkuaidiSpf.getLoginUser().getUserId()
							+ "</counterman_id></body></request>");

			// //System.out.println("扫描单号完成取件" + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String uploadDeliverNoResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						// //System.out.println("扫描单号完成取件" +
						// uploadDeliverNoResult);
						JSONObject json = new JSONObject(uploadDeliverNoResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						} else {
							msg.what = Constants.FAILED;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 获取订单详情（包括IM对话）
	 * 
	 * @param context
	 * @param handler
	 * @param OrderNo
	 */
	public static void getOrderImDetail(final Context context, final Handler handler, String OrderNo) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "order.im.detail" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<order_number>" + OrderNo + "</order_number>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>")
					.append("<user_role>2</user_role></body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String orderImDetailResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						//Log.d("iii", "订单详情：" + orderImDetailResult);
						Message msg = new Message();
						msg.what = Constants.ORDER_IM_DETAIL_GET_OK;
						msg.obj = orderImDetailResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.ORDER_IM_DETAIL_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 订单IM回复
	 * 
	 * @param context
	 * @param handler
	 * @param orderNO
	 * @param contentType
	 * @param content
	 * @param voiceLength
	 * @param type
	 *            回复客服or回复客户
	 */
	public static void getOrderImAdd(final Context context, final Handler handler, String orderNO,
			final int contentType, String content, long voiceLength, int type) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "order.im.add" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<order_number>" + orderNO + "</order_number>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>")
					.append("<user_role>2</user_role>").append("<content_type>" + contentType + "</content_type>")
					.append("<content>" + content + "</content>").append("<session_type>" + type + "</session_type>")
					.append("<voice_length>" + voiceLength + "</voice_length></body></request>");

			// //System.out.println("回复客户   " + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String orderImAddResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, WSDL, soapAction, request);
						// //System.out.println("回复客户：" + orderImAddResult);
						Message msg = new Message();
						msg.what = Constants.ORDER_IM_ADD_GET_OK;
						msg.arg1 = contentType;
						msg.obj = orderImAddResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.ORDER_IM_ADD_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 请求变更订单状态，旧接口不再使用
	 * 
	 * @param context
	 * @param handler
	 * @param phoneNumber
	 * @param orderid
	 * @param accept
	 * 
	 */
	// public static void stateChange(final Context context,
	// final Handler handler, String phoneNumber, String orderid,
	// final int accept) {
	// ConnectivityManager manager = (ConnectivityManager) context
	// .getApplicationContext().getSystemService(
	// Context.CONNECTIVITY_SERVICE);
	// NetworkInfo networkInfo = manager.getActiveNetworkInfo();
	// if (manager == null || networkInfo == null
	// || !networkInfo.isAvailable()) {
	// Utility.showToast(context, "网络连接错误,请稍后重试!");
	// } else {
	// SimpleDateFormat sDateFormat = new SimpleDateFormat(
	// "yyyy-MM-dd hh:mm:ss");
	// String date = sDateFormat.format(new java.util.Date());
	//
	// String partner_name = "androids";
	// String token = new MD5().toMD5(partner_name + "," + phoneNumber
	// + "," + orderid + "," + accept + ","
	// + "bac500a42230c8d7d1820f1f1fa9b578");
	//
	// final String targetNameSpace = "urn:kuaidihelp_dts";
	// final String Method = "exec";
	// final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
	// final String soapAction = "urn:kuaidihelp_dts#dts#exec";
	// final StringBuffer request = new StringBuffer(
	// "<?xml version='1.0' encoding='utf-8' ?><request><header>")
	// .append("<service_name>" + "wdorder.state"
	// + "</service_name>")
	// .append("<partner_name>" + partner_name + "</partner_name>")
	// .append("<time_stamp>" + date + "</time_stamp>")
	// .append("<version>" + VERSION + "</version>")
	// .append("<format>" + "json" + "</format>")
	// .append("<token>" + token + "</token></header><body>")
	// .append("<user_name>" + phoneNumber + "</user_name>")
	// .append("<order_id>" + orderid + "</order_id>")
	// .append("<accept>" + accept + "</accept>"
	// + "</body></request>");
	// // //System.out.println(request);
	//
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// stateChangeResult = WebServiceHelper.getInstance()
	// .getPart(context, targetNameSpace, Method,
	// WSDL, soapAction, request);
	// Message msg = new Message();
	// msg.what = Constants.STATE_CHANGE_GET_OK;
	// msg.arg1 = accept;
	// msg.obj = stateChangeResult;
	// handler.sendMessage(msg);
	// } catch (Exception e) {
	// Message msg = new Message();
	// msg.what = Constants.STATE_CHANGE_GET_FAILD;
	// handler.sendMessage(msg);
	// }
	// }
	// }).start();
	// }
	// }

	// 订单***********************************************************************************
	// 消息***********************************************************************************
	/**
	 * 发送留言
	 * 
	 * @param context
	 * @param handler
	 * @param userid
	 *            用户Id
	 * @param content
	 *            留言内容
	 * @param targetId
	 *            对应详情Id
	 * 
	 */
	// public static Object sendLiuyan(final Context context, final Handler
	// handler,
	// final int contentType, String userid, String content,
	// String targetId, long voiceLength){
	// JSONObject data = new JSONObject();
	// SimpleDateFormat sDateFormat = new SimpleDateFormat(
	// "yyyy-MM-dd hh:mm:ss");
	// String date = sDateFormat.format(new java.util.Date());
	// String token = new MD5().toMD5("androids" + "," + "3," + userid
	// + "," + contentType + "," + content + "," + targetId + ","
	// + voiceLength + "," + "bac500a42230c8d7d1820f1f1fa9b578");
	// try {
	// data.put("sname", "liuyan.add");
	// data.put("time_stamp", date);
	// data.put("version", VERSION);
	// data.put("format", "json");
	// data.put("token", token);
	// data.put("user_role", 3);
	// data.put("user_id", userid);
	// data.put("content", content);
	// data.put("content_type", contentType);
	// data.put("voice_length", voiceLength);
	// data.put("target_id", targetId);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return data;
	// }

	public static void sendLiuyan(final Context context, final Handler handler, final int contentType, String userid,
			String content, String targetId, long voiceLength) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + "3," + userid + "," + contentType + "," + content + ","
					+ targetId + "," + voiceLength + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8'?><request><header>")
					.append("<service_name>" + "liuyan.add" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_role>3</user_role>").append("<user_id>" + userid + "</user_id>")
					.append("<content>" + content + "</content>")
					.append("<content_type>" + contentType + "</content_type><params>")
					.append("<voice_length>" + voiceLength + "</voice_length>")
					.append("<target_id>" + targetId + "</target_id></params></body></request>");

			//System.out.println("liuyan    " + request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String sendTxtLiuyanResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.SEND_LIUYAN_GET_OK;
						msg.arg1 = contentType;
						msg.obj = sendTxtLiuyanResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.SEND_LIUYAN_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 留言详情
	 * 
	 * @param context
	 * @param handler
	 * @param targetId
	 *            Id列表
	 * 
	 */
	public static void getliuyancontent(final Context context, final Handler handler, String targetId) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + "3," + targetId + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "liuyan.detail" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_role>3</user_role>")
					.append("<role_ident>" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "</role_ident>")
					.append("<target_id>" + targetId + "</target_id></body></request>");
			//System.out.println("留言详情" + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String liuyanContentResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						//System.out.println("留言详情：" + liuyanContentResult);
						Message msg = new Message();
						msg.what = Constants.LIUYANCONTENT_GET_OK;
						msg.obj = liuyanContentResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.LIUYANCONTENT_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 留言列表
	 * 
	 * @param context
	 * @param handler
	 * @param userid
	 *            用户Id
	 */
	public static void getLiuyanList(final Context context, final Handler handler, String userid, int page_num,
			int page_size) {

		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + userid + "," + "bac500a42230c8d7d1820f1f1fa9b578");
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "liuyan.list" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_role>" + 3 + "</user_role>").append("<user_id>" + userid + "</user_id>")
					.append("<liuyan_type>" + 0 + "</liuyan_type>").append("<page_num>" + page_num + "</page_num>")
					.append("<page_size>" + page_size + "</page_size>" + "</body></request>");
			// //System.out.println("liuyan" + request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String liuyanlistResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						//System.out.println("详情：" + liuyanlistResult);
						Message msg = new Message();
						msg.what = Constants.LIUYANLIST_GET_OK;
						msg.obj = liuyanlistResult + "%msg";
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.LIUYANLIST_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 筛选留言
	 * 
	 * @param context
	 * @param handler
	 * @param startTime
	 * @param endTime
	 */
	public static void getLiuyanListInfo(final Context context, final Handler handler, String startTime,
			String endTime, String userMobile, String orderNumber, int pageNum, int pageSize) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		}
		final String targetNameSpace = "urn:kuaidihelp_dts";
		final String method = "exec";
		final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
		final String soapAction = "urn:kuaidihelp_dts#dts#exec";
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new Date());

		String start_time_str = null;
		String end_time_str = null;

		if (startTime != null) {
			start_time_str = "<start_time>" + startTime + "</start_time>";
		}

		if (endTime != null) {
			end_time_str = "<end_time>" + endTime + "</end_time>";
		}

		final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
				.append("<service_name>" + "liuyan.list" + "</service_name>")
				.append("<partner_name>" + "androids" + "</partner_name>")
				.append("<time_stamp>" + date + "</time_stamp>")
				.append("<version>" + VERSION + "</version>")
				.append("<format>" + "json" + "</format>")
				.append("<token>" + "" + "</token></header><body>")
				.append("<page_num>" + pageNum + "</page_num>")
				.append("<page_size>" + pageSize + "</page_size>")
				.append("<liuyan_type>" + 0 + "</liuyan_type>")
				.append("<user_mobile>" + userMobile + "</user_mobile>")
				.append("<order_number>" + orderNumber + "</order_number>")
				.append("<user_role>" + 3 + "</user_role>")
				.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>" + start_time_str
						+ end_time_str + "</body></request>");
		// //System.out.println(request);
		// .append("<page_num>"+pageNum+"</page_num>")
		// .append("<page_size>"+""+"</page_size></body></request>");

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String getLiuyanListResultByTime = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
							method, WSDL, soapAction, request);

					// //System.out.println("反馈："+getLiuyanListResultByTime);

					JsonXmlParser.parseScreeningLiuyanList(context, handler, getLiuyanListResultByTime);

				} catch (Exception e) {
					e.printStackTrace();
					// Message msg = new Message();
					// msg.what = Constants.DELIVERY_LIST_GET_FAILED;//
					// ////////////////////////////////////////////需要重新写一个
					// handler.sendMessage(msg);
				}
			}
		}).start();

	}

	/**
	 * @param context
	 * @param handler
	 * @param deleteIds
	 *            删除留言
	 */
	public static void deleteLiuyan(final Context context, final Handler handler, String deleteIds) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + deleteIds + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "liuyan.del" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<user_role>3</user_role>")
					.append("<delids>" + deleteIds + "</delids>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>"
							+ "</body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String deleteLiuyanResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						// //System.out.println(deleteLiuyanResult);

						JSONObject json = new JSONObject(deleteLiuyanResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						} else {
							msg.what = Constants.FAILED;
							msg.obj = body.get("desc").toString();
						}
						handler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 获取投诉内容
	 * 
	 * @param context
	 * @param handler
	 * 
	 */
	public static void getComplain(final Context context, final Handler handler, int presentPage, int pageSize) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + SkuaidiSpf.getLoginUser().getUserId()
					+ ",yonghu," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "tousu.list" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<uid>" + SkuaidiSpf.getLoginUser().getUserId() + "</uid>")
					// .append("<uid>3545</uid>")
					.append("<page_num>" + presentPage + "</page_num>")
					.append("<page_size>" + pageSize + "</page_size>")
					.append("<type>yewuyuan</type>" + "</body></request>");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String complainResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						// //System.out.println(complainResult);
						Message msg = new Message();
						msg.what = Constants.COMPLAIN_GET_SUCCESS;
						msg.obj = complainResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.COMPLAIN_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 删除投诉
	 * 
	 * @param context
	 * @param handler
	 * @param deleteIds
	 *            投诉详情Id列表
	 * 
	 */
	public static void deleteComplain(final Context context, final Handler handler, String deleteIds) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + deleteIds + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "tousu.del" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<user_role>3</user_role>")
					.append("<delids>" + deleteIds + "</delids>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>"
							+ "</body></request>");

			// //System.out.println(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String deleteComplainResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);

						// //System.out.println(deleteComplainResult);
						JSONObject json = new JSONObject(deleteComplainResult);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						} else {
							msg.what = Constants.FAILED;
							msg.obj = body.get("desc").toString();
						}
						handler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 获取投诉详情
	 * 
	 * @param context
	 * @param handler
	 * @param ComplainId
	 *            投诉详情Id
	 */
	public static void getComplainContent(final Context context, final Handler handler, String ComplainId) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + ComplainId + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "tousu.detail" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token></token></header><body>")
					.append("<user_role>" + "3" + "</user_role>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>")
					.append("<id>" + ComplainId + "</id></body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String complainContentResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.COMPLAIN_CONTENT_GET_SUCCESS;
						msg.obj = complainContentResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.COMPLAIN_CONTENT_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 获取通知列表
	 * 
	 * @param context
	 * @param handler
	 */
	public static void getNotice(final Context context, final Handler handler) {
		final String targetNameSpace = "urn:kuaidihelp_dts";
		final String method = "exec";
		final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
		final String soapAction = "urn:kuaidihelp_dts#dts#exec";
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new Date());

		final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
				.append("<service_name>" + "cm.notice.list" + "</service_name>")
				.append("<partner_name>" + "androids" + "</partner_name>")
				.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
				.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
				.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id></body></request>");

		// //System.out.println("通知" + request);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String noticeResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, method,
							WSDL, soapAction, request);
					// //System.out.println("通知：" + noticeResult);

					JSONObject json = new JSONObject(noticeResult);
					JSONObject response = json.getJSONObject("response");
					JSONObject body = response.getJSONObject("body");
					String status = body.get("status").toString();
					Message msg = new Message();
					if (status.equals("success")) {
						msg.what = Constants.SUCCESS;
						List<NoticeInfo> notices;
						JSONArray noticelist = body.optJSONArray("cmNoticeList");
						if (noticelist != null) {
							notices = new ArrayList<NoticeInfo>();

							for (int i = 0; i < noticelist.length(); i++) {
								NoticeInfo notice = new NoticeInfo();
								JSONObject noticeObj = (JSONObject) noticelist.get(i);
								notice.setNoticeId(noticeObj.get("notice_id").toString());
								notice.setContent(noticeObj.get("content").toString());
								notice.setCreatTime(Long.parseLong(noticeObj.get("create_time").toString()));
								notice.setUnRead(noticeObj.optInt("read_status", 0));
								notices.add(notice);
							}
							msg.obj = notices;
						} else {
							msg.obj = null;
						}
					} else {
						msg.what = Constants.FAILED;

					}
					handler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = Constants.GET_FAID;
					handler.sendMessage(msg);
				}
			}
		}).start();
		// }
	}

	/**
	 * 投诉详情
	 * 
	 * @param context
	 * @param handler
	 * @param noticeId
	 *            投诉Id
	 */
	public static void getNoticeDetail(final Context context, final Handler handler, String noticeId) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);

		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "cm.notice.detail" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<notice_id>" + noticeId + "</notice_id>")
					.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id></body></request>");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String noticeDetail = WebServiceHelper.getInstance().getPart(context, targetNameSpace, method,
								WSDL, soapAction, request);
						JSONObject json = new JSONObject(noticeDetail);
						JSONObject response = json.getJSONObject("response");
						JSONObject body = response.getJSONObject("body");
						String status = body.get("status").toString();
						Message msg = new Message();
						if (status.equals("success")) {
							msg.what = Constants.SUCCESS;
						}
						handler.sendMessage(msg);
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	/**
	 * 获取咨讯中心列表
	 * 
	 * @param context
	 * @param handler
	 * @param type
	 */

	public static void getInformation(final Context context, final Handler handler, String type) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {

			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String partner_name = "androids";
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String wSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "news.getlist" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token></token></header><body>")
					.append("<page>1</page>").append("<number>50</number>").append("<type>" + type + "</type>")
					.append("<detail></detail></body></request>");
			// getInforresult = "";
			// //System.out.println(request);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String getInforresult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, wSDL, soapAction, request);

						Message msg = new Message();
						msg.what = Constants.INFO_GET_SUCCESS;
						msg.obj = getInforresult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.INFO_GET_FAID;
						handler.sendMessage(msg);
						e.printStackTrace();
					}

				}
			}).start();

		}

	}

	/**
	 * 自助审核提交
	 * 
	 * @param context
	 * @param handler
	 * @param deliver 审核所需5个派件单号
	 */

	public static void setSelfaudit(final Context context, final Handler handler, String deliver) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String partner_name = "androids";
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String wSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "cm.self.verify.apply" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token></token></header><body>")
					.append("<cm_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</cm_id>")
					.append("<pj>" + deliver + "</pj></body></request>");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String setSelfauditresult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, wSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.INFO_GET_SUCCESS;
						msg.obj = setSelfauditresult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.INFO_GET_FAID;
						handler.sendMessage(msg);
						e.printStackTrace();
					}

				}
			}).start();

		}

	}

	/**
	 * 审核结果
	 * 
	 * @param context
	 * @param handler
	 */

	public static void getAuditstatus(final Context context, final Handler handler) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String partner_name = "androids";
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String wSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "cm.self.verify.get" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token></token></header><body>")
					.append("<cm_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</cm_id></body></request>");
			// getAuditstatusresult = "";
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String getAuditstatusresult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								method, wSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.TYPE_AUDIT_SUCCESS;
						msg.obj = getAuditstatusresult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.TYPE_AUDIT_FAIL;
						handler.sendMessage(msg);
						e.printStackTrace();
					}

				}
			}).start();

		}

	}

	/**
	 * 短连接
	 * 
	 * @param context
	 * @param handler
	 */

	public static void getshorturl(final Context context, final Handler handler, String url) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String partner_name = "androids";
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String wSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "short.url" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format>").append("<token></token></header><body>")
					.append("<url>" + url + "</url></body></request>");
			// getshorturl = "";
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String getshorturl = WebServiceHelper.getInstance().getPart(context, targetNameSpace, method,
								wSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.SHORT_URL_SUCCESS;
						msg.obj = getshorturl;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.SHORT_URL_FAIL;
						handler.sendMessage(msg);
						e.printStackTrace();
					}

				}
			}).start();

		}

	}

	/**
	 * 获取短连接 短信分享内的
	 */
	public static void getShortUrl(final Context context, final Handler handler, String order_number) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);

		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "shorturl.get" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
					.append("<format>" + "json" + "</format></header><body>").append("<no>" + order_number + "</no>")
					.append("<brand>" + SkuaidiSpf.getLoginUser().getExpressNo() + "</brand></body></request>");
			// request = "";
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String getShortUtl = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
								WSDL, soapAction, request);
						//Log.i("iii", "url" + getShortUtl);
						Message msg = new Message();
						msg.what = Constants.SHARE_GET_OK;
						msg.obj = getShortUtl;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.SHARE_GET_FAILED;
						handler.sendMessage(msg);
					}

				}

			}).start();
		}

	}

	// 消息***********************************************************************************

	// 超派***********************************************************************************
	/**
	 * 给定详细地址判断是否属于派送范围
	 * 
	 * @param context
	 * @param handler
	 * @param area_id
	 *            区域id
	 * @param address
	 *            详细地址
	 * @param express
	 *            快递品牌
	 */
	public static void getOverarea(final Context context, final Handler handler, String area_id, String address,
			String express) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + area_id + "," + address + "," + express + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "shop.match.app" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<area_id>" + area_id + "</area_id>").append("<detail>" + address + "</detail>")
					.append("<express_company>" + express + "</express_company>" + "</body></request>");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String overareaResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						// //System.out.println(overareaResult);
						Message msg = new Message();
						msg.what = Constants.OVERAREA_GET_OK;
						msg.obj = overareaResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.OVERAREA_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 根据行政区域和品牌信息查询网点，主要用在快递员选网点处
	 * 
	 * @param context
	 * @param handler
	 * @param area_id
	 *            行政区域id
	 * @param address
	 *            详细地址 此处传值为“”
	 * @param express
	 *            快递公司
	 */
	public static void getBranch(final Context context, final Handler handler, String area_id, String address,
			String express) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + area_id + "," + address + "," + express + ",,1,1" + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "shop.match" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<area_id>" + area_id + "</area_id>").append("<detail>" + address + "</detail>")
					.append("<is_unque/><page_size>" + 100 + "</page_size>").append("<channel></channel>")
					.append("<express_company>" + express
					// +"zt"
							+ "</express_company>" + "</body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String branchResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace, Method,
								WSDL, soapAction, request);

						// //System.out.println(branchResult);

						Message msg = new Message();
						msg.what = Constants.BRANCH_GET_OK;
						msg.obj = branchResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.BRANCH_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 根据行政区域和品牌查询派送范围
	 * 
	 * @param context
	 * @param handler
	 * @param area_id
	 *            行政區域ID
	 * @param express
	 *            品牌
	 */
	public static void getSendRange(final Context context, final Handler handler, String area_id, String express) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);

		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + area_id + "," + express + ","
					+ "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "shop.match.app" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<area_id>" + area_id + "</area_id>")
					.append("<express_company>" + express + "</express_company>" + "</body></request>");
			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String sendRangeResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						// //System.out.println(branchResult);
						Message msg = new Message();
						msg.what = Constants.SEND_RANGE_GET_OK;
						msg.obj = sendRangeResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.SEND_RANGE_GET_FAILD;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	// 超派***********************************************************************************
	// 快递查询***********************************************************************************
	/**
	 * 运单跟踪
	 * 
	 * @param context
	 * @param handler
	 * @param deliverNO
	 *            运单号
	 */
	public static void findExpress(final Context context, final Handler handler, String deliverNO) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);

		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(SkuaidiSpf.getLoginUser().getExpressNo() + "," + deliverNO + ","
					+ partner_name + "," + "bac500a42230c8d7d1820f1f1fa9b578");

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "multinfo" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>")
					.append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>")
					.append("<token>" + token + "</token></header><body>")
					.append("<express_company>" + SkuaidiSpf.getLoginUser().getExpressNo()
							+ "</express_company>")
					.append("<deliver_no>" + deliverNO + "</deliver_no>")
					.append("<params><option>GetException</option><option>ElapsedTime</option><option>GetStatus</option><option>GetWuliu_phone</option></params></body></request>");

			//System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String findExpressResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						//System.out.println("运单跟踪" + findExpressResult);
						Message msg = new Message();
						msg.what = Constants.FIND_EXPRESS_GET_OK;
						msg.obj = findExpressResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.FIND_EXPRESS_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	/**
	 * 根据网点Id获取网点信息
	 * 
	 * @param context
	 * @param handler
	 * @param BranchNo
	 *            网点Id
	 */
	public static void getBranchInfo(final Context context, final Handler handler, String BranchNo) {
		if (!Utility.isNetworkConnected()) {
			Message msg = new Message();
			msg.what = Constants.NETWORK_FAILED;
			handler.sendMessage(msg);

		} else {
			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new Date());

			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "shop.infor" + "</service_name>")
					.append("<partner_name>" + "androids" + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + "" + "</token></header><body>")
					.append("<shop>" + BranchNo + "</shop>")
					.append("<infor_type>" + "comm" + "</infor_type></body></request>");

			// //System.out.println(request);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String getBranchInfo = WebServiceHelper.getInstance().getPart(context, targetNameSpace, method,
								WSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.BRANCH_GET_OK;
						msg.obj = getBranchInfo;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.BRANCH_GET_FAILD;
						handler.sendMessage(msg);
					}

				}
			}).start();
		}
	}

	// 快递查询***********************************************************************************

	// 其他***********************************************************************************

	/**
	 * 将百度推送回传的数据上传到服务器
	 * 
	 * @param context
	 * @param handler
	 * @param imei
	 * @param userId
	 * @param ChannelId
	 * @param appId
	 * @param baiduUserId
	 * @param isOpen
	 * 
	 */
	public static void getBadiduPush(final Context context, final Handler handler, String imei, String userId,
			String ChannelId, String appId, String baiduUserId, boolean isOpen) {
		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (manager == null || networkInfo == null || !networkInfo.isAvailable()) {
			UtilToolkit.showToast( "网络连接错误,请稍后重试!");
		} else {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String partner_name = "androids";
			String token = new MD5().toMD5(partner_name + "," + imei + "," + userId + "," + ChannelId + ","
					+ baiduUserId + ",android,courier," + isOpen + "bac500a42230c8d7d1820f1f1fa9b578");
			int tempIsOpen = 1;
			if (!isOpen) {
				tempIsOpen = 0;
			} else {
				tempIsOpen = 1;
			}

			final String targetNameSpace = "urn:kuaidihelp_dts";
			final String Method = "exec";
			final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
			final String soapAction = "urn:kuaidihelp_dts#dts#exec";
			final StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='utf-8' ?><request><header>")
					.append("<service_name>" + "push.channel" + "</service_name>")
					.append("<partner_name>" + partner_name + "</partner_name>")
					.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + VERSION + "</version>")
					.append("<format>" + "json" + "</format>").append("<token>" + token + "</token></header><body>")
					.append("<user_id>" + userId + "</user_id>").append("<imei>" + imei + "</imei>")
					.append("<model>" + Build.MODEL + "</model>").append("<channel_id>" + ChannelId + "</channel_id>")
					.append("<app_id>" + appId + "</app_id>")
					.append("<baidu_user_id>" + baiduUserId + "</baidu_user_id>")
					.append("<device_type>android</device_type>").append("<user_type>courier</user_type>")
					.append("<is_open>" + tempIsOpen + "</is_open>" + "</body></request>");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String baiduPushResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
								Method, WSDL, soapAction, request);
						Message msg = new Message();
						msg.what = Constants.BAIDU_PUSH_GET_OK;
						msg.obj = baiduPushResult;
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = Constants.BAIDU_PUSH_GET_FAID;
						handler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	// UserCenterActivity.java***********************************************************************************

	/**
	 * 添加&获得 用户业务类型（接口） action : add(添加) & show（显示）
	 * 
	 * @param context
	 * @param handler
	 * @param action
	 * @param businesstype
	 */
	public static void UserBusinessType(final Context context, final Handler handler, final String user_id,
			final String action, final String businesstype, final String registType) {

		JSONObject data = new JSONObject();
		try {
			data.put("sname", "counterman.businesstype");
			data.put("pname", "androids");
			data.put("id", user_id);
			data.put("action", action);
			data.put("businesstype", businesstype);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				Message msg = new Message();
				if (sname.equals("counterman.businesstype")) {
					//System.out.println("用户业务类型返回：" + result);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						String data = json.getString("data");

						if (code.equals("0")) {
							if (action.equals("add")) {
								if (registType.equals("xiugai")) {
									msg.what = Constants.USER_XIUGAI_PERSON_INFO_OK;
								} else if (registType.equals("modify")) {
									if (data.equals("1")) {// 修改成功
										SkuaidiSpf.saveUserBusinessType(context, businesstype);
										msg.what = Constants.USER_MODIFY_BUSYNESSTYPE_SUCCESS;
									} else {
										UtilToolkit.showToast("修改失败");
									}
								} else {
									UtilToolkit.showToast(result);
								}
							} else if (action.equals("show")) {
								SkuaidiSpf.saveUserBusinessType(context, data);
								msg.what = Constants.USER_GETBUSINESSTYPE_OK;
							} else {
								//System.out.println("异常");
							}
						} else {
						}
						handler.sendMessage(msg);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				Message msg = new Message();
				msg.what = Constants.USER_GETBUSINESSTYPE_FAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}, new Handler());

		httpHelper.getPart(data);

	}

	/**
	 * 添加&获得 用户身份信息（接口） action : add(添加) & show（显示）
	 */
	public static void UserRoleType(final Context context, final Handler handler, final String action,
			final String roletype) {
		UserInfo userInfo = new UserInfo();
		JSONObject data = new JSONObject();

		try {
			data.put("sname", "counterman.roletype");
			data.put("pname", "androids");
			data.put("id", SkuaidiSpf.getLoginUser().getUserId());
			data.put("action", action);
			data.put("roletype", roletype);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				// progressDialog.dismiss();
				if (sname.equals("counterman.roletype")) {

					//System.out.println("用户身份返回：" + result);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						String data = json.getString("data");

						if (code.equals("0")) {
							if (action.equals("add")) {
								if (data.equals("1")) {// 修改成功
									SkuaidiSpf.saveUserRoleType(context, roletype);
									Message msg = new Message();
									msg.what = Constants.USER_MODIFY_ROLETYPE_SUCCESS;
									handler.sendMessage(msg);
								} else {
									UtilToolkit.showToast("修改失败");
								}
							} else if (action.equals("show")) {
								SkuaidiSpf.saveUserRoleType(context, data);
								Message msg = new Message();
								msg.what = Constants.USER_GETROLETYPE_OK;
								handler.sendMessage(msg);
							} else {
								//System.out.println("异常");
							}
						} else {
							UtilToolkit.showToast("修改失败");
						}

					} catch (JSONException e) {
						e.printStackTrace();
						UtilToolkit.showToast("异常");
					}
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				Message msg = new Message();
				msg.what = Constants.USER_GETROLETYPE_FAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}, new Handler());

		httpHelper.getPart(data);
	}

	/**
	 * 添加&获取评论内容 &点赞
	 * @param handler
	 * @param tucaoId
	 * @param deal
	 * @param page
	 * @param position
	 * @param channel
	 */
	public static void TucaoPinglunDetail(final Handler handler, String content,
										  final String tucaoId, final String deal, String replay_shop, String detail_id, String page,
										  final int position, String channel) {

		JSONObject data = new JSONObject();

		try {
			data.put("sname", "tucao_android_s");
			data.put("pname", "androids");
			data.put("content", content);
			data.put("channel", channel);
			data.put("deal", deal);
			data.put("id", tucaoId);
			data.put("reply_shop", replay_shop);
			data.put("detail_id", detail_id);
			data.put("page", page);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {

				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if (code.equals("0")) {
						Message msg = new Message();
						switch (deal){
							case "get":
								String data = json.optString("data");
								if (!Utility.isEmpty(data)) {
									JsonXmlParser.paseTucaoDetail(handler, result);
								}else{
									msg.what = Constants.CIRCLE_EXPRESS_GET_TUCAOINFO_DETAIL_FAIL;// 获取数据失败
								}
								break;
							case "add":
								msg.what = Constants.CIRCLE_EXPRESS_ADD_PINGLUN_SUCCESS;// 上传成功
								break;
							case "good":
								msg.what = Constants.CIRCLE_EXPRESS_GOOD_SUCCESS;// 点赞成功
								msg.obj = position;
								break;
						}
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						String message;
						switch (deal){
							case "get":
								message = json.getString("msg");
								msg.obj = message;
								msg.what = Constants.CIRCLE_EXPRESS_GET_TUCAOINFO_DETAIL_FAIL;// 获取数据失败
								break;
							case "add":
								message = json.getString("msg");
								msg.obj = message;
								msg.what = Constants.CIRCLE_EXPRESS_ADD_PINGLUN_FAIL;// 上传失败
								break;
							case "good":
								msg.what = Constants.CIRCLE_EXPRESS_GOOD_FAIL;// 点赞失败
								break;
						}
						handler.sendMessage(msg);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				// UtilToolkit.showToast(result);
				Message msg = new Message();
				msg.what = Constants.CIRCLE_GETDATA_FAIL;
				handler.sendMessage(msg);
				// //System.out.println("更新数据失败，请手动刷新");
			}
		}, new Handler());

		Map<String, String> head = new HashMap<String, String>();
		head.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
		httpHelper.getPart(data, head);
	}

	/**
	 * 获取用户短信发送量、回复量和失败量
	 * 
	 * @param context
	 * @param handler
	 */
	public static void getSendMessageCount(Context context, final Handler handler) {

		JSONObject data = new JSONObject();

		try {
			data.put("sname", "informcount.get");
			data.put("pname", "androids");
			data.put("cm_app_id", SkuaidiSpf.getLoginUser().getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("请求的数据：" + data);

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
			Message msg = new Message();

			@Override
			public void onSuccess(String result, String sname) {
				//System.out.println("返回的数据：" + result);
				DeliverNoHistory deliverNoHistory = null;
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if (code.equals("0")) {
						//System.out.println("返回成功");
						JSONObject data = json.getJSONObject("data");
						String status = data.getString("status");
						if (status.equals("success")) {
							msg.what = Constants.GET_MESSAGE_COUNT_SUCCESS;
							deliverNoHistory = new DeliverNoHistory();
							deliverNoHistory.setSentCount(data.getString("sentCount").toString());// 保存发送量
							//System.out.println("返回的数据是：" + data.getString("sentCount").toString());
							deliverNoHistory.setReplyCount(data.getString("replyCount").toString());// 保存回复量
							deliverNoHistory.setFailCount(data.getString("failCount").toString());// 保存失败量
							msg.obj = deliverNoHistory;
							handler.sendMessage(msg);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					//System.out.println("出现异常了");
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				//System.out.println("接口出现异常了");
			}

		}, new Handler());
		httpHelper.getPart(data);

	}

	/**
	 * 获得二维码名片访问量和收藏量（接口）
	 * 
	 * @author 顾冬冬
	 */
	public static void getQrcodeCardFangwenShoucang(final Context context, final Handler handler) {
		JSONObject data = new JSONObject();
		try {
			data.put("pname", "androids");
			data.put("sname", "counterman.qcode.pv");

			JSONObject param = new JSONObject();
			param.put("cm_id", SkuaidiSpf.getLoginUser().getUserId());
			param.put("phone", SkuaidiSpf.getLoginUser().getPhoneNumber());
			data.put("param", param);
		} catch (JSONException e) {
			e.printStackTrace();
		}


//		showProgressDialog("");

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				if (sname.equals("counterman.qcode.pv")) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						//System.out.println("code:" + code);
						if (code.equals("0")) {
							JsonXmlParser.parseVisitBisinessCard(context, handler, result);
						} else {
							UtilToolkit.showToast("加载失败");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
			}
		}, handler);
		httpHelper.getPart(data);
	}

	/**
	 * 获得账户余额
	 * 
	 * @author gudd
	 * @param context
	 * @param handler
	 * @param sname
	 *            接口名：（withdraw.account 获取账户余额）（withdraw.detail 获取账户流水信息详情）
	 * @param action
	 *            （getinfo）（getlist）
	 */
	public static Object getAccountExplain(Context context, Handler handler, String sname, String action) {
		JSONObject data = new JSONObject();
		try {
			data.put("pname", "androids");
			data.put("sname", sname);
			data.put("action", action);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 获得账户余额
	 * 
	 * @author gudd
	 * @param context
	 * @param handler
	 * @param sname
	 *            接口名：（withdraw.account 获取账户余额）（withdraw.detail 获取账户流水信息详情）
	 * @param action
	 *            （getinfo）（getlist）
	 */

	public static void getAccountExplain2(final Context context, final Handler handler, String sname, String action) {

		JSONObject data = new JSONObject();
		try {
			data.put("pname", "androids");
			data.put("sname", sname);
			data.put("action", action);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				Message msg = new Message();
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if (code.equals("0")) {
						if (sname.equals("withdraw.account")) {
							JsonXmlParser.paseMyFundsAccount(context, handler, result);
						} else if (sname.equals("withdraw.detail")) {
							JsonXmlParser.paseMyfundsAccountDetail(context, handler, result);
						} else {

						}
					} else if (code.equals("1103")) {
						UtilToolkit.showToast("登录超时，请重新登录");
					} else {
						UtilToolkit.showToast("登录超时，请重新登录");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				UtilToolkit.showToast("登录超时，请重新登录");
			}

		}, handler);
		Map<String, String> head = new HashMap<String, String>();
		head.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
		httpHelper.getPart(data, head);
	}

	/**
	 * @topic 吐槽id 获取的是这条吐槽的信息
	 */
	public static void getTuCaoData(final Handler handler, String topic_id, String channel) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "tucao_android_s");
			data.put("pname", "androids");
			data.put("channel", channel);
			data.put("deal", "get");
			data.put("topic_id", topic_id);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		//System.out.println("我在请求：：：：：" + data);

		HttpHelper httpHelper = new HttpHelper(new HttpHelper.OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				//System.out.println("我在返回：：：：：" + result);
				Message msg = new Message();
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					String data = json.optString("data");
					if (code.equals("0")) {
						if (!Utility.isEmpty(data) && !data.equals("[]")) {
							JsonXmlParser.parseCircleGetHead(handler, result);
						}
					} else {
						msg.what = Constants.GETDATA_FAIL;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = Constants.GETDATA_FAIL;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				// Utility.showToast(context, result);
				Message msg = new Message();
				msg.what = Constants.CIRCLE_EXPRESS_FAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}, handler);
		//System.out.println("session_id    " + SkuaidiSpf.getLoginUser().getSession_id());

		Map<String, String> head = new HashMap<>();
		head.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
		httpHelper.getPart(data, head);

	}

	/**
	 * 添加模板，删除模板，修改模板，获取模板列表
	 * 
	 * @author 顾冬冬
	 * @param context
	 * @param handler
	 * @param action
	 *            添加 -add; 修改-update; 删除-delete; 获取列表-getlist
	 */
	public static Object ModelManager(Context context, Handler handler, String title, String action, String content,
			String tid) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform.template");
			data.put("pname", "androids");
			data.put("title", title);
			data.put("action", action);
			data.put("content", content);
			if (!KuaiBaoStringUtilToolkit.isEmpty(tid)) {
				data.put("tid", tid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 免费派件通知单条发送传递接口
	 * 
	 * @author 顾冬冬
	 * @param brand
	 *            -快递品牌
	 * @param order_number
	 *            -运单号--这里传编号
	 * @param user_phone
	 *            -发送的手机号
	 * @param dh
	 *            -添加的单号
	 * @param pic
	 *            -图片
	 * @param user_wallet
	 *            ：1-同意使用可提现金额消费短信;0-不同意使用可提现金额消费短信
	 * @param app_ver
	 *            -APP版本号
	 * @param sendTime
	 * @return
	 */
	public static Object freeSendMessageSingle(String brand, int order_number,String user_phone, String dh, String pic, String user_wallet, int app_ver, String template_id, long sendTime) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform.user2");
			data.put("pname", "androids");
			data.put("brand", brand);
			data.put("order_number", order_number);
			data.put("user_phone", user_phone);
			data.put("dh", dh);
			data.put("pic", pic);
			data.put("use_wallet", "1");
			data.put("app_ver", app_ver);
			data.put("template_id", template_id);
			data.put("send_time", sendTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 获取外块订单
	 * 
	 * @author 顾冬冬
	 * @param latitude 经度
	 * @param longitude 纬度
	 * @param deal 获取方法
	 * @param page 页数
	 * @param pagelen 每页多少条
	 * @param get_done 已经完成的订单
*            （传1）
	 * @param get_my 已经接了但未完成的订单
	 */
	public static Object getOutSide(String latitude, String longitude,
									String deal, String page, String pagelen, String get_done, String get_my) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "weirenwu_s");
			data.put("pname", "androids");
			data.put("lng", longitude);
			data.put("lat", latitude);
			data.put("deal", deal);
			data.put("page", page);
			data.put("pagelen", pagelen);
			data.put("get_done", get_done);
			data.put("get_my", get_my);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 抢外单和释放外单接口
	 * 
	 * @author 顾冬冬
	 * @param deal 方法
	 * @param id 任务ID
	 * @return
	 */
	public static Object robOutsideBlock(String deal, String id) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "weirenwu_s");
			data.put("pname", "androids");
			data.put("deal", deal);
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 添加店铺
	 * 
	 * @author gudd
	 * @created 2015/1/14
	 * @param sname 接口名称1
	 *            -business.shop.add--2-business.shop.update
	 * @param shop_id 店铺ID
	 *            --用于店铺更新信息时使用
	 * @param shop_name 店铺名称
	 * @param shop_address 店铺地址
	 * @param shop_type 店铺类型
	 * @param shop_desc 店铺说明
	 * @param phone 店铺联系电话
	 * @param revenue_demands 跑腿收入类型
	 *            1：5+5%元;2:10元
	 * @param shop_logo 店铺商标
	 * @return data
	 */
	public static Object AddNewShop(String sname, String shop_id, String shop_name,
									String shop_address, String shop_type, String shop_desc, String phone, String revenue_demands,
									String shop_logo) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", sname);
			data.put("pname", "androids");
			data.put("shop_id", shop_id);
			data.put("shop_name", shop_name);
			data.put("shop_address", shop_address);
			data.put("shop_type", shop_type);
			data.put("shop_desc", shop_desc);
			data.put("phone", phone);
			data.put("revenue_demands", revenue_demands);
			data.put("shop_logo", shop_logo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 获取店铺列表
	 * 
	 * @author gudd
	 * @param page_size
*            每页条数
	 * @param page_num
	 */
	public static Object getBusinessShopList(String page_size, String page_num) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "business.shop.list");
			data.put("pname", "androids");
			data.put("role", "2");// 参数2这里表示业务员的意思
			data.put("page_size", page_size);//
			data.put("page_num", page_num);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 获取店铺详情
	 * 
	 * @author gudd
	 * @param shop_id 店铺ID
	 * @param baidu_uid
	 */
	public static Object getBusinessShopDetail(String shop_id,
											   String baidu_uid) {
		JSONObject data = new JSONObject();

		try {
			data.put("sname", "business.shop.detail");
			data.put("pname", "androids");
			data.put("shop_id", shop_id);
			data.put("baidu_uid", baidu_uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 上传或下载店铺商品图片
	 * 
	 * @author gudd
	 * @created 2015/1/15
	 * @param action
	 *            上传或下载
	 * @param shop_id
 *            店铺ID
	 * @param pic_arr
	 */
	public static Object uploadBusinessShopPic(String action,
											   String shop_id, String pic_arr) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "business.shop.pic");
			data.put("pname", "androids");
			data.put("act", action);
			data.put("shop_id", shop_id);
			data.put("photo", pic_arr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * 删除店铺商品图片
	 * 
	 * @author gudd
	 * @param context
	 * @param handler
	 * @param imageID
	 */
	public static Object deleteShopImage(final Context context, final Handler handler, String imageID) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "business_shop_pic");
			data.put("pname", "androids");
			data.put("act", "delete");
			data.put("spid", imageID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * @author gudd 获取其它店铺列表-默认返回每页15条数据
	 * 
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @param page_num 页数
	 * @param page_size 每页条数
	 * @return
	 */
	public static Object getAnotherShopList(String latitude, String longitude,
											String page_num, String page_size) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "business.othershop.list");
			data.put("pname", "androids");
			data.put("lng", longitude);
			data.put("lat", latitude);
			data.put("page_num", page_num);
			data.put("page_size", page_size);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 *  gudd 设置短信记录条目中的签收状态
	 * @param handler
	 * @param inform_id 条目ID
	 * @param position 点击的条目的下标
	 */
	public static void setSignInStatus(final Handler handler, String inform_id, final int position) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "update.sign");
			data.put("pname", "androids");
			data.put("inform_id", inform_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				Message msg = new Message();
				try {
					JSONObject jsonObj = new JSONObject(result);
					String code = jsonObj.getString("code");
					if (code.equals("0")) {
						JSONObject data = jsonObj.getJSONObject("data");
						String status = data.getString("status");
						if (status.equals("success")) {
							JSONObject resultObj = data.getJSONObject("result");
							UtilToolkit.showToast(resultObj.getString("retStr"));
							msg.what = Constants.GET_SIGN_IN_STATUS_SUCCESS;// 签收更新成功，通知界面改变
							msg.arg1 = position;// 点击的条目的下标

						} else {
							UtilToolkit.showToast(data.getString("desc"));
						}
					} else {
						UtilToolkit.showToast(jsonObj.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
				//System.out.println("gudd signIn result:" + result);
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {

			}
		}, handler);

		Map<String, String> head = new HashMap<>();
		head.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
		httpHelper.getPart(data, head);
	}

	public static final void deleteAddShopImgs(final Handler handler, String imgId) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "business_shop_pic");
			data.put("pname", "androids");
			data.put("act", "delete");
			data.put("spid", imgId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				try {
					JSONObject json = new JSONObject(result);
					String code = json.optString("code");
					if (code.equals("0")) {
						JSONObject data = json.getJSONObject("data");
						String status = data.optString("status");
						if (status.equals("success")) {
							Message msg = new Message();
							msg.what = PhotoShowActivity.DELETE_IMAGE_SUCCESS;
							handler.sendMessage(msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
			}
		}, handler);

		Map<String, String> head = new HashMap<>();
		head.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
		httpHelper.getPart(data, head);
	}
}
