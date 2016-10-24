package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gdd
 * on 2016/6/14.
 */
public class HistoryRecordDetail implements Serializable {


    private static final long serialVersionUID = -1472158400471973641L;
    /**
     * total_count : 1
     * list : [{"content":"","ivr_status_msg":"","speaker_role":"counterman","voice_length":null,"ivr_status":1,"speak_time":1465289693,"message_id":"388745437","speaker_id":"237","speaker_phone":"13661964640","content_type":"6","ivr_user_input":""}]
     * page_num : 1
     * page_count : 1
     */

    private String total_count;
    private String page_num;
    private int page_count;
    /**
     * content :
     * ivr_status_msg :
     * speaker_role : counterman
     * voice_length : null
     * ivr_status : 1
     * speak_time : 1465289693
     * message_id : 388745437
     * speaker_id : 237
     * speaker_phone : 13661964640
     * content_type : 6
     * ivr_user_input :
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

    public static class ListBean {
        private String content;
        private String ivr_status_msg;
        private String speaker_role;
        private Object voice_length;
        private int ivr_status;
        private long speak_time;
        private String message_id;
        private String speaker_id;
        private String speaker_phone;
        private String content_type;
        private String ivr_user_input;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getIvr_status_msg() {
            return ivr_status_msg;
        }

        public void setIvr_status_msg(String ivr_status_msg) {
            this.ivr_status_msg = ivr_status_msg;
        }

        public String getSpeaker_role() {
            return speaker_role;
        }

        public void setSpeaker_role(String speaker_role) {
            this.speaker_role = speaker_role;
        }

        public Object getVoice_length() {
            return voice_length;
        }

        public void setVoice_length(Object voice_length) {
            this.voice_length = voice_length;
        }

        public int getIvr_status() {
            return ivr_status;
        }

        public void setIvr_status(int ivr_status) {
            this.ivr_status = ivr_status;
        }

        public long getSpeak_time() {
            return speak_time;
        }

        public void setSpeak_time(long speak_time) {
            this.speak_time = speak_time;
        }

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }

        public String getSpeaker_id() {
            return speaker_id;
        }

        public void setSpeaker_id(String speaker_id) {
            this.speaker_id = speaker_id;
        }

        public String getSpeaker_phone() {
            return speaker_phone;
        }

        public void setSpeaker_phone(String speaker_phone) {
            this.speaker_phone = speaker_phone;
        }

        public String getContent_type() {
            return content_type;
        }

        public void setContent_type(String content_type) {
            this.content_type = content_type;
        }

        public String getIvr_user_input() {
            return ivr_user_input;
        }

        public void setIvr_user_input(String ivr_user_input) {
            this.ivr_user_input = ivr_user_input;
        }
    }
}
