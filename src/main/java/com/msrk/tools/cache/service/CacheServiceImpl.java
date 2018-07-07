package com.msrk.tools.cache.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@Component
public class CacheServiceImpl implements CacheService<String, Data> {

	@Autowired
	private Cache<String, Data> cache;

	@Override
	public void add(String key, Data value) {
		cache.set(key, value);

	}

	@Override
	public Data get(String key) {
		return cache.get(key);
	}


	@Override
	public int getCacheDataSize() {
		return cache.size();
	}

	
	@Override
	public void add(CacheItem<String, Data> cacheItem) {
		Data cData = cache.get(cacheItem.getKey());
		if (cData == null || cData.getCreatedOn() < cacheItem.getValue().getCreatedOn()) {
			cache.set(cacheItem.getKey(), cacheItem.getValue());
		}
	}

	@Override
	public void add(List<CacheItem<String, Data>> cacheItems) {
		for(CacheItem<String, Data> cacheItem:cacheItems){
			Data cData = cache.get(cacheItem.getKey());
			if(cData==null || cData.getCreatedOn()<cacheItem.getValue().getCreatedOn()){
				cache.set(cacheItem.getKey(),cacheItem.getValue());
			}
		}
	}
}
