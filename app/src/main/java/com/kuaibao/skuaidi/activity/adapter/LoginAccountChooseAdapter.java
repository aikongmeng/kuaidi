package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;

import java.util.List;

import gen.greendao.bean.LoginUserAccount;

/**
 * Created by ligg on 2016/6/1 14:52.
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
public class LoginAccountChooseAdapter extends BaseQuickAdapter<LoginUserAccount> {

    private AccountDelete mAccountDelete;
    private Context context;
    public AccountDelete getAccountDelete() {
        return mAccountDelete;
    }

    public void setAccountDelete(AccountDelete accountDelete) {
        mAccountDelete = accountDelete;
    }

    public LoginAccountChooseAdapter(Context context, List<LoginUserAccount> list) {
        super(R.layout.listitem_user_account_choose, list);
        this.context=context;
    }

    public void notifyDataChange(List<LoginUserAccount> newData){
        mData=newData;
        notifyDataSetChanged();
    }
    @Override
    protected void convert(BaseViewHolder helper, final LoginUserAccount item) {
        GlideUtil.GlideHeaderImg(this.context,item.getUserId(),(ImageView) helper.getView(R.id.iv_user_headimg),R.drawable.ic_loading,R.drawable.icon_yonghu);
//        GlideUtil.GlideCircleImg(this.context, item.getHeadImgUrl(),
//                (ImageView) helper.getView(R.id.iv_user_headimg),
//                R.drawable.geng_icon_touxiang,
//                R.drawable.geng_icon_touxiang);
        helper.setText(R.id.tv_account_phonenumber, item.getPhoneNumber());
        helper.setOnClickListener(R.id.iv_delete_login_account, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog(item.getPhoneNumber());
            }
        });
    }

    public interface AccountDelete{
        void onConfirmDelete(String phoneNum);
    }
    private void showConfirmDeleteDialog(final String phoneNum){
        CustomDialog.Builder builder = new CustomDialog.Builder(this.context);
        builder.setMessage("确定删除该账号"+phoneNum+"?");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(mAccountDelete!=null){
                    mAccountDelete.onConfirmDelete(phoneNum);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
