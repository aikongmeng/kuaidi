package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CloudVoiceRecordAdapter extends BaseAdapter {

	private Context mContext = null;
	private Handler mHandler = null;
	private List<CloudVoiceRecordEntry> cvre = null;
	private SkuaidiDB skuaidiDB = null;
	private List<CloudRecord> cRecords = null;
	private ButtonClickListener butClickListener = null;

	private ImageView ivPickUp = null;// 取件状态图标
	private TextView tv_no;// 编号
	private TextView tv_time;// 时间
	private TextView tvContent;// 发送内容
	private TextView tv_status;// 发送状态
	private TextView tvTimeTitle;
	private RelativeLayout title;
	private TextView tvMobile = null;// 手机号显示区域
	private View splitLine;
	private View line1, line2;
	private ImageView redCircle;
	private RelativeLayout llCall;// 拨打电话
	private int scrollPosition;
	private boolean havaName = false;

	public CloudVoiceRecordAdapter(Context context, Handler handler, List<CloudVoiceRecordEntry> cloudVoiceRecordEntries, ButtonClickListener buttonClickListener) {
		this.mContext = context;
		this.mHandler = handler;
		this.cvre = cloudVoiceRecordEntries;
		this.butClickListener = buttonClickListener;
		skuaidiDB = SkuaidiDB.getInstanse(context);
		cRecords = skuaidiDB.getCloudRecordModels();
	}

	@Override
	public int getCount() {
		if (null != cvre && 0 != cvre.size()) {
			return cvre.size();
		} else {
			return 0;
		}
	}

	@Override
	public CloudVoiceRecordEntry getItem(int position) {
		return cvre.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void modifySignedStatus(int position){
		if(cvre.get(position).getSigned() == 0){
			cvre.get(position).setSigned(1);
		}else if(cvre.get(position).getSigned() == 1){
			cvre.get(position).setSigned(0);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.cloud_voice_record_item, null);
		ivPickUp = (ImageView) convertView.findViewById(R.id.ivPickUp);
		tv_no = (TextView) convertView.findViewById(R.id.tv_no);
		tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		tvContent = (TextView) convertView.findViewById(R.id.tvContent);
		tv_status = (TextView) convertView.findViewById(R.id.tv_status);
		llCall = (RelativeLayout) convertView.findViewById(R.id.llCall);
		tvTimeTitle = (TextView) convertView.findViewById(R.id.tvTimeTitle);
		title = (RelativeLayout) convertView.findViewById(R.id.title);
		tvMobile = (TextView) convertView.findViewById(R.id.tvMobile);
		splitLine = convertView.findViewById(R.id.line);
		line1 = convertView.findViewById(R.id.line1);
		line2 = convertView.findViewById(R.id.line2);
		redCircle = (ImageView) convertView.findViewById(R.id.redPoint);
		scrollPosition = position;
		CloudVoiceRecordEntry entry = getItem(position);
		tv_time.setText(entry.getCall_time().substring(11, 16));
		timeTitleControl(position);
		String phone = entry.getCall_number();
		if (!Utility.isEmpty(phone))
			tvMobile.setText(entry.getCall_number());
		else
			tvMobile.setText("1**********");
		tv_no.setText(entry.getBh());
		if (entry.getNoreadFlag() != 0) {
			redCircle.setVisibility(View.VISIBLE);
		} else {
			redCircle.setVisibility(View.GONE);
		}
		if(!Utility.isEmpty(entry.getLastMsgContent())){
			tvContent.setText(entry.getLastMsgContent());
		}else{
			tvContent.setText("");
		}
		
		tv_status.setText(entry.getStatus_msg());
		switch (entry.getStatus()){
			case "1":// 发送失败
				tv_status.setTextColor(ContextCompat.getColor(mContext,R.color.red_f74739));
				break;
			case "2":// 收到|查看
				tv_status.setTextColor(ContextCompat.getColor(mContext,R.color.green_39b54a));
				break;
			case "3":// 已发送
				tv_status.setTextColor(ContextCompat.getColor(mContext,R.color.status_orange));
				break;
			case "4":// 已回复
				tv_status.setTextColor(ContextCompat.getColor(mContext,R.color.default_green));
				break;
		}

		if (entry.getSigned() == 0) {
			ivPickUp.setBackgroundResource(R.drawable.message_notsign_icon);
		} else if (entry.getSigned() == 1) {
			ivPickUp.setBackgroundResource(R.drawable.message_signed_icon);
		}

		if (!Utility.isEmpty(entry.getVoice_name())) {
			for (int i = 0; i < cRecords.size(); i++) {
				if (entry.getVoice_name().equals(cRecords.get(i).getFileName())) {
					havaName = true;
				}
			}
		}

		llCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butClickListener.call(v, position, getItem(position).getCall_number());
			}
		});
		ivPickUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butClickListener.updateSignedStatus(v, position, getItem(position).getCid());
			}
		});

		return convertView;
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private static final String FORMAT_TODAY = "今天";
	private static final String FORMAT_YESTERDAY = "昨天";

	private void timeTitleControl(int position) {
		String tempTime = "";
		if (getItem(position).getCreate_time() != null) {
			tempTime = timeFormat(getItem(position).getCreate_time());
		}
		String theLastTime = "";
		if (position != 0) {
			if (getItem(position).getCreate_time() != null) {
				theLastTime = timeFormat(getItem(position - 1).getCreate_time());
			}
		}
		tvTimeTitle.setText(tempTime);
		if (position != 0 && tempTime.equals(theLastTime)) {
			line1.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			splitLine.setVisibility(View.GONE);
		} else {
			line1.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			splitLine.setVisibility(View.VISIBLE);
		}

		if (cvre.get(position) != null) {
			if (cvre.size() != position + 1) {
				if (cvre.get(position + 1) != null) {
					String updateTime1 = getItem(position).getCreate_time();
					String updateTime2 = cvre.get(position + 1).getCreate_time();
					if (updateTime1.substring(0, 10).equals(updateTime2.substring(0, 10))) {
						line2.setVisibility(View.VISIBLE);
					} else {
						line2.setVisibility(View.GONE);
					}
				} else {
					line2.setVisibility(View.GONE);
				}
			}
		} else {
			line2.setVisibility(View.GONE);
		}
	}

	private String timeFormat(String date) {
		if (TextUtils.isEmpty(date)) {
			return date;
		}
		String now = DATE_FORMAT.format(System.currentTimeMillis());
		if (date.substring(0, 10).equals(now.substring(0, 10))) {
			return FORMAT_TODAY;
		} else if (date.substring(0, 7).equals(now.substring(0, 7)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
			return FORMAT_YESTERDAY;
		} else {
			return date.substring(0, 10);
		}
	}

	/**
	 * 更新列表数据
	 */
	public void notifyList(List<CloudVoiceRecordEntry> cvre) {
		this.cvre = cvre;
		notifyDataSetChanged();
	}

	/**
	 * 获取列表详情
	 */
	public List<CloudVoiceRecordEntry> getListDetail() {
		return this.cvre;
	}

	public void notifyListPlayIcon() {
		cRecords = skuaidiDB.getCloudRecordModels();
		notifyDataSetChanged();
	}

	public interface ButtonClickListener {
		/** 拨打电话 **/
		void call(View v, int position, String number);

		/** 更新取件状态 **/
		void updateSignedStatus(View v, int position, String cid);
	}

}
