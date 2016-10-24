package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.wheelview.widget.OnWheelChangedListener;
import com.kuaibao.skuaidi.wheelview.widget.WheelView;
import com.kuaibao.skuaidi.wheelview.widget.adapter.ArrayWheelAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间选择pop
 * 
 * @author 顾冬冬
 * 
 */
public class SelectTimePop extends PopupWindow implements OnWheelChangedListener {
	View view;
	Context context;

	public static final int DATE = 1;
	public static final int HOUR = 2;
	public static final int MINUTE = 3;

	private LinearLayout pop;
	private LinearLayout ll_cancel;// 取消
	private LinearLayout ll_ok;// 确定
	private TextView tv_ok;// 确定文本
	private WheelView date;// 日期
	private WheelView hour;// 小时
	private WheelView minute;// 分钟

	private String curDateStr;// 当前时间字符串
	private String newDateStr;// 新的时间字符串
	private Date newDate;

	private int selectDateIndex = 0;
	private int selectHourIndex = 0;
	private int selectMinuteIndex = 0;

	private String dateArr[] = new String[3];
	private String hourArr[] = { "00点", "01点", "02点", "03点", "04点", "05点", "06点", "07点", "08点", "09点", "10点", "11点", "12点", "13点", "14点", "15点", "16点", "17点", "18点", "19点", "20点",
			"21点", "22点", "23点" };
	private String minuteArr[] = { "00分", "10分", "20分", "30分", "40分", "50分" };

