package com.kuaibao.skuaidi.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.entry.AreaItem;

import java.util.ArrayList;
import java.util.List;

public class AddressDB {
	private static SQLiteDatabase db = new AddressDBHelper().openDatabase();

	/**
	 * 查询所有省份
	 * 
	 * @return
	 */
	public static List<AreaItem> getAllProInfoStrs() {
		List<AreaItem> areas = new ArrayList<AreaItem>();
		String sql = "select id,pid,name,names,level from tbl_area where level = 1";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			AreaItem area = new AreaItem();
			area.setId(cursor.getString(cursor.getColumnIndex("id")));
			area.setPid(cursor.getString(cursor.getColumnIndex("pid")));
			area.setName(cursor.getString(cursor.getColumnIndex("name")));
			area.setNames(cursor.getString(cursor.getColumnIndex("names")));
			area.setLevel(cursor.getString(cursor.getColumnIndex("level")));
			areas.add(area);
		}
		cursor.close();

		return areas;
	}
	/**
	 * 根据父id查询下面的所有子区域[根据省可以市，根据市可查区]
	 * @return
	 */
	public static List<AreaItem> getCityInfoStr(String pid) {
		List<AreaItem> areas = new ArrayList<AreaItem>();
		String sql = "select id,pid,name,names,level from tbl_area where pid = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { pid });
		while (cursor.moveToNext()) {
			AreaItem area = new AreaItem();
			area.setId(cursor.getString(cursor.getColumnIndex("id")));
			area.setPid(cursor.getString(cursor.getColumnIndex("pid")));
			area.setName(cursor.getString(cursor.getColumnIndex("name")));
			area.setNames(cursor.getString(cursor.getColumnIndex("names")));
			area.setLevel(cursor.getString(cursor.getColumnIndex("level")));
			areas.add(area);
		}
		cursor.close();

		return areas;
	}

	/**
	 * 根据首字母缩写或名称查询name
	 * 
	 * @param short_pinyin
	 * @return
	 */
	public static List<AreaItem> getNameforstr(String str) {
		List<AreaItem> areas = new ArrayList<AreaItem>();

		Cursor cursor = null;
		try {
			cursor = db.query("tbl_area", new String[] { "id", "pid", "name", "names", "level" },
					"short_pinyin like ? or names like ?", new String[] { "%" + str + "%", "%" + str + "%" }, null,
					null, null);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		while (cursor.moveToNext()) {
			AreaItem area = new AreaItem();
			area.setId(cursor.getString(cursor.getColumnIndex("id")));
			area.setPid(cursor.getString(cursor.getColumnIndex("pid")));
			area.setName(cursor.getString(cursor.getColumnIndex("name")));
			area.setNames(cursor.getString(cursor.getColumnIndex("names")));
			area.setLevel(cursor.getString(cursor.getColumnIndex("level")));
			areas.add(area);
		}

		cursor.close();

		return areas;
	}

	public static AreaItem getAreafromNames(String str) {
		AreaItem area = new AreaItem();
		Cursor cursor = db.query("tbl_area", new String[] { "id", "pid", "name", "names", "level" }, "names like ?",
				new String[] { "%" + str + "%" }, null, null, null);

		if (cursor.getCount() < 1) {
			cursor.close();

			return null;
		} else {
			cursor.moveToFirst();
			area.setId(cursor.getString(cursor.getColumnIndex("id")));
			area.setPid(cursor.getString(cursor.getColumnIndex("pid")));
			area.setName(cursor.getString(cursor.getColumnIndex("name")));
			area.setNames(cursor.getString(cursor.getColumnIndex("names")));
			area.setLevel(cursor.getString(cursor.getColumnIndex("level")));

			cursor.close();

			return area;
		}

	}

	public static AreaItem getParentArea(String pid) {
		AreaItem areaItem = new AreaItem();
		String sql = "select id,pid,name,names,level from tbl_area where id = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { pid });
		cursor.moveToFirst();
		areaItem.setId(cursor.getString(cursor.getColumnIndex("id")));
		areaItem.setPid(cursor.getString(cursor.getColumnIndex("pid")));
		areaItem.setName(cursor.getString(cursor.getColumnIndex("name")));
		areaItem.setNames(cursor.getString(cursor.getColumnIndex("names")));
		areaItem.setLevel(cursor.getString(cursor.getColumnIndex("level")));

		cursor.close();

		return areaItem;
	}

	// public static List<String> getCityInfoStr(String pro) {
	// List<String> list = new ArrayList<String>();
	// String sql =
	// "select name from tbl_area where pid = (select id from tbl_area where name = ?)";
	// Cursor cursor = db.rawQuery(sql, new String[] { pro });
	// while (cursor.moveToNext()) {
	// String city = cursor.getString(cursor.getColumnIndex("name"));
	// list.add(city);
	// }
	//
	// return list;
	// }

	/**
	 * 根据区名and市id查询id
	 * 
	 * @param county
	 * @return
	 */
	// public static String getId(String county, String city) {
	// String sql =
	// "select id from tbl_area where name = ? and pid = (select id from tbl_area where name = ?)";
	// Cursor cursor = db.rawQuery(sql, new String[] { county, city });
	// String id = "";
	// while (cursor.moveToNext()) {
	// id = cursor.getInt(cursor.getColumnIndex("id")) + "";
	// }
	//
	// return id;
	// }

	/**
	 * 根据id查询全部名称
	 * 
	 * @param id
	 * @return
	 */
	// public static String getNames(String id) {
	// String sql = "select names from tbl_area where id = ?";
	// Cursor cursor = db.rawQuery(sql, new String[] { id });
	// String names = "";
	// while (cursor.moveToNext()) {
	// names = cursor.getString(cursor.getColumnIndex("names"));
	// }
	//
	// return names;
	// }

	/**
	 * 根据区名和id和市名查询全部名称
	 * 
	 * @param name
	 * @return
	 */
	// public static String getNamesforcounty(String county, String city, String
	// id) {
	// String sql =
	// "select names from tbl_area where name = ? and id = ? and names like '%"
	// + city + "%'";
	// Cursor cursor = db.rawQuery(sql, new String[] { county, id });
	// String names = "";
	// while (cursor.moveToNext()) {
	// names = cursor.getString(cursor.getColumnIndex("names"));
	// }
	//
	// return names;
	// }

	/**
	 * 根据区名和市名查询全部名称
	 * 
	 * @param name
	 * @return
	 */
	// public static String getNamesforcounty(String county, String city) {
	// String sql =
	// "select names from tbl_area where name = ? and names like '%"
	// + city + "%'";
	// Cursor cursor = db.rawQuery(sql, new String[] { county });
	// String names = "";
	// while (cursor.moveToNext()) {
	// names = cursor.getString(cursor.getColumnIndex("names"));
	// }
	//
	// return names;
	// }

	/**
	 * 根据全部名称查询id
	 * 
	 * @param names
	 * @return
	 */
	// public static String getIdfornames(String names) {
	// String sql = "select id from tbl_area where names = ?";
	// Cursor cursor = db.rawQuery(sql, new String[] { names });
	// String id = "";
	// while (cursor.moveToNext()) {
	// id = cursor.getInt(cursor.getColumnIndex("id")) + "";
	// }
	//
	// return id;
	// }

}
