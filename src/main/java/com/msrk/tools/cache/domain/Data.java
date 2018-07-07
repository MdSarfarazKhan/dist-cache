package com.msrk.tools.cache.domain;

import java.io.Serializable;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class Data implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String value;
	private long createdOn;
	
	public Data(){
		
	}
	
	public Data(String value, long createdOn) {
		super();
		this.value = value;
		this.createdOn = createdOn;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
}
