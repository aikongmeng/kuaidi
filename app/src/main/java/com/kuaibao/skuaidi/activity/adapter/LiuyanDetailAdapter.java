package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.NotifyImageShowActivity;
import com.kuaibao.skuaidi.dialog.menu.DynamicCommDialog;
import com.kuaibao.skuaidi.entry.LinkMovementClickMethod;
import com.kuaibao.skuaidi.entry.MessageDetail;
import com.kuaibao.skuaidi.entry.MessageDetail.Attach;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.PorterShapeImageView;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressLint("SimpleDateFormat")
public class LiuyanDetailAdapter extends BaseAdapter {
	private static final String REG = "[0-9]{3,4}-?[0-9]*-?[0-9]*";
	private Context context;
	private ArrayList<MessageDetail> messages;
	final int TYPE_LIUYAN_INFO = 0;
	final int TYPE_LIUYAN_IM = 1;
	private MediaPlayer mp;
	private boolean isPlaying = false;
	private long time;
	private Timer timer;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private long latestTime = 0l;
	private int lastPosition;
	private String msgId;
	private String msgType;
	private MessageDetail messageDetail;
	private SparseArray<String> playStateArray=new SparseArray<String>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				// holder.recording_palying_duration_liuyan.setText(format.format(time
				// * 1000 - 28800000));

				break;

