package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.HistoryCloudCallDetailAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecord;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecordDetail;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.service.DownloadTask;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryCloudCallDetailActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.pull)
    PullToRefreshView pull;
    @BindView(R.id.listView)
    ListView listView;

    private HistoryRecord.ListBean hListBean;
    private HistoryCloudCallDetailAdapter adapter;
    private int pageNum = 1;
    private int totalNum = 1;
    private View listHeaderView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_cloud_call_detail);
        listHeaderView = LayoutInflater.from(this).inflate(R.layout.listitem_recorddetail_first, (ViewGroup) findViewById(R.id.content), false);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getData();
        initView();
        initListener();
    }

    private void getData() {
        hListBean = (HistoryRecord.ListBean) getIntent().getSerializableExtra("listBean");
        historyGetDetail(1);
    }

    private void initView() {
        tvTitleDes.setText("编号:" + hListBean.getBh());
        List<HistoryRecordDetail.ListBean> listBean = new ArrayList<>();

        adapter = new HistoryCloudCallDetailAdapter(this,listBean);

        TextView tvFlowExpressNo = (TextView) listHeaderView.findViewById(R.id.tvFlowExpressNo);
        RelativeLayout llPhone = (RelativeLayout) listHeaderView.findViewById(R.id.llPhone);
        tvFlowExpressNo.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        TextView tvSendPhone = (TextView) listHeaderView.findViewById(R.id.tvSendPhone);
        TextView tvExpressNo = (TextView) listHeaderView.findViewById(R.id.tvExpressNo);
        RelativeLayout rlExpressNo = (RelativeLayout) listHeaderView.findViewById(R.id.rlExpressNo);
        View line = listHeaderView.findViewById(R.id.line);
        tvSendPhone.setText(Utility.isEmpty(hListBean.getCall_number())?"1**********":hListBean.getCall_number());
        if (!Utility.isEmpty(hListBean.getDh()) && hListBean.getDh().length()>8) {
            tvExpressNo.setText(Utility.isEmpty(hListBean.getDh()) ? "" : hListBean.getDh());
            rlExpressNo.setVisibility(Utility.isEmpty(hListBean.getDh()) ? View.GONE : View.VISIBLE);
            line.setVisibility(Utility.isEmpty(hListBean.getDh()) ? View.GONE : View.VISIBLE);
        }
        listView.addHeaderView(listHeaderView);
        listView.setAdapter(adapter);
    }

    private void initListener(){
        pull.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                pageNum = 1;
                historyGetDetail(1);
                KLog.d("kb","totalNum is  --> "+totalNum+"  pageNum is --> "+pageNum);
            }
        });

        pull.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                historyGetDetail(pageNum);
                KLog.d("kb","totalNum is  --> "+totalNum+"  pageNum is --> "+pageNum);
            }
        });

        // 播放语音
        adapter.setPlayVoice(new HistoryCloudCallDetailAdapter.PlayVoice() {
            @Override
            public void playVoice(HistoryRecordDetail.ListBean listBean) {
                String speaker_role = listBean.getSpeaker_role();
                if (!Utility.isEmpty(speaker_role)){
                    if (speaker_role.equals("counterman")){
                        switch (listBean.getContent_type()){
                            case "3":
                                if (isPlaying){
                                    DownloadTask.stopPlayLocalVoice();
                                    isPlaying = false;
                                }else{
                                    isPlaying = true;
                                    DownloadTask.playLocalVoice(listBean.getContent());
                                }
                                break;
                            case "6":
                                if (isPlaying){
                                    DownloadTask.stopPlayLocalVoice();
                                    isPlaying = false;
                                }else{
                                    String path = "";
                                    try {
                                        JSONObject content = new JSONObject(listBean.getContent());
                                        path = content.optString("path");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (!Utility.isEmpty(path)) {
                                        isPlaying = true;
                                        String voiceName = path.substring(path.lastIndexOf("/") + 1);
                                        DownloadTask.playLocalVoice(voiceName);
                                    }else{
                                        isPlaying = false;
                                        UtilToolkit.showToast("播放失败");
                                    }
                                }
                                break;
                        }
                    }else{// user|shop
                        if (listBean.getContent_type().equals("7")){
                            if (isPlaying){
                                DownloadTask.stopPlayLocalVoice();
                                isPlaying = false;
                            }else {
                                if (!Utility.isEmpty(listBean.getContent())) {
                                    String voiceName = listBean.getContent().substring(listBean.getContent().lastIndexOf("/") + 1);// 获取文件名称
                                    DownloadTask.playLocalVoice(voiceName);
                                    isPlaying = true;
                                }
                            }
                        }else{
                            if (isPlaying){
                                DownloadTask.stopPlayLocalVoice();
                                isPlaying = false;
                            }else {
                                DownloadTask.playLocalVoice(listBean.getContent());
                            }
                        }
                    }
                }
            }
        });
    }

    @Subscribe
    public void onReceiveEvent(MessageEvent msg){
        switch (msg.type){
            case 0X01:// play start
                isPlaying = true;
                break;
            case 0X02:// 播放結束
                isPlaying = false;
                break;
            case 0XE5:// play error
                isPlaying = false;
                break;
            default:
                isPlaying = false;
                break;
        }
    }

    private void historyGetDetail(int page_num) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "history/getDetail");
            data.put("topic_id", hListBean.getTopic_id());
            data.put("page_num", page_num);
            data.put("page_size", Constants.PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }


    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        pull.onFooterRefreshComplete();
        pull.onHeaderRefreshComplete();

        HistoryRecordDetail _HRDetail = JSON.parseObject(result, HistoryRecordDetail.class);
        totalNum = _HRDetail.getPage_count();
        if (pageNum == 1){
            adapter.clear();
        }
        if (pageNum <= totalNum)
            pageNum++;
        adapter.addEntity(_HRDetail.getList());
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        pull.onFooterRefreshComplete();
        pull.onHeaderRefreshComplete();

        if (!Utility.isEmpty(result)){
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvFlowExpressNo:// 运单跟踪按钮
                Intent intent = new Intent();
                intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
                intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
                intent.putExtra("order_number", hListBean.getDh());
                intent.setClass(this, CopyOfFindExpressResultActivity.class);
                startActivity(intent);
                break;
            case R.id.llPhone:// 拨打电话
                if (!Utility.isEmpty(hListBean.getCall_number()) && !hListBean.getCall_number().contains("*")){
                    AcitivityTransUtil.showChooseTeleTypeDialog(this, "", hListBean.getCall_number(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
                }else{
                    UtilToolkit.showToast("电话拨打失败");
                }
                break;
        }
    }
}
