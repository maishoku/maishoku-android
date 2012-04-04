package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Item implements Serializable {

	private static final long serialVersionUID = 5816012272682951312L;
	
	private int id;
	private int price;
	private boolean available;
	private Category category;
	private OptionSet[] option_sets;
	private String name_english;
	private String name_japanese;
	private String description_english;
	private String description_japanese;
	private String default_image_url;
	private String thumbnail_image_url;
	private Topping[] toppings;
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public OptionSet[] getOption_sets() {
		return option_sets;
	}
	
	public void setOption_sets(OptionSet[] option_sets) {
		this.option_sets = option_sets;
	}
	
	public Topping[] getToppings() {
		return toppings;
	}
	
	public void setToppings(Topping[] toppings) {
		this.toppings = toppings;
	}
	
	public String getName() {
		return API.getLanguage() == Language.ja ? name_japanese : name_english;
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
	
	public String getDescription() {
		return API.getLanguage() == Language.ja ? description_japanese : description_english;
	}
	
	public String getDefault_image_url() {
		return default_image_url;
	}
	
	public void setDefault_image_url(String default_image_url) {
		this.default_image_url = default_image_url;
	}
	
	public String getThumbnail_image_url() {
		return thumbnail_image_url;
	}
	
	public void setThumbnail_image_url(String thumbnail_image_url) {
		this.thumbnail_image_url = thumbnail_image_url;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
