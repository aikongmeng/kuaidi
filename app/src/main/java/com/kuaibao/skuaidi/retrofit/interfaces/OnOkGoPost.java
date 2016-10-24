package com.kuaibao.skuaidi.retrofit.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;

/**
 * Created by lgg on 2016/10/13 11:16.
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

public interface OnOkGoPost {
    void onBeforeRequest(BaseRequest request,String msg,boolean isUploadFile);
    void onSuccessRequest(JSONObject responseJson, Call call, okhttp3.Response response,boolean isUploadFile);
    void onErrorRequest(Call call, okhttp3.Response response, Exception e,boolean isUploadFile);
    void upProgressRequest(long currentSize, long totalSize, float progress, long networkSpeed,boolean isUploadFile);
}
