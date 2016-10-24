package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/** 短信举证适配器 */
@SuppressLint("SimpleDateFormat")
public class E3ProofSMAdapter extends BaseAdapter {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Context context;
	private final List<Message> infos;

	public E3ProofSMAdapter(Context context, List<Message> infos) {
		this.context = context;
		this.infos = infos;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.sms_proof_item, null);
			holder.tv_phone_number = (TextView) convertView.findViewById(R.id.tv_phone_number);
			holder.tv_message_lately = (TextView) convertView.findViewById(R.id.tv_message_lately);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_message_lately.setText(infos.get(position).getMessageContent());
		String name = infos.get(position).getPersonName();
		if (TextUtils.isEmpty(name)) {
			holder.tv_phone_number.setText(infos.get(position).getPersonPhoneNumber());
		} else {
			holder.tv_phone_number.setText(name);
		}
		String messagetime = format.format(((Message) getItem(position)).getMessageDate());

		String flag = parseDate(((Message) getItem(position)).getMessageDate());

		if (flag.equals("今天")) {
			holder.tv_time.setText("今天 " + messagetime.substring(10, 16));
		} else if (flag.equals("昨天")) {
			holder.tv_time.setText("昨天 " + messagetime.substring(10, 16));
		} else {
			holder.tv_time.setText(messagetime.substring(0, 16));
		}

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_phone_number, tv_message_lately, tv_time;
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
