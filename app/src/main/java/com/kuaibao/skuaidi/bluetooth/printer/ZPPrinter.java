package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import zpSDK.zpSDK.zpBluetoothPrinter;

/**
 * Created by kuaibao on 2016/7/18.
 *
 * 芝柯打印机
 */

public class ZPPrinter extends PrinterBase {

    private static zpBluetoothPrinter zpPrinter;
    private BluetoothDevice printer;
    private static ZPPrinter zPrinter;

    private ZPPrinter(zpBluetoothPrinter zpPrinter, BluetoothDevice printer){
        this.zpPrinter = zpPrinter;
        this.printer = printer;
    }

    public static ZPPrinter getInstance(BluetoothDevice printer){
        if(Utility.isEmpty(zPrinter)){
            zpPrinter = new zpBluetoothPrinter(SKuaidiApplication.getContext());
            zPrinter = new ZPPrinter(zpPrinter, printer);
        }
        return zPrinter;
    }

    @Override
    public void connect(BluetoothDevice device, final ConnectedCallBack callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(zpPrinter != null && !zpPrinter.isConnected()){
                    zpPrinter.connect(printer.getAddress());
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
        if(!Utility.isEmpty(zpPrinter)) {
            zpPrinter.connect(device.getAddress());
        }
    }

    @Override
    public void disConnect() {
        if(!Utility.isEmpty(zpPrinter)) {
            zpPrinter.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        if(!Utility.isEmpty(zpPrinter)){
            return zpPrinter.isConnected();
        }
        return false;
    }

    @Override
    public String getPrinterStatus() {
        if(!Utility.isEmpty(zpPrinter)){
            KLog.i("tag", "状态:"+zpPrinter.printerStatus());
            return "OK";
        }
        return "打印错误";
    }

    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        String charge = order.getCollection_amount();
        zpPrinter.pageSetup(page_width, page_height);
        printFirstPager(num, bigChar, charge, order);
        printSecondPager(num, bigChar, charge, order);
        printThirdPager(num, bigChar, charge, order);
        zpPrinter.print(0, 1);
    }

    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        zpPrinter.pageSetup(568, 568);
        zpPrinter.drawBarCode(8, 540, "12345678901234567", 128, true, 3, 60);
        zpPrinter.drawQrCode(350, 48, "111111111", 0, 3, 0);
        zpPrinter.drawText(90, 48+100, "400-8800-", 2, 0, 0, false, false);
        zpPrinter.drawText(100, 48+100+56, "株洲      张贺", 4, 0, 0, false, false);
        zpPrinter.drawText(250, 48+100+56+56, "经由  株洲", 2, 0, 0, false, false);

        zpPrinter.drawText(100, 48+100+56+56+80, "2015110101079-01-01   广州", 2, 0, 0, false, false);
        zpPrinter.drawText(100, 48+100+56+56+80+80, "2015-11-01  23:00    卡班", 2, 0, 0, false, false);

        zpPrinter.drawBarCode(124,48+100+56+56+80+80+80 , "12345678901234567", 128, false, 2, 60);
        zpPrinter.print(0, 1);
    }

