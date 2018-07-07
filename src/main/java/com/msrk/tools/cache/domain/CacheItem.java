package com.msrk.tools.cache.domain;

import java.io.Serializable;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class CacheItem<K,V> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;
	
	public CacheItem(){}
	
	public CacheItem(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
}
