package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class SendMsgMenuDialog implements OnClickListener {

    private Context mContext = null;
    private Display display = null;
    private Dialog dialog = null;
    private View view = null;

    private RelativeLayout rl_gunScan = null;// 巴枪扫描 区域
    private TextView gunScanHint = null;// 短信发送成功自动巴枪上传提示
    private View line1, line2;// 线条1
    private ImageView cb_gun_scan = null;// 巴枪扫描checkbox图片
    private TextView tv_sendTime_tag = null;// 显示发送短信时间说明

    private onClickListener onclickListener;

    public SendMsgMenuDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SendMsgMenuDialog builder(boolean hasProblemOrder) {
        // 获取Dialog布局
        view = LayoutInflater.from(mContext).inflate(R.layout.send_msg_activity_menu, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        initView(hasProblemOrder);

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.START | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }


    private void initView(boolean hasProblemOrder) {
        ImageView ivCloseMenu = (ImageView) view.findViewById(R.id.ivCloseMenu);// 关闭按钮
        rl_gunScan = (RelativeLayout) view.findViewById(R.id.rl_gunScan);
        gunScanHint = (TextView) view.findViewById(R.id.gunScanHint);
        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        View view1 = view.findViewById(R.id.view1);
        cb_gun_scan = (ImageView) view.findViewById(R.id.cb_gun_scan);
        ImageView iv_doubt = (ImageView) view.findViewById(R.id.iv_doubt);// 巴枪扫描说明按钮
        RelativeLayout rl_sendTime = (RelativeLayout) view.findViewById(R.id.rl_sendTime);// 定时发短信按钮
        tv_sendTime_tag = (TextView) view.findViewById(R.id.tv_sendTime_tag);// 显示发送短信时间说明
        RelativeLayout rl_sendCloudCall = (RelativeLayout) view.findViewById(R.id.rl_sendCloudCall);// 一键云呼昨日未签收手机号
        RelativeLayout rl_sendMsgCall = (RelativeLayout) view.findViewById(R.id.rl_sendMsg);// 给昨日未取件手机号发短信
        RelativeLayout rl_importPhoneNumber = (RelativeLayout) view.findViewById(R.id.rl_importPhoneNumber);// 批量导入客户手机号
        RelativeLayout setting_password = (RelativeLayout) view.findViewById(R.id.setting_password);// 取件密码设置
        ViewGroup SendFailAutoCloudCall = (ViewGroup) view.findViewById(R.id.SendFailAutoCloudCall);// 发送短信失败自动发送一条云呼

        ivCloseMenu.setOnClickListener(this);
        cb_gun_scan.setOnClickListener(this);
        iv_doubt.setOnClickListener(this);
        rl_sendTime.setOnClickListener(this);
        rl_sendCloudCall.setOnClickListener(this);
        rl_sendMsgCall.setOnClickListener(this);
        rl_importPhoneNumber.setOnClickListener(this);
        SendFailAutoCloudCall.setOnClickListener(this);
        setting_password.setOnClickListener(this);

        if (!SkuaidiSpf.getGunScanStatus(mContext)) {
            cb_gun_scan.setBackgroundResource(R.drawable.icon_push_close);
        } else {
            cb_gun_scan.setBackgroundResource(R.drawable.icon_push_open);
        }

        if (!SkuaidiSpf.getTimeSendMsg(mContext).isTimeSendCheckBoxIsSelect()) {
            tv_sendTime_tag.setText("");
        } else {
            tv_sendTime_tag.setText(SkuaidiSpf.getTimeSendMsg(mContext).getTimeSendTimeString());
        }

        if (hasProblemOrder) {// 如果是问题单号进来
            rl_gunScan.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            gunScanHint.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);

        } else {
            rl_gunScan.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            gunScanHint.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }
        if (!SkuaidiSpf.getLoginUser().getExpressNo().equals("sto")) {
            rl_gunScan.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            gunScanHint.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
    }

    public SendMsgMenuDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public SendMsgMenuDialog addClickListener(onClickListener onclickListener) {
        this.onclickListener = onclickListener;
        return this;
    }

    public void show(boolean hasProblemOrder) {
        if (hasProblemOrder) {// 如果是问题单号进来
            rl_gunScan.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            gunScanHint.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        } else {
            rl_gunScan.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            gunScanHint.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }

    public interface onClickListener {
        /**
         * 同时做巴枪扫描
         **/
        void gunScan(ImageView imgView);

        /**
         * 巴枪扫描说明
         **/
        void gunScanDesc();

        /**
         * 定时发送短信
         **/
        void timeSendMsg(TextView textView);

        /**
         * 一键云呼昨日未签收手机号
         **/
        void sendCloudCall();

        /**
         * 给昨日未取件手机号发短信
         **/
        void sendMsg();

        /**
         * 批量导入客户手机号
         **/
        void importPhoneNumber();

        /**
         * 发送短信失败自动发送云呼功能
         **/
        void autoCloudCall(View view);

        /**
         * 取件密码设置
         **/
        void settingPassword();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_password:
                onclickListener.settingPassword();
                dialog.dismiss();
                break;
            case R.id.SendFailAutoCloudCall:// 发送短信失败自动发送一条云呼按钮
                onclickListener.autoCloudCall(v);
                dialog.dismiss();
                break;
            case R.id.ivCloseMenu:
                dialog.dismiss();
                break;
            case R.id.cb_gun_scan:
                onclickListener.gunScan(cb_gun_scan);
                break;
            case R.id.iv_doubt:
                onclickListener.gunScanDesc();
                dialog.dismiss();
                break;
            case R.id.rl_sendTime:
                onclickListener.timeSendMsg(tv_sendTime_tag);
                dialog.dismiss();
                break;
            case R.id.rl_sendCloudCall:
                onclickListener.sendCloudCall();
                dialog.dismiss();
                break;
            case R.id.rl_sendMsg:
                onclickListener.sendMsg();
                dialog.dismiss();
                break;
            case R.id.rl_importPhoneNumber:
                onclickListener.importPhoneNumber();
                dialog.dismiss();
                break;
        }

    }


}
