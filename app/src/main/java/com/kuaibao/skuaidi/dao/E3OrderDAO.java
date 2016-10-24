package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.service.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class E3OrderDAO {
	private static SQLiteDatabase db;

	/**
	 * 添加快件
	 * 
	 * @param order
	 * @return
	 */
	public static synchronized int addOrder(E3_order order, String company, String courierNo) {
		if (order == null)
			return -1;
		Cursor cursor = null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues values = orderToValues(order);
			String sql = "select * from E3_order where type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 0 and isUpload = 0";
			String[] args = new String[] { order.getType(), order.getOrder_number(), company, courierNo };
			cursor = db.rawQuery(sql, args);
			if (cursor.getCount() > 0) {
				db.update(
						"E3_order",
						values,
						"type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 0 and isUpload = 0",
						args);
				cursor.close();
			} else {
				db.insert("E3_order", null, values);
			}
			int toUploadCount = E3OrderDAO.getOrderCount(company, courierNo);
			if (toUploadCount > 0)
				AlarmReceiver.count = toUploadCount;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return 0;
	}

	/**
	 * 更新快件
	 * 
	 * @param
	 * @return
	 */
	public static synchronized int updateOrder(List<E3_order> orders, String company, String courierNo) {
		if (orders == null || orders.size() == 0)
			return -1;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			for (E3_order order : orders) {

				ContentValues values = new ContentValues();
				values.put("isUpload", 1);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());
				String upload_time = formatter.format(curDate);
				values.put("upload_time", upload_time);
				db.update(
						"E3_order",
						values,
						"type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 0 and isUpload = 0",
						new String[] { order.getType(), order.getOrder_number(), order.getCompany(),
								order.getCourier_job_no() });

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 缓存快件
	 * 
	 * @param order
	 * @return
	 */
	public static synchronized int cacheOrder(E3_order order, String company, String courierNo) {
		if (order == null)
			return -1;
		Cursor cursor = null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			String sql = "select * from E3_order where type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 1";
			cursor = db.rawQuery(sql, new String[] { order.getType(), order.getOrder_number(), company, courierNo });
			ContentValues values = orderToValues(order);
			if (cursor.getCount() > 0) {
				db.update("E3_order", values,
						"type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 1",
						new String[] { order.getType(), order.getOrder_number(), company, courierNo });
				cursor.close();
				cursor = null;
			} else {
				db.insert("E3_order", null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}

		}

		return 0;
	}

	public static synchronized void updateCacheOrder(E3_order order, String company, String courierNo) {
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues values = orderToValues(order);
			db.update("E3_order", values,
					"type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 1",
					new String[] { order.getType(), order.getOrder_number(), company, courierNo });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @param orders
	 * 
	 * @param company
	 * 
	 * @param courierNo
	 * 
	 * @return
	 */
	public static synchronized void cacheOrders2(List<E3_order> orders, String company, String courierNo) {
		if (orders == null)
			return;
		Cursor cursor = null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.beginTransaction();
			for (E3_order order : orders) {

				String sql = "select * from E3_order where type = ? and order_number = ? and company = ? and courier_job_no = ? ";
				cursor = db
						.rawQuery(sql, new String[] { order.getType(), order.getOrder_number(), company, courierNo });
				ContentValues values = orderToValues(order);
				if (cursor.getCount() > 0) {
					db.update("E3_order", values,
							"type = ? and order_number = ? and company = ? and courier_job_no = ? ",
							new String[] { order.getType(), order.getOrder_number(), company, courierNo });
					cursor.close();
				} else {
					db.insert("E3_order", null, values);
				}

			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

	}
	public static synchronized void cacheOrders(List<E3_order> orders, String company, String courierNo) {
		if (orders == null)
			return;
		Cursor cursor = null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.beginTransaction();
			for (E3_order order : orders) {

				String sql = "select * from E3_order where type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 1";
				cursor = db
						.rawQuery(sql, new String[] { order.getType(), order.getOrder_number(), company, courierNo });
				ContentValues values = orderToValues(order);
				if (cursor.getCount() > 0) {
					db.update("E3_order", values,
							"type = ? and order_number = ? and company = ? and courier_job_no = ? and isCache = 1",
							new String[] { order.getType(), order.getOrder_number(), company, courierNo });
					cursor.close();
				} else {
					db.insert("E3_order", null, values);
				}

			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

	}

	/**
	 * @param order
	 * @return
	 */
	private static ContentValues orderToValues(E3_order order) {
		ContentValues values = new ContentValues();
		values.put("order_number", order.getOrder_number());
		values.put("type_E3", order.getType_E3());
		values.put("type", order.getType());
		values.put("wayBillType_E3", order.getWayBillType_E3());
		values.put("type_extra", order.getType_extra());
		values.put("action", order.getAction());
		values.put("action_desc", order.getType_extra());
		values.put("operatorCode", order.getOperatorCode());
		values.put("sender_name", order.getSender_name());
		values.put("courier_job_no", order.getCourier_job_no());
		values.put("company", order.getCompany());
		values.put("scan_time", order.getScan_time());
		values.put("upload_time", order.getUpload_time());
		values.put("picPath", order.getPicPath());
		values.put("firmname", order.getFirmname());
		values.put("isUpload", order.getIsUpload());
		values.put("problem_desc", order.getProblem_desc());
		values.put("station_name", order.getSta_name());
		values.put("isCache", order.getIsCache());
		values.put("latitude", order.getLatitude());
		values.put("longitude", order.getLongitude());
		values.put("phone_number", order.getPhone_number());
		values.put("order_weight",order.getOrder_weight());
		values.put("resType",order.getResType());//货样/非货样
		values.put("thirdBranch",order.getThirdBranch());//第三方营业厅
		values.put("thirdBranchId",order.getThirdBranchId());//第三方营业厅id
		return values;
	}

	/**
	 * 批量添加快件信息
	 * 
	 * @param orders
	 * @param company
	 * @param courierNo
	 * @return
	 */
	public static synchronized ArrayList<Integer> addOrders(List<E3_order> orders, String company, String courierNo) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		if (orders == null)
			return null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.beginTransaction();
			for (E3_order order : orders) {
				addOrder(order, company, courierNo);
				positions.add(orders.indexOf(order));
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			int toUploadCount = E3OrderDAO.getOrderCount(company, courierNo);
			if (toUploadCount > 0)
				AlarmReceiver.count = toUploadCount;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return positions;

	}

	/**
	 * 根据类型查询快件信息
	 * 
	 * @param type
	 * @param company
	 * @param courierNo
	 * @param
	 * @return
	 */
	public static synchronized ArrayList<E3_order> queryOrderByType(String type, String company, String courierNo) {
		Cursor cursor = null;
		ArrayList<E3_order> orders = new ArrayList<E3_order>();
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String sql_queryall;

			sql_queryall = "select * from E3_order where isUpload = 0 and type = ? and company = ? and courier_job_no = ? and isCache = 0 order by scan_time desc";
			cursor = db.rawQuery(sql_queryall, new String[] { type, company, courierNo });
			while (cursor.moveToNext()) {
				E3_order order = new E3_order();
				getOrderFromCursor(cursor, orders, order);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return orders;

	}

	/**
	 * 查询当天同一类型的所有单号
	 * 
	 * @param type
	 * @param company
	 * @param courierNo
	 * @param
	 * @return uploadedOnly 只包含已经上传单号（用于数据上传内的重复校验）
	 */
	public static synchronized ArrayList<E3_order> queryOrdersToday(String type, String company, String courierNo,
			boolean uploadedOnly) {
		Cursor cursor = null;
		ArrayList<E3_order> orders = new ArrayList<E3_order>();
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = new Date(System.currentTimeMillis());
			String today = formatter.format(curDate);

			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();

			String sql_queryall;
			if (uploadedOnly) {
				sql_queryall = "select * from E3_order where type = ? and company = ? and courier_job_no = ? and scan_time like ? and isCache = 0  and isUpload = 1 order by scan_time desc";

			} else {
				sql_queryall = "select * from E3_order where type = ? and company = ? and courier_job_no = ? and scan_time like ? and isCache = 0 order by scan_time desc";

			}
			cursor = db.rawQuery(sql_queryall, new String[] { type, company, courierNo, today + "%" });

			while (cursor.moveToNext()) {
				E3_order order = new E3_order();
				getOrderFromCursor(cursor, orders, order);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return orders;

	}

	/**
	 * 查询缓存的快件
	 * 
	 * @param type
	 * @param company
	 * @param courierNo
	 * @param
	 * @return
	 */
	public static synchronized ArrayList<E3_order> queryCacheOrder(String type, String company, String courierNo) {
		Cursor cursor = null;
		ArrayList<E3_order> orders = new ArrayList<>();
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String sql_queryall = "select * from E3_order where isUpload = 0 and type = ? and company = ? and courier_job_no = ? and isCache = 1 order by scan_time desc";
			cursor = db.rawQuery(sql_queryall, new String[] { type, company, courierNo });
			while (cursor.moveToNext()) {
				E3_order order = new E3_order();
				getOrderFromCursor(cursor, orders, order);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return orders;

	}

	/**
	 * @param cursor
	 * @param orders
	 * @param order
	 */
	private static void getOrderFromCursor(Cursor cursor, ArrayList<E3_order> orders, E3_order order) {
		order.setAction(cursor.getString(cursor.getColumnIndex("action")));
		order.setAction_desc(cursor.getString(cursor.getColumnIndex("action_desc")));
		order.setCourier_job_no(cursor.getString(cursor.getColumnIndex("courier_job_no")));
		order.setCompany(cursor.getString(cursor.getColumnIndex("company")));
		order.setFirmname(cursor.getString(cursor.getColumnIndex("firmname")));
		order.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
		order.setPicPath(cursor.getString(cursor.getColumnIndex("picPath")));
		order.setScan_time(cursor.getString(cursor.getColumnIndex("scan_time")));
		order.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
		order.setOperatorCode(cursor.getString(cursor.getColumnIndex("operatorCode")));
		order.setType(cursor.getString(cursor.getColumnIndex("type")));
		order.setType_E3(cursor.getString(cursor.getColumnIndex("type_E3")));
		order.setWayBillType_E3(cursor.getString(cursor.getColumnIndex("wayBillType_E3")));
		order.setType_extra(cursor.getString(cursor.getColumnIndex("type_extra")));
		order.setIsUpload(cursor.getInt(cursor.getColumnIndex("isUpload")));
		order.setUpload_time(cursor.getLong(cursor.getColumnIndex("upload_time")));
		order.setProblem_desc(cursor.getString(cursor.getColumnIndex("problem_desc")));
		order.setSta_name(cursor.getString(cursor.getColumnIndex("station_name")));
		order.setIsCache(cursor.getInt(cursor.getColumnIndex("isCache")));
		order.setScan_time(cursor.getString(cursor.getColumnIndex("scan_time")));
		order.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
		order.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
		order.setPhone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
		order.setOrder_weight(cursor.getDouble(cursor.getColumnIndex("order_weight")));
		order.setResType(cursor.getInt(cursor.getColumnIndex("resType")));
		order.setThirdBranch(cursor.getString(cursor.getColumnIndex("thirdBranch")));
		order.setThirdBranchId(cursor.getString(cursor.getColumnIndex("thirdBranchId")));
		orders.add(order);
	}

	/**
	 * 统计未上传快件数量
	 * 
	 * @param company
	 * @param courierNO
	 * @return
	 */
	public static synchronized int getOrderCount(String company, String courierNO) {
		Cursor cursor = null;
		int count = 0;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String sql_queryall = "select * from E3_order where isUpload = 0 and company = ? and courier_job_no = ? and isCache = 0";
			cursor = db.rawQuery(sql_queryall, new String[] { company, courierNO });
			count = cursor.getCount();
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}

		}
		return count;
	}

	/**
	 * 批量删除快件信息
	 * 
	 * @param orders
	 * @throws SQLException
	 */
	public static synchronized void deleteOrders(List<E3_order> orders) throws SQLException {
		if (orders == null)
			return;
		String company = null;
		String courierNO = null;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.beginTransaction();
			for (E3_order order : orders) {
				db.delete(
						"E3_order",
						"order_number = ? and type= ? and company = ? and courier_job_no = ? and isCache = 0",
						new String[] { order.getOrder_number(), order.getType(), order.getCompany(),
								order.getCourier_job_no() });
				company = order.getCompany();
				courierNO = order.getCourier_job_no();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			int toUploadCount = E3OrderDAO.getOrderCount(company, courierNO);
			if (toUploadCount > 0)
				AlarmReceiver.count = toUploadCount;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 批量删除缓存的快件信息
	 * 
	 * @param orders
	 * @throws SQLException
	 */
	public static synchronized void deleteCacheOrders(List<E3_order> orders) throws SQLException {
		if (orders == null)
			return;
		try {

			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.beginTransaction();
			for (E3_order order : orders) {
				db.delete(
						"E3_order",
						"order_number = ? and type= ? and company = ? and courier_job_no = ? and isCache = 1",
						new String[] { order.getOrder_number(), order.getType(), order.getCompany(),
								order.getCourier_job_no() });

			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除快件信息
	 * 
	 * @param e3_order
	 * @throws SQLException
	 */
	public static synchronized int deleteOrder(E3_order e3_order) throws SQLException {
		if (e3_order == null)
			return 0;
		int i = 0;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			i = db.delete(
					"E3_order",
					"isUpload = 0 and order_number = ? and type= ? and company = ? and courier_job_no = ? and isCache = 0",
					new String[] { e3_order.getOrder_number(), e3_order.getType(), e3_order.getCompany(),
							e3_order.getCourier_job_no() });

			int toUploadCount = E3OrderDAO.getOrderCount(e3_order.getCompany(), e3_order.getCourier_job_no());
			if (toUploadCount > 0)
				AlarmReceiver.count = toUploadCount;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return i;
	}

	/**
	 * 删除缓存的快件信息
	 * 
	 * @param e3_order
	 * @throws SQLException
	 */
	public static synchronized void deleteCacheOrder(E3_order e3_order) throws SQLException {
		if (e3_order == null)
			return;
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.delete(
					"E3_order",
					"isCache = 1 and order_number = ? and type= ? and company = ? and courier_job_no = ?",
					new String[] { e3_order.getOrder_number(), e3_order.getType(), e3_order.getCompany(),
							e3_order.getCourier_job_no() });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static synchronized void deleteUploadedOrders(String company, String job_no, String scanTime) {
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.delete("E3_order", "isUpload = 1 and company = ? and courier_job_no = ? and scan_time not like ?",
					new String[] { company, job_no, scanTime + "%" });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
