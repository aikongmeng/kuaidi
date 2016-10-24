package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class AddVoiceModelAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<CloudRecord> cRecords = null;
	private ButtonOnclick mOnclick = null;
	
	private TextView tv_voice_title;// 模板标题
//	private TextView tv_voice_length;// 模板语音时长
	private ImageView iv_audit_bg;// 审核状态图片
	private RadioButton rdbtn_choose;// 选中状态的勾
	private LinearLayout ll_play_icon;// 播放按钮 
	private ImageView iv_play_icon;// 播放按钮
	private TextView tv_rec_time;// 正在播放的时长
	private TextView tv_rec_total_time;// 模板语音时长
	private ProgressBar progressbar;// 进度条
	private RelativeLayout rl_edit;// 编辑按钮
	private RelativeLayout rl_delete;// 删除按钮
	
	private int pg_max = 0;// progress的最大值
	private int pg_cur = 0;// progress的当前值
 	
	public AddVoiceModelAdapter(Context context,Handler handler,List<CloudRecord> cloudRecords,ButtonOnclick onclick){
		super();
		this.mContext = context;
		this.cRecords = cloudRecords;
		this.mOnclick = onclick;
		
	}
	
	@Override
	public int getCount() {
		if(0 == cRecords.size()){
			return 0;
		}else {
			return cRecords.size();
		}
	}

	@Override
	public CloudRecord getItem(int arg0) {
		return cRecords.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.cloud_voice_model_item, null);
		
		tv_voice_title = (TextView) convertView.findViewById(R.id.tv_vocie_title);// 模板标题
//		tv_voice_length = (TextView) convertView.findViewById(R.id.tv_voice_length);// 模板语音时长
		iv_audit_bg = (ImageView) convertView.findViewById(R.id.iv_audit_bg);// 审核状态图片
		rdbtn_choose = (RadioButton) convertView.findViewById(R.id.rdbtn_choose);// 选中状态的勾
		ll_play_icon = (LinearLayout) convertView.findViewById(R.id.ll_play_icon);// 播放按钮 
		iv_play_icon = (ImageView) convertView.findViewById(R.id.iv_play_icon);// 播放按钮
		tv_rec_time = (TextView) convertView.findViewById(R.id.tv_rec_time);// 正在播放的时长
		tv_rec_total_time = (TextView) convertView.findViewById(R.id.tv_rec_total_time);// 模板语音时长
		progressbar = (ProgressBar) convertView.findViewById(R.id.voice_record_progressbar);// 进度条
		rl_edit = (RelativeLayout) convertView.findViewById(R.id.rl_edit);// 编辑按钮
		rl_delete = (RelativeLayout) convertView.findViewById(R.id.rl_delete);// 删除按钮
		
		if(null != getItem(position)){
			tv_voice_title.setText(getItem(position).getTitle());// 设置标题
			String timeFormate = Utility.formatTime(getItem(position).getVoiceLength());
//			tv_voice_length.setText(timeFormate);// 设置语音时长
			tv_rec_total_time.setText(timeFormate);// 同上
			rdbtn_choose.setChecked(getItem(position).isChoose());// 设置是否被选中
//			if(getItem(position).getExamineStatus().equals("1")){
//				iv_audit_bg.setBackgroundResource(R.drawable.model_audited);// 设置已审核图片
//			}else if(getItem(position).getExamineStatus().equals("0")){
//				iv_audit_bg.setBackgroundResource(R.drawable.model_unexamine);// 设置未审核图片
//			}else if(getItem(position).getExamineStatus().equals("2")){
//				iv_audit_bg.setBackgroundResource(R.drawable.model_rejected);// 设置已拒绝图片
//			}else{
//				iv_audit_bg.setBackgroundResource(R.drawable.model_unexamine);// 设置未审核图片
//			}
			
			final String title = getItem(position).getTitle();
			final String ivid = getItem(position).getIvid();
			final String localPath = getItem(position).getPathLocal();
			final String servicePath = getItem(position).getPathService();
			final int voiceLength = getItem(position).getVoiceLength();
			
			rl_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnclick.onModify(v, position, ivid,title);
				}
			});
			
			rl_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnclick.onDelete(v, position, ivid);
				}
			});
			
			ll_play_icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnclick.onPlayMusic(v, position, localPath, servicePath, voiceLength);
				}
			});
			
			pg_max = getItem(position).getVoiceLength();// 语音时长
			pg_cur = getItem(position).getCurrentProgress();// 当前进度
			progressbar.setMax(pg_max);
			if(pg_cur == 0 || pg_cur > pg_max){
				progressbar.setProgress(0);
				CloudRecord cRecord = cRecords.get(position);
				cRecord.setCurrentProgress(0);
				this.cRecords.set(position, cRecord);
				iv_play_icon.setBackgroundResource(R.drawable.cloud_play_stop);
			}else{
				String curTime = Utility.formatTime(pg_cur);
				tv_rec_time.setText(curTime);
				progressbar.setProgress(pg_cur);
				iv_play_icon.setBackgroundResource(R.drawable.cloud_play_start);
			}
		}
		return convertView;
	}
	
	/**更新progressbar**/
	public void chargeProgress(int position ,CloudRecord cRecord){
		this.cRecords.set(position, cRecord);
		notifyDataSetChanged();
	}
	/**获取当前列表中的数据**/
	public List<CloudRecord> getAdapterList(){
		return cRecords;
	}
	/**设置当前列表中的数据**/
	public void setCustomAdapter(List<CloudRecord> ccRecords){
		this.cRecords = ccRecords;
		notifyDataSetChanged();
	}

	/**設置點擊的條目被選中**/
	public void setSelected(int position){
		for (CloudRecord c : cRecords){
			c.setChoose(false);
		}
		getItem(position).setChoose(true);
		notifyDataSetChanged();
	}

	/**每个item上面按钮的点击事件接口**/
	public interface ButtonOnclick{
		void onDelete(View v, int position, String ivid);
		void onModify(View v, int position, String ivid,String title);
		void onPlayMusic(View v,int position,String localPath,String servicePath,int voiceLength);
	}

}
