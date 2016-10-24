package com.kuaibao.skuaidi.dispatch.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dispatch.adapter.SignResultAdapter;
import com.kuaibao.skuaidi.dispatch.bean.ScanResult;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShowSignResultActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.tv_sign_people)
    TextView tv_sign_people;
    @BindView(R.id.ll_sign_phone)
    LinearLayout ll_sign_phone;
    @BindView(R.id.tv_sign_phone)
    TextView tv_sign_phone;
    @BindView(R.id.tv_sign_type)
    TextView tv_sign_type;
    @BindView(R.id.tv_detail_address)
    TextView tv_detail_address;
    @BindView(R.id.tv_sign_time)
    TextView tv_sign_time;
    @BindView(R.id.lv_order_numbers)
    RecyclerView rv_order_numbers;
    @BindView(R.id.tv_sign_phone_tag)
    TextView tv_sign_phone_tag;
    public static final String SIGN_RESULT="sign_result";
    private ScanResult result;
    private List<String> billNums;
    private List<String> orderNums = new ArrayList<String>();
    private SignResultAdapter adapter;
    private Drawable[] drawables = new Drawable[2];

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x112){
                boolean isAll = (boolean) msg.obj;
                adapter.setStatus(!isAll);
                orderNums.clear();
                if(isAll){
                    orderNums.add(billNums.get(0));
                }else{
                    orderNums.addAll(billNums);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sign_result);
        drawables[0] = getResources().getDrawable(R.drawable.dispatch_dropmenu_icon);
        drawables[1] = getResources().getDrawable(R.drawable.dispatch_dropmenuoff_icon);
        tv_title_des.setText("签收信息");
        tv_more.setVisibility(View.GONE);
        result = (ScanResult) getIntent().getSerializableExtra(SIGN_RESULT);
        billNums = result.getWaybillNums();
        orderNums.add(billNums.get(0));
        String nickName = result.getNickName();
        tv_sign_people.setText(TextUtils.isEmpty(nickName)?"扫码签收":nickName);
        if("alipay".equals(result.getScanPlatform())){
            tv_sign_phone_tag.setText("支付宝");
        }else if("weixin".equals(result.getScanPlatform())){
            tv_sign_phone_tag.setText("手机号");
        }
        if(!TextUtils.isEmpty(result.getPhone())){
            tv_sign_phone.setText(result.getPhone());
        }else{
            ll_sign_phone.setVisibility(View.GONE);
        }
        tv_sign_type.setText(result.getSignType());
        tv_detail_address.setText(result.getAddress());
        tv_sign_time.setText(result.getSignTime());
        rv_order_numbers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SignResultAdapter(billNums, orderNums, drawables, handler);
        rv_order_numbers.setAdapter(adapter);
    }

    public void back(View view){
        finish();
    }
}
