package com.kuaibao.skuaidi.entry;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;
import java.util.List;

@Table(name = "order")
public class Order implements Serializable {
	@Transient
	private static final long serialVersionUID = -971869769809265983L;
	/**
	 * 订单实体
	 */
	//private static final long serialVersionUID = 7445742116048980655L;
	// 订单号
	private String id;
	// 订单类型
	private String order_type;
	// 收件人姓名
	private String name;
	// 收件人电话
	private String phone;
	// 收件人地址
	private String address;
	//收件人省份
	private String receiptProvince;
	//收件人城市
	private String receiptCity;
	//收件人地区
	private String receiptCountry;
	//收件人的详细地址
	private String receiptDetailAddress;
	// 发件人电话
	private String senderPhone;
	// 发件人地址
	private String senderAddress;
	//发件人省份
	private String senderProvince;
	//发件人城市
	private String senderCity;
	//发件人地区
	private String senderCountry;
	//发件人的详细地址
	private String senderDetailAddress;
	// 发件人姓名
	private String senderName;
	// 物品信息
	private String articleInfo;
	// 地址头
	private String addressHead;

	// 备注
	private String ps;

	// 短信是否发送
	private String inform_sender_when_sign;

	private String time;
	// private int type;//定制服务，非定制服务
	private int newIm;// 数据类型待修改
	private int isread;
	// 运单号
	private String DeliverNo;
	private String type;
	// 运单状态
	private String order_state_cname;
	//是否录单成功
	private String is_send;
	//是否已经打印成功
	private String isPrint;
	//是否已采集实名信息
	private String isRealName;
	//打印电子面单的底单路径
	private String certificatePath;
	private int loc_order_id;
	private String voice_name;
	
	private int express_type;
	private String characters;

	public String getCharging_weight() {
		return charging_weight;
	}

	public void setCharging_weight(String charging_weight) {
		this.charging_weight = charging_weight;
	}

	private String charging_weight;//物品重量

	// 用户实际支付的金额
	private String real_pay;
	// 订单总额
	private String need_pay;
	// 用户支付的总额
	private String price;
	private String collection_amount;//代收货款

	private List<OrderIm> orders;

	private boolean ischeck = false;

	public boolean getIscheck() {
		return ischeck;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}

	private int isReadStateUpload;

	public String getInform_sender_when_sign() {
		return inform_sender_when_sign;
	}

	public void setInform_sender_when_sign(String inform_sender_when_sign) {
		this.inform_sender_when_sign = inform_sender_when_sign;
	}

	public int getIsReadStateUpload() {
		return isReadStateUpload;
	}

	public void setIsReadStateUpload(int isReadStateUpload) {
		this.isReadStateUpload = isReadStateUpload;
	}

	public String getVoice_name() {
		return voice_name;
	}

