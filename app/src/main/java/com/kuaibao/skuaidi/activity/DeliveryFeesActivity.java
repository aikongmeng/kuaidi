package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.DeliveryFeesAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiNotifyTextVIew;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.DeliveryFees;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryFeesActivity extends SkuaiDiBaseActivity {
	private static final String WAYBILL_FEE = "waybill_fee/index";
	private ListView lv_record;
	private TextView tv_money, tv_title_desc, tv_count, tv_notify, tv_no_data_show;
	private SkuaidiTextView tv_more;
	private SkuaidiNotifyTextVIew title_notify;
	private ImageView iv_face;
	private Context context;
	DeliveryFeesAdapter adapter;
	List<DeliveryFees> mList;
	private int pageIndex = 1;
	private int total_page;
	private RelativeLayout fl_no_data;
	private LinearLayout ll_bottom;
	private String fromType; //来源界面的标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.activity_delivery_fees);
		fromType = getIntent().getStringExtra("item_type");
		initView();
		if("zongbu".equals(fromType) && !"T".equals(SkuaidiSpf.getIsBossSetFee())){
			fl_no_data.setVisibility(View.VISIBLE);
			iv_face.setImageResource(R.drawable.headquarters_hint_icon);
			tv_no_data_show.setText("暂未开通总部直发");
		}else if("wangdian".equals(fromType) && !"T".equals(SkuaidiSpf.getIsShopSetFee())){
			fl_no_data.setVisibility(View.VISIBLE);
			iv_face.setImageResource(R.drawable.branch_hint_icon);
			tv_no_data_show.setText("暂未开通网点直发");
		}else if("check".equals(fromType) && !"T".equals(SkuaidiSpf.getIsSetFee())){
			fl_no_data.setVisibility(View.VISIBLE);
			iv_face.setImageResource(R.drawable.branch_hint_icon);
			tv_no_data_show.setText("暂未设置派费");
		}else{
			getDevileryFees(pageIndex);
		}
	}

	private void initView() {
		fl_no_data = (RelativeLayout) findViewById(R.id.fl_no_data);
		lv_record = (ListView) findViewById(R.id.lv_record);
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		tv_title_desc = (TextView) findViewById(R.id.tv_title_des);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		tv_notify = (TextView) findViewById(R.id.tv_notify);
		title_notify = (SkuaidiNotifyTextVIew) findViewById(R.id.title_notify);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_no_data_show = (TextView) findViewById(R.id.tv_no_data_show);
		iv_face = (ImageView) findViewById(R.id.iv_face);
		if("zongbu".equals(fromType)){
			tv_title_desc.setText("总部直发清单");
			tv_more.setText("派费统计");
//			tv_notify.setText("直发派费说明。点击查看详情");
			title_notify.setVisibility(View.GONE);
			tv_count.setText("今日总部直发:");
		}else if("wangdian".equals(fromType)){
			tv_title_desc.setText("网点直发清单");
			tv_more.setText("派费统计");
//			tv_notify.setText("直发派费说明。点击查看详情");
			title_notify.setVisibility(View.GONE);
			tv_count.setText("今日网点直发:");
		}else if("check".equals(fromType)){
			tv_title_desc.setText("派费对账单");
			tv_more.setText("历史对账");
			tv_notify.setText("对账单金额来自于自己设置的全额派费");
			tv_count.setText("今日对账金额:");
		}
		lv_record.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position != mList.size()) {
					Intent intent = new Intent(context, CopyOfFindExpressResultActivity.class);
					intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
					intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
					intent.putExtra("order_number", mList.get(position).getWaybill_no());
					startActivity(intent);
				}
			}

		});
		mList = new ArrayList<DeliveryFees>();
		adapter = new DeliveryFeesAdapter(context, mList);
		View ListfootView = LayoutInflater.from(context).inflate(R.layout.fund_list_load_more_layout, null, false);
		ListfootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (total_page <= pageIndex) {
					UtilToolkit.showToast( "没有更多内容");
				} else {
					pageIndex++;
					getDevileryFees(pageIndex);
				}
			}
		});
		lv_record.addFooterView(ListfootView);// 插入到列表最下面
		lv_record.setAdapter(adapter);
		tv_notify.setMovementMethod(LinkMovementMethod.getInstance());
		tv_notify.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
				Drawable drawable = tv_notify.getCompoundDrawables()[2];
				// 如果右边没有图片，不再处理
				if (drawable == null)
					return false;
				// 如果不是按下事件，不再处理
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				// 计算点击的位置，如果点击到小叉删除按钮，则隐藏
				if (event.getX() > tv_notify.getWidth() - tv_notify.getPaddingRight() - drawable.getIntrinsicWidth()) {
					findViewById(R.id.title_notify).setVisibility(View.GONE);
				}
