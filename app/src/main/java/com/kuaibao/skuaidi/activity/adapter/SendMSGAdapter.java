package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SendMSGAdapter extends BaseAdapter {

	private Context mContext = null;
	private ViewHolder holder = null;
	private setButtonClick buttonClick = null;
	private setButOnLongClick butOnLongClick = null;
	private List<NotifyInfo2> infos = null;
	private int count = 1;
	private boolean isMaxCount = false;// 显示全部数据
	
	public SendMSGAdapter(Context context,List<NotifyInfo2> info2s,setButtonClick buttonClick,setButOnLongClick butOnLongClick){
		this.mContext = context;
		this.infos = info2s;
		this.buttonClick = buttonClick;
		this.butOnLongClick = butOnLongClick;
	}
	
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public NotifyInfo2 getItem(int position) {
		if(Utility.isEmpty(infos)){
			infos = new ArrayList<>();
		}
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/** 获取列表中的数据 **/
	public List<NotifyInfo2> getListData(){
		return infos;
	}
	
	/**设置列表最少显示1条**/
	public void setMinCount(){
		this.count = 1;
		isMaxCount = false;
		notifyDataSetChanged();
	}
	/**设置指定条数**/
	public void setPhoneNumberCount(int count){
		this.count = count;
		isMaxCount = false;// 不显示单号
		notifyDataSetChanged();
	}
	/** 是否显示全部 **/
	public boolean isShowAll(){
		return isMaxCount;
	}
	
	/**设置列表最大显示200条
	 * isCustomSize : 自定义列表最大长度
	 * count : 列表长度 **/
	public void setMaxCount(int count){
		this.count = count;
		isMaxCount = true;// 显示单号
		notifyDataSetChanged();
	}
	/**设置列表显示指定条目数**/
	public void setItemCount(int count){
		this.count = count;
		notifyDataSetChanged();
	}
	/**设置适配器数据**/
	public void setAdapterData(List<NotifyInfo2> infos){
		this.infos = infos;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sendmsg_adapter_item, parent,false);
			holder.rawLine = (LinearLayout) convertView.findViewById(R.id.rawLine);
			holder.tv_NoTag = (TextView) convertView.findViewById(R.id.tv_NoTag);
			holder.tv_No = (TextView) convertView.findViewById(R.id.tv_No);
			holder.rl_No = (RelativeLayout) convertView.findViewById(R.id.rl_No);
			holder.tv_PhoneNo = (TextView) convertView.findViewById(R.id.tv_PhoneNo);
			holder.rl_PhoneNo = (RelativeLayout) convertView.findViewById(R.id.rl_PhoneNo);
			holder.tv_OrderNo = (TextView) convertView.findViewById(R.id.tv_OrderNo);
			holder.ll_OrderNo = (RelativeLayout) convertView.findViewById(R.id.ll_OrderNo);
			holder.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
			holder.iv_delete_icon = (ImageView) convertView.findViewById(R.id.iv_delete_icon);
			holder.anim_play_audio = (ImageView) convertView.findViewById(R.id.anim_play_audio);
			holder.rlOrderNo = (ViewGroup) convertView.findViewById(R.id.rlOrderNo);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position == getCount()-1){
			holder.rawLine.setVisibility(View.GONE);
		}else{
			holder.rawLine.setVisibility(View.VISIBLE);
			holder.rawLine.setPadding(20, 0, 20, 0);
		}
		
		if(isMaxCount){// 如果显示全部
			holder.tv_NoTag.setVisibility(View.GONE);
			holder.ll_delete.setVisibility(View.GONE);
			holder.rlOrderNo.setVisibility(View.VISIBLE);
		}else{
			holder.tv_NoTag.setVisibility(View.VISIBLE);
			holder.rlOrderNo.setVisibility(View.GONE);
			holder.ll_delete.setVisibility(View.VISIBLE);
			if((!Utility.isEmpty(getItem(position).getSender_mobile())&& getItem(position).isPlayVoiceAnim())|| getItem(position).isPlayVoiceAnim()){
				holder.anim_play_audio.setBackgroundResource(R.drawable.anim_audio_play);
//				Timer animationTimer = new Timer();
				AnimationDrawable ad = (AnimationDrawable) holder.anim_play_audio.getBackground();
				ad.start();
				
				holder.anim_play_audio.setVisibility(View.VISIBLE);
				holder.iv_delete_icon.setVisibility(View.GONE);
			}else{
				holder.anim_play_audio.setBackgroundResource(R.drawable.icon_cloud_call_voice_active_1);
				if (!Utility.isEmpty(getItem(position).getSender_mobile())) {
					holder.iv_delete_icon.setVisibility(View.VISIBLE);
					holder.anim_play_audio.setVisibility(View.GONE);
				}else{
					holder.iv_delete_icon.setVisibility(View.GONE);
					holder.anim_play_audio.setVisibility(View.VISIBLE);
				}
			}
		}
		holder.tv_No.setText(getItem(position).getExpressNo());// 设置编号 
		holder.tv_OrderNo.setText(getItem(position).getExpress_number());// 设置运单号
		String mobilePhone = getItem(position).getSender_mobile().replaceAll(" ", "");
		if(mobilePhone.length() == 11){// 设置手机号
			holder.tv_PhoneNo.setTextColor(mContext.getResources().getColor(R.color.gray_1));
			if(mobilePhone.substring(0, 1).equals("1")){
				mobilePhone = mobilePhone.substring(0, 3)+"-"+mobilePhone.substring(3, 7)+"-"+mobilePhone.substring(7);
				holder.tv_PhoneNo.setText(mobilePhone);
				/*SpannableString ss = new SpannableString(mobilePhone);
				List<Integer> notDigitList = StringUtil.getNotNumberList(mobilePhone);
				for (int i = 0;i<notDigitList.size();i++){
					int index = notDigitList.get(i);
					ss.setSpan(new AbsoluteSizeSpan(18,true), index, index+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素。
				}
				holder.tv_PhoneNo.setText(ss);*/
			}else{
				holder.tv_PhoneNo.setText(mobilePhone);
				holder.tv_PhoneNo.setTextColor(mContext.getResources().getColor(R.color.red));
			}
		}else {
			holder.tv_PhoneNo.setText(mobilePhone);
			holder.tv_PhoneNo.setTextColor(mContext.getResources().getColor(R.color.red));
		}
		
		holder.rl_No.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonClick.modifyNo(v, position,infos);
			}
		});
		holder.rl_PhoneNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonClick.addPhoneNumber(v, position);
			}
		});
		holder.ll_OrderNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonClick.addOrderNo(v, position,getItem(position).getExpressNo(),infos);
			}
		});
		holder.ll_OrderNo.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(!Utility.isEmpty(getItem(position).getExpress_number())){
					butOnLongClick.showOrder(v, position, getItem(position).getExpress_number());
				}
				return false;
			}
		});
		
		holder.iv_delete_icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonClick.deletePhoneAndOrderNo(v, position,getItem(position));
			}
		});
		
		holder.anim_play_audio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonClick.playAudio(v, position);
			}
		});
		
		return convertView;
	}
	
	class ViewHolder{
		LinearLayout rawLine;// 线条
		TextView tv_NoTag;// 编号文字
		TextView tv_No;// 编号
		RelativeLayout rl_No;// 点击修改编号控件
		TextView tv_PhoneNo;// 手机号
		RelativeLayout rl_PhoneNo;// 修改手机号控件
		TextView tv_OrderNo;// 单号
		RelativeLayout ll_OrderNo;// 修改单号控件
		LinearLayout ll_delete;// 删除部分
		ImageView iv_delete_icon;// 删除按钮
		ImageView anim_play_audio;// 号码识别动画
		ViewGroup rlOrderNo = null;//单号部分 
	}
	
	public  interface setButtonClick{
		/**修改编号**/
		void modifyNo(View v,int position,List<NotifyInfo2> notifyInfo2s);
		/**添加手机号码**/
		void addPhoneNumber(View v,int position);
		/**删除当前手机号和运单号**/
		void deletePhoneAndOrderNo(View v,int position,NotifyInfo2 notifyInfo2);
		/**添加运单号**/
		void addOrderNo(View v,int position,String expressNo,List<NotifyInfo2> infos);
		/** 开始录音按钮 **/
		void playAudio(View v,int position);
	}
	
	public interface setButOnLongClick{
		void showOrder(View v,int position,String orderStr);
	}

	

}
