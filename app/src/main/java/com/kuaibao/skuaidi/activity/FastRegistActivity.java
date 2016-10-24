package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;

public class FastRegistActivity extends SkuaiDiBaseActivity {

	private Context context;
	private LinearLayout ll_view;
	private EditText et_edit_pwd;//输入要修改的密码框 
	private TextView tv_title_des;//title
	private ImageView iv_title_back;//back btn
	private Button bt_title_more;//刷新界面
	private TextView tv_show_desc;//发送短信说明
	private Button btn_send_msg;//发送短信按钮 
	
	private String[] smsMsg;
	private String note = "";//发送短信说明
	private String phone = "";//服务器接收短信的手机号
	private String content = "";//发送的内容
	
	
	private String fromEvent = "";
	
	private final int RECEIVE_SMS_MESSAGE = 101;
	private final int RESET_SMS_PWD = 102;
	
	Handler handler = new Handler(){
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECEIVE_SMS_MESSAGE://快递注册入口 
				btn_send_msg.setEnabled(true);
				dismissProgressDialog();
				smsMsg= (String[]) msg.obj;
				note = smsMsg[0];
				phone = smsMsg[1];
				content = smsMsg[2];
//				tv_show_desc.setText(note);//显示说明
				//***设置指定字符串颜色 {
//				String textNote = String.format(note, "10655010200721944");
//		        int index[] = new int[3];
//		        index[0] = textNote.indexOf("10655010200721944");
//		        SpannableStringBuilder style = new SpannableStringBuilder(textNote);
//		        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(color.default_green)), index[0], index[0] + 17, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		        tv_show_desc.setText(note);
		        // }
		        ll_view.setVisibility(View.VISIBLE);
		        break;

			case RESET_SMS_PWD://重置密码入口 
				btn_send_msg.setEnabled(true);
				dismissProgressDialog();
				smsMsg= (String[]) msg.obj;
				note = smsMsg[0];
				phone = smsMsg[1];
				tv_show_desc.setText(note);
				ll_view.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.fast_regist_activity);
		context = this;
		getData();
		initView();
		setListener();
		call_interface();
	}

	private void getData() {
		//获取是快速注册还是找回密码进此界面的入口
		fromEvent = getIntent().getStringExtra("fromEvent");
	}

	private void call_interface() {
		if(fromEvent.equals("freeRegist")){//快速注册入口 
			//调用接口信息获得发送的手机号码，说明和需要发送的内容
			JSONObject data = new JSONObject();
			try {
				data.put("sname","smsreg.get");
				data.put("phone", getIntent().getStringExtra("phone_str"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			showProgressDialog( "");
		}else if(fromEvent.equals("getBackPwd")) {//找回密码入口 
			//调用接口信息获得发送的手机号码，说明和需要发送的内容
			JSONObject data = new JSONObject();
			try {
				data.put("sname", "smsresetpwd.get");
				data.put("phone", getIntent().getStringExtra("phone_str"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			showProgressDialog( "");
		}
	}

	/**
	 * 
	 */
	private void initView(){
		et_edit_pwd = (EditText) findViewById(R.id.et_edit_pwd);
		ll_view = (LinearLayout) findViewById(R.id.ll_view);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_show_desc = (TextView) findViewById(R.id.tv_show_desc);
		btn_send_msg = (Button) findViewById(R.id.btn_send_msg);
		if(fromEvent.equals("freeRegist")){
			tv_title_des.setText("快速注册");
		}else if (fromEvent.equals("getBackPwd")) {
			tv_title_des.setText("快速重置");
			et_edit_pwd.setVisibility(View.VISIBLE);
		}
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setText("刷新");
		
		bt_title_more.setOnClickListener(new MyOnclickListener());
		iv_title_back.setOnClickListener(new MyOnclickListener());
		btn_send_msg.setOnClickListener(new MyOnclickListener());
	}
	
	private void setListener() {
		//密码输入框监听
		et_edit_pwd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int length = s.length();
				if(length < 6 || length > 16){
					et_edit_pwd.setTextColor(context.getResources().getColor(color.orange_1));
				}else {
					et_edit_pwd.setTextColor(context.getResources().getColor(color.gray_2));
				}
				
			}
		});
	}
	
	/**
	 * 点击事件
	 * @author gudd
	 *
	 */
	class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send_msg://发送短信
				btn_send_msg.setEnabled(false);
				if(fromEvent.equals("freeRegist")){
					if(!phone.equals("") && !content.equals("")){
						sendSMS(phone, content, null);// 使用自己的手机发送短信（使用手机在后台发送短信）
					}else {
						btn_send_msg.setEnabled(true);
						UtilToolkit.showToast("未发送成功，请重新刷新后再次发送");
					}
				}else if(fromEvent.equals("getBackPwd")){
					if(et_edit_pwd.getText().toString().length()<6){
						btn_send_msg.setEnabled(true);
						UtilToolkit.showToast("请输入6~16位密码");
					}else if (et_edit_pwd.getText().toString().length()>16) {
						btn_send_msg.setEnabled(true);
						UtilToolkit.showToast("请输入6~16位密码");
					}else {
						btn_send_msg.setEnabled(true);
						if(!phone.equals("") && !et_edit_pwd.getText().toString().trim().equals("")){
							sendSMS(phone, "CZMM "+et_edit_pwd.getText().toString().trim(), null);
						}else {
							btn_send_msg.setEnabled(true);
							UtilToolkit.showToast("未发送成功，请重新刷新后再次发送");
						}
					}
					
				}
				
				break;

			case R.id.iv_title_back://返回
				finish();
				break;
			case R.id.bt_title_more://刷新界面
				call_interface();
				break;
			default:
				break;
			}
		}
		
	}
	
	@Override
	protected void OnSMSSendSuccess() {
		setResult(RegisterOrModifyInfoActivity.FAST_FINDPWD_OR_REGIST_RESULTCODE);
		finish();
		UtilToolkit.showToast("短信发送成功");
	}

	@Override
	protected void OnSMSSendFail() {
		super.OnSMSSendFail();
		UtilToolkit.showToast("短信发送失败");
	}
	
	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Message msg = new Message();
		if(result!=null){
			if(sname.equals("smsreg.get")){//快速注册
				try {
					JSONObject retArr = result.getJSONObject("retArr");
					String note = retArr.getString("note");//解析说明内容
					String phone = retArr.getString("phone");//解析手机
					String content = retArr.getString("content");//解析发送内容
					
					String[] smsMsg = new String[3];
					smsMsg[0] = note;
					smsMsg[1] = phone;
					smsMsg[2] = content;
					msg.what = RECEIVE_SMS_MESSAGE;
					msg.obj = smsMsg;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (sname.equals("smsresetpwd.get")) {//重置密码
				try {
					JSONObject retArr = result.getJSONObject("retArr");
					String note = retArr.getString("note");//解析说明内容
					String phone = retArr.getString("phone");//解析手机
					
					String[] smsMsg = new String[2];
					smsMsg[0] = note;
					smsMsg[1] = phone;
					msg.what = RESET_SMS_PWD;
					msg.obj = smsMsg;
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		}
		handler.sendMessage(msg);
	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		if(!result.equals("")){
			btn_send_msg.setEnabled(true);
			UtilToolkit.showToast(result);
		}else {
			btn_send_msg.setEnabled(true);
			UtilToolkit.showToast("请重新刷新...");
			dismissProgressDialog();
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code ,String sname, String msg,
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
