package com.kuaibao.skuaidi.texthelp;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 类名: TextMarquee 
 * 原理: 跑马灯效果-针对textview【自定义textview】 时间: 2016-1-7
 * 下午2:16:51 <br/>
 * 
 * 作者： 顾冬冬
 */
public class TextMarquee extends TextView{

    public TextMarquee(Context context) {  
        super(context);  
        createView();  
    }  
  
    public TextMarquee(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        createView();  
    }  
  
    public TextMarquee(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        createView();  
    }  
  
    private void createView() {  
        setEllipsize(TruncateAt.MARQUEE);  
        setMarqueeRepeatLimit(-1);  
        setFocusableInTouchMode(true);  
    }  
  
    @Override  
    protected void onFocusChanged(boolean focused, int direction,  
            Rect previouslyFocusedRect) {  
        if (focused) {  
            super.onFocusChanged(focused, direction, previouslyFocusedRect);  
        }  
    }  
  
    @Override  
    public void onWindowFocusChanged(boolean focused) {  
        if (focused) {  
            super.onWindowFocusChanged(focused);  
        }  
    }  
  
    @Override  
    public boolean isFocused() {  
        return true;  
    }  
}
