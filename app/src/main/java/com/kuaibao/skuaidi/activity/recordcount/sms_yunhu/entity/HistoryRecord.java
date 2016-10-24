package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kb82 on 2016/6/13.
 */
public class HistoryRecord implements Serializable{


    private static final long serialVersionUID = -5928025696457457209L;
    /**
     * total_count : 5
     * list : [{"status":2,"voice_title":"顾老师","user_input_key":null,"signed":"0","last_msg_time":"2016-06-07 16:54:53","last_msg_content":"","call_number":"13661964640","dh":"0","status_msg":"呼叫成功","create_time":"2016-06-07 16:54:43","bh":"1","topic_id":"374923897","last_msg_content_type":"6","call_duration":"7"}]
     * page_num : 1
     * page_count : 1
     */

    private String total_count;
    private String page_num;
    private int page_count;
    /**
     * status : 2
     * voice_title : 顾老师
     * user_input_key : null
     * signed : 0
     * last_msg_time : 2016-06-07 16:54:53
     * last_msg_content :
     * call_number : 13661964640
     * dh : 0
     * status_msg : 呼叫成功
     * create_time : 2016-06-07 16:54:43
     * bh : 1
     * topic_id : 374923897
     * last_msg_content_type : 6
     * call_duration : 7
     */

    private List<ListBean> list;

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public String getPage_num() {
        return page_num;
    }

    public void setPage_num(String page_num) {
        this.page_num = page_num;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        private int status;
        private String voice_title;
        private Object user_input_key;
        private String signed;
        private String last_msg_time;
        private String last_msg_content;
        private String call_number;
        private String dh;
        private String status_msg;
        private String create_time;
        private String bh;
        private String topic_id;
        private String last_msg_content_type;
        private String call_duration;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getVoice_title() {
            return voice_title;
        }

        public void setVoice_title(String voice_title) {
            this.voice_title = voice_title;
        }

        public Object getUser_input_key() {
            return user_input_key;
        }

        public void setUser_input_key(Object user_input_key) {
            this.user_input_key = user_input_key;
        }

        public String getSigned() {
            return signed;
        }

        public void setSigned(String signed) {
            this.signed = signed;
        }

        public String getLast_msg_time() {
            return last_msg_time;
        }

        public void setLast_msg_time(String last_msg_time) {
            this.last_msg_time = last_msg_time;
        }

        public String getLast_msg_content() {
            return last_msg_content;
        }

        public void setLast_msg_content(String last_msg_content) {
            this.last_msg_content = last_msg_content;
        }

        public String getCall_number() {
            return call_number;
        }

        public void setCall_number(String call_number) {
            this.call_number = call_number;
        }

        public String getDh() {
            return dh;
        }

        public void setDh(String dh) {
            this.dh = dh;
        }

        public String getStatus_msg() {
            return status_msg;
        }

        public void setStatus_msg(String status_msg) {
            this.status_msg = status_msg;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getBh() {
            return bh;
        }

        public void setBh(String bh) {
            this.bh = bh;
        }

        public String getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(String topic_id) {
            this.topic_id = topic_id;
        }

        public String getLast_msg_content_type() {
            return last_msg_content_type;
        }

        public void setLast_msg_content_type(String last_msg_content_type) {
            this.last_msg_content_type = last_msg_content_type;
        }

        public String getCall_duration() {
            return call_duration;
        }

        public void setCall_duration(String call_duration) {
            this.call_duration = call_duration;
        }
    }
}
