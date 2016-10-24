package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.util.Utility;

import java.text.DecimalFormat;

/**
 * 收款后扫快递运单号
 *
 * @author a4
 */
public class CollectionAddExpressNoActivity extends CaptureActivity implements OnGlobalLayoutListener {
    SkuaidiTextView tv_finish;
    SkuaidiImageView iv_back;
    private String money;
    private Context context;
    private RelativeLayout re_top;
    private View title;
    private TextView tv_status;
    private TextView tv_instruction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        context = this;
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title_des)).setText("交易完成");
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_status.setText(getIntent().getStringExtra("desc"));

        tv_instruction = (TextView) findViewById(R.id.tv_instruction);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("instruction"))) {
            tv_instruction.setText(getIntent().getStringExtra("instruction"));
            tv_instruction.setVisibility(View.VISIBLE);
        }
        tv_finish = ((SkuaidiTextView) findViewById(R.id.tv_more));
        tv_finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addCollectionExpress();
            }
        });
        TextView tv_money = (TextView) findViewById(R.id.tv_money);
        // TextView tv_paytime = (TextView) findViewById(R.id.tv_paytime);
        money = getIntent().getStringExtra("money");
        float money_f = Float.parseFloat(money);
        DecimalFormat format = new DecimalFormat("##0.00");
        String moneyFormated = format.format(money_f);
        tv_money.setText("+" + moneyFormated);

        // tv_paytime.setText(getIntent().getStringExtra("create_time"));
        ((TextView) findViewById(R.id.scan_view).findViewById(R.id.tv_title_des)).setText("添加单号");
        findViewById(R.id.tv_cap_finish).setVisibility(View.GONE);
        findViewById(R.id.scan_view).findViewById(R.id.iv_title_back).setVisibility(View.GONE);
        iv_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (MakeCollectionsActivity.activitys != null) {// 订单收款扫单号，
                    for (Activity activity : MakeCollectionsActivity.activitys) {
                        activity.finish();
                    }
                    Intent intent = new Intent(OrderInfoActivity.ACTION_SET_MONEY);
                    intent.putExtra("money", money);
                    sendBroadcast(intent);
                    if (MakeCollectionsActivity.activitys != null)
                        MakeCollectionsActivity.activitys.clear();
                }
                back(view);
            }
        });
        findViewById(R.id.tv_cap_finish).setVisibility(View.GONE);

        re_top = (RelativeLayout) findViewById(R.id.re_top);
        re_top.getViewTreeObserver().addOnGlobalLayoutListener(this);

        ViewTreeObserver vto = mListview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
        title = findViewById(R.id.title);
        title.getViewTreeObserver().addOnGlobalLayoutListener(this);

//        MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//        EventBus.getDefault().post(m);// 执行此广播,立刻执行调用钱包金额接口
    }

    @Override
    protected void onDestroy() {
        if (MakeCollectionsActivity.activitys != null)
            MakeCollectionsActivity.activitys.remove(this);
        super.onDestroy();
    }

    @Override
    public void onGlobalLayout() {
        mListview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        mListview.getLayoutParams().height = display.getHeight() - findViewById(R.id.ll_tips1).getLayoutParams().height
                - re_top.getHeight() - title.getHeight() * 2 - Utility.getStatusBarHeight(context)
                - getFramingRectBottom() - Utility.dip2px(context, 20);
        mListview.setLayoutParams(mListview.getLayoutParams());

        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // statusBarHeight是上面所求的状态栏的高度
        int statusBarHeight = Utility.getStatusBarHeight(context);
        int titleBarHeight = contentTop - statusBarHeight;

    }

    public int getFramingRectBottom() {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point(display.getWidth(), display.getHeight());
        float denysity = context.getResources().getDisplayMetrics().density;
        int width = screenResolution.x - 2 * (int) (10 * denysity);
        int height = screenResolution.y / 5;
        int leftOffset = (screenResolution.x - width) / 2;
        int topOffset = (screenResolution.y) / 2 - height;
        int bottom = Utility.dip2px(context, 44) + height;
        return bottom;
    }
}
