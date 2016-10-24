package com.kuaibao.skuaidi.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.bluetooth.BluetoothService;
import com.kuaibao.skuaidi.bluetooth.printer.BluetoothPrinterUtil;
import com.kuaibao.skuaidi.bluetooth.printer.KMPrinter;
import com.kuaibao.skuaidi.bluetooth.printer.PrintDataService;
import com.kuaibao.skuaidi.bluetooth.printer.PrinterBase;
import com.kuaibao.skuaidi.bluetooth.printer.QRPrinter;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaiDiPopupWindow;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.BarcodeUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * 打印电子面单
 * @author xy
 *
 */
public class PrintExpressBillActivity extends SkuaiDiBaseActivity implements View.OnClickListener{
	
	public static final int REQEUST_CODE = 0x98;
	public static final int RESPONSE_CODE = 0x99;
	private static final String ACT_USE = "express_use";
	private static final String ACT_GET = "express_number_get";
	private static final String PRINT_STATUS = "order/orderPrint";
	private List<BluetoothDevice> devices;
	private BluetoothService bluetoothService;
	private SkuaidiNewDB newDB;
	private ImageView iv_express_barcode, iv_sender_phone, iv_shou_phone;
	private TextView tv_waybill_num,tv_sender_name,tv_sender_address,tv_sender_phone,title,
	                 tv_shou_name,tv_shou_address,tv_shou_phone,tv_article_info,tv_adress_head, tv_thing_weight;
	private View tv_adress_head_edit,tv_manage,ll_customer_write,ll_customer_qrcode_write,
				 ll_manual_input,tv_print_preview,tv_onekey_print;
	private Order order;
	
	private Context context;
	
	private static final String NOTICE_TITLE = "发短信通知客户填写信息";
	
	private String num;
	
	private List<String> deviceNames;
	
	private BluetoothDevice device;
	
