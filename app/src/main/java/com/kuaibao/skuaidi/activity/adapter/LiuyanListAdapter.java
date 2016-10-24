package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MessageList;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.Utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

@SuppressLint({ "SimpleDateFormat", "UseSparseArrays" })
public class LiuyanListAdapter extends BaseAdapter {
	Context context;

	List<MessageList> messages;
	LayoutInflater inflater;
	String messagetime;
	String messageTimeResponse;

	Set<String> messageSet;
	String deleteIds = "";
	MessageList message;
	boolean ck_flag = false;
	boolean select_flag = false;
	int selectCount = 0;
	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

	public LiuyanListAdapter(Context context, List<MessageList> messages) {
		this.context = context;
		this.messages = messages;

		inflater = LayoutInflater.from(context);
		message = new MessageList();
	}

	public LiuyanListAdapter(Context context, List<MessageList> messages, boolean select_flag) {
		this.context = context;
		this.messages = messages;
		this.select_flag = select_flag;

		inflater = LayoutInflater.from(context);
		message = new MessageList();
	}

	public void show_checkbox() {
		ck_flag = true;
		messageSet = new HashSet<String>();
		deleteIds = "";
		this.notifyDataSetChanged();
		initSelected();
	}

	private void initSelected() {
		for (int i = 0; i < getCount(); i++) {
			map.put(i, false);
		}
	}

	public void hide_checkbox() {
		ck_flag = false;
		messageSet = null;
		deleteIds = "";
		this.notifyDataSetChanged();
	}

