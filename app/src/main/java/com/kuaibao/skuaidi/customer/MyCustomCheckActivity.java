package com.kuaibao.skuaidi.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.customer.adapter.CustomManageAdapter;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.util.CustomUtils;

import java.util.List;

/**
 * Created by dlfeng on 2016/8/17 18:22.
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
public class MyCustomCheckActivity extends CustomManageActivity implements View.OnClickListener{

    private Context context;
    private View bottomView;
    private boolean isAllChecked = false;
    private SkuaidiNewDB newDb;
    private TextView commit,tv_all_check;
    private ImageView allCheck;
    private CustomUtils cusUtils = new CustomUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra("loadType", LOAD_TYPE_CHECKITEM);
        getIntent().putExtra("title", "选择联系人");
        context = this;
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        newDb = SkuaidiNewDB.getInstance();
    }

    @Override
    protected View initBottomView() {
        bottomView = LayoutInflater.from(context).inflate(R.layout.common_batch_check_layout, null);
        commit = (TextView) bottomView.findViewById(R.id.tv_all_check_commit);
        allCheck = (ImageView) bottomView.findViewById(R.id.iv_all_check);
        commit.setOnClickListener(this);
        allCheck.setOnClickListener(this);
        ll_bottom.setClickable(false);
        return bottomView;
    }

    @Override
    protected void onDatasInitFinish(CustomManageAdapter adapter) {
        setCheckedCount();
    }

    @Override
    protected void initListViewData() {
        cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
            @Override
            public void updateCustomList(List<MyCustom> customs) {
                list.clear();
                list.addAll(customs);
                adapter.updateListView(customs);
                setCheckedCount();
            }
        }, 3);
    }


    @Override
    protected void onItemClickListener(AdapterView<?> adapterView, View view,
                                       int position, long id) {
        if(!((MyCustom) adapter.getItem(position)).isChecked()){
            ((MyCustom) adapter.getItem(position)).setChecked(true);
            commit.setClickable(true);

        }else{
            ((MyCustom) adapter.getItem(position)).setChecked(false);
        }
        setCheckedCount();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected boolean onItemLongClickListener(AdapterView<?> adapterView,
                                              View view, int position, long id) {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_all_check_commit:
                for (int i = 0; i < adapter.getCustomList().size(); i++) {
                    if(adapter.getCustomList().get(i).isChecked()){
                        MyCustom custom = adapter.getCustomList().get(i);
                        custom.setGroup(MyCustom.GROUP_BANED_RECORD);
                        custom.setChecked(false);
                        newDb.addBanedRecorderCustomer(custom);
                    }
                }
                setResult(ACTIVITY_RESULT_OK);
                finish();
                break;
            case R.id.iv_all_check:
                if(!isAllChecked){
                    for (int i = 0; i < adapter.getCustomList().size(); i++) {
                        adapter.getCustomList().get(i).setChecked(true);
                    }
                    commit.setClickable(true);
                    allCheck.setImageResource(R.drawable.icon_ethreescan_success);
                    isAllChecked = true;
                }else{
                    for (int i = 0; i < adapter.getCustomList().size(); i++) {
                        adapter.getCustomList().get(i).setChecked(false);
                    }
                    commit.setClickable(false);
                    allCheck.setImageResource(R.drawable.icon_ethreescan_fail);
                    isAllChecked = false;
                }
                setCheckedCount();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onAdapterDataSetChanged(CustomManageAdapter adapter) {
        setCheckedCount();
    }

    @Override
    protected void onBack(View v) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < getAllCustoms().size(); i++) {
                    getAllCustoms().get(i).setChecked(false);
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBack(null);
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setCheckedCount(){
        commit.setText("确定("+getCheckedCount()+"/"+adapter.getCount()+")");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected boolean isUseGuide() {
        return false;
    }

}
