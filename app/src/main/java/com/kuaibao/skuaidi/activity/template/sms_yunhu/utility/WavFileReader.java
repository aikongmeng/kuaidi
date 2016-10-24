package com.kuaibao.skuaidi.activity.template.sms_yunhu.utility;

import android.util.Log;

import com.kuaibao.skuaidi.activity.template.sms_yunhu.entry.AudioProperties;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileReader {

//    private static final String TAG = WavFileReader.class.getSimpleName();
    private static final String TAG = "gudd";

    public static DataInputStream mDataInputStream;
    public static WavFileHeader mWavFileHeader;

    public static AudioProperties openFile(String filepath) throws IOException {
        if (mDataInputStream != null) {
            closeFile();
        }
        FileInputStream fis = new FileInputStream(filepath);
        mDataInputStream = new DataInputStream(fis);
        return readHeader();
    }

    public static void closeFile() throws IOException {
        if (mDataInputStream != null) {
            mDataInputStream.close();
            mDataInputStream = null;
        }
    }

    public WavFileHeader getmWavFileHeader() {
        return mWavFileHeader;
    }

    public int readData(byte[] buffer, int offset, int count) {

        if (mDataInputStream == null || mWavFileHeader == null) {
            return -1;
        }

        try {
            int nbytes = mDataInputStream.read(buffer, offset, count);
            if (nbytes == -1) {
                return 0;
            }
            return nbytes;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static AudioProperties readHeader() {

        if (mDataInputStream == null) {
            return null;
        }

        AudioProperties audioProperties = new AudioProperties();

        WavFileHeader header = new WavFileHeader();

        byte[] intValue = new byte[4];
        byte[] shortValue  = new byte[2];

        try {
            header.mChunkID = "" + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte();
            Log.d(TAG, "Read file chunkID:"+header.mChunkID);
            audioProperties.setFile_chunkID(header.mChunkID);// RIFF

            mDataInputStream.read(intValue);
            header.mChunkSize = byteArrayToInt(intValue);
            Log.d(TAG, "Read file chunkSize:"+header.mChunkSize);
            audioProperties.setFile_chunkSize(header.mChunkSize);// 文件大小-long类型

            header.mFormat = "" + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte();
            Log.d(TAG, "Read file format:"+header.mFormat);
            audioProperties.setFile_Format(header.mFormat);// 文件格式-WAVE

            header.mSubChunk1ID = "" + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte();
            Log.d(TAG, "Read fmt chunkID:"+header.mSubChunk1ID);
            audioProperties.setFmt_chunkID(header.mSubChunk1ID);// fmt

            mDataInputStream.read(intValue);
            header.mSubChunk1Size = byteArrayToInt(intValue);
            Log.d(TAG, "Read fmt chunkSize:"+header.mSubChunk1Size);
            audioProperties.setFmt_chunkSize(header.mSubChunk1Size);// 编码位数

            mDataInputStream.read(shortValue);
            header.mAudioFormat = byteArrayToShort(shortValue);
            Log.d(TAG, "Read audioFormat:"+header.mAudioFormat);
            audioProperties.setAudioFormat(header.mAudioFormat);// 2 Bytes | 编码方式，一般为0x0001

            mDataInputStream.read(shortValue);
            header.mNumChannel = byteArrayToShort(shortValue);
            Log.d(TAG, "Read channel number:"+header.mNumChannel);
            audioProperties.setChannel_number(header.mNumChannel);// 声道

            mDataInputStream.read(intValue);
            header.mSampleRate = byteArrayToInt(intValue);
            Log.d(TAG, "Read samplerate:"+header.mSampleRate);
            audioProperties.setSamplerate(header.mSampleRate);// 采样率

            mDataInputStream.read(intValue);
            header.mByteRate = byteArrayToInt(intValue);
            Log.d(TAG, "Read byterate:"+header.mByteRate);
            audioProperties.setByterate(header.mByteRate);// 每秒播放字节数

            mDataInputStream.read(shortValue);
            header.mBlockAlign = byteArrayToShort(shortValue);
            Log.d(TAG, "Read blockalign:"+header.mBlockAlign);
            audioProperties.setBlockalign(header.mBlockAlign);// 2 Bytes | 数据块对齐单位(每个采样需要的字节数)| int blockalign=声道数*量化数/8

            mDataInputStream.read(shortValue);
            header.mBitsPerSample = byteArrayToShort(shortValue);
            Log.d(TAG, "Read bitspersample:"+header.mBitsPerSample);
            audioProperties.setBitspersample(header.mBitsPerSample);//2 Bytes | 每个采样需要的bit数(单位：位)|int 量化数 int bitpersamples=8或16

            header.mSubChunk2ID = "" + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte() + (char)mDataInputStream.readByte();
            Log.d(TAG, "Read data chunkID:"+header.mSubChunk2ID);
            audioProperties.setData_chunkID(header.mSubChunk2ID);//就是“data”了。

            mDataInputStream.read(intValue);
            header.mSubChunk2Size = byteArrayToInt(intValue);
            Log.d(TAG, "Read data chunkSize:"+header.mSubChunk2Size);
            audioProperties.setData_chunkSize(header.mSubChunk2Size);//存储的是文件大小刨去44字节后的值

            Log.d(TAG, "Read wav file success !");

            // 计算时间-单位：秒（s）
            int second = (int) ((audioProperties.getFile_chunkSize()-44)/audioProperties.getByterate());// 文件播放时长=(文件总长度-文件头长度)/每秒所需的字节数
            audioProperties.setPlay_time(second);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        mWavFileHeader = header;

        return audioProperties;
//        return true;
    }

    private static short byteArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
