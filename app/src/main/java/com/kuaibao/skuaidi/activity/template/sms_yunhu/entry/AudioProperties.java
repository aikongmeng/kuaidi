package com.kuaibao.skuaidi.activity.template.sms_yunhu.entry;

/**
 * Created by 顾冬冬
 */
public class AudioProperties {
    //8KHz采样、16比特量化的线性PCM语音信号的WAVE文件头格式表（共44字节）
    private String file_chunkID;//资源互换文件格式【RIFF】
    private long file_chunkSize = 0L;// 文件总长
    private String file_Format;// 文件格式【后缀】
    private String fmt_chunkID;//【fmt】
    private long fmt_chunkSize = 0L;//数值为16或18，18则最后又附加信息
    private short audioFormat;// 2 Bytes | 编码方式，一般为0x0001
    private short channel_number;// 2 Bytes | 声道数目，1--单声道；2--双声道
    private int samplerate;//4 Bytes | 采样率 | |
    private int byterate;//4 Bytes | 每秒播放字节数
    private short blockalign;// 2 Bytes | 数据块对齐单位(每个采样需要的字节数)| int blockalign=声道数*量化数/8
    private short bitspersample;//2 Bytes | 每个采样需要的bit数(单位：位)|int 量化数 int bitpersamples=8或16
    private String data_chunkID;//就是“data”了。
    private int data_chunkSize;//存储的是文件大小刨去44字节后的值
    private long play_time = 0L;// 文件播放时长【文件播放时长=(文件总长度-文件头长度)/每秒所需的字节数】

    public String getFile_chunkID() {
        return file_chunkID;
    }

    public void setFile_chunkID(String file_chunkID) {
        this.file_chunkID = file_chunkID;
    }

    public long getFile_chunkSize() {
        return file_chunkSize;
    }

    public void setFile_chunkSize(long file_chunkSize) {
        this.file_chunkSize = file_chunkSize;
    }

    public String getFile_Format() {
        return file_Format;
    }

    public void setFile_Format(String file_Format) {
        this.file_Format = file_Format;
    }

    public String getFmt_chunkID() {
        return fmt_chunkID;
    }

    public void setFmt_chunkID(String fmt_chunkID) {
        this.fmt_chunkID = fmt_chunkID;
    }

    public long getFmt_chunkSize() {
        return fmt_chunkSize;
    }

    public void setFmt_chunkSize(long fmt_chunkSize) {
        this.fmt_chunkSize = fmt_chunkSize;
    }

    public short getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(short audioFormat) {
        this.audioFormat = audioFormat;
    }

    public short getChannel_number() {
        return channel_number;
    }

    public void setChannel_number(short channel_number) {
        this.channel_number = channel_number;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public int getByterate() {
        return byterate;
    }

    public void setByterate(int byterate) {
        this.byterate = byterate;
    }

    public short getBlockalign() {
        return blockalign;
    }

    public void setBlockalign(short blockalign) {
        this.blockalign = blockalign;
    }

    public short getBitspersample() {
        return bitspersample;
    }

    public void setBitspersample(short bitspersample) {
        this.bitspersample = bitspersample;
    }

    public String getData_chunkID() {
        return data_chunkID;
    }

    public void setData_chunkID(String data_chunkID) {
        this.data_chunkID = data_chunkID;
    }

    public int getData_chunkSize() {
        return data_chunkSize;
    }

    public void setData_chunkSize(int data_chunkSize) {
        this.data_chunkSize = data_chunkSize;
    }

    public long getPlay_time() {
        return play_time;
    }

    public void setPlay_time(long play_time) {
        this.play_time = play_time;
    }
}
