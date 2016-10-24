package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;

import java.util.ArrayList;
import java.util.List;

public class BadDescriptionDAO {
	private static SQLiteDatabase db;

	public static synchronized List<BadDescription> queryAllBadDescription(String company, String job_number) {
		List<BadDescription> descriptions = new ArrayList<>();
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		Cursor cursor = db.query("bad_description", null, "company = ? and job_number = ?", new String[] { company,
				job_number }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				BadDescription description = new BadDescription();
				description.setId(cursor.getInt(cursor.getColumnIndex("id")));
				description.setDescription(cursor.getString(cursor.getColumnIndex("description")));
				description.setCompany(cursor.getString(cursor.getColumnIndex("company")));
				description.setJob_number(cursor.getString(cursor.getColumnIndex("job_number")));
				descriptions.add(description);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return descriptions;
	}

	public static synchronized void delBadDescriptionById(int id) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String delSql = "delete from bad_description where id = " + id;
		db.execSQL(delSql);
	}

	public static synchronized BadDescription addBadDescription(BadDescription description) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("description", description.getDescription());
		values.put("company", description.getCompany());
		values.put("job_number", description.getJob_number());
		db.insert("bad_description", null, values);
		Cursor cursor = db.rawQuery("select * from bad_description where id = (select max(id) from bad_description)",
				null);
		cursor.moveToFirst();
		description.setId(cursor.getInt(cursor.getColumnIndex("id")));
		cursor.close();
		return description;
	}

}
