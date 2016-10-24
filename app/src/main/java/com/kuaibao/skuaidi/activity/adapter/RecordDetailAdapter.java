package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.RecordDetail;
import com.kuaibao.skuaidi.service.DownloadTask;
import com.kuaibao.skuaidi.util.Utility;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecordDetailAdapter extends BaseAdapter {
	private final int TYPE_ITEM_FIRST = 1;
	private final int TYPE_ITEM_SECOND = 2;

	private Context mContext = null;
	private HolderFirst holderFirst = null;
	private HolderSecond holderSecond = null;
	private MediaPlayer mPlayer = null;
	private OnButtonClickListener onButtonClickListener = null;// 回调接口
	private List<RecordDetail> recordDetails = null;
	private RecordDetail recordDetail = null;

	// *****************参数变量****************
	private String mobilePhone = "";// 手机号
	private String expressNumber = "";// 运单号
	private String cmName = "";// 快递员姓名
	private String signedTime = "";// 签收时间

	/**
	 * 顾冬冬
	 * @param context
	 * @param mobilePhone
	 *            :手机号码
	 * @param expressNumber
	 *            ：运单号码
	 * @param onButtonClickListener
	 *            ：回调接口
	 */
	public RecordDetailAdapter(Context context, String mobilePhone, String expressNumber,String signedTime, List<RecordDetail> recordDetails,
			OnButtonClickListener onButtonClickListener) {
		this.mContext = context;
		this.onButtonClickListener = onButtonClickListener;
		this.mobilePhone = mobilePhone;
		this.expressNumber = expressNumber;
		this.recordDetails = recordDetails;
		this.signedTime = signedTime;
	}

	@Override
	public int getCount() {
		return recordDetails.size() + 1;
	}

	@Override
	public RecordDetail getItem(int position) {
		if (0 == position) {
			return recordDetail;
		} else {
			return recordDetails.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	/**
	 * 设置数据
	 */
	public void setRecordDetail(List<RecordDetail> recordDetails) {
		this.recordDetails = recordDetails;
		notifyDataSetChanged();
	}

	/**
	 * 设置快递员姓名
	 */
	public void setCmName(String cmName,String signedTime) {
		this.cmName = cmName;
		this.signedTime = signedTime;
		notifyDataSetChanged();
	}

	/** 设置参数值（包括快递员姓名，单号，手机号，列表内容） **/
	public void setData(String cmName, String dh, String phoneNumber,String signedTime) {
		this.cmName = cmName;
		this.expressNumber = dh;
		this.mobilePhone = phoneNumber;
		this.signedTime = signedTime;
		notifyDataSetChanged();
	}

	/** 设置快递单号 **/
	public void setExpressNumber(String expressNumber,String mobilePhone) {
		this.expressNumber = expressNumber;
		this.mobilePhone = mobilePhone;
		notifyDataSetChanged();
	}

	/**
	 * 获取列表中的数据
	 */
	public List<RecordDetail> getRecordDetail() {
		return recordDetails;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_ITEM_FIRST;
		} else {
			return TYPE_ITEM_SECOND;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holderFirst = null;
		holderSecond = null;

		int type = getItemViewType(position);

		if (convertView == null) {
			switch (type) {
			case TYPE_ITEM_FIRST:
				holderFirst = new HolderFirst();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_recorddetail_first, null);
				holderFirst.rlExpressNo = (RelativeLayout) convertView.findViewById(R.id.rlExpressNo);
				holderFirst.rlPHoneNo = (RelativeLayout) convertView.findViewById(R.id.rlPHoneNo);
				holderFirst.tvExpressNo = (TextView) convertView.findViewById(R.id.tvExpressNo);
				holderFirst.tvFlowExpressNo = (TextView) convertView.findViewById(R.id.tvFlowExpressNo);
				holderFirst.line = convertView.findViewById(R.id.line);
				holderFirst.tvSendPhone = (TextView) convertView.findViewById(R.id.tvSendPhone);
				holderFirst.llPhone = (RelativeLayout) convertView.findViewById(R.id.llPhone);
				holderFirst.ll_signed_time = (LinearLayout) convertView.findViewById(R.id.ll_signed_time);
				holderFirst.tv_signed_time = (TextView) convertView.findViewById(R.id.tv_signed_time);
				convertView.setTag(holderFirst);
				break;
			case TYPE_ITEM_SECOND:
				holderSecond = new HolderSecond();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_recorddetail_second2, null);
				holderSecond.tvTimeReceive = (TextView) convertView.findViewById(R.id.tvTimeReceive);
				holderSecond.rlCustomReceive = (RelativeLayout) convertView.findViewById(R.id.rlCustomReceive);
				holderSecond.imvVoiceContentReceive = (ImageView) convertView.findViewById(R.id.imvVoiceContentReceive);
				holderSecond.tvContentReceive = (TextView) convertView.findViewById(R.id.tvContentReceive);
				holderSecond.tvRecordTimeReceive = (TextView) convertView.findViewById(R.id.tvRecordTimeReceive);
				holderSecond.rlMySend = (RelativeLayout) convertView.findViewById(R.id.rlMySend);
				holderSecond.tvContentSend = (TextView) convertView.findViewById(R.id.tvContentSend);
				holderSecond.tvRecordTimeSend = (TextView) convertView.findViewById(R.id.tvRecordTimeSend);
				holderSecond.rlContentBgSend = (ViewGroup) convertView.findViewById(R.id.rlContentBgSend);
				holderSecond.rlContentBgReceive = (ViewGroup) convertView.findViewById(R.id.rlContentBgReceive);
				holderSecond.imvVoiceContentSend = (ImageView) convertView.findViewById(R.id.imvVoiceContentSend);
				convertView.setTag(holderSecond);
				break;

			default:
				break;
			}

		} else {
			switch (type) {
			case TYPE_ITEM_FIRST:
				holderFirst = (HolderFirst) convertView.getTag();
				break;
			case TYPE_ITEM_SECOND:
				holderSecond = (HolderSecond) convertView.getTag();
				break;

			default:
				break;
			}
		}

		switch (type) {
		case TYPE_ITEM_FIRST:
			// 运单号没有则不显示运单号区域
			holderFirst.tvExpressNo.setText(expressNumber);
			holderFirst.rlExpressNo.setVisibility(Utility.isEmpty(expressNumber) ? View.GONE : View.VISIBLE);
			holderFirst.line.setVisibility(Utility.isEmpty(expressNumber) ? View.GONE : View.VISIBLE);

			holderFirst.tvSendPhone.setText(mobilePhone);
			holderFirst.rlPHoneNo.setVisibility(Utility.isEmpty(mobilePhone) || (!Utility.isEmpty(mobilePhone) && "1**********".equals(mobilePhone)) ? View.GONE : View.VISIBLE);
			holderFirst.line.setVisibility(Utility.isEmpty(mobilePhone) || "1**********".equals(mobilePhone) ? View.GONE : View.VISIBLE);
			holderFirst.llPhone.setVisibility(Utility.isEmpty(mobilePhone) || mobilePhone.contains("*") ? View.GONE : View.VISIBLE);// 手机号码中存在*号则拨打电话按钮隐藏

			if (!Utility.isEmpty(signedTime)){
				String showSignedTime = signedTime.substring(5,16);
				holderFirst.ll_signed_time.setVisibility(View.VISIBLE);
				holderFirst.tv_signed_time.setText("取件时间："+showSignedTime);
			}else{
				holderFirst.ll_signed_time.setVisibility(View.GONE);
			}

			holderFirst.tvFlowExpressNo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onButtonClickListener.flowExpressNo(expressNumber);
				}
			});
			holderFirst.llPhone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onButtonClickListener.call(mobilePhone);
				}
			});
			break;
		case TYPE_ITEM_SECOND:
			recordDetail = getItem(position);
			String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

			String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(recordDetail.getSpeak_time());

			if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
				holderSecond.tvTimeReceive.setText("今天 " + updateTime.substring(10, 16));
			} else if (now.substring(0, 8).equals(updateTime.substring(0, 8))
					&& Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
				holderSecond.tvTimeReceive.setText("昨天 " + updateTime.substring(10, 16));
			} else {
				holderSecond.tvTimeReceive.setText(updateTime.substring(0, 16));
			}

			String speaker_role = recordDetail.getSpeaker_role();
			final int content_type = recordDetail.getContent_type();
			if (!Utility.isEmpty(speaker_role) && speaker_role.equals("counterman")) {
				holderSecond.rlMySend.setVisibility(View.VISIBLE);
				holderSecond.rlCustomReceive.setVisibility(View.GONE);// 隐藏客户部分
				if (!Utility.isEmpty(content_type) && content_type == 1) {// 快递员发送单独纯文本
					holderSecond.tvContentSend.setVisibility(View.VISIBLE);
					holderSecond.tvContentSend.setText(recordDetail.getContent());// 设置快递员发送文本
					holderSecond.imvVoiceContentSend.setVisibility(View.GONE);
					holderSecond.tvRecordTimeSend.setVisibility(View.GONE);
				} else if (!Utility.isEmpty(content_type) && content_type == 6) {// 快递员发送的云呼语音
					String ivr_user_input = recordDetail.getIvr_user_input();// 用户对语音的按键反馈
					holderSecond.tvContentSend.setVisibility(View.VISIBLE);
					holderSecond.tvContentSend.setText(recordDetail.getContent_title());
					holderSecond.imvVoiceContentSend.setVisibility(View.VISIBLE);// 显示快递员语音
					holderSecond.tvRecordTimeSend.setVisibility(View.VISIBLE);// 显示回复语音时长控件
					holderSecond.tvRecordTimeSend.setTextColor(mContext.getResources().getColor(R.color.blue_1));// 设置语音时长颜色为蓝色
					// if(!Utility.isEmpty(ivr_user_input)){
					// holderSecond.tvRecordTimeSend.setText("反馈"+ivr_user_input);//
					// 显示用户反馈
					// }else{
					// holderSecond.tvRecordTimeSend.setText("");
					// }
					holderSecond.tvRecordTimeSend.setVisibility(View.GONE);

					String voiceName = recordDetail.getContent_path().substring(recordDetail.getContent_path().lastIndexOf("/") + 1);
					String voicePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName;
					File file = new File(voicePath);

					String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
					File curDirectory = new File(voiceDirectory);
					if (!curDirectory.exists()) {
						curDirectory.mkdirs();
					}

					if (!file.exists()) {
						FinalHttp finalHttp = new FinalHttp();
						finalHttp.download(recordDetail.getContent_path(),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName,
								new AjaxCallBack<File>() {
									@Override
									public void onLoading(long count, long current) {
										super.onLoading(count, current);
									}

									@Override
									public void onSuccess(File t) {
										super.onSuccess(t);
										//System.out.println("gudd 下载成功");
									}

									@Override
									public void onFailure(Throwable t, int errorNo, String strMsg) {
										super.onFailure(t, errorNo, strMsg);
										//System.out.println("gudd 下载失败  " + strMsg);
									}
								});
					}
				} else if (!Utility.isEmpty(content_type) && content_type == 3) {// 非云呼语音
					holderSecond.tvContentSend.setVisibility(View.GONE);
					holderSecond.imvVoiceContentSend.setVisibility(View.VISIBLE);// 显示快递员语音
					holderSecond.tvRecordTimeSend.setVisibility(View.VISIBLE);// 显示回复语音时长控件
					holderSecond.tvRecordTimeSend.setTextColor(mContext.getResources().getColor(R.color.gray_1));// 设置语音时长颜色为黑色
					holderSecond.tvRecordTimeSend.setText(recordDetail.getVoice_length() + "\"");
					String voiceFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + recordDetail.getContent();
					File fileVoice = new File(voiceFile);

					String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
					File fileDirectory = new File(voiceDirectory);
					if (!fileDirectory.exists()) {
						fileDirectory.mkdirs();
					}
					if (!fileVoice.exists()) {
						FinalHttp finalHttp = new FinalHttp();
						finalHttp.download("http://upload.kuaidihelp.com/liuyan/new/" + recordDetail.getContent(),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + recordDetail.getContent(),
								new AjaxCallBack<File>() {
									@Override
									public void onLoading(long count, long current) {
										super.onLoading(count, current);
									}

									@Override
									public void onSuccess(File t) {
										super.onSuccess(t);
										//System.out.println("gudd 下载成功");
									}

									@Override
									public void onFailure(Throwable t, int errorNo, String strMsg) {
										super.onFailure(t, errorNo, strMsg);
										//System.out.println("gudd 下载失败  " + strMsg);
									}
								});
					}

				}
			} else if (!Utility.isEmpty(speaker_role) && (speaker_role.equals("user") || speaker_role.equals("shop"))) {
				holderSecond.rlMySend.setVisibility(View.GONE);// 隐藏快递员部分
				holderSecond.rlCustomReceive.setVisibility(View.VISIBLE);
				if (!Utility.isEmpty(content_type) && content_type == 1) {// 客户发送单独纯文本
					holderSecond.tvContentReceive.setText(recordDetail.getContent());// 设置客户发送文本
					holderSecond.imvVoiceContentReceive.setVisibility(View.GONE);// 显示语音ICON
					holderSecond.tvRecordTimeReceive.setVisibility(View.GONE);
				} else if (!Utility.isEmpty(content_type) && content_type == 3) {// 客户回复的语音
					holderSecond.tvContentReceive.setText("");// 设置客户发送文本
					holderSecond.tvRecordTimeReceive.setVisibility(View.VISIBLE);// 显示客户语音时长
					holderSecond.imvVoiceContentReceive.setVisibility(View.VISIBLE);// 显示语音ICON
					holderSecond.tvRecordTimeReceive.setText(recordDetail.getVoice_length() + "\"");

					String voiceFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + recordDetail.getContent();
					File fileVoice = new File(voiceFile);

					String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
					File fileDirectory = new File(voiceDirectory);
					if (!fileDirectory.exists()) {
						fileDirectory.mkdirs();
					}
					if (!fileVoice.exists()) {
						FinalHttp finalHttp = new FinalHttp();
						finalHttp.download("http://upload.kuaidihelp.com/liuyan/new/" + recordDetail.getContent(),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + recordDetail.getContent(),
								new AjaxCallBack<File>() {
									@Override
									public void onLoading(long count, long current) {
										super.onLoading(count, current);
									}

									@Override
									public void onSuccess(File t) {
										super.onSuccess(t);
										//System.out.println("gudd 下载成功");
									}

									@Override
									public void onFailure(Throwable t, int errorNo, String strMsg) {
										super.onFailure(t, errorNo, strMsg);
										//System.out.println("gudd 下载失败  " + strMsg);
									}
								});
					}

				}else if (!Utility.isEmpty(content_type) && content_type == 7) {// 云呼过程中客户按按键以后录音，返回给快递员的录音	
					holderSecond.tvContentReceive.setText("语音留言");// 设置客户发送文本
					holderSecond.imvVoiceContentReceive.setVisibility(View.VISIBLE);// 显示语音ICON
//					holderSecond.tvRecordTimeReceive.setText(recordDetail.getVoice_length() + "\"");
//					holderSecond.tvRecordTimeReceive.setVisibility(View.VISIBLE);// 显示客户语音时长
					
					String voiceName = recordDetail.getContent().substring(recordDetail.getContent().lastIndexOf("/") + 1);// 获取文件名称
					String voiceFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName;
					File fileVoice = new File(voiceFile);

					String voiceDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice";
					File fileDirectory = new File(voiceDirectory);
					if (!fileDirectory.exists()) {
						fileDirectory.mkdirs();
					}
					if (!fileVoice.exists()) {
						FinalHttp finalHttp = new FinalHttp();
						finalHttp.download("http://upload.kuaidihelp.com/" + recordDetail.getContent().substring(8),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/voice/" + voiceName,
								new AjaxCallBack<File>() {
									@Override
									public void onLoading(long count, long current) {
										super.onLoading(count, current);
									}

									@Override
									public void onSuccess(File t) {
										super.onSuccess(t);
										//System.out.println("gudd 下载成功7");
									}

									@Override
									public void onFailure(Throwable t, int errorNo, String strMsg) {
										super.onFailure(t, errorNo, strMsg);
										//System.out.println("gudd 下载失败 7 " + strMsg);
									}
								});
					}
				}else{
					holderSecond.rlCustomReceive.setVisibility(View.GONE);
				}
			}

			holderSecond.rlContentBgReceive.setOnClickListener(new OnClickListener() {// 客户语音播放

				@Override
				public void onClick(View v) {
					// startPlayRecord(recordDetails.get(position-1).getContent());
					if(!Utility.isEmpty(content_type) && content_type == 7){
						String voiceName = recordDetails.get(position - 1).getContent().substring(recordDetails.get(position - 1).getContent().lastIndexOf("/") + 1);// 获取文件名称
						DownloadTask.playLocalVoice(voiceName);
					}else{
						DownloadTask.playLocalVoice(recordDetails.get(position - 1).getContent());
					}
				}
			});

			holderSecond.rlContentBgSend.setOnClickListener(new OnClickListener() {// 快递员云呼语音播放

				@Override
				public void onClick(View v) {
					if (!Utility.isEmpty(content_type) && content_type == 6) {
						String servicePath = recordDetails.get(position - 1).getContent_path();
						String voiceName = servicePath.substring(servicePath.lastIndexOf("/") + 1);
						// startPlayRecord(voiceName);
						DownloadTask.playLocalVoice(voiceName);
					} else if (!Utility.isEmpty(content_type) && content_type == 3) {
						DownloadTask.playLocalVoice(recordDetails.get(position - 1).getContent());
					}

				}
			});

			break;

		default:
			break;
		}

		return convertView;
	}

	class HolderFirst {
		RelativeLayout rlExpressNo = null;
		RelativeLayout rlPHoneNo;
		TextView tvExpressNo = null;// 运单号显示控件
		TextView tvFlowExpressNo = null;// 运单号跟踪按钮
		View line = null;// 线条
		TextView tvSendPhone = null;// 手机号显示控件
		RelativeLayout llPhone = null;// 拨打电话按钮
		LinearLayout ll_signed_time;// 签收时间区域
		TextView tv_signed_time;// 签收时间
	}

	class HolderSecond {
		TextView tvTimeReceive = null;// 时间显示控件
		RelativeLayout rlCustomReceive = null;// 客户回复：接收区域
		ImageView imvVoiceContentReceive = null;// 客户接收语音图片
		TextView tvContentReceive = null;// 客户回复内容
		TextView tvRecordTimeReceive = null;// 客户回复语音时长显示控件
		RelativeLayout rlMySend = null;// 发送区域
		TextView tvContentSend = null;// 发送内容
		ImageView imvVoiceContentSend = null;// 发送语音回复ICON
		TextView tvRecordTimeSend = null;// 发送回复语音时长显示控件
		ViewGroup rlContentBgSend = null;// 我回复的区域
		ViewGroup rlContentBgReceive = null;// 客户回复的区域
	}

	public interface OnButtonClickListener {
		/**
		 * 运单跟踪方法
		 * @param expressNo 运单号
		 */
		void flowExpressNo(String expressNo);

		/**
		 * 打电话方法
		 * @param mobilePhone 手机号码
		 */
		void call(String mobilePhone);
	}

}
