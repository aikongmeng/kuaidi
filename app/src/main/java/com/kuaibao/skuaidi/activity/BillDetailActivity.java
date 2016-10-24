package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
/**
 * 在线账单详情界面
 * @author gudd
 *
 */
public class BillDetailActivity extends SkuaiDiBaseActivity {

	private Context context;
	private TextView tv_title_des;//标题
	private SkuaidiImageView iv_title_back;//返回
	private ImageView iv_gou;//提示支付成功图标
	private TextView tv_zhifu_state;//支付成功图标
	private TextView recived_money;//收款方
	private TextView tv_thing_des;//物品信息
	private TextView tv_deal_time;//交易时间
	private TextView tv_deal_num;//交易号
	private TextView tv_search_order;//查看订单按钮
	private TextView tv_money;//显示付款金额
	
	private String type;//交易类型（提示说明）
	private String desc;//物品信息
	private String available_money;//交易现金
	private String avail_time;//交易时间
	private String trade_number;//交易号
	private String success_time;//提现成功时间
	private String money;//提现交易金额
	private String order_number;//订单号
	private String income_type_val;//交易类型（in类型详情说明）
	private String outcome_type_val;
	private String is_successed;// 1代表成功 0代表进行中
	private String resulttypestr;//交易状态
	private String[] thingsInfo;
	public static int BILL_DETAIL_ACTIVITY = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.bill_detail_activity);
		context = this;
		
		initView();
		getData();
		setData();
	}
	//初始化数据 
	private void initView(){
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		iv_gou = (ImageView) findViewById(R.id.iv_gou);
		tv_zhifu_state = (TextView) findViewById(R.id.tv_zhifu_state);
		recived_money = (TextView) findViewById(R.id.recived_money);
		tv_thing_des = (TextView) findViewById(R.id.tv_thing_des);
		tv_deal_time = (TextView) findViewById(R.id.tv_deal_time);
		tv_deal_num = (TextView) findViewById(R.id.tv_deal_num);
		tv_search_order = (TextView) findViewById(R.id.tv_search_order);
		tv_money = (TextView) findViewById(R.id.tv_money);
		
		iv_title_back.setOnClickListener(new MyOnClickListener());
		tv_search_order.setOnClickListener(new MyOnClickListener());
	}
	
	//获取数据
	private void getData(){
		type = getIntent().getStringExtra("type");
		resulttypestr = getIntent().getStringExtra("resulttypestr");
		if(type.equals("in")){
			avail_time = getIntent().getStringExtra("avail_time");
			available_money = getIntent().getStringExtra("available_money");
			income_type_val = getIntent().getStringExtra("income_type_val");
		}else if(type.equals("out")){
			success_time = getIntent().getStringExtra("success_time");
			money = getIntent().getStringExtra("money");
			outcome_type_val = getIntent().getStringExtra("outcome_type_val");// 暂时还没用到
			is_successed = getIntent().getStringExtra("is_successed");
		}
		desc = getIntent().getStringExtra("desc");
		avail_time = getIntent().getStringExtra("avail_time");
		trade_number = getIntent().getStringExtra("trade_number");
		order_number = getIntent().getStringExtra("order_number");
	}
	
	//设置数据 
	private void setData(){
		tv_title_des.setText("账单详情");
		tv_zhifu_state.setText(resulttypestr);//设置交易状态（title）
		if(type.equals("in")){
//			tv_zhifu_state.setText("支付成功");
			tv_deal_time.setText(avail_time);
			tv_money.setText("￥ "+available_money);
			if(!income_type_val.equals("order_comm")){
				tv_search_order.setVisibility(View.GONE);
			}else {
				tv_search_order.setVisibility(View.GONE);
			}
			tv_deal_num.setText(trade_number);
		}else if(type.equals("out")){
			if(is_successed.equals("0")){
				iv_gou.setImageResource(R.drawable.doing_icon);
//				tv_zhifu_state.setText("提现中");
				tv_deal_time.setText("");
				tv_deal_num.setText("");
				tv_money.setText("￥ "+money);
				tv_search_order.setVisibility(View.GONE);//隐藏查看订单按钮
			}else if (is_successed.equals("1")) {
//				tv_zhifu_state.setText("提现成功");
				iv_gou.setImageResource(R.drawable.succeed_icon);
				tv_deal_time.setText(success_time);
				tv_money.setText("￥ "+money);
				tv_search_order.setVisibility(View.GONE);//隐藏查看订单按钮
				tv_deal_num.setText(trade_number);
			}else if(is_successed.equals("2")){
//				tv_zhifu_state.setText("提现失败");
				iv_gou.setImageResource(R.drawable.failure_icon);
				tv_deal_time.setText("");
				tv_deal_num.setText("");
				tv_money.setText("￥ "+money);
				tv_search_order.setVisibility(View.GONE);//隐藏查看订单按钮
			}
			
		}
		
		if(KuaiBaoStringUtilToolkit.isEmpty(desc) == false && !desc.equals("null") && desc!=null){
			if(desc.contains("\\|\\|") || desc.contains("||")){
				thingsInfo = desc.split("\\|\\|");
				recived_money.setText(thingsInfo[0]);// 收款方
				tv_thing_des.setText(thingsInfo[1]);// 物品信息
			}else{
				recived_money.setText("");// 收款方
				tv_thing_des.setText(desc);// 物品信息
			}
		}else {
			recived_money.setText("");
			tv_thing_des.setText("");
		}
//		tv_deal_num.setText(trade_number);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
				
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//查看订单
			case R.id.tv_search_order:
				
				break;
				//返回
			case R.id.iv_title_back:
				setResult(BILL_DETAIL_ACTIVITY);
				finish();
				break;
			default:
				break;
			}
			
		}
		
	}
	
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
	}
	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
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
