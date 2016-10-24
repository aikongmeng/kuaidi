package com.kuaibao.skuaidi.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.bluetooth.printer.PrinterBase;
import com.kuaibao.skuaidi.bluetooth.printer.PrinterManager;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 打印电子面单
 * Created by cj on 2016/4/13.
 */
public class PrintBillActivity extends SkuaiDiBaseActivity implements View.OnClickListener{
    private static final String UPLOAD_THERMAL_PAPER = "AllThermalPaper/sendThermalPaper";//获取运单号、回填单号、录单合并接口
    private static final String UPLOAD_ORDER_SHEET = "image/uploadComm";//提交底单图片
    private static final String ORDER_PRINT = "order/orderPrint";//更新已打印，并保存电子面单凭证截图接口
    private TextView tv_sender_name, tv_sender_address, tv_sender_phone, tv_receiptor_name,
            tv_receiptor_address, tv_receiptor_phone, tv_adress_head, tv_adress_head_edit,
            tv_title_des, tv_thing_info, tv_thing_weight, tv_money, tv_deliver_num, tv_print_ticket;
    private RelativeLayout rl_delivery_num, rl_print_ticket;
    private View ll_ticket_top, fl_big_char,rl_article_info, rl_article_info_tag, rl_money_collection, rl_thing_weight;
    private Order order;
    private Context context;
    private BluetoothDevice device;
    private String num;//运单号
    private BluetoothAdapter btAdapter;
    private String expressNo;
    private String bigChar;
    private PrinterBase myPrinter;
    private boolean noPrinting = true;//没有打印任务
    private Handler handler = new Handler();
    private int time = 2000;

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                if("OK".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    UtilToolkit.showToast( "打印成功");
                    noPrinting = true;
                    tv_print_ticket.setText("再次打印");
                    if(TextUtils.isEmpty(order.getCertificatePath())) {
                        saveCopySheet();
                    }
                }else if("Printing".equals(myPrinter.getPrinterStatus())){
                    handler.postDelayed(this, time);
                }else if("NoPaper".equals(myPrinter.getPrinterStatus())) {
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "打印机缺纸", rl_print_ticket.getRootView());
                }else if("printer is disconnect".equals(myPrinter.getPrinterStatus())) {
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "未连接打印机", rl_print_ticket.getRootView());
                }else if("CoverOpened".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "打印机舱盖打开", rl_print_ticket.getRootView());
                }else if("Print Write Error".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "无法发送数据", rl_print_ticket.getRootView());
                }else if("Print Read Error".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "打印机无响应", rl_print_ticket.getRootView());
                }else if("BatteryLow".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "打印机电压低，请充电", rl_print_ticket.getRootView());
                }else if("Overheat".equals(myPrinter.getPrinterStatus())){
                    dismissProgressDialog();
                    noPrinting = true;
                    Utility.showFailDialog(context, "打印机机芯过热，请等待", rl_print_ticket.getRootView());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bill);
        context = this;
        expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        order = (Order) getIntent().getSerializableExtra("order");
        initView();
        initData();
    }


    private void initView() {
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        ll_ticket_top = findViewById(R.id.ll_ticket_top);
        rl_delivery_num = (RelativeLayout) findViewById(R.id.rl_delivery_num);
        tv_deliver_num = (TextView) findViewById(R.id.tv_deliver_num);
        tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
        tv_sender_address = (TextView) findViewById(R.id.tv_sender_address);
        tv_sender_phone = (TextView) findViewById(R.id.tv_sender_phone);
        tv_receiptor_name = (TextView) findViewById(R.id.tv_receiptor_name);
        tv_receiptor_address = (TextView) findViewById(R.id.tv_receiptor_address);
        tv_receiptor_phone = (TextView) findViewById(R.id.tv_receiptor_phone);
        tv_thing_info = (TextView) findViewById(R.id.tv_thing_info);
        tv_thing_weight = (TextView) findViewById(R.id.tv_thing_weight);
        tv_money = (TextView) findViewById(R.id.tv_money);
        rl_article_info = findViewById(R.id.rl_article_info);
        rl_article_info_tag = findViewById(R.id.rl_article_info_tag);
        rl_money_collection = findViewById(R.id.rl_money_collection);
        rl_thing_weight = findViewById(R.id.rl_thing_weight);
        fl_big_char = findViewById(R.id.fl_big_char);
        tv_adress_head = (TextView) findViewById(R.id.tv_adress_head);
        tv_adress_head_edit = (TextView) findViewById(R.id.tv_adress_head_edit);
        tv_print_ticket = (TextView) findViewById(R.id.tv_print_ticket);
        rl_print_ticket = (RelativeLayout) findViewById(R.id.rl_print_ticket);
        if("sto".equals(expressNo) && TextUtils.isEmpty(order.getArticleInfo()) && TextUtils.isEmpty(order.getCharging_weight())){
            rl_article_info.setVisibility(View.GONE);
        }else {
            if (TextUtils.isEmpty(order.getArticleInfo())) {
                rl_article_info_tag.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(order.getCharging_weight())) {
                rl_thing_weight.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(order.getCollection_amount())) {
                rl_money_collection.setVisibility(View.GONE);
            }
        }
    }

    private void initData(){
        tv_title_des.setText("打印电子面单");
        num = order.getDeliverNo();
        if(TextUtils.isEmpty(num)){
            rl_delivery_num.setVisibility(View.GONE);
        }else{
            tv_deliver_num.setText(num);
            rl_delivery_num.setVisibility(View.VISIBLE);
        }
        if("sto".equals(expressNo)){
            bigChar = getIntent().getStringExtra("bigChar");
            fl_big_char.setVisibility(View.VISIBLE);
            tv_adress_head_edit.setVisibility(View.VISIBLE);
        }else if("zt".equals(expressNo)){
            bigChar = order.getCharacters();
            rl_money_collection.setVisibility(View.VISIBLE);
        }
        if("1".equals(order.getIsPrint())){
            tv_print_ticket.setText("再次打印");
        }
        device = getIntent().getParcelableExtra("device");
        tv_sender_name.setText(order.getSenderName());
        tv_sender_address.setText(order.getSenderAddress());
        tv_sender_phone.setText(order.getSenderPhone());
        tv_receiptor_name.setText(order.getName());
        tv_receiptor_address.setText(order.getAddress());
        tv_receiptor_phone.setText(order.getPhone());
        tv_thing_info.setText(order.getArticleInfo());
        tv_thing_weight.setText(order.getCharging_weight()+"kg");
        tv_money.setText(order.getCollection_amount()+"元");
        if(TextUtils.isEmpty(bigChar)){
            fl_big_char.setVisibility(View.GONE);
        }else{
            fl_big_char.setVisibility(View.VISIBLE);
        }
        tv_adress_head.setText(bigChar);
        tv_adress_head_edit.setOnClickListener(this);
        rl_print_ticket.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(rl_print_ticket.getId() == v.getId()){
            showProgressDialog("");//PrintBillActivity.this,"正在打印", true);
            if(noPrinting){
                noPrinting = false;
                if("1".equals(order.getIs_send()) && "sto".equals(expressNo)){
                    printOrder();
                }else {
                    requestFromServer();
                }
           }
        }else if(tv_adress_head_edit.getId() == v.getId()){
            final SkuaidiDialog dialog = new SkuaidiDialog(context);
            dialog.setTitle("编辑大字");
            dialog.isUseEditText(true);
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.showEditTextTermsArea(false);
            dialog.showTermsSelect(false);
            dialog.setEditText(bigChar);
            dialog.setEditTextHint("请编辑大字");
            dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                @Override
                public void onClick(View v) {
                    tv_adress_head.setText(dialog.getEditTextContent());
                    bigChar = dialog.getEditTextContent();
                }
            });
            dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                }
            });
            dialog.showDialog();
        }
    }

    private void requestFromServer(){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", UPLOAD_THERMAL_PAPER);
            data.put("orderNumber", order.getId());
            data.put("waybillNo", order.getDeliverNo());
            boolean hasnotice = SkuaidiSpf.getHasNoticeAddressor(context);
            data.put("shipperInform", hasnotice ? "1" : "0");
            data.put("collectionAmount", order.getCollection_amount());
            data.put("chargingWeight", order.getCharging_weight());
            data.put("isSendRet", "0");
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印电子面单
     */
    private void printOrder(){
        if (null == btAdapter) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
        }

        if(Utility.isEmpty(myPrinter)){
            if(device.getName().startsWith("QR")){
                myPrinter = PrinterManager.getPrinter(device, null, null);
            }else if(device.getName().startsWith("XT")){
                myPrinter = PrinterManager.getPrinter(device, null, null);
            }else if(device.getName().startsWith("KM")){
                myPrinter = PrinterManager.getPrinter(device, null, null);
            }else if(device.getName().startsWith("L3")){
                myPrinter = PrinterManager.getPrinter(device, PrintBillActivity.this, handler);
            }else if(device.getName().startsWith("JLP")){
                myPrinter = PrinterManager.getPrinter(device, null, null);
            }
        }
        if(!Utility.isEmpty(myPrinter) && !myPrinter.isConnected()){
            myPrinter.connect(device);
        }
        if("sto".equals(expressNo)){
            myPrinter.printStoContent(num, bigChar, order);
            handler.postDelayed(runnable, time);
        }else{
            myPrinter.printZTContnet(num, bigChar, order);
            handler.postDelayed(runnable, time);
        }
        UMShareManager.onEvent(context, "print_success_qrcode", "print_success", "打印电子面单:一键打印");
    }

    public void printAsync(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                printOrder();
                return null;
            }
            protected void onPostExecute(Void result) {
                dismissProgressDialog();//PrintBillActivity.this);
            }
        }.execute();
    }

    /**
     * 保存打印底单图片
     */
    private void saveCopySheet(){
        ll_ticket_top.setDrawingCacheEnabled(true);
        ll_ticket_top.buildDrawingCache();
        Bitmap bmp = ll_ticket_top.getDrawingCache();
        if (bmp != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("sname", UPLOAD_ORDER_SHEET);
                data.put("fileStream", Utility.bitMapToString(bmp));
                data.put("path", "Certificate");
                data.put("suffix", ".jpg");
                httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  更新已打印，并保存电子面单凭证截图
     * @param path 底单路径
     */
    private void updatePrintStatus(String path){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", ORDER_PRINT);
            data.put("orderNumber", order.getId());
            data.put("path", path);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(UPLOAD_THERMAL_PAPER.equals(sname)) {
            num = obj.optString("waybillNo");
            if("zt".equals(expressNo)) {
                bigChar = obj.optString("mark");
                tv_adress_head.setText(bigChar);
            }
            tv_deliver_num.setText(num);
            order.setIs_send("1");
            if (!TextUtils.isEmpty(num)) {
                fl_big_char.setVisibility(View.VISIBLE);
                rl_delivery_num.setVisibility(View.VISIBLE);
                printOrder();
            } else {
                Utility.showFailDialog(context, "运单号为空", rl_print_ticket.getRootView());
            }
        }else if(UPLOAD_ORDER_SHEET.equals(sname) && !TextUtils.isEmpty(obj.optString("src"))){
            order.setCertificatePath(obj.optString("src"));
            updatePrintStatus(obj.optString("src"));
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        noPrinting = true;
        if("200026".equals(code)) {
            Utility.showFailDialog(context, result, rl_print_ticket.getRootView());
        }else{
            UtilToolkit.showToast(result);
        }
        if(UPLOAD_ORDER_SHEET.equals(sname)){
            updatePrintStatus("");
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    public void back(View v) {
        finish();
    }

    @Override
    public void finish() {
        Intent intent=new Intent();
        intent.putExtra("trans_num", num);
        setResult(188, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
