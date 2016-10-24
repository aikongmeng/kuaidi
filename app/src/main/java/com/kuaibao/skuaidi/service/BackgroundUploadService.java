package com.kuaibao.skuaidi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundUploadService extends Service {
	String company;
	String courierNO;
	int retryTimes = 0;
	TimerTask timerTask;
	Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		//Log.i(MYSERVICE, "onBind" + this);
		return null;
	}

	@Override
	public void onCreate() {
		//Log.i(MYSERVICE, "onCreate" + this);
		super.onCreate();
		company = SkuaidiSpf.getLoginUser().getExpressNo();
		courierNO = E3SysManager.getCourierNO();

		timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				int toUploadCount = E3OrderDAO.getOrderCount(company, courierNO);
				if (toUploadCount == 0 || retryTimes >= 3 || !SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
					BackgroundUploadService.this.stopSelf();
					timer.cancel();
				} else {
					retryTimes++;
					if (Looper.myLooper() == null) {
						Looper.prepare();
					}

					for (int i = 0; i < E3SysManager.scanTypes.length; i++) {
						final int j = i;
						final BackgroundUploadingTask task = new BackgroundUploadingTask();
						new Thread() {
							public void run() {
								task.uploadDatas(E3OrderDAO.queryOrderByType(E3SysManager.scanTypes[j], company,
										courierNO));
							}
						}.start();

					}

					Looper.loop();

				}

			}
		};
		timer.schedule(timerTask, 1000, 1000 * 60 * 5);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.i(MYSERVICE, "onStartCommand" + this);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		//Log.i(MYSERVICE, "onDestroy" + this);
		super.onDestroy();
	}

}
