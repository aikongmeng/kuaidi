package com.kuaibao.skuaidi.business.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.SkuaidiEditText;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.AddressDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.entry.AreaItem;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 中通录单界面
 * Created by cj on 2016/9/21.
 */

public class RecordOrderActivity extends RxRetrofitBaseActivity implements GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.iv_scan_icon)
    ImageView iv_scan_icon;
    @BindView(R.id.et_order_num)
    SkuaidiEditText et_order_num;
    @BindView(R.id.rl_sender_address)
    RelativeLayout rl_sender_address;
    @BindView(R.id.et_sender_name)
    SkuaidiEditText et_sender_name;
    @BindView(R.id.et_sender_phone)
    SkuaidiEditText et_sender_phone;
    @BindView(R.id.tv_sender_choose_address)
    TextView tv_sender_choose_address;
    @BindView(R.id.et_sender_address)
    SkuaidiEditText et_sender_address;
    @BindView(R.id.rl_receiver_address)
    RelativeLayout rl_receiver_address;
    @BindView(R.id.et_receiver_name)
    SkuaidiEditText et_receiver_name;
    @BindView(R.id.et_receiver_phone)
    SkuaidiEditText et_receiver_phone;
    @BindView(R.id.tv_receiver_choose_address)
    TextView tv_receiver_choose_address;
    @BindView(R.id.et_receiver_address)
    SkuaidiEditText et_receiver_address;
    @BindView(R.id.et_thing_info)
    SkuaidiEditText et_thing_info;
    @BindView(R.id.viewMasker)
    View viewMasker;
    @BindView(R.id.et_item_weight)
    SkuaidiEditText et_item_weight;
    @BindView(R.id.rl_money_amount)
    RelativeLayout rl_money_amount;
    @BindView(R.id.et_money_amount)
    SkuaidiEditText et_money_amount;
    @BindView(R.id.tv_submit_info)
    TextView tv_submit_info;

    public static final String ORDER_EDIT = "order.im.senduser.edit";
    private Order order;
    private OptionsPickerView pvOptions;
    private GeocodeSearch mgGeocodeSearch;
    private Context context;
    private boolean isChanged = false; //是否编辑过页面
    private ArrayList<AreaItem> provinceOptions = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<AreaItem>>> districtOptions = new ArrayList<>();
    private ArrayList<ArrayList<AreaItem>> cityOptions = new ArrayList<>();
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_order);
        context = this;
        order = (Order) getIntent().getSerializableExtra("recordOrder");
        if(mgGeocodeSearch==null){
            mgGeocodeSearch = new GeocodeSearch(this);
            mgGeocodeSearch.setOnGeocodeSearchListener(this);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAddressData();
            }
        }).start();
        tv_title_des.setText("录单");
        if(!TextUtils.isEmpty(order.getDeliverNo())) {
            et_order_num.setText(order.getDeliverNo());
            et_order_num.setEnabled(false);
            iv_scan_icon.setVisibility(View.GONE);
        }
        et_sender_name.setText(order.getSenderName());
        et_sender_phone.setText(order.getSenderPhone());
        if (!TextUtils.isEmpty(order.getSenderProvince()) || !TextUtils.isEmpty(order.getSenderCity()) || !TextUtils.isEmpty(order.getSenderCountry())) {
            tv_sender_choose_address.setText(order.getSenderProvince() + order.getSenderCity() + order.getSenderCountry());
        }
        et_sender_address.setText(order.getSenderDetailAddress());
        et_receiver_name.setText(order.getName());
        et_receiver_phone.setText(order.getPhone());
        if (!TextUtils.isEmpty(order.getReceiptProvince()) || !TextUtils.isEmpty(order.getReceiptCity()) || !TextUtils.isEmpty(order.getReceiptCountry())) {
            tv_receiver_choose_address.setText(order.getReceiptProvince() + order.getReceiptCity() + order.getReceiptCountry());
        }
        et_receiver_address.setText(order.getReceiptDetailAddress());
        et_thing_info.setText(order.getArticleInfo());
        et_item_weight.setText(order.getCharging_weight());
        et_money_amount.setText(order.getCollection_amount());
        if("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
            rl_money_amount.setVisibility(View.VISIBLE);
        }
        automaticPopsoftKeyboard();
        addWatcher();
    }

    @OnClick({R.id.iv_scan_icon, R.id.rl_sender_address, R.id.iv_refresh_location, R.id.rl_receiver_address,
            R.id.tv_submit_info, R.id.tv_write_auto_sender, R.id.tv_write_auto_receiver})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_scan_icon:
                // 跳转到拍照扫描界面
                Intent intent = new Intent(context, CaptureActivity.class);
                intent.putExtra("qrcodetype", Constants.TYPE_ORDER_ONE);
                if (order.getExpress_type() == 2) {
                    intent.putExtra("isSto", true);
                }
                startActivityForResult(intent, Constants.TYPE_ORDER_ONE);
                break;
            case R.id.rl_sender_address:
                hideSoftKeyboard(view);
                viewMasker.setVisibility(View.VISIBLE);
                setPickerView(rl_sender_address.getId());
                break;
            case R.id.iv_refresh_location:
                LatitudeAndLongitude lalo = SkuaidiSpf.getLatitudeOrLongitude(context);
                if(Utility.isEmpty(lalo.getLatitude()) || Utility.isEmpty(lalo.getLongitude())){
                    showNoPermissionDialog();
                    return;
                }
                showProgressDialog( "地址定位中...");
                LatLonPoint latLonPoint = new LatLonPoint(Double.parseDouble(lalo.getLatitude()), Double.parseDouble(lalo.getLongitude()));
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                mgGeocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
                break;
            case R.id.rl_receiver_address:
                hideSoftKeyboard(view);
                viewMasker.setVisibility(View.VISIBLE);
                setPickerView(rl_receiver_address.getId());
                break;
            case R.id.tv_submit_info:
                UMShareManager.onEvent(context, "order_recordOrder_update", "recordOrder_update", "业务-订单-录单-完善订单");
                if((!"省、市、区".equals(tv_sender_choose_address.getText().toString()) && TextUtils.isEmpty(order.getSenderCountry())) ||
                        (!"省、市、区".equals(tv_receiver_choose_address.getText().toString()) && TextUtils.isEmpty(order.getReceiptCountry()))) {
                    SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
                    dialog.setTitleGray("温馨提示");
                    dialog.setTitleColor(R.color.title_bg);
                    dialog.setContentGray("省市区信息不完整，请重新填写");
                    dialog.isUseMiddleBtnStyle(true);
                    dialog.setMiddleButtonTextGray("我知道了");
                    dialog.showDialogGray(et_sender_phone.getRootView());
                    return;
                }
                SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
                dialog.setTitleGray("温馨提示");
                dialog.setTitleColor(R.color.title_bg);
                dialog.setContentGray("确认要录单到中通系统吗?");
                dialog.setPositionButtonTextGray("确认");
                dialog.setNegativeButtonTextGray("取消");
                dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog( "正在录单...");
                        if (isChanged){
                            final ApiWrapper apiWrapper = new ApiWrapper();
                            apiWrapper.editOrder(ORDER_EDIT, buildOrderParams()).subscribe(newSubscriber(new Action1<JSONObject>() {
                                @Override
                                public void call(JSONObject jsonObject) {
                                    if (jsonObject.containsKey("status") && jsonObject.containsKey("desc") && "fail".equals(jsonObject.getString("status"))) {
                                        UtilToolkit.showToast(jsonObject.getString("desc"));
                                        return;
                                    }else{
                                        sendOrder();
                                    }
                                }
                            }));
                        }else {
                            sendOrder();
                        }

                    }
                });
                dialog.showDialogGray(tv_submit_info.getRootView());
                break;
            case R.id.tv_write_auto_sender:
                from = "sender";
                showDialog();
                break;
            case R.id.tv_write_auto_receiver:
                from = "receiver";
                showDialog();
                break;
        }
    }

    /**
     * 智能录入对话框
     */
    private void showDialog(){
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        dialog.setTitle("智能录入");
        dialog.isUseBigEditText(true);
        dialog.setBigEditTextHint("依次输入地址、姓名、电话，用空格隔开，地址里不要有空格，如：浙江省江山市中山路1号 张三 18616161616");
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.setDonotAutoDismiss(true);// 设置所有按钮不自动隐藏
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                String temp = dialog.getBigEditTextContent().trim();
                parseInfo(temp);
                dialog.showSoftInput(false);
                dialog.setDismiss();

            }});
        dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
            @Override
            public void onClick() {
                dialog.showSoftInput(false);
                dialog.setDismiss();
            }
        });
        dialog.showDialog();

    }

    /**
     * 智能录入拆分输入信息
     * @param str
     */
    private void parseInfo(final String str) {
        showProgressDialog("加载中...");
        final ApiWrapper apiWrapper = new ApiWrapper();
        apiWrapper.spliteInfo(str).subscribe(newSubscriber(new Action1<JSONObject>() {

            @Override
            public void call(JSONObject jsonObject) {
                if (!Utility.isEmpty(jsonObject.getString("area"))) {
                    String area = StringUtil.isEmpty(jsonObject.getString("area"));
                    String[] areas = area.split(",");
                    String province = areas[0];
                    String city = areas.length > 1 ? areas[1]:"";
                    String country = areas.length > 2 ? areas[2]:"";
                    if ("sender".equals(from)) {
                        order.setSenderProvince(province);
                        order.setSenderCity(city);
                        order.setSenderCountry(country);
                        tv_sender_choose_address.setText(area.replaceAll(",", ""));
                        et_sender_address.setText(jsonObject.getString("address"));
                        et_sender_phone.setText(jsonObject.getString("phone"));
                        et_sender_name.setText(jsonObject.getString("name"));
                    } else if ("receiver".equals(from)) {
                        order.setReceiptProvince(province);
                        order.setReceiptCity(city);
                        order.setReceiptCountry(country);
                        tv_receiver_choose_address.setText(area.replaceAll(",", ""));
                        et_receiver_address.setText(jsonObject.getString("address"));
                        et_receiver_phone.setText(jsonObject.getString("phone"));
                        et_receiver_name.setText(jsonObject.getString("name"));
                    }
                }
            }
        }));
    }

    /**
     * 录单
     */
    public void sendOrder(){
        final ApiWrapper apiWrapper = new ApiWrapper();
        boolean hasnotice = SkuaidiSpf.getHasNoticeAddressor(context);
        apiWrapper.ztSendOrder(order.getId(), hasnotice ? "1" : "0", et_order_num.getText().toString(), et_money_amount.getText().toString(), et_item_weight.getText().toString(), "1")
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        if(jsonObject.containsKey("waybillNo")){
                            UtilToolkit.showToast("录单成功！");
                            finish();
                        }else{
                            UtilToolkit.showToast("录单失败！");
                        }
                    }
                }));
    }

    private void addWatcher(){
        addTextWatcher(et_order_num);
        addTextWatcher(et_sender_name);
        addTextWatcher(et_sender_phone);
        addTextWatcher(tv_sender_choose_address);
        addTextWatcher(et_sender_address);
        addTextWatcher(et_receiver_name);
        addTextWatcher(et_receiver_phone);
        addTextWatcher(tv_receiver_choose_address);
        addTextWatcher(et_receiver_address);
        addTextWatcher(et_thing_info);
        addTextWatcher(et_item_weight);
        addTextWatcher(et_money_amount);
    }

    private void addTextWatcher(final TextView tv){
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp;
                int posDot;
                if(tv.getId() == R.id.et_item_weight) {
                    temp = s.toString();
                    posDot = temp.indexOf(".");
                    if (posDot <= 0) {
                        if (temp.matches("^\\d+$")) {
                            return;
                        } else {
                            s.clear();
                            s.append(temp.replaceAll("\\D", ""));
                            return;
                        }
                    }
                    if (temp.length() - posDot - 1 > 2) {
                        s.delete(posDot + 3, posDot + 4);
                    }
                }else if(tv.getId() == R.id.et_money_amount) {
                    temp = s.toString();
                    posDot = temp.indexOf(".");
                    if (posDot <= 0) {
                        if (temp.matches("^\\d+$")) {
                            return;
                        } else {
                            s.clear();
                            s.append(temp.replaceAll("\\D", ""));
                            return;
                        }
                    }
                    if (temp.length() - posDot - 1 > 2) {
                        s.delete(posDot + 3, posDot + 4);
                    }
                }else{
                    if(!TextUtils.isEmpty(s.toString())){
                        isChanged = true;
                    }
                    if (isCompleted()) {
                        tv_submit_info.setEnabled(true);
                        tv_submit_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                    } else {
                        tv_submit_info.setEnabled(false);
                        tv_submit_info.setBackgroundResource(R.drawable.shape_btn_gray1);

                    }
                }
            }
        });

    }


    private void setPickerView(final int viewID) {
        // 选项选择器
        pvOptions = new OptionsPickerView(this);
        // 三级联动效果
        pvOptions.setPicker(provinceOptions, cityOptions, districtOptions, true);
        pvOptions.setCyclic(false, false, false);
        // 设置默认选中的item
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                AreaItem a = provinceOptions.get(options1);
                AreaItem b = cityOptions.get(options1).get(option2);
                AreaItem c = districtOptions.get(options1).get(option2).get(options3);
                String curPro = a.getName();
                String curCity = b.getName();
                String curCountry = c.getName();
                if (viewID == rl_sender_address.getId()) {
                    tv_sender_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
                    tv_sender_choose_address.setText(curPro + curCity + curCountry);
                    order.setSenderProvince(curPro);
                    order.setSenderCity(curCity);
                    order.setSenderCountry(curCountry);
                } else if (viewID == rl_receiver_address.getId()) {
                    tv_receiver_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
                    tv_receiver_choose_address.setText(curPro + curCity + curCountry);
                    order.setReceiptProvince(curPro);
                    order.setReceiptCity(curCity);
                    order.setReceiptCountry(curCountry);
                }
                viewMasker.setVisibility(View.GONE);
            }

        });
        pvOptions.show();
    }

    /**
     * 初始化省市区信息
     */
    private void initAddressData(){
        List<AreaItem> pros = AddressDB.getAllProInfoStrs();
        for (int i = 0; i < pros.size(); i++) {
            provinceOptions.add(pros.get(i));
            ArrayList<AreaItem> citys = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(pros.get(i).getId());
            cityOptions.add(citys);
            ArrayList<ArrayList<AreaItem>> tempList=new ArrayList<>();
            for (int j = 0; j < citys.size(); j++) {
                ArrayList<AreaItem> mDistricts = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(citys.get(j).getId());
                tempList.add(mDistricts);
            }
            districtOptions.add(tempList);
        }
    }

    /**
     * 自动弹出软键盘
     */
    private void automaticPopsoftKeyboard() {
        et_sender_name.setFocusable(true);
        et_sender_name.requestFocus();
        et_sender_name.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) et_sender_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_sender_name, 0);
            }
        },500);
    }

    /**
     * 隐藏软键盘
     *
     * @param
     */
    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showNoPermissionDialog(){
        SkuaidiDialog skuaidiDialog = new SkuaidiDialog(RecordOrderActivity.this);
        skuaidiDialog.setTitle("提示");
        skuaidiDialog.setContent("地址信息获取失败，可能是定位权限未打开。请到手机的设置-应用-快递员-权限管理-定位-设为允许");
        skuaidiDialog.isUseEditText(false);
        skuaidiDialog.setPositionButtonTitle("去设置");
        skuaidiDialog.setNegativeButtonTitle("取消");
        skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                SKuaidiApplication.getInstance().exit();
            }
        });
        skuaidiDialog.showDialog();
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TYPE_ORDER_ONE && resultCode == Constants.TYPE_ORDER_ONE) {
            String transNum = data.getStringExtra("decodestr");
            order.setDeliverNo(transNum);
            et_order_num.setText(transNum);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        dismissProgressDialog();
        RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
        if(!Utility.isEmpty(address)) {
            order.setSenderProvince(address.getProvince());
            String city = TextUtils.isEmpty(address.getCity())?address.getProvince():address.getCity();
            order.setSenderCity(city);
            order.setSenderCountry(address.getDistrict());
            tv_sender_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
            tv_sender_choose_address.setText(address.getProvince() + city + address.getDistrict());
            int index = address.getFormatAddress().indexOf(address.getDistrict())+address.getDistrict().length();
            et_sender_address.setText(address.getFormatAddress().substring(index));
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        dismissProgressDialog();
    }

        public Map<String,String> buildOrderParams(){
            Map<String,String> params=new HashMap<>();
            params.put("order_number", order.getId());
            params.put("senderName", et_sender_name.getText().toString());
            params.put("senderPhone", et_sender_phone.getText().toString());
            params.put("senderAddress", et_sender_address.getText().toString());
            params.put("send_address_province", order.getSenderProvince());
            params.put("send_address_city", order.getSenderCity());
            params.put("send_address_county", order.getSenderCountry());
            params.put("name", et_receiver_name.getText().toString());
            params.put("phone", et_receiver_phone.getText().toString());
            params.put("address", et_receiver_address.getText().toString());
            params.put("addressHead", order.getReceiptProvince()+order.getReceiptCity()+order.getReceiptCountry());
            params.put("receive_address_province", order.getReceiptProvince());
            params.put("receive_address_city", order.getReceiptCity());
            params.put("receive_address_county", order.getReceiptCountry());
            params.put("articleInfo", et_thing_info.getText().toString());
            params.put("charging_weight", et_item_weight.getText().toString());
            params.put("collection_amount", et_money_amount.getText().toString());
            return params;
        }

    /**
     * 判断内容是否填写完整
     */
    private boolean isCompleted() {
        return !(TextUtils.isEmpty(et_order_num.getText().toString())
                || TextUtils.isEmpty(et_sender_name.getText().toString())
                || TextUtils.isEmpty(et_sender_phone.getText().toString())
                || TextUtils.isEmpty(et_sender_address.getText().toString())
                || TextUtils.isEmpty(et_receiver_name.getText().toString())
                || TextUtils.isEmpty(et_receiver_phone.getText().toString().trim())
                || TextUtils.isEmpty(et_receiver_address.getText())
                || "省、市、区".equals(tv_sender_choose_address.getText().toString())
                || "省、市、区".equals(tv_receiver_choose_address.getText().toString()));

    }
}
