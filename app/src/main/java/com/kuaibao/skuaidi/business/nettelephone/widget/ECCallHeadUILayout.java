package com.kuaibao.skuaidi.business.nettelephone.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;


/**
 * 呼叫界面顶部呼叫信息显示区域
 */
public class ECCallHeadUILayout extends LinearLayout implements View.OnClickListener {

    /**通话者昵称*/
    private TextView mCallName;
    /**通话号码*/
    private TextView mCallNumber;
    /**通话时间*/
    private Chronometer mCallTime;
    /**呼叫状态描述*/
    private TextView mCallMsg;
    /**当前是否正在进行通话*/
    private boolean mCalling = false;
    /**是否显示通话参数信息*/
    private boolean mShowCallTips = false;

    private TextView tv_back;

    public Chronometer getmCallTime() {
		return mCallTime;
	}

	public void setmCallTime(Chronometer mCallTime) {
		this.mCallTime = mCallTime;
	}

	public ECCallHeadUILayout(Context context) {
        this(context , null);
    }

    public ECCallHeadUILayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECCallHeadUILayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.ec_call_head_layout, this);
        mCallName = (TextView) findViewById(R.id.layout_call_name);
        mCallNumber = (TextView) findViewById(R.id.layout_call_number);
        mCallTime = (Chronometer) findViewById(R.id.chronometer);
        mCallMsg = (TextView) findViewById(R.id.layout_call_msg);
        tv_back=(TextView) findViewById(R.id.tv_phoneing_back);
        tv_back.setOnClickListener(this);
    }

    public void setBackVisible(boolean show){
        tv_back.setVisibility(show?View.VISIBLE:View.GONE);
    }
   

    /**
     * 设置当前的呼叫状态
     * @param calling
     */
    public void setCalling(boolean calling) {
        this.mCalling = calling;

        if(calling) {
            mCallTime.setBase(SystemClock.elapsedRealtime());
            mCallTime.setVisibility(View.VISIBLE);
            mCallTime.start();
        } else {
            mCallTime.stop();
        }
        mCallMsg.setVisibility((calling && !mShowCallTips)? View.GONE:View.VISIBLE);
    }

    /**
     * 是否显示通话参数信息
     * @param isShowing
     */
    public void setCallTipsShowing(boolean isShowing) {
        mShowCallTips = isShowing;
    }

    /**
     * 设置呼叫昵称
     * @param text 昵称
     */
    public void setCallName(CharSequence text) {
        if(mCallName != null) {
            mCallName.setText(text);
        }
    }

    /**
     * 设置呼叫号码
     * @param mobile 号码
     */
    public void setCallNumber(CharSequence mobile) {
        if(mCallNumber != null) {
            mCallNumber.setText(mobile);
        }
    }

    /**
     * 设置呼叫状态描述
     * @param text
     */
    public void setCallTextMsg(String text) {
        if(mCallMsg == null) {
            return ;
        }
        if((text == null || text.length() <= 0) && !mCalling ) {
            mCallMsg.setVisibility(View.GONE);
        } else {
            mCallMsg.setText(text);
            mCallMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置呼叫状态描述
     * @param resid
     */
    public void setCallTextMsg(int resid) {
        setCallTextMsg(getResources().getString(resid));
    }
    

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.tv_phoneing_back:
                if(sendDTMFDelegate!=null){
                    sendDTMFDelegate.onClickBackBotton();
                }
                break;
	    }
	}


	 
	 public interface OnSendDTMFDelegate {
	        void sendDTMF(char c);
         void onClickBackBotton();
	    }
	 
	 private OnSendDTMFDelegate sendDTMFDelegate;

	public OnSendDTMFDelegate getSendDTMFDelegate() {
		return sendDTMFDelegate;
	}

	public void setSendDTMFDelegate(OnSendDTMFDelegate sendDTMFDelegate) {
		this.sendDTMFDelegate = sendDTMFDelegate;
	}
	 
	 

}
