package com.kuaibao.skuaidi.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.github.piasy.rxandroidaudio.RxAmplitude;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gen.greendao.bean.ICallLog;
import gen.greendao.dao.ICallLogDao;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OnlineService extends Service {
    private final static String TAG = "kb";
    /**
     * 定时唤醒的时间间隔，5分钟
     */
//    private final static int ALARM_INTERVAL = 5 * 60 * 1000;
//    private final static int WAKE_REQUEST_CODE = 6666;
    private final static int GRAY_SERVICE_ID = -1001;
    private static final int REPEAT_TAG = 0xF1;
    private Timer timer ;
    private TimerTask task;
    private TelephonyManager telManager;
    private TelListener phoneStateListener;
    private OutcallReceiver outcallReceiver;

    private boolean incomingFlag = false;
    private String calling_number = null;
    private Chronometer mChronometer;
    private WindowManager windowManager;
    private View view;
    private WindowManager.LayoutParams param;
    private float y;
    private int statusBarHeight;
    private float mTouchStartY;
    private TextView tvCalledName;
    private TextView tvCalledAddress;
    private MyCustom custom=null;
    private boolean mIsRecording = false;
    private MarqueeTextView tv_record_msg;
    private int mScreenWidth;
    private int mScreenHeight;
    private TextView tv_record_state;
    private Button btn_giveup_record;
    private Button btn_lanjian;
    private Button btn_paijian;
    private ProgressActivity progressLanjian;
    private ProgressActivity progressPaijian;
    private LinearLayout ll_phone_order;
    private LinearLayout ll_phone_sms;
    private LinearLayout ll_phone_call;
    private LinearLayout ll_phone_footerview;
    private boolean isAdded=false;
    private LinearLayout ll_record_parent;
    private CompositeSubscription mCompositeSubscription;
    private TextView tvOrderStatus,tvSmsStatus,tvCallStatus;
    private ICallLog callLog;
    private LanOrPieBroadcastReceiver mLanOrPieBroadcastReceiver;
    public static final String LAN_PIE_ACTION="com.kuaibao.skuaidi.lanOrPie";
    private Subscription mRecordSubscription;
    private AudioRecorder mAudioRecorder;
    //private StreamAudioRecorder mStreamAudioRecorder;
    private File mAudioFile;
    //private FileOutputStream mFileOutputStream;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REPEAT_TAG:
                    if (SkuaidiSpf.IsLogin()) {
                        if(!TextUtils.isEmpty(SkuaidiSpf.getLatitudeOrLongitude(SKuaidiApplication.getContext()).getLongitude()) && !TextUtils.isEmpty(SkuaidiSpf.getLatitudeOrLongitude(SKuaidiApplication.getContext()).getLatitude())){
                            KuaidiApi.sendOnlineInfo(getApplicationContext(), handler,
                                    Double.parseDouble(SkuaidiSpf.getLatitudeOrLongitude(SKuaidiApplication.getContext()).getLongitude()),
                                    Double.parseDouble(SkuaidiSpf.getLatitudeOrLongitude(SKuaidiApplication.getContext()).getLatitude()));
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public void onCreate() {
        KLog.i("kb","OnlineService onCreate");
        super.onCreate();
        telManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener=new TelListener();
        telManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //注册广播
        outcallReceiver=new OutcallReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outcallReceiver, filter);

        mLanOrPieBroadcastReceiver=new LanOrPieBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LAN_PIE_ACTION);
        registerReceiver(mLanOrPieBroadcastReceiver,intentFilter);

        mCompositeSubscription=new CompositeSubscription();
        timer=new Timer(true);
        task=new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = REPEAT_TAG;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 10 * 1000, 30 * 60 * 1000);
        //mStreamAudioRecorder = StreamAudioRecorder.getInstance();
    }

    private class LanOrPieBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            KLog.i("kb","OnlineService onEvent");
            if(intent!=null){
                int code=intent.getIntExtra(LanOrPieService.LAN_PIE_RESULT_CODE,-1);
                switch(code){
                    case LanOrPieService.LAN_OPERATION_SUCCESS:
                        if(progressLanjian!=null){
                            progressLanjian.showContent();
                        }
                        if(btn_lanjian!=null){
                            btn_lanjian.setEnabled(false);
                            btn_lanjian.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    removeWindowView();
                                }
                            },1000);
                        }
                        UtilToolkit.showToast("订单创建成功");
                        break;
                    case LanOrPieService.LAN_OPERATION_FAIL:
                        if(progressLanjian!=null){
                            progressLanjian.showContent();
                        }
                        if(btn_lanjian!=null){
                            btn_lanjian.setEnabled(true);
                        }
                        UtilToolkit.showToast("订单创建失败");
                        break;
                    case LanOrPieService.PIE_OPERATION_SUCCESS:
                        if(progressPaijian!=null){
                            progressPaijian.showContent();
                        }
                        if(btn_paijian!=null){
                            btn_paijian.setEnabled(false);
                            btn_paijian.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    removeWindowView();
                                }
                            },1000);
                        }
                        UtilToolkit.showToast("留言创建成功");
                        break;
                    case LanOrPieService.PIE_OPERATION_FAIL:
                        if(progressPaijian!=null){
                            progressPaijian.showContent();
                        }
                        if(btn_paijian!=null){
                            btn_paijian.setEnabled(true);
                        }
                        UtilToolkit.showToast("留言创建失败");
                        break;
                }
            }
        }
    }

    private class TelListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
                if (!SkuaidiSpf.IsLogin()) return;
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        calling_number = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(incomingNumber);
                        if (TextUtils.isEmpty(calling_number) || "4009200530".equals(calling_number)) {
                            return;
                        }
                        incomingFlag = true;//标识当前是来电
                        KLog.i("kb", "RINGING :"+ calling_number);
                        if(SkuaidiSpf.getIsShowWindow(getApplicationContext(),SkuaidiSpf.getLoginUser().getPhoneNumber())){
                            showWindowPhoneView(getApplicationContext(),calling_number);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(incomingFlag){
                            KLog.i("kb", "incoming ACCEPT :"+ calling_number);
                            String loginPhone=SkuaidiSpf.getLoginUser().getPhoneNumber();
                            if(SkuaidiSpf.getIsShowWindow(getApplicationContext(),loginPhone)){
                                startRecord(loginPhone);
                            }
                        }else{
                            KLog.i("kb", "outgoing ACCEPT :"+ calling_number);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                            KLog.i("kb", incomingFlag? "incoming IDLE":"outgoing IDLE");
                            if(mIsRecording){
                                stopRecord();
                            }else{
                                if(windowManager!=null && view !=null && isAdded){
                                    windowManager.removeView(view);
                                    isAdded=false;
                                }
                            }
                        break;
                }
        }
    }

    private class OutcallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!SkuaidiSpf.IsLogin()) return;
            //如果是拨打电话
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                calling_number = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                if (TextUtils.isEmpty(calling_number) || "4009200530".equals(calling_number)) {
                    return;
                }
                incomingFlag = false;
                KLog.i("kb", "call OUT:"+calling_number);
                if(SkuaidiSpf.getIsShowWindow(context,SkuaidiSpf.getLoginUser().getPhoneNumber())){
                    showWindowPhoneView(context,calling_number);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.i(TAG, "GrayService->onStartCommand");
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        //发送唤醒广播来促使挂掉的UI进程重新启动起来
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent();
//        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
//        PendingIntent operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "GrayService->onDestroy");
        super.onDestroy();
        //取消监听
        if(phoneStateListener!=null && telManager!=null){
            telManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            phoneStateListener=null;
        }
        //解除注册广播
        if(outcallReceiver!=null){
            unregisterReceiver(outcallReceiver);
            outcallReceiver=null;
        }
        if(mLanOrPieBroadcastReceiver!=null){
            unregisterReceiver(mLanOrPieBroadcastReceiver);
            mLanOrPieBroadcastReceiver=null;
        }
        if(mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe();
        }
        Intent intent2 = new Intent(getApplicationContext(), OnlineService.class);
        startService(intent2);
    }

    private void createFloatWindow(Context context){
        windowManager= SKuaidiApplication.getInstance().getMWindowManager();
        statusBarHeight=Utility.getStatusBarHeight(context);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        param=new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            param.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            param.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        param.format= PixelFormat.RGBA_8888;
        param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        param.alpha = 1.0f;
        param.gravity= Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        param.x= 0;
        param.y=mScreenHeight/2;
        param.width=mScreenWidth;
        param.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view= LayoutInflater.from(context).inflate(R.layout.intercept_phone_window,null);
        initView(view);
    }

    private void showWindowPhoneView(Context context,String phoneNum){
        if(view==null || param==null){
            createFloatWindow(context);
        }
        if(!isAdded){
            windowManager.addView(view, param);
            isAdded=true;
        }

        if(incomingFlag){
            toggleRecordParentVisible(false);
        }

        if(!TextUtils.isEmpty(phoneNum)){
            custom= SkuaidiNewDB.getInstance().selectCustomerByPhoneNum(phoneNum);
        }
        if(custom!=null){
            if(TextUtils.isEmpty(custom.getName())){
                if(!TextUtils.isEmpty(custom.getPhone())){
                    tvCalledName.setText(custom.getPhone());
                }else{
                    tvCalledName.setText("新客户");
                }
            }else{
                tvCalledName.setText(custom.getName());
            }

            if(!TextUtils.isEmpty(custom.getAddress())){
                tvCalledAddress.setText(custom.getAddress());
            }else{
                tvCalledAddress.setText("暂无该客户地址信息");
            }
        }else{
            tvCalledName.setText("新客户");
            tvCalledAddress.setText("暂无该客户地址信息");
        }
        toggleTextVisible(true);

        if(ll_phone_footerview!=null){
            ll_phone_footerview.setVisibility(incomingFlag? View.VISIBLE:View.GONE);
            if(ll_phone_footerview.getVisibility()==View.VISIBLE){
                setTheseVisbile(true);
                final ApiWrapper apiWrapper=new ApiWrapper();
                Subscription subscription=apiWrapper.getCallerInfo(phoneNum).subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        if(jsonObject!=null){
                            if(!TextUtils.isEmpty(jsonObject.getString("lastOrderStatus"))){
                                tvOrderStatus.setText(jsonObject.getString("lastOrderStatus"));
                            }
                            if(!TextUtils.isEmpty(jsonObject.getString("lastSmsTime"))){
                                Date date=UtilityTime.formatTimeByStr(jsonObject.getString("lastSmsTime"),UtilityTime.YYYY_MM_DD_HH_MM_SS);
                                tvSmsStatus.setText(UtilityTime.timeFormat2(date.getTime()));
                            }
                            String lastNetCallTime=jsonObject.getString("lastNetCallTime");
                            ICallLogDao iCallLogDao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                            QueryBuilder<ICallLog> qb = iCallLogDao.queryBuilder();
                            qb.where(ICallLogDao.Properties.MasterPhone.eq(SkuaidiSpf.getLoginUser().getPhoneNumber()),ICallLogDao.Properties.CallNum.eq(calling_number))
                                    .orderDesc(ICallLogDao.Properties.CallDate);
                            List<ICallLog> callogs=qb.build().list();
                            long localLastCallTime=0;
                            if(callogs!=null && callogs.size()>0){
                                localLastCallTime=callogs.get(0).getCallDate();
                            }
                            if(!TextUtils.isEmpty(lastNetCallTime)){
                                Date date=UtilityTime.formatTimeByStr(lastNetCallTime,UtilityTime.YYYY_MM_DD_HH_MM_SS);
                                if(localLastCallTime>0){
                                    Date localDate=new Date(localLastCallTime);
                                    if(localDate.after(date)){
                                        tvCallStatus.setText(UtilityTime.timeFormat2(localLastCallTime));
                                    }else{
                                        tvCallStatus.setText(UtilityTime.timeFormat2(date.getTime()));
                                    }
                                }else{
                                    tvCallStatus.setText(UtilityTime.timeFormat2(date.getTime()));
                                }
                            }else{
                                if(localLastCallTime>0){
                                    tvCallStatus.setText(UtilityTime.timeFormat2(localLastCallTime));
                                }
                            }
                        }
                    }
                }));
                if(mCompositeSubscription!=null){
                    mCompositeSubscription.add(subscription);
                }
            }
            toggleButtonState(true);
        }

        if(!incomingFlag){
            startRecord(SkuaidiSpf.getLoginUser().getPhoneNumber());
        }
    }

    private void toggleRecordParentVisible(boolean visible){
        if(ll_record_parent!=null){
            ll_record_parent.setVisibility(visible? View.VISIBLE:View.GONE);
        }
    }

    private void setTheseVisbile(boolean visbile){
        if(btn_giveup_record!=null){
            btn_giveup_record.setVisibility(visbile ? View.GONE:View.VISIBLE);
        }
        if(progressPaijian!=null){
            progressPaijian.setVisibility(visbile ? View.GONE:View.VISIBLE);
        }
        if(progressLanjian!=null){
            progressLanjian.setVisibility(visbile ? View.GONE:View.VISIBLE);
        }

        if(ll_phone_order!=null){
            ll_phone_order.setVisibility(visbile ? View.VISIBLE:View.GONE);
        }
        if(ll_phone_sms!=null){
            ll_phone_sms.setVisibility(visbile ? View.VISIBLE:View.GONE);
        }
        if(ll_phone_call!=null){
            ll_phone_call.setVisibility(visbile ? View.VISIBLE:View.GONE);
        }
    }

    private void initView(final View view){
        ll_record_parent= (LinearLayout) view.findViewById(R.id.ll_record_parent);
        ll_phone_footerview= (LinearLayout) view.findViewById(R.id.ll_phone_footerview);
        ll_phone_order= (LinearLayout) view.findViewById(R.id.ll_phone_order);
        ll_phone_sms= (LinearLayout) view.findViewById(R.id.ll_phone_sms);
        ll_phone_call= (LinearLayout) view.findViewById(R.id.ll_phone_call);
        btn_giveup_record= (Button) view.findViewById(R.id.btn_giveup_record);
        progressLanjian= (ProgressActivity) view.findViewById(R.id.progress_lanjian);
        progressPaijian= (ProgressActivity) view.findViewById(R.id.progress_paijian);
        btn_lanjian= (Button) view.findViewById(R.id.btn_lanjian);
        btn_paijian= (Button) view.findViewById(R.id.btn_paijian);
        tv_record_state= (TextView) view.findViewById(R.id.tv_record_state);
        tv_record_msg= (MarqueeTextView) view.findViewById(R.id.tv_record_msg);
        mChronometer= (Chronometer) view.findViewById(R.id.chronometer);
        tvCalledName= (TextView) view.findViewById(R.id.call_name);
        tvCalledAddress= (TextView) view.findViewById(R.id.called_address);
        tvOrderStatus= (TextView) view.findViewById(R.id.tv_order_status);
        tvSmsStatus= (TextView) view.findViewById(R.id.tv_sms_status);
        tvCallStatus= (TextView) view.findViewById(R.id.tv_call_status);
        addListenser();
    }

    private void startRecord(String userPhone) {
        toggleRecordParentVisible(true);
        tvCalledAddress.setVisibility(incomingFlag? View.GONE:View.VISIBLE);
        if(!SkuaidiSpf.getIsRecordingOpen(getApplicationContext(),userPhone)){
            if(mChronometer!=null) mChronometer.setVisibility(View.GONE);
            if(tv_record_state!=null){
                tv_record_state.setText("自动录音功能已关闭");
                tv_record_state.setTextSize(13);
            }
            return;
        }
        if(mChronometer!=null) mChronometer.setVisibility(View.VISIBLE);
        if(tv_record_state!=null){
            tv_record_state.setText("录音中...");
            tv_record_state.setTextSize(15);
        }

        mIsRecording = true;

//        try {
//            String uuid=System.nanoTime()+"";
//            initCallLog(uuid);
//            String fileSuffix= uuid + ".stream.m4a";
//            mAudioFile=new File(Constants.LOCAL_RECORD_PATH+fileSuffix);
//            String filePath=mAudioFile.getAbsolutePath();
//            if(!FileUtils.fileExists(filePath.substring(0,filePath.lastIndexOf("/")))){
//                FileUtils.fileMkdirs(filePath.substring(0,filePath.lastIndexOf("/")));
//            }
//            Log.d(TAG, "to prepare record");
//            mAudioFile.createNewFile();
//            mFileOutputStream = new FileOutputStream(mAudioFile);
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//            mChronometer.start();
//            if(mStreamAudioRecorder==null) mStreamAudioRecorder=StreamAudioRecorder.getInstance();
//            mStreamAudioRecorder.start(new StreamAudioRecorder.AudioDataCallback() {
//                @Override
//                public void onAudioData(byte[] data, int size) {
//                    if (mFileOutputStream != null) {
//                        try {
//                            mFileOutputStream.write(data, 0, size);
//                        } catch (final IOException e) {
//                            setAudioRecordFail(e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                @Override
//                public void onError() {
//                    setAudioRecordFail("录音失败");
//                }
//            });
//        } catch (final IOException e) {
//            setAudioRecordFail(e.getMessage());
//            e.printStackTrace();
//        }

        if(mAudioRecorder==null){
            mAudioRecorder = AudioRecorder.getInstance();
            mAudioRecorder.setOnErrorListener(new AudioRecorder.OnErrorListener() {
                @Override
                public void onError(@AudioRecorder.Error int error) {
                    KLog.i( "kb"," AudioRecorder Error code: " + error);
                    UtilToolkit.showToast("录音失败");
                    mIsRecording = false;
                    if(mChronometer!=null) mChronometer.stop();
                    if(tv_record_state!=null) tv_record_state.setText("录音失败");
                }
            });
        }
        Toast.makeText(getApplicationContext(),"为保证录音清晰,已自动为您开启扬声器",Toast.LENGTH_LONG).show();
        mRecordSubscription = Single.just(true)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        String uuid=System.nanoTime()+"";
                        initCallLog(uuid);
                        String fileSuffix= uuid + ".file.m4a";
                        mAudioFile=new File(Constants.LOCAL_RECORD_PATH+fileSuffix);
                        String filePath=mAudioFile.getAbsolutePath();
                        if(!FileUtils.fileExists(filePath.substring(0,filePath.lastIndexOf("/")))){
                            FileUtils.fileMkdirs(filePath.substring(0,filePath.lastIndexOf("/")));
                        }
                        Log.d(TAG, "to prepare record");
                        return mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                                192000, 192000, mAudioFile);
                    }
                })
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        mChronometer.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChronometer.setBase(SystemClock.elapsedRealtime());
                                mChronometer.start();
                                OpenSpeaker();
                            }
                        },300);
                        return mAudioRecorder.startRecord();
                    }
                })
                .toObservable()
                .flatMap(new Func1<Boolean, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Boolean aBoolean) {
                        return RxAmplitude.from(mAudioRecorder);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer level) {
                        //int progress = mAudioRecorder.progress();
                        //Log.d(TAG, "amplitude: " + level + ", progress: " + progress);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        UtilToolkit.showToast("录音失败");
                        mIsRecording = false;
                        if(mChronometer!=null) mChronometer.stop();
                        if(tv_record_state!=null) tv_record_state.setText("录音失败");
                    }
                });
    }

