package com.kuaibao.skuaidi.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kuaibao.skuaidi.entry.MyCustom;

import java.util.List;

/**
 * Created by kuaibao on 2016/8/31.
 * Description:    ${todo}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseAllSyncResult {
    private int page_total;
    private int page_current;
    private int page_num;
    private List<MyCustom> data;

    public int getPage_total() {
        return page_total;
    }

    public void setPage_total(int page_total) {
        this.page_total = page_total;
    }

    public int getPage_current() {
        return page_current;
    }

    public void setPage_current(int page_current) {
        this.page_current = page_current;
    }

    public int getPage_num() {
        return page_num;
    }

    public void setPage_num(int page_num) {
        this.page_num = page_num;
    }

    public List<MyCustom> getData() {
        return data;
    }

    public void setData(List<MyCustom> data) {
        this.data = data;
    }

    //    class Customer{
//        private String id;
//        private String name;
//        private String tel;
//        private String note;
//        private String address;
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getTel() {
//            return tel;
//        }
//
//        public void setTel(String tel) {
//            this.tel = tel;
//        }
//
//        public String getNote() {
//            return note;
//        }
//
//        public void setNote(String note) {
//            this.note = note;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//    }
}
