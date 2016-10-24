package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.CollectionRecordsAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 收款记录
 *
 * @author wangqiang
 *
 */
public class CollectionRecordActivity extends SkuaiDiBaseActivity {
	public static final int REALNAME_COLLECTION = 0X1111000;
	//收款统计
	private static final String HTTP_M_KUAIDIHELP_COM_SHOUKUAN_LIST = "http://m.kuaidihelp.com/shoukuan/list?trans_type=offline";
	// 收款记录列表接口
	private static final String PAYMENT_LIST = "payment/plist";
	@BindView(R.id.tv_collection_times) TextView tv_collection_times;// 今日收款几笔
	@BindView(R.id.tv_collection_amount) TextView tv_collection_amount;// 今日收款多少钱
	@BindView(R.id.tv_online_collection_times) TextView collectionCount;// 在线收款几笔
	@BindView(R.id.tv_online_collection_amount) TextView collectionMoney;// 在线收款多少钱
	@BindView(R.id.tv_pay_collection_fee) TextView tv_pay_collection_fee;// 上缴收件费
	@BindView(R.id.lv_record) ListView lv_record;
	@BindView(R.id.pull_refresh_view) PullToRefreshView pullToRefreshView;
	@BindView(R.id.tv_title_des) TextView title;
	@BindView(R.id.tv_more) SkuaidiTextView tvMore;