	public String deletemessages() {
		deleteIds = "";
		for (String targetId : messageSet) {
			if (!deleteIds.equals("")) {
				deleteIds += "," + targetId;
			} else {
				deleteIds += targetId;
			}
		}

		return deleteIds;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public MessageList getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public void clearData() {
		messages.clear();
		notifyDataSetChanged();
	}
	
	public List<MessageList> getList(){
		return messages;
	}
	ViewHolder holder = null;
	Timer timer = null;

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem_exception_center, null);
			holder.tv_order_type = (TextView) convertView.findViewById(R.id.tv_order_type);
			holder.tv_order_no = (TextView) convertView.findViewById(R.id.tv_order_no);
			holder.iv_multiselec_icon = (ImageView) convertView.findViewById(R.id.iv_multiselec_icon);
			holder.tv_txt_content = (TextView) convertView.findViewById(R.id.tv_txt_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_dispatch_type = (TextView) convertView.findViewById(R.id.tv_dispatch_type);
			holder.ck_delete = (CheckBox) convertView.findViewById(R.id.ckbox_delete);
			holder.imv_icon_jiant = (ImageView) convertView.findViewById(R.id.imv_icon_jiant);
			holder.tv_isnew = (ImageView) convertView.findViewById(R.id.tv_isnew);
			holder.img_order_icon = (ImageView) convertView.findViewById(R.id.img_order_icon);
			holder.tv_time_title = (TextView) convertView.findViewById(R.id.tv_time_title);
			holder.view_divider = convertView.findViewById(R.id.view_divider);

			holder.view_divider1 = convertView.findViewById(R.id.view_divider1);
			holder.center_view = convertView.findViewById(R.id.center_view);
			holder.recording_center_view = convertView.findViewById(R.id.recording_center_view);

			holder.playBtn = (TextView) convertView.findViewById(R.id.playorpause);
			holder.recordingCusPhoneNum = (TextView) convertView.findViewById(R.id.recording_cus_phonenum);
			holder.recordingDate = (TextView) convertView.findViewById(R.id.recording_date);
			holder.recordingDuration = (TextView) convertView.findViewById(R.id.recording_duration);
			holder.recordingPlayingDuration = (TextView) convertView.findViewById(R.id.recording_palying_duration);
			holder.bottom_margin = convertView.findViewById(R.id.bottom_margin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.recording_center_view.setVisibility(View.GONE);
		holder.view_divider1.setVisibility(View.GONE);
		holder.bottom_margin.setVisibility(View.GONE);
		holder.center_view.setVisibility(View.VISIBLE);
		if (messages != null && 0 != messages.size()) {

			message = messages.get(position);
			
			if("branch".equals(message.getM_type())){
				holder.img_order_icon.setImageResource(R.drawable.icon_branch);
				holder.tv_order_type.setText(StringUtil.isEmpty(message.getWaybill_type())+"：");
				if("Nul".equals(message.getHas_assign())){
					holder.tv_dispatch_type.setText("");
				}else if("0".equals(message.getHas_assign())){
					holder.tv_dispatch_type.setTextColor(context.getResources().getColor(R.color.red_f74739));
					holder.tv_dispatch_type.setText("未分配");
				}else{
					holder.tv_dispatch_type.setTextColor(context.getResources().getColor(R.color.default_green_2));
					holder.tv_dispatch_type.setText("已分配");
				}
//				if("inbox".equals(message.getTopic_cate())){
//					if("0".equals(message.getHas_assign())){
//						holder.tv_dispatch_type.setText("未分配");
//					}else{
//						holder.tv_dispatch_type.setText("已分配");
//					}
//				}else {
//					holder.tv_dispatch_type.setVisibility(View.GONE);
//				}
			}else if("order".equals(message.getM_type())){
				holder.img_order_icon.setImageResource(R.drawable.icon_order);
				holder.tv_order_type.setText(StringUtil.isEmpty(message.getWaybill_type())+"：");
				holder.tv_dispatch_type.setText("");
			}else if("customer".equals(message.getM_type())){
				holder.img_order_icon.setImageResource(R.drawable.icon_customer);
				holder.tv_order_type.setText(StringUtil.isEmpty(message.getWaybill_type())+"：");
				holder.tv_dispatch_type.setText("");
			}else if("problem".equals(message.getM_type())){
				holder.img_order_icon.setImageResource(R.drawable.icon_branch);
				holder.tv_order_type.setText("");
				if("1".equals(message.getIs_reply())) {
					holder.tv_dispatch_type.setText("已回复");
				}else{
					holder.tv_dispatch_type.setText("");
				}
			}
			
			if (message.getTotal_unread() > 0) {
				holder.tv_isnew.setVisibility(View.VISIBLE);//未读
			} else {
				holder.tv_isnew.setVisibility(View.GONE);
			}

			// 批量删除时视图改变
			if (ck_flag) {
				holder.ck_delete.setVisibility(View.VISIBLE);
				holder.imv_icon_jiant.setVisibility(View.GONE);
				holder.ck_delete.setChecked(map.get(position));
			} else {
				holder.ck_delete.setVisibility(View.GONE);
				//又不要箭头了
				holder.imv_icon_jiant.setVisibility(View.GONE);
			}
			//条目可选时视图改变
			if(select_flag){
				holder.iv_multiselec_icon.setVisibility(View.VISIBLE);
				if(message.isSelected()){
					holder.iv_multiselec_icon.setImageResource(R.drawable.batch_add_checked);
				}else{
					holder.iv_multiselec_icon.setImageResource(R.drawable.select_edit_identity);
				}
			}
			holder.tv_order_no.setText((TextUtils.isEmpty(message.getWaybill_no())?"无运单号":message.getWaybill_no()));
			if("audio".equals(message.getLast_reply_type())){
				holder.tv_txt_content.setText("语音");
			}else if("image".equals(message.getLast_reply_type())){
				holder.tv_txt_content.setText("图片");
			}else if("problem".equals(message.getM_type())){
				holder.tv_txt_content.setText(StringUtil.isEmpty(message.getWaybill_type()));
			}else{
				holder.tv_txt_content.setText(message.getLast_reply());
			}

			messagetime = message.getUpdate_date();
			if (position != 0) {
				messageTimeResponse = messages.get(position - 1).getUpdate_date();
			}
			if(!Utility.isEmpty(messagetime) && message.getUpdate_time().length()>=6){
				holder.tv_time.setText(message.getUpdate_time().substring(0, 5));
			}else{
				messagetime = "--";
			}
			
			if(TextUtils.isEmpty(messagetime)){
				 holder.tv_time_title.setVisibility(View.GONE);
			}else{
				holder.tv_time_title.setVisibility(View.VISIBLE);
				//System.out.println("messagetime=================="+messagetime);
				if("2015-10-30".equals(messagetime)){
					Utility.setTimeDate1(messagetime, holder.tv_time_title);
				}
				Utility.setTimeDate1(messagetime, holder.tv_time_title);
			}
			// 时间一起的留言放在一起~
			if (position != 0 && messagetime.equals(messageTimeResponse)) {
				holder.tv_time_title.setVisibility(View.GONE);
				holder.view_divider.setVisibility(View.GONE);
			} else {
				holder.tv_time_title.setVisibility(View.VISIBLE);
				holder.view_divider.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public void notifyDataSetChanged(List<MessageList> messages) {
		this.messages = messages;
		super.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageView imv_order_icon;// 标识用户头像
		TextView tv_order_type;
		TextView tv_order_no;
		TextView tv_txt_content;
		TextView tv_time;
		TextView tv_dispatch_type;
		CheckBox ck_delete;
		ImageView imv_icon_jiant;
		ImageView tv_isnew;
		ImageView img_order_icon;
		ImageView iv_multiselec_icon;
		TextView tv_time_title;
		View view_divider;
		View center_view;
		View view_divider1, bottom_margin;
		View recording_center_view;

		TextView recordingCusPhoneNum, recordingDate, recordingPlayingDuration, recordingDuration;
		TextView playBtn;

	}

}
