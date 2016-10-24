package com.kuaibao.skuaidi.entry;

import android.text.TextUtils;

import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
/**
 * 申通数据
 * @author a4
 *
 */
public class STODatas implements IScanDatas {

	public JSONArray getUploadDatas(List<E3_order> orders) {

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

				if ("收件".equals(type) || "派件".equals(type)) {
					wayBill.put("operatorCode", orders.get(i).getOperatorCode());
				} else if ("签收件".equals(type)) {
					if (!TextUtils.isEmpty(orders.get(i).getPicPath())) {
						wayBill.put("signPic",
								Utility.bitMapToString(Utility.getImage(orders.get(i).getPicPath())));

					} else {
						wayBill.put("signType", orders.get(i).getWayBillType_E3());
					}
				} else if ("问题件".equals(type)) {
					wayBill.put("badWayBillCode", E3SysManager.getBadWaiBillTypeId(orders.get(i).getWayBillType_E3()));
					if (E3SysManager.getBadWaiBillTypeId(orders.get(i).getWayBillType_E3()) == 0) {
						wayBill.put("badWayBillType", orders.get(i).getWayBillType_E3());
					}
				}
				wayBill.put("scan_time", orders.get(i).getScan_time());
				JSONObject location = new JSONObject();
				location.put("latitude", orders.get(i).getLatitude());
				location.put("longitude", orders.get(i).getLongitude());
				wayBill.put("location", location);
				wayBill.put("weight", orders.get(i).getOrder_weight());
				wayBill.put("resType", orders.get(i).getResType());
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
