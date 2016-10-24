package com.kuaibao.skuaidi.activity.template.sms_yunhu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.entry.AudioProperties;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.utility.MediaFile;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.utility.WavFileReader;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.DrawableLeftWithTextViewCenter;
import com.kuaibao.skuaidi.recorder.CloudCallRecordUtility;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by 顾冬冬[上传云呼语音模板]
 */
public class UploadTemplateCloudCallActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    private Activity mActivity;
    private AudioProperties audioProperties;
    private boolean isWavFile = false;// 是否是WAV文件

    private DrawableLeftWithTextViewCenter select_audio;// 选择语音文件按钮
    private Button upload;// 上传按钮
    private TextView format ;
    private String filePath;// 文件路径
    private String fileFormat;// 文件格式（文件名并且带后缀：eg:.wav）
    private String  fileName;//文件名

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case AddVoiceModelActivity.ADD_VOICE_SUCCESS:
//                    getModels();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_template_cloudcall);

        mActivity = this;

        findView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK){
                filePath = Utility.getAbsolutePathByURI(mActivity,data);

//                filePath = Uri.decode(data.getDataString());
//                //通过data.getDataString()得到的路径如果包含中文路径，则会出现乱码现象，经过Uri.decode()函数进行解码，得到正确的路径。
//                // 但是此时路径为Uri路径，必须转换为String路径，网上有很多方法，本人通过对比发现，Uri路径里多了"file：//"
//                // 字符串，所以采用以下方法将前边带的字符串截取掉，获得String路径，可能通用性不够好，下一步会学习更好的方法。
//                filePath = filePath.substring(7, filePath.length());

                isWavFile = MediaFile.isWavFileType(filePath);

                try {
                    audioProperties =  WavFileReader.openFile(filePath);// 获取语音文件头信息
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!Utility.isEmpty(filePath) && filePath.contains("/"))// 获取路径中最后一个"/"后面的所有文字
                    fileFormat = filePath.substring(filePath.lastIndexOf("/")+1);
                fileName = fileFormat;
                if (!Utility.isEmpty(fileName) && fileName.contains("."))// 去掉文件名称的后缀
                    fileName = fileName.substring(0,fileName.indexOf("."));
                if (fileName.length()>20)// 上传模板title名字长度不能超过20个字
                    fileName = fileName.substring(0,20);

                if (null != audioProperties){
                    if (0L != audioProperties.getFile_chunkSize()){
                        float fileSize_kb = audioProperties.getFile_chunkSize();// 获取文件总长

                        String fileSize_n;// 将文件转成字节样式
                        if (fileSize_kb>1024){
                            fileSize_n = (int)(fileSize_kb/1024)+"kb";
                        }else{
                            fileSize_n = fileSize_kb+"b";
                        }
                        String str = fileFormat+"("+fileSize_n+")"+"\n重选";

                        int pos_fileName_end = str.indexOf("(");
                        int pos_fileSize_end = str.indexOf(")")+1;

                        int pos_reselect_start = str.length()-2;
                        int pos_reselect_end = str.length();

                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(12, 186, 160)), 0, pos_fileName_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(152, 152, 152)), pos_fileName_end, pos_fileSize_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(255,59,48)), pos_reselect_start, pos_reselect_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        select_audio.setText(style);
                    }else {
                        String str = fileFormat+"\n重选";
                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(12, 186, 160)), 0, str.length()-2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                        style.setSpan(new ForegroundColorSpan(Color.rgb(152, 152, 152)), pos_fileName_end, pos_fileSize_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(255,59,48)), str.length()-2, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        select_audio.setText(style);
                    }
                    upload.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                    upload.setEnabled(true);
                    /*format.setText("filesize: "+audioProperties.getFile_chunkSize()
                            +"\nfileFormat:"+audioProperties.getFile_Format()
                            +"\n采样率[编码格式]："+audioProperties.getSamplerate()
                            +"\n位数："+audioProperties.getBitspersample()
                            +"\n编码："+audioProperties.getAudioFormat()
                            +"\n每秒所需字节数："+audioProperties.getByterate()
                            +"\n对齐："+audioProperties.getBlockalign()
                            +"\npath");*/
                }else{
                    String str = fileFormat+"\n重选";
                    SpannableStringBuilder style = new SpannableStringBuilder(str);
                    style.setSpan(new ForegroundColorSpan(Color.rgb(12, 186, 160)), 0, str.length()-2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(Color.rgb(255,59,48)), str.length()-2, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    select_audio.setText(style);
                }
                select_audio.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                select_audio.setGravity(Gravity.CENTER);
            }
    }

    private void findView(){
        ImageView back = (ImageView) findViewById(R.id.iv_title_back);
        TextView title = (TextView) findViewById(R.id.tv_title_des);
        select_audio = (DrawableLeftWithTextViewCenter) findViewById(R.id.select_audio);
        select_audio.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notify_addmodel_cloud_icon,0,0,0);
        upload = (Button) findViewById(R.id.upload);
        format = (TextView) findViewById(R.id.format);

        title.setText("上传语音模板");
        back.setOnClickListener(this);
        select_audio.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /** 添加模板 **/
    private void addModel(String title, long recordLength, String recordPath) {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "add");
            json.put("title", title);// 标题
            json.put("len", recordLength);// 录音时长
            json.put("voice", recordPath);// 录音流
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        dismissProgressDialog();//mActivity);
        if ("ivr.voice".equals(sname)) {
            if ("add".equals(act)) {
                finish();
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//mActivity);
        if ("ivr.voice".equals(sname)) {
            UtilToolkit.showToast_Custom(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        dismissProgressDialog();//mActivity);
        if ("ivr.voice".equals(sname)) {
            UtilToolkit.showToast_Custom(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_back:// 返回
                finish();
                break;
            case R.id.select_audio:// 选择语音文件
                // 打开系统文件浏览功能
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
                intent.setType("audio/wav");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
                break;
            case R.id.upload:// 上传语音
                if (isWavFile){// 是正确的wav文件且是编码为8K，16位的音频
                    if (audioProperties != null){
                        int codeFormat = audioProperties.getSamplerate();// 编码格式-8k
                        int bitspersample= audioProperties.getBitspersample();// 位数-16位
                        long fileSize = audioProperties.getFile_chunkSize();// 文件大小(单位：b)
                        int second = (int) audioProperties.getPlay_time();// 获取wav文件播放时间
                        long totalSize = 3221225472L;// 文件最大可上传大小|单位：b
                        if (!Utility.isEmpty(codeFormat) && codeFormat==8000){
                            if (!Utility.isEmpty(bitspersample) && bitspersample==16){
                                if (!Utility.isEmpty(fileSize) && fileSize<totalSize){
                                    try {
                                        addModel(fileName, second, new CloudCallRecordUtility().readStream(filePath));
                                        showProgressDialog("");//mActivity,"请稍候...");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    UtilToolkit.showToast_Custom("您上传的语音文件超过3M");
                                }
                            }else{
                                UtilToolkit.showToast_Custom("您的编码位数不正确");
                            }
                        }else{
                            UtilToolkit.showToast_Custom("您的编码格式非8kHz");
                        }
                    }else{
                        UtilToolkit.showToast("您选择的文件不正确");
                    }
                }else{
                    UtilToolkit.showToast_Custom("文件格式不正确，请重新选择");
                }
                break;

        }
    }
}
