package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Option implements Serializable {

	private static final long serialVersionUID = -8333163851655073049L;
	
	private int id;
	private boolean item_based;
	private int price_delta;
	private String name_english;
	private String name_japanese;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isItem_based() {
		return item_based;
	}
	
	public void setItem_based(boolean item_based) {
		this.item_based = item_based;
	}
	
	public int getPrice_delta() {
		return price_delta;
	}
	
	public void setPrice_delta(int price_delta) {
		this.price_delta = price_delta;
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
	
	public String getName() {
		return API.getLanguage() == Language.ja ? name_japanese : name_english;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%+d円)", getName(), price_delta);
	}

}
