package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ContinuousScanAdapter extends BaseAdapter {

    NotifyInfo info;
    LayoutInflater inflater;
    List<NotifyInfo> infos;
    Handler handler;
    private int position = -1;
    private List<NotifyInfo2> info2s = null;
    private ArrayList<E3_order> orders;
    private String type = "";
    private Context context;
    private int qrcodetype = 0;
    private PicViewClickListener mPicViewClickListener;
    /**
     * 重复单号
     */
    private List<NotifyInfo> repeatList;
    private boolean notCheckRepeat = false;

    private int outOfNumber = 1;

    private String from;// 来源
    private List<CloudVoiceMsgDetailEntry> cloudInfos = null;// 发送云呼界面列表实体


    public ContinuousScanAdapter(Context context, Handler handler, List<NotifyInfo> infos, int qrcodetype, String tag) {
        super();
        this.context = context;
        this.infos = infos;
        this.handler = handler;
        this.qrcodetype = qrcodetype;
        inflater = LayoutInflater.from(context);
    }

    public ContinuousScanAdapter(Context context, Handler handler, List<NotifyInfo> infos, String type) {
        super();
        this.context = context;
        this.infos = infos;
        this.handler = handler;
        inflater = LayoutInflater.from(context);
        this.type = type;

    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public NotifyInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setNotCheckRepeat(boolean notCheckRepeat) {
        this.notCheckRepeat = notCheckRepeat;
    }

    public List<NotifyInfo> getRepeatList() {
        return repeatList;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (qrcodetype == Constants.TYPE_COLLECTION || qrcodetype == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
                convertView = inflater.inflate(R.layout.capture_list_collection, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.capture_list, parent, false);
            }
            holder.tv_capture_num = (TextView) convertView.findViewById(R.id.tv_capture_num);
            holder.tv_capture_order = (TextView) convertView.findViewById(R.id.tv_capture_order);
            holder.iv_capture_del = (ImageView) convertView.findViewById(R.id.iv_capture_del);
            holder.rl_del = (RelativeLayout) convertView.findViewById(R.id.rl_del);
            holder.ll_info = (ViewGroup) convertView.findViewById(R.id.ll_info);
            holder.tv_express_status_tip = (TextView) convertView.findViewById(R.id.tv_express_status_tip);
            holder.tv_express_status = (TextView) convertView.findViewById(R.id.tv_express_status);
            holder.tv_actiondesc = (TextView) convertView.findViewById(R.id.tv_actiondesc);
            if (qrcodetype != Constants.TYPE_COLLECTION && qrcodetype != Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
                holder.tv_actiondesc.setTag(position);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        info = infos.get(position);

        // 普通扫描界面部分
        if (qrcodetype == Constants.TYPE_PAIJIAN || qrcodetype == Constants.TYPE_PAIJIAN_ONE || qrcodetype == Constants.TYPE_FIND_EXPRESS || qrcodetype == Constants.TYPE_COLLECTION || qrcodetype == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
            if (qrcodetype != Constants.TYPE_COLLECTION && qrcodetype != Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
                holder.ll_info.setVisibility(View.GONE);
                holder.tv_actiondesc.setVisibility(View.GONE);
            }
        } else {// E3功能有关扫描部分
            if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(type) && !TextUtils.isEmpty(info.getPicPath())) {
                holder.tv_actiondesc.setVisibility(View.VISIBLE);
                holder.tv_actiondesc.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(info.getPicPath())) {
                            if (mPicViewClickListener != null) {
                                mPicViewClickListener.onClick(v, position);
                            }
                        }
                    }
                });
            } else {
                holder.tv_actiondesc.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(info.getStatus())) {
                if (qrcodetype != Constants.TYPE_COLLECTION && qrcodetype != Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
                    holder.ll_info.setVisibility(View.GONE);
                }
            } else {
                if (qrcodetype != Constants.TYPE_COLLECTION && qrcodetype != Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {
                    holder.ll_info.setVisibility(View.VISIBLE);
                    holder.tv_express_status.setText(info.getStatus());
                }
            }
            if (haveRepetition(info)) {
                holder.tv_capture_order.setTextColor(context.getResources().getColor(R.color.red_f74739));
                if (((CaptureActivity) context).playRepeatedTone) {
                    ((CaptureActivity) context).playRepeatedTone();
                }
            } else {
                holder.tv_capture_order.setTextColor(context.getResources().getColor(R.color.text_black));

            }
            ((CaptureActivity) context).playRepeatedTone = false;
            if (info.getIsUpload() == 1) {
                holder.iv_capture_del.setImageResource(R.drawable.icon_uploaded);
            } else {
                holder.iv_capture_del.setImageResource(R.drawable.icon_delete);
            }
        }

        if (this.position != -1) {
            if (!Utility.isEmpty(from) && "yunhu".equals(from)) {
                if (position+this.position<cloudInfos.size()) {
                    holder.tv_capture_num.setText(cloudInfos.get(position + this.position).getMobile_no());
                    if(Utility.isNumeric(cloudInfos.get(cloudInfos.size()-1).getMobile_no())){
                        outOfNumber = Integer.parseInt(cloudInfos.get(cloudInfos.size()-1).getMobile_no());
                    }
                }else {
                    outOfNumber++;
                    holder.tv_capture_num.setText(outOfNumber+"");
                }
            } else {
                if (position+this.position < info2s.size()){
                    holder.tv_capture_num.setText(info2s.get(position + this.position).getExpressNo());
                    if(Utility.isNumeric(info2s.get(info2s.size()-1).getExpressNo())){
                        outOfNumber = Integer.parseInt(info2s.get(info2s.size()-1).getExpressNo());
                    }
                }else{
                    outOfNumber++;
                    holder.tv_capture_num.setText(outOfNumber+"");
                }
            }
        } else {
            holder.tv_capture_num.setText((position + 1) + "");
        }

        holder.tv_capture_order.setText(info.getExpress_number());
        holder.iv_capture_del.setOnClickListener(new OnClickListener() {// 点击删除按钮

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                E3_order order = new E3_order();
                order.setCompany(((CaptureActivity) context).company);
                try {
                    order.setOrder_number(infos.get(position).getExpress_number());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }
                order.setCourier_job_no(((CaptureActivity) context).courierNO);
                order.setType(E3SysManager.scanToTypeMap.get(type));

                E3SysManager.deletePic(infos.get(position).getPicPath());
                E3OrderDAO.deleteCacheOrder(order);
                msg.what = Constants.SCAN_DEL;
                msg.obj = infos.get(position).getExpress_number();
                handler.sendMessage(msg);
                infos.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public int getOrderNo() {
        return position;
    }

    public static class ViewHolder {
        TextView tv_capture_num;
        TextView tv_capture_order;
        TextView tv_express_status_tip;
        ViewGroup ll_info;
        TextView tv_express_status;
        TextView tv_actiondesc;
        ImageView iv_capture_del, repeat;
        RelativeLayout rl_del;
    }

    /**
     * @param position
     * @param list
     */
    public void notifyDataSetChanged(int position, List<NotifyInfo2> list) {
        findRepeatList(infos, orders);
        this.position = position;
        this.info2s = list;
        notifyDataSetChanged();
    }

    /**
     * @param position
     * @param from
     * @param list
     */
    public void notifyDataSetChangedYunhu(int position, String from, List<CloudVoiceMsgDetailEntry> list) {
        this.position = position;
        this.from = from;
        this.cloudInfos = list;
        notifyDataSetChanged();
    }

    /**
     * 专为从发短信界面进入的时候传来带字母的编号参数使用的
     */
    public void setPicViewClickListener(PicViewClickListener picViewClickListener) {
        mPicViewClickListener = picViewClickListener;
    }

    /**
     * 是否重复
     */
    private boolean haveRepetition(NotifyInfo info) {
        if (!notCheckRepeat) {
            findTodayOrders();
            ArrayList<String> numberList = new ArrayList<>();
            if (orders != null) {
                for (E3_order order : orders) {
                    numberList.add(order.getOrder_number());
                }
            }
            return checkRepeat(info, numberList);
        }
        return false;
    }

    public interface PicViewClickListener {
        void onClick(View v, int position);
    }

    private boolean checkRepeat(NotifyInfo info, ArrayList<String> numberList) {
        /*if (numberList != null && numberList.contains(info.getExpress_number()) || !TextUtils.isEmpty(info.getStatus())
                && (type != null && type.contains(info.getStatus())) || "扫收件".equals(type) && !TextUtils.isEmpty(info.getStatus())
                || "问题件".equals(type) && "疑难件".equals(info.getStatus()) || "扫签收".equals(type)
                && "已签收".equals(info.getStatus()) || "扫派件".equals(type) && !TextUtils.isEmpty(info.getStatus())
                && info.getStatus().contains("派")) {
            return true;
        }*/
        return (numberList != null && numberList.contains(info.getExpress_number()) || !TextUtils.isEmpty(info.getStatus())
                && (type != null && type.contains(info.getStatus())) || "扫收件".equals(type) && !TextUtils.isEmpty(info.getStatus())
                || "问题件".equals(type) && "疑难件".equals(info.getStatus()) || "扫签收".equals(type)
                && "已签收".equals(info.getStatus()) || "扫派件".equals(type) && !TextUtils.isEmpty(info.getStatus())
                && info.getStatus().contains("派"));
//        return false;
    }

    private void findRepeatList(List<NotifyInfo> infos, ArrayList<E3_order> orders) {

        ArrayList<String> numberList = new ArrayList<>();
        if (orders != null) {
            for (E3_order order : orders) {
                numberList.add(order.getOrder_number());
            }
        }
        if (repeatList == null)
            repeatList = new ArrayList<>();
        else
            repeatList.clear();
        for (int i = 0; i < infos.size(); i++) {
            if (checkRepeat(infos.get(i), numberList)) {
                repeatList.add(infos.get(i));
            }
        }
    }

    private void findTodayOrders() {
        if (orders == null) {
            orders = E3OrderDAO.queryOrdersToday(E3SysManager.scanToTypeMap.get(type),
                    ((CaptureActivity) context).company, ((CaptureActivity) context).courierNO, false);

        }
    }
}
