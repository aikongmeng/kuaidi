package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MyRecordsSection extends SectionEntity<SMSRecord> {
    public MyRecordsSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public MyRecordsSection(SMSRecord t) {
        super(t);
    }

}
