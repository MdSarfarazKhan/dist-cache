package com.msrk.tools.cache.domain;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class Cache<K,V> {
	
	private Map<K,V> map;
	
	public Cache(){
		map = new ConcurrentHashMap<>();
	}
	
	public Cache(int size){
		map = new ConcurrentHashMap<>(size);
	}
	
	public void set(K k,V v){
		map.put(k, v);
	}
	public V get(K k)
	{
		return map.get(k);
	}
	
	public int size()
	{
		return map.size();
	}
	
	public Iterator<Entry<K, V>> iterator(){
		return map.entrySet().iterator();
	}
	
}
