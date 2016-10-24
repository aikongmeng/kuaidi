package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 短信记录列表适配器
 * 顾冬冬
 */
public class SmsRecordAdapter extends BaseAdapter {
	private Context mContext = null;
	private List<SmsRecord> mSmsRecords = null;
	private SmsRecord smsRecord = null;
	private ViewHolder holder = null;
	private OnclickListener clickListener = null;

	public SmsRecordAdapter(Context context, List<SmsRecord> smsRecords, OnclickListener clickListener) {
		this.mContext = context;
		this.mSmsRecords = smsRecords;
		this.clickListener = clickListener;
	}

	@Override
	public int getCount() {
		return mSmsRecords.size();
	}

	@Override
	public SmsRecord getItem(int position) {
		return mSmsRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/** 刷新列表 **/
	public void notifyData(List<SmsRecord> smsRecords) {
		this.mSmsRecords = smsRecords;
		notifyDataSetChanged();
	}

	/** 获取适配器中的数据 **/
	public List<SmsRecord> getAdapterData() {
		return mSmsRecords;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_record_adapter_item, parent,false);
			holder.item = (ViewGroup) convertView.findViewById(R.id.item);
			holder.redPoint = (ImageView) convertView.findViewById(R.id.redPoint);
			holder.ivPickUp = (ImageView) convertView.findViewById(R.id.ivPickUp);
			holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
			holder.tvMobilePhone = (TextView) convertView.findViewById(R.id.tvMobilePhone);
			holder.tvLastUpdateTime = (TextView) convertView.findViewById(R.id.tvLastUpdateTime);
			holder.tvCmContent = (TextView) convertView.findViewById(R.id.tvCmContent);
			holder.tvSmsSendStatus = (TextView) convertView.findViewById(R.id.tvSmsSendStatus);
			holder.tvTimeTitle = (TextView) convertView.findViewById(R.id.tvTimeTitle);
			holder.title = (ViewGroup) convertView.findViewById(R.id.title);
			holder.line1 = convertView.findViewById(R.id.line1);
			holder.line = convertView.findViewById(R.id.line);
			holder.line2 = convertView.findViewById(R.id.line2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		smsRecord = getItem(position);
		// 将数据设置进控件中
		setParams(position);

		holder.item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickListener.itemClickEvent(holder.item, position, mSmsRecords.get(position));
			}
		});

		holder.ivPickUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickListener.updateSign(holder.ivPickUp, position, mSmsRecords.get(position).getInform_id());
			}
		});
		return convertView;
	}

	/**
	 * 设置参数值到控件上去
	 */
	private void setParams(int position) {
		switch (smsRecord.getSigned()) {// 取件状态
			case "0":// 未取件
				holder.ivPickUp.setBackgroundResource(R.drawable.message_notsign_icon);
				break;
			case "1":// 已取件
				holder.ivPickUp.setBackgroundResource(R.drawable.message_signed_icon);
				break;
		}
		holder.redPoint.setVisibility(smsRecord.getCm_nr_flag() > 0 ? View.VISIBLE: View.GONE);
		// 设置编号
		holder.tvNo.setText(!Utility.isEmpty(smsRecord.getExpress_number()) ? smsRecord.getExpress_number() : "");

		// 设置c用户手机号码
		if (Utility.isEmpty(smsRecord.getUser_phone()) || "1**********".equals(smsRecord.getUser_phone())) {
			holder.tvMobilePhone.setText(!Utility.isEmpty(smsRecord.getDh()) ? smsRecord.getDh() : "1**********");
		}else {
			holder.tvMobilePhone.setText(smsRecord.getUser_phone());
		}
		// 设置C用户最后回复内容
		holder.tvCmContent.setText(!Utility.isEmpty(smsRecord.getLast_msg_content()) ? smsRecord.getLast_msg_content() : "");

		// 设置最后更新时间（时：分）
		holder.tvLastUpdateTime.setText(UtilityTime.getDateTimeByMillisecond(smsRecord.getLast_update_time(), UtilityTime.HH_MM));

		// 设置信息发送状态
		String status = smsRecord.getStatus();

		switch (status){
			case "send":// 已发送
				holder.tvSmsSendStatus.setText("已发送");
				holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.status_orange));
				break;
			case "receive":// 已收到
				holder.tvSmsSendStatus.setText("已收到");
				holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.default_green_2));
				break;
			case "readed":// 已查看
				holder.tvSmsSendStatus.setText("已收到");
				holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.default_green_2));
				break;
			case "return":// 已回复
				holder.tvSmsSendStatus.setText("已回复");
				holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.main_color));
				break;
			case "faild":// 发送失败
				holder.tvSmsSendStatus.setText("发送失败");
				holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.red_f74739));
				break;
		}

		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

		// 设置相同时间数据放在一起
		String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(smsRecord.getLast_update_time() * 1000);
		String messageTimeResponse = "";
		if (position != 0) {
			long nextTime;
			nextTime = (mSmsRecords.get(position - 1)).getLast_update_time() * 1000;
			messageTimeResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextTime);
		}

		if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
			holder.tvTimeTitle.setText("今天 ");
		} else if (now.substring(0, 8).equals(updateTime.substring(0, 8))
				&& Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
			holder.tvTimeTitle.setText("昨天 ");
		} else {
			holder.tvTimeTitle.setText(updateTime.substring(0, 10));
		}

		
		if (position != 0 && updateTime.substring(0, 10).equals(messageTimeResponse.substring(0, 10))) {
			holder.line1.setVisibility(View.GONE);
			holder.title.setVisibility(View.GONE);
			holder.line.setVisibility(View.GONE);
		} else {
			holder.line1.setVisibility(View.VISIBLE);
			holder.title.setVisibility(View.VISIBLE);
			holder.line.setVisibility(View.VISIBLE);
		}
		
		if (mSmsRecords.get(position) != null) {
			if (mSmsRecords.size() != position + 1) {
				if (mSmsRecords.get(position + 1) != null) {
					String updateTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mSmsRecords.get(position).getLast_update_time() * 1000);
					String updateTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(mSmsRecords.get(position + 1).getLast_update_time() * 1000);
					if (updateTime1.substring(0, 10).equals(updateTime2.substring(0, 10))) {
						holder.line2.setVisibility(View.VISIBLE);
					} else {
						holder.line2.setVisibility(View.GONE);
					}
				} else {
					holder.line2.setVisibility(View.GONE);
				}
			}
		} else {
			holder.line2.setVisibility(View.GONE);
		}

	}
	

	private class ViewHolder {
		ViewGroup item = null;
		ImageView redPoint = null;// 小红点
		ImageView ivPickUp = null;// 签收/未签收icon
		TextView tvNo = null;// 编号
		TextView tvMobilePhone = null;// 手机号
		TextView tvLastUpdateTime = null;// 最后一次更新时间
		TextView tvCmContent = null;// 最后一条客户回复内容
		TextView tvSmsSendStatus = null;// 消息发送状态
		TextView tvTimeTitle = null;// 时间title
		ViewGroup title;// 显示时间区域
		View line = null;
		View line1 = null;
		View line2 = null;
	}

	public interface OnclickListener {
		/**
		 * @Title: itemClickEvent
		 * @Description: 列表进入详情
		 * @param @param view
		 * @param @param position
		 * @param @param topicId
		 * @return void
		 */
		void itemClickEvent(View view, int position, SmsRecord smsRecord);

		/**
		 * @Title: updateSign
		 * @Description: 签收事件
		 * @param view
		 *            按钮视图
		 * @param position
		 *            点击下标
		 * @return void
		 */
		void updateSign(View view, int position, String informId);
	}

}
