package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FindExpressAdapter;
import com.kuaibao.skuaidi.activity.adapter.FindExpressHistoryAdapter;
import com.kuaibao.skuaidi.activity.view.CustomDialog;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.ExpressHistory;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 罗娜 查快递
 */
public class FindExpressActivity extends RxRetrofitBaseActivity {
    private static final String ACCEPTED = "0123456789-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    Context context;

    @BindView(R.id.lv_findexpress_history)
    ListView lv;
    @BindView(R.id.iv_delete_ordernum)
    ImageView iv_delete_ordernum;
    @BindView(R.id.iv_get_qrcode)
    ImageView iv_qrcode;
    @BindView(R.id.et_order_number)
    EditText et_orderNum;
    @BindView(R.id.bt_get_qrcode)
    Button bt_qrcode;
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.input_notice)
    TextView input_notice;


//    private boolean moreScan = false;
    private FindExpressHistoryAdapter adapter;
    private FindExpressAdapter mAdapter;
    private List<NotifyInfo> ConScans;
    private SkuaidiDB skuaidiDb;
    private List<ExpressHistory> expressHistories;
    // 当如后的提示文本框
    private String expressFirmName, express, decodestr;
    // edittext的值
    CharSequence text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.findexpress);

        context = this;
        ButterKnife.bind(this);
        // 本地数据库
        skuaidiDb = SkuaidiDB.getInstanse(context);
        expressHistories = new ArrayList<>();
        ConScans = new ArrayList<>();
        getControl();
        setData();
        setListener();

    }

    private void setListener() {
        /**
         * Listview 长按事件
         */
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

//                if (!moreScan) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(FindExpressActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("是否确定删除该条记录?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            skuaidiDb.deleteExpressHistory(expressHistories.get(arg2).getDeliverNo());
                            if (skuaidiDb.getExpressHistory() != null) {
                                expressHistories.clear();
                                expressHistories.addAll(skuaidiDb.getExpressHistory());
                                adapter.notifyDataSetChanged();
                            } else {
                                expressHistories.clear();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
//                }
                return true;
            }

        });
        /**
         * ListView点击事件 跳转到 FindExpressResultActivity
         */
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (Utility.isNetworkConnected()) {
                    Intent intent = new Intent(context,CopyOfFindExpressResultActivity.class);
                    intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
                    intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
//                    if (moreScan) {
//                        intent.putExtra("order_number", ConScans.get(arg2).getExpress_number());
//                        intent.putExtra("moreScan", moreScan);
//                    } else {
                        intent.putExtra("order_number", expressHistories.get(arg2).getDeliverNo());
//                    }
                    startActivity(intent);
                } else {
                    UtilToolkit.showToast("当前网络未连接，请联网后再重试！");
                }
            }

        });

        et_orderNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_orderNum.getText().toString().equals("")) {
                    iv_delete_ordernum.setVisibility(View.GONE);
                    bt_qrcode.setVisibility(View.GONE);
                    iv_qrcode.setVisibility(View.VISIBLE);
                } else {

                    iv_delete_ordernum.setVisibility(View.VISIBLE);
                    bt_qrcode.setVisibility(View.VISIBLE);
                    iv_qrcode.setVisibility(View.GONE);

                }
            }
        });
    }

    private void getControl() {
//        moreScan = getIntent().getBooleanExtra("moreScan", false);


        if ("yt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            // 圆通默认弹出字母键盘
            et_orderNum.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        } else {
            et_orderNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        et_orderNum.setKeyListener(DigitsKeyListener.getInstance(ACCEPTED));

        tv_title_des.setText("查快递");
        bt_qrcode.setBackgroundResource(SkuaidiSkinManager.getSkinResId("big_button_selector"));
        text = et_orderNum.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        if (!et_orderNum.getText().toString().equals("")) {
            iv_delete_ordernum.setVisibility(View.VISIBLE);
        }
        UserInfo info = SkuaidiSpf.getLoginUser();
        if (info.getExpressNo().equals("sto")) {
            // 监听文本发生改变
            et_orderNum.addTextChangedListener(textwatchlitener);
        }
    }

    /**
     * 输入单号智能匹配
     */
    private TextWatcher textwatchlitener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            String et_text = et_orderNum.getText().toString();
            // 判断是否是申通公司的单号

            if (et_text.equals("")) {
                input_notice.setVisibility(View.GONE);
            }
            if (!isNumeric(et_text) && !et_text.equals("S") && !et_text.equals("ST") && !et_text.equals("STO")) {
                input_notice.setVisibility(View.VISIBLE);
                input_notice.setText("可能不是申通单号！");
            } else if (et_text.length() > 13) {
                input_notice.setVisibility(View.VISIBLE);
                input_notice.setText("可能不是申通单号！");
            } else if (et_text.length() > 3 && et_text.length() <= 13) {
                String two = et_text.substring(0, 2);
                String three = et_text.substring(0, 3);
                if (et_text.length() <= 12
                        && (three.equals("268") || three.equals("368") || three.equals("468") || three.equals("568")
                        || three.equals("668") || three.equals("768") || three.equals("868")
                        || three.equals("968") || three.equals("588") || three.equals("688")
                        || three.equals("888") || three.equals("900") || three.equals("STO")
                        || three.equals("968") || three.equals("290") || two.equals("11") || two.equals("22"))) {
                    input_notice.setVisibility(View.GONE);

                } else if (et_text.length() <= 13 && two.equals("33")) {
                    input_notice.setVisibility(View.GONE);
                } else {
                    input_notice.setVisibility(View.VISIBLE);
                    input_notice.setText("可能不是申通单号！");
                }
            }

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };

    @OnClick({R.id.iv_title_back, R.id.bt_get_qrcode,R.id.iv_get_qrcode,R.id.iv_delete_ordernum})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                SkuaidiSpf.saveOrderNum(context, et_orderNum.getText().toString());
                finish();
                break;
            case R.id.bt_get_qrcode:// 确定按钮-开始查找指定快递
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("网络连接错误,请稍后重试!");
                } else {
                    expressFirmName = SkuaidiSpf.getLoginUser().getExpressFirm();
                    express = SkuaidiSpf.getLoginUser().getExpressNo();
                    final String orderNumber = et_orderNum.getText().toString().replaceAll(" ", "");
                    et_orderNum.setText(orderNumber);
                    if (orderNumber.equals("")) {
                        // 运单号未填写
                        UtilToolkit.showToast("请输入运单号!");
                    } else if (!expressFirmName.equals("请选择快递公司") && !orderNumber.equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("expressfirmName", expressFirmName);
                        intent.putExtra("express_no", express);
                        intent.putExtra("order_number", orderNumber);
                        intent.setClass(context, CopyOfFindExpressResultActivity.class);
                        startActivity(intent);
                        et_orderNum.setText("");
                    }
                }
                break;
            case R.id.iv_get_qrcode:// 扫条码图标
                Intent intent = new Intent(context, CaptureActivity.class);
                intent.putExtra("qrcodetype", Constants.TYPE_FIND_EXPRESS);
                startActivityForResult(intent, Constants.REQUEST_QRCODE);
                break;
            case R.id.iv_delete_ordernum:// 删除输入的单号
                et_orderNum.setText("");
                input_notice.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 判断单号输入是否为数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QRCODE && resultCode == Constants.TYPE_FIND_EXPRESS) {
            decodestr = data.getStringExtra("decodestr");
            //System.out.println("扫描单号：" + decodestr);
            et_orderNum.setText(decodestr);
        }
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

    @Override
    protected void onRestart() {
        super.onRestart();
        // 扫描界面传过来的数据
//        if (moreScan) {
//            List<NotifyInfo> list = (List<NotifyInfo>) getIntent().getSerializableExtra("express_list");
//
//            if (list != null) {
//                for (int i = 0; i < list.size(); i++) {
//
//                    ConScans.add(list.get(i));
//
//                }
//            }
//
//            mAdapter = new FindExpressAdapter(context, ConScans);
//            lv.setAdapter(mAdapter);
//            // 数据库拿到的数据
//        } else
        if (skuaidiDb.getExpressHistory() != null) {
            expressHistories.clear();
            expressHistories.addAll(skuaidiDb.getExpressHistory());
            adapter = new FindExpressHistoryAdapter(context, expressHistories);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    // 加载数据
    public void setData() {
//        if (moreScan) {
//            List<NotifyInfo> list = (List<NotifyInfo>) getIntent().getSerializableExtra("express_list");
//
//            if (list != null) {
//                for (int i = 0; i < list.size(); i++) {
//                    ConScans.add(list.get(i));
//
//                }
//            }
//            mAdapter = new FindExpressAdapter(context, ConScans);
//            lv.setAdapter(mAdapter);
//            // 数据库拿到的数据
//        } else
        if (skuaidiDb.getExpressHistory() != null) {
            expressHistories.clear();
            expressHistories.addAll(skuaidiDb.getExpressHistory());
            adapter = new FindExpressHistoryAdapter(context, expressHistories);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
