package com.kuaibao.skuaidi.entry;

import org.json.JSONArray;

import java.util.List;
/**
 * 抽象数据接口
 * @author a4
 *
 */
public interface IScanDatas {

	JSONArray getUploadDatas(List<E3_order> orders);
}
