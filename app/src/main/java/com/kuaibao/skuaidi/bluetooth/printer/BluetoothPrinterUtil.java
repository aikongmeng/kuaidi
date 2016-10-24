package com.kuaibao.skuaidi.bluetooth.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.Order;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class BluetoothPrinterUtil {
	private static final byte[] byteCode = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7,
			8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
			25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
			42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58,
			59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
			76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
			93, 94, 95, 96, 97, 98, 99 };

	public static final byte ESC = 0x1B;
	public static final byte FS = 0x1C;
	public static final byte GS = 0x1D;
	public static final byte DLE = 16;
	public static final byte EOT = 4;
	public static final byte ENQ = 5;
	public static final byte SP = 0x20;
	public static final byte DC4 = 0x14;
	public static final byte HT = 0x09;
	public static final byte LF = 0x0A;
	public static final byte CR = 0x0D;
	public static final byte DEL = 0x07;
	public static final byte FF = 0x0C;
	public static final byte SO = 0x0E;
	public static final byte CAN = 24;

	public static final byte[] FEED_LINE = { 10 };

	public static final byte ALIGN_LEFT = 0;
	public static final byte ALIGN_CENTER = 1;
	public static final byte ALIGN_RIGHT = 2;

	final static int BUFFER_SIZE = 4096;
	private static final String CODE = "utf-8";

	/**
	 * 对一个byte[] 进行打印
	 * 
	 * @param printText
	 * @return add by yidie
	 */
	public static boolean printBytes(byte[] printText, OutputStream outputStream) {
		boolean returnValue = true;
		try {
			outputStream.write(printText);
		} catch (Exception ex) {
			returnValue = false;
		}
		return returnValue;
	}

	/**
	 * "\n" 就是换行
	 * 
	 * @param paramString
	 * @return add by yidie
	 */
	public static boolean printString(String paramString,
			OutputStream outputStream) {
		return printBytes(getGbk(paramString), outputStream);
	}

	/***************************************************************************
	 * add by yidie 2012-01-10 功能：设置打印绝对位置 参数： int 在当前行，定位光标位置，取值范围0至576点 说明：
	 * 在字体常规大小下，每汉字24点，英文字符12点 如位于第n个汉字后，则position=24*n
	 * 如位于第n个半角字符后，则position=12*n
	 ****************************************************************************/

	public static byte[] setCusorPosition(int position) {
		byte[] returnText = new byte[4]; // 当前行，设置绝对打印位置 ESC $ bL bH
		returnText[0] = 0x1B;
		returnText[1] = 0x24;
		returnText[2] = (byte) (position % 256);
		returnText[3] = (byte) (position / 256);
		return returnText;
	}

	/**
	 * 设置打印机的行高
	 * 
	 * @param h
	 * @return
	 */
	public static byte[] setLineHeight(byte h) {
		byte[] returnText = new byte[] { 0x1B, 0x33, h }; // 切纸； 1B 33 n
		return returnText;
	}

	public static byte[] setDefaultLineHeight() {
		byte[] returnText = new byte[] { 0x1B, 0x32 }; // 切纸； 1B 32
		return returnText;
	}

	public static byte[] InputStreamTOByte(InputStream in) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return outStream.toByteArray();
	}

	/**
	 * 打印我有外卖的logo
	 * 
	 * @param c
	 */
	public static void printLogo(Context c, OutputStream outputStream) {
		printBytes(setLineHeight((byte) 0), outputStream);
		InputStream is = c.getClass().getResourceAsStream("/assets/bill.bin");
		byte[] b;
		try {
			b = InputStreamTOByte(is);
			printBytes(b, outputStream);
			printBytes(setDefaultLineHeight(), outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] getLogo(Context c) {
		InputStream is = c.getClass().getResourceAsStream(
				"/assets/express_logo/logo_sto.png");
		byte[] b;
		try {
			b = InputStreamTOByte(is);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到店铺logo
	 * 
	 * @param c
	 * @param bit
	 * @return
	 */
	public static byte[] getLogo(Context c, byte[] bit) {
		InputStream is = c.getClass().getResourceAsStream("/assets/bill.bin");
		byte[] b = bit;
		try {
			b = InputStreamTOByte(is);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getLogo(byte[] bs) {
		byte[] b;
		try {
			b = bs;
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 支付打印(二维码)
	 * 
	 * @param b
	 * @param money
	 * @return [url=home.php?mod=space&uid=2643633]@throws[/url]
	 *         InvalidParameterException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static boolean printAlipayTitle(byte[] b, String money,
			OutputStream outputStream) throws InvalidParameterException,
			SecurityException, IOException {

		int iNum = 0;
		byte[] tempBuffer = new byte[1000];

		byte[] oldText = setAlignCenter('2');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setWH('4');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setBold(true);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = getGbk("支付凭证\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignCenter('2');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setWH('3');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setBold(true);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = getGbk("您共消费了" + money + "元\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignCenter('2');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setWH('3');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = setBold(true);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		oldText = getGbk("请扫码支付\n\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		try {
			outputStream.write(tempBuffer);
			printBytes(b, outputStream);
			printString("\n\n\n", outputStream);
			printBytes(CutPaper(), outputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static byte[] printStoExpressBill(Context context,
			Order order) throws InvalidParameterException,
			SecurityException, IOException {
		int iNum = 0;
		byte[] logo = PicFromPrintUtils
				.draw2PxPoint(PicFromPrintUtils.compressBitmap(
						((BitmapDrawable) context.getResources().getDrawable(
								R.drawable.logo_sto)).getBitmap(), 192, 92));

		byte[] tempBuffer = new byte[logo.length + 8000];

		byte[] oldText = setAlignType(1);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setLineHeight((byte) 0);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = logo;
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setDefaultLineHeight();
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBarcodeWidth(2.5f);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBarcodeHeight(65);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setHRIPrintPosition(2);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = PrintBarcode(order.getDeliverNo());
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignType(0);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("收件："+order.getAddress()+"\n      "+order.getName()+"  "+order.getPhone()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("发件："+order.getSenderAddress()+"\n      "+order.getSenderName()+"  "+order.getSenderPhone()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignType(1);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('3');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk(order.getAddressHead()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignType(0);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("物品："+"办公室迷你摇头风  |  签字：                          "+"\n      扇                  |\n日期："+order.getTime()+"         +|          年    月    日\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("------------------------------------------------\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("收件："+order.getAddress()+"\n      "+order.getName()+"  "+order.getPhone()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("发件："+order.getSenderAddress()+"\n      "+order.getSenderName()+"  "+order.getSenderPhone()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("物品："+order.getArticleInfo()+"\n日期："+order.getTime()+"\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignType(1);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("────────────────────────\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBarcodeWidth(3f);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBarcodeHeight(65);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setHRIPrintPosition(2);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = PrintBarcode(order.getDeliverNo());
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("\n\n\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = CutPaper();
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		return tempBuffer;
	}

	/***************************************************************************
	 * add by yidie 2012-01-12 功能：报表打印 参数： String 打印标题，如“月报表：2013-01”
	 * ReportUserSale 打印内容，包含 UserSaleInfo[]
	 ****************************************************************************/

	public static boolean printReportUser(OutputStream outputStream)
			throws InvalidParameterException, SecurityException, IOException {

		int iNum = 0;

		byte[] tempBuffer = new byte[8000];

		byte[] oldText = setAlignCenter('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('3');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setCusorPosition(324);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		String strTime = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.SIMPLIFIED_CHINESE).format(new Date());
		oldText = getGbk(strTime + "打印\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignCenter('2');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('4');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBold(true);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("\n\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setAlignCenter('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setBold(false);
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = setWH('1');
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("　　   用户  　　　　　　   售出数量　售出金额\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = getGbk("----------------------------------------------\n");
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;

		oldText = CutPaper();
		System.arraycopy(oldText, 0, tempBuffer, iNum, oldText.length);
		iNum += oldText.length;
		try {
			outputStream.write(tempBuffer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static byte[] getGbk(String stText) {
		byte[] returnText = null;
		try {
			returnText = stText.getBytes("GBK"); // 必须放在try内才可以
		} catch (Exception ex) {
		}
		return returnText;
	}

	/**
	 * 选择字符大小
	 * 
	 * @param dist
	 * @return
	 */
	public static byte[] setWH(char dist) {
		byte[] returnText = new byte[3]; // GS ! 11H 倍宽倍高
		returnText[0] = 0x1D;
		returnText[1] = 0x21;

		switch (dist) // 1-无；2-倍宽；3-倍高； 4-倍宽倍高
		{
		case '2':
			returnText[2] = 0x10;
			break;
		case '3':
			returnText[2] = 0x01;
			break;
		case '4':
			returnText[2] = 0x11;
			break;
		default:
			returnText[2] = 0x00;
			break;
		}

		return returnText;
	}

	/**
	 * 打印的对齐方式
	 * 
	 * @param dist
	 * @return
	 */
	public static byte[] setAlignCenter(char dist) {
		byte[] returnText = new byte[3]; // 对齐 ESC a
		returnText[0] = 0x1B;
		returnText[1] = 0x61;

		switch (dist) // 1-左对齐；2-居中对齐；3-右对齐
		{
		case '2':
			returnText[2] = 0x01;
			break;
		case '3':
			returnText[2] = 0x02;
			break;
		default:
			returnText[2] = 0x00;
			break;
		}
		return returnText;
	}

	/**
	 * 设置加粗
	 * 
	 * @param dist
	 * @return
	 */
	public static byte[] setBold(boolean dist) {
		byte[] returnText = new byte[3]; // 加粗 ESC E
		returnText[0] = 0x1B;
		returnText[1] = 0x45;

		if (dist) {
			returnText[2] = 0x01; // 表示加粗
		} else {
			returnText[2] = 0x00;
		}
		return returnText;
	}

	public static byte[] getBarCodeToByte(String stBarcode, int width,
			int height) {
		if (width == 0 || width < 200) {
			width = 200;
		}

		if (height == 0 || height < 50) {
			height = 50;
		}

		try {
			// 文字编码
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, CODE);

			BitMatrix bitMatrix = new MultiFormatWriter().encode(stBarcode,
					BarcodeFormat.CODE_128, width, height, hints);
			Bitmap bitmap = BitMatrixToBitmap(bitMatrix);
			if (bitmap == null) {
				return null;
			}
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			// 将Bitmap压缩成PNG编码，质量为100%存储
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			bitmap.recycle();
			bitmap = null;
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getBarCodeToBitmap(String stBarcode, int width,
			int height) {
		if (width == 0 || width < 200) {
			width = 200;
		}

		if (height == 0 || height < 50) {
			height = 50;
		}

		try {
			// 文字编码
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, CODE);

			BitMatrix bitMatrix = new MultiFormatWriter().encode(stBarcode,
					BarcodeFormat.CODE_128, width, height, hints);
			return BitMatrixToBitmap(bitMatrix);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * BitMatrix转换成Bitmap
	 * 
	 * @param matrix
	 * @return
	 */
	private static Bitmap BitMatrixToBitmap(BitMatrix matrix) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
			}
		}
		return createBitmap(width, height, pixels);
	}

	/**
	 * 生成Bitmap
	 * 
	 * @param width
	 * @param height
	 * @param pixels
	 * @return
	 */
	private static Bitmap createBitmap(int width, int height, int[] pixels) {
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
//	
//	private static byte[] getAutographByte(Order order){
//		int i = order.getArticleInfo().length()/6;
//		int space = 
//		"物品："+
//	}
	
	/**
	 * 打印一维条码 注释：编码为CODE_128
	 * 
	 * @param strBarcode
	 * @return
	 */
	public static byte[] PrintBarcode(String strBarcode) {
		byte[] barCodeBytes = strToByte(strBarcode);
		int iLength = strBarcode.length() % 2 == 0 ? 8 + barCodeBytes.length
				: 10 + barCodeBytes.length;
		byte[] returnText = new byte[iLength];

		returnText[0] = 0x1D;
		returnText[1] = 0x6B;
		returnText[2] = 0x49;
		returnText[3] = strBarcode.length() % 2 == 0 ? (byte) (4 + barCodeBytes.length)
				: (byte) (6 + barCodeBytes.length);
		returnText[4] = 0x7B;
		returnText[5] = 0x42;
		returnText[6] = 0x7B;
		returnText[7] = 0x43;
		int idx = 8;
		if (strBarcode.length() % 2 == 0) {
			for (int i = 0; i < barCodeBytes.length; i++) {
				returnText[idx] = barCodeBytes[i];
				idx++;
			}
		} else {
			for (int i = 0; i < barCodeBytes.length - 1; i++) {
				returnText[idx] = barCodeBytes[i];
				//System.out.println(barCodeBytes[i]);
				idx++;
			}
			returnText[iLength - 3] = 0x7B;
			returnText[iLength - 2] = 0x42;
			returnText[iLength - 1] = barCodeBytes[barCodeBytes.length - 1];
		}

		return returnText;
	}

	/**
	 * 设置需要打印的一维条码高度 范围：0 <= height <= 255 描述：设置一维条码高度为height个打印点 默认：n=162
	 * 
	 * @param height
	 * @return
	 */
	public static byte[] setBarcodeHeight(int height) {
		byte[] size = new byte[3];
		size[0] = 0x1D;
		size[1] = 0x68;
		size[2] = (byte) height;
		return size;
	}

	/**
	 * 设置需要打印的一维条码宽度 范围：2 <= width <= 6 默认：width = 2
	 * 
	 * @param width
	 * @return
	 */
	public static byte[] setBarcodeWidth(float width) {
		return new byte[] { GS, 0x77, (byte) width };
	}

	/**
	 * 选择HRI字符的打印位置 范围：0 <= n <=3 || 48 <= n <= 51 0||48:不打印 1||49:条码上方
	 * 2||50:条码下方 3||51:条码上、下方都打印 注释：1，HRI是对条码内容注释的字符 2，不受下划线、放大、加粗、旋转等影响 默认：n=0
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setHRIPrintPosition(int n) {
		return new byte[] { GS, 0x48, (byte) n };
	}

	public static byte[] print_and_feed_lines(int n) {

		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 100;
		result[2] = (byte) n;
		return result;
	}

	public static byte[] print_and_reverse_feed_lines(int n) {

		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 101;
		result[2] = (byte) n;
		return result;
	}

	public static byte[] print_linefeed() {
		byte[] result = new byte[1];
		result[0] = LF;
		return result;
	}

	/**
	 * 切纸
	 * 
	 * @return
	 */
	public static byte[] CutPaper() {
		byte[] returnText = new byte[] { 0x1D, 0x56, 0x42, 0x00 }; // 切纸； GS V
																	// 66D 0D
		return returnText;
	}

	// public static void creatView(Context context,
	// final OutputStream outputStream) {
	// final View v = LayoutInflater.from(context).inflate(
	// R.layout.express_bill_view, null);
	// Dialog dialog = new Dialog(context);
	// dialog.setContentView(v);
	// dialog.show();
	// ViewTreeObserver vto = v.getViewTreeObserver();
	// vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	// @Override
	// public void onGlobalLayout() {
	// v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	// ImageView iv = (ImageView) v.findViewById(R.id.iv_barcode);
	// iv.setImageBitmap(getBarCodeToBitmap("363606310467", 500, 100));
	// try {
	// outputStream.write(PrintUtil.setLineHeight((byte) 0x0));
	// outputStream.write(PicFromPrintUtils.draw2PxPoint(PrintUtil
	// .createViewBitmap(v)));
	// // outputStream.write(bitmapToByte(PrintUtil.createViewBitmap(v)));
	// outputStream.write("\n\n\n".getBytes());
	// outputStream.write(PrintUtil.CutPaper());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	public static Bitmap createViewBitmap(View v) {
		Bitmap bitmap = Bitmap.createBitmap(360, 720, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		v.draw(canvas);
		return PicFromPrintUtils.compressPic(bitmap, 360, 720);
	}

	public static byte[] bitmapToByte(Bitmap bitmap) {

		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		// 将Bitmap压缩成PNG编码，质量为100%存储
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);	
		bitmap.recycle();
		bitmap = null;
		return os.toByteArray();
	}

	/**
	 * 初始化打印机 描述：清除打印缓冲区中的数据，复位打印机打印参数到打印机缺省参数。
	 * 注释：1，通过配置软件设置的参数不被清除；2，串口接收缓冲区中的命令不被清除
	 * 
	 * @return
	 */
	public static byte[] initPrinter() {
		byte[] result = new byte[2];
		result[0] = ESC;
		result[1] = 0x40;
		return result;
	}

	/**
	 * 设置绝对打印位置 位置：距离(x+y*256)点处 1,如果设置位置在指定打印区域外，该命令被忽略
	 * 2,横向移动单位值：1/203英寸，约0.125mm,即一个打印点的尺寸 0 <= x <= 255 0 <= y <= 255
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static byte[] setAbsolutePrintPosition(int x, int y) {
		byte[] result = new byte[4];
		result[0] = ESC;
		result[1] = 0x24;
		result[2] = (byte) x;
		result[3] = (byte) y;
		return result;
	}

	/**
	 * 选择/取消下划线模式 0 <= n <= 2; n=0:取消下划线模式 n=1:选择下划线模式（1点宽） n=2:选择下划线模式（2点宽）
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setUnderlineType(int n) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x2D;
		if (n >= 2) {
			n = 2;
		}
		result[2] = byteCode[n];
		return result;
	}

	/**
	 * 设置行距 0 <= n <= 255 注释:行距包括文字高度和两行文字之间的空白高度。
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setLineSpace(int n) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x33;
		if (n >= 255) {
			n = 255;
		}
		result[2] = (byte) n;
		return result;
	}

	/**
	 * 选择/取消双重打印模式
	 * 
	 * @param isDoublePrint
	 * @return
	 */
	public static byte[] isUseDoublePrint(boolean isDoublePrint) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x47;
		if (isDoublePrint) {
			result[2] = 0x00;
		} else {
			result[2] = 0x01;
		}
		return result;
	}

	/**
	 * 选择字体 n=0: 中文->24*24点阵 英文->24*12点阵 n=1: 中文->16*16点阵 英文->16*8点阵 默认：n=0
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setFoutStyle(int n) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x4D;
		result[2] = (byte) n;
		return result;
	}

	/**
	 * 选择字符集编码 范围：n=0 || n=1 n=0:中文简体GB18030 n=1:中文繁体BIG5 默认：n=0
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setCharacterEncoding(int n) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x4E;
		result[2] = (byte) n;
		return result;
	}

	/**
	 * 选择对齐方式 范围：0 <= n <= 2 n=0:左对齐 n=1:居中 n=2:右对齐 默认：n=0
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] setAlignType(int n) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x61;
		result[2] = (byte) n;
		return result;
	}

	/**
	 * 设置左边距 范围：0 <= nL <= 255,0 <= nH <= 255 注释：a,左边距设置为(nL+nH*256)打印点
	 * b,在标准模式下、该命令执行时如果不在行首，则下行有效 c,如果设置超出了最大可用打印宽度，则取最大可用打印宽度
	 * 
	 * @param nL
	 * @param nH
	 * @return
	 */
	public static byte[] setLeftMargin(byte nL, byte nH) {
		return new byte[] { GS, 0x4C, nL, nH };
	}

	private static byte[] strToByte(String strBarcode) {
		int sLength = strBarcode.length() % 2 == 0 ? strBarcode.length() / 2
				: strBarcode.length() / 2 + 1;
		int[] strs = new int[sLength];
		byte[] barcodeByte = new byte[sLength];
		int index = 0;
		for (int i = 0; i < strBarcode.length();) {
			if (i == strBarcode.length() - 1) {
				barcodeByte[sLength - 1] = strBarcode.substring(i).getBytes()[0];
			} else {
				strs[index] = Integer.parseInt(strBarcode.substring(i, i + 2));
			}
			index++;
			i = i + 2;
		}

		for (int i = 0; i < (strBarcode.length() % 2 == 0 ? strs.length
				: strs.length - 1); i++) {
			barcodeByte[i] = byteCode[strs[i]];
		}
		return barcodeByte;
	}

	public static byte[] creatPrintPage(int width, int height) {
		//System.out.println("nwl :"+ (PicFromPrintUtils.int2ByteArr(width)[3] & 0xFF));
		//System.out.println("nwh : " + PicFromPrintUtils.int2ByteArr(width)[0]);
		//System.out.println("nhl : "+ (PicFromPrintUtils.int2ByteArr(height)[3] & 0xFF));
		//System.out.println("nhh : " + PicFromPrintUtils.int2ByteArr(height)[0]);
		//
		// return new byte[] { FS, 0x4C, 0x70,
		// (byte) (PicFromPrintUtils.int2ByteArr(width)[3] & 0xFF),
		// (byte) (PicFromPrintUtils.int2ByteArr(width)[0]),
		// (byte) (PicFromPrintUtils.int2ByteArr(height)[3] & 0xFF),
		// (byte) (PicFromPrintUtils.int2ByteArr(height)[0]),
		// 0x00 };

		return new byte[] { FS, 0x4C, 0x70, (byte) 255, 2, (byte) 255, 2, 0x00 };
	}

	public static byte[] printPage() {
		return new byte[] { FS, 0x4C, 0x6F, 0x00, 0x00 };
	}

	public static byte[] drawLine(int startX, int startY, int endX, int endY) {
		return new byte[] { FS, 0x4C, 0x6C, 0x02, 0x01,
				(byte) (PicFromPrintUtils.int2ByteArr(startX)[3] & 0xFF),
				PicFromPrintUtils.int2ByteArr(startX)[0],
				(byte) (PicFromPrintUtils.int2ByteArr(startY)[3] & 0xFF),
				PicFromPrintUtils.int2ByteArr(startY)[0],
				(byte) (PicFromPrintUtils.int2ByteArr(endX)[3] & 0xFF),
				PicFromPrintUtils.int2ByteArr(endX)[0],
				(byte) (PicFromPrintUtils.int2ByteArr(endY)[3] & 0xFF),
				PicFromPrintUtils.int2ByteArr(endY)[0]};
	}

	public static byte[] printText(String str) {

		byte[] byteStr = getGbk(str);
		byte[] result = new byte[byteStr.length + 8];
		result[0] = FS;
		result[1] = 0x4C;
		result[2] = 0x74;
		result[3] = 35;
		result[4] = 35;
		result[5] = 0;
		result[6] = 0;
		int index = 7;
		for (int i = 0; i < byteStr.length; i++) {
			result[index] = byteStr[i];
			index++;
		}
		result[index] = 0x00;
		return result;
	}
}
