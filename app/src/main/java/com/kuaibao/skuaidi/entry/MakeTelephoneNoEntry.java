package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * Created by gudd on 2016/4/22.
 * 去电显号参数
 */
public class MakeTelephoneNoEntry implements Serializable {
    private static final long serialVersionUID = 3924542820598178861L;
    String in_use;// 去电显示号码是否开启状态
    String make_telephone_no;// 去电显示号码
    String new_apply_phone;// 新设置的去电显示号码
    String new_apply_phone_state;// 新设置的去电显示号码申请状态

    public String getIn_use() {
        return in_use;
    }

    public void setIn_use(String in_use) {
        this.in_use = in_use;
    }

    public String getMake_telephone_no() {
        return make_telephone_no;
    }

    public void setMake_telephone_no(String make_telephone_no) {
        this.make_telephone_no = make_telephone_no;
    }

    public String getNew_apply_phone() {
        return new_apply_phone;
    }

    public void setNew_apply_phone(String new_apply_phone) {
        this.new_apply_phone = new_apply_phone;
    }

    public String getNew_apply_phone_state() {
        return new_apply_phone_state;
    }

    public void setNew_apply_phone_state(String new_apply_phone_state) {
        this.new_apply_phone_state = new_apply_phone_state;
    }
}
