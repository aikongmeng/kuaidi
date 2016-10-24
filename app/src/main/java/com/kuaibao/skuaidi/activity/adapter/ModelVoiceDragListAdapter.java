package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class ModelVoiceDragListAdapter extends BaseModelDragListAdapter<CloudRecord> {

	private ButtonOnclick onclick = null;
	private TextView drag_list_item_title = null; // 标题
	private LinearLayout ll_play_icon = null;// 播放按钮【用于点击区域】 
	private ImageView iv_play_icon = null;// 播放按钮
	private TextView tv_rec_time = null;// 进度时间
	private ProgressBar progressbar = null;// 进度条
	private TextView tv_rec_total_time = null; // 录音时长
	
	private int pg_max = 0;// progress的最大值
	private int pg_cur = 0;// progress的当前值
	
	public ModelVoiceDragListAdapter(Context context, List<CloudRecord> replyModels,ButtonOnclick onclick) {
		super(context, replyModels);
		this.onclick = onclick;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		convertView = LayoutInflater.from(context).inflate(R.layout.model_drag_list_adapter_item, null);
		
		drag_list_item_title = (TextView) convertView.findViewById(R.id.drag_list_item_title); // 标题
		ll_play_icon = (LinearLayout) convertView.findViewById(R.id.ll_play_icon);// 播放按钮【用于点击区域】 
		iv_play_icon = (ImageView) convertView.findViewById(R.id.iv_play_icon);// 播放按钮
		tv_rec_time = (TextView) convertView.findViewById(R.id.tv_rec_time);// 进度时间
		progressbar = (ProgressBar) convertView.findViewById(R.id.voice_record_progressbar);// 进度条
		tv_rec_total_time = (TextView) convertView.findViewById(R.id.tv_rec_total_time);// 录音时长
		final CloudRecord c = (CloudRecord) getItem(position);
		if(!Utility.isEmpty(c.getTitle())){
			drag_list_item_title.setText(c.getTitle());
		}else{
			drag_list_item_title.setText("");
		}
		
		if (!Utility.isEmpty(c.getVoiceLength())) {
			String timeFormate = Utility.formatTime(c.getVoiceLength());
			tv_rec_total_time.setText(timeFormate);
		}
		
		ll_play_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onclick.onPlayMusic(arg0, position, c.getPathLocal(),c.getPathService(), c.getVoiceLength());
			}
		});
		
		pg_max = c.getVoiceLength();// 语音时长
		pg_cur = c.getCurrentProgress();// 当前进度
		progressbar.setMax(pg_max);
		if(pg_cur == 0 || pg_cur > pg_max){
			progressbar.setProgress(0);
			CloudRecord cRecord = c;
			cRecord.setCurrentProgress(0);
			getModels().set(position, cRecord);
			iv_play_icon.setBackgroundResource(R.drawable.cloud_play_stop);
		}else{
			String curTime = Utility.formatTime(pg_cur);
			tv_rec_time.setText(curTime);
			progressbar.setProgress(pg_cur);
			iv_play_icon.setBackgroundResource(R.drawable.cloud_play_start);
		}
		
		return convertView;
	}
	
	/**更新progressbar**/
	public void chargeProgress(int position ,CloudRecord cRecord){
		getModels().set(position, cRecord);
		notifyDataSetChanged();
	}
	
	public interface ButtonOnclick{
		void onPlayMusic(View v,int position,String localPath,String servicePath,int voiceLength);
	}

}
