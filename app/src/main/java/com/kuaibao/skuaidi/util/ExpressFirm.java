package com.kuaibao.skuaidi.util;

import com.kuaibao.skuaidi.R;

public class ExpressFirm {

	/**
	 * 根据品牌名获取品牌缩写
	 * 
	 * @param expressFirm
	 * @return
	 */
	public static String expressFirmToNo(String expressFirm) {
		String expressNo = null;
		if (expressFirm.equals("DHL快递")) {
			expressNo = "dhl";
		} else if (expressFirm.equals("德邦物流")) {
			expressNo = "dp";
		} else if (expressFirm.equals("EMS")) {
			expressNo = "ems";
		} else if (expressFirm.equals("联邦快递")) {
			expressNo = "fedex";
		} else if (expressFirm.equals("国通快递")) {
			expressNo = "gt";
		} else if (expressFirm.equals("汇强快递")) {
			expressNo = "hq";
		} else if (expressFirm.equals("汇通快递")) {
			expressNo = "ht";
		} else if (expressFirm.equals("京东快递")) {
			expressNo = "jd";
		} else if (expressFirm.equals("快捷速递")) {
			expressNo = "kj";
		} else if (expressFirm.equals("龙邦速递")) {
			expressNo = "lb";
		} else if (expressFirm.equals("联昊通快递")) {
			expressNo = "lht";
		} else if (expressFirm.equals("能达快递")) {
			expressNo = "nd";
		} else if (expressFirm.equals("邮政")) {
			expressNo = "post";
		} else if (expressFirm.equals("全峰快递")) {
			expressNo = "qf";
		} else if (expressFirm.equals("全一快递")) {
			expressNo = "qy";
		} else if (expressFirm.equals("如风达")) {
			expressNo = "rfd";
		} else if (expressFirm.equals("速尔快递")) {
			expressNo = "se";
		} else if (expressFirm.equals("顺丰快递")) {
			expressNo = "sf";
		} else if (expressFirm.equals("申通快递")) {
			expressNo = "sto";
		} else if (expressFirm.equals("天天快递")) {
			expressNo = "tt";
		} else if (expressFirm.equals("UPS")) {
			expressNo = "ups";
		} else if (expressFirm.equals("韵达快递")) {
			expressNo = "yd";
		} else if (expressFirm.equals("优速快递")) {
			expressNo = "ys";
		} else if (expressFirm.equals("圆通快递")) {
			expressNo = "yt";
		} else if (expressFirm.equals("宅急送")) {
			expressNo = "zjs";
		} else if (expressFirm.equals("中通快递")) {
			expressNo = "zt";
		}
		return expressNo;
	}

	public static String expressPosition(String expressNo) {
		String expressPosition = null;
		if (expressNo.equals("dhl")) {
			expressPosition = "DHL快递员";
		} else if (expressNo.equals("dp")) {
			expressPosition = "德邦快递员";
		} else if (expressNo.equals("ems")) {
			expressPosition = "EMS快递员";
		} else if (expressNo.equals("fedex") || expressNo.equals("fedexInter")) {
			expressPosition = "联邦快递员";
		} else if (expressNo.equals("gt")) {
			expressPosition = "国通快递员";
		} else if (expressNo.equals("hq")) {
			expressPosition = "汇强快递员";
		} else if (expressNo.equals("ht")) {
			expressPosition = "汇通快递员";
		} else if (expressNo.equals("jd")) {
			expressPosition = "京东快递员";
		} else if (expressNo.equals("kj")) {
			expressPosition = "快捷速递员";
		} else if (expressNo.equals("lb")) {
			expressPosition = "龙邦速递员";
		} else if (expressNo.equals("lht")) {
			expressPosition = "联昊通快递员";
		} else if (expressNo.equals("nd")) {
			expressPosition = "能达快递员";
		} else if (expressNo.equals("post")) {
			expressPosition = "邮政快递员";
		} else if (expressNo.equals("qf")) {
			expressPosition = "全峰快递员";
		} else if (expressNo.equals("qy")) {
			expressPosition = "全一快递员";
		} else if (expressNo.equals("rfd")) {
			expressPosition = "如风达快递员";
		} else if (expressNo.equals("se")) {
			expressPosition = "速尔快递员";
		} else if (expressNo.equals("sf")) {
			expressPosition = "顺丰快递员";
		} else if (expressNo.equals("sto")) {
			expressPosition = "申通快递员";
		} else if (expressNo.equals("tt")) {
			expressPosition = "天天快递员";
		} else if (expressNo.equals("ups")) {
			expressPosition = "UPS快递员";
		} else if (expressNo.equals("yd")) {
			expressPosition = "韵达快递员";
		} else if (expressNo.equals("ys")) {
			expressPosition = "优速快递员";
		} else if (expressNo.equals("yt")) {
			expressPosition = "圆通快递员";
		} else if (expressNo.equals("zjs")) {
			expressPosition = "宅急送快递员";
		} else if (expressNo.equals("dy")) {
			expressPosition = "大洋快递员";
		} else if (expressNo.equals("kr")) {
			expressPosition = "宽容快递员";
		} else if (expressNo.equals("sad")) {
			expressPosition = "赛澳递快递员";
		} else if (expressNo.equals("wx")) {
			expressPosition = "万象快递员";
		} else if (expressNo.equals("yc")) {
			expressPosition = "远长快递员";
		} else if (expressNo.equals("zy")) {
			expressPosition = "增益快递员";
		} else if (expressNo.equals("jd")) {
			expressPosition = "京东快递员";
		}
		return expressPosition;
	}

