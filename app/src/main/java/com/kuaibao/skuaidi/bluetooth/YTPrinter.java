package com.kuaibao.skuaidi.bluetooth;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import printpp.printpp_zt.BluetoothPort;


/**
 * Created by vant on 15/10/28.
 */
public class YTPrinter {

    private static BluetoothPort Port = null;
    private Bitmap Lable = null;
    private int SleepTime = 2;
    private int PortTimeOut = 3000;
    public YTPrinter(){
        if(Port == null){
            Port = new BluetoothPort();
        }
    }

    public void connect(String address){
        if(Port.isOpen)Port.close();
        Port.open(address,PortTimeOut);//3000ms超时
        Port.flushReadBuffer();
    }
    //参数：
    //address 蓝牙设备的Mac地址

    //2.关闭与蓝牙打印机的连接。
    public void disconnect() {
        if(Port.isOpen)Port.close();
    }

    //3.判断是否连接
    public boolean isConnected(){
        return Port.isOpen;
    }
    //返回值：true（已连接） | false （未连接）

    public Bitmap GetBitmap()
    {
        return Lable;
    }

    //4.页模式下打印
    public  void print(int horizontal,int skip){
        if(Lable == null)return;
        int DotWidth = Lable.getWidth();
        if(DotWidth > 576)DotWidth = 576;
        int DotHeight = Lable.getHeight();
        int ByteWidth = (Lable.getWidth() - 1) / 8 + 1;
        if(ByteWidth > 72)ByteWidth = 72;
        int ByteHeight = DotHeight;
        byte[] Cmd = new byte[4];
        Cmd[0] = 0x1F;
        Cmd[1] = 0x11;
        Cmd[2] = 0x48;
        Cmd[3] = 0x00;


        if(Port.isOpen)
        {
            if(horizontal != 0)
                for (int j = 0; j < DotHeight; j++) {
                    byte[] Data = new byte[72];
                    for (int i = 0; i < DotWidth; i++) {
                        if (Lable.getPixel(DotWidth - 1 - i, DotHeight - 1 - j) == Color.BLACK) //Lable.setPixel(DotWidth - 1 - i, DotHeight - 1 - j,Color.BLACK);
                        {
                            Data[i / 8] |= (0x01 << (i % 8));
                        }
                    }
                    //测试
                    Port.write(Cmd, 0, 4);
                    Port.write(Data, 0, 72);
                    //Port.WriteM30(Data);
                }
            else
            {
                for(int j = 0; j < DotHeight; j++){
                    byte[] Data = new byte[72];
                    for(int i = 0; i < DotWidth; i++){
                        if(Lable.getPixel(i,j) == Color.BLACK) //Lable.setPixel(DotWidth - 1 - i, DotHeight - 1 - j,Color.BLACK);
                        {
                            Data[i / 8] |= (0x01 << (i % 8));
                        }
                    }
                    //测试
                    Port.write(Cmd,0,4);
                    Port.write(Data,0,72);
                }
            }
            if(skip == 1) {
                Cmd[0] = 0x0E;
                Port.write(Cmd,0,1);
            }
        }

    }
    //参数：
    //horizontal:
    //        0:正常打印，不旋转；
    //        1：整个页面顺时针旋转180°后，再打印
    //skip：
    //        0：打印结束后不定位，直接停止；
    //        1：打印结束后定位到标签分割线，如果无缝隙，最大进纸30mm后停止

    //5.设置打印纸张大小（打印区域）的大小
    public void pageSetup(int pageWidth, int pageHeight){
        if(pageWidth > 560)pageWidth = 576;
        Lable = Bitmap.createBitmap( pageWidth, pageHeight,Config.ARGB_8888);
    }
    //参数：
    //pageWidth 打印区域宽度
    //pageHeight 打印区域高度

