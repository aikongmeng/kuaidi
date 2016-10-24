package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by gdd on 2016/6/13.
 */
public class MyRecordsCloudSection extends SectionEntity<HistoryRecord.ListBean> {


    public MyRecordsCloudSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public MyRecordsCloudSection(HistoryRecord.ListBean listBean) {
        super(listBean);
    }
}
