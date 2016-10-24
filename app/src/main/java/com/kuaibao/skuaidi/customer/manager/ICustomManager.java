package com.kuaibao.skuaidi.customer.manager;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaibao.skuaidi.customer.entity.ResponseAllSyncResult;
import com.kuaibao.skuaidi.customer.entity.ResponsePartSyncResult;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by kuaibao on 2016/8/31.
 * Description:    ${todo}
 */
public class ICustomManager {
    private int PAGE_NUM = 50;//每页返回数据条数
    protected CompositeSubscription mCompositeSubscription;
    public ICustomManager(){
        mCompositeSubscription = new CompositeSubscription();
    }
    public static final String SYNC_CUSTOM_FINISH="ICustomManager_finish";
    private void doAllSync(int pageSize,int page){
        final ApiWrapper wrapper=new ApiWrapper();
        Subscription mSubscription = wrapper.synchroAllCacheCustomerDataV1(pageSize,page).subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<ResponseAllSyncResult>() {
            @Override
            public void call(ResponseAllSyncResult responseCustoms) {
                int total_page=responseCustoms.getPage_total();
                int currentPage=responseCustoms.getPage_current();
                KLog.i("kb","getData size:--->"+responseCustoms.getData().size());
                if(responseCustoms.getData().size() > 0) {
                    SkuaidiNewDB.getInstance().insertCustomers_v2(responseCustoms.getData());
                }
                if(currentPage<total_page){
                    doAllSync(PAGE_NUM,currentPage+1);
                }else{
                    SkuaidiSpf.setCustomerLastSyncTime(SkuaidiSpf.getLoginUser().getUserId(), System.currentTimeMillis()+"");
                    synchroPartCacheCustomerData();
                }
            }
        }));
        mCompositeSubscription.add(mSubscription);
    }

    /*
    * 全量同步
    */
    public void synchroAllCacheCustomerData(){
        SkuaidiNewDB.getInstance().deleteAllCustomer();
        doAllSync(PAGE_NUM,1);
    }
    /*
	 * 增量同步
	 */
    public void synchroPartCacheCustomerData(){
        final UserInfo userInfo=SkuaidiSpf.getLoginUser();
        List<MyCustom> addCustomes = SkuaidiNewDB.getInstance().selectAllToSyncCustomer();
        List<MyCustom> modifyCustomes = SkuaidiNewDB.getInstance().selectAllModifyToSyncCustomer();
        List<MyCustom> delCustomes = SkuaidiNewDB.getInstance().selectAllDeletedToSyncCustomer();
        StringBuffer del_ids = new StringBuffer();
        for(int i = 0 ; i < delCustomes.size(); i++){
            del_ids.append(delCustomes.get(i).getId()+",");
        }
        if(del_ids.toString().endsWith(",")){
            del_ids.deleteCharAt(del_ids.toString().length()-1);
        }
        JSONArray addArray = JSON.parseArray(JSON.toJSONString(addCustomes, true));
        JSONArray modifyArray = JSON.parseArray(JSON.toJSONString(modifyCustomes, true));

        JSONArray changeArray=new JSONArray();
        changeArray.addAll(addArray);
        changeArray.addAll(modifyArray);

        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.synchroPartCacheCustomerDataV1(del_ids.toString(),changeArray.toJSONString(),SkuaidiSpf.getCustomerLastSyncTime(userInfo.getUserId()))
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<ResponsePartSyncResult>() {
                    @Override
                    public void call(ResponsePartSyncResult jsonObject) {
                        SkuaidiSpf.setCustomerLastSyncTime(SkuaidiSpf.getLoginUser().getUserId(), jsonObject.getLast_sync_time());
                        List<MyCustom> change=jsonObject.getChange();
                        if(change != null && change.size() > 0){
                            for(MyCustom cus: change){
                                if (SkuaidiNewDB.getInstance().isHaveCustomer(cus.getId())) {
                                    SkuaidiNewDB.getInstance().modifyCustomer(cus, 0);
                                }else{
                                    SkuaidiNewDB.getInstance().insertCustomer(cus);
                                }
                            }
                        }
                        if(!TextUtils.isEmpty(jsonObject.getDel())){
                            SkuaidiNewDB.getInstance().emptyToAddTableCache();
                            String[] dels = jsonObject.getDel().split(",");
                            for(int i = 0; i < dels.length; i++){
                                if(SkuaidiNewDB.getInstance().isHaveCustomer(dels[i])){
                                    //如果本地存在服务器返回的用户的id，则该客户是服务器端改变的，更新本地数据
                                    SkuaidiNewDB.getInstance().deleteCustomer(dels[i]);
                                }
                            }
                        }
                        onFinish();
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void onFinish(){
        EventBus.getDefault().post(SYNC_CUSTOM_FINISH);
        mCompositeSubscription.unsubscribe();
    }


}
