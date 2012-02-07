package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Restaurant implements Serializable {

	private static final long serialVersionUID = -8267366911607387698L;
	
	private int id;
	private int minimum_order;
	private DeliveryTime delivery_time;
	private String name_japanese;
	private String name_english;
	private String phone_contact;
	private String phone_order;
	private String address;
	private String commaSeparatedCuisines;
	private String[] hours;
	private Cuisine[] cuisines;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getMinimum_order() {
		return minimum_order;
	}
	
	public void setMinimum_order(int minimum_order) {
		this.minimum_order = minimum_order;
	}
	
	public DeliveryTime getDelivery_time() {
		return delivery_time;
	}
	
	public void setDelivery_time(DeliveryTime delivery_time) {
		this.delivery_time = delivery_time;
	}
	
	public String getName() {
		return API.getLanguage() == Language.ja ? name_japanese : name_english;
	}
	
	public String getName_japanese() {
		return name_japanese;
	}
	
	public void setName_japanese(String name_japanese) {
		this.name_japanese = name_japanese;
	}
	
	public String getName_english() {
		return name_english;
	}
	
	public void setName_english(String name_english) {
		this.name_english = name_english;
	}
	
	public String getPhone_contact() {
		return phone_contact;
	}
	
	public void setPhone_contact(String phone_contact) {
		this.phone_contact = phone_contact;
	}
	
	public String getPhone_order() {
		return phone_order;
	}
	
	public void setPhone_order(String phone_order) {
		this.phone_order = phone_order;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCommaSeparatedCuisines() {
		if (commaSeparatedCuisines == null) {
			StringBuilder sb = new StringBuilder();
			for (Cuisine cuisine: cuisines) {
				if (sb.length() != 0) {
					sb.append(", ");
				}
				sb.append(API.getLanguage() == Language.ja ? cuisine.getName_japanese() : cuisine.getName_english());
			}
			commaSeparatedCuisines = sb.toString();
		}
		return commaSeparatedCuisines;
	}
	
	public String[] getHours() {
		return hours;
	}
	
	public void setHours(String[] hours) {
		this.hours = hours;
	}
	
	public Cuisine[] getCuisines() {
		return cuisines;
	}
	
	public void setCuisines(Cuisine[] cuisines) {
		this.cuisines = cuisines;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