	/**
	 * 根据品牌缩写获取品牌名
	 * 
	 * @param expressNo
	 * @return
	 */
	public static String expressNoToFirm(String expressNo) {
		String expressFirm = null;
		if (expressNo.equals("dhl")) {
			expressFirm = "DHL快递";
		} else if (expressNo.equals("dp")) {
			expressFirm = "德邦物流";
		} else if (expressNo.equals("ems")) {
			expressFirm = "EMS";
		} else if (expressNo.equals("fedex") || expressNo.equals("fedexInter")) {
			expressFirm = "联邦快递";
		} else if (expressNo.equals("gt")) {
			expressFirm = "国通快递";
		} else if (expressNo.equals("hq")) {
			expressFirm = "汇强快递";
		} else if (expressNo.equals("ht")) {
			expressFirm = "汇通快递";
		} else if (expressNo.equals("jd")) {
			expressFirm = "京东快递";
		} else if (expressNo.equals("kj")) {
			expressFirm = "快捷速递";
		} else if (expressNo.equals("lb")) {
			expressFirm = "龙邦速递";
		} else if (expressNo.equals("lht")) {
			expressFirm = "联昊通快递";
		} else if (expressNo.equals("nd")) {
			expressFirm = "能达快递";
		} else if (expressNo.equals("post")) {
			expressFirm = "邮政";
		} else if (expressNo.equals("qf")) {
			expressFirm = "全峰快递";
		} else if (expressNo.equals("qy")) {
			expressFirm = "全一快递";
		} else if (expressNo.equals("rfd")) {
			expressFirm = "如风达";
		} else if (expressNo.equals("se")) {
			expressFirm = "速尔快递";
		} else if (expressNo.equals("sf")) {
			expressFirm = "顺丰快递";
		} else if (expressNo.equals("sto")) {
			expressFirm = "申通快递";
		} else if (expressNo.equals("tt")) {
			expressFirm = "天天快递";
		} else if (expressNo.equals("ups")) {
			expressFirm = "UPS";
		} else if (expressNo.equals("yd")) {
			expressFirm = "韵达快递";
		} else if (expressNo.equals("ys")) {
			expressFirm = "优速快递";
		} else if (expressNo.equals("yt")) {
			expressFirm = "圆通快递";
		} else if (expressNo.equals("zjs")) {
			expressFirm = "宅急送";
		} else if (expressNo.equals("zt")) {
			expressFirm = "中通快递";
		} else if (expressNo.equals("dy")) {
			expressFirm = "大洋快递";
		} else if (expressNo.equals("kr")) {
			expressFirm = "宽容快递";
		} else if (expressNo.equals("sad")) {
			expressFirm = "赛澳快递";
		} else if (expressNo.equals("wx")) {
			expressFirm = "万象快递";
		} else if (expressNo.equals("yc")) {
			expressFirm = "远长快递";
		} else if (expressNo.equals("zy")) {
			expressFirm = "增益快递";
		} else if (expressNo.equals("jd")) {
			expressFirm = "京东快递";
		}

		return expressFirm;
	}

