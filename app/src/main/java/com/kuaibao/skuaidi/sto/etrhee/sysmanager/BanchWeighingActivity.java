package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.adapter.BanchQuickAdapter;
import com.kuaibao.skuaidi.util.IsGuid;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BanchWeighingActivity extends RxRetrofitBaseActivity {
    public static final String LIST_DATA = "listData";
    private List<NotifyInfo> listData;

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_more)
    SkuaidiImageView iv_more;
    private BanchQuickAdapter mAdapter;
    @BindView(R.id.select_all_weight)
    CheckBox mCheckBox;
    @BindView(R.id.ll_sto_weight_notify)
    LinearLayout ll_sto_weight_notify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banch_weighing);
        listData = (List<NotifyInfo>) getIntent().getSerializableExtra(LIST_DATA);
        initView();
    }

    private void initView() {
        tvTitleDes.setText("物品类别/重量");
        iv_more.setImageResource(R.drawable.issue_question);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),
                        getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
                .build());  //添加分割线
        mRecyclerView.setAdapter(mAdapter);
        if(!IsGuid.activityIsGuided(getApplicationContext(),BanchWeighingActivity.this.getClass().getName())){
            ll_sto_weight_notify.setVisibility(View.VISIBLE);
        }
    }

    public void setCheckAll(){
        if(mAdapter.getData().size()==mAdapter.getSelectedListData().size()){
            if(!mCheckBox.isChecked())mCheckBox.setChecked(true);
        }else{
            if(mCheckBox.isChecked())mCheckBox.setChecked(false);
        }
    }

    private void initAdapter() {
        mAdapter = new BanchQuickAdapter(this, listData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_title_back, R.id.ll_upload_banch, R.id.tv_more,R.id.tv_banch_weighting,R.id.select_all_weight,R.id.iv_close_notify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                onBackEvent();
                break;
            case R.id.ll_upload_banch:
                onSaveClick();
                break;
            case R.id.tv_more:
                Intent intent = new Intent(BanchWeighingActivity.this, WebViewActivity.class);
                intent.putExtra("fromwhere", "weighing_explanation");
                startActivity(intent);
                break;
            case R.id.tv_banch_weighting:
                List<NotifyInfo> selectedList=mAdapter.getSelectedListData();
                if(selectedList.size()==0){
                    UtilToolkit.showToast("请先勾选单号");
                    return;
                }
                showBranchWeightDialog(selectedList);
                break;
            case R.id.select_all_weight:
                mAdapter.selectAllOrNot(mCheckBox.isChecked());
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_close_notify:
                ll_sto_weight_notify.setVisibility(View.GONE);
                IsGuid.setIsGuided(getApplicationContext(),BanchWeighingActivity.this.getClass().getName());
                break;
        }
    }

    private void showBranchWeightDialog(final List<NotifyInfo> selectedList){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.banch_weight_edit,null);
        final ClearEditText clearEditText= (ClearEditText) view.findViewById(R.id.et_banchweigh);
        clearEditText.setFocusable(true);
        clearEditText.setFocusableInTouchMode(true);
        clearEditText.requestFocus();
        builder.setContentView(view);
        builder.setTitle("批量录入重量");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(clearEditText.getText().toString().trim())){
                    UtilToolkit.showToast("重量未填写");
                    return;
                }
                double weight=Double.parseDouble(clearEditText.getText().toString().trim());
                if(weight<0.2){
                    UtilToolkit.showToast("最低重量不能小于0.2kg");
                    return;
                }
                if(weight==0.2){
                    UtilToolkit.showToast("默认重量为0.2kg,不必重复输入");
                    return;
                }
                for(NotifyInfo notifyInfo:selectedList){
                    notifyInfo.setWeight(weight);
                }
                dialog.dismiss();
                mAdapter.notifyNewDataChanged(selectedList);
                mAdapter.notifyDataSetChanged();
                setCheckAll();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)clearEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(clearEditText, 0);
            }
        },500);
    }

    private void onBackEvent(){
        if (mAdapter.isEditd()) {
            showWarningDialog("录入收件重量信息未保存,是否保存?",true,false);
            return;
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            onBackEvent();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击保存
     */
    private void onSaveClick() {
        int switchType=isContainLessOrMoreWeight();
        if (1==switchType) {
            showWarningDialog("单号未填写重量，将默认为0.2kg",false,true);
            return;
        }
        if(2==switchType){
            showWarningDialog("有快件的重量超过50kg,是否继续保存?",false,true);
            return;
        }
        setOkSave();
    }

    private int isContainLessOrMoreWeight(){
        int contain = 0;//是否包含重量为0的单号或者超过50KG的单子
        for (NotifyInfo info : listData) {
            if (info.getWeight() == 0) {
                contain = 1;
                return contain;
            }else if(info.getWeight() > 50){
                contain = 2;
                return contain;
            }
        }
        return contain;
    }

    private void setOkSave(){
        Intent intent=getIntent();
        intent.putExtra("dataList", (Serializable) listData);
        setResult(RESULT_OK, intent);
        finish();
        UtilToolkit.showToast("保存成功");
    }

    private void showWarningDialog(String msg, final boolean negativeFinish,final boolean doubleCheck){
        final SkuaidiDialog skuaidiDialog = new SkuaidiDialog(this);
        skuaidiDialog.setTitle("温馨提示");
        skuaidiDialog.setContent(msg);
        skuaidiDialog.isUseEditText(false);
        skuaidiDialog.setPositionButtonTitle(negativeFinish?"保存":"确定");
        skuaidiDialog.setNegativeButtonTitle("取消");
        skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                skuaidiDialog.dismiss();
                if(!doubleCheck){
                    int switchType=isContainLessOrMoreWeight();
                    if(2==switchType){
                        showWarningDialog("有快件的重量超过50kg,是否继续保存?",true,true);
                        return;
                    }
                }
                setOkSave();
            }
        });
        skuaidiDialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
            @Override
            public void onClick() {
                skuaidiDialog.dismiss();
                if(negativeFinish) {
                    finish();
                }
            }
        });
        skuaidiDialog.showDialog();
    }
}
