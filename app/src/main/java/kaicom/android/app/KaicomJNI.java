package kaicom.android.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
/**
 **函数命名规则:public 接口以大写开头(回调函数除外，应用层已经做掉了，不更改)
 **         private 接口以小写开头
 **函数参数：把每个函数的参数和返回值标准清楚：包括类型 和 参数含义
 **
 **平台代码区分：通过mPlatform = getPlatform()区分设备是420/585/520/510
 **
 **
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class KaicomJNI {

	/*xxxxxxxxxxxxxxxxxx下面是 "宏"参数定义xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx*/
	/*xxxxxxxFor Public xxxxxxxxxxxxxxxx*/
	private static final String KAICOM_DISABLE_USB_DEBUG = "com.kaicom.disable_usb_debug";
	private static final String KAICOM_DISABLE_SCREEN_LOCK = "com.kaicom.disable.screen_lock";
	private static final String KAICOM_DISABLE_INSTALL_PACKAGE = "com.kaicom.disable.install.package";
	private static final String KAICOM_DISABLE_STATUSBAR_EXPAND = "com.kaicom.disable_statusbar_expand";
	/**
	 * 若action的值为0x5A  则两项都屏蔽；
	 * 若action的值为0x50      则屏蔽完成button；
	 * 若action的值为0xA   则屏蔽取消button；
	 * 若action的值为0x0         则系统默认模式；
	 */
	public static final int ACTION_DEFAULT = 0x0;				//恢复系统默认
	public static final int ACTION_DIS_CANCEL_FINISH = 0X5A;	//都屏蔽
	public static final int ACTION_DIS_CANCEL = 0XA;  			//屏蔽取消button
	public static final int ACTION_DIS_FINISH = 0x50; 			//屏蔽完成button
	/*xxxxxxxFor wdt420 xxxxxxxxxxxxxxxx*/
	private static final int KAICOM_SCANNER_MODE_KEY = 0xaa;
	private static final int KAICOM_SCANNER_MODE_LIB = 0x00;
	private static final String KAICOM_DISABLE_BROWSER = "com.kaicom.disable.browser";
	private static final String KAICOM_CONFIG_GPS_STATUS = "com.kaicom.config.gps.status";
	private static final String SYSTEM_KAICOM_SCANNED_ENDFIX = "com.kaicom.scanner.endfix";
	private static final String SYSTEM_KAICOM_SCANNER_RUNTIME_MODE = "com.kaicom.scanner.runtime.mode";
	private static final String KAICOM_INSTALL_PACKAGE_WITH_SILENCE = "com.kaicom.install.packege.with.silence";
	private static final String KAICOM_INSTALL_PACKAGE_EXTRA_APK_PATH = "com.kaicom.install.packege.extra.apk.path";
	private static final String KAICOM_INSTALL_PACKAGE_EXTRA_TIPS_FORMAT = "com.kaicom.install.packege.extra.tips.format";
	/*xxxxxxxFor wdt585 xxxxxxxxxxxxxxxx*/
	/*xxxxxxxFor wdt520 xxxxxxxxxxxxxxxx*/
	private static final String KAICOM_DISABLE_KEYDOWN_TONE = "com.kaicom.disable.keydowntone";
	private static final String KAICOM_DISABLE_SCAN_MESSAGE = "com.kaicom.disable.scan.message";
	private static final String KAICOM_DISABLE_TOUCH_SCREEN = "com.kaicom.disable.touch_screen";
	private static final String KAICOM_DISABLE_SCAN_MESSAGE_ENTER = "com.kaicom.disable.scan.message.enter";

	static final String KAICOM_INSTALL_APPS_BUTTONS = "com.kaicom.install.package.buttons";
	/**520以后电容屏和电阻屏之分*/
	public static final String RTP = "aw2083";//电阻屏
	public static final String CTP = "S3203";//电容屏
	/*xxxxxxxFor wdt510 xxxxxxxxxxxxxxxx*/

	/*xxxxxxxxxxxxxxxxxxEndxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx*/
	public static final int GPIO_VIBRATE 			= 0X10;			//马达
	public static final int GPIO_CAMFLASH 			= 0X20;			//闪光灯
	public static final int GPIO_LED_LEFT_RED 		= 0X30;		//LED灯
	public static final int GPIO_LED_WIFI 			= 0X40;
	public static final int GPIO_LED_RIGHT_BLUE 	= 0X50;
	public static final int GPIO_LED_RIGHT_GREEN 	= 0X60;
	public static final int GPIO_KEY_LIGHT 			= 0X70;			//键盘背光
	public static final int GPIO_ON 				= 0;					// io 开
	public static final int GPIO_OFF 				= 1;					// io 关
	public static final int RFID_FIND_TAG 			= 0x80;
	public static final int RFID_READ_BLOCK 		= 0x40;
	public static final int RFID_WRITE_BLOCK 		= 0x20;
	public static final int BARCODE_EAN13 			= 0x0001; // EAN13、UPCA编码
	public static final int BARCODE_EAN8 			= 0x0002; // EAN8编码
	public static final int BARCODE_EAN 			= 0x0003;
	public static final int BARCODE_UPCE 			= 0x0004; // UPCE编码
	public static final int BARCODE_93 				= 0x0008; // CODE93编码
	public static final int BARCODE_128 			= 0x0010; // CODE128类编码
	public static final int BARCODE_39COMMON 		= 0x0020; // CODE39普通编码
	public static final int BARCODE_39FULLASCII 	= 0x0040; // CODE39全ASCII编码
	public static final int BARCODE_39 				= 0x0060;
	public static final int BARCODE_CODABAR 		= 0x0080; // 库德巴编码
	public static final int BARCODE_ITF25 			= 0x0100; // 交叉25编码
	public static final int BARCODE_STD25 			= 0x0200; // 标准25编码(工业25码) 注:一般不读
	public static final int BARCODE_MTRX25 			= 0x0400; // 矩阵25编码 注:一般不读
	public static final int BARCODE_25 				= 0x0700;
	public static final int BARCODE_CHNPOST 		= 0x0800; // 中国邮政码
	public static final int BARCODE_11 				= 0x1000; // CODE11编码
	public static final int BARCODE_MSI 			= 0x2000; // MSI编码
	public static final int BARCODE_39EMS 			= 0x10000; // CODE39EMS编码,BARCODE_39FULLASCII子集
	public static final int BARCODE_ALL = ~0; // 使能全部条码扫描
	public static final int BARCODE_NONE = 0; // 禁止全部条码扫描

	public static final int MODEL_SE955 	= 0; // 默认如果I2C设备读取不到扫描头ID都认为SE955在线
	public static final int MODEL_SE950 	= 1;
	public static final int MODEL_SE4500 	= 2;
	public static final int MODEL_MDL2001	= 3;
	public static final int MODEL_UE966		= 4;
	public static final int MODEL_SE965		= 5;

	public static final int PLATFORM_WDT420 = 1;//用于平台区分
	public static final int PLATFORM_WDT585 = 2;
	public static final int PLATFORM_WDT520 = 3;
	public static final int PLATFORM_WDT521 = 3;
	public static final int PLATFORM_WDT585P = 3;
	public static final int PLATFORM_WDT510 = 4;

	private static int mScannerCounter=0;	//统计巴枪扫描次数
	private Context mContext;
	private static KaicomJNI kaicomJNI;
	Scanner2D mScanner = null;
	private int mScanModel = 0;
	private int mPlatform = 0;
	private static boolean isKaicom = false;

	/**
	 * 载入库文件
	 * 二维头的库 在Scanner2D里面
	 */
	static {

		if(new Build().MODEL.equals("520")
				|| new Build().MODEL.equals("521")
				|| new Build().MODEL.endsWith("586")){
			System.loadLibrary("kaicom520");
			isKaicom = true;
		}else if(new Build().MODEL.equals("510")){
			System.loadLibrary("kaicom510");
			isKaicom = true;
		}else if(new Build().MODEL.equals("585")){
			System.loadLibrary("kaicom585");
			isKaicom = true;
		}else if(new Build().MODEL.equals("420")){
			System.loadLibrary("kaicom");
			isKaicom = true;
		}

	}

	/**
	 * 扫描头回调接口
	 * @author Administrator
	 *
	 */
	public interface ScanCallBack {
		/**
		 * 回调接口，str是扫描结果
		 * @param str
		 */
		void onScanResults(String str);
		/**
		 * 软解使用 type是条码类型  str是扫描结果
		 * @param str
		 * @param type
		 */
		void onScanResults(String str, int type);
	}


	/**
	 * GPIO 状态设置。
	 *
	 * @param gpio
	 *            GPIO_VIBRATE。GPIO_CAMFLASH。GPIO_LED_*
	 * @param status
	 *            GPIO_ON 点亮GPIO。GPIO_OFF 熄灭GPIO
	 * */
	public native void SetGPIOStatus(int gpio, int status);

	/**
	 * 获取平台号。
	 *
	 * @return 1=PLATFORM_WDT420，2=PLATFORM_WDT585
	 * 		3 = PLATFORM_WDT520,4 = PLATFORM_WDT510;
	 */
	public native int GetPlatform();

	/**
	 * 获取扫描头类型
	 *
	 * @return 0=MODEL_SE955，1=MODEL_SE950，2=MODEL_SE4500
	 * 		   3=MODEL_MDL2001, 4=MODEL_UE966 5=MODEL_SE965
	 */
	public native int GetScannerModel();

	/**
	 * 打开扫描头句柄
	 */
	private native void setScannerOn();

	public void SetScannerOn() {
		try
		{
			RandomAccessFile file = new RandomAccessFile("/sdcard/scanner", "rw");
			try
			{
				file.seek(0);
				mScannerCounter=file.readInt();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.doOpen(mContext);
		} else{
			setScannerOn();
		}
	}

	public static boolean isKaicom(){
		return isKaicom;
	}

	/**
	 * 开始扫描
	 */
	private native void setScannerStart();

	public void SetScannerStart() {
		try
		{
			RandomAccessFile file = new RandomAccessFile("/sdcard/scanner", "rw");
			try
			{
				file.seek(0);
				file.writeInt(++mScannerCounter);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.doStart();
		} else
			setScannerStart();
	}

	/**
	 * 停止扫描
	 */
	private native void setScannerStop();

	public void SetScannerStop() {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.doStop();
		} else{
			setScannerStop();
		}
	}

	/**
	 * 关闭扫描头句柄
	 */
	private native void setScannerOff();

	public void SetScannerOff() {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.doClose();
		} else{
			setScannerOff();
		}
	}

	/**
	 * 设置扫描头参数
	 */
	/**
	 * 设置扫描头参数。目前只支持设置一维软解的扫描类型
	 *
	 * @param parameter
	 *            SCANNER_DECODE_TYPE 0X18000000 如设置只扫描EAN13，传入的参数为
	 *            (SCANNER_DECODE_TYPE|BARCODE_EAN13)
	 */
	private native void setScannerParameter(int parameter);

	public void SetScannerParameter(int parameter) {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.setParameter(parameter);
		} else
			setScannerParameter(parameter);
	}

	/**
	 * @param para	参数
	 * @param value 参数值
	 * @return		0成功 -1失败
	 */
	public int SetScannerPara(int para, int value){
		//setScannerOn();

		return setScannerPara(para, value);
	}
	/**
	 * @param para
	 * @return	value 参数值 获取的参数值
	 */
	public int GetScannerPara(int para){
		int ret = -1;
		//setScannerOn();
		ret =  getScannerPara(para);
		//setScannerOff();

		return ret;
	}

	private native int setScannerPara(int para, int value);
	private native int getScannerPara(int para);

	/**
	 * 触发扫描
	 */
	private native void setScannerRetriger();

	public void SetScannerRetriger() {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.resetTrigger();
		} else
			setScannerRetriger();
	}

	/**
	 * 设置扫描超时时间
	 *
	 * @param sec
	 *            单位为秒
	 */
	private native void setScannerTimerOut(int sec);

	public void SetScannerTimerOut(int sec) {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner.setTimeout(sec);
		} else
			setScannerTimerOut(sec);
	}

	/**
	 * 是否在扫描
	 */
	private native boolean getScannerIsScanning();

	public boolean GetScannerIsScanning() {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			return mScanner.isScanning();
		} else
			return getScannerIsScanning();
	}

	/**
	 * 打开随机触摸事件句柄
	 */
	private native void setRandomTsEventOn();

	public void SetRandomTsEventOn() {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setRandomTsEventOn();
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_setRandomTsEventOn();
		}
	}

	/**
	 * 关闭停止随机触摸事件
	 */
	private native void setRandomTsEventOff();

	public void SetRandomTsEventOff() {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setRandomTsEventOff();
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_setRandomTsEventOff();
		}
	}

	/**
	 * 设置随机触摸事件超时时间,并启动产生随机触摸事件
	 *
	 * @param minute
	 *            超时时间参数；量化单位为分钟
	 */
	private native void setRandomTsEventTimeOut(int minute);

	public void SetRandomTsEventTimeOut(int minute) {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setRandomTsEventTimeOut(minute);
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_setRandomTsEventTimeOut(minute);
		}
	}

	/**
	 * 系统重启
	 */
	private native void setSystemReboot();

	public void SetSystemReboot() {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setSystemReboot();
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_SetSystemReboot();
		}
	}

	/**
	 * 系统关机
	 */
	private native void setSystemShutdown();

	public void SetSystemShutdown() {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setSystemShutdown();
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_SetSystemShutdown();
		}
	}

	/**
	 * 设置系统时间;
	 *
	 * @param mm
	 *            1970到现在的毫秒时间值的字符串
	 */
	private native void setSystemTime(String mm);

	public void SetSystemTime(String mm) {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setSystemTime(mm);
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_SetSystemTime(mm);
		}
	}

	/**
	 * 设置机器码
	 *
	 * @param code
	 *            机器码字符串
	 */
	private native void setMachineCode(String code);

	public void SetMachineCode(String code) {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			setMachineCode(code);
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			rongQi_SetMachineCode(code);
		}
	}

	/**
	 * 获取机器码
	 *
	 * @return 返回机器码字符串
	 */
	private native String getMachineCode();

	public String GetMachineCode() {
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			return getMachineCode();
		}else if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			return rongQi_GetMachineCode();
		}else{
			return null;
		}
	}

	/**
	 * 打开电池采样句柄
	 */
	private native void setBatterySampleOpen();

	public void SetBatterySampleOpen() {
		if (mPlatform == PLATFORM_WDT420) {
			setBatterySampleOpen();
		}
	}

	/**
	 * 获取电池电压
	 *
	 * @return 返回为整形 如实际电压为4.123V 那么返回值为4123
	 */
	private native int getBatteryVoltage();
	/**
	 * 510不实现此功能
	 * @return
	 */
	public int GetBatteryVoltage() {
		if(mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT420){

			return getBatteryVoltage();
		}else
			return 0;
	}

	/**
	 * 关闭电池采样句柄
	 */
	private native void setBatterySampleClose();

	public void SetBatterySampleClose() {
		if (mPlatform == PLATFORM_WDT420) {
			setBatterySampleClose();
		}
	}

	/**
	 * 设置电池校准 校准基准电压为4V 。接入基准电压4V ，
	 *
	 * @param adj
	 *            如何测得电压为4.120v 那么校准电压为 (4-4.120)*1000
	 */
	private native void setBatteryAdjustValue(int adj);

	public void SetBatteryAdjustValue(int adj) {
		if (mPlatform == PLATFORM_WDT420) {
			setBatteryAdjustValue(adj);
		}
	}

	/**
	 *
	 * 打开RFID 句柄
	 */
	public native void SetRfidOn();

	/**
	 * 写RFID 扇块。。
	 *
	 * @param str
	 *            为要写入的数据
	 * @param block
	 *            为扇区块索引
	 */
	public native void WriteRfidData(String str, int block);

	/**
	 * 读RFID 扇块。，block为扇区块索引
	 */
	public native void ReadRfidData(int block, int size);

	/**
	 * 查找RFID 标签
	 */
	public native void FindRfidTag();

	/**
	 * 关闭RFID 句柄
	 */
	public native void SetRfidOff();

	/**
	 * 获取硬件版本号
	 */
	public native int GetHardWareVersion();

	ScanCallBack mScanCB, mRfidCB;
	String mScanCode;

	/**
	 * 扫描头回调句柄
	 */
	/*
	 * public void ScanResults(String str) { mScanCode = str; if (mScanCB !=
	 * null) mScanCB.onScanResults(mScanCode);
	 *
	 * }
	 */
	/**
	 * 软解扫描头回调句柄，type 返回扫描条码的类型。
	 */
	public void ScanResults(String str, int type) {
		mScanCode = str;
		if (mScanCB != null) {
			mScanCB.onScanResults(mScanCode, type);
			mScanCB.onScanResults(mScanCode);
		}

	}

	/**
	 * RFID 回调
	 */
	public void rfidResults(String str, int type) {
		mScanCode = str;
		if (mRfidCB != null)
			mRfidCB.onScanResults(mScanCode, type);
	}

	public int getmScanModel(){
		return mScanModel;
	}

	private KaicomJNI(Context context) {

		mScanModel = GetScannerModel();
		//Log.v("MODEL", "################New KaicomJNI###mScanModel="+mScanModel);
		mPlatform = GetPlatform();
		mContext = context;

	}


	public static KaicomJNI getInstance(Context context) {
		if (kaicomJNI == null) {
			kaicomJNI = new KaicomJNI(context);
		}
		return kaicomJNI;
	}

	/**
	 * mScanCB 为扫描头回调
	 */
	public void setmScanCB(ScanCallBack mScanCB) {
		if (mScanModel == MODEL_SE4500) {
			if(mScanner==null)
			{
				mScanner = new Scanner2D(mScanCB);
			}
			mScanner = new Scanner2D(mScanCB);
		}
		this.mScanCB = mScanCB;
	}

	/**
	 * mRfidCB 为RFID回调
	 */
	public void setmRfidCB(ScanCallBack mRfidCB) {
		this.mRfidCB = mRfidCB;
	}


	/**
	 * 说明：以下是一些系统定制功能
	 *
	 *
	 * ***********************************************************************************/
	//Begin: zzd add
	public void enableKeydownTone(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			if (mContext != null){
				Intent intent = new Intent(KAICOM_DISABLE_KEYDOWN_TONE);
				intent.putExtra(KAICOM_DISABLE_KEYDOWN_TONE, false);
				mContext.startService(intent);
			}
		}
	}

	public int GetStatusKeydownTone(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			if(mContext != null) {
				return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
						"keydown_tone_enabled", 1);
			}
		}
		return 1;
	}

	public void disableKeydownTone(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			if (mContext != null){
				Intent intent = new Intent(KAICOM_DISABLE_KEYDOWN_TONE);
				intent.putExtra(KAICOM_DISABLE_KEYDOWN_TONE, true);
				mContext.startService(intent);
			}
		}
	}
