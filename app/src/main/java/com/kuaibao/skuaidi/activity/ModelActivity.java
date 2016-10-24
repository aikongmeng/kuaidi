package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ModelAdapter;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMsgBachSignActivity;
import com.kuaibao.skuaidi.activity.smsrecord.RecordSendSmsSelectActivity;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddModelActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.json.entry.SendRecordSelectSmsOrCloudParameter;
import com.kuaibao.skuaidi.json.entry.SendRecordSelectVoiceParam;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 顾冬冬模板选择
 */
public class ModelActivity extends SkuaiDiBaseActivity {
    private final String BUTTON_LEFT = "button_left";
    private final String BUTTON_MIDDLE = "button_middle";
    private final String BUTTON_RIGHT = "button_right";

    private Context context;
    private ListView lv_model;
    private ModelAdapter adapter;
    private static List<ReplyModel> models = new ArrayList<>();
    private SkuaidiTextView tv_more;// 排序按钮
    private static SkuaidiDB skuaidiDb;
    private Intent mIntent;
    private String from_activity;

    private String modelTid = "";// 模板对应服务器上的id
    private TextView button_left, button_middle, button_right;
    private String selectState = "approved";
    private static String template_type;// 模板类型，"sms",短信模板；"liuyan",客户消息模板

