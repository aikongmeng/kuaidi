package com.kuaibao.skuaidi.dispatch.bean;

/**
 * Created by wang on 2016/5/18. 巴枪上传新接口响应 data字段
 */
public class ResponseData {
    private int code;
    private String status;
    private String desc;
    private String result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