//End: zzd add
	/**
	 * 屏蔽状态栏下拉
	 * 说明：不允许状态栏进行下拉
	 */
	public void TurnOffStatusBarExpand() {
		Intent intent = new Intent(KAICOM_DISABLE_STATUSBAR_EXPAND);
		intent.putExtra(KAICOM_DISABLE_STATUSBAR_EXPAND, true);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}

	/**
	 * 屏蔽状态栏下拉
	 * @return  1 使能允许，0禁止下拉
	 */
	public int GetStatusBarExpand(){
		if (mPlatform == PLATFORM_WDT585) {
			if(mContext != null) {
				return android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
						"statusbar_enable_expand", 1);
			} else {
				return 1;
			}
		}else if(mPlatform == PLATFORM_WDT520){
			if(mContext != null) {
				//System.out.println("###########################get1："+ android.provider.Settings.Global.getInt(mContext.getContentResolver(),"statusbar_enable_expand", 1));
				return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
						"statusbar_enable_expand", 1);
			} else {
				return 1;
			}
		}else{//420
			return	getStatusBarExpand();
		}
	}
	private native int getStatusBarExpand();


	/**
	 * 使能状态栏下拉
	 * 说明：允许状态栏进行下拉
	 */
	public void TurnOnStatusBarExpand() {
		Intent intent = new Intent(KAICOM_DISABLE_STATUSBAR_EXPAND);
		intent.putExtra(KAICOM_DISABLE_STATUSBAR_EXPAND, false);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}
	/**
	 * 屏蔽USB debug
	 * 说明：关闭usb调试功能
	 */
	public void TurnOffUSBDebug() {
		Intent intent = new Intent(KAICOM_DISABLE_USB_DEBUG);
		intent.putExtra(KAICOM_DISABLE_USB_DEBUG, true);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}
	/**
	 * 屏蔽USB debug
	 * @return 1 打开， 0 未打开
	 */
	public int GetStatusUSBDebug(){
		if (mPlatform == PLATFORM_WDT585) {
			if(mContext != null) {
				return android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
						"adb_enabled", 0);
			} else {
				return 0;
			}
		}else if (mPlatform == PLATFORM_WDT520){
			if(mContext != null) {
				//System.out.println("###########################get2："+ android.provider.Settings.Global.getInt(mContext.getContentResolver(),"adb_enabled", 0));
				return   android.provider.Settings.Global.getInt(mContext.getContentResolver(),
						"adb_enabled", 0);
				//return android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
				//        "adb_enabled", 0);
			} else {
				return 0;
			}
		}else{
			return getStatusUSBDebug();
		}
	}
	private native int getStatusUSBDebug();


	/**
	 * 使能usb debug
	 * 说明：打开usb调试功能
	 */
	public void TurnOnUSBDebug() {
		Intent intent = new Intent(KAICOM_DISABLE_USB_DEBUG);
		intent.putExtra(KAICOM_DISABLE_USB_DEBUG, false);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}
	/**
	 * 屏蔽安装APK
	 * 说明：不允许安装第三方apk
	 */
	public void TurnOffInstallManager() {
		Intent intent = new Intent(KAICOM_DISABLE_INSTALL_PACKAGE);
		intent.putExtra(KAICOM_DISABLE_INSTALL_PACKAGE, 0);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}

	/**
	 * 屏蔽安装APK
	 * @return 1 允许， 0 不允许安装
	 */
	public int GetStatusInstallManager(){
		if (mPlatform == PLATFORM_WDT585) {
			if(mContext != null) {
				return android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
						"install_enabled", 1);
			} else {
				return 1;
			}
		}else if (mPlatform == PLATFORM_WDT520){
			if(mContext != null) {

				return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
						"install_enabled", 1);

			} else {
				return 1;
			}
		}
		else if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510){
			return getStatusInstallManager();
		}else{
			return -1;
		}
	}
	private native int getStatusInstallManager();


	/**
	 * 使能安装APK
	 * 说明：允许安装第三方apk
	 */
	public void TurnOnInstallManager() {
		Intent intent = new Intent(KAICOM_DISABLE_INSTALL_PACKAGE);
		intent.putExtra(KAICOM_DISABLE_INSTALL_PACKAGE, 1);
		if (mContext != null)
			mContext.sendBroadcast(intent);
	}

	/**
	 * 功能：设置apk安装界面
	 * 输入参数:	ACTION_DEFAULT = 0x0;				//恢复系统默认
	 *			ACTION_DIS_CANCEL_FINISH = 0X5A;	//都屏蔽
	 *			ACTION_DIS_CANCEL = 0XA;  			//屏蔽取消button
	 *			ACTION_DIS_FINISH = 0x50; 			//屏蔽完成button
	 *
	 */
	public void CfgInstallManager(int action){
		if (mPlatform == PLATFORM_WDT510){
			if (mContext != null){
				Intent intent = new Intent(KAICOM_INSTALL_APPS_BUTTONS);
				intent.putExtra(KAICOM_INSTALL_APPS_BUTTONS, action);
				mContext.sendBroadcast(intent);
			}
		}
		else{
			if (mContext != null){
				Intent intent = new Intent(KAICOM_DISABLE_INSTALL_PACKAGE);
				intent.putExtra(KAICOM_DISABLE_INSTALL_PACKAGE, action);
				mContext.sendBroadcast(intent);
			}
		}

	}
	/**
	 * 使能扫描按键消息
	 * 说明：在非扫描界面 检测扫描按键 进行扫描
	 */
	public void TurnOnScanMessage(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			Intent intent = new Intent(KAICOM_DISABLE_SCAN_MESSAGE);
			intent.putExtra(KAICOM_DISABLE_SCAN_MESSAGE, false);
			if (mContext != null)
				mContext.startService(intent);
		}else if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			Intent intent = new Intent(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE);
			intent.putExtra(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE,
					KAICOM_SCANNER_MODE_KEY);
			intent.putExtra(SYSTEM_KAICOM_SCANNED_ENDFIX, "\n");
			mContext.sendBroadcast(intent);
		}
	}
	/**
	 * 禁止扫描按键消息
	 * 说明：只在扫描界面 检测扫描按键 进行扫描
	 */
	public void TurnOffScanMessage(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			Intent intent = new Intent(KAICOM_DISABLE_SCAN_MESSAGE);
			intent.putExtra(KAICOM_DISABLE_SCAN_MESSAGE, true);
			if (mContext != null)
				mContext.startService(intent);
		}else if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			Intent intent = new Intent(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE);
			intent.putExtra(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE,
					KAICOM_SCANNER_MODE_LIB);
			mContext.sendBroadcast(intent);
		}
	}
	/**
	 * 使能扫描按键消息+enter
	 * 说明：扫描结果添加 回车 后缀
	 */
	public void TurnOnScanMessageAddEnter(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			Intent intent = new Intent(KAICOM_DISABLE_SCAN_MESSAGE_ENTER);
			intent.putExtra(KAICOM_DISABLE_SCAN_MESSAGE_ENTER, false);
			if (mContext != null)
				mContext.startService(intent);
		}else if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			Intent intent = new Intent(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE);
			intent.putExtra(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE,
					KAICOM_SCANNER_MODE_KEY);
			intent.putExtra(SYSTEM_KAICOM_SCANNED_ENDFIX, "\n");
			mContext.sendBroadcast(intent);

		}
	}
	/**
	 * 禁止扫描按键消息+enter
	 * 说明：扫描时不添加 回车 后缀
	 */
	public void TurnOffScanMessageAddEnter(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520) {
			Intent intent = new Intent(KAICOM_DISABLE_SCAN_MESSAGE_ENTER);
			intent.putExtra(KAICOM_DISABLE_SCAN_MESSAGE_ENTER, true);
			if (mContext != null)
				mContext.startService(intent);
		}else if(mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			Intent intent = new Intent(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE);
			intent.putExtra(SYSTEM_KAICOM_SCANNER_RUNTIME_MODE,
					KAICOM_SCANNER_MODE_KEY);
			intent.putExtra(SYSTEM_KAICOM_SCANNED_ENDFIX, "\0");
			mContext.sendBroadcast(intent);
		}
	}
	private native int getStatusScreenLock();


	/**
	 * 使能触摸解锁
	 * 说明：恢复触摸解锁
	 */
	public void TurnOnScreenLock(){
		Intent intent = new Intent(KAICOM_DISABLE_SCREEN_LOCK);
		intent.putExtra(KAICOM_DISABLE_SCREEN_LOCK, false);
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			if (mContext != null)
				mContext.sendBroadcast(intent);
		}else if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if (mContext != null)
				mContext.startService(intent);
		}
	}

	/**
	 *
	 * @return 1 使能解锁， 0禁止解锁
	 */
	public int GetStatusScreenLock(){
		if (mPlatform == PLATFORM_WDT585){
			if(mContext != null) {

				return android.provider.Settings.System.getInt(mContext.getContentResolver(),
						"screen_lock_on", 1);
			} else {
				return 1;
			}
		}else if (mPlatform == PLATFORM_WDT520){
			if(mContext != null) {
				//System.out.println("###########################get3："+ android.provider.Settings.System.getInt(mContext.getContentResolver(),"screen_lock_on", 1));
				return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
						"screen_lock_on", 1);
			} else {
				return 1;
			}
		}else if (mPlatform == PLATFORM_WDT420) {
			if (mContext != null)
				return Settings.Secure.getInt(mContext.getContentResolver(),
						KAICOM_DISABLE_SCREEN_LOCK, 1);
			else
			{
				return 1;
			}
		}
		else if (mPlatform == PLATFORM_WDT510) {
			return getStatusScreenLock();
		}
		else {
			return -1;
		}
	}

	/**
	 * 禁止触摸解锁
	 * 说明：待机唤醒不需要触摸解锁
	 */
	public void TurnOffScreenLock(){
		Intent intent = new Intent(KAICOM_DISABLE_SCREEN_LOCK);
		intent.putExtra(KAICOM_DISABLE_SCREEN_LOCK, true);
		if(mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			if (mContext != null)
				mContext.sendBroadcast(intent);
		}else if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if (mContext != null)
				mContext.startService(intent);
		}
	}



	/**
	 * 配置唤醒源
	 * @return 1 电源键唤醒	0 任意键唤醒
	 */
	public int GetStatusWakeUp(){
		return getStatusWakeUp();
	}
	private native int getStatusWakeUp();

	/**
	 * 配置电源键唤醒
	 * 说明：420不支持此功能
	 */
	private native void cfgPowerKeyWakeUp();
	public void CfgPowerKeyWakeUp(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT510){
			cfgPowerKeyWakeUp();
		}
	}
	/**
	 * 配置任意按键唤醒
	 * 说明：420不支持此功能
	 */
	private native void cfgAnyKeyWakeUp();
	public void CfgAnyKeyWakeUp(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT510){
			cfgAnyKeyWakeUp();
		}
	}

	/**
	 * 使能触摸屏
	 * 说明：触摸屏去掉触摸功能
	 */
	public void Disable_touch_screen(){
		if (mPlatform == PLATFORM_WDT420) {
			Intent intent = new Intent(KAICOM_DISABLE_TOUCH_SCREEN);
			intent.putExtra(KAICOM_DISABLE_TOUCH_SCREEN, true);
			if (mContext != null)
				mContext.sendBroadcast(intent);
		}else if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT510){
			enableTouchpanel(0);
		}

	}

	/**
	 * 禁止使用触摸屏
	 * 说明：恢复触摸屏触摸功能
	 */
	public void Enable_touch_screen()
	{
		if (mPlatform == PLATFORM_WDT420) {
			Intent intent = new Intent(KAICOM_DISABLE_TOUCH_SCREEN);
			intent.putExtra(KAICOM_DISABLE_TOUCH_SCREEN, false);
			if (mContext != null)
				mContext.sendBroadcast(intent);
		}
		else if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT510){
			enableTouchpanel(1);
		}


	}

	/**
	 * 触摸屏开关
	 * @return 1开，0 关
	 */
	public int GetStatusTouch_screen(){
		if(PLATFORM_WDT420 == GetPlatform()){
			return getStatusTouchscreen420();
		}else if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT510){
			return getStatusTouchscreen();
		}else{
			return -1;
		}
	}
	//420返回触摸屏使能状态
	private int getStatusTouchscreen420()
	{
		int ret = 0;
		if (mContext != null)
			ret = Settings.Secure.getInt(mContext.getContentResolver(),
					KAICOM_DISABLE_TOUCH_SCREEN, 0);

		return ret;
	}

	//585返回触摸屏使能状态
	private native int getStatusTouchscreen();
	//420触摸屏开关
	private native void enableTouchpanel(int flag);

	/**
	 * 420安装apk
	 * @param apkPath	为文件路径,请确保路径及文件的完整性;
	 * @param runAfterFinished 为true时,Apk安装完成之后会自动运行;为false时,Apk安装完成之后不运行
	 */
	public void KaicomInstallApkWithSilence(String apkPath,boolean runAfterFinished)
	{
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510
				||mPlatform == PLATFORM_WDT520
				||mPlatform == PLATFORM_WDT585) {

			//Log.v("jdh", "#######################/mnt/sdcard/abc.apk install");
			Intent intent = new Intent(KAICOM_INSTALL_PACKAGE_WITH_SILENCE);
			intent.putExtra(KAICOM_INSTALL_PACKAGE_EXTRA_APK_PATH, apkPath);
			if(runAfterFinished)
				intent.putExtra(KAICOM_INSTALL_PACKAGE_EXTRA_TIPS_FORMAT, 1);
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 开关420gps
	 * @param set true 开，false 关
	 */
	public void TurnOnOffGPS(boolean set)
	{
		if (mPlatform == PLATFORM_WDT420
				||mPlatform == PLATFORM_WDT510) {
			Intent intent = new Intent(KAICOM_CONFIG_GPS_STATUS);
			intent.putExtra(KAICOM_CONFIG_GPS_STATUS, set);
			if (mContext != null)
				mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 屏蔽 / 使能 浏览器访问
	 */
	public void TurnOnOffBrowser(boolean set){

		Intent intent = new Intent(KAICOM_DISABLE_BROWSER);
		intent.putExtra(KAICOM_DISABLE_BROWSER, set);
		if(mContext != null)
			mContext.sendBroadcast(intent);
	}


	/**
	 * 获取库的版本号
	 * @return
	 */
	public native String getlibVer();

	/**
	 ** 以下是针对荣旗的
	 ×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
	 **
	 **
	 */
	/**
	 *  差分包升级接口
	 *  path:升级包的绝对路径
	 */
	private static final String KAICOM_INTENT_ACTION_OTAUPDATE = "com.kaicom.intent.action.OtaService";

	public void KaicomOtaUpdate()
	{
		if (mPlatform == PLATFORM_WDT585
				|| mPlatform == PLATFORM_WDT520) {
			Intent intent = new Intent(KAICOM_INTENT_ACTION_OTAUPDATE);
			mContext.startService(intent);

		}else if (mPlatform == PLATFORM_WDT510){
			Intent intent = new Intent(KAICOM_INTENT_ACTION_OTAUPDATE);
			mContext.sendBroadcast(intent);
		}
	}



	/**
	 * 此函数只适用于 520平台
	 * @return
	 */
	public String GetTPType(){
		if (mPlatform == PLATFORM_WDT520) {
			return getTPDType();
		}else{
			return null;
		}
	}
	private native String getTPDType();

	private void rongQi_setRandomTsEventOn(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			//在此实现代码
			if(mContext != null){
				Intent intent = new Intent("com.kaili.setRandomEvent");
				intent.putExtra("com.kaili.setRandomEvent", true);
				mContext.startService(intent);
			}
		}
	}

	private void rongQi_setRandomTsEventOff(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			//在此实现代码
			if(mContext != null){
				Intent intent = new Intent("com.kaili.setRandomEvent");
				intent.putExtra("com.kaili.setRandomEvent", false);
				mContext.startService(intent);
			}
		}
	}

	private void rongQi_setRandomTsEventTimeOut(int minute){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			//在此实现代码
			if(mContext != null){
				Intent intent = new Intent("com.kaili.setRandomEventTimeOut");
				intent.putExtra("com.kaili.setRandomEventTimeOut", minute);
				mContext.startService(intent);
			}
		}
	}

	private void rongQi_SetSystemReboot(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext !=  null){
				mContext.startService(new Intent("com.kaili.reboot"));
			}
		}
	}

	private void rongQi_SetSystemShutdown(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext !=  null){
				mContext.startService(new Intent("com.kaili.shutdown"));
			}
		}
	}

	private void rongQi_SetSystemTime(String mm){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext != null){
				Intent intent = new Intent("com.kaili.setsystemtime");
				intent.putExtra("time", mm);
				mContext.startService(intent);
			}
		}
	}

	private void rongQi_SetMachineCode(String code){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){

			if(mContext != null){
				Intent intent = new Intent("com.kaili.SetMachineCode");
				intent.putExtra("com.kaili.SetMachineCode", code);
				mContext.startService(intent);
			}

		}
	}

	private String rongQi_GetMachineCode(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			BufferedReader buffer;
			String s;
			File file;
			try {
				InputStream in = new FileInputStream("/data/app/machine_code.txt");
				if(in!=null){
					buffer =new BufferedReader(new InputStreamReader(in));
					s = buffer.readLine();
					if(s==null){
						return null;//"please set machine code first";
					}else{
						return s;
					}
				}
				in.close();

			}catch (IOException e) {
				// TODO Auto-generated catch block
				//Log.v("Machine", e.getMessage());
			}
			return null;//"can not read";
		}
		return null;//"platform not fit";
	}

