package com.kuaibao.skuaidi.bluetooth.printer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.example.iprtlabelprinterlibrary.BasePrinter;
import com.example.iprtlabelprinterlibrary.BasePrinter.PRotate;
import com.example.iprtlabelprinterlibrary.IPRTPrinter;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.Utility;

/**
 * Created by kuaibao on 2016/7/19.
 *
 * 万印和打印机
 */

public class BTPrinter extends PrinterBase{
    private static IPRTPrinter iPrinter;
    private boolean isConnected = false;
    private static BTPrinter btPrinter;

    private BTPrinter(IPRTPrinter iPrinter){
        this.iPrinter = iPrinter;
    }

    public static BTPrinter getInstance(Activity activity, Handler handler){
        if(Utility.isEmpty(btPrinter)){
            iPrinter = new IPRTPrinter(activity, handler);
            btPrinter = new BTPrinter(iPrinter);
        }
        return btPrinter;
    }
    @Override
    public void connect(final BluetoothDevice device, final ConnectedCallBack callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(!Utility.isEmpty(iPrinter)) {
                    iPrinter.connect(device.getAddress());
                    isConnected = true;
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
        if(!Utility.isEmpty(iPrinter)) {
            iPrinter.connect(device.getAddress());
        }
    }

    @Override
    public void disConnect() {
        if(!Utility.isEmpty(iPrinter)) {
           iPrinter.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        if(!Utility.isEmpty(iPrinter) && !isConnected){
            try {
                isConnected = iPrinter.isConnected();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return isConnected;
    }

    @Override
    public void printZTContnet(String num, String bigChar, Order order) {
        String charge = order.getCollection_amount();
        //设置纸长宽
        iPrinter.pageSetup(568, 1632);
        //第一个表格线
        //drawLine( 线宽，起始X，起始Y，结尾X，结尾Y);
        iPrinter.drawLine(-1, 12, 120, 568, 120);
        iPrinter.drawLine(-1, 12, 280, 568, 280);
        iPrinter.drawLine(-1, 12, 360, 568, 360);
        iPrinter.drawLine(-1, 12, 424, 568, 424);
        iPrinter.drawLine(-1, 12, 544, 568, 544);
        iPrinter.drawLine(-1, 12, 648, 568, 648);
        iPrinter.drawLine(0, 284, 360, 284, 424);
        iPrinter.drawLine(0, 56, 424, 56, 824);
        iPrinter.drawLine(0, 284, 648, 284, 824);

        //第一行内容
        iPrinter.drawText(1,32,0,260,20,"代收货款：￥"+charge);
        iPrinter.inverse(260,20,260+(6*32+charge.length()*20), 20, 32);

        //打印二维码
        iPrinter.drawBarCode(3,1,96,80,100,num);
        iPrinter.drawText(0,56,0,72,190,addSpace2Barcode2(num));

        //表格一第一行
        int x = calculateX(page_left, page_right, bigChar, 56);
        iPrinter.drawText(1,56,0,x,255, bigChar);
        x = calculateX(page_left, first_vertical_line2_x1, bigChar, first_text4_size);
        iPrinter.drawText(0,32,0,x,350,bigChar);
        String time = order.getTime();
        iPrinter.drawText(0,32,0,332,350, time.substring(0, time.indexOf(" ")));
        //第二行
        iPrinter.drawText(1,24,0,16,400,"收");
        iPrinter.drawText(1,24,0,16,424,"件");
        iPrinter.drawText(1,24,0,16,448,"信");
        iPrinter.drawText(1,24,0,16,472,"息");

        iPrinter.drawText(0,24,0,64,400,"收件人："+order.getName()+"　手机/电话："+order.getPhone());
        printChangeLineText(iPrinter, "地址："+order.getAddress(), 64, 430, 24, 19, 24);

        //第三行
        iPrinter.drawText(1,24,0,16,515,"寄");
        iPrinter.drawText(1,24,0,16,535,"件");
        iPrinter.drawText(1,24,0,16,559,"信");
        iPrinter.drawText(1,24,0,16,583,"息");
        iPrinter.drawText(0,20,0,64,520,"寄件人："+order.getSenderName()+"　　手机/电话："+order.getSenderPhone());
        printChangeLineText(iPrinter, "地址："+order.getSenderAddress(), 64, 548, 20, 20, 20);


        //第四行
        iPrinter.drawText(1,24,0,16,676,"服");
        iPrinter.drawText(1,24,0,16,700,"务");

        iPrinter.drawText(0,55,0,64,634,"内容品名: "+ order.getArticleInfo());
        String weight = TextUtils.isEmpty(order.getCharging_weight()) ?"":order.getCharging_weight()+"kg";
        iPrinter.drawText(0,55,0,64,666,"计费重量: "+weight);
        iPrinter.drawText(0,55,0,64,698,"声明价值: ￥");
        iPrinter.drawText(0,55,0,64,730,"代收货款: ￥"+charge);
        iPrinter.drawText(0,55,0,64,762,"到付金额: ￥");
        iPrinter.drawText(0,24,0,284,620,"签收人/签收时间");
        iPrinter.drawText(0,24,0,442,760,"月   日");



        //第二个表格线
        iPrinter.drawLine(-1, 12, 966, 568, 966);
        iPrinter.drawLine(-1, 12, 1014, 568, 1014);
        iPrinter.drawLine(-1, 12, 1134, 568, 1134);
        iPrinter.drawLine(-1, 12, 1182, 568, 1182);
//        iPrinter.drawLine(-1, 12, 1200, 568, 1200);
        iPrinter.drawLine(0, 284, 966, 284, 1134);
        iPrinter.drawLine(0, 112, 1134, 112, 1230);
        iPrinter.drawLine(0, 224, 1134, 224, 1230);
        iPrinter.drawLine(0, 344, 1134, 344, 1230);
        iPrinter.drawLine(0, 456, 1134, 456, 1230);
//        iPrinter.drawLine(0, 568, 1104, 568, 1200);

        iPrinter.drawLine(-1, 12, 1334, 568, 1334);
        iPrinter.drawLine(-1, 12, 1454, 568, 1454);
        iPrinter.drawLine(-1, 12, 1502, 568, 1502);
        iPrinter.drawLine(-1, 12, 1550, 568, 1550);
        iPrinter.drawLine(-1, 12, 1630, 568, 1630);
        iPrinter.drawLine(0, 284, 1230, 284, 1454);
        iPrinter.drawLine(0, 112, 1454, 112, 1550);

        iPrinter.drawLine(0, 224, 1454, 224, 1550);
        iPrinter.drawLine(0, 344, 1454, 344, 1550);
        iPrinter.drawLine(0, 456, 1454, 456, 1550);
        iPrinter.drawLine(0, 568, 1454, 568, 1550);
//        iPrinter.drawLine(0, 284, 1520, 284, 1600);


        //表格二第一行
        iPrinter.drawText(0,55,0,16,952,"运单号："+num);
        iPrinter.drawText(0,55,0,308,952,"订单号："+order.getId());

        //第二行
        iPrinter.drawText(0,55,0,16,1000,"收件方信息：");
        iPrinter.drawText(0,55,0,16,1024,order.getName()+"  "+order.getPhone());
        printChangeLineText(iPrinter, "地址:"+order.getAddress(), 16, 1048, 20, 13, 55);
        iPrinter.drawText(0,55,0,308,1000,"寄件方信息：");
        iPrinter.drawText(0,55,0,308,1024,order.getSenderName()+"  "+order.getSenderPhone());
        printChangeLineText(iPrinter, "地址:"+order.getSenderAddress(), 308, 1048, 20, 13, 55);
        //第三行

        iPrinter.drawText(0,55,0,16,1120,"内容品名：");
        iPrinter.drawText(0,55,0,120,1120,"计费重量：");
        iPrinter.drawText(0,55,0,240,1120,"声明价值：");
        iPrinter.drawText(0,55,0,352,1120,"代收货款：");
        iPrinter.drawText(0,55,0,464,1120,"到付金额：");

        //第四行
        iPrinter.drawText(0,55,0,16,1158,order.getArticleInfo());
        iPrinter.drawText(0,55,0,152,1158,order.getCharging_weight());
        iPrinter.drawText(0,55,0,248,1158,"");
        iPrinter.drawText(0,55,0,368,1158,charge);
        iPrinter.drawText(0,55,0,480,1158,"");

        //第五行条形码
        iPrinter.drawBarCode(1,1,64,20,1210, num);
        iPrinter.drawText(0,55,0,16,1276,addSpace2Barcode2(num));

        //第六行
        iPrinter.drawText(0,55,0,16,1320,"收件方信息");
        iPrinter.drawText(0,55,0,16,1344,order.getName()+"  "+order.getPhone());
        printChangeLineText(iPrinter, "地址:"+order.getAddress(), 16, 1368, 20, 13, 55);
        iPrinter.drawText(0,55,0,308,1320,"寄件方信息：");
        iPrinter.drawText(0,55,0,308,1344,order.getSenderName()+"  "+order.getSenderPhone());
        printChangeLineText(iPrinter, "地址:"+order.getSenderAddress(), 308, 1368, 20, 13, 55);

        //第七行
        iPrinter.drawText(0,55,0,16,1440,"内容品名");
        iPrinter.drawText(0,55,0,120,1440,"计费重量");
        iPrinter.drawText(0,55,0,240,1440,"声明价值");
        iPrinter.drawText(0,55,0,352,1440,"代收货款");
        iPrinter.drawText(0,55,0,464,1440,"到付金额");

        //第八行
        iPrinter.drawText(0,55,0,16,1480,order.getArticleInfo());
        iPrinter.drawText(0,55,0,152,1488,order.getCharging_weight());
        iPrinter.drawText(0,55,0,248,1488,"");
        iPrinter.drawText(0,55,0,368,1488,charge);
        iPrinter.drawText(0,55,0,480,1488,"");

        //第九行
        iPrinter.drawText(0,55,0,16,1536,"打印时间");
        iPrinter.drawText(0,55,0,316,1536,"签收人/签收时间");
        iPrinter.drawText(0,55,0,16,1568,"");
        iPrinter.drawText(0,55,0,484,1568,"月    日");

        iPrinter.print(0, 0);
    }

    @Override
    public void printStoContent(String num, String bigChar, Order order) {
        iPrinter.pageSetup(568, 600);

        iPrinter.drawText(72, 238, "360024139575", 28, 0, 0, 0, 0, PRotate.Rotate_0);

        //表格一第一行

        iPrinter.drawText(72, 292, "测试", 36, 0, 0, 0, 0, PRotate.Rotate_0);
        iPrinter.drawText(13, 376, "测试", 28, 0, 0, 0, 0, PRotate.Rotate_0);
        iPrinter.drawText(332, 376, "测试", 28, 0, 0, 0, 0, PRotate.Rotate_0);

        //第二行
        iPrinter.drawText(16, 436, "测试", 20, 1, 0, 0, 0, BasePrinter.PRotate.Rotate_0);
        iPrinter.drawText(16, 460, "测试", 20, 1, 0, 0, 0, PRotate.Rotate_0);
        iPrinter.drawText(16, 484, "测试", 20, 1, 0, 0, 0, PRotate.Rotate_0);
        iPrinter.drawText(16, 508, "测试", 20, 1, 0, 0, 0, PRotate.Rotate_0);
        iPrinter.print(0, 2);
    }

    /**
     * 方法用于打印文字太多换行打印
     *
     * @param iPrinter
     * @param text
     * @param x
     * @param y
     * @param height       字体实际高度
     * @param num          换行字数
     * @param size         对应打印机打印文字高度参数
     */
    protected void printChangeLineText(IPRTPrinter iPrinter, String text, int x, int y, int height, int num, int size) {
//        if (height == 16) {
//            size = 1;
//        } else if (height == 24) {
//            size = 2;
//        }
        if (text != null && num > 0 && text.length() > num) {
            StringBuilder sb = new StringBuilder(text);
            String subs = sb.substring(0, num);
            //打印
            iPrinter.drawText(0, size, 0, x, y, subs);
            sb.delete(0, num);
            String remainming = sb.toString();
            if (remainming.length() > num) {
                //x,y设置
                y += height;
                printChangeLineText(iPrinter, remainming, x, y, height, num, size);
            } else {
                //打印
                y += height;
                iPrinter.drawText(0,size,0,x,y,remainming);
            }
        } else {
            iPrinter.drawText(0, size, 0, x, y, text);
        }
    }

    @Override
    public String getPrinterStatus() {
        int status = -1;
        String result = "";
        if(!Utility.isEmpty(iPrinter)){
            status = iPrinter.getPrinterStatus();
        }
        switch (status){
            case 0://打印状态正常
                result = "OK";
                break;
            case 1://打印机通信异常
                result = "Print Write Error";
                break;
            case 2://缺纸
                result = "NoPaper";
                break;
            case 4://打印机开盖
                result = "CoverOpened";
                break;
            default:
                break;
        }
        return result;
    }
}
