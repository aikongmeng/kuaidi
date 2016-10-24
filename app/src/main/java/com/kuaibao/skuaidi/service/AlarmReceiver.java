package com.kuaibao.skuaidi.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysSweepRecordActivity;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.socks.library.KLog;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class AlarmReceiver extends BroadcastReceiver {
	Dialog dialog;
	public static int count;// 本地未上传数据。
	public static final int NOTIFICATION_ID = 1;
	private final int NOTIFICATION_CLICK_TO_ACTIVITY = 1;
	private NotificationManager mNotificationManager;
	private static final String url = "http://m.kuaidihelp.com/help/baqiang_tip.html";
	private static final String title = "巴枪上传提醒";

	@Override
	public void onReceive(final Context context, Intent intent) {
		KLog.i("kb","闹钟激发@@@@@@@@@");
		//new CallLogsApi().sendCallLogsToServer();
		if (count <= 0 ) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_alert, null);

		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn()) {
			Intent mIntent = new Intent(context, AlarmReceiver.class);
			showNotification(context, "【快递员】单号上传", "你有未上传的单号信息", mIntent, R.string.app_name,
					NOTIFICATION_CLICK_TO_ACTIVITY);
		} else {
			KeyguardLock mKeyguardLock = km.newKeyguardLock("com.kuaibao.skuaidi.service.AlarmReceiver");

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				if (km.isKeyguardLocked())
					mKeyguardLock.disableKeyguard();// 让键盘锁失效
			} else {
				if (km.inKeyguardRestrictedInputMode())
					mKeyguardLock.disableKeyguard();// 让键盘锁失效
			}

			dialog = new AlertDialog.Builder(context).create();
			dialog.setCancelable(false);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
			dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
			dialog.show();
			dialog.getWindow().setContentView(layout);
			// 3. 消息内容
			TextView countView = (TextView) layout.findViewById(R.id.tv_count);
			countView.setText("你有" + count + "票单号未做巴枪数据上传，\n  请及时上传保证签收率哦！");

			layout.findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
					// 清除通知栏
					if (mNotificationManager != null)
						mNotificationManager.cancel(R.string.app_name);

				}
			});

			layout.findViewById(R.id.btn_do).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.cancel();
					if (mNotificationManager != null)
						mNotificationManager.cancel(R.string.app_name);
					Intent intent = new Intent(context, EThreeSysSweepRecordActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});
			final ImageButton doubtButton = (ImageButton) layout.findViewById(R.id.doubt);
			ViewTouchDelegate.expandViewTouchDelegate(doubtButton, 10, 10, 10, 10);
			doubtButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.cancel();
					Intent intent = new Intent(context, WebLoadView.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("from", "alarmDialog");
					ArrayList<String> parameters = new ArrayList<String>();
					parameters.add(url);
					parameters.add(title);
					intent.putStringArrayListExtra("parameters", parameters);
					context.startActivity(intent);

				}
			});

		}

	}

	/*
	 * 显示通知栏showNotification
	 */
	public void showNotification(Context context, String title, String description, Intent intent, int messegeId,
			int clickType) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.logo)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setContentTitle(title)
				.setContentText(description);
		mBuilder.setTicker(title);
		mBuilder.setContentIntent(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(messegeId, mBuilder.build());

	}

}
