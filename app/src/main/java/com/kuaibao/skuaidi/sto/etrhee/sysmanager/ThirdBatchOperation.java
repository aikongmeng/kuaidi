package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wang on 2016/8/16.
 */
public class ThirdBatchOperation implements BatchOperations{
    private EthreeInfoScanAdapter adapter;
    ThirdBatchOperation(EthreeInfoScanAdapter adapter){
        this.adapter=adapter;
    }

    @Override
    public void operate() {
        EventBus.getDefault().post(adapter.getCheckedList());
    }
}
