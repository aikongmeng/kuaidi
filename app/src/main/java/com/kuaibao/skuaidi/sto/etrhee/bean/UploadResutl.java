package com.kuaibao.skuaidi.sto.etrhee.bean;

import java.util.List;

/**
 * Created by wang on 2016/5/12.
 */
public class UploadResutl {


    /**
     * waybillNo : 762471203384
     * reason : 不是申通单号，请重新录入
     */

    private List<ErrorBean> error;
    private List<String> success;

    public List<ErrorBean> getError() {
        return error;
    }

    public void setError(List<ErrorBean> error) {
        this.error = error;
    }

    public List<String> getSuccess() {
        return success;
    }

    public void setSuccess(List<String> success) {
        this.success = success;
    }

    public static class ErrorBean {
        private String waybillNo;
        private String reason;

        public String getWaybillNo() {
            return waybillNo;
        }

        public void setWaybillNo(String waybillNo) {
            this.waybillNo = waybillNo;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
