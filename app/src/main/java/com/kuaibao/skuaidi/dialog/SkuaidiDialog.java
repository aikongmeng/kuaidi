package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkuaidiDialog extends Dialog {
	private Context context;
	private InputMethodManager imm;
	private TextView title, textContent;// 标题
	private EditText et_deliver_no;// 输入框
	private EditText et_write_phon;// 大输入框
	private LinearLayout ll_write_phoneNo;// 大输入框区域
	private RelativeLayout rl_editBox;// 输入框
	private RelativeLayout rl_body;// 头部
	private LinearLayout ll_import_phoneNo;// 带颜色双按钮
	private LinearLayout ll_bottom_btn;// 对话框底部按钮区域

	private ViewGroup terms = null;// 条件区域
	private ViewGroup terms_select = null;// 条件选择【图片和文字-组合】

	private ImageView iv_select_indentity = null;// 选择条件图片

	private TextView tv_write_phoneNo;// 批量录入客户手机号按钮
	private TextView tv_import_unreceipted;// 导入未签收手机号码按钮
	private TextView terms_warning = null;// 条件警告
	private TextView tv_terms_name = null;// 选择条件文字

	private Button btn_negative;// 取消按钮
	private Button btn_positive;// 确定按钮
	private Button btn_close;// 删除小输入框中的内容按钮
	private Button btn_positive_always;// 总是确定
	private Button btn_single;
	private View ll_double;
	private View view_line;// 三个按钮时第二根竖线

	private boolean autoDismiss = false;
	private boolean useLettersAndNumbers = false;// 使用字母和数字标记
	private boolean isSelectTerms = false;// 小编辑框输入框下方条件按钮被选中
	private boolean isInputFailContent = false;// 输入内容是否错误

	private LayoutInflater inflater = getLayoutInflater();
	private SingleButtonOnclickListener singleButtonOnclickListener;
	private PositonButtonOnclickListener positonButtonOnclickListener;
	private NegativeButtonOnclickListener negativeButtonOnclickListener;
	private PositiveAlwaysButtonOnclickListener positiveAlwaysButtonOnclickListner;
	private BodyOfButtonToUpOnclickListener bodyOfButtonToUpOnclickListener;
	private BodyOfButtonToDownOnlclickListener bodyOfButtonToDownOnlclickListener;

	public SkuaidiDialog(Context context) {
		super(context);
		// super(context, R.style.skuaidiCommonDialog);
		this.context = context;
		initView();
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
		this.title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
	}

	/**
	 * 设置提示信息
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		textContent.setText(content);
	}

	/**
	 * 设置单个按钮文本
	 * 
	 * @param title
	 */
	public void setSingleButtonTitle(String title) {
		btn_single.setText(title);
	}
	/** 设置单个按钮字体颜色 **/
	public void setSingleButtonColor(int res_color){
		btn_single.setTextColor(context.getResources().getColor(res_color));
	}

	/**
	 * 设置中间按钮文本
	 * 
	 * @param title
	 */
	public void setPositiveMiddleBtnTitle(String title) {
		btn_positive_always.setText(title);
	}

	/**
	 * 判断是否使用三个按钮并且中间为textview控件
	 * 
	 * @param isUseTheThreeButton
	 */
	public void isUseTheThreeButton(boolean isUseTheThreeButton) {
		if (isUseTheThreeButton) {
			rl_editBox.setVisibility(View.GONE);
			view_line.setVisibility(View.VISIBLE);
			btn_positive_always.setVisibility(View.VISIBLE);
			textContent.setVisibility(View.VISIBLE);
		} else {
			view_line.setVisibility(View.GONE);
			btn_positive_always.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否使用输入框
	 * 
	 * @param isUseEditText
	 *            true:使用 false:不使用
	 */
	public void isUseEditText(boolean isUseEditText) {
		if (isUseEditText) {
			rl_editBox.setVisibility(View.VISIBLE);
			textContent.setVisibility(View.GONE);
			Utility.showKeyBoard(et_deliver_no,true);
		} else {
			rl_editBox.setVisibility(View.GONE);
			textContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 是否使用单个按钮
	 * 
	 * @param isUseSingleButton
	 *            true:使用 false:不使用
	 */
	public void isUseSingleButton(boolean isUseSingleButton) {
		if (isUseSingleButton) {
			ll_double.setVisibility(View.GONE);
			btn_single.setVisibility(View.VISIBLE);
		} else {
			ll_double.setVisibility(View.VISIBLE);
			btn_single.setVisibility(View.GONE);
		}
	}

	/**
	 * 使用大文本输入框
	 * 
	 * @param isUseBigEditText
	 *            true:使用
	 */
	public void isUseBigEditText(boolean isUseBigEditText) {
		if (isUseBigEditText) {
			rl_editBox.setVisibility(View.GONE);
			ll_write_phoneNo.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置dialog确定或取消不自动隐藏
	 * 
	 * @param donotdismiss
	 */
	public void setDonotAutoDismiss(boolean donotdismiss) {
		this.autoDismiss = donotdismiss;
	}

	/**
	 * 设置dialog隐藏
	 * 
	 */
	public void setDismiss() {
		dismiss();
	}

	/**
	 * 设置大文本输入框提示hint
	 * 
	 * @param hint
	 */
	public void setBigEditTextHint(String hint) {
		et_write_phon.setHint(hint);
		Utility.showKeyBoard(et_write_phon,true);
	}

	/**获取大文本框**/
	public View getBigEdittextView(){
		return et_write_phon;
	}

	/**
	 * setBigEditTextKeyLisenter:设置大输入框中只能输入的字符
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param digits
	 */
	public void setBigEditTextKeyLisenter(String digits) {
		et_write_phon.setKeyListener(DigitsKeyListener.getInstance(digits));
	}

	/** 显示输入框软键盘 **/
	public void showSoftInput(boolean isShow) {
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShow) {
			et_deliver_no.setFocusable(true);
			et_deliver_no.setFocusableInTouchMode(true);
			imm.showSoftInputFromInputMethod(et_deliver_no.getWindowToken(), 0);
		} else {
			imm.hideSoftInputFromWindow(et_deliver_no.getWindowToken(), 0);
		}
	}

	/**
	 * 设置编辑框的监听器
	 * @param watcher
	 */
	public void setEditTextWatcher(TextWatcher watcher){
		et_deliver_no.addTextChangedListener(watcher);
	}


	/**
	 * 
	 * @return 获取大文本输入框中的内容
	 */
	public String getBigEditTextContent() {
		return et_write_phon.getText().toString();
	}

	/**
	 * 
	 * @return 设置大文本输入框中的内容
	 */
	public void setBigEditTextContent(String text) {
		et_write_phon.setText(text);
		et_write_phon.setSelection(text.length());
	}

	/**
	 * 是否使用带颜色按钮，不使用底部按钮
	 * 
	 * @param isUseDoubleButton
	 *            true:使用 false:不使用
	 */
	@SuppressWarnings("deprecation")
	public void isUseDoubleButton(boolean isUseDoubleButton) {
		if (isUseDoubleButton) {
			ll_import_phoneNo.setVisibility(View.VISIBLE);
			rl_editBox.setVisibility(View.GONE);
			rl_body.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_white));// 设置主体背景的四个角为圆角
			ll_bottom_btn.setVisibility(View.GONE);// 隐藏底部按钮区域
		}
	}

	/**
	 * 设置文本框hint
	 * 
	 * @param hint
	 */
	public void setEditTextHint(String hint) {
		// 设置hint字体大小
		SpannableString ss = new SpannableString(hint);
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置hint
		et_deliver_no.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
	}

	/**
	 * @Description:设置文本框内容
	 * @Title: setEditText
	 * @param @return
	 *            设定文件
	 * @return voic 返回类型
	 * @author 顾冬冬
	 */
	public void setEditText(String str) {
		if (!Utility.isEmpty(str)) {
			et_deliver_no.setText(str);
			et_deliver_no.setSelection(et_deliver_no.getText().toString().length());
		} else {
			et_deliver_no.setText("");
		}
	}

	/**
	 * 设置文本框hint颜色
	 * 
	 * @param color
	 */
	public void setEditTExtHintColor(int color) {
		et_deliver_no.setHintTextColor(color);
	}

	/**
	 * 设置编辑框输入弹出键盘类型
	 * 
	 * @param inputTypeStyle
	 */
	public void setEditTextInputTypeStyle(int inputTypeStyle) {
		et_deliver_no.setInputType(inputTypeStyle);
	}

	/**
	 * 设置编辑框中能输入具体个数字符
	 * 
	 * @param length
	 */
	public void setEditTextContent(int length) {
		et_deliver_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
	}

	/**
	 * 设置大编辑框输入弹出键盘类型
	 * 
	 * @param inputTypeStyle
	 */
	public void setBigEditTextInputTypeStyle(int inputTypeStyle) {
		et_write_phon.setInputType(inputTypeStyle);
	}

	/**
	 * 获取文本框内容
	 * 
	 * @return
	 */
	public String getEditTextContent() {
		return et_deliver_no.getText().toString();
	}

	/** 获取文本框视图 **/
	public View getEditTextView() {
		return et_deliver_no;
	}

	/**
	 * 设置确定按钮文案
	 * 
	 * @param positionButtonTitle
	 */
	public void setPositionButtonTitle(String positionButtonTitle) {
		btn_positive.setText(positionButtonTitle);
	}

	/**
	 * 设置取消按钮文案
	 * 
	 * @param negativeButtonTitle
	 */
	public void setNegativeButtonTitle(String negativeButtonTitle) {
		btn_negative.setText(negativeButtonTitle);
	}

	/**
	 * 设置主体中第一个按钮（上）按钮方案
	 * 
	 * @param bodyOfButtonToUpTitle
	 */
	public void setBodyOfButtonToUpTitle(String bodyOfButtonToUpTitle) {
		tv_write_phoneNo.setText(bodyOfButtonToUpTitle);
	}

	/**
	 * 设置主体中第二个按钮（下）按钮方案
	 * 
	 * @param bodyOfButtonToDownTitle
	 */
	public void setBodyOfButtonToDownTitle(String bodyOfButtonToDownTitle) {
		tv_import_unreceipted.setText(bodyOfButtonToDownTitle);
	}

	/**
	 * showEditTextTermsArea:设置是否显示小编辑输入框下方的条件区域
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param isShow
	 */
	public void showEditTextTermsArea(boolean isShow) {
		if (isShow) {
			terms.setVisibility(View.VISIBLE);
		} else {
			terms.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置是否显示图文组合的条件选择区域
	 *
	 * @param isShow
     */
	public void showTermsSelect(boolean isShow){
		if(isShow){
			terms_select.setVisibility(View.VISIBLE);
		}else{
			terms_select.setVisibility(View.GONE);
		}
	}

	/**
	 * isSelectEditTextTermsArea:获取是否显示小编辑输入框下方条件区域状态
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @return
	 */
	public boolean isSelectEditTextTermsArea() {
		return isSelectTerms;
	}

	/**
	 * setEditTextTermsText:输入内容错误提示文字
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param text
	 */
	public void setEditTextTermsText(String text) {
		if (!Utility.isEmpty(text)) {
			terms_warning.setVisibility(View.VISIBLE);
		} else {
			terms_warning.setVisibility(View.GONE);
		}
		terms_warning.setText(text);
	}

	/**
	 * setEditTextTermsText:输入内容错误提示文字颜色
	 * 
	 * 作者： 顾冬冬
	 * 
	 */
	public void setEditTextTermsTextColor(int color) {
		terms_warning.setTextColor(context.getResources().getColor(color));
	}

	/**
	 * setEditTextSelectTermsText:设置选择条件按钮文本文字
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param text
	 */
	public void setEditTextSelectTermsText(String text) {
		tv_terms_name.setText(text);
	}

	/**
	 * setEditTextSelectTermsText:设置选择条件按钮文本文字颜色
	 * 
	 * 作者： 顾冬冬
	 * 
	 */
	public void setEditTextSelectTermsTextColor(int color) {
		tv_terms_name.setTextColor(context.getResources().getColor(color));
	}

	/**
	 * isInputContentFail:获取小输入框中输入的内容是否为不匹配状态
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @return
	 */
	public boolean isInputContentFail() {
		return isInputFailContent;
	}

	/**
	 * setSendSmsNoTerms:【这里用于发送短信界面编号设置（仅）】
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param useLettersAndNumbers
	 */
	public void setSendSmsNoTerms(boolean useLettersAndNumbers) {
		final String digits = "abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		/*
		 * et_deliver_no.setKeyListener(DigitsKeyListener.getInstance(digits));
		 */
		et_deliver_no.setKeyListener(new DigitsKeyListener() {
			@Override
			public int getInputType() {
				return InputType.TYPE_TEXT_VARIATION_PASSWORD;
			}

			@Override
			protected char[] getAcceptedChars() {
				char[] data = digits.toCharArray();
				return data;
			}

		});
		this.useLettersAndNumbers = useLettersAndNumbers;
	}

	private void initView() {
		View layout = inflater.inflate(R.layout.dialog_mannul_input, null);
		title = (TextView) layout.findViewById(R.id.title);
		rl_editBox = (RelativeLayout) layout.findViewById(R.id.rl_editBox);
		ll_import_phoneNo = (LinearLayout) layout.findViewById(R.id.ll_import_phoneNo);
		ll_write_phoneNo = (LinearLayout) layout.findViewById(R.id.ll_write_phoneNo);
		ll_bottom_btn = (LinearLayout) layout.findViewById(R.id.ll_bottom_btn);
		rl_body = (RelativeLayout) layout.findViewById(R.id.rl_body);
		textContent = (TextView) layout.findViewById(R.id.dialog_content);
		et_deliver_no = (EditText) layout.findViewById(R.id.et_deliver_no);
		et_write_phon = (EditText) layout.findViewById(R.id.et_write_phon);
		btn_negative = (Button) layout.findViewById(R.id.btn_negative);
		btn_positive = (Button) layout.findViewById(R.id.btn_positive);
		btn_close = (Button) layout.findViewById(R.id.btn_close);
		btn_single = (Button) layout.findViewById(R.id.btn_single);
		btn_positive_always = (Button) layout.findViewById(R.id.btn_positive_always);
		view_line = layout.findViewById(R.id.view_line);
		ll_double = layout.findViewById(R.id.ll_double);
		terms = (ViewGroup) layout.findViewById(R.id.terms);
		terms_warning = (TextView) layout.findViewById(R.id.terms_warning);
		terms_select = (ViewGroup) layout.findViewById(R.id.terms_select);
		iv_select_indentity = (ImageView) layout.findViewById(R.id.iv_select_indentity);
		tv_terms_name = (TextView) layout.findViewById(R.id.tv_terms_name);

		tv_write_phoneNo = (TextView) layout.findViewById(R.id.tv_write_phoneNo);
		tv_import_unreceipted = (TextView) layout.findViewById(R.id.tv_import_unreceipted);

		et_deliver_no.setPadding(5, 15, 5, 15);

		et_deliver_no.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (!Utility.isEmpty(et_deliver_no.getText().toString())) {
					btn_close.setVisibility(View.VISIBLE);
					et_deliver_no.setPadding(5, 15, 35, 15);
				} else {
					btn_close.setVisibility(View.GONE);
					et_deliver_no.setPadding(5, 15, 5, 15);
				}

				if (useLettersAndNumbers) {
					String pattern = "^[A-Za-z]{1}[0-9]{0,4}|^[A-Za-z]{2}[0-9]{0,3}|^[0-9]{0,5}";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(arg0);
					if (!m.matches()) {
						setEditTextTermsText("输入编号有误");
						setEditTextTermsTextColor(R.color.red1);
						isInputFailContent = false;// 标记为输入错误状态
					} else {
						setEditTextTermsText("");
						isInputFailContent = true;// 标记为输入正确状态
					}
					// et_deliver_no.removeTextChangedListener(this);//解除文字改变事件
					// et_deliver_no.setText(arg0.toString().toUpperCase());//转换
					// et_deliver_no.setSelection(arg0.toString().length());//重新设置光标位置
					// et_deliver_no.addTextChangedListener(this);//重新绑
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (useLettersAndNumbers) {
					final String temp = s.toString();
					if (!Utility.isEmpty(temp)) {

						String tem = temp.substring(temp.length() - 1, temp.length());
						char[] temC = tem.toCharArray();
						int mid = temC[0];

						// 判断如果是小写的字母的换，就转换
						if (mid >= 97 && mid <= 122) {// 小写字母
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									// 小写转大写
									et_deliver_no.setText(temp.toUpperCase());
									et_deliver_no.setSelection(temp.toString().length());// 重新设置光标位置
								}
							}, 300);
						}
					}
				}
			}
		});

		terms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSelectTerms) {
					isSelectTerms = false;// 状态设置为未选中
					iv_select_indentity.setBackgroundResource(R.drawable.select_edit_identity);
				} else {
					isSelectTerms = true;// 状态设置为选中
					iv_select_indentity.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				}
			}
		});

		btn_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				et_deliver_no.setText("");
			}
		});

		tv_write_phoneNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bodyOfButtonToUpOnclickListener != null) {
					bodyOfButtonToUpOnclickListener.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		tv_import_unreceipted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bodyOfButtonToDownOnlclickListener != null) {
					bodyOfButtonToDownOnlclickListener.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		btn_negative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (negativeButtonOnclickListener != null) {
					negativeButtonOnclickListener.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		btn_positive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (positonButtonOnclickListener != null) {
					positonButtonOnclickListener.onClick(arg0);
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		btn_single.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (singleButtonOnclickListener != null) {
					singleButtonOnclickListener.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		btn_positive_always.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (positiveAlwaysButtonOnclickListner != null) {
					positiveAlwaysButtonOnclickListner.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
			}
		});

		Window dialogWindow = this.getWindow();
		// WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// lp.alpha = 0.7f;
		// dialogWindow.setAttributes(lp);
		dialogWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		dialogWindow.setGravity(Gravity.CENTER);
		setContentView(layout);
	}

	/**
	 * @Description: 设置单个按钮点击时候的事件
	 * @Title: setSingleClickListener
	 * @Author 顾冬冬
	 * @CreateDate 2015-7-16 下午4:17:10
	 * @Param @param singleButtonOnclickListener
	 * @Return void
	 */
	public void setSingleClickListener(SingleButtonOnclickListener singleButtonOnclickListener) {
		this.singleButtonOnclickListener = singleButtonOnclickListener;
	}

	/**
	 * 设置确定按钮点击事件||单个按钮点击事件
	 * 
	 * @param positionClickListener
	 */
	public void setPosionClickListener(PositonButtonOnclickListener positionClickListener) {
		this.positonButtonOnclickListener = positionClickListener;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 * @param negativeclickListener
	 */
	public void setNegativeClickListener(NegativeButtonOnclickListener negativeclickListener) {
		this.negativeButtonOnclickListener = negativeclickListener;
	}

	/**
	 * 设置中间按钮点击事件
	 * 
	 * @param positonButtonOnclickListener
	 */
	public void setPositiveMiddleClickListener(PositiveAlwaysButtonOnclickListener positonButtonOnclickListener) {
		this.positiveAlwaysButtonOnclickListner = positonButtonOnclickListener;
	}

	/**
	 * 设置主体中第一个按钮（上）点击事件
	 * 
	 * @param bodyOfButtonToUpOnclickListener
	 */
	public void setBodyOfButtonToUpClickListener(BodyOfButtonToUpOnclickListener bodyOfButtonToUpOnclickListener) {
		this.bodyOfButtonToUpOnclickListener = bodyOfButtonToUpOnclickListener;
	}

	/**
	 * 设置主体中第二个按钮（下）点击事件
	 * 
	 * @param bodyOfButtonToDownOnlclickListener
	 */
	public void setBodyOfButtonToDownClickListener(BodyOfButtonToDownOnlclickListener bodyOfButtonToDownOnlclickListener) {
		this.bodyOfButtonToDownOnlclickListener = bodyOfButtonToDownOnlclickListener;
	}

	public void showDialog() {
		this.show();
	}

	public interface SingleButtonOnclickListener {
		void onClick();
	}

	public interface PositonButtonOnclickListener {
		void onClick(View v);
	}

	public interface NegativeButtonOnclickListener {
		void onClick();
	}

	public interface PositiveAlwaysButtonOnclickListener {
		void onClick();
	}

	public interface BodyOfButtonToUpOnclickListener {
		void onClick();
	}

	public interface BodyOfButtonToDownOnlclickListener {
		void onClick();
	}
}
