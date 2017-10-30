package com.zpstudio.ui.adv.fragment;

public class Advertisement {
	String link;
	String phone_id;
	String content;
	String id;
	String lm;
	String titles;
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPhone_id() {
		return phone_id;
	}
	public void setPhone_id(String phone_id) {
		this.phone_id = phone_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLm() {
		return lm;
	}
	public void setLm(String lm) {
		this.lm = lm;
	}
	public String getTitles() {
		return titles;
	}
	public void setTitles(String titles) {
		this.titles = titles;
	}
	@Override
	public String toString() {
		return "Advertisement [link=" + link + ", phone_id=" + phone_id
				+ ", content=" + content + ", id=" + id + ", lm=" + lm
				+ ", titles=" + titles + "]";
	}
	
}
