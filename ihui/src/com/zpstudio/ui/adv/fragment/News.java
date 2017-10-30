package com.zpstudio.ui.adv.fragment;

public class News {
//	 "news_titles": "李克强谈营改增:当前做",
//     "news_id": "32",
//     "news_page": "2"
	String news_titles;
	String news_id;
	String news_page;
	@Override
	public String toString() {
		return "News [news_titles=" + news_titles + ", news_id=" + news_id
				+ ", news_page=" + news_page + "]";
	}
	public String getNews_titles() {
		return news_titles;
	}
	public void setNews_titles(String news_titles) {
		this.news_titles = news_titles;
	}
	public String getNews_id() {
		return news_id;
	}
	public void setNews_id(String news_id) {
		this.news_id = news_id;
	}
	public String getNews_page() {
		return news_page;
	}
	public void setNews_page(String news_page) {
		this.news_page = news_page;
	}
}
