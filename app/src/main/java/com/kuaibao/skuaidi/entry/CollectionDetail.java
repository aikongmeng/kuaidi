package com.kuaibao.skuaidi.entry;

import java.util.List;

public class CollectionDetail {

	private String order_number;
	private String trade_number;
	private String money;
	private String pay_time;
	private String pay_method;
	private String buyer_id;
	private String status;
	private String is_add;
	private List<Express_List> express_list;
	private Real_info real_info;

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getTrade_number() {
		return trade_number;
	}

	public void setTrade_number(String trade_number) {
		this.trade_number = trade_number;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIs_add() {
		return is_add;
	}

	public void setIs_add(String is_add) {
		this.is_add = is_add;
	}

	public List<Express_List> getExpress_list() {
		return express_list;
	}

	public void setExpress_list(List<Express_List> express_list) {
		this.express_list = express_list;
	}

	public Real_info getReal_info() {
		return real_info;
	}

	public void setReal_info(Real_info real_info) {
		this.real_info = real_info;
	}

	public class Express_List {
		private String express_number;
		private int is_upload;

		public String getExpress_number() {
			return express_number;
		}

		public void setExpress_number(String express_number) {
			this.express_number = express_number;
		}

		public int getIs_upload() {
			return is_upload;
		}

		public void setIs_upload(int is_upload) {
			this.is_upload = is_upload;
		}

	}
	
	public class Real_info{
		String nickname;//昵称
		String status;
		String instruction;//认证信息
		String url;
		String buyer_method;// 支付来源
		String buyer_openid;// 支付ID
		
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getInstruction() {
			return instruction;
		}
		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getBuyer_method() {
			return buyer_method;
		}
		public void setBuyer_method(String buyer_method) {
			this.buyer_method = buyer_method;
		}
		public String getBuyer_openid() {
			return buyer_openid;
		}
		public void setBuyer_openid(String buyer_openid) {
			this.buyer_openid = buyer_openid;
		}
		
		
	}

}
