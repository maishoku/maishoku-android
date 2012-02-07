package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.maishoku.android.API;
import com.maishoku.android.API.Language;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OptionSet implements Serializable {

	private static final long serialVersionUID = 3987955988782225585L;
	
	private int id;
	private String name_english;
	private String name_japanese;
	private Option[] options;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
	
	public Option[] getOptions() {
		return options;
	}
	
	public void setOptions(Option[] options) {
		this.options = options;
	}
	
	public String getName() {
		return API.getLanguage() == Language.ja ? name_japanese : name_english;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
