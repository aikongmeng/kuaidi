package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import printpp.printpp_zt.PrintPP_CPCL;

/**
 * Created by kuaibao on 2016/7/17.
 *
 * 启锐打印机
 */

public class QRPrinter extends PrinterBase {
    //申通边框
    private static final int TOP_BOX_START_X = 0;
    private static final int TOP1_BOX_START_Y = 0;
    private static final int TOP_BOX_END_X = 598;
    private static final int TOP1_BOX_END_Y = 664;
    private static final int TOP2_BOX_START_Y = 696;
    private static final int TOP2_BOX_END_Y = 968;
    private static final int TOP3_BOX_START_Y = 998;
    private static final int TOP3_BOX_END_Y = 1400;

    private static PrintPP_CPCL iPrinter;
    private BluetoothDevice printer;
    private static QRPrinter qrPrinter;

    private QRPrinter(PrintPP_CPCL iPrinter, BluetoothDevice printer){
        this.iPrinter = iPrinter;
        this.printer = printer;
    }

    public static QRPrinter getInstance(BluetoothDevice printer){
        if(Utility.isEmpty(qrPrinter)){
            iPrinter = new PrintPP_CPCL();
            qrPrinter = new QRPrinter(iPrinter, printer);
        }
        return qrPrinter;
    }

    @Override
    public void connect(BluetoothDevice device, final ConnectedCallBack callBack) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(printer != null && !iPrinter.isConnected()){
                    iPrinter.connect(printer.getAddress());
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                callBack.connectedCallback();
            }
        }.execute();
    }

    @Override
    public void connect(BluetoothDevice device) {
        if(!Utility.isEmpty(device))
            iPrinter.connect(device.getAddress());
    }

    @Override
    public void disConnect() {
        if(!Utility.isEmpty(iPrinter)){
            iPrinter.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        if(!Utility.isEmpty(iPrinter)){
            return iPrinter.isConnected();
        }
        return false;
    }

    @Override
    public String getPrinterStatus() {
        if(!Utility.isEmpty(iPrinter)){
            return iPrinter.printerStatus();
        }
        return "";
    }

    /**
     * 中通打印样板
     */
    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        String charge = order.getCollection_amount();
        iPrinter.pageSetup(page_width, page_height);
        printFirstPager(num, bigChar, charge, order);
        printSecondPager(num, bigChar, charge, order);
        printThirdPager(num, bigChar, charge, order);
        iPrinter.print(0, 1);
    }

    /**
     * 申通打印样板
     */
    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        iPrinter.pageSetup(600, 1420);
        iPrinter.drawBox(2, TOP_BOX_START_X, TOP1_BOX_START_Y, TOP_BOX_END_X, TOP1_BOX_END_Y); //第一联边框
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP1_BOX_START_Y+88, TOP_BOX_END_X, TOP1_BOX_START_Y+88,true);//第一联横线1
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP1_BOX_START_Y+88+128, TOP_BOX_END_X, TOP1_BOX_START_Y+88+128,true);//第一联横线2
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP1_BOX_START_Y+88+128+80, TOP_BOX_END_X, TOP1_BOX_START_Y+88+128+80,true);//第一联横线3
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP1_BOX_START_Y+88+128+80+144, TOP_BOX_END_X-56-16, TOP1_BOX_START_Y+88+128+80+144,true);
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP1_BOX_START_Y+88+128+80+144+128, TOP_BOX_END_X-56-16, TOP1_BOX_START_Y+88+128+80+144+128,true);
        iPrinter.drawLine(2, 52, TOP1_BOX_START_Y+88+128+80, 52, TOP1_BOX_START_Y+88+128+80+144+128,true);//第一联竖线1，从左到右
