package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.widget.ListView;

import com.kuaibao.skuaidi.entry.NotifyInfo;

import java.util.List;

/**
 * 保存，上传之前的数据完整性检查接口
 * @author a4
 *
 */
public interface DataChecker {
	boolean check(ListView lv, List<NotifyInfo> list);
}
