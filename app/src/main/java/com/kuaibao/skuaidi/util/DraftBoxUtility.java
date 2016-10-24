package com.kuaibao.skuaidi.util;

import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.entry.NotifyInfo2;

import java.util.List;

/**
 * 草稿箱工具类
 * @author 顾冬冬
 *
 */
public class DraftBoxUtility {

	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接编号-用于发送短信界面适配器中的数据使用
	 * @param @param infos
	 * @return void
	 */
	public static String pinjieNumber(List<NotifyInfo2> infos) {
		String number = "";
		for (int i = 0; i < infos.size(); i++) {
			NotifyInfo2 notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getExpressNo())) {
				number += notifyInfo2.getExpressNo() + ",";
			} else {
				number += " ,";
			}
		}
		return number;
	}
	
	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接编号-用于云呼短信界面适配器中的数据使用
	 * @param @param infos
	 * @return void
	 */
	public static String pinjieNumberCloud(List<CloudVoiceMsgDetailEntry> infos) {
		String number = "";
		for (int i = 0; i < infos.size(); i++) {
			CloudVoiceMsgDetailEntry notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getMobile_no())) {
				number += notifyInfo2.getMobile_no() + ",";
			} else {
				number += " ,";
			}
		}
		return number;
	}

	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接手机号-用于发送短信界面适配器中的数据使用
	 * @param @param infos
	 * @return void
	 */
	public static String pinjiePhoneNumber(List<NotifyInfo2> infos) {
		String phoneNumber = "";
		for (int i = 0; i < infos.size(); i++) {
			NotifyInfo2 notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getSender_mobile())) {
				phoneNumber += notifyInfo2.getSender_mobile().replaceAll(" ", "") + ",";
			} else {
				phoneNumber += " ,";
			}
		}
		return phoneNumber;
	}
	
	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接手机号-用于发送短信界面适配器中的数据使用
	 * @param @param infos
	 * @return void
	 */
	public static String pinjiePhoneNumberCloud(List<CloudVoiceMsgDetailEntry> infos) {
		String phoneNumber = "";
		for (int i = 0; i < infos.size(); i++) {
			CloudVoiceMsgDetailEntry notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getMobile())) {
				phoneNumber += notifyInfo2.getMobile().replaceAll(" ", "") + ",";
			} else {
				phoneNumber += " ,";
			}
		}
		return phoneNumber;
	}

	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接手机号-用于发送短信界面适配器中的数据使用
	 * @param @param infos
	 * @return void
	 */
	public static String pinjieOrderNumberCloud(List<CloudVoiceMsgDetailEntry> infos) {
		String orderNumber = "";
		for (int i = 0; i < infos.size(); i++) {
			CloudVoiceMsgDetailEntry notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getOrder_no())) {
				orderNumber += notifyInfo2.getOrder_no().replaceAll(" ", "") + ",";
			} else {
				orderNumber += " ,";
			}
		}
		return orderNumber;
	}

	/**
	 * @Title: pinjieNumber
	 * @Description: 拼接单号-用于发送短信界面适配器中的数据使用
	 * @param infos
	 * @return void
	 */
	public static String pinjieOrderNumber(List<NotifyInfo2> infos) {
		String orderNumber = "";
		for (int i = 0; i < infos.size(); i++) {
			NotifyInfo2 notifyInfo2 = infos.get(i);
			if (!Utility.isEmpty(notifyInfo2.getExpress_number())) {
				orderNumber += notifyInfo2.getExpress_number().replaceAll(" ", "") + ",";
			} else {
				orderNumber += " ,";
			}
		}
		return orderNumber;
	}
	
	/**
	 * @Title: strToArr
	 * @Description:将包含"，"的字符串转成数组
	 * @param @param str
	 * @return void
	 */
	public static String[] strToArr(String str) {
		if (!Utility.isEmpty(str))
			return str.split(",");
		else
			return null;
	}
	
}