    //6.打印的边框
    public void drawBox(int lineWidth, int top_left_x,
                                 int top_left_y, int bottom_right_x, int bottom_right_y){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        //PathEffect effect = new DashPathEffect(new float[] {2,2,2,2}, 1);
        //paint.setPathEffect(effect);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        canvas.drawRect(top_left_x,top_left_y,bottom_right_x,bottom_right_y,paint);

    }
    //参数：
    //lineWidth 边框线条宽度
    //top_left_x 矩形框左上角x坐标
    //top_left_y 矩形框左上角y坐标
    //bottom_right_x 矩形框右下角x坐标
    //bottom_right_y 矩形框右下角y坐标

    //7.打印线条
    public void drawLine(int lineWidth, int start_x, int start_y,int end_x, int end_y,boolean fullline){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        PathEffect effect;
        if(!fullline) {
            effect = new DashPathEffect(new float[] {3,3,3,3}, 1);
            paint.setPathEffect(effect);
        }
        canvas.drawLine(start_x, start_y, end_x, end_y, paint);
    }
    //参数：
    //lineWidth 线条宽度
    //start_x 线条起始点x坐标
    //start_y 线条起始点y坐标
    //end_x 线条结束点x坐标
    //end_y 线条结束点y坐标
    //fullline  true:实线  false: 虚线

    //8.页模式下打印文本框
    public void drawText(int text_x, int text_y, String text,int fontSize, int rotate,int bold, boolean reverse, boolean underline){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.SANS_SERIF);
        //textPaint.setTypeface(Typeface.MONOSPACE);
        switch(fontSize)
        {
            case 1:
                textPaint.setTextSize(16);
                text_y -= 2;
                break;
            case 2:
                textPaint.setTextSize(24);
                text_y -= 3;
                break;
            case 3:
                textPaint.setTextSize(32);
                text_y -= 4;
                break;
            case 4:
                textPaint.setTextSize(48);
                text_y -= 6;
                break;
            case 5:
                textPaint.setTextSize(64);
                text_y -= 8;
                break;
            case 6:
                textPaint.setTextSize(96);
                text_y -= 12;
                break;
            case 7:
                textPaint.setTextSize(128);
                text_y -= 16;
                break;
            default:
                textPaint.setTextSize(24);
                text_y -= 3;
                break;
        }
        switch(rotate)
        {
            case 1:
                canvas.rotate(90);
                break;
            case 2:
                canvas.rotate(180);
                break;
            case 3:
                canvas.rotate(270);
                break;
            default:
                canvas.rotate(0);
                break;
        }
        if(bold == 0) textPaint.setFakeBoldText(false);
        else textPaint.setFakeBoldText(true);

        textPaint.setUnderlineText(underline);

