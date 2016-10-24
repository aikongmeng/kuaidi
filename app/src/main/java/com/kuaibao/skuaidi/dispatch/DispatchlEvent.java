package com.kuaibao.skuaidi.dispatch;

/**
 * Created by wang on 2016/5/3.
 */
public class DispatchlEvent {
    //点击的item 位置
    private int position;
    private String eventType;
    private String actionType;

    public DispatchlEvent(int position, String eventType, String actionType) {
        this.position = position;
        this.eventType = eventType;
        this.actionType = actionType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActionType() {
        return actionType;
    }
}