			default:
				break;
			}
		}
	};

	public LiuyanDetailAdapter(ArrayList<MessageDetail> messages, Context context) {
		this.context = context;
		this.messages = messages;

	}

	public void setMsgId(String msgId){
		this.msgId = msgId;
	}

	public void setMsgType(String msgType){
		this.msgType = msgType;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public MessageDetail getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void notifyDataSetChanged(ArrayList<MessageDetail> messages) {
		this.messages = messages;
		notifyDataSetChanged();
	}

	ViewHolderFirst holderFirst;

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_exception_detail, parent, false);

			holder.tv_time_receive = (TextView) convertView.findViewById(R.id.tv_time_receive);

			holder.rl_receive = (RelativeLayout) convertView.findViewById(R.id.rl_receive);
			holder.imv_voice_content_receive = (ImageView) convertView.findViewById(R.id.imv_voice_content_receive);
			holder.iv_image_content_receive = (ImageView) convertView.findViewById(R.id.iv_image_content_receive);
			holder.tv_user_info = (TextView) convertView.findViewById(R.id.tv_user_info);
			holder.tv_content_receive = (TextView) convertView.findViewById(R.id.tv_content_receive);
			//holder.tv_content_receive.setTextIsSelectable(true); 
			 holder.tv_post_name_receive = (TextView)
			 convertView.findViewById(R.id.tv_post_name_receive);

			holder.rl_send = (RelativeLayout) convertView.findViewById(R.id.rl_send);
			holder.rl_content_bg_send = (RelativeLayout) convertView.findViewById(R.id.rl_content_bg_send);
			holder.tv_content_send = (TextView) convertView.findViewById(R.id.tv_content_send);
			//holder.tv_content_send.setTextIsSelectable(true); 
			holder.iv_image_content_send = (PorterShapeImageView) convertView.findViewById(R.id.iv_image_content_send);
			holder.imv_voice_content_send = (ImageView) convertView.findViewById(R.id.imv_voice_content_send);
			holder.imv_phone_dialog_send = (ImageView) convertView.findViewById(R.id.imv_phone_dialog_send);
			holder.tv_post_name_send = (TextView)
			 convertView.findViewById(R.id.tv_post_name_send);

			holder.recording_center_view_liuyan = convertView.findViewById(R.id.recording_center_view_liuyan);
			holder.playorpause_liuyan = (TextView) convertView.findViewById(R.id.playorpause_liuyan);
			holder.recording_duration_liuyan = (TextView) convertView.findViewById(R.id.recording_duration_liuyan);
			holder.recording_palying_duration_liuyan = (TextView) convertView
					.findViewById(R.id.recording_palying_duration_liuyan);
			holder.tv_file_deleted = (TextView) convertView.findViewById(R.id.tv_file_deleted);
			holder.view_line_between = convertView.findViewById(R.id.view_line_between);
			holder.imv_header_receive= (ImageView) convertView.findViewById(R.id.imv_header_receive);
			holder.imv_header_send= (ImageView) convertView.findViewById(R.id.imv_header_send);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		messageDetail = messages.get(position);
		Utility.setTimeDate4(messageDetail.getPost_time(), holder.tv_time_receive);
		String type = getItem(position).getT();
		int content_type = getItem(position).getContent_type();
		final String countent = getItem(position).getContent();
		final Attach attach = getItem(position).getAttach();
		// /storage/emulated/0/skuaidi/call_recording/13127966212_1446434499980.amr
		if (content_type == 3) {
			holder.rl_send.setVisibility(View.GONE);
			holder.rl_receive.setVisibility(View.GONE);
			holder.recording_center_view_liuyan.setVisibility(View.VISIBLE);

			if (attach != null) {
				attach.getLength();
				// holder.recording_duration_liuyan.setText(dateFormat.format(new
				// Date(attach.getLength())));

				Utility.setTimeDate1(messageDetail.getPost_time(), holder.recording_duration_liuyan);

				holder.recording_palying_duration_liuyan.setText(format.format(attach.getLength() - 28800000));
			}

			File file = new File(Constants.LOCAL_RECORD_PATH + countent);
			if(!file.exists()){
				holder.playorpause_liuyan.setBackgroundResource(R.drawable.message_record);
				holder.playorpause_liuyan.setEnabled(false);
				holder.tv_file_deleted.setVisibility(View.VISIBLE);
				holder.view_line_between.setVisibility(View.VISIBLE);
			}else{
				holder.playorpause_liuyan.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
				holder.playorpause_liuyan.setEnabled(true);
				holder.tv_file_deleted.setVisibility(View.GONE);
				holder.view_line_between.setVisibility(View.GONE);
			}
			holder.playorpause_liuyan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try {
						if (isPlaying == true) {
							if (mp != null) {
								mp.stop();
								mp.release();
							}
							time = 0;
							if (timer != null)
								timer.cancel();
							holder.playorpause_liuyan.setBackgroundResource(SkuaidiSkinManager
									.getSkinResId("record_play_small"));
							holder.recording_palying_duration_liuyan.setText(format.format(attach.getLength() - 28800000));
							isPlaying = false;
						} else {
							mp = new MediaPlayer();
							mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									isPlaying = false;
									time = 0;
									mp.stop();
									mp.release();
									timer.cancel();
									holder.playorpause_liuyan.setBackgroundResource(SkuaidiSkinManager
											.getSkinResId("record_play_small"));
								}
							});
							mp.reset();
							mp.setDataSource(Constants.LOCAL_RECORD_PATH+countent);
							mp.prepare();
							mp.start();
							timer = new Timer(true);
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									holder.playorpause_liuyan.post(new Runnable() {

										@Override
										public void run() {
											if (!holder.recording_palying_duration_liuyan.getText().toString()
													.equals(format.format(time * 1000 - 28800000))) {
												holder.recording_palying_duration_liuyan.setText(format
														.format(time * 1000 - 28800000));
												time = time + 1;
											}
										}
									});

								}
							}, 0, 1000);
							holder.playorpause_liuyan.setBackgroundResource(SkuaidiSkinManager
									.getSkinResId("record_stop_small"));
							isPlaying = true;
						}

					} catch (IllegalStateException e) {
						UtilToolkit.showToast("录音文件播放失败！");
						e.printStackTrace();
					} catch (IOException e) {
						UtilToolkit.showToast("录音文件播放失败！");
						e.printStackTrace();
					}

				}
			});
		}else if (content_type == 4){
			holder.rl_send.setVisibility(View.VISIBLE);
			holder.rl_receive.setVisibility(View.GONE);
			holder.tv_content_send.setVisibility(View.VISIBLE);
			holder.tv_content_send.setText("通话录音  "+ Utility.formatTime(getItem(position).getLength()));
			holder.iv_image_content_send.setVisibility(View.GONE);
			holder.imv_phone_dialog_send.setVisibility(View.VISIBLE);
			holder.imv_phone_dialog_send.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String state=playStateArray.get(position);
					if(null != state){
						playStateArray.put(position,"default".equals(state)?"active":"default");
					}else{
						playStateArray.put(position,"active");
					}
					KLog.i("tag","state0:"+playStateArray.get(position));
					if("active".equals(playStateArray.get(position))){
						KLog.i("tag", "state1:"+getItem(position).getContent());
						MessageEvent event=new MessageEvent(0XF1,getItem(position).getContent());
						event.setPosition(position);
						EventBus.getDefault().post(event);
					}else{// 停止当前条目播放的
						MessageEvent event=new MessageEvent(0XF2,"stop right now");
						EventBus.getDefault().post(event);
					}
				}
			});
		} else {
			holder.recording_center_view_liuyan.setVisibility(View.GONE);
			if ("from".equals(type)) {
				holder.rl_send.setVisibility(View.GONE);
				holder.rl_receive.setVisibility(View.VISIBLE);
				String postInfo = "";

				if (!TextUtils.isEmpty(messageDetail.getPost_username())) {
					postInfo += " " + messageDetail.getPost_username();
				}

				showBitmapByStatus(convertView, holder, attach, type);
				 holder.tv_post_name_receive.setText(postInfo);
				if (!TextUtils.isEmpty(messageDetail.getContent())) {
					holder.tv_content_receive.setVisibility(View.VISIBLE);
					SpannableString s = new SpannableString(messageDetail.getContent());
					filterNumber(s);
					holder.tv_content_receive.setText(s);
					holder.tv_content_receive.setMovementMethod(LinkMovementClickMethod.getInstance());
				} else {
					holder.tv_content_receive.setVisibility(View.GONE);
				}

			} else if ("to".equals(type)) {
				holder.rl_receive.setVisibility(View.GONE);
				holder.imv_phone_dialog_send.setVisibility(View.GONE);
				holder.rl_send.setVisibility(View.VISIBLE);
				showBitmapByStatus(convertView, holder, attach, type);
				 holder.tv_post_name_send.setText("我");
				if(TextUtils.isEmpty(messageDetail.getContent())){
					holder.tv_content_send.setVisibility(View.GONE);
				}else{
					holder.tv_content_send.setVisibility(View.VISIBLE);
					SpannableString s = new SpannableString(messageDetail.getContent());
					filterNumber(s);
					holder.tv_content_send.setText(s);
					holder.tv_content_send.setMovementMethod(LinkMovementClickMethod.getInstance());
				}
			}
		}
		//holder.tv_post_name_receive.setPadding(holder.imv_header_receive.getWidth(),0,0,0);
		//holder.tv_post_name_send.setPadding(0,0,holder.imv_header_send.getWidth(),0);
		holder.tv_content_receive.setOnLongClickListener(new MyOnLongClickListener((Activity)context, holder.tv_content_receive.getText().toString()));
		holder.tv_content_send.setOnLongClickListener(new MyOnLongClickListener((Activity)context, holder.tv_content_send.getText().toString()));
		return convertView;

	}

	private final static class MyOnLongClickListener implements OnLongClickListener{
		Activity activity;
		String str;
		public MyOnLongClickListener(Activity activity,String str){
			this.activity=activity;
			this.str=str;
		}
		@Override
		public boolean onLongClick(View v) {
			DynamicCommDialog dialog=new DynamicCommDialog(activity).builder().addClickListener(new DynamicCommDialog.OnDialogMenuClickListener() {
				@Override
				public void copy() {
					ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
					cmb.setText(str.trim());
					UtilToolkit.showToast( "文本已复制到粘贴板");
				}
			}).setCancleable(true).serOnTouchOutSide(true);
			dialog.showDialog();
			return true;
		}
	}

	ImageView imageView = null;
	private void showBitmapByStatus(View convertView, final ViewHolder holder, final Attach attach, String msgType) {
		View line = null;
		if ("from".equals(msgType)) {
			line = convertView.findViewById(R.id.view1);
			imageView = holder.iv_image_content_receive;
		} else {
			line = convertView.findViewById(R.id.view2);
			imageView = holder.iv_image_content_send;
		}
		if (attach != null && !TextUtils.isEmpty(attach.getUrl())) {
			
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, NotifyImageShowActivity.class);
					intent.putExtra("image", attach.getUrl());
					intent.putExtra("from", "liuyanDetail_iv_image_content_receive");
					context.startActivity(intent);

				}
			});
			if("image".equals(attach.getType())){
				line.setVisibility(View.GONE);
			}else{
				line.setVisibility(View.VISIBLE);
			}
			imageView.setVisibility(View.VISIBLE);
