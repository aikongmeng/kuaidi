package com.kuaibao.skuaidi.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.kuaibao.skuaidi.activity.model.HistoryOrder;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.entry.CircleExpressTuCaoDetail;
import com.kuaibao.skuaidi.entry.ClickItem;
import com.kuaibao.skuaidi.entry.ComplainInfo;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.entry.MessageInfo;
import com.kuaibao.skuaidi.entry.MyFundsAccount;
import com.kuaibao.skuaidi.entry.MyfundsAccountDetail;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.entry.RangeInfo;
import com.kuaibao.skuaidi.entry.ReceiverInfo;
import com.kuaibao.skuaidi.entry.SendRangeInfo;
import com.kuaibao.skuaidi.entry.ShopInfo;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.entry.VisitBusinessCardInfo;
import com.kuaibao.skuaidi.entry.WuliuInfo;
import com.kuaibao.skuaidi.entry.WuliuItem;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gen.greendao.bean.SKuaidiCircle;


public class JsonXmlParser {

	/**
	 * 获取订单列表
	 * 
	 * @param context
	 * @param handler
	 * @param orderResult
	 *            提供分页支持
	 */
	public static void parseOrderList(Context context, Handler handler, String orderResult) {
		List<Order> orders = null;

		if (orderResult != null && !orderResult.equals("")) {
			try {

				JSONObject json = new JSONObject(orderResult);
				JSONObject body = null;
				if (json.getJSONObject("data") != null) {
					//Log.i("data", "data");
					body = json.getJSONObject("data");
				} else {
					//Log.i("response", "response");
					body = json.getJSONObject("response");
				}

				int total_page;
				total_page = body.getInt("total_page");
				JSONArray arrOrders = body.optJSONArray("order");
				if (arrOrders != null) {
					orders = new ArrayList<Order>();
					for (int i = 0; i < arrOrders.length(); i++) {
						Order order = new Order();
						JSONObject orderObj = (JSONObject) arrOrders.get(i);
						order.setId(orderObj.getString("order_id"));
						order.setOrder_type(orderObj.getString("order_type"));
						order.setPhone(orderObj.getString("phone_number"));
						order.setAddress(orderObj.getString("address"));
						order.setPs(orderObj.getString("ps"));
						order.setTime(orderObj.getString("create_time"));
						order.setNewIm(orderObj.getInt("unReadImCount"));
						order.setIsread(orderObj.getInt("unReadOrder"));
						order.setType(orderObj.getString("type"));
						order.setInform_sender_when_sign("inform_sender_when_sign");
						order.setOrder_state_cname(orderObj.getString("transportation_status"));
						String deliverNO = orderObj.getString("express_number");
						if (deliverNO != null && !deliverNO.equals("") && !deliverNO.equals("null")) {
							order.setDeliverNo(deliverNO);
						} else {
							order.setDeliverNo("");
						}
						orders.add(order);
					}
				} else {
					orders = new ArrayList<Order>();
				}

				Message msg = new Message();
				msg.what = Constants.ORDER_PARSE_OK;
				msg.arg1 = total_page;
				msg.obj = orders;
				// //System.out.println(orders.toString() + "-----JsonXmlParer");
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.ORDER_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 获取历史订单列表
	 * 
	 * @param context
	 * @param handler
	 * @param orderResult
	 * 
	 */
	public static void parseHistoryOrderList(Context context, Handler handler, String orderResult) {
		List<HistoryOrder> orders = null;

		if (orderResult != null && !orderResult.equals("")) {
			try {
				JSONObject json = new JSONObject(orderResult);
				JSONArray datas = json.optJSONArray("data");

				// SkuaidiSpf.setVerified(context, verify_status);
				if (datas != null) {
					orders = new ArrayList<HistoryOrder>();
					for (int i = 0; i < datas.length(); i++) {
						HistoryOrder order = new HistoryOrder();
						JSONObject orderObj = (JSONObject) datas.get(i);
						order.setOrder_id(orderObj.getString("order_id"));
						order.setOrder_number(orderObj.getString("order_number"));
						order.setUser_name(orderObj.getString("user_name"));
						order.setOrder_state(orderObj.getString("order_state"));
						order.setCreate_time(orderObj.getString("create_time"));
						order.setDeal_time(orderObj.getString("deal_time"));
						order.setExpress_rand(orderObj.getString("express_rand"));
						order.setCounterman_mobile(orderObj.getString("counterman_mobile"));
						order.setSend_user_mobile(orderObj.getString("send_user_mobile"));
						order.setSend_address_detail(orderObj.getString("send_address_detail"));
						order.setType(orderObj.getString("type"));
						if (TextUtils.isEmpty(orderObj.getString("express_number"))) {
							order.setExpress_number("");
						} else {
							order.setExpress_number(orderObj.getString("express_number"));
						}
						if (TextUtils.isEmpty(orderObj.getString("transportation_status"))) {
							order.setTransportation_status("");
						} else {
							order.setTransportation_status(orderObj.getString("transportation_status"));
						}
						orders.add(order);
					}
				} else {
					orders = new ArrayList<HistoryOrder>();
				}

				Message msg = new Message();
				msg.what = Constants.ORDER_PARSE_OK;
				msg.arg1 = 10000001;
				msg.obj = orders;
				// //System.out.println(orders.toString() + "-----JsonXmlParer");
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.ORDER_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}


	/**
	 * 发送订单IM
	 * 
	 * @param handler
	 * @param orderImAddResult
	 * @param contentType
	 */
	public static void parseOrderImAdd(Handler handler, String orderImAddResult, int contentType) {

		if (orderImAddResult != null & !orderImAddResult.equals("")) {
			try {
				JSONObject json = new JSONObject(orderImAddResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				String status = body.getString("status");

				Message msg = new Message();
				msg.what = Constants.ORDER_IM_ADD_PARSE_OK;
				msg.arg2 = contentType;
				if (!status.equals("success")) {
					msg.arg1 = 0;
					String desc = body.getString("desc");
					msg.obj = desc;
				} else {
					msg.arg1 = 1;
					String oiid = body.getString("oiid");
					msg.obj = oiid;
				}
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.ORDER_IM_ADD_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 解析留言列表筛选功能 gudd TODO
	 * 
	 * @param paserly
	 * @throws JSONException
	 */
	public static void parseScreeningLiuyanList(Context context, Handler handler, String paserly) throws JSONException {
		if (paserly != null && !paserly.equals("")) {
			List<MessageInfo> messages = null;
			MessageInfo message = null;

			JSONObject jsonLiuyanList = new JSONObject(paserly);
			JSONObject response = jsonLiuyanList.getJSONObject("response");
			JSONObject body = response.getJSONObject("body");
			int total_page;
			String status;
			status = body.getString("status");
			total_page = body.getInt("total_page");

			Message msg = new Message();
			msg.what = Constants.LIUYANLIST_GET_SCREENING_SUCCESS;
			if (!status.equals("success")) {
				msg.arg1 = 0;
				String desc = body.getString("desc");
				msg.obj = desc;
			} else {
				msg.arg1 = 1;
				messages = new ArrayList<MessageInfo>();
				JSONArray liuyanlist = body.optJSONArray("liuyanList");
				if (liuyanlist != null) {
					for (int i = 0; i < liuyanlist.length(); i++) {
						message = new MessageInfo();
						JSONObject messageObj = (JSONObject) liuyanlist.get(i);
						message.setLyId(messageObj.getString("lyid"));
						message.setTargetId(messageObj.getString("target_id"));
						SkuaidiSpf.savetargetid(context, messageObj.getString("target_id"));
						message.setOrderNo(messageObj.getString("exp_no"));
						message.setMessageType(messageObj.getInt("liuyan_type"));
						message.setSpeakerId(messageObj.getString("speaker_id"));
						message.setSpeakRole(messageObj.getInt("speaker_role"));
						message.setPhone_num(messageObj.getString("user_phone"));
						message.setTip(messageObj.getString("tip"));
						if (messageObj.getString("content").contains(".amr")) {
							message.setContentType(Constants.TYPE_YUYIN);
							message.setVoiceContent(messageObj.getString("content"));
						} else {
							message.setContentType(Constants.TYPE_TXT);
							message.setTxtContent(messageObj.getString("content"));
						}
						message.setTime(messageObj.getLong("speak_time"));
						message.setNotRead(messageObj.optInt("nrCount", 0));
						messages.add(message);
					}
					msg.arg2 = total_page;
					msg.obj = messages;
				} else {
					msg.obj = new ArrayList<MessageInfo>();
				}
			}
			handler.sendMessage(msg);

		}
	}


	/**
	 * 留言列表
	 * 
	 * @param handler
	 * @param complainResult
	 *            投诉详情结果解析
	 */
	public static void parseComplain(Handler handler, String complainResult) {
		List<ComplainInfo> complains;
		ComplainInfo complain;

		if (complainResult != null & !complainResult.equals("")) {
			try {
				JSONObject json = new JSONObject(complainResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				String status = body.getString("status");
				int total_page;
				total_page = body.getInt("total_page");

				Message msg = new Message();
				msg.what = Constants.COMPLAIN_PARSE_SUCCESS;
				if (!status.equals("success")) {
					msg.arg1 = 0;
					String desc = body.getString("desc");
					msg.obj = desc;
				} else {
					msg.arg1 = 1;
					complains = new ArrayList<ComplainInfo>();
					JSONArray list = body.optJSONArray("list");
					if (list != null) {
						for (int i = 0; i < list.length(); i++) {
							JSONObject tempcomplain = (JSONObject) list.get(i);
							complain = new ComplainInfo();
							complain.setComplainId(tempcomplain.getString("id"));
							complain.setDeliverNo(tempcomplain.getString("deliver_no"));
							complain.setContactMobile(tempcomplain.getString("contact_mobile"));
							complain.setContent(tempcomplain.getString("content"));

							complain.setPhoto(tempcomplain.getString("photo"));
							complain.setCreatTime(tempcomplain.getString("create_time"));
							complain.setDealStatus(tempcomplain.getString("status"));
							complain.setUnRead(tempcomplain.optInt("unReadCount", 0));
							// 状态：'未受理'、'已受理'、'已完成'、'受理中'、'已拒绝'
							if (complain.getDealStatus().equals("已受理")) {
								complain.setDealResult(tempcomplain.getString("result"));
								complain.setDealTime(tempcomplain.getString("shouli_time"));
								complains.add(complain);
							}

						}
						msg.arg2 = total_page;
						msg.obj = complains;
					} else {
						msg.obj = null;
					}
				}
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.COMPLAIN_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 留言详情
	 * 
	 * @param handler
	 * @param complainContentResult
	 */
	public static void parseComplainContent(Handler handler, String complainContentResult) {
		ComplainInfo complain = null;

		if (complainContentResult != null & !complainContentResult.equals("")) {
			try {
				JSONObject json = new JSONObject(complainContentResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				String status = body.getString("status");

				Message msg = new Message();
				msg.what = Constants.COMPLAIN_CONTENT_PARSE_SUCCESS;
				if (!status.equals("success")) {
					msg.arg1 = 0;
					String desc = body.getString("desc");
					msg.obj = desc;
				} else {
					msg.arg1 = 1;
					JSONObject list = body.getJSONObject("list");
					complain = new ComplainInfo();
					complain.setCreatTime(list.get("create_time").toString());
					complain.setContent(list.get("content").toString());
					complain.setDealTime(list.get("shouli_time").toString());
					complain.setDealResult(list.get("result").toString());
					complain.setPhoto(list.get("photo").toString());
					complain.setContactMobile(list.get("contact_mobile").toString());
					complain.setDeliverNo("");
					msg.obj = complain;
				}
				handler.sendMessage(msg);

			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.COMPLAIN_CONTENT_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 解析访问名片数量和收藏数量
	 * 
	 * @param handler
	 */
	public static void parseVisitBisinessCard(Context context, Handler handler, String result) {

		VisitBusinessCardInfo visitCardInfo;
		visitCardInfo = new VisitBusinessCardInfo();

		if (result != null & !result.equals("")) {
			try {
				JSONObject json = new JSONObject(result);
				JSONObject jsonArr = json.optJSONObject("data");

				Message msg = new Message();
				msg.what = Constants.PARSE_VISIT_CARD_OK;
				if (jsonArr != null) {

					visitCardInfo.setPv_count(jsonArr.getString("pv_count"));
					visitCardInfo.setAdd_count(jsonArr.getString("add_count"));
					visitCardInfo.setPicked_map_count(jsonArr.getString("point_count"));

					SkuaidiSpf.saveFangwenAndShoucang(context, visitCardInfo);
				}
				msg.obj = visitCardInfo;
				handler.sendMessage(msg);

			} catch (Exception e) {
				e.printStackTrace();

				// Message msg = new Message();
				// msg.what = Constants.PARSE_VISIT_CARD_FAILD;
				// handler.sendMessage(msg);
			}
		}

	}

	/**
	 * 解析获取的网点信息
	 * 
	 * @param handler
	 * @param overareaResult
	 * @param express_no
	 * 
	 */
	public static void parseBranchInfo(Handler handler, String overareaResult, String express_no) {
		List<BranchInfo> overAreaInfos = null;

		if (!overareaResult.equals("")) {
			try {
				JSONObject json = new JSONObject(overareaResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				JSONObject result;
				//Log.i("test", "物流流转信息===" + overareaResult);
				try {
					result = body.getJSONObject("result");
					int matches_count = result.getInt("matches_count");
					if (matches_count > 0) {
						JSONObject matches = result.getJSONObject("matches");
						overAreaInfos = getOverAreaList(matches);

						Message msg = new Message();
						msg.what = Constants.BRANCH_PARSE_OK;
						msg.obj = overAreaInfos;
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = Constants.BRANCH_PARSE_OK;
						overAreaInfos = new ArrayList<BranchInfo>();
						msg.obj = overAreaInfos;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = Constants.BRANCH_PARSE_FAILD;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.BRANCH_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * @param detail
	 * @return
	 * @throws JSONException
	 *             解析获取的网点Item
	 */
	private static List<BranchInfo> getOverAreaList(JSONObject detail) throws JSONException {
		List<BranchInfo> overAreaInfos;
		BranchInfo overAreaInfo = null;

		Iterator<String> keyIter = detail.keys();
		String key;
		JSONObject item;

		overAreaInfos = new ArrayList<BranchInfo>();
		overAreaInfos.clear();
		while (keyIter.hasNext()) {
			overAreaInfo = new BranchInfo();
			key = keyIter.next();
			item = (JSONObject) detail.get(key);
			overAreaInfo.setIndexShopId(item.getString("index_shop_id"));
			overAreaInfo.setIndexShopName(item.getString("index_shop_name"));
			overAreaInfo.setShopId(item.getString("shop_id"));
			overAreaInfo.setChannel(item.getString("channel"));

			if (item.optString("customer_service_phone") != null) {
				overAreaInfo.setCustomerServicePhone(item.optString("customer_service_phone"));
			} else {
				overAreaInfo.setCustomerServicePhone("");
			}

			overAreaInfos.add(overAreaInfo);
		}
		return overAreaInfos;
	}

	/**
	 * 超派范围解析
	 * 
	 * @param handler
	 * @param sendRangeResult
	 */
	public static void parseSendRange(Handler handler, String sendRangeResult) {
		//System.out.println("快递查询：   " + sendRangeResult);
		if (sendRangeResult != null && !sendRangeResult.equals("")) {
			try {
				JSONObject json = new JSONObject(sendRangeResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				JSONObject result = body.getJSONObject("result");

				SendRangeInfo sendRangeInfo = new SendRangeInfo();
				List<RangeInfo> sendranges;
				List<String> notsendranges;

				RangeInfo tempRangeInfo;
				List<String> roadnumbers;
				int count = 0;

				JSONObject deliver = result.optJSONObject("deliver");
				// //System.out.println(deliver);
				if (deliver != null) {

					JSONArray road = deliver.getJSONArray("road");
					sendranges = new ArrayList<RangeInfo>();
					sendranges.clear();
					for (int i = 0; i < road.length(); i++) {
						tempRangeInfo = new RangeInfo();

						JSONObject roadinfo = (JSONObject) road.get(i);
						String roadname = roadinfo.getString("road_name");
						tempRangeInfo.setRoadname(roadname);
						tempRangeInfo.setStart(count);
						count = tempRangeInfo.getStart() + roadname.length();
						tempRangeInfo.setEnd(count);
						count += 4;

						JSONArray roadnos = roadinfo.getJSONArray("road_number");
						roadnumbers = new ArrayList<String>();
						roadnumbers.clear();
						for (int j = 0; j < roadnos.length(); j++) {
							String roadno = roadnos.getString(j).replaceAll("&nbsp;", " ");

							roadnumbers.add(roadno);
						}
						tempRangeInfo.setRoad_numbers(roadnumbers);
						sendranges.add(tempRangeInfo);
					}
				} else {
					sendranges = null;
				}
				JSONObject nodeliver = result.optJSONObject("nodeliver");
				// //System.out.println(nodeliver);
				if (nodeliver != null) {
					notsendranges = new ArrayList<String>();
					notsendranges.clear();
					JSONArray overstepRanges = nodeliver.getJSONArray("overstep_range");
					for (int i = 0; i < overstepRanges.length(); i++) {
						String overstepRange = overstepRanges.getString(i);
						notsendranges.add(overstepRange);
					}
				} else {
					notsendranges = null;
				}
				sendRangeInfo.setSendranges(sendranges);
				sendRangeInfo.setNotsendranges(notsendranges);

				Message msg = new Message();
				msg.what = Constants.SEND_RANGE_PARSE_OK;
				msg.obj = sendRangeInfo;
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.SEND_RANGE_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 给定地址是否数据派送范围
	 * 
	 * @param handler
	 * @param overareaResult
	 */
	public static void parseOverarea(Handler handler, String overareaResult) {
		if (overareaResult != null && !overareaResult.equals("")) {
			try {
				JSONObject json = new JSONObject(overareaResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				String result = body.getString("result");

				Message msg = new Message();
				msg.what = Constants.OVERAREA_PARSE_OK;
				msg.obj = result.toString();
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.OVERAREA_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}

	// 超派***********************************************************************************

	// 快递查询***********************************************************************************
	/**
	 * 运单跟踪
	 *  @param handler
	 * @param findExpressResult
	 */
	public static void parseFindExpress(Handler handler, String findExpressResult) {
		//Log.i("test", "===返回==" + findExpressResult);
		if (!findExpressResult.equals("")) {
			WuliuInfo wuliuInfo = new WuliuInfo();

			try {
				JSONObject json = new JSONObject(findExpressResult);
				JSONObject response = json.optJSONObject("response");
				JSONObject body = response.optJSONObject("body");
				if (body == null) {
					Message msg = new Message();
					msg.what = Constants.FIND_EXPRESS_PAISE_FAID;
					handler.sendMessage(msg);
					return;
				}

				String status = body.optString("status");
				if (status.equals("fail")) {
					String desc = body.optString("desc");
					Message msg = new Message();
					msg.what = Constants.FIND_EXPRESS_PAISE_FAID;
					msg.obj = desc;
					handler.sendMessage(msg);
					return;
				}

				// if (express.equals("sto") || express.equals("zt")
				// || express.equals("yd")) {
				JSONObject getexception = body.optJSONObject("GetException");
				if (getexception != null) {
					String isException;
					isException = getexception.opt("exception").toString();

					if (isException == null || isException.equals("0")) {
						wuliuInfo.setIsException("0");
					} else if (isException.equals("1")) {
						wuliuInfo.setIsException("1");
						wuliuInfo.setExceptionName(getexception.get("name ").toString());
						wuliuInfo.setExceptionType(getexception.get("type").toString());
						wuliuInfo.setExceptionReason(getexception.get("reason").toString());
						wuliuInfo.setExceptionId(getexception.get("exception_id").toString());
						wuliuInfo.setExceptionMessage(getexception.get("message").toString());
					}
					// }
				}

				JSONObject getstatus = body.optJSONObject("GetStatus");
				if (getstatus != null) {

					wuliuInfo.setStatus(getstatus.opt("position").toString());

					if (getstatus.optString("record") != null && !getstatus.optString("record").equals("null")) {
						wuliuInfo.setRecord(getstatus.get("record").toString());
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = sdf.format(date);
						time = time.substring(5, time.length());
						wuliuInfo.setFirst_time(time.substring(0, 11));
					} else {
						wuliuInfo.setRecord("");
					}
					if (getstatus.optString("name") != null && !getstatus.optString("name").equals("null")) {
						wuliuInfo.setShopName(getstatus.get("name").toString());
					} else {
						wuliuInfo.setShopName("");
					}

					if (getstatus.has("home_shop_id")) {
						wuliuInfo.setHomeShopId(getstatus.get("home_shop_id").toString());
					} else {
						wuliuInfo.setHomeShopId("");
					}
					if (getstatus.has("customer_service_phone")) {
						wuliuInfo.setCustomerServicePhole(getstatus.get("customer_service_phone").toString());
					} else {
						wuliuInfo.setCustomerServicePhole("");
					}

					if (wuliuInfo.getCustomerServicePhole().indexOf(",") != -1) {
						wuliuInfo.setCustomerServicePhole(wuliuInfo.getCustomerServicePhole().substring(0, wuliuInfo.getCustomerServicePhole().indexOf(",")));
					}
				}
				JSONArray getwuliu = body.optJSONArray("GetWuliu_phone");
				if (getwuliu != null && getwuliu.length() > 0) {
					List<WuliuItem> wuliuItems = new ArrayList<WuliuItem>();

					for (int i = 0; i < getwuliu.length(); i++) {
						if (KuaiBaoStringUtilToolkit.isEmpty(getwuliu.optString(i))) {
							continue;
						}
						String str = getwuliu.getString(i).toString();

						// String str = getwuliu.get(i).toString()
						// .replaceAll(" ", "");

						WuliuItem wuliuItem = new WuliuItem();
						int indexSpace1 = str.indexOf(' ');
						String date = str.substring(0, indexSpace1);
						wuliuItem.setDate(date);
						int indexSpace2 = str.indexOf(' ', indexSpace1 + 1);
						// wuliuItem.setTime(str.substring(indexSpace1 + 1,
						// indexSpace2));
						if(date.length()==10){
							wuliuItem.setTime(str.substring(11, 19));
						}else if(date.length()==9){
							wuliuItem.setTime(str.substring(10, 19));
						}else if(date.length()==8){
							wuliuItem.setTime(str.substring(9, 18));
						}
						
						String detail = str.substring(indexSpace2 + 1);

						int positionFlag = detail.indexOf("$");
						int namepositionflag = str.indexOf("#");
						// 截取中间的名字出来
						String name = "";
						if (namepositionflag != -1) {

							Pattern p = Pattern.compile("#(.*):");
							Matcher m = p.matcher(str);
							if (m.find()) {
								name = str.substring(m.start(), m.end());
								// name = name.substring(1, name.length()-1);
							}

						}

						if (positionFlag != -1) {
							List<ClickItem> clicks = new ArrayList<ClickItem>();
							String tempString = "";
							while (positionFlag != -1) {
								ClickItem clickItem = new ClickItem();
								clickItem.setStart(positionFlag);

								tempString = detail.substring(positionFlag + 1);
								String branchInfo = tempString.substring(0, tempString.indexOf("$"));

								clickItem.setEnd(positionFlag + branchInfo.indexOf(":"));
								clickItem.setExpressName(branchInfo.substring(0, branchInfo.indexOf(":")));
								clickItem.setExpressId(branchInfo.substring(branchInfo.indexOf(":") + 1, branchInfo.indexOf(",")));
								detail = detail.replace("$" + branchInfo + "$", clickItem.getExpressName());
								// //System.out.println(detail + " detail");
								clicks.add(clickItem);
								positionFlag = detail.indexOf("$");

							}
							wuliuItem.setClicks(clicks);
							wuliuItem.setSender_name(name);
						} else {

						}
						wuliuItem.setDetail(detail);
						wuliuItems.add(wuliuItem);
					}
					wuliuInfo.setWuliuItems(wuliuItems);
				}
				JSONObject deliver_info = body.optJSONObject("deliver");
				// 返回收件人信息
				if (deliver_info != null) {
					wuliuInfo.setDiliver_phone(deliver_info.optString("courier_phone").toString());

				}

				Message msg = new Message();
				msg.what = Constants.FIND_EXPRESS_PAISE_OK;
				msg.obj = wuliuInfo;
				handler.sendMessage(msg);
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = Constants.FIND_EXPRESS_PAISE_FAID;
				handler.sendMessage(msg);
			}

		}
	}

	/**
	 * 物流信息中的网点信息
	 * 
	 * @param context
	 * @param handler
	 * @param getBranchResult
	 */
	public static void parseBranchInfo(Context context, Handler handler, String getBranchResult) {
		BranchInfo branchInfo = null;
		if (getBranchResult != null && !getBranchResult.equals("")) {
			try {
				//Log.i("iii", "返回的物流公司信息=====" + getBranchResult);
				JSONObject json = new JSONObject(getBranchResult);
				JSONObject response = json.getJSONObject("response");
				JSONObject body = response.getJSONObject("body");
				JSONObject result = body.getJSONObject("result");
				branchInfo = new BranchInfo();
				branchInfo.setShopId(result.get("index_shop_id").toString());
				branchInfo.setIndexShopName(result.get("index_shop_name").toString());
				branchInfo.setCustomerServicePhone(result.get("customer_service_phone").toString());
				branchInfo.setAddress_detail(result.get("address_detail").toString());
				Message msg = new Message();
				msg.what = Constants.BRANCH_PARSE_OK;
				msg.obj = branchInfo;
				handler.sendMessage(msg);
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = Constants.BRANCH_PARSE_FAILD;
				handler.sendMessage(msg);
			}
		}
	}
	// 快递圈相关

	public static void parseCircleGetHead(Handler handler, String result) {
		if (result != null && !result.equals("")) {
			SKuaidiCircle cirTuCaoInfo = new SKuaidiCircle();
			Message msg = new Message();
			try {
				JSONObject json = new JSONObject(result);
				JSONArray data = json.getJSONArray("data");
				JSONObject object = data.optJSONObject(0);
				if(object==null)
					return;
				cirTuCaoInfo.setId(object.optString("id"));
				cirTuCaoInfo.setWduser_id(object.optString("wduser_id"));
				cirTuCaoInfo.setShop(object.optString("shop"));
				cirTuCaoInfo.setBrand(object.optString("brand"));
				cirTuCaoInfo.setCounty(object.optString("county"));
				cirTuCaoInfo.setContent(object.optString("content"));
				cirTuCaoInfo.setUpdate_time(object.optString("update_time"));
				cirTuCaoInfo.setPic(object.optString("pic"));
				cirTuCaoInfo.setIs_good(object.optBoolean("is_good"));
				cirTuCaoInfo.setHuifu(object.optString("huifu"));
				cirTuCaoInfo.setGood(object.optString("good"));
				cirTuCaoInfo.setMessage(object.optString("message"));
				msg.what = Constants.CIRCLE_GET_HEADINFO_OK;
				msg.obj = cirTuCaoInfo;
			} catch (Exception e) {
				e.printStackTrace();
				UtilToolkit.showToast(result);
				msg=null;
				return;
			}
			handler.sendMessage(msg);

		}
	}

	/**
	 * 解析吐槽留言数据详情
	 *  @param handler
	 * @param result
	 */
	public static void paseTucaoDetail(Handler handler, String result) {
		if (!Utility.isEmpty(result)) {
			Message msg = new Message();
			try {
				JSONObject json = new JSONObject(result);
				JSONArray tucaoDetailList = json.getJSONArray("data");
				List<CircleExpressTuCaoDetail> cirTuCaoDetails = new ArrayList<>();

				if (null != tucaoDetailList && 0 != tucaoDetailList.length()) {
					msg.what = Constants.CIRCLE_EXPRESS_GET_TUCAODETAIL_SUCCESS;
					for (int i = 0; i < tucaoDetailList.length(); i++) {
						JSONObject tucaoDetail = (JSONObject) tucaoDetailList.get(i);
						CircleExpressTuCaoDetail cirTuCaoDetail = new CircleExpressTuCaoDetail();
						cirTuCaoDetail.setZhuti_id(tucaoDetail.getString("id"));
						cirTuCaoDetail.setWduser_id(tucaoDetail.getString("wduser_id"));
						cirTuCaoDetail.setReply_wduser_id(tucaoDetail.getString("reply_wduser_id"));
						cirTuCaoDetail.setDetail_id(tucaoDetail.getString("details_id"));
						cirTuCaoDetail.setReplay_shop(tucaoDetail.getString("reply_shop"));
						cirTuCaoDetail.setShop(tucaoDetail.getString("shop"));
						cirTuCaoDetail.setBrand(tucaoDetail.getString("brand"));
						cirTuCaoDetail.setContent(tucaoDetail.getString("content"));
						cirTuCaoDetail.setCounty(tucaoDetail.getString("county"));
						cirTuCaoDetail.setUpdate_time(tucaoDetail.getString("update_time"));
						cirTuCaoDetail.setMessage(tucaoDetail.getString("message"));
						cirTuCaoDetails.add(cirTuCaoDetail);
					}
					msg.obj = cirTuCaoDetails;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);

		}
	}
	
	
	/**
	 * @Title: paseUserAccount 
	 * @Description:【解析接口返回数据】解析我的资金账户信息
	 * @param result
	 * @author: 顾冬冬
	 * @return MyFundsAccount
	 */
	public static MyFundsAccount paseUserAccount(JSONObject result){
		if(result != null ){
			MyFundsAccount accountInfo = new MyFundsAccount();
			accountInfo.setWduser_id(result.optString("wduser_id"));// 快递员ID
			accountInfo.setAvail_money(result.optString("avail_money"));// 可用余额
			accountInfo.setWithdrawable_money(result.optString("withdrawable_money"));// 可提现金额
			accountInfo.setBaidu_account(result.optString("baidu_account"));// 百度账号
			accountInfo.setBaidu_userid(result.optString("baidu_userid"));// 百度账号ID
			accountInfo.setAlipay_userid(result.optString("alipay_userid"));
			accountInfo.setAlipay_name(result.optString("alipay_name"));// 支付宝真实姓名
			accountInfo.setAlipay_account(result.optString("alipay_account"));// 支付宝账号名称
			accountInfo.setWxpay_name(result.optString("wxpay_name"));// 微信昵称
			accountInfo.setWxpay_openid(result.optString("wxpay_openid"));// 微信OPENID
			accountInfo.setScore(result.optString("score"));// 积分
			
			accountInfo.setTotal_money(result.optString("total_money"));//总金额
			accountInfo.setCan_cash_money(result.optString("can_cash_money"));// 可提现金额
			accountInfo.setCan_sms_count(result.optInt("can_sms_count"));// 可发短信条数
			accountInfo.setCan_ivr_count(result.optInt("can_ivr_count"));// 可云呼次数
			
			JSONObject cash_desc_info = result.optJSONObject("cash_desc_info");
			accountInfo.setCash_desc_info_isShow(cash_desc_info.optString("is_show"));
			accountInfo.setCash_desc_info_desc(cash_desc_info.optString("desc"));
			JSONObject avail_desc_info = result.optJSONObject("avail_desc_info");
			accountInfo.setAvail_desc_info_isShow(avail_desc_info.optString("is_show"));
			accountInfo.setAvail_desc_info_desc(avail_desc_info.optString("desc"));
			return accountInfo;
		}
		return null;
	}

	/**
	 * 解析我的资金账户余额
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void paseMyFundsAccount2(Context context, Handler handler, JSONObject result) {

		if (result != null && !result.equals("")) {
			Message msg = new Message();
			MyFundsAccount myFundsAccount = null;
			try {
				myFundsAccount = new MyFundsAccount();
				JSONObject data = result.getJSONObject("data");
				myFundsAccount.setId(data.getString("id"));
				myFundsAccount.setWduser_id(data.getString("wduser_id"));// 快递员ID
				myFundsAccount.setAvail_money(data.getString("avail_money"));// 剩余金额
				myFundsAccount.setWithdrawable_money(data.getString("withdrawable_money"));// 可提现金额
				myFundsAccount.setNotavail_money(data.getString("notavail_money"));// 冻结金额
				myFundsAccount.setBaidu_account(data.getString("baidu_account"));// 百度账号
				myFundsAccount.setBaidu_userid(data.getString("baidu_userid"));// 百度账号ID
				myFundsAccount.setScore(data.optString("score"));// 积分
				myFundsAccount.setAlipay_name(data.optString("alipay_name"));// 支付宝真实姓名
				myFundsAccount.setAlipay_account(data.optString("alipay_account"));// 支付宝账号名称
				myFundsAccount.setWxpay_name(data.optString("wxpay_name"));// 微信昵称
				myFundsAccount.setWxpay_openid(data.optString("wxpay_openid"));// 微信OPENID
				
				msg.what = Constants.ZHIFU_GETINFO_SUCCESS;
				msg.obj = myFundsAccount;
			} catch (JSONException e) {
				e.printStackTrace();

			}
			handler.sendMessage(msg);
		}

	}

	/**
	 * 解析我的资金账户余额
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void paseMyFundsAccount(Context context, Handler handler, String result) {

		if (result != null && !result.equals("")) {
			Message msg = new Message();
			MyFundsAccount myFundsAccount = null;
			try {
				myFundsAccount = new MyFundsAccount();
				JSONObject json = new JSONObject(result);
				JSONObject data = json.getJSONObject("data");
				myFundsAccount.setId(data.getString("id"));
				myFundsAccount.setWduser_id(data.getString("wduser_id"));
				myFundsAccount.setAvail_money(data.getString("avail_money"));
				myFundsAccount.setNotavail_money(data.getString("notavail_money"));
				myFundsAccount.setBaidu_account(data.getString("baidu_account"));
				myFundsAccount.setBaidu_userid(data.getString("baidu_userid"));

				msg.what = Constants.ZHIFU_GETINFO_SUCCESS;
				msg.obj = myFundsAccount;
			} catch (JSONException e) {
				e.printStackTrace();

			}
			handler.sendMessage(msg);
		}

	}

	/**
	 * 解析我的资金流水信息
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void paseMyfundsAccountDetail2(Context context, Handler handler, JSONObject result) {
		if (result != null) {
			Message msg = new Message();
			MyfundsAccountDetail myFundsAccountDetail = null;
			List<MyfundsAccountDetail> myfundsAccountDetails = null;
			myfundsAccountDetails = new ArrayList<MyfundsAccountDetail>();
			try {
				JSONObject data = result.getJSONObject("data");
				int count_pageStr = data.getInt("count_page");
				int total_recordsStr = data.getInt("total_records");
				JSONArray listArray = data.getJSONArray("list");
				if (listArray != null) {
					for (int i = 0; i < listArray.length(); i++) {
						JSONObject accountDetaild = (JSONObject) listArray.get(i);
						myFundsAccountDetail = new MyfundsAccountDetail();

						myFundsAccountDetail.setId(accountDetaild.getString("id"));
						myFundsAccountDetail.setType(accountDetaild.getString("type"));
						myFundsAccountDetail.setWduser_id(accountDetaild.getString("wduser_id"));
						myFundsAccountDetail.setMoney(accountDetaild.getString("money"));
						myFundsAccountDetail.setTrade_number(accountDetaild.getString("trade_number"));
						myFundsAccountDetail.setDesc(accountDetaild.getString("desc"));
						myFundsAccountDetail.setResultTypeStr(accountDetaild.getString("ResultTypeStr"));
						if (accountDetaild.getString("type").equals("in")) {
							myFundsAccountDetail.setOrder_number(accountDetaild.getString("order_number"));
							myFundsAccountDetail.setIncome_type(accountDetaild.getString("income_type"));
							myFundsAccountDetail.setIncome_type_val(accountDetaild.getString("income_type_val"));
							myFundsAccountDetail.setAvailable_money(accountDetaild.getString("available_money"));
							myFundsAccountDetail.setGet_time(accountDetaild.getString("get_time"));
							myFundsAccountDetail.setIs_available(accountDetaild.getString("is_available"));
							myFundsAccountDetail.setAvail_time(accountDetaild.getString("avail_time"));
						} else if (accountDetaild.getString("type").equals("out")) {
							myFundsAccountDetail.setOutcome_type(accountDetaild.getString("outcome_type"));
							myFundsAccountDetail.setApply_time(accountDetaild.getString("apply_time"));
							myFundsAccountDetail.setSuccess_time(accountDetaild.getString("success_time"));
							myFundsAccountDetail.setIs_successed(accountDetaild.getString("is_successed"));
							myFundsAccountDetail.setOutcome_type_val(accountDetaild.getString("outcome_type_val"));
						}

						myfundsAccountDetails.add(myFundsAccountDetail);
					}
					msg.what = Constants.ZHIFU_GETINFO_DETAIL_SUCCESS;
					msg.obj = myfundsAccountDetails;
					msg.arg1 = count_pageStr;
					msg.arg2 = total_recordsStr;
				}
			} catch (Exception e) {
			}
			handler.sendMessage(msg);
		}
	}

	/**
	 * 解析我的资金流水信息
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void paseMyfundsAccountDetail(Context context, Handler handler, String result) {
		if (result != null && !result.equals("")) {
			Message msg = new Message();
			MyfundsAccountDetail myFundsAccountDetail = null;
			List<MyfundsAccountDetail> myfundsAccountDetails = null;
			myfundsAccountDetails = new ArrayList<MyfundsAccountDetail>();
			try {
				JSONObject json = new JSONObject(result);
				JSONObject data = json.getJSONObject("data");
				String count_pageStr = data.getString("count_page");
				String total_recordsStr = data.getString("total_records");
				JSONArray listArray = data.getJSONArray("list");

				if (listArray != null) {
					for (int i = 0; i < listArray.length(); i++) {
						JSONObject accountDetaild = (JSONObject) listArray.get(i);
						myFundsAccountDetail = new MyfundsAccountDetail();

						myFundsAccountDetail.setId(accountDetaild.getString("id"));
						myFundsAccountDetail.setType(accountDetaild.getString("type"));
						myFundsAccountDetail.setWduser_id(accountDetaild.getString("wduser_id"));
						myFundsAccountDetail.setMoney(accountDetaild.getString("money"));
						myFundsAccountDetail.setTrade_number(accountDetaild.getString("trade_number"));
						myFundsAccountDetail.setDesc(accountDetaild.getString("desc"));
						myFundsAccountDetail.setResultTypeStr(accountDetaild.getString("ResultTypeStr"));
						if (accountDetaild.getString("type").equals("in")) {
							myFundsAccountDetail.setOrder_number(accountDetaild.getString("order_number"));
							myFundsAccountDetail.setIncome_type(accountDetaild.getString("income_type"));
							myFundsAccountDetail.setIncome_type_val(accountDetaild.getString("income_type_val"));
							myFundsAccountDetail.setAvailable_money(accountDetaild.getString("available_money"));
							myFundsAccountDetail.setGet_time(accountDetaild.getString("get_time"));
							myFundsAccountDetail.setIs_available(accountDetaild.getString("is_available"));
							myFundsAccountDetail.setAvail_time(accountDetaild.getString("avail_time"));
						} else if (accountDetaild.getString("type").equals("out")) {
							myFundsAccountDetail.setOutcome_type(accountDetaild.getString("outcome_type"));
							myFundsAccountDetail.setApply_time(accountDetaild.getString("apply_time"));
							myFundsAccountDetail.setSuccess_time(accountDetaild.getString("success_time"));
							myFundsAccountDetail.setIs_successed(accountDetaild.getString("is_successed"));
							myFundsAccountDetail.setOutcome_type_val(accountDetaild.getString("outcome_type_val"));
						}

						myfundsAccountDetails.add(myFundsAccountDetail);
					}
					msg.what = Constants.ZHIFU_GETINFO_DETAIL_SUCCESS;
					msg.obj = myfundsAccountDetails;
					msg.arg1 = Integer.parseInt(count_pageStr);
					msg.arg2 = Integer.parseInt(total_recordsStr);
				}
			} catch (Exception e) {
			}
			handler.sendMessage(msg);
		}
	}

	/**
	 * 解析外单数据信息
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void parseOutSideList(Context context, Handler handler, JSONObject result) {
		Message msg = new Message();
		if (result != null) {
			try {
				List<LatestOutSide> latestOutSides = new ArrayList<LatestOutSide>();
				LatestOutSide latestOutSide = null;
				JSONArray dataArr = result.optJSONArray("retArr");
				if (dataArr != null && dataArr.length() != 0) {
					for (int i = 0; i < dataArr.length(); i++) {
						JSONObject latestOutSideJsonObj = (JSONObject) dataArr.get(i);
						latestOutSide = new LatestOutSide();
						latestOutSide.setId(latestOutSideJsonObj.getString("id"));
						latestOutSide.setUser_mobile(latestOutSideJsonObj.getString("user_mobile"));
						latestOutSide.setMission(latestOutSideJsonObj.getString("mission"));
						latestOutSide.setSend(latestOutSideJsonObj.getString("send"));
						latestOutSide.setPic(latestOutSideJsonObj.getString("pic"));
						latestOutSide.setReceive(latestOutSideJsonObj.getString("receive"));
						latestOutSide.setState(latestOutSideJsonObj.getString("state"));
						latestOutSide.setCreate_time(latestOutSideJsonObj.getString("create_time"));
						latestOutSide.setLimit_time(latestOutSideJsonObj.getString("limit_time"));
						latestOutSide.setPickup_time(latestOutSideJsonObj.getString("pickup_time"));
						latestOutSide.setReward_type(latestOutSideJsonObj.getString("reward_type"));
						latestOutSide.setReward(latestOutSideJsonObj.getString("reward"));
						latestOutSide.setPay(latestOutSideJsonObj.getString("pay"));
						latestOutSide.setPay_first(latestOutSideJsonObj.getString("pay_first"));
						latestOutSide.setWduser_id(latestOutSideJsonObj.getString("wduser_id"));
						latestOutSide.setLat(latestOutSideJsonObj.getString("lat"));
						latestOutSide.setLng(latestOutSideJsonObj.getString("lng"));
						latestOutSide.setSend_lat(latestOutSideJsonObj.getString("send_lat"));
						latestOutSide.setSend_lng(latestOutSideJsonObj.getString("send_lng"));
						latestOutSide.setSend_hash(latestOutSideJsonObj.getString("send_hash"));
						latestOutSide.setSend_receive_distance(latestOutSideJsonObj.getString("send_receive_distance"));
						latestOutSide.setDistance(latestOutSideJsonObj.getString("distance"));
						JSONArray pay_explain = latestOutSideJsonObj.getJSONArray("pay_explain");
						JSONArray delivery_explain = latestOutSideJsonObj.getJSONArray("delivery_explain");
						String[] pay_exp = new String[pay_explain.length()];
						String[] delivery_exp = new String[delivery_explain.length()];
						for (int j = 0; j < pay_explain.length(); j++) {
							String pay_explain_str = pay_explain.getString(j);
							pay_exp[j] = pay_explain_str;
						}
						latestOutSide.setPay_explain(pay_exp);
						for (int x = 0; x < delivery_explain.length(); x++) {
							String delivery_exp_str = delivery_explain.getString(x);
							delivery_exp[x] = delivery_exp_str;
						}
						latestOutSide.setDelivery_explain(delivery_exp);
						latestOutSides.add(latestOutSide);// 实体放入集合
					}
					msg.what = Constants.OUTSIDE_GET_SUCCESS;
					msg.obj = latestOutSides;
				} else {
					msg.what = Constants.OUTSIDE_GET_DATAISNULL;// 未获取到数据
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendMessage(msg);
	}

	/**
	 * 解析外单数据信息
	 * 
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void parseOutSideList2(Context context, Handler handler, JSONObject result) {
		Message msg = new Message();
		if (result != null) {
			try {
				List<LatestOutSide> latestOutSides = new ArrayList<LatestOutSide>();
				LatestOutSide latestOutSide = null;
				JSONArray retArr = result.optJSONArray("retArr");
				if (retArr != null && retArr.length() != 0) {
					for (int i = 0; i < retArr.length(); i++) {
						JSONObject latestOutSideJsonObj = (JSONObject) retArr.get(i);
						latestOutSide = new LatestOutSide();
						latestOutSide.setId(latestOutSideJsonObj.getString("id"));
						latestOutSide.setUser_mobile(latestOutSideJsonObj.getString("user_mobile"));
						latestOutSide.setMission(latestOutSideJsonObj.getString("mission"));
						latestOutSide.setSend(latestOutSideJsonObj.getString("send"));
						latestOutSide.setPic(latestOutSideJsonObj.getString("pic"));
						latestOutSide.setReceive(latestOutSideJsonObj.getString("receive"));
						latestOutSide.setState(latestOutSideJsonObj.getString("state"));
						latestOutSide.setCreate_time(latestOutSideJsonObj.getString("create_time"));
						latestOutSide.setLimit_time(latestOutSideJsonObj.getString("limit_time"));
						latestOutSide.setPickup_time(latestOutSideJsonObj.getString("pickup_time"));
						latestOutSide.setReward_type(latestOutSideJsonObj.getString("reward_type"));
						latestOutSide.setReward(latestOutSideJsonObj.getString("reward"));
						latestOutSide.setPay(latestOutSideJsonObj.getString("pay"));
						latestOutSide.setPay_first(latestOutSideJsonObj.getString("pay_first"));
						latestOutSide.setWduser_id(latestOutSideJsonObj.getString("wduser_id"));
						latestOutSide.setLat(latestOutSideJsonObj.getString("lat"));
						latestOutSide.setLng(latestOutSideJsonObj.getString("lng"));
						latestOutSide.setSend_lat(latestOutSideJsonObj.getString("send_lat"));
						latestOutSide.setSend_lng(latestOutSideJsonObj.getString("send_lng"));
						latestOutSide.setSend_hash(latestOutSideJsonObj.getString("send_hash"));
						latestOutSide.setSend_receive_distance(latestOutSideJsonObj.getString("send_receive_distance"));
						latestOutSide.setDistance(latestOutSideJsonObj.getString("distance"));
						JSONArray pay_explain = latestOutSideJsonObj.getJSONArray("pay_explain");
						JSONArray delivery_explain = latestOutSideJsonObj.getJSONArray("delivery_explain");
						String[] pay_exp = new String[pay_explain.length()];
						String[] delivery_exp = new String[delivery_explain.length()];
						for (int j = 0; j < pay_explain.length(); j++) {
							String pay_explain_str = pay_explain.getString(j);
							pay_exp[j] = pay_explain_str;
						}
						latestOutSide.setPay_explain(pay_exp);
						for (int x = 0; x < delivery_explain.length(); x++) {
							String delivery_exp_str = delivery_explain.getString(x);
							delivery_exp[x] = delivery_exp_str;
						}
						latestOutSide.setDelivery_explain(delivery_exp);
						latestOutSides.add(latestOutSide);// 实体放入集合
					}
					msg.what = Constants.OUTSIDE_GET_SUCCESS;
					msg.obj = latestOutSides;
				} else {
					msg.what = Constants.OUTSIDE_GET_DATAISNULL;// 未获取到数据
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendMessage(msg);
	}

	/**
	 * 解析店铺信息
	 * 
	 */
	public static void parseShopInfo(Context context, Handler handler, JSONObject result) {
		Message msg = new Message();
		if (result != null) {
			try {
				JSONObject desc = result.getJSONObject("retArr");
				String total_records = desc.getString("total_records");// 返回给我总条数
				String total_pages = desc.getString("total_pages");// 返回给我总页数-前提是需要传页码和每页条数
				String page_num = desc.getString("page_num");// 返回给我每页条数-前提是需要传页码和每页条数
				JSONArray data_list = desc.optJSONArray("data");
				if (data_list != null && !data_list.equals("null") && data_list.length() != 0) {
					List<ShopInfo> shopInfos = new ArrayList<ShopInfo>();
					for (int i = 0; i < data_list.length(); i++) {
						JSONObject shopinfoObj = (JSONObject) data_list.get(i);
						ShopInfo shopInfo = new ShopInfo();
						shopInfo.setShop_id(shopinfoObj.getString("shop_id"));
						shopInfo.setCm_phone(shopinfoObj.getString("cm_phone"));
						shopInfo.setShop_logo(shopinfoObj.getString("shop_logo"));
						shopInfo.setShop_name(shopinfoObj.getString("shop_name"));
						shopInfo.setShop_address(shopinfoObj.getString("shop_address"));
						shopInfo.setShop_type(shopinfoObj.getString("shop_type"));
						shopInfo.setShop_desc(shopinfoObj.getString("shop_desc"));
						shopInfo.setPhone(shopinfoObj.getString("phone"));
						shopInfo.setRevenue_demands(shopinfoObj.getString("revenue_demands"));
						shopInfo.setLng(shopinfoObj.getString("lng"));
						shopInfo.setLat(shopinfoObj.getString("lat"));
						shopInfo.setService_times(shopinfoObj.getString("service_times"));
						shopInfo.setBaidu_uid(shopinfoObj.getString("baidu_uid"));
						shopInfos.add(shopInfo);
					}
					msg.what = Constants.GET_SHOP_INFO_SUCCESS;// 获取店铺列表成功
					Object[] object = new Object[4];
					object[0] = shopInfos;
					object[1] = total_records;
					object[2] = total_pages;
					object[3] = page_num;
					msg.obj = object;
				} else {// 没有数据
					msg.what = Constants.GET_SHOP_INFO_FAIL;// 获取店铺列表失败
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendMessage(msg);
	}

	/**
	 * 获取店铺详情-这里主要解析图片
	 * 
	 * @author gudd
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void parseBusinessShopDetail(Context context, Handler handler, JSONObject result) {
		if (result != null) {
			try {
				Message msg = new Message();
				List<ShopInfoImg> shopInfoImgs = new ArrayList<ShopInfoImg>();
				ShopInfoImg shopinfoImg;
				JSONObject json_desc = result.getJSONObject("retArr");
				String shop_pic = json_desc.optString("shop_pic");
				JSONArray shop_pic_arr = new JSONArray(shop_pic);
				if (shop_pic_arr != null && shop_pic_arr.length() != 0) {// 有图片
					for (int i = 0; i < shop_pic_arr.length(); i++) {
						shopinfoImg = new ShopInfoImg();
						JSONObject arr = (JSONObject) shop_pic_arr.get(i);
						String photoName = arr.getString("photo");
						String photoId = arr.getString("spid");
						shopinfoImg.setSpid(photoId);
						shopinfoImg.setPhotoName(Constants.URL_MY_SHOP_IMG_ROOT + photoName);// 保存图片完整网络链接
						shopInfoImgs.add(shopinfoImg);
					}
					msg.what = Constants.GET_SHOP_IMAGE_SUCCESS;// 获取店铺商品图片成功
					msg.obj = shopInfoImgs;
				}
				handler.sendMessage(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 解析其他店铺列表
	 * 
	 * @author gudd
	 * @param context
	 * @param handler
	 * @param result
	 */
	public static void parseAnotherShopInfo(Context context, Handler handler, JSONObject result) {
		Message msg = new Message();
		Object[] objects = new Object[4];
		try {
			JSONObject retArr = result.getJSONObject("retArr");
			String total_records = retArr.getString("total_records");
			String total_pages = retArr.getString("total_pages");
			String page_num = retArr.getString("page_num");
			JSONArray dataArr = retArr.optJSONArray("data");
			if (dataArr != null && dataArr.length() != 0) {
				List<ShopInfo> shopInfos = new ArrayList<ShopInfo>();
				for (int i = 0; i < dataArr.length(); i++) {
					ShopInfo shopInfo = new ShopInfo();
					JSONObject one = (JSONObject) dataArr.get(i);
					shopInfo.setShop_id(one.getString("shop_id"));
					shopInfo.setCm_id(one.getString("cm_id"));
					shopInfo.setCm_phone(one.getString("cm_phone"));
					shopInfo.setShop_logo(one.getString("shop_logo"));
					shopInfo.setShop_name(one.getString("shop_name"));
					shopInfo.setShop_address(one.getString("shop_address"));
					shopInfo.setShop_type(one.getString("shop_type"));
					shopInfo.setShop_desc(one.getString("shop_desc"));
					shopInfo.setPhone(one.getString("phone"));
					shopInfo.setRevenue_demands(one.getString("revenue_demands"));
					shopInfo.setService_times(one.getString("service_times"));
					shopInfo.setBaidu_uid(one.getString("baidu_uid"));
					shopInfo.setDistance(one.getString("distance"));
					shopInfos.add(shopInfo);
				}
				objects[0] = shopInfos;
				objects[1] = total_records;
				objects[2] = total_pages;
				objects[3] = page_num;
				msg.obj = objects;
				msg.what = Constants.GET_SHOP_INFO_SUCCESS;// 获取其他店铺列表成功
			} else {
				msg.what = Constants.GET_SHOP_INFO_FAIL;// 获取其他店铺列表失败
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handler.sendMessage(msg);
	}

	/**
	 * 获取收件人信息
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static List<ReceiverInfo> parseReceiverInfo(JSONObject jsonObject) {
		List<ReceiverInfo> receiverInfos = new ArrayList<>();
		try {
			JSONArray retArray = jsonObject.optJSONArray("retArr");
			for (int i = 0; i < retArray.length(); i++) {
				ReceiverInfo receiverInfo = new ReceiverInfo();
				JSONObject one = (JSONObject) retArray.get(i);
				receiverInfo.setExpress_number(one.optString("express_number"));
				receiverInfo.setRec_name(one.optString("rec_name"));
				receiverInfo.setRec_mobile(one.optString("rec_mobile"));
				receiverInfo.setAddress(one.optString("address"));
				receiverInfos.add(receiverInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiverInfos;
	}

}
