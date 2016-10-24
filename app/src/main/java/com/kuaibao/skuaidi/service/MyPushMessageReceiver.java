package com.kuaibao.skuaidi.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.LiuyanCenterActivity;
import com.kuaibao.skuaidi.activity.LiuyanDetailActivity;
import com.kuaibao.skuaidi.activity.LoadWebInformationActivity;
import com.kuaibao.skuaidi.activity.NoticeDetailActivity;
import com.kuaibao.skuaidi.activity.OrderInfoActivity;
import com.kuaibao.skuaidi.activity.OutSideDetailActivity;
import com.kuaibao.skuaidi.activity.ShopMigrateActivity;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.wallet.FundDetailsActivity;
import com.kuaibao.skuaidi.api.WebServiceHelper;
import com.kuaibao.skuaidi.circle.CircleExpressDetailActivity;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.entry.MessageEventBundle;
import com.kuaibao.skuaidi.entry.NoticeInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.MD5;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyPushMessageReceiver extends PushMessageReceiver {

	private String baiduPushResult;

	private final int NOTIFICATION_CLICK_TO_ACTIVITY = 1;
	private final int NOTIFICATION_CLICK_TO_SERVICE = 2;
	private final int NOTIFICATION_CLICK_SEND_BROADCAST = 3;

	@Override
	public void onBind(final Context context, int errorCode, String appId, String userId, String channelId,
			String requestId) {
		// context BroadcastReceiver 的执行 Context
		// errorCode 绑定接口返回值，0 - 成功
		// appid 应用 id。errorCode 非 0 时为 null
		// userId 应用 user id。errorCode 非 0 时为 null
		// channelId 应用 channel id。errorCode 非 0 时为 null
		// requestId 向服务端发起的请求 id。在追查问题时有用；
		// String responseString = "onBind errorCode=" + errorCode + " appid="
		// + appId + " userId=" + userId + " channelId=" + channelId
		// + " requestId=" + requestId;
		// //System.out.println(responseString);

		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			if (SkuaidiSpf.IsLogin()) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = tm.getDeviceId();
				ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				if (manager == null || networkInfo == null || !networkInfo.isAvailable()) {
					UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				} else {
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String date = sDateFormat.format(new java.util.Date());

					int tempIsOpen = 1;
					String partner_name = "androids";
					String token = new MD5().toMD5(partner_name + "," + imei + "," + userId + "," + channelId + ","
							+ userId + ",android,courier," + tempIsOpen + "bac500a42230c8d7d1820f1f1fa9b578");

					final String targetNameSpace = "urn:kuaidihelp_dts";
					final String Method = "exec";
					final String WSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
					final String soapAction = "urn:kuaidihelp_dts#dts#exec";
					final StringBuffer request = new StringBuffer(
							"<?xml version='1.0' encoding='utf-8' ?><request><header>")
							.append("<service_name>" + "push.channel" + "</service_name>")
							.append("<partner_name>" + partner_name + "</partner_name>")
							.append("<time_stamp>" + date + "</time_stamp>").append("<version>" + "v1" + "</version>")
							.append("<format>" + "json" + "</format>")
							.append("<token>" + token + "</token></header><body>").append("<imei>" + imei + "</imei>")
							.append("<model>" + Build.MODEL + "</model>")
							.append("<user_id>" + SkuaidiSpf.getLoginUser().getUserId() + "</user_id>")
							.append("<channel_id>" + channelId + "</channel_id>")
							.append("<app_id>" + appId + "</app_id>")
							.append("<baidu_user_id>" + userId + "</baidu_user_id>")
							.append("<device_type>android</device_type>").append("<user_type>courier</user_type>")
							.append("<is_open>" + tempIsOpen + "</is_open>" + "</body></request>");
					KLog.i("push",request);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								baiduPushResult = WebServiceHelper.getInstance().getPart(context, targetNameSpace,
										Method, WSDL, soapAction, request);
								KLog.i("push", baiduPushResult);
								if (baiduPushResult != null & !baiduPushResult.equals("")) {
									try {
										JSONObject json = new JSONObject(baiduPushResult);
										JSONObject response = json.getJSONObject("response");
										JSONObject body = response.getJSONObject("body");
										String status = body.getString("push_status");

										if (!status.equals("success")) {
											String desc = body.getString("push_desc");
											UtilToolkit.showToast( desc);
										} else {

										}

									} catch (JSONException e) {
										KLog.e("对不起,建立百度推送绑定服务器异常!");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								KLog.e("pushchannel error");
							}
						}
					}).start();
				}
			}
		} else {
			KLog.e("对不起,建立百度推送失败!");
		}

	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {

	}

	@Override
	public void onMessage(Context context, String message, String customContentString) {
		// context 上下文
		// message 推送的消息
		// customContentString 自定义内容,为空或者 json 字符串
		//System.out.println(message + "_customContentString");
		KLog.i("push", message);
		if (message != null && !message.equals("")) {

			try {
				JSONObject json = new JSONObject(message);
				String strCustomContent = json.getString("custom_content");
				JSONObject customContent = new JSONObject(strCustomContent);
				String messageType = customContent.getString("message_type");
				String m_id = customContent.optString("m_id");
				String m_type = customContent.optString("m_type");
				//Log.i("logi", messageType);
				String tempId = String.valueOf(System.currentTimeMillis());
				String messageId = tempId.substring(tempId.length() - 10, tempId.length() - 1);
				String title = customContent.getString("title");
				String description = customContent.getString("description");

				if ("order".equals(messageType)) {// 订单消息
					String orderState = customContent.getString("order_state");
					Intent intent = new Intent(context, OrderInfoActivity.class);

					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					if (orderState.equals("dealed")) {
						String orderId = customContent.getString("order_id");
						intent.putExtra("orderno", orderId);
						intent.putExtra("push", "");
						showNotification(context, "【快递员】新消息", "你有新的订单消息", intent, messageId,
								NOTIFICATION_CLICK_TO_ACTIVITY);
					}

				} else if ("liuyan_user".equals(messageType)) {// 留言消息
					Intent intent = new Intent(context, LiuyanDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					intent.putExtra("m_id", m_id);
					intent.putExtra("m_type", m_type);
					intent.putExtra("push", "push");
					int contentType = customContent.getInt("content_type");
					if (contentType == Constants.TYPE_TXT) {
						showNotification(context, title, description, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					} else if (contentType == Constants.TYPE_YUYIN) {
						showNotification(context, "【快递员】新消息", title, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					}
				} else if("liuyan_list".equals(messageType)){
					Intent intent = new Intent(context, LiuyanCenterActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					int contentType = customContent.getInt("content_type");
					if (contentType == Constants.TYPE_TXT) {
						showNotification(context, title, description, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					} else if (contentType == Constants.TYPE_YUYIN) {
						showNotification(context, "【快递员】新消息", title, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					}
					
				}else if ("cmNotice".equals(messageType)) {
					Intent intent = new Intent(context, NoticeDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					String type = customContent.optString("title");
					NoticeInfo notice = new NoticeInfo();
					notice.setContent(customContent.optString("description"));
					notice.setNoticeId(customContent.optString("cmn_id"));
					notice.setType(type);
					notice.setCreatTime(Long.parseLong(customContent.getString("create_time")));
					intent.putExtra("notice", notice);
					intent.putExtra("isPush", true);
					String showTitle = "";
					showTitle = "【快递员】" + type;
					showNotification(context, showTitle, description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				} else if ("orderIm".equals(messageType)) {
					String orderNO = customContent.getString("order_number");
					Intent intent = new Intent(context, OrderInfoActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					intent.putExtra("orderno", orderNO);
					intent.putExtra("push", "");
					showNotification(context, "【快递员】订单用户新消息", description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
					SkuaidiSpf.saveOrderNumber(context, "orderNum");
				} else if ("pay".equals(messageType)) {// 支付推送
					Intent intent = new Intent(context, FundDetailsActivity.class);
					intent.putExtra("mark", "pay");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					showNotification(context, title, description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				} else if ("circle".equals(messageType)) {// 快递圈推送
					String topic_id = customContent.getString("topic_id");
					Intent intent = new Intent(context, CircleExpressDetailActivity.class);
					intent.putExtra("topic_id", topic_id);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					showNotification(context, title, description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				} else if ("information".equals(messageType)) {
					// 资讯中心小红点
					SkuaidiSpf.saveHotDot(context, true);
					String information_id = customContent.getString("id");
					String tucao_id = customContent.getString("group_id");// 吐槽id
					Intent intent = new Intent();
					intent.putExtra("group_id", information_id);
					intent.putExtra("title", title);
					intent.putExtra("loadType", "push");
					intent.putExtra("tucao_id", tucao_id);
					intent.putExtra("url", "http://m.kuaidihelp.com/news/server/" + information_id + ".html");

					intent.setClass(context, LoadWebInformationActivity.class);
					showNotification(context, title, description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				} else if ("shop_migrate".equals(messageType)) {
					JSONObject shopInfo = customContent.getJSONObject("custom_content");
					// //System.out.println("shopInfo:" + shopInfo.toString());

					BranchInfo branch = new BranchInfo();
					branch.setIndexShopId(shopInfo.getString("index_shop_id"));
					branch.setIndexShopName(shopInfo.getString("shop_name"));
					branch.setExpressNo(shopInfo.getString("brand"));

					Intent intent = new Intent(context, ShopMigrateActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					intent.putExtra("shopinfo", branch);
					intent.putExtra("pushtype", "shop_migrate");
					// intent.setAction("pushshopmigrate");
					// context.sendBroadcast(intent);
					showNotification(context, "【快递员】网点邀请", description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				} else if ("weirenwu".equals(messageType)) {
					String weirenwu_id = customContent.getString("weirenwu_id");
					Intent intent = new Intent(context, OutSideDetailActivity.class);
					intent.putExtra("weirenwu_id", weirenwu_id);// test 235
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					showNotification(context, title, description, intent,
							Long.toString(System.currentTimeMillis() % 1000000000), NOTIFICATION_CLICK_TO_ACTIVITY);
				}else if ("topic".equals(messageType)) {// 短信记录	
					
					String topic_id = customContent.getString("topic_id");
					int contentType = customContent.getInt("content_type");
					// 参数返回结果-inform_user-派件通知， ivr-云呼通知
					String source = customContent.getString("source");
					Intent intent = new Intent(context, RecordDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
					intent.putExtra("topic_id", topic_id);
					intent.putExtra("source", source);
					if (contentType == Constants.TYPE_TXT) {
						showNotification(context, title, description, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					} else if (contentType == Constants.TYPE_YUYIN) {
						showNotification(context, "【快递员】新消息", title, intent, messageId, NOTIFICATION_CLICK_TO_ACTIVITY);
					}

				}else if("fixbug".equals(messageType)){// 补丁发布，请在线修复bug

				}else if("notifySign".equals(messageType)){// 扫码签收成功通知
					int signState=customContent.optInt("signState",-1);
					String expressNo=customContent.optString("waybill_no","");
					String reason=customContent.optString("reason","");
					Bundle bundle=new Bundle();
					bundle.putInt("signState",signState);
					bundle.putString("waybill_no",expressNo);
					bundle.putString("description",description);
					bundle.putString("reason",reason);
					MessageEventBundle event=new MessageEventBundle("signNotify",bundle);
					EventBus.getDefault().post(event);
				}
			} catch (JSONException e) {
				// //System.out.println("解析失败！");
				UtilToolkit.showToast("解析失败！");
			}

		}

	}

	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		// context 上下文
		// title 推送的通知的标题
		// description 推送的通知的描述
		// customContentString 自定义内容，为空或者 json 字符串

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
		//Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		/*
		 * if (errorCode == 0) { SkuaidiSpf.setBind(context, false); }
		 */

	}

	/*
	 * 显示通知栏showNotification
	 */
	public void showNotification(Context context, String title, String description, Intent intent, String messegeId,
			int clickType) {
		int id = Integer.parseInt(messegeId);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				//.setSmallIcon(R.drawable.logo)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentTitle(title)
				.setContentText(description);
		mBuilder.setTicker(title);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (clickType == NOTIFICATION_CLICK_TO_ACTIVITY) {
			mBuilder.setContentIntent(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		} else if (clickType == NOTIFICATION_CLICK_TO_SERVICE) {
			mBuilder.setContentIntent(PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		} else if (clickType == NOTIFICATION_CLICK_SEND_BROADCAST) {
			mBuilder.setContentIntent(PendingIntent
					.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
		}
		if (android.os.Build.VERSION.SDK_INT < 21){
			mBuilder.setSmallIcon(R.drawable.logo);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(id, mBuilder.build());
		}else {
			mBuilder.setSmallIcon(R.drawable.logo_transent);
			Notification nf=mBuilder.build();
			Class<?> clazz = nf.getClass();
			Field field = null;
			try {
				field = clazz.getDeclaredField("color");
				field.setAccessible(true);
				field.set(nf, context.getResources().getColor(R.color.text_green_one));
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(id, nf);
			if(!TextUtils.isEmpty(description) && description.contains("订单消息")){
				playSound(context);
			}
		}
	}

	//播放自定义的声音
	public void playSound(Context context) {
		String uri = "android.resource://" + context.getPackageName() + "/"+R.raw.order_coming;
		Uri no=Uri.parse(uri);
		Ringtone r = RingtoneManager.getRingtone(context, no);
		r.play();
	}

	//	public static void testNotification(Context context,String title,String description){
//		int id= 1;
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//				//.setSmallIcon(R.drawable.logo)
//				.setAutoCancel(true)
//				.setDefaults(Notification.DEFAULT_ALL)
//				.setContentTitle(title)
//				.setContentText(description);
//		mBuilder.setTicker(title);
//
//		if (android.os.Build.VERSION.SDK_INT < 21){
//			mBuilder.setSmallIcon(R.drawable.logo);
//			NotificationManager mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			mNotificationManager.notify(id, mBuilder.build());
//		}else {
//			mBuilder.setSmallIcon(R.drawable.logo_transent);
//			Notification nf=mBuilder.build();
//			Class<?> clazz = nf.getClass();
//			Field field = null;
//			try {
//				field = clazz.getDeclaredField("color");
//				field.setAccessible(true);
//				field.set(nf, context.getResources().getColor(R.color.text_green_one));
//			} catch (NoSuchFieldException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//			NotificationManager mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			mNotificationManager.notify(id, nf);
//		}
//	}
	@Override
	public void onNotificationArrived(Context context, String title,
									  String description, String customContentString) {

//		String notifyString = "onNotificationArrived  title=\"" + title
//				+ "\" description=\"" + description + "\" customContent="
//				+ customContentString;
//		//Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
//		if (!TextUtils.isEmpty(customContentString)) {
//			JSONObject customJson = null;
//			try {
//				customJson = new JSONObject(customContentString);
//				String myvalue = null;
//				if (!customJson.isNull("mykey")) {
//					myvalue = customJson.getString("mykey");
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		// 你可以參考 onNotificationClicked中的提示从自定义内容获取具体值
		//updateContent(context, notifyString);
	}
}