//				else {
//					if("申通快递".equals(SkuaidiSpf.getLoginUser().getExpressFirm())){
//						loadWeb("http://m.kuaidihelp.com/help/pf_exp?brand=sto", "派费说明");
//					}else if("中通快递".equals(SkuaidiSpf.getLoginUser().getExpressFirm())){
//						loadWeb("http://m.kuaidihelp.com/help/pf_exp?brand=zto", "派费说明");
//					}else{
//						loadWeb("http://m.kuaidihelp.com/help/pf_exp", "派费说明");
//					}
//				}
				return false;

			}
		});
		lv_record = (ListView) findViewById(R.id.lv_record);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				loadWeb("http://m.kuaidihelp.com/waybill/list", "派费统计");
				if("zongbu".equals(fromType)){
					loadWeb("http://m.kuaidihelp.com/waybill/list", "派费统计");
				}else if("wangdian".equals(fromType)){
					loadWeb("http://m.kuaidihelp.com/waybill/list?type=shop", "派费统计");
				}else if("check".equals(fromType)){
					loadWeb("http://m.kuaidihelp.com/waybill/list?type=person", "派费统计");
				}
			}
		});
	}

	private void getDevileryFees(int pageIndex) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", WAYBILL_FEE);
			if("zongbu".equals(fromType)){
				data.put("type", "boss");
			}else if("wangdian".equals(fromType)){
				data.put("type", "shop");
			}else if("check".equals(fromType)){
				data.put("type", "person");
			}
			data.put("page", pageIndex);
			data.put("page_size", 5);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
			showProgressDialog( "正在加载数据..");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissProgressDialog();
		}
	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (result == null) {
			dismissProgressDialog();
			return;
		}
		if (WAYBILL_FEE.equals(sname)) {
			JSONObject today_award = result.optJSONObject("today_award");
			if (total_page == 0) {
				String total = result.optString("total_page");
				try {
					total_page = Integer.parseInt(total);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (today_award != null) {
				String money = today_award.optString("money");
				if (!TextUtils.isEmpty(money) && !"null".equals(money)) {
					tv_money.setText(money);
				}
			}
			List<DeliveryFees> list = parseDliveryFees(result.optString("list"));
			if (pageIndex == 1)
				mList.clear();
			if (list != null && list.size() != 0)
				mList.addAll(list);

			if (mList.size() == 0) {
				fl_no_data.setVisibility(View.VISIBLE);
				if("zongbu".equals(fromType)){
					tv_no_data_show.setText("无总部直发记录");
				}else if("wangdian".equals(fromType)){
					tv_no_data_show.setText("无网点直发记录");
				}else if("check".equals(fromType)){
					tv_no_data_show.setText("无派费记录");
				}
			} else {
				lv_record.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			dismissProgressDialog();
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (WAYBILL_FEE.equals(sname)) {
			dismissProgressDialog();
			UtilToolkit.showToast( result);
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		// TODO Auto-generated method stub

	}

	private List<DeliveryFees> parseDliveryFees(String str) {
		List<DeliveryFees> list = null;
		Gson gson = new Gson();
		try {
			list = gson.fromJson(str, new TypeToken<List<DeliveryFees>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return list;

	}

}
