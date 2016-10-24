package com.kuaibao.skuaidi.manager;

import android.annotation.SuppressLint;

import com.kuaibao.skuaidi.activity.model.OffLineAddMessageCache;
import com.kuaibao.skuaidi.activity.model.OffLineModifyCusListCache;
import com.kuaibao.skuaidi.application.SKuaidiApplication;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 * 
 * 离线数据管理类
 * @author xy
 * @since 2014-10-15 15:22
 * @version 1
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SkuaidiOffLineProcessingManager {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static FinalDb finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
	private static final String SNAME_ADD_ORDER = "order.cmadd2cm";
	private static final String SNAME_ADD_MESSAGE = "liuyan.add";
	private static final String SNAME_ADD_CUSTOMER = "counterman.consumer.add";
	private static final String SNAME_MIDIFY_CUSTOMER = "counterman.consumer.update";
	private static final String SNAME_DELETE_CUSTOMER = "counterman.consumer.del";
	private static final String SNAME_BATCHADD_CUSTOMER = "counterman.consumer.batadd";
	
	public static void offLineDataProcessing(JSONObject data,Map<String, String> cookie){
		onDataProcessing(data,cookie);
	}
	
	
	/**
	 * 离线请求数据存库
	 * @param data
	 * @param cookie
	 */
	private static void onDataProcessing(JSONObject data,Map<String, String> cookie){
		
		try {
			
			String sname = data.getString("sname");
			
			if(sname.equals(SNAME_ADD_MESSAGE)){//添加留言
				
				OffLineAddMessageCache addMessageCache = new OffLineAddMessageCache();
				
				JSONObject jsonObject = data.getJSONObject("params");
				
				addMessageCache.setBrand(jsonObject.getString("brand"));
				addMessageCache.setVoice_length(jsonObject.getInt("voice_length"));
				addMessageCache.setExp_no(jsonObject.getString("exp_no"));
				
				addMessageCache.setAdd_datetime(dateFormat.format(new Date()));
				addMessageCache.setApp_loc_id(data.getString("app_loc_id"));
				addMessageCache.setClose_pushmsg(data.getInt("close_pushmsg"));
				addMessageCache.setContent(data.getString("content"));
				addMessageCache.setContent_type(data.getString("content_type"));
				addMessageCache.setPush_role(data.getInt("push_role"));
				addMessageCache.setUser_phone(data.getString("user_phone"));
				addMessageCache.setUser_role(data.getInt("user_role"));
				
				finalDb.save(addMessageCache);
				
			}else if(sname.equals(SNAME_ADD_ORDER)){//添加订单
				
				
			}else if(sname.equals(SNAME_ADD_CUSTOMER)){//添加客户
				
				OffLineModifyCusListCache modifyCusListCache = new OffLineModifyCusListCache();
				modifyCusListCache.setCusId("");
				modifyCusListCache.setModifyDate(dateFormat.format(new Date()));
				
				JSONObject jsonObject = data.getJSONObject("param").getJSONObject("field");
				modifyCusListCache.setCm_id(jsonObject.getString("cm_id"));
				modifyCusListCache.setAddress(jsonObject.getString("address"));
				modifyCusListCache.setName(jsonObject.getString("name"));
				modifyCusListCache.setNote(jsonObject.getString("note"));
				modifyCusListCache.setPhone(jsonObject.getString("phone"));
				
				finalDb.save(modifyCusListCache);
				
			}else if(sname.equals(SNAME_MIDIFY_CUSTOMER)){//修改客户
				
				OffLineModifyCusListCache modifyCusListCache = new OffLineModifyCusListCache();
				
				modifyCusListCache.setModifyDate(dateFormat.format(new Date()));
				
				JSONObject json =  data.getJSONObject("param").getJSONObject("where");
				modifyCusListCache.setCusId(json.getString("id"));
				modifyCusListCache.setCm_id(json.getString("cm_id"));
				
				JSONObject jsonObject = data.getJSONObject("param").getJSONObject("field");
				modifyCusListCache.setAddress(jsonObject.getString("address"));
				modifyCusListCache.setName(jsonObject.getString("name"));
				modifyCusListCache.setNote(jsonObject.getString("note"));
				modifyCusListCache.setPhone(jsonObject.getString("phone"));
				
				finalDb.save(modifyCusListCache);
				
			}else if(sname.equals(SNAME_DELETE_CUSTOMER)){//删除客户
				
			}else if(sname.equals(SNAME_BATCHADD_CUSTOMER)){//批量添加客户
				String cm_id = data.getString("cm_id");
				String modifyDate = dateFormat.format(new Date());
				JSONArray array = data.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					
					JSONObject jsonObject = array.getJSONObject(i);
					OffLineModifyCusListCache cache = new OffLineModifyCusListCache();
					cache.setCm_id(cm_id);
					cache.setModifyDate(modifyDate);
					cache.setAddress(jsonObject.getString("address"));
					cache.setCusId("");
					cache.setName(jsonObject.getString("name"));
					cache.setNote(jsonObject.getString("note"));
					cache.setPhone(jsonObject.getString("phone"));
					
					finalDb.save(cache);
					
				}
			}
			
			
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
}
