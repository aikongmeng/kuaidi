package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecordDetail;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gdd
 * on 2016/6/14.
 */
public class CloudRecordDetailAdapter extends XRecyclerView.Adapter<XRecyclerView.ViewHolder> {

    private Activity _context;
    private List<HistoryRecordDetail.ListBean> _list;
    private String voiceDirectory;// 路径本地目录最后一层
    private File fileDirectory;// 文件目录最后一层
    private String localPath;
    private String serverPath;

    public CloudRecordDetailAdapter(Activity context, List<HistoryRecordDetail.ListBean> listBean) {
        _context = context;
        _list = listBean;
    }

    @Override
    public XRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(_context).inflate(R.layout.record_detail, parent, false);
        return new MyCloudRecordDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(XRecyclerView.ViewHolder holder, int position) {
        HistoryRecordDetail.ListBean listBean = _list.get(position);
        if (holder instanceof MyCloudRecordDetailViewHolder) {
            MyCloudRecordDetailViewHolder mHolder = (MyCloudRecordDetailViewHolder) holder;
            mHolder.time.setText(UtilityTime.timeFormat(listBean.getSpeak_time() * 1000));
            if (!Utility.isEmpty(listBean.getSpeaker_role())) {
                if (listBean.getSpeaker_role().equals("counterman")) {// 快递员回复
                    mHolder.sSection.setVisibility(View.VISIBLE);
                    mHolder.cSection.setVisibility(View.GONE);

                    if (!Utility.isEmpty(listBean.getContent_type())) {// 快递员回复类型|包括【1：纯文本|3：非云呼语音|6：云呼语音】
                        mHolder.sVoiceIcon.setVisibility(listBean.getContent_type().equals("1") ? View.GONE : View.VISIBLE);

                        String path = "";// 语音路径
                        String title = "";// 语音标题
                        if (listBean.getContent_type().equals("1")) {
                            mHolder.sContext.setText(listBean.getContent());
                            mHolder.sTime.setVisibility(View.GONE);
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
                            mHolder.sContext.setText("");
                            mHolder.sTime.setVisibility(View.VISIBLE);
                            mHolder.sTime.setText(listBean.getVoice_length() + "\"");

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
                            mHolder.sContext.setText(title);
                            mHolder.sVoiceIcon.setVisibility(View.VISIBLE);
                            mHolder.sTime.setVisibility(View.VISIBLE);
                            mHolder.sTime.setText(listBean.getVoice_length() + "\"");

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
                    mHolder.sSection.setVisibility(View.GONE);
                    mHolder.cSection.setVisibility(View.VISIBLE);

                    if (!Utility.isEmpty(listBean.getContent_type())) {

                        switch (listBean.getContent_type()) {
                            case "1":
                                mHolder.cContext.setText(!Utility.isEmpty(listBean.getContent()) ? listBean.getContent() : "");
                                mHolder.cVoiceIcon.setVisibility(View.GONE);
                                mHolder.cTime.setVisibility(View.GONE);
                                break;
                            case "3":
                                mHolder.cContext.setText("");
                                mHolder.cVoiceIcon.setVisibility(View.VISIBLE);
                                mHolder.cTime.setVisibility(View.VISIBLE);
                                mHolder.cTime.setText(listBean.getVoice_length() + "\"");

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
                                mHolder.cContext.setText("语音留言");
                                mHolder.cVoiceIcon.setVisibility(View.VISIBLE);
                                mHolder.cTime.setVisibility(View.GONE);

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
        }
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

    @Override
    public int getItemCount() {
        return _list.size();
    }

    @OnClick({R.id.s_play, R.id.c_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.s_play:// 播放s語音
                break;
            case R.id.c_play:// 播放c語音
                break;
        }
    }

    public class MyCloudRecordDetailViewHolder extends XRecyclerView.ViewHolder {
        @BindView(R.id.time)
        TextView time;// 時間
        @BindView(R.id.s_time)
        TextView sTime;// s語音時長
        @BindView(R.id.s_context)
        TextView sContext;// s文本
        @BindView(R.id.s_voice_icon)
        ImageView sVoiceIcon;// s語音圖標
        @BindView(R.id.s_section)
        RelativeLayout sSection;// s部份
        @BindView(R.id.c_voice_icon)
        ImageView cVoiceIcon;// c語音圖標
        @BindView(R.id.c_context)
        TextView cContext;// c文本
        @BindView(R.id.c_time)
        TextView cTime;// c語音時長
        @BindView(R.id.c_section)
        RelativeLayout cSection;// c部份

        public MyCloudRecordDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
