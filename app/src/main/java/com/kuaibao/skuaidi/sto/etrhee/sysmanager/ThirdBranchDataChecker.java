package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.widget.ListView;

import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.util.List;

/**
 * 发件数据检查类
 * @author a4
 *
 */
public class ThirdBranchDataChecker implements DataChecker{
	@Override
	public boolean check(ListView lv,List<NotifyInfo> list) {
		
		for (int i = 0; i < list.size(); i++) {
			if (KuaiBaoStringUtilToolkit.isEmpty(list.get(i).getThirdBranch())) {
				UtilToolkit.showToast("请选择营业厅");
				lv.smoothScrollToPosition(i);
				return false;
			}
		}
		return true;
	}
}
