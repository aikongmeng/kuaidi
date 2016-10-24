package com.kuaibao.skuaidi.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

import java.util.List;
import java.util.regex.Pattern;

public class AllInterface {
	/**
	 * 根据品牌名获取 品牌代号
	 * @param no
	 * @return
	 */
	public static String getExpressNoStr(String no) {
		String express_name = "";
		if (no.equals("dhl")) {
			express_name = "DHL";
		} else if (no.equals("yt")) {
			express_name = "圆通";
		} else if (no.equals("sto")) {
			express_name = "申通";
		} else if (no.equals("zt")) {
			express_name = "中通";
		} else if (no.equals("yd")) {
			express_name = "韵达";
		} else if (no.equals("dp")) {
			express_name = "德邦";
		} else if (no.equals("ems")) {
			express_name = "EMS";
		} else if (no.equals("gt")) {
			express_name = "国通";
		} else if (no.equals("ht")) {
			express_name = "汇通";
		} else if (no.equals("hq")) {
			express_name = "汇强";
		} else if (no.equals("kj")) {
			express_name = "快捷";
		} else if (no.equals("lb")) {
			express_name = "龙邦";
		} else if (no.equals("qy")) {
			express_name = "全一";
		} else if (no.equals("qf")) {
			express_name = "全峰";
		} else if (no.equals("qrt")) {
			express_name = "全日通";
		} else if (no.equals("rfd")) {
			express_name = "如风达";
		} else if (no.equals("sf")) {
			express_name = "顺丰";
		} else if (no.equals("se")) {
			express_name = "速尔";
		} else if (no.equals("tt")) {
			express_name = "天天";
		} else if (no.equals("zjs")) {
			express_name = "宅急送";
		} else if (no.equals("post")) {
			express_name = "邮政";
		} else if (no.equals("fedexInter") || no.equals("fedex")) {
			express_name = "联邦";
		} else if (no.equals("jd")) {
			express_name = "京东";
		} else if (no.equals("lht")) {
			express_name = "联昊通";
		} else if (no.equals("nd")) {
			express_name = "能达";
		} else if (no.equals("ups")) {
			express_name = "UPS";
		} else if (no.equals("ys")) {
			express_name = "优速";
		} else if (no.equals("dy")) {
			express_name = "大洋";
		} else if (no.equals("kr")) {
			express_name = "宽容";
		} else if (no.equals("sad")) {
			express_name = "赛澳递";
		} else if (no.equals("wx")) {
			express_name = "万象";
		} else if (no.equals("yc")) {
			express_name = "远长";
		} else if (no.equals("zy")) {
			express_name = "增益";
		}

		return express_name;
	}
	/**
	 * 根据品牌代号获取 公司电话
	 * @param no
	 * @return
	 */
	public static String getExpressCall(String no) {
		String express_call = "";
		if (no.equals("dhl")) {
			express_call = "800-810-8000";
		} else if (no.equals("yt")) {
			express_call = "021-69777888";
		} else if (no.equals("sto")) {
			express_call = "400-889-5543";
		} else if (no.equals("zt")) {
			express_call = "4008-270-270";
		} else if (no.equals("yd")) {
			express_call = "400-821-6789";
		} else if (no.equals("dp")) {
			express_call = "400-830-5555";
		} else if (no.equals("ems")) {
			express_call = "11183";
		} else if (no.equals("gt")) {
			express_call = "400-111-1123";
		} else if (no.equals("ht")) {
			express_call = "400-956-5656";
		} else if (no.equals("hq")) {
			express_call = "400-000-0177";
		} else if (no.equals("kj")) {
			express_call = "400-830-4888";
		} else if (no.equals("lb")) {
			express_call = "021-39283333";
		} else if (no.equals("qy")) {
			express_call = "400-663-1111";
		} else if (no.equals("qf")) {
			express_call = "400-100-0001";
		} else if (no.equals("qrt")) {
			express_call = "020-86298999";
		} else if (no.equals("rfd")) {
			express_call = "400-010-6660";
		} else if (no.equals("sf")) {
			express_call = "4008-111-111";
		} else if (no.equals("se")) {
			express_call = "400-158-9888";
		} else if (no.equals("tt")) {
			express_call = "4001-888-888";
		} else if (no.equals("zjs")) {
			express_call = "400-6789-000";
		} else if (no.equals("fedex") || no.equals("fedexInter")) {
			express_call = "400-886-1888";
		} else if (no.equals("jd")) {
			express_call = "400-606-5500";
		} else if (no.equals("lht")) {
			express_call = "0769-88620000";
		} else if (no.equals("nd")) {
			express_call = "400-620-1111";
		} else if (no.equals("ups")) {
			express_call = "400-820-8388";
		} else if (no.equals("ys")) {
			express_call = "400-1111-119";
		} else if (no.equals("post")) {
			express_call = "11185";
		}

		return express_call;
	}
	/**
	 * 根据输入单号 返回可能对应的快递公司
	 * @param ordernumber
	 * @return
	 */
	public static String[] getExpressNoForRule(String ordernumber) {
		String[] express_no = new String[] {};
		int length = ordernumber.length();
		if (length == 8 || length == 9) {
			express_no = new String[] { "dp" };
		} else if (length == 10) {
			if (ordernumber.substring(0, 1).matches("[0-9]+$")) {
				String str = ordernumber.substring(0, 3);
				if (str.matches("[0-9]+$")) {
					int number = Integer.parseInt(str);
					if ((number >= 231 && number <= 237) || number == 610
							|| number == 611 || number == 710) {
						express_no = new String[] { "gt", "yt", "zjs" };
					} else if (number == 230
							|| (number >= 502 && number <= 505)) {
						express_no = new String[] { "gt", "zjs", "yt" };
					} else if ((number >= 373 && number <= 376)
							|| number == 317 || number == 322 || number == 323
							|| number == 327 || number == 329 || number == 330
							|| number == 460 || number == 466
							|| (number >= 480 && number <= 482)
							|| number == 489 || number == 803 || number == 856
							|| number == 860 || number == 869) {
						express_no = new String[] { "zjs", "yt", "gt" };
					} else if ((number >= 119 && number <= 130)) {
						express_no = new String[] { "jd" };
					} else {
						express_no = new String[] { "yt", "zjs", "gt" };
					}
				}
			} else {
				express_no = new String[] { "yt" };
			}
		} else if (length == 11) {
			if (ordernumber.substring(0, 1).matches("[0-9]+$")) {
				express_no = new String[] { "jd" };
			} else {
				express_no = new String[] { "ups" };
			}
		} else if (length == 12) {
			String str = ordernumber.substring(0, 3);
			if (str.matches("[0-9]+$")) {
				int number = Integer.parseInt(str);
				if (number == 268 || number == 368 || number == 468
						|| number == 568 || number == 668 || number == 868
						|| number == 888 || number == 900) {
					express_no = new String[] { "sto" };
				} else if (number == 358 || number == 518 || number == 618
						|| number == 718 || number == 728 || number == 738
						|| number == 751 || number == 761 || number == 762
						|| number == 763 || number == 778) {
					express_no = new String[] { "zt" };
				} else if ((str.substring(0, 1).equals("0") && !str
						.equals("000"))
						|| number == 113
						|| number == 114
						|| number == 116
						|| number == 117
						|| number == 118
						|| number == 131
						|| number == 199
						|| number == 203
						|| number == 204
						|| number == 205
						|| number == 206
						|| number == 302
						|| number == 575
						|| number == 591
						|| number == 594
						|| number == 660
						|| number == 730
						|| number == 756
						|| number == 903
						|| number == 904
						|| number == 905 || number == 966) {
					express_no = new String[] { "sf" };
				} else if (number == 210 || number == 250 || number == 280
						|| number == 420) {
					express_no = new String[] { "ht" };
				} else if (number == 560 || number == 580 || number == 776) {
					express_no = new String[] { "tt" };
				} else if (number == 300 || number == 340 || number == 370
						|| number == 710) {
					express_no = new String[] { "qf" };
				} else if (number == 768) {
					express_no = new String[] { "sto", "zt" };
				} else if (number == 220) {
					express_no = new String[] { "sto", "ht" };
				} else if (number == 701 || number == 660 || number == 757) {
					express_no = new String[] { "zt", "sf" };
				} else if (number == 350) {
					express_no = new String[] { "ht", "qf" };
				} else if (number == 310 || number == 510) {
					express_no = new String[] { "sf", "ht" };
				} else if (number == 550 || number == 886 || number == 530) {
					express_no = new String[] { "tt", "sf", "kj" };
				} else if (number == 688) {
					express_no = new String[] { "sto", "sf", "zt" };
				} else if (number == 588) {
					express_no = new String[] { "sto", "sf" };
				} else if (number == 0) {
					express_no = new String[] { "jd" };
				} else if (number == 370) {
					express_no = new String[] { "sf", "qf" };
				} else if (number == 500) {
					express_no = new String[] { "se", "ht" };
				} else if (number == 880) {
					express_no = new String[] { "se", "kj" };
				} else if (number == 990 || number == 980) {
					express_no = new String[] { "kj" };
				} else {
					express_no = new String[] { "sf", "tt", "ht" };
				}
			} else if (str.matches("[a-zA-Z]+$")) {
				express_no = new String[] { "yt" };
			} else {
				express_no = new String[] { "sf", "tt", "ht" };
			}
		} else if (length == 13) {
			String str = ordernumber.substring(0, 2);
			if (str.matches("[0-9]+$")) {
				String str1 = ordernumber.substring(0, 3);
				if (str1.matches("[0-9]+$")) {
					int number = Integer.parseInt(str1);
					if (number == 120 || number == 130 || number == 140
							|| number == 150 || number == 160 || number == 170
							|| number == 190 || number == 200 || number == 220
							|| number == 310 || number == 500 || number == 520
							|| number == 660 || number == 800 || number == 880
							|| number == 900) {
						express_no = new String[] { "yd" };
					} else if ((number >= 101 && number <= 129 && number != 113 && number != 120)
							|| (number >= 501 && number <= 519)) {
						express_no = new String[] { "ems" };
					} else if (number >= 990 && number <= 999) {
						express_no = new String[] { "post" };
					} else if (number == 100) {
						express_no = new String[] { "yd", "ems" };
					} else if (number == 500) {
						express_no = new String[] { "ems", "yd" };
					} else if (number == 113) {
						express_no = new String[] { "ems", "rfd" };
					} else if (number == 313 || number == 513 || number == 713
							|| number == 913) {
						express_no = new String[] { "rfd", "yd", "ems" };
					} else if (number == 901) {
						express_no = new String[] { "jd" };
					} else {
						express_no = new String[] { "yd", "ems", "post" };
					}
				} else {
					express_no = new String[] { "yd", "ems", "post" };
				}
			} else {
				String str1 = ordernumber.substring(11, 13);
				if (str1.matches("[a-zA-Z]+$")) {
					express_no = new String[] { "ems" };
				} else {
					express_no = new String[] { "post" };
				}
			}
		} else if (length >= 14 && length <= 18) {
			String str = ordernumber.substring(0, 2);
			if (str.equals("1Z") || str.equals("1z")) {
				express_no = new String[] { "ups" };
			} else {
				express_no = new String[] { "rfd" };
			}
		}
		return express_no;
	}

	public static String formatCall(String call_str) {
		char[] cs = call_str.toCharArray();
		String newStr = "";
		for (int i = 0, j = cs.length; i < j; i++) {
			Pattern pattern = Pattern.compile("[0-9]*");
			String s = cs[i] + "";
			if (pattern.matcher(s).matches() || "-".equals(s)) {
				newStr += s;
			} else {
				newStr += ";";
			}
		}
		String[] strs = newStr.split(";");
		for (int i = 0, j = strs.length; i < j; i++) {
			if (!strs[i].equals("")) {
				return strs[i];
			}
		}
		return call_str;
	}

	public static boolean isbackActivity(Context context) {
		List<RunningTaskInfo> taskList = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE))
				.getRunningTasks(Integer.MAX_VALUE);
		boolean flag = true;
		if (taskList.size() > 0) {
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).baseActivity.getClassName().equals(
						"com.kuaibao.kuaidi.activity.MainActivity")) {
					flag = false;
				}
			}
		}
		return flag;
	}

}