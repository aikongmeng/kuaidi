package com.kuaibao.skuaidi.entry;

import com.kuaibao.skuaidi.util.SkuaidiSpf;
/**
 * 创建巴枪扫描数据对象的简单工厂
 * @author a4
 *
 */
public class ScanDatasFactory {

	public  IScanDatas createScanDatas() {
		IScanDatas datas = null;
		String brand = SkuaidiSpf.getLoginUser().getExpressNo();
		if ("qf".equals(brand)) {
			datas = new QFDatas();
		} else if ("zt".equals(brand)) {
			datas = new ZTDatas();
		} else if ("sto".equals(brand)) {
			datas = new STODatas();
		}

		return datas;
	}

}