//jdh

//王斌：

//麦克风和听筒测试API

//jdh




	/**
	 *  MIC 听筒测试  开始
	 */
	public void RongQi_StartSoundTest(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext != null){
				Intent intent = new Intent("com.kaili.SoundTest");
				intent.putExtra("com.kaili.SoundTest", true);
				mContext.startService(intent);
			}
		}else if (mPlatform == PLATFORM_WDT510){
			Intent intent = new Intent("com.kaili.SoundTest");
			intent.putExtra("com.kaili.SoundTest", true);
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 *  MIC 听筒测试  结束
	 */
	public void RongQi_StopSoundTest(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext != null){
				Intent intent = new Intent("com.kaili.SoundTest");
				intent.putExtra("com.kaili.SoundTest", false);
				mContext.startService(intent);
			}
		}else if (mPlatform == PLATFORM_WDT510){

			Intent intent = new Intent("com.kaili.SoundTest");
			intent.putExtra("com.kaili.SoundTest", false);
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 *  触屏校准
	 */
	public void RongQi_SetupTouchPanelCalibrate(){
		if (mPlatform == PLATFORM_WDT585
				||mPlatform == PLATFORM_WDT520){
			if(mContext != null){
				mContext.startService(new Intent("com.kaili.TouchPanelCalibrate"));
			}
		}
	}
}
