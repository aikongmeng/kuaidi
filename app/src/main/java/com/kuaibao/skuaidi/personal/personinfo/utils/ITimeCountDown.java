package com.kuaibao.skuaidi.personal.personinfo.utils;

import android.os.CountDownTimer;

/**
 * Created by lgg on 2016/8/29 13:53.
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
public class ITimeCountDown extends CountDownTimer {
    private ITimerCallBack mITimerCallBack;

    public ITimerCallBack getITimerCallBack() {
        return mITimerCallBack;
    }

    public void setITimerCallBack(ITimerCallBack ITimerCallBack) {
        mITimerCallBack = ITimerCallBack;
    }

    public ITimeCountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if(mITimerCallBack!=null){
            mITimerCallBack.onTimeCountDown(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if(mITimerCallBack!=null){
            mITimerCallBack.onTimerFinish();
        }
    }

    public interface ITimerCallBack{
        void onTimeCountDown(long mills);
        void onTimerFinish();
    }
}
