package com.kuaibao.skuaidi.application;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alipay.euler.andfix.patch.PatchManager;
import com.facebook.stetho.Stetho;
import com.iflytek.cloud.SpeechUtility;
import com.kuaibao.skuaidi.BuildConfig;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.db.FinalDBHelper;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.greendao.helper.MySQLiteOpenHelper;
import com.kuaibao.skuaidi.imageloader.GlideImageLoader;
import com.kuaibao.skuaidi.retrofit.OkHttpFactory;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.Utility;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.okgo.OkGo;
import com.socks.library.KLog;

import net.tsz.afinal.FinalDb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.litepal.LitePalApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.feng.skin.manager.loader.SkinManager;
import gen.greendao.dao.DaoMaster;
import gen.greendao.dao.DaoSession;


/**
 * update by lgg 优化精简onCreate 无用方法及变量，加快应用启动速度
 */
public class SKuaidiApplication extends LitePalApplication {

    public static final int EXIT_ALL_NOTIFY = 9999;

    private static SKuaidiApplication instance;

    private Map<String, Map<String, Object>> datas = new HashMap<>();

    private static String FirstFolder = "skuaidi";// 一级目录
    private static String SecondFolder = "call_recording";// 二级目录

    private static String tackPicSecondFolder = "pic";
    private static String tackPicShtreeFolder = "e3_waybills";

    private final static String ALBUM_PATH = Constants.ROOT + File.separator + FirstFolder + File.separator;

    private final static String Second_PATH = ALBUM_PATH + SecondFolder + File.separator;

    private final static String picSecond_path = ALBUM_PATH + tackPicSecondFolder + File.separator;

    private final static String picThree_path = picSecond_path + tackPicShtreeFolder + File.separator;

    public final static int SHOW_TOAST = 101;

    public final static int SHOW_TOAST_CUSTOM = 0x1010;

