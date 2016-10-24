package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class SMSDetailAdapter extends BaseAdapter {

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Context context;
	private List<Message> SMSes;

	public SMSDetailAdapter(Context context, List<Message> SMSes) {
		this.context = context;
		this.SMSes = SMSes;
	}

	@Override
	public int getCount() {
		return SMSes.size();
	}

	@Override
	public Object getItem(int arg0) {
		return SMSes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.sms_detail_layout_item, null);

			holder = new ViewHolder();
			holder.receiveTime = (TextView) convertView
					.findViewById(R.id.sms_tv_time_receive);
			holder.contentReceive = (TextView) convertView
					.findViewById(R.id.sms_tv_content_receive);
			holder.contentSend = (TextView) convertView
					.findViewById(R.id.sms_tv_content_send);
			holder.headerReceive = (ImageView) convertView
					.findViewById(R.id.sms_imv_header_receive);
			holder.headerSend = (ImageView) convertView
					.findViewById(R.id.sms_imv_header_send);
			holder.receive = (RelativeLayout) convertView
					.findViewById(R.id.sms_rl_receive);
			holder.send = (RelativeLayout) convertView
					.findViewById(R.id.sms_rl_send);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();

		}

		String messagetime = format.format(((Message) getItem(arg0))
				.getMessageDate());
		
		String flag = parseDate(((Message) getItem(arg0))
				.getMessageDate());
		
		if (flag.equals("今天")) {
			holder.receiveTime.setText("今天 " + messagetime.substring(10, 16));
		} else if (flag.equals("昨天")) {
			holder.receiveTime.setText("昨天 " + messagetime.substring(10, 16));
		} else {
			holder.receiveTime.setText(messagetime.substring(0, 16));
		}

		if (((Message) getItem(arg0)).getMessageType() == 1) {
			holder.contentReceive.setText(((Message) getItem(arg0))
					.getMessageContent());
			holder.send.setVisibility(View.GONE);
			holder.receive.setVisibility(View.VISIBLE);
			holder.receiveTime.setVisibility(View.VISIBLE);
		} else {
			holder.contentSend.setText(((Message) getItem(arg0))
					.getMessageContent());
			holder.send.setVisibility(View.VISIBLE);
			holder.receive.setVisibility(View.GONE);
			holder.receiveTime.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	class ViewHolder {
		TextView receiveTime, contentReceive, contentSend;
		ImageView headerReceive, headerSend;
		RelativeLayout receive, send;
	}
	private String parseDate(long createTime){    
        try {
            String ret = "";
            Calendar now = Calendar.getInstance();
            long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            if(ms_now-createTime<ms){
                ret = "今天";
            }else if(ms_now-createTime<(ms+24*3600*1000)){
                ret = "昨天";
            }else if(ms_now-createTime<(ms+24*3600*1000*2)){
                ret = "前天";
            }else{
                ret = "更早";
            }
            return ret;
            } catch (Exception e) {
            e.printStackTrace();
            }
        return null;
    }
}