    protected void printFirstPager(String num, String bigChar, String charge, Order order) {
        zpPrinter.drawLine(1, first_horizontal_line1_x1, first_horizontal_line1_y1, first_horizontal_line1_x2, first_horizontal_line1_y2, true);
        zpPrinter.drawLine(1, first_horizontal_line2_x1, first_horizontal_line2_y1, first_horizontal_line2_x2, first_horizontal_line2_y2, true);
        zpPrinter.drawLine(1, first_horizontal_line3_x1, first_horizontal_line3_y1, first_horizontal_line3_x2, first_horizontal_line3_y2, true);
        zpPrinter.drawLine(1, first_horizontal_line4_x1, first_horizontal_line4_y1, first_horizontal_line4_x2, first_horizontal_line4_y2, true);
        zpPrinter.drawLine(1, first_horizontal_line5_x1, first_horizontal_line5_y1, first_horizontal_line5_x2, first_horizontal_line5_y2, true);
        zpPrinter.drawLine(1, first_horizontal_line6_x1, first_horizontal_line6_y1, first_horizontal_line6_x2, first_horizontal_line6_y2, true);
        zpPrinter.drawLine(1, first_vertical_line1_x1, first_vertical_line1_y1, first_vertical_line1_x2, first_vertical_line1_y2, true);
        zpPrinter.drawLine(1, first_vertical_line2_x1, first_vertical_line2_y1, first_vertical_line2_x2, first_vertical_line2_y2, true);
        zpPrinter.drawLine(1, first_vertical_line3_x1, first_vertical_line3_y1, first_vertical_line3_x2, first_vertical_line3_y2, true);

//        zpSDK.DrawText(first_text1_x, first_text1_y, first_text1, "55", 24, false, false);
        zpPrinter.drawText(first_text1_x, first_text1_y, first_text1+charge, 5, 0, 0, true, false);
//        zpPrinter.Drawinverse_Line(first_text1_x, first_text1_y, 32, first_text1);

        String barcode = addSpace2Barcode2(num);
//        zpSDK.DrawText(first_text2_x-120, first_text2_y+8, barcode, "24", 0, false, false);
        zpPrinter.drawText(first_text2_x - 56, first_text2_y - 8, barcode, 7, 0, 0, false, false);

        int x = calculateX(page_left, page_right, bigChar, 56);
//        zpSDK.DrawText(x, first_text3_y-10, first_text3,font24, 0, false, false);
        zpPrinter.drawText(x, first_text3_y - 10, bigChar, 8, 0, 0, false, false);

        x = calculateX(page_left, first_vertical_line2_x1, bigChar, first_text4_size);
        zpPrinter.drawText(x, first_text4_y, bigChar, 5, 0, 0, false, false);

        String time = order.getTime();
        x = calculateX(first_vertical_line2_x1, page_right, time.substring(0,time.indexOf(" ")), first_text5_size / 2);
        zpPrinter.drawText(x, first_text5_y, time.substring(0,time.indexOf(" ")), 5, 0, 0, false, false);

        zpPrinter.drawText(first_text6_x, first_text6_y, first_text6, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text7_x, first_text7_y, first_text7, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text8_x, first_text8_y, first_text8, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text9_x, first_text9_y, first_text9, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text10_x, first_text10_y, first_text10+order.getName(), 3, 0, 0, false, false);
        zpPrinter.drawText(first_text11_x, first_text11_y, first_text11+order.getPhone(), 3, 0, 0, false, false);

        printChangeLineText(first_text12+order.getAddress(), first_text12_x, first_text12_y, first_text12_size, 19, 3);
//        zpSDK.DrawText(first_text12_x, first_text12_y, first_text12, "sans",first_text12_size, false, false);
        zpPrinter.drawText(first_text13_x, first_text13_y, first_text13, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text14_x, first_text14_y, first_text14, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text15_x, first_text15_y, first_text15, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text16_x, first_text16_y, first_text16, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text17_x, first_text17_y, first_text17+order.getSenderName(), 2, 0, 0, false, false);
        zpPrinter.drawText(first_text18_x, first_text18_y, first_text18+order.getSenderPhone(), 2, 0, 0, false, false);

        printChangeLineText(first_text19+order.getSenderAddress(), first_text19_x, first_text19_y, 20, 23, 2);
//        zpSDK.DrawText(first_text19_x, first_text19_y, first_text19, "sans",first_text19_size, false, false);
        zpPrinter.drawText(first_text20_x, first_text20_y, first_text20, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text21_x, first_text21_y, first_text21, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text22_x, first_text22_y, first_text22+order.getArticleInfo(), 1, 0, 0, false, false);
        String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
        zpPrinter.drawText(first_text23_x, first_text23_y, first_text23+weight, 1, 0, 0, false, false);
        zpPrinter.drawText(first_text24_x, first_text24_y, first_text24, 1, 0, 0, false, false);
        zpPrinter.drawText(first_text25_x, first_text25_y, first_text25+charge, 1, 0, 0, false, false);
        zpPrinter.drawText(first_text26_x, first_text26_y, first_text26, 1, 0, 0, false, false);
        zpPrinter.drawText(first_text27_x, first_text27_y, first_text27, 3, 0, 0, false, false);
        zpPrinter.drawText(first_text28_x, first_text28_y, first_text28, 3, 0, 0, false, false);

        zpPrinter.drawBarCode(first_barcode1_x , first_barcode1_y, num, 128, false, 4, 80);
    }

