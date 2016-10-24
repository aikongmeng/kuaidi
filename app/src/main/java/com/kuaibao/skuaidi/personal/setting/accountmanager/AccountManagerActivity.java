package com.kuaibao.skuaidi.personal.setting.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.personal.PersonalFragment;
import com.kuaibao.skuaidi.personal.setting.accountmanager.adapter.MyAccountAdapter;
import com.kuaibao.skuaidi.popup.SlideFromBottomPopup2;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.KBAccount;
import gen.greendao.bean.UserBind;
import gen.greendao.dao.KBAccountDao;
import gen.greendao.dao.UserBindDao;
import rx.Subscription;
import rx.functions.Action1;

public class AccountManagerActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.recycler_list)
    RecyclerView mRecyclerView;

    private MyAccountAdapter mMyAccountAdapter;
    private List<KBAccount> listUser;
    private UserInfo userInfo;
    public static final int CHANGE_ACCOUNT=0xADC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        initData();
        setData();
    }

    private void initData(){
        userInfo=SkuaidiSpf.getLoginUser();
    }
    private void setData(){
        mTvTitleDes.setText("账号切换");
        tv_more.setVisibility(View.VISIBLE);
        tv_more.setText("编辑");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        listUser=getBindUser();
        initAdapter(false);
    }

    private List<KBAccount> getBindUser(){

        List<KBAccount> accounts=new ArrayList<>();
        KBAccountDao userAccountDao= SKuaidiApplication.getInstance().getDaoSession().getKBAccountDao();
        KBAccount mainAccount=userAccountDao.load(userInfo.getPhoneNumber());
        mainAccount.setCurrentUser(true);
        accounts.add(mainAccount);
        UserBindDao dao= SKuaidiApplication.getInstance().getDaoSession().getUserBindDao();
        QueryBuilder<UserBind> qb = dao.queryBuilder();
        qb.where(UserBindDao.Properties.Master.eq(mainAccount.getPhoneNumber()));
        List<UserBind> bindUsers=qb.build().list();
        if(bindUsers!=null && bindUsers.size()>0){
            for(UserBind userBind:bindUsers){
                String guestPhone=userBind.getGuest();
                if(!TextUtils.isEmpty(guestPhone)){
                    KBAccount account=userAccountDao.load(guestPhone);
                    if(account!=null){
                        account.setCurrentUser(false);
                        if(!accounts.contains(account)){
                            accounts.add(account);
                        }
                    }
                }
            }
        }
        QueryBuilder<UserBind> qb2 = dao.queryBuilder();
        qb2.where(UserBindDao.Properties.Guest.eq(mainAccount.getPhoneNumber()));
        List<UserBind> bindUsers2=qb2.build().list();
        if(bindUsers2!=null && bindUsers2.size()>0){
            for(UserBind userBind2:bindUsers2){
                String masterPhone=userBind2.getMaster();
                if(!TextUtils.isEmpty(masterPhone)){
                    KBAccount account=userAccountDao.load(masterPhone);
                    if(account!=null){
                        account.setCurrentUser(false);
                        if(!accounts.contains(account)){
                            accounts.add(account);
                        }
                    }
                }
            }
        }
        return accounts;
    }
    private void initAdapter(boolean isShowDelete){
        mMyAccountAdapter=new MyAccountAdapter(this,listUser,isShowDelete);
        if(!isShowDelete){
            mMyAccountAdapter.addFooterView(getFooterView());
        }
        mRecyclerView.setAdapter(mMyAccountAdapter);
        mMyAccountAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(!mMyAccountAdapter.isShowDelete()){//切换账号
                    if(!listUser.get(position).getCurrentUser())
                    autoLogin(listUser.get(position).getPhoneNumber(),listUser.get(position).getPassword());
                }else{//编辑删除
                    showBottomPop(position);
                }
            }
        });
    }

    private void showBottomPop(final int position){
            SlideFromBottomPopup2 sliderPop=new SlideFromBottomPopup2(AccountManagerActivity.this);
            sliderPop.setPopClick(new SlideFromBottomPopup2.OnBottomPopClick() {
                @Override
                public void onClickFirstPop() {//删除账号
                    deleteDateWithType(position);
                }
                @Override
                public void onClickSecondPop() {//删除账号和本地记录(已废弃)
                    //deleteDateWithType(position,true);
                }
            });
            sliderPop.showPopupWindow();
    }

    private void deleteDateWithType(int position){
        if(listUser.get(position).getCurrentUser()) {//当前登录用户注销,跳转重新登录
            //E3OrderDAO.deleteAllUploadedOrders(E3SysManager.getCourierNO());

            UpdateReviewInfoUtil.updateCurrentReviewStatus("");
            KBAccountDao userAccountDao= SKuaidiApplication.getInstance().getDaoSession().getKBAccountDao();
            userAccountDao.delete(listUser.get(position));

            UserBindDao userBindDao= SKuaidiApplication.getInstance().getDaoSession().getUserBindDao();
            Query query = userBindDao.queryBuilder().where(UserBindDao.Properties.Master.eq(userInfo.getPhoneNumber())).build();
            List<UserBind> userBinds= query.list();
            if(userBinds!=null && userBinds.size()>0){
                for(UserBind userBind:userBinds){
                    userBindDao.delete(userBind);
                }
            }

            Query query2 = userBindDao.queryBuilder().where(UserBindDao.Properties.Guest.eq(userInfo.getPhoneNumber())).build();
            List<UserBind> userBinds2= query2.list();
            if(userBinds2!=null && userBinds2.size()>0){
                for(UserBind userBind:userBinds2){
                    userBindDao.delete(userBind);
                }
            }

            SkuaidiSpf.exitLogin(getApplicationContext());
            SkuaidiSpf.setIsLogin(false);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            EventBus.getDefault().post(new MessageEvent(SKuaidiApplication.EXIT_ALL_NOTIFY,""));
            UtilToolkit.showToast( "已退出登录");
            Utility.stopPushService();
            //Utility.clearAppData(true);
        }else{
            //E3OrderDAO.deleteAllUploadedOrders(E3SysManager.getCourierNO());

            UserBindDao userBindDao= SKuaidiApplication.getInstance().getDaoSession().getUserBindDao();
            Query query = userBindDao.queryBuilder().where(UserBindDao.Properties.Master.eq(userInfo.getPhoneNumber()), UserBindDao.Properties.Guest.eq(listUser.get(position).getPhoneNumber())).build();
            List<UserBind> userBinds= query.list();
            if(userBinds!=null && userBinds.size()>0){
                    userBindDao.delete(userBinds.get(0));
            }

            Query query2 = userBindDao.queryBuilder().where(UserBindDao.Properties.Master.eq(listUser.get(position).getPhoneNumber()), UserBindDao.Properties.Guest.eq(userInfo.getPhoneNumber())).build();
            List<UserBind> userBinds2= query2.list();
            if(userBinds2!=null && userBinds2.size()>0){
                    userBindDao.delete(userBinds2.get(0));
            }

            listUser=getBindUser();
            initAdapter("完成".equals(tv_more.getText().toString()));
            //Utility.clearAppData(true);
        }
    }

    private void autoLogin(final String phoneNumber,final String pwd){
        showProgressDialog("");//AccountManagerActivity.this,"正在切换账号...");
        final ApiWrapper wrapper=new ApiWrapper();
        Subscription subscription = wrapper.loginV1(phoneNumber,pwd)
                .subscribe(newSubscriber(new Action1<LoginUserInfo>() {
                    @Override
                    public void call(LoginUserInfo userInfo) {
                        SkuaidiSpf.setSessionId(TextUtils.isEmpty(userInfo.getSession_id())?"":userInfo.getSession_id());
                        userInfo.setPhoneNumber(phoneNumber);
                        userInfo.setPassword(pwd);
                        BaseRxHttpUtil.changeLoginUserInfo(userInfo);
                        AccountUtils.insertOrUpdateKBAccount();
                        initData();
                        listUser=getBindUser();
                        initAdapter(false);
                        setStatusBar();
                        DynamicSkinChangeManager.changSkinByLoginUser();
                        UtilToolkit.showToast("账号切换成功");
                        UpdateReviewInfoUtil.updateReviewInfo(false);
                        MessageEvent messageEvent=new MessageEvent(CHANGE_ACCOUNT,"");
                        EventBus.getDefault().post(messageEvent);
                        Utility.stopPushService();
                        EventBus.getDefault().post(new MessageEvent(PersonalFragment.P_REFRESH_HEAD_IMG,""));
                    }
                }));
        mCompositeSubscription.add(subscription);
    }
    private View getFooterView(){
        final View view = getLayoutInflater().inflate(R.layout.add_usr_footerview, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountManagerActivity.this,AddAccountActivity.class);
                startActivityForResult(intent,0XAC);
            }
        });
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0XAC:
                if(resultCode==RESULT_OK){
                    listUser=getBindUser();
                    initAdapter(false);
                }
                break;
        }
    }

    @OnClick({R.id.iv_title_back, R.id.tv_more})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_more:
                switchEditState();
                break;
        }
    }

    private void switchEditState(){
        tv_more.setText("编辑".equals(tv_more.getText().toString())?"完成":"编辑");
        initAdapter("完成".equals(tv_more.getText().toString()));
    }
}
