package com.kuaibao.skuaidi.entry;

/**
 * 
 * 巴枪权限实体
 * 
 * @author wangqiang
 *
 */
public class ScanScope {

	private ScanType sj;
	private ScanType fj;
	private ScanType dj;
	private ScanType pj;
	private ScanType wtj;
	private ScanType qsj;

	public ScanType getSj() {
		return sj;
	}

	public void setSj(ScanType sj) {
		this.sj = sj;
	}

	public ScanType getFj() {
		return fj;
	}

	public void setFj(ScanType fj) {
		this.fj = fj;
	}

	public ScanType getDj() {
		return dj;
	}

	public void setDj(ScanType dj) {
		this.dj = dj;
	}

	public ScanType getPj() {
		return pj;
	}

	public void setPj(ScanType pj) {
		this.pj = pj;
	}

	public ScanType getWtj() {
		return wtj;
	}

	public void setWtj(ScanType wtj) {
		this.wtj = wtj;
	}

	public ScanType getQsj() {
		return qsj;
	}

	public void setQsj(ScanType qsj) {
		this.qsj = qsj;
	}

	public class ScanType {

		private int access;
		private String note;

		public int getAccess() {
			return access;
		}

		public void setAccess(int access) {
			this.access = access;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

	}

	@Override
	public String toString() {
		return "ScanScope [sj=" + sj + ", fj=" + fj + ", dj=" + dj + ", pj=" + pj + ", wtj=" + wtj + ", qsj=" + qsj + "]";
	}

}
