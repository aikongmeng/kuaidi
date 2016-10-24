package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.sto.etrhee.activity.E3ProofSMSDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class E3ProofSMSDetailAdapter extends BaseAdapter {

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Context context;
	private List<Message> SMSes;
	private String dataFrom;// 数据源
	public boolean selectData = false;
	public TextView receiveTime, contentReceive, contentSend;
	public CheckBox cb_check_receive, cb_check_send, cb_temp;
	public View receive, send;

	public E3ProofSMSDetailAdapter(Context context, List<Message> SMSes, String dataFrom) {
		this.context = context;
		this.SMSes = SMSes;
		this.dataFrom = dataFrom;
	}

	@Override
	public int getCount() {
		return SMSes.size();
	}

	@Override
	public Message getItem(int arg0) {
		return SMSes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(context).inflate(R.layout.e3_proof_sms_detail_item, null);

		receiveTime = (TextView) convertView.findViewById(R.id.sms_tv_time_receive);
		contentReceive = (TextView) convertView.findViewById(R.id.sms_tv_content_receive);
		contentSend = (TextView) convertView.findViewById(R.id.sms_tv_content_send);
		receive = convertView.findViewById(R.id.sms_rl_receive);
		send = convertView.findViewById(R.id.sms_rl_send);
		cb_check_receive = (CheckBox) convertView.findViewById(R.id.cb_check_receive);
		cb_check_send = (CheckBox) convertView.findViewById(R.id.cb_check_send);

		if (E3ProofSMSDetailActivity.DATA_FROM_E3_PROOF_ACTIVITY.equals(dataFrom) && !selectData) {
			cb_check_receive.setVisibility(View.GONE);
			cb_check_send.setVisibility(View.GONE);

		} else {
			cb_check_receive.setVisibility(View.VISIBLE);
			cb_check_send.setVisibility(View.VISIBLE);
		}
		String messagetime = format.format(getItem(arg0).getMessageDate());

		String flag = parseDate(getItem(arg0).getMessageDate());

		if (flag.equals("今天")) {
			receiveTime.setText("今天 " + messagetime.substring(10, 16));
		} else if (flag.equals("昨天")) {
			receiveTime.setText("昨天 " + messagetime.substring(10, 16));
		} else {
			receiveTime.setText(messagetime.substring(0, 16));
		}

		if (getItem(arg0).getMessageType() == 1) {
			contentReceive.setText(getItem(arg0).getMessageContent());
			send.setVisibility(View.GONE);
			receive.setVisibility(View.VISIBLE);
			if (getItem(arg0).isSelected()) {
				cb_check_receive.setChecked(true);
			} else {
				cb_check_receive.setChecked(false);
			}
			receiveTime.setVisibility(View.VISIBLE);
		} else {
			contentSend.setText(getItem(arg0).getMessageContent());
			send.setVisibility(View.VISIBLE);
			receive.setVisibility(View.GONE);
			receiveTime.setVisibility(View.VISIBLE);
			if (getItem(arg0).isSelected()) {
				cb_check_send.setSelected(true);
			} else {
				cb_check_send.setSelected(false);
			}
		}

		return convertView;
	}

	private String parseDate(long createTime) {
		try {
			String ret = "";
			Calendar now = Calendar.getInstance();
			long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now
					.get(Calendar.SECOND));// 毫秒数
			long ms_now = now.getTimeInMillis();
			if (ms_now - createTime < ms) {
				ret = "今天";
			} else if (ms_now - createTime < (ms + 24 * 3600 * 1000)) {
				ret = "昨天";
			} else if (ms_now - createTime < (ms + 24 * 3600 * 1000 * 2)) {
				ret = "前天";
			} else {
				ret = "更早";
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
