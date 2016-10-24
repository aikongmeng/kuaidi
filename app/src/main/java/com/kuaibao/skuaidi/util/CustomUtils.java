package com.kuaibao.skuaidi.util;

import android.os.AsyncTask;

import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuaibao on 2016/5/9.
 */
public class CustomUtils {

    private int ALL_CUSTOMS = 1;//查询全部客户
    private int BANED_CUSTOMS = 2;//查询禁止录音组客户
    private int NOT_BANED_CUSTOMS = 3;//查询非禁止录音组的客户
    private List<MyCustom> list;
    private SkuaidiNewDB newDB  = SkuaidiNewDB.getInstance();

    public void getCusFromDB(final UpdateCustom updateCustom, final int type){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                list = new ArrayList<MyCustom>();
                if(type == ALL_CUSTOMS){
                    List<MyCustom> cusoms = UtilToolkit.filledData(newDB.selectAllCustomer());
                    list.clear();
                    list.addAll(cusoms);
                    Collections.sort(list, new PinyinComparator());
                }else if(type == BANED_CUSTOMS) {
                    List<MyCustom> cusoms = UtilToolkit.filledData(newDB.selectAllCustomer());
                    list.clear();
                    for (int i = 0; i < cusoms.size(); i++) {
                        MyCustom custom = cusoms.get(i);
                        if(custom.getGroup() == MyCustom.GROUP_BANED_RECORD){
                            list.add(custom);
                        }
                    }
                    Collections.sort(list, new PinyinComparator());
                }else if(type == NOT_BANED_CUSTOMS){
                    List<MyCustom> cusoms = UtilToolkit.filledData(newDB.selectAllCustomer());
                    list.clear();
                    for (int i = 0; i < cusoms.size(); i++) {
                        MyCustom custom = cusoms.get(i);
                        if(custom.getGroup() != MyCustom.GROUP_BANED_RECORD){
                            list.add(custom);
                        }
                    }
                    Collections.sort(list, new PinyinComparator());
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                updateCustom.updateCustomList(list);
            }
        }.execute();
    }

    /**
     * 获取数据库中的最新的客户信息
     */
    public interface UpdateCustom{
        void updateCustomList(List<MyCustom> customs);
    }
}
