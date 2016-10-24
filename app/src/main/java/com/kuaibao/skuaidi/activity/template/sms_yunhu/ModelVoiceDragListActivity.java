package com.kuaibao.skuaidi.activity.template.sms_yunhu;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ModelVoiceDragListAdapter;
import com.kuaibao.skuaidi.activity.adapter.ModelVoiceDragListAdapter.ButtonOnclick;
import com.kuaibao.skuaidi.activity.view.SkuaidiDragListView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 语音排序列表
 */
public class ModelVoiceDragListActivity extends RxRetrofitBaseActivity implements OnClickListener{

	private final static int UPDATE_PROGRESS = 0x1004;
	
	private Context mContext = null;
	private SkuaidiDB skuaidiDB = null;
	private ModelVoiceDragListAdapter modelDragListAdapter;
	private SkuaidiDragListView drag_list;// 模板列表

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_more)
    TextView tvMore;// 完成按钮
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_PROGRESS:// 更新progress
				int position = msg.arg1;
				int currentPositon = msg.arg2;
				updateProgress(position, currentPositon);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.model_drag_list_activity);
        ButterKnife.bind(this);
		mContext = this;
		EventBus.getDefault().register(this);
		skuaidiDB = SkuaidiDB.getInstanse(mContext);
		initView();
//		clouRecordUtility = new CloudCallRecordUtility(mContext, handler, iv_voice_anim_left, iv_voice_anim_right);
	}
	
	private void initView(){
		drag_list = (SkuaidiDragListView) findViewById(R.id.drag_list);

		tvTitleDes.setText("我的模板");
		tvMore.setVisibility(View.VISIBLE);
        tvMore.setText("完成");
		getModels();// 获取模板
	}
	
	private boolean isPlaying = false;
	int clickPosition = 0;// 记录每次点击的列表ID
	private Thread thread = null;
	private updateRunnable runnable = null;
	private MediaPlayer mPlayer = null;

	private void getModels(){
        List<CloudRecord> models = AddVoiceModelActivity.getDragVoiceModels();
		modelDragListAdapter = new ModelVoiceDragListAdapter(mContext, models,new ButtonOnclick() {
			
			@Override
			public void onPlayMusic(View v, final int position, final String localPath, String servicePath, final int voiceLength) {
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
//					clouRecordUtility.stopPlaying();
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
								runnable = new updateRunnable(position, voiceLength);
								thread = new Thread(runnable);
								thread.start();
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
		drag_list.setAdapter(modelDragListAdapter);
	}
	
	private void updateProgress(int position, int currentPos) {
		try {
			CloudRecord crCloudRecord = modelDragListAdapter.getModels().get(position);
			crCloudRecord.setCurrentProgress(currentPos);
			modelDragListAdapter.chargeProgress(position, crCloudRecord);
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

	@OnClick({R.id.iv_title_back,R.id.tv_more})
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_title_back:// 返回
			stopPlayRecord();
			finish();
			break;
			
		case R.id.tv_more:// 完成按钮
//			List<CloudRecord> models = new ArrayList<>();
			List<CloudRecord> models = modelDragListAdapter.getModels();
			// 将新获取到的列表按照下标设置好顺序【从0开始】
			for (int i = 0; i < models.size(); i++) {
				models.get(i).setSort_no(i+"");
			}
			skuaidiDB.insertCloudRecord(models);
			Intent intent = new Intent();
			intent.putExtra("models", (Serializable)models);
			setResult(902, intent);
			stopPlayRecord();
			finish();
			break;

		default:
			break;
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            stopPlayRecord();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
	public void onEventMainThread(UpdateList event) {

		String msg = event.getMsg();
		if (!Utility.isEmpty(msg) && msg.equals("updateList")) {
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
	}
	private void startPlayRecord(String path) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
//					setRecordStopImgBg();
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

	private void stopPlayRecord() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			isPlaying = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
        //ButterKnife.unbind(this);
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
				//Log.i("GUDD", "currentPosition in run  " + currentPosition);
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
	
	public class UpdateList{
		private String mMsg;
		
		public UpdateList(String msg) {
			mMsg = msg;
		}
		public String getMsg(){
			return mMsg;
		}
	}


	
}
