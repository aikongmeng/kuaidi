package com.kuaibao.skuaidi.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.FollowersActivity;
import com.kuaibao.skuaidi.activity.MycustomAddActivity;
import com.kuaibao.skuaidi.activity.view.MyCustomDialog;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.customer.adapter.CustomManageAdapter;
import com.kuaibao.skuaidi.customer.manager.ICustomManager;
import com.kuaibao.skuaidi.db.SkuaidiAndroidSystemDBManager;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.util.CustomUtils;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by dlfeng on 2016/8/17 10:51.
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
public class MyCustomManageActivity extends CustomManageActivity {
    private boolean single = false;
    private boolean mohuSearch = false;
    private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
    private Context context;
    public static final String ACTION_NOYIFY = "customer_notyfy";
    private CustomUtils cusUtils = new CustomUtils();
    private View  headView;
    private UserInfo mUserInfo;

    private ICustomManager iCustomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        if (!getIntent().hasExtra("loadType")) {
            getIntent().putExtra("loadType", LOAD_TYPE_ACQUIESCENCE);
        }
        getIntent().putExtra("title", "客户管理");
        mUserInfo=SkuaidiSpf.getLoginUser();
        iCustomManager=new ICustomManager();
        if(TextUtils.isEmpty(SkuaidiSpf.getCustomerLastSyncTime(mUserInfo.getUserId()))){
            iCustomManager.synchroAllCacheCustomerData();
        }else{
            iCustomManager.synchroPartCacheCustomerData();
        }

