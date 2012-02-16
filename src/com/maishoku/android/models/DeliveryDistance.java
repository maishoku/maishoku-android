package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeliveryDistance implements Serializable {

	private static final long serialVersionUID = 8130896017535415168L;
	
	private double lower_bound;
	private double upper_bound;
	private int minimum_delivery;
	
	public double getLower_bound() {
		return lower_bound;
	}
	
	public void setLower_bound(double lower_bound) {
		this.lower_bound = lower_bound;
	}
	
	public double getUpper_bound() {
		return upper_bound;
	}
	
	public void setUpper_bound(double upper_bound) {
		this.upper_bound = upper_bound;
	}
	
	public int getMinimum_delivery() {
		return minimum_delivery;
	}
	
	public void setMinimum_delivery(int minimum_delivery) {
		this.minimum_delivery = minimum_delivery;
	}
	
	@Override
	public String toString() {
		return String.format("%d [%f, %f]", minimum_delivery, lower_bound, upper_bound);
	}

}
