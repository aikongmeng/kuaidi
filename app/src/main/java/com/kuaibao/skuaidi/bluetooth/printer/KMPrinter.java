package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import HPRTAndroidSDK.HPRTPrinterHelper;


/**
 * Created by kuaibao on 2016/7/17.
 */

public class KMPrinter extends PrinterBase{

    private BluetoothDevice device;
    private static HPRTPrinterHelper hprtPrinter;
    private static KMPrinter kmPrinter;

    private KMPrinter(HPRTPrinterHelper hprtPrinter, BluetoothDevice device){
        this.hprtPrinter = hprtPrinter;
        this.device = device;
    }

    public static KMPrinter getInstance(BluetoothDevice device){
        if(Utility.isEmpty(kmPrinter)){
            hprtPrinter = new HPRTPrinterHelper(SKuaidiApplication.getContext(), device.getName());
            kmPrinter = new KMPrinter(hprtPrinter, device);
        }
        return kmPrinter;
    }

    @Override
    public void connect(final BluetoothDevice device, final ConnectedCallBack callback) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                if(!Utility.isEmpty(hprtPrinter)) {
//                    KLog.i("tag", "正在连接打印机");
//                    hprtPrinter.PortOpen("Bluetooth," + device.getAddress());
//                }
//                return null;
//            }
//
//            protected void onPostExecute(Void result) {
//                callback.connectedCallback();
//            }
//        }.execute();
        if(!Utility.isEmpty(hprtPrinter)) {
            KLog.i("tag", "正在连接打印机");
            hprtPrinter.PortOpen("Bluetooth," + device.getAddress());
            callback.connectedCallback();
        }
    }

    @Override
    public void connect(BluetoothDevice device) {
        if(!Utility.isEmpty(hprtPrinter)) {
            hprtPrinter.PortOpen("Bluetooth," + device.getAddress());
        }
    }

    @Override
    public void disConnect() {
        if(!Utility.isEmpty(hprtPrinter) && hprtPrinter.IsOpened()){
            hprtPrinter.PortClose();
        }
    }

