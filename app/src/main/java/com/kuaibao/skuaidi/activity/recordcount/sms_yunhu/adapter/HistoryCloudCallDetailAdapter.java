package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ABSBaseAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecordDetail;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by gdd
 * on 2016/6/15.
 */
public class HistoryCloudCallDetailAdapter extends ABSBaseAdapter<HistoryRecordDetail.ListBean>{

    private Activity _activity;
    private String localPath;
    private String serverPath;
    private PlayVoice i_playVoice;

    public HistoryCloudCallDetailAdapter(Activity activity, List<HistoryRecordDetail.ListBean> list){
        _activity = activity;
        addEntity(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(_activity).inflate(R.layout.record_detail,parent,false);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.sTime = (TextView) convertView.findViewById(R.id.s_time);
            holder.sContext = (TextView) convertView.findViewById(R.id.s_context);
            holder.sVoiceIcon = (ImageView) convertView.findViewById(R.id.s_voice_icon);
            holder.sSection = (RelativeLayout) convertView.findViewById(R.id.s_section);
            holder.cVoiceIcon = (ImageView) convertView.findViewById(R.id.c_voice_icon);
            holder.cContext = (TextView) convertView.findViewById(R.id.c_context);
            holder.cTime = (TextView) convertView.findViewById(R.id.c_time);
            holder.cSection = (RelativeLayout) convertView.findViewById(R.id.c_section);
            holder.sPlay = (LinearLayout) convertView.findViewById(R.id.s_play);
            holder.cPlay = (LinearLayout) convertView.findViewById(R.id.c_play);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final HistoryRecordDetail.ListBean listBean = (HistoryRecordDetail.ListBean) getItem(position);
        holder.sPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_playVoice.playVoice(listBean);
            }
        });
        holder.cPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_playVoice.playVoice(listBean);
            }
        });

        holder.time.setText(UtilityTime.timeFormat(listBean.getSpeak_time() * 1000));
        if (!Utility.isEmpty(listBean.getSpeaker_role())) {
            if (listBean.getSpeaker_role().equals("counterman")) {// 快递员回复
                holder.sSection.setVisibility(View.VISIBLE);
                holder.cSection.setVisibility(View.GONE);

                if (!Utility.isEmpty(listBean.getContent_type())) {// 快递员回复类型|包括【1：纯文本|3：非云呼语音|6：云呼语音】
                    holder.sVoiceIcon.setVisibility(listBean.getContent_type().equals("1") ? View.GONE : View.VISIBLE);

                    String path = "";// 语音路径
                    String title = "";// 语音标题
                    if (listBean.getContent_type().equals("1")) {
                        holder.sContext.setText(listBean.getContent());
                        holder.sTime.setVisibility(View.GONE);
                    } else {
                        try {
                            JSONObject content = new JSONObject(listBean.getContent());
                            path = content.optString("path");
                            title = content.optString("title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (listBean.getContent_type().equals("3")) {
                        holder.sContext.setText("");
                        holder.sTime.setVisibility(Utility.isEmpty(listBean.getVoice_length())?View.GONE:View.VISIBLE);
                        holder.sTime.setText(listBean.getVoice_length() + "\"");

                        localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + listBean.getContent();
                        serverPath = "http://upload.kuaidihelp.com/liuyan/new/" + listBean.getContent();
                        File fileVoice = new File(localPath);

                        String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
                        File fileDirectory = new File(voiceDirectory);
                        if (!fileDirectory.exists()) {
                            fileDirectory.mkdirs();
                        }
                        if (!fileVoice.exists()) {
                            downloadFile(serverPath,localPath);
                        }
                    } else if (listBean.getContent_type().equals("6")) {
                        holder.sContext.setText(Utility.isEmpty(title)?"":title);
                        holder.sVoiceIcon.setVisibility(View.VISIBLE);
                        holder.sTime.setVisibility(Utility.isEmpty(listBean.getVoice_length())?View.GONE:View.VISIBLE);
                        holder.sTime.setText(listBean.getVoice_length() + "\"");

                        String voiceName = path.substring(path.lastIndexOf("/") + 1);
                        localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName;
                        File file = new File(localPath);

                        String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
                        File curDirectory = new File(voiceDirectory);
                        if (!curDirectory.exists()) {
                            curDirectory.mkdirs();
                        }

                        if (!file.exists()) {
                            downloadFile(path,localPath);
                        }
                    }
                }

            } else {// 包括：speaker_role为【user,shop】{用户或网点回复}
                holder.sSection.setVisibility(View.GONE);
                holder.cSection.setVisibility(View.VISIBLE);

                if (!Utility.isEmpty(listBean.getContent_type())) {

                    switch (listBean.getContent_type()) {
                        case "1":
                            holder.cContext.setText(!Utility.isEmpty(listBean.getContent()) ? listBean.getContent() : "");
                            holder.cVoiceIcon.setVisibility(View.GONE);
                            holder.cTime.setVisibility(View.GONE);
                            break;
                        case "3":
                            holder.cContext.setText("");
                            holder.cVoiceIcon.setVisibility(View.VISIBLE);
                            holder.cTime.setVisibility(Utility.isEmpty(listBean.getVoice_length())?View.GONE:View.VISIBLE);
                            holder.cTime.setText(listBean.getVoice_length() + "\"");

                            localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + listBean.getContent();// 本地路径
                            serverPath = "http://upload.kuaidihelp.com/liuyan/new/" + listBean.getContent();
                            File localFile = new File(localPath);// 文件
                            String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";// 本地路径最后一层目录
                            File fileDirectory = new File(voiceDirectory);
                            if (!fileDirectory.exists()) {
                                fileDirectory.mkdirs();
                            }

                            if (!localFile.exists()) {
                                downloadFile(serverPath,localPath);
                            }
                            break;
                        case "7":
                            holder.cContext.setText("语音留言");
                            holder.cVoiceIcon.setVisibility(View.VISIBLE);
                            holder.cTime.setVisibility(View.GONE);

                            String voiceName = listBean.getContent().substring(listBean.getContent().lastIndexOf("/") + 1);// 获取文件名称
                            localPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName;
                            serverPath = "http://upload.kuaidihelp.com/" + listBean.getContent().substring(8);
                            File fileVoice = new File(localPath);
                            voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
                            fileDirectory = new File(voiceDirectory);
                            if (!fileDirectory.exists()) {
                                fileDirectory.mkdirs();
                            }
                            if (!fileVoice.exists()) {
                                downloadFile(serverPath,localPath);
                            }
                            break;
                        default:

                            break;
                    }
                }
            }
        }
        return convertView;
    }

    private void downloadFile(String server_path, String local_path) {
        FinalHttp finalHttp = new FinalHttp();
        finalHttp.download(server_path, local_path, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(File t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    public void setPlayVoice(PlayVoice playVoice){
        this.i_playVoice = playVoice;
    }

    public interface PlayVoice{
        void playVoice(HistoryRecordDetail.ListBean listBean);
    }

    class ViewHolder{
        TextView time;// 時間
        TextView sTime;// s語音時長
        TextView sContext;// s文本
        ImageView sVoiceIcon;// s語音圖標
        RelativeLayout sSection;// s部份
        ImageView cVoiceIcon;// c語音圖標
        TextView cContext;// c文本
        TextView cTime;// c語音時長
        RelativeLayout cSection;// c部份
        LinearLayout sPlay;// s播放語音
        LinearLayout cPlay;// c播放語音
    }
}
