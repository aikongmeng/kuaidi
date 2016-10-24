package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ProvinceBean;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.feng.skin.manager.base.BaseActivity;

public class OldRecordsActivity extends BaseActivity {

    public static final String OLD_RECORDS_NAME = "old_records_name";
    public static final String SMS_OLD_RECORDS = "短信";
    public static final String YUNHU_OLD_RECORDS = "云呼";
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.tv_count_record_des)
    TextView mTvCountRecordDes;
    @BindView(R.id.tv_records_starttime)
    TextView tv_starttime;
    @BindView(R.id.tv_records_etphone)
    EditText user_phone;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ProvinceBean> options3Items = new ArrayList<>();

//    private List<String> option1 = new ArrayList<>();
//    private List<List<String>> option2 = new ArrayList<>();
//    private List<String> option3 = new ArrayList<>();

    private OptionsPickerView pvOptions;
    private Calendar calendar;
    private int year;
    private int month;

    private String record_type = "";
    @BindView(R.id.bt_chaxun)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_records);
        ButterKnife.bind(OldRecordsActivity.this);
        record_type = getIntent().getStringExtra(OldRecordsActivity.OLD_RECORDS_NAME);
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateButtonStatus(){
        btnLogin.setEnabled(canLogin());
    }

    private boolean canLogin(){
        if(TextUtils.isEmpty(tv_starttime.getText().toString().trim()) ||
                TextUtils.isEmpty(user_phone.getText().toString().trim())){
            return false;
        }
        return true;
    }

    private void setTimeOptions(){
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        year = calendar.get(Calendar.YEAR);
//        month = calendar.get(Calendar.MONTH);

//        option1.clear();
//        option2.clear();
//        option3.clear();
//
//        // 选项一
//        option1.add(String.valueOf(year-1));
//        option1.add(String.valueOf(year));
//        // 选项二









        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;

        options1Items.clear();
        options2Items.clear();
        options3Items.clear();
        // 选项一
        options1Items.add(new ProvinceBean(0,(year-1)+"年","",""));
        options1Items.add(new ProvinceBean(1,(year)+"年","",""));
        // 选项二
        ArrayList<String> option2_01 = new ArrayList<>();
        ArrayList<String> option2_02 = new ArrayList<>();
        for (int i =month;i<=12;i++){
            option2_01.add(i+"月");
        }
        for (int i = 1;i<=month;i++){
            option2_02.add(i+"月");
        }
        options2Items.add(option2_01);
        options2Items.add(option2_02);

//        for (int i = 0;i<12;i++){
//            options2Items.add(new ProvinceBean(i,i+1+"月","",""));
//        }
        // 选项三
        options3Items.add(new ProvinceBean(0,"上半月","",""));
        options3Items.add(new ProvinceBean(1,"下半月","",""));
        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, options3Items);
        //设置选择的三级单位
//        pwOptions.setLabels("年", "月", "日");
        pvOptions.setTitle("");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(1, 0, 0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                //返回的分别是三个级别的选中位置
                String year = options1Items.get(options1).getPickerViewText().substring(0,options1Items.get(options1).getPickerViewText().indexOf("年"));
                String month = options2Items.get(options1).get(options2).substring(0,options2Items.get(options1).get(options2).indexOf("月"));
                if (month.length()<2){
                    month = "0"+month;
                }
                String halfOfDay = options3Items.get(options3).getPickerViewText();
                tv_starttime.setText(year+"-"+month+"-"+halfOfDay);
            }
        });
    }

    private void setData() {
        mTvTitleDes.setText("历史记录");
//        mTvCountRecordDes.setText(String.format(getResources().getString(R.string.old_record_des),this.record_type));
        mTvCountRecordDes.setText("可查一年内记录（不含近两周）以半个月为条件查询");

        pvOptions = new OptionsPickerView(this);
        // 时间
        calendar = Calendar.getInstance();

        user_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                    onBtnClick();
                    return true;
                }
                return false;
            }
        });
        user_phone.addTextChangedListener(myTextWatcher);

        updateButtonStatus();
    }
    private  final TextWatcher myTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            updateButtonStatus();
        }
    };


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @OnClick(R.id.rl_records_starttime)
    public void clickStartTime(){
        setTimeOptions();
        if(!pvOptions.isShowing())
            pvOptions.show();
    }

    @OnClick(R.id.bt_chaxun)
    public void onBtnClick(){
        String startTime=tv_starttime.getText().toString();
        String userPhone=user_phone.getText().toString();


        if(TextUtils.isEmpty(startTime) || TextUtils.isEmpty(userPhone)){
            UtilToolkit.showToast("请填完整查询条件");
            return;
        }
//        if (!canNext(startTime,stopTime)){
//            UtilToolkit.showToast("请选择相同月份前半月或后半月日期");
//            return ;
//        }

        Intent intent;
        if (!TextUtils.isEmpty(record_type) && record_type.equals(SMS_OLD_RECORDS)){
            intent=new Intent(this,OldRecordsListActivity.class);
        }else{
            intent = new Intent(this,HistoryCloudCallListActivity.class);
        }
        intent.putExtra(OldRecordsListActivity.START_TIME,startTime);
        intent.putExtra(OldRecordsListActivity.USR_PHONE,userPhone);
        startActivity(intent);
    }

    private boolean canNext(String startTime,String endTime){
        int s_month = Integer.parseInt(startTime.substring(startTime.indexOf("-")+1,startTime.lastIndexOf("-")));
        int e_month = Integer.parseInt(endTime.substring(endTime.indexOf("-")+1,endTime.lastIndexOf("-")));
        int s_day = Integer.parseInt(startTime.substring(startTime.lastIndexOf("-")+1));
        int e_day = Integer.parseInt(endTime.substring(endTime.lastIndexOf("-")+1));
        if (s_month != e_month){// 判断两个日期是否都是相同月
            return false;
        }
        if (s_day<=15){
            return e_day >= s_day && e_day <= 15;
        }else{
            return e_day >= s_day;
        }
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }
}
