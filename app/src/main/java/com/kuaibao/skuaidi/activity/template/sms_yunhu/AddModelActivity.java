package com.kuaibao.skuaidi.activity.template.sms_yunhu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 顾冬冬 添加或修改模板，根据从上级Activity传过来的operatetype值区分 add添加 edit为修改
 */
public class AddModelActivity extends SkuaiDiBaseActivity {
	private Context context;

	private SkuaidiDialog dialog;

	private TextView tv_title_des;
	private TextView tv_number, tv_desc, tvInsertIcon;
	private Button btn_commit;
	private EditText et_new_content;// 模板内容

	private String operateType = "";// 区分是添加还是修改模板
	private String modelTid = "";// 服务器上面的模板id

	private String titleStr = "";// 编辑模板过来的时候获取的模板标题
	private SkuaidiDB skuaidiDb;
	boolean isFirstDelTitle = true;// 作为是否是第一次删除对话框中模板标题的标记

	private String ordernum = "#NON#";
	private String order_danhao = "#DHDHDHDHDH#";// 运单单号标记
    private String model_pwd = "#MM#";// 取件密码
	private String model_surl = "#SURLSURLSURLSURLS#";// 短链接
	
	private String template_type;//模板类型。

	protected Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.MODEL_ADD_SUCCESS:
				btn_commit.setEnabled(true);
				String msgString = msg.obj.toString();
				UtilToolkit.showToast(msgString);
				finish();
				if(dialog != null){
					dialog = null;
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
		setContentView(R.layout.add_model);
		context = this;
		skuaidiDb = SkuaidiDB.getInstanse(context);
		template_type=getIntent().getStringExtra("template_type");
		findView();
		setLinstener();
		setOnTouchListener();
	}

	private void findView() {
		RelativeLayout rl_input_bianhao = (RelativeLayout) findViewById(R.id.rl_input_bianhao);// 插入编号按钮
		RelativeLayout rl_input_danhao = (RelativeLayout) findViewById(R.id.rl_input_danhao);// 插入单号按钮
		RelativeLayout rl_input_password = (RelativeLayout) findViewById(R.id.rl_input_password);
		RelativeLayout rl_insert_url = (RelativeLayout) findViewById(R.id.rl_insert_url);// 插入链接按钮

		ImageView iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tvInsertIcon = (TextView) findViewById(R.id.tvInsertIcon);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		TextView add_no_desc = (TextView) findViewById(R.id.add_no_desc);
		et_new_content = (EditText) findViewById(R.id.et_new_content);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		ViewGroup rl_show_desc = (ViewGroup) findViewById(R.id.rl_show_desc);
		btn_commit = (Button) findViewById(R.id.btn_commit);
		btn_commit.setEnabled(false);
		tv_title_des.setText("新建模板");
		tv_number.setText("0/129");
		rl_show_desc.setOnClickListener(new MyOnClickListener());
		add_no_desc.setOnClickListener(new MyOnClickListener());
		rl_input_bianhao.setOnClickListener(new MyOnClickListener());
		rl_input_danhao.setOnClickListener(new MyOnClickListener());
		rl_input_password.setOnClickListener(new MyOnClickListener());
		rl_insert_url.setOnClickListener(new MyOnClickListener());
		iv_title_back.setOnClickListener(new MyOnClickListener());
		btn_commit.setOnClickListener(new MyOnClickListener());
	}

