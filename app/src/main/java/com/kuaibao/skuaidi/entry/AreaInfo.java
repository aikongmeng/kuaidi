package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * 取派与超派区域
 * 
 * @author wq
 * 
 */
public class AreaInfo implements Serializable {

	private static final long serialVersionUID = 8235145166462152313L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	private String name;
	private String[] items;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

}
