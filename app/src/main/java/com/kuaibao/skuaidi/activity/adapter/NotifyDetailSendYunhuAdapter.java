package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotifyDetailSendYunhuAdapter extends BaseAdapter {

    private Context mContext = null;
    private List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries;
    private ButtonOnclick buttonOnclick = null;
    private ButtonOnLongClickListener buttonOnLongClickListener;

    private int count = 1;// 设置显示列表中条目数量

    private boolean isShowAll;

    public NotifyDetailSendYunhuAdapter(Context context, List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries,
                                        ButtonOnclick buttonOnclick, ButtonOnLongClickListener buttonOnLongClickListener) {
        this.mContext = context;
        this.buttonOnclick = buttonOnclick;
        this.cloudVoiceMsgDetailEntries = cloudVoiceMsgDetailEntries;
        this.buttonOnLongClickListener = buttonOnLongClickListener;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CloudVoiceMsgDetailEntry getItem(int position) {
        return cloudVoiceMsgDetailEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 获取手机号码列表
     **/
    public List<CloudVoiceMsgDetailEntry> getVoiceMsgDetail() {
        return cloudVoiceMsgDetailEntries;
    }

    /**
     * 设置( 编号/手机号 )列表
     **/
    public void setVoiceMsgDetail(List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries) {
        this.cloudVoiceMsgDetailEntries = cloudVoiceMsgDetailEntries;
        for (int i = 0 ;i<this.cloudVoiceMsgDetailEntries.size();i++){
            if (this.cloudVoiceMsgDetailEntries.get(i).getMobile().contains("*")){
                this.cloudVoiceMsgDetailEntries.get(i).setMobile("");
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 设置单条数据变化
     **/
    public void setVoiceMsgItemDetail(int position, CloudVoiceMsgDetailEntry cvmde) {
        this.cloudVoiceMsgDetailEntries.set(position, cvmde);
        notifyDataSetChanged();
    }

    /**
     * 设置列表中条目数量
     **/
    public void setListCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.notify_detail_send_yunhu_item, parent, false);
            holder.ll_modifyNO = (RelativeLayout) convertView.findViewById(R.id.ll_modifyNO);// 修改编号控件
            holder.tv_notify_num = (TextView) convertView.findViewById(R.id.tv_notify_num);// 编号
            holder.tv_notify_phone = (TextView) convertView.findViewById(R.id.tv_notify_phone);// 手机号码
            holder.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);// 删除按钮
            holder.iv_delete_icon = (ImageView) convertView.findViewById(R.id.iv_delete_icon);// 删除按钮图片
            holder.anim_play_audio = (ImageView) convertView.findViewById(R.id.anim_play_audio);// 语音识别动画
            holder.line = convertView.findViewById(R.id.line);
            holder.rl_input_order = (RelativeLayout) convertView.findViewById(R.id.rl_input_order);// 单号录入区
            holder.tv_order_no = (TextView) convertView.findViewById(R.id.tv_order_no);// 单号显示
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String mobileNo;
        if (isShowAllItems()) {// 显示全部列表
            mobileNo = getItem(position).getMobile_no();
            holder.ll_delete.setVisibility(View.GONE);
            holder.rl_input_order.setVisibility(View.VISIBLE);
        } else {// 显示列表部分内容
            mobileNo = "编号" + getItem(position).getMobile_no();
            holder.ll_delete.setVisibility(View.VISIBLE);
            holder.rl_input_order.setVisibility(View.GONE);
        }
        holder.tv_notify_num.setText(mobileNo);

        // 设置单号
        String order = getItem(position).getOrder_no();
        holder.tv_order_no.setText(order);

        // 设置手机号码
        String mobile = getItem(position).getMobile();
        if (mobile.length() != 0 && mobile.length() < 11) {
            holder.tv_notify_phone.setText(mobile);
//            setWordSpan(mobile,holder.tv_notify_phone);
            holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.red));
        } else if (mobile.length() > 12) {
            if (mobile.substring(0, 2).equals("86") && mobile.substring(2, 3).equals("1")) {
                String phone = mobile.substring(2, 5) + "-" + mobile.substring(5, 9) + "-" + mobile.substring(9, 13);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else if (mobile.substring(0, 3).equals("+86") && mobile.substring(3, 4).equals("1")) {
                String phone = mobile.substring(3, 6) + "-" + mobile.substring(6, 10) + "-" + mobile.substring(10, 14);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else if (mobile.substring(0, 5).equals("17951") && mobile.substring(5, 6).equals("1")) {
                String phone = mobile.substring(5, 8) + "-" + mobile.substring(8, 12) + "-" + mobile.substring(12, 16);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else {
                char item = '0';
                boolean isExist = false;
                for (int i = 0; i < mobile.length(); i++) {
                    item = mobile.charAt(i);
                    if (item == '0' || item == '1') {
                        isExist = true;
                        break;
                    }
                }
                int mobile_length = mobile.substring(mobile.indexOf(item)).length();
                if (isExist && mobile_length == 11) {// 从存在1或0的位置开始往后依然存在11位数字
                    String no = mobile.substring(mobile.indexOf(item), mobile.indexOf(item) + 11);
                    String phone = no.substring(0, 3) + "-" + no.substring(3, 7) + "-" + no.substring(7);
                    holder.tv_notify_phone.setText(phone);
//                    setWordSpan(phone,holder.tv_notify_phone);
                    getItem(position).setMobile(phone.replaceAll("-", ""));
                } else {
                    holder.tv_notify_phone.setText(mobile);
//                    setWordSpan(mobile,holder.tv_notify_phone);
                    holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.red));
                }

            }
        } else if (getItem(position).getMobile().length() == 11) {
            holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
            if (mobile.substring(0, 1).equals("0") || mobile.substring(0, 1).equals("1")) {// 如果号码第一个字符是0或者是1的时候
                String phone = mobile.substring(0, 3) + "-" + mobile.substring(3, 7) + "-" + mobile.substring(7);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else {
                holder.tv_notify_phone.setText(mobile);
//                setWordSpan(mobile,holder.tv_notify_phone);
                holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.red));
            }
        } else if (getItem(position).getMobile().length() == 12) {
            holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
            if (mobile.substring(0, 1).equals("0") || mobile.substring(0, 1).equals("1")) {// 如果号码第一个字符是0或者是1的时候
                String phone = mobile.substring(0, 4) + "-" + mobile.substring(4, 8) + "-" + mobile.substring(8);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else if (mobile.substring(1, 2).equals("0") || mobile.substring(1, 2).equals("1")) {
                String phone = mobile.substring(1, 4) + "-" + mobile.substring(4, 8) + "-" + mobile.substring(8);
                holder.tv_notify_phone.setText(phone);
//                setWordSpan(phone,holder.tv_notify_phone);
                getItem(position).setMobile(phone.replaceAll("-", ""));
            } else {
                holder.tv_notify_phone.setText(mobile);
//                setWordSpan(mobile,holder.tv_notify_phone);
                holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.red));
            }
        } else {
            holder.tv_notify_phone.setText(mobile);
            holder.tv_notify_phone.setTextColor(Utility.getColor(mContext,R.color.gray_1));
        }

        if ((!Utility.isEmpty(getItem(position).getMobile()) && getItem(position).isPlayVoiceAnim()) || getItem(position).isPlayVoiceAnim()) {
            holder.anim_play_audio.setBackgroundResource(R.drawable.anim_audio_play);
            AnimationDrawable ad = (AnimationDrawable) holder.anim_play_audio.getBackground();
            Timer animationTimer = new Timer();
            animationTimer.schedule(new AnimationTimer(ad), 100);
            holder.iv_delete_icon.setVisibility(View.GONE);
            holder.anim_play_audio.setVisibility(View.VISIBLE);
        } else {
            holder.anim_play_audio.setBackgroundResource(R.drawable.icon_cloud_call_voice_active_1);
            if (!Utility.isEmpty(getItem(position).getMobile())) {
                holder.iv_delete_icon.setVisibility(View.VISIBLE);
                holder.anim_play_audio.setVisibility(View.GONE);
            } else {
                holder.iv_delete_icon.setVisibility(View.GONE);
                holder.anim_play_audio.setVisibility(View.VISIBLE);
            }

        }

        if (position == getCount() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        holder.ll_modifyNO.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonOnclick.modidyNO(v, position, cloudVoiceMsgDetailEntries);
            }
        });

        holder.tv_notify_phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOnclick.mobileBtn(v, position);
            }
        });

        holder.ll_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOnclick.deleteMobile(v, position);
            }
        });

        holder.anim_play_audio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                buttonOnclick.playAudio(arg0, position);
            }
        });

        holder.rl_input_order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOnclick.inputOrderNo(v, position);
            }
        });

        holder.rl_input_order.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                buttonOnLongClickListener.orderLongClick(v, position, getItem(position).getOrder_no());
                return false;
            }
        });
        return convertView;
    }

    /*private void setWordSpan(String mobilePhone, TextView et){
        SpannableString ss = new SpannableString(mobilePhone);
        List<Integer> notDigitList = StringUtil.getNotNumberList(mobilePhone);
        if (null != notDigitList) {
            for (int i = 0; i < notDigitList.size(); i++) {
                int index = notDigitList.get(i);
                ss.setSpan(new AbsoluteSizeSpan(18, true), index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素。
            }
        }
        et.setText(ss);
    }*/

    public static class AnimationTimer extends TimerTask {
        final AnimationDrawable animation;

        public AnimationTimer(AnimationDrawable animation) {
            this.animation = animation;
        }

        @Override
        public void run() {

            animation.start();
            this.cancel();
        }
    }

    public void showAllItem() {
        count = SendYunHuActivity.YUNHU_MAX_LIST_COUNT;
        isShowAll = true;
        notifyDataSetChanged();
    }

    public void showItems(int position) {
        count = position;
        isShowAll = false;
        notifyDataSetChanged();
    }

    public boolean isShowAllItems() {
        return isShowAll;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public interface ButtonOnLongClickListener {
        void orderLongClick(View v, int position, String orderNo);
    }

    public interface ButtonOnclick {
        /**
         * 修改编号
         **/
        void modidyNO(View v, int position, List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries);

        /**
         * 修改手机号
         **/
        void mobileBtn(View v, int position);

        /**
         * 删除手机号
         **/
        void deleteMobile(View v, int position);

        /**
         * 开始录音按钮
         **/
        void playAudio(View v, int position);

        /**
         * 点击输入单号方法
         *
         * @param v        被点击的控件
         * @param position 点击的列表条目下标
         */
        void inputOrderNo(View v, int position);
    }

    class ViewHolder {
        RelativeLayout ll_modifyNO = null;// 修改编号控件
        TextView tv_notify_num = null;// 编号
        TextView tv_notify_phone = null;// 手机号码
        LinearLayout ll_delete = null;// 删除按钮
        ImageView iv_delete_icon = null;// 删除按钮图片
        ImageView anim_play_audio = null;// 语音识别动画
        View line = null;// 线条
        RelativeLayout rl_input_order = null;// 录入单号控件
        TextView tv_order_no = null;// 单号显示框
    }
}
