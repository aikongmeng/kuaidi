package com.kuaibao.skuaidi.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.MakeTelephoneNoEntry;
import com.kuaibao.skuaidi.service.YuhuOutNumSettingStategetService;
import com.kuaibao.skuaidi.service.YuhuOutNumSettingStategetService.Callback;
import com.kuaibao.skuaidi.service.YuhuOutNumSettingStategetService.MyBinder;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 云呼去电显号设置
 */

public class YunhuOutNumSettingActivity extends SkuaiDiBaseActivity implements OnClickListener, Callback {

    private final int COUNT_DOWN = 0x1001;// 倒计时
    private final int END_COUNT_DOWN = 0x1002;// 结束倒计时
    private final int ANSWER_AGAIN = 0X1003;// 再次接听
    private final String BUTTON_RESETTING = "button_resetting";// 重新设置
    private final String BUTTON_SEE_DETAIL = "button_seedetails";// 查看详情
    private final String BUTTON_ANSWER_AGAIN = "button_answer_again";// 重新接听
    private String VALID_DISPLAY_NUMBER = "ivr/validDisplayNumber2";// 去电显号设置获取状态接口
    private Context mContext;
    private Timer timer;
    private TimerTask timertask1, timerTask2;
    private View ll_step1_flag;
    private ViewGroup stepOne;//  第一部分
    private ViewGroup stepTwo;// 第二部分

    private View setting_split_line3;
    private TextView title;
    private TextView step1_currentNum;//第一部分【显示 当前去电显示号码】
    private TextView step1_open, step2_next, step1_setting_state, tv_setting3,
            step2_textFlag1;
    private ImageView setting_driver_open, icon_setting2, icon_setting3;

    private TextView new_phone;//新去电显示手机号码【显示号码控件】
    private String in_use;// 去电显示号码是否开启状态
    private String make_telephone_no;// 去电显示号码
    private String new_apply_phone;// 新设置的去电显示号码
    private String new_apply_phone_state;// 新设置的去电显示号码申请状态

    private boolean isStepTwo = false;// 是否是第二步修改号码操作
    private long second = 90;// 倒计时90秒
    private final long endTime = second * 1000;// 结束时间【毫秒】
    private String button_state;// 按钮状态及功能
    private String newInputPhoneNumber;// 修改的新手机号码
    private boolean isAnswerAgain = false;// 是否是重新接听功能
    private boolean isSetSuccess = false;//是否显示号码是否已经设置成功


    private Message message;
    private YuhuOutNumSettingStategetService mService;
    final static String SNAME_SHOWNUM_SWITCH = "ivr.updateDisplayNumberUseStatus";

