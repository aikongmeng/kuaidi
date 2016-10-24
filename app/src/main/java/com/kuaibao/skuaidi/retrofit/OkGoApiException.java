package com.kuaibao.skuaidi.retrofit;

import com.kuaibao.skuaidi.retrofit.api.entity.Response;

/**
 * Created by lgg on 2016/10/13 13:15.
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

public class OkGoApiException extends IllegalStateException {
    private Response mSimpleResponse;

    public Response getSimpleResponse() {
        return mSimpleResponse;
    }

    public void setSimpleResponse(Response simpleResponse) {
        mSimpleResponse = simpleResponse;
    }

    public OkGoApiException() {
    }
    public OkGoApiException(String detailMessage) {
        super(detailMessage);
    }
    public OkGoApiException(String detailMessage,Response simpleResponse) {
        super(detailMessage);
        this.mSimpleResponse=simpleResponse;
    }
}
