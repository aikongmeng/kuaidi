package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.widget.ListView;

import com.kuaibao.skuaidi.entry.NotifyInfo;

import java.util.List;

public class DataManager {

    DataChecker dataChecker;

    public DataManager(String scanType) {
        if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
            dataChecker = new BadDataChecker();
        } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
            dataChecker = new SignDataChecker();
        } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
            dataChecker = new FaDataChecker();
        } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
            dataChecker = new ThirdBranchDataChecker();
        }
    }

    /**
     * 判断数据是否完整
     *
     * @param lv
     * @param list
     * @return
     */
    public boolean isComplete(ListView lv, List<NotifyInfo> list) {
        return dataChecker == null ? true : dataChecker.check(lv, list);
    }
}
