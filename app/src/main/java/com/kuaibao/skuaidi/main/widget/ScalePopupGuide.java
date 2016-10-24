package com.kuaibao.skuaidi.main.widget;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.main.PermissionGuideActivity;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.kuaibao.skuaidi.service.RomUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

/**
 * Created by lgg on 2016/10/21 13:35.
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
 * #                     no bug forever                #
 * #                                                   #
 */

public class ScalePopupGuide extends BasePopupWindow implements View.OnClickListener{

    private View popupView;
    private boolean isBelowApi21;

    public boolean isBelowApi21() {
        return isBelowApi21;
    }

    public ScalePopupGuide(Activity context, boolean isBelowApi21) {
        super(context);
        this.isBelowApi21=isBelowApi21;
        bindEvent();
    }

    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected View getClickToDismissView() {
        return null;
    }

    @Override
    public View getPopupView() {
        popupView= LayoutInflater.from(mContext).inflate(R.layout.layout_pop_guide_permission,null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView!=null){
            popupView.findViewById(R.id.iv_click_goto_set).setOnClickListener(this);
            popupView.findViewById(R.id.rl_guide_permission_parent).setOnClickListener(this);
            CheckBox checkBox=(CheckBox) popupView.findViewById(R.id.select_no_warn_again);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isBelowApi21){
                                    SkuaidiSpf.setBelowApi21NeedShowPermissionDialog(!isChecked);
                                }else{
                                    SkuaidiSpf.setNeedShowPermissionDialog(!isChecked);
                                }
                            }
                        });
            checkBox.setOnClickListener(this);
            popupView.findViewById(R.id.tv_show_permisson_guide).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_click_goto_set:
                dismiss();
                RomUtils.goToApplicationDetail(mContext);
                break;
            case R.id.tv_show_permisson_guide:
                dismiss();
                Intent intent=new Intent(mContext, PermissionGuideActivity.class);
                mContext.startActivityForResult(intent,PermissionGuideActivity.REQUEST_GO_TO_SETTING);
                break;
            case R.id.select_no_warn_again:
                dismiss();
                break;
            case R.id.rl_guide_permission_parent:
                dismiss();
                break;
            default:
                break;
        }
    }
}