	private void setLinstener() {
		Intent intent = getIntent();
		operateType = intent.getStringExtra("operatetype");
		if (operateType == null) {
			operateType = "add";
		}
		String modelId = intent.getStringExtra("modelid");
		modelTid = intent.getStringExtra("modelTid");

		if (operateType.equals("edit")) {
			tv_title_des.setText("修改模板");
			String str = skuaidiDb.getModelContent(modelId);
			titleStr = skuaidiDb.getModelTitle(modelId);// 获取模板标题

			btn_commit.setEnabled(true);
			btn_commit.setBackgroundResource(R.drawable.selector_base_green_qianse1);
			
			if (!Utility.isEmpty(str)) {
				if (str.contains("#NO#"))
					str = str.replace("#NO#",ordernum);
				if (str.contains("#DH#"))
					str = str.replaceAll("#DH#", order_danhao);
				if (str.contains("#SURL#"))
					str = str.replaceAll("#SURL#", model_surl);
			}

			TextInsertImgParser textInsertImgParser = new TextInsertImgParser(context);
			et_new_content.setText(textInsertImgParser.replace(str));

			if (et_new_content.getText().length() > 65) {
				tv_desc.setVisibility(View.VISIBLE);
			} else {
				tv_desc.setVisibility(View.GONE);
			}
			String contentLengthStr = et_new_content.getText().length() + "/129";
			tv_number.setText(contentLengthStr);
			tv_number.setTextColor(Utility.getColor(context,R.color.gray_3));
		}
		et_new_content.setSelection(et_new_content.getText().toString().length());
	}

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

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.rl_show_desc:// 关于严禁发送违法短信内容的公告
				loadWeb(Constants.URL_SEND_WEIFA_DUANXIN_TONGZHI, "");
				break;
			case R.id.rl_insert_url:// 插入链接
				if (et_new_content.getText().toString().contains(model_surl) || et_new_content.getText().toString().length() > 110) {
					UtilToolkit.showToast( "不可以再插入链接");
				} else {
					// EditText对象
					EditText mEditText = (EditText) findViewById(R.id.et_new_content);
					// int index = mEditText.getSelectionStart();// 获取光标所在位置
					Editable edit = mEditText.getEditableText();// 获取EditText的文字
					// 名字做为关键字，为以后是否显示图片做判断的依据
					SpannableString ss = new SpannableString(model_surl);
					Drawable d = Utility.getDrawable(context,R.drawable.model_url);
					d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					edit.append(ss);
				}
				break;
			case R.id.btn_commit:// 添加模板（到服务器）
				isFirstDelTitle = true;
				dialog = new SkuaidiDialog(context);
				dialog.setTitle("填写短信模板名称");
				dialog.setPositionButtonTitle("保存");
				dialog.setNegativeButtonTitle("取消");
				dialog.isUseEditText(true);
				dialog.setEditTextHint("模板名称不超过20个字");
				if(operateType.equals("edit")){
					dialog.setEditText(titleStr);
				}else{
					dialog.setEditText("新短信模板");
				}
				dialog.setEditTextContent(20);
				dialog.showSoftInput(true);
				dialog.setPosionClickListener(new PositonButtonOnclickListener() {

					@Override
					public void onClick(View v) {
						String model_title = dialog.getEditTextContent();
						if (Utility.isEmpty(model_title)) {
							model_title = "新短信模板";
						}

						if (Utility.isNetworkConnected()) {
							btn_commit.setEnabled(false);
							String contentStr = et_new_content.getText().toString();// 文本框的内容
							int contentLength = contentStr.length();// 文本框中的长度

							if (!Utility.isEmpty(contentStr) && contentStr.contains(ordernum))
								contentStr = contentStr.replace(ordernum,"#NO#");
							if (!Utility.isEmpty(contentStr) && contentStr.contains(order_danhao))
								contentStr = contentStr.replace(order_danhao,"#DH#");
							if (!Utility.isEmpty(contentStr) && contentStr.contains(model_surl))
								contentStr = contentStr.replace(model_surl,"#SURL#");

							if (operateType.equals("add")) {// 如果是添加模板进入
								if ("".equals(contentStr.trim())) {// 如果未输入模板内容
									btn_commit.setEnabled(true);
									UtilToolkit.showToast( "添加内容不得为空");
								} else {
									if (contentLength <= 129 && contentLength <= 129) {
										showProgressDialog( "请稍候...");
										addModel(model_title, contentStr);
									} else {
										btn_commit.setEnabled(true);
										UtilToolkit.showToast( "模板内容不得超过129个字");
									}
								}
							} else if (operateType.equals("edit")) {
								if ("".equals(contentStr.trim())) {// 如果没有输入模板内容
									btn_commit.setEnabled(true);
									UtilToolkit.showToast( "内容不得为空");
								} else {
									if (et_new_content.getText().toString().length() <= 129) {
										showProgressDialog( "请稍候...");
										updateModel(modelTid, model_title, contentStr);
									} else {
										btn_commit.setEnabled(true);
										UtilToolkit.showToast("模板内容不得超过129个字");
									}
								}
							}
						} else {
							btn_commit.setEnabled(true);
							UtilToolkit.showToast("没有连接网络，请设置您的网络");
						}
					}
				});
				dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

