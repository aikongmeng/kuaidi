package com.kuaibao.skuaidi.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CloudCallRecordUtility {
	// 音频获取源
	private int audioSource = MediaRecorder.AudioSource.MIC;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static int sampleRateInHz = 8000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	// 缓冲区字节大小
	private int bufferSizeInBytes = 0;
	private AudioRecord audioRecord = null;
	private MediaPlayer mPlayer = null;// 音乐播放类
	private boolean isRecord = false;// 设置正在录制的状态
	// 文件名
	private String file_name = String.valueOf(System.currentTimeMillis());
	private String fileName = file_name;
	// AudioName裸音频数据文件
	public String AudioName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/raw" + fileName + ".raw";
	// NewAudioName可播放的音频文件
	public String NewAudioName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/wav" + fileName + ".wav";

	private Context mContext;
	private Handler mHandler;
	private ImageView Left_image;
	private ImageView Right_image;
	private Thread iThread = null;
	private int volume = 0;// 录音时声音大小
	
	public CloudCallRecordUtility() {
		super();
	}

	/**
	 * @Author 顾冬冬
	 * @CreateDate 2015-7-9 上午11:27:00
	 */
	public CloudCallRecordUtility(Context context, Handler handler, ImageView Left_v, ImageView Right_v) {
		super();
		mContext = context;
		mHandler = handler;
		Left_image = Left_v;
		Right_image = Right_v;
	}
	
//	private void showTips(final String vol){
//		toast.setText(vol);
//		toast.show();
//	}

	private void creatAudioRecord() {
		// 获得缓冲区字节大小
		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		// 创建AudioRecord对象
		audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
		//Log.i("GUDD", "gudd audioRecord success");
	}

	
	public void startRecord() {
		creatAudioRecord();
		try {
			audioRecord.startRecording();
			// 让录制状态为true
			isRecord = true;
			// 开启音频文件写入线程
			iThread = new Thread(new Runnable() {

				@Override
				public void run() {
					writeDateTOFile();// 往文件中写入裸数据
				}
			});
			iThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// new Thread(new AudioRecordThread()).start();
	}

	public void stopRecord() {
		close();
	}

	private void close() {
		if (null != audioRecord) {
			try {
				//System.out.println("stopRecord");
				isRecord = false;// 停止文件写入
				audioRecord.stop();
				audioRecord.release();// 释放资源
				audioRecord = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			iThread = null;
		}
		copyWaveFile(AudioName, NewAudioName);// 给裸数据加上头文件
		deleteRawFile();
	}

	// class AudioRecordThread implements Runnable {
	// @Override
	// public void run() {
	// writeDateTOFile();// 往文件中写入裸数据
	// }
	// }

	/** 删除临时源文件 **/
	public void deleteRawFile() {
		File file = new File(AudioName);
		if (file.exists())
			file.delete();
	}

	/** 删除录音文件 **/
	public void deleteWavFile() {
		File file = new File(NewAudioName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
	 * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
	 * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
	 */
	private void writeDateTOFile() {
		// new一个byte数组用来存一些字节数据，大小为缓冲区大小
		byte[] audiodata = new byte[bufferSizeInBytes];
		FileOutputStream fos = null;
		int readsize = 0;
		try {
			File file_path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord");
			if (!file_path.exists()) {
				file_path.mkdirs();
			}
			// 开通输出流到文件
			fos = new FileOutputStream(AudioName);// 建立一个可存取字节的文件

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != fos) {
			while (isRecord) {
				readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
				if (readsize < 0)
					return;
				//Log.i("gudd", "vol ---->  "+getVolumeMax(readsize, audiodata));
				volume = 18 * getVolumeMax(readsize, audiodata) / 32768;
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						updateDisplay(Left_image, Right_image, volume);// 录音时修改动画

					}
				}, 200);

				if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
					try {
						fos.write(audiodata);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				fos.close();// 关闭写入流
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 这里得到可播放的音频文件
	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		// long totalDataLen = totalAudioLen + 36;
		long totalDataLen = totalAudioLen + 24;
		long longSampleRate = sampleRateInHz;
		int channels = 1;
		long byteRate = 16 * sampleRateInHz * channels / 8;
		byte[] data = new byte[bufferSizeInBytes];
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 24;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
	 * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有 自己特有的头文件。
	 */
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff); // 文件长度
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W'; // "WAVE"标志
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' '; // 过渡字节（不定）
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[22] = (byte) (channels & 0xFF);
		header[23] = (byte) (0xFF & channels >>> 8);
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (1 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';// 数据标记符data
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff); // 语音数据长度
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	/**
	 * @Title: getVolumeMax
	 * @Description: 获取录音最大音量
	 * @Author 顾冬冬
	 * @CreateDate 2015-7-9 上午10:40:43
	 * @Param @param r
	 * @Param @param bytes_pkg
	 * @Param @return
	 * @Return int
	 */
	private int getVolumeMax(int r, byte[] bytes_pkg) {
		int mShortArrayLenght = r / 2;
		short[] short_buffer = byteArray2ShortArray(bytes_pkg, mShortArrayLenght);
		int max = 0;
		if (r > 0) {
			for (int i = 0; i < mShortArrayLenght; i++) {
				if (Math.abs(short_buffer[i]) > max) {
					max = Math.abs(short_buffer[i]);
				}
			}
		}
		return max;
	}

	private short[] byteArray2ShortArray(byte[] data, int items) {
		short[] retVal = new short[items];
		for (int i = 0; i < retVal.length; i++)
			retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

		return retVal;
	}

	public void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(NewAudioName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** 播放本地录音 **/
	public void startPlaying(String audioLocalPath) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(audioLocalPath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 停止播放 **/
	public void stopPlaying() {
		if (null != mPlayer && mPlayer.isPlaying()) {
			mPlayer.stop();// 停止播放
			mPlayer.release();// 释放播放器占用的资源
			mPlayer = null;
		}
	}

	private void updateDisplay(ImageView L_View, ImageView R_View, int signalEMA) {

		switch (signalEMA) {
		case 0:
		case 1:
			L_View.setImageResource(R.drawable.rec_l1);
			R_View.setImageResource(R.drawable.rec_r1);
			break;
		case 2:
		case 3:
			L_View.setImageResource(R.drawable.rec_l2);
			R_View.setImageResource(R.drawable.rec_r2);
			break;
		case 4:
		case 5:
			L_View.setImageResource(R.drawable.rec_l3);
			R_View.setImageResource(R.drawable.rec_r3);
			break;
		case 6:
		case 7:
			L_View.setImageResource(R.drawable.rec_l4);
			R_View.setImageResource(R.drawable.rec_r4);
			break;
		case 8:
		case 9:
			L_View.setImageResource(R.drawable.rec_l5);
			R_View.setImageResource(R.drawable.rec_r5);
			break;
		case 10:
		case 11:
			L_View.setImageResource(R.drawable.rec_l6);
			R_View.setImageResource(R.drawable.rec_r6);
			break;
		case 12:
		case 13:
			L_View.setImageResource(R.drawable.rec_l7);
			R_View.setImageResource(R.drawable.rec_r7);
			break;
		case 14:
		case 15:
			L_View.setImageResource(R.drawable.rec_l8);
			R_View.setImageResource(R.drawable.rec_r8);
			break;
		case 16:
		case 17:
			L_View.setImageResource(R.drawable.rec_l9);
			R_View.setImageResource(R.drawable.rec_r9);
			break;
		default:
			L_View.setImageResource(R.drawable.rec_l9);
			R_View.setImageResource(R.drawable.rec_r9);
			break;
		}
	}

	/**
	 * @Title: readStream
	 * @Description: 将录音转成String
	 * @Author 顾冬冬
	 * @CreateDate 2015-7-10 下午2:41:26
	 * @Param @param path
	 * @Param @return
	 * @Param @throws Exception
	 * @Return String
	 */
	public String readStream(String path) throws Exception {
		String recordStr = "";
		File recordFile = new File(path);
		try {
			FileInputStream fis = new FileInputStream(recordFile);
			byte[] buffer = new byte[(int) recordFile.length()];
			fis.read(buffer);
			recordStr = Base64.encodeToString(buffer, Base64.NO_WRAP);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordStr;
	}

	public static void LOG(String logStr) {
		//Log.i("GUDD", logStr);
	}
}
