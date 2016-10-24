package com.kuaibao.skuaidi.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;

import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

public class MyAudioCheckService extends Service {
    private List<Double> dbList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLock = new Object();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        KLog.i("kb", "MyAudioCheckService onStart");
        new Thread(){
            @Override
            public void run() {
                super.run();
                getNoiseLevel();
            }
        }.start();
    }

    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;
    long startRecordTime;

    public void getNoiseLevel() {
        if (isGetVoiceRun) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
                } catch (Exception e) {
                    KLog.e("kb", e.getMessage());
                    SkuaidiSpf.setAudioPermission(false);
                }
                if (mAudioRecord == null) {
                    SkuaidiSpf.setAudioPermission(false);
                    return;
                }
                isGetVoiceRun = true;
                try {
                    mAudioRecord.startRecording();
                    short[] buffer = new short[BUFFER_SIZE];
                    startRecordTime = System.currentTimeMillis();
                    dbList.clear();
                    while (isGetVoiceRun) {
                        //r是实际读取的数据长度，一般而言r会小于buffersize
                        int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                        //KLog.i("kb", "r:---->"+r);
                        double v = 0;
                        // 将 buffer 内容取出，进行平方和运算
                        for (int i = 0; i < buffer.length; i++) {
                            ////Log.i("kb", "buffer:--->"+buffer[i]);
                            v += buffer[i];
                        }
                        //Log.i("kb", "v:---->"+v);
                        // 平方和除以数据总长度，得到音量大小。
                        double mean = v / buffer.length;
                        //Log.i("kb", "mean:--->"+mean);
                        dbList.add(mean);
                        //double volume = 10 * Math.log10(mean);
                        ////Log.d(TAG, "分贝值:" + volume);
                        // 大概一秒十次
                        synchronized (mLock) {
                            try {
                                mLock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                SkuaidiSpf.setAudioPermission(false);
                            }
                        }
                        if ((System.currentTimeMillis() - startRecordTime) >= 3000) {
                            isGetVoiceRun = false;
                        }
                    }
                    mAudioRecord.stop();
                    mAudioRecord.release();
                } catch (Exception e) {
                    KLog.e("kb", e.getMessage());
                    SkuaidiSpf.setAudioPermission(false);
                }
                mAudioRecord = null;
                stopSelf();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.i("kb", "MyAudioCheckService onDestroy");
        if (dbList.size() > 1) {
            List<Double> newDbList = dbList.subList(dbList.size() / 2, dbList.size());
            boolean canAudio = false;
            for (int i = 0; i < newDbList.size(); i++) {
                if (newDbList.get(i) != 0.0D) {
                    canAudio = true;
                    break;
                }
            }
            if (!canAudio) {
                SkuaidiSpf.setAudioPermission(false);
            } else {
                SkuaidiSpf.setAudioPermission(true);
            }
        } else {
            SkuaidiSpf.setAudioPermission(false);
        }
        KLog.i("kb", "SkuaidiSpf.setAudioPermission:---->"+SkuaidiSpf.getAudioPermission());
        if(dbList!=null){
            dbList.clear();
            dbList=null;
        }
    }

}
