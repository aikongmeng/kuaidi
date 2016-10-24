package com.kuaibao.skuaidi.activity.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 支付详情页面
 * @author 顾冬冬
 *
 */
public class TopUpDetailActivity extends SkuaiDiBaseActivity {

	private Context context;
	
	private SkuaidiImageView iv_title_back;// 返回按钮
	private TextView tv_title_des;// 标题 
	
	private ImageView iv_dispose;// 状态标志
	private TextView tv_dispose;// 状态说明
	private TextView tv_top_up_title;// 状态标题
	private TextView tv_price;// 价格
	private TextView tv_receive;// 收款方
	private TextView tv_goods;// 商品
	private TextView tv_no;// 交易单号 
	private TextView tv_time;// 交易时间
	private TextView tv_curStatus;// 交易状态
	
	private String createTime;// 创建时间
	private String price;// 成交金额
	private String goods_name;// 商品名称
	private String buyer_sp_username;// 被充值人公司网点和姓名
	private String order_no;//订单号
	private String pay_status;// 支付状态
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.top_up_detail_activity);
		context = this;
		initView();
		initData();
		displayView();
		
	}
	
	private void initView(){
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		
		iv_dispose = (ImageView) findViewById(R.id.iv_dispose);
		tv_dispose = (TextView) findViewById(R.id.tv_dispose);
		tv_top_up_title = (TextView) findViewById(R.id.tv_top_up_title);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_receive = (TextView) findViewById(R.id.tv_receive);
		tv_goods = (TextView) findViewById(R.id.tv_goods);
		tv_no = (TextView) findViewById(R.id.tv_no);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_curStatus = (TextView) findViewById(R.id.tv_curStatus);
		
		iv_title_back.setOnClickListener(onClickListener);
	}
	
	private void initData(){
		createTime = getIntent().getStringExtra("createTime");
		price =  getIntent().getStringExtra("total_amount");
		goods_name = getIntent().getStringExtra("good_name");
		order_no = getIntent().getStringExtra("order_no");
		pay_status = getIntent().getStringExtra("pay_status");
		buyer_sp_username = getIntent().getStringExtra("buyer_sp_username");
	}
	
	@SuppressLint("SimpleDateFormat")
	private void displayView(){
		tv_title_des.setText("支付详情");
		
		if("success".equals(pay_status)){
			iv_dispose.setBackgroundResource(R.drawable.top_up_success);
			tv_dispose.setText("支付成功");
			tv_dispose.setTextColor(context.getResources().getColor(R.color.default_green_2));
			tv_top_up_title.setText(goods_name);
			tv_price.setText(price);
			tv_receive.setText(buyer_sp_username);
			tv_goods.setText(goods_name);
			tv_no.setText(order_no);
			long time=System.currentTimeMillis();  
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");  
			Date d1=new Date(time);  
			String t1=format.format(d1);  
			tv_time.setText(t1);
			tv_curStatus.setText("支付成功");
		}else if("review".equals(pay_status)){
			iv_dispose.setBackgroundResource(R.drawable.top_up_dispose);
			tv_dispose.setText("正在处理中");
			tv_dispose.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
			tv_top_up_title.setText(goods_name);
			tv_price.setText(price);
			tv_receive.setText(buyer_sp_username);
			tv_goods.setText(goods_name);
			tv_no.setText(order_no);
			long time=System.currentTimeMillis();  
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");  
			Date d1=new Date(time);  
			String t1=format.format(d1);  
			tv_time.setText(t1);
			tv_curStatus.setText("正在处理中");
		}
	}
	
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
	
	
	
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if(code.equals("7") && null != result){
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
