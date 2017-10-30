package com.zpstudio.bbs.bean;

import java.util.List;


public class BBSPost {
	private long id;
	
	private int post_type;
	
	private String title;
	
	private String content;
	
	private List<String> medias;
	
	private List<ImageInfo> medias_thumb;
	
	private String extra_data;
	
	private long sectionId;
	
	private List<BBSReply> replies;
	
	private String owner;
	
	private String nickname;
	
	private String avatar;
	
	private int hitCount;
	
	private int locked;
	
	private int service_rank ;
	
	private int hotel_rank ;
	
	private int traffic_rank ;
	
	private int scenic_rank;
	
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
	
	
	public int getPost_type() {
		return post_type;
	}

	public void setPost_type(int post_type) {
		this.post_type = post_type;
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	
	/**
	 * @return the medias
	 */
	public List<String> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<String> medias) {
		this.medias = medias;
	}

	
	/**
	 * @return the medias_thumb
	 */
	public List<ImageInfo> getMedias_thumb() {
		return medias_thumb;
	}

	/**
	 * @param medias_thumb the medias_thumb to set
	 */
	public void setMedias_thumb(List<ImageInfo> medias_thumb) {
		this.medias_thumb = medias_thumb;
	}

	/**
	 * @return the extra_data
	 */
	public String getExtra_data() {
		return extra_data;
	}

	/**
	 * @param extra_data the extra_data to set
	 */
	public void setExtra_data(String extra_data) {
		this.extra_data = extra_data;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * @return the service_rank
	 */
	public int getService_rank() {
		return service_rank;
	}

	/**
	 * @param service_rank the service_rank to set
	 */
	public void setService_rank(int service_rank) {
		this.service_rank = service_rank;
	}

	/**
	 * @return the hotel_rank
	 */
	public int getHotel_rank() {
		return hotel_rank;
	}

	/**
	 * @param hotel_rank the hotel_rank to set
	 */
	public void setHotel_rank(int hotel_rank) {
		this.hotel_rank = hotel_rank;
	}

	/**
	 * @return the traffic_rank
	 */
	public int getTraffic_rank() {
		return traffic_rank;
	}

	/**
	 * @param traffic_rank the traffic_rank to set
	 */
	public void setTraffic_rank(int traffic_rank) {
		this.traffic_rank = traffic_rank;
	}

	/**
	 * @return the scenic_rank
	 */
	public int getScenic_rank() {
		return scenic_rank;
	}

	/**
	 * @param scenic_rank the scenic_rank to set
	 */
	public void setScenic_rank(int scenic_rank) {
		this.scenic_rank = scenic_rank;
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
	 * @return the sectionId
	 */
	public long getSectionId() {
		return sectionId;
	}

	/**
	 * @param sectionId the sectionId to set
	 */
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}

	/**
	 * @return the replies
	 */
	public List<BBSReply> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<BBSReply> replies) {
		this.replies = replies;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * @param hitCount the hitCount to set
	 */
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
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
