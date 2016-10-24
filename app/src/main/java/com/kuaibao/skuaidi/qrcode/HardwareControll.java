package com.kuaibao.skuaidi.qrcode;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.android.barcodecontroll.BarcodeControll;

import java.io.UnsupportedEncodingException;

public class HardwareControll implements BarcodeControll.ScanCallBack {
	
	private  Handler m_handler = null;
	static public final int BARCODE_READ = 0;
	private static boolean start_Scan = false;
	private BarcodeControll barcodeControll = new BarcodeControll(this);
	public static int flag = 0;
	
	public HardwareControll(Handler mHandler){
		m_handler = mHandler;
	}
	
	public  void Open() {
		barcodeControll.Barcode_open();
		start_Scan = false;
		
	}

	public  void Close() {
		set_power(0x04);
		start_Scan = false;
		barcodeControll.Barcode_Close();
	}

	public  void scan_start() {
		barcodeControll.Barcode_StartScan();
		SpecialEquipmentScanActivity.ledC.SetRedLed(true);
		start_Scan = true;
	}

	public  void scan_stop() {
		SpecialEquipmentScanActivity.ledC.SetRedLed(false);
		barcodeControll.Barcode_StopScan();
	}

	public  void set_power(int controll) {
		barcodeControll.setpowerstate(controll);
		
	}
	public void onScanResults(byte[] data) {
		if(m_handler != null&&start_Scan){
		set_power(0x04);
		set_power(0x03);
		SpecialEquipmentScanActivity.ledC.SetRedLed(false);
		SpecialEquipmentScanActivity.ledC.SetGreenLed(true);
		Message msg = new Message();
		try {
			String info = null;
			info = new String(data, "GBK");
			msg.what = BARCODE_READ;
			msg.obj = info;
			m_handler.sendEmptyMessageDelayed(100, 100);
			m_handler.sendMessage(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		}
	}
	
	public  void writeData(byte[] data) {
		barcodeControll.Barcode_Write(data);
	}

	public  void singleCmd() {
		try {
			if (Build.BRAND.equals("Sinfone")){
				scan_start();
			}
			
			wakeUpUE988();
			byte[] b1 = { 7, (byte) 198, 4, 8, (byte) 255, (byte) 159, 0,
					(byte) 253, (byte) 137 };
			barcodeControll.Barcode_Write(b1);
			Thread.sleep(200);
			byte[] b2 = { 7, (byte) 198, 4, 8, (byte) 255, (byte) 235, 1,
					(byte) 253, 60 };
			barcodeControll.Barcode_Write(b2);
			Thread.sleep(200);
			byte[] b3 = { 7, (byte) 198, 4, 8, 0, (byte) 138, 2, (byte) 254,
					(byte) 155 };
			barcodeControll.Barcode_Write(b3);
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Build.BRAND.equals("Sinfone")){
			scan_stop();
		}
	}

	public  void continueCmd() {
		try {
			
			if (Build.BRAND.equals("Sinfone")){
				scan_start();
			}
			
			wakeUpUE988();
			byte[] b1 = { 7, (byte) 198, 4, 8, (byte) 255, (byte) 159, 0,
					(byte) 253, (byte) 137 };
			
			barcodeControll.Barcode_Write(b1);
			Thread.sleep(200);
			byte[] b2 = { 7, (byte) 198, 4, 8, (byte) 255, (byte) 235, 1,
					(byte) 253, (byte) 60 };
			barcodeControll.Barcode_Write(b2);
			Thread.sleep(200); 
			byte[] b3 = { 7, (byte) 198, 4, 8, 20, (byte) 138, 5, (byte) 254,
					(byte) 132 };
			barcodeControll.Barcode_Write(b3);
			Thread.sleep(500);
			scan_stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (Build.BRAND.equals("Sinfone")){
				scan_stop();
			}
		}
	}

	static byte[] wakeupcmd = { 0 };

	private  void wakeUpUE988() {
		barcodeControll.Barcode_Write(wakeupcmd);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
