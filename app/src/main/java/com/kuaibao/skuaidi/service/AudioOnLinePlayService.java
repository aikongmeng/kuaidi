package com.kuaibao.skuaidi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.kuaibao.skuaidi.business.nettelephone.calllog.utils.NewPlayer;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ligg on 2016/4/8.
 */
public class AudioOnLinePlayService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.i("kb","Audio OnlineService onCreate");
        NewPlayer.getInstance();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        KLog.i("kb","AudioOnlinePlayService onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        release();
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        switch (event.type) {
            case 0XF1://开始播放某个Uri UI通知
                if(!TextUtils.isEmpty(event.message)){
                    KLog.i("kb","AudioOnlinePlay start #####");
                    playUrlOrPath(event.message,event.position);
                }
                break;
            case 0XF2://停止播放某个Uri UI通知
                stopPlay();
                break;
            case 0XF3:// 退出UI 注销Service 释放单例NewPlayer中所有资源
                stopSelf();
                break;
            case 0XF4:
                stopPre();
                break;
            case 0X01://开始播放 Player通知
                KLog.i("kb","开始播放 Player通知");
                break;
            case 0X02://停止播放 Player通知 自动播放完毕，或者播放出错
                KLog.i("kb","Audio Service 0X02");
                break;
            case 0XE1://一些播放过程中发生的错误 Player通知
            case 0XE2:
            case 0XE3:
            case 0XE4:
            case 0XE5:
                KLog.e("kb","播放错误:---->"+event.message);
                UtilToolkit.showToast("录音播放失败");
                if(!TextUtils.isEmpty(event.message)){
                    FileUtils.fileDelete(event.message);// 清除播放错误的文件，因为可能是下载不完整导致的
                }
                break;
            case 0XA0:
                askPlayInfo();
                break;
        }
    }
    private void askPlayInfo(){
        MessageEvent event=new MessageEvent(0XA1,NewPlayer.getPlayingInfo());
        EventBus.getDefault().post(event);
    }
    private void stopPre(){
        NewPlayer.stopPrePlayWhileDownloading();
    }

    private void release(){
            NewPlayer.releaseMediaPlayer();
            NewPlayer.destoryInstance();
    }
    private void playUrlOrPath(String uri,int position){
            NewPlayer.playUrlOrPath(uri,position);
    }

    private void stopPlay(){
            NewPlayer.stop();
    }
}
