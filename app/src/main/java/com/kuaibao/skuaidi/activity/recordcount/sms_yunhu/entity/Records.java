package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ligg on 2016/4/13 15:07.
 * Email: 2880098674@kuaidihelp.com
 */
public class Records implements Parcelable {
    private int id;
    private long timeStamp;
    private int count;
    private String extra;

    protected Records(Parcel in) {
        id = in.readInt();
        timeStamp = in.readLong();
        count = in.readInt();
        extra = in.readString();
    }
    public Records(){

    }
    public Records(long timeStamp,int count,String extra){
        this.timeStamp=timeStamp;
        this.count=count;
        this.extra=extra;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(timeStamp);
        dest.writeInt(count);
        dest.writeString(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Records> CREATOR = new Creator<Records>() {
        @Override
        public Records createFromParcel(Parcel in) {
            return new Records(in);
        }

        @Override
        public Records[] newArray(int size) {
            return new Records[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Records{" +
                "id=" + id +
                ", timeStamp=" + timeStamp +
                ", count=" + count +
                ", extra='" + extra + '\'' +
                '}';
    }
}
