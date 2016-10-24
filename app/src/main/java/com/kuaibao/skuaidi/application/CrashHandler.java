package com.kuaibao.skuaidi.application;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.kuaibao.skuaidi.BuildConfig;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ligg on 2016/5/12 15:56.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                KLog.e(TAG,e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
       ex.printStackTrace();
        String logName=UtilityTime.getDateTimeByMillisecond3(new Date(),UtilityTime.YYYY_MM_DD_HH_MM_SS)+".txt";
        try {
            FileUtils.writeStringToFile(new File(com.kuaibao.skuaidi.util.FileUtils.toRootPath()+"/crashlog/"+logName),"","UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if(SKuaidiApplication.mPatchManager!=null && !BuildConfig.DEBUG)
                SKuaidiApplication.mPatchManager.removeAllPatch();//移除所有补丁,排除是补丁引起的崩溃
                Toast.makeText(mContext, "很抱歉~崩溃了 请尝试重启应用", Toast.LENGTH_LONG).show();
                Looper.loop();

            }
        }.start();
        //在此可执行其它操作，如获取设备信息、执行异常登出请求、保存错误日志到本地或发送至服务端
        return true;
    }

}
