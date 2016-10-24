package com.kuaibao.skuaidi.json.entry;

/**
 * Created by gdd
 * on 2016/6/24.
 */
public class MakeCollectionDescParameter {


    /**
     * rates : 0.004
     * desc : 注：微信收款时，微信支付会收取4‰手续费
     */

    private double rates;
    private String desc;

    public double getRates() {
        return rates;
    }

    public void setRates(double rates) {
        this.rates = rates;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