    @Override
    public boolean isConnected() {
        if(!Utility.isEmpty(hprtPrinter)){
            return hprtPrinter.IsOpened();
        }
        return false;
    }

    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        String charge = order.getCollection_amount();
        if(!Utility.isEmpty(hprtPrinter)) {
            if(!hprtPrinter.IsOpened()){
                hprtPrinter.PortOpen("Bluetooth,"+device.getAddress());
            }
            try
            {
                HPRTPrinterHelper.PrintData("BACKFEED 32");
                HPRTPrinterHelper.PrintData("SIZE 76 mm,200 mm");
                HPRTPrinterHelper.PrintData("DENSITY 6");
                HPRTPrinterHelper.PrintData("CLS ");
                HPRTPrinterHelper.PrintData("BOX 0,0,576,1600,0");
                HPRTPrinterHelper.PrintData("TEXT 260,30,\"1\",0,2,2,\"代收货款:￥"+charge+"\"");
                HPRTPrinterHelper.PrintData("REVERSE 260,26,"+(6*32+charge.length()*16)+",40");
                HPRTPrinterHelper.PrintData("BAR 0,96,576,1");
                //打印条码
                HPRTPrinterHelper.PrintData("BARCODE 80,112,\"128\",80,0,0,4,4,\""+num+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,210,\"0\",0,2,2,\""+addSpace2Barcode2(num)+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,256,576,1");
                //字体较大的地址
                HPRTPrinterHelper.PrintData("TEXT "+(289-24*bigChar.length())+",272,\"3\",0,2,2,\""+ bigChar +"\"");
                HPRTPrinterHelper.PrintData("BAR 0,336,576,1");
                //字体较小的地址
                HPRTPrinterHelper.PrintData("TEXT "+(140-12*bigChar.length())+",352,\"1\",0,2,2,\""+ bigChar +"\"");
                HPRTPrinterHelper.PrintData("BAR 288,336,1,64");
                //时间
                String time = order.getTime();
                HPRTPrinterHelper.PrintData("TEXT 360,352,\"1\",0,2,2,\""+time.substring(0, time.indexOf(" "))+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,400,576,1");
                HPRTPrinterHelper.PrintData("BAR 60,400,1,400");
                HPRTPrinterHelper.PrintData("TEXT 16,416,\"0\",0,1,1,\"收\"");
                HPRTPrinterHelper.PrintData("TEXT 16,440,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 16,464,\"0\",0,1,1,\"信\"");
                HPRTPrinterHelper.PrintData("TEXT 16,488,\"0\",0,1,1,\"息\"");
                HPRTPrinterHelper.PrintData("TEXT 80,425,\"0\",0,1,1,\"收件人：\"");
                //收件人
                HPRTPrinterHelper.PrintData("TEXT 168,425,\"0\",0,1,1,\""+order.getName()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 300,425,\"0\",0,1,1,\"手机/电话：\"");
                //电话
                HPRTPrinterHelper.PrintData("TEXT 424,425,\"0\",0,1,1,\""+order.getPhone()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,456,\"0\",0,1,1,\"地址：\"");
                //地址
                HPRTPrinterHelper.PrintData("BLOCK 136,456,420,80,\"0\",0,1,1,2,\""+order.getAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,520,576,1");
                HPRTPrinterHelper.PrintData("TEXT 16,528,\"0\",0,1,1,\"寄\"");
                HPRTPrinterHelper.PrintData("TEXT 16,552,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 16,576,\"0\",0,1,1,\"信\"");
                HPRTPrinterHelper.PrintData("TEXT 16,600,\"0\",0,1,1,\"息\"");
                HPRTPrinterHelper.PrintData("TEXT 80,536,\"2\",0,1,1,\"寄件人：\"");
                //寄件人
                HPRTPrinterHelper.PrintData("TEXT 165,536,\"2\",0,1,1,\""+order.getSenderName()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 300,536,\"2\",0,1,1,\"手机/电话：\"");
                //电话
                HPRTPrinterHelper.PrintData("TEXT 424,536,\"2\",0,1,1,\""+order.getSenderPhone()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,564,\"2\",0,1,1,\"地址：\"");
                //地址
                HPRTPrinterHelper.PrintData("BLOCK 136,564,720,80,\"2\",0,1,1,2,\""+order.getSenderAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,624,576,1");
                HPRTPrinterHelper.PrintData("TEXT 16,680,\"0\",0,1,1,\"服\"");
                HPRTPrinterHelper.PrintData("TEXT 16,710,\"0\",0,1,1,\"务\"");
                HPRTPrinterHelper.PrintData("TEXT 80,632,\"1\",0,1,1,\"内容品名：\""+order.getArticleInfo()+"\"");
                String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
                HPRTPrinterHelper.PrintData("TEXT 80,660,\"1\",0,1,1,\"计费重量："+weight+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,688,\"1\",0,1,1,\"声明价值：￥"+""+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,716,\"1\",0,1,1,\"代收货款：￥"+charge+"\"");
                HPRTPrinterHelper.PrintData("TEXT 80,744,\"1\",0,1,1,\"到付金额：￥"+""+"\"");

                HPRTPrinterHelper.PrintData("BAR 288,624,1,176");
                HPRTPrinterHelper.PrintData("TEXT 300,640,\"0\",0,1,1,\"签收人/签收时间\"");
                HPRTPrinterHelper.PrintData("TEXT 480,760,\"0\",0,1,1,\"月    日\"");
//                HPRTPrinterHelper.PrintData("BAR 0,800,576,1");

                HPRTPrinterHelper.PrintData("BAR 0,936,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,952,\"1\",0,1,1,\"运单号：\"");
                HPRTPrinterHelper.PrintData("TEXT 90,952,\"1\",0,1,1,\""+num+"\"");

                HPRTPrinterHelper.PrintData("BAR 288,936,1,168");
                HPRTPrinterHelper.PrintData("TEXT 310,952,\"1\",0,1,1,\"订单号：\"");
                HPRTPrinterHelper.PrintData("TEXT 370,952,\"1\",0,1,1,\""+order.getId()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,984,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,992,\"1\",0,1,1,\"收件方信息：\"");
                HPRTPrinterHelper.PrintData("BLOCK 24,1013,240,80,\"1\",0,1,1,2,\""+order.getName()+"  "+order.getPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 24,1034,240,80,\"1\",0,1,1,2,\""+order.getAddress()+"\"");

                HPRTPrinterHelper.PrintData("TEXT 310,992,\"1\",0,1,1,\"寄件方信息：\"");
                HPRTPrinterHelper.PrintData("BLOCK 310,1012,240,80,\"1\",0,1,1,2,\""+order.getSenderName()+"  "+order.getSenderPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 310,1032,240,80,\"1\",0,1,1,2,\""+order.getSenderAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,1104,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,1120,\"1\",0,1,1,\"内容品名\"");
                HPRTPrinterHelper.PrintData("TEXT 120,1120,\"1\",0,1,1,\"计费重量（kg）\"");
                HPRTPrinterHelper.PrintData("TEXT 232,1120,\"1\",0,1,1,\"声明价值（￥）\"");
                HPRTPrinterHelper.PrintData("TEXT 344,1120,\"1\",0,1,1,\"代收货款（￥）\"");
                HPRTPrinterHelper.PrintData("TEXT 452,1120,\"1\",0,1,1,\"到付金额（￥）\"");
                HPRTPrinterHelper.PrintData("BAR 0,1152,576,1");

                HPRTPrinterHelper.PrintData("BAR 112,1104,1,96");
                HPRTPrinterHelper.PrintData("BAR 224,1104,1,96");
                HPRTPrinterHelper.PrintData("BAR 336,1104,1,96");
                HPRTPrinterHelper.PrintData("BAR 448,1104,1,96");

                HPRTPrinterHelper.PrintData("TEXT 24,1168,\"1\",0,1,1,\""+order.getArticleInfo()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 128,1168,\"1\",0,1,1,\""+order.getCharging_weight()+"\"");
                //声明价值
                HPRTPrinterHelper.PrintData("TEXT 240,1168,\"1\",0,1,1,\" \"");
                //代付金额
                HPRTPrinterHelper.PrintData("TEXT 352,1168,\"1\",0,1,1,\""+charge+"\"");
                //到付金额
                HPRTPrinterHelper.PrintData("TEXT 460,1168,\"1\",0,1,1,\""+""+"\"");

//                HPRTPrinterHelper.PrintData("BAR 0,1200,576,1");
                //条码
                HPRTPrinterHelper.PrintData("BARCODE 32,1215,\"128\",56,0,0,2,2,\""+num+"\"");
                HPRTPrinterHelper.PrintData("TEXT 40,1280,\"1\",0,1,1,\""+addSpace2Barcode2(num)+"\"");
                HPRTPrinterHelper.PrintData("BAR 296,1200,1,224");

                HPRTPrinterHelper.PrintData("BAR 0,1304,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,1315,\"1\",0,1,1,\"收件方信息：\"");
                HPRTPrinterHelper.PrintData("BLOCK 24,1336,240,80,\"1\",0,1,1,2,\""+order.getName()+"  "+order.getPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 24,1357,240,80,\"1\",0,1,1,2,\""+order.getAddress()+"\"");

                HPRTPrinterHelper.PrintData("TEXT 310,1315,\"1\",0,1,1,\"寄件方信息：\"");
                HPRTPrinterHelper.PrintData("BLOCK 310,1336,240,80,\"1\",0,1,1,8,\""+order.getSenderName()+"  "+order.getSenderPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 310,1357,240,80,\"1\",0,1,1,8,\""+order.getSenderAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,1424,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,1440,\"1\",0,1,1,\"内容品名\"");
                HPRTPrinterHelper.PrintData("TEXT 120,1440,\"1\",0,1,1,\"计费重量(kg)\"");
                HPRTPrinterHelper.PrintData("TEXT 232,1440,\"1\",0,1,1,\"声明价值(￥)\"");
                HPRTPrinterHelper.PrintData("TEXT 344,1440,\"1\",0,1,1,\"代收货款(￥)\"");
                HPRTPrinterHelper.PrintData("TEXT 452,1440,\"1\",0,1,1,\"到付金额(￥)\"");
                HPRTPrinterHelper.PrintData("BAR 0,1472,576,1");

                HPRTPrinterHelper.PrintData("BAR 112,1424,1,96");
                HPRTPrinterHelper.PrintData("BAR 224,1424,1,96");
                HPRTPrinterHelper.PrintData("BAR 336,1424,1,96");
                HPRTPrinterHelper.PrintData("BAR 448,1424,1,96");

                HPRTPrinterHelper.PrintData("TEXT 24,1488,\"1\",0,1,1,\""+order.getArticleInfo()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 128,1488,\"1\",0,1,1,\""+order.getCharging_weight()+"\"");
                HPRTPrinterHelper.PrintData("TEXT 240,1488,\"1\",0,1,1,\""+""+"\"");
                HPRTPrinterHelper.PrintData("TEXT 352,1488,\"1\",0,1,1,\""+charge+"\"");
                HPRTPrinterHelper.PrintData("TEXT 460,1488,\"1\",0,1,1,\""+""+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,1520,576,1");
                HPRTPrinterHelper.PrintData("TEXT 24,1536,\"1\",0,1,1,\"打印时间\"");
                HPRTPrinterHelper.PrintData("TEXT 24,1560,\"1\",0,1,1,\""+third_text20+"\"");

                HPRTPrinterHelper.PrintData("BAR 288,1520,1,80");
                HPRTPrinterHelper.PrintData("TEXT 312,1536,\"1\",0,1,1,\"签收人/签收时间\"");
                HPRTPrinterHelper.PrintData("TEXT 496,1560,\"1\",0,1,1,\"月    日\"");
                HPRTPrinterHelper.PrintData("BAR 0,1600,576,1");
                HPRTPrinterHelper.PrintData("PRINT 1,1");
            }
            catch(Exception e)
            {
                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
            }
        }else{
            UtilToolkit.showToast("打印失败");
        }
    }

    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        if(!Utility.isEmpty(hprtPrinter)) {
            HPRTPrinterHelper.PrintData("BACKFEED 32");
            if(!hprtPrinter.IsOpened()){
                hprtPrinter.PortOpen("Bluetooth,"+device.getAddress());
            }
            try {
                HPRTPrinterHelper.PrintData("SIZE 76 mm,179 mm");
                HPRTPrinterHelper.PrintData("DENSITY 6");
                HPRTPrinterHelper.PrintData("CLS");
                HPRTPrinterHelper.PrintData("BOX 0,0,576,664,2");
                HPRTPrinterHelper.PrintData("BAR 0,80,576,2");
                Bitmap res;
                String expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
                if("ht".equals(expressNo)){
                    res = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_ht_print1);
                }else if("qf".equals(expressNo)){
                    res = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_qf_print1);
                }else if("yd".equals(expressNo)){
                    res = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_yd_print1);
                }else if("yt".equals(expressNo)){
                    res = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_yt_print1);
                }else{
                    res = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_sto_print1);
                }
                HPRTPrinterHelper.printImage("16", "16", res, true);
                //条码
                HPRTPrinterHelper.PrintData("BARCODE 114,96,\"128\",80,0,0,3,4,\""+num+"\"");
                HPRTPrinterHelper.PrintData("TEXT 114,180,\"0\",0,1,1,\""+Utility.formatOrderNo(num)+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,208,576,2");
                HPRTPrinterHelper.PrintData("TEXT 80,225,\"0\",0,2,2,\""+bigChar+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,288,576,2");
                HPRTPrinterHelper.PrintData("BAR 48,288,2,288");
                HPRTPrinterHelper.PrintData("TEXT 16,333,\"0\",0,1,1,\"收 \"");
                HPRTPrinterHelper.PrintData("TEXT 16,381,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 56,305,\"1\",0,2,2,\""+order.getName()+"  "+order.getPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 56,338,480,70,\"1\",0,2,2,2,\""+order.getAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,432,528,2");
                HPRTPrinterHelper.PrintData("TEXT 16,480,\"0\",0,1,1,\"发 \"");
                HPRTPrinterHelper.PrintData("TEXT 16,528,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 56,450,\"0\",0,1,1,\""+order.getSenderName()+"  "+order.getSenderPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 56,482,412,70,\"0\",0,1,1,2,\""+order.getSenderAddress()+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,576,528,2");
                HPRTPrinterHelper.PrintData("TEXT 10,592,\"0\",0,1,1,\"签收人/签收时间\"");
                HPRTPrinterHelper.PrintData("TEXT 10,622,\"1\",0,1,1,\"您的签字代表您已验收此包裹，并已确认商品信息无误\"");
                HPRTPrinterHelper.PrintData("TEXT 10,640,\"1\",0,1,1,\"包装完好，没有划痕，破损等表面质量问题\"");

                HPRTPrinterHelper.PrintData("TEXT 416,624,\"0\",0,1,1,\"月  日\"");
                HPRTPrinterHelper.PrintData("BAR 528,288,2,376");
                HPRTPrinterHelper.PrintData("TEXT 540,400,\"0\",0,1,1,\"派\"");
                HPRTPrinterHelper.PrintData("TEXT 540,470,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 540,540,\"0\",0,1,1,\"联\"");

                HPRTPrinterHelper.PrintData("BOX 0,696,576,968,2");
                Bitmap bitmap;
                if("ht".equals(expressNo)){
                    bitmap = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_ht_print2);
                }else if("qf".equals(expressNo)){
                    bitmap = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_qf_print2);
                }else if("yd".equals(expressNo)){
                    bitmap = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_yd_print2);
                }else if("yt".equals(expressNo)){
                    bitmap = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_yt_print2);
                }else{
                    bitmap = BitmapFactory.decodeResource(SKuaidiApplication.getContext().getResources(), R.drawable.logo_sto_print2);
                }
                HPRTPrinterHelper.printImage("16", "712", bitmap, true);
                HPRTPrinterHelper.PrintData("BARCODE 356,712,\"128\",36,0,0,2,2,\""+num+"\"");
                HPRTPrinterHelper.PrintData("TEXT 350, 754, \"1\", 0, 1, 1,\""+Utility.formatOrderNo(num)+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,776,576,2");
                HPRTPrinterHelper.PrintData("TEXT 16,808,\"0\",0,1,1,\"收\"");
                HPRTPrinterHelper.PrintData("TEXT 16,856,\"0\",0,1,1,\"件\"");

                HPRTPrinterHelper.PrintData("BAR 48,776,2,136");
                HPRTPrinterHelper.PrintData("TEXT 56,792,\"0\",0,1,1,\""+order.getName()+"  "+order.getPhone()+"\"");

                HPRTPrinterHelper.PrintData("BLOCK 56,834,412,70,\"0\",0,1,1,2,\""+order.getAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 0,912,528,2");
                String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
                HPRTPrinterHelper.PrintData("TEXT 16,928,\"0\",0,1,1,\"物品："+order.getArticleInfo()+" "+weight+"\"");
                HPRTPrinterHelper.PrintData("BOX 406,923,510,957,2");
                HPRTPrinterHelper.PrintData("TEXT 423,930,\"0\",0,1,1,\"已验视\"");
                HPRTPrinterHelper.PrintData("BAR 528,776,2,192");
                HPRTPrinterHelper.PrintData("TEXT 540,816,\"0\",0,1,1,\"客\"");
                HPRTPrinterHelper.PrintData("TEXT 540,864,\"0\",0,1,1,\"户\"");
                HPRTPrinterHelper.PrintData("TEXT 540,912,\"0\",0,1,1,\"联\"");

                HPRTPrinterHelper.PrintData("BOX 0,1000,576,1408,2");
                HPRTPrinterHelper.printImage("16", "1016", bitmap, true);
                HPRTPrinterHelper.PrintData("BARCODE 356,1016,\"128\",36,0,0,2,2,\""+num+"\"");
                HPRTPrinterHelper.PrintData("TEXT 350, 1058, \"1\", 0, 1, 1,\""+Utility.formatOrderNo(num)+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,1080,576,2");
                HPRTPrinterHelper.PrintData("TEXT 16,1112,\"0\",0,1,1,\"收\"");
                HPRTPrinterHelper.PrintData("TEXT 16,1160,\"0\",0,1,1,\"件\"");

                HPRTPrinterHelper.PrintData("BAR 48,1080,2,272");
                HPRTPrinterHelper.PrintData("TEXT 56,1096,\"0\",0,1,1,\""+order.getName()+"  "+order.getPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 56,1138,412,70,\"0\",0,1,1,2,\""+order.getAddress()+"\"");
                HPRTPrinterHelper.PrintData("BAR 0,1216,528,2");
                HPRTPrinterHelper.PrintData("TEXT 16,1248,\"0\",0,1,1,\"发\"");
                HPRTPrinterHelper.PrintData("TEXT 16,1296,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 56,1232,\"0\",0,1,1,\""+order.getSenderName()+"  "+order.getSenderPhone()+"\"");
                HPRTPrinterHelper.PrintData("BLOCK 56,1274,412,70,\"0\",0,1,1,2,\""+order.getSenderAddress()+"\"");

                HPRTPrinterHelper.PrintData("BAR 528,1080,2,328");
                HPRTPrinterHelper.PrintData("TEXT 540,1146,\"0\",0,1,1,\"寄\"");
                HPRTPrinterHelper.PrintData("TEXT 540,1224,\"0\",0,1,1,\"件\"");
                HPRTPrinterHelper.PrintData("TEXT 540,1302,\"0\",0,1,1,\"联\"");
                HPRTPrinterHelper.PrintData("BAR 0,1352,528,2");
                HPRTPrinterHelper.PrintData("TEXT 16,1368,\"0\",0,1,1,\"物品："+order.getArticleInfo()+" "+weight+"\"");
                HPRTPrinterHelper.PrintData("BOX 406,1362,510,1400,2");
                HPRTPrinterHelper.PrintData("TEXT 423,1370,\"0\",0,1,1,\"已验视\"");
                HPRTPrinterHelper.PrintData("PRINT 1,1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            UtilToolkit.showToast("打印失败");
        }
    }

    @Override
    public String getPrinterStatus() {
        KLog.i("tag", HPRTPrinterHelper.Status());
        return "OK";
    }

}
