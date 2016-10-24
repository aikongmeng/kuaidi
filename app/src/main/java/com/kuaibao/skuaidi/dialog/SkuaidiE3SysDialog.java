package com.kuaibao.skuaidi.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.EthreeChoiceIdAdapter;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.ZTOutletsActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

/**
 * 
 * E3相关通用DiaLog
 * 
 * @author xy
 * 
 */
public class SkuaidiE3SysDialog {

	private Context context;
	private TextView title, textContent, tv_ct6_top;// 标题
	private Button btn_negative;// 取消按钮
	public Button btn_positive;// 确定按钮
	private Button btn_single;
	private TextView courier_name, tv_courier_name;
	private TextView courier_phone;
	private TextView courier_latticepoint, tv_courier_latticepoint, tv_notice_content, tv_dialog6_flag, tv_content8;
	private TextView courier_jobNo;
	private ImageView iv_title;
	public TextView content;
	public TextView tv_edit_OutletsCode;

	public EditText et_jobNo, et_pwd, et_courier;
	private ImageView iv_jobNo_clear;
	private View ll_double, ll_ct6_bottom;
	private LayoutInflater inflater;
	private PositiveButtonOnclickListener positiveButtonOnclickListener;
	private NegativeButtonOnclickListener negativeButtonOnclickListener;
	private EditTextMaxLengthListener editTextMaxLengthListener;
	private E3Dialog e3Dialog;
	private EthreeChoiceIdAdapter mAdapter;
	private AdapterItemClickListener adapterItemClickListener;
	private int choice_index;
	private TextView tv_point_id;
	private int type;
	public static final int TYPE_NONE_COURIER_INFO = 3;
	public static final int TYPE_HAVE_COURIER_INFO = 0;
	public static final int TYPE_ISNOT_COURIER_INFO = 1;
	public static final int TYPE_MAKESURE_COURIER_INFO = 2;
	public static final int TYPE_CHOICE_COURIER_INFO = 4;
	public static final int TYPE_COMMON = 5;
	public static final int TYPE_DESIGNATED_PERSONNEL = 6;
	public static final int TYPE_ZT_VERIFY = 7;// 中通认证，可输入工号密码
	/** 中通巴枪重新审核 */
	public static final int TYPE_ZT_REAUDIT = 8;
	private View v;
	private ListView lv_choice_id;
	private String getCourierNum;
	public static final int REQUEST_CODE = 1;
	private String courierNO;

	private TextView tv_remember;
	
	private String scanType;
	
	public boolean isChecked = false;//记住工号

