package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Order implements Serializable {

	private static final long serialVersionUID = -2948845113506069991L;
	
	private int id;
	private int total_price;
	private PaymentMethod payment_method;
	private Position[] items;
	private Restaurant restaurant;
	private String confirmation_number;
	private String status;
	private String time_created;
	private User user;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTotal_price() {
		return total_price;
	}
	
	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}
	
	public PaymentMethod getPayment_method() {
		return payment_method;
	}
	
	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}
	
	public Position[] getItems() {
		return items;
	}
	
	public void setItems(Position[] items) {
		this.items = items;
	}
	
	public Restaurant getRestaurant() {
		return restaurant;
	}
	
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	
	public String getConfirmation_number() {
		return confirmation_number;
	}
	
	public void setConfirmation_number(String confirmation_number) {
		this.confirmation_number = confirmation_number;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTime_created() {
		return time_created;
	}
	
	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

}
