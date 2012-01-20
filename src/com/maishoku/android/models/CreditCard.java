package com.maishoku.android.models;

public class CreditCard {

	private int id;
	private String card_number;
	private String expiration_date;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCard_number() {
		return card_number;
	}
	
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}
	
	public String getExpiration_date() {
		return expiration_date;
	}
	
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}
	
	@Override
	public String toString() {
		return card_number;
	}

}
