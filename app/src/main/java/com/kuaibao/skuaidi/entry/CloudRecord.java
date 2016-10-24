package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @ClassName: CloudRecord
 * @Author 顾冬冬
 * @CreateDate 2015-7-10 下午1:48:49
 * @Description: 云呼录音模板参数
 */
public class CloudRecord implements Serializable{
	private static final long serialVersionUID = -8633520778544116249L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	/** 录音模板ID **/
	private String ivid = "";
	/** 是否已经选中 **/
	private boolean isChoose = false;
	/** 模板标题 **/
	private String title = "";
	/** 文件名称 **/
	private String fileName = "";
	/** 模板创建时间 **/
	private String createTime = "";
	/** 模板创建时间-时间戳 **/
	private long modifytime = 0L;
	/** 录音时长 **/
	private int voiceLength = 0;
	/** 录音保存的本地路径 **/
	private String pathLocal = "";
	/** 录音在服务器上的下载地址 **/
	private String pathService = "";
	/** 录音的审核状态 **/
	private String examineStatus = "";
	/** 添加到数据库的时间 **/
	private long time = 0L;
	/** 用户ID **/
	private String userID = "";
	/** 当前progress位置 **/
	private int currentProgress = 0;
	/** 排序的号码 **/
	private String sort_no = "";

	public String getIvid() {
		return ivid;
	}

	public void setIvid(String ivid) {
		this.ivid = ivid;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

    public long getModifytime() {
        return modifytime;
    }

    public void setModifytime(long modifytime) {
        this.modifytime = modifytime;
    }

    public int getVoiceLength() {
		return voiceLength;
	}

	public void setVoiceLength(int voiceLength) {
		this.voiceLength = voiceLength;
	}

	public String getPathLocal() {
		return pathLocal;
	}

	public void setPathLocal(String pathLocal) {
		this.pathLocal = pathLocal;
	}

	public String getPathService() {
		return pathService;
	}

	public void setPathService(String pathService) {
		this.pathService = pathService;
	}

	public String getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus;
	}

	public long getTime() {
		return time;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSort_no() {
		return sort_no;
	}

	public void setSort_no(String sort_no) {
		this.sort_no = sort_no;
	}

	public int getCurrentProgress() {
		return currentProgress;
	}

	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}

}
