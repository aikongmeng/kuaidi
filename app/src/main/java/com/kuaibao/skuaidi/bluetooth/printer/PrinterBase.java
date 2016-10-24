package com.kuaibao.skuaidi.bluetooth.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.util.Date;

/**
 * Created by kuaibao on 2016/7/17.
 * 打印机基类
 */

public abstract class PrinterBase {

    protected static final double MULTIPLE = 8;
    protected static final int page_width = (int) (70 * MULTIPLE);
    protected static final int page_height = (int) (200 * MULTIPLE);
    protected static final int page_left = (int) (0 * MULTIPLE);
    protected static final int page_right = (int) (70 * MULTIPLE);
    protected static final int first_pager_bottom = (int) (96 * MULTIPLE);
    //    private static final int second_pager_bottom =(int) (50*MULTIPLE);
    protected static final int second_pager_bottom = (int) (146 * MULTIPLE);
    public static BluetoothAdapter myBluetoothAdapter;
    protected final int first_horizontal_line1_x1 = page_left;
    protected final int first_horizontal_line1_y1 = (int) (12 * MULTIPLE);
    protected final int first_horizontal_line1_x2 = page_right;
    protected final int first_horizontal_line1_y2 = first_horizontal_line1_y1;
    protected final int first_horizontal_line2_x1 = page_left;
    protected final int first_horizontal_line2_y1 = (int) (20 * MULTIPLE) + first_horizontal_line1_y1;
    protected final int first_horizontal_line2_x2 = page_right;
    protected final int first_horizontal_line2_y2 = first_horizontal_line2_y1;
    protected final int first_horizontal_line3_x1 = page_left;
    protected final int first_horizontal_line3_y1 = (int) (10 * MULTIPLE) + first_horizontal_line2_y1;
    protected final int first_horizontal_line3_x2 = page_right;
    protected final int first_horizontal_line3_y2 = first_horizontal_line3_y1;
    protected final int first_horizontal_line4_x1 = page_left;
    protected final int first_horizontal_line4_y1 = (int) (8 * MULTIPLE) + first_horizontal_line3_y1;
    protected final int first_horizontal_line4_x2 = page_right;
    protected final int first_horizontal_line4_y2 = first_horizontal_line4_y1;
    protected final int first_horizontal_line5_x1 = page_left;
    protected final int first_horizontal_line5_y1 = (int) (15 * MULTIPLE) + first_horizontal_line4_y1;
    protected final int first_horizontal_line5_x2 = page_right;
    protected final int first_horizontal_line5_y2 = first_horizontal_line5_y1;
    protected final int first_horizontal_line6_x1 = page_left;
    protected final int first_horizontal_line6_y1 = (int) (13 * MULTIPLE) + first_horizontal_line5_y1;
    protected final int first_horizontal_line6_x2 = page_right;
    protected final int first_horizontal_line6_y2 = first_horizontal_line6_y1;
    protected final int first_vertical_line1_x1 = page_left + (int) (8 * MULTIPLE);
    protected final int first_vertical_line1_y1 = first_horizontal_line4_y1;
    protected final int first_vertical_line1_x2 = first_vertical_line1_x1;
    protected final int first_vertical_line1_y2 = first_pager_bottom;
    protected final int first_vertical_line2_x1 = page_left + (int) (35 * MULTIPLE);
    protected final int first_vertical_line2_y1 = first_horizontal_line3_y1;
    protected final int first_vertical_line2_x2 = first_vertical_line2_x1;
    protected final int first_vertical_line2_y2 = first_horizontal_line4_y1;
    protected final int first_vertical_line3_x1 = first_vertical_line2_x1;
    protected final int first_vertical_line3_y1 = first_horizontal_line6_y1;
    protected final int first_vertical_line3_x2 = first_vertical_line2_x1;
    protected final int first_vertical_line3_y2 = first_pager_bottom;
    //    private final int first_text1_size= 0;
    protected final int first_text1_size = 32;
    protected final int first_text1_x = page_left + (int) (35 * MULTIPLE)-20;
    protected final int first_text1_y = (int) ((12 * MULTIPLE - first_text1_size) / 2);
    protected final int first_text2_size = 32;
    protected final int first_text2_x = page_left + (int) (16 * MULTIPLE);
    protected final int first_text2_y = first_horizontal_line2_y1 - 48;
    protected final int first_text3_size = 48;
    //    protected final int first_text3_x = page_left+ (int) (10*MULTIPLE);
    protected final int first_text3_y = first_horizontal_line2_y1 + (int) ((10 * MULTIPLE - first_text3_size) / 2);
    protected final int first_text4_size = 32;
    //    protected final int first_text4_x = page_left+ (int) (10*MULTIPLE);
    protected final int first_text4_y = first_horizontal_line3_y1 + (int) ((8 * MULTIPLE - first_text4_size) / 2);
    protected final int first_text5_size = 24;
    //    protected final int first_text5_x = first_horizontal_line3_x1+ (int) (10*MULTIPLE);
    protected final int first_text5_y = first_horizontal_line3_y1 + (int) ((8 * MULTIPLE - first_text5_size) / 2);
    protected final int first_text6_size = 24;
    protected final int first_text6_x = page_left + (int) ((8 * MULTIPLE - first_text6_size) / 2);
    protected final int first_text6_y = first_horizontal_line4_y1 + (int) (15 * MULTIPLE / 2 - 2 * first_text6_size);
    protected final String first_text6 = "收";
    protected final int first_text7_size = first_text6_size;
    protected final int first_text7_x = first_text6_x;
    protected final int first_text7_y = first_horizontal_line4_y1 + (int) (15 * MULTIPLE / 2 - first_text7_size);
    protected final String first_text7 = "件";
    protected final int first_text8_size = first_text6_size;
    protected final int first_text8_x = first_text6_x;
    protected final int first_text8_y = first_horizontal_line4_y1 + (int) (15 * MULTIPLE / 2);
    protected final String first_text8 = "信";
    protected final int first_text9_size = first_text6_size;
    protected final int first_text9_x = first_text6_x;
    protected final int first_text9_y = first_horizontal_line4_y1 + (int) (15 * MULTIPLE / 2 + first_text8_size);
    protected final String first_text9 = "息";
    protected final int first_text10_size = 24;
    protected final int first_text10_x = first_vertical_line1_x1 + first_text10_size / 2;
    protected final int first_text10_y = first_horizontal_line4_y1 + first_text10_size;
    protected final int first_text11_size = 24;
    protected final int first_text11_x = page_left + (int) (35 * MULTIPLE);
    protected final int first_text11_y = first_text10_y;
    protected final int first_text12_size = 24;
    protected final int first_text12_x = first_text10_x;
    protected final int first_text12_y = first_text10_y + first_text12_size+5;
    protected final int first_text13_size = 24;
    protected final int first_text13_x = page_left + (int) ((8 * MULTIPLE - first_text13_size) / 2);
    protected final int first_text13_y = first_horizontal_line5_y1 + (int) (13 * MULTIPLE / 2 - 2 * first_text13_size);
    protected final String first_text13 = "寄";
    protected final int first_text14_size = first_text13_size;
    protected final int first_text14_x = first_text13_x;
    protected final int first_text14_y = first_horizontal_line5_y1 + (int) (13 * MULTIPLE / 2 - first_text7_size);
    protected final String first_text14 = "件";
    protected final int first_text15_size = first_text13_size;
    protected final int first_text15_x = first_text13_x;
    protected final int first_text15_y = first_horizontal_line5_y1 + (int) (13 * MULTIPLE / 2);
    protected final String first_text15 = "信";
    protected final int first_text16_size = first_text13_size;
    protected final int first_text16_x = first_text13_x;
    protected final int first_text16_y = first_horizontal_line5_y1 + (int) (13 * MULTIPLE / 2 + first_text8_size);
    protected final String first_text16 = "息";
    protected final int first_text17_size = 16;
    protected final int first_text17_x = first_vertical_line1_x1 + first_text17_size / 2;
    protected final int first_text17_y = first_horizontal_line5_y1 + first_text17_size;
    protected final int first_text18_size = 16;
    protected final int first_text18_x = page_left + (int) (35 * MULTIPLE);
    protected final int first_text18_y = first_text17_y;
    protected final int first_text19_size = 20;
    protected final int first_text19_x = first_text17_x;
    protected final int first_text19_y = first_text18_y + first_text19_size+5;
    protected final int first_text20_size = 24;
    protected final int first_text20_x = first_text13_x;
    protected final int first_text20_y = first_horizontal_line6_y1 + (int) (22 * MULTIPLE / 2 - first_text7_size);
    protected final String first_text20 = "服";
    protected final int first_text21_size = first_text13_size;
    protected final int first_text21_x = first_text13_x;
    protected final int first_text21_y = first_horizontal_line6_y1 + (int) (22 * MULTIPLE / 2);
    protected final String first_text21 = "务";
    protected final int first_text22_size = 16;
    protected final int first_text22_x = first_vertical_line1_x1 + first_text22_size;
    protected final int first_text22_y = first_horizontal_line6_y1 + 6;
    protected final int first_text23_size = first_text22_size;
    protected final int first_text23_x = first_text22_x;
    protected final int first_text23_y = first_text22_y + first_text23_size + 12;
    protected final int first_text24_size = first_text22_size;
    protected final int first_text24_x = first_text22_x;
    protected final int first_text24_y = first_text23_y + first_text23_size + 12;
    protected final int first_text25_size = first_text22_size;
    protected final int first_text25_x = first_text22_x;
    protected final int first_text25_y = first_text24_y + first_text23_size + 12;
    protected final int first_text26_size = first_text22_size;
    protected final int first_text26_x = first_text22_x;
    protected final int first_text26_y = first_text25_y + first_text23_size + 12;
    protected final int first_text27_size = 24;
    protected final int first_text27_x = first_vertical_line3_x1 + first_text27_size;
    protected final int first_text27_y = first_horizontal_line6_y1 + 12;
    protected final String first_text27 = "签收人/签收时间";
    protected final int first_text28_size = 24;
    protected final int first_text28_x = first_vertical_line3_x1 + (int) (20 * MULTIPLE);
    protected final int first_text28_y = first_pager_bottom - 2 * first_text27_size;
    protected final String first_text28 = "月    日";
    protected final int first_barcode1_x = page_left + (int) (10 * MULTIPLE);
    protected final int first_barcode1_y = first_horizontal_line1_y1 + (int) (2 * MULTIPLE);
    protected final int second_horizontal_line1_x1 = page_left;
    //    private final int second_horizontal_line1_y1 = (int) (17* MULTIPLE);
    protected final int second_horizontal_line1_y1 = first_pager_bottom+ 12 + (int) (17 * MULTIPLE);
    protected final int second_horizontal_line1_x2 = page_right;
    protected final int second_horizontal_line1_y2 = second_horizontal_line1_y1;
    protected final int second_horizontal_line2_x1 = page_left;
    protected final int second_horizontal_line2_y1 = (int) (6 * MULTIPLE) + second_horizontal_line1_y1;
    protected final int second_horizontal_line2_x2 = page_right;
    protected final int second_horizontal_line2_y2 = second_horizontal_line2_y1;
    protected final int second_horizontal_line3_x1 = page_left;
    protected final int second_horizontal_line3_y1 = (int) (15 * MULTIPLE) + second_horizontal_line2_y1;
    protected final int second_horizontal_line3_x2 = page_right;
    protected final int second_horizontal_line3_y2 = second_horizontal_line3_y1;
    protected final int second_horizontal_line4_x1 = page_left;
    protected final int second_horizontal_line4_y1 = (int) (6 * MULTIPLE) + second_horizontal_line3_y1;
    protected final int second_horizontal_line4_x2 = page_right;
    protected final int second_horizontal_line4_y2 = second_horizontal_line4_y1;
    protected final int second_vertical_line1_x1 = page_left + (int) (35 * MULTIPLE);
    protected final int second_vertical_line1_y1 = second_horizontal_line1_y1;
    protected final int second_vertical_line1_x2 = second_vertical_line1_x1;
    protected final int second_vertical_line1_y2 = second_horizontal_line3_y1;
    protected final int second_vertical_line2_x1 = page_left + (int) (14 * MULTIPLE);
    protected final int second_vertical_line2_y1 = second_horizontal_line3_y1;
    protected final int second_vertical_line2_x2 = second_vertical_line2_x1;
    protected final int second_vertical_line2_y2 = second_pager_bottom;
    protected final int second_vertical_line3_x1 = second_vertical_line2_x1 + (int) (14 * MULTIPLE);
    protected final int second_vertical_line3_y1 = second_horizontal_line3_y1;
    protected final int second_vertical_line3_x2 = second_vertical_line3_x1;
    protected final int second_vertical_line3_y2 = second_pager_bottom;
    protected final int second_vertical_line4_x1 = second_vertical_line3_x1 + (int) (14 * MULTIPLE);
    protected final int second_vertical_line4_y1 = second_horizontal_line3_y1;
    protected final int second_vertical_line4_x2 = second_vertical_line4_x1;
    protected final int second_vertical_line4_y2 = second_pager_bottom;
    protected final int second_vertical_line5_x1 = second_vertical_line4_x1 + (int) (14 * MULTIPLE);
    protected final int second_vertical_line5_y1 = second_horizontal_line3_y1;
    protected final int second_vertical_line5_x2 = second_vertical_line5_x1;
    protected final int second_vertical_line5_y2 = second_pager_bottom;
    protected final int second_text1_size = 16;
    protected final int second_text1_x = page_left + 8;
    protected final int second_text1_y = second_horizontal_line1_y1 + (int) ((6 * MULTIPLE - second_text1_size) / 2);
    protected final int second_text2_size = 16;
    protected final int second_text2_x = second_vertical_line1_x1 + 8;
    protected final int second_text2_y = second_text1_y;
    protected final int second_text3_size = 16;
    protected final int second_text3_x = second_text1_x;
    protected final int second_text3_y = second_horizontal_line2_y1 + 12;
    protected final String second_text3 = "收件方信息";
    protected final int second_text4_size = 16;
    protected final int second_text4_x = second_text1_x;
    protected final int second_text4_y = second_text3_y + second_text4_size + 8;
    protected final int second_text5_size = 16;
    protected final int second_text5_x = second_text1_x;
    protected final int second_text5_y = second_text4_y + second_text4_size + 8;
    protected final int second_text6_size = 16;
    protected final int second_text6_x = second_text2_x;
    protected final int second_text6_y = second_horizontal_line2_y1 + 12;
    protected final String second_text6 = "寄件方信息";
    protected final int second_text7_size = 16;
    protected final int second_text7_x = second_text6_x;
    protected final int second_text7_y = second_text6_y + second_text4_size + 8;
    protected final int second_text8_size = 16;
    protected final int second_text8_x = second_text6_x;
    protected final int second_text8_y = second_text7_y + second_text4_size + 8;
    protected final int second_text9_size = 16;
    protected final int second_text9_x = page_left + 20;
    protected final int second_text9_y = second_horizontal_line3_y1 + (int) ((6 * MULTIPLE - second_text9_size) / 2);
    protected final String second_text9 = "内容品名";
    protected final int second_text10_size = 16;
    protected final int second_text10_x = second_vertical_line2_x1 + 10;
    protected final int second_text10_y = second_text9_y;
    protected final String second_text10 = "计费重量(kg)";
    protected final int second_text11_size = 16;
    protected final int second_text11_x = second_vertical_line3_x1 + 10;
    protected final int second_text11_y = second_text9_y;
    protected final String second_text11 = "声明价值(￥)";
    protected final int second_text12_size = 16;
    protected final int second_text12_x = second_vertical_line4_x1 + 10;
    protected final int second_text12_y = second_text9_y;
    protected final String second_text12 = "代收货款(￥)";
    protected final int second_text13_size = 16;
    protected final int second_text13_x = second_vertical_line5_x1 + 10;
    protected final int second_text13_y = second_text9_y;
    protected final String second_text13 = "到付金额(￥)";
    protected final int second_text14_size = 16;
    //    protected final int second_text14_x = page_left+ 8;
    protected final int second_text14_y = second_horizontal_line4_y1 + (int) ((6 * MULTIPLE - second_text9_size) / 2);
    protected final int second_text15_size = 16;
    //    protected final int second_text15_x = second_vertical_line2_x1+ 22;
    protected final int second_text15_y = second_text14_y;
    protected final int second_text16_size = 16;
    //    protected final int second_text16_x = second_vertical_line3_x1+ 22;
    protected final int second_text16_y = second_text14_y;
    protected final int second_text17_size = 16;
    //    protected final int second_text17_x = second_vertical_line4_x1+ 22;
    protected final int second_text17_y = second_text14_y;
    protected final int second_text18_size = 16;
    //    protected final int second_text18_x = second_vertical_line5_x1+ 22;
    protected final int second_text18_y = second_text14_y;
    protected final int third_horizontal_line1_x1 = page_left;
    protected final int third_horizontal_line1_y1 = second_pager_bottom + (int) (13 * MULTIPLE);
    protected final int third_horizontal_line1_x2 = page_right;
    protected final int third_horizontal_line1_y2 = third_horizontal_line1_y1;
    protected final int third_horizontal_line2_x1 = page_left;
    protected final int third_horizontal_line2_y1 = (int) (15 * MULTIPLE) + third_horizontal_line1_y1;
    protected final int third_horizontal_line2_x2 = page_right;
    protected final int third_horizontal_line2_y2 = third_horizontal_line2_y1;
    protected final int third_horizontal_line3_x1 = page_left;
    protected final int third_horizontal_line3_y1 = (int) (6 * MULTIPLE) + third_horizontal_line2_y1;
    protected final int third_horizontal_line3_x2 = page_right;
    protected final int third_horizontal_line3_y2 = third_horizontal_line3_y1;
    protected final int third_horizontal_line4_x1 = page_left;
    protected final int third_horizontal_line4_y1 = (int) (6 * MULTIPLE) + third_horizontal_line3_y1;
    protected final int third_horizontal_line4_x2 = page_right;
    protected final int third_horizontal_line4_y2 = third_horizontal_line4_y1;
    protected final int third_vertical_line1_x1 = page_left + (int) (35 * MULTIPLE);
    protected final int third_vertical_line1_y1 = second_pager_bottom;
    protected final int third_vertical_line1_x2 = third_vertical_line1_x1;
    protected final int third_vertical_line1_y2 = third_horizontal_line2_y1;
    protected final int third_vertical_line2_x1 = page_left + (int) (14 * MULTIPLE);
    protected final int third_vertical_line2_y1 = third_horizontal_line2_y1;
    protected final int third_vertical_line2_x2 = third_vertical_line2_x1;
    protected final int third_vertical_line2_y2 = third_horizontal_line4_y1;
    protected final int third_vertical_line3_x1 = third_vertical_line2_x1 + (int) (14 * MULTIPLE);
    protected final int third_vertical_line3_y1 = third_horizontal_line2_y1;
    protected final int third_vertical_line3_x2 = third_vertical_line3_x1;
    protected final int third_vertical_line3_y2 = third_horizontal_line4_y1;
    protected final int third_vertical_line4_x1 = third_vertical_line3_x1 + (int) (14 * MULTIPLE);
    protected final int third_vertical_line4_y1 = third_horizontal_line2_y1;
    protected final int third_vertical_line4_x2 = third_vertical_line4_x1;
    protected final int third_vertical_line4_y2 = third_horizontal_line4_y1;
    protected final int third_vertical_line5_x1 = third_vertical_line4_x1 + (int) (14 * MULTIPLE);
    protected final int third_vertical_line5_y1 = third_horizontal_line2_y1;
    protected final int third_vertical_line5_x2 = third_vertical_line5_x1;
    protected final int third_vertical_line5_y2 = third_horizontal_line4_y1;
    protected final int third_vertical_line6_x1 = page_left + (int) (35 * MULTIPLE);
    protected final int third_vertical_line6_y1 = third_horizontal_line4_y1;
    protected final int third_vertical_line6_x2 = third_vertical_line6_x1;
    protected final int third_vertical_line6_y2 = (int) (200 * MULTIPLE);
    protected final int third_text1_size = 20;
    protected final int third_text1_x = page_left + 40;
    protected final int third_text1_y = third_horizontal_line1_y1 - third_text1_size - 10;
    protected final int third_text2_size = 16;
    protected final int third_text2_x = page_left + 8;
    protected final int third_text2_y = third_horizontal_line1_y1 + 12;
    protected final String third_text2 = "收件方信息";
    protected final int third_text3_size = 16;
    protected final int third_text3_x = third_text2_x;
    protected final int third_text3_y = third_text2_y + third_text3_size + 8;
    protected final int third_text4_size = 16;
    protected final int third_text4_x = third_text2_x;
    protected final int third_text4_y = third_text3_y + third_text4_size + 8;
    protected final int third_text5_size = 16;
    protected final int third_text5_x = third_vertical_line1_x1 + 8;
    protected final int third_text5_y = third_horizontal_line1_y1 + 12;
    protected final String third_text5 = "寄件方信息";
    protected final int third_text6_size = 16;
    protected final int third_text6_x = third_text5_x;
    protected final int third_text6_y = third_text5_y + third_text4_size + 8;
    protected final int third_text7_size = 16;
    protected final int third_text7_x = third_text5_x;
    protected final int third_text7_y = third_text6_y + third_text4_size + 8;
    protected final int third_text9_size = 16;
    protected final int third_text9_x = page_left + 20;
    protected final int third_text9_y = third_horizontal_line2_y1 + (int) ((6 * MULTIPLE - third_text9_size) / 2);
    protected final String third_text9 = "内容品名";
    protected final int third_text10_size = 16;
    protected final int third_text10_x = third_vertical_line2_x1 + 10;
    protected final int third_text10_y = third_text9_y;
    protected final String third_text10 = "计费重量(kg)";
    protected final int third_text11_size = 16;
    protected final int third_text11_x = third_vertical_line3_x1 + 10;
    protected final int third_text11_y = third_text9_y;
    protected final String third_text11 = "声明价值(￥)";
    protected final int third_text12_size = 16;
    protected final int third_text12_x = third_vertical_line4_x1 + 10;
    protected final int third_text12_y = third_text9_y;
    protected final String third_text12 = "代收货款(￥)";
    protected final int third_text13_size = 16;
    protected final int third_text13_x = third_vertical_line5_x1 + 10;
    protected final int third_text13_y = third_text9_y;
    protected final int third_text14_size = 16;
    //    protected final int third_text14_x = page_left+ 8;
    protected final int third_text14_y = third_horizontal_line3_y1 + (int) ((6 * MULTIPLE - third_text9_size) / 2);
    protected final int third_text15_size = 16;
    //    protected final int third_text15_x = third_vertical_line2_x1+ 22;
    protected final int third_text15_y = third_text14_y;
    protected final int third_text16_size = 16;
    //    protected final int third_text16_x = third_vertical_line3_x1+ 22;
    protected final int third_text16_y = third_text14_y;
    protected final int third_text17_size = 16;
    //    protected final int third_text17_x = third_vertical_line4_x1+ 22;
    protected final int third_text17_y = third_text14_y;
    protected final int third_text18_size = 16;
    //    protected final int third_text18_x = third_vertical_line5_x1+ 22;
    protected final int third_text18_y = third_text14_y;
    protected final int third_text19_size = 16;
    protected final int third_text19_x = page_left + 8;
    protected final int third_text19_y = third_horizontal_line4_y1 + 16;
    protected final String third_text19 = "打印时间";
    protected final int third_text20_size = 16;
    protected final int third_text20_x = page_left + 8;
    protected final int third_text20_y = third_text19_y + third_text19_size + 8;
    protected final int third_text21_size = 16;
    protected final int third_text21_x = third_vertical_line6_x1 + 8;
    protected final int third_text21_y = third_horizontal_line4_y1 + 16;
    protected final String third_text21 = "快递员签名/签名时间";
    protected final int third_text22_size = 16;
    protected final int third_text22_x = third_vertical_line6_x1 + 132;
    protected final int third_text22_y = page_height - 2 * third_text22_size - 40;
    protected final String third_text22 = "       月       日";
    protected final int third_barcode1_x = page_left + (int) (4 * MULTIPLE);
    protected final int third_barcode1_y = second_pager_bottom + (int) (2 * MULTIPLE);
    protected String first_text1 = "代收货款：￥";
    protected String first_text3 = "哈尔滨转齐齐哈尔";//居中打印
    protected String first_text4 = "哈尔滨转齐齐哈尔";//居中打印
    protected String first_text5 = "2015-12-24";//居中打印
    protected String first_text10 = "收件人：";
    protected String first_text11 = "手机/电话：";
    protected String first_text12 = "地址：";//换行
    protected String first_text17 = "寄件人：";
    protected String first_text18 = "手机/电话：";
    protected String first_text19 = "地址：";//换行
    protected String first_text22 = "内容品名：";
    protected String first_text23 = "计费重量：";
    protected String first_text24 = "声明价值：￥";
    protected String first_text25 = "代收货款：￥";
    protected String first_text26 = "到付金额：￥";
    protected String second_text1 = "运单号：" ;
    protected String second_text2 = "订单号：";
    protected String second_text4 = "小风采 15021498306";//姓名+电话
    protected String second_text5 = "收件人地址收件人地址收件人地址收件人地址收件人地址";//需要换行打印
    protected String second_text7 = "李晓晓 15021498306";//姓名+电话
    protected String second_text8 = "寄件人地址寄件人地址寄件人地址寄件人地址寄件人地址";//需要换行打印
    protected String second_text14 = "衣服";//居中
    protected String second_text15 = "";//居中
    protected String second_text16 = "2999.95";//居中
    protected String second_text17 = "2999.95";//居中
    protected String second_text18 = "2999.95";//居中
    protected String third_text3 = "小风采 15021498306";//姓名+电话
    protected String third_text4 = "收件人地址收件人地址收件人地址收件人地址收件人地址";//需要换行打印
    protected String third_text6 = "李晓晓 15021498306";//姓名+电话
    protected String third_text7 = "寄件人地址寄件人地址寄件人地址寄件人地址寄件人地址";//需要换行打印
    protected String third_text13 = "到付金额(￥)";
    protected String third_text14 = "衣服";//居中
    protected String third_text15 = "";//居中
    protected String third_text16 = "2999.95";//居中
    protected String third_text17 = "2999.95";//居中
    protected String third_text18 = "2999.95";//居中
    protected String third_text20 = UtilityTime.getDateTimeByMillisecond3(new Date(), "yyyy-MM-dd HH:mm:ss");