//        iPrinter.drawLine(2, TOP_BOX_END_X-56-16, TOP1_BOX_START_Y+108+108, TOP_BOX_END_X-56-16, TOP1_BOX_END_Y,true);//第一联竖线2，从左到右
//        iPrinter.drawLine(2, 260+8, TOP1_BOX_START_Y+108+108+176+176, 260+8, TOP1_BOX_END_Y, true);//第一联竖线3，从左到右
        iPrinter.drawLine(2, TOP_BOX_END_X-56-16, TOP1_BOX_START_Y+88+128+80, TOP_BOX_END_X-56-16, TOP1_BOX_END_Y, true);//第一联竖线2，从左到右
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
        iPrinter.drawGraphic(20, TOP1_BOX_START_Y+12, res.getWidth(), res.getHeight(), res);
        iPrinter.drawBarCode(120, TOP1_BOX_START_Y+88+12, num, 1, 0, 3, 80);
        //条码数据
        iPrinter.drawText(120+12, TOP1_BOX_START_Y+88+20+76, Utility.formatOrderNo(num), 2,  0,0,false,false);
        iPrinter.drawText(299-24*bigChar.length(),  TOP1_BOX_START_Y+88 + 128 +20 , bigChar, 4, 0, 0, false, false);
        iPrinter.drawText(12,TOP1_BOX_START_Y+88 +128 + 80 +32,32,120,"收 件",2,0,  0,false,false);
        iPrinter.drawText(12,TOP1_BOX_START_Y+88+128+80+144+32,32,120,"发 件",2,0,  0,false,false);
        iPrinter.drawText(12,TOP1_BOX_START_Y+88+128+80+144+128+16,"签收人/签收时间",2,0,  0,false,false);
        String desc = "您的签字代表您已验收此包裹，并已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。";
        iPrinter.drawText(12,TOP1_BOX_START_Y+88+128+80+144+128+48,400,32,desc,1,0,  0,false,false);
        iPrinter.drawText(430,TOP1_BOX_START_Y+88+128+80+144+128+36, 20, 20, "月", 2, 0, 0, false, false);
        iPrinter.drawText(490,TOP1_BOX_START_Y+88+128+80+144+128+36, 20, 20, "日", 2, 0, 0, false, false);
        iPrinter.drawText(52+20,TOP1_BOX_START_Y+88 +128+80+24,448,40,order.getName()+"  "+order.getPhone(),3,0,  1,false,false);
        iPrinter.drawText(52+20,TOP1_BOX_START_Y+88 +128+80+24+32,424,100,order.getAddress(),3,0,  1,false,false);
        iPrinter.drawText(52+20,TOP1_BOX_START_Y+88+128+80+144+24,448,40,order.getSenderName()+"  "+order.getSenderPhone(),2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP1_BOX_START_Y+88+128+80+144+24+32,424,100,order.getSenderAddress(),2,0,  0,false,false);
        iPrinter.drawText(TOP_BOX_END_X-56-5,TOP1_BOX_START_Y+88 +128+80+104,32,96,"派   件   联",2,0,0,false,false);

