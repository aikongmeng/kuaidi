package com.kuaibao.skuaidi.entry;

import com.kuaibao.skuaidi.activity.model.SortModel;
import com.kuaibao.skuaidi.customer.entity.Tags;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;
import java.util.List;

@Table(name = "mycustom_1")
public class MyCustom extends SortModel implements Serializable {
	
	public static final int GROUP_ACQUIESCENCE = 0;
	public static final int GROUP_BANED_RECORD = 1;//禁止录音
	@Transient
	private static final long serialVersionUID = 9025229897233471476L;

	//private static final long serialVersionUID = -3040820825626119467L;
	private String id;
	private String phone;
	private String name;
	private String address;
	private String note;
	private String time;
	private boolean isChecked = false;
	private int group;//组别
	private int _index;
	private boolean flag;
	private String tel;
	private List<Tags> tags; //标签信息

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public boolean getFlag() {
		return flag;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getId() {
		return id;
	}

	public int get_index() {
		return _index;
	}

	public void set_index(int _index) {
		this._index = _index;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "MyCustom [id=" + id + ", phone=" + phone + ", name=" + name + ", address=" + address + ", note=" + note
				+ ", time=" + time + ", isChecked=" + isChecked + ", group=" + group + ", _index=" + _index + ", flag="
				+ flag + "]";
	}
	
	
}