    private MakeTelephoneNoEntry makeTelephoneNoEntry;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT_DOWN:
                    String buttonText = second + "秒后 重新接听";
                    SpannableStringBuilder style = new SpannableStringBuilder(buttonText);
                    if (second >= 10) {
                        style.setSpan(new ForegroundColorSpan(Color.rgb(247, 71, 57)), 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(102, 102, 102)), 2, buttonText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    } else {
                        style.setSpan(new ForegroundColorSpan(Color.rgb(247, 71, 57)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.rgb(102, 102, 102)), 1, buttonText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    step2_next.setText(style);
                    cantClick();
                    break;
                case END_COUNT_DOWN:
                    button_state = BUTTON_RESETTING;
                    step2_next.setText("重新设置");
                    canClick();
                    break;
                case ANSWER_AGAIN:
                    step2_next.setText("重新接听");
                    button_state = BUTTON_ANSWER_AGAIN;// 设置按钮功能为重新接听作用
                    isStepTwo = true;// 设置为第二步操作
                    canClick();
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
        setContentView(R.layout.yunhu_outnum_setting_layout);
        mContext = this;
        getControl();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == YunhuOutNumSettingModifyActivity.MODIFYNUMBER_SUCCESS) {
                newInputPhoneNumber = data.getStringExtra("newInputMobile");
                stepOne.setVisibility(View.GONE);
                stepTwo.setVisibility(View.VISIBLE);
                isStepTwo = true;// 设置为第二步操作
                registerService();
                startCountDown();// 开始倒计时
            }
        }
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        second = 90;
        timer = new Timer();
        timertask1 = new TimerTask() {
            @Override
            public void run() {
                second--;
                if (second > 0) {
                    message = new Message();
                    message.what = COUNT_DOWN;
                    mHandler.sendMessage(message);
                } else {
                    second = 0;
                }
            }
        };

        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                timer = null;
                endCountDown_sendmsg();
            }
        };
        timer.schedule(timertask1, 1000, 1000);// 延时1秒执行，每隔1秒执行一次
        timer.schedule(timerTask2, endTime);// 计时结束后，停止全部计时任务
    }

    /**
     * 取消倒计时
     */
    private void stopCountDown() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timertask1) {
            timertask1.cancel();
            timertask1 = null;
        }
        if (null != timerTask2) {
            timerTask2.cancel();
            timerTask2 = null;
        }
    }

    /**
     * 按钮不可点击
     **/
    private void cantClick() {
        step2_next.setBackgroundResource(R.drawable.shape_btn_gray1);
        step2_next.setClickable(false);
    }

    /**
     * 按钮可点击
     **/
    private void canClick() {
        step2_next.setClickable(true);
        step2_next.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        step2_next.setTextColor(Utility.getColor(mContext,R.color.white));
    }

    /**
     * 结束倒计时显示界面消息
     */
    private void endCountDown_sendmsg() {
        message = new Message();
        message.what = ANSWER_AGAIN;
        mHandler.sendMessage(message);
    }

    private void getControl() {
        title = (TextView) findViewById(R.id.tv_title_des);
        stepOne = (ViewGroup) findViewById(R.id.setting_step1);
        stepTwo = (ViewGroup) findViewById(R.id.setting_step2);
        ll_step1_flag = findViewById(R.id.ll_step1_flag);
        step1_currentNum = (TextView) findViewById(R.id.step1_currentNum);
        step1_open = (TextView) findViewById(R.id.step1_open);
        step1_setting_state = (TextView) findViewById(R.id.step1_setting_state);
        setting_driver_open = (ImageView) findViewById(R.id.setting_driver_open);
        icon_setting2 = (ImageView) findViewById(R.id.icon_setting2);
        icon_setting3 = (ImageView) findViewById(R.id.icon_setting3);
        tv_setting3 = (TextView) findViewById(R.id.tv_setting3);
        setting_split_line3 = findViewById(R.id.setting_split_line3);
        step2_textFlag1 = (TextView) findViewById(R.id.step2_textFlag1);
        step2_next = (TextView) findViewById(R.id.step2_next);

        new_phone = (TextView) findViewById(R.id.new_phone);// 显示新手机号码

        step1_open.setOnClickListener(this);
        step2_next.setOnClickListener(this);
        setting_driver_open.setOnClickListener(this);
    }

    private void requstData(String sname, String act) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", sname);
            if (!TextUtils.isEmpty(act)) {
                data.put("apply", act);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    private void initData() {
        title.setText("去电显示号码设置");
        makeTelephoneNoEntry = (MakeTelephoneNoEntry) getIntent().getSerializableExtra("makeTelephoneNoPhone");

        if (null != makeTelephoneNoEntry) {
            in_use = makeTelephoneNoEntry.getIn_use();
            make_telephone_no = makeTelephoneNoEntry.getMake_telephone_no();
            new_apply_phone = makeTelephoneNoEntry.getNew_apply_phone();
            new_apply_phone_state = makeTelephoneNoEntry.getNew_apply_phone_state();
        } else {
            getYunhuSettingState();
        }
        freshUI(isStepTwo);
    }

    private void getYunhuSettingState() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", VALID_DISPLAY_NUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    public void back(View v) {
//        if (stepOne.getVisibility() == View.VISIBLE) {
        finish();
        cancelStateGet();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        String phoneNum = SKuaidiApplication.getInstance().getPhoneNum();
//        if (!TextUtils.isEmpty(phoneNum) && phoneNum.equals("4009200530")) {
//            SKuaidiApplication.getInstance().setPhoneNum("");
//        }
    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        switch (v.getId()) {
            case R.id.step1_open:
                // 修改功能|重新设置功能
                mIntent = new Intent(mContext, YunhuOutNumSettingModifyActivity.class);
                startActivityForResult(mIntent, 1);
                break;
            case R.id.step2_next:
                switch (button_state){
                    case BUTTON_SEE_DETAIL:
                        freshUI(false);
                        break;
                    case BUTTON_RESETTING:
                        // 重新设置功能
                        mIntent = new Intent(mContext, YunhuOutNumSettingModifyActivity.class);
                        startActivityForResult(mIntent, 1);
                        break;
                    case BUTTON_ANSWER_AGAIN:
                        validDisplayNumber(newInputPhoneNumber);
                        isAnswerAgain = true;
                        isStepTwo = true;// 设置为第二步操作
                        startCountDown();// 开始倒计时
                        break;
                }
                break;
            case R.id.setting_driver_open:
                showProgressDialog( "");
                requstData(SNAME_SHOWNUM_SWITCH, "");
                break;
            default:
                break;
        }
    }

    boolean isBond;
    private ServiceConnection conn;

    private void registerService() {
        conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((MyBinder) service).getService();
                mService.setCallback(YunhuOutNumSettingActivity.this);
                mService.startStateGet();
            }
        };
        Intent service = new Intent(mContext, YuhuOutNumSettingStategetService.class);
        bindService(service, conn, Service.BIND_AUTO_CREATE);
        isBond = true;
    }

    /**
     * 取消服务继续调用接口功能
     */
    private void cancelStateGet() {
        if (isBond) {
            unbindService(conn);
            isBond = false;
        }
    }

    /**
     * 服务运行时保持5秒调用一次接口
     */
    @Override
    public void onServiceDoing() {
        getYunhuSettingState();
    }

    @Override
    public void onServiceOver() {
        if(!isSetSuccess) {
            UtilToolkit.showToast("设置去电显示号码功能失败");
        }
    }

    /**
     * 刷新界面
     */
    private void freshUI(boolean isStepTwo) {
        if (!isStepTwo) {// 是否是修改后的状态【第二步】
            stepOne.setVisibility(View.VISIBLE);
            stepTwo.setVisibility(View.GONE);
            if (!Utility.isEmpty(make_telephone_no)) {
                step1_currentNum.setText(make_telephone_no);
                setting_driver_open.setVisibility(View.VISIBLE);
                ll_step1_flag.setVisibility(View.GONE);
                step1_open.setText("修改");
                step1_open.setVisibility(View.VISIBLE);
                if (!Utility.isEmpty(in_use) && "y".equals(in_use)) {
                    setting_driver_open.setBackgroundResource(R.drawable.icon_push_open);
                } else {
                    setting_driver_open.setBackgroundResource(R.drawable.icon_push_close);
                }
            } else {
                step1_currentNum.setText("");
                setting_driver_open.setVisibility(View.GONE);
            }

            if (!Utility.isEmpty(new_apply_phone)) {
                ll_step1_flag.setVisibility(View.VISIBLE);
                new_phone.setText(new_apply_phone);
                step1_open.setText("重新设置");

                if (!Utility.isEmpty(new_apply_phone_state) && "apply".equals(new_apply_phone_state)) {
                    step1_open.setVisibility(View.GONE);
                    step1_setting_state.setText("处理中");
                    step1_setting_state.setTextColor(Utility.getColor(mContext,R.color.blue_colorPrimary));
                } else {
                    step1_open.setVisibility(View.VISIBLE);
                    step1_setting_state.setText("设置失败");
                    step1_setting_state.setTextColor(Utility.getColor(mContext,R.color.red));
                }
            }
        } else {
            stepOne.setVisibility(View.GONE);
            stepTwo.setVisibility(View.VISIBLE);
            if (!Utility.isEmpty(new_apply_phone) && "reject".equals(new_apply_phone_state)) {// 设置失败
                icon_setting2.setBackgroundResource(R.drawable.icon_setting1_out);
                icon_setting3.setBackgroundResource(R.drawable.icon_setting_fail);
                setting_split_line3.setVisibility(View.VISIBLE);
                step2_textFlag1.setText("设置失败!");
                tv_setting3.setTextColor(Utility.getColor(mContext,R.color.text_hint));
                button_state = BUTTON_RESETTING;
                step2_next.setText("重新设置");
                canClick();
                cancelStateGet();
                stopCountDown();
            } else if (Utility.isEmpty(new_apply_phone) && "".equals(new_apply_phone_state)) {// 设置成功
                icon_setting2.setBackgroundResource(R.drawable.icon_setting1_out);
                setting_split_line3.setVisibility(View.VISIBLE);
                icon_setting3.setBackgroundResource(R.drawable.icon_setting_success);
                step2_textFlag1.setText("设置成功!");
                isSetSuccess = true;
                tv_setting3.setTextColor(Utility.getColor(mContext,R.color.green_39b54a));
                step2_next.setText("查看详情");
                button_state = BUTTON_SEE_DETAIL;
                canClick();
                cancelStateGet();
                stopCountDown();
            } else {// 继续等待
                icon_setting2.setBackgroundResource(R.drawable.icon_setting1_in);
                setting_split_line3.setVisibility(View.INVISIBLE);
                icon_setting3.setBackgroundResource(R.drawable.icon_setting1_default);
                step2_textFlag1.setText("请保持手机通畅\n您将收到400-920-0530的来电");
                button_state = BUTTON_ANSWER_AGAIN;
            }
        }

    }

    /**
     * 修改显号接口
     *
     * @param newPhoneNumber 调用接口时传给服务器的新手机号码
     */
    private void validDisplayNumber(String newPhoneNumber) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", VALID_DISPLAY_NUMBER);
            data.put("phoneNumber", newPhoneNumber);
            data.put("apply", "y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }



    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        dismissProgressDialog();
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != result) {
            if (VALID_DISPLAY_NUMBER.equals(sname)) {// 返回去电显号开启状态
                if (isAnswerAgain){
                    isAnswerAgain = false;
                }else {
                    makeTelephoneNoEntry = new MakeTelephoneNoEntry();
                    makeTelephoneNoEntry.setMake_telephone_no(result.optString("phone"));
                    makeTelephoneNoEntry.setIn_use(result.optString("in_use"));
                    makeTelephoneNoEntry.setNew_apply_phone(result.optString("new_apply_phone"));
                    makeTelephoneNoEntry.setNew_apply_phone_state(result.optString("new_apply_sate"));

                    make_telephone_no = makeTelephoneNoEntry.getMake_telephone_no();
                    in_use = makeTelephoneNoEntry.getIn_use();
                    new_apply_phone = makeTelephoneNoEntry.getNew_apply_phone();
                    new_apply_phone_state = makeTelephoneNoEntry.getNew_apply_phone_state();
                    freshUI(isStepTwo);
                }
            } else if (SNAME_SHOWNUM_SWITCH.equals(sname)) {
                in_use = result.optString("in_use");
                if (!Utility.isEmpty(in_use) && "y".equals(in_use)) {
                    setting_driver_open.setBackgroundResource(R.drawable.icon_push_open);
                } else {
                    setting_driver_open.setBackgroundResource(R.drawable.icon_push_close);
                }
            }
        } else {
            UtilToolkit.showToast("数据获取异常");
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        UtilToolkit.showToast(result);
        dismissProgressDialog();
        if (!Utility.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
