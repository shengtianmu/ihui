package com.zpstudio.datacenter.db;

public class Gift {
	long id;
	String title; 
	String titlePicOne; //picture
	double author;  //price,yuan 
	double lng;
	double lat;
	String http;
	double v_distance;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the titlePicOne
	 */
	public String getTitlePicOne() {
		return titlePicOne;
	}
	/**
	 * @param titlePicOne the titlePicOne to set
	 */
	public void setTitlePicOne(String titlePicOne) {
		this.titlePicOne = titlePicOne;
	}
	/**
	 * @return the author
	 */
	public double getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(double author) {
		this.author = author;
	}
	/**
	 * @return the lng
	 */
	public double getLng() {
		return lng;
	}
	/**
	 * @param lng the lng to set
	 */
	public void setLng(double lng) {
		this.lng = lng;
	}
	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public String getHttp() {
		return http;
	}
	public void setHttp(String http) {
		this.http = http;
	}
	/**
	 * @return the v_distance
	 */
	public double getV_distance() {
		return v_distance;
	}
	/**
	 * @param v_distance the v_distance to set
	 */
	public void setV_distance(double v_distance) {
		this.v_distance = v_distance;
	}
	
	
}

