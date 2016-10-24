package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CallLog;
import com.kuaibao.skuaidi.activity.model.CallRecordingMp3;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.DensityUtil;
import com.kuaibao.skuaidi.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 
 * 删除通讯记录适配器
 * 
 * @author xy
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DeleteCallLogsAdapter extends BaseAdapter {
	private List<CallLog> callLogs;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private MediaPlayer mp;
	private boolean isPlaying = false;
	private int playingIndex = -1;
	public static final int DATA_NOTYFYED = 909;
	
	public DeleteCallLogsAdapter(Context context,
			List<CallLog> callLogs) {
		this.context = context;
		this.callLogs = callLogs;
	}
	
	@Override
	public int getCount() {
		return callLogs.size();
	}
	
	public List<CallLog> getAllItem(){
		return callLogs;
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
	public View getView(final int position, View convertView, final ViewGroup arg2) {
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.delete_call_logs_item2, null);
			holder = new ViewHolder();
			holder.callerName_new = (TextView) convertView.findViewById(R.id.caller_name_new);
			holder.call_type_new = (ImageView) convertView.findViewById(R.id.iv_outgoing_new);
			holder.call_date_new = (TextView) convertView.findViewById(R.id.call_date_new);
			holder.play_recording_new = (ImageView) convertView.findViewById(R.id.iv_play_radio_new);
			holder.delete_list_item_new = (CheckBox) convertView.findViewById(R.id.delete_list_item_new);
			holder.call_phone_new=(TextView) convertView.findViewById(R.id.call_phone_new);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		callerName = ((CallLog) getItem(position)).getCallerName();
		phoneNumber = ((CallLog) getItem(position)).getPhoneNumber();
		holder.delete_list_item_new.setChecked(callLogs.get(position).getFlag());
		phoneNum = phoneNumber.length() >= 11 ? phoneNumber.substring(phoneNumber.length() - 11, phoneNumber.length()) : phoneNumber;
		final CallRecordingMp3 recordingMp3 = ((CallLog) getItem(position)).getCallRecordingMp3();
		if (recordingMp3 != null) {
			if (isPlaying == true && position== playingIndex) {
				holder.play_recording_new.setBackgroundResource(R.drawable.record_stop_small);
			} else {
				holder.play_recording_new.setBackgroundResource(R.drawable.record_play_small);
			}
			holder.play_recording_new
					.setOnClickListener(new View.OnClickListener() {					
						@Override
						public void onClick(final View v) {
							try {
								if (isPlaying == true
										&& playingIndex != position) {
									
									mp.stop();
									mp.release();
									mp = null;
									indexView.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
									mp = new MediaPlayer();
									mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

										@Override
										public void onCompletion(
												MediaPlayer arg0) {
											isPlaying = false;
											mp = null;
											v.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
										}
									});
									mp.reset();
									mp.setDataSource(recordingMp3.getUrl());
									mp.prepare();
									mp.start();									
									v.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_stop_small"));
									playingIndex = position;
									indexView = v;
								} else if (isPlaying == true
										&& playingIndex == position) {
									mp.stop();
									mp.release();
									mp = null;									
									v.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
									isPlaying = false;
									playingIndex = position;
								} else {
									indexView = v;
									UMShareManager.onEvent(context, "callLog_callRecording_play", "callLog", "通话记录:播放录音");
									mp = new MediaPlayer();
									mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

										@Override
										public void onCompletion(
												MediaPlayer arg0) {
											isPlaying = false;
											mp = null;											
											v.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
										}
									});
									mp.reset();
									mp.setDataSource(recordingMp3.getUrl());
									mp.prepare();
									mp.start();								
									v.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_stop_small"));
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
			holder.play_recording_new.setVisibility(View.VISIBLE);
		} else {
			holder.play_recording_new.setVisibility(View.INVISIBLE);
		}
		
		String formatDateString = format.format(new Date(((CallLog) getItem(position)).getCallDate()));
		if(((CallLog) getItem(position)).getCallType()==0){
			holder.call_type_new.setVisibility(View.VISIBLE);
			//holder.call_type.setImageResource(R.drawable.incoming);
		}else{
			holder.call_type_new.setVisibility(View.INVISIBLE);
			//holder.call_type.setImageResource(R.drawable.outgoing);
		}
		Utility.setTimeDate2(formatDateString, holder.call_date_new);
		if(TextUtils.isEmpty(callerName)||"新客户".equals(callerName)){
			holder.callerName_new.setText(phoneNumber);
			holder.call_phone_new.setText("");
			holder.call_phone_new.setPadding(DensityUtil.dip2px(context, 20), 0, 0, 0);
		}else{
			holder.callerName_new.setText(callerName);
			holder.call_phone_new.setText(phoneNumber);
			holder.call_phone_new.setPadding(DensityUtil.dip2px(context, 20), 0, DensityUtil.dip2px(context, 12), 0);
		}
		return convertView;
	}
	
	

	
	public static	class ViewHolder {
	 public TextView callerName_new,call_phone_new, call_date_new;
	 //public View callLogsItemBg;
	 public	CheckBox delete_list_item_new;
	 public ImageView play_recording_new,call_type_new;
			
	}
	
	public void stopPlayRecording() {
		if (mp != null) {
			mp.pause();
			mp.stop();
			mp.release();
			mp = null;
		}
	}
}
