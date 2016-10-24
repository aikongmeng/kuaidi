package kaicom.android.app;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.motorolasolutions.adc.decoder.BarCodeReader;

import kaicom.android.app.KaicomJNI.ScanCallBack;
public class Scanner2D implements BarCodeReader.DecodeCallback {
	private BarCodeReader bcr = null;
	private int trigMode = BarCodeReader.ParamVal.LEVEL;

	static final int STATE_IDLE = 0;
	static final int STATE_DECODE = 1;
	static final int STATE_HANDSFREE = 2;
	private int state = STATE_IDLE;

	private int mTimeOut =10;
	private Handler mHander =new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(state!=STATE_IDLE)
			{
				doStop();
			}
		}

	};
	ScanCallBack mScan;

	static
	{
		System.loadLibrary("IAL");
		System.loadLibrary("SDL");
		System.loadLibrary("barcodereader44");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDecodeComplete(int symbology, int length, byte[] data,
								 BarCodeReader reader) {
		// TODO Auto-generated method stub
		if (state == STATE_DECODE)
			state = STATE_IDLE;

		if (length > 0) {
			if (isHandsFree() == false && isAutoAim() == false)
				bcr.stopDecode();
			if (symbology == 0x99) // type 99?
			{
				symbology = data[0];
				int n = data[1];
				int s = 2;
				int d = 0;
				int len = 0;
				byte d99[] = new byte[data.length];
				for (int i = 0; i < n; ++i) {
					s += 2;
					len = data[s++];
					System.arraycopy(data, s, d99, d, len);
					s += len;
					d += len;
				}
				d99[d] = 0;
				data = d99;
			}
			/*
			 * dspStat("[" + decodes + "] type: " + symbology + " len: " +
			 * length); dspData(new String(data));
			 */
			//data[length]=0;
			//String str =new 
			////System.out.println(new String(data));
			if(mScan!=null)
			{
				String str = new String(data);
				str = str.substring(0, length);
				mScan.onScanResults(str, symbology);
				mScan.onScanResults(str);
			}
		} else // no-decode
		{

			switch (length) {
				case BarCodeReader.DECODE_STATUS_TIMEOUT:
					//System.out.println("decode timed out");
					break;

				case BarCodeReader.DECODE_STATUS_CANCELED:
					//System.out.println("decode cancelled");
					break;

				case BarCodeReader.DECODE_STATUS_ERROR:
				default:
					//System.out.println("decode failed");
					break;
			}
		}
		mHander.removeMessages(0);
	}

	@Override
	public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
		// TODO Auto-generated method stub

	}
	// ----------------------------------------
	private boolean isHandsFree() {
		return (trigMode == BarCodeReader.ParamVal.HANDSFREE);
	}

	// ----------------------------------------
	private boolean isAutoAim() {
		return (trigMode == BarCodeReader.ParamVal.AUTO_AIM);
	}
	private int doSetParam(int num, int val) {
		String s = "";
		int ret = bcr.setParameter(num, val);
		if (ret != BarCodeReader.BCR_ERROR) {
			if (num == BarCodeReader.ParamNum.PRIM_TRIG_MODE) {
				trigMode = val;
				if (val == BarCodeReader.ParamVal.HANDSFREE) {
					s = "HandsFree";
				} else if (val == BarCodeReader.ParamVal.AUTO_AIM) {
					s = "AutoAim";
					ret = bcr
							.startHandsFreeDecode(BarCodeReader.ParamVal.AUTO_AIM);
					if (ret != BarCodeReader.BCR_SUCCESS) {
						//System.out.println("AUtoAIm start FAILED");
					}
				} else if (val == BarCodeReader.ParamVal.LEVEL) {
					s = "Level";
				}
			} else if (num == BarCodeReader.ParamNum.IMG_VIDEOVF) {

			}
		} else
			s = " FAILED (" + ret + ")";

		//System.out.println("Set #" + num + " to " + val + " " + s);
		return ret;
	}

	private int setIdle() {
		int prevState = state;
		int ret = prevState; // for states taking time to chg/end

		state = STATE_IDLE;
		switch (prevState) {
			case STATE_HANDSFREE:
				resetTrigger();
				// fall thru
			case STATE_DECODE:

				//bcr.stopDecode();
				break;
			default:
				ret = STATE_IDLE;
		}
		return ret;
	}

	public void resetTrigger() {
		/*doSetParam(BarCodeReader.ParamNum.PRIM_TRIG_MODE,
				BarCodeReader.ParamVal.LEVEL);
		trigMode = BarCodeReader.ParamVal.LEVEL;*/
		doStart();
	}

	public void doStart() {
	//	android.util.Log.d("HUAWEI", "Scanner2D *** doStart()");
		//if (setIdle() != STATE_IDLE)
		//return;
		//mHander.sendEmptyMessageDelayed(0, mTimeOut*1000);

		if(bcr!=null)
		{
			bcr.startDecode(); // start decode (callback gets results)
			state = STATE_DECODE;
		}
		/*
		int ret = bcr.startHandsFreeDecode(BarCodeReader.ParamVal.HANDSFREE);
		if (ret == BarCodeReader.BCR_SUCCESS)
		{
			trigMode = BarCodeReader.ParamVal.HANDSFREE;
			state = STATE_HANDSFREE;
		}*/
	}
	public void doStop()
	{
		if(bcr!=null&&(state == STATE_DECODE))
		{
			bcr.stopDecode();
			state = STATE_IDLE;
		}

	}
	public void doOpen(Context mContext)
	{
		try
		{
			bcr = BarCodeReader.open(1,mContext.getApplicationContext()); // Android 4.3 and above
			if (bcr == null) {
				//android.util.Log.d("HUAWEI", "Scanner2D *** doOpen() *** bcr == null");
				//System.out.println("open failed");
				return;
			}
			bcr.setDecodeCallback(this);
			doSetParam(765, 0);
		}
		catch (Exception e)
		{
			//android.util.Log.v("sjb","open excp:" + e);
		}
	}
	public void doClose()
	{
		if (bcr != null) {
			setIdle();
			bcr.release();
			bcr = null;
		}
	}
	public void setParameter(int par)
	{
		doSetParam((par>>16)&0xffff,par&0xffff);
	}
	public boolean isScanning()
	{
		return state == STATE_DECODE || state == STATE_HANDSFREE;
	}
	public void setTimeout(int sec)
	{
		mTimeOut = sec;
	}

	public Scanner2D() {

	}
	public Scanner2D(ScanCallBack scan) {
		mScan = scan;
	}
}