    protected void printSecondPager(String num, String bigChar, String charge, Order order) {
        zpPrinter.drawLine(1, second_horizontal_line1_x1, second_horizontal_line1_y1, second_horizontal_line1_x2, second_horizontal_line1_y2, true);
        zpPrinter.drawLine(1, second_horizontal_line2_x1, second_horizontal_line2_y1, second_horizontal_line2_x2, second_horizontal_line2_y2, true);
        zpPrinter.drawLine(1, second_horizontal_line3_x1, second_horizontal_line3_y1, second_horizontal_line3_x2, second_horizontal_line3_y2, true);
        zpPrinter.drawLine(1, second_horizontal_line4_x1, second_horizontal_line4_y1, second_horizontal_line4_x2, second_horizontal_line4_y2, true);
        zpPrinter.drawLine(1, second_vertical_line1_x1, second_vertical_line1_y1, second_vertical_line1_x2, second_vertical_line1_y2, true);
        zpPrinter.drawLine(1, second_vertical_line2_x1, second_vertical_line2_y1, second_vertical_line2_x2, second_vertical_line2_y2, true);
        zpPrinter.drawLine(1, second_vertical_line3_x1, second_vertical_line3_y1, second_vertical_line3_x2, second_vertical_line3_y2, true);
        zpPrinter.drawLine(1, second_vertical_line4_x1, second_vertical_line4_y1, second_vertical_line4_x2, second_vertical_line4_y2, true);
        zpPrinter.drawLine(1, second_vertical_line5_x1, second_vertical_line5_y1, second_vertical_line5_x2, second_vertical_line5_y2, true);

        zpPrinter.drawText(second_text1_x, second_text1_y, second_text1+num,1, 0, 0, false, false);
        zpPrinter.drawText(second_text2_x, second_text2_y, second_text2+order.getId(), 1, 0, 0, false, false);
        zpPrinter.drawText(second_text3_x, second_text3_y, second_text3, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text4_x, second_text4_y, order.getName()+"  "+order.getPhone(), 1, 0, 0, false, false);
        printChangeLineText(order.getAddress(), second_text5_x, second_text5_y, second_text5_size, 14, 1);
//        zpSDK.DrawText(second_text5_x, second_text5_y, second_text5, "sans",second_text5_size, false, false);
        zpPrinter.drawText(second_text6_x, second_text6_y, second_text6, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text7_x, second_text7_y, order.getSenderName()+"  "+order.getSenderPhone(), 1, 0, 0, false, false);
        printChangeLineText(order.getSenderAddress(), second_text8_x, second_text8_y, second_text8_size, 14, 1);
//        zpSDK.DrawText(second_text8_x, second_text8_y, second_text8, "sans",second_text8_size, false, false);
        zpPrinter.drawText(second_text9_x, second_text9_y, second_text9, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text10_x, second_text10_y, second_text10, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text11_x, second_text11_y, second_text11, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text12_x, second_text12_y, second_text12, 1, 0, 0, false, false);
        zpPrinter.drawText(second_text13_x, second_text13_y, second_text13, 1, 0, 0, false, false);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {//内容品名，暂时没有
            x = calculateX(page_left, second_vertical_line2_x1, order.getArticleInfo(), second_text14_size);
            zpPrinter.drawText(x, second_text14_y, order.getArticleInfo(), 1, 0, 0, false, false);
        }

        x = calculateX(second_vertical_line2_x1, second_vertical_line3_x1, order.getCharging_weight(), second_text15_size / 2);
        zpPrinter.drawText(x, second_text15_y, order.getCharging_weight(), 1, 0, 0, false, false);

        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line3_x1, second_vertical_line4_x1, "", second_text16_size / 2);
            zpPrinter.drawText(x, second_text16_y, "", 1, 0, 0, false, false);
        }


        if (charge != null && charge.length() > 0) {//代收货款有可能没有
            x = calculateX(second_vertical_line4_x1, second_vertical_line5_x1, charge, second_text17_size / 2);
            zpPrinter.drawText(x, second_text17_y, charge, 1, 0, 0, false, false);
        }
        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line5_x1, page_right, "", second_text18_size / 2);
            zpPrinter.drawText(x, second_text18_y, "", 1, 0, 0, false, false);
        }

    }

    protected void printThirdPager(String num, String bigChar, String charge, Order order) {
        zpPrinter.drawLine(1, third_horizontal_line1_x1, third_horizontal_line1_y1, third_horizontal_line1_x2, third_horizontal_line1_y2, true);
        zpPrinter.drawLine(1, third_horizontal_line2_x1, third_horizontal_line2_y1, third_horizontal_line2_x2, third_horizontal_line2_y2, true);
        zpPrinter.drawLine(1, third_horizontal_line3_x1, third_horizontal_line3_y1, third_horizontal_line3_x2, third_horizontal_line3_y2, true);
        zpPrinter.drawLine(1, third_horizontal_line4_x1, third_horizontal_line4_y1, third_horizontal_line4_x2, third_horizontal_line4_y2, true);
        zpPrinter.drawLine(1, third_vertical_line1_x1, third_vertical_line1_y1, third_vertical_line1_x2, third_vertical_line1_y2, true);
        zpPrinter.drawLine(1, third_vertical_line2_x1, third_vertical_line2_y1, third_vertical_line2_x2, third_vertical_line2_y2, true);
        zpPrinter.drawLine(1, third_vertical_line3_x1, third_vertical_line3_y1, third_vertical_line3_x2, third_vertical_line3_y2, true);
        zpPrinter.drawLine(1, third_vertical_line4_x1, third_vertical_line4_y1, third_vertical_line4_x2, third_vertical_line4_y2, true);
        zpPrinter.drawLine(1, third_vertical_line5_x1, third_vertical_line5_y1, third_vertical_line5_x2, third_vertical_line5_y2, true);
        zpPrinter.drawLine(1, third_vertical_line6_x1, third_vertical_line6_y1, third_vertical_line6_x2, third_vertical_line6_y2, true);

        String barcode = addSpace2Barcode2(num);
        zpPrinter.drawText(third_text1_x, third_text1_y, barcode, 2, 0, 0, false, false);
        zpPrinter.drawText(third_text2_x, third_text2_y, third_text2, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text3_x, third_text3_y, order.getName()+"  "+order.getPhone(), 1, 0, 0, false, false);
        printChangeLineText(order.getAddress(), third_text4_x, third_text4_y, 16, 14, 1);
//        zpSDK.DrawText(third_text4_x, third_text4_y, third_text4, "sans",third_text4_size, false, false);
        zpPrinter.drawText(third_text5_x, third_text5_y, third_text5, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text6_x, third_text6_y, order.getSenderName()+"  "+order.getSenderPhone(), 1, 0, 0, false, false);
//        zpSDK.DrawText(third_text7_x, third_text7_y, third_text7, "sans",third_text7_size, false, false);
        printChangeLineText(order.getSenderAddress(), third_text7_x, third_text7_y, 16, 14, 1);
        zpPrinter.drawText(third_text9_x, third_text9_y, third_text9, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text10_x, third_text10_y, third_text10, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text11_x, third_text11_y, third_text11, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text12_x, third_text12_y, third_text12, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text13_x, third_text13_y, third_text13, 1, 0, 0, false, false);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {
            x = calculateX(page_left, third_vertical_line2_x1, order.getArticleInfo(), third_text14_size);
            zpPrinter.drawText(x, third_text14_y, order.getArticleInfo(), 1, 0, 0, false, false);
        }
        x = calculateX(third_vertical_line2_x1, third_vertical_line3_x1, order.getCharging_weight(), third_text15_size / 2);
        zpPrinter.drawText(x, third_text15_y, order.getCharging_weight(), 1, 0, 0, false, false);

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line3_x1, third_vertical_line4_x1, "", third_text16_size / 2);
            zpPrinter.drawText(x, third_text16_y, "", 1, 0, 0, false, false);
        }

        if (charge != null) {
            x = calculateX(third_vertical_line4_x1, third_vertical_line5_x1, charge, third_text17_size / 2);
            zpPrinter.drawText(x, third_text17_y, charge, 1, 0, 0, false, false);
        }

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line5_x1, page_right, "", third_text18_size / 2);
            zpPrinter.drawText(x, third_text18_y, "", 1, 0, 0, false, false);
        }

        zpPrinter.drawText(third_text19_x, third_text19_y, third_text19, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text20_x, third_text20_y, third_text20, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text21_x, third_text21_y, third_text21, 1, 0, 0, false, false);
        zpPrinter.drawText(third_text22_x, third_text22_y, third_text22, 1, 0, 0, false, false);

        zpPrinter.drawBarCode(third_barcode1_x, third_barcode1_y, num, 128, false, 2, 60);
    }


    //方法用于打印文字太多换行打印
    private void printChangeLineText(String text, int x, int y, int height, int num, int font) {
        if (text != null && num > 0 && text.length() > num) {
            StringBuilder sb = new StringBuilder(text);
            String subs = sb.substring(0, num);
            //打印

            zpPrinter.drawText(x, y, subs,font, 0, 0, false, false);
            sb.delete(0, num);
            String remainming = sb.toString();
            if (remainming.length() > num) {
                //x,y设置
                y += height;
                printChangeLineText(remainming, x, y, height, num, font);
            } else {
                //打印
                y += height;
                zpPrinter.drawText(x, y, remainming, font, 0, 0, false, false);
            }
        } else {
            zpPrinter.drawText(x, y, text, font, 0, 0, false, false);
        }
    }

}
