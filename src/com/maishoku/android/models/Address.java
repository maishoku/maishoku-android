package com.maishoku.android.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Address {

	private Integer id;
	private Integer frequency;
	private Double lat;
	private Double lon;
	private String first_date;
	private String last_date;
	private String address;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getFrequency() {
		return frequency;
	}
	
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	
	public Double getLat() {
		return lat;
	}
	
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	public Double getLon() {
		return lon;
	}
	
	public void setLon(Double lon) {
		this.lon = lon;
	}
	
	public String getFirst_date() {
		return first_date;
	}
	
	public void setFirst_date(String first_date) {
		this.first_date = first_date;
	}
	
	public String getLast_date() {
		return last_date;
	}
	
	public void setLast_date(String last_date) {
		this.last_date = last_date;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return this.address;
	}

}
