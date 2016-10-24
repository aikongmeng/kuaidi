package com.kuaibao.skuaidi.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 数据库管理基类
 * 
 * @author xy
 * @time 2015-3-17 9:54
 * 
 */
public class SkuaidiBaseDBHelper extends SQLiteOpenHelper {
	private static Context mContext;
	private static String mBaseDBSql;
	private static String mDb_path;
	private static final String BASE_DB_PATH = "schema";

	/**
	 * 
	 * 构造函数
	 * 
	 * @param context
	 * @param name
	 * @param version
	 *            数据库版本
	 * @param db_path
	 *            数据库根路径
	 * @param baseDBSqlFileName
	 *            数据库第一个版本的SQL文件名
	 */
	public SkuaidiBaseDBHelper(Context context, String name, int version, String db_path, String baseDBSqlFileName) {
		super(context, name, null, version);
//		KLog.i("kb","db name :" + name);
//		KLog.i("kb","db version :" + version);
//		KLog.i("kb","db db_path :" + db_path);
//		KLog.i("kb","db baseDBSqlFileName :" + baseDBSqlFileName);
		mContext = context;
		mBaseDBSql = baseDBSqlFileName;
		mDb_path = BASE_DB_PATH + "/" + db_path + "/";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		executeAssetsSQL(db, mBaseDBSql);
	}

	/**
	 * 读取SQL文件 执行SQL语句
	 * 
	 * @param db
	 * @param schemaName
	 *            .sql文件名
	 */

	private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(mContext.getAssets().open(mDb_path + schemaName), "UTF-8"));

			KLog.i("kb","路径:" + mDb_path + schemaName);
			String line;
			String buffer = "";
			while ((line = in.readLine()) != null) {
				buffer += line;
				if (line.trim().endsWith(";")) {
					KLog.i("kb","sql : " + buffer.replace(";", ""));
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
		} catch (Exception e) {
			//Log.e("db-error", e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				//Log.e("db-error", e.toString());
			}
		}
	}

	/**
	 * 数据库升级
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int changeCnt = newVersion - oldVersion;
		for (int i = 0; i < changeCnt; i++) {
			// 依次执行updatei_i+1文件 由1更新到2 [1_2]，2更新到3 [2_3]
			String schemaName = "update" + (oldVersion + i) + "_" + (oldVersion + i + 1) + ".sql";
			executeAssetsSQL(db, schemaName);
			//清空客户管理的同步时间，这样数据库升级后，会重新全量同步客户管理的数据。（尤其是数据库版本从18到19时，客户数据表增加了一个user_id字段)
			if(oldVersion == 18 && newVersion == 19){
				SkuaidiSpf.setCustomerLastSyncTime(SkuaidiSpf.getLoginUser().getUserId(), "");
			}
		}
	}

}
