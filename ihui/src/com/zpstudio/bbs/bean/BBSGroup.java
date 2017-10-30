package com.zpstudio.bbs.bean;

import java.util.List;

public class BBSGroup {
	private long id;
	
	private String name;//版块名称
	
	private List<String> images;
	
	private String owner;//版块管理者
	
	private String nickname;
	
	private String avatar;
	
	private List<BBSSection> sections;//板块的帖子
	
	private int locked;
	
	private String createtime;
	
	private String refreshtime;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the sections
	 */
	public List<BBSSection> getSections() {
		return sections;
	}

	/**
	 * @param sections the sections to set
	 */
	public void setSections(List<BBSSection> sections) {
		this.sections = sections;
	}

	/**
	 * @return the createtime
	 */
	public String getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	

	/**
	 * @return the images
	 */
	public List<String> getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

	/**
	 * @return the locked
	 */
	public int getLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(int locked) {
		this.locked = locked;
	}

	/**
	 * @return the refreshtime
	 */
	public String getRefreshtime() {
		return refreshtime;
	}

	/**
	 * @param refreshtime the refreshtime to set
	 */
	public void setRefreshtime(String refreshtime) {
		this.refreshtime = refreshtime;
	}
	
	
	
	
}