	private boolean isHaveConected = false;
	private BluetoothAdapter btAdapter;
	private PrinterBase myPrinter;
	private SkuaiDiPopupWindow inputWindow;
	private boolean noPrinting = true;//没有打印任务
	private Handler handler = new Handler();
	private int time = 2000;

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				if("OK".equals(myPrinter.getPrinterStatus())){
					dismissProgressDialog();
					noPrinting = true;
					UtilToolkit.showToast( "打印成功");
					updatePrintStatus();
				}else if("Printing".equals(myPrinter.getPrinterStatus())){
					handler.postDelayed(this, time);
				}else {
					dismissProgressDialog();
					noPrinting = true;
					Utility.showFailDialog(context, "打印失败", iv_express_barcode.getRootView());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.print_express_bill_layout);
		context = this;
		newDB = SkuaidiNewDB.getInstance();
		bluetoothService = BluetoothService.getService(getApplicationContext());
		getControl();
		initDatas(false);
		addListener();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(TextUtils.isEmpty(num)){
			requestHttp(ACT_GET);
		}
	}

	private void getControl(){
		
		title = (TextView) findViewById(R.id.tv_title_des);
		iv_express_barcode = (ImageView) findViewById(R.id.iv_express_barcode);
		
		tv_waybill_num = (TextView) findViewById(R.id.tv_waybill_num);
		tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
		tv_sender_address = (TextView) findViewById(R.id.tv_sender_address);
		tv_sender_phone = (TextView) findViewById(R.id.tv_sender_phone);
		iv_sender_phone = (ImageView) findViewById(R.id.iv_sender_phone);
		tv_shou_name = (TextView) findViewById(R.id.tv_shou_name);
		tv_shou_address = (TextView) findViewById(R.id.tv_shou_address);
		tv_shou_phone = (TextView) findViewById(R.id.tv_shou_phone);
		iv_shou_phone = (ImageView) findViewById(R.id.iv_shou_phone);
		tv_article_info = (TextView) findViewById(R.id.tv_article_info);
		tv_thing_weight = (TextView) findViewById(R.id.tv_thing_weight);
		tv_adress_head = (TextView) findViewById(R.id.tv_adress_head);
		
		tv_manage = findViewById(R.id.tv_manage);
		tv_adress_head_edit = findViewById(R.id.tv_adress_head_edit);
		ll_customer_write = findViewById(R.id.ll_customer_write);
		ll_customer_qrcode_write = findViewById(R.id.ll_customer_qrcode_write);
		ll_manual_input = findViewById(R.id.ll_manual_input);
		tv_print_preview = findViewById(R.id.tv_print_preview);
		tv_onekey_print = findViewById(R.id.tv_onekey_print);
		
	}
	
	@SuppressWarnings("deprecation")
	private void initDatas(boolean isEdit){
		title.setText("打印电子面单");
		if(!isEdit){
			order = (Order) getIntent().getSerializableExtra("order");
		}
		if(!TextUtils.isEmpty(order.getDeliverNo())){
			iv_express_barcode.setImageBitmap(UtilToolkit.getBarCodeToBitmap(order.getDeliverNo(), getWindowManager().getDefaultDisplay().getWidth()*5/8, 72));
			num = order.getDeliverNo();
		}else{
			requestHttp(ACT_GET);
		}
		tv_waybill_num.setText(Utility.formatOrderNo(order.getDeliverNo()));
		tv_sender_name.setText(order.getSenderName());
		tv_sender_address.setText(order.getSenderAddress());
		tv_sender_phone.setText(order.getSenderPhone());
//		if(Utility.isEmpty(order.getSenderPhone())){
//			iv_sender_phone.setVisibility(View.GONE);
//		}else{
//			iv_sender_phone.setVisibility(View.VISIBLE);
//		}
		tv_shou_name.setText(order.getName());
		tv_shou_address.setText(order.getAddress());
		tv_shou_phone.setText(order.getPhone());
//		if(Utility.isEmpty(order.getPhone())){
//			iv_shou_phone.setVisibility(View.GONE);
//		}else{
//			iv_shou_phone.setVisibility(View.VISIBLE);
//		}
		tv_article_info.setText(order.getArticleInfo());
		tv_thing_weight.setText(order.getCharging_weight());
		if(TextUtils.isEmpty(order.getAddressHead())){
			order.setAddressHead(order.getReceiptProvince()+order.getReceiptCity()+order.getReceiptCountry());
		}
		tv_adress_head.setText(order.getAddressHead());
		if(TextUtils.isEmpty(order.getAddressHead())){
			tv_adress_head_edit.setVisibility(View.GONE);
			tv_adress_head.setText("点击此处添加大字");
			tv_adress_head.setTextColor(context.getResources().getColor(R.color.default_green_2));
		}
		device = getIntent().getParcelableExtra("device");
//		deviceNames = new ArrayList<String>();
//		String[] cache = SkuaidiSpf.getBluetoothDeviceName().split(",");
//		for (int i = 0; i < cache.length; i++) {
//			deviceNames.add(cache[i]);
//		}
//		devices = bluetoothService.getBondList();
//		for (int i = 0; i < devices.size(); i++) {
//			if(isHaveConected){
//				break;
//			}
//			for (int j = 0; j < cache.length; j++) {
//				if(devices.get(i).getName().equals(cache[j])){
//					device = devices.get(i);
//					isHaveConected = true;
//					break;
//				}
//			}
//		}
	}
	
	private void sendSMS(){
		JSONObject object = new JSONObject();
		try {
			object.put("sname", "sms.send");
			object.put("order_number", order.getId());
			object.put("mobile", order.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(object,false,HttpHelper.SERVICE_V1);
	}
	
	private void requestHttp(String act){
		JSONObject object = new JSONObject();
		try {
			object.put("sname", "spreadsheets");
			object.put("act", act);
			if(act.equals(ACT_USE)){
				JSONObject useNum = new JSONObject();
				useNum.put("order_number", order.getId());
				useNum.put("number", num);
				useNum.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
				object.put("data", useNum);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
	}
	
	private void addListener(){
		tv_manage.setOnClickListener(this);
		tv_adress_head_edit.setOnClickListener(this);
		ll_customer_write.setOnClickListener(this);
		ll_customer_qrcode_write.setOnClickListener(this);
		ll_manual_input.setOnClickListener(this);
		tv_print_preview.setOnClickListener(this);
		tv_onekey_print.setOnClickListener(this);
		if(TextUtils.isEmpty(order.getAddressHead())){
			tv_adress_head.setOnClickListener(this);
		}
	}
	
	
	public void back(View v){
		finish();
	}
	
	/**
	 * 通知客户填写
	 * @param v
	 */
	private void informCustomerInput(View v){
		SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(this);
		dialog.setTitleGray(NOTICE_TITLE);
		dialog.setTitleColor(R.color.sto_main_color);
		dialog.setContentGray("短信内容固定，不可编辑。后带链接（客户不可见，短信内点开链接填写）");
		dialog.setPositionButtonTextGray("发送");
		dialog.setPositionButtonClickListenerGray(new DialogSendBTNListener());
		dialog.showDialogGray(v);
	}
	
	/**
	 * 预览面单
	 */
	private void previewPrintBill(View v){
		PrintBillPreviewPopopWindow previewWindow = new PrintBillPreviewPopopWindow();
		previewWindow.show(v);
	}
	
	/**
	 * 二维码填写
	 * @param v
	 */
	private void qrCodeInput(View v){
		ShowQrcodePopwindow popwindow = new ShowQrcodePopwindow();
		popwindow.show(v);
	}
	
	private void manualInput(){
		Intent intent = new Intent(this, UpdateAddressorActivity.class);
		intent.putExtra("isEditItemInfo", true);
		intent.putExtra("order", order);
		startActivityForResult(intent, REQEUST_CODE);
	}
	
	private void editBigWords(View v){
		final SkuaidiDialog grayStyle = new SkuaidiDialog(this);
		grayStyle.isUseEditText(true);
		grayStyle.setTitle("编辑大字");
		grayStyle.setEditText(order.getAddressHead());
		grayStyle.setEditTextHint("请输入大字");
		grayStyle.setPositionButtonTitle("确认");
		grayStyle.setPosionClickListener(new PositonButtonOnclickListener() {
			
			@Override
			public void onClick(View v) {
				order.setAddressHead(grayStyle.getEditTextContent());
				if(!TextUtils.isEmpty(order.getAddressHead())){
					tv_adress_head.setOnClickListener(null);
					tv_adress_head.setText(order.getAddressHead());
					tv_adress_head.setTextColor(context.getResources().getColor(R.color.text_black));
					tv_adress_head_edit.setVisibility(View.VISIBLE);
				}else{
					tv_adress_head.setOnClickListener(PrintExpressBillActivity.this);
					tv_adress_head.setText("点击此处添加大字");
					tv_adress_head.setTextColor(context.getResources().getColor(R.color.default_green_2));
					tv_adress_head_edit.setVisibility(View.GONE);
				}
			}
		});
		grayStyle.showDialog();
	}
	
	private void printBill(View v){
		inspect();
		if(!TextUtils.isEmpty(num) && Utility.isEmpty(order.getDeliverNo())){
			requestHttp(ACT_USE);
		}else if(!TextUtils.isEmpty(num) && !Utility.isEmpty(order.getDeliverNo())){
			try {
				PrintDataService.getPrinter(context).print(device,BluetoothPrinterUtil.printStoExpressBill(context, order));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打印电子面单
	 */
	private void printBill(){
		if (null == btAdapter) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (!btAdapter.isEnabled()) {
			btAdapter.enable();
		}

		if(Utility.isEmpty(myPrinter)){
			if(device.getName().startsWith("QR")){
				myPrinter = QRPrinter.getInstance(device);
			}else if(device.getName().startsWith("KM")){
				myPrinter = KMPrinter.getInstance(device);
			}else{
				dismissProgressDialog();
				UtilToolkit.showToast("当前版本仅支持启锐打印机、快麦打印机，");
				return;
			}
		}
		if(!Utility.isEmpty(myPrinter) && !myPrinter.isConnected()){
			myPrinter.connect(device);
		}
		String bigChar = tv_adress_head.getText().toString();
		myPrinter.printStoContent(num, bigChar, order);
		handler.postDelayed(runnable, time);
	}
	
	private void inspect(){
		if(TextUtils.isEmpty(num)){
//			UtilToolkit.showToast("请先维护电子面单号段");
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(this);
			dialog.setTitleGray("温馨提示");
			dialog.setTitleColor(R.color.sto_main_color);
			dialog.setContentGray("请先管理电子面单号！");
			dialog.setPositionButtonTextGray("立即管理");
			dialog.setNegativeButtonTextGray("我再看看");
			dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ExpressWayBillManageActivity.class);
					startActivity(intent);
				}
			});
			dialog.showDialogGray(tv_onekey_print.getRootView());
			return;
		}
		if(TextUtils.isEmpty(order.getSenderName())
				||TextUtils.isEmpty(order.getSenderAddress())
				||TextUtils.isEmpty(order.getSenderPhone())){
			UtilToolkit.showToast("请先完善发件人信息");
			return;
		}
		if(TextUtils.isEmpty(order.getName())
				||TextUtils.isEmpty(order.getAddress())
				||TextUtils.isEmpty(order.getPhone())){
			UtilToolkit.showToast("请先完善收件人信息");
			return;
		}
		if(TextUtils.isEmpty(order.getAddressHead())){
			UtilToolkit.showToast("请先完善大字");
			return;
		}
		if(!Utility.isNetworkConnected()){
			UtilToolkit.showToast("当前网络未连接");
			return;
		}
	}
	
	
	private class DialogSendBTNListener implements PositionButtonOnclickListenerGray{

		@Override
		public void onClick(View v) {
			sendSMS();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQEUST_CODE&&resultCode == RESPONSE_CODE){
			order = (Order) data.getSerializableExtra("order");
			initDatas(true);
		}
	}
	
	
	/**
	 * 
	 * 电子面单预览window
	 * @author xy
	 *
	 */
	private class PrintBillPreviewPopopWindow {
		
		private Window window;
		private ImageView iv_barTop,iv_barcode;
		private TextView tv_preview_billNo,tv_preview_shou_address,tv_preview_sender_address,
						 tv_preview_address_head,tv_preview_article_info,tv_date_top,
						 tv_preview_shou_address_bottom,tv_preview_article_info_bottom,
						 tv_date_bottom,tv_preview_sender_address_bottom,tv_billNo_bottom;
		private View preview,printView;
		
		public PrintBillPreviewPopopWindow(){
			window = new Window();
		}
		
		private class Window extends PopupWindow{
			public Window(){
				super(context);
				initView();
				initDatas();
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.MATCH_PARENT);
				setBackgroundDrawable(new ColorDrawable(0xb0000000));
				setContentView(preview);
			}
			
			private void initView(){
				preview = LayoutInflater.from(context).inflate(R.layout.express_bill_view, null);
				printView = preview.findViewById(R.id.print_view);
				iv_barTop = (ImageView) preview.findViewById(R.id.iv_barTop);
				iv_barcode = (ImageView) preview.findViewById(R.id.iv_barcode);
				tv_preview_billNo = (TextView) preview.findViewById(R.id.tv_preview_billNo);
				tv_preview_shou_address = (TextView) preview.findViewById(R.id.tv_preview_shou_address);
				tv_preview_sender_address = (TextView) preview.findViewById(R.id.tv_preview_sender_address);
				tv_preview_address_head = (TextView) preview.findViewById(R.id.tv_preview_address_head);
				tv_preview_article_info = (TextView) preview.findViewById(R.id.tv_preview_article_info);
				tv_date_top = (TextView) preview.findViewById(R.id.tv_date_top);
				tv_preview_shou_address_bottom = (TextView) preview.findViewById(R.id.tv_preview_shou_address_bottom);
				tv_preview_sender_address_bottom = (TextView) preview.findViewById(R.id.tv_preview_sender_address_bottom);
				tv_preview_article_info_bottom = (TextView) preview.findViewById(R.id.tv_preview_article_info_bottom);
				tv_date_bottom = (TextView) preview.findViewById(R.id.tv_date_bottom);
				tv_billNo_bottom = (TextView) preview.findViewById(R.id.tv_billNo_bottom);
			}
			
			private void initDatas(){
				ViewTreeObserver vto = iv_barTop.getViewTreeObserver();   
		        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		            @Override  
		            public void onGlobalLayout() { 
		            	iv_barTop.setImageBitmap(UtilToolkit.getBarCodeToBitmap(num, (int)(preview.getWidth()/2.1), preview.getWidth()/12));
		            }   
		        });
		        
		        ViewTreeObserver vto1 = iv_barcode.getViewTreeObserver();   
		        vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		            @Override  
		            public void onGlobalLayout() { 
		            	iv_barcode.setImageBitmap(UtilToolkit.getBarCodeToBitmap(num, preview.getWidth()/2+20, preview.getWidth()/11));
		            }   
		        });
				tv_preview_billNo.setText(num);
				tv_billNo_bottom.setText(num);
				tv_preview_shou_address.setText(order.getAddress()+"\n"+order.getName()+" "+order.getPhone());
				tv_preview_sender_address.setText(order.getSenderAddress()+"\n"+order.getSenderName()+" "+order.getSenderPhone());
				tv_preview_address_head.setText(order.getAddressHead());
				tv_preview_article_info.setText(order.getArticleInfo());
				tv_date_top.setText("日期："+order.getTime().split(" ")[0]);
				tv_preview_shou_address_bottom.setText(order.getAddress()+"\n"+order.getName()+" "+order.getPhone());
				tv_preview_sender_address_bottom.setText(order.getSenderAddress()+"\n"+order.getSenderName()+" "+order.getSenderPhone());
				tv_preview_article_info_bottom.setText("物品："+order.getArticleInfo());
				tv_date_bottom.setText("日期："+order.getTime().split(" ")[0]);
				preview.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			}
		}
		public void show(View v){
			window.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}
	
	/**
	 * 
	 * 匹配蓝牙列表window
	 * @author xy
	 *
	 */
	private class BondBluetoothListWindow{
		private View convertView;
		private ListView bondList;
		private Window window;
		
		public BondBluetoothListWindow(){
			devices = bluetoothService.getBondList();
			window = new Window();
		}
		
		private class Window extends PopupWindow{
			public Window(){
				initView();
				initDatas();
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.MATCH_PARENT);
				setBackgroundDrawable(new ColorDrawable(0xb0000000));
				setContentView(convertView);
			}
			
			private void initView(){
				convertView = LayoutInflater.from(context).inflate(R.layout.bond_bluetooth_list_layout, null);
				bondList = (ListView) convertView.findViewById(R.id.lv_bond_list);
				bondList.setAdapter(new BondBluetoothAdapter());
			}
			
			private void initDatas(){
				
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			}
		}
		
		private class BondBluetoothAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				return devices.size();
			}

			@Override
			public BluetoothDevice getItem(int position) {
				return devices.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(context).inflate(R.layout.bond_list_item, null);
				TextView bond = (TextView) convertView.findViewById(R.id.tv_bond);
				bond.setText(getItem(position).getName());
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//PrintDataService.getPrinter(context).print(getItem(position));
						deviceNames.add(getItem(position).getName());
						SkuaidiSpf.saveBluetoothDeviceName(deviceNames);
						isHaveConected = true;
						device = getItem(position);
						window.dismiss();
						if(isHaveConected){
							try {
								PrintDataService.getPrinter(context).print(device,BluetoothPrinterUtil.printStoExpressBill(context, order));
								order.setDeliverNo(num);
								newDB.updateOrderInfo(order);
							} catch (InvalidParameterException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
				return convertView;
			}
			
		}
		
		public void show(View v){
			window.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}

	/**
	 * 更新打印状态
	 */
	private void updatePrintStatus(){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", PRINT_STATUS);
			data.put("orderNumber", order.getId());
			data.put("waybillNo", order.getDeliverNo());
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 二维码展示window
	 * @author xy
	 *
	 */
	private class ShowQrcodePopwindow{
		private View convertView;
		private ImageView qrCodeView;
		private Window window;
		
		public ShowQrcodePopwindow(){
			window = new Window();
		}
		
		private class Window extends PopupWindow{
			
			public Window(){
				super(context);
				initView();
				initDatas();
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.MATCH_PARENT);
				setBackgroundDrawable(new ColorDrawable(0xb0000000));
				setContentView(convertView);
			}
			
			private void initView(){
				convertView = LayoutInflater.from(context).inflate(R.layout.show_qrcode_window_layout, null);
				qrCodeView = (ImageView) convertView.findViewById(R.id.iv_qrcode);
			}
			
			private void initDatas(){
				Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
				int size = (int) (DisplayUtil.getScreenMetrics().x/1.5);
				String url = "http://m.kuaidihelp.com/wduser/order_info_update?mb="+SkuaidiSpf.getLoginUser().getPhoneNumber()+"&order_number="+order.getId();
				qrCodeView.setImageBitmap(BarcodeUtils.createQRImage(url, size, size, logo));
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			}
		}
		public void show(View v){
			window.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(sname.equals("spreadsheets")&&act.equals(ACT_GET)){
			num = result.optString("number");
			if(TextUtils.isEmpty(num)){
				iv_express_barcode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_nonum_barcode));
				tv_waybill_num.setTextColor(getResources().getColor(R.color.red_f74739));
				tv_waybill_num.setText("请 先 管 理 电 子 面 单 号！");
			}else{
				iv_express_barcode.setImageBitmap(UtilToolkit.getBarCodeToBitmap(num, getWindowManager().getDefaultDisplay().getWidth()*5/8, 65));
				tv_waybill_num.setText(Utility.formatOrderNo(num));
			}
		}else if(sname.equals("spreadsheets")&&act.equals(ACT_USE)){
			if(noPrinting){
				try {
					order.setDeliverNo(num);
					printBill();
					newDB.updateOrderInfo(order);
				} catch (InvalidParameterException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
//			if(isHaveConected){
//				try {
//					order.setDeliverNo(num);
//					PrintDataService.getPrinter(context).print(device,BluetoothPrinterUtil.printStoExpressBill(context, order));
//					newDB.updateOrderInfo(order);
//				} catch (InvalidParameterException e) {
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}else{
//				BondBluetoothListWindow listWindow = new BondBluetoothListWindow();
//				listWindow.show(tv_onekey_print);
//			}
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		UtilToolkit.showToast(result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname,
			String msg, JSONObject result) {

	}

	@Override
	public void onClick(View v) {
		Intent intent;	
		switch (v.getId()) {
		case R.id.tv_manage://电子面单管理
			intent = new Intent(this, ExpressWayBillManageActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_adress_head_edit://大字头编辑
			editBigWords(v);                                                                                                                                                                                                                 
			break;
		case R.id.ll_customer_write://客户填写
			informCustomerInput(v);
			break;
		case R.id.ll_customer_qrcode_write://当面填写
			qrCodeInput(v);
			break;
		case R.id.ll_manual_input://手动输入
			manualInput();
			break;
		case R.id.tv_print_preview://打印预览
			previewPrintBill(v);
			break;
		case R.id.tv_onekey_print://一键打印
			inspect();
			showProgressDialog( "打印中...");
			if(!TextUtils.isEmpty(num) && Utility.isEmpty(order.getDeliverNo())){
				requestHttp(ACT_USE);
			}else if(!TextUtils.isEmpty(num) && !Utility.isEmpty(order.getDeliverNo())){
				printBill();
			}
			break;
		case R.id.tv_adress_head:
			editBigWords(v);
			break;
		default:
			break;
		}
	}

	@Override
	public void finish() {
		bluetoothService.disconnect();
		super.finish();
	}
	
}
