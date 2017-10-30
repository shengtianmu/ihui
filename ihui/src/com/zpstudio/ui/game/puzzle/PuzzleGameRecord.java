package com.zpstudio.ui.game.puzzle;

public class PuzzleGameRecord {
	long id;
	long puzzlegame_id;
	String player;
	String endtime;
	int status;
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
	 * @return the puzzlegame_id
	 */
	public long getPuzzlegame_id() {
		return puzzlegame_id;
	}
	/**
	 * @param puzzlegame_id the puzzlegame_id to set
	 */
	public void setPuzzlegame_id(long puzzlegame_id) {
		this.puzzlegame_id = puzzlegame_id;
	}
	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * @param player the player to set
	 */
	public void setPlayer(String player) {
		this.player = player;
	}
	/**
	 * @return the endtime
	 */
	public String getEndtime() {
		return endtime;
	}
	/**
	 * @param endtime the endtime to set
	 */
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	} 
	
	
}
