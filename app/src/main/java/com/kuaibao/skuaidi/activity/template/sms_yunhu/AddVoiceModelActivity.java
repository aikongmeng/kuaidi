package com.kuaibao.skuaidi.activity.template.sms_yunhu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.adapter.AddVoiceModelAdapter;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendCloudCallBachSignActivity;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.smsrecord.RecordSendSmsSelectActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.AddVoiceModelMenuDialog;
import com.kuaibao.skuaidi.dialog.AddVoiceModelMenuDialog.onBtnClickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiSpecialDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiSpecialDialog.Body1PicClickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiSpecialDialog.ButtonCancleClickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiSpecialDialog.ButtonOkClickListener;
import com.kuaibao.skuaidi.dialog.menu.ActionSheetDialog;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.recorder.CloudCallRecordUtility;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 顾冬冬
 * 添加语音模板界面
 */
public class AddVoiceModelActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private final String BUTTON_LEFT = "button_left";
	private final String BUTTON_MIDDLE = "button_middle";
	private final String BUTTON_RIGHT = "button_right";

	private final static int GET_VOCIE_LIST_SUCCESS = 0x1001;
	private final static int DELETE_VOICE_SUCCESS = 0x1002;
	private final static int UPDATE_VOICE_SUCCESS = 0x1003;
	private final static int UPDATE_PROGRESS = 0x1004;
	private final static int TIMER_TASK = 0x1005;
	public final static int ADD_VOICE_SUCCESS = 0X1006;
	private final static int GET_ISADDMODEL_STATUS_SUCCESS = 0X1007;
	private final static int REQUEST_UPLOAD_VOICE_MODEL = 0X1008;// 上传语音模板界面请求

	private Context mContext = null;
	private Intent mIntent = null;
	private static SkuaidiDB skuaidiDB = null;
	private MediaPlayer mPlayer = null;
	private Timer timer = null;
	private MyTask myTask = null;
	private SkuaidiSpecialDialog dialog = null;

	private AddVoiceModelMenuDialog menuDialog = null;

	private CloudCallRecordUtility clouRecordUtility = null;
	private static List<CloudRecord> cloudRecords = new ArrayList<>();
	private List<SmsRecord> smsRecords ;
	private List<CloudVoiceRecordEntry> cvres;
	private AddVoiceModelAdapter  adapter = null;
	private Thread thread = null;
	private updateRunnable runnable = null;

	private SkuaidiTextView tv_more;// 更多按钮
	private TextView button_left, button_middle, button_right;
	private ListView lv_model;// 模板列表
	private LinearLayout ll_state;// title[模板状态按钮]
	private TextView tv_title_des;// title[文字]

	private ImageView iv_voice_anim_left;// 录音菜单左边动画
	private ImageView iv_voice_anim_right;// 录音菜单右边动画
	private TextView record_time;// 录音菜单录音时间
	private TextView tv_ok;// 完成按钮
	private TextView record_tag;// 状态说明
	private TextView tvRestartRecord = null; // 重录提示文字
	private TextView tvTryPlay = null;// 试听文字
	private ImageView ivRestartRecord = null;// 重录按钮
	private ImageView ivRecording = null;// 正在录音按钮
	private ImageView ivTryPlay = null;// 试听按钮
	private RelativeLayout rlRecord = null;// 重新录音按钮区域
	private RelativeLayout rlRestartRecord = null;// 录音按钮区域
	private RelativeLayout rlTryPlay = null;// 试听按钮区域

	private boolean isPlaying = false;
	private String fromActivityType = "";
	private int selectState = 1;// 选择模板状态【0：待审核|1:已审核|2：已拒绝】

	private int rec_status = 1;// 录音状态 （1：初始值 |2：正在录音 | 3：录音结束 | 4：正在播放）
	private int rec_time_count = 0;// 录音时长
	private int rec_time = 50;
	private String file_path = "";// 录音路径
	private String t_ivid = "";// 模板对应服务器上的ID
	private String t_title = "";// 模板title

	private boolean isRecordAudio = true;// 操作是否是录音功能
    int clickPosition = 0;// 记录每次点击的列表ID

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_ISADDMODEL_STATUS_SUCCESS:
                if (isRecordAudio) {
                    menuDialog.show();
                    startRecord();// 开始录音
                }else{
                    mIntent = new Intent(mContext, UploadTemplateCloudCallActivity.class);
                    startActivityForResult(mIntent,REQUEST_UPLOAD_VOICE_MODEL);
                }
				break;
			case ADD_VOICE_SUCCESS:
				getModels();
				break;
			case GET_VOCIE_LIST_SUCCESS:// 获取列表成功
				switch (selectState){
					case 0:
						cloudRecords = skuaidiDB.getCloudRecordModels("0");// 从数据库中获取待审核的模板
						break;
					case 1:
						cloudRecords = getDragVoiceModels();// 从数据库中获取已审核的模板
						break;
					case 2:
						cloudRecords = skuaidiDB.getCloudRecordModels("2");// 从数据库中获取被拒绝的模板
						break;
					default:
						break;
				}
				adapter.setCustomAdapter(cloudRecords);
				break;
			case DELETE_VOICE_SUCCESS:// 删除指定模板成功
				skuaidiDB.deleteCloudByivid(t_ivid);
				for (int i = 0; i < adapter.getAdapterList().size(); i++) {
					if (adapter.getAdapterList().get(i).getIvid().equals(t_ivid)) {
						adapter.getAdapterList().remove(i);
					}
				}
				adapter.notifyDataSetChanged();
				t_ivid = "";
				break;
			case UPDATE_VOICE_SUCCESS:// 更新模板成功
				if (!msg.obj.toString().contains("失败")) {
					skuaidiDB.updateCloudByivid(t_title, t_ivid);
//					List<CloudRecord> cRecords2 = skuaidiDB.getCloudRecordModels();
					List<CloudRecord> cRecords2 = getDragVoiceModels();
					adapter.setCustomAdapter(cRecords2);
					adapter.notifyDataSetChanged();
				}
				break;
			case UPDATE_PROGRESS:// 更新progress
				updateProgress(msg.arg1, msg.arg2);
				break;
			case TIMER_TASK:
				if (rec_status == 2) {
					if (rec_time > 0) {
						rec_time = rec_time - 1;
						rec_time_count = 50 - rec_time;// 记录录音时长
					} else {
						rec_time = 0;
						rec_status = 3;
						record_tag.setText("录音结束");
						if (timer != null) {
							timer.cancel();
							timer.purge();
							timer = null;
						}
						if (myTask != null) {
							myTask.cancel();
							myTask = null;
						}
						clouRecordUtility.stopRecord();// 停止录音
						setRecordStopImgBg();
					}
				} else if (rec_status == 4) {
					rec_time = rec_time - 1;
					if (rec_time <= 0) {
						if (timer != null) {
							timer.cancel();
							timer.purge();
							timer = null;
						}
						if (myTask != null) {
							myTask.cancel();
							myTask = null;
						}
						rec_status = 3;
						record_tag.setText("录音结束");
						rec_time = 0;
					}
				}
				record_time.setText(Utility.formatTime(rec_time));

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.add_voice_model_activity);
		mContext = this;

		menuDialog = new AddVoiceModelMenuDialog(mContext);
		menuDialog.builder().setCanceledOnTouchOutside(false).setOnBtnClickListener(new onBtnClickListener() {

			@Override
			public void recordCancel() {
				stopRecord();// 停止录音
			}

			@Override
			public void recordOk() {
				UMShareManager.onEvent(mContext, "CloudModel_record_voice_ok", "SMS", "云呼模板:完成录音");
				rec_status = 1;
				if (timer != null) {
					timer.cancel();
					timer.purge();
					timer = null;
				}
				if (myTask != null) {
					myTask.cancel();
					myTask = null;
				}
				clouRecordUtility.stopRecord();// 停止录音
				if (rec_time_count < 5) {// 如果录音时间小于1秒
					clouRecordUtility.deleteWavFile();
					clouRecordUtility.deleteRawFile();
					UtilToolkit.showToast("录音时间小于5秒");
					return;
				}
				final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
				dialog.setTitle("保存语音模板");
				dialog.isUseEditText(true);
				dialog.setPositionButtonTitle("确定");
				dialog.setNegativeButtonTitle("取消");
				dialog.setEditTextHint("请输入模板名称");
				dialog.setDonotAutoDismiss(true);
				dialog.setCancelable(false);
				dialog.setPosionClickListener(new PositonButtonOnclickListener() {

					@Override
					public void onClick(View v) {
						if (Utility.isEmpty(dialog.getEditTextContent())) {
							UtilToolkit.showToast("请输入模板名称");
						} else if (!Utility.isNetworkConnected()) {
							UtilToolkit.showToast("请设置网络");
						} else {
							UMShareManager.onEvent(mContext, "CloudModel_record_voice_send", "SMS", "云呼模板:提交录音");
							showProgressDialog("请稍候");
							try {
								addModel(dialog.getEditTextContent(), rec_time_count, clouRecordUtility.readStream(file_path));
							} catch (Exception e) {
								e.printStackTrace();
							}
							dialog.dismiss();
						}
					}
				});
				dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

					@Override
					public void onClick() {
						dialog.dismiss();
					}
				});
				dialog.showDialog();
			}

			@Override
			public void restartRecord() {
				startRecord();// 开始录音
			}

			@Override
			public void recording() {// 正在录音
				UMShareManager.onEvent(mContext, "CloudModel_record_voice_stop", "SMS", "云呼模板:结束录音");
				setRecordStopImgBg();
				rec_status = 3;
				record_tag.setText("录音结束");
				if (timer != null) {
					timer.cancel();
					timer.purge();
					timer = null;
				}
				if (myTask != null) {
					myTask.cancel();
					myTask = null;
				}
				clouRecordUtility.stopRecord();// 停止录音
				if (rec_time_count < 5) {// 如果录音时间小于5秒
					rec_status = 1;
					clouRecordUtility.deleteWavFile();
					clouRecordUtility.deleteRawFile();
					UtilToolkit.showToast("录音时间小于5秒");
				}
			}

			@Override
			public void tryPlay() {
				if (rec_status == 3) {
					UMShareManager.onEvent(mContext, "CloudModel_record_voice_play", "SMS", "云呼模板:试听录音");
					setTryPlayImgBg();
					rec_status = 4;
					rec_time = rec_time_count;
					record_tag.setText("试听中");
					startPlayRecord(clouRecordUtility.NewAudioName);
					timer = new Timer();
					myTask = new MyTask();
					timer.schedule(myTask, 0, 1000);
				} else if (rec_status == 4) {
					UMShareManager.onEvent(mContext, "CloudModel_record_voice_stop_play", "SMS", "云呼模板:试听结束");
					setRecordStopImgBg();
					rec_status = 3;
					record_tag.setText("录音结束");
					stopPlayRecord();
				}
			}

			@Override
			public void dialogOnkeyListener(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == (KeyEvent.KEYCODE_BACK)) {
					dialog.dismiss();
					stopRecord();
				}
			}
		});
		findView();
		clouRecordUtility = new CloudCallRecordUtility(mContext, handler, iv_voice_anim_left, iv_voice_anim_right);
		skuaidiDB = SkuaidiDB.getInstanse(mContext);

		showModel();
		getData();
		setViewListener();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 901 && resultCode == 902) {// 模板排序
			selectState = 1;
			tv_more.setVisibility(View.VISIBLE);
			changeButtonBG(button_left, BUTTON_LEFT);
			cloudRecords.clear();
			cloudRecords = (List<CloudRecord>) data.getSerializableExtra("models");
			adapter.setCustomAdapter(cloudRecords);
		}else if(requestCode == REQUEST_UPLOAD_VOICE_MODEL){
			getModels();
		}
	}

	/** *获取数据 */
	private void getData() {
		fromActivityType = getIntent().getStringExtra("fromActivityType");
		tv_more.setVisibility(View.VISIBLE);
		tv_more.setText("排序");
		if (!Utility.isEmpty(fromActivityType)&&(fromActivityType.equals("resend_smsORcloud")||"cloudCall_resend_smsORcloud".equals(fromActivityType))){
			ll_state.setVisibility(View.GONE);
			tv_title_des.setVisibility(View.VISIBLE);
			tv_title_des.setText("重发云呼");
			tv_more.setText("发送");
			if ("resend_smsORcloud".equals(fromActivityType))
				smsRecords = (List<SmsRecord>) getIntent().getSerializableExtra("smsRecords");
			else
				cvres = (List<CloudVoiceRecordEntry>) getIntent().getSerializableExtra("smsRecords");
		}
	}

	/** 查找控件 */
	private void findView() {
		SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.back);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		button_left = (TextView) findViewById(R.id.button_left);
		button_middle = (TextView) findViewById(R.id.button_middle);
		button_right = (TextView) findViewById(R.id.button_right);
		lv_model = (ListView) findViewById(R.id.lv_model);
		ll_state = (LinearLayout) findViewById(R.id.ll_state);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);

		LinearLayout btn_add_model = (LinearLayout) findViewById(R.id.btn_add_model);// 录音按钮
		AddVoiceModelActivity.this.iv_voice_anim_left = (ImageView) menuDialog.getIvVoiceAnimLeft();// 录音菜单左边动画
		AddVoiceModelActivity.this.iv_voice_anim_right = (ImageView) menuDialog.getIvVoiceAnimRight();// 录音菜单右边动画
		AddVoiceModelActivity.this.record_time = (TextView) menuDialog.getRecordTime();// 录音菜单录音时间
		AddVoiceModelActivity.this.tv_ok = (TextView) menuDialog.getTvOk();// 完成按钮
		AddVoiceModelActivity.this.record_tag = (TextView) menuDialog.getRecord_tag();// 录音状态说明
		AddVoiceModelActivity.this.tvRestartRecord = (TextView) menuDialog.getTvRestartRecord(); // 重录提示文字
		AddVoiceModelActivity.this.tvTryPlay = (TextView) menuDialog.getTvTryPlay();// 试听文字
		AddVoiceModelActivity.this.ivRestartRecord = (ImageView) menuDialog.getIvRestartRecord();// 重录按钮
		AddVoiceModelActivity.this.ivRecording = (ImageView) menuDialog.getIvRecording();// 正在录音按钮
		AddVoiceModelActivity.this.ivTryPlay = (ImageView) menuDialog.getIvTryPlay();// 试听按钮
		AddVoiceModelActivity.this.rlRecord = (RelativeLayout) menuDialog.getRlRecord();// 重新录音按钮区域
		AddVoiceModelActivity.this.rlRestartRecord = (RelativeLayout) menuDialog.getRlRestartRecord();// 录音按钮区域
		AddVoiceModelActivity.this.rlTryPlay = (RelativeLayout) menuDialog.getRlTryPlay();// 试听按钮区域

		iv_title_back.setOnClickListener(this);
		button_left.setOnClickListener(this);
		button_middle.setOnClickListener(this);
		button_right.setOnClickListener(this);
		tv_more.setOnClickListener(this);
		btn_add_model.setOnClickListener(this);

		button_left.setText("已审核");
		button_middle.setText("待审核");
		button_right.setText("未通过");

		changeButtonBG(button_left, BUTTON_LEFT);
	}

    /** 初始化模板 */
    private void showModel() {
        cloudRecords = getDragVoiceModels();
        adapter = new AddVoiceModelAdapter(mContext, handler, cloudRecords, new AddVoiceModelAdapter.ButtonOnclick() {

            @Override
            public void onPlayMusic(View v, final int position, final String localPath, String servicePath, final int voiceLength) {
                UMShareManager.onEvent(mContext, "CloudModel_item_play", "SMS", "云呼模板:条目播放");
                if (isPlaying) {
                    if (clickPosition != position) {
                        clickPosition = position;
                        stopPlayRecord();
                        if (null != runnable) {
                            runnable.setThreadStop();
                        }
                        if (null != thread) {
                            thread.interrupt();
                            thread = null;
                        }
                        startPlayRecord(localPath);
                        isPlaying = true;
                        if (thread == null) {
                            runnable = new updateRunnable(position, voiceLength);
                            thread = new Thread(runnable);
                            thread.start();
                        }
                    } else {
                        clickPosition = position;
                        stopPlayRecord();
                        isPlaying = false;
                        if (null != runnable) {
                            runnable.setThreadStop();
                        }
                        if (null != thread) {
                            thread.interrupt();
                            thread = null;
                        }
                    }
                    clouRecordUtility.stopPlaying();
                } else {
                    clickPosition = position;

                    if (Utility.isEmpty(localPath)) {
                        UtilToolkit.showToast("播放错误，请重新刷新列表");
                        return;
                    }

                    File file = new File(localPath);
                    if (file.exists()) {
                        isPlaying = true;
                        runnable = new updateRunnable(position, voiceLength);
                        thread = new Thread(runnable);
                        thread.start();
                        startPlayRecord(localPath);
                    } else {
                        if (!Utility.getSDIsExist()) {
                            UtilToolkit.showToast("SD卡不存在");
                            return;
                        }
                        File voiceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord");
                        if (!voiceDirectory.exists()) {
                            voiceDirectory.mkdirs();
                        }
                        FinalHttp fh = new FinalHttp();
                        fh.download(servicePath, localPath, new AjaxCallBack<File>() {
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                //System.out.println("count:" + count + "  current :" + current);
                            }

                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                startPlayRecord(localPath);
                                isPlaying = true;
                                runnable = new updateRunnable(position, voiceLength);
                                thread = new Thread(runnable);
                                thread.start();
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
            }

            @Override
            public void onModify(View v, int position, final String ivid, String title) {// 修改模板
                UMShareManager.onEvent(mContext, "CloudModel_item_modify", "SMS", "云呼模板:编辑名称");
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("请设置网络");
                } else {
                    final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
                    dialog.setTitle("编辑模板名称");
                    dialog.isUseEditText(true);
                    dialog.setDonotAutoDismiss(false);
                    dialog.setEditTextHint("请输入模板名称");
                    dialog.setEditText(title);
                    dialog.setPositionButtonTitle("确认");
                    dialog.setNegativeButtonTitle("取消");
                    dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                        @Override
                        public void onClick(View v) {
                            t_ivid = ivid;
                            t_title = dialog.getEditTextContent();
                            if (Utility.isEmpty(t_title)) {
                                UtilToolkit.showToast("请输入模板名称");
                                return;
                            }
                            updateModel(t_title, t_ivid);
                            dialog.dismiss();
                        }
                    });
                    dialog.showDialog();
                }
            }

            @Override
            public void onDelete(View v, final int position, final String ivid) {// 删除模板
                UMShareManager.onEvent(mContext, "CloudModel_item_delete", "SMS", "云呼模板:删除模板");
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("请设置网络");
                } else {
                    SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(mContext);
                    dialog.setTitleGray("提示");
                    dialog.setContentGray("您是否确认删除此条模板？");
                    dialog.setPositionButtonTextGray("确认");
                    dialog.setNegativeButtonTextGray("取消");
                    dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
                        @Override
                        public void onClick(View v) {

                            stopPlayRecord();
                            isPlaying = false;
                            if (null != runnable) {
                                runnable.setThreadStop(position);
                                runnable = null;
                            }
                            if (null != thread) {
                                thread.interrupt();
                                thread = null;
                            }
                            t_ivid = ivid;
                            deleteModel(ivid);
                        }
                    });
                    dialog.showDialogGray(v);
                }
            }
        });
        lv_model.setAdapter(adapter);
        getModels();
    }

	/** 获取排序好了的数据 **/
	public static List<CloudRecord> getDragVoiceModels() {
		cloudRecords.clear();
		List<CloudRecord> cacheModels  = skuaidiDB.getCloudRecordModels("1");
		String sort_no = "";
		String sort_no_null = "";
		for (int i = 0; i < cacheModels.size(); i++) {
			if (!Utility.isEmpty(cacheModels.get(i).getSort_no())) {
				if (!Utility.isEmpty(sort_no)) {
					sort_no = sort_no + "," + cacheModels.get(i).getSort_no();
				} else {
					sort_no = cacheModels.get(i).getSort_no();
				}
			} else {
				if (!Utility.isEmpty(sort_no_null)) {
					sort_no_null = sort_no_null + "," + cacheModels.get(i).getIvid();
				} else {
					sort_no_null = cacheModels.get(i).getIvid();
				}
			}
		}
		String[] sort_noArr = null;// 保存有排序编号的对象的排序号
		String[] sort_noTid = null;// 保存没有排序编号的对象中的TID【IVID：模板ID】
		if (!Utility.isEmpty(sort_no)) {
			sort_noArr = sort_no.split(",");
		}
		if (!Utility.isEmpty(sort_no_null)) {
			sort_noTid = sort_no_null.split(",");
		}

		if (sort_noTid != null) {
			for (String tid:sort_noTid){
				cloudRecords.add(skuaidiDB.getCloudRecordModels("ivid", tid, "1"));
			}
		}

		if (sort_noArr != null) {
			int[] sort_noIntArr = new int[sort_noArr.length];// 将排序编号转换成整型重新放到数组中
			for (int i = 0; i < sort_noArr.length; i++) {
				sort_noIntArr[i] = Integer.parseInt(sort_noArr[i]);
			}
			Arrays.sort(sort_noIntArr);// 重新将编号按照升序排序
			for (int intarr:sort_noIntArr){
				cloudRecords.add(skuaidiDB.getCloudRecordModels("sortNo", intarr + "", "1"));// 将重新排好序的对象重新放进列表中
			}
		}
		return cloudRecords;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:// 返回按钮
			setResult(SendYunHuActivity.OUT_CHOOSE_MODEL);
			stopPlayRecord();
			finish();
			break;
		case R.id.btn_add_model:// 新增语音模板按钮
				new ActionSheetDialog(mContext).builder().setCancelable(false).setCanceledOnTouchOutside(false)
						.addSheetItem("录制语音模板", ActionSheetDialog.SheetItemColor.Gray_666666, new ActionSheetDialog.OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						UMShareManager.onEvent(mContext, "CloudModel_record_voice", "SMS", "云呼模板:录音按钮");
						judgeIsAddModel();
						isRecordAudio = true;
					}
				}).addSheetItem("从文件中选择", ActionSheetDialog.SheetItemColor.Gray_666666, new ActionSheetDialog.OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						judgeIsAddModel();
						isRecordAudio = false;

					}
				}).show();
			break;

		case R.id.button_left:
			cloudRecords.clear();
			selectState = 1;
			tv_more.setVisibility(View.VISIBLE);
			changeButtonBG(button_left, BUTTON_LEFT);
			cloudRecords = getDragVoiceModels();
			adapter.setCustomAdapter(cloudRecords);
			stopPlayRecord();
			break;
		case R.id.button_middle:
			cloudRecords.clear();
			selectState = 0;
			tv_more.setVisibility(View.GONE);
			changeButtonBG(button_middle, BUTTON_MIDDLE);
			cloudRecords = skuaidiDB.getCloudRecordModels("0");// 获取待审核通过的模板
			adapter.setCustomAdapter(cloudRecords);
			stopPlayRecord();
			break;
		case R.id.button_right:
			cloudRecords.clear();
			selectState = 2;
			tv_more.setVisibility(View.GONE);
			changeButtonBG(button_right, BUTTON_RIGHT);
			cloudRecords = skuaidiDB.getCloudRecordModels("2");// 获取已拒绝的模板
			adapter.setCustomAdapter(cloudRecords);
			stopPlayRecord();
			break;
		case R.id.tv_more:
			if (!Utility.isEmpty(fromActivityType)&&("resend_smsORcloud".equals(fromActivityType)||"cloudCall_resend_smsORcloud".equals(fromActivityType))){
				String ivid = skuaidiDB.getSelectedCloudModelTid();
				if (!Utility.isEmpty(ivid)){
					resendInform(ivid);
				}else
					UtilToolkit.showToast("请选择一个模板");
				return;
			}

			if (!UtilityTime.isToday(mContext, SkuaidiSpf.getVoiceCloudTemplateSortCurDate(mContext))){
				SkuaidiSpf.saveVoiceCloudTemplateSortCurDate(mContext,UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(),UtilityTime.YYYY_MM_DD));
				UMShareManager.onEvent(mContext, "CloudModel_record_voice_sort_Num", "SMS", "云呼模板:模板排序人数【个人当天只统计1次】");
			}
			mIntent = new Intent(mContext, ModelVoiceDragListActivity.class);
			startActivityForResult(mIntent, 901);
			stopPlayRecord();
			break;
		default:
			break;
		}
	}

	private void changeButtonBG(TextView button_textview, String button_name) {
		if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
			button_left.setBackgroundResource(R.drawable.shape_default_orange_radius_left);
			button_middle.setBackgroundColor(Utility.getColor(mContext,R.color.sto_main_color));
			button_right.setBackgroundResource(R.drawable.shape_default_orange_radius_right);
		}else{
			button_left.setBackgroundResource(R.drawable.shape_default_green_radius_left);
			button_middle.setBackgroundColor(Utility.getColor(mContext,R.color.default_green));
			button_right.setBackgroundResource(R.drawable.shape_default_green_radius_right);
		}
		button_left.setTextColor(Utility.getColor(mContext,R.color.white));
		button_middle.setTextColor(Utility.getColor(mContext,R.color.white));
		button_right.setTextColor(Utility.getColor(mContext,R.color.white));
		if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
			button_textview.setTextColor(Utility.getColor(mContext,R.color.sto_main_color));
		}else{
			button_textview.setTextColor(Utility.getColor(mContext,R.color.default_green));
		}

		switch (button_name){
			case BUTTON_LEFT:
				button_left.setBackgroundResource(R.drawable.shape_radius_btn_left_white2);
				break;
			case BUTTON_MIDDLE:
				button_middle.setBackgroundColor(Utility.getColor(mContext,R.color.white));
				break;
			case BUTTON_RIGHT:
				button_right.setBackgroundResource(R.drawable.shape_radius_btn_right_white2);
				break;
		}
	}

	class MyTask extends TimerTask {

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = TIMER_TASK;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 开始录音
	 */
	private void startRecord() {
		if (!Utility.getSDIsExist()) {
			UtilToolkit.showToast("SD卡不存在，不能录音");
			return;
		}
		UMShareManager.onEvent(mContext, "CloudModel_record_voice_start", "SMS", "云呼模板:开始录音");
		setRecordingImgBg();
		record_tag.setText("录音中");
		rec_status = 2;
		if (!Environment.getExternalStorageDirectory().exists()) {
			UtilToolkit.showToast("SD卡不存在");
			return;
		}
		file_path = clouRecordUtility.NewAudioName;
		clouRecordUtility.startRecord();// 开始录音
		rec_time = 50;
		timer = new Timer();
		myTask = new MyTask();// 每次要执行的代码
		timer.schedule(myTask, 0, 1000);// 从第0秒开始，每隔1秒执行一次
	}

	/**
	 * 结束录音
	 */
	public void stopRecord() {
		UMShareManager.onEvent(mContext, "CloudModel_record_voice_cancel", "SMS", "云呼模板:取消录音");
		rec_status = 1;
		stopPlayRecord();
		try {
			clouRecordUtility.stopRecord();// 停止录音
			clouRecordUtility.deleteWavFile();
			clouRecordUtility.deleteRawFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (myTask != null) {
			myTask.cancel();
			myTask = null;
		}
		rec_time = 0;
	}

	private void setRecordingImgBg() {
		ivRecording.setBackgroundResource(R.drawable.record_voice_recording_green);
		ivRestartRecord.setBackgroundResource(R.drawable.record_voice_restartrecord_gray);
		ivTryPlay.setBackgroundResource(R.drawable.record_voice_tryplay_gray);
		tvRestartRecord.setTextColor(Utility.getColor(mContext,R.color.gray_4));
		tvTryPlay.setTextColor(Utility.getColor(mContext,R.color.gray_4));
		tv_ok.setTextColor(Utility.getColor(mContext,R.color.gray_3));
		tv_ok.setEnabled(false);
		rlRecord.setEnabled(true);
		rlRestartRecord.setEnabled(false);
		rlTryPlay.setEnabled(false);
	}

	private void setRecordStopImgBg() {
		ivRecording.setBackgroundResource(R.drawable.record_voice_recording_gray);
		ivRestartRecord.setBackgroundResource(R.drawable.record_voice_restartrecord_green);
		ivTryPlay.setBackgroundResource(R.drawable.record_voice_tryplay_green);
		tvRestartRecord.setTextColor(Utility.getColor(mContext,R.color.gray_2));
		tvTryPlay.setTextColor(Utility.getColor(mContext,R.color.gray_2));
		tv_ok.setTextColor(Utility.getColor(mContext,R.color.default_green_2));
		tv_ok.setEnabled(true);
		rlRecord.setEnabled(false);
		rlRestartRecord.setEnabled(true);
		rlTryPlay.setEnabled(true);
	}

	private void setTryPlayImgBg() {
		ivRecording.setBackgroundResource(R.drawable.record_voice_recording_gray);
		ivRestartRecord.setBackgroundResource(R.drawable.record_voice_restartrecord_green);
		ivTryPlay.setBackgroundResource(R.drawable.record_voice_tryplaystop_green);
		tvRestartRecord.setTextColor(Utility.getColor(mContext,R.color.gray_2));
		tvTryPlay.setTextColor(Utility.getColor(mContext,R.color.gray_2));
		tv_ok.setTextColor(Utility.getColor(mContext,R.color.default_green_2));
		tv_ok.setEnabled(true);
		rlRecord.setEnabled(false);
		rlRestartRecord.setEnabled(true);
		rlTryPlay.setEnabled(true);
	}

	private void setViewListener() {
		lv_model.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				if (adapter.getItem(position).getExamineStatus().equals("1")) {
					skuaidiDB.clearCloudChooseModelStatus();
					skuaidiDB.setCloudIsChoose(adapter.getItem(position).getIvid());
					adapter.setSelected(position);
					if (!Utility.isEmpty(fromActivityType) && fromActivityType.equals("smsRecordDetailActivity")) {// 从短信记录详情而来
						stopPlayRecord();
						dialog = new SkuaidiSpecialDialog(mContext);
						dialog.setTitle("语音呼叫");
						dialog.setButtonCancleTitle("取消");
						dialog.setButtonOkTitle("呼叫");
						dialog.setBody1TextContext(adapter.getItem(position).getTitle());
						dialog.setBody1PicClickListener(new Body1PicClickListener() {

							@Override
							public void onclick() {

								final String localPath = adapter.getItem(position).getPathLocal();
								String servicePath = adapter.getItem(position).getPathService();

								if (Utility.isEmpty(localPath)) {
									UtilToolkit.showToast("播放错误，请重新刷新列表");
									return;
								}

								if (isPlaying) {
									dialog.setBody1PicIcon(R.drawable.cloud_play_stop2);
									stopPlayRecord();
								} else {
									dialog.setBody1PicIcon(R.drawable.cloud_play_start2);
									File file = new File(localPath);
									if (file.exists()) {
										isPlaying = true;
										startPlayRecord(localPath);
									} else {
										if (!Utility.getSDIsExist()) {
											UtilToolkit.showToast("SD卡不存在");
											return;
										}
										File voiceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
												+ "/skuaidi/cRecord");
										if (!voiceDirectory.exists()) {
											voiceDirectory.mkdirs();
										}
										FinalHttp fh = new FinalHttp();
										fh.download(servicePath, localPath, new AjaxCallBack<File>() {
											@Override
											public void onLoading(long count, long current) {
												super.onLoading(count, current);
												//System.out.println("count:" + count + "  current :" + current);
											}

											@Override
											public void onSuccess(File t) {
												super.onSuccess(t);
												startPlayRecord(localPath);
												isPlaying = true;
											}

											@Override
											public void onFailure(Throwable t, int errorNo, String strMsg) {
												super.onFailure(t, errorNo, strMsg);
												UtilToolkit.showToast("录音下载失败或已不存在该录音，请删除");
											}
										});
									}

								}
							}
						});
						dialog.setButtonCancleClickListener(new ButtonCancleClickListener() {

							@Override
							public void onclick() {
								stopPlayRecord();
								dialog.dismiss();
							}
						});
						dialog.setButtonOkClickListener(new ButtonOkClickListener() {

							@Override
							public void onclick() {
								setResult(RecordDetailActivity.RESULT_COME_FROM_VOICEMODELACTIVITY);
								stopPlayRecord();
								finish();
							}
						});
						dialog.setNotAutoDismiss(true);// 设置不自动隐藏
						dialog.showDialog();

					} else if (null != fromActivityType && fromActivityType.equals("AutoCloudCall")) {
						mIntent = new Intent();
						mIntent.putExtra("isChoose", true);
						setResult(SendMSGActivity.RESULT_AUTO_CLOUD_CALL, mIntent);
						stopPlayRecord();
						finish();
					} else if (!Utility.isEmpty(fromActivityType) && "selectTimingModel".equals(fromActivityType)) {
						stopPlayRecord();
						finish();
					} else if(!Utility.isEmpty(fromActivityType) && ("resend_smsORcloud".equals(fromActivityType) || "cloudCall_resend_smsORcloud".equals(fromActivityType))){
						cloudRecords.clear();
						cloudRecords = getDragVoiceModels();
						adapter.setCustomAdapter(cloudRecords);
						stopPlayRecord();
					}else if(!Utility.isEmpty(fromActivityType) && "sendCloudCallBachSign".equals(fromActivityType)) {// 派件发云呼界面
                        stopPlayRecord();
                        mIntent = new Intent();
                        mIntent.putExtra("voiceTemplate",adapter.getAdapterList().get(position));
                        setResult(SendCloudCallBachSignActivity.RESULT_CODE,mIntent);
                        finish();
					}else {
						setResult(SendYunHuActivity.OUT_CHOOSE_MODEL);
						stopPlayRecord();
						finish();
					}
				} else {
					UtilToolkit.showToast("请选择已审核模板");
				}
			}
		});
	}

	class updateRunnable implements Runnable {

		int position = 0;// 列表下标
		int currentPosition = 0;// progress 当前进度
		int maxCount = 0;// progress 最大值

		public updateRunnable() {
			super();// 使用super（）可以调用父类中的方法
		}

		public updateRunnable(int position, int maxCount) {
			super();
			this.position = position;
			this.maxCount = maxCount;
		}

		public void setThreadStop() {
			currentPosition = maxCount + 2;
		}

		// 用于当列表正在播放录音时同时做了删除该条录音的操作时使用
		public void setThreadStop(int position) {
			if (this.position == position) {
				this.position = -1;
			}
		}

		@Override
		public void run() {
			synchronized (this) {
				if (position != -1) {
					while (currentPosition <= maxCount) {
						Message msg = handler.obtainMessage();
						msg.what = UPDATE_PROGRESS;
						currentPosition = currentPosition + 1;
						msg.arg1 = position;
						msg.arg2 = currentPosition;
						msg.sendToTarget();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (currentPosition > maxCount) {
						Message msg = handler.obtainMessage();
						msg.what = UPDATE_PROGRESS;
						currentPosition = currentPosition + 1;
						msg.arg1 = position;
						msg.arg2 = currentPosition;
						msg.sendToTarget();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void updateProgress(int position, int currentPos) {
		try {
			CloudRecord crCloudRecord = adapter.getAdapterList().get(position);
			crCloudRecord.setCurrentProgress(currentPos);
			adapter.chargeProgress(position, crCloudRecord);
		} catch (Exception e) {
			e.printStackTrace();
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

	private void startPlayRecord(String path) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					setRecordStopImgBg();
					isPlaying = false;
					mPlayer = null;
				}
			});
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			mPlayer.start();
			isPlaying = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopPlayRecord() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			isPlaying = false;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (myTask != null) {
			myTask.cancel();
			myTask = null;
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		dismissProgressDialog();
		Message msg;
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("ivr.voice".equals(sname)) {
			if ("add".equals(act)) {
				if (result== null) {
					return;
				}
				try {
					String retStr = result.getString("retStr");
					String fileName = result.getString("fileName");
					UtilToolkit.showToast(retStr);
					File file = new File(file_path);
					String newFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/" + fileName + ".wav";
					file.renameTo(new File(newFileName));// 拿到接口给的名字以后，把本地的录音名字改成接口返回的，并保存在本地数据库中
				} catch (JSONException e) {
					e.printStackTrace();
				}
				msg = new Message();
				msg.what = ADD_VOICE_SUCCESS;
				handler.sendMessage(msg);
			} else if ("getlist".equals(act)) {
				List<CloudRecord> cRecords = new ArrayList<>();
				if (result== null) {
					return;
				}
				try {
                    Debug.startMethodTracing();
                    CloudRecordArr ca= JSON.parseObject(result.toString(),CloudRecordArr.class);
                    Debug.stopMethodTracing();
                    for (int i = 0;i<ca.getRetArr().size();i++){
                        AddVoiceModelActivity.CloudRecordArr.RetArrBean object = ca.getRetArr().get(i);
                        CloudRecord cRecord = new CloudRecord();
                        cRecord.setCreateTime(object.getCreate_time());// 语音创建时间
						cRecord.setModifytime(UtilityTime.timeStringToTimeStamp(object.getCreate_time(),UtilityTime.YYYY_MM_DD_HH_MM_SS));
                        cRecord.setTitle(object.getTitle());// 语音标题
                        cRecord.setIvid(object.getIvid()+"");// 语音对应服务器ID
                        cRecord.setFileName(object.getFile_name());// 语音名称
                        cRecord.setExamineStatus(object.getState()+"");// 语音审核状态
                        cRecord.setVoiceLength(object.getVoice_length());// 语音的时长
                        String pathService = "http://upload.kuaidihelp.com" + object.getPath().substring(object.getPath().indexOf("/",2));// 音频下载路径
                        cRecord.setPathService(pathService);// 保存音频下载路径
                        cRecord.setPathLocal(Environment.getExternalStorageDirectory().getAbsolutePath()+"/skuaidi/cRecord/"+object.getFile_name()+".wav");
                        cRecord.setChoose(false);
                        List<CloudRecord> cacheList = skuaidiDB.getCloudRecordModels();
                        if (null != cacheList && cacheList.size() != 0) {
                            for (int j = 0; j < cacheList.size(); j++) {
                                CloudRecord cacheCloudRecord = cacheList.get(j);
                                if (cRecord.getIvid().equals(cacheCloudRecord.getIvid()) && cRecord.getExamineStatus().equals("1")) {
                                    cRecord.setSort_no(cacheCloudRecord.getSort_no());
                                    break;
                                } else {
                                    cRecord.setSort_no("");
                                }
                            }
                        }
                        cRecords.add(cRecord);
                    }
					skuaidiDB.insertCloudRecord(cRecords);

				} catch (Exception e) {
					e.printStackTrace();
				}

				cloudRecords = skuaidiDB.getCloudRecordModels();

				// List<ReplyModel> cacheModels =
				// getModels(skuaidiDb.getPaijianModels());//
				// 获取到服务器所有数据以后在本地数据库中的全部数据
				for (int i = 0; i < cloudRecords.size(); i++) {
					for (int j = 0; j < cRecords.size(); j++) {
						if (null != cloudRecords.get(i)) {
							if (cloudRecords.get(i).getIvid().equals(cRecords.get(j).getIvid())) {
								break;
							}
							if (j == cRecords.size() - 1) {
								skuaidiDB.deleteCloudByivid(cloudRecords.get(i).getIvid());// 删除本地对应服务器上没有的模板
							}
						}
					}
				}

				msg = new Message();
				msg.what = GET_VOCIE_LIST_SUCCESS;
				handler.sendMessage(msg);
			} else if ("delete".equals(act)) {
				if (result== null) {
					return;
				}
				try {
					String retStr = result.getString("retStr");
					UtilToolkit.showToast(retStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				msg = new Message();
				msg.what = DELETE_VOICE_SUCCESS;
				handler.sendMessage(msg);
			} else if ("update".equals(act)) {
				if (result== null) {
					return;
				}
				String retStr = "";
				try {
					retStr = result.getString("retStr");
					UtilToolkit.showToast(retStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				msg = new Message();
				msg.what = UPDATE_VOICE_SUCCESS;
				msg.obj = retStr;
				handler.sendMessage(msg);
			} else if ("verify_limit".equals(act)) {
				msg = new Message();
				msg.what = GET_ISADDMODEL_STATUS_SUCCESS;
				handler.sendMessage(msg);
			}
		}else if ("ivr/resendIvr".equals(sname)){
			UtilToolkit.showToast_Custom(message);
			setResult(RecordSendSmsSelectActivity.RESULT_RESEND);
			finish();
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	private String callData(){
		if (null == smsRecords){
			UtilToolkit.showToast("数据获取失败");
			return "";
		}
		String[] call_data = new String[smsRecords.size()];
		for (int i = 0;i<smsRecords.size();i++){
			call_data[i] = smsRecords.get(i).getInform_id();
		}
		return new Gson().toJson(call_data);
	}

	private String callDataCloudCallRecord(){
		if (null ==cvres){
			UtilToolkit.showToast("数据获取失败");
			return "";
		}
		String[] call_data = new String[cvres.size()];
		for (int i =0;i<cvres.size();i++){
			call_data[i] = cvres.get(i).getCid();
		}
		return new Gson().toJson(call_data);
	}

	/** 【短信记录|云呼记录】 筛选未取件|发送失败 重新发送云呼接口
	 * tid:云呼模板**/
	private void resendInform(String tid){
		JSONObject data = new JSONObject();
		try {
			data.put("sname","ivr/resendIvr");
			data.put("ivid",tid);
			if ("resend_smsORcloud".equals(fromActivityType)) {
				data.put("from", "inform_user");
				data.put("call_data", callData());
			}else{
				data.put("from", "ivr");
				data.put("call_data", callDataCloudCallRecord());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data,false, HttpHelper.SERVICE_V1);
	}

	/** 获取模板列表 **/
	private void getModels() {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", "ivr.voice");
			json.put("act", "getlist");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
	}

	/** 更新模板 **/
	private void updateModel(String title, String ivid) {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", "ivr.voice");
			json.put("act", "update");
			json.put("title", title);
			json.put("ivid", ivid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
	}

	/** 删除模板 **/
	private void deleteModel(String ivid) {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", "ivr.voice");
			json.put("act", "delete");
			json.put("ivid", ivid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
	}

	/** 添加模板 **/
	private void addModel(String title, long recordLength, String recordPath) {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", "ivr.voice");
			json.put("act", "add");
			json.put("title", title);// 标题
			json.put("len", recordLength);// 录音时长
			json.put("voice", recordPath);// 录音流
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
	}

	/** 调用是否可以继续上传录音接口 **/
	private void judgeIsAddModel() {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", "ivr.voice");
			json.put("act", "verify_limit");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(SendYunHuActivity.OUT_CHOOSE_MODEL);
			stopPlayRecord();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}


	public static class CloudRecordArr {


        /**
         * ivid : -1
         * title : 代收点快取
         * file_name : 10010
         * path : /upload/ivr_voice/sample/10010.wav
         * voice_length : 12
         * create_time : 2015-08-13 09:00:00
         * state : 1
         */

        private List<RetArrBean> retArr;

        public List<RetArrBean> getRetArr() {
            return retArr;
        }

        public void setRetArr(List<RetArrBean> retArr) {
            this.retArr = retArr;
        }

        public static class RetArrBean {
            private int ivid;
            private String title;
            private String file_name;
            private String path;
            private int voice_length;
            private String create_time;
            private int state;

            public int getIvid() {
                return ivid;
            }

            public void setIvid(int ivid) {
                this.ivid = ivid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFile_name() {
                return file_name;
            }

            public void setFile_name(String file_name) {
                this.file_name = file_name;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public int getVoice_length() {
                return voice_length;
            }

            public void setVoice_length(int voice_length) {
                this.voice_length = voice_length;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }
        }
    }
}