//		//第二联
        iPrinter.drawBox(2, TOP_BOX_START_X,TOP2_BOX_START_Y, TOP_BOX_END_X, TOP2_BOX_END_Y);//第二联边框
        iPrinter.drawLine(2, TOP_BOX_START_X, TOP2_BOX_START_Y+80, TOP_BOX_END_X, TOP2_BOX_START_Y+80,true);//第二联横线1，从左到右
        iPrinter.drawLine(2,TOP_BOX_START_X, TOP2_BOX_START_Y+80+136, TOP_BOX_END_X-56-16, TOP2_BOX_START_Y+80+136,true);//第二联横线2，从左到右
        iPrinter.drawLine(2, 52, TOP2_BOX_START_Y+80, 52, TOP2_BOX_START_Y+80+136,true);//第二联竖线1，从左到右
        iPrinter.drawLine(2, TOP_BOX_END_X-56-16, TOP2_BOX_START_Y+80,TOP_BOX_END_X-56-16, TOP2_BOX_END_Y,true);//第二联竖线2，从左到右
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
        iPrinter.drawGraphic(20, TOP2_BOX_START_Y+16, bitmap.getWidth(), bitmap.getHeight(), bitmap);
        iPrinter.drawBarCode(320, TOP2_BOX_START_Y+16, num, 1, 0, 2, 36);
        //条码数据
        iPrinter.drawText(320+8, TOP2_BOX_START_Y+54, Utility.formatOrderNo(num), 1,  0,0,false,false);
        iPrinter.drawText(12,TOP2_BOX_START_Y+80+35,32,120,"收 件",2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP2_BOX_START_Y+80+28,448,40,order.getName()+"  "+order.getPhone(),2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP2_BOX_START_Y+80+28+32,424,100,order.getAddress(),2,0,  0,false,false);
        iPrinter.drawText(TOP_BOX_END_X-56-5,TOP2_BOX_START_Y+80+32,32,96,"客  户  联",2,0,0,false,false);
        String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
        iPrinter.drawText(12+8,TOP2_BOX_START_Y+80+136+22-5,"物品："+order.getArticleInfo()+" "+weight,2,0,  0,false,false);
        iPrinter.drawBox(2, TOP_BOX_END_X-56-16-120, TOP2_BOX_START_Y+80+136+11, TOP_BOX_END_X-56-16-16, TOP2_BOX_END_Y-11);
        iPrinter.drawText(TOP_BOX_END_X-56-16-120+17, TOP2_BOX_START_Y+80+136+11+6, "已验视", 2, 0, 0, false, false);

        //第三联
        iPrinter.drawBox(2, TOP_BOX_START_X, TOP3_BOX_START_Y, TOP_BOX_END_X, TOP3_BOX_END_Y);//第三联边框
        iPrinter.drawLine(2,TOP_BOX_START_X, TOP3_BOX_START_Y+80, TOP_BOX_END_X, TOP3_BOX_START_Y+80,true);//第三联横线1，从左到右
        iPrinter.drawLine(2,TOP_BOX_START_X,TOP3_BOX_START_Y+80+136, TOP_BOX_END_X-56-16, TOP3_BOX_START_Y+80+136,true);//第三联横线2，从左到右
        iPrinter.drawLine(2,TOP_BOX_START_X, TOP3_BOX_START_Y+80+136+136, TOP_BOX_END_X-56-16, TOP3_BOX_START_Y+80+136+136,true);//第三联横线3，从左到右
        iPrinter.drawLine(2, 52, TOP3_BOX_START_Y+80, 52, TOP3_BOX_START_Y+80+136+136, true);//第三联竖线1，从左到右
        iPrinter.drawLine(2, TOP_BOX_END_X-56-16, TOP3_BOX_START_Y+80, TOP_BOX_END_X-56-16, TOP3_BOX_END_Y, true);//第三联竖线2，从左到右

        iPrinter.drawGraphic(20, TOP3_BOX_START_Y+16, bitmap.getWidth(), bitmap.getHeight(), bitmap);
        iPrinter.drawBarCode(320, TOP3_BOX_START_Y+16, num, 1, 0, 2, 36);
        //条码数据
        iPrinter.drawText(320+8, TOP3_BOX_START_Y+54, Utility.formatOrderNo(num), 1,  0,0,false,false);
        iPrinter.drawText(12,TOP3_BOX_START_Y+80+32,32,120,"收 件",2,0,  0,false,false);
        iPrinter.drawText(12,TOP3_BOX_START_Y+80+136+32,32,120,"发 件",2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP3_BOX_START_Y+80+17,448,40,order.getName()+"  "+order.getPhone(),2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP3_BOX_START_Y+80+17+32,424,100,order.getAddress(),2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP3_BOX_START_Y+80+136+17,448,40,order.getSenderName()+"  "+order.getSenderPhone(),2,0,  0,false,false);
        iPrinter.drawText(52+20,TOP3_BOX_START_Y+80+136+17+32,424,100,order.getSenderAddress(),2,0,  0,false,false);
        iPrinter.drawText(TOP_BOX_END_X-56-5,TOP3_BOX_START_Y+80+84,32,96,"寄   件   联",2,0,0,false,false);
        iPrinter.drawText(12+8,TOP3_BOX_START_Y+80+136+136+18-5,"物品："+order.getArticleInfo()+ " "+weight,2,0,  0,false,false);
        iPrinter.drawBox(2, TOP_BOX_END_X-56-16-120, TOP3_BOX_START_Y+80+136+136+8, TOP_BOX_END_X-56-16-16, TOP3_BOX_END_Y-8);
        iPrinter.drawText(TOP_BOX_END_X-56-16-120+17, TOP3_BOX_START_Y+80+136+136+8+5, "已验视", 2, 0, 0, false, false);
        iPrinter.print(0, 1);
    }

    /**
     * 中通打印第一联
     */
    private void printFirstPager(String num, String bigChar, String charge, Order order) {
        iPrinter.drawLine(1, first_horizontal_line1_x1, first_horizontal_line1_y1, first_horizontal_line1_x2, first_horizontal_line1_y2, true);
        iPrinter.drawLine(1, first_horizontal_line2_x1, first_horizontal_line2_y1, first_horizontal_line2_x2, first_horizontal_line2_y2, true);
        iPrinter.drawLine(1, first_horizontal_line3_x1, first_horizontal_line3_y1, first_horizontal_line3_x2, first_horizontal_line3_y2, true);
        iPrinter.drawLine(1, first_horizontal_line4_x1, first_horizontal_line4_y1, first_horizontal_line4_x2, first_horizontal_line4_y2, true);
        iPrinter.drawLine(1, first_horizontal_line5_x1, first_horizontal_line5_y1, first_horizontal_line5_x2, first_horizontal_line5_y2, true);
        iPrinter.drawLine(1, first_horizontal_line6_x1, first_horizontal_line6_y1, first_horizontal_line6_x2, first_horizontal_line6_y2, true);
        iPrinter.drawLine(1, first_vertical_line1_x1, first_vertical_line1_y1, first_vertical_line1_x2, first_vertical_line1_y2, true);
        iPrinter.drawLine(1, first_vertical_line2_x1, first_vertical_line2_y1, first_vertical_line2_x2, first_vertical_line2_y2, true);
        iPrinter.drawLine(1, first_vertical_line3_x1, first_vertical_line3_y1, first_vertical_line3_x2, first_vertical_line3_y2, true);

        if (first_text1.equals("标准快递")) {
            iPrinter.drawText(first_text1_x + 3 * 32, first_text1_y, first_text1+charge, 3, 0, 1, true, false);
        } else {
            iPrinter.drawText(first_text1_x, first_text1_y, first_text1+charge, 3, 0, 1, true, false);
        }
        String barcode = addSpace2Barcode2(num);
        iPrinter.drawText(first_text2_x - 38, first_text2_y, barcode, 4, 0, 0, false, false);

        int x = calculateX(page_left, page_right, bigChar, first_text3_size);
        iPrinter.drawText(x, first_text3_y, bigChar, 9, 0, 0, false, false);

        x = calculateX(page_left, first_vertical_line2_x1, bigChar, first_text4_size);
        iPrinter.drawText(x, first_text4_y, bigChar, 3, 0, 0, false, false);

        String time = order.getTime();
        x = calculateX(first_vertical_line2_x1, page_right, time.substring(0,time.indexOf(" ")), first_text5_size / 2);
        iPrinter.drawText(x, first_text5_y, time.substring(0,time.indexOf(" ")), 3, 0, 0, false, false);

        iPrinter.drawText(first_text6_x, first_text6_y, first_text6, 2, 0, 0, false, false);
        iPrinter.drawText(first_text7_x, first_text7_y, first_text7, 2, 0, 0, false, false);
        iPrinter.drawText(first_text8_x, first_text8_y, first_text8, 2, 0, 0, false, false);
        iPrinter.drawText(first_text9_x, first_text9_y, first_text9, 2, 0, 0, false, false);
        iPrinter.drawText(first_text10_x, first_text10_y, first_text10 + order.getName(), 2, 0, 0, false, false);
        iPrinter.drawText(first_text11_x, first_text11_y, first_text11 + order.getPhone(), 2, 0, 0, false, false);

        printChangeLineText(iPrinter, first_text12 + order.getAddress(), first_text12_x, first_text12_y, first_text12_size, 19, 1);
        iPrinter.drawText(first_text13_x, first_text13_y, first_text13, 2, 0, 0, false, false);
        iPrinter.drawText(first_text14_x, first_text14_y, first_text14, 2, 0, 0, false, false);
        iPrinter.drawText(first_text15_x, first_text15_y, first_text15, 2, 0, 0, false, false);
        iPrinter.drawText(first_text16_x, first_text16_y, first_text16, 2, 0, 0, false, false);
        iPrinter.drawText(first_text17_x, first_text17_y, first_text17 + order.getSenderName(), 8, 0, 0, false, false);
        iPrinter.drawText(first_text18_x, first_text18_y, first_text18 + order.getSenderPhone(), 8, 0, 0, false, false);

        printChangeLineText(iPrinter, first_text19 + order.getSenderAddress(), first_text19_x, first_text19_y, first_text19_size, 23, 8);
        iPrinter.drawText(first_text20_x, first_text20_y, first_text20, 2, 0, 0, false, false);
        iPrinter.drawText(first_text21_x, first_text21_y, first_text21, 2, 0, 0, false, false);
        iPrinter.drawText(first_text22_x, first_text22_y, first_text22 + order.getArticleInfo(), 1, 0, 0, false, false);
        String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
        iPrinter.drawText(first_text23_x, first_text23_y, first_text23 + weight, 1, 0, 0, false, false);
        iPrinter.drawText(first_text24_x, first_text24_y, first_text24, 1, 0, 0, false, false);
        iPrinter.drawText(first_text25_x, first_text25_y, first_text25+charge, 1, 0, 0, false, false);
        iPrinter.drawText(first_text26_x, first_text26_y, first_text26, 1, 0, 0, false, false);
        iPrinter.drawText(first_text27_x, first_text27_y, first_text27, 2, 0, 0, false, false);
        iPrinter.drawText(first_text28_x, first_text28_y, first_text28, 2, 0, 0, false, false);

        iPrinter.drawBarCode(first_barcode1_x, first_barcode1_y, num, 1, 0, 4, 80);
    }

    /**
     * 中通打印第二联
     */
    private void printSecondPager(String num, String bigChar, String charge, Order order) {
        iPrinter.drawLine(1, second_horizontal_line1_x1, second_horizontal_line1_y1, second_horizontal_line1_x2, second_horizontal_line1_y2, true);
        iPrinter.drawLine(1, second_horizontal_line2_x1, second_horizontal_line2_y1, second_horizontal_line2_x2, second_horizontal_line2_y2, true);
        iPrinter.drawLine(1, second_horizontal_line3_x1, second_horizontal_line3_y1, second_horizontal_line3_x2, second_horizontal_line3_y2, true);
        iPrinter.drawLine(1, second_horizontal_line4_x1, second_horizontal_line4_y1, second_horizontal_line4_x2, second_horizontal_line4_y2, true);
        iPrinter.drawLine(1, second_vertical_line1_x1, second_vertical_line1_y1, second_vertical_line1_x2, second_vertical_line1_y2, true);
        iPrinter.drawLine(1, second_vertical_line2_x1, second_vertical_line2_y1, second_vertical_line2_x2, second_vertical_line2_y2, true);
        iPrinter.drawLine(1, second_vertical_line3_x1, second_vertical_line3_y1, second_vertical_line3_x2, second_vertical_line3_y2, true);
        iPrinter.drawLine(1, second_vertical_line4_x1, second_vertical_line4_y1, second_vertical_line4_x2, second_vertical_line4_y2, true);
        iPrinter.drawLine(1, second_vertical_line5_x1, second_vertical_line5_y1, second_vertical_line5_x2, second_vertical_line5_y2, true);

        iPrinter.drawText(second_text1_x, second_text1_y, second_text1 + num, 1, 0, 0, false, false);
        iPrinter.drawText(second_text2_x, second_text2_y, second_text2 + order.getId(), 1, 0, 0, false, false);
        iPrinter.drawText(second_text3_x, second_text3_y, second_text3, 1, 0, 0, false, false);
        iPrinter.drawText(second_text4_x, second_text4_y, order.getName() + "  "+order.getPhone(), 1, 0, 0, false, false);
        printChangeLineText(iPrinter, order.getAddress(), second_text5_x, second_text5_y, second_text5_size, 14, 1);
        iPrinter.drawText(second_text6_x, second_text6_y, second_text6, 1, 0, 0, false, false);
        iPrinter.drawText(second_text7_x, second_text7_y, order.getSenderName() + "  "+order.getSenderPhone(), 1, 0, 0, false, false);
        printChangeLineText(iPrinter, order.getSenderAddress(), second_text8_x, second_text8_y, second_text8_size, 14, 1);
        iPrinter.drawText(second_text9_x, second_text9_y, second_text9, 1, 0, 0, false, false);
        iPrinter.drawText(second_text10_x, second_text10_y, second_text10, 1, 0, 0, false, false);
        iPrinter.drawText(second_text11_x, second_text11_y, second_text11, 1, 0, 0, false, false);
        iPrinter.drawText(second_text12_x, second_text12_y, second_text12, 1, 0, 0, false, false);
        iPrinter.drawText(second_text13_x, second_text13_y, second_text13, 1, 0, 0, false, false);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {//内容品名，暂时没有
            x = calculateX(page_left, second_vertical_line2_x1, order.getArticleInfo(), second_text14_size);
            iPrinter.drawText(x, second_text14_y, order.getArticleInfo(), 1, 0, 0, false, false);
        }

        x = calculateX(second_vertical_line2_x1, second_vertical_line3_x1, order.getCharging_weight(), second_text15_size / 2);
        iPrinter.drawText(x, second_text15_y, order.getCharging_weight(), 1, 0, 0, false, false);

        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line3_x1, second_vertical_line4_x1, "", second_text16_size / 2);
            iPrinter.drawText(x, second_text16_y, "", 1, 0, 0, false, false);
        }


        if (charge != null && charge.length() > 0) {//代收货款有可能没有
            x = calculateX(second_vertical_line4_x1, second_vertical_line5_x1, charge, second_text17_size / 2);
            iPrinter.drawText(x, second_text17_y, charge, 1, 0, 0, false, false);
        }
        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line5_x1, page_right, "", second_text18_size / 2);
            iPrinter.drawText(x, second_text18_y, "", 1, 0, 0, false, false);
        }

    }

    /**
     * 中通打印第三联
     */
    protected void printThirdPager(String num, String bigChar, String charge, Order order) {
        iPrinter.drawLine(1, third_horizontal_line1_x1, third_horizontal_line1_y1, third_horizontal_line1_x2, third_horizontal_line1_y2, true);
        iPrinter.drawLine(1, third_horizontal_line2_x1, third_horizontal_line2_y1, third_horizontal_line2_x2, third_horizontal_line2_y2, true);
        iPrinter.drawLine(1, third_horizontal_line3_x1, third_horizontal_line3_y1, third_horizontal_line3_x2, third_horizontal_line3_y2, true);
        iPrinter.drawLine(1, third_horizontal_line4_x1, third_horizontal_line4_y1, third_horizontal_line4_x2, third_horizontal_line4_y2, true);
        iPrinter.drawLine(1, third_vertical_line1_x1, third_vertical_line1_y1, third_vertical_line1_x2, third_vertical_line1_y2, true);
        iPrinter.drawLine(1, third_vertical_line2_x1, third_vertical_line2_y1, third_vertical_line2_x2, third_vertical_line2_y2, true);
        iPrinter.drawLine(1, third_vertical_line3_x1, third_vertical_line3_y1, third_vertical_line3_x2, third_vertical_line3_y2, true);
        iPrinter.drawLine(1, third_vertical_line4_x1, third_vertical_line4_y1, third_vertical_line4_x2, third_vertical_line4_y2, true);
        iPrinter.drawLine(1, third_vertical_line5_x1, third_vertical_line5_y1, third_vertical_line5_x2, third_vertical_line5_y2, true);
        iPrinter.drawLine(1, third_vertical_line6_x1, third_vertical_line6_y1, third_vertical_line6_x2, third_vertical_line6_y2, true);

        String barcode = addSpace2Barcode2(num);
        iPrinter.drawText(third_text1_x, third_text1_y, barcode, 2, 0, 0, false, false);
        iPrinter.drawText(third_text2_x, third_text2_y, third_text2, 1, 0, 0, false, false);
        iPrinter.drawText(third_text3_x, third_text3_y, order.getName()+"  "+order.getPhone(), 1, 0, 0, false, false);
        printChangeLineText(iPrinter, order.getAddress(), third_text4_x, third_text4_y, third_text4_size, 14, 1);
        iPrinter.drawText(third_text5_x, third_text5_y, third_text5, 1, 0, 0, false, false);
        iPrinter.drawText(third_text6_x, third_text6_y, order.getSenderName()+"  "+order.getSenderPhone(), 1, 0, 0, false, false);
        printChangeLineText(iPrinter, order.getSenderAddress(), third_text7_x, third_text7_y, third_text7_size, 14, 1);
        iPrinter.drawText(third_text9_x, third_text9_y, third_text9, 1, 0, 0, false, false);
        iPrinter.drawText(third_text10_x, third_text10_y, third_text10, 1, 0, 0, false, false);
        iPrinter.drawText(third_text11_x, third_text11_y, third_text11, 1, 0, 0, false, false);
        iPrinter.drawText(third_text12_x, third_text12_y, third_text12, 1, 0, 0, false, false);
        iPrinter.drawText(third_text13_x, third_text13_y, third_text13, 1, 0, 0, false, false);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {
            x = calculateX(page_left, third_vertical_line2_x1, order.getArticleInfo(), third_text14_size);
            iPrinter.drawText(x, third_text14_y, order.getArticleInfo(), 1, 0, 0, false, false);
        }
        x = calculateX(third_vertical_line2_x1, third_vertical_line3_x1, order.getCharging_weight(), third_text15_size / 2);
        iPrinter.drawText(x, third_text15_y, order.getCharging_weight(), 1, 0, 0, false, false);

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line3_x1, third_vertical_line4_x1, "", third_text16_size / 2);
            iPrinter.drawText(x, third_text16_y, "", 1, 0, 0, false, false);
        }

        if (charge != null) {
            x = calculateX(third_vertical_line4_x1, third_vertical_line5_x1, charge, third_text17_size / 2);
            iPrinter.drawText(x, third_text17_y, charge, 1, 0, 0, false, false);
        }

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line5_x1, page_right, "", third_text18_size / 2);
            iPrinter.drawText(x, third_text18_y, "", 1, 0, 0, false, false);
        }

        iPrinter.drawText(third_text19_x, third_text19_y, third_text19, 1, 0, 0, false, false);
        iPrinter.drawText(third_text20_x, third_text20_y, third_text20, 1, 0, 0, false, false);
        iPrinter.drawText(third_text21_x, third_text21_y, third_text21, 1, 0, 0, false, false);
        iPrinter.drawText(third_text22_x, third_text22_y, third_text22, 1, 0, 0, false, false);

        iPrinter.drawBarCode(third_barcode1_x, third_barcode1_y, num, 8, 0, 2, 60);
    }

    /**
     * 方法用于打印文字太多换行打印
     *
     * @param printPP_cpcl
     * @param text
     * @param x
     * @param y
     * @param height       字体实际高度
     * @param num          换行字数
     * @param size         对应打印机打印文字高度参数
     */
    protected void printChangeLineText(PrintPP_CPCL printPP_cpcl, String text, int x, int y, int height, int num, int size) {
        if (height == 16) {
            size = 1;
        } else if (height == 24) {
            size = 2;
        }
        if (text != null && num > 0 && text.length() > num) {
            StringBuilder sb = new StringBuilder(text);
            String subs = sb.substring(0, num);
            //打印
            printPP_cpcl.drawText(x, y, subs, size, 0, 0, false, false);
            sb.delete(0, num);
            String remainming = sb.toString();
            if (remainming.length() > num) {
                //x,y设置
                y += height;
                printChangeLineText(printPP_cpcl, remainming, x, y, height, num, size);
            } else {
                //打印
                y += height;
                printPP_cpcl.drawText(x, y, remainming, size, 0, 0, false, false);
            }
        } else {
            printPP_cpcl.drawText(x, y, text, size, 0, 0, false, false);
        }
    }

}
