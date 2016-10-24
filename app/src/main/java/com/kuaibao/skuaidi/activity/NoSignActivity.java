package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.NoSignAdapter;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HH 未签收
 */
public class NoSignActivity extends SkuaiDiBaseActivity implements OnClickListener {

    private TextView tv_title_des;
    private Button bt_title_more;
    private Context context;
    private List<NotifyInfo> orders = new ArrayList<NotifyInfo>();
    private NotifyInfo order;
    private ImageView iv_nosign;
    private NoSignAdapter adapter;
    private ListView lv_no_sign;
    private ImageView iv_nosign_rad;
    private boolean selected = false;
    private List<NotifyInfo> select_order = new ArrayList<NotifyInfo>();
    private TextView bt_nosign_ok;
    private TextView tv_bad;
    private TextView tv_nosing_type;
    private String flag;
    /**
     * 数据量太大是不适合用Intent传递，直接引用
     */
    public static List<NotifyInfo> selectedOrders;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.nosign_activity);
        context = this;
        getControl();

        flag = getIntent().getStringExtra("flag");
        if (flag != null) {
            if (flag.equals("question")) {
                tv_nosing_type.setText("问题类型");

                tv_title_des.setText("问题件");
            }
        }
        orders = (List<NotifyInfo>) SKuaidiApplication.getInstance().onReceiveMsg("NoSingActivity", "NoSing");

        initData();
        allselect(null);//进入页面默认全选

    }

    private void initData() {
        if (orders != null && orders.size() > 0) {
            adapter = new NoSignAdapter(context, orders, new NoSignAdapter.CheckCallBack() {

                @Override
                public void checkStatus(boolean isAllCheck) {
                    if (isAllCheck) {
                        selected = true;
                        iv_nosign.setImageResource(R.drawable.batch_add_checked);
                    } else {
                        selected = false;
                        iv_nosign.setImageResource(R.drawable.select_edit_identity);
                    }
                }
            }, flag);
            lv_no_sign.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            bt_nosign_ok.setEnabled(false);
            tv_bad.setEnabled(false);
        }

    }

    private void getControl() {
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("未签收");
        bt_title_more = (Button) findViewById(R.id.bt_title_more);
        lv_no_sign = (ListView) findViewById(R.id.lv_no_sign);
        iv_nosign_rad = (ImageView) findViewById(R.id.iv_nosign_rad);
        tv_nosing_type = (TextView) findViewById(R.id.tv_nosing_type);
        iv_nosign = (ImageView) findViewById(R.id.iv_nosign);
        bt_nosign_ok = (TextView) findViewById(R.id.tv_nosign_ok);
        tv_bad = (TextView) findViewById(R.id.tv_bad);
        bt_nosign_ok.setOnClickListener(this);
        tv_bad.setOnClickListener(this);
        tv_bad.setText("问题件" + "(" + 0 + "/" + orders.size() + ")");
        //tv_bad.setTextColor(getResources().getColorStateList(R.color.white));
        bt_nosign_ok.setText("签收扫描" + "(" + 0 + "/" + orders.size() + ")");
        //bt_nosign_ok.setTextColor(getResources().getColorStateList(R.color.white));
        lv_no_sign.setOnItemClickListener(new OnItemClickListener() {
            /**
             * 跳转到查询快递界面
             */
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Intent intent = new Intent(context, CopyOfFindExpressResultActivity.class);
                intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
                intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
                intent.putExtra("order_number", orders.get(position).getExpress_number());
                startActivity(intent);
            }
        });
    }

    public TextView getSignView() {
        return bt_nosign_ok;
    }

    public TextView getBadView() {
        return tv_bad;
    }

    /**
     * 全选按钮监听
     *
     * @param view
     */
    public void allselect(View view) {
        if (adapter == null)
            return;
        List<NotifyInfo> orders = adapter.getList();
        if (!selected) {
            selected = true;
            iv_nosign.setImageResource(R.drawable.batch_add_checked);
            for (int i = 0; i < orders.size(); i++) {
                orders.get(i).setChecked(true);
            }
            bt_nosign_ok.setText("签收扫描(" + orders.size() + "/" + orders.size() + ")");
            tv_bad.setText("问题件(" + orders.size() + "/" + orders.size() + ")");
            adapter.setCheckCount(orders.size());
            adapter.notifyDataSetChanged();
            lv_no_sign.smoothScrollToPosition(orders.size() - 1);
        } else {
            selected = false;
            iv_nosign.setImageResource(R.drawable.select_edit_identity);
            for (int i = 0; i < orders.size(); i++) {
                orders.get(i).setChecked(false);
            }
            bt_nosign_ok.setText("签收扫描" + "(" + 0 + "/" + orders.size() + ")");
            tv_bad.setText("问题件" + "(" + 0 + "/" + orders.size() + ")");
            adapter.setCheckCount(0);
            adapter.notifyDataSetChanged();
        }

    }

    // 返回图标
    public void back(View v) {
        finish();

    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (Utility.isNetworkConnected() == true) {
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

    @Override
    public void onClick(View v) {
        String scanType = null;
        switch (v.getId()) {
            case R.id.tv_nosign_ok:
                if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                    if ("question".equals(flag)) {
                        UMShareManager.onEvent(context, "question_ok_sto_qrcode", "question_ok_sto", "有派无签:问题件:签收扫描(申通)");
                    } else {
                        UMShareManager.onEvent(context, "nosign_ok_sto_qrcode", "nosign_ok_sto", "有派无签:未签收:签收扫描(申通)");
                    }

                } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {

                    if ("question".equals(flag)) {
                        UMShareManager.onEvent(context, "question_ok_zt_qrcode", "question_ok_zt", "有派无签:问题件:签收扫描(申通)");
                    } else {
                        UMShareManager.onEvent(context, "nosign_ok_zt_qrcode", "nosign_ok_zt", "有派无签:未签收:签收扫描(中通)");
                    }
                }
                scanType = "扫签收";
                break;
            case R.id.tv_bad:
                if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                    if ("question".equals(flag)) {
                        UMShareManager.onEvent(context, "question_bad_sto_qrcode", "question_bad_sto", "有派无签:问题件:问题件(申通)");
                    } else {
                        UMShareManager.onEvent(context, "nosign_bad_sto_qrcode", "nosign_bad_sto", "有派无签:未签收:问题件(申通)");
                    }

                } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                    if ("question".equals(flag)) {
                        UMShareManager.onEvent(context, "question_bad_zt_qrcode", "question_bad_zt", "有派无签:问题件:问题件(中通)");
                    } else {
                        UMShareManager.onEvent(context, "nosign_bad_zt_qrcode", "nosign_bad_zt", "有派无签:未签收:问题件(中通)");
                    }

                }
                scanType = "问题件";
                break;
            default:
                break;
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).isChecked()) {
                select_order.add(orders.get(i));
            }
        }
        if (select_order.size() < 1) {
            UtilToolkit.showToast("请选择你要处理的单号");
            return;
        }
        final String scanTypeTemp = scanType;
        SkuaidiDialog skuaidiDialog = new SkuaidiDialog(NoSignActivity.this);
        skuaidiDialog.setTitle("温馨提示");
        skuaidiDialog.setContent("有派无签数据可能由于延时,快递公司服务器故障等导致不准,请务必确认确实是可签收的单号");
        skuaidiDialog.isUseEditText(false);
        skuaidiDialog.setPositionButtonTitle("继续操作");
        skuaidiDialog.setNegativeButtonTitle("取消");
        skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EthreeInfoScanActivity.class);
                intent.putExtra("scanType", scanTypeTemp);
                intent.putExtra("from", "NoSignActivity");
                if (select_order.size() > 50) {
                    selectedOrders = select_order;
                } else {
                    intent.putExtra("e3WayBills", (Serializable) select_order);
                }
                startActivity(intent);
                finish();
                select_order=null;
            }
        });
        skuaidiDialog.showDialog();
        skuaidiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });


    }

}
