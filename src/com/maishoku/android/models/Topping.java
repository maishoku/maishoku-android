package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Topping implements Serializable {

	private static final long serialVersionUID = -550133657260474331L;
	
	private int id;
	private int price_fixed;
	private double price_percentage;
	private String name_english;
	private String name_japanese;
	private String description_english;
	private String description_japanese;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPrice_fixed() {
		return price_fixed;
	}
	
	public void setPrice_fixed(int price_fixed) {
		this.price_fixed = price_fixed;
	}
	
	public double getPrice_percentage() {
		return price_percentage;
	}
	
	public void setPrice_percentage(double price_percentage) {
		this.price_percentage = price_percentage;
	}
	
	public String getName_english() {
		return name_english;
	}
	
	public void setName_english(String name_english) {
		this.name_english = name_english;
	}
	
	public String getName_japanese() {
		return name_japanese;
	}
	
	public void setName_japanese(String name_japanese) {
		this.name_japanese = name_japanese;
	}
	
	public String getDescription_english() {
		return description_english;
	}
	
	public void setDescription_english(String description_english) {
		this.description_english = description_english;
	}
	
	public String getDescription_japanese() {
		return description_japanese;
	}
	
	public void setDescription_japanese(String description_japanese) {
		this.description_japanese = description_japanese;
	}
	
	public String getName() {
		return API.getLanguage() == Language.ja ? name_japanese : name_english;
	}
	
	public String getDescription() {
		return API.getLanguage() == Language.ja ? description_japanese : description_english;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%+d円)", getName(), price_fixed);
	}

}
