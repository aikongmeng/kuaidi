package com.kuaibao.skuaidi.business.nettelephone.calllog.adapter;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.calllog.utils.net.download.DownloadProgressListener;
import com.kuaibao.skuaidi.business.nettelephone.calllog.utils.net.download.FileDownloader;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import gen.greendao.bean.INetCallInfo;

/**
 * Created by lgg on 2016/10/16 16:39.
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
 * #                     no bug forever                #
 * #                                                   #
 */

public class NetCallLogAdapterV2 extends BaseQuickAdapter<INetCallInfo> {
    private SparseArray<String> playStateArray;
    private OnClickItemChildEvent mOnClickItemChildEvent;
    public SparseArray<String> getPlayStateArray() {
        return playStateArray;
    }
    public void setOnClickItemChildEvent(OnClickItemChildEvent onClickItemChildEvent) {
        mOnClickItemChildEvent = onClickItemChildEvent;
    }

    public NetCallLogAdapterV2(List<INetCallInfo> data) {
        super(R.layout.net_callinfo_item,data);
        playStateArray=new SparseArray<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, final INetCallInfo iNetCallInfo) {
        if(TextUtils.isEmpty(iNetCallInfo.getCalled_name())){
            helper.setVisible(R.id.cust_tel,false);
            helper.setText(R.id.caller_name,iNetCallInfo.getCalled());
        }else{
            helper.setVisible(R.id.cust_tel,true);
            helper.setText(R.id.caller_name,iNetCallInfo.getCalled_name());
            helper.setText(R.id.cust_tel,iNetCallInfo.getCalled());
        }
        helper.setText(R.id.call_time,UtilityTime.formatTime((long)(iNetCallInfo.getTalkDuration()*1000)));
        Utility.setTimeDate2(iNetCallInfo.getCreate_time(), (TextView)helper.getView(R.id.call_date));
        String state=this.playStateArray.get(helper.getAdapterPosition());
        if(!TextUtils.isEmpty(iNetCallInfo.getRecordurl())){
            helper.getView(R.id.ll_iv_audio_parent).setVisibility(View.VISIBLE);
            if(state!=null){
                if("default".equals(state)){
                    helper.setBackgroundRes(R.id.iv_play_netcall_audio,R.drawable.record_play_small);
                }else{
                    helper.setBackgroundRes(R.id.iv_play_netcall_audio,R.drawable.record_stop_small);
                }
            }else{
                helper.setBackgroundRes(R.id.iv_play_netcall_audio,R.drawable.record_play_small);
            }
            helper.setOnClickListener(R.id.iv_play_netcall_audio,new OnlineAudioPlayListener(iNetCallInfo.getRecordurl(),(ImageView)helper.getView(R.id.iv_play_netcall_audio),helper.getAdapterPosition()));
            helper.setOnClickListener(R.id.iv_netcall_share,new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(mOnClickItemChildEvent!=null){
                        mOnClickItemChildEvent.onClickShare(iNetCallInfo);
                    }
                }
            });
        }else{
            helper.getView(R.id.ll_iv_audio_parent).setVisibility(View.INVISIBLE);
            helper.setBackgroundRes(R.id.iv_play_netcall_audio,R.drawable.record_play_small);
        }
    }

    public class OnlineAudioPlayListener implements View.OnClickListener{
        private String url="";

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private int position;
        private ImageView iv_play;

        public OnlineAudioPlayListener(String url, ImageView iv_play, int position){
            this.url=url;
            this.iv_play=iv_play;
            this.position=position;
        }
        @Override
        public void onClick(View v) {
            if(!TextUtils.isEmpty(url)){
                String recordUrl="http://upload.kuaidihelp.com"+url;
                KLog.i("kb",recordUrl);
                String state=playStateArray.get(this.position);
                if(state!=null){
                    KLog.i("kb","iv_play Tag:--->"+state);
                    iv_play.setBackgroundResource("default".equals(state)?R.drawable.record_stop_small:R.drawable.record_play_small);
                    playStateArray.put(this.position,"default".equals(state)?"active":"default");
                }else{
                    iv_play.setBackgroundResource(R.drawable.record_stop_small);
                    playStateArray.put(this.position,"active");
                }
                KLog.i("kb","iv_play Tag:--->"+playStateArray.get(this.position));
                if("active".equals(playStateArray.get(this.position))){
                    checkAudioExists(recordUrl,v);// 播放条件判断
                }else{// 停止当前条目播放的
                    MessageEvent event=new MessageEvent(0XF2,"stop right now");
                    EventBus.getDefault().post(event);
                }
            }else {
                UtilToolkit.showToast("录音无法播放,刷新后重试");
            }
        }
        private void checkAudioExists(final String recordUrl,View v){
            File savDir = new File(FileUtils.toRootPath()+url.substring(0,url.lastIndexOf("/")));
            if(!FileUtils.fileExists(FileUtils.toRootPath()+url)){// 录音文件本地不存在，先下载，下载完毕再播放
                if(!Utility.isNetworkAvailable(mContext.getApplicationContext())){
                    UtilToolkit.showToast("录音无法播放,请检查您的网络连接");
                    String state=playStateArray.get(this.position,"");
                    iv_play.setBackgroundResource("default".equals(state)?R.drawable.record_stop_small:R.drawable.record_play_small);
                    playStateArray.put(this.position,"default".equals(state)?"active":"default");
                    return;
                }
                // 同时去异步下载下来
                if(!FileUtils.fileExists(FileUtils.toRootPath()+url.substring(0,url.lastIndexOf("/")))){
                    FileUtils.fileMkdirs(FileUtils.toRootPath()+url.substring(0,url.lastIndexOf("/")));
                }
                if(!savDir.isDirectory()){
                    KLog.i("kb","saveDir is not directory");
                    return;
                }
                MessageEvent event=new MessageEvent(0XF4,"stop preplay while downloading");
                EventBus.getDefault().post(event);
                download(recordUrl,savDir,position,url);
            }else{// 录音文件存在，直接播放
                MessageEvent event=new MessageEvent(0XF1,FileUtils.toRootPath()+url);
                event.setPosition(position);
                EventBus.getDefault().post(event);
            }
        }
    }
    public void download(String path,File savDir,int position,String url) {
        KLog.i("kb","开始异步下载$$$$");
        DownloadTask task=new DownloadTask(path,savDir,position,url);
        new Thread(task).start();
    }
    private final class DownloadTask implements Runnable {
        private String path;
        private File saveDir;
        private FileDownloader loader;
        private int position;
        private boolean isStart=false;
        private String url;
        public DownloadTask(String path, File saveDir,int position,String url) {
            this.path = path;
            this.saveDir = saveDir;
            this.position=position;
            this.url=url;
        }

        /**
         * 退出下载
         */
        public void exit() {
            if (loader != null)
                loader.exit();
        }

        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
            @Override
            public void onDownloadSize(int size,int totalSize,String filePath) {
                float percent=(float)size/totalSize;
                KLog.i("kb","download size:--->"+size+";totalSize:--->"+totalSize+";percent:--->"+percent);
                if(percent>=0.2){//缓冲到20%开始播放
                    if(!isStart){
                        String state=playStateArray.get(position);
                        if("active".equals(state)){
                            MessageEvent event=new MessageEvent(0XF1,filePath);
                            event.setPosition(position);
                            EventBus.getDefault().post(event);
                        }
                        isStart=true;
                    }
                }
            }

            @Override
            public void onDownloadComplete(String filePath) {
                KLog.i("kb","download complete");
            }
        };

        public void run() {
            try {
                // 实例化一个文件下载器
                loader = new FileDownloader(SKuaidiApplication.getInstance(), path, saveDir, 10);
                // 设置进度条最大值
                loader.download(downloadProgressListener);
            } catch (Exception e) {
                MessageEvent event=new MessageEvent(0X02,"auto stop play");
                event.setPosition(position);
                EventBus.getDefault().post(event);
                KLog.e("kb",e.getMessage());
                FileUtils.fileDelete(FileUtils.toRootPath()+url);// 清除下载错误的文件，防止以后的播放错误
                MessageEvent event1=new MessageEvent(0XEE,"录音播放失败,请检查您的网络环境");
                EventBus.getDefault().post(event1);
            }
        }
    }
    public interface OnClickItemChildEvent{
        void onClickShare(INetCallInfo callInfo);
    }
}