	public SelectTimePop(Context context, OnClickListener onClickListener) {
		super(context);
		this.context = context;

		dateArr[0] = DateHelper.getDate(DateHelper.TIME_TODAY, DateHelper.YYYY_MM_DD_CH).substring(5);
		dateArr[1] = DateHelper.getDate(DateHelper.TIME_TOMORROW, DateHelper.YYYY_MM_DD_CH).substring(5);
		dateArr[2] = DateHelper.getDate(DateHelper.TIME_THEDAY_AFTER_TOMORROW, DateHelper.YYYY_MM_DD_CH).substring(5);

		view = View.inflate(context, R.layout.select_time_wheel_pop, null);
		pop = (LinearLayout) view.findViewById(R.id.pop);
		ll_cancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
		ll_ok = (LinearLayout) view.findViewById(R.id.ll_ok);
		tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		date = (WheelView) view.findViewById(R.id.date);
		hour = (WheelView) view.findViewById(R.id.hour);
		minute = (WheelView) view.findViewById(R.id.minute);

		ll_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// exitAnimation();
				dismiss();
			}
		});

		if (null == onClickListener) {
			ll_ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
		} else {
			ll_ok.setOnClickListener(onClickListener);
		}

		tv_ok.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.popUpWindowEnterExit);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

		initDate();
		setUpListener();
		setUpData();
	}
	
	public void showPopupWindow(View v){
		// 显示的时候再重新获取一下时间设置日期-存放到日期数组中
		dateArr[0] = DateHelper.getDate(DateHelper.TIME_TODAY, DateHelper.YYYY_MM_DD_CH).substring(5);
		dateArr[1] = DateHelper.getDate(DateHelper.TIME_TOMORROW, DateHelper.YYYY_MM_DD_CH).substring(5);
		dateArr[2] = DateHelper.getDate(DateHelper.TIME_THEDAY_AFTER_TOMORROW, DateHelper.YYYY_MM_DD_CH).substring(5);
		this.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	@SuppressLint("SimpleDateFormat")
	private void initDate() {
		Date date = new Date();// 当前的日期时间

		long exchangeDate = date.getTime() + 60 * 10 * 1000;
		newDate = new Date(exchangeDate);

		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDateStr = simple.format(date);// 获得当前时间
		newDateStr = simple.format(newDate);// 获得+10分钟后的日期

	}

	private void setUpListener() {
		date.addChangingListener(this);
		hour.addChangingListener(this);
		minute.addChangingListener(this);

		date.setBackgroundColor(context.getResources().getColor(color.white));
		hour.setBackgroundColor(context.getResources().getColor(color.white));
		minute.setBackgroundColor(context.getResources().getColor(color.white));
	}

	private void setUpData() {
		date.setViewAdapter(new ArrayWheelAdapter<String>(context, dateArr));
		hour.setViewAdapter(new ArrayWheelAdapter<String>(context, hourArr));
		minute.setViewAdapter(new ArrayWheelAdapter<String>(context, minuteArr));
		// 设置可见条目数量
		date.setVisibleItems(5);
		hour.setVisibleItems(5);
		minute.setVisibleItems(5);
		// 设置wheel可循环
		date.setCyclic(false);
		hour.setCyclic(true);
		minute.setCyclic(true);

		/**
		 * 初始化默认时间****************************
		 */

		int hourIndex;
		int minuIndex;
		try {
			hourIndex = Integer.parseInt(String.valueOf(newDate.getHours()).substring(0, 2));
		} catch (Exception e) {
			hourIndex = Integer.parseInt(String.valueOf(newDate.getHours()).substring(0, 1));
		}
		try {
			minuIndex = Integer.parseInt(String.valueOf(newDate.getMinutes()).substring(0, 2)) % Integer.parseInt(String.valueOf(newDate.getMinutes()).substring(0, 1)) == 0 ? Integer
					.parseInt(String.valueOf(newDate.getMinutes()).substring(0, 1)) + 1 : Integer.parseInt(String.valueOf(newDate.getMinutes()).substring(0, 1)) + 1;// 如果+10分钟以后的日期里的分钟对他分钟里的下标为0的数字取余数为0的时候设置它的下标为分钟下标为0+1，否则为下标为0的值
		} catch (Exception e) {
			minuIndex = Integer.parseInt(String.valueOf(newDate.getMinutes()).substring(0, 1)) < 10 ? 1 : 2;// 如果+10分钟以后的日期里的分钟下标为1的数字小于10则设置要显示列表内容的下标为1，否则为2
		}
		if (minuIndex == 6) {
			minuIndex = 0;
			if(hourIndex+1 == 24){
				hourIndex = 0;
			}else{
				hourIndex = hourIndex + 1;
			}
		}
		minute.setCurrentItem(minuIndex);
		hour.setCurrentItem(hourIndex);
		/*// 设置当前日期
		if (curDateStr.substring(0, 10).equals(newDateStr.substring(0, 10))) {
			date.setCurrentItem(0);
			// 设置默认日期
			dateStr = dateArr[0];
		} else {
			date.setCurrentItem(1);
			// 设置默认日期
			dateStr = dateArr[1];
		}
		// 设置默认小时数
		hourStr = hourArr[hourIndex];
		// 设置默认分钟数
		minuteStr = minuteArr[minuIndex];*/
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if(newValue == -1){
			newValue = 0;
		}
		if (wheel == date) {
			selectDateIndex = newValue;
		} else if (wheel == hour) {
			selectHourIndex = newValue;
		} else if (wheel == minute) {
			selectMinuteIndex = newValue;
		}
	}

	/** 获取选择的条目的下标ID **/
	public int getSelectIndex(int time) {
		if (time == DATE) {
			return selectDateIndex;
		} else if (time == HOUR) {
			return selectHourIndex;
		} else if (time == MINUTE) {
			return selectMinuteIndex;
		}
		return 0;
	}

	/**
	 * @Title: getTimeStamp
	 * @Description: 获取选中时间的时间戳
	 * @param
	 * @return long
	 */
	public long getTimeStamp() {
		long timeStamp = 0;// 时间戳变量
		int dateIndex = getSelectIndex(SelectTimePop.DATE);
		int hourIndex = getSelectIndex(SelectTimePop.HOUR);
		int minuteIndex = getSelectIndex(SelectTimePop.MINUTE);
		// 计算日期-返回时间戳
		if (dateIndex == 0) {
			timeStamp = DateHelper.getTimeStamp(0,
					hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":" + minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00",
					DateHelper.YYYY_MM_DD);
		} else if (dateIndex == 1) {
			timeStamp = DateHelper.getTimeStamp(1,
					hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":" + minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00",
					DateHelper.YYYY_MM_DD);
		} else if (dateIndex == 2) {
			timeStamp = DateHelper.getTimeStamp(2,
					hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":" + minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00",
					DateHelper.YYYY_MM_DD);
		}
		return timeStamp;
	}

	/**
	 * @Title: getSendTimeStr
	 * @Description: 获取发送时间用于显示在文本框中的字符串
	 * @return String
	 */
	public String getSendTimeStr() {
		String date = dateArr[getSelectIndex(SelectTimePop.DATE)];
		String hour = hourArr[getSelectIndex(SelectTimePop.HOUR)];
		String minute = minuteArr[getSelectIndex(SelectTimePop.MINUTE)];
		return date + " " + hour.substring(0, hour.indexOf("点")) + ":" + minute.substring(0, minute.indexOf("分"));
	}

	/**
	 * @Title: isMoreThanTheCurrent10Minutes
	 * @Description: 选中的时间是否大于当前10分钟以上
	 * @return boolean
	 */
	public boolean isMoreThanTheCurrent10Minutes() {

		int dateIndex = getSelectIndex(SelectTimePop.DATE);
		int hourIndex = getSelectIndex(SelectTimePop.HOUR);
		int minuteIndex = getSelectIndex(SelectTimePop.MINUTE);

		// 年月日
		String dateStr = "";

		if (dateIndex == 0) {
			dateStr = DateHelper.getAppointDate(0, DateHelper.YYYY_MM_DD);
			dateStr = dateStr + " " + hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":"
					+ minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00";
		} else if (dateIndex == 1) {
			dateStr = DateHelper.getAppointDate(1, DateHelper.YYYY_MM_DD);
			dateStr = dateStr + " " + hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":"
					+ minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00";
		} else if (dateIndex == 2) {
			dateStr = DateHelper.getAppointDate(2, DateHelper.YYYY_MM_DD);
			dateStr = dateStr + " " + hourArr[hourIndex].substring(0, hourArr[hourIndex].indexOf("点")) + ":"
					+ minuteArr[minuteIndex].substring(0, minuteArr[minuteIndex].indexOf("分")) + ":" + "00";
		}

		java.util.Date nows = new java.util.Date();// 获取当前时间对象
		if (!((DateHelper.getTimeStamp(dateStr, "yyyy-MM-dd HH:mm:ss") - nows.getTime() / 1000) < 60 * 10)) {
			return true;
		} else {
			UtilToolkit.showToast("选择的时间应大于当前时间10分钟以上");
		}
		return false;
	}

}