//			DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
//					.bitmapConfig(Config.RGB_565).build();
//			ImageLoader.getInstance().displayImage(attach.getUrl(), holder.iv_image_content_receive, imageOptions);
//			Glide.with(context).load(attach.getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
//				@Override
//				public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//					RelativeLayout.LayoutParams params;
//					float width = resource.getWidth();
//					float height = resource.getHeight();
//					if(height > width && height > 150){
//						imageView.getLayoutParams().height = 150;
//						width = width*(150/height);
//						imageView.getLayoutParams().width = Math.round(width);
//					}else if(width > height && width > 150){
//						imageView.getLayoutParams().width = 150;
//						height = height*(150/width);
//						imageView.getLayoutParams().width = Math.round(height);
//					}
//					imageView.setImageBitmap(resource);
//				}
//			});
//			((SKuaidiApplication)context.getApplicationContext()).GlideUrlToImg(attach.getUrl(),imageView);
			GlideUtil.GlideUrlToImg(context, attach.getUrl(), imageView);
		} else {
			line.setVisibility(View.GONE);
			imageView.setVisibility(View.GONE);
		}
	}

	public class TextClickableSpan extends ClickableSpan {
		private String text;
		public TextClickableSpan(String text) {
			this.text = text;
		}
		@Override
		public void onClick(View view) {
			String phone = text;
			if(phone.startsWith("1") && phone.length() == 11){
				AcitivityTransUtil.showChooseTeleTypeDialog((Activity)context, "", phone,AcitivityTransUtil.LIUYAN_CALL_DIALOG, "visible", msgId, msgType);
			}else{
				AcitivityTransUtil.showChooseTeleTypeDialog((Activity)context, "", phone,AcitivityTransUtil.LIUYAN_CALL_DIALOG, "gone", msgId, msgType);
			}

		}
	}

	private void filterNumber(Spannable s) {
		Matcher m = Pattern.compile(REG).matcher(s.toString());
		while (m.find()) {
			String text = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(m.group());
			if(text.length() > 7 && text.length() < 13) {
				if(text.startsWith("1") && text.length() == 11){
					KLog.i("tag", "手机号码");
				}else if((text.length() == 11 || text.length() == 12) && !text.startsWith("0")){
					continue;
				}
				TextClickableSpan span = new TextClickableSpan(text);
				s.setSpan(span, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	class ViewHolder {

		TextView tv_time_receive;
		 TextView tv_post_name_receive;
		 TextView tv_post_name_send;

		View centerView;
		View bottomView;
		View rl_liuyan_detail_item;
		View view_line_between;

		View recording_center_view_liuyan;
		TextView playorpause_liuyan;
		// TextView recording_date_liuyan;
		TextView recording_duration_liuyan;
		TextView tv_file_deleted;
		TextView recording_palying_duration_liuyan;
		RelativeLayout rl_receive;
		TextView tv_time;
		ImageView imv_header_receive;
		RelativeLayout rl_content_bg_receive;
		TextView tv_content_receive;
		ImageView imv_voice_content_receive;
		ImageView iv_image_content_receive;
		// ImageButton imb_help_receive;
		TextView tv_record_time_receive;
		TextView tv_user_info;

		RelativeLayout rl_send;
		ImageView imv_header_send;
		RelativeLayout rl_content_bg_send;
		TextView tv_content_send;
		PorterShapeImageView iv_image_content_send;
		ImageView imv_voice_content_send;
		ImageView imv_phone_dialog_send;
		TextView tv_record_time_send;
	}

	class ViewHolderFirst {
		TextView tv_sender_phone, playorpause_liuyan, recording_palying_duration_liuyan, recording_date_liuyan,
				recording_duration_liuyan;
		ImageView imv_phone_icon;
		TextView tv_deliverno, tv_order_tailAfter;
		RelativeLayout rl_express_no;// 快递运单号区域
		TextView tv_express_no;// 显示运单号控件
		View recording_center_view_liuyan, view1;
	}

	public void stopPlayRecording() {
		if (mp != null) {
			try {
				mp.pause();
				mp.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			mp.release();
			mp = null;
		}
	}
}