	List<CollectionRecords> recordsList;
	private CollectionRecordsAdapter adapter;
	private Context context;
	private int page_num = 1;
	private SkuaidiDialog dialog;
	private String payment = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_collection_record);
		context = this;
		ButterKnife.bind(this);
		initview();

	}

	@OnClick({R.id.tv_more})
	public void onClick(View v){
		switch (v.getId()){
			case R.id.tv_more:
				loadWebCommon(WebLoadView.SHOUKUAN_ONLINE_URL);
				break;
		}
	}

	private void initview() {
		title.setText("收款记录");
		tvMore.setText("统计");

		pullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				page_num = 1;
				getPaymentRecords(page_num);
			}
		});

		pullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				page_num += 1;
				getPaymentRecords(page_num);

			}
		});
		initData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REALNAME_COLLECTION && resultCode == RESULT_OK){
			finish();
		}
	}

	private void initData() {
		recordsList = new ArrayList<>();
		adapter = new CollectionRecordsAdapter(context, recordsList);
		lv_record.setAdapter(adapter);
		getPaymentRecords(page_num);
		tv_collection_times.setText(getResources().getString(R.string.text_collection_times, ""));
		tv_collection_amount.setText(getResources().getString(R.string.text_collection_amount, ""));
		lv_record.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if("online".equals(recordsList.get(position).getType())){
					Intent intent=new Intent(context, CollectionDetailActivity.class);
					intent.putExtra("money", recordsList.get(position).getMoney());
					intent.putExtra("tag", "from_list");
					intent.putExtra("order_number", recordsList.get(position).getOrder_number());
					startActivity(intent);
				}else if("offline".equals(recordsList.get(position).getType())){
					Intent intent;
					if (recordsList.get(position).getIsAccounting().equals("1")){
						intent = new Intent(context,CollectionDetailOfflineActivity.class);
					}else {
						intent = new Intent(context, CollectionAccountDetailActivity.class);
					}
					intent.putExtra("detailInfo", recordsList.get(position));
					startActivityForResult(intent,REALNAME_COLLECTION);
				}
			}
		});
		tv_pay_collection_fee.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showProgressDialog( "加载中");
				JSONObject data = new JSONObject();
				try {
					data.put("sname", "fee_turn/get");
					httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void getPaymentRecords(int pageIndex) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", PAYMENT_LIST);
			data.put("get_type", "order.list");
			data.put("trans_type", "offline");
			data.put("page", page_num);
			data.put("page_size", 15);
			data.put("version","1.0");
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private List<CollectionRecords> parseCollectionRecords(String records) {
		List<CollectionRecords> list = new ArrayList<>();
		if(!Utility.isEmpty(records)){
			JSONArray array;
			try {
				array = new JSONArray(records);
				for(int i = 0; i < array.length(); i++){
					JSONObject obj = array.getJSONObject(i);
					CollectionRecords record = new CollectionRecords();
					if("offline".equals(obj.optString("type"))){
						record.setType(obj.optString("type"));
						record.setTran_msg(obj.optString("name"));
						record.setMoney(obj.optString("money"));
						record.setAvail_time(obj.optString("avail_time"));
						record.setDesc(obj.optString("desc"));
						record.setStatus(obj.optInt("status"));
						record.setOfId(obj.optString("ofId"));
						JSONArray arr = obj.optJSONArray("waybill_list");
						StringBuilder builder = new StringBuilder();
						for(int j = 0; j < arr.length(); j++){
							if(j == arr.length() - 1){
								builder.append(arr.optString(j));
							}else{
								builder.append(arr.optString(j)+",");
							}
						}
						record.setOrder_number(builder.toString());
						record.setIsAccounting(obj.optString("is_accounting"));
						record.setId(obj.optString("id"));
					}else if("online".equals(obj.optString("type"))){
						record.setType(obj.optString("type"));
						record.setOrder_number(obj.optString("order_number"));
						record.setMoney(obj.optString("money"));
						record.setTrade_number(obj.optString("trade_number"));
						record.setAvail_time(obj.optString("avail_time"));
						record.setDesc(obj.optString("desc"));
						record.setTran_msg(obj.optString("trans_msg"));
					}
					list.add(record);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return list;

	}

	private void showPayDialog(String str) {
		dialog = new SkuaidiDialog(context);
		dialog.setTitle("上缴收件费");
		dialog.setPositionButtonTitle("上缴");
		dialog.setNegativeButtonTitle("取消");
		dialog.setDonotAutoDismiss(true);
		dialog.isUseEditText(true);
		dialog.setEditTextHint("今日最多可上缴" + str + "元");
		dialog.setEditTextInputTypeStyle(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
		dialog.setEditTextWatcher(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();
				int posDot = temp.indexOf(".");
				if (posDot <= 0) {
					if (temp.matches("^\\d+$")) {
						return;
					} else {
						edt.clear();
						edt.append(temp.replaceAll("\\D", ""));
						return;
					}
				}
				if (temp.length() - posDot - 1 > 2) {
					edt.delete(posDot + 3, posDot + 4);
				}
			}
		});
		dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
			@Override
			public void onClick(View v) {
				if (Utility.isEmpty(dialog.getEditTextContent())) {
					dialog.showEditTextTermsArea(true);
					dialog.setEditTextTermsText("请输入上缴金额");
					dialog.showTermsSelect(false);
					dialog.setEditTextTermsTextColor(R.color.red1);
				} else {
					JSONObject data = new JSONObject();
					try {
						data.put("sname", "fee_turn/set");
						data.put("money", dialog.getEditTextContent());
						payment = dialog.getEditTextContent();
						httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
		});
		dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener(){

			@Override
			public void onClick() {
				dialog.setDismiss();
			}
		});
		dialog.showDialog();
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		KLog.json(json);
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (result == null) {
			pullToRefreshView.onHeaderRefreshComplete();
			pullToRefreshView.onFooterRefreshComplete();
			return;
		}
		if (PAYMENT_LIST.equals(sname)) {
			JSONObject summary = result.optJSONObject("summary");
			if (summary != null&&page_num == 1) {
				String total_num = summary.optString("total_num");
				String total_money = summary.optString("total_money");
				String online_num = summary.optString("online_num");
				String online_money = summary.optString("online_money");

				total_num = TextUtils.isEmpty(total_num) ? "0" : total_num;
				total_money = TextUtils.isEmpty(total_money) ? "0.00" : total_money;
				online_num = TextUtils.isEmpty(online_num) ? "0" : online_num;
				online_money = TextUtils.isEmpty(online_money) ? "0.00" : online_money;

				tv_collection_times.setText(getResources().getString(R.string.text_collection_times, total_num));
				tv_collection_amount.setText(getResources().getString(R.string.text_collection_amount, total_money));
				collectionCount.setText(getResources().getString(R.string.text_online_collection_times,online_num));
				collectionMoney.setText(getResources().getString(R.string.text_online_collection_amount,online_money));
			}
			List<CollectionRecords> list = parseCollectionRecords(result.optString("list"));
			if (page_num == 1) {
				recordsList.clear();
				recordsList.addAll(list);
			} else {
				recordsList.addAll(list);
			}
			adapter.notifyDataSetChanged();
			pullToRefreshView.onHeaderRefreshComplete();
			pullToRefreshView.onFooterRefreshComplete();
		}else if("fee_turn/get".equals(sname)){
			dismissProgressDialog();
			String state = result.optString("state");
			String money = result.optString("today_turn_money");
			if("on".equals(state)){
				showPayDialog(money);
			}else{
				Utility.showFailDialog(context, result.optString("desc"), tv_pay_collection_fee.getRootView());
			}

		}else if("fee_turn/set".equals(sname)){
			dialog.setDismiss();
			UtilToolkit.showToast("成功上缴收件费" + payment + "\n请到资金明细中查看");
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (PAYMENT_LIST.equals(sname)) {
			if (!TextUtils.isEmpty(result))
				UtilToolkit.showToast( result);
			pullToRefreshView.onHeaderRefreshComplete();
			pullToRefreshView.onFooterRefreshComplete();
		}else if("fee_turn/get".equals(sname)){
			dismissProgressDialog();
			Utility.showFailDialog(context, result, tv_pay_collection_fee.getRootView());
		}else if("fee_turn/set".equals(sname)){
			dialog.setDismiss();
			Utility.showFailDialog(context, result, tv_pay_collection_fee.getRootView());
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