    private boolean loginSuccess;

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public static String VERSION_NAME = "";
    public static int VERSION_CODE;
    public static PatchManager mPatchManager;
    public static final int maxImgCount = 9;//允许选择图片最大数
    public static BluetoothDevice device= null;
    private DaoSession mDaoSession;
    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 98:
                    List<Object> objs = ((List<Object>) msg.obj);
                    ((HttpHelper.OnResultListener) objs.get(0)).onSuccess(objs.get(1).toString(), objs.get(2).toString());
                    break;
                case 99:
                    List<Object> objs1 = ((List<Object>) msg.obj);
                    if (objs1.get(1) != null) {
                        if (objs1.get(1).toString().contains("无网络连接")) {
                            if (Utility.getRunningTask(SKuaidiApplication.getContext(), SKuaidiApplication.getContext().getPackageName())) {
                                ((HttpHelper.OnResultListener) objs1.get(0)).onFail(objs1.get(1).toString(), null, "");
                            }
                        } else {
                            ((HttpHelper.OnResultListener) objs1.get(0)).onFail(objs1.get(1).toString(), null, "");
                        }
                    }
                    break;
                case SHOW_TOAST:
                    Toast.makeText(SKuaidiApplication.getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_TOAST_CUSTOM:
                    ToastHelper.showToast(SKuaidiApplication.getContext(), msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //开启dex进程的话也会进入application
        if (isDexProcess()) {
            return;
        }
        doInstallBeforeLollipop();
        MultiDex.install(this);
    }

    private void doInstallBeforeLollipop() {
        //满足3个条件，1.第一次安装开启，2.主进程，3.API<21(因为21之后ART的速度比dalvik快接近10倍(毕竟5.0之后的手机性能也要好很多))
        if (isAppFirstInstall() && !isDexProcessOrOtherProcesses() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                createTempFile();
                startDexProcess();
                while (true) {
                    if (existTempFile()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        setAppNoteFirstInstall();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAppNoteFirstInstall() {
        SharedPreferences sharedPreferences = getSharedPreferences("install", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstInstall", false).commit();
    }

    private boolean existTempFile() {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "kuaibao.skuaidi";
        return new File(filePath).exists();
    }

    private void createTempFile() throws IOException {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "kuaibao.skuaidi";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
    }

    private void startDexProcess() {
        Intent intent = new Intent(this, DexActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isAppFirstInstall() {
        SharedPreferences sharedPreferences = getSharedPreferences("install", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstInstall", true);
    }

    private boolean isDexProcessOrOtherProcesses() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null && activityManager.getRunningAppProcesses() != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (pid == appProcess.pid) {
                    if (appProcess.processName.equals("com.kuaibao.skuaidi:dex") ||
                            appProcess.processName.equals("com.kuaibao.skuaidi:bdservice_v1") ||
                            appProcess.processName.equals("com.kuaibao.skuaidi:remote") ||
                            appProcess.processName.equals("com.kuaibao.skuaidi:leakcanary") ||
                            appProcess.processName.equals("com.kuaibao.skuaidi:OnlineService")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isDexProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null && activityManager.getRunningAppProcesses() != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (pid == appProcess.pid) {
                    if (appProcess.processName.equals("com.kuaibao.skuaidi:dex")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isDexProcess()) {
            return;
        }
        instance = this;
        LitePalApplication.initialize(this);
        makeDir();

        // 语音识别部分
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
        if(!TextUtils.isEmpty(Build.CPU_ABI) && Build.CPU_ABI.contains("x86")){
        }else{
            SpeechUtility.createUtility(SKuaidiApplication.this, "appid=" + getString(R.string.appid));
            // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
            // Setting.showLogcat(false);
        }

        InitUtil.initParams();
//        LeakCanary.install(this);

        //皮肤管理器加载
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();

        FeedbackAPI.initAnnoy(this, InitUtil.ALI_BAICHUAN_APPID);

        //CrashHandler.getInstance().init(getApplicationContext());
        initAndFix();//初始化热修复框架

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        initOkGo();
        initImagePicker();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
    }

    private void initOkGo(){
        OkGo.init(this);
        OkHttpFactory.initOkGoClient();
    }

    private void initAndFix() {
        try {
            PackageInfo mPackageInfo = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
            VERSION_NAME = mPackageInfo.versionName;
            VERSION_CODE = mPackageInfo.versionCode;
            //SkuaidiSpf.setSKuaidiVersionName(VERSION_NAME);
            //SkuaidiSpf.setSKuaidiVersionCode(VERSION_CODE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //KLog.i("kb", "VERSION_NAME:--->" + VERSION_CODE);
        if (!BuildConfig.DEBUG) {
            mPatchManager = new PatchManager(this);
            mPatchManager.init(VERSION_CODE + "");
            Log.d("kb", "inited.");
            // load patch
            mPatchManager.loadPatch();
            KLog.d("kb", "apatch loaded.");
        }
    }

    public static SKuaidiApplication getInstance() {
        return instance;
    }

    public void exit() {
        EventBus.getDefault().post(new MessageEvent(SKuaidiApplication.EXIT_ALL_NOTIFY, ""));
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public FinalDb getFinalDbCache() {
        return FinalDBHelper.getInstanse().getFinalDB();
    }

    public void postMsg(String toWhere, String key, Object msg) {
        if (datas.get(toWhere) == null) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(key, msg);
            datas.put(toWhere, data);
        } else {
            datas.get(toWhere).put(key, msg);
        }
    }

    public Object onReceiveMsg(String where, String key) {
        try {
            return datas.get(where).get(key);
        } catch (Exception e) {
            return null;
        }
    }

    private void makeDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dirFirstFile = new File(ALBUM_PATH);// 新建一级主目录
            if (!dirFirstFile.exists()) {// 判断文件夹目录是否存在
                System.out.println(dirFirstFile.mkdir());// 如果不存在则创建
            }
            File dirSecondFile = new File(Second_PATH);// 新建二级主目录
            if (!dirSecondFile.exists()) {// 判断文件夹目录是否存在
                System.out.println(dirSecondFile.mkdir());// 如果不存在则创建
            }
            dirSecondFile = new File(picSecond_path);// 新建二级主目录
            if (!dirSecondFile.exists()) {// 判断文件夹目录是否存在
                System.out.println(dirSecondFile.mkdir());// 如果不存在则创建
            }
            File dirThreeFile = new File(picThree_path);// 新建一级主目录
            if (!dirThreeFile.exists()) {// 判断文件夹目录是否存在
                System.out.println(dirThreeFile.mkdir());// 如果不存在则创建
            }
        }
    }

    public void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public void sendMessage(HttpHelper.OnResultListener listener, int what, String result, String sname) {
        Message msg = new Message();
        List<Object> objs = new ArrayList<Object>();
        objs.add(listener);
        objs.add(result);
        objs.add(sname);
        msg.what = what;
        msg.obj = objs;
        handler.sendMessage(msg);
    }

    public WindowManager getMWindowManager() {
        return (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public synchronized DaoSession getDaoSession() {
        if (mDaoSession == null) {
            initDaoSession();
        }
        return mDaoSession;
    }

    private void initDaoSession() {
        // 相当于得到数据库帮助对象，用于便捷获取db
        // 这里会自动执行upgrade的逻辑.backup all table→del all table→create all new table→restore data
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, "greendao.db", null);
        // 得到可写的数据库操作对象
        Database db = helper.getWritableDb();
        // 获得Master实例,相当于给database包装工具
        DaoMaster daoMaster = new DaoMaster(db);
        // 获取类似于缓存管理器,提供各表的DAO类
        mDaoSession = daoMaster.newSession();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

}
