package com.kuaibao.skuaidi.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class FinalDBHelper implements DbUpdateListener {

	private static FinalDBHelper dbHelper;
	private static FinalDb finalDb;
	public static final int FINALDB_VERSION = 8;

	private FinalDBHelper() {
		finalDb = FinalDb.create(SKuaidiApplication.getInstance(), "skuaidi.db", true, FINALDB_VERSION, this);
	}

	public static synchronized FinalDBHelper getInstanse() {
		if (dbHelper == null) {
			dbHelper = new FinalDBHelper();
		}
		return dbHelper;
	}

	public FinalDb getFinalDB() {
		return finalDb;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = oldVersion + 1; i <= newVersion; i++) {
			executeAssetsSQL(db, i);
		}
	}

	private void executeAssetsSQL(SQLiteDatabase db, int sqlIndex) {

		String[] updateSQLs = new String[] { "" };
		try {
			updateSQLs = DBSql.getSQL().get(sqlIndex - 2);
		} catch (IndexOutOfBoundsException e1) {
			e1.printStackTrace();
			return;
		}

		for (int i = 0; i < updateSQLs.length; i++) {
			try {
				db.execSQL(updateSQLs[i]);
			} catch (SQLException e) {
				//Log.d("FINALDB-ERROR", e.toString());
			} catch (RuntimeException e) {
				//Log.d("FINALDB-ERROR", e.toString());
			}
		}
	}

	private static final class DBSql {
		static final List<String[]> sqls = new ArrayList<String[]>();
		static {
			final String[] version2 = new String[] { "alter table circleexpresstucaoinfo add message varchar",
					"alter table circleexpresstucaoinfo add pic varchar" };
			final String[] version3 = new String[] { "alter table CallRecordingMp3 add cacheOrderId integer" };
			final String[] version4 = new String[] { "alter table CustomerCallLog add type integer" };
			final String[] version5 = new String[] { "alter table CustomerCallLog add isCanAddMSG integer",
					"alter table CustomerCallLog add isCanAddOrder integer" };
			final String[] version6 = new String[] { "alter table CustomerCallLog add isUploaded integer",
					"alter table CourierReviewInfo add courierLatticePointId varchar" };
			final String[] version7 = new String[] { "alter table CallRecordingMp3 add orderNumber varchar" };
			sqls.add(version2);
			sqls.add(version3);
			sqls.add(version4);
			sqls.add(version5);
			sqls.add(version6);
			sqls.add(version7);
		}

		public static List<String[]> getSQL() {
			return sqls;
		}

	}

}
