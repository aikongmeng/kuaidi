package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ClickItem;
import com.kuaibao.skuaidi.entry.WuliuItem;
import com.kuaibao.skuaidi.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindExpressResultAdapter extends ArrayAdapter<WuliuItem> {
	private String flag;
	private Context context;
	private String express_name;
	private String express_id;
	private String express_name_2;
	private String str2;
	private final int BUILDER_SHOW = 7;
	private final int BUILDER_COURIER_SHOW = 9;
	private List<String> list_express_id;
	private Handler handler;
	private String courier;
	private String courier_firm;
	private String courier_call;
	private String courier_2;
	private String today;
	private String yesterday;
	private String newstr;
	private long minResponseTime = 0l;
	private ArrayList<WuliuItem> problemList = new ArrayList<WuliuItem>();

	public FindExpressResultAdapter(Context context, List<WuliuItem> list, String flag, Handler handler) {
		super(context, 0, list);
		this.context = context;
		this.flag = flag;
		this.handler = handler;
		list_express_id = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			list_express_id.add("");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		today = sdf.format(now.getTime());
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		yesterday = sdf.format(now.getTime());
		for (int i = 0; i < this.getCount(); i++) {
			if (!problemList.contains(getItem(i)) && getItem(i).getDetail().contains("【问题件】")) {
				problemList.add(0, getItem(i));
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(getContext()).inflate(R.layout.findexpress_result_item, null);
		final LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_findexpress_result_item);
		TextView tv_time = (TextView) convertView.findViewById(R.id.tv_findexpress_result_time);
		TextView tv_date = (TextView) convertView.findViewById(R.id.tv_findexpress_result_date);
		TextView tv_detail = (TextView) convertView.findViewById(R.id.tv_findexpress_result_detail);
		ImageView iv = (ImageView) convertView.findViewById(R.id.iv_findexpress_resultstate);
		if (position == 0) {
			tv_time.setTextColor(context.getResources().getColor(R.color.text_black));
			tv_date.setTextColor(context.getResources().getColor(R.color.text_black));
			tv_detail.setTextColor(context.getResources().getColor(R.color.text_black));
			tv_detail.setTextSize(16);
			// 异常情况
			if (flag.equals("1")) {
				iv.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_result_exception));
			} else {
				iv.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_success));
			}
		}
		WuliuItem wuliu = getItem(position);
		//Log.i("iii", wuliu.toString());
		String wuliutime = wuliu.getTime();
		if (wuliutime.indexOf(":") != -1) {
			wuliutime = wuliutime.substring(0, wuliutime.lastIndexOf(":"));
		}
		tv_time.setText(wuliutime);
		String wuliudate = wuliu.getDate();
		if (wuliudate.trim().equals(today)) {
			tv_date.setText("今天");
		} else if (wuliudate.trim().equals(yesterday)) {
			tv_date.setText("昨天");
		} else {
			tv_date.setText(wuliudate.replaceAll("-", "."));
		}

		String str = wuliu.getDetail();
		int namepositionflag = str.indexOf("#");
		if (wuliu.getClicks() != null) {
			// 添加超链接
			tv_detail.setMovementMethod(new LinkMovementMethod());

			// 截取中间的名字出来
			String oldname = "";
			String newname = "";
			int startname = 0;
			int endname = 0;

			if (namepositionflag != -1) {// 派件员信息 、物流公司 都加超链接

				Pattern p = Pattern.compile("#(.*)#");
				Pattern pattern = Pattern.compile("#(.*):");
				Matcher m = p.matcher(str);
				Matcher m2 = pattern.matcher(str);

				if (m.find()) {// 全部
					//Log.i("iii", ">>>>>>>>>>" + m.group(1));
					oldname = str.substring(m.start(), m.end());// #魏志峰:安徽利辛公司,0558-2905709#
					oldname = oldname.substring(1, oldname.length() - 1);
				}
				if (m2.find()) {
					// 名字
					String sub_oldname = str.substring(m2.start(), m2.end());//
					//Log.i("iii", "newname=" + sub_oldname);
					newname = sub_oldname.substring(1, sub_oldname.length() - 1);// 魏志峰:安徽利辛公司,0558-2905709

				}
				startname = str.indexOf("#");
				endname = str.indexOf("#") + newname.length();
				// str.replaceAll("#(.*):", newname);

				newstr = str.replaceAll("#(.*)#", newname);

				SpannableStringBuilder style = new SpannableStringBuilder(newstr);

				for (int i = 0; i < wuliu.getClicks().size(); i++) {
					final ClickItem clickItem = wuliu.getClicks().get(i);

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
							ds.setUnderlineText(false);
						}

						@Override
						public void onClick(View widget) {
							Message msg = new Message();
							msg.what = Constants.GET_SENDER_INFO;
							msg.obj = clickItem;
							handler.sendMessage(msg);

						}
					}, startname, endname, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
							ds.setUnderlineText(false);
						}

						@Override
						public void onClick(View widget) {
							Message msg = new Message();
							msg.what = Constants.GET_BRNCH_INFO;
							msg.obj = clickItem;
							handler.sendMessage(msg);

						}
					}, clickItem.getStart(), clickItem.getEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}

				tv_detail.setText(style);
			} else {// 只有物流公司信息
				SpannableStringBuilder style = new SpannableStringBuilder(str);
				for (int i = 0; i < wuliu.getClicks().size(); i++) {
					final ClickItem clickItem = wuliu.getClicks().get(i);

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
							ds.setUnderlineText(false);
						}

						@Override
						public void onClick(View widget) {
							Message msg = new Message();
							msg.what = Constants.GET_BRNCH_INFO;
							msg.obj = clickItem;
							handler.sendMessage(msg);

						}
					}, clickItem.getStart(), clickItem.getEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
				tv_detail.setText(style);
			}

		} else {
			// 只给派件员添加超链接
			tv_detail.setMovementMethod(new LinkMovementMethod());
			if (null != str) {
				// 截取中间的名字出来
				String oldname = "";
				String newname = "";
				int startname = 0;
				int endname = 0;
				if (namepositionflag != -1) {// 派件员信息 、物流公司 都加超链接
					Pattern p = Pattern.compile("#(.*)#");
					Pattern pattern = Pattern.compile("#(.*):");
					Matcher m = p.matcher(str);
					Matcher m2 = pattern.matcher(str);

					if (m.find()) {// 全部
						oldname = str.substring(m.start(), m.end());// #魏志峰:安徽利辛公司,0558-2905709#
						oldname = oldname.substring(1, oldname.length() - 1);
					}
					if (m2.find()) {
						// 名字
						String sub_oldname = str.substring(m2.start(), m2.end());//
						//Log.i("iii", "newname=" + sub_oldname);
						newname = sub_oldname.substring(1, sub_oldname.length() - 1);// 魏志峰:安徽利辛公司,0558-2905709

					}
					startname = str.indexOf("#");
					endname = str.indexOf("#") + newname.length();
					// str.replaceAll("#(.*):", newname);

					newstr = str.replaceAll("#(.*)#", newname);

					SpannableStringBuilder style = new SpannableStringBuilder(newstr);

					final String clickItem = wuliu.getDetail();

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
							ds.setUnderlineText(false);
						}

						@Override
						public void onClick(View widget) {
							Message msg = new Message();
							msg.what = Constants.GET_SENDER_INFO;
							msg.obj = clickItem;
							handler.sendMessage(msg);

						}
					}, startname, endname, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					tv_detail.setText(style);
				}else{
					tv_detail.setText(str);
				}
			}
		}
		if (wuliu.getDetail().contains("【问题件】")) {
			SpannableStringBuilder ssb = new SpannableStringBuilder(wuliu.getDetail() + " 查看");
			//Log.d("问题件", "问题件position:" + position);
			ssb.setSpan(new ClickableSpan() {

							@Override
							public void onClick(View widget) {
								if (minResponseTime != 0l && System.currentTimeMillis() - minResponseTime < 1000) {
									return;
								}
								minResponseTime = System.currentTimeMillis();
								Message msg = new Message();
								msg.what = Constants.PROBLEM_CAUSE;
								msg.obj = problemList.indexOf(getItem(position)) + 1;
								handler.sendMessage(msg);
							}

							@Override
							public void updateDrawState(TextPaint ds) {
								super.updateDrawState(ds);
								ds.setColor(Color.parseColor("#3a9af9"));
								ds.setUnderlineText(false);
							}
						}, wuliu.getDetail().length() + 1, wuliu.getDetail().length() + 3,
					Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			tv_detail.setText(ssb);
		}

		ll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					ll.setBackgroundResource(R.color.item_hover);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					ll.setBackgroundResource(R.color.item_hover);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					ll.setBackgroundResource(R.drawable.list_item_bg);
				} else {
					ll.setBackgroundResource(R.drawable.list_item_bg);
				}
				return true;
			}
		});
		return convertView;
	}

}