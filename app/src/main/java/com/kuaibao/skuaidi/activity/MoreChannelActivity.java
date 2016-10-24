package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.DragGridViewAdapter;
import com.kuaibao.skuaidi.activity.expressShop.MyShopActivity;
import com.kuaibao.skuaidi.activity.make.realname.RealNameInputActivity;
import com.kuaibao.skuaidi.activity.make.realname.RealNameInputLocalActivity;
import com.kuaibao.skuaidi.activity.smsrecord.SmsRecordActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.business.entity.ResponseReadStatus;
import com.kuaibao.skuaidi.business.nettelephone.NetTelePhoneActivity;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.common.view.DragGridView.DragGrid;
import com.kuaibao.skuaidi.customer.MyCustomManageActivity;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dispatch.activity.DispatchActivity;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更多菜单
 * Created by wang on 2016/6/13.
 */
public class MoreChannelActivity extends SkuaiDiBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.dgv_more)
    DragGrid dgvMore;
    private List<String> channelList;//更多里面的功能选项
    private DragGridViewAdapter businessMenuAdapter;
    private Context context;
    HashMap<String, Integer> function_redCircle_map = new HashMap();//功能名与小红点的键值对
    private static SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
    private int redPoint_sms = 0;// 短信记录小红点
    private int redPoint_ivr = 0;// 语音记录小红点
    private int redPoint_order = 0;// 订单小红点
    private int redPoint_liuyan = 0;// 留言小红点
    private int redpoint_notice = 0;// 通知中心小红点
    private int redpoint_dispatch = 0;// 通知中心小红点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_channel);
        ButterKnife.bind(this);
        tvTitleDes.setText("更多");
        context = this;
        function_redCircle_map = (HashMap<String, Integer>) getIntent().getSerializableExtra("read_map");
        channelList = SkuaidiSpf.getUserMoreItems(SkuaidiSpf.getLoginUser().getPhoneNumber());
        businessMenuAdapter = new DragGridViewAdapter(this, channelList, false);
        dgvMore.containMoreItem(false);
        dgvMore.setAdapter(businessMenuAdapter);
        businessMenuAdapter.notifyRedCircleChanged(function_redCircle_map);
        businessMenuAdapter.setOnAddListener(new DragGridViewAdapter.AddListener() {
            @Override
            public void onAdd(int position) {
                List<String> tempList = SkuaidiSpf.getUserBusinessItems(SkuaidiSpf.getLoginUser().getPhoneNumber());
                if (tempList == null) {
                    KLog.w("业务菜单数据异常！");
                    return;
                }
                if ("更多".equals(tempList.get(tempList.size() - 1))) { //业务菜单页最后一项为“更多”，添加在更多之前
                    tempList.add(tempList.size() - 1, channelList.get(position));
                } else {
                    if (tempList.size() >= 2) {
                        tempList.add(tempList.get(tempList.size() - 2));
                        tempList.set(tempList.size() - 3, channelList.get(position));
                    }

                }
                SkuaidiSpf.setUserBusiinessItems(SkuaidiSpf.getLoginUser().getPhoneNumber(), tempList);
                businessMenuAdapter.remove(position);
                businessMenuAdapter.setReset(true);
                SkuaidiSpf.setUserMoreItems(SkuaidiSpf.getLoginUser().getPhoneNumber(), businessMenuAdapter.getChannelList());
                UtilToolkit.showToast("已添加到【首页】");


            }
        });
        dgvMore.setItemClickListener(new DragGrid.MyItemClickListener() {
            @Override
            public void OnItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;
                switch (channelList.get(position)) {
                    case "短信记录":
                        UMShareManager.onEvent(context, "business_item_piePiece", "business", "派件短信");
                        intent = new Intent(context, SmsRecordActivity.class);
                        // intent = new Intent(context, StartPageActivity.class);
                        startActivity(intent);
                        break;
                    case "云呼记录":
                        UMShareManager.onEvent(context, "business_item_cloudVoice", "business", "云呼记录");
                        intent = new Intent(context, CloudVoiceRecordActivity.class);
                        startActivity(intent);
                        break;
                    case "查快递":
                        // 查快递
                        UMShareManager.onEvent(context, "business_item_lanPiece", "business", "揽件查询");
                        intent = new Intent(context, FindExpressActivity.class);
                        startActivity(intent);
                        break;
                    case "收款":
                        UMShareManager.onEvent(context, "business_item_gathering", "business", "业务界面收款");
                        // intent = new Intent(context, WebViewActivity.class);
                        // intent.putExtra("fromwhere", "receive_money");
                        intent = new Intent(context, MakeCollectionsActivity.class);
                        startActivity(intent);
                        break;
                    case "留言":
                        UMShareManager.onEvent(context, "business_item_Message", "business", "业务留言");
                        intent = new Intent(context, LiuyanCenterActivity.class);
                        LiuyanCenterActivity.fromWhere = 1;
                        intent.putExtra("type", 1);
                        intent.putExtra("FlagIsRun", true);
                        startActivity(intent);
                        break;
                    case "客户管理":
                        UMShareManager.onEvent(context, "callLog_customer_manager", "callLog", "客户管理");
                        intent = new Intent(context, MyCustomManageActivity.class);
                        startActivity(intent);
                        break;
                    case "订单":
                        UMShareManager.onEvent(context, "business_item_order", "business", "业务订单");
                        intent = new Intent(context, OrderCenterActivity.class);
                        OrderCenterActivity.fromWhere = 1;
                        startActivity(intent);
                        break;
                    case "实名寄递":
                        if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getArea()) && SkuaidiSpf.getLoginUser().getArea().contains("浙江")){// 浙江地区
                            if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getExpressNo()) && ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo()) || "zt".equals(SkuaidiSpf.getLoginUser().getExpressNo()))){// 中通|申通【使用浙江地区H5网页】
                                intent = new Intent(context, RealNameInputActivity.class);
//                        intent.putExtra("money", money);
                                startActivity(intent);
                            }else{// 非中通|申通【使用原生实名登记】
                                // 扫描单号【默认单扫】
                                /*Intent mIntent = new Intent(context, CaptureActivity.class);
                                mIntent.putExtra("isContinuous", false);
                                mIntent.putExtra("qrcodetype", Constants.TYPE_KEEP_ACCOUNTS);
                                mIntent.putExtra("from", "keep_accounts");// 来自记账
                                startActivityForResult(mIntent, BusinessFragment.REQUEST_SCAN_EXPRESS_NO);*/
                                originalAuth();
                            }
                        }else{// 非浙江地区
                            if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getExpressNo()) &&"sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){// 申通【使用全国H5网页】
                                loadWebCommon("http://m.kuaidihelp.com/realname/senderInfo?mobile=" + SkuaidiSpf.getLoginUser().getPhoneNumber());
                            }else{// 非申通【使用原生实名登记】
                                // 扫描单号【默认单扫】
                                /*Intent mIntent = new Intent(context, CaptureActivity.class);
                                mIntent.putExtra("isContinuous", false);
                                mIntent.putExtra("qrcodetype", Constants.TYPE_KEEP_ACCOUNTS);
                                mIntent.putExtra("from", "keep_accounts");// 来自记账
                                startActivityForResult(mIntent, BusinessFragment.REQUEST_SCAN_EXPRESS_NO);*/
                                originalAuth();
                            }
                        }
                        break;
                    case "我的店铺":
                        UMShareManager.onEvent(context, "business_item_shop", "business", "更多模块：我的快递店铺");
                        intent = new Intent(context, MyShopActivity.class);
                        startActivity(intent);
                        break;
                    case "通话":
                        UMShareManager.onEvent(context, "business_item_customer", "business", "通话");
                        intent = new Intent(context, NetTelePhoneActivity.class);
                        // intent = new Intent(context, CallLogsActivity.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case "超派查询":
                        UMShareManager.onEvent(context, "business_item_superSends", "business", "超派查询");
                        intent = new Intent(context, OverareaQueryActivity.class);
                        startActivity(intent);
                        break;
                    case "格格货栈":
                        UMShareManager.onEvent(context, "business_item_cabinet", "business", "格格货栈");
                        intent = new Intent(context, SkuaidiCabinetActivity.class);
                        startActivity(intent);
                        break;
                    case "派件":
                        CurrentE3VerifyInfo currentE3VerifyInfo = E3SysManager.getReviewInfoNew();
                        if (currentE3VerifyInfo != null) {
                            intent = new Intent(context, DispatchActivity.class);
                            startActivity(intent);
                        } else {
                            UtilToolkit.showToast("巴枪权限异常！");

                        }
                        break;
                    case "丰巢管家":
                        UMShareManager.onEvent(context, "business_item_cabinet", "business", "丰巢货栈");
                        loadWebCommon("http://m.kuaidihelp.com/expressBox/fc?longitude=" + SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude() + "&latitude=" + SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude());
                        break;
                    case "快递柜":
                        UMShareManager.onEvent(context, "business_item_cabinet", "business", "快递柜");
                        intent = new Intent(context, SkuaidiCabinetActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        KLog.w();
                        break;

                }
            }
        });
    }

    private void originalAuth() {
        Intent intent = new Intent(this, RealNameInputLocalActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
            return;
        }
        if ("noread_info/get_info".equals(sname)) {
            redPoint_sms = json.optInt("sms");
            redPoint_ivr = json.optInt("ivr");
            redPoint_order = json.optInt("order");
            redPoint_liuyan = json.optInt("liuyan");
            redpoint_notice = json.optInt("notice");
            redpoint_dispatch=json.optInt("delivery");
            String vip = json.optString("vip");
            ResponseReadStatus responseReadStatus=new ResponseReadStatus();
            responseReadStatus.setSms(redPoint_sms);
            responseReadStatus.setIvr(redPoint_ivr);
            responseReadStatus.setOrder(redPoint_order);
            responseReadStatus.setLiuyan(redPoint_liuyan);
            responseReadStatus.setNotice(redpoint_notice);
            responseReadStatus.setDelivery(redpoint_dispatch);
            responseReadStatus.setVip(vip);
            ACache.get(getApplicationContext()).put("ResponseReadStatus",result);
            SkuaidiSpf.saveClientIsVIP(context, vip);// 保存用户是否是VIP用户状态【VIP特权可直接发送短信】
            function_redCircle_map.clear();
            function_redCircle_map.put("短信记录", redPoint_sms);
            function_redCircle_map.put("云呼记录", redPoint_ivr);
            function_redCircle_map.put("订单", redPoint_order);
            function_redCircle_map.put("留言", redPoint_liuyan);
            function_redCircle_map.put("派件", redpoint_dispatch);
            businessMenuAdapter.notifyRedCircleChanged(function_redCircle_map);
            EventBus.getDefault().post(new MessageEvent(MainActivity.NEW_NOTICE_COMING,""));
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }


    @OnClick(R.id.iv_title_back)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(101, intent);
        finish();
    }

    /**
     * 获取显示小红点状态
     */
    private void showReadState() {
        if (Utility.isNetworkConnected()) {
            getReadStatus();
        } else {
            if (newDB.isHaveUnreadOrder()) {
                function_redCircle_map.put("订单", 1);
            } else {
                function_redCircle_map.put("订单", 0);
            }
            businessMenuAdapter.notifyRedCircleChanged(function_redCircle_map);
        }

    }


    private final void getReadStatus() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "noread_info/get_info");
            data.put("role", "courier");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showReadState();
    }
}