    protected void initData(Order order, String num) {
        first_text3 = order.getArticleInfo();
        first_text4 = order.getSenderAddress();
//        long time =Long.parseLong(electronPagerVO.getPrintDate());
        first_text5 = order.getTime();//yyyy-MM-dd
        third_text20 = order.getTime();//"yyyy-MM-dd HH:mm
        first_text10 = "收件人：" + order.getName();
        first_text11 = "手机/电话：" + order.getPhone();
        third_text3 = second_text4 = order.getName() + "  " + order.getPhone();

//        first_text3 = electronPagerVO.getSendSite();
        first_text12 = "地址：" + order.getAddress();
        third_text4 = second_text5 = order.getAddress();

        first_text17 = "寄件人：" + order.getSenderName();
        first_text18 = "手机/电话：" + order.getSenderPhone();
        third_text6 = second_text7 = order.getSenderName() + "  " + order.getSenderPhone();

        first_text19 = "地址：" + order.getSenderAddress();
        third_text7 = second_text8 = order.getSenderAddress();

        //商品内容,暂时没有数据
        first_text22 = "内容品名：";
        second_text14 = null;
        third_text14 = null;

        first_text23 = "计费重量：0.00 (kg)";
        third_text15 = second_text15 = "0.00";

        //声明价值，暂时没有数据
        first_text24 = "声明价值：";
        second_text16 = null;
        third_text16 = null;
        //到付金额，暂时没有数据
        String toPay = order.getReal_pay();
        first_text26 = "到付金额：￥" + toPay;
        second_text18 = toPay;
        third_text18 = toPay;

        if (order.getNeed_pay() != null) {
            first_text1 = "代收货款：￥" + order.getNeed_pay();
            first_text25 = "代收金额：￥" + order.getNeed_pay();
            third_text17 = second_text17 = order.getNeed_pay();
        } else {
            first_text1 = "标准快递";
            first_text25 = "代收金额：￥";
            second_text17 = null;
            third_text17 = null;
        }
        String orderCode = order.getId();
        if (orderCode != null && orderCode.length() > 0 && orderCode.length() != 36) {
            second_text2 = "订单号：" + orderCode;
        } else {
            second_text2 = "订单号：";
        }
    }


     public abstract void connect(BluetoothDevice device, ConnectedCallBack callback);
     public abstract void connect(BluetoothDevice device);
     public abstract void disConnect();
     public abstract boolean isConnected();
     public abstract void printZTContnet(String num, String bigChar, Order order);
     public abstract void printStoContent(String num, String bigChar, Order order);
     public abstract String getPrinterStatus();

    protected String addSpace2Barcode2(String barcode) {
        StringBuilder sb = new StringBuilder(barcode);
        sb.insert(9, " ");
        sb.insert(6, " ");
        sb.insert(3, " ");
        return sb.toString();
    }

    //横向居中
    protected int calculateX(int startX, int endX, String text, int height) {
        int re = 0;
        if (text != null && text.length() > 0) {
            int x = text.length() * height;
            re = (endX - startX - x) / 2 + startX;
        }
        if (re <= 0) {
            re = 10;
        }
        return re;
    }

    public interface ConnectedCallBack{
        void connectedCallback();
    }
}
