package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.JQPrinter;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.Printer_define;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl.Barcode;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl.JPL;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl.Page;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl.Text;
import com.kuaibao.skuaidi.bluetooth.printer.jq.printer.jpl.Text.TEXT_ENLARGE;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

/**
 * Created by kuaibao on 2016/7/19.
 *
 * 济强打印机
 */

public class JLPPrinter extends PrinterBase {

    private static JQPrinter jqPrinter;
    private static JLPPrinter jlPrinter;

    private JLPPrinter(JQPrinter jqPrinter){
        this.jqPrinter = jqPrinter;
    }

    public static JLPPrinter getInstance(){
        if(Utility.isEmpty(jlPrinter)){
            jqPrinter = new JQPrinter(Printer_define.PRINTER_MODEL.JLP351);
            jlPrinter = new JLPPrinter(jqPrinter);
        }
        return jlPrinter;
    }
    @Override
    public void connect(final BluetoothDevice device, final ConnectedCallBack callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(!Utility.isEmpty(jqPrinter)){
//                    jqPrinter.close();
                    jqPrinter.wakeUp();
                    jqPrinter.open(device.getAddress());
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
//        if(!Utility.isEmpty(jqPrinter)){
//            jqPrinter.close();
//        }
          if(!Utility.isEmpty(jqPrinter) && !jqPrinter.isOpen) {
              jqPrinter.wakeUp();
              jqPrinter.open(device.getAddress());
          }
    }

    @Override
    public void disConnect() {
        if(!Utility.isEmpty(jqPrinter)){
            jqPrinter.close();
        }
    }

    @Override
    public boolean isConnected() {
        if(!Utility.isEmpty(jqPrinter)){
            return jqPrinter.isOpen;
        }
        return false;
    }

    @Override
    public String getPrinterStatus() {
        KLog.i("tag", jqPrinter.getPrinterState(1000));
        return "OK";
    }

    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        String charge = order.getCollection_amount();
        if (!jqPrinter.getJPLsupport()) {
            UtilToolkit.showToast( "打印失败");
            return;
        }
        jqPrinter.jpl.page.start(0, 0, page_width, page_height, Page.PAGE_ROTATE.x0);
        printFirstPager(num, bigChar, charge, order);
        printSecondPager(num, bigChar, charge, order);
        printThirdPager(num, bigChar, charge, order);
        jqPrinter.jpl.page.end();
        jqPrinter.jpl.page.print();
        jqPrinter.jpl.feedMarkOrGap(0);
    }

    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        if (!jqPrinter.getJPLsupport()) {
            UtilToolkit.showToast( "打印失败");
        }
        JPL jpl = jqPrinter.jpl;
        jpl.page.start(0,0,576,424, Page.PAGE_ROTATE.x90);
        jpl.barcode.code128(Printer_define.ALIGN.CENTER,0,575, 0, 64, Barcode.BAR_UNIT.x2, Barcode.BAR_ROTATE.ANGLE_0, "1515651545");//printer.jpl.barcode.code128(16, 0, 64, BAR_UNIT.x3, BAR_ROTATE.ANGLE_0, bar_str);
        //	printer.jpl.barcode.PDF417(16, 68, 5, 3, 4, BAR_UNIT.x2, ROTATE.x0, bar_str);
        jpl.text.drawOut(Printer_define.ALIGN.CENTER,0,575, 66, "测试", 16, false, false, false, false, Text.TEXT_ENLARGE.x2, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);//printer.jpl.text.drawOut(96, 64, bar_str);
        //	printer.jpl.barcode.QRCode(0, 120, 0, QRCODE_ECC.LEVEL_M, BAR_UNIT.x3, ROTATE.x0, bar_str);
        jpl.graphic.line(8,96,568,96, 3);
        jpl.graphic.line(8, 160, 568, 160, 3);
        jpl.graphic.line(8, 224,568, 224, 3);
        jpl.graphic.line(8, 288,568, 288, 3);

        jpl.graphic.line(8,96,8, 288, 3);
        jpl.graphic.line(568, 96,568, 288, 3);
        jpl.graphic.line(304, 96,304, 224, 3);
        jpl.graphic.line(456, 96,456, 160, 3);

