package com.kuaibao.skuaidi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Message;
import android.text.TextUtils;

import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.DeliverNoHistory;
import com.kuaibao.skuaidi.entry.ExpressHistory;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilityDb;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkuaidiDB {
    private static final int VERSION = 15;
    SkuaidiDBHelper dbHelper;
    SQLiteDatabase db;
    Context context;
    private String TAG = "gudd";

    private String DB_KUAIBAO = "kuaibao.db";
    private String TABLE_EXPRESS_HISTOTY = "expresshistory";
    private String TABLE_DELIVERY_LIST = "deliverylist";
    private String TABLE_NUMBER_LIST = "moredelivery";
    private String TABLE_PHONE = "phone";
    private String TABLE_REPLY_MODEL = "replymodel";
    //private String TABLE_CIRCLE_EXPRESS = "circleexpress";// 快递圈表
    private String TABLE_CLOUD_RECORE = "cloudrecord";
    private static SkuaidiDB instanse;

    private SkuaidiDB(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = new SkuaidiDBHelper(context.getApplicationContext(), DB_KUAIBAO, null, VERSION);
    }

    public static synchronized SkuaidiDB getInstanse(Context context) {
        if (instanse == null) {
            instanse = new SkuaidiDB(context.getApplicationContext());
        }
        return instanse;
    }

    // 云呼录音模板*****************************************************************************

    /**
     *  根据ivid来更新指定模板中的title
     *  updateCloudByivid
     *  顾冬冬
     *  2015-7-16 下午3:27:45
     *  u_title 模板title
     *  ivid 模板对应服务器上的id
     */
    public synchronized void updateCloudByivid(String u_title, String ivid) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", u_title);
        long curTime = System.currentTimeMillis();
        cv.put("modify_time", curTime);
        cv.put("create_time",UtilityTime.getDateTimeByMillisecond2(curTime,UtilityTime.YYYY_MM_DD_HH_MM_SS));
        cv.put("sort_no","");
        String whereClause = "ivid=?";
        String[] whereArgs = {ivid};
        db.update(TABLE_CLOUD_RECORE, cv, whereClause, whereArgs);
    }

    /**
     * 删除指定模板
     **/
    public synchronized void deleteCloudByivid(String ivid) {
        db = dbHelper.getWritableDatabase();
        String whereClause = "ivid=?";
        String[] whereArgs = {ivid};
        db.delete(TABLE_CLOUD_RECORE, whereClause, whereArgs);
    }

    /**
     * 删除云呼语音模板所有内容
     **/
    public synchronized void clearCloudModel() {
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_CLOUD_RECORE, null, null);
    }

    /**
     * 根据TID检查本地是否有此条模板
     *
     * @return true:有 false:没有
     */
    @SuppressWarnings("null")
    public synchronized boolean isHaveCloudRecordModel(String recordId) {
        if (TextUtils.isEmpty(recordId))
            return false;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select id from cloudrecord where ivid = '" + recordId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != cursor && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }

    }

    /**
     * @Title: saveCloudRecord
     * @Description: 为模板添加数据
     * @Author 顾冬冬
     * @CreateDate 2015-7-10 下午4:00:47
     * @Param @param cloudRecords
     * @Return void
     */
    public synchronized void insertCloudRecord(List<CloudRecord> cloudRecords) {
        db = dbHelper.getWritableDatabase();
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        for (int i = 0; i < cloudRecords.size(); i++) {
            CloudRecord cRecord = cloudRecords.get(i);
            if (isHaveCloudRecordModel(cRecord.getIvid())) {
                ContentValues values = new ContentValues();
                values.put("title", cRecord.getTitle());
                values.put("file_name", cRecord.getFileName());
                values.put("create_time", cRecord.getCreateTime());
                values.put("voice_length", cRecord.getVoiceLength());
                values.put("path_local", cRecord.getPathLocal());
                values.put("path_service", cRecord.getPathService());
                values.put("examine_status", cRecord.getExamineStatus());
                values.put("user_id", userInfo.getUserId());
                values.put("sort_no", cRecord.getSort_no());
                String whereClause = "ivid=?";
                String[] whereArgs = {cRecord.getIvid()};
                db.update(TABLE_CLOUD_RECORE, values, whereClause, whereArgs);
            } else {
                ContentValues values = new ContentValues();
                values.put("time", System.currentTimeMillis());
                values.put("ivid", cRecord.getIvid());
                if (cRecord.isChoose() == true) {
                    values.put("ischoose", 1);
                } else {
                    values.put("ischoose", 0);
                }
                values.put("title", cRecord.getTitle());
                values.put("file_name", cRecord.getFileName());
                values.put("create_time", cRecord.getCreateTime());
                values.put("voice_length", cRecord.getVoiceLength());
                values.put("path_local", cRecord.getPathLocal());
                values.put("path_service", cRecord.getPathService());
                values.put("examine_status", cRecord.getExamineStatus());
                values.put("user_id", userInfo.getUserId());
                values.put("sort_no", cRecord.getSort_no());
                db.insert(TABLE_CLOUD_RECORE, null, values);
            }
        }
    }

    /**
     * 获取云呼录音模板
     */
    public synchronized List<CloudRecord> getCloudRecordModels() {
        List<CloudRecord> cRecords = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            String recordBy = "modify_time DESC";// 按时间升序排列
            Cursor cursor = db.query(TABLE_CLOUD_RECORE, null, null, null, null, null, recordBy);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CloudRecord cRecord = new CloudRecord();
                    cRecord.setIvid(cursor.getString(cursor.getColumnIndex("ivid")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        cRecord.setChoose(true);
                    } else {
                        cRecord.setChoose(false);
                    }
                    cRecord.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    cRecord.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
                    cRecord.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                    cRecord.setModifytime(cursor.getLong(cursor.getColumnIndex("modify_time")));
                    cRecord.setVoiceLength(cursor.getInt(cursor.getColumnIndex("voice_length")));
                    cRecord.setPathLocal(cursor.getString(cursor.getColumnIndex("path_local")));
                    cRecord.setPathService(cursor.getString(cursor.getColumnIndex("path_service")));
                    cRecord.setExamineStatus(cursor.getString(cursor.getColumnIndex("examine_status")));
                    cRecord.setSort_no(cursor.getString(cursor.getColumnIndex("sort_no")));
                    cRecords.add(cRecord);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cRecords;
    }

    /**
     * @Title: getCloudRecordModels
     * @Description: 获取指定指定状态的云呼录音模板
     * @Author 顾冬冬
     * @CreateDate 2015-7-10 下午7:41:22
     * @Param @return
     * @Return list<CloudRecord>
     */
    public synchronized List<CloudRecord> getCloudRecordModels(String queryState) {
        List<CloudRecord> cRecords = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            String recordBy = "modify_time DESC";// 按时间升序排列
            String selection = "examine_status = ?";
            String[] selectionArgs = {queryState};
            Cursor cursor = db.query(TABLE_CLOUD_RECORE, null, selection, selectionArgs, null, null, recordBy);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CloudRecord cRecord = new CloudRecord();
                    cRecord.setIvid(cursor.getString(cursor.getColumnIndex("ivid")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        cRecord.setChoose(true);
                    } else {
                        cRecord.setChoose(false);
                    }
                    cRecord.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    cRecord.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
                    cRecord.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                    cRecord.setModifytime(cursor.getLong(cursor.getColumnIndex("modify_time")));
                    cRecord.setVoiceLength(cursor.getInt(cursor.getColumnIndex("voice_length")));
                    cRecord.setPathLocal(cursor.getString(cursor.getColumnIndex("path_local")));
                    cRecord.setPathService(cursor.getString(cursor.getColumnIndex("path_service")));
                    cRecord.setExamineStatus(cursor.getString(cursor.getColumnIndex("examine_status")));
                    cRecord.setSort_no(cursor.getString(cursor.getColumnIndex("sort_no")));
                    cRecords.add(cRecord);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cRecords;
    }

    /**
     * getCloudRecordModels
     * 获取指定编号，指定状态的云呼录音模板|获取指定状态的模板
     * 顾冬冬
     * 2015-7-10 下午7:41:22
     * return list<CloudRecord>
     */
    public synchronized CloudRecord getCloudRecordModels(String selectMode, String ividOrSortNO, String queryState) {
        try {
            db = dbHelper.getReadableDatabase();
            String recordBy = "time ASC";// 按时间升序排列
            String selection = "";
            if (selectMode.equals("ivid")) {
                selection = "ivid = ? and examine_status = ?";
            } else if (selectMode.equals("sortNo")) {
                selection = "sort_no = ? and examine_status = ?";
            }
            String[] selectionArgs = {ividOrSortNO, queryState};
            Cursor cursor = db.query(TABLE_CLOUD_RECORE, null, selection, selectionArgs, null, null, recordBy);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CloudRecord cRecord = new CloudRecord();
                    cRecord.setIvid(cursor.getString(cursor.getColumnIndex("ivid")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        cRecord.setChoose(true);
                    } else {
                        cRecord.setChoose(false);
                    }
                    cRecord.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    cRecord.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
                    cRecord.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                    cRecord.setModifytime(cursor.getLong(cursor.getColumnIndex("modify_time")));
                    cRecord.setVoiceLength(cursor.getInt(cursor.getColumnIndex("voice_length")));
                    cRecord.setPathLocal(cursor.getString(cursor.getColumnIndex("path_local")));
                    cRecord.setPathService(cursor.getString(cursor.getColumnIndex("path_service")));
                    cRecord.setExamineStatus(cursor.getString(cursor.getColumnIndex("examine_status")));
                    cRecord.setSort_no(cursor.getString(cursor.getColumnIndex("sort_no")));
                    return cRecord;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 获取云呼模板中已经被选中的模板的ID **/
    public synchronized String getSelectedCloudModelTid(){
        String selection = "ischoose=?";
        String[] selectionArgs={"1"};
        Cursor c = db.query(TABLE_CLOUD_RECORE,null,selection,selectionArgs,null,null,null);
        if (c.getCount()>0){
            if (c.moveToFirst()){
                return c.getString(c.getColumnIndex("ivid"));
            }
        }
        return "";
    }

    /**
     * @Title: deleteCloudRecordModel
     * @Description: 按照模板ID删除录音模板
     * @Author 顾冬冬
     * @CreateDate 2015-7-10 下午4:26:15
     * @Param @param ivid
     * @Return void
     */
    public synchronized void deleteCloudRecordModel(String ivid) {
        db = dbHelper.getWritableDatabase();
        String whereClause = "ivid=?";
        String[] whereArgs = {ivid};
        db.delete(TABLE_CLOUD_RECORE, whereClause, whereArgs);
    }

    /**
     * 清理所有模板内容的选中状态
     */
    public synchronized void clearCloudChooseModelStatus() {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ischoose", 0);
        String whereClause = "user_id=?";
        String[] whereArgs = {SkuaidiSpf.getLoginUser().getUserId()};
        db.update(TABLE_CLOUD_RECORE, cv, whereClause, whereArgs);
    }

    /**
     * 设置选中状态 ivid:模板对应服务器上的ID
     */
    public synchronized void setCloudIsChoose(String ivid) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ischoose", 1);
        String whereClause = "ivid=?";
        String[] whereArgs = {ivid};
        db.update(TABLE_CLOUD_RECORE, cv, whereClause, whereArgs);
    }

    // 快递查询记录****************************************************************************

    /**
     * 向表中插入查快递记录
     *
     * @param deliverNO
     * @param deliverState
     * @return
     */
    public synchronized long insertNewExpressHistory(String deliverNO, String deliverState, String record, String firsttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("deliverno", deliverNO);
        cv.put("record", record);
        cv.put("firsttime", firsttime);
        cv.put("deliverstate", deliverState);
        cv.put("time", System.currentTimeMillis());

        long newRowId;

        newRowId = db.insert(TABLE_EXPRESS_HISTOTY, null, cv);
        return newRowId;
    }

    public synchronized long FailinsertNewExpressHistory(String deliverNO, String firsttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("deliverno", deliverNO);
        cv.put("firsttime", firsttime);
        cv.put("time", System.currentTimeMillis());

        long newRowId;

        newRowId = db.insert(TABLE_EXPRESS_HISTOTY, null, cv);
        return newRowId;
    }

    /**
     * 修改指定查件记录的状态
     *
     * @param deliverNO
     * @param deliverState
     */
    public synchronized void updateDeliverState(String deliverNO, String deliverState, String record, String firsttime) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("deliverstate", deliverState);
        cv.put("record", record);
        cv.put("firsttime", firsttime);
        cv.put("time", System.currentTimeMillis());

        String whereClause = "deliverno=?";
        String[] whereArgs = {deliverNO};

        db.update(TABLE_EXPRESS_HISTOTY, cv, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 修改指定查件记录的状态
     *
     * @param deliverNO
     */
    public synchronized void FailupdateDeliverState(String deliverNO, String firsttime) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("firsttime", firsttime);
        cv.put("time", System.currentTimeMillis());

        String whereClause = "deliverno=?";
        String[] whereArgs = {deliverNO};

        db.update(TABLE_EXPRESS_HISTOTY, cv, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 更新指定查件记录的注释
     *
     * @param deliverNO
     * @param remark
     */

    public synchronized void updateRemark(String deliverNO, String remark) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("remark", remark);
        cv.put("time", System.currentTimeMillis());

        String whereClause = "deliverno=?";
        String[] whereArgs = {deliverNO};
        db.update(TABLE_EXPRESS_HISTOTY, cv, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 删除指定查件记录
     *
     * @param deliverNO
     */
    public synchronized void deleteExpressHistory(String deliverNO) {
        db = dbHelper.getWritableDatabase();

        String whereClause = "deliverno=?";
        String[] whereArgs = {deliverNO};
        db.delete(TABLE_EXPRESS_HISTOTY, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 判断指定单号的记录是否存在
     *
     * @param deliverNO
     * @return
     */
    public synchronized boolean isExpressHistoryExist(String deliverNO) {
        boolean isExist = false;
        db = dbHelper.getReadableDatabase();

        String selection = "deliverno=?";
        String[] selectionArgs = {deliverNO};
        Cursor cursor = db.query(true, TABLE_EXPRESS_HISTOTY, null, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

        if (count > 0) {
            isExist = true;
        }

        return isExist;
    }

    /**
     * 查快递后插入查件记录
     */
    public synchronized void insertExpressHistory(String deliverNO, String deliverState, String record, String firsttime) {
        boolean isExist = isExpressHistoryExist(deliverNO);
        if (isExist) {
            updateDeliverState(deliverNO, deliverState, record, firsttime);
        } else {
            insertNewExpressHistory(deliverNO, deliverState, record, firsttime);
        }
    }

    public synchronized void FailinsertExpressHistory(String deliverNO, String firsttime) {
        boolean isExist = isExpressHistoryExist(deliverNO);
        if (isExist) {
            FailupdateDeliverState(deliverNO, firsttime);
        } else {
            FailinsertNewExpressHistory(deliverNO, firsttime);
        }
    }

    /**
     * 获取查件记录
     *
     * @return
     */
    public synchronized List<ExpressHistory> getExpressHistory() {
        List<ExpressHistory> expressHistories = null;

        db = dbHelper.getReadableDatabase();
        String orderBy = "time DESC";

        Cursor cursor = db.query(TABLE_EXPRESS_HISTOTY, null, null, null, null, null, orderBy);

        if (cursor.getCount() > 0) {
            expressHistories = new ArrayList<ExpressHistory>();

            while (cursor.moveToNext()) {
                ExpressHistory expressHistory = new ExpressHistory();
                expressHistory.setDeliverNo(cursor.getString(cursor.getColumnIndex("deliverno")));
                expressHistory.setRecord(cursor.getString(cursor.getColumnIndex("record")));
                expressHistory.setFirstTime(cursor.getString(cursor.getColumnIndex("firsttime")));
                expressHistory.setStatus(cursor.getString(cursor.getColumnIndex("deliverstate")));
                String state = cursor.getString(cursor.getColumnIndex("deliverstate"));

                if (!KuaiBaoStringUtilToolkit.isEmpty(state)) {
                    if (state.equals("signed")) {
                        expressHistory.setStatus("已签收");
                    } else if (state.equals("sending")) {
                        expressHistory.setStatus("派送中");
                    } else if (state.equals("delivering")) {
                        expressHistory.setStatus("运送中");
                    } else {// collected
                        expressHistory.setStatus("取件中");
                    }
                } else {
                    expressHistory.setStatus("查询失败");
                }
                expressHistory.setRemarks(cursor.getString(cursor.getColumnIndex("remark")));

                expressHistories.add(expressHistory);

            }
        }
        cursor.close();
        // db.close();

        return expressHistories;
    }

    /**
     * 通过订单号取查询记录
     *
     * @param deliverNO
     * @return
     */
    public synchronized String getExpressById(String deliverNO) {
        String remark = "";
        db = dbHelper.getReadableDatabase();
        String orderBy = "time DESC"; // 为query最后一项 判断以什么来排序

        String selection = "deliverno=?";
        String[] selectionArgs = {deliverNO};
        Cursor cursor = db.query(true, TABLE_EXPRESS_HISTOTY, null, selection, selectionArgs, null, null, null, null);

        if (cursor.moveToFirst()) {
            remark = cursor.getString(cursor.getColumnIndex("remark"));
        }

        if (remark == null) {
            remark = "";
        }

        cursor.close();
        // db.close();

        return remark;
    }

    // 单号扫描****************************************************************************
    /**
     * 单号是否存在
     *
     * @param deliverNO
     * @return
     */
    // public boolean isDeliverNoExist(String deliverNO) {
    // boolean isExist = false;
    // db = dbHelper.getReadableDatabase();
    //
    // String selection = "deliverno=?";
    // String[] selectionArgs = { deliverNO };
    // Cursor cursor = db.query(true, TABLE_DELIVER_NO_HISTORY, null,
    // selection, selectionArgs,null, null, null, null);
    // int count = cursor.getCount();
    // cursor.close();
    // //db.close();
    //
    // if (count > 0) {
    // isExist = true;
    // }
    //
    // return isExist;
    // }

    /**
     * 插入新单号
     *
     * @return
     */
    public synchronized long insertNewDelivery(DeliverNoHistory delivery) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("topicid", delivery.getTopicId());
        cv.put("deliverno", delivery.getDeliverNo());
        cv.put("time", delivery.getTime());
        cv.put("mobile ", delivery.getMobile());
        cv.put("status", delivery.getStatus());
        cv.put("tip", delivery.getTip());

        long newRowId;

        newRowId = db.insert(TABLE_DELIVERY_LIST, null, cv);
        // db.close();
        return newRowId;
    }

    public synchronized void saveDeliveries(List<DeliverNoHistory> deliveries) {
        for (int i = 0; i < deliveries.size(); i++) {
            insertNewDelivery(deliveries.get(i));
        }
    }

    public synchronized void saveLiuyanMessages(List<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {

        }
    }

    public synchronized long insertDelivery(String topicid, String deliverno, String time, String mobile, String status, String tip) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("topicid", topicid);
        cv.put("deliverno", deliverno);
        cv.put("time", time);
        cv.put("mobile ", mobile);
        cv.put("status", status);
        cv.put("tip", tip);

        long newRowId;

        newRowId = db.insert(TABLE_DELIVERY_LIST, null, cv);

        // db.close();
        return newRowId;
    }

    // 以下为多单扫描后插入单号

    /**
     * 插入单号，没有则插入，有则更新
     */
    public synchronized void insertDeliverNo(String deliverno, String status, String record, String firsttime, String remarks) {
        boolean isExist = isDeliverNoExist(deliverno);
        if (!isExist) {
            insertDelivery(deliverno, status, record, firsttime, remarks);
        } else {
            updateDeliverNo(deliverno, status, record, firsttime, remarks);
        }
    }

    /**
     * 扫描后插入的单号
     *
     * @param deliverno 单号
     * @param status    状态
     * @param remarks   标记
     * @param record    最后一条流转信息
     * @param firsttime 第一次查快递的时间
     * @return
     */

    public synchronized long insertDelivery(String deliverno, String status, String remarks, String record, String firsttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("deliverno", deliverno);
        // cv.put("time", System.currentTimeMillis());
        cv.put("status", status);
        cv.put("remarks", remarks);
        cv.put("time", firsttime);
        cv.put("record", record);
        long newRowId = 0;
        try {
            newRowId = db.insert(TABLE_NUMBER_LIST, null, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        //Log.i("iii", newRowId + "");
        //System.out.println(newRowId);
        // db.close();
        return newRowId;
    }

    /**
     * 更新单号
     *
     * @param deliverNo
     * @param record
     */
    public synchronized void updateDeliverNo(String deliverNo, String status, String remarks, String record, String firsttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("deliverno", deliverNo);
        cv.put("status", status);
        cv.put("remarks", remarks);
        cv.put("record", record);
        cv.put("time", firsttime);
        // cv.put("time", System.currentTimeMillis());
        String whereClause = "deliverno=?";
        String[] whereArgs = {deliverNo};
        try {
            db.update(TABLE_NUMBER_LIST, cv, whereClause, whereArgs);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        // db.close();
    }

    /**
     * 获取单号 降序desc，升序asc
     *
     * @return
     */
    public synchronized List<NotifyInfo> getDelivery() {
        List<NotifyInfo> scans = null;
        db = dbHelper.getReadableDatabase();

        String orderBy = "time asc";
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NUMBER_LIST, null, null, null, null, null, orderBy);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            scans = new ArrayList<NotifyInfo>();
            while (cursor.moveToNext()) {
                NotifyInfo scan = new NotifyInfo();
                scan.setExpress_number(cursor.getString(cursor.getColumnIndex("deliverno")));
                scan.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                String state = cursor.getString(cursor.getColumnIndex("status"));
                if (state.equals("signed")) {
                    scan.setStatus("已签收");
                } else if (state.equals("sending")) {
                    scan.setStatus("派送中");
                } else if (state.equals("delivering")) {
                    scan.setStatus("运送中");
                } else if (state.equals("collected")) {// collected
                    scan.setStatus("取件中");
                }
                scans.add(scan);
            }
            cursor.close();
        }
        // db.close();
        return scans;
    }

    /**
     * 单号是否存在
     *
     * @param deliverno
     * @return
     */

    public synchronized boolean isDeliverNoExist(String deliverno) {
        boolean isExist = false;
        db = dbHelper.getReadableDatabase();
        String selection = "deliverno = ?";
        String[] selectionArgs = {deliverno};
        Cursor cursor = null;
        try {
            if (true == UtilityDb.checkTableExists(db, TABLE_NUMBER_LIST)) {
                cursor = db.query(true, TABLE_NUMBER_LIST, null, selection, selectionArgs, null, null, null, null);
                cursor.close();
                if (null != cursor && cursor.getCount() > 0) {
                    isExist = true;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return isExist;
    }

    public synchronized int deleteOrder(String deliverno) {
        db = dbHelper.getWritableDatabase();

        String whereClause = "deliverno = ?";
        String[] whereArgs = {deliverno};
        int result = 0;
        try {
            result = db.delete(TABLE_NUMBER_LIST, whereClause, whereArgs);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        // db.close();
        return result;
    }

    // public int delete(String number) {
    // SQLiteDatabase db = helper.getWritableDatabase();
    // int result = db.delete("blacknumber", "number=?",
    // new String[] { number });
    // //db.close();
    // return result;
    // }

    /**
     * 删除表中的所有数据
     */
    public synchronized void clearTableOrder() {
        db = dbHelper.getWritableDatabase();
        try {
            db.delete(TABLE_NUMBER_LIST, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        // db.close();
    }

    //
    // public void updateDeliverNoHistory(String deliverNo, String mobile,
    // String status) {
    // db = dbHelper.getWritableDatabase();
    //
    // ContentValues cv = new ContentValues();
    // cv.put("time", System.currentTimeMillis());
    // cv.put("mobile ", mobile);
    // cv.put("status", status);
    //
    // String whereClause = "deliverno=?";
    // String[] whereArgs = { deliverNo };
    // db.update(TABLE_DELIVERY_LIST, cv, whereClause, whereArgs);
    //
    // //db.close();
    // }
    //
    // /**
    // * 插入单号，没有则插入，有则更新
    // *
    // * @param deliverNO
    // * @param time
    // */
    // public void insertDeliverNoHistory(String deliverNo, String mobile,
    // String status) {
    // boolean isExist = isDeliverNoExist(deliverNo);
    // if (!isExist) {
    // insertNewDeliverNoHistory(deliverNo, mobile, status);
    // } else {
    // updateDeliverNoHistory(deliverNo, mobile, status);
    // }
    // }

    /**
     * 获取扫描记录
     *
     * @return
     */
    public synchronized List<DeliverNoHistory> getDeliveryList() {
        List<DeliverNoHistory> deliverNoHistories = new ArrayList<DeliverNoHistory>();

        db = dbHelper.getWritableDatabase();
        String orderBy = "time DESC";

        Cursor cursor = db.query(TABLE_DELIVERY_LIST, null, null, null, null, null, orderBy);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DeliverNoHistory deliverNoHistory = new DeliverNoHistory();
                deliverNoHistory.setTopicId(cursor.getString(cursor.getColumnIndex("topicid")));
                deliverNoHistory.setDeliverNo(cursor.getString(cursor.getColumnIndex("deliverno")));
                deliverNoHistory.setTime(cursor.getString(cursor.getColumnIndex("time")));
                deliverNoHistory.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                deliverNoHistory.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                deliverNoHistory.setTip(cursor.getString(cursor.getColumnIndex("tip")));
                deliverNoHistories.add(deliverNoHistory);
            }
        }
        cursor.close();
        // db.close();

        return deliverNoHistories;
    }

    /**
     * 删除表中的所有数据
     */
    public synchronized void clearTableDeliveryList() {
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_DELIVERY_LIST, null, null);
        // db.close();
    }

    /**
     * 根据运单号，查询扫描订单的时间
     *
     * @param deliverNO
     * @return
     */
    // public long getTimeFromDeliverHistory(String deliverNO) {
    // long time = 0L;
    // db = dbHelper.getReadableDatabase();
    // String orderBy = "time DESC";
    //
    // String selection = "deliverno=?";
    // String[] selectionArgs = { deliverNO };
    // Cursor cursor = db.query(true, TABLE_DELIVERY_LIST, null,
    // selection, selectionArgs, null, null, null, null);
    //
    // if (cursor.moveToFirst()) {
    // time = cursor.getLong(cursor.getColumnIndex("time"));
    // }
    //
    // cursor.close();
    // //db.close();
    //
    // return time;
    // }

    // 派件通知模板****************************************************************************
    // version2使用，version3删除表paijianmodel，创建新表replymodel代替此表

    /**
     * 插入派件通知模板
     *
     * @param modelContent
     * @param isChoose
     * @return
     */
    // public long insertPaijianModel(String modelContent, boolean isChoose) {
    // db = dbHelper.getWritableDatabase();
    // ContentValues cv = new ContentValues();
    // cv.put("modelcontent", modelContent);
    // if (isChoose) {
    // cv.put("ischoose", 1);
    // } else {
    // cv.put("ischoose", 0);
    // }
    // cv.put("time", System.currentTimeMillis());
    //
    // long newRowId;
    //
    // newRowId = db.insert(TABLE_PAIJIAN_MODEL, null, cv);
    // return newRowId;
    // }

    /**
     * 清理所有模板内容的选中状态
     *
     */
    // public void clearChooseModel() {
    // db = dbHelper.getWritableDatabase();
    //
    // ContentValues cv = new ContentValues();
    // cv.put("ischoose", 0);
    //
    // db.update(TABLE_PAIJIAN_MODEL, cv, null, null);
    //
    // //db.close();
    // }

    /**
     * 获取状态和排序号都存在的模板|获取状态和指定模板ID的模板
     */
    public synchronized ReplyModel getPaijianModel(String select, String state, String sort_no) {

        db = dbHelper.getReadableDatabase();
        String selection = "";
        if (select.equals("tid")) {
            selection = "state = ? and tid = ?";
        } else if (select.equals("sort_no")) {
            selection = "state = ? and sort_no = ?";
        }
        String[] selectionArgs = {state, sort_no};
        Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToFirst()) {
                ReplyModel paijianModel = new ReplyModel();
                paijianModel.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                paijianModel.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                paijianModel.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                paijianModel.setState(cursor.getString(cursor.getColumnIndex("state")));
                paijianModel.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                paijianModel.setId(cursor.getString(cursor.getColumnIndex("mid")));
                paijianModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                    paijianModel.setChoose(true);
                } else {
                    paijianModel.setChoose(false);
                }
                paijianModel.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                paijianModel.setSortNo(cursor.getString(cursor.getColumnIndex("sort_no")));
                return paijianModel;
            }
        }
        cursor.close();
        return null;
    }

    /**
     * 通过状态筛选对应的短信模板
     **/
    public synchronized Map<String, ReplyModel> getPaijianModels(String state1) {
        Map<String, ReplyModel> paijianModels = new LinkedHashMap<>();
        try {
            db = dbHelper.getReadableDatabase();
            String orderBy = "modify_time DESC";
            String selection = "state = ?";
            String[] selectionArgs = {state1};
            Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, orderBy);
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    ReplyModel paijianModel = new ReplyModel();
                    paijianModel.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                    paijianModel.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                    paijianModel.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                    paijianModel.setModify_time(cursor.getLong(cursor.getColumnIndex("modify_time")));
                    paijianModel.setState(cursor.getString(cursor.getColumnIndex("state")));
                    paijianModel.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                    paijianModel.setId(cursor.getString(cursor.getColumnIndex("mid")));
                    paijianModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        paijianModel.setChoose(true);
                    } else {
                        paijianModel.setChoose(false);
                    }
                    paijianModel.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                    paijianModel.setSortNo(cursor.getString(cursor.getColumnIndex("sort_no")));

                    paijianModels.put(paijianModel.getTid(), paijianModel);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paijianModels;
    }

    /**
     * 通过状态筛选对应的短信模板
     **/
//	public synchronized Map<String, ReplyModel> getPaijianModels(String state1, String state2) {
    public synchronized List<ReplyModel> getPaijianModels(String state1, String state2) {
//		Map<String, ReplyModel> paijianModels = new HashMap<String, ReplyModel>();
        List<ReplyModel> replyModels = new ArrayList<ReplyModel>();
        try {
            db = dbHelper.getReadableDatabase();
            String orderBy = "modify_time DESC";
            String selection = "state = ? or state = ?";
            String[] selectionArgs = {state1, state2};
            Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, orderBy);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ReplyModel paijianModel = new ReplyModel();
                    paijianModel.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                    paijianModel.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                    paijianModel.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                    paijianModel.setState(cursor.getString(cursor.getColumnIndex("state")));
                    paijianModel.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                    paijianModel.setId(cursor.getString(cursor.getColumnIndex("mid")));
                    paijianModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        paijianModel.setChoose(true);
                    } else {
                        paijianModel.setChoose(false);
                    }
                    paijianModel.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                    paijianModel.setSortNo(cursor.getString(cursor.getColumnIndex("sort_no")));
                    replyModels.add(paijianModel);
//					paijianModels.put(paijianModel.getTid(), paijianModel);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replyModels;
    }

    /**
     * 读取所有派件通知模板内容
     *
     * @return
     */
    public synchronized Map<String, ReplyModel> getPaijianModels() {
        Map<String, ReplyModel> paijianModels = new HashMap<>();
        try {
            db = dbHelper.getReadableDatabase();
            String orderBy = "time ASC";// 按时间升序排列
            // TABLE_PAIJIAN_MODEL
            Cursor cursor = db.query(TABLE_REPLY_MODEL, null, null, null, null, null, orderBy);
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    ReplyModel paijianModel = new ReplyModel();
                    paijianModel.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                    paijianModel.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                    paijianModel.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                    paijianModel.setState(cursor.getString(cursor.getColumnIndex("state")));
                    paijianModel.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                    paijianModel.setId(cursor.getString(cursor.getColumnIndex("mid")));
                    paijianModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        paijianModel.setChoose(true);
                    } else {
                        paijianModel.setChoose(false);
                    }
                    paijianModel.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                    paijianModel.setSortNo(cursor.getString(cursor.getColumnIndex("sort_no")));
                    paijianModel.setTemplate_type(cursor.getColumnIndex("template_type"));
                    paijianModels.put(paijianModel.getTid(), paijianModel);
                }
            }
            cursor.close();
            // db.close();
        } catch (Exception e) {
            // paijianModels = paijianModels;
        }
        return paijianModels;
    }

    /**
     * 按类型和状态读取所有派件通知模板内容
     *
     * @return
     */
    public synchronized Map<String, ReplyModel> getPaijianModels(int template_type, String state) {
        Map<String, ReplyModel> paijianModels = new HashMap<>();
        try {
            db = dbHelper.getReadableDatabase();
            String orderBy = "modify_time DESC";// 按时间升序排列
            // TABLE_PAIJIAN_MODEL
            String selection = "template_type=? and state=?";
            String[] selectionArgs = {String.valueOf(template_type), state};
            Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, orderBy);
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    ReplyModel paijianModel = new ReplyModel();
                    paijianModel.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                    paijianModel.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                    paijianModel.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                    paijianModel.setState(cursor.getString(cursor.getColumnIndex("state")));
                    paijianModel.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                    paijianModel.setId(cursor.getString(cursor.getColumnIndex("mid")));
                    paijianModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    paijianModel.setTemplate_type(cursor.getInt(cursor.getColumnIndex("template_type")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        paijianModel.setChoose(true);
                    } else {
                        paijianModel.setChoose(false);
                    }
                    paijianModel.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                    paijianModel.setSortNo(cursor.getString(cursor.getColumnIndex("sort_no")));
                    paijianModels.put(paijianModel.getTid(), paijianModel);
                    if (cursor.getInt(cursor.getColumnIndex("ly_select_status")) == 1) {
                        paijianModel.setLy_select_status(true);
                    } else {
                        paijianModel.setLy_select_status(false);
                    }
                }
            }
            cursor.close();
            // db.close();
        } catch (Exception e) {
            // paijianModels = paijianModels;
        }
        return paijianModels;
    }

    /**
     * 是否有模板内容被选中
     *
     * @return
     */
    // public boolean isChooseModel() {
    // boolean isChoose = false;
    // db = dbHelper.getReadableDatabase();
    //
    // String selection = "ischoose=?";
    // String[] selectionArgs = { "1" };
    // Cursor cursor = db.query(true, TABLE_PAIJIAN_MODEL, null, selection,
    // selectionArgs, null, null, null, null);
    // int count = cursor.getCount();
    // cursor.close();
    // //db.close();
    //
    // if (count > 0) {
    // isChoose = true;
    // }
    //
    // return isChoose;
    // }

    /**
     * 设置选中状态
     *
     */
    // public void setIsChoose(String modelContent) {
    // db = dbHelper.getWritableDatabase();
    //
    // ContentValues cv = new ContentValues();
    // cv.put("ischoose", 1);
    //
    // String whereClause = "modelcontent=?";
    // String[] whereArgs = { modelContent };
    // db.update(TABLE_PAIJIAN_MODEL, cv, whereClause, whereArgs);
    //
    // //db.close();
    // }

    // 结束，以上为version2使用的操作

    // 派件通知模板****************************************************************************
    // version3的操作
    // 表的列：mid,modelcontent,ischoose,time,type,userid
    public synchronized void copyOldTable(List<ReplyModel> models) {
        if (getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN) == null || getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN).size() < 1) {
            insertNewModel("你好，你的快件已经准备派送了，请准备收件", 1, 0L);
            insertNewModel("你好，包裹已放门卫处，请取件", 0, 0L);
            insertNewModel("你好，打电话无人接听，明天再送", 0, 0L);
            insertNewModel("你好，你的包裹不在我的派送范围内，请自己来取", 0, 0L);
        }

        if (getPaijianModels() == null || getPaijianModels().size() < 1) {
            return;
        }

        String mid = getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN).get(0).getId();
        boolean flagIsChoose = false;
        for (int i = 0; i < models.size(); i++) {
            insertNewModel(models.get(i).getModelContent(), models.get(i).isChoose() ? 1 : 0, models.get(i).getTime());
            if (models.get(i).isChoose()) {
                flagIsChoose = true;
            }
        }
        if (flagIsChoose) {
            setIsNotChoose(mid);
        }

        try {
            db = dbHelper.getWritableDatabase();
            String sqlDeletePaijianTable = "drop table paijianmodel";
            db.execSQL(sqlDeletePaijianTable);
            // db.close();
        } catch (Exception e) {
        }

    }

    public synchronized void copyOldTable1(List<ReplyModel> models) {
        if (getReplyModels(Constants.TYPE_QUICK_MODEL_SIGN) == null || getReplyModels(Constants.TYPE_QUICK_MODEL_SIGN).size() < 1) {
            insertNewModel("你好，你的快件已经准备派送了，请准备收件", 1, 0L);
            insertNewModel("你好，包裹已放门卫处，请取件", 0, 0L);
            insertNewModel("你好，打电话无人接听，明天再送", 0, 0L);
            insertNewModel("你好，你的包裹不在我的派送范围内，请自己来取", 0, 0L);
        }

        if (getPaijianModels() == null || getPaijianModels().size() < 1) {
            return;
        }

        String mid = getReplyModels(Constants.TYPE_QUICK_MODEL_SIGN).get(0).getId();
        boolean flagIsChoose = false;
        for (int i = 0; i < models.size(); i++) {
            insertNewModel(models.get(i).getModelContent(), models.get(i).isChoose() ? 1 : 0, models.get(i).getTime());
            if (models.get(i).isChoose()) {
                flagIsChoose = true;
            }
        }
        if (flagIsChoose) {
            setIsNotChoose(mid);
        }

        db = dbHelper.getWritableDatabase();
        String sqlDeletePaijianTable = "drop table paijianmodel";
        db.execSQL(sqlDeletePaijianTable);
        // db.close();
    }

    /**
     * 插入新的模板记录，主要用于把version2的数据转到version3中
     *
     * @param modelContent
     * @param ischoose
     * @param time
     * @return
     */
    public synchronized long insertNewModel(String modelContent, int ischoose, long time) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("modelcontent", modelContent);
        cv.put("ischoose", ischoose);
        cv.put("time", time);
        cv.put("type", Constants.TYPE_REPLY_MODEL_SIGN);
        cv.put("userid", SkuaidiSpf.getLoginUser().getUserId());

        long newRowId;

        newRowId = db.insert(TABLE_REPLY_MODEL, null, cv);
        return newRowId;
    }

    /**
     * 添加新模板
     *
     * @param modelContent
     * @param isChoose
     * @param type
     * @return
     */
    public synchronized long insertNewModel(String modelContent, boolean isChoose, int type) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("modelcontent", modelContent);
        if (isChoose) {
            cv.put("ischoose", true);
        } else {
            cv.put("ischoose", false);
        }
        cv.put("time", System.currentTimeMillis());
        cv.put("type", type);
        cv.put("userid", SkuaidiSpf.getLoginUser().getUserId());

        long newRowId;

        newRowId = db.insert(TABLE_REPLY_MODEL, null, cv);
        return newRowId;
    }

    /**
     * 添加模板
     *
     * @param replyModels
     */
    public synchronized void insertNewReplyModel(List<ReplyModel> replyModels) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        for (int i = 0; i < replyModels.size(); i++) {
            ReplyModel replyModel = replyModels.get(i);
            values.put("template_type", replyModel.getTemplate_type());
            values.put("time", System.currentTimeMillis());
            values.put("modelcontent", replyModel.getModelContent());
            values.put("userid", userInfo.getUserId());
            values.put("apply_time", replyModel.getApply_time());
            values.put("approve_time", replyModel.getApprove_time());
            values.put("modify_time",replyModel.getModify_time());
            values.put("state", replyModel.getState());
            values.put("sort_no", replyModel.getSortNo());
            values.put("title", replyModel.getTitle());
            String tid = replyModel.getTid();
            values.put("tid", tid);
            if (isHaveModel(tid)) {
                int id = db.update(TABLE_REPLY_MODEL, values, "tid = '" + tid + "'", null);
                //System.out.println("gudd_id" + id);
            } else {
                values.put("type", 1);
                values.put("ischoose", 0);
                long id = db.insert(TABLE_REPLY_MODEL, null, values);
            }
        }

    }

    // /**
    // * gudd
    // * @param tid 服务器上对就模板ID
    // */
    // public void deleteReplyModel(String tid){
    //
    // db = dbHelper.getWritableDatabase();
    // String whereClause = "tid=?";
    // String[] whereArgs = { tid };
    // db.delete(TABLE_REPLY_MODEL, whereClause, whereArgs);
    // //db.close();
    // }

    /**
     * 清理所有模板内容的选中状态
     */
    public synchronized void clearChooseModel(int type) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ischoose", 0);
        String whereClause = "type=? and userid=?";
        String[] whereArgs = {type + "", SkuaidiSpf.getLoginUser().getUserId()};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);
    }

    /**
     * 设置当前用户留言模板所有的选中状态为没选中
     **/
    public synchronized void clearSelectModel(int template_type) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ly_select_status", 0);
        String whereClause = "template_type=? and userid=?";
        String[] whereArgs = {template_type + "", SkuaidiSpf.getLoginUser().getUserId()};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);
    }

    /**
     * 是否有模板内容被选中
     *
     * @return
     */
    public synchronized boolean isChooseModel(int type) {
        boolean isChoose = false;
        db = dbHelper.getReadableDatabase();

        String selection = "ischoose=? and type=? and userid=?";
        String[] selectionArgs = {"1", type + "", SkuaidiSpf.getLoginUser().getUserId()};
        Cursor cursor = db.query(true, TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

        if (count > 0) {
            isChoose = true;
        }

        return isChoose;
    }

    /**
     * 设置选中状态
     */
    public synchronized void setIsChoose(String mid) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ischoose", 1);
        String whereClause = "mid=?";
        String[] whereArgs = {mid};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);
    }

    /**
     * 设置指定留言模板为选中状态
     **/
    public synchronized void setIsChooseTemplate_ly(String tid, int isSelectStatus) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ly_select_status", isSelectStatus);
        String whereClause = "tid=?";
        String[] whereArgs = {tid};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);
    }

    /**
     * 设置未选中状态
     */
    public synchronized void setIsNotChoose(String mid) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("ischoose", 0);

        String whereClause = "mid=?";
        String[] whereArgs = {mid};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 获取指定类型的回复模板
     *
     * @return
     */
    public synchronized List<ReplyModel> getReplyModels(int type) {
        List<ReplyModel> models = null;
        try {
            db = dbHelper.getReadableDatabase();
            String selection = "type=? and userid=?";
            String[] selectionArgs = {type + "", SkuaidiSpf.getLoginUser().getUserId()};
            String orderBy = "modify_time DESC";// 升序 ASC--降序是：DESC
            Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, orderBy);
            if (cursor.getCount() > 0) {
                models = new ArrayList<>();
                while (cursor.moveToNext()) {
                    ReplyModel model = new ReplyModel();
                    model.setId(cursor.getString(cursor.getColumnIndex("mid")));
                    model.setModelContent(cursor.getString(cursor.getColumnIndex("modelcontent")));
                    if (cursor.getInt(cursor.getColumnIndex("ischoose")) == 1) {
                        model.setChoose(true);
                    } else {
                        model.setChoose(false);
                    }
                    model.setApply_time(cursor.getString(cursor.getColumnIndex("apply_time")));
                    model.setApprove_time(cursor.getString(cursor.getColumnIndex("approve_time")));
                    model.setModify_time(cursor.getLong(cursor.getColumnIndex("modify_time")));
                    model.setState(cursor.getString(cursor.getColumnIndex("state")));
                    model.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                    model.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    models.add(model);
                }
            }
            cursor.close();
            // db.close();
        } catch (Exception e) {
            models = null;
        }
        return models;
    }

    /**
     * 修改指定模板的内容
     *
     * @param mid
     */
    public synchronized void updateModel(String mid, String modelContent, int type, ReplyModel replyModel) {
        clearChooseModel(type);

        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("modelcontent", modelContent);
        cv.put("ischoose", 1);
        cv.put("time", System.currentTimeMillis());
        cv.put("apply_time", replyModel.getApply_time());
        cv.put("approve_time", replyModel.getApprove_time());
        cv.put("modify_time",replyModel.getModify_time());
        cv.put("userid", SkuaidiSpf.getLoginUser().getUserId());
        cv.put("state", replyModel.getState());
        String whereClause = "mid=?";
        String[] whereArgs = {mid};
        db.update(TABLE_REPLY_MODEL, cv, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 删除指定模板
     *
     * @param mid
     */
    public synchronized void deleteModel(String mid) {
        db = dbHelper.getWritableDatabase();

        String whereClause = "mid=?";
        String[] whereArgs = {mid};
        db.delete(TABLE_REPLY_MODEL, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 删除指定模板
     */
    public synchronized void deleteModelByTid(String tid) {
        db = dbHelper.getWritableDatabase();

        String whereClause = "tid=?";
        String[] whereArgs = {tid};
        db.delete(TABLE_REPLY_MODEL, whereClause, whereArgs);

        // db.close();
    }

    /**
     * 根据TID检查本地是否有此条模板
     * @param tid
     * @return true:有 false:没有
     */
    @SuppressWarnings("null")
    public synchronized boolean isHaveModel(String tid) {

        if (TextUtils.isEmpty(tid))
            return false;

        db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
//			cursor = db.rawQuery("select mid from replymodel where tid = '" + tid + "'", null);
//			cursor = db.rawQuery("select * from replymodel where tid = '" + tid + "'", null);
            String selection = "tid = ?";
            String[] selectionArgs = {tid};
            cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != cursor && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }

    }

    // 通过模板数据库中的ID获取数据库中模板标题-（使用中）
    public synchronized String getModelTitle(String mid) {
        String modelTitle = "";
        db = dbHelper.getReadableDatabase();

        String selection = "mid=?";
        String[] selectionArgs = {mid};
        Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                modelTitle = cursor.getString(cursor.getColumnIndex("title"));
            }
        }
        cursor.close();
        return modelTitle;
    }

    // 通过模板数据库中的ID获取数据库中模板内容--（使用中）
    public synchronized String getModelContent(String mid) {
        String modelContent = "";
        db = dbHelper.getReadableDatabase();

        String selection = "mid=?";
        String[] selectionArgs = {mid};
        Cursor cursor = db.query(TABLE_REPLY_MODEL, null, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                modelContent = cursor.getString(cursor.getColumnIndex("modelcontent"));
            }
        }
        cursor.close();
        // db.close();

        return modelContent;
    }

    // 获取模板数据库中已经被选中的模板的ID 1:true 0:false
    public synchronized String getSelectedModelId() {
        db = dbHelper.getReadableDatabase();
        String selection = "ischoose=?";
        String[] selectionArgs = {"1"};
        Cursor c = db.query(TABLE_REPLY_MODEL,null,selection,selectionArgs,null,null,null);
        if (c.getCount()>0){
            if (c.moveToFirst())
                return c.getString(c.getColumnIndex("tid"));
        }
        return "";
    }

    /**
     * 手机号是否存在
     */

    public synchronized boolean isPhoneNoExist(String deliverNo, String phone) {
        boolean isExist = false;

        db = dbHelper.getReadableDatabase();

        String selection = "deliverno=? ";
        String[] selectionArgs = {deliverNo};
        Cursor cursor = db.query(true, TABLE_PHONE, null, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

        if (count > 0) {
            isExist = true;
        }

        return isExist;
    }

    /**
     * 删除表中的所有数据
     */
    public synchronized void clearTablePhone() {
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_PHONE, null, null);
        // db.close();
    }

    public synchronized void clearReplymodel() {
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_REPLY_MODEL, null, null);
        // db.close();
    }
}
