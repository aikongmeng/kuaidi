package com.kuaibao.skuaidi.activity.scan_mobile;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.scan_mobile.adapter.GunScanAdapter;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.litepal.entry.Number;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 扫描枪输入单号界面
 * Created by gdd
 * on 2016/9/20.
 */
public class GunScanActivity extends RxRetrofitBaseActivity implements TextWatcher,GunScanAdapter.DeleteOrderCallBack {

    @BindView(R.id.tv_more) TextView tvMore;
    @BindView(R.id.tv_title_des) TextView tvTitleDes;
    @BindView(R.id.et_scan_no) EditText etScanNo;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private int position = -1;
    private List<NotifyInfo> list;
    private Map<String,Boolean> wallBillCash = new HashMap<>();
    private List<String> no;
    private GunScanAdapter adapter;
    private int maxCount;// 最大输入数

    private Ringtone ringtone;// 拦截件语音提示
    private Ringtone repeatedRingtone;// 拦截件语音提示


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gun_scan);

        etScanNo.addTextChangedListener(this);

        getControl();
        getData();

        etScanNo.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etScanNo.getWindowToken(), 0); // 强制隐藏键盘
            }
        },500);
    }

    private void getControl() {
        initBeepSound();
        initRepeatedTone();

        tvTitleDes.setText("扫描枪输入");
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText("完成");
    }

    private void getData() {

        maxCount = getIntent().getIntExtra("inputMaxCount",0);
        position = getIntent().getIntExtra("listposition",-1);
        list = (List<NotifyInfo>) getIntent().getSerializableExtra("list");
        no = (List<String>) getIntent().getSerializableExtra("no");

        for (NotifyInfo info : list){
            wallBillCash.put(info.getExpress_number(),true);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(this,R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线

        adapter = new GunScanAdapter(position,list,no);
        adapter.setDeleteOrder(this);
        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.iv_title_back,R.id.tv_more})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:// 返回按钮
                goBackActivity();
                break;
            case R.id.tv_more:// 完成按钮
                MessageEvent messageEvent;
                if (!Utility.isEmpty(getIntent().getStringExtra("from")) && "yunhu".equals(getIntent().getStringExtra("from"))) {
                    messageEvent = new MessageEvent(0xa1003, "");
                }else{
                    messageEvent = new MessageEvent(0xa1002,"");
                }
                Intent intent = new Intent();
                intent.putExtra("list", (Serializable) list);
                messageEvent.putIntent(intent);
                EventBus.getDefault().post(messageEvent);
                DataSupport.deleteAll(Number.class);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){// 返回按钮
            goBackActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 扫单提示铃声初始化
     */
    private void initBeepSound() {
        Uri soundURI = UtilToolkit.getResourceUri(this.getApplicationContext(), R.raw.beep);
        ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), soundURI);
        if (ringtone != null) {
            ringtone.setStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    /**
     * 播放扫单提示音
     */
    private void playBeepTone() {
        if (ringtone != null)
            ringtone.play();
    }

    /**
     * 扫描单号重复提示铃声初始化
     */
    private void initRepeatedTone() {
        Uri soundURI = UtilToolkit.getResourceUri(this.getApplicationContext(), R.raw.scan_required);
        repeatedRingtone = RingtoneManager.getRingtone(this.getApplicationContext(), soundURI);
        if (repeatedRingtone != null)
            repeatedRingtone.setStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 扫描单号重复提示铃声播放
     */
    private void playRepeatedTone() {
        if (repeatedRingtone != null)
            repeatedRingtone.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
        if (repeatedRingtone != null && repeatedRingtone.isPlaying()){
            repeatedRingtone.stop();
            repeatedRingtone = null;
        }
    }

    private void goBackActivity(){
        if (null != list && list.size() != 0){
            SkuaidiDialog dialog = new SkuaidiDialog(this);
            dialog.setTitle("放弃扫描");
            dialog.setContent("你将放弃已经扫描的单号!\n确认放弃？");
            dialog.isUseEditText(false);
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    DataSupport.deleteAll(Number.class);
                }
            });
            dialog.showDialog();
        }else{
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0)
            return;
        if (s.charAt(s.length() - 1) == '\n' && s.length() > 7) {
            if (maxCount != 0 && list.size() < maxCount) {
                String number = s.subSequence(0, s.length() - 1).toString();
                if (!E3SysManager.isValidWaybillNo(number)) {
                    UtilToolkit.showToast("非"+ E3SysManager.brandMap.get(SkuaidiSpf.getLoginUser().getExpressNo())+"单号！");
                    playRepeatedTone();
                    s.clear();
                    return;
                }

                if (wallBillCash.get(number) == null) {
                    wallBillCash.put(number, true);
                    // 保存数据库用作备份
                    Number number1 = new Number();
                    number1.setOrderNo(number);
                    number1.saveThrows();

                    NotifyInfo2 info = new NotifyInfo2();
                    info.setExpress_number(number);
                    list.add(info);
                    playBeepTone();
//                    adapter.notifyDataSetChangedByPosition(no);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size() - 1);
                } else {
                    UtilToolkit.showToast("单号重复！");
                    playRepeatedTone();
                }
            }
            s.clear();
        }
    }

    @Override// 删除单号
    public void deleteOrderSuccess(NotifyInfo notifyInfo,int index) {
        list.remove(index);
        DataSupport.deleteAll(Number.class,"orderNo=?",notifyInfo.getExpress_number());
//        DataSupport.delete(Number.class,index);
        adapter.notifyDataSetChanged();
    }
}
