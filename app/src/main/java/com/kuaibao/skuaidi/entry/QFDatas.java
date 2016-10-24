package com.kuaibao.skuaidi.entry;

import android.text.TextUtils;

import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
/**
 * 	全峰数据
 * @author a4
 *
 */
public class QFDatas implements IScanDatas {
	public JSONArray getUploadDatas(List<E3_order> orders) {
		//FinalDb finalDb = BackUpService.getfinalDb();
//		CourierReviewInfo reviewInfo = finalDb.findAllByWhere(CourierReviewInfo.class,
//				"courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'")
//				.get(0);
		CourierReviewInfo reviewInfo=E3SysManager.getReviewInfo();
		JSONArray wayBills = new JSONArray();
		try {
			String type = "";
			String scanType;
			String site_code;
			for (int i = 0; i < orders.size(); i++) {
				scanType = orders.get(i).getType();
				site_code = E3SysManager.getSiteCode(orders.get(i).getSta_name(), scanType);
				JSONObject wayBill = new JSONObject();
				wayBill.put("waybillNo", orders.get(i).getOrder_number());
				if (scanType.equals("到件")) {
					wayBill.put("forward_station", site_code);
				} else if (scanType.equals("发件")) {
					wayBill.put("next_station", site_code);
				}
				type = orders.get(i).getType();

				// 全峰
				if ("签收件".equals(type)) {
					if (!TextUtils.isEmpty(orders.get(i).getPicPath())) {
						wayBill.put("signPic",
								Utility.bitMapToString(Utility.getImage(orders.get(i).getPicPath())));
					} else {
						wayBill.put("signType", orders.get(i).getWayBillType_E3());
					}
				} else if ("问题件".equals(type)) {

					String[] badDesc = orders.get(i).getWayBillType_E3().split("\n");
					String badSubject = badDesc[0];
					String badType = badDesc[1];
					wayBill.put("type", badSubject);// 未明确参数，先默认传"0"
					wayBill.put("register_site", reviewInfo.getCourierLatticePoint());// 录入网点
					wayBill.put("send_site", "unknown");// 寄件网点
					wayBill.put("register_man", reviewInfo.getCourierName());// 录入人
					wayBill.put("problem_cause", badType);// 问题件内容
					wayBill.put("register_man_department", "unknown");// 问题件内容
				} else if ("收件".equals(type) || "派件".equals(type)) {// 发件不指定发件员
					wayBill.put("operatorCode", orders.get(i).getOperatorCode());
				}

				wayBill.put("scan_time", orders.get(i).getScan_time());
				JSONObject location = new JSONObject();
				location.put("latitude", orders.get(i).getLatitude());
				location.put("longitude", orders.get(i).getLongitude());
				wayBill.put("location", location);
				wayBills.put(wayBill);
			}
		} catch (NumberFormatException e) {
			UtilToolkit.showToast("单号格式异常！");
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return wayBills;

	}
}
