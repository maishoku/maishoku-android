package com.maishoku.android.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Position implements Serializable {

	private static final long serialVersionUID = -3069497864486123374L;
	
	private Item item;
	private int quantity;
	private List<Option> options;
	private List<Topping> toppings;
	private User user;
	
	public Position(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
		options = new LinkedList<Option>();
		toppings = new LinkedList<Topping>();
	}
	
	public List<Option> getOptions() {
		return options;
	}
	
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public List<Topping> getToppings() {
		return toppings;
	}
	
	public void setToppings(List<Topping> toppings) {
		this.toppings = toppings;
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
	
	public String logString() {
		return String.format("%s: %d\n%s\n%s", item.getName(), quantity, toppings.toString(), options.toString());
	}
	
	@Override
	public String toString() {
		return String.format("%s: %d", item.getName(), quantity);
	}

}
