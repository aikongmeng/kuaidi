package com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl;

import com.kuaibao.skuaidi.bluetooth.printer.jq.port.Port;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.PrinterParam;

public class BaseJPL
{
	protected PrinterParam _param;
	protected Port _port;
	protected byte _cmd[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	/*
	 * 构造函数
	 */
	public BaseJPL(PrinterParam param) {
		_param = param;
		_port = _param.port;
	}
}
