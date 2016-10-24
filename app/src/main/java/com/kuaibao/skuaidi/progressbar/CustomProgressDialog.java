package com.kuaibao.skuaidi.progressbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * Created by ligg on 2016/4/20 14:54.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Activity context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public CustomProgressDialog(Activity context) {
        this(context, R.style.CustomProgressDialog);
    }
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.setContentView(R.layout.customprogressdialog);
        tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
        init(context);
    }
    private TextView tvMsg;

    public CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.customprogressdialog);
        tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            if(strMessage!=null && strMessage.length()>0){
                if(strMessage.length()>10){
                    tvMsg.setTextSize(12);
                }else if(strMessage.length()>8){
                    tvMsg.setTextSize(14);
                }else{
                    tvMsg.setTextSize(18);
                }
                tvMsg.setText(strMessage);
            }else{
                tvMsg.setText("加载中...");
            }
        }
        init(context);
    }

    private void init(Context context){
        Window dialogWindow = getWindow();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //lp.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.6
        lp.width = (int) (display.getWidth() * 0.6); // 宽度设置为屏幕的0.65
        lp.dimAmount=0.35f;
        lp.alpha=0.7f;
        dialogWindow.setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //        tvMsg.setBackgroundColor(Color.argb(0, 0, 255, 0));
        //        tvMsg.setTextColor(Color.argb(255, 255, 255, 255));
    }

    public void setMessage(String message){
        if (tvMsg != null) {
            if(message!=null && message.length()>0){
                if(message.length()>10){
                    tvMsg.setTextSize(12);
                }else if(message.length()>8){
                    tvMsg.setTextSize(14);
                }else{
                    tvMsg.setTextSize(18);
                }
                tvMsg.setText(message);
            }else{
                tvMsg.setText("加载中...");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }
}
