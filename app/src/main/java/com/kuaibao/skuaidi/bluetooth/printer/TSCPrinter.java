package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;

import com.example.tscdll.TSCActivity;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.Utility;

/**
 * Created by kuaibao on 2016/7/18.
 */

public class TSCPrinter extends PrinterBase {

    private TSCActivity tscDll;
    private BluetoothDevice printer;
    private Context context;
    private static TSCPrinter tscPrinter;

    private TSCPrinter(TSCActivity tscDll, BluetoothDevice printer, Context context){
        this.tscDll = tscDll;
        this.printer = printer;
        this.context = context;
    }

    public static TSCPrinter getInstance(TSCActivity tscDll, BluetoothDevice printer, Context context){
        if(Utility.isEmpty(tscPrinter)){
            tscPrinter = new TSCPrinter(tscDll, printer, context);
        }
        return tscPrinter;
    }

    @Override
    public void connect(BluetoothDevice device, final ConnectedCallBack callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(tscDll != null){
                    tscDll.openport(printer.getAddress());
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                callback.connectedCallback();
            }
        }.execute();
    }

    @Override
    public void connect(BluetoothDevice device) {
    }

    @Override
    public void disConnect() {
        if(Utility.isEmpty(tscDll)){
            tscDll = new TSCActivity();
        }
        tscDll.closeport();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        if(Utility.isEmpty(tscDll)){
            tscDll = new TSCActivity();
        }
        tscDll.openport(printer.getAddress());

        tscDll.setup(70, 110, 4, 4, 0, 0, 0);
        tscDll.clearbuffer();
        tscDll.sendcommand("SET TEAR ON\n");
        tscDll.sendcommand("SET COUNTER @1 1\n");
        tscDll.sendcommand("@1 = \"0001\"\n");
        tscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
        tscDll.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
        tscDll.printerfont(100, 250, "3", 0, 1, 1, "987654321");
        tscDll.printlabel(2, 1);

        tscDll.closeport();
    }

    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        if(Utility.isEmpty(tscDll)){
            tscDll = new TSCActivity();
        }
        tscDll.openport(printer.getAddress());

        tscDll.setup(70, 110, 4, 4, 0, 0, 0);
        tscDll.clearbuffer();
        tscDll.sendcommand("SET TEAR ON\n");
        tscDll.sendcommand("SET COUNTER @1 1\n");
        tscDll.sendcommand("@1 = \"0001\"\n");
        tscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
        tscDll.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
        tscDll.printerfont(100, 250, "3", 0, 1, 1, "987654321");
        tscDll.printlabel(2, 1);

        tscDll.closeport();
    }

    @Override
    public String getPrinterStatus() {
        return null;
    }
}
