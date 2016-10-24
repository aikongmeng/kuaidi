package com.kuaibao.skuaidi.entry;

import android.content.Intent;

public class MessageEvent {
    public String message;
    public int type;
    public int position;
    public Intent intent;

    public MessageEvent(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Intent getIntent(){
     return intent;
    }
    public void putIntent(Intent intent){
        this.intent = intent;
    }
}
