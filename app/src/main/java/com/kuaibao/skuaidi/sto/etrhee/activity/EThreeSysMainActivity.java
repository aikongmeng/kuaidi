package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.SignAgingActivity;
import com.kuaibao.skuaidi.activity.adapter.BusinessMenuAdapter;
import com.kuaibao.skuaidi.activity.view.NotifyBroadCast;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ScanScope;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.FingertipDeviceManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.E3ScanActivity;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.ethree2.E3StoChooseAccountActivity;
import com.kuaibao.skuaidi.sto.ethree2.E3ZTAccountLoginActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.core.Arrays;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * E3系统主界面
 *
 * @author xy
 *         <p/>
 *         lgg reconsitution 2016/06/13
 */
public class EThreeSysMainActivity extends SkuaiDiBaseActivity implements OnClickListener, OnItemClickListener {
    private Activity context;
    private RelativeLayout rl_title_center;
    private TextView courier_job_num, courier_name, courier_phone, device_ID, courier_lattice_point;
    private TextView tv_update;
    private TextView tv_name;
    private GridView controlCenter;
    private BusinessMenuAdapter businessMenuAdapter;
    private View more;
    private String noticeDetail = "";
    private String empno = "";
    protected FinalDb finalDb;
    protected String brand = "";
    protected CurrentE3VerifyInfo currentE3VerifyInfo;
    /**
     * 获取用户身份
     */
    private boolean getIdentity = false;

    private int toUploadCount;

    private static String deleteday;

    private NotifyBroadCast notifyBroadCast;

