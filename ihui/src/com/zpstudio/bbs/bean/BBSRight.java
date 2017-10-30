package com.zpstudio.bbs.bean;

import java.util.List;

public class BBSRight {
	long post_id;
	int type;
	List<String> whiteList;
	List<String> blackList;
	/**
	 * @return the post_id
	 */
	public long getPost_id() {
		return post_id;
	}
	/**
	 * @param post_id the post_id to set
	 */
	public void setPost_id(long post_id) {
		this.post_id = post_id;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the whiteList
	 */
	public List<String> getWhiteList() {
		return whiteList;
	}
	/**
	 * @param whiteList the whiteList to set
	 */
	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}
	/**
	 * @return the blackList
	 */
	public List<String> getBlackList() {
		return blackList;
	}
	/**
	 * @param blackList the blackList to set
	 */
	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}
	
	
}
