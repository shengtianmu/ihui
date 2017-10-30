package com.zpstudio.datacenter.db;

public class TurnTableGame {
	long id;
	double total_bet;
	double total_winning_price;
	double bet;
	String image;
	
	
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
	 * @return the total_bet
	 */
	public double getTotal_bet() {
		return total_bet;
	}

	/**
	 * @param total_bet the total_bet to set
	 */
	public void setTotal_bet(double total_bet) {
		this.total_bet = total_bet;
	}

	/**
	 * @return the total_winning_price
	 */
	public double getTotal_winning_price() {
		return total_winning_price;
	}

	/**
	 * @param total_winning_price the total_winning_price to set
	 */
	public void setTotal_winning_price(double total_winning_price) {
		this.total_winning_price = total_winning_price;
	}

	/**
	 * @return the bet
	 */
	public double getBet() {
		return bet;
	}
	/**
	 * @param bet the bet to set
	 */
	public void setBet(double bet) {
		this.bet = bet;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

}
