package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wang on 2016/5/16.
 */
public class FocusedTextView extends TextView {
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        return true;
    }
}
