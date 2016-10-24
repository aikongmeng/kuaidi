package com.kuaibao.skuaidi.dispatch.bean;

/**
 * Created by wang on 2016/8/31.
 */
public class TagNotice {
    private String waybillNo;
    private Info info;

    public class Info {
        private String weight;
        private String message;
        private String liuyan;
        private String pay;
        private String intercept;
        private String tousu;
        private String complain;
        private String noBox;
        private String sign;
        private String send;
        private String notes;

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLiuyan() {
            return liuyan;
        }

        public void setLiuyan(String liuyan) {
            this.liuyan = liuyan;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getIntercept() {
            return intercept;
        }

        public void setIntercept(String intercept) {
            this.intercept = intercept;
        }

        public String getTousu() {
            return tousu;
        }

        public void setTousu(String tousu) {
            this.tousu = tousu;
        }

        public String getComplain() {
            return complain;
        }

        public void setComplain(String complain) {
            this.complain = complain;
        }

        public String getNoBox() {
            return noBox;
        }

        public void setNoBox(String noBox) {
            this.noBox = noBox;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSend() {
            return send;
        }

        public void setSend(String send) {
            this.send = send;
        }
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info Info) {
        this.info = Info;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }
}