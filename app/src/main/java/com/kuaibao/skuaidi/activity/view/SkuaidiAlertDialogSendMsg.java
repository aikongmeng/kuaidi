package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SkuaidiAlertDialogSendMsg implements OnClickListener{
	/**短信里面一键云呼**/
	public static final int FROM_SEND_VOICE = 0;
	/**短信里面一键发短信**/
	public static final int FROM_SEND_MSG = 1;
	/**云呼里面给呼叫失败发短信**/
	public static final int FROM_VOICE_SEND_VOICE = 2;
	/**云呼里面给呼叫失败重新发起呼叫**/
	public static final int FROM_VOICE_SEND_MSG = 3;
	
	// 因为单号是多少位不确定、在添加模板那字数长度设置时有问题，
	private String ordernum = "#NO#";
	private String orderDH = "#DHDHDHDHDH#";
	private String model_url = "#SURLSURLSURLSURLS#";
	
	private Context mContext = null;
	private Display display = null;
	private Dialog dialog = null;
	private SkuaidiDB skuaidiDB = null;
	private BtnOnClickListener btnOnClickListener = null;
	private View view = null;
	private TextView tvTitle = null;// dialog 标题
	private LinearLayout llDefault = null;// 添加模板按钮
	private RelativeLayout rlVoiceModel = null;// 语音模板使用
	private RelativeLayout rlMsgModel = null;// 短信模板使用
	private TextView tvModelTitle = null;// 语音模板标题
	private TextView tvVoiceTime = null;// 语音模板时长 
	private TextView tvMsgModelDesc = null;// 短信模板内容 
	private TextView tvCountOfPhone = null;// 接口返回号码条数
	private Button btnCancel,btnOk;
	
	private int from_event = -1;
	// 短信模板参数********************
	private List<ReplyModel> models = null;// 模板集合
	private String modelContent = "";// 保存模板内容
	private String modelID = "";// 保存模板ID
	private String modelStatus = "";// 保存模板审核状态
	// 语音模板参数********************
	private List<CloudRecord> cloudRecords = new ArrayList<CloudRecord>();
	private String modelVoiceID = "";// 语音模板ID
	private String modelVoiceStatus = "";// 语音模板状态
	
	private LinearLayout llAlertDialogSendVoice = null;

	public SkuaidiAlertDialogSendMsg(Context context,int from) {
		this.mContext = context;
		this.from_event = from;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		skuaidiDB = SkuaidiDB.getInstanse(mContext);
	}
	
	public SkuaidiAlertDialogSendMsg builder() {
		// 获取Dialog布局
		view = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_send_cloud_voice, null);

		initView();
		// 定义Dialog布局和参数
		dialog = new Dialog(mContext, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		llAlertDialogSendVoice.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

		return this;
	}
	
	public SkuaidiAlertDialogSendMsg setOnclickListener(BtnOnClickListener btnOnClickListener){
		this.btnOnClickListener = btnOnClickListener;
		return this;
	}
	
	private void initView(){
		llAlertDialogSendVoice = (LinearLayout) view.findViewById(R.id.llAlertDialogSendVoice);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);// dialog 标题
		llDefault = (LinearLayout) view.findViewById(R.id.llDefault);// 添加模板
		rlVoiceModel = (RelativeLayout) view.findViewById(R.id.rlVoiceModel);// 语音模板使用
		rlMsgModel = (RelativeLayout) view.findViewById(R.id.rlMsgModel);// 短信模板使用
		tvModelTitle = (TextView) view.findViewById(R.id.tvModelTitle);// 语音模板标题
		tvVoiceTime = (TextView) view.findViewById(R.id.tvVoiceTime);// 语音模板时长 
		tvMsgModelDesc = (TextView) view.findViewById(R.id.tvMsgModelDesc);// 短信模板内容 
		tvCountOfPhone = (TextView) view.findViewById(R.id.tvCountOfPhone);// 接口返回号码条数
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnOk = (Button) view.findViewById(R.id.btnOk);
		
		llDefault.setOnClickListener(this);
		rlVoiceModel.setOnClickListener(this);
		rlMsgModel.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnOk.setOnClickListener(this);

		if (from_event == 1) {// 一键发短信
			tvTitle.setText("给昨日未取件手机号发短信");
			showMsgModel();
		} else if (from_event == 0) {// 一键云呼
			tvTitle.setText("一键云呼昨日未取件手机号");
			showVoiceModel();
		} else if(from_event == 2){
			tvTitle.setText("给呼叫失败发短信");
			showMsgModel();
		} else if(from_event == 3){
			tvTitle.setText("给呼叫失败重新发起呼叫");
			showVoiceModel();
		}
		
	}
	/**设置发送消息**/
	public SkuaidiAlertDialogSendMsg setSendInfo(String sendInfo){
		tvCountOfPhone.setText(sendInfo);
		return this;
	}
	
	/**设置是否可以点击空白区域关闭dialog**/
	public SkuaidiAlertDialogSendMsg setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}
	
	public void show(){
		dialog.show();
	}
	public void dismiss(){
		dialog.dismiss();
	}
	
	/**短信模板**/
	private void showMsgModel() {
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
		modelContent = model.getModelContent();// 获取数据库中被选中的模板内容
		modelID = model.getTid();// 获取数据库中被选中的模板ID
		modelStatus = model.getState();// 获取数据库中被选中模板状态

		if (!Utility.isEmpty(modelContent) && 0 != modelContent.length()) {
			llDefault.setVisibility(View.GONE);
			rlMsgModel.setVisibility(View.VISIBLE);
			if (modelContent.contains("#DH#")) {
				modelContent = modelContent.replaceAll("#DH#", orderDH);
				if (modelContent.length() >= 129)
					modelContent = modelContent.substring(0, 129);
			} else {
				if (modelContent.length() >= 129)
					modelContent = modelContent.substring(0, 129);
			}
			if (modelContent.contains("#SURL#")) {
				modelContent = modelContent.replaceAll("#SURL#", model_url);
			}
		} else {
			llDefault.setVisibility(View.VISIBLE);
			rlMsgModel.setVisibility(View.GONE);
			modelContent = "";
		}

		TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(mContext);
		if (!Utility.isEmpty(modelContent) && 0 != modelContent.length()) {
			tvMsgModelDesc.setText(mTextInsertImgParser.replace(modelContent));
		} else {
			tvMsgModelDesc.setText("");
		}
	}
	/**显示语音模板**/
	private void showVoiceModel() {
		// 获取所有的模板并找出已经被选中过的
		cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
		// 判断模板列表是否为空
		if(null == cloudRecords || 0 == cloudRecords.size())
			return ;
		CloudRecord cRecord = null;
		// 循环遍历找出已经被选择过的那一条录音模板
		
		for (int i = 0; i < cloudRecords.size(); i++) {
			if(cloudRecords.get(i).isChoose() == true){
				cRecord = cloudRecords.get(i);
				break;
			}
		}
		// 判断是否存在已经被选择的模板
		if(null != cRecord){
			if(!Utility.isEmpty(cRecord.getTitle()) && !Utility.isEmpty(cRecord.getTime())){
				llDefault.setVisibility(View.GONE);
				rlVoiceModel.setVisibility(View.VISIBLE);
				modelVoiceID = cRecord.getIvid();
				modelVoiceStatus = cRecord.getExamineStatus();
				tvModelTitle.setText(cRecord.getTitle());
				String time_str = Utility.formatTime(cRecord.getVoiceLength());
				tvVoiceTime.setText(time_str);
			}else{
				llDefault.setVisibility(View.VISIBLE);
				rlVoiceModel.setVisibility(View.GONE);
			}
		}
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llDefault:
			btnOnClickListener.chooseModel();
			dialog.dismiss();
			break;
		case R.id.rlVoiceModel:
			btnOnClickListener.chooseModel();
			dialog.dismiss();
			break;
		case R.id.rlMsgModel:
			btnOnClickListener.chooseModel();
			dialog.dismiss();
			break;
		case R.id.btnCancel:
			dialog.dismiss();
			break;
		case R.id.btnOk:
			if(from_event == 0 || from_event == 3){// 云呼
				btnOnClickListener.sendMsg(modelVoiceID, modelVoiceStatus);
			}else if(from_event == 1 || from_event == 2){// 短信
				btnOnClickListener.sendMsg(modelID, modelStatus);
			}
			break;
		default:
			break;
		}
	}
	
	public interface BtnOnClickListener{
		/**
		 * 选择模板
		 * @param fromEvent 功能ID （0 ：一键云呼功能   1： 发短信功能） 
		 */
		void chooseModel();
//		/**选择语音模板**/
//		public void chooseVoiceModel();
//		/**选择短信模板**/
//		public void chooseMsgModel();
		/**
		 * 发送按钮
		 * @param modelID 模板ID
		 * @param modelStatus 模板状态
		 */
		void sendMsg(String modelID, String modelStatus);
	}

}
