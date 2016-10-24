package com.kuaibao.skuaidi.customer.entity;

import com.kuaibao.skuaidi.entry.MyCustom;

import java.util.List;

/**
 * Created by kuaibao on 2016/8/31.
 * Description:    ${todo}
 */
public class ResponsePartSyncResult {
    private String del;
    private List<MyCustom> change;
    private String last_sync_time;

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public List<MyCustom> getChange() {
        return change;
    }

    public void setChange(List<MyCustom> change) {
        this.change = change;
    }

    public String getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(String last_sync_time) {
        this.last_sync_time = last_sync_time;
    }
}
