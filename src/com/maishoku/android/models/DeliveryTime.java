package com.maishoku.android.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeliveryTime {

	private String value_japanese;
	private String value_english;
	
	public String getValue_japanese() {
		return value_japanese;
	}
	
	public void setValue_japanese(String value_japanese) {
		this.value_japanese = value_japanese;
	}
	
	public String getValue_english() {
		return value_english;
	}
	
	public void setValue_english(String value_english) {
		this.value_english = value_english;
	}
	
	@Override
	public String toString() {
		return API.getLanguage() == Language.ja ? value_japanese : value_english;
	}

}
