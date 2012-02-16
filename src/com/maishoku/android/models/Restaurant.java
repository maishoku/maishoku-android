package com.maishoku.android.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Restaurant implements Serializable {

	private static final long serialVersionUID = -8267366911607387698L;
	
	private int minimumDelivery = Integer.MIN_VALUE;
	private String commaSeparatedCuisines;
	
	private int id;
	private int minimum_order;
	private double distance;
	private DeliveryTime delivery_time;
	private String name_japanese;
	private String name_english;
	private String phone_contact;
	private String phone_order;
	private String address;
	private String[] hours;
	private Cuisine[] cuisines;
	private DeliveryDistance[] delivery_distances;
	
	public int getMinimumDelivery() {
		if (minimumDelivery != Integer.MIN_VALUE) {
			return minimumDelivery;
		}
		minimumDelivery = minimum_order;
		Arrays.sort(delivery_distances, new Comparator<DeliveryDistance>() {
			@Override
			public int compare(DeliveryDistance a, DeliveryDistance b) {
				return Double.compare(a.getUpper_bound(), b.getUpper_bound());
			}
		});
		for (DeliveryDistance deliveryDistance: delivery_distances) {
			if (distance <= deliveryDistance.getUpper_bound()) {
				minimumDelivery = deliveryDistance.getMinimum_delivery();
				break;
			}
		}
		return minimumDelivery;
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
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
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
	
	public DeliveryDistance[] getDelivery_distances() {
		return delivery_distances;
	}
	
	public void setDelivery_distances(DeliveryDistance[] delivery_distances) {
		this.delivery_distances = delivery_distances;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
