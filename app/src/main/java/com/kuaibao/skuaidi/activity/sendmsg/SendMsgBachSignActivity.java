package com.kuaibao.skuaidi.activity.sendmsg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.sendmsg.adapter.SendMsgBachSignAdapter;
import com.kuaibao.skuaidi.activity.view.ShowTextPop;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dao.SaveNoDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.ReceiverInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 批量签收发短信
 **/
public class SendMsgBachSignActivity extends SkuaiDiBaseActivity implements View.OnClickListener, SendMsgBachSignAdapter.ButtonClickListener {

    private final int REQUEST_MODEL = 0x1001;// 请求选择模板
    public static final int RESULT_MODEL = 0x1002;// 返回选择的模板
    private final int GET_PHONENUMBER_SUCCESS = 0X1003;// 通过单号获取手机号码返回成功状态

    private TextView hint_addModel;// 选择模板
    private TextView tv_show_model;// 显示模板
    private SkuaidiTextView tv_more;// 发送按钮
    private View line;
    private ListView list;// 列表

    private Activity mContext;
    private ReplyModel modelInfo;
    private SendMsgBachSignAdapter adapter;
    private InputMethodManager imm;
    private SaveNoEntry saveNoEntry;

    private String ident;
    private String modelId;// 模板ID
    private List<NumberPhonePair> listData;// 数据列表

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_PHONENUMBER_SUCCESS:
                    @SuppressWarnings("unchecked")
                    List<ReceiverInfo> receiverInfos = (List<ReceiverInfo>) msg.obj;
                    for (int i = 0; i < receiverInfos.size(); i++) {
                        String rec_mobile = receiverInfos.get(i).getRec_mobile();// 返回的手机号码是连续的
                        autoJudgeMobilePhone(i, rec_mobile);
                    }
                    adapter.setListData(listData);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg_bach_sign);
        mContext = this;

        initView();
        getsetData();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventTest(final MessageEvent msg) {
        switch (msg.type) {
            case 0xEE:
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = Integer.parseInt(msg.message);
                        KLog.i("kb", "当前EditText位置:---->" + position);
                        if (msg.position >= 0) {
                            //得到要更新的item的view
                            setNextEditTextFoucs(position + 1);
                        }
                    }
                });
                break;
        }
    }

    private void setNextEditTextFoucs(int position) {
        if ((position) == listData.size()) {
            return;
        }
        View localView = list.getChildAt(position);
        if (localView != null) {
            if (localView.findViewById(R.id.et_PhoneNo) != null) {
                EditText et_PhoneNo = (EditText) localView.findViewById(R.id.et_PhoneNo);
//                KLog.i("kb","下一位置胡EditText is null ?"+et_PhoneNo==null?"true":"false");
                if (et_PhoneNo.isEnabled()) {
                    et_PhoneNo.requestFocus();
                    et_PhoneNo.setFocusable(true);
                    et_PhoneNo.setSelected(true);
                } else {
                    setNextEditTextFoucs(position + 1);
                }
            } else {
                KLog.i("kb", "findViewById(R.id.iv_play_netcall_audio) is null");
            }
        } else {
            KLog.i("kb", "lv.getChildAt is null");
        }
    }

    private void initView() {
        SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);// 返回按钮
        TextView tv_title_des = (TextView) findViewById(R.id.tv_title_des);// 标题
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);// 发送按钮
        hint_addModel = (TextView) findViewById(R.id.hint_addModel);
        tv_show_model = (TextView) findViewById(R.id.tv_show_model);
        line = findViewById(R.id.line);
        list = (ListView) findViewById(R.id.list);

        tv_title_des.setText("发短信");
        line.setVisibility(View.GONE);
        tv_more.setVisibility(View.VISIBLE);
        tv_more.setText("发送");

        iv_title_back.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        hint_addModel.setOnClickListener(this);
    }

    private void getsetData() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        listData = (List<NumberPhonePair>) getIntent().getSerializableExtra("numberPhonePairs");// 从派件群发短信中获取数据
        setBH();

        // 通过运单号获取手机号码
        // 将运单号拼接起来
        String orderNum = "";
        for (int i = 0; i < listData.size(); i++) {
            orderNum = orderNum + listData.get(i).getDh() + "|";
        }
        if (orderNum.lastIndexOf("|") > 0)
            orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
        getPhoneNumberByOrderNo(orderNum);// 【接口】

        ident = getIntent().getStringExtra("ident");
        if (listData == null) {
            listData = new ArrayList<>();
        }
        adapter = new SendMsgBachSignAdapter(mContext, listData);
        adapter.setButtonClickListener(SendMsgBachSignActivity.this);
        list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MODEL && resultCode == RESULT_MODEL) {
            modelInfo = (ReplyModel) data.getSerializableExtra("modelObject");
            if (null != modelInfo) {
                String modelContent = modelInfo.getModelContent();
                String modelTitle = modelInfo.getTitle();
                if (!Utility.isEmpty(modelContent) && modelContent.contains("#NO#"))
                    modelContent = modelContent.replace("#NO#", "#NON#");
                if (!Utility.isEmpty(modelContent) && modelContent.contains("#DH#"))
                    modelContent = modelContent.replace("#DH#", "#DHDHDHDHDH#");
                if (!Utility.isEmpty(modelContent) && modelContent.contains("#SURL#")) {
                    modelContent = modelContent.replace("#SURL#", "#SURLSURLSURLSURLS#");
                }
                modelId = modelInfo.getTid();
                // 设置模板内容
                line.setVisibility(View.VISIBLE);
                tv_show_model.setVisibility(View.VISIBLE);
                TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(mContext);
                if (!Utility.isEmpty(modelContent) && 0 != modelContent.length()) {
                    tv_show_model.setText(mTextInsertImgParser.replace(modelContent));
                    setModelTitle(modelTitle, true);// 设置模板标题
                } else {
                    setModelTitle(modelTitle, false);// 设置模板标题
                }
            }
        }

    }

    /**
     * setModelTitle:设置短信模板标题
     * 顾冬冬
     */
    private void setModelTitle(String modelTitle, boolean display) {
        if (display) {
            if (!Utility.isEmpty(modelTitle)) {
                hint_addModel.setText(modelTitle);
                hint_addModel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sendmsg_next, 0);
            } else {
                hint_addModel.setText("短信模板");
                hint_addModel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sendmsg_next, 0);
            }
            hint_addModel.setTextColor(Utility.getColor(mContext, R.color.gray_1));
        } else {
            hint_addModel.setText(mContext.getResources().getString(R.string.send_msg_addmodel));// 请选择短信模板
            hint_addModel.setTextColor(Utility.getColor(mContext, R.color.default_green_2));
            hint_addModel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sendmsg_add, 0, R.drawable.sendmsg_next, 0);
        }

    }

    private List<NumberPhonePair> getSendData(List<NumberPhonePair> listData) {
        List<NumberPhonePair> npps = new ArrayList<>();
        for (int i = 0, j = listData.size(); i < j; i++) {
            NumberPhonePair npp = listData.get(i);
            String phone = npp.getPhone();
            if (!Utility.isEmpty(phone) && !phone.contains("*"))
                npps.add(npp);
        }
        return npps;
    }

    /**
     * tid:模板ID
     * ident:派件列表选择单号获取手号后保存在服务器中的缓存ID
     * listData:编号 手机号 单号
     **/
    private void informBydhSend(String tid, String ident, List<NumberPhonePair> listData) {
        showProgressDialog( "请稍候...");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_bydh/send2");
            data.put("ident", ident);
            data.put("tid", tid);
            data.put("dhs", new Gson().toJson(getSendData(listData)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 通过单号自动获取手机号
     **/
    private void getPhoneNumberByOrderNo(String orderNum) {
        showProgressDialog("正在获取手机号码，请稍候...");
        if (TextUtils.isEmpty(orderNum))
            return;
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("sname", "express.contacts");
            jsonData.put("no", orderNum);
            jsonData.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(jsonData, false, INTERFACE_VERSION_NEW);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        dismissProgressDialog();
        tv_more.setEnabled(true);
        Message hMsg = null;
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ("inform_bydh/send2".equals(sname)) {
            SaveNoDAO.saveNo(saveNoEntry);
            finish();
        } else if (!Utility.isEmpty(sname) && "express.contacts".equals(sname)) {// 调用接口获取收件人信息
            List<ReceiverInfo> receiverInfo = JsonXmlParser.parseReceiverInfo(result);
            hMsg = new Message();
            hMsg.what = GET_PHONENUMBER_SUCCESS;
            hMsg.obj = receiverInfo;
        }
        if (null != mHandler && null != msg) {
            mHandler.sendMessage(hMsg);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        tv_more.setEnabled(true);
        if ("inform_bydh/send2".equals(sname)) {
            switch (code) {
                case "10000":
                    if (!Utility.isEmpty(result))
                        UtilToolkit.showToast(result);
                    break;
                case "10002":
                    SkuaidiDialog skuaidiDialog = new SkuaidiDialog(mContext);
                    skuaidiDialog.setTitle("余额不足");
                    skuaidiDialog.setContent(result);
                    skuaidiDialog.isUseEditText(false);
                    skuaidiDialog.setPositionButtonTitle("充值");
                    skuaidiDialog.setNegativeButtonTitle("取消");
                    skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent mIntent = new Intent(mContext, TopUpActivity.class);
                            startActivity(mIntent);
                        }
                    });
                    skuaidiDialog.showDialog();
                    break;
            }
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        tv_more.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回
                finish();
                break;
            case R.id.hint_addModel:// 选择模板
                mIntent = new Intent(mContext, ModelActivity.class);
                mIntent.putExtra("from_activity", "backSignSendMsg");// 批量签收界面选择模板
                startActivityForResult(mIntent, REQUEST_MODEL);
                break;
            case R.id.tv_more:// 发送
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("网络环境不可用");
                    return;
                }
                sendMsg(v);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void modidyNo(View view, int position) {
        customNo(view, position, listData);
    }

    @Override
    public void showOrder(View view, int position) {
        ShowTextPop showTextPop = new ShowTextPop(mContext, listData.get(position).getDh());
        showTextPop.showAsDropDown(view, 0, -(view.getHeight() + view.getHeight() / 2));
    }

    /**
     * 自定义编号
     */
    private void customNo(View v, final int position, final List<NumberPhonePair> listData) {
        UMShareManager.onEvent(mContext, "SendMSG_CustomNo", "SendMSG", "发短信:自定义编号");
        final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
        dialog.setTitle("设置起始编号");
        dialog.isUseEditText(true);
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.showEditTextTermsArea(true);
        dialog.setEditTextContent(5);
        dialog.setSendSmsNoTerms(true);
        dialog.setDonotAutoDismiss(true);
        dialog.setEditText(listData.get(position).getBh());
        dialog.setEditTextHint("最大99999，前两位支持输入字母");
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                if (!Utility.isEmpty(dialog.getEditTextContent())) {
                    if (!dialog.getEditTextContent().trim().isEmpty()) {
                        if (dialog.isInputContentFail()) {// 如果输入内容正确
                            String cusNumber = dialog.getEditTextContent();
                            if (dialog.isSelectEditTextTermsArea()) {// 只修改当前编号
                                listData.get(position).setBh(cusNumber);
                                adapter.notifyDataSetChanged();
                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                dialog.dismiss();
                            } else {// 修改以下所以编号

                                String firstWord = cusNumber.substring(0, 1);
                                Pattern p = Pattern.compile("[a-zA-Z]");
                                Matcher m, m2, m3;
                                if (cusNumber.length() >= 2) {// 编号长度>=2
                                    String secondWord = cusNumber.substring(1, 2);
                                    m = p.matcher(firstWord);
                                    m2 = p.matcher(secondWord);
                                    if (m.matches() && m2.matches()) {// 前面两个字符都是字母【当前两位为字母时】
                                        if (cusNumber.length() > 2) {// 编号长度>2
                                            try {
                                                int customNo = Integer.parseInt(cusNumber.substring(2));// 将输入编号转为整型
                                                if (customNo <= 999) {
                                                    for (int i = position, j = listData.size(); i < j; i++) {
                                                        NumberPhonePair npp = listData.get(i);
                                                        if (customNo > 999) {
                                                            customNo = 1;
                                                        }
                                                        npp.setBh(cusNumber.substring(0, 2) + customNo);
                                                        customNo++;
                                                    }
                                                    adapter.notifyDataSetChanged();
//                                                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                    // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                    imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                                } else {
                                                    UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            dialog.dismiss();
                                        } else {// 编号长度==2&编号全为字母
                                            UtilToolkit.showToast("字母后面需输入数字");
                                        }
                                    } else if (m.matches()) {// 只有第一个字符是字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber.substring(1));// 将输入编号转为整型
                                            if (customNo <= 9999) {
                                                for (int i = position, j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 9999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(cusNumber.substring(0, 1) + customNo);
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    } else {// 没有字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position, j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                } else {// 编号长度<2
                                    m3 = p.matcher(firstWord);
                                    if (m3.matches()) {// 编号长度==1&为字母
                                        UtilToolkit.showToast("字母后面需输入数字");
                                    } else {// 编号长度==1&为数字
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position, j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                }
                            }

                        } else {
                            UtilToolkit.showToast("起始编号格式有误，请重新输入");
                        }
                    }
                }
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
            }
        });
        dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {

            @Override
            public void onClick() {
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                dialog.dismiss();
            }
        });
        dialog.showDialog();
        Utility.showKeyBoard((EditText) dialog.getEditTextView(), true);
    }

    private void setBH() {
        String lastNoHead = "";// 最后一个编号的前两个字符
        int lastNo = 1;// 最后一个编号【数字】
        saveNoEntry = SaveNoDAO.getSaveNo(SaveNoDAO.NO_SMS_BACHSIGN);
        if (saveNoEntry != null) {
            lastNoHead = saveNoEntry.getSave_letter();// 获取保存的字母
            lastNo = saveNoEntry.getSave_number();// 获取保存的数字
            if (Utility.isEmpty(lastNo)) {
                lastNo = 1;
            }

            String save_time = UtilityTime.getDateTimeByMillisecond(saveNoEntry.getSaveTime() / 1000, UtilityTime.YYYY_MM_DD);
            String cur_time = UtilityTime.getDateTimeByMillisecond(System.currentTimeMillis() / 1000, UtilityTime.YYYY_MM_DD);
            String save_time_day, cur_time_day;

            if (!Utility.isEmpty(save_time)) {
                save_time_day = save_time.substring(save_time.length() - 2, save_time.length());
                cur_time_day = cur_time.substring(cur_time.length() - 2, cur_time.length());
                if (!save_time_day.equals(cur_time_day)) {
                    lastNo = 1;
                }
            }
        }

        for (int i = 0; i < listData.size(); i++) {
            if (lastNo > 99999) {
                lastNo = 1;
            }
            listData.get(i).setBh(lastNoHead + lastNo);
            lastNo++;
        }
    }

    /**
     * 获取保存编号
     **/
    private SaveNoEntry getSaveNoEntry(String save_letter, int save_number) {
        SaveNoEntry saveNoEntry = new SaveNoEntry();
        saveNoEntry.setSave_from(SaveNoDAO.NO_SMS_BACHSIGN);
        saveNoEntry.setSaveTime(System.currentTimeMillis());
        saveNoEntry.setSave_userPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        saveNoEntry.setSave_letter(save_letter);
        saveNoEntry.setSave_number(save_number);
        return saveNoEntry;
    }

    /**
     * 发送短信
     */
    private void sendMsg(View v) {
        if (modelInfo == null) {
            UtilToolkit.showToast("请选择发送模板");
            return;
        }
        if (null != listData && 0 != listData.size() && checkAtLeastOnePhone(listData)) {
            String lastNumber = listData.get(listData.size() - 1).getBh();

            String firstChar = lastNumber.substring(0, 1);// 编号第一个字符
            String regularExpression = "[a-zA-Z]";
            Pattern p = Pattern.compile(regularExpression);
            Matcher m, m2;
            m = p.matcher(firstChar);
            if (lastNumber.length() > 2) {
                String secondChar = lastNumber.substring(1, 2);// 编号第二个字符
                m2 = p.matcher(secondChar);
                if (m.matches() && m2.matches()) {// 前两个字符都是字母
                    saveNoEntry = getSaveNoEntry(firstChar + secondChar, Integer.parseInt(lastNumber.substring(2)) + 1);
                } else if (m.matches() && !m2.matches()) {// 只有第一个字符是字母
                    saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)) + 1);
                } else if (!m.matches() && !m2.matches()) {// 全部是数字
                    saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                }
            } else if (lastNumber.length() == 2) {
                String secondChar = lastNumber.substring(1, 2);// 编号第二个字符
                m2 = p.matcher(secondChar);
                if (m.matches() && m2.matches()) {
                    saveNoEntry = getSaveNoEntry(firstChar + secondChar, 1);
                } else if (m.matches() && !m2.matches()) {
                    saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)) + 1);
                } else if (!m.matches() && !m2.matches()) {
                    saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                }
            } else {
                // 只有一个字符并且是字母
                if (m.matches()) {
                    saveNoEntry = getSaveNoEntry(firstChar, 1);
                } else {// 只有一个字符并且是数字
                    saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                }
            }
            for (int i = 0, j = listData.size(); i < j; i++) {
                String no = listData.get(i).getBh();
                String mobile = listData.get(i).getPhone();
                if (!Utility.isEmpty(mobile)) {
                    mobile = mobile.replace(" ", "");
                    mobile = mobile.replace("-", "");
                    listData.get(i).setPhone(mobile);
                    // 判断是否输入有手机号码
                    if ((!mobile.substring(0, 1).equals("1") || !StringUtil.judgeStringEveryCharacterIsNumber(mobile) || mobile.length() != 11) && !mobile.contains("*")) {// 15202131503
                        tv_more.setEnabled(true);
                        UtilToolkit.showToast("编号为" + no + "的手机号有误");
                        return;
                    }
                }
            }
            if (checkWhetherNoInput(listData)){
                tv_more.setEnabled(true);
                SkuaidiDialog dialog = new SkuaidiDialog(mContext);
                dialog.setTitle("温馨提示");
                dialog.setContent("部分手机号\n未填写，是否继续发送？");
                dialog.isUseEditText(false);
                dialog.setPositionButtonTitle("继续发送");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener()  {// 继续发送

                    @Override
                    public void onClick(View v) {
                        informBydhSend(modelId, ident, listData);
                        tv_more.setEnabled(false);
                    }
                });
                dialog.showDialog();
            }else{
                informBydhSend(modelId, ident, listData);
                tv_more.setEnabled(false);
            }

        } else {
            UtilToolkit.showToast("请输入要发送的手机号");
            tv_more.setEnabled(true);
        }
    }

    /**检查至少输入一条手机号码
     * 至少输入一条手机号码：return true;
     * 完全没有一条手机号码：return false;**/
    private boolean checkAtLeastOnePhone(List<NumberPhonePair> list){
        boolean isTag = false;
        for (NumberPhonePair npp : list){
            if (!Utility.isEmpty(npp.getPhone())){
                isTag = true;
                break;
            }
        }
        return isTag;
    }

    /**检查是否有单号没有输入手机号
     * 至少有一条未输入手机号码：return true;
     * 全部单号都对应输入手机号码：return false;**/
    private boolean checkWhetherNoInput(List<NumberPhonePair> list){
        for (NumberPhonePair npp : list){
            if (Utility.isEmpty(npp.getPhone())){
                return true;
            }
        }
        return false;
    }


    private int i = 0;
    private int j = 0;

    // 获取列表中是否在在相同的手机号
    private boolean getExistTheSameMobilePhone(List<NumberPhonePair> list) {
        boolean exist = false;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (!list.get(i).getPhone().contains("*") && !Utility.isEmpty(list.get(i).getPhone()) && list.get(i).getPhone().equals(list.get(j).getPhone())) {
                    exist = true;
                    this.i = i;
                    this.j = j;
                    break;
                }
            }
            if (exist) {
                break;
            }
        }
        return exist;
    }

    /**
     * 自动判断手机号码是否符合规则，自动去掉不必要的标识字符
     **/
    private void autoJudgeMobilePhone(int i, String mobilePhone) {
        listData.get(i).setPhone(SendMSGActivity.formatPhoneNumber(mobilePhone));
    }
}