        if(reverse){
            Rect rect = new Rect();
            textPaint.getTextBounds(text,0,text.length(),rect);
            rect.top += textPaint.getTextSize();
            rect.bottom += textPaint.getTextSize();
            paint.setColor(Color.BLACK);
            paint.setStyle(Style.FILL);
            rect.left += text_x;
            rect.right += text_x;
            rect.top += text_y;
            rect.bottom += text_y;
            canvas.drawRect(rect, paint);
            textPaint.setColor(Color.WHITE);
            canvas.drawText(text,text_x,text_y+textPaint.getTextSize(),textPaint);
        }
        else
        {
            paint.setStyle(Style.STROKE);
            textPaint.setColor(Color.BLACK);
            canvas.drawText(text,text_x,text_y+textPaint.getTextSize(),textPaint);
        }
    }
    //参数：
    //text_x 起始横坐标
    //text_y 起始纵坐标
    //text  打印的文本内容
    //fontSize 字体大小 :
    //        1：16点阵；
    //        2：24点阵；
    //        3：32点阵；
    //        4：24点阵放大一倍；
    //        5：32点阵放大一倍
    //        6：24点阵放大两倍；
    //        7：32点阵放大两倍；
    //其他：24点阵
    //rotate 旋转角度:
    //        0：不旋转；	1：90度；	2：180°；	3:270°
    //bold 是否粗体
    //      0：否； 1：是
    //reverse 是否反白
    //      false：不反白；true：反白
    //underline  是有有下划线
    //      false:没有；true：有

    //9.打印文字
    public void  drawText(int text_x, int text_y, int width, int height,String str, int fontsize,
                          int rotate ,int bold, boolean underline,boolean reverse) {
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.SANS_SERIF);
        switch(fontsize)
        {
            case 1:
                textPaint.setTextSize(16);
                text_y -= 2;
                break;
            case 2:
                textPaint.setTextSize(24);
                text_y -= 3;
                break;
            case 3:
                textPaint.setTextSize(32);
                text_y -= 4;
                break;
            case 4:
                textPaint.setTextSize(48);
                text_y -= 6;
                break;
            case 5:
                textPaint.setTextSize(64);
                text_y -= 8;
                break;
            case 6:
                textPaint.setTextSize(96);
                text_y -= 12;
                break;
            case 7:
                textPaint.setTextSize(128);
                text_y -= 16;
                break;
            default:
                textPaint.setTextSize(24);
                text_y -= 3;
                break;
        }
        switch(rotate)
        {
            case 1:
                canvas.rotate(90);
                break;
            case 2:
                canvas.rotate(180);
                break;
            case 3:
                canvas.rotate(270);
                break;
            default:
                canvas.rotate(0);
                break;
        }
        if(bold == 0) textPaint.setFakeBoldText(false);
        else textPaint.setFakeBoldText(true);

        textPaint.setUnderlineText(underline);

        if(reverse) {

            Rect rect = new Rect();
            paint.setColor(Color.BLACK);
            paint.setStyle(Style.FILL);


            textPaint.setColor(Color.WHITE);
            StaticLayout staticLayout=new StaticLayout(str, textPaint, width, Alignment.ALIGN_NORMAL, 0.82f, 0.0f, false);
            canvas.translate(text_x, text_y);

            rect.left = text_x;
            rect.top = text_y;
            rect.right = text_x + staticLayout.getWidth();
            rect.bottom = text_y + staticLayout.getWidth();
            canvas.drawRect(rect, paint);
            staticLayout.draw(canvas);
        }
        else
        {
            paint.setStyle(Style.STROKE);

            textPaint.setColor(Color.BLACK);
            StaticLayout staticLayout = new StaticLayout(str, textPaint, width, Alignment.ALIGN_NORMAL, 0.82f, 0.0f, false);
            canvas.translate(text_x, text_y);
            staticLayout.draw(canvas);
        }

    }
    //参数：
    //text_x 起始横坐标
    //text_y 起始纵坐标
    //width	 文本框宽度
    //height	 文本框高度
    //text	 文本内容
    //fontSize	字体大小：
    //        1：16点阵；
    //        2：24点阵；
    //        3：32点阵；
    //        4：24点阵放大一倍；
    //        5：32点阵放大一倍
    //        6：24点阵放大两倍；
    //        7：32点阵放大两倍；
    //        其他：24点阵
    //rotate 旋转角度:
    //        0：不旋转；	1：90度；	2：180°；	3:270°
    //bold 是否粗体
    //        0：否； 1：是
    //reverse 是否反白
    //        false：不反白；true：反白
    //underline  是有有下划线
    //        false:没有 true：有

    private Bitmap encodeAsBitmap(String text, BarcodeFormat format, int Width, int Height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix temp = writer.encode(text, format, 1, 1);
        BitMatrix result = writer.encode(text, format, temp.getWidth() * Width, Height);
        int width = result.getWidth();
        int height = result.getHeight();
        int AAA = 0;
        int BBB = 0;

        for (int x = 0; x < width; x++) {
            if(result.get(x, 0) == true)
            {
                AAA = x;
                break;
            }
        }
        for (int x = 0; x < width; x++) {
            if(result.get(width - 1 - x, 0) == true)
            {
                BBB = x;
                break;
            }
        }
        width -= (AAA + BBB);


        int[] pixels = new int[width * Width * Height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(AAA + x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    //10.一维条码
    public void drawBarCode(int start_x, int start_y, String text,int type, int rotate,int linewidth, int height){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        BarcodeFormat barcodeFormat;
        switch(type)
        {
            case 0://Code39
                barcodeFormat = BarcodeFormat.CODE_39;
                break;
            case 1:
                barcodeFormat = BarcodeFormat.CODE_128;
                break;
            case 2:
                barcodeFormat = BarcodeFormat.CODE_93;
                break;
            case 3:
                barcodeFormat = BarcodeFormat.CODABAR;
                break;
            case 4:
                barcodeFormat = BarcodeFormat.EAN_8;
                break;
            case 5:
                barcodeFormat = BarcodeFormat.EAN_13;
                break;
            case 6:
                barcodeFormat = BarcodeFormat.UPC_A;
                break;
            case 7:
                barcodeFormat = BarcodeFormat.UPC_E;
                break;
            case 8:
                barcodeFormat = BarcodeFormat.ITF;
                break;
            default:
                barcodeFormat = BarcodeFormat.CODE_128;
                break;
        }
        try {
            Bitmap barcodeBitmap = encodeAsBitmap(text, barcodeFormat, linewidth, height);
            Matrix matrix = new Matrix();
            matrix.postRotate(90 * rotate);
            Bitmap dstbmp = Bitmap.createBitmap(barcodeBitmap, 0, 0, barcodeBitmap.getWidth(), barcodeBitmap.getHeight(), matrix, true);
            canvas.drawBitmap(dstbmp,start_x,start_y,paint);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //参数：
    //start_x 一维码起始横坐标
    //start_y 一维码起始纵坐标
    //text    内容
    //type 条码类型：
    //        0：CODE39；	1：CODE128；
    //        2：CODE93；	3：CODEBAR；
    //        4：EAN8；  	5：EAN13；
    //        6：UPCA;   	7:UPC-E;
    //        8:ITF
    //Linewidth 条码线宽度
    //Height 条码高度


    private Bitmap createQRImage(String Text, int ver,int lel) {
        try {
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix;
            int Width;
            if(lel == 0)
            {
                BitMatrix Temp = new QRCodeWriter().encode(Text, BarcodeFormat.QR_CODE, 1, 1);
                Width = Temp.getWidth() * ver;

            }
            else
            {
                Width = (lel * 4 + 17) * ver;
            }
            bitMatrix = new QRCodeWriter().encode(Text, BarcodeFormat.QR_CODE, Width,Width);
            int[] pixels = new int[Width * Width];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < Width; y++) {
                for (int x = 0; x < Width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * Width + x] = 0xff000000;
                    } else {
                        pixels[y * Width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(Width, Width, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, Width, 0, 0, Width, Width);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    //11.二维码
    public void drawQrCode(int start_x, int start_y, String text,int rotate, int ver, int lel){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();

        Bitmap barcodeBitmap = createQRImage(text,lel,ver);
        Matrix matrix = new Matrix();
        matrix.postRotate(90 * rotate);
        Bitmap dstbmp = Bitmap.createBitmap(barcodeBitmap, 0, 0, barcodeBitmap.getWidth(), barcodeBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(dstbmp,start_x,start_y,paint);

    }
    //参数：
    //start_x 二维码起始横坐标
    //start_y 二维码起始纵坐标
    //text    二维码内容
    //rotate 旋转角度：
    //        0：不旋转；	1：90度；	2：180°；	3:270°
    //ver  QrCode宽度(2-6)
    //lel  QrCode纠错等级(0-20)

    //12.图片
    public void drawGraphic(int start_x, int start_y, int bmp_size_x,int bmp_size_y, Bitmap bmp){
        Canvas canvas = new Canvas(Lable);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bmp,start_x,start_y,paint);
    }
    //参数：
    //start_x 图片起始点x坐标
    //start_y 图片起始点y坐标
    //bmp_size_x 图片的宽度
    //bmp_size_y 图片的高度
    //bmp 图片

    //13.打印机的状态信息
    public String printerStatus(){

        return "一切都没问题，帅锅～";
    }

    //14.定位到标签
    public void feed(){
        if(Port.isOpen)
        {
            byte[] Data = new byte[1];
            Data[0] = 0x0E;
            Port.write(Data,0,1);
        }
    }


}
