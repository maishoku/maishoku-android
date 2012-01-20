package com.maishoku.android.models;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

public class Category {

	private String name_english;
	private String name_japanese;
	private Item[] items;
	
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
	
	public Item[] getItems() {
		return items;
	}
	
	public void setItems(Item[] items) {
		this.items = items;
	}

}
