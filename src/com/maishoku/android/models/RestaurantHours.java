package com.maishoku.android.models;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RestaurantHours implements Serializable {

	private static final long serialVersionUID = 5131107612152802914L;
	private static final int LENGTH = 4;
	private static final int HALF = LENGTH / 2;
	
	private String day_name;
	private String open_time;
	private String close_time;
	
	private static String formatTime(String time) {
		String _time;
		int length = time.length();
		if (length < LENGTH) {
			StringBuilder pad = new StringBuilder();
			for (int i = 0, n = LENGTH - length; i < n; i++) {
				pad.append('0');
			}
			_time = pad + time;
		} else {
			_time = time;
		}
		String hour = _time.substring(0, HALF);
		String minutes = _time.substring(HALF);
		return String.format("%s:%s", hour, minutes);
	}
	
	public String getDay_name() {
		return day_name;
	}
	
	public void setDay_name(String day_name) {
		this.day_name = day_name;
	}
	
	public String getOpen_time() {
		return formatTime(open_time);
	}
	
	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
	
	public String getClose_time() {
		return formatTime(close_time);
	}
	
	public void setClose_time(String close_time) {
		this.close_time = close_time;
	}

}
