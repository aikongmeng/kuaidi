package com.kuaibao.skuaidi.bluetooth.printer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

/**
 * Created by cj on 2016/10/24.
 */

public class PrinterManager {
    public static PrinterBase getPrinter(BluetoothDevice device, Activity activity, Handler handler){
        PrinterBase myPrinter = null;
        String expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
        if (!Utility.isEmpty(device.getName()) && device.getName().startsWith("QR")) {
            myPrinter = QRPrinter.getInstance(device);
        } else if (!Utility.isEmpty(device.getName()) && device.getName().startsWith("XT") && "zt".equals(expressNo)) {
            myPrinter = ZPPrinter.getInstance(device);
        } else if (!Utility.isEmpty(device.getName()) && device.getName().startsWith("KM")) {
            myPrinter = KMPrinter.getInstance(device);
        }else if (!Utility.isEmpty(device.getName()) && device.getName().startsWith("L3") && "zt".equals(expressNo)) {
            myPrinter = BTPrinter.getInstance(activity, handler);
        } else if (!Utility.isEmpty(device.getName()) && device.getName().startsWith("JLP") && "zt".equals(expressNo)) {
            myPrinter = JLPPrinter.getInstance();
        }
        return myPrinter;
    }
}
