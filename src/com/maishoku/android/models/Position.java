package com.maishoku.android.models;

public class Position {

	private Item item;
	private int quantity;
	private User user;
	
	public Position(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %d", item.getName(), quantity);
	}

}
