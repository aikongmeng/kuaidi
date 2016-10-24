package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.text.SimpleDateFormat;
import java.util.List;

public class SmsRecordDraftAdapter extends BaseAdapter {

	private Context mContext = null;
//	private ItemOnclickListener itemOnclickListener = null;
	private List<DraftBoxSmsInfo> draftBoxInfos = null;
	private String DH = "#DHDHDHDHDH#";
	private String SURL = "#SURLSURLSURLSURLS#";

	public SmsRecordDraftAdapter(Context context, List<DraftBoxSmsInfo> draftBoxInfos) {
		this.mContext = context;
//		this.itemOnclickListener = itemOnclickListener;
		this.draftBoxInfos = draftBoxInfos;
	}

	@Override
	public int getCount() {
		return draftBoxInfos.size();
	}

	@Override
	public DraftBoxSmsInfo getItem(int arg0) {
		return draftBoxInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void setData(List<DraftBoxSmsInfo> draftInfos){
		this.draftBoxInfos = draftInfos;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.record_draft_box_item, null);
			holder.rlDraftItem = (ViewGroup) arg1.findViewById(R.id.rlDraftItem);
			holder.tvTimeTitle = (TextView) arg1.findViewById(R.id.tvTimeTitle);
			holder.line = arg1.findViewById(R.id.line);
			holder.tvPhoneNum = (TextView) arg1.findViewById(R.id.tvPhoneNum);
			holder.tvSaveTime = (TextView) arg1.findViewById(R.id.tvSaveTime);
			holder.tvSaveContext = (TextView) arg1.findViewById(R.id.tvSaveContext);
			holder.timingTag = (TextView) arg1.findViewById(R.id.timingTag);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}

		String phoneNumber = getItem(arg0).getPhoneNumber();
		long saveTime = getItem(arg0).getDraftSaveTime();
		String saveContent = getItem(arg0).getSmsContent();
		String timingTag = getItem(arg0).getTimingTag();
		// 定时发送的条目用到
		String last_upadte_time = getItem(arg0).getLastUpdateTime();
		
		
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

		if(!Utility.isEmpty(timingTag) && "y".equals(timingTag)){
			String messageTimeResponse = "";
			if(arg0 != 0){// 如果不是第一条数据，则查找后一条数据
				messageTimeResponse = getItem(arg0-1).getLastUpdateTime();
			}
			
			if (now.substring(0, 10).equals(last_upadte_time.substring(0, 10))) {
				holder.tvTimeTitle.setText("今天 ");
			} else if (now.substring(0, 8).equals(last_upadte_time.substring(0, 8)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(last_upadte_time.substring(8, 10)) == 1) {
				holder.tvTimeTitle.setText("昨天 ");
			} else {
				holder.tvTimeTitle.setText(last_upadte_time.substring(0, 10));
			}
			if (arg0 != 0 && last_upadte_time.substring(0, 10).equals(messageTimeResponse.substring(0, 10))) {
				holder.tvTimeTitle.setVisibility(View.GONE);
				holder.line.setVisibility(View.GONE);
			} else {
				holder.tvTimeTitle.setVisibility(View.VISIBLE);
				holder.line.setVisibility(View.VISIBLE);
			}
			
			holder.tvPhoneNum.setText(phoneNumber);
			holder.tvSaveTime.setText(last_upadte_time.substring(11, 16));
			holder.timingTag.setVisibility(View.VISIBLE);
		}else{
			if (saveTime != 0) {
				// 设置相同时间数据放在一起
				String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(saveTime);
				String messageTimeResponse = "";
				if (arg0 != 0) {
					String timing_tag = getItem(arg0-1).getTimingTag();
					if(!Utility.isEmpty(timing_tag) && timing_tag.equals("y")){
						messageTimeResponse = getItem(arg0-1).getLastUpdateTime();
					}else{
						long nextTime;
						nextTime = (draftBoxInfos.get(arg0 - 1)).getDraftSaveTime();
						messageTimeResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextTime);
					}
					
				}

				if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
					holder.tvTimeTitle.setText("今天 ");
				} else if (now.substring(0, 8).equals(updateTime.substring(0, 8)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
					holder.tvTimeTitle.setText("昨天 ");
				} else {
					holder.tvTimeTitle.setText(updateTime.substring(0, 10));
				}

				if (arg0 != 0 && updateTime.substring(0, 10).equals(messageTimeResponse.substring(0, 10))) {
					holder.tvTimeTitle.setVisibility(View.GONE);
					holder.line.setVisibility(View.GONE);
				} else {
					holder.tvTimeTitle.setVisibility(View.VISIBLE);
					holder.line.setVisibility(View.VISIBLE);
				}
			}else{
				holder.tvTimeTitle.setVisibility(View.GONE);
				holder.line.setVisibility(View.GONE);
			}
			holder.timingTag.setVisibility(View.INVISIBLE);
			holder.tvPhoneNum.setText(arrPhoneNumberToStr(phoneNumberToArr(phoneNumber)));
			holder.tvSaveTime.setText(UtilityTime.getDateTimeByMillisecond2(saveTime, UtilityTime.HH_MM));
		}
		
		
		if(Utility.isEmpty(saveContent)){
			holder.tvSaveContext.setText("无短信内容");
		}else{
			TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(mContext);
			holder.tvSaveContext.setText(mTextInsertImgParser.replace(replaceText(saveContent)));
		}
		return arg1;
	}
	
	/** 替换模板 **/
	private String replaceText(String content){
		String context = content;
		if(content.contains("#DH#")){
			context = context.replaceAll("#DH#", DH);
		}
		if(context.contains("#SURL#")){
			context = context.replaceAll("#SURL#", SURL);
		}
		return context;
	}
	
	public String[] phoneNumberToArr(String phoneNumber){
		return phoneNumber.split(",");
	}
	
	public String[] phoneNumberToArr2(String phoneNumber){
		return phoneNumber.split("、");
	}
	
	public String arrPhoneNumberToStr(String[] phoneNumber){
		String showPhoneNumber = "";
		
		for(int i = 0;i<phoneNumber.length;i++){
			if(!Utility.isEmpty(phoneNumber[i]) && !phoneNumber[i].trim().equals("")){
				showPhoneNumber += phoneNumber[i]+"、";
			}else{
				continue;
			}
		}
		
		String[] newPhoneNumber = phoneNumberToArr2(showPhoneNumber);
		
		if(newPhoneNumber.length == 0){
			return "";
		}else if(newPhoneNumber.length == 1){
			return newPhoneNumber[0];
		}else if(newPhoneNumber.length ==2){
			return newPhoneNumber[0]+"、"+newPhoneNumber[1];
		}else{
			return newPhoneNumber[0]+"、"+newPhoneNumber[1]+"等"+newPhoneNumber.length+"个号码";
		}
	}

	class ViewHolder {
		ViewGroup rlDraftItem = null;// 草稿条目
		TextView tvTimeTitle = null;// time title
		View line = null;// 线条
		TextView tvPhoneNum = null;// 手机号码显示
		TextView tvSaveTime = null;// 时间显示
		TextView tvSaveContext = null;// 短信内容
		TextView timingTag = null;
	}

}
