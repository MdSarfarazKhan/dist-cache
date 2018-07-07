package com.msrk.tools.cache.service;

import java.util.List;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public interface CacheService <K,V>{
	
	public void add(K key,V value);
	
	public void add(CacheItem<K,V> cacheItem);
	public V get(K key);

	void add(List<CacheItem<String, Data>> cacheItems);

	int getCacheDataSize();
}