	public void setVoice_name(String voice_name) {
		this.voice_name = voice_name;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public int getNewIm() {
		return newIm;
	}

	public void setNewIm(int newIm) {
		this.newIm = newIm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIsread() {
		return isread;
	}

	public void setIsread(int isread) {
		this.isread = isread;
	}

	public String getDeliverNo() {
		return DeliverNo;
	}

	public void setDeliverNo(String deliverNo) {
		DeliverNo = deliverNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrder_state_cname() {
		return order_state_cname;
	}

	public void setOrder_state_cname(String order_state_cname) {
		this.order_state_cname = order_state_cname;
	}

	public List<OrderIm> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderIm> orders) {
		this.orders = orders;
	}

	public int getLoc_order_id() {
		return loc_order_id;
	}

	public void setLoc_order_id(int loc_order_id) {
		this.loc_order_id = loc_order_id;
	}

	public String getReal_pay() {
		return real_pay;
	}

	public void setReal_pay(String real_pay) {
		this.real_pay = real_pay;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public void setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getArticleInfo() {
		return articleInfo;
	}

	public void setArticleInfo(String articleInfo) {
		this.articleInfo = articleInfo;
	}

	public String getAddressHead() {
		return addressHead;
	}

	public void setAddressHead(String addressHead) {
		this.addressHead = addressHead;
	}
	
	
	public int getExpress_type() {
		return express_type;
	}

	public void setExpress_type(int express_type) {
		this.express_type = express_type;
	}

	public String getReceiptProvince() {
		return receiptProvince;
	}

	public String getReceiptCity() {
		return receiptCity;
	}

	public String getReceiptCountry() {
		return receiptCountry;
	}

	public String getSenderProvince() {
		return senderProvince;
	}

	public String getSenderCity() {
		return senderCity;
	}

	public String getSenderCountry() {
		return senderCountry;
	}

	public void setReceiptProvince(String receiptProvince) {
		this.receiptProvince = receiptProvince;
	}

	public void setReceiptCity(String receiptCity) {
		this.receiptCity = receiptCity;
	}

	public void setReceiptCountry(String receiptCountry) {
		this.receiptCountry = receiptCountry;
	}

	public void setSenderProvince(String senderProvince) {
		this.senderProvince = senderProvince;
	}

	public void setSenderCity(String senderCity) {
		this.senderCity = senderCity;
	}

	public void setSenderCountry(String senderCountry) {
		this.senderCountry = senderCountry;
	}
	public void setReceiptDetailAddress(String receiptDetailAddress) {
		this.receiptDetailAddress = receiptDetailAddress;
	}

	public String getReceiptDetailAddress() {
		return receiptDetailAddress;
	}

	public String getSenderDetailAddress() {
		return senderDetailAddress;
	}

	public void setSenderDetailAddress(String senderDetailAddress) {
		this.senderDetailAddress = senderDetailAddress;
	}

	public void setNeed_pay(String need_pay) {
		this.need_pay = need_pay;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getNeed_pay() {
		return need_pay;
	}

	public String getPrice() {
		return price;
	}

	public void setCollection_amount(String collection_amount){
		this.collection_amount = collection_amount;
	}

	public String getCollection_amount(){
		return collection_amount;
	}

	public void setIs_send(String is_send) {
		this.is_send = is_send;
	}

	public String getIs_send() {
		return is_send;
	}

	public String getIsPrint() {
		return isPrint;
	}

	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}

	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	public String getIsRealName() {
		return isRealName;
	}

	public void setIsRealName(String isRealName) {
		this.isRealName = isRealName;
	}

	public String getCharacters() {
		return characters;
	}

	public void setCharacters(String characters) {
		this.characters = characters;
	}

	@Override
	public String toString() {
		return "Order{" +
				"id='" + id + '\'' +
				", order_type='" + order_type + '\'' +
				", name='" + name + '\'' +
				", phone='" + phone + '\'' +
				", address='" + address + '\'' +
				", receiptProvince='" + receiptProvince + '\'' +
				", receiptCity='" + receiptCity + '\'' +
				", receiptCountry='" + receiptCountry + '\'' +
				", receiptDetailAddress='" + receiptDetailAddress + '\'' +
				", senderPhone='" + senderPhone + '\'' +
				", senderAddress='" + senderAddress + '\'' +
				", senderProvince='" + senderProvince + '\'' +
				", senderCity='" + senderCity + '\'' +
				", senderCountry='" + senderCountry + '\'' +
				", senderDetailAddress='" + senderDetailAddress + '\'' +
				", senderName='" + senderName + '\'' +
				", articleInfo='" + articleInfo + '\'' +
				", addressHead='" + addressHead + '\'' +
				", ps='" + ps + '\'' +
				", inform_sender_when_sign='" + inform_sender_when_sign + '\'' +
				", time='" + time + '\'' +
				", newIm=" + newIm +
				", isread=" + isread +
				", DeliverNo='" + DeliverNo + '\'' +
				", type='" + type + '\'' +
				", order_state_cname='" + order_state_cname + '\'' +
				", loc_order_id=" + loc_order_id +
				", voice_name='" + voice_name + '\'' +
				", express_type=" + express_type +
				", real_pay='" + real_pay + '\'' +
				", need_pay='" + need_pay + '\'' +
				", price='" + price + '\'' +
				", orders=" + orders +
				", ischeck=" + ischeck +
				", isReadStateUpload=" + isReadStateUpload +
				'}';
	}
}
