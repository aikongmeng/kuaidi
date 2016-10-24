package com.kuaibao.skuaidi.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuaibao.skuaidi.util.UtilityDb;

public class SkuaidiDBHelper extends SQLiteOpenHelper {
	Context context;

	public SkuaidiDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context.getApplicationContext(), name, factory, version);
		this.context = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 登陆过的用户 v4创建
		/*
		 * uid 在表中的id mobile 用户名即手机号 time 登陆时间
		 */
		String createTableUser = "create table user(" + "uid integer primary key autoincrement," + "mobile varchar,"
				+ "time integer)";
		db.execSQL(createTableUser);

		// 用于记录发送失败的订单Im消息
		/*
		 * localid 本地派件消息id orderno 订单号 contenttype 内容的类型 content 发送内容
		 * touserrole 发送给谁1表示发送给用户，2表示发送给网点 time 发送时间 mobile
		 * 用户手机号,touserrole为2时，此字段为空
		 */
		String createTableFailedOrderIm = "create table failedorderim(" + "localid integer primary key autoincrement,"
				+ "orderno varchar," + "contenttype integer," + "content varchar," + "touserrole integer,"
				+ "mobile varchar," + "time integer)";
		db.execSQL(createTableFailedOrderIm);

		// 快递查询记录 V1创建
		/*
		 * deliverno 运单号 deliverstate 运单状态 remark 备注 time 查询时间
		 */
		String createTableExpressHistory = "create table expresshistory(" + "deliverno varchar primary key,"
				+ "deliverstate varchar," + "record varchar," + "firsttime varchar," + "remark varchar,"
				+ "time integer)";
		db.execSQL(createTableExpressHistory);

		// 派件模块扫描记录 V2创建表delivernohistory，V4换表为deliverylist,并删除旧表
		/*
		 * topicid 对应记录的ID deliverno 运单号 time 扫描时间（保存最新扫描时间） mobile 用户手机号 status
		 * 最新一条记录的发送状态
		 */
		String createTableDeliveryList = "create table deliverylist(" + "topicid varchar," + "deliverno varchar,"
				+ "time integer," + "mobile varchar," + "status varchar," + "tip varchar)";
		db.execSQL(createTableDeliveryList);

		// 派件消息
		/*
		 * localid 本地派件消息id deliverno 运单号 contenttype 内容的类型 content 发送内容 time
		 * 发送时间 mobile 用户手机号
		 */
		String createTableFailedDeliveryMessage = "create table faileddeliverymessage("
				+ "localid integer primary key autoincrement," + "deliverno varchar," + "contenttype integer,"
				+ "content varchar," + "mobile varchar," + "time integer)";
		db.execSQL(createTableFailedDeliveryMessage);

		// 快递员各类型消息模板 v2为表paijianmodel，v3改为表replymodel
		/*
		 * mid 模板Id modelcontent 模板内容 ischoose 模板是否被选中 time
		 * 最后操作模板（包括选中、添加、修改）的时间 type 模板类型 userid 模板所属用户
		 */
		String createTableReplyModel = "create table replymodel(" + "mid integer primary key autoincrement,"
				+ "modelcontent varchar," + "ischoose integer," + "time integer," + "type integer,"
				+ "userid varchar, tid varchar,apply_time varchar,approve_time varchar,state varchar,title varchar,sort_no varchar,template_type integer,ly_select_status integer,modify_time long)";
		db.execSQL(createTableReplyModel);

		String createTableScan = "create table moredelivery(id integer primary key autoincrement, deliverno varchar(20), status varchar,record varchar, time varchar, remarks varchar)";
		db.execSQL(createTableScan);
		String createTablePhone = "create table phone(id integer primary key autoincrement, deliverno varchar, phone varchar)";
		db.execSQL(createTablePhone);

		/*
		 * tucao_id;// 吐槽id String wduser_id;// 网点用户id shop;// 网点 brand;// 快递公司
		 * county;// 区域 content;// 吐槽内容 update_time;// 更新时间 is_good;//
		 * 点赞状态-boolean类型 huifu;// 回复 String good;// 点赞 pic;//图片（可包含多张图片
		 * message;//快递员（网点+公司 imageurls;// 九宫格图片的URL集合(小图)
		 * imageurlsbig;//九宫格图片的URL集合(大图)
		 */
		String createTableCircleExpress = "create table circleexpress(" + "id integer primary key autoincrement,"
				+ "tucao_id varchar," + "wduser_id varchar," + "shop varchar," + "brand varchar," + "county varchar,"
				+ "content varchar," + "update_time varchar," + "is_good varchar ," + "huifu varchar,"
				+ "good varchar," + "pic varchar," + "message varchar," + "imageurls varchar,"
				+ "imageurlsbig varchar)";
		db.execSQL(createTableCircleExpress);
		/*
		 *id:自增ID//ivid:录音ID//ischoose:是否选中//title:模板标题// file_name：文件名称//  create_time:模板创建时间//voice_length：录音时长//
		 * path_local：保存录音本地路径//path_service: 服务器下载地址 // examine_status：  审核状态// time:模板保存到数据库的时间// user_id:用户id// sort_no:排序号码
		 * modify_time:每次修改的时间==create_time-创建时间【服务器每次修改模板会覆盖create_time】*/
		String createTableCloudRecordModelString = "create table cloudrecord(id integer primary key autoincrement,"
				+"ivid varchar,ischoose integer,title varchar,file_name varchar,create_time varchar,"
				+"voice_length integer,path_local varchar,path_service varchar,examine_status varchar,time integer,user_id varchar,sort_no varchar,modify_time long)";
		db.execSQL(createTableCloudRecordModelString);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (newVersion >= 7) {

			db.execSQL("DELETE FROM 'replymodel'");
			// sqlite_sequence
			// 系统默认存放表的表，name是要删除的表;seq是name表中自动升序后的最大值（如replymodel自增到99，则seq的值即为99）
			db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'replymodel'");

			String sql = "alter table replymodel add tid varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "alter table replymodel add apply_time varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "alter table replymodel add approve_time varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "alter table replymodel add state varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (newVersion >= 8) {
			/*
			 * tucao_id;// 吐槽id String wduser_id;// 网点用户id shop;// 网点 brand;//
			 * 快递公司 county;// 区域 content;// 吐槽内容 update_time;// 更新时间 is_good;//
			 * 点赞状态-boolean类型 huifu;// 回复 String good;// 点赞 pic;//图片（可包含多张图片
			 * message;//快递员（网点+公司 imageurls;// 九宫格图片的URL集合(小图)
			 * imageurlsbig;//九宫格图片的URL集合(大图)
			 */
			String createTableCircleExpress = "create table circleexpress(" + "id integer primary key autoincrement,"
					+ "tucao_id varchar," + "wduser_id varchar," + "shop varchar," + "brand varchar,"
					+ "county varchar," + "content varchar," + "update_time varchar," + "is_good varchar ,"
					+ "huifu varchar," + "good varchar," + "pic varchar," + "message varchar," + "imageurls varchar,"
					+ "imageurlsbig varchar)";
			try {
				db.execSQL(createTableCircleExpress);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		/**
		 * 更新快递单号表
		 */
		if (newVersion >= 9) {
			db.execSQL("drop table expresshistory");
			String createTableExpressHistory = "create table expresshistory(" + "deliverno varchar primary key,"
					+ "deliverstate varchar," + "record varchar," + "firsttime varchar," + "remark varchar,"
					+ "time integer)";
			try {
				db.execSQL(createTableExpressHistory);
			} catch (SQLException e) {
				//Log.w("iii", e.getMessage());
			}
		}

		/** 在模板中加入字段title **/
		if (newVersion >= 10) {
			String addTitleToTableReplyModel = "alter table replymodel add title varchar";
			try {
				db.execSQL(addTitleToTableReplyModel);
			} catch (SQLException e) {
				e.printStackTrace();
				//Log.w("iii", e.getMessage());
			}
		}
		
		if(newVersion >= 11){
			if(true == UtilityDb.tabbleIsExist(db, "cloudrecord")){
				String delete_table = "drop table cloudrecord";
				try {
					db.execSQL(delete_table);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*
			 *id:自增ID//ischoose:是否选中//title:模板标题// file_name：文件名称// create_time:模板创建时间//  voice_length：录音时长//
			 * path_local：保存录音本地路径// path_service: 服务器下载地址 // examine_status：  审核状态// user_id:用户ID
			 */
			String createTableCloudRecordModelString = "create table cloudrecord(id integer primary key autoincrement,"
					+"ivid varchar,ischoose integer,title varchar,file_name varchar,create_time varchar,"
					+"voice_length integer,path_local varchar,path_service varchar,examine_status varchar,time integer,user_id varchar)";
			try {
				db.execSQL(createTableCloudRecordModelString);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (newVersion >= 13) {
			String sql = "alter table replymodel add sort_no varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "alter table cloudrecord add sort_no varchar";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (newVersion >=14) {
			// 模板所属【短信：0】【留言：1】类型
			String sql = "alter table replymodel add template_type integer";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 留言短信模板选择状态【选择为1，没选择为0】
			sql = "alter table replymodel add ly_select_status integer";
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (newVersion >=15){
			// 短信模板更新后的最新时间
			String sql = "alter table replymodel add modify_time long";
			try{
				db.execSQL(sql);
			} catch (Exception e){
				e.printStackTrace();
			}
			sql = "alter table cloudrecord add modify_time long";
			try{
				db.execSQL(sql);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
}
