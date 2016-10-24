package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

/**
 * Created by ligg on 2016/4/15 18:51.
 * Email: 2880098674@kuaidihelp.com
 */
public class Item {
    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public final int type;
    public final Records mRecords;

//    public int sectionPosition;
//    public int listPosition;

    public Item(int type, Records records) {
        this.type = type;
        this.mRecords = records;
    }

    public Records getRecords() {
        return mRecords;
    }

    @Override public String toString() {
        return mRecords.toString();
    }
}
