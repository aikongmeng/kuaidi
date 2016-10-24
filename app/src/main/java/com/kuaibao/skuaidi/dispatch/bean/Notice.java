package com.kuaibao.skuaidi.dispatch.bean;

import java.io.Serializable;

/**
 * Created by wang on 2016/8/31.
 */
public class Notice implements Serializable {
    private static final long serialVersionUID = 6346992843992083832L;
    private info info;
    private String waybillNo;
    private String noticeUpdateTime;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getNoticeUpdateTime() {
        return noticeUpdateTime;
    }

    public void setNoticeUpdateTime(String noticeUpdateTime) {
        this.noticeUpdateTime = noticeUpdateTime;
    }

    public info getInfo() {
        return info;
    }

    public void setInfo(info info) {
        this.info = info;
    }

    public class info implements Serializable{
        private static final long serialVersionUID = 5272051820657878694L;
        private int message;
        private int liuyan;
        private int intercept;
        private int pay;
        private int tousu;

        public int getMessage() {
            return message;
        }

        public void setMessage(int message) {
            this.message = message;
        }

        public int getLiuyan() {
            return liuyan;
        }

        public void setLiuyan(int liuyan) {
            this.liuyan = liuyan;
        }

        public int getIntercept() {
            return intercept;
        }

        public void setIntercept(int intercept) {
            this.intercept = intercept;
        }

        public int getPay() {
            return pay;
        }

        public void setPay(int pay) {
            this.pay = pay;
        }

        public int getTousu() {
            return tousu;
        }

        public void setTousu(int tousu) {
            this.tousu = tousu;
        }
    }

}