    List<Integer> images = Arrays.asList(R.drawable.e3_scan_lanpice_icon, R.drawable.e3_scan_fapice_icon,
            R.drawable.e3_scan_daopice_icon, R.drawable.e3_scan_piepice_icon, R.drawable.e3_scan_badpice_icon,
            R.drawable.e3_scan_signedpice_icon, R.drawable.e3_scan_record_icon, R.drawable.e3_scan_upload_icon,
            R.drawable.icon_singaging);
    List<String> description = Arrays.asList("收件扫描", "发件扫描", "到件扫描", "派件扫描", "问题件", "签收扫描", "扫描记录", "数据上传", "有派无签");
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ethree_sys_main_layout);
        context = this;
        EventBus.getDefault().register(this);
        finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
        mUserInfo = SkuaidiSpf.getLoginUser();
        brand= mUserInfo.getExpressNo();
        currentE3VerifyInfo = E3SysManager.getReviewInfoNew();
        empno = E3SysManager.getCourierNO();
        getControl();
        setMenu();
        setDatas();
        setListener();
        getBrodcast();
        deleteUploadedOrders();
    }

    /**
     * 删除已经上传过的单号
     */
    private void deleteUploadedOrders() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        String today = formatter.format(curDate);
        if (!today.equals(deleteday)) {
            deleteday = today;
            E3OrderDAO.deleteUploadedOrders(brand, empno, today);
        }

    }

    /**
     * 查询快递员巴枪扫描权限
     */
    private void getScanScope() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "scan.access.get");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    private void getBrodcast() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("sname", "inform.broadcast");
                    object.put("action", "get");
                    object.put("type", "scan");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
            }
        }).start();
    }

    private void getControl() {
        rl_title_center = (RelativeLayout) findViewById(R.id.rl_title_center);
        courier_job_num = (TextView) findViewById(R.id.tv_courier_job_num);
        courier_name = (TextView) findViewById(R.id.tv_courier_name);
        courier_phone = (TextView) findViewById(R.id.tv_courier_phone);
        device_ID = (TextView) findViewById(R.id.tv_device_id);
        courier_lattice_point = (TextView) findViewById(R.id.tv_courier_lattice_point);
        controlCenter = (GridView) findViewById(R.id.gv_control_center);
        more = findViewById(R.id.tv_more);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_name = (TextView) findViewById(R.id.tv_name);
    }

    private void setMenu() {
        businessMenuAdapter = new BusinessMenuAdapter(context, brand, empno);
        if (E3SysManager.BRAND_STO.equals(brand) || E3SysManager.BRAND_ZT.equals(brand)) {
            getScanScope();
        }
        ScanScope ss = SkuaidiSpf.getUserScanScope(context);
        setMenuItems(ss);

    }

    private void setDatas() {
        if (currentE3VerifyInfo != null) {
            courier_job_num.setText(currentE3VerifyInfo.getCounterman_code());
            courier_lattice_point.setText(currentE3VerifyInfo.getShop_name());
            courier_phone.setText(mUserInfo.getPhoneNumber());
            courier_name.setText(currentE3VerifyInfo.getCounterman_name());
            if ("zt".equals(brand)) {
                device_ID.setText(currentE3VerifyInfo.getImei());
            } else {
                device_ID.setText(Utility.getOnlyCode());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        toUploadCount = E3OrderDAO.getOrderCount(brand, empno);
        businessMenuAdapter.notifyDataSetChanged();
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

    private void setListener() {
        controlCenter.setOnItemClickListener(this);
        more.setOnClickListener(this);
        tv_update.setOnClickListener(this);
    }

    boolean isCanOperation = true;

    @SuppressWarnings("unchecked")
    @Override
    protected void onRequestSuccess(String sname, String msg, String data, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("inform.broadcast".equals(sname)) {
            noticeDetail = result.optJSONObject("retArr").optString("content");
            if (!TextUtils.isEmpty(noticeDetail)) {
                notifyBroadCast = new NotifyBroadCast(context, 10, rl_title_center, noticeDetail, this);
                if (!isFinishing())
                    notifyBroadCast.show();
            }
        } else if ("scan.access.get".equals(sname)) {
            try {
                JSONObject jb = result.optJSONObject("retArr");
                if (jb != null) {
                    Gson gson = new Gson();
                    ScanScope ss = gson.fromJson(jb.toString(), ScanScope.class);
                    SkuaidiSpf.saveUserScanScope(context, ss);
                    setMenuItems(ss);
                } else {
                    if (getIdentity) {
                        String identity = result.optString("retArr");
                        if (!TextUtils.isEmpty(identity) && !"null".equals(identity))
                            tv_name.setText(identity + ":");
                    }
                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 依据用户巴抢权限设置要显示的扫描项。
     *
     * @param ss
     */
    private void setMenuItems(ScanScope ss) {
        if (ss != null) {
            if (E3SysManager.BRAND_STO.equals(brand) || E3SysManager.BRAND_ZT.equals(brand)) {// 申通
                // 权限不为空
                ArrayList<Integer> imageList = new ArrayList<>();
                ArrayList<String> descList = new ArrayList<>();
                if (ss.getSj() != null && ss.getSj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_lanpice_icon);// 收件
                    descList.add("收件扫描");
                }
                if (ss.getFj() != null && ss.getFj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_fapice_icon);// 发件
                    descList.add("发件扫描");
                }
                if (ss.getDj() != null && ss.getDj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_daopice_icon);// 到件
                    descList.add("到件扫描");
                }
                if (ss.getPj() != null && ss.getPj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_piepice_icon);// 派件
                    descList.add("派件扫描");
                }
                if (ss.getWtj() != null && ss.getWtj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_badpice_icon);// 问题件
                    descList.add("问题件");
                }
                if (ss.getQsj() != null && ss.getQsj().getAccess() == 1) {
                    imageList.add(R.drawable.e3_scan_signedpice_icon);// 签收件
                    descList.add("签收扫描");
                }
                imageList.add(R.drawable.e3_scan_record_icon);
                imageList.add(R.drawable.e3_scan_upload_icon);
                imageList.add(R.drawable.icon_singaging);
                descList.add("扫描记录");
                descList.add("数据上传");
                descList.add("有派无签");
                images = imageList;
                description = descList;
            }

        } else {
            if (E3SysManager.BRAND_QF.equals(brand)) {
                ArrayList<Integer> imageList = new ArrayList(Arrays.asList(R.drawable.e3_scan_lanpice_icon,
                        R.drawable.e3_scan_piepice_icon, R.drawable.e3_scan_badpice_icon,
                        R.drawable.e3_scan_signedpice_icon, R.drawable.e3_scan_record_icon, R.drawable.e3_scan_upload_icon));
                ArrayList<String> descList = new ArrayList(Arrays.asList("收件扫描", "派件扫描", "问题件", "签收扫描", "扫描记录",
                        "数据上传"));
                images = imageList;
                description = descList;
            } else if (E3SysManager.BRAND_ZT.equals(brand)) {
                ArrayList imageList = new ArrayList(Arrays.asList(R.drawable.e3_scan_lanpice_icon,
                        R.drawable.e3_scan_piepice_icon, R.drawable.e3_scan_badpice_icon,
                        R.drawable.e3_scan_signedpice_icon, R.drawable.e3_scan_record_icon, R.drawable.e3_scan_upload_icon,
                        R.drawable.icon_singaging));
                ArrayList<String> descList = new ArrayList(Arrays.asList("收件扫描", "派件扫描", "问题件", "签收扫描", "扫描记录",
                        "数据上传", "有派无签"));
                images = imageList;
                description = descList;
            }

        }
        if (E3SysManager.BRAND_ZT.equals(brand)) {
            images.add(4, R.drawable.e3_scan_thirdparty_icon);
            description.add(4, "第三方签收");
        }
        businessMenuAdapter.setImages(images);
        businessMenuAdapter.setDescription(description);
        controlCenter.setAdapter(businessMenuAdapter);
        businessMenuAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if ("scan.access.get".equals(sname)) {
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    public void onBack(View v) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCanOperation == true) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.tv_more:
                Intent intent = new Intent(this, DataScanSettingActivity.class);
                startActivity(intent);
                break;
            // 广播通知
            case R.id.tvBroadCast:
                UMShareManager.onEvent(context, "eThreeSysMainActivity_broadcast", "eThreeSysMain", "巴枪扫描:广播通知");
                String phone = mUserInfo.getPhoneNumber();
                loadWeb("http://m.kuaidihelp.com/help/broadcast?mobile=" + phone + "&type=scan", "");
                break;
            case R.id.tv_update:
                String desc = "";
                if ("zt".equals(brand)) {
                    desc = "你将进行巴枪账号的修改,确认修改吗?";
                } else if ("sto".equals(brand)) {
                    desc = "请确认你有新的工号并将手机号" + mUserInfo.getPhoneNumber() + "维护在该工号下,这样修改信息后可更新到你最新的工号,网点信息";
                } else if ("qf".equals(brand)) {
                    desc = "如果要修改快递员信息：\n1.请联系全峰站点客服，将你登录快递员APP的手机号维护在现在使用的工号下。\n2.全峰总部要求手机号对应工号的唯一性，请务必删除原有工号下的手机号信息。\n3.修改完毕后，重新登录快递员APP即可更新信息。";
                }
                showModifyAlert(brand, "快递员巴枪账号修改", desc);
                break;
        }
    }

    private void showModifyAlert(final String brand, String title, String desc) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(desc);
        builder.setTitle(title);
        if ("qf".equals(brand)) {
            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if ("zt".equals(brand)) {
                        Intent intent = new Intent(EThreeSysMainActivity.this, E3ZTAccountLoginActivity.class);
                        intent.putExtra(E3ZTAccountLoginActivity.BRANCH_NO_NAME, null == empno ? "" : empno);
                        intent.putExtra(E3ZTAccountLoginActivity.FROM_WHERE_NAME, "E3MainActivity");
                        startActivity(intent);
                    } else if ("sto".equals(brand)) {
                        Intent intent = new Intent(EThreeSysMainActivity.this, E3StoChooseAccountActivity.class);
                        intent.putExtra(E3StoChooseAccountActivity.FROM_WHERE, "e3MainActivity");
                        intent.putExtra(E3StoChooseAccountActivity.TITLT_NAME, "快递员巴枪账号修改");
                        intent.putExtra(E3StoChooseAccountActivity.BRAND_TYPE_NAME, "sto");
                        startActivity(intent);
                    }
                }
            });
        }
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> viewGroup, View convertView, int position, long id) {
        Intent intent;

        switch (images.get(position)) {
            case R.drawable.e3_scan_lanpice_icon:
                UMShareManager.onEvent(context, "E3_scan_lanPice", "E3", "E3：扫收件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_LANPICE);
                break;
            case R.drawable.e3_scan_fapice_icon:
                UMShareManager.onEvent(context, "E3_scan_faPice", "E3", "E3：扫发件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_FAPICE);
                break;
            case R.drawable.e3_scan_daopice_icon:
                UMShareManager.onEvent(context, "E3_scan_shouPice", "E3", "E3：扫到件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_DAOPICE);
                break;
            case R.drawable.e3_scan_piepice_icon:
                UMShareManager.onEvent(context, "E3_scan_piePice", "E3", "E3：扫派件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_PIEPICE);
                break;
            case R.drawable.e3_scan_badpice_icon:
                UMShareManager.onEvent(context, "E3_scan_badWayBill", "E3", "E3：扫问题件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_BADPICE);
                break;
            case R.drawable.e3_scan_signedpice_icon:
                UMShareManager.onEvent(context, "E3_scan_signedWayBill", "E3", "E3：扫签收件");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_SIGNEDPICE);
                break;
            case R.drawable.e3_scan_thirdparty_icon:
                UMShareManager.onEvent(context, "E3_scan_signed_third_party", "E3", "E3：第三方签收");
                SpecialEquipmentHandle(E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY);
                break;
            case R.drawable.e3_scan_record_icon:
                UMShareManager.onEvent(context, "E3_sweepRecord", "E3", "E3：扫描记录");
                intent = new Intent(context, EThreeSysSweepRecordActivity.class);
                intent.putExtra("dataType", "sweepRecord");
                startActivity(intent);
                break;
            case R.drawable.e3_scan_upload_icon:
                UMShareManager.onEvent(context, "E3_sweepUpload", "E3", "E3：数据上传");
                toUploadCount = E3OrderDAO.getOrderCount(brand, empno);
                if (businessMenuAdapter != null)
                    businessMenuAdapter.notifyDataSetChanged();
                if (toUploadCount == 0) {
                    UtilToolkit.showToast("没有未上传的数据！");
                    return;
                }
                Intent mIntent = new Intent(context, BackgroundUploadService.class);
                stopService(mIntent);
                intent = new Intent(context, EThreeSysSweepRecordActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.drawable.icon_singaging:
                UMShareManager.onEvent(context, "E3_signaging", "E3", "E3：有派无签");
                intent = new Intent(context, SignAgingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void SpecialEquipmentHandle(String scanType) {
        Intent intent;
        String type = E3SysManager.scanToTypeMap.get(scanType);
        // 查询缓存数据
        ArrayList<E3_order> orders = E3OrderDAO.queryCacheOrder(type, brand, empno);
        if (orders != null && orders.size() != 0) {
            showImportDialog(orders, scanType);
            return;
        }

        if (FingertipDeviceManager.isSpecialEquipment()) {
            intent = new Intent(context, EthreeInfoScanActivity.class);
            intent.putExtra("isSpecialEquipment", true);
        } else {
            intent = new Intent(context, E3ScanActivity.class);
            intent.putExtra("isContinuous", true);
        }
        intent.putExtra("scanType", scanType);
        startActivity(intent);
    }

    /**
     * app崩溃前缓存的单号导入提示
     */
    private void showImportDialog(final ArrayList<E3_order> orders, final String scanType) {
        final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, new View(
                context));
        dialog.setTitle("导入未处理数据");
        dialog.setCommonContent("发现未处理的扫描数据，是否导入？");
        dialog.setPositiveButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");

        dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

            @Override
            public void onClick() {
                dialog.dismiss();
                Intent intent = new Intent(context, EthreeInfoScanActivity.class);
                intent.putExtra("scanType", scanType);
                intent.putExtra("e3WayBills", orderToInfo(orders, 0, 1, scanType));
                startActivity(intent);
            }
        });
        dialog.setNegativeClickListener(new SkuaidiE3SysDialog.NegativeButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                // 取消导入，则删除缓存数据
                E3OrderDAO.deleteCacheOrders(orders);
                Intent intent;
                if (FingertipDeviceManager.isSpecialEquipment()) {
                    intent = new Intent(context, EthreeInfoScanActivity.class);
                    intent.putExtra("isSpecialEquipment", true);

                } else {
                    intent = new Intent(context, E3ScanActivity.class);
                    intent.putExtra("isContinuous", true);
                }
                intent.putExtra("scanType", scanType);
                startActivity(intent);
                dialog.dismiss();
            }

        });
        if (!isFinishing())
            dialog.showDialog();
    }

    /**
     * @param orders isupload 是否已经上传过
     * @return
     */
    private ArrayList<NotifyInfo> orderToInfo(ArrayList<E3_order> orders, int isUpload, int isCache, String scanType) {
        ArrayList<NotifyInfo> infos = new ArrayList<NotifyInfo>();
        for (E3_order order : orders) {
            NotifyInfo info = new NotifyInfo();
            info.setExpress_number(order.getOrder_number());
            info.setWayBillTypeForE3(order.getType_extra());
            info.setCourierJobNO(order.getOperatorCode());
            if (scanType.equals(E3SysManager.SCAN_TYPE_LANPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 收件
                info.setCourierJobNO(currentE3VerifyInfo.getCounterman_code());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCounterman_name());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_FAPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 发件
                info.setCourierJobNO(currentE3VerifyInfo.getCounterman_code());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCounterman_name());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_DAOPICE)) {// 到件

            } else if (scanType.equals(E3SysManager.SCAN_TYPE_PIEPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 派件
                info.setCourierJobNO(currentE3VerifyInfo.getCounterman_code());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCounterman_name());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_BADPICE)) {// 问题件

            } else if (scanType.equals(E3SysManager.SCAN_TYPE_SIGNEDPICE)) {// 签收件

            }
            info.setStation_name(order.getSta_name());
            info.setPicPath(order.getPicPath());
            info.setProblem_desc(order.getProblem_desc());

            info.setScanTime(order.getScan_time());
            info.setLatitude(order.getLatitude());
            info.setLongitude(order.getLongitude());
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            infos.add(info);
        }
        return infos;
    }

    @Override
    protected void onDestroy() {
        if (notifyBroadCast != null) {
            notifyBroadCast.dismiss();
            if (notifyBroadCast.mTimer != null) {
                notifyBroadCast.mTimer.cancel();
                notifyBroadCast.mTimer = null;
            }
            if (notifyBroadCast.task != null) {
                notifyBroadCast.task.cancel();
                notifyBroadCast.task = null;
            }
            notifyBroadCast = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && toUploadCount > 0 && SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            Intent mIntent = new Intent(context, BackgroundUploadService.class);
            startService(mIntent);
        }
    }

    public static final String FINISH_MESSAGE = "pleaseFinish";

    @Subscribe
    public void onReciveFinishNotify(String message) {
        if (FINISH_MESSAGE.equals(message)) {
            finish();
        }
    }
}