					@Override
					public void onClick() {
						dialog.showSoftInput(false);
					}
				});
				dialog.getEditTextView().setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View view, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_DEL){
							if(dialog != null){
								if(isFirstDelTitle){
									isFirstDelTitle = false;
									dialog.setEditText("");
								}
							}
						}
						return false;
					}
				});
				dialog.showDialog();
				break;
			case R.id.rl_input_bianhao:// 插入编号
				if (et_new_content.getText().toString().length() >= 124) {
					UtilToolkit.showToast( "不可以再插入编号");
				} else {
					// EditText对象
					EditText mEditText = (EditText) findViewById(R.id.et_new_content);
					int index = mEditText.getSelectionStart();// 获取光标所在位置
					Editable edit = mEditText.getEditableText();// 获取EditText的文字
					// 名字做为关键字，为以后是否显示图片做判断的依据
					SpannableString ss = new SpannableString(ordernum);
					Drawable d = Utility.getDrawable(context,R.drawable.model_no);
					d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (index < 0 || index >= edit.length()) {
						edit.append(ss);
					} else {
						edit.insert(index, ss);// 光标所在位置插入文字
					}
				}
				break;
			case R.id.rl_input_danhao:// 插入单号
				String strContent = et_new_content.getText().toString();
				int contentLength = strContent.length();
				if (contentLength > 117) {
					UtilToolkit.showToast( "不可以再插入单号");
				} else {
					// EditText对象
					EditText mEditText = (EditText) findViewById(R.id.et_new_content);
					int index = mEditText.getSelectionStart();// 获取光标所在位置
					Editable edit = mEditText.getEditableText();// 获取EditText的文字
					// 名字做为关键字，为以后是否显示图片做判断的依据
					SpannableString ss = new SpannableString(order_danhao);
					Drawable d = Utility.getDrawable(context,R.drawable.model_order);
					d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (index < 0 || index >= edit.length()) {
						edit.append(ss);
					} else {
						edit.insert(index, ss);// 光标所在位置插入文字
					}
				}
				break;
				case R.id.rl_input_password:// 插入取件密码
                    if (et_new_content.getText().toString().length() > 125) {
						UtilToolkit.showToast( "不可以再插入取件密码");
                    } else {
                        // EditText对象
                        EditText mEditText = (EditText) findViewById(R.id.et_new_content);
                        int index = mEditText.getSelectionStart();// 获取光标所在位置
                        Editable edit = mEditText.getEditableText();// 获取EditText的文字
                        // 名字做为关键字，为以后是否显示图片做判断的依据
                        SpannableString ss = new SpannableString(model_pwd);
                        Drawable d = Utility.getDrawable(context,R.drawable.model_password);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (index < 0 || index >= edit.length()) {
                            edit.append(ss);
                        } else {
                            edit.insert(index, ss);// 光标所在位置插入文字
                        }
                    }
					break;
			case R.id.iv_title_back:
				finish();
				break;
			case R.id.add_no_desc:// 如何添加编号？
				loadWeb(Constants.ADD_MODEL_DESC, "");
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void setOnTouchListener() {

		/** 文本内容变化监听 **/
		et_new_content.addTextChangedListener(new TextWatcher() {

			boolean isHaveShortUrl = false;
			int index;
			int length;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				length = s.length();
				if (s.toString().contains(model_surl)) {
					isHaveShortUrl = true;
					index = s.toString().indexOf(model_surl) + model_surl.length();
				} else {
					isHaveShortUrl = false;
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// ****控制保存按钮颜色
				btn_commit.setEnabled(!Utility.isEmpty(et_new_content.getText().toString().trim()));
				tvInsertIcon.setVisibility(!Utility.isEmpty(et_new_content.getText().toString().trim())?View.VISIBLE:View.GONE);
				btn_commit.setBackgroundResource(!Utility.isEmpty(et_new_content.getText().toString().trim())?R.drawable.selector_base_green_qianse1:R.drawable.shape_btn_gray1);

				if (s.length() > length) {
					if (isHaveShortUrl && s.toString().indexOf(model_surl) + model_surl.length() != s.length()) {
						s.delete(index, s.length());
					}
				}

                tv_desc.setText("此短信以2条发送");
                tv_desc.setVisibility(s.length()>65?View.VISIBLE:View.GONE);
                tvInsertIcon.setVisibility(s.length()>65?View.GONE:View.VISIBLE);

				String contentLengthStr = s.length() + "/129";
				tv_number.setText(contentLengthStr);
				tv_number.setTextColor(Utility.getColor(context,R.color.gray_3));

			}
		});

	}

	/**添加模板**/
	private void addModel(String title,String content){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_template/add");
			data.put("title", title);
			data.put("content", content);
			data.put("template_type", template_type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}
	
	private void updateModel(String tid,String title,String content){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_template/update");
			data.put("title", title);
			data.put("content", content);
			data.put("tid", tid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}
	
	@Override
	protected void onRequestSuccess(String sname, String message, String result, String act) {
		dismissProgressDialog();
		if ("inform_template/add".equals(sname) || "inform_template/update".equals(sname)) {
			Message msg = new Message();
			msg.obj = message;
			msg.what = Constants.MODEL_ADD_SUCCESS;
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String msg, String act, JSONObject data_fail) {
		dismissProgressDialog();
		btn_commit.setEnabled(true);
		UtilToolkit.showToast(msg);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		dismissProgressDialog();
		btn_commit.setEnabled(true);
		if (!msg.equals("")) {
			UtilToolkit.showToast(msg);
		}

		if (Utility.isNetworkConnected()) {
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
}
