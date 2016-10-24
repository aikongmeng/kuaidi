package com.kuaibao.skuaidi.activity.sendcloudcall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.sendcloudcall.adapter.SendCloudCallBachSignAdapter;
import com.kuaibao.skuaidi.activity.sendcloudcall.apibaseactivity.SendCloudCallBachSignBaseActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.utility.SendCloudCallBachSignUtility;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.ShowTextPop;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dao.SaveNoDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.service.AudioOnLinePlayService;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 批量签收发云呼
 **/
public class SendCloudCallBachSignActivity extends SendCloudCallBachSignBaseActivity implements SendCloudCallBachSignAdapter.ButtonClickListener {

    private final int REQUEST_CODE = 0x10110;
    public static final int RESULT_CODE = 0x10111;

    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_more)
    SkuaidiTextView tvMore;
    @BindView(R.id.btn_add_cloud_voice)
    RelativeLayout btnAddCloudVoice;// 选择语音模板-小
    @BindView(R.id.ll_voice_model)
    LinearLayout llVoiceModel;// 选择语音模板-大
    @BindView(R.id.tv_vocie_title)
    TextView tvVoiceTitle;// 模板标题
    @BindView(R.id.tv_rec_total_time)
    TextView tvRecTotalTime;// 总时间
    @BindView(R.id.tv_rec_time)
    TextView tvRecTime;// 语音播放进度
    @BindView(R.id.voice_record_progressbar)
    ProgressBar progressBar;// 进度条
    @BindView(R.id.iv_play_icon)
    ImageView ivPlayIcon;// 播放按钮图片

    private Activity mActivity;

    private InputMethodManager imm;
    private SendCloudCallBachSignAdapter adapter;
    private Timer timer;
    private SaveNoEntry saveNoEntry;

    private List<NumberPhonePair> listData;
    private boolean isPlaying = false;// 是否正在播放-false：未播放
    private int i = 0;
    private int _no1;
    private int _no2;

    private String localPath = "";// 录音本地地址
    private String serverPath = "";// 服务器地址
    private String voice_name = "";// 语音名称
    private int voice_length = 0;// 语音时长
    private String voice_ivid ;// 语音模板ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_cloud_call_bach_sign);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mActivity = this;

        initView();
        getsetData();
        setListener();

        Intent intent=new Intent(mActivity, AudioOnLinePlayService.class);
        mActivity.startService(intent);
    }

    private void initView() {
        tvTitleDes.setText("云呼");
        tvMore.setText("发送");
    }

    private void getsetData(){
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        listData = (List<NumberPhonePair>) getIntent().getSerializableExtra("numberPhonePairs");// 从派件群发短信中获取数据
//        listData = new ArrayList<>();
//        for (int i = 0;i<10;i++){
//            NumberPhonePair n = new NumberPhonePair();
//            n.setDh("12312312312312");
//            listData.add(n);
//        }


        SendCloudCallBachSignUtility.setBH(listData);
        if (listData == null){
            listData = new ArrayList<>();
        }
        adapter = new SendCloudCallBachSignAdapter(mActivity,listData);
        adapter.setButtonClickListener(SendCloudCallBachSignActivity.this);
        list.setAdapter(adapter);
    }

    private void setListener() {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){// 列表滑动的时候关闭软键盘
                    if (mActivity.getCurrentFocus() != null)
                        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void refreshUI(CloudRecord _cr) {
        if (_cr != null) {
            btnAddCloudVoice.setVisibility(View.GONE);
            llVoiceModel.setVisibility(View.VISIBLE);
            tvVoiceTitle.setText(Utility.isEmpty(_cr.getTitle()) ? "派件专用语音":_cr.getTitle());// 设置语音标题
            tvRecTotalTime.setText(Utility.formatTime(_cr.getVoiceLength()));
            localPath = _cr.getPathLocal();// 录音本地地址
            serverPath = _cr.getPathService();// 录音服务器地址
            voice_name = _cr.getFileName();// 录音名称
            voice_length = _cr.getVoiceLength();// 录音时长
            voice_ivid = _cr.getIvid();// 录音ID
            progressBar.setMax(voice_length);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE){
            CloudRecord cr = (CloudRecord) data.getSerializableExtra("voiceTemplate");
            refreshUI(cr);
        }

    }

    @Subscribe
    public void onEvent(final MessageEvent msg) {
        switch(msg.type){
            case 0xEE:
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        int position=Integer.parseInt(msg.message);
                        KLog.i("kb","当前EditText位置:---->"+position);
                        if (msg.position >= 0) {
                            //得到要更新的item的view
                            SendCloudCallBachSignUtility.setNextEditTextFoucs(list,position+1,listData);
                        }
                    }
                });
                break;
            case 0X01:// play start
                timing();
                break;
            case 0x02:// play stop
                stopPlay();
                break;
            case 0XBB1:// whether exist the same phone number
                String[] index = msg.message.split(";");
                if (!Utility.isEmpty(index)) {
                    _no1 = Integer.parseInt(index[0]);
                    _no2 = Integer.parseInt(index[1]);
                }
                break;
        }
    }

    @OnClick({R.id.iv_title_back, R.id.tv_more, R.id.btn_add_cloud_voice,R.id.ll_voice_model,R.id.ll_play_icon})
    public void onClick(View view) {
        Intent mIntent;
        switch (view.getId()) {
            case R.id.iv_title_back:// 返回
                stopPlay();
                finish();
                break;
            case R.id.tv_more:// 发送云呼
                stopPlay();
                sendCloud();
                break;
            case R.id.btn_add_cloud_voice:// 选择语音模板-小
                UMShareManager.onEvent(mActivity, "Cloud_select_model", "CloudVoice", "云呼：选择语音模板");
                stopPlay();
                mIntent = new Intent(mActivity, AddVoiceModelActivity.class);
                mIntent.putExtra("fromActivityType","sendCloudCallBachSign");
                startActivityForResult(mIntent, REQUEST_CODE);
                break;
            case R.id.ll_voice_model:// 选择语音模板-大
                UMShareManager.onEvent(mActivity, "Cloud_select_model", "CloudVoice", "云呼：选择语音模板");
                stopPlay();
                mIntent = new Intent(mActivity, AddVoiceModelActivity.class);
                mIntent.putExtra("fromActivityType","sendCloudCallBachSign");
                startActivityForResult(mIntent, REQUEST_CODE);
                break;
            case R.id.ll_play_icon:// 播放语音
                if (!isPlaying) {// 如果当前没有播放则打开播放
                    File voice = new File(localPath);
                    if (voice.exists()) {// 如果本地存在这个录音
                        MessageEvent event = new MessageEvent(0XF1, localPath);
                        EventBus.getDefault().post(event);
                    } else {// 如果本地不存在这个录音就需要下载
                        // 判断是否存在SD_CARD
                        if (!Utility.getSDIsExist()) {
                            UtilToolkit.showToast("SD卡不存在");
                            return;
                        }
                        File voiceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord");
                        boolean mkdirsStatus = false;
                        if (!voiceDirectory.exists()) {
                            mkdirsStatus = voiceDirectory.mkdirs();
                        }
                        if (!mkdirsStatus)
                            return;
                        FinalHttp fh = new FinalHttp();
                        fh.download(serverPath, Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/" + voice_name
                                + ".wav", new AjaxCallBack<File>() {
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                            }

                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                MessageEvent event = new MessageEvent(0XF1, localPath);
                                EventBus.getDefault().post(event);
                            }

                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                super.onFailure(t, errorNo, strMsg);
                                UtilToolkit.showToast("录音下载失败或已不存在该录音，请删除");
                            }
                        });
                    }
                }else{

                    stopPlay();
                }
                break;
        }
    }

    private void timing(){
        isPlaying = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (i < voice_length){
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(i);
                            tvRecTime.setText(Utility.formatTime(i));
                            ivPlayIcon.setBackgroundResource(R.drawable.cloud_play_start);
                        }
                    });
                    i++;
                }
            }
        },0,1000);
    }

    private void stopPlay(){
        MessageEvent event=new MessageEvent(0XF2,"stop right now");
        EventBus.getDefault().post(event);
        i = 0;
        isPlaying = false;
        progressBar.setProgress(0);
        tvRecTime.setText(Utility.formatTime(0));
        ivPlayIcon.setBackgroundResource(R.drawable.cloud_play_stop);
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /** 发送云呼 **/
    private void sendCloud(){
        tvMore.setEnabled(false);
        if (!Utility.isEmpty(voice_ivid)){
            final List<NumberPhonePair> newList = SendCloudCallBachSignUtility.getExistHaveACellPhoneNumberNewList(adapter.getList());
            if (!Utility.isEmpty(newList) && newList.size()>0) {
                String lastItemBH = SendCloudCallBachSignUtility.getBhFormat(newList.get(newList.size() - 1).getBh());
                if (!Utility.isEmpty(lastItemBH)) {
                    saveNoEntry = SendCloudCallBachSignUtility.getSaveNoEntry(lastItemBH.split(";")[0], Integer.parseInt(lastItemBH.split(";")[1])+1);
                } else {
                    saveNoEntry = SendCloudCallBachSignUtility.getSaveNoEntry("", 1);
                }
            }
            if (Utility.isEmpty(newList) || newList.size() == 0) {
                tvMore.setEnabled(true);
                UtilToolkit.showToast("请输入手机号码");
                return;
            }

            for (int i = 0;i<adapter.getList().size();i++){
                String no = adapter.getList().get(i).getBh();
                String mobile  = adapter.getList().get(i).getPhone();
                mobile = mobile.replaceAll(" ","");
                mobile = mobile.replace("-","");
                if (!Utility.isEmpty(mobile) && (mobile.length()<=11 || mobile.length() >=12) && !StringUtil.judgeStringEveryCharacterIsNumber(mobile)){
                    UtilToolkit.showToast("编号 "+no+" 的手机号码有误~");
                    tvMore.setEnabled(true);
                    return;
                }
            }

            if (SendCloudCallBachSignUtility.existTheSameMobilePhone(adapter.getList())){
                SkuaidiDialog dialog = new SkuaidiDialog(mActivity);
                dialog.setTitle("提示");
                dialog.isUseEditText(false);
                dialog.setContent("编号：" + adapter.getList().get(_no1).getBh() + "  和编号：" + adapter.getList().get(_no2).getBh() + "  的手机号相同，是否返回修改？");
                dialog.setPositionButtonTitle("继续发送");
                dialog.setNegativeButtonTitle("返回修改");

                dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMore.setEnabled(true);
                        sendCall(newList,voice_ivid);
                        showProgressDialog("");//mActivity,"请稍候...");
                    }
                });
                dialog.showDialog();
            }else{
                tvMore.setEnabled(true);
                sendCall(newList,voice_ivid);
                showProgressDialog("");//mActivity,"请稍候...");
            }
        }else{
            UtilToolkit.showToast("请选择语音模板");
            tvMore.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageEvent event = new MessageEvent(0XF3,"stop service");
        EventBus.getDefault().post(event);
        EventBus.getDefault().unregister(this);
        //ButterKnife.(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            stopPlay();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void modidyNo(View view, int position) {
        SendCloudCallBachSignUtility.customNo(mActivity,adapter,position,listData);
    }

    @Override
    public void onTouchOrder(View view, int position) {
//        if (mActivity.getCurrentFocus() != null)
//            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void showOrder(final View view, int position) {
        ShowTextPop showTextPop = new ShowTextPop(mActivity, listData.get(position).getDh());
        showTextPop.showAsDropDown(view, 0, -(view.getHeight()+view.getHeight()/2));
    }

    @Override
    protected void onRequestStatus(boolean requestStatus) {
        tvMore.setEnabled(true);
        dismissProgressDialog();//mActivity);
        if (requestStatus){
            SaveNoDAO.saveNo(saveNoEntry);
            finish();
        }
    }
}
