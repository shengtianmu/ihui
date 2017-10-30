package com.zpstudio.ui.adv.fragment;

public class Advert {
	private String adv_phone_id;
	private String admin_city;
	public String getAdv_phone_id() {
		return adv_phone_id;
	}
	public void setAdv_phone_id(String adv_phone_id) {
		this.adv_phone_id = adv_phone_id;
	}
	private String valid_end;
	private String content;
	private String link;
	private String credit;
	public String getAdmin_city() {
		return admin_city;
	}
	public void setAdmin_city(String admin_city) {
		this.admin_city = admin_city;
	}
	public String getValid_end() {
		return valid_end;
	}
	public void setValid_end(String valid_end) {
		this.valid_end = valid_end;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	@Override
	public String toString() {
		return "Advert [admin_city=" + admin_city + ", valid_end=" + valid_end
				+ ", content=" + content + ", link=" + link + ", credit="
				+ credit + "]";
	}
}
