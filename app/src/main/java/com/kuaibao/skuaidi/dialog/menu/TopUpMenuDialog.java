package com.kuaibao.skuaidi.dialog.menu;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.TopUpButtonView;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gdd
 * on 2016/9/18.
 * 充值界面选择充值方式菜单
 */
public class TopUpMenuDialog implements View.OnClickListener{

    private Context context;
    private TopUpMenuSelectOnClickListener topUpMenuSelectOnClickListener;
    private DisplayMetrics displayMetrics;
    private Dialog dialog;
    private TopUpButtonView[] topUpButtonViews;

    @BindView(R.id.tv_title) TextView tvTitle;// 标题
    @BindView(R.id.view_pay_scan) TopUpButtonView viewPayScan;// 支付宝扫码充值
    @BindView(R.id.view_pay_quick) TopUpButtonView viewPayQuick;// 支付宝快捷充值
    @BindView(R.id.view_pay_wx) TopUpButtonView viewPayWx;// 微信充值
    @BindView(R.id.view_pay_cash_to_consumption) TopUpButtonView viewPayCashToConsumption;// 可提现转可消费

    public TopUpMenuDialog(Context context){
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    }

    public TopUpMenuDialog builder(){
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_top_up_menu,null);

        view.setMinimumWidth(displayMetrics.widthPixels);
        ButterKnife.bind(this,view);

        initView();

        dialog = new Dialog(context,R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.START | Gravity.BOTTOM);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        window.setAttributes(wl);
        return this;
    }

    public TopUpMenuDialog setCancledOnTouchOutside(boolean cancel){
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public TopUpMenuDialog showDialog(){
        dialog.show();
        return this;
    }

    public void initView(){
        tvTitle.setText("充值方式");

        topUpButtonViews = new TopUpButtonView[]{viewPayScan,viewPayQuick,viewPayWx,viewPayCashToConsumption};
        setSelectItem(SkuaidiSpf.getSelectItem(context));

        viewPayScan.setPayIcon(Utility.getDrawable(context,R.drawable.btn_alipay));
        viewPayScan.setPayTitle("支付宝扫码支付");
        viewPayScan.setPayDesc("推荐支付宝安装在其他手机上的用户使用");

        viewPayQuick.setPayIcon(Utility.getDrawable(context,R.drawable.btn_alipay));
        viewPayQuick.setPayTitle("支付宝快捷支付");
        viewPayQuick.setPayDesc("推荐支付宝安装在本机的用户使用");

        viewPayWx.setPayIcon(Utility.getDrawable(context,R.drawable.btn_weixin));
        viewPayWx.setPayTitle("微信支付");
        viewPayWx.setPayDesc("推荐已安装微信的用户使用");

        viewPayCashToConsumption.setPayIcon(Utility.getDrawable(context,R.drawable.btn_turn));
        viewPayCashToConsumption.setPayTitle("可提现转入可消费");
        viewPayCashToConsumption.setPayDesc("将提现金额转入可消费金额");
    }

    @OnClick({R.id.iv_close,R.id.view_pay_scan,R.id.view_pay_quick,R.id.view_pay_wx,R.id.view_pay_cash_to_consumption})
    public void onClick(View v) {
        int selectItem = 0;// 选择条目下标
        switch (v.getId()){
            case R.id.iv_close:
                dialog.dismiss();
                break;
            case R.id.view_pay_scan:
                selectItem = 0;
                break;
            case R.id.view_pay_quick:
                selectItem = 1;
                break;
            case R.id.view_pay_wx:
                selectItem = 2;
                break;
            case R.id.view_pay_cash_to_consumption:
                selectItem = 3;
                break;
        }
        setSelectItem(selectItem);
        SkuaidiSpf.saveSelectItem(context,selectItem);
        topUpMenuSelectOnClickListener.onClick(selectItem);
        dialog.dismiss();
    }

    private void setSelectItem(int selectItem){
        for (int i = 0;i<topUpButtonViews.length;i++){
            topUpButtonViews[i].setSelect(i == selectItem);
        }
    }

    public void setTopUpMenuSelectOnclickListener(TopUpMenuSelectOnClickListener topUpMenuSelectOnClickListener){
        this.topUpMenuSelectOnClickListener = topUpMenuSelectOnClickListener;
    }

    public interface TopUpMenuSelectOnClickListener{
        void onClick(int selectItem);
    }




}