	public SkuaidiE3SysDialog(Context context, int type, View v) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.type = type;
		e3Dialog = new E3Dialog(context);
		this.v = v;
	}

	public SkuaidiE3SysDialog(Context context, int type, View v, EditTextMaxLengthListener editTextMaxLengthListener,String scanType) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.type = type;
		this.v = v;
		this.editTextMaxLengthListener = editTextMaxLengthListener;
		this.scanType=scanType;
		e3Dialog = new E3Dialog(context);
	}
	

	private class E3Dialog extends PopupWindow {

		private TextView choice_certain;

		public E3Dialog(Context context) {
			super(context);
			initView();
		}

		private void initView() {
			View layout = inflater.inflate(R.layout.e3_dialog_layout, null);
			layout.setBackgroundDrawable(new ColorDrawable(0xb0000000));
			title = (TextView) layout.findViewById(R.id.title);
			tv_ct6_top = (TextView) layout.findViewById(R.id.tv_ct6_top);
			textContent = (TextView) layout.findViewById(R.id.tv_content);
			btn_negative = (Button) layout.findViewById(R.id.btn_negative);
			btn_positive = (Button) layout.findViewById(R.id.btn_positive);
			btn_single = (Button) layout.findViewById(R.id.btn_single);
			ll_double = layout.findViewById(R.id.ll_double);
			if (type == TYPE_HAVE_COURIER_INFO) {
				View content = layout.findViewById(R.id.dialog_content);
				courier_name = (TextView) content.findViewById(R.id.courier_name);
				courier_phone = (TextView) content.findViewById(R.id.courier_phone);
				courier_latticepoint = (TextView) content.findViewById(R.id.courier_latticepoint);
				courier_jobNo = (TextView) content.findViewById(R.id.courier_job_number);
				tv_point_id = (TextView) content.findViewById(R.id.tv_point_id);
				content.setVisibility(View.VISIBLE);
			} else if (type == TYPE_ISNOT_COURIER_INFO) {
				layout.findViewById(R.id.dialog_content2).setVisibility(View.VISIBLE);
			} else if (type == TYPE_MAKESURE_COURIER_INFO) {
				layout.findViewById(R.id.dialog_content3).setVisibility(View.VISIBLE);
				tv_notice_content = (TextView) layout.findViewById(R.id.tv_notice_content);
				iv_title = (ImageView) layout.findViewById(R.id.iv_title);
			} else if (type == TYPE_COMMON || type == TYPE_NONE_COURIER_INFO) {
				content = (TextView) layout.findViewById(R.id.dialog_content5);
				content.setVisibility(View.VISIBLE);
			} else if (type == TYPE_CHOICE_COURIER_INFO) {
				LinearLayout content1 = (LinearLayout) layout.findViewById(R.id.dialog_content_lv);
				content1.setVisibility(View.VISIBLE);
				lv_choice_id = (ListView) layout.findViewById(R.id.lv_choice_id);

				courier_phone = (TextView) layout.findViewById(R.id.choice_courier_phone);
				choice_certain = (TextView) layout.findViewById(R.id.choice_certain);

				/**
				 * 用户信息列表的监听 得到选中的用户信息
				 */
				lv_choice_id.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView, View convertView, int position, long id) {
						mAdapter.setChecked(position);
						choice_index = position;

					}
				});

				// content.setVisibility(View.VISIBLE);
			} else if (type == TYPE_DESIGNATED_PERSONNEL) {
				layout.findViewById(R.id.dialog_content6).setVisibility(View.VISIBLE);
				tv_courier_latticepoint = (TextView) layout.findViewById(R.id.tv_courier_latticepoint);
				tv_courier_name = (TextView) layout.findViewById(R.id.tv_courier_name);
				et_jobNo = (EditText) layout.findViewById(R.id.et_ct6);
				tv_remember = (TextView) layout.findViewById(R.id.tv_remember);
				iv_jobNo_clear = (ImageView) layout.findViewById(R.id.iv_et_clear);
				ll_ct6_bottom = layout.findViewById(R.id.ll_ct6_bottom);
				tv_dialog6_flag = (TextView) layout.findViewById(R.id.tv_dialog6_flag);
				TextWatcher textWatcher = new MYTextWatcher();
				courierNO = E3SysManager.getCourierNO();
				
				String jonNO = SkuaidiSpf.getRememberJobNO(courierNO,scanType);
				if (TextUtils.isEmpty(jonNO)) {
					if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
						et_jobNo.setHint("请输入员工编号或手机号");
					} else if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
						et_jobNo.setHint("请输入员工编号（最后四位）");
					} else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
						et_jobNo.setHint("请输入员工编号");
					}
				} else {
					//获取带上次记录的工号，显示出来
					et_jobNo.setText(jonNO);
					et_jobNo.setSelection(et_jobNo.getText().length());
					//调用接口获取网点，姓名
					textWatcher.afterTextChanged(et_jobNo.getText());
					tv_remember.setCompoundDrawablesWithIntrinsicBounds(
							context.getResources().getDrawable(R.drawable.batch_add_checked), null, null, null);
					isChecked=true;
				}

				tv_remember.setOnClickListener(new OnClickListener() {
					

					@Override
					public void onClick(View v) {
						if (!isChecked) {
							tv_remember.setCompoundDrawablesWithIntrinsicBounds(
									context.getResources().getDrawable(R.drawable.batch_add_checked), null, null, null);
							// 保存指定工号
							isChecked = true;
						} else {
							tv_remember.setCompoundDrawablesWithIntrinsicBounds(
									context.getResources().getDrawable(R.drawable.select_edit_identity), null, null,
									null);
							isChecked = false;
						}

					}
				});
				et_jobNo.addTextChangedListener(textWatcher);
				iv_jobNo_clear.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						et_jobNo.setText("");
					}
				});
			} else if (type == TYPE_ZT_VERIFY) {// 中通认证，可输入工号密码
				layout.findViewById(R.id.dialog_content6).setVisibility(View.VISIBLE);
				content = (TextView) layout.findViewById(R.id.dialog_content5);
				content.setVisibility(View.VISIBLE);
				layout.findViewById(R.id.fl_pwd).setVisibility(View.VISIBLE);
				tv_courier_latticepoint = (TextView) layout.findViewById(R.id.tv_courier_latticepoint);
				tv_courier_name = (TextView) layout.findViewById(R.id.tv_courier_name);
				et_jobNo = (EditText) layout.findViewById(R.id.et_ct6);
				et_pwd = (EditText) layout.findViewById(R.id.et_pwd);
				tv_edit_OutletsCode = (TextView) layout.findViewById(R.id.tv_edit_OutletsCode);

				et_jobNo.setHint("请输入工号");
				iv_jobNo_clear = (ImageView) layout.findViewById(R.id.iv_et_clear);
				ll_ct6_bottom = layout.findViewById(R.id.ll_ct6_bottom);
				tv_dialog6_flag = (TextView) layout.findViewById(R.id.tv_dialog6_flag);
				et_courier = (EditText) layout.findViewById(R.id.et_ct7);
				if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
					et_jobNo.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							UMShareManager.onEvent(context, "SkuaidiE3SysDialog", "ZT", "ZT：选择网点");
							if (et_pwd.getVisibility() != View.VISIBLE) {
								Intent intent = new Intent(context, ZTOutletsActivity.class);
								((Activity) context).startActivityForResult(intent, REQUEST_CODE);
							}

						}
					});

					et_courier.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							UMShareManager.onEvent(context, "SkuaidiE3SysDialog", "ZT", "ZT：选择网点");
							if (et_pwd.getVisibility() != View.VISIBLE) {
								Intent intent = new Intent(context, ZTOutletsActivity.class);
								((Activity) context).startActivityForResult(intent, REQUEST_CODE);
							}

						}
					});
				}

			} else if (type == TYPE_ZT_REAUDIT) {
				layout.findViewById(R.id.dialog_content8).setVisibility(View.VISIBLE);
				tv_content8 = (TextView) layout.findViewById(R.id.tv_content8);
				layout.findViewById(R.id.tv_readuit).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						UMShareManager.onEvent(context, "SkuaidiE3SysDialog", "ZT", "ZT：重新审核");
						//((BusinessActivity) context).reAudit();
					}
				});
			}
			btn_negative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mAdapter != null) {
						arg0.setTag(choice_index);
					}
					if (negativeButtonOnclickListener != null) {
						negativeButtonOnclickListener.onClick(arg0);
					}
					dismiss();
				}
			});

			btn_positive.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (positiveButtonOnclickListener != null) {
						positiveButtonOnclickListener.onClick();
					}
				}
			});

			btn_single.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (positiveButtonOnclickListener != null) {
						positiveButtonOnclickListener.onClick();
					}
					dismiss();
				}
			});

			layout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (isEnable == true) {
						// dismiss();
					}
				}
			});
			update();
			setFocusable(true);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setContentView(layout);
			setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

			setBackgroundDrawable(new ColorDrawable());
		}
	}

	boolean isEnable = true;

	public void isEnable(boolean isEnable) {
		this.isEnable = isEnable;
		e3Dialog.setBackgroundDrawable(null);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * 
	 * @param adapter
	 */
	public void setAdapter(EthreeChoiceIdAdapter adapter) {
		mAdapter = adapter;
		lv_choice_id.setAdapter(mAdapter);
		lv_choice_id.getLayoutParams().height = 300;
	}

	/**
	 * 设置提示信息
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		switch (type) {
		case TYPE_MAKESURE_COURIER_INFO:
			tv_notice_content.setText(content);
			break;
		case TYPE_ZT_REAUDIT:
			tv_content8.setText(content);
		default:
			break;
		}
		textContent.setText(textContent.getText() + content);
	}

	public void setCommonContent(CharSequence content) {
		this.content.setText(content);
	}

	public void setTitleimage(int bit) {
		iv_title.setImageResource(bit);
	}

	/**
	 * 设置单个按钮文本
	 * 
	 * @param title
	 */
	public void setSingleButtonTitle(String title) {
		btn_single.setText(title);
	}

	/*
	 * public void setLatticepointId(String courierLatticepointID) { if (type ==
	 * TYPE_DESIGNATED_PERSONNEL) { tv_courier_latticepoint.setText("网点ID：  " +
	 * courierLatticepointID); ll_ct6_bottom.setVisibility(View.VISIBLE);
	 * tv_dialog6_flag.setVisibility(View.GONE); return; }
	 * courier_latticepoint.setText(courier_latticepoint.getText() +
	 * courierLatticepointID); }
	 */
	public void setCourierLatticepoint(String courierLatticepoint) {
		if (type == TYPE_DESIGNATED_PERSONNEL) {
			tv_courier_latticepoint.setText("网点：  " + courierLatticepoint);
			ll_ct6_bottom.setVisibility(View.VISIBLE);
			tv_dialog6_flag.setVisibility(View.GONE);
			return;
		}
		courier_latticepoint.setText(courier_latticepoint.getText() + courierLatticepoint);
	}

	public void setCourierName(String courierName) {
		if (type == TYPE_DESIGNATED_PERSONNEL) {
			tv_courier_name.setText("姓名：  " + courierName);
			ll_ct6_bottom.setVisibility(View.VISIBLE);
			tv_dialog6_flag.setVisibility(View.GONE);
			return;
		}
		courier_name.setText(courier_name.getText() + courierName);
	}

	public String getCourierName() {
		if (TextUtils.isEmpty(getEditTextContent().trim())) {
//			return BackUpService
//					.getfinalDb()
//					.findAllByWhere(CourierReviewInfo.class,
//							"courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'").get(0)
//					.getCourierName();
			return E3SysManager.getReviewInfoNew().getCounterman_name();
		} else {
			return tv_courier_name.getText().toString().substring(3).trim();
		}
	}

	public void setCourierPhone(String courierPhone) {
		courier_phone.setText(courier_phone.getText() + courierPhone);
	}

	public void setTvPonitId(String pointid) {
		tv_point_id.setText(tv_point_id.getText() + pointid);
	}

	public View getTVPoint() {
		return tv_point_id;
	}

	public void setDesignatedPersonnelHint(String hint) {
		tv_dialog6_flag.setText(hint);
		tv_dialog6_flag.setVisibility(View.VISIBLE);
		ll_ct6_bottom.setVisibility(View.GONE);
	}

	public void setCourierJobNum(String jobNum) {
		courier_jobNo.setText(courier_jobNo.getText() + jobNum);
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

	public void setDialogType(int type) {
		this.type = type;
	}

	/**
	 * 设置确定按钮文案
	 * 
	 * @param positiveButtonTitle
	 */
	public void setPositiveButtonTitle(String positiveButtonTitle) {
		btn_positive.setText(positiveButtonTitle);
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
	 * 设置确定按钮点击事件||单个按钮点击事件
	 * 
	 * @param positiveClickListener
	 */
	public void setPositiveClickListener(PositiveButtonOnclickListener positiveClickListener) {
		this.positiveButtonOnclickListener = positiveClickListener;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 * @param negativeclickListener
	 */
	public void setNegativeClickListener(NegativeButtonOnclickListener negativeclickListener) {
		this.negativeButtonOnclickListener = negativeclickListener;
	}

	public void setAdapterItemClickListener(AdapterItemClickListener adapterItemClickListener) {
		this.adapterItemClickListener = adapterItemClickListener;
	}

	public void setEditTextMaxLengthListener(EditTextMaxLengthListener editTextMaxLengthListener) {
		this.editTextMaxLengthListener = editTextMaxLengthListener;
	}

	public void showDialog() {
		new Handler().postDelayed(new Runnable() {

			public void run() {
				e3Dialog.showAtLocation(v, Gravity.CENTER, 0, 0);
			}

		}, 100L);

	}

	public boolean isShowing() {
		return e3Dialog.isShowing();
	}

	public void dismiss() {
		if (e3Dialog.isShowing()) {
			e3Dialog.dismiss();
		}
	}

	public String getEditTextContent() {
		if (et_jobNo != null && et_jobNo.getText() != null)
			return et_jobNo.getText().toString().trim();
		else
			return "";
	}

	public String getCourierContent() {
		if (et_courier != null && et_courier.getText() != null)
			return et_courier.getText().toString();
		else
			return "";
	}

	/**
	 * EditText 不可编辑
	 */
	public void setEditTextEnable() {
		et_jobNo.setKeyListener(null);
	}

	public void setEditTextCount(String count) {
		et_jobNo.setText(count);
	}

	public String getEditText_PWD_Content() {
		if (et_pwd != null)
			return et_pwd.getText().toString();
		return "";
	}

	public void setEditText_PWD_visiability(boolean b) {
		if (b) {
			et_pwd.setVisibility(View.VISIBLE);
		} else {
			et_pwd.setVisibility(View.GONE);
		}

	}

	public void setHintVisibility(boolean b) {
		if (b) {
			tv_ct6_top.setVisibility(View.VISIBLE);
		} else {
			tv_ct6_top.setVisibility(View.GONE);
		}

	}

	public void setCourierNum(String getCourierNum) {
		this.getCourierNum = getCourierNum;
	}

	public String getCourierNum() {
		return getCourierNum;
	}

	public CourierReviewInfo getCheckedInfo() {
		return null;
	}

	public interface PositiveButtonOnclickListener {
		void onClick();
	}

	public interface NegativeButtonOnclickListener {
		void onClick(View v);
	}

	public interface AdapterItemClickListener {
		void onClick(int id);
	}

	public interface EditTextMaxLengthListener {
		void onEditTextMaxLength(SkuaidiE3SysDialog e3SysDialog, EditText edit, String content);
	}

	class MYTextWatcher implements TextWatcher {
		boolean flag = true;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
				// 全峰员工号并没有长度规则
				flag = s.toString().length() < 15 && s.toString().length() >= 6;
			} else if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
				flag = s.toString().length() <= 3;
			}
			if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
				// 中通员工号并没有长度规则
				flag = s.toString().length() < 15 && s.toString().length() >= 6;
			}

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			if (s.toString().length() == 0) {
				iv_jobNo_clear.setVisibility(View.GONE);
				ll_ct6_bottom.setVisibility(View.GONE);
				tv_dialog6_flag.setVisibility(View.GONE);
			} else {
				iv_jobNo_clear.setVisibility(View.VISIBLE);
			}
			if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
				if (s.toString().length() == 5) {
					s.delete(4, 5);
				}
				if (s.toString().length() == 4 && flag == true) {
					if (editTextMaxLengthListener != null) {
						editTextMaxLengthListener.onEditTextMaxLength(SkuaidiE3SysDialog.this, et_jobNo, s.toString());
					}
				}
			} else if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())
					|| "zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
				if (s.toString().length() > 6 && flag == true) {
					if (editTextMaxLengthListener != null) {
						editTextMaxLengthListener.onEditTextMaxLength(SkuaidiE3SysDialog.this, et_jobNo, s.toString());
					}
				}

			}

		}
	}

}
