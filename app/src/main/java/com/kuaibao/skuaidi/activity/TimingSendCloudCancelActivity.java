package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.SelectTimePop;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.DraftBoxCloudVoiceInfo;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: TimingSendCloudCancelActivity <br/>
 * 时间: 2016-1-7 上午10:08:45 <br/>
 * 
 * 作者： 顾冬冬 版本：v4.5.0
 */
public class TimingSendCloudCancelActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private static final int SELECT_NEW_MODEL = 0X1001;
	public final static int PLAYING_VOICE_ING = 0x1002;// 正在播放录音

	private Context mContext = null;
	private Intent mIntent = null;

	private DraftBoxCloudVoiceInfo draftBoxSmsInfo = null;
	private SelectTimePop pop = null;// 选择定时发送时间
	private SkuaidiDialog dialog = null;
	private SkuaidiDB skuaidiDB = null;
	private MediaPlayer mPlayer = null;
	private MyRunnable myRunnable = null;
	private Thread mThread = null;

	private SkuaidiImageView back = null;// 返回
	private TextView title = null;// title
	private SkuaidiTextView more = null;
	private TextView tvContacts = null;// 显示发送手机号码
	private TextView tvModelTitle = null;// 模板标题
	private TextView tvSendTime = null;// 定时发送时间
	private TextView tv_rec_total_time = null;// 录音总时长
	private TextView tv_rec_time = null;// 播放进度时间
	private Button btnDeleteTiming = null;// 删除并取消定时发送按钮
	private ViewGroup selectTiming = null;// 选择时间按钮
	private ViewGroup selectModel = null;// 选择模板
	private ViewGroup ll_play_icon = null;// 播放录音按钮
	private ProgressBar voice_record_progressbar = null;// 播放进度条
	private ImageView iv_play_icon = null;// 播放图标

	private List<CloudRecord> cloudRecords = new ArrayList<CloudRecord>();

	private String timeId = "";// 条目ID
	private String sendTiming = "";// 定时发送时间
	private String modelId = "";// 模板ID
	private String sendPhone = "";// 手机号码

	// 选中的模板信息
	private String voice_path_local = "";// 选中模板录音本地路径
	private String voice_path_service = "";// 选中模板录音对应服务器下载的路径
	private String voice_name = "";// 选中模板的录音名称
	private int voice_length = 0;// 选中模板的语音时长
	private String voice_title = "";// 选中模板的标题

	private long timeStamp = 0;// 选择好的时间戳变量
	private boolean isPlaying = false;// 是否正在播放-false：未播放

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PLAYING_VOICE_ING:
				int curProgress = msg.arg1;
				int maxLength = msg.arg2;
				voice_record_progressbar.setMax(maxLength);
				if (curProgress == 0 || curProgress > maxLength) {
					tv_rec_time.setText(Utility.formatTime(0));
					voice_record_progressbar.setProgress(0);
					iv_play_icon.setBackgroundResource(R.drawable.cloud_play_stop);
				} else {
					tv_rec_time.setText(Utility.formatTime(curProgress));
					voice_record_progressbar.setProgress(curProgress);
					iv_play_icon.setBackgroundResource(R.drawable.cloud_play_start);
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
		setContentView(R.layout.timing_send_cloud_cancel_activity);
		mContext = this;

		initView();
		getData();
		showModel();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_NEW_MODEL) {
			// 获取所有的模板并找出已经被选中过的
			cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
			// 判断模板列表是否为空
			if (null == cloudRecords || 0 == cloudRecords.size())
				return;
			CloudRecord cRecord = null;
			// 循环遍历找出已经被选择过的那一条录音模板

			for (int i = 0; i < cloudRecords.size(); i++) {
				if (cloudRecords.get(i).isChoose()) {
					cRecord = cloudRecords.get(i);
					break;
				}
			}
			// 判断是否存在已经被选择的模板
			if (null != cRecord) {
				// 获取模板参数
				modelId = cRecord.getIvid();
				voice_length = cRecord.getVoiceLength();
				voice_path_local = cRecord.getPathLocal();
				voice_path_service = cRecord.getPathService();
				voice_title = cRecord.getTitle();
				voice_name = cRecord.getFileName();
				// 设置模板参数显示
				if (!Utility.isEmpty(voice_title)) {
					tvModelTitle.setText(voice_title);
				} else {
					tvModelTitle.setText("");
				}
				String time_str = Utility.formatTime(voice_length);
				tv_rec_total_time.setText(time_str);
			}
			more.setVisibility(View.VISIBLE);

		}
	}

	/**
	 * initView:初始化控件
	 * 
	 * 作者： 顾冬冬
	 */
	private void initView() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);
		more = (SkuaidiTextView) findViewById(R.id.tv_more);
		tvContacts = (TextView) findViewById(R.id.tvContacts);
		tvModelTitle = (TextView) findViewById(R.id.tvModelTitle);
		tvSendTime = (TextView) findViewById(R.id.tvSendTime);
		tv_rec_total_time = (TextView) findViewById(R.id.tv_rec_total_time);
		btnDeleteTiming = (Button) findViewById(R.id.btnDeleteTiming);
		tv_rec_time = (TextView) findViewById(R.id.tv_rec_time);
		selectTiming = (ViewGroup) findViewById(R.id.selectTiming);
		selectModel = (ViewGroup) findViewById(R.id.selectModel);
		ll_play_icon = (ViewGroup) findViewById(R.id.ll_play_icon);
		voice_record_progressbar = (ProgressBar) findViewById(R.id.voice_record_progressbar);
		iv_play_icon = (ImageView) findViewById(R.id.iv_play_icon);

		more.setText("保存");
		more.setVisibility(View.GONE);

		back.setOnClickListener(this);
		more.setOnClickListener(this);
		btnDeleteTiming.setOnClickListener(this);
		selectTiming.setOnClickListener(this);
		selectModel.setOnClickListener(this);
		ll_play_icon.setOnClickListener(this);
	}

	/**
	 * getData:获取数据
	 * 
	 * 作者： 顾冬冬
	 */
	private void getData() {
		pop = new SelectTimePop(mContext, this);
//		toast = new ToastCustom(mContext, 5, title);
		skuaidiDB = SkuaidiDB.getInstanse(mContext);

		draftBoxSmsInfo = (DraftBoxCloudVoiceInfo) getIntent().getSerializableExtra("draftBoxRecord");

		timeId = draftBoxSmsInfo.getId();
		sendTiming = DateHelper.getTimeStamp(DateHelper.getTimeStamp(draftBoxSmsInfo.getSendTime(), DateHelper.YYYY_MM_DD_HH_MM_SS));
		sendPhone = draftBoxSmsInfo.getPhoneNumber();
		modelId = draftBoxSmsInfo.getModelId();

		title.setText(sendPhone);
		tvContacts.setText(sendPhone);
		timeStamp = DateHelper.getTimeStamp(draftBoxSmsInfo.getSendTime(), DateHelper.YYYY_MM_DD_HH_MM_SS);
		tvSendTime.setText(sendTiming.substring(5, 17));

	}

	private void showModel() {
		// 获取所有的模板并找出已经被选中过的
		cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
		if (null == cloudRecords || 0 == cloudRecords.size())
			return;

		CloudRecord cRecord = null;
		for (int i = 0; i < cloudRecords.size(); i++) {
			String mId = cloudRecords.get(i).getIvid();
			if (modelId.equals(mId)) {
				cRecord = cloudRecords.get(i);
				break;
			} else {
				cRecord = null;
			}
		}

		if (null != cRecord) {
			voice_length = cRecord.getVoiceLength();
			voice_path_local = cRecord.getPathLocal();
			voice_path_service = cRecord.getPathService();
			voice_title = cRecord.getTitle();
			voice_name = cRecord.getFileName();
			// 设置模板参数显示
			if (!Utility.isEmpty(voice_title)) {
				tvModelTitle.setText(voice_title);
			} else {
				tvModelTitle.setText("");
			}
			String time_str = Utility.formatTime(voice_length);
			tv_rec_total_time.setText(time_str);
		}
	}

	/** 确认使用定时发送功能 **/
	private void timingTransmission() {
		timeStamp = pop.getTimeStamp();
		tvSendTime.setText(pop.getSendTimeStr());
		pop.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:// 返回
			stopPlayRecord();
			finish();
			break;
		case R.id.tv_more:// 保存修改
			updateTiming(timeId, modelId, timeStamp);
			break;
		case R.id.selectModel:// 选择模板
			mIntent = new Intent(mContext, AddVoiceModelActivity.class);
			mIntent.putExtra("fromActivityType", "selectTimingModel");
			startActivityForResult(mIntent, SELECT_NEW_MODEL);
			stopPlayRecord();
			break;
		case R.id.selectTiming:
			pop.showPopupWindow(selectTiming);
			stopPlayRecord();
			break;
		case R.id.ll_ok:// 选择好时间
			if (pop.isMoreThanTheCurrent10Minutes()) {
				timingTransmission();
				more.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnDeleteTiming:// 删除定时发送
			stopPlayRecord();
			dialog = new SkuaidiDialog(mContext);
			dialog.setTitle("删除提示");
			dialog.setContent("删除后将取消定时发送，确定要删除该条草稿？");
			dialog.isUseEditText(false);
			dialog.setPositionButtonTitle("确定");
			dialog.setNegativeButtonTitle("取消");
			dialog.setPosionClickListener(new PositonButtonOnclickListener() {
				@Override
				public void onClick(View v) {
					deleteTiming(timeId);
				}
			});
			dialog.showDialog();
			break;
		case R.id.ll_play_icon:// 播放录音按钮
			if (isPlaying == true) {
				stopPlayRecord();

			} else {
				if (Utility.isEmpty(voice_path_local)) {
					UtilToolkit.showToast("模板有误，请重新选择");
					return;
				}
				File file = new File(voice_path_local);
				if (file.exists()) {// 如果存在文件
					myRunnable = new MyRunnable(voice_length);
					mThread = new Thread(myRunnable);
					mThread.start();
					startPlayRecord(voice_path_local);
				} else {
					if (Utility.getSDIsExist() == false) {
						UtilToolkit.showToast("SD卡不存在");
						return;
					}
					File voiceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord");
					if (!voiceDirectory.exists()) {
						voiceDirectory.mkdirs();
					}
					FinalHttp fh = new FinalHttp();
					fh.download(voice_path_service, Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/" + voice_name
							+ ".wav", new AjaxCallBack<File>() {
						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							//System.out.println("count:" + count + "  current :" + current);
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							myRunnable = new MyRunnable(voice_length);
							mThread = new Thread(myRunnable);
							mThread.start();
							startPlayRecord(voice_path_local);
							//System.out.println("gudd 下载成功");
						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							UtilToolkit.showToast("录音下载失败或已不存在该录音，请删除");
							//System.out.println("gudd 下载失败  " + strMsg);
						}
					});
				}

			}
			break;
		}

	}

	/**
	 * @Description:开始播放录音
	 * @Title: startPlayRecord
	 * @param @param path 设定文件
	 * @return void 返回类型
	 * @author 顾冬冬
	 */
	private void startPlayRecord(String path) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					isPlaying = false;
					mPlayer = null;
				}
			});
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			mPlayer.start();
			isPlaying = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description:停止播放录音
	 * @Title: stopPlayRecord
	 * @return void 返回类型
	 * @author 顾冬冬
	 */
	private void stopPlayRecord() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			isPlaying = false;
		}
		if (null != myRunnable) {
			myRunnable.setRunnableStop();
		}
		if (null != mThread) {
			mThread.interrupt();
			mThread = null;
			isPlaying = false;
		}
	}

	class MyRunnable implements Runnable {
		int m_CurProgress = 0;// 当前进度
		int m_maxLength = 0;

		public MyRunnable(int maxLength) {
			super();
			this.m_maxLength = maxLength;
		}

		public void setRunnableStop() {
			m_CurProgress = m_maxLength + 2;
		}

		@Override
		public void run() {
			while (m_CurProgress <= m_maxLength) {
				Message msg = mHandler.obtainMessage();
				msg.what = PLAYING_VOICE_ING;
				m_CurProgress = m_CurProgress + 1;
				msg.arg1 = m_CurProgress;
				msg.arg2 = m_maxLength;
				msg.sendToTarget();
				try {
					Thread.sleep(1000);// 每隔一秒执行一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (m_CurProgress > m_maxLength) {
				Message msg = mHandler.obtainMessage();
				msg.what = PLAYING_VOICE_ING;
				m_CurProgress = m_CurProgress + 1;
				msg.arg1 = m_CurProgress;
				msg.arg2 = m_maxLength;
				msg.sendToTarget();
				try {
					Thread.sleep(1000);// 每隔一秒执行一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * deleteTiming:删除定时发送【接口】 作者： 顾冬冬
	 * 
	 * @param id
	 */
	private void deleteTiming(String id) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "ivr/delete_timing");
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * updateTiming:更新云呼定时发送时间或模板【接口】
	 * 
	 * 作者： 顾冬冬
	 * 
	 * @param id
	 * @param ivid
	 * @param send_time
	 */
	private void updateTiming(String id, String ivid, long send_time) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "ivr/update_timing");
			data.put("id", id);
			data.put("ivid", ivid);
			data.put("send_time", send_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		if ("ivr/delete_timing".equals(sname)) {
			UtilToolkit.showToast_Custom(msg);
			finish();
		} else if ("ivr/update_timing".equals(sname)) {
			UtilToolkit.showToast_Custom(msg);
			more.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast_Custom(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
