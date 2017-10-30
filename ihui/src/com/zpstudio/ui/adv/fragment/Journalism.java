package com.zpstudio.ui.adv.fragment;

import java.io.Serializable;

public class Journalism implements Serializable {
//	"id": "8",
//    "titles": "江浙沪远离喧嚣的木屋酒店：上海77熏衣岛",
//    "news_pic": "http://www.02727.com.cn/upfiles/shoptitlepic/1/20160126170350574.png",
//    "source": "新浪",
//    "create_time": "2016-01-26 17:03:52",
//    "tj": ""
	String id;
	String titles;
	String news_pic;
	String source;
	String create_time;
	String tj;
	
//	   "admin_city": "上海",
//     "valid_end": "2017-12-21 00:00:00",
//     "content": "http://www.02727.com.cn:8080/adv_uploads/zhuanqian01.jpg",
//     "link": "http://www.02727.com.cn/advertisement.php?id=195",
//     "credit": "1000000000"
	String admin_city;
	String valid_end;
	String content;
	String link;
	String credit;
	boolean is;
	
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
	public boolean isIs() {
		return is;
	}
	public void setIs(boolean is) {
		this.is = is;
	}
	public String getTj() {
		return tj;
	}
	public void setTj(String tj) {
		this.tj = tj;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitles() {
		return titles;
	}
	public void setTitles(String titles) {
		this.titles = titles;
	}
	public String getNews_pic() {
		return news_pic;
	}
	public void setNews_pic(String news_pic) {
		this.news_pic = news_pic;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "Journalism [id=" + id + ", titles=" + titles + ", news_pic="
				+ news_pic + ", source=" + source + ", create_time="
				+ create_time + ", tj=" + tj + ", admin_city=" + admin_city
				+ ", valid_end=" + valid_end + ", content=" + content
				+ ", link=" + link + ", credit=" + credit + ", is=" + is + "]";
	}
}
