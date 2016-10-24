package com.kuaibao.skuaidi.business.nettelephone.calllog.utils;

/**
 * Created by ligg on 2016/4/7.
 */

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;

import com.kuaibao.skuaidi.entry.MessageEvent;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class NewPlayer implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener,MediaPlayer.OnErrorListener {

    public static MediaPlayer mediaPlayer; // 媒体播放器
    private static int position;
    private static String surl;
    private NewPlayer() {
        try {
            if(mediaPlayer==null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private volatile  static NewPlayer playerInstance;
    public static NewPlayer getInstance() {
        if (playerInstance == null) {
            synchronized (NewPlayer.class) {
                if (playerInstance == null) {
                    playerInstance=new NewPlayer();
                }
            }
        }
        return playerInstance;
    }

    public static void destoryInstance(){
        if(playerInstance!=null){
            playerInstance=null;
        }
    }

    public static void stopPrePlayWhileDownloading(){
        if(mediaPlayer==null)
            return;
        if(mediaPlayer.isPlaying()){
            MessageEvent event=new MessageEvent(0X02,"force stop play");
            event.setPosition(NewPlayer.position);
            EventBus.getDefault().post(event);
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public static void playUrlOrPath(String url,int position) {
        if(mediaPlayer==null || TextUtils.isEmpty(url))
            return;
        KLog.i("kb","传入参数:--->"+url+";position:--->"+position);
        //url="http://abv.cn/music/光辉岁月.mp3"; //测试用
        try {
            stopPrePlayWhileDownloading();
            NewPlayer.position=position;
            NewPlayer.surl=url;
            mediaPlayer.reset();
            //Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(url); // 设置数据源
            //mediaPlayer.prepare(); // prepare自动播放
            mediaPlayer.prepareAsync();// 开始在后台缓冲音频文件并返回
        } catch (IllegalArgumentException e) {
            KLog.e("kb",e.getMessage());
            stopAuto();
            MessageEvent event=new MessageEvent(0XE1,e.getMessage());
            event.message=NewPlayer.surl;
            EventBus.getDefault().post(event);
            e.printStackTrace();
        } catch (SecurityException e) {
            KLog.e("kb",e.getMessage());
            stopAuto();
            MessageEvent event=new MessageEvent(0XE2,e.getMessage());
            event.message=NewPlayer.surl;
            EventBus.getDefault().post(event);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            KLog.e("kb",e.getMessage());
            stopAuto();
            MessageEvent event=new MessageEvent(0XE3,e.getMessage());
            event.message=NewPlayer.surl;
            EventBus.getDefault().post(event);
            e.printStackTrace();
        } catch (IOException e) {
            KLog.e("kb",e.getMessage());
            stopAuto();
            MessageEvent event=new MessageEvent(0XE4,e.getMessage());
            event.message=NewPlayer.surl;
            EventBus.getDefault().post(event);
            e.printStackTrace();
        }
    }

    public static void play() {
        mediaPlayer.start();
    }
    public static void pause() {
        mediaPlayer.pause();
    }
    public static void stop(){
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
    }

    public static void releaseMediaPlayer(){
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public static void stopAuto() {
        KLog.i("kb","stopAuto");
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
        MessageEvent event=new MessageEvent(0X02,"auto stop play");
        event.setPosition(NewPlayer.position);
        EventBus.getDefault().post(event);
    }

    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp == mediaPlayer) {
            mediaPlayer.start();
            KLog.e("kb","mediaPlayer"+" onPrepared");
            MessageEvent event=new MessageEvent(0X01,"play start");
            EventBus.getDefault().post(event);
        }
        //mp.start();
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        KLog.e("kb","mediaPlayer"+" onCompletion");
        stopAuto();
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if(!mediaPlayer.isPlaying()) {
            return;
        }
        int currentProgress = 100 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        KLog.e("kb",currentProgress + "% play "+percent + " buffer");
    }

    public static String getPlayingInfo(){
        String playInfo="";
        if(mediaPlayer!=null){
            playInfo+=(mediaPlayer.getCurrentPosition()+";"+mediaPlayer.getDuration());
        }
        return playInfo;
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        switch (what)
        {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                KLog.e("kb","MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                KLog.e("kb","MEDIA_ERROR_SERVER_DIED" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                KLog.e("kb","MEDIA_ERROR_UNKNOWN" + extra);
                break;
        }
        stopAuto();
        MessageEvent event=new MessageEvent(0XE5,"play error");
        event.message=NewPlayer.surl;
        EventBus.getDefault().post(event);
        return false;
    }
}
