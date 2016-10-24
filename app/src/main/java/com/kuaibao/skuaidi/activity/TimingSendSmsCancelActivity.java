package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.SelectTimePop;
import com.kuaibao.skuaidi.activity.view.ToastCustom;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @ClassName: SmsTimingSendCancelActivity
 * @Description: 定时发送修改或取消界面
 * @author 顾冬冬
 * @date 2015-12-8 上午9:54:57
 */
public class TimingSendSmsCancelActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private static final int SELECT_NEW_MODEL = 0X1001;

	private Context mContext = null;
	private Intent mIntent = null;
	private SkuaidiDB skuaidiDB = null;
	private String DH = "#DHDHDHDHDH#";
	private String SURL = "#SURLSURLSURLSURLS#";

	private DraftBoxSmsInfo draftBoxSmsInfo = null;
	private ToastCustom toast = null;// 自定义吐司-显示在屏幕中央的大toast
	private SelectTimePop pop = null;// 选择定时发送时间
	private TextInsertImgParser mTextInsertImgParser = null;// 模板中用于替换图片的类
	private SkuaidiDialog dialog = null;

	private SkuaidiImageView back = null;
	private TextView title = null;
	private SkuaidiTextView more = null;

	private RelativeLayout selectTiming = null;// 选择定时发送时间按钮
	private RelativeLayout selectModel = null;// 选择模板
	private TextView tvSendTime = null;// 定时发送时间
	private TextView tvModelTitle = null;// 模板标题
	private TextView tvModelContent = null;// 模板内容
	private TextView tvContacts = null;// 显示联系人
	private Button btnDeleteTiming = null;// 删除并取消定时发送按钮

	private List<ReplyModel> models = null;// 模板集合

	private String timeId = "";// 条目ID
	private String sendTiming = "";// 定时发送时间
	private String modelTitle = "";// 模板的title
	private String modelContent = "";// 模板的详细内容
	private String sendPhone = "";// 手机号码

	private long timeStamp = 0;// 选择好的时间戳变量
	private String modelId = "";// 重新选择的模板ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.sms_timing_send_cancel_activity);
		mContext = this;
		skuaidiDB = SkuaidiDB.getInstanse(mContext);
		mTextInsertImgParser = new TextInsertImgParser(mContext);
		initView();
		getData();
	}

	private void initView() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);
		more = (SkuaidiTextView) findViewById(R.id.tv_more);

		selectTiming = (RelativeLayout) findViewById(R.id.selectTiming);
		selectModel = (RelativeLayout) findViewById(R.id.selectModel);

		tvSendTime = (TextView) findViewById(R.id.tvSendTime);
		tvModelTitle = (TextView) findViewById(R.id.tvModelTitle);
		tvModelContent = (TextView) findViewById(R.id.tvModelContent);
		tvContacts = (TextView) findViewById(R.id.tvContacts);
		btnDeleteTiming = (Button) findViewById(R.id.btnDeleteTiming);

		selectTiming.setOnClickListener(this);
		selectModel.setOnClickListener(this);
		back.setOnClickListener(this);
		more.setOnClickListener(this);
		btnDeleteTiming.setOnClickListener(this);

		more.setText("保存");
		more.setVisibility(View.GONE);
	}

	private void getData() {
		toast = new ToastCustom(mContext, 5, title);
		pop = new SelectTimePop(mContext, this);
		draftBoxSmsInfo = (DraftBoxSmsInfo) getIntent().getSerializableExtra("draftBoxRecord");

		timeId = draftBoxSmsInfo.getId();
		sendTiming = DateHelper.getTimeStamp(DateHelper.getTimeStamp(draftBoxSmsInfo.getSendTime(),
				DateHelper.YYYY_MM_DD_HH_MM_SS));
		modelTitle = draftBoxSmsInfo.getModelTitle();
		modelContent = draftBoxSmsInfo.getSmsContent();
		sendPhone = draftBoxSmsInfo.getPhoneNumber();

		timeStamp = DateHelper.getTimeStamp(draftBoxSmsInfo.getSendTime(), DateHelper.YYYY_MM_DD_HH_MM_SS);

		tvSendTime.setText(sendTiming.substring(5, 17));
		title.setText(sendPhone);

		if (!Utility.isEmpty(modelTitle)) {
			tvModelTitle.setText(modelTitle);
		} else {
			tvModelTitle.setText("");
		}

		tvModelContent.setText(mTextInsertImgParser.replace(replaceText(modelContent)));
		tvContacts.setText(sendPhone);

	}

	/** 替换模板 **/
	private String replaceText(String content) {
		String context = content;
		if (content.contains("#DH#")) {
			context = context.replaceAll("#DH#", DH);
		}
		if (context.contains("#SURL#")) {
			context = context.replaceAll("#SURL#", SURL);
		}
		return context;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.tv_more:
			if (Utility.isNetworkConnected() == true) {
				updateTiming(timeId, timeStamp, modelId);
			} else {
				UtilToolkit.showToast("没有网络");
			}
			break;

		case R.id.selectTiming:// 选择定时发送时间
			// pop = new SelectTimePop(mContext, this);
			// pop.showAtLocation(selectTiming, Gravity.BOTTOM, 0, 0);
			pop.showPopupWindow(selectTiming);
			break;
		case R.id.selectModel:// 选择模板
			mIntent = new Intent(mContext, ModelActivity.class);
			mIntent.putExtra("from_activity", "selectTimingModel");
			startActivityForResult(mIntent, SELECT_NEW_MODEL);
			break;
		case R.id.btnDeleteTiming:// 删除定时内容
			if (Utility.isNetworkConnected() == true) {
				dialog = new SkuaidiDialog(mContext);
				dialog.setTitle("删除提示");
				dialog.setContent("删除后将取消定时发送,\n确定要删除？");
				dialog.setPositionButtonTitle("是");
				dialog.setNegativeButtonTitle("否");
				dialog.isUseEditText(false);
				dialog.setPosionClickListener(new PositonButtonOnclickListener() {

					@Override
					public void onClick(View v) {
						deleteTiming(timeId);
					}
				});
				dialog.showDialog();
			} else {
				UtilToolkit.showToast("没有网络");
			}
			break;
		case R.id.ll_ok:// 选择好时间
			if (pop.isMoreThanTheCurrent10Minutes()) {
				timingTransmission();
				more.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}

	}

	/** 确认使用定时发送功能 **/
	private void timingTransmission() {
		timeStamp = pop.getTimeStamp();
		tvSendTime.setText(pop.getSendTimeStr());
		pop.dismiss();
	}

	/**
	 * @Title: updateTiming
	 * @Description:更新定时发送消息
	 * @param id
	 * @param send_timing
	 * @param tid
	 * @author: 顾冬冬
	 * @return void
	 */
	private void updateTiming(String id, long send_timing, String tid) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/update_timing");
			data.put("role", "courier");
			data.put("id", id);
			data.put("send_time", send_timing);
			data.put("tid", tid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/** 删除定时发送 **/
	private void deleteTiming(String id) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/delete_timing");
			data.put("role", "courier");
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_NEW_MODEL) {
			getModel();
			more.setVisibility(View.VISIBLE);
		}
	}

	private void getModel() {
		models = skuaidiDB.getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN);
		if (null == models || 0 == models.size())
			return;

		ReplyModel model = new ReplyModel();
		for (int i = 0; i < models.size(); i++) {
			if (models.get(i).isChoose() == true) {
				model = models.get(i);
				break;
			}
		}
		modelTitle = model.getTitle();
		modelContent = model.getModelContent();// 获取数据库中被选中的模板内容
		modelId = model.getTid();// 获取数据库中被选中的模板ID
		tvModelTitle.setText(modelTitle);
		tvModelContent.setText(mTextInsertImgParser.replace(replaceText(modelContent)));
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		if (!Utility.isEmpty(sname) && "inform_user/delete_timing".equals(sname)) {
			toast.show(msg);
			finish();
		} else if (!Utility.isEmpty(sname) && "inform_user/update_timing".equals(sname)) {
			toast.show(msg);
			more.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (!Utility.isEmpty(sname) && "inform_user/update_timing".equals(sname)) {
			if (!Utility.isEmpty(data_fail)) {
				try {
					String recharge = data_fail.getString("recharge");
					if (!Utility.isEmpty(recharge) && recharge.equals("y")) {
						dialog = new SkuaidiDialog(mContext);
						dialog.setTitle("提示");
						dialog.setContent(result);
						dialog.setPositionButtonTitle("充值");
						dialog.setNegativeButtonTitle("取消");
						dialog.isUseEditText(false);
						dialog.setPosionClickListener(new PositonButtonOnclickListener() {
							@Override
							public void onClick(View v) {
								mIntent = new Intent(mContext, TopUpActivity.class);
								startActivity(mIntent);
							}
						});
						dialog.showDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				toast.show(result);
			}
		} else {
			if (!Utility.isEmpty(result)) {
				toast.show(result);
			}
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
