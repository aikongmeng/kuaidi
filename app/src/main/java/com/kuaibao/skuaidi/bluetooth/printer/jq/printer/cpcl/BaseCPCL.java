package com.kuaibao.skuaidi.bluetooth.printer.jq.printer.cpcl;

import android.util.Log;

import com.kuaibao.skuaidi.bluetooth.printer.jq.port.Port;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.PrinterParam;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.Printer_define.PRINTER_MODEL;

import java.io.UnsupportedEncodingException;

public class BaseCPCL {
	protected PRINTER_MODEL _model;
	protected Port _port;
	protected boolean _support;
	protected PrinterParam _param;
	protected byte[] _cmd = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public BaseCPCL(PrinterParam param) {
		_param = param;
		_model = _param.model;
		_port = _param.port;
		_support = _param.cpclSupport;		
	}
	
	protected boolean portSendCmd(String cmd)
	{
		cmd +="\r\n";
		byte[] data = null;
		try 
		{
			data = cmd.getBytes("GBK");
		} 
		catch (UnsupportedEncodingException e) 
		{
			Log.e("JQ", "Sting getBytes('GBK') failed");
			return false;
		}
		return _port.write(data, 0, data.length);
	}
}