        jpl.text.drawOut(14, 104, "15165151", 24, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x2, JPL.ROTATE.x0);
        jpl.text.drawOut(14, 168, "51516514564", 24, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(320, 168, "465465465456", 24, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(320, 168+26, "021-61645760", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(14, 232, "11111111111", 24, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(14, 296, "www.qs-express.com", 32, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.page.end();
        jpl.page.print();
        jpl.feedMarkOrGap(0);//printer.jpl.feedNextLabelEnd(48);//printer.jpl.feedNextLabelBegin();
    }

    /**
     * 中通绘制第一联
     */
    protected void printFirstPager(String num, String bigChar, String charge,  Order order) {
        jqPrinter.jpl.graphic.line(first_horizontal_line1_x1, first_horizontal_line1_y1, first_horizontal_line1_x2, first_horizontal_line1_y2, 1);
        jqPrinter.jpl.graphic.line(first_horizontal_line2_x1, first_horizontal_line2_y1, first_horizontal_line2_x2, first_horizontal_line2_y2, 1);
        jqPrinter.jpl.graphic.line(first_horizontal_line3_x1, first_horizontal_line3_y1, first_horizontal_line3_x2, first_horizontal_line3_y2, 1);
        jqPrinter.jpl.graphic.line(first_horizontal_line4_x1, first_horizontal_line4_y1, first_horizontal_line4_x2, first_horizontal_line4_y2, 1);
        jqPrinter.jpl.graphic.line(first_horizontal_line5_x1, first_horizontal_line5_y1, first_horizontal_line5_x2, first_horizontal_line5_y2, 1);
        jqPrinter.jpl.graphic.line(first_horizontal_line6_x1, first_horizontal_line6_y1, first_horizontal_line6_x2, first_horizontal_line6_y2, 1);
        jqPrinter.jpl.graphic.line(first_vertical_line1_x1, first_vertical_line1_y1,first_vertical_line1_x2, first_vertical_line1_y2, 1);
        jqPrinter.jpl.graphic.line(first_vertical_line2_x1, first_vertical_line2_y1, first_vertical_line2_x2, first_vertical_line2_y2, 1);
        jqPrinter.jpl.graphic.line(first_vertical_line3_x1, first_vertical_line3_y1, first_vertical_line3_x2, first_vertical_line3_y2, 1);

        jqPrinter.jpl.text.drawOut(first_text1_x, first_text1_y, first_text1+charge, first_text1_size, false, true, false, false, Text.TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        String barcode = addSpace2Barcode2(num);
        jqPrinter.jpl.text.drawOut(first_text2_x - 38, first_text2_y - 16, barcode, 48, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        int x = calculateX(page_left, page_right, bigChar, first_text3_size);
        jqPrinter.jpl.text.drawOut(x, first_text3_y, bigChar, 56, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        x = calculateX(page_left, first_vertical_line2_x1, bigChar, first_text4_size);
        jqPrinter.jpl.text.drawOut(x, first_text4_y, bigChar, first_text4_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        String time = order.getTime();
        x = calculateX(first_vertical_line2_x1, page_right, time.substring(0, time.indexOf(" ")), first_text5_size / 2);
        jqPrinter.jpl.text.drawOut(x, first_text5_y, time.substring(0, time.indexOf(" ")), first_text5_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text6_x, first_text6_y, first_text6, first_text6_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text7_x, first_text7_y, first_text7, first_text7_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text8_x, first_text8_y, first_text8, first_text8_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text9_x, first_text9_y, first_text9, first_text9_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text10_x, first_text10_y, first_text10+order.getName(), first_text10_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text11_x, first_text11_y, first_text11+order.getPhone(), first_text11_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        printChangeLineText(first_text12+order.getAddress(), first_text12_x, first_text12_y, first_text12_size, 19);
        jqPrinter.jpl.text.drawOut(first_text13_x, first_text13_y, first_text13, first_text13_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text14_x, first_text14_y, first_text14, first_text14_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text15_x, first_text15_y, first_text15, first_text15_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text16_x, first_text16_y, first_text16, first_text16_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text17_x, first_text17_y, first_text17+order.getSenderName(), 20, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text18_x, first_text18_y, first_text18+order.getSenderPhone(), 20, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        printChangeLineText(first_text19+order.getSenderAddress(), first_text19_x, first_text19_y, first_text19_size, 23);
        jqPrinter.jpl.text.drawOut(first_text20_x, first_text20_y, first_text20, first_text20_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text21_x, first_text21_y, first_text21, first_text21_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text22_x, first_text22_y, first_text22+order.getArticleInfo(), first_text22_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
        jqPrinter.jpl.text.drawOut(first_text23_x, first_text23_y, first_text23+weight, first_text23_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text24_x, first_text24_y, first_text24, first_text24_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text25_x, first_text25_y, first_text25+charge, first_text25_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text26_x, first_text26_y, first_text26, first_text26_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text27_x, first_text27_y, first_text27, first_text27_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(first_text28_x, first_text28_y, first_text28, first_text28_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        jqPrinter.jpl.barcode.code128(first_barcode1_x+20, first_barcode1_y - 8, 80, Barcode.BAR_UNIT.x4, Barcode.BAR_ROTATE.ANGLE_0, num);

    }

    /**
     * 中通绘制第二联
     */
    protected void printSecondPager(String num, String bigChar, String charge, Order order) {
        jqPrinter.jpl.graphic.line(second_horizontal_line1_x1, second_horizontal_line1_y1, second_horizontal_line1_x2, second_horizontal_line1_y2, 1);
        jqPrinter.jpl.graphic.line(second_horizontal_line2_x1, second_horizontal_line2_y1, second_horizontal_line2_x2, second_horizontal_line2_y2, 1);
        jqPrinter.jpl.graphic.line(second_horizontal_line3_x1, second_horizontal_line3_y1, second_horizontal_line3_x2, second_horizontal_line3_y2, 1);
        jqPrinter.jpl.graphic.line(second_horizontal_line4_x1, second_horizontal_line4_y1, second_horizontal_line4_x2, second_horizontal_line4_y2, 1);
        jqPrinter.jpl.graphic.line(second_vertical_line1_x1, second_vertical_line1_y1, second_vertical_line1_x2, second_vertical_line1_y2, 1);
        jqPrinter.jpl.graphic.line(second_vertical_line2_x1, second_vertical_line2_y1, second_vertical_line2_x2, second_vertical_line2_y2, 1);
        jqPrinter.jpl.graphic.line(second_vertical_line3_x1, second_vertical_line3_y1, second_vertical_line3_x2, second_vertical_line3_y2, 1);
        jqPrinter.jpl.graphic.line(second_vertical_line4_x1, second_vertical_line4_y1, second_vertical_line4_x2, second_vertical_line4_y2, 1);
        jqPrinter.jpl.graphic.line(second_vertical_line5_x1, second_vertical_line5_y1, second_vertical_line5_x2, second_vertical_line5_y2, 1);

        jqPrinter.jpl.text.drawOut(second_text1_x, second_text1_y, second_text1+num, second_text1_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text2_x, second_text2_y, second_text2+order.getId(), second_text2_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text3_x, second_text3_y, second_text3, second_text3_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text4_x, second_text4_y, order.getName()+"  "+order.getPhone(), second_text4_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        printChangeLineText(order.getAddress(), second_text5_x, second_text5_y, second_text5_size, 14);
        jqPrinter.jpl.text.drawOut(second_text6_x, second_text6_y, second_text6, second_text6_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text7_x, second_text7_y, order.getSenderName()+"  "+order.getSenderPhone(), second_text7_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        printChangeLineText(order.getSenderAddress(), second_text8_x, second_text8_y, second_text8_size, 14);
        jqPrinter.jpl.text.drawOut(second_text9_x, second_text9_y, second_text9, second_text9_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text10_x, second_text10_y, second_text10, second_text10_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text11_x, second_text11_y, second_text11, second_text11_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text12_x, second_text12_y, second_text12, second_text12_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(second_text13_x, second_text13_y, second_text13, second_text13_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {//内容品名，暂时没有
            x = calculateX(page_left, second_vertical_line2_x1, order.getArticleInfo(), second_text14_size);
            jqPrinter.jpl.text.drawOut(x, second_text14_y, order.getArticleInfo(), second_text14_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }

        x = calculateX(second_vertical_line2_x1, second_vertical_line3_x1, order.getCharging_weight(), second_text15_size / 2);
        jqPrinter.jpl.text.drawOut(x, second_text15_y, order.getCharging_weight(), second_text15_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line3_x1, second_vertical_line4_x1, "", second_text16_size / 2);
            jqPrinter.jpl.text.drawOut(x, second_text16_y, "", second_text16_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }


        if (charge != null && charge.length() > 0) {//代收货款有可能没有
            x = calculateX(second_vertical_line4_x1, second_vertical_line5_x1, charge, second_text17_size / 2);
            jqPrinter.jpl.text.drawOut(x, second_text17_y, charge, second_text17_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }
        if (order.getPrice() != null) {
            x = calculateX(second_vertical_line5_x1, page_right, "", second_text18_size / 2);
            jqPrinter.jpl.text.drawOut(x, second_text18_y, "", second_text18_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }

    }

    /**
     * 中通绘制第三联
     */
    protected void printThirdPager(String num, String bigChar, String charge, Order order) {
        jqPrinter.jpl.graphic.line(third_horizontal_line1_x1, third_horizontal_line1_y1, third_horizontal_line1_x2, third_horizontal_line1_y2, 1);
        jqPrinter.jpl.graphic.line(third_horizontal_line2_x1, third_horizontal_line2_y1, third_horizontal_line2_x2, third_horizontal_line2_y2, 1);
        jqPrinter.jpl.graphic.line(third_horizontal_line3_x1, third_horizontal_line3_y1, third_horizontal_line3_x2, third_horizontal_line3_y2, 1);
        jqPrinter.jpl.graphic.line(third_horizontal_line4_x1, third_horizontal_line4_y1, third_horizontal_line4_x2, third_horizontal_line4_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line1_x1, third_vertical_line1_y1, third_vertical_line1_x2, third_vertical_line1_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line2_x1, third_vertical_line2_y1, third_vertical_line2_x2, third_vertical_line2_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line3_x1, third_vertical_line3_y1, third_vertical_line3_x2, third_vertical_line3_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line4_x1, third_vertical_line4_y1, third_vertical_line4_x2, third_vertical_line4_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line5_x1, third_vertical_line5_y1, third_vertical_line5_x2, third_vertical_line5_y2, 1);
        jqPrinter.jpl.graphic.line(third_vertical_line6_x1, third_vertical_line6_y1, third_vertical_line6_x2, third_vertical_line6_y2, 1);

        String barcode = addSpace2Barcode2(num);
        jqPrinter.jpl.text.drawOut(third_text1_x, third_text1_y, barcode, third_text1_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text2_x, third_text2_y, third_text2, third_text2_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text3_x, third_text3_y, order.getName()+"  "+order.getPhone(), third_text3_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        printChangeLineText(order.getAddress(), third_text4_x, third_text4_y, third_text4_size, 14);
        jqPrinter.jpl.text.drawOut(third_text5_x, third_text5_y, third_text5, third_text5_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text6_x, third_text6_y, order.getSenderName()+"  "+order.getSenderPhone(), third_text6_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        printChangeLineText(order.getSenderAddress(), third_text7_x, third_text7_y, third_text7_size, 14);
        jqPrinter.jpl.text.drawOut(third_text9_x, third_text9_y, third_text9, third_text9_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text10_x, third_text10_y, third_text10, third_text10_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text11_x, third_text11_y, third_text11, third_text11_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text12_x, third_text12_y, third_text12, third_text12_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text13_x, third_text13_y, third_text13, third_text13_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        int x = 10;
        if (!Utility.isEmpty(order.getArticleInfo())) {
            x = calculateX(page_left, third_vertical_line2_x1, order.getArticleInfo(), third_text14_size);
            jqPrinter.jpl.text.drawOut(x, third_text14_y, order.getArticleInfo(), third_text14_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }
        x = calculateX(third_vertical_line2_x1, third_vertical_line3_x1, order.getCharging_weight(), third_text15_size / 2);
        jqPrinter.jpl.text.drawOut(x, third_text15_y, order.getCharging_weight(), third_text15_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line3_x1, third_vertical_line4_x1, "", third_text16_size / 2);
            jqPrinter.jpl.text.drawOut(x, third_text16_y, "", third_text16_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }

        if (charge != null) {
            x = calculateX(third_vertical_line4_x1, third_vertical_line5_x1, charge, third_text17_size / 2);
            jqPrinter.jpl.text.drawOut(x, third_text17_y, charge, third_text17_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }

        if (order.getPrice() != null) {
            x = calculateX(third_vertical_line5_x1, page_right, "", third_text18_size / 2);
            jqPrinter.jpl.text.drawOut(x, third_text18_y, "", third_text18_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }

        jqPrinter.jpl.text.drawOut(third_text19_x, third_text19_y, third_text19, third_text19_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text20_x, third_text20_y, third_text20, third_text20_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text21_x, third_text21_y, third_text21, third_text21_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jqPrinter.jpl.text.drawOut(third_text22_x, third_text22_y, third_text22, third_text22_size, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        jqPrinter.jpl.barcode.code128(third_barcode1_x, third_barcode1_y, 52, Barcode.BAR_UNIT.x2, Barcode.BAR_ROTATE.ANGLE_0, num);

    }


    private void printChangeLineText(String text, int x, int y, int height, int num) {
        if (text != null && num > 0 && text.length() > num) {
            StringBuilder sb = new StringBuilder(text);
            String subs = sb.substring(0, num);
            //打印
            jqPrinter.jpl.text.drawOut(x, y, subs, height, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
            sb.delete(0, num);
            String remainming = sb.toString();
            if (remainming.length() > num) {
                //x,y设置
                y += height;
                printChangeLineText(remainming, x, y, height, num);
            } else {
                //打印
                y += height;
                jqPrinter.jpl.text.drawOut(x, y, remainming, height, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
            }
        } else {
            jqPrinter.jpl.text.drawOut(x, y, text, height, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        }
    }
}
