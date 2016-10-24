package com.kuaibao.skuaidi.business.entity;

/**
 * Created by lgg on 2016/8/15 18:58.
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
public class ResponseZTRules {
    private String ztRules;//中通单号规则
    private String ztRulesOrder;//中通电子面单规则

    public String getZtRules() {
        return ztRules;
    }

    public void setZtRules(String ztRules) {
        this.ztRules = ztRules;
    }

    public String getZtRulesOrder() {
        return ztRulesOrder;
    }

    public void setZtRulesOrder(String ztRulesOrder) {
        this.ztRulesOrder = ztRulesOrder;
    }

    @Override
    public String toString() {
        return "ResponseZTRules{" +
                "ztRules='" + ztRules + '\'' +
                ", ztRulesOrder='" + ztRulesOrder + '\'' +
                '}';
    }
}
