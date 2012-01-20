package com.maishoku.android;

public class Result<T> {

	public final boolean success;
	public final String message;
	public final T resource;
	
	public Result(boolean success, String message, T resource) {
		this.success = success;
		this.message = message;
		this.resource = resource;
	}

}
