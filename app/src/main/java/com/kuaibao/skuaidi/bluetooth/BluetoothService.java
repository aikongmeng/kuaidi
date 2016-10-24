package com.kuaibao.skuaidi.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * 蓝牙服务类
 * 
 * @author 徐洋
 * 
 */

@SuppressLint("NewApi")
public class BluetoothService {

	public static final int STATE_CONNECTED = 0;
	public static final int STATE_DISCONNECTED = 1;
	public static final int STATE_TURN_ON = 2;
	public static final int STATE_TURN_OFF = 3;

	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private OutputStream outputStream = null;
	private static final int REQUEST_OPEN_BT_CODE = 1;
	private static BluetoothService service;
	private static Context mContext;
	private boolean isConnected = false;

	private BluetoothService() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		initDeviceListener();
	}

	public static synchronized BluetoothService getService(Context context) {
		mContext = context;
		if (service == null) {
			service = new BluetoothService();
		}
		return service;
	}

	/**
	 * 打开蓝牙
	 * 
	 * @param activity
	 */
	public void openBluetooth(Activity activity) {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
	}


	public void openBluetooth(){
		bluetoothAdapter.enable();
	}

	/**
	 * 关闭蓝牙
	 */
	public void closeBluetooth() {
		bluetoothAdapter.disable();
	}

	/**
	 * 判断蓝牙是否打开
	 * 
	 * @return
	 */
	public boolean isOpen() {
		return bluetoothAdapter.isEnabled();
	}

	/**
	 * 扫描蓝牙设备
	 */
	public void startDiscovery() {
		bluetoothAdapter.startDiscovery();
	}

	/**
	 * 取消扫描
	 */
	public void cancelDiscovery() {
		bluetoothAdapter.cancelDiscovery();
	}

	/**
	 * 是否正处于扫描过程中
	 * 
	 * @return
	 */
	public boolean isDiscoverying() {
		return bluetoothAdapter.isDiscovering();
	}

	/**
	 * 获取蓝牙设备Name
	 * 
	 * @return
	 */
	public String getName() {
		return bluetoothAdapter.getName();
	}

	/**
	 * 获取蓝牙设备的硬件地址(MAC地址),例如:00:11:22:AA:BB:CC
	 * 
	 * @return
	 */
	public String getAddress() {
		return bluetoothAdapter.getAddress();
	}

	/**
	 * 设置蓝牙设备的name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		bluetoothAdapter.setName(name);
	}

	public boolean isConnect() {
		if (bluetoothAdapter == null) {
			return false;
		} else if (!isOpen()) {
			return false;
		}
		return isConnected;
	}

	public boolean connect(BluetoothDevice device) {
		if (!isConnected) {
			try {
				bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
				bluetoothSocket.connect();
				outputStream = bluetoothSocket.getOutputStream();
				isConnected = true;
				if (bluetoothAdapter.isDiscovering()) {
					//System.out.println("关闭适配器！");
					this.bluetoothAdapter.isDiscovering();
				}
			} catch (Exception e) {
				// Toast.makeText(this.context, "连接失败！", 1).show();
				return false;
			}
			// Toast.makeText(this.context, this.device.getName() + "连接成功！",
			// Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return true;
		}
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * 断开蓝牙设备连接
	 */
	public void disconnect() {
		//System.out.println("断开蓝牙设备连接");
		isConnected = false;
		if (bluetoothSocket == null || outputStream == null) {
			return;
		}
		try {
			bluetoothSocket.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getState() {
		return bluetoothAdapter.getState();
	}

	/**
	 * 获取绑定列表
	 * 
	 * @return
	 */
	public List<BluetoothDevice> getBondList() {
		return new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices());
	}

	/**
	 * 添加已绑定设备到集合
	 * 
	 * @param device
	 */
	public void addBoundDevices(BluetoothDevice device) {

	}

	/**
	 * 添加未绑定设备到集合
	 * 
	 * @param device
	 */
	public void addUnBoundDevices(BluetoothDevice device) {

	}

	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}

	protected void changeState(boolean isConnected) {
		this.isConnected = isConnected;
	}

	private void initDeviceListener() {
		int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
		int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
		int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
		int gatt = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);
		int gattserver = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT_SERVER);
		int flag = -1;

		if (a2dp == BluetoothProfile.STATE_CONNECTED) {
			flag = a2dp;
		} else if (headset == BluetoothProfile.STATE_CONNECTED) {
			flag = headset;
		} else if (health == BluetoothProfile.STATE_CONNECTED) {
			flag = health;
		} else if (gatt == BluetoothProfile.STATE_CONNECTED) {
			flag = gatt;
		} else if (gattserver == BluetoothProfile.STATE_CONNECTED) {
			flag = gattserver;
		}

		if (flag != -1) {
			bluetoothAdapter.getProfileProxy(mContext, new ServiceListener() {
				@Override
				public void onServiceDisconnected(int profile) {
					// TODO Auto-generated method stub
					isConnected = false;
				}

				@SuppressLint("NewApi")
				@Override
				public void onServiceConnected(int profile, BluetoothProfile proxy) {
					// TODO Auto-generated method stub
					List<BluetoothDevice> mDevices = proxy.getConnectedDevices();
					if (mDevices != null && mDevices.size() > 0) {
						for (BluetoothDevice device : mDevices) {
							//Log.i("W", "device name:" + device.getName());
						}
						isConnected = true;
					} else {
						isConnected = false;
					}

				}

			}, flag);
			isConnected = true;
		}
	}
}
