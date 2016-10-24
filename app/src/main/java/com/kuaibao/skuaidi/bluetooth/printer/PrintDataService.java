package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.kuaibao.skuaidi.bluetooth.BluetoothService;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

public class PrintDataService {

	private static PrintDataService printDataService;
	private BluetoothService bluetoothService;
	private static Context mContext;
	private List<BluetoothDevice> devices;

	private PrintDataService() {
		bluetoothService = BluetoothService.getService(mContext);
		devices = bluetoothService.getBondList();
	}

	public static synchronized PrintDataService getPrinter(Context context) {
		mContext = context;
		if (printDataService == null) {
			printDataService = new PrintDataService();
		}
		return printDataService;
	}

	public void print(BluetoothDevice device,byte[] buffer) {

		if (bluetoothService.connect(device)) {
			try {
//				BluetoothPrinterUtil.printStoExpressBill(mContext,
//						bluetoothService.getOutputStream());
				bluetoothService.getOutputStream().write(buffer);
			} catch (InvalidParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
