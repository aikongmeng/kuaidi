package com.kuaibao.skuaidi.activity.sendcloudcall.utility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.sendcloudcall.adapter.SendCloudCallBachSignAdapter;
import com.kuaibao.skuaidi.dao.SaveNoDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 顾冬冬
 */
public class SendCloudCallBachSignUtility {

    /**
     * 自定义编号
     */
    public static void customNo(Context context, final SendCloudCallBachSignAdapter adapter, final int position, final List<NumberPhonePair> listData) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        UMShareManager.onEvent(context, "SendMSG_CustomNo", "SendMSG", "发短信:自定义编号");
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        dialog.setTitle("设置起始编号");
        dialog.isUseEditText(true);
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.showEditTextTermsArea(true);
        dialog.setEditTextContent(5);
        dialog.setSendSmsNoTerms(true);
        dialog.setDonotAutoDismiss(true);
        dialog.setEditText(listData.get(position).getBh());
        dialog.setEditTextHint("最大99999，前两位支持输入字母");
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                if (!Utility.isEmpty(dialog.getEditTextContent())) {
                    if (!dialog.getEditTextContent().trim().isEmpty()) {
                        if (dialog.isInputContentFail()) {// 如果输入内容正确
                            String cusNumber = dialog.getEditTextContent();
                            if (dialog.isSelectEditTextTermsArea()) {// 只修改当前编号
                                listData.get(position).setBh(cusNumber);
                                adapter.notifyDataSetChanged();
                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                dialog.dismiss();
                            } else {// 修改以下所以编号

                                String firstWord = cusNumber.substring(0, 1);
                                Pattern p = Pattern.compile("[a-zA-Z]");
                                Matcher m, m2, m3;
                                if (cusNumber.length() >= 2) {// 编号长度>=2
                                    String secondWord = cusNumber.substring(1, 2);
                                    m = p.matcher(firstWord);
                                    m2 = p.matcher(secondWord);
                                    if (m.matches() && m2.matches()) {// 前面两个字符都是字母【当前两位为字母时】
                                        if (cusNumber.length() > 2) {// 编号长度>2
                                            try {
                                                int customNo = Integer.parseInt(cusNumber.substring(2));// 将输入编号转为整型
                                                if (customNo <= 999) {
                                                    for (int i = position, j = listData.size(); i < j; i++) {
                                                        NumberPhonePair npp = listData.get(i);
                                                        if (customNo > 999) {
                                                            customNo = 1;
                                                        }
                                                        npp.setBh(cusNumber.substring(0, 2) + customNo);
                                                        customNo++;
                                                    }
                                                    adapter.notifyDataSetChanged();
//                                                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                    // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                    imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                                } else {
                                                    UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                    return;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            dialog.dismiss();
                                        } else {// 编号长度==2&编号全为字母
                                            UtilToolkit.showToast("字母后面需输入数字");
                                            return;
                                        }
                                    } else if (m.matches()) {// 只有第一个字符是字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber.substring(1));// 将输入编号转为整型
                                            if (customNo <= 9999) {
                                                for (int i = position,j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 9999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(cusNumber.substring(0, 1) + customNo);
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    } else {// 没有字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position,j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                } else {// 编号长度<2
                                    m3 = p.matcher(firstWord);
                                    if (m3.matches()) {// 编号长度==1&为字母
                                        UtilToolkit.showToast("字母后面需输入数字");
                                        return;
                                    } else {// 编号长度==1&为数字
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position,j = listData.size(); i < j; i++) {
                                                    NumberPhonePair npp = listData.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    npp.setBh(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.notifyDataSetChanged();
//                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                }
                            }

                        } else {
                            UtilToolkit.showToast("起始编号格式有误，请重新输入");
                            return;
                        }
                    }
                }
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
            }
        });
        dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {

            @Override
            public void onClick() {
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                dialog.dismiss();
            }
        });
        dialog.showDialog();
        Utility.showKeyBoard((EditText) dialog.getEditTextView(), true);
    }

    /**
     * 设置列表中的编号
     */
    public static  List<NumberPhonePair> setBH( List<NumberPhonePair> listData){
        String lastNoHead = "";// 最后一个编号的前两个字符
        int lastNo = 1;// 最后一个编号【数字】
        SaveNoEntry saveNoEntry = SaveNoDAO.getSaveNo(SaveNoDAO.NO_CLOUD_BACHSIGN);
        if (saveNoEntry != null) {
            lastNoHead = saveNoEntry.getSave_letter();// 获取保存的字母
            lastNo = saveNoEntry.getSave_number();// 获取保存的数字
            if (Utility.isEmpty(lastNo)) {
                lastNo = 1;
            }

            String save_time = UtilityTime.getDateTimeByMillisecond(saveNoEntry.getSaveTime() / 1000, UtilityTime.YYYY_MM_DD);
            String cur_time = UtilityTime.getDateTimeByMillisecond(System.currentTimeMillis() / 1000, UtilityTime.YYYY_MM_DD);
            String save_time_day, cur_time_day;

            if (!Utility.isEmpty(save_time)) {
                save_time_day = save_time.substring(save_time.length() - 2, save_time.length());
                cur_time_day = cur_time.substring(cur_time.length() - 2, cur_time.length());
                if (!save_time_day.equals(cur_time_day)) {
                    lastNo = 1;
                }
            }
        }

        for (int i = 0; i < listData.size(); i++) {
            if (lastNo > 99999) {
                lastNo = 1;
            }
            listData.get(i).setBh(lastNoHead + lastNo);
            lastNo++;
        }
        return listData;
    }

    /**获取编号格式
     * 字母和数字之间用‘;’号隔开**/
    public static String getBhFormat(String bh){
        if (!Utility.isEmpty(bh)){
            if (bh.length()>2){// 字符长度大于2
                if (Character.isLetter(bh.charAt(0)) && Character.isLetter(bh.charAt(1))){// 如果第一和第二个字符是字母
                    return bh.substring(0,2)+";"+bh.substring(2);
                }else if(Character.isLetter(bh.charAt(0))){// 如果第一个字符是字母
                    return bh.substring(0,1)+";"+bh.substring(1);
                }else{// 字符中没有字母
                    return ";"+bh;
                }
            }else if(bh.length()>1){// 字符长度为2
                if (Character.isLetter(bh.charAt(0))){// 如果第一个字符是字母
                    return bh.substring(0,1)+";"+bh.substring(1);
                }else{
                    return ";"+bh;
                }
            }else{// 字符长度为1
                return ";"+bh;
            }
        }
        return "";
    }

    /**
     * 获取保存编号对象
     **/
    public static SaveNoEntry getSaveNoEntry(String save_letter, int save_number) {
        SaveNoEntry saveNoEntry = new SaveNoEntry();
        saveNoEntry.setSave_from(SaveNoDAO.NO_CLOUD_BACHSIGN);
        saveNoEntry.setSaveTime(System.currentTimeMillis());
        saveNoEntry.setSave_userPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        saveNoEntry.setSave_letter(save_letter);
        saveNoEntry.setSave_number(save_number);
        return saveNoEntry;
    }

    public static void setNextEditTextFoucs(ListView list,int position,List<NumberPhonePair> listData){
        if((position)==listData.size()){
            return;
        }
//        View localView=list.getChildAt(position);
        View localView = getViewByPosition(position,list);
        if(localView !=null){
            if(localView.findViewById(R.id.et_PhoneNo)!=null){
                EditText et_PhoneNo= (EditText) localView.findViewById(R.id.et_PhoneNo);
                KLog.i("kb","下一位置的EditText is null ?"+et_PhoneNo==null?"true":"false");
                if(et_PhoneNo.isEnabled()){
                    if(position+2<=listData.size())
                        list.smoothScrollToPosition(position+2);
                    et_PhoneNo.requestFocus();
                    et_PhoneNo.setFocusable(true);
                    et_PhoneNo.setSelected(true);
                }else{
                    setNextEditTextFoucs(list,position+1,listData);
                }
            }else{
                KLog.i("kb","findViewById(R.id.iv_play_netcall_audio) is null");
            }
        }else{
            KLog.i("kb","lv.getChildAt is null");
        }
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /** 获取列表中有手机号码的条目 **/
    @Nullable
    public static List<NumberPhonePair> getExistHaveACellPhoneNumberNewList(List<NumberPhonePair> list){
        List<NumberPhonePair> newList = new ArrayList<>();
        for (int i = 0;i<list.size();i++){
            String phone = list.get(i).getPhone();
            if (!Utility.isEmpty(phone)){
                phone = phone.replaceAll(" ","");
                phone = phone.replaceAll("-","");
                if (phone.length()>=11 && phone.length()<=12){
                    newList.add(list.get(i));
                }else{
                    UtilToolkit.showToast("编号为" + list.get(i).getBh() + "的手机号有误");
                    return null;
                }
            }
        }
        return newList;
    }

    /**
     * 获取列表中是否在在相同的手机号
     */
    public static boolean existTheSameMobilePhone(List<NumberPhonePair> npp) {
        boolean exist = false;
        for (int i = 0; i < npp.size() - 1; i++) {
            for (int j = i + 1; j < npp.size(); j++) {
                if (!Utility.isEmpty(npp.get(i).getPhone()) && !Utility.isEmpty(npp.get(j).getPhone())) {
                    if (npp.get(i).getPhone().equals(npp.get(j).getPhone())) {
                        String index = i + ";" + j;
                        MessageEvent messageEvent = new MessageEvent(0XBB1, index);
                        EventBus.getDefault().post(messageEvent);
                        exist = true;
                        break;
                    }
                }
            }
            if (exist) {
                break;
            }
        }
        return exist;
    }
}
