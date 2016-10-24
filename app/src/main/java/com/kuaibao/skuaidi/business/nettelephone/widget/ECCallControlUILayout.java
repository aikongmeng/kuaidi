package com.kuaibao.skuaidi.business.nettelephone.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.business.nettelephone.util.VoIPCallHelper;


/**
 * 通话界面功能控制按钮操作区域
 */
public class ECCallControlUILayout extends LinearLayout
        implements View.OnClickListener {

    /**通话控制区域*/
    // private LinearLayout mCallingshowPanel;
    /**静音按钮*/
    private ImageView mCallMute;
    /**免提按钮*/
    private ImageView mCallHandFree;
    /**挂断按钮*/
    private ImageButton mCallRelease;
    /**控制按钮监听接口*/
    private OnCallControlDelegate mOnCallControlDelegate;

    private RelativeLayout rl_direct_call;
    private RelativeLayout rl_huibo_call;

    public ECCallControlUILayout(Context context) {
        this(context, null);
    }

    public ECCallControlUILayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public ECCallControlUILayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        TypedArray typedArray = context.obtainStyledAttributes(attrs ,R.styleable.call_control);
        int callDirect = typedArray.getInt(R.styleable.call_control_call_direct , 0);
        // 设置呼叫界面显示
        setCallDirect(CallLayout.values()[callDirect]);
    }

    /**
     * 设置通话界面显示的类型
     * @param callDirect
     */
    public void setCallDirect(CallLayout callDirect) {
        if(callDirect ==CallLayout.OUTGOING){
            rl_direct_call.setVisibility(View.VISIBLE);
            rl_huibo_call.setVisibility(View.GONE);
        }else if(callDirect == CallLayout.HUIBO){
            rl_direct_call.setVisibility(View.GONE);
            rl_huibo_call.setVisibility(View.VISIBLE);
        }
//        if(callDirect == CallLayout.INCOMING) {
//            //mCallingshowPanel.setVisibility(View.GONE);
//        } else if (callDirect == CallLayout.OUTGOING || callDirect == CallLayout.ALERTING) {
//            // mCallingshowPanel.setVisibility(View.VISIBLE);
//            setControlEnable(callDirect == CallLayout.ALERTING);
//        } else if (callDirect == CallLayout.INCALL) {
//            // mCallingshowPanel.setVisibility(View.VISIBLE);
//            setControlEnable(true);
//        }
    }

    /**
     * 设置通话控制按钮是否可用
     * @param enable
     */
    public void setControlEnable(boolean enable) {
        mCallMute.setEnabled(enable);
        mCallHandFree.setEnabled(enable);
    }

    private void initView() {
        View.inflate(getContext(), R.layout.ec_call_control_layout, this);
        // 通话进行过程中显示
        //mCallingshowPanel = (LinearLayout) findViewById(R.id.calling_bottom_show);
        // 通话控制按钮区域
        rl_direct_call=(RelativeLayout) findViewById(R.id.rl_direct_call);
        rl_huibo_call=(RelativeLayout) findViewById(R.id.rl_huibo_call);
        mCallMute = (ImageView) findViewById(R.id.layout_call_mute);
        mCallHandFree = (ImageView) findViewById(R.id.layout_call_handfree);

        mCallHandFree.setClickable(true);
        mCallMute.setClickable(true);
        mCallHandFree.setOnClickListener(l);
        mCallMute.setOnClickListener(l);

        // 挂断电话按钮
        mCallRelease = (ImageButton) findViewById(R.id.layout_call_release);
        mCallRelease.setOnClickListener(this);
    }

    public void setReleaseButtonEnable(boolean isEnable){
        if(!isEnable){
            mCallRelease.setEnabled(false);
            mCallRelease.setBackgroundResource(R.drawable.icon_call_records_hangup_2);
        }
    }
    /**
     * 监听通话按钮
     * @param delegate
     */
    public void setOnCallControlDelegate(OnCallControlDelegate delegate) {
        mOnCallControlDelegate = delegate;
    }

    private OnClickListener l = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_call_mute:
                    VoIPCallHelper.setMute();
                    boolean mute = VoIPCallHelper.getMute();
                    mCallMute.setImageResource(mute ? R.drawable.voip_call_mute_on : R.drawable.voip_call_mute);
                    break;
                case R.id.layout_call_handfree:
                    VoIPCallHelper.setHandFree();
                    boolean handFree = VoIPCallHelper.getHandFree();
                    mCallHandFree .setImageResource(handFree ? R.drawable.voip_call_handfree_on : R.drawable.voip_call_handfree);
                    break;
            }
        }
    };
    @Override
    public void onClick(View v) {
        if(mOnCallControlDelegate == null) {
            return ;
        }
        switch (v.getId()) {
            case R.id.layout_call_release:
                ImageButton viewRelease = (ImageButton) v;
                mOnCallControlDelegate.onViewRelease(this, viewRelease);
                break;
        }
    }

    /**
     * 通话界面显示类型
     */
    public enum CallLayout {
        INCOMING , ALERTING ,OUTGOING , INCALL,HUIBO
    }

    public interface OnCallControlDelegate {
        void onViewAccept(ECCallControlUILayout controlPanelView , ImageButton view);
        void onViewReject(ECCallControlUILayout controlPanelView , ImageButton view);
        void onViewRelease(ECCallControlUILayout controlPanelView , ImageButton view);
    }
}