        showProgressDialog("");//MyCustomManageActivity.this,"");
        //SKuaidiApplication.getInstance().postMsg("BackUpService", "isCustomActivityDestroy", false);
        mohuSearch = getIntent().getBooleanExtra("mohuSearch", false);
        single = getIntent().getBooleanExtra("single", false);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SKuaidiApplication.getInstance().postMsg("BackUpService", "isCustomActivityDestroy", true);
        list.clear();
        /*if (customReceiver != null) {
            unregisterReceiver(customReceiver);
            customReceiver = null;
        }*/
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
            @Override
            public void updateCustomList(List<MyCustom> customs) {
                list.clear();
                list.addAll(customs);
                adapter.updateListView(list, LOAD_TYPE_ACQUIESCENCE);
            }
        }, 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void onEvent(String event){
        if(ICustomManager.SYNC_CUSTOM_FINISH.equals(event)){
            reRreshView();
            dismissProgressDialog();
        }
    }

    private void reRreshView(){
        cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
            @Override
            public void updateCustomList(List<MyCustom> customs) {
                list.clear();
                list.addAll(customs);
                adapter.updateListView(list, LOAD_TYPE_ACQUIESCENCE);
            }
        }, 1);
    }

    @Override
    protected void initListViewData() {
        cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
            @Override
            public void updateCustomList(List<MyCustom> customs) {
                list.clear();
                list.addAll(customs);
                adapter.updateListView(list, LOAD_TYPE_ACQUIESCENCE);
            }
        }, 1);
    }

    @Override
    protected View initBottomView() {
        return null;
    }

    @Override
    protected void onDatasInitFinish(CustomManageAdapter adapter) {

    }

    @Override
    protected void onItemClickListener(AdapterView<?> adapterView, View view, int position, long id) {

        UMShareManager.onEvent(context, "customer_manager_itemClick", "customer_manager", "客户管理:条目点击");
        MyCustom myCustom = adapter.getCustomList().get(position);
        String customer_id = newDB.queryCustomer(myCustom.get_index());
        myCustom.setId(customer_id);
        Intent intent = new Intent();
        if (mohuSearch) {// 如果是模糊搜索进来的就返回手机号到模糊搜索界面
            String phone = myCustom.getPhone();
            intent.putExtra("phone", phone);
            setResult(RESULT_OK, intent);
            finish();
        } else if (single) {
            String phone = myCustom.getPhone();
            if (getIntent().getStringExtra("ordernumber") != null) {
                intent.putExtra("ordernumber", getIntent().getStringExtra("ordernumber"));
            }
            intent.putExtra("phone", phone);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mycustom", myCustom);
            intent.putExtra("type", "get");
            intent.putExtras(bundle);
            intent.setClass(context, MycustomAddActivity.class);
            startActivity(intent);
        }
    }

    private void asyncContacts() {
        List<String> adressBook = SkuaidiAndroidSystemDBManager.getSysAdressBook();
        if (adressBook.size() == 0) {
            SkuaidiDialog dialog = new SkuaidiDialog(context);
            dialog.setTitle("提醒");
            dialog.setPositionButtonTitle("确定");
            dialog.setNegativeButtonTitle("取消");
            dialog.setContent("对不起，当前查询不到通讯录列表信息，请确认是否已添加联系人至手机通讯录，如果有，请信任该软件或放开获取通讯录权限后再读取通讯录");
            dialog.isUseEditText(false);
            dialog.show();
        } else {
            final List<MyCustom> contacts = new ArrayList<MyCustom>();
            for (int i = 0; i < adressBook.size(); i++) {
                MyCustom cus = new MyCustom();
                String string = adressBook.get(i);
                cus.setName(TextUtils.isEmpty(string.substring(0, adressBook.get(i).indexOf("-contact-"))) ? string.substring(adressBook.get(i).indexOf("-contact-") + 9).replaceAll(" ", "") : string.substring(0, adressBook.get(i).indexOf("-contact-")));
                String phone = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(adressBook.get(i).substring(adressBook.get(i).indexOf("-contact-") + 9).replaceAll(" ", ""));
                cus.setPhone(phone);
                cus.setTel(phone);
                contacts.add(cus);
            }
            if(!Utility.isNetworkConnected()){
                for (MyCustom myCustom : contacts) {
                    myCustom.setTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                    myCustom.set_index(newDB.queryCustomerMaxId());
                    newDB.insertCustomer(myCustom);
                }
                list.addAll(contacts);
                adapter.notifyDataSetChanged();
                UtilToolkit.showToast("添加成功");
                return;
            }
            final ApiWrapper wrapper = new ApiWrapper();
            wrapper.asyncLocalContacts("counterman.consumer.batadd", SkuaidiSpf.getLoginUser().getUserId(), JSON.toJSONString(contacts))
                    .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                        @Override
                        public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                            for (int i = 0; i < contacts.size(); i++) {
                                try {
                                    contacts.get(i).setTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                                    contacts.get(i).setId(jsonObject.getString(contacts.get(i).getPhone()));
                                    contacts.get(i).set_index(newDB.queryCustomerMaxId());
                                    newDB.insertCustomer(contacts.get(i));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            list.addAll(contacts);
                            adapter.notifyDataSetChanged();
                            UtilToolkit.showToast("添加成功");
                        }
                    }));
        }
    }

    private int position;
    private MyCustom custom;

    @Override
    protected boolean onItemLongClickListener(AdapterView<?> adapterView, View view, final int position, long id) {

        this.position = position;
        MyCustomDialog.OnMyCustomDialogListener mListener = new MyCustomDialog.OnMyCustomDialogListener() {

            @Override
            public void click(View v) {
                final MyCustom myCustom = adapter.getCustomList().get(position);
                String customer_id = newDB.selectCustomerByPhoneNum(myCustom.getPhone()).getId();
                if("".equals(customer_id)){
                    myCustom.setId(customer_id);
                }

                if (v.getId() == R.id.tv_dialog_mycustom_update) {
                    Intent intent = new Intent();
                    intent.putExtra("mycustom", myCustom);
                    intent.putExtra("type", "update");
                    intent.setClass(context, MycustomAddActivity.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.tv_dialog_mycustom_cancel) {
                    UMShareManager.onEvent(context, "customer_manager_delete", "customer_manager", "客户管理:删除客户");
                    if (!Utility.isNetworkConnected() || "".equals(myCustom.getId())) {
                        newDB.deleteSynCustomerByPhone(myCustom.getPhone());
                        for (int i = 0; i < list.size(); i++) {
                            if(list.get(i).get_index()==myCustom.get_index()){
                                list.remove(i);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        UtilToolkit.showToast("删除成功");
                    }else{
                        adapter.getCustomList().remove(position);
                        adapter.notifyDataSetChanged();
                        custom = myCustom;
                        String tel = "";
                        if(myCustom.getTags() != null && myCustom.getTags().size() > 0) {
                            tel = myCustom.getPhone();
                        }
                        final ApiWrapper wrapper=new ApiWrapper();
                        Subscription subscription = wrapper.delCustomById(myCustom.getId(), tel).subscribe(newSubscriber(new Action1<String>() {

                            @Override
                            public void call(String jsonObject) {
                                if (jsonObject != null) {
                                    newDB.deleteCustomerByPhone(custom.getPhone());
                                    list.remove(custom);
                                }
                            }

                        }));

                        mCompositeSubscription.add(subscription);
                    }
                }
            }
        };
        MyCustomDialog dialog = new MyCustomDialog(context, MyCustomDialog.DIALOG_TYPE_ACQUIESCENCE, mListener);
        dialog.show();
        return true;
    }


    @Override
    protected void addHeadView() {
        super.addHeadView();
        headView = LayoutInflater.from(context).inflate(
                R.layout.mycus_top_item, null);
        headView.findViewById(R.id.sync_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncContacts();
            }
        });
        headView.findViewById(R.id.who_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FollowersActivity.class);
                context.startActivity(intent);
            }
        });
        headView.findViewById(R.id.baned_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,
                        BanedRecordersActivity.class);
                List<MyCustom> banedList = new ArrayList<MyCustom>();
                for (int i = 0; i < adapter.getCustomList().size(); i++) {
                    MyCustom custom = adapter.getCustomList().get(i);
                    if(custom.getGroup()==MyCustom.GROUP_BANED_RECORD){
                        banedList.add(custom);
                    }
                }
                intent.putExtra("banedList", (Serializable)banedList);
                context.startActivity(intent);
            }
        });

        adapter.addHeaderView(headView);
    }

    @Override
    protected boolean isUseGuide() {
        return true;
    }


}
