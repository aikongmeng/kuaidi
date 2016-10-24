package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class NotifyInfo2 extends NotifyInfo implements Serializable {

    private static final long serialVersionUID = -3895397335635790094L;
    /**
     *
     */
    //private static final long serialVersionUID = 1L;
    private String ExpressNo;// 编号

    /** 是否设置开始录音[动画状态] **/
    private boolean playVoiceAnim = false;

    public String getExpressNo() {
        return ExpressNo;
    }

    public void setExpressNo(String expressNo) {
        ExpressNo = expressNo;
    }

    public boolean isPlayVoiceAnim() {
        return playVoiceAnim;
    }

    public void setPlayVoiceAnim(boolean playVoiceAnim) {
        this.playVoiceAnim = playVoiceAnim;
    }

}