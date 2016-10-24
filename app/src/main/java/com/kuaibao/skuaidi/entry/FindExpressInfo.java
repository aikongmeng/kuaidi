package com.kuaibao.skuaidi.entry;

public class FindExpressInfo {
	private String order;
	private String state;

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "FindExpressInfo [order=" + order + ", state=" + state + "]";
	}

}
