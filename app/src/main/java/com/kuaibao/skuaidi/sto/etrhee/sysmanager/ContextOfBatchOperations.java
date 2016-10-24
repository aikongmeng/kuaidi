package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.entry.NotifyInfo;

import java.util.List;

/**
 * 
 * 
 * @author a4
 * 
 */
public class ContextOfBatchOperations {
	BatchOperations operation;

	public ContextOfBatchOperations(Context context, EthreeInfoScanAdapter adapter, String scanType, ImageView iv,
			EditTextMaxLengthListener editTextMaxLengthListener, List<NotifyInfo> nextsite_list,
			List<NotifyInfo> upsite_list, View view) {
		if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
			operation = new FaBatchOperation(context, adapter, scanType, iv, editTextMaxLengthListener, view,
					nextsite_list);

		} else if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
			operation = new DaoBatchOperation(context, adapter, scanType, iv, editTextMaxLengthListener, view,
					upsite_list);

		} else if (E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType) || E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)) {

			operation = new LanBatchOperation(context, adapter, scanType, iv, editTextMaxLengthListener, view);

		} else if ((E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_BADPICE
				.equals(scanType))) {
			operation = new SignBadBatchOperation(context, adapter, scanType, iv, editTextMaxLengthListener, view);
		}else if(E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)){
			operation = new ThirdBatchOperation(adapter);
		}

	}

	public void doAction() {
		operation.operate();
	}

}
