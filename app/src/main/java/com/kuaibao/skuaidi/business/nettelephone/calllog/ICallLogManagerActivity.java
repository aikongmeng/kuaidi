package com.kuaibao.skuaidi.business.nettelephone.calllog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.calllog.adapter.ManagerCallLogAdapter;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.LocalCallLogFragment;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.software.shell.fab.ActionButton;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.ICallLog;
import gen.greendao.dao.ICallLogDao;
import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ICallLogManagerActivity extends RxRetrofitBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    public static final String MANAGER_TYPE_NAME="MANAGER_TYPE_NAME";
    public static final String MANAGER_DELETE_TYPE="MANAGER_DELETE_TYPE";
    public static final String MANAGER_UPLOAD_TYPE="MANAGER_UPLOAD_TYPE";
    private String managerType;

    @BindView(R.id.fab_activity_action_button)
    ActionButton actionButton;
    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.rv_manager_call_log)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_call_log_nodata)
    TextView tv_call_log_nodata;
    @BindView(R.id.ll_log_content)
    LinearLayout ll_log_content;
    @BindView(R.id.select_all_call)
    CheckBox select_all;
    @BindView(R.id.relative_layout_main)
    RelativeLayout relative_layout_main;
    private ManagerCallLogAdapter mManagerCallLogAdapter;
    private int offset=0;
    private UserInfo mUserInfo;
    private boolean hadDeleteLog;
    private Snackbar snackbar;
    private List<ICallLog> selectd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icall_log_manager);
        initData();
        initView();
    }

    private void initView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        mManagerCallLogAdapter=new ManagerCallLogAdapter(this,new ArrayList<ICallLog>());
        mManagerCallLogAdapter.openLoadMore(LocalCallLogFragment.PAGE_SIZE,true);
        mManagerCallLogAdapter.setOnLoadMoreListener(this);
        mManagerCallLogAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                CheckBox checkBox= (CheckBox) view.findViewById(R.id.ck_select);
                checkBox.setChecked(!checkBox.isChecked());
                mManagerCallLogAdapter.getData().get(i).setChecked(checkBox.isChecked());
                select_all.setChecked(canSelectAllChecked());
            }
        });
        mRecyclerView.setAdapter(mManagerCallLogAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerViewScrollDetector(DisplayUtil.dip2px(4)) {
            @Override
            void onScrollUp() {
                if(actionButton.isShown()){
                    actionButton.hide();
                }
            }
            @Override
            void onScrollDown() {
                if(actionButton.isHidden()){
                    actionButton.show();
                }
            }
        });
        queryLocalCallLog(offset,true);
        if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
            actionButton.setButtonColor(ContextCompat.getColor(this,R.color.sto_text_color));
            actionButton.setButtonColorPressed(ContextCompat.getColor(this,R.color.sto_btn_orange_hover));
        }
        if(MANAGER_DELETE_TYPE.equals(managerType)){
            actionButton.setImageResource(R.drawable.icon_call_log_delete);
        }else if(MANAGER_UPLOAD_TYPE.equals(managerType)){
            actionButton.setImageResource(R.drawable.icon_call_log_upload);
        }
        if(MANAGER_UPLOAD_TYPE.equals(managerType)) {
            snackbar = Snackbar.make(relative_layout_main, "只展示未上传且有对应录音的记录", Snackbar.LENGTH_LONG);
        }else if(MANAGER_DELETE_TYPE.equals(managerType)){
            snackbar = Snackbar.make(relative_layout_main, "删除记录同时会删除对应的录音", Snackbar.LENGTH_LONG);
        }
            snackbar.setAction("隐藏", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    actionButton.setVisibility(View.VISIBLE);
                }
            });
            if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
                snackbar.setActionTextColor(ContextCompat.getColor(this,R.color.sto_text_color));
            }else{
                snackbar.setActionTextColor(ContextCompat.getColor(this,R.color.title_bg));
            }
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(this,R.color.background_gray));
            textView.setTextSize(13);
            snackbar.show();
    }

    private abstract class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener {
        public RecyclerViewScrollDetector(int scrollThreshold){
            mScrollThreshold = scrollThreshold;
        }
        private int mScrollThreshold;
        abstract void onScrollUp();
        abstract void onScrollDown();
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
            if (isSignificantDelta) {
                if (dy > 0) {
                    onScrollUp();
                } else {
                    onScrollDown();
                }
            }
        }
    }

    private void queryLocalCallLog(final int offset, final boolean isRefresh){
        Observable.create(new Observable.OnSubscribe<List<ICallLog>>() {
            @Override
            public void call(Subscriber<? super List<ICallLog>> subscriber) {
                ICallLogDao dao= SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                QueryBuilder<ICallLog> qb = dao.queryBuilder();
                if(MANAGER_DELETE_TYPE.equals(managerType)){
                    qb.where(ICallLogDao.Properties.MasterPhone.eq(mUserInfo.getPhoneNumber()))
                            .orderDesc(ICallLogDao.Properties.CallDate).offset(offset * LocalCallLogFragment.PAGE_SIZE).limit(LocalCallLogFragment.PAGE_SIZE);
                }else if(MANAGER_UPLOAD_TYPE.equals(managerType)){
                    qb.where(ICallLogDao.Properties.MasterPhone.eq(mUserInfo.getPhoneNumber()),ICallLogDao.Properties.HadUpload.notEq(1),ICallLogDao.Properties.RecordingFilePath.notEq("''"))
                            .orderDesc(ICallLogDao.Properties.CallDate).offset(offset * LocalCallLogFragment.PAGE_SIZE).limit(LocalCallLogFragment.PAGE_SIZE);
                }
                List<ICallLog> callogs=qb.build().list();
                subscriber.onNext(callogs);
            }
        }).flatMap(new Func1<List<ICallLog>, Observable<List<ICallLog>>>() {
            @Override
            public Observable<List<ICallLog>> call(List<ICallLog> iCallLogs) {
                if(iCallLogs!=null){
                    if(MANAGER_DELETE_TYPE.equals(managerType)){
                        for(ICallLog iCallLog:iCallLogs){
                            iCallLog.setChecked(false);
                        }
                    }else if(MANAGER_UPLOAD_TYPE.equals(managerType)){
                        List<ICallLog> splitList=new ArrayList<>();
                        for(ICallLog iCallLog:iCallLogs){
                            if(!TextUtils.isEmpty(iCallLog.getRecordingFilePath()) && (new File(iCallLog.getRecordingFilePath()).exists())){
                                iCallLog.setChecked(false);
                                splitList.add(iCallLog);
                            }
                        }
                        return Observable.just(splitList);
                    }
                }
                return Observable.just(iCallLogs);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        setList(null,isRefresh);
                    }
                })
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<List<ICallLog>>() {
                    @Override
                    public void call(List<ICallLog> iCallLogs) {
                        setList(iCallLogs,isRefresh);
                    }
                }));
    }

    private void setList(List<ICallLog> iCallLogs,boolean isRefresh){
        if(iCallLogs!=null && iCallLogs.size()>0){
            ll_log_content.setVisibility(View.VISIBLE);
            tv_call_log_nodata.setVisibility(View.GONE);
            if(isRefresh){
                mManagerCallLogAdapter.setNewData(iCallLogs);
            }else{
                mManagerCallLogAdapter.notifyDataChangedAfterLoadMore(iCallLogs,true);
            }
        }else{
            if(isRefresh){
                tv_call_log_nodata.setVisibility(View.VISIBLE);
                ll_log_content.setVisibility(View.GONE);
            }else{
                UtilToolkit.showToast("没有更多数据啦~");
                mManagerCallLogAdapter.notifyDataChangedAfterLoadMore(false);
            }
        }
    }

    private void initData(){
        managerType=getIntent().getStringExtra(MANAGER_TYPE_NAME);
        mUserInfo= SkuaidiSpf.getLoginUser();
        if(MANAGER_UPLOAD_TYPE.equals(managerType)){
            tvDes.setText("上传录音");
        }else if(MANAGER_DELETE_TYPE.equals(managerType)){
            tvDes.setText("删除记录");
        }
    }

    @OnClick({R.id.iv_title_back,R.id.fab_activity_action_button,R.id.select_all_call})
    public void onClick(View view){
      switch (view.getId()){
          case R.id.iv_title_back:
              if(MANAGER_DELETE_TYPE.equals(managerType)){
                  if(hadDeleteLog) setResult(RESULT_OK);
              }
              finish();
              break;
          case R.id.fab_activity_action_button:
              handlerFabClick();
              break;
          case R.id.select_all_call:
              toggleSelectState(select_all.isChecked());
              break;
        }
    }

    private void handlerFabClick(){
        if(selectd==null || selectd.size()==0){
            selectd=getSelectedCall();
        }
        if(MANAGER_DELETE_TYPE.equals(managerType)){
            if(selectd.size()==0){
                UtilToolkit.showToast("请勾选您要删除的记录");
                return;
            }
            showMessageDialog("确定删除勾选的记录吗?",selectd);
        }else if(MANAGER_UPLOAD_TYPE.equals(managerType)){
            if(selectd.size()==0){
                UtilToolkit.showToast("请勾选您要上传的录音");
                return;
            }
            if(!Utility.isNetworkConnected()){
                UtilToolkit.showToast("请检查您的网络连接");
                return;
            }
            if(Utility.getNetworkType()!=Utility.NETTYPE_WIFI){
                showMessageDialog("当前在非wifi下,将使用数据流量\n确定要继续上传?",selectd);
                return;
            }
            uploadRecord(selectd);
        }
    }


    private void uploadRecord(List<ICallLog> iCallLogs){
        JSONObject jsonParam=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        List<File> uploadFile=new ArrayList<>();
        for(int i=0;i<iCallLogs.size();i++){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("title","file"+i);
            jsonObject.put("fileName",iCallLogs.get(i).getRecordingFilePath().substring(iCallLogs.get(i).getRecordingFilePath().lastIndexOf("/")+1));
            jsonObject.put("record_time", UtilityTime.getDateTimeByMillisecond2(iCallLogs.get(i).getCallDate(),UtilityTime.YYYY_MM_DD_HH_MM_SS));
            jsonObject.put("sound_len",iCallLogs.get(i).getCallDurationTime());
            jsonObject.put("type",ICallLog.TYPE_INCOMING_CALL==iCallLogs.get(i).getCallType()?"in":"out");
            jsonObject.put("phone",iCallLogs.get(i).getCallNum());
            jsonArray.add(jsonObject);
            uploadFile.add(new File(iCallLogs.get(i).getRecordingFilePath()));
        }
        jsonParam.put("uploadRecord",jsonArray);
        okGoPost("FileImage/recordUpload",jsonParam,uploadFile,"正在上传...");
    }

    @Override
    public void onSuccessRequest(JSONObject responseData, Call call, Response response, boolean isUploadFile) {
        super.onSuccessRequest(responseData, call, response, isUploadFile);
        if(selectd!=null && selectd.size()>0){
            for(ICallLog iCallLog:selectd){
                ICallLogDao dao= SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                ICallLog log=dao.load(iCallLog.getUuid());
                if(log!=null){
                    log.setHadUpload(1);
                    dao.update(log);
                }
            }
        }
        mManagerCallLogAdapter.getData().remove(selectd);
        offset=0;
        queryLocalCallLog(offset,true);
        if(selectd!=null){
            selectd.clear();
            selectd=null;
        }
        UtilToolkit.showToast("上传成功");
    }

    private void showMessageDialog(String message,final List<ICallLog> selectd){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(MANAGER_DELETE_TYPE.equals(managerType)){
                    ICallLogDao iCallLogDao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                    for(ICallLog iCallLog:selectd){
                        iCallLogDao.deleteByKey(iCallLog.getUuid());
                        if(!TextUtils.isEmpty(iCallLog.getRecordingFilePath())){
                            File file=new File(iCallLog.getRecordingFilePath());
                            if(file.exists()) file.delete();
                        }
                        mManagerCallLogAdapter.getData().remove(iCallLog);
                    }
                    mManagerCallLogAdapter.notifyDataSetChanged();
                    UtilToolkit.showToast("删除成功");
                    hadDeleteLog=true;
                }else if(MANAGER_UPLOAD_TYPE.equals(managerType)){
                    uploadRecord(selectd);
                }
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private List<ICallLog> getSelectedCall(){
        List<ICallLog> callUids=new ArrayList<>();
        for(ICallLog iCallLog:mManagerCallLogAdapter.getData()){
            if(iCallLog.isChecked() && 1!=iCallLog.getHadUpload()){
                callUids.add(iCallLog);
            }
        }
        return callUids;
    }

    private boolean canSelectAllChecked(){
        for(ICallLog iCallLog : mManagerCallLogAdapter.getData()){
            if(!iCallLog.isChecked()){
                return false;
            }
        }
        return true;
    }

    private void toggleSelectState(boolean isChecked){
        for(ICallLog iCallLog : mManagerCallLogAdapter.getData()){
            iCallLog.setChecked(isChecked);
        }
        mManagerCallLogAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreRequested() {
        queryLocalCallLog(++offset,false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