	/**
	 * 根据品牌缩写获取品牌图标
	 * 
	 * @param expressNo
	 * @return
	 */
	public static int getExpressIcon(String expressNo) {
		int headIconId = 0;
		if (expressNo.equals("dhl")) {
			headIconId = R.drawable.icon_dhl;
		} else if (expressNo.equals("dp")) {
			headIconId = R.drawable.icon_dp;
		} else if (expressNo.equals("ems")) {
			headIconId = R.drawable.icon_ems;
		} else if (expressNo.equals("fedex") || expressNo.equals("fedexInter")) {
			headIconId = R.drawable.icon_fedexhome;
		} else if (expressNo.equals("gt")) {
			headIconId = R.drawable.icon_gt;
		} else if (expressNo.equals("hq")) {
			headIconId = R.drawable.icon_hq;
		} else if (expressNo.equals("ht")) {
			headIconId = R.drawable.icon_ht;
		} else if (expressNo.equals("jd")) {
			headIconId = R.drawable.icon_jd;
		} else if (expressNo.equals("kj")) {
			headIconId = R.drawable.icon_kj;
		} else if (expressNo.equals("lb")) {
			headIconId = R.drawable.icon_lb;// 待修改
		} else if (expressNo.equals("lht")) {
			headIconId = R.drawable.icon_lht;
		} else if (expressNo.equals("nd")) {
			headIconId = R.drawable.icon_nd;
		} else if (expressNo.equals("post")) {
			headIconId = R.drawable.icon_post;
		} else if (expressNo.equals("qf")) {
			headIconId = R.drawable.icon_qf;
		} else if (expressNo.equals("qy")) {
			headIconId = R.drawable.icon_qy;
		} else if (expressNo.equals("rfd")) {
			headIconId = R.drawable.icon_rfd;
		} else if (expressNo.equals("se")) {
			headIconId = R.drawable.icon_se;
		} else if (expressNo.equals("sf")) {
			headIconId = R.drawable.icon_sf;// 待完善
		} else if (expressNo.equals("sto")) {
			headIconId = R.drawable.icon_sto;
		} else if (expressNo.equals("tt")) {
			headIconId = R.drawable.icon_tt;
		} else if (expressNo.equals("ups")) {
			headIconId = R.drawable.icon_ups;// 待修改
		} else if (expressNo.equals("yd")) {
			headIconId = R.drawable.icon_yd;
		} else if (expressNo.equals("ys")) {
			headIconId = R.drawable.icon_ys;
		} else if (expressNo.equals("yt")) {
			headIconId = R.drawable.icon_yt;
		} else if (expressNo.equals("zjs")) {
			headIconId = R.drawable.icon_zjs;
		} else if (expressNo.equals("zt")) {
			headIconId = R.drawable.icon_zt;
		}

		return headIconId;
	}

	/**
	 * 根据品牌缩写获取品牌图标头像
	 * 
	 * @param expressNo
	 * @return
	 */
	public static int getExpressHeadIcon(String expressNo) {
		int headIconId = 0;
		if (expressNo.equals("dhl")) {
			headIconId = R.drawable.icon_head_dhl;
		} else if (expressNo.equals("dp")) {
			headIconId = R.drawable.icon_head_dp;
		} else if (expressNo.equals("ems")) {
			headIconId = R.drawable.icon_head_ems;
		} else if (expressNo.equals("fedex") || expressNo.equals("fedexInter")) {
			headIconId = R.drawable.icon_head_fedex;
		} else if (expressNo.equals("gt")) {
			headIconId = R.drawable.icon_head_gt;
		} else if (expressNo.equals("hq")) {
			headIconId = R.drawable.icon_head_hq;
		} else if (expressNo.equals("ht")) {
			headIconId = R.drawable.icon_head_ht;
		} else if (expressNo.equals("jd")) {
			headIconId = R.drawable.icon_head_jd;
		} else if (expressNo.equals("kj")) {
			headIconId = R.drawable.icon_head_kj;
		} else if (expressNo.equals("lb")) {
			headIconId = R.drawable.icon_renxiang;// 待修改
		} else if (expressNo.equals("lht")) {
			headIconId = R.drawable.icon_head_lht;
		} else if (expressNo.equals("nd")) {
			headIconId = R.drawable.icon_head_nd;
		} else if (expressNo.equals("post")) {
			headIconId = R.drawable.icon_head_post;
		} else if (expressNo.equals("qf")) {
			headIconId = R.drawable.icon_head_qf;
		} else if (expressNo.equals("qy")) {
			headIconId = R.drawable.icon_head_qy;
		} else if (expressNo.equals("rfd")) {
			headIconId = R.drawable.icon_head_rfd;
		} else if (expressNo.equals("se")) {
			headIconId = R.drawable.icon_head_se;
		} else if (expressNo.equals("sf")) {
			headIconId = R.drawable.icon_renxiang;// 待完善
		} else if (expressNo.equals("sto")) {
			headIconId = R.drawable.icon_head_sto;
		} else if (expressNo.equals("tt")) {
			headIconId = R.drawable.icon_head_tt;
		} else if (expressNo.equals("ups")) {
			headIconId = R.drawable.icon_renxiang;// 待修改
		} else if (expressNo.equals("yd")) {
			headIconId = R.drawable.icon_head_yd;
		} else if (expressNo.equals("ys")) {
			headIconId = R.drawable.icon_head_ys;
		} else if (expressNo.equals("yt")) {
			headIconId = R.drawable.icon_head_yt;
		} else if (expressNo.equals("zjs")) {
			headIconId = R.drawable.icon_head_zjs;
		} else if (expressNo.equals("zt")) {
			headIconId = R.drawable.icon_head_zt;
		}

		return headIconId;
	}
	
