package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.drawable;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 罗娜 服务说明
 */
public class ServiceStateActivity extends RxRetrofitBaseActivity {
	Context context;
	private TextView tv_title;
	private EditText et_new_content;
	private TextView tv_number;
	private TextView tv_tip;
	private ImageView iv_edit_icon;

	private TextView tv_edit;
	private InputMethodManager im;
	private Button bt_title_more ;
	private SkuaidiImageView iv_title_back;// 返回按钮


	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.NETWORK_FAILED:
				UtilToolkit.showToast("网络连接错误,请稍后重试!");
				break;
			case Constants.RANGE_GET_FAILD:
				if (msg.arg1 == 1) {
					UtilToolkit.showToast( "提交失败，请稍后重试！");
				} else if (msg.arg1 == 2) {
//					et_new_content.setText("");
				}
				break;

			case Constants.RANGE_PARSE_FAILD:
				if (msg.arg2 == 1) {
					UtilToolkit.showToast( "提交失败，请稍后重试！");
				} else if (msg.arg2 == 2) {
//					et_new_content.setText("");
				}
				break;

			case Constants.RANGE_PARSE_OK:
				if (msg.arg1 == 0) {
					UtilToolkit.showToast( "提交失败，请稍后重试！");
				} else if (msg.arg1 == 1) {
					if (msg.arg2 == 1) {
						UtilToolkit.showToast( "提交成功");

						
						et_new_content.setFocusable(false);
						tv_edit.setTextColor(Color.rgb(116, 116, 116));
						iv_edit_icon.setBackgroundResource(R.drawable.grzl_icon_bj);
						et_new_content.setTextColor(Color.rgb(116, 116, 116));
						bt_title_more.setEnabled(false);
						bt_title_more.setBackgroundResource(drawable.shape_title_nofocusbg_bt);
//						tv_edit.setTextColor(color.black);
						// //System.out.println("提交成功");
					} else if (msg.arg2 == 2) {
						if (msg.obj.toString() != null
								&& !msg.obj.toString().equals("null")) {

							et_new_content.setText(msg.obj.toString());
						} else {
//							et_new_content.setText("");
						}
					}
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.service_statement);
		context = this;

		getControl();
		getData();

	}

	private void getData() {
		KuaidiApi.addpickup(context, handler, "service_note", "read", "", 2);
	}

	private void getControl() {

//		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_title_back = (SkuaidiImageView)findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(onclickListener);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setEnabled(false);
		
		bt_title_more.setText(getResources().getString(R.string.commit));
		bt_title_more.setBackgroundResource(drawable.shape_title_nofocusbg_bt);
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		et_new_content = (EditText) findViewById(R.id.et_new_content);
		
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		iv_edit_icon = (ImageView) findViewById(R.id.iv_edit);


		tv_title.setText(getResources().getString(R.string.my_service_state));
		
		tv_number
				.setText(et_new_content.getText().toString().length() + "/200");
		tv_tip.setText(getResources().getString(R.string.service_state_tip));


		et_new_content.setFocusable(false);


		et_new_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (!s.equals("")) {
//					bt_title_more.setFocusableInTouchMode(true);
//					bt_title_more.setBackground(background)
				}else {
					UtilToolkit.showToast( "请输入你的服务说明");
				}
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				tv_number.setText(et_new_content.getText().toString().length()
						+ "/200");
			}
		});


		et_new_content.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasfocus) {
				if (hasfocus) {
					
					//弹出键盘
					im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					im.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
				}

			}
		});

		/**
		 * submit Event
		 * 提交按钮 click event
		 */
		bt_title_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (et_new_content.getText().toString().trim().equals("")) {
					UtilToolkit.showToast( "服务说明不得为空");
				} else if (et_new_content.getText().toString().length() > 200) {
					UtilToolkit.showToast( "服务说明不得超过200个字");
				} else {
					KuaidiApi.addpickup(context, handler, "service_note", "write",
							et_new_content.getText().toString(), 1);
				}
				
			}
		});

	}


	/**
	 * 编辑按钮click event
	 * 
	 * @param view
	 */
	public void editEvent(View view) {
		tv_edit.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		iv_edit_icon.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_modify"));
		et_new_content.setTextColor(Color.rgb(0, 0, 0));
		et_new_content.setFocusableInTouchMode(true);
		et_new_content.requestFocus();
		bt_title_more.setEnabled(true);
		bt_title_more.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_title_bt"));
		et_new_content.setSelection(et_new_content.getText().toString().length());//将光标移动到文本末

	}
	
	private OnClickListener onclickListener = new OnClickListener() {
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
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

}