    private List<SmsRecord> smsRecords;
    private List<CloudVoiceRecordEntry> cvres;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if ("sms".equals(template_type) || "resend_smsORcloud".equals(from_activity) || "cloudCall_resend_smsORcloud".equals(from_activity)) {
                        if (selectState.equals("approved")) {
                            adapter.setList(getDragModels());
                        } else if (selectState.equals("apply")) {
                            adapter.setList(skuaidiDb.getPaijianModels("apply", "indeterminate"));
                        } else if (selectState.equals("reject")) {
                            adapter.setList(getModels(skuaidiDb.getPaijianModels("reject")));
                        }
                    } else if ("liuyan".equals(template_type)) {
                        adapter.setList(getModels(skuaidiDb.getPaijianModels(1, "approved")));
                        View layout_no_mode = findViewById(R.id.layout_no_mode);// 客户消息模板，没有模板是的提示
                        if (adapter.getCount() == 0) {// 客户消息模板，没有模板显示提示，添加模板
                            layout_no_mode.setVisibility(View.VISIBLE);
                        } else {
                            layout_no_mode.setVisibility(View.GONE);
                        }
                    }
                    MessageEvent messageEvent = new MessageEvent(0Xa1001,"");
                    EventBus.getDefault().post(messageEvent);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.list_models);
        context = this;
        skuaidiDb = SkuaidiDB.getInstanse(context);
        template_type = getIntent().getStringExtra("template_type");
        if (TextUtils.isEmpty(template_type)) {
            template_type = "sms";
        }
        getControl();
        initModels();
        setListener();
    }

    private void initModels() {
        models.clear();
        models = getDragModels();
        // 短信模板适配器
        adapter = new ModelAdapter(context, skuaidiDb, models, new ModelAdapter.ButtonClickListener() {
            @Override
            public void onDelete(View v, int position, String locModelId, String serverModelId) {// 删除模板

                if (getModels(skuaidiDb.getPaijianModels()).size() > 2) {
                    deleteModel(serverModelId);// 删除模板
                    modelTid = serverModelId;
                } else {
                    UtilToolkit.showToast("请至少保留两个模板");
                }
            }

            @Override
            public void onModify(View v, int position, String locModelId, String serverModelId) {// 修改模板
                modelTid = serverModelId;
                Intent intent = new Intent(context, AddModelActivity.class);
                intent.putExtra("modelid", locModelId);
                intent.putExtra("modelTid", serverModelId);
                intent.putExtra("operatetype", "edit");
                intent.putExtra("template_type", template_type);
                startActivityForResult(intent, Constants.REQUEST_ADD_MODEL);
            }
        });
        lv_model.setAdapter(adapter);
        getModelsList();
    }

    /**
     * 获取审核通过的排过序的模板列表
     **/
    public static List<ReplyModel> getDragModels() {

        models.clear();
        if ("liuyan".equals(template_type)) {
            models = getModels(skuaidiDb.getPaijianModels(1, "approved"));

            boolean haveSelect = false;
            if (models != null && models.size() != 0) {
                for (int i = 0; i < models.size(); i++) {
                    if (null != models.get(i) && models.get(i).isLy_select_status()) {
                        haveSelect = true;
                        break;
                    }
                } // 如果没有选中模板
                if (!haveSelect) {
                    // 设置审核通过的排序好的第一条模板为选中状态 ，并设置对应的数据库中的模板为选中状态
                    for (int i = 0; i < models.size(); i++) {
                        if (null != models.get(i) && "approved".equals(models.get(i).getState())) {
                            models.get(i).setLy_select_status(true);
                            skuaidiDb.setIsChooseTemplate_ly(models.get(i).getTid(), 1);
                            break;
                        } else {
                            models.get(i).setLy_select_status(false);
                            skuaidiDb.setIsChooseTemplate_ly(models.get(i).getTid(), 0);
                        }
                    }
                }
            }

            return models;
        }
        List<ReplyModel> replyModels = getModels(skuaidiDb.getPaijianModels("approved"));
        // step1:将集合中每个对象中的sort_no这个参数中的值拿出来
        String sort_no = "";
        String sort_no_null = "";
        for (int i = 0; i < replyModels.size(); i++) {
            if (!Utility.isEmpty(replyModels.get(i).getSortNo())) {
                if (!Utility.isEmpty(sort_no)) {
                    sort_no = sort_no + "," + replyModels.get(i).getSortNo();
                } else {
                    sort_no = replyModels.get(i).getSortNo();
                }
            } else {
                if (!Utility.isEmpty(sort_no_null)) {
                    sort_no_null = sort_no_null + "," + replyModels.get(i).getTid();
                } else {
                    sort_no_null = replyModels.get(i).getTid();
                }
            }
        }
        String[] sort_noArr = null;// 保存有排序编号的对象的排序号
        String[] sort_noTid = null;// 保存没有排序编号的对象中的TID【TID：模板ID】
        if (!Utility.isEmpty(sort_no)) {
            sort_noArr = sort_no.split(",");
        }
        if (!Utility.isEmpty(sort_no_null)) {
            sort_noTid = sort_no_null.split(",");
        }

        if (sort_noTid != null) {
            for (String sort_notid : sort_noTid) {
                models.add(skuaidiDb.getPaijianModel("tid", "approved", sort_notid));
            }
        }

        if (sort_noArr != null) {
            int[] sort_noIntArr = new int[sort_noArr.length];// 将排序编号转换成整型重新放到数组中
            for (int i = 0; i < sort_noArr.length; i++) {
                sort_noIntArr[i] = Integer.parseInt(sort_noArr[i]);
            }
            Arrays.sort(sort_noIntArr);// 重新将编号按照升序排序
            for (int noIntArr : sort_noIntArr) {
                models.add(skuaidiDb.getPaijianModel("sort_no", "approved", noIntArr + ""));// 将重新排好序的对象重新放进列表中
            }
        }

        boolean haveSelect = false;
        replyModels = getModels(skuaidiDb.getPaijianModels());
        if (replyModels != null && replyModels.size() != 0) {
            for (int i = 0; i < replyModels.size(); i++) {
                if (null != replyModels.get(i) && replyModels.get(i).isChoose()) {
                    haveSelect = true;
                    break;
                }
            }
            // 如果没有选中模板
            if (!haveSelect) {
                // 设置审核通过的排序好的第一条模板为选中状态 ，并设置对应的数据库中的模板为选中状态
                for (int i = 0; i < models.size(); i++) {
                    if (null != models.get(i)) {
                        models.get(i).setChoose(true);
                        skuaidiDb.setIsChoose(models.get(i).getId());
                        break;
                    }
                }
            }
        }
        return models;
    }

    /**
     * 将从数据库中拿出来的模板添加到新的变量中
     **/
    public static List<ReplyModel> getModels(Map<String, ReplyModel> models_m) {
        List<ReplyModel> models = new ArrayList<>();
        Set<Map.Entry<String, ReplyModel>> setMap = models_m.entrySet();
        Iterator<Map.Entry<String, ReplyModel>> iterator = setMap.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ReplyModel> mEntry = iterator.next();
            models.add(mEntry.getValue());
        }
        return models;
    }

    private void getControl() {

        LinearLayout btn_add_model = (LinearLayout) findViewById(R.id.btn_add_model);// 添加模板按钮
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
        button_left = (TextView) findViewById(R.id.button_left);
        button_middle = (TextView) findViewById(R.id.button_middle);
        button_right = (TextView) findViewById(R.id.button_right);
        TextView tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        TextView tv_addModel = (TextView) findViewById(R.id.tv_addModel);// 添加模板按钮中的文字
        tv_more.setVisibility(View.VISIBLE);
        tv_more.setText("排序");
        button_left.setText("已审核");
        button_middle.setText("待审核");
        button_right.setText("未通过");
        changeButtonBG(button_left, BUTTON_LEFT);

        from_activity = getIntent().getStringExtra("from_activity");
        btn_add_model.setOnClickListener(new MyOnclickListener());
        tv_more.setOnClickListener(new MyOnclickListener());
        button_left.setOnClickListener(new MyOnclickListener());
        button_middle.setOnClickListener(new MyOnclickListener());
        button_right.setOnClickListener(new MyOnclickListener());

        lv_model = (ListView) findViewById(R.id.lv_model);// listview

        if ("liuyan".equals(template_type)) {
            tv_addModel.setText("新增消息模板");
            tv_more.setVisibility(View.GONE);
        }
        if ("resend_smsORcloud".equals(from_activity) || "cloudCall_resend_smsORcloud".equals(from_activity)) {
            findViewById(R.id.ll_state).setVisibility(View.GONE);
            tv_title_des.setVisibility(View.VISIBLE);
            tv_title_des.setText("重发短信");
            tv_more.setVisibility(View.VISIBLE);
            tv_more.setText("发送");
            if ("resend_smsORcloud".equals(from_activity))
                smsRecords = (List<SmsRecord>) getIntent().getSerializableExtra("smsRecords");
            else
                cvres = (List<CloudVoiceRecordEntry>) getIntent().getSerializableExtra("smsRecords");
        }
    }

    private void setListener() {
        lv_model.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position) != null && "reject".equals(adapter.getItem(position).getState())) {// 被拒绝
                    UtilToolkit.showToast("该条为被拒绝模板，请重新编辑");
                } else if (!Utility.isEmpty(from_activity)
                        && ((from_activity.equals("select_mode") || (from_activity.equals("selectTimingModel") || from_activity.equals("draftBox") || from_activity.equals("resend_smsORcloud")||"cloudCall_resend_smsORcloud".equals(from_activity)))
                        && (adapter.getItem(position).getState().equals("apply") || adapter
                        .getItem(position).getState().equals("indeterminate")))) {
                    UtilToolkit.showToast("该条模板正在审核中，请重新选择");
                } else {
                    // 如果是【谁收藏了我-消息群发功能】选择模板
                    if (!Utility.isEmpty(template_type) && "liuyan".equals(template_type)) {
                        if (adapter.getItem(position).getState().equals("indeterminate") || adapter.getItem(position).getState().equals("apply")) {
                            UtilToolkit.showToast("该条模板正在审核中，请重新选择");
                            return;
                        }
                        skuaidiDb.clearSelectModel(1);// 1:留言模板
                        skuaidiDb.setIsChooseTemplate_ly(adapter.getItem(position).getTid(), 1);
                        mIntent = new Intent();
                        mIntent.putExtra("modelObject", adapter.getItem(position));
                        setResult(SendBulkSMSActivity.RESULT_SELECT_MODEL, mIntent);
                        finish();
                        return;
                    }



                    skuaidiDb.clearChooseModel(Constants.TYPE_REPLY_MODEL_SIGN);
                    skuaidiDb.setIsChoose(adapter.getItem(position).getId());
                    skuaidiDb.updateModel(adapter.getItem(position).getId(),
                            skuaidiDb.getModelContent(adapter.getItem(position).getId()), Constants.TYPE_REPLY_MODEL_SIGN,
                            adapter.getItem(position));
                    SkuaidiSpf.removeContent(context);

                    // 如果是【批量签收发送短信】选择模板
                    if (!Utility.isEmpty(from_activity) && "backSignSendMsg".equals(from_activity)){
                        if (adapter.getItem(position).getState().equals("indeterminate") || adapter.getItem(position).getState().equals("apply")) {
                            UtilToolkit.showToast("该条模板正在审核中，请重新选择");
                            return;
                        }
                        mIntent = new Intent();
                        mIntent.putExtra("modelObject", adapter.getItem(position));
                        setResult(SendMsgBachSignActivity.RESULT_MODEL, mIntent);
                        finish();
                        return;
                    }
                    if (null != from_activity
                            && (from_activity.equals("select_mode") || from_activity.equals("selectTimingModel"))) {
                        setResult(Constants.SELECT_MODEL_SUCCESS);
                        finish();
                    } else if (null != from_activity && (from_activity.equals("sendMore") || from_activity.equals("draftBox"))) {
                        setResult(Constants.SELECT_SEND_MORE);
                        finish();
                    } else if (!Utility.isEmpty(from_activity) && "autoSendMsg".equals(from_activity)) {
                        if (!adapter.getItem(position).getState().equals("approved")) {
                            UtilToolkit.showToast("请选择已经审核通过的模板");
                        } else {
                            mIntent = new Intent();
                            mIntent.putExtra("isChoose", true);
                            setResult(SendYunHuActivity.RESULT_AUTO_SEND_MSG, mIntent);
                            finish();
                        }
                    } else if ("dispatch".equals(from_activity)) {
                        mIntent = new Intent();
                        mIntent.putExtra("modelObject", adapter.getItem(position));
                        setResult(SendBulkSMSActivity.RESULT_SELECT_MODEL, mIntent);
                        finish();
                    } else if (!Utility.isEmpty(from_activity) && ("resend_smsORcloud".equals(from_activity) || "cloudCall_resend_smsORcloud".equals(from_activity))) {
//                        models.clear();
//                        models = getDragModels();
//                        adapter.setList(models);
                        models = adapter.getModelsList();
                        for (int i = 0;i<models.size();i++){
                            models.get(i).setChoose(false);
                        }
                        models.get(position).setChoose(true);
                        adapter.setList(models);
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 902) {
            selectState = "approved";
            changeButtonBG(button_left, BUTTON_LEFT);
            models.clear();
            models = (List<ReplyModel>) data.getSerializableExtra("models");
            adapter.setList(models);
        } else if (requestCode == Constants.REQUEST_ADD_MODEL) {
            getModelsList();
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    private void startAddModelActivity() {
        UMShareManager.onEvent(context, "SMS_SMSModel_add", "SMS", "SMS:添加短信模板");
        mIntent = new Intent(context, AddModelActivity.class);
        mIntent.putExtra("template_type", template_type);
        startActivityForResult(mIntent, Constants.REQUEST_ADD_MODEL);
    }

    class MyOnclickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_model:// 添加模板
                    startAddModelActivity();
                    break;
                case R.id.tv_more:// 排序按钮
                    if (!Utility.isEmpty(from_activity)&&("resend_smsORcloud".equals(from_activity)||"cloudCall_resend_smsORcloud".equals(from_activity))) {//用于在短信记录|云呼记录筛选后发送短信
                        String tid = skuaidiDb.getSelectedModelId();
                        if (!Utility.isEmpty(tid)) {
                            resendInform(tid);
                        } else {
                            UtilToolkit.showToast("请选择一个模板");
                        }
                        return;
                    }
                    if (!UtilityTime.isToday(context, SkuaidiSpf.getSmsTemplateSortCurDate(context))) {
                        SkuaidiSpf.saveSmsTemplateSortCurDate(context, UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(), UtilityTime.YYYY_MM_DD));
                        UMShareManager.onEvent(context, "SMS_SMSModel_sort_Num", "SMS", "SMS:模板排序人数【个人当天只统计1次】");
                    }

                    UMShareManager.onEvent(context, "SMS_SMSModel_sort", "SMS", "SMS:模板排序");

                    if (null != getModels(skuaidiDb.getPaijianModels("approved"))
                            && 0 != getModels(skuaidiDb.getPaijianModels("approved")).size()) {
                        mIntent = new Intent(context, ModelDragListActivity.class);
                        int requestCode = 901;
                        startActivityForResult(mIntent, requestCode);
                    } else {// 没有已审核的模板
                        UtilToolkit.showToast("没有已审核的模板");
                    }
                    break;
                case R.id.button_left:// 已审核
                    selectState = "approved";
                    changeButtonBG(button_left, BUTTON_LEFT);
                    models.clear();
                    models = getDragModels();
                    adapter.setList(models);
                    tv_more.setVisibility(View.VISIBLE);
                    break;
                case R.id.button_middle:// 待审核
                    selectState = "apply";
                    changeButtonBG(button_middle, BUTTON_MIDDLE);
                    models.clear();
                    models = skuaidiDb.getPaijianModels("apply", "indeterminate");
                    adapter.setList(models);
                    tv_more.setVisibility(View.GONE);
                    break;
                case R.id.button_right:// 未审核
                    selectState = "reject";
                    changeButtonBG(button_right, BUTTON_RIGHT);
                    models.clear();
                    models = getModels(skuaidiDb.getPaijianModels("reject"));
                    adapter.setList(models);
                    tv_more.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

    }

    private void changeButtonBG(TextView button_textview, String button_name) {
        if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            button_left.setBackgroundResource(R.drawable.shape_default_orange_radius_left);
            button_middle.setBackgroundColor(Utility.getColor(context,R.color.sto_main_color));
            button_right.setBackgroundResource(R.drawable.shape_default_orange_radius_right);
        } else {
            button_left.setBackgroundResource(R.drawable.shape_default_green_radius_left);
            button_middle.setBackgroundColor(Utility.getColor(context,R.color.default_green));
            button_right.setBackgroundResource(R.drawable.shape_default_green_radius_right);
        }
        button_left.setTextColor(Utility.getColor(context,R.color.white));
        button_middle.setTextColor(Utility.getColor(context,R.color.white));
        button_right.setTextColor(Utility.getColor(context,R.color.white));
        if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            button_textview.setTextColor(Utility.getColor(context,R.color.sto_main_color));
        } else {
            button_textview.setTextColor(Utility.getColor(context,R.color.default_green));
        }
        switch (button_name) {
            case BUTTON_LEFT:
                button_left.setBackgroundResource(R.drawable.shape_radius_btn_left_white2);
                break;
            case BUTTON_MIDDLE:
                button_middle.setBackgroundColor(Utility.getColor(context,R.color.white));
                break;
            case BUTTON_RIGHT:
                button_right.setBackgroundResource(R.drawable.shape_radius_btn_right_white2);
                break;
        }
    }

    /**
     * 将数据转成json格式
     **/
    private String getCallData() {
        List<SendRecordSelectSmsOrCloudParameter> srssocps = new ArrayList<>();
        for (int i = 0; i < smsRecords.size(); i++) {
            SendRecordSelectSmsOrCloudParameter srssocp = new SendRecordSelectSmsOrCloudParameter();
            srssocp.setInform_id(smsRecords.get(i).getInform_id());
            srssocp.setBh(smsRecords.get(i).getExpress_number());
            srssocp.setDh(smsRecords.get(i).getDh());
            srssocps.add(srssocp);
        }
        return new Gson().toJson(srssocps);
    }

    /**
	 * 将数据转成json格式
	 **/
	private String getCallData_cloudCallRecord() {
		List<SendRecordSelectVoiceParam> srssocps = new ArrayList<>();
		for (int i = 0; i < cvres.size(); i++) {
			SendRecordSelectVoiceParam srssocp = new SendRecordSelectVoiceParam();
			srssocp.setCid(cvres.get(i).getCid());
			srssocp.setBh(cvres.get(i).getBh());
			srssocp.setDh(cvres.get(i).getDh());
			srssocps.add(srssocp);
		}
		return new Gson().toJson(srssocps);
	}

    /**
     * 短信记录或云呼记录里面筛选后【未取件|发送失败】重新发送短信接口
     * modelTid:模板ID
     **/
    private void resendInform(String modelTid) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname","inform_user/resendInform");
            data.put("role", "courier");
            data.put("tid", modelTid);
            if ("resend_smsORcloud".equals(from_activity)) {
                data.put("from", "inform_user");
                data.put("call_data", getCallData());
            }else {
                data.put("from", "ivr");
                data.put("call_data", getCallData_cloudCallRecord());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 获取模板列表【接口】
     **/
    private void getModelsList() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_template/getlist");
            data.put("template_type", template_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 删除模板【接口】
     **/
    private void deleteModel(String tid) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_template/del");
            data.put("tid", tid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result1 = null;
        try {
            result1 = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        final JSONObject result = result1;
        if ("inform_template/getlist".equals(sname)) {
            onAsynchronousProcessing(new OnAsynchronous() {
                @Override
                public void onProcessingFinish() {

                }

                @Override
                public void onAsynchronousFunction() {
                    List<ReplyModel> cacheList = new ArrayList<>();
                    try {
                        if (result == null) {
                            UtilToolkit.showToast("获取数据失败");
                            return;
                        }
                        JSONArray descArr = result.getJSONArray("data");// 被解析数据需要修改
                        for (int i = 0; i < descArr.length(); i++) {
                            ReplyModel replyModel = new ReplyModel();
                            replyModel.setTid(descArr.getJSONObject(i).getString("tid"));
                            replyModel.setModelContent(descArr.getJSONObject(i).getString("content"));
                            replyModel.setApply_time(descArr.getJSONObject(i).getString("apply_time"));
                            replyModel.setApprove_time(descArr.getJSONObject(i).getString("approve_time"));
                            replyModel.setModify_time(UtilityTime.timeStringToTimeStamp(descArr.getJSONObject(i).getString("approve_time"),UtilityTime.YYYY_MM_DD_HH_MM_SS));
                            replyModel.setState(descArr.getJSONObject(i).getString("state"));
                            replyModel.setTitle(descArr.getJSONObject(i).getString("title"));
                            if ("sms".equals(template_type)) {
                                replyModel.setTemplate_type(0);
                            } else {
                                replyModel.setTemplate_type(1);
                            }
                            List<ReplyModel> cacheModels = getModels(skuaidiDb.getPaijianModels());// 获取当前数据库里所有的数据
                            if (null != cacheModels && 0 != cacheModels.size()) {
                                for (int j = 0; j < cacheModels.size(); j++) {// 循环当前数据库
                                    ReplyModel replyModel2 = cacheModels.get(j);
                                    if (replyModel.getTid().equals(replyModel2.getTid())
                                            && replyModel.getState().equals("approved")) {// 服务器解析当前条目与本地数据库所有数据对比【如果数据库中存在当前解析的条目】
                                        replyModel.setSortNo(replyModel2.getSortNo());// 将本地数据库中对应的排序号设置到当前解析的条目中
                                        break;
                                    } else {
                                        replyModel.setSortNo("");// 设置解析的当前条目排序号为空
                                    }
                                }
                            }
                            cacheList.add(replyModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    skuaidiDb.insertNewReplyModel(cacheList);

                    List<ReplyModel> cacheModels = getModels(skuaidiDb.getPaijianModels());// 获取到服务器所有数据以后在本地数据库中的全部数据
                    for (int i = 0; i < cacheModels.size(); i++) {
                        for (int j = 0; j < cacheList.size(); j++) {
                            if (null != cacheModels.get(i)) {
                                if (cacheModels.get(i).getTid().equals(cacheList.get(j).getTid())) {
                                    break;
                                }
                                if (j == cacheList.size() - 1) {
                                    skuaidiDb.deleteModelByTid(cacheModels.get(i).getTid());// 删除本地保存的但服务器上没有的模板【删除的是本地的】
                                }
                            }
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });
        } else if ("inform_template/del".equals(sname)) {
            UtilToolkit.showToast(msg);
            for (int i = 0; i < adapter.getList().size(); i++) {
                if (adapter.getList().get(i).getTid().equals(modelTid)) {
                    // ********************删除模板********************************
                    skuaidiDb.deleteModelByTid(modelTid);
                    skuaidiDb.setIsChoose(adapter.getList().get(0).getId());
                    adapter.getItem(0).setChoose(true);
                    adapter.getList().remove(i);
                    adapter.notifyDataSetChanged();
                    // *********************保存当前列表中新的排序tid**************************
                    if (null != adapter) {
                        List<ReplyModel> models = adapter.getModelsList();
                        if (null != models && 0 != models.size()) {
                            String tids = "";
                            for (int j = 0; j < models.size(); j++) {
                                String tid = models.get(j).getTid();
                                tids = tids + "," + tid;
                            }
                            tids = tids.substring(1, tids.length());
                            SkuaidiSpf.setModelDragSort(context, tids);
                        }
                    }
                    break;
                }
            }
        }else if("inform_user/resendInform".equals(sname)){
            if (!Utility.isEmpty(msg)){
                UtilToolkit.showToast_Custom(msg);
                setResult(RecordSendSmsSelectActivity.RESULT_RESEND);
                finish();
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (!Utility.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (Utility.isNetworkConnected()) {
            if (code.equals("7") && null != result) {
                try {
                    String desc = result.optString("desc");
                    UtilToolkit.showToast(desc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
