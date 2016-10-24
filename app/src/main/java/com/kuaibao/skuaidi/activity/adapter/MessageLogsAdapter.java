package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MycustomAddActivity;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.MyCustom;

import net.tsz.afinal.FinalDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * 
 * 短信列表适配器
 * 
 * @author xy
 * 
 */
@SuppressLint("SimpleDateFormat")
public class MessageLogsAdapter extends BaseAdapter {
	private FinalDb db = (FinalDb) SKuaidiApplication.getInstance().getFinalDbCache();
	private List<Message> messageLogs;
	private Context context;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public MessageLogsAdapter(Context context, List<Message> messageLogs) {
		this.context = context;
		this.messageLogs = messageLogs;
	}

	@Override
	public int getCount() {
		return messageLogs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return messageLogs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.message_logs_item, null);
			holder = new ViewHolder();
			holder.personName = (TextView) convertView
					.findViewById(R.id.person_name);
			holder.messageContent = (TextView) convertView
					.findViewById(R.id.message_content);
			holder.click_mAddOrExamine = (TextView) convertView
					.findViewById(R.id.click_mAddOrExamine);
			holder.messageDate = (TextView) convertView
					.findViewById(R.id.message_date);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String phoneNumber = ((Message)getItem(arg0)).getPersonPhoneNumber();
		String phoneNum = phoneNumber.length()>=11?phoneNumber.substring(phoneNumber.length()-11, phoneNumber.length()):phoneNumber;
		String personName = ((Message)getItem(arg0)).getPersonName();
		final Intent intent = new Intent(context, MycustomAddActivity.class);
		MyCustom myCustom = new MyCustom();
		final Bundle bundle = new Bundle();
		
		
		holder.messageDate.setText(sdf.format(new Date(((Message) getItem(arg0))
				.getMessageDate())));
		Resources resource = context.getResources();
		ColorStateList csl = null;
		
		if(db.findAllByWhere(MyCustom.class, "phone like '%" + phoneNum + "%'") != null
				&& db.findAllByWhere(MyCustom.class,
						"phone like '%" + phoneNum + "%'").size() > 0){
			myCustom = db.findAllByWhere(MyCustom.class, "phone like '%" + phoneNum + "%'").get(0);
			personName = myCustom.getName();
			bundle.putSerializable("mycustom", myCustom);
			holder.click_mAddOrExamine.setText("查看客户");
			csl = resource.getColorStateList(R.color.green_message_bg);
			holder.click_mAddOrExamine.setTextColor(csl);
			holder.click_mAddOrExamine
					.setBackgroundResource(R.drawable.btn_bg_example);
			holder.click_mAddOrExamine
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					intent.putExtra("type", "get");
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		}else{
			holder.click_mAddOrExamine.setText("添加客户");
			csl = resource.getColorStateList(R.color.white);
			holder.click_mAddOrExamine.setTextColor(csl);
			myCustom.setName(personName);
			myCustom.setPhone(phoneNumber);
			bundle.putSerializable("mycustom", myCustom);
			holder.click_mAddOrExamine
			.setBackgroundResource(R.drawable.btn_bg_add);
			holder.click_mAddOrExamine
			.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					intent.putExtra("type", "addFromMessageLogs");
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		}
		holder.personName.setText(personName);
		holder.messageContent.setText(((Message) getItem(arg0)).getMessageContent());
		

		return convertView;
	}
	
	class ViewHolder {
		TextView personName, messageContent, click_mAddOrExamine,messageDate;
				
	}

}
