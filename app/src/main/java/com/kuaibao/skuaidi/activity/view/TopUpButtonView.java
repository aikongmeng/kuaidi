package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gdd on 2016/9/18.
 */
public class TopUpButtonView extends RelativeLayout {
    private Context context;
    @BindView(R.id.iv_pay_icon) ImageView ivPayIcon;
    @BindView(R.id.tv_pay_title) TextView tvPayTitle;
    @BindView(R.id.tv_pay_desc) TextView tvPayDesc;
    @BindView(R.id.iv_select) ImageView ivSelect;

    public TopUpButtonView(Context context) {
//        super(context);
        this(context,null);
    }

    public TopUpButtonView(Context context, AttributeSet attrs) {
//        super(context, attrs);
        this(context,attrs,0);
    }

    public TopUpButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //第一个参数:布局的资源id 第二个参数:填充的根视图 第三个参数是否将载入的视图绑定到根视图中
        LayoutInflater.from(context).inflate(R.layout.view_topup_button, this, true);
//        View.inflate(context, R.layout.view_topup_button, this);
        ButterKnife.bind(this);
    }

    /**
     * 设置icon
     **/
    public void setPayIcon(Drawable drawable) {
        ivPayIcon.setBackground(drawable);
    }

    /**
     * 设置title
     **/
    public void setPayTitle(String payTitle) {
        tvPayTitle.setText(payTitle);
    }

    public void setPayDesc(String payDesc) {
        tvPayDesc.setText(payDesc);
    }

    public void setSelect(Boolean isSelect) {
        ivSelect.setBackground(isSelect ? Utility.getDrawable(context, R.drawable.batch_add_checked) : Utility.getDrawable(context, R.drawable.select_edit_identity));
    }

}
