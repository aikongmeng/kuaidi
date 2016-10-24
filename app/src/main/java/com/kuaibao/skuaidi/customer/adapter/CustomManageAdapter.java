package com.kuaibao.skuaidi.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.customer.CustomManageActivity;
import com.kuaibao.skuaidi.customer.entity.Tags;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dlf on 2016/8/17 08:45.
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
public class CustomManageAdapter extends BaseQuickAdapter<MyCustom> {

    public static final String TAG = "CustomManageAdapter";
    public static final int LOAD_TYPE_ACQUIESCENCE = 0;
    public static final int LOAD_TYPE_CHECKITEM = 1;
    public static final int LOAD_TYPE_HIDETOP = 2;
    private int loadType = LOAD_TYPE_ACQUIESCENCE;
    private Context context;

    public CustomManageAdapter(Context context, List<MyCustom> list, int loadType) {
        super(R.layout.listitem_mycustom, list);
        this.context=context;
        this.loadType = loadType;
    }
    public List<MyCustom> getCustomList(){
        return mData;
    }

    public void notifyDataChange(List<MyCustom> newData){
        mData=newData;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MyCustom myCustom) {

        if(loadType == LOAD_TYPE_CHECKITEM){
            baseViewHolder.setVisible(R.id.iv_multiselect_icon,true);
            if(!myCustom.isChecked()){
                baseViewHolder.setImageResource(R.id.iv_multiselect_icon,R.drawable.icon_ethreescan_fail);
            }
            else{
                baseViewHolder.setImageResource(R.id.iv_multiselect_icon,R.drawable.icon_ethreescan_success);
            }
            baseViewHolder.setVisible(R.id.tv_listitem_mycustom_call,false);
            baseViewHolder.setVisible(R.id.iv_listitem_mycustom_message,false);
        }
        if(loadType == LOAD_TYPE_ACQUIESCENCE){
            int intercept = 0;
            int tousu = 0;
            int pay = 0;
            List<Tags> tags = myCustom.getTags();
            if(tags != null && tags.size() > 0) {
                for (Tags tag : tags) {
                    if ("intercept".equals(tag.getType())) {
                        baseViewHolder.setVisible(R.id.iv_lan, true);
                        intercept++;
                    }
                    if ("tousu".equals(tag.getType()) || "complain".equals(tag.getType()) || "noBox".equals(tag.getType())
                            || "sign".equals(tag.getType()) || "send".equals(tag.getType())) {
                        baseViewHolder.setVisible(R.id.iv_su, true);
                        tousu++;
                    }
                    if ("pay".equals(tag.getType())) {
                        baseViewHolder.setVisible(R.id.iv_huo, true);
                        pay++;
                    }
                }
            }
            if(intercept == 0){
                baseViewHolder.setVisible(R.id.iv_lan, false);
            }
            if(tousu == 0){
                baseViewHolder.setVisible(R.id.iv_su, false);
            }
            if(pay == 0){
                baseViewHolder.setVisible(R.id.iv_huo, false);
            }
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(myCustom);
        int forSection = getPositionForSection(section);
        if(loadType == LOAD_TYPE_ACQUIESCENCE) {
            forSection++;
        }
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(baseViewHolder.getLayoutPosition()  == forSection){
            baseViewHolder.setVisible(R.id.catalog,true);
            baseViewHolder.setText(R.id.catalog,myCustom.getSortLetters());
        }else{
            baseViewHolder.setVisible(R.id.catalog,false);
        }
        baseViewHolder.setText(R.id.catalog,myCustom.getSortLetters());

        baseViewHolder.setText(R.id.tv_listitem_mycustom_name,KuaiBaoStringUtilToolkit.isEmpty(myCustom.getName())?"客户":myCustom.getName());
        baseViewHolder.setText(R.id.tv_listitem_mycustom_call,myCustom.getPhone());
        HashMap<String,String> sa=new HashMap<String, String>();
        sa.put("callerName", KuaiBaoStringUtilToolkit.isEmpty(myCustom.getName())?"":myCustom.getName());
        sa.put("phone", myCustom.getPhone());
        baseViewHolder.getView(R.id.iv_listitem_mycustom_call).setTag(sa);
        baseViewHolder.setImageResource(R.id.iv_listitem_mycustom_call,R.drawable.icon_phone);
        baseViewHolder.getView(R.id.iv_listitem_mycustom_message).setTag(myCustom.getPhone());
        baseViewHolder.setImageResource(R.id.iv_listitem_mycustom_message,R.drawable.icon_msg);

        baseViewHolder.setOnClickListener(R.id.iv_listitem_mycustom_call, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMShareManager.onEvent(context, "customer_manager_phoneCall", "customer_manager", "客户管理:打电话");
                //TODO 打电话弹出popUpWindow

                HashMap<String,String> map=(HashMap<String,String>) view.getTag();
                AcitivityTransUtil.showChooseTeleTypeDialog((CustomManageActivity)context, map.get("callerName"), map.get("phone"),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
            }
        });
        baseViewHolder.setOnClickListener(R.id.iv_listitem_mycustom_message, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMShareManager.onEvent(context, "customer_manager_SMSSend", "customer_manager", "客户管理:发短信");
                Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:"
                        + view.getTag().toString()));
                context.startActivity(intent);
            }
        });

    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(MyCustom myCustom) {
        if(null == myCustom || myCustom.getSortLetters()==null)
            return 0;
        return myCustom.getSortLetters().charAt(0);
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {


        for (int i = 0; i < mData.size(); i++) {
            String sortStr = mData.get(i).getSortLetters();
            if(!TextUtils.isEmpty(sortStr)) {
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 	根据此Adapter的特殊性重写SIDEBAR点击获取位置POSItion
     */
    public int getPositionForSection1(int section) {
        for (int i = 0; i < getItemCount()-1; i++) {
            String sortStr = mData.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i+1;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    @SuppressWarnings("unused")
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
    public void updateListView(List<MyCustom> list,int loadType){
        this.loadType = loadType;
        this.mData = list;
        notifyDataSetChanged();
    }
    public void updateListView(List<MyCustom> list){
        this.mData.clear();
        this.mData.addAll(list);
        notifyDataSetChanged();
    }

    public int getCount(){
        return mData.size();
    }

}
