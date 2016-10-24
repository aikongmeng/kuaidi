package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CallLog;
import com.kuaibao.skuaidi.activity.model.CallRecordingMp3;
import com.kuaibao.skuaidi.activity.model.CustomerCallLog;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 录音选择结果
 * 
 * @author wq
 * 
 */
@SuppressLint("SimpleDateFormat")
public class AudioProofPreviewAdapter extends BaseAdapter {
	public List<CallLog> callLogs;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private MediaPlayer mp;
	private boolean isPlaying = false;
	private int playingIndex = -1;
	private Map<String, MyCustom> customs = new HashMap<String, MyCustom>();
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();

	public AudioProofPreviewAdapter(Context context, List<CallLog> callLogs) {
		this.context = context;
		this.callLogs = callLogs;
		for (int i = 0; i < this.callLogs.size(); i++) {
			MyCustom cus = new MyCustom();
			CallLog callLog = this.callLogs.get(i);
			cus.setName(callLog.getCallerName());
			cus.setPhone(callLog.getPhoneNumber());
			customs.put(i + "", cus);
		}
	}

	@Override
	public int getCount() {
		return callLogs.size();
	}

	@Override
	public Object getItem(int position) {

		return callLogs.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	MyCustom cus;
	String mp3File = "";
	String callerName = "";
	String phoneNumber = "";
	String phoneNum = "";
	ViewHolder holder;
	View indexView;

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.proof_audio_item, parent, false);
			holder = new ViewHolder();
			holder.callerName = (TextView) convertView.findViewById(R.id.caller_name);
			holder.callerType = (TextView) convertView.findViewById(R.id.caller_type);
			holder.call_date = (TextView) convertView.findViewById(R.id.call_date);
			holder.callLogsItemBg = convertView.findViewById(R.id.call_logs_item_bg);
			holder.play_recording = (TextView) convertView.findViewById(R.id.click_play_recording);
			// holder.delete_list_item = (CheckBox)
			// convertView.findViewById(R.id.delete_list_item);
			holder.call_type = (ImageView) convertView.findViewById(R.id.call_type);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		callerName = ((CallLog) getItem(position)).getCallerName();
		phoneNumber = ((CallLog) getItem(position)).getPhoneNumber();
		// holder.delete_list_item.setChecked(callLogs.get(position).getFlag());
		phoneNum = phoneNumber.length() >= 11 ? phoneNumber.substring(phoneNumber.length() - 11, phoneNumber.length()) : phoneNumber;
		holder.play_recording.setBackgroundResource(R.drawable.record_play_small);
		final CallRecordingMp3 recordingMp3 = ((CallLog) getItem(position)).getCallRecordingMp3();
		if (recordingMp3 != null) {
			if (isPlaying == true && position == playingIndex) {
				holder.play_recording.setBackgroundResource(R.drawable.record_stop_small);
			} else {
				holder.play_recording.setBackgroundResource(R.drawable.record_play_small);
			}
			holder.play_recording.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					try {
						if (isPlaying == true && playingIndex != position) {

							mp.stop();
							mp.release();
							mp = null;
							indexView.setBackgroundResource(R.drawable.record_play_small);
							mp = new MediaPlayer();
							mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									isPlaying = false;
									mp = null;
									v.setBackgroundResource(R.drawable.record_play_small);
								}
							});
							mp.reset();
							mp.setDataSource(recordingMp3.getUrl());
							mp.prepare();
							mp.start();
							v.setBackgroundResource(R.drawable.record_stop_small);
							playingIndex = position;
							indexView = v;
						} else if (isPlaying == true && playingIndex == position) {
							mp.stop();
							mp.release();
							mp = null;
							v.setBackgroundResource(R.drawable.record_play_small);
							isPlaying = false;
							playingIndex = position;
						} else {
							indexView = v;
							UMShareManager.onEvent(context, "callLog_callRecording_play", "callLog", "通话记录:播放录音");
							mp = new MediaPlayer();
							mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									isPlaying = false;
									mp = null;
									v.setBackgroundResource(R.drawable.record_play_small);
								}
							});
							mp.reset();
							mp.setDataSource(recordingMp3.getUrl());
							mp.prepare();
							mp.start();
							v.setBackgroundResource(R.drawable.record_stop_small);
							isPlaying = true;
							playingIndex = position;
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			holder.play_recording.setVisibility(View.VISIBLE);
		} else {
			holder.play_recording.setVisibility(View.INVISIBLE);
		}

		String formatDateString = format.format(new Date(((CallLog) getItem(position)).getCallDate()));
		if (((CallLog) getItem(position)).getCallType() == 0) {
			holder.call_type.setImageResource(R.drawable.incoming);
		} else {
			holder.call_type.setImageResource(R.drawable.outgoing);
		}
		Utility.setTimeDate2(formatDateString, holder.call_date);

		final List<MyCustom> cuss = newDB.selectCustomerByPhone(phoneNum);

		if (recordingMp3 != null) {
			mp3File = recordingMp3.getTitle();
		}
		if (cuss.size() > 0) {
			cus = cuss.get(0);
			holder.callerType.setText(cus.getName());
		}

		holder.callLogsItemBg.setBackgroundResource(R.color.white);

		holder.callerName.setText(phoneNumber);
		holder.callerType.setText(callerName);

		if (callLogs.get(position).getIsCanAddOrder() == CustomerCallLog.CANNOT_ADD_ORDER) {
		}

		if (callLogs.get(position).getIsCanAddMSG() == CustomerCallLog.CANNOT_ADD_MSG) {
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView callerName, callerType, call_date, play_recording;
		public View callLogsItemBg;
		public ImageView call_type;

	}

	public void stopPlayRecording() {
		if (mp != null) {
			mp.pause();
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	public void isBatchAdd(boolean isBatchAdd) {
		notifyDataSetChanged();
	}

	public List<MyCustom> getWillAddCustoms() {
		List<MyCustom> list = new ArrayList<MyCustom>();
		for (String key : customs.keySet()) {
			list.add(customs.get(key));
		}
		return list;
	}
}
