package com.kuaibao.skuaidi.business.nettelephone.calllog.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.service.LanOrPieService;
import com.kuaibao.skuaidi.util.DensityUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import gen.greendao.bean.ICallLog;


/**
 * Created by lgg on 2016/10/9 10:02.
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

public class LocalCallLogAdapter extends BaseQuickAdapter<ICallLog> {

    private Context context;
    private SparseArray<String> playStateArray=new SparseArray<>();
    //private StreamAudioPlayer mStreamAudioPlayer;
    //static final int BUFFER_SIZE = 2048;
    //byte[] mBuffer;
    //private int currentPlayPosition=-1;
    public SparseArray<String> getPlayStateArray() {
        return playStateArray;
    }

    public LocalCallLogAdapter(Context context, List<ICallLog> data) {
        super(R.layout.call_logs_item2, data);
        this.context=context;
//        mStreamAudioPlayer = StreamAudioPlayer.getInstance();
//        mBuffer = new byte[BUFFER_SIZE];
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final ICallLog iCallLog) {
        String callerName=iCallLog.getCustomerName();
        String phoneNum=iCallLog.getCallNum();
        phoneNum = phoneNum.length() >= 11 ? phoneNum.substring(phoneNum.length() - 11, phoneNum.length()) : phoneNum;
        if (TextUtils.isEmpty(callerName) || "新客户".equals(callerName)) {
            baseViewHolder.setText(R.id.caller_name_new,phoneNum);
            TextView callNum=baseViewHolder.getView(R.id.call_phone_new);
            callNum.setText("");
            callNum.setPadding(DensityUtil.dip2px(context, 20), 0, 0, 0);
        } else {
            baseViewHolder.setText(R.id.caller_name_new,callerName);
            TextView callNum=baseViewHolder.getView(R.id.call_phone_new);
            callNum.setText(phoneNum);
            callNum.setPadding(DensityUtil.dip2px(context, 20), 0, DensityUtil.dip2px(context, 12), 0);
        }
        baseViewHolder.setImageResource(R.id.iv_outgoing_new,iCallLog.getCallType()==ICallLog.TYPE_INCOMING_CALL ? R.drawable.icon_call_records_outing_calls:R.drawable.icon_call_records_incoming_calls);
        if(iCallLog.getCallDurationTime()>0){
            baseViewHolder.setVisible(R.id.call_time,true);
            baseViewHolder.setText(R.id.call_time,UtilityTime.formatTime(iCallLog.getCallDurationTime()));
        }else{
            baseViewHolder.setVisible(R.id.call_time,false);
        }
        Utility.setTimeDate2(UtilityTime.getDateTimeByMillisecond2(iCallLog.getCallDate(),UtilityTime.YYYY_MM_DD_HH_MM_SS), (TextView)baseViewHolder.getView(R.id.call_date_new));

        String state=this.playStateArray.get(baseViewHolder.getAdapterPosition());
        final ImageView iv_play_radio_new=baseViewHolder.getView(R.id.iv_play_radio_new);
        if(!TextUtils.isEmpty(iCallLog.getRecordingFilePath()) && (new File(iCallLog.getRecordingFilePath())).exists()){
            iv_play_radio_new.setVisibility(View.VISIBLE);
            if(state!=null){
                if("default".equals(state)){
                    iv_play_radio_new.setBackgroundResource(R.drawable.record_play_small);
                }else{
                    iv_play_radio_new.setBackgroundResource(R.drawable.record_stop_small);
                }
            }else{
                iv_play_radio_new.setBackgroundResource(R.drawable.record_play_small);
            }
            iv_play_radio_new.setOnClickListener(new LocalRecordPlayListener(iCallLog.getRecordingFilePath(),iv_play_radio_new,baseViewHolder.getAdapterPosition()));
//            iv_play_radio_new.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String state=playStateArray.get(baseViewHolder.getAdapterPosition());
//                    if(state!=null){
//                        KLog.i("kb","iv_play Tag:--->"+state);
//                        iv_play_radio_new.setBackgroundResource("default".equals(state)?R.drawable.record_stop_small:R.drawable.record_play_small);
//                        playStateArray.put(baseViewHolder.getAdapterPosition(),"default".equals(state)?"active":"default");
//                    }else{
//                        iv_play_radio_new.setBackgroundResource(R.drawable.record_stop_small);
//                        playStateArray.put(baseViewHolder.getAdapterPosition(),"active");
//                    }
//                    KLog.i("kb","iv_play Tag:--->"+playStateArray.get(baseViewHolder.getAdapterPosition()));
//                    if("active".equals(playStateArray.get(baseViewHolder.getAdapterPosition()))){
//                        if(currentPlayPosition>=0){
//                            if(mStreamAudioPlayer!=null){
//                                currentPlayPosition=-1;
//                                mStreamAudioPlayer.release();
//                            }
//                            playStateArray.put(currentPlayPosition,"default");
//                            notifyItemChanged(currentPlayPosition);
//                        }
//                        if(currentPlayPosition<0)
//                        play(new File(iCallLog.getRecordingFilePath()),iv_play_radio_new,baseViewHolder.getAdapterPosition());
//                    }else{// 停止当前条目播放的
//                        if(mStreamAudioPlayer!=null){
//                            playFinish(iv_play_radio_new,baseViewHolder.getAdapterPosition(),"");
//                            mStreamAudioPlayer.release();
//                        }
//                    }
//                }
//            });
        }else{
            iv_play_radio_new.setVisibility(View.INVISIBLE);
            iv_play_radio_new.setBackgroundResource(R.drawable.record_play_small);
        }

        final ProgressActivity lanProgress=baseViewHolder.getView(R.id.progress_call_log);
        final ProgressActivity pieProgress=baseViewHolder.getView(R.id.progress_call_log_2);
        ((ProgressActivity)baseViewHolder.getView(R.id.progress_call_log)).showContent();
        ((ProgressActivity)baseViewHolder.getView(R.id.progress_call_log_2)).showContent();
        TextView btnLan=baseViewHolder.getView(R.id.mark_lanPiece_new);
        btnLan.setEnabled(0==iCallLog.getHadLan());
        TextView btnPie=baseViewHolder.getView(R.id.mark_piePiece_new);
        btnPie.setEnabled(0==iCallLog.getHadPie());

        btnLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isNetworkAvailable(mContext)){
                    UtilToolkit.showToast("请检查您的网络连接,稍后重试");
                    return;
                }
                if(!lanProgress.isLoading()){
                    lanProgress.showLoading();
                }
                LanOrPieService.buildAndStartIntent(context,LanOrPieService.LAN_OPERATION,mData.get(baseViewHolder.getAdapterPosition()),
                        baseViewHolder.getAdapterPosition(),LanOrPieService.FROM_CALL_FRAGMENT);
            }
        });
        btnPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isNetworkAvailable(mContext)){
                    UtilToolkit.showToast("请检查您的网络连接,稍后重试");
                    return;
                }
                if(!pieProgress.isLoading()){
                    pieProgress.showLoading();
                }
                LanOrPieService.buildAndStartIntent(context,LanOrPieService.PIE_OPERATION,mData.get(baseViewHolder.getAdapterPosition()),
                        baseViewHolder.getAdapterPosition(),LanOrPieService.FROM_CALL_FRAGMENT);
            }
        });
    }

//    private void play(File mOutputFile,final ImageView ivPlay,final int position) {
//        currentPlayPosition=position;
//        Observable.just(mOutputFile).subscribeOn(Schedulers.io()).subscribe(new Action1<File>() {
//            @Override
//            public void call(File file) {
//                try {
//                    mStreamAudioPlayer.init();
//                    FileInputStream inputStream = new FileInputStream(file);
//                    int read;
//                    while ((read = inputStream.read(mBuffer)) > 0) {
//                        if(currentPlayPosition>=0){
//                            mStreamAudioPlayer.play(mBuffer, read);
//                        }else{
//                            break;
//                        }
//                    }
//                    inputStream.close();
//                    playFinish(ivPlay,position,"");
//                    mStreamAudioPlayer.release();
//                } catch (IOException e) {
//                    playFinish(ivPlay,position,e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }, new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                playFinish(ivPlay,position,throwable.getMessage());
//                throwable.printStackTrace();
//            }
//        });
//    }
//
//    private void playFinish(final ImageView ivPlay,final int position,final String msg){
//        currentPlayPosition=-1;
//        ivPlay.post(new Runnable() {
//            @Override
//            public void run() {
//                ivPlay.setBackgroundResource(R.drawable.record_play_small);
//                playStateArray.put(position,"default");
//                if(!TextUtils.isEmpty(msg)){
//                    UtilToolkit.showToast(msg);
//                }
//            }
//        });
//    }

    private class LocalRecordPlayListener implements View.OnClickListener{
        private String url="";
        public int getPosition() {
            return position;
        }
        public void setPosition(int position) {
            this.position = position;
        }
        private int position;
        private ImageView iv_play;
        public LocalRecordPlayListener(String url, ImageView iv_play, int position){
            this.url=url;
            this.iv_play=iv_play;
            this.position=position;
        }
        @Override
        public void onClick(View v) {
            if(!TextUtils.isEmpty(url)){
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
                    MessageEvent event=new MessageEvent(0XF1,url);
                    event.setPosition(position);
                    EventBus.getDefault().post(event);
                }else{// 停止当前条目播放的
                    MessageEvent event=new MessageEvent(0XF2,"stop right now");
                    EventBus.getDefault().post(event);
                }
            }else {
                UtilToolkit.showToast("录音无法播放,刷新后重试");
            }
        }
    }
}
