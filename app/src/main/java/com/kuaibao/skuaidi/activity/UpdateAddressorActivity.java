package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.SkuaidiEditText;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.AddressDB;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.AreaItem;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateAddressorActivity extends SkuaiDiBaseActivity implements GeocodeSearch.OnGeocodeSearchListener , OnClickListener{

	private ImageView iv_title_back, iv_refresh_location;
	private TextView tv_title_des, tv_addressor_choose_address, tv_recipient_choose_address, tv_submit_info,
			tv_write_auto_sender, tv_write_auto_receiver;
	private Button bt_title_more, btn_submit, btn_cancle;
	private SkuaidiEditText tv_addressor_name, et_sender_phone, tv_addressor_address, et_recipient_addressor_name,
			et_recipient_phone, et_recipient_addressor_address, et_item_info, et_item_weight, et_amount_money;
	private View rl_item_info, rl_addressor_address, rl_recipient_addressor_address,rl_amount_money, wheelView, viMasker;
	private Order order;
	private boolean orderChaned = false;
	private OptionsPickerView pvOptions;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
	private String mSendProvince, mSendCity, mSendCountry, mReceiptProvince, mReceiptCity, mReceiptCountry;
	private ArrayList<AreaItem> provinceOptions = new ArrayList<>();
	private ArrayList<ArrayList<ArrayList<AreaItem>>> districtOptions = new ArrayList<>();
	private ArrayList<ArrayList<AreaItem>> cityOptions = new ArrayList<>();
	private GeocodeSearch mgGeocodeSearch;
	private String from;
	private String PARSEINFO = "order/parseInfo";
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.updateaddressor);
		context = this;
		if (getIntent().hasExtra("isEditItemInfo")) {
			order = (Order) getIntent().getSerializableExtra("order");
		} else {
			order = (Order) getIntent().getSerializableExtra("update");
		}
		getControl();
		initDatas();
		addListener();
		if(mgGeocodeSearch==null){
			mgGeocodeSearch = new GeocodeSearch(this);
			mgGeocodeSearch.setOnGeocodeSearchListener(this);
		}
	}

	private void getControl() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		tv_write_auto_sender = (TextView) findViewById(R.id.tv_write_auto_sender);
		tv_addressor_address = (SkuaidiEditText) findViewById(R.id.tv_addressor_address);
		tv_addressor_name = (SkuaidiEditText) findViewById(R.id.tv_addressor_name);
		tv_addressor_choose_address = (TextView) findViewById(R.id.tv_addressor_choose_address);
		iv_refresh_location = (ImageView) findViewById(R.id.iv_refresh_location);
		et_recipient_addressor_address = (SkuaidiEditText) findViewById(R.id.tv_recipient_addressor_address);
		et_recipient_addressor_name = (SkuaidiEditText) findViewById(R.id.tv_recipient_addressor_name);
		et_recipient_phone = (SkuaidiEditText) findViewById(R.id.et_recipient_phone);
		tv_write_auto_receiver = (TextView) findViewById(R.id.tv_write_auto_receiver);
		tv_recipient_choose_address = (TextView) findViewById(R.id.tv_recipient_choose_address);
		et_sender_phone = (SkuaidiEditText) findViewById(R.id.et_sender_phone);
		et_item_info = (SkuaidiEditText) findViewById(R.id.et_item_info);
		et_item_weight = (SkuaidiEditText) findViewById(R.id.et_item_weight);
		et_amount_money = (SkuaidiEditText) findViewById(R.id.et_amount_money);
