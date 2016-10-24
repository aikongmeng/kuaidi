package com.kuaibao.skuaidi.business.nettelephone.calllog.utils.net.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;


/**
 * 业务bean
 *
 */
public class FileService {
	private static DBOpenHelper openHelper;

	private static FileService sFileService;
	public static FileService getInstance(){
		if(sFileService==null || openHelper==null){
			sFileService=new FileService(SKuaidiApplication.getContext());
		}
		return sFileService;
	}
	private FileService(Context context) {
		openHelper = new DBOpenHelper(context);
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 *
	 * @param path
	 * @return
	 */
	public static Map<Integer, Integer> getData(String path) {
		if(openHelper==null){
			return new HashMap<>();
		}
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select threadid, downlength from filedownlog where downpath=?",
						new String[] { path });
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		while (cursor.moveToNext()) {
			data.put(cursor.getInt(0), cursor.getInt(1));
		}
		cursor.close();
		//db.close();
		return data;
	}

//	public Integer getTotalLength(String path){
//		KLog.i("kb","getTotalLength");
//		SQLiteDatabase db = openHelper.getReadableDatabase();
//		Cursor cursor = db
//				.rawQuery(
//						"select totallength from filehttplog where downpath=?",
//						new String[] { path });
//		int totalLength=0;
//		if(cursor.moveToFirst()){
//			totalLength = cursor.getInt(cursor.getColumnIndex("totallength"));
//		}
//		cursor.close();
//		db.close();
//		KLog.i("kb","getTotalLength end");
//		return totalLength;
//	}

	public static void setTotalLength(int totalLength,String path){
		KLog.i("kb","setTotalLength");
		if(openHelper==null){
			return ;
		}
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select totallength from filehttplog where downpath=?",
						new String[] { path });
		int fileLength=0;
		if(cursor.moveToFirst()){
			fileLength = cursor.getInt(cursor.getColumnIndex("totallength"));
		}
		db = openHelper.getWritableDatabase();
		if(fileLength<=0){
			db.execSQL(
					"insert into filehttplog(downpath, totallength) values(?,?)",
					new Object[] { path ,totalLength});
		}else{
			db.execSQL(
					"update filehttplog set totallength=? where downpath=?",
					new Object[] { totalLength,path });
			//updateTotalLength(totalLength,path);
		}

			cursor.close();
			//db.close();
		KLog.i("kb","setTotalLength end");
	}

//	public void updateTotalLength(int totalLength,String path){
//		KLog.i("kb","setTotalLength");
//		SQLiteDatabase db = openHelper.getWritableDatabase();
//		db.execSQL(
//				"update filehttplog set totallength=? where downpath=?",
//				new Object[] { totalLength,path });
//		db.close();
//		KLog.i("kb","setTotalLength end");
//	}

	/**
	 * 保存每条线程已经下载的文件长度
	 *
	 * @param path
	 * @param map
	 */
	public static void save(String path, Map<Integer, Integer> map) {// int threadid,
		if(openHelper==null){
			return ;
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db.execSQL(
						"insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
						new Object[] { path, entry.getKey(), entry.getValue() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		//db.close();
	}



	/**
	 * 实时更新每条线程已经下载的文件长度
	 *
	 * @param path
	 * @param map
	 */
	public static void update(String path, int threadId, int pos) {
		if(openHelper==null){
			return ;
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL(
				"update filedownlog set downlength=? where downpath=? and threadid=?",
				new Object[] { pos, path, threadId });
		//db.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 *
	 * @param path
	 */
	public static void delete(String path) {
		if(openHelper==null){
			return ;
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from filedownlog where downpath=?",
				new Object[] { path });
		//db.close();
	}

	public static void closeDB(){
		if(sFileService==null){
			return;
		}
		if(openHelper==null){
			return;
		}
		openHelper.close();
		openHelper=null;
		sFileService=null;
		KLog.i("kb","close DB finish");
	}

}