	/**
	 * 判断指定的订单号是否是指定品牌的，仅供参考
	 * 
	 * @param expressNo
	 * @param deliverNo
	 * @return
	 */
	public static boolean isValidExpressNO(String expressNo, String deliverNo) {
		boolean isValid = true;
		if (expressNo.equals("dhl")) {

		} else if (expressNo.equals("dp")) {
			if (deliverNo.length() < 8 || deliverNo.length() > 9) {
				isValid = false;
			}
		} else if (expressNo.equals("ems")) {
			if (deliverNo.length() != 13) {
				isValid = false;
			}

		} else if (expressNo.equals("fedex") || expressNo.equals("fedexInter")) {

		} else if (expressNo.equals("gt")) {
			if (deliverNo.length() != 10) {
				isValid = false;
			} else if (deliverNo.substring(0, 1).matches("[a-zA-Z]")) {
				isValid = false;
			}
		} else if (expressNo.equals("hq")) {

		} else if (expressNo.equals("ht")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("jd")) {
			isValid = (deliverNo.length() == 10 && !deliverNo.substring(0, 1)
					.matches("[a-zA-Z]"))
					|| deliverNo.length() == 12
					|| (deliverNo.length() == 13
					&& !deliverNo.substring(0, 1).matches("[a-zA-Z]") && !deliverNo
					.substring(1, 2).matches("[a-zA-Z]"));
		} else if (expressNo.equals("kj")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("lb")) {

		} else if (expressNo.equals("lht")) {

		} else if (expressNo.equals("nd")) {

		} else if (expressNo.equals("post")) {
			if (deliverNo.length() != 13) {
				isValid = false;
			} else if (!deliverNo.substring(0, 2).matches("[0-9][0-9]")
					&& deliverNo.substring(11, 13).matches("[a-zA-Z][a-zA-Z]")) {
				isValid = false;
			}
		} else if (expressNo.equals("qf")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("qy")) {

		} else if (expressNo.equals("rfd")) {
			if (deliverNo.length() < 13 || deliverNo.length() > 18) {
				isValid = false;
			} else if (deliverNo.length() == 13) {
				if (!deliverNo.substring(0, 2).matches("[0-9][0-9]")) {
					isValid = false;
				}
			} else if (deliverNo.length() > 13 || deliverNo.length() <= 18) {
				isValid = false;
			}
		} else if (expressNo.equals("se")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("sf")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("sto")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("tt")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("ups")) {
			if (deliverNo.length() < 14 || deliverNo.length() > 18) {
				isValid = false;
			} else if (!deliverNo.substring(0, 2).matches("lz")) {
				isValid = false;
			}
		} else if (expressNo.equals("yd")) {
			if (deliverNo.length() != 13) {
				isValid = false;
			}
		} else if (expressNo.equals("ys")) {

		} else if (expressNo.equals("yt")) {
			if (deliverNo.length() != 10 && deliverNo.length() != 12) {
				isValid = false;
			}
		} else if (expressNo.equals("zjs")) {
			if (deliverNo.length() != 10) {
				isValid = false;
			} else if (deliverNo.substring(0, 1).matches("[a-zA-Z]")) {
				isValid = false;
			}
		} else if (expressNo.equals("zt")) {
			if (deliverNo.length() != 12) {
				isValid = false;
			}
		}

		return isValid;
	}

}