//		rl_item_info = findViewById(R.id.rl_item_info);
		rl_addressor_address = findViewById(R.id.rl_addressor_address);
		rl_recipient_addressor_address = findViewById(R.id.rl_recipient_addressor_address);
		rl_amount_money = findViewById(R.id.rl_amount_money);
		tv_submit_info = (TextView) findViewById(R.id.tv_submit_info);
		viMasker = findViewById(R.id.viMasker);
	}

	private void initDatas() {
		if(Utility.isEmpty(order.getId())){
			tv_title_des.setText("创建订单");
		}else{
			tv_title_des.setText("完善信息");
		}
		bt_title_more.setText("保存");
		bt_title_more.setVisibility(View.INVISIBLE);
		tv_addressor_name.setText(StringUtil.isEmpty(order.getSenderName()));
		mSendProvince = order.getSenderProvince();
		mSendCity = order.getSenderCity();
		mSendCountry = order.getSenderCountry();
		mReceiptProvince = order.getReceiptProvince();
		mReceiptCity = order.getReceiptCity();
		mReceiptCountry = order.getReceiptCountry();
		if (!TextUtils.isEmpty(order.getSenderProvince()) || !TextUtils.isEmpty(order.getSenderCity()) || !TextUtils.isEmpty(order.getSenderCountry())) {
			tv_addressor_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
			tv_addressor_choose_address.setText(order.getSenderProvince() + order.getSenderCity() + order.getSenderCountry());
		}
		tv_addressor_address.setText(StringUtil.isEmpty(order.getSenderDetailAddress()));
		et_sender_phone.setText(StringUtil.isEmpty(order.getSenderPhone()));
		et_recipient_addressor_name.setText(StringUtil.isEmpty(order.getName()));

		if (!TextUtils.isEmpty(order.getReceiptProvince()) || !TextUtils.isEmpty(order.getReceiptCity()) || !TextUtils.isEmpty(order.getReceiptCountry())) {
			tv_recipient_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
			tv_recipient_choose_address.setText(order.getReceiptProvince() + order.getReceiptCity() + order.getReceiptCountry());
		}
		et_recipient_addressor_address.setText(StringUtil.isEmpty(order.getReceiptDetailAddress()));
		et_sender_phone.setText(StringUtil.isEmpty(order.getSenderPhone()));

		et_recipient_addressor_name.setText(StringUtil.isEmpty(order.getName()));
		et_recipient_phone.setText(StringUtil.isEmpty(order.getPhone()));
		et_item_info.setText(StringUtil.isEmpty(order.getArticleInfo()));
		et_item_weight.setText(StringUtil.isEmpty(order.getCharging_weight()));
		et_amount_money.setText(StringUtil.isEmpty(order.getCollection_amount()));
		if("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
			rl_amount_money.setVisibility(View.VISIBLE);
		}
		if (!Utility.isEmpty(et_sender_phone.getText().toString())) {
			tv_submit_info.setEnabled(true);
			tv_submit_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
		} else {
			tv_submit_info.setEnabled(false);
			tv_submit_info.setBackgroundResource(R.drawable.shape_btn_gray1);

		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				initAddressData();
			}
		}).start();
	}

	private void exit() {
		if (orderChaned) {
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(UpdateAddressorActivity.this);
			dialog.setTitleGray("温馨提示");
			dialog.setTitleColor(R.color.title_bg);
			dialog.setContentGray("修改了信息还未保存，确认现在\n返回吗？ ");
			dialog.setNegativeButtonTextGray("继续填写");
			dialog.setPositionButtonTextGray("立即离开");
			dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			dialog.showDialogGray(iv_title_back.getRootView());
		} else {
			finish();
		}
	}


	private void setPickerView(final int viewID) {
		// 选项选择器
		pvOptions = new OptionsPickerView(this);
		// 三级联动效果
		pvOptions.setPicker(provinceOptions, cityOptions, districtOptions, true);
		pvOptions.setCyclic(false, false, false);
		// 设置默认选中的item
		pvOptions.setSelectOptions(0, 0, 0);
		pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				AreaItem a = provinceOptions.get(options1);
				AreaItem b = cityOptions.get(options1).get(option2);
				AreaItem c = districtOptions.get(options1).get(option2).get(options3);
				String curPro = a.getName();
				String curCity = b.getName();
				String curCountry = c.getName();
				KLog.i("tag", curPro + "********" + curCity + "********" + curCountry);
				if (viewID == rl_recipient_addressor_address.getId()) {
					mReceiptProvince = curPro;
					mReceiptCity = curCity;
					mReceiptCountry = curCountry;
					tv_recipient_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
					tv_recipient_choose_address.setText(mReceiptProvince + mReceiptCity + mReceiptCountry);
					order.setReceiptProvince(curPro);
					order.setReceiptCity(curCity);
					order.setReceiptCountry(curCountry);
				} else if (viewID == rl_addressor_address.getId()) {
					mSendProvince = curPro;
					mSendCity = curCity;
					mSendCountry = curCountry;
					tv_addressor_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
					tv_addressor_choose_address.setText(mSendProvince + mSendCity + mSendCountry);
					order.setSenderProvince(curPro);
					order.setSenderCity(curCity);
					order.setSenderCountry(curCountry);
				}
				viMasker.setVisibility(View.GONE);
			}

		});
		pvOptions.show();
	}

	private void addListener() {
		addTextWatcher(et_sender_phone);
		addTextWatcher(et_item_weight);
		addTextWatcher(et_amount_money);
		iv_title_back.setOnClickListener(this);
		iv_title_back.setOnClickListener(this);
		tv_submit_info.setOnClickListener(this);
		rl_addressor_address.setOnClickListener(this);
		rl_recipient_addressor_address.setOnClickListener(this);
		iv_refresh_location.setOnClickListener(this);
		tv_write_auto_sender.setOnClickListener(this);
		tv_write_auto_receiver.setOnClickListener(this);
		automaticPopsoftKeyboard();
	}

	private void addTextWatcher(final TextView tv){
		tv.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String temp;
				int posDot;
				switch (tv.getId()){
					case R.id.et_sender_phone:
						if (!Utility.isEmpty(s)) {
							orderChaned = true;
						}
						if (!Utility.isEmpty(et_sender_phone.getText().toString())) {
							tv_submit_info.setEnabled(true);
							tv_submit_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
						} else {
							tv_submit_info.setEnabled(false);
							tv_submit_info.setBackgroundResource(R.drawable.shape_btn_gray1);

						}
						break;
					case R.id.et_item_weight:
						temp = s.toString();
						posDot = temp.indexOf(".");
						if (posDot <= 0) {
							if (temp.matches("^\\d+$")) {
								return;
							} else {
								s.clear();
								s.append(temp.replaceAll("\\D", ""));
								return;
							}
						}
						if (temp.length() - posDot - 1 > 2) {
							s.delete(posDot + 3, posDot + 4);
						}
						break;
					case R.id.et_amount_money:
						temp = s.toString();
						posDot = temp.indexOf(".");
						if (posDot <= 0) {
							if (temp.matches("^\\d+$")) {
								return;
							} else {
								s.clear();
								s.append(temp.replaceAll("\\D", ""));
								return;
							}
						}
						if (temp.length() - posDot - 1 > 2) {
							s.delete(posDot + 3, posDot + 4);
						}
						break;
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_title_back:
				exit();
				break;
			case R.id.tv_submit_info:
				saveData();
				break;
			case R.id.rl_addressor_address:
				hideSoftKeyboard(v);
//				bottom_vMasker.setVisibility(View.VISIBLE);
				viMasker.setVisibility(View.VISIBLE);
				setPickerView(rl_addressor_address.getId());
				break;
			case R.id.rl_recipient_addressor_address:
				hideSoftKeyboard(v);
//				bottom_vMasker.setVisibility(View.VISIBLE);
				viMasker.setVisibility(View.VISIBLE);
				setPickerView(rl_recipient_addressor_address.getId());
				break;
			case R.id.iv_refresh_location:
				LatitudeAndLongitude lalo = SkuaidiSpf.getLatitudeOrLongitude(context);
				if(Utility.isEmpty(lalo.getLatitude()) || Utility.isEmpty(lalo.getLongitude())){
					showNoPermissionDialog();
					return;
				}
				showProgressDialog( "地址定位中...");
				LatLonPoint latLonPoint = new LatLonPoint(Double.parseDouble(lalo.getLatitude()), Double.parseDouble(lalo.getLongitude()));
				RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
				mgGeocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
				break;
			case R.id.tv_write_auto_sender:
				from = "sender";
				showDialog();
				break;
			case R.id.tv_write_auto_receiver:
				from = "receiver";
				showDialog();
				break;
		}
	}

	/**
	 * 智能录入对话框
	 */
	private void showDialog(){
		final SkuaidiDialog dialog = new SkuaidiDialog(context);
		dialog.setTitle("智能录入");
		dialog.isUseBigEditText(true);
		dialog.setBigEditTextHint("依次输入地址、姓名、电话，用空格隔开，地址里不要有空格，如：浙江省江山市中山路1号 张三 18616161616");
		dialog.setPositionButtonTitle("确认");
		dialog.setNegativeButtonTitle("取消");
		dialog.setDonotAutoDismiss(true);// 设置所有按钮不自动隐藏
		dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
		dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

			@Override
			public void onClick(View v) {
				showProgressDialog("加载中...");
				String temp = dialog.getBigEditTextContent().trim();
				JSONObject data = new JSONObject();
				try {
					data.put("sname", PARSEINFO);
					data.put("fullInfo", temp);
					httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dialog.showSoftInput(false);
				dialog.setDismiss();

			}});
		dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
			@Override
			public void onClick() {
				dialog.showSoftInput(false);
				dialog.setDismiss();
			}
		});
		dialog.showDialog();

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		dismissProgressDialog();
		if (sname.equals("order.im.senduser.edit")) {
			KLog.i("tag", "result:" + result);
			setResult(224);
			finish();
		}else if("order/add2cm".equals(sname)){
			order.setId(result);
			newDB.addOrderInfo(order);
			setResult(224);
			finish();
		}else if(PARSEINFO.equals(sname)){
			JSONObject obj = null;
			try {
				obj = new JSONObject(result);
				if(!Utility.isEmpty(obj.optString("area"))){
					String area = StringUtil.isEmpty(obj.optString("area"));
					String[] areas = area.split(",");
					if("sender".equals(from)){
						mSendProvince = areas[0];
						mSendCity = areas.length > 1 ? areas[1]:"";
						mSendCountry = areas.length > 2 ? areas[2]:"";
						tv_addressor_choose_address.setText(area.replaceAll(",",""));
						tv_addressor_address.setText(obj.optString("address"));
						et_sender_phone.setText(obj.optString("phone"));
						tv_addressor_name.setText(obj.optString("name"));
					}else if("receiver".equals(from)){
						mReceiptProvince = areas[0];
						mReceiptCity = areas.length > 1 ? areas[1]:"";
						mReceiptCountry = areas.length > 2 ? areas[2]:"";
						tv_recipient_choose_address.setText(area.replaceAll(",",""));
						et_recipient_addressor_address.setText(obj.optString("address"));
						et_recipient_phone.setText(obj.optString("phone"));
						et_recipient_addressor_name.setText(obj.optString("name"));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		UtilToolkit.showToast(result);
	}

	/**
	 * 判断是否是一个正确的电话号码
	 *
	 * @param inputPhone
	 * @return
	 */
	private boolean isTruePhone(String inputPhone) {
		if (!Utility.isEmpty(inputPhone) && inputPhone.startsWith("1") && inputPhone.length() == 11) {
			return true;
		} else if (!Utility.isEmpty(inputPhone) && inputPhone.startsWith("0") && inputPhone.length() <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * 更新本地和服务器端的订单数据后，关闭当前页面
	 */
	private void saveData() {
		boolean flag = false;
		String content = "";
		if(!isTruePhone(et_sender_phone.getText().toString()) || (!Utility.isEmpty(et_recipient_phone.getText().toString())
				&& !isTruePhone(et_recipient_phone.getText().toString()))){
			flag = true;
			content = "电话填写有误，请输入正确\n的号码";
		}else if((!"省、市、区".equals(tv_addressor_choose_address.getText().toString()) && TextUtils.isEmpty(mSendCountry)) ||
				(!"省、市、区".equals(tv_recipient_choose_address.getText().toString()) && TextUtils.isEmpty(mReceiptCountry))){
			flag = true;
			content = "省市区信息不完整，请重新填写";
		}
		if (flag) {
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(UpdateAddressorActivity.this);
			dialog.setTitleGray("温馨提示");
			dialog.setTitleColor(R.color.title_bg);
			dialog.setContentGray(content);
			dialog.isUseMiddleBtnStyle(true);
			dialog.setMiddleButtonTextGray("我知道了");
			dialog.showDialogGray(et_sender_phone.getRootView());
			return;
		}

		if (Utility.isEmpty(et_sender_phone.getText().toString())) {
			UtilToolkit.showToast("请完善信息");
			return;
		}

		order.setAddress(tv_recipient_choose_address.getText() + et_recipient_addressor_address.getText().toString());
		order.setAddressHead(tv_recipient_choose_address.getText().toString());
		order.setReceiptDetailAddress(et_recipient_addressor_address.getText().toString());
		order.setName(et_recipient_addressor_name.getText().toString());
		order.setPhone(et_recipient_phone.getText().toString());
		order.setReceiptProvince(mReceiptProvince);
		order.setReceiptCity(mReceiptCity);
		order.setReceiptCountry(mReceiptCountry);
		order.setSenderName(tv_addressor_name.getText().toString());
		order.setSenderAddress(tv_addressor_choose_address.getText() + tv_addressor_address.getText().toString());
		order.setSenderDetailAddress(tv_addressor_address.getText().toString());
		order.setSenderPhone(et_sender_phone.getText().toString());
		order.setSenderProvince(mSendProvince);
		order.setSenderCity(mSendCity);
		order.setSenderCountry(mSendCountry);
		order.setArticleInfo(et_item_info.getText().toString());

		if (getIntent().hasExtra("isEditItemInfo")) {
			order.setArticleInfo(et_item_info.getText().toString());
		}
		if(!Utility.isEmpty(order.getId())){
			newDB.updateOrderInfo(order);
		}

		// 调接口，获取模板内容
		if (Utility.isNetworkConnected() == false) {
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(
					UpdateAddressorActivity.this);
			dialog.setTitleGray("提示");
			dialog.setTitleSkinColor("main_color");
			dialog.setContentGray("您没有连接网络，是否进行设置？");
			dialog.setPositionButtonTextGray("设置");
			dialog.setNegativeButtonTextGray("取消");
			dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS);
					startActivity(intent);
				}
			});
			dialog.showDialogGray(tv_addressor_name);
		} else {
			showProgressDialog( "正在保存...");
			JSONObject data;
			try {
				if(Utility.isEmpty(order.getId())) {
					data = new JSONObject();
					data.put("sname", "order/add2cm");
					data.put("counterman_mobile", SkuaidiSpf.getLoginUser().getPhoneNumber());
					data.put("send_user", tv_addressor_name.getText().toString());
					data.put("send_user_mobile",et_sender_phone.getText().toString());
					data.put("send_address_id","");
					data.put("send_address_province",mSendProvince);
					data.put("send_address_city",mSendCity);
					data.put("send_address_county",mSendCountry);
					data.put("send_address_detail",tv_addressor_address.getText().toString());
					data.put("note", "");
					data.put("article_info", et_item_info.getText().toString());
					data.put("charging_weight", et_item_weight.getText().toString());
					data.put("collection_amount", et_amount_money.getText().toString());
					data.put("receive_user",et_recipient_addressor_name.getText().toString());
					data.put("receive_user_mobile", et_recipient_phone.getText().toString());
					data.put("receive_address_id", "");
					data.put("receive_address_province", mReceiptProvince);
					data.put("receive_address_city", mReceiptCity);
					data.put("receive_address_county", mReceiptCountry);
					data.put("receive_address_detail",et_recipient_addressor_address.getText().toString());
					httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
				}else{
					data = new JSONObject();
					data.put("sname", "order.im.senduser.edit");
					data.put("order_number", order.getId());
					data.put("senderName", tv_addressor_name.getText().toString());
					data.put("senderPhone", et_sender_phone.getText().toString());
					data.put("senderAddress", tv_addressor_address.getText().toString());
					data.put("send_address_province", mSendProvince);
					data.put("send_address_city", mSendCity);
					data.put("send_address_county", mSendCountry);
					data.put("name", et_recipient_addressor_name.getText().toString());
					data.put("phone", et_recipient_phone.getText().toString());
					data.put("address", et_recipient_addressor_address.getText().toString());
					data.put("addressHead", order.getAddressHead());
					data.put("receive_address_province", mReceiptProvince);
					data.put("receive_address_city", mReceiptCity);
					data.put("receive_address_county", mReceiptCountry);
					data.put("articleInfo", et_item_info.getText());
					data.put("charging_weight", et_item_weight.getText().toString());
					data.put("collection_amount", et_amount_money.getText().toString());
					httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				dismissProgressDialog();
				UtilToolkit.showToast("数据提交失败");
			}
		}
		if (getIntent().hasExtra("isEditItemInfo")) {
			Intent intent = new Intent();
			intent.putExtra("order", order);
			setResult(PrintExpressBillActivity.RESPONSE_CODE, intent);
		}
	}

	/**
	 * 自动弹出软键盘
	 */
	private void automaticPopsoftKeyboard() {
		tv_addressor_name.setFocusable(true);
		tv_addressor_name.requestFocus();
		tv_addressor_name.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) tv_addressor_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(tv_addressor_name, 0);
			}
		},500);
	}

	/**
	 * 隐藏软键盘
	 *
	 * @param
	 */
	private void hideSoftKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void finish() {
		hideSoftKeyboard(tv_addressor_name);
		super.finish();
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname,
											 String msg, JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if (code.equals("7") && null != result) {
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 初始化省市区信息
	 */
	private void initAddressData(){
		List<AreaItem> pros = AddressDB.getAllProInfoStrs();
		for (int i = 0; i < pros.size(); i++) {
			provinceOptions.add(pros.get(i));
			ArrayList<AreaItem> citys = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(pros.get(i).getId());
			cityOptions.add(citys);
			ArrayList<ArrayList<AreaItem>> tempList=new ArrayList<>();
			for (int j = 0; j < citys.size(); j++) {
				ArrayList<AreaItem> mDistricts = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(citys.get(j).getId());
				tempList.add(mDistricts);
			}
			districtOptions.add(tempList);
		}
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int i) {
		KLog.i("kb","onRegeocodeSearched:rCode="+i+";RegeocodeResult:--->"+result.getRegeocodeAddress().getFormatAddress());
		dismissProgressDialog();
		RegeocodeAddress address = result.getRegeocodeAddress();
		if(!Utility.isEmpty(address)) {
			order.setSenderProvince(address.getProvince());
			String city = TextUtils.isEmpty(address.getCity())?address.getProvince():address.getCity();
			order.setSenderCity(city);
			order.setSenderCountry(address.getDistrict());
			mSendProvince = address.getProvince();
			mSendCity = city;
			mSendCountry = address.getDistrict();
			tv_addressor_choose_address.setTextColor(getResources().getColor(R.color.gray_1));
			tv_addressor_choose_address.setText(address.getProvince() + city + address.getDistrict());
			int index = address.getFormatAddress().indexOf(address.getDistrict())+address.getDistrict().length();
			tv_addressor_address.setText(address.getFormatAddress().substring(index));
		}
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int i) {
		KLog.i("kb","onRegeocodeSearched:rCode="+i+";RegeocodeResult:--->");
		dismissProgressDialog();
	}

	private void showNoPermissionDialog(){
		SkuaidiDialog skuaidiDialog = new SkuaidiDialog(UpdateAddressorActivity.this);
		skuaidiDialog.setTitle("提示");
		skuaidiDialog.setContent("地址信息获取失败，可能是定位权限未打开。请到手机的设置-应用-快递员-权限管理-定位-设为允许");
		skuaidiDialog.isUseEditText(false);
		skuaidiDialog.setPositionButtonTitle("去设置");
		skuaidiDialog.setNegativeButtonTitle("取消");
		skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
				SKuaidiApplication.getInstance().exit();
			}
		});
		skuaidiDialog.showDialog();
	}
}