//    private void setAudioRecordFail(final String msg){
//        tv_record_state.post(new Runnable() {
//            @Override
//            public void run() {
//                UtilToolkit.showToast(msg);
//                mIsRecording = false;
//                if(mChronometer!=null) mChronometer.stop();
//                if(tv_record_state!=null) tv_record_state.setText("录音失败");
//            }
//        });
//        if(mFileOutputStream!=null){
//            try {
//                mFileOutputStream.close();
//                mFileOutputStream = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private int currVolume;
    //打开扬声器
    public void OpenSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCallLog(String uuid){
        callLog=new ICallLog();
        callLog.setUuid(uuid);
        callLog.setMasterPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        callLog.setCallNum(calling_number);
        callLog.setCallType(incomingFlag ? ICallLog.TYPE_INCOMING_CALL : ICallLog.TYPE_OUTGOING_CALL);
        callLog.setCallDate(System.currentTimeMillis());
        if(custom!=null){
            callLog.setCustomerName(custom.getName());
        }else{
            String name = UtilToolkit.getCallerNameFromPhoneNumber(getApplicationContext(), calling_number);
            callLog.setCustomerName(calling_number.equals(name) ? "":name);
        }
        if(custom==null){
            addCustom(callLog.getCustomerName());
        }
    }

    private void addCustom(String customName) {
        MyCustom custom = new MyCustom();
        custom.setPhone(calling_number);
        custom.setTel(calling_number);
        custom.setName(customName);
        custom.setAddress("");
        custom.setNote("");
        int maxId = SkuaidiNewDB.getInstance().queryCustomerMaxId();
        custom.set_index(maxId);
        SkuaidiNewDB.getInstance().insertCustomer(custom);
    }

    private void toggleTextVisible(boolean visible){
        if(mChronometer!=null){
            if(visible){
                mChronometer.setVisibility(View.VISIBLE);
            }else{
                mChronometer.stop();
                mChronometer.setVisibility(View.GONE);
            }
        }
        if(tv_record_state!=null){
            tv_record_state.setVisibility(visible ? View.VISIBLE :View.GONE);
        }
        if(tv_record_msg!=null){
            if(!visible){
                tv_record_msg.setVisibility(View.VISIBLE);
                tv_record_msg.setText("录音文件:"+(mAudioFile==null? "" :mAudioFile.getAbsolutePath()));
                tv_record_msg.init(windowManager);
                tv_record_msg.startScroll();
                tv_record_msg.setEnabled(false);
            }else{
                tv_record_msg.setVisibility(View.GONE);
            }
        }
        if(tvCalledAddress!=null){
            tvCalledAddress.setVisibility(visible? View.VISIBLE :View.GONE);
        }
    }

    private void stopRecord() {
        toggleTextVisible(false);
        if(ll_phone_footerview!=null){
            ll_phone_footerview.setVisibility(View.VISIBLE);
        }
        setTheseVisbile(false);

        if(tvCalledName!=null){
            tvCalledName.setText(mChronometer.getText().toString());
        }

        if(mIsRecording && mAudioRecorder!=null){
            if (mRecordSubscription != null && !mRecordSubscription.isUnsubscribed()) {
                mRecordSubscription.unsubscribe();
                mRecordSubscription = null;
            }
            int seconds=mAudioRecorder.stopRecord();
            KLog.i("kb","录音时长:--->"+seconds);
            mIsRecording = false;
            saveCallLog();
            CloseSpeaker();
        }

//        if(mIsRecording && mStreamAudioRecorder!=null){
//            mStreamAudioRecorder.stop();
//            try {
//                mFileOutputStream.close();
//                mFileOutputStream = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mIsRecording = false;
//            saveCallLog();
//            CloseSpeaker();
//        }
    }

    private void saveCallLog(){
        if(callLog!=null){
            long duration=SystemClock.elapsedRealtime()-mChronometer.getBase();
            callLog.setCallDurationTime(duration);
            callLog.setRecordingFilePath(mAudioFile.getAbsolutePath());
            ICallLogDao dao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
            dao.insert(callLog);
        }
    }

    private void deleteCurrentCallLog(){
        if(callLog!=null){
            ICallLogDao dao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
            dao.deleteByKey(callLog.getUuid());
            if(!TextUtils.isEmpty(callLog.getRecordingFilePath())){
                File file=new File(callLog.getRecordingFilePath());
                if(file.exists()) file.delete();
            }
        }
    }

    private void addListenser(){
        view.findViewById(R.id.iv_close_phonewindown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeWindowView();
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                y = event.getRawY()-statusBarHeight;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartY =  event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;
                }
                return true;
            }
        });
        btn_giveup_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeWindowView();
                deleteCurrentCallLog();
            }
        });
        btn_lanjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isNetworkAvailable(getApplicationContext())){
                    UtilToolkit.showToast("请检查您的网络连接,稍后重试");
                    return;
                }
                if(callLog!=null){
                    if(progressLanjian!=null && !progressLanjian.isLoading()){
                        progressLanjian.showLoading();
                    }
                    LanOrPieService.buildAndStartIntent(getApplicationContext(),LanOrPieService.LAN_OPERATION,callLog,
                            -1,LanOrPieService.FROM_ONLINE_SERVICE);
                }
            }
        });
        btn_paijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isNetworkAvailable(getApplicationContext())){
                    UtilToolkit.showToast("请检查您的网络连接,稍后重试");
                    return;
                }
                if(callLog!=null){
                    if(progressPaijian!=null && !progressPaijian.isLoading()){
                        progressPaijian.showLoading();
                    }
                    LanOrPieService.buildAndStartIntent(getApplicationContext(),LanOrPieService.PIE_OPERATION,callLog,
                            -1,LanOrPieService.FROM_ONLINE_SERVICE);
                }
            }
        });
    }

    private void toggleButtonState(boolean reset){
        if(progressLanjian!=null)progressLanjian.showContent();
        if(progressPaijian!=null)progressPaijian.showContent();
        if(btn_paijian!=null){
            btn_paijian.setEnabled(true);
        }
        if(btn_lanjian!=null){
            btn_lanjian.setEnabled(true);
        }
    }

    private void updateViewPosition(){
        //更新浮动窗口位置参数
        if(param!=null && view !=null && windowManager!=null){
            param.x=0;
            param.y=(int) (y-mTouchStartY);
            if(param.y<statusBarHeight || param.y> mScreenHeight-mScreenHeight/5){
                return;
            }
            windowManager.updateViewLayout(view, param);
        }
    }

    private void removeWindowView(){
        stopRecord();
        if(windowManager!=null && view !=null && isAdded){
            windowManager.removeView(view);
            isAdded=false;
        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public void onCreate() {
            Log.i(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "InnerService -> onStartCommand");
            startForeground(GRAY_SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "InnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
