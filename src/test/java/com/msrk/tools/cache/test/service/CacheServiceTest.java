package com.msrk.tools.cache.test.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.service.CacheService;
import com.msrk.tools.cache.test.BaseTest;

public class CacheServiceTest extends BaseTest{

	@Autowired
	private CacheService<String,Data> cacheService;
	
	@Test
	public void testSetKey(){
		String key="hello";
		String value="world";
		long time = new Date().getTime();
		Data extpected = new Data(value,time);
		
		cacheService.add(key, extpected);
		Data actual = cacheService.get(key);
		
		Assert.assertEquals(extpected.getValue(), actual.getValue());
		Assert.assertEquals(extpected.getCreatedOn(), actual.getCreatedOn());
		
	}
	
	@Test
	public void testCacheItem(){
		
		String key="good";
		String value="morning";
		long time = new Date().getTime();
		
		CacheItem<String,Data> expected = new CacheItem<String, Data>(key, new Data(value, time));
		
		cacheService.add(expected);
		Data actual = cacheService.get(key);
		
		Assert.assertEquals(expected.getValue().getValue(), actual.getValue());
		Assert.assertEquals(expected.getValue().getCreatedOn(), actual.getCreatedOn());
		
	}
	
	@Test
	public void testCacheData(){
		
		String key="good";
		String value="morning";
		long time = new Date().getTime();		
		CacheItem<String,Data> expected1 = new CacheItem<String, Data>(key, new Data(value, time));
		
		key="good1";
		value="morning1";
		CacheItem<String,Data> expected2 = new CacheItem<String, Data>(key, new Data(value, time));
		
		key="good2";
		value="morning3";
		CacheItem<String,Data> expected3 = new CacheItem<String, Data>(key, new Data(value, time));
		
		List<CacheItem<String,Data>> list = new ArrayList<>();
		list.add(expected1);
		list.add(expected2);
		list.add(expected3);
		
		cacheService.add(list);	
		Data actual = cacheService.get(expected1.getKey());
		
		Assert.assertEquals(expected1.getValue().getValue(), actual.getValue());
		Assert.assertEquals(expected1.getValue().getCreatedOn(), actual.getCreatedOn());
		
		actual = cacheService.get(expected2.getKey());
		
		Assert.assertEquals(expected2.getValue().getValue(), actual.getValue());
		Assert.assertEquals(expected2.getValue().getCreatedOn(), actual.getCreatedOn());
		actual = cacheService.get(expected3.getKey());
		
		Assert.assertEquals(expected3.getValue().getValue(), actual.getValue());
		Assert.assertEquals(expected3.getValue().getCreatedOn(), actual.getCreatedOn());

		
	}
	
	public void testCacheSize(){
		String key="good";
		String value="morning";
		long time = new Date().getTime();		
		CacheItem<String,Data> expected1 = new CacheItem<String, Data>(key, new Data(value, time));
		
		key="good1";
		value="morning1";
		CacheItem<String,Data> expected2 = new CacheItem<String, Data>(key, new Data(value, time));
		
		key="good2";
		value="morning3";
		CacheItem<String,Data> expected3 = new CacheItem<String, Data>(key, new Data(value, time));
		
		List<CacheItem<String,Data>> list = new ArrayList<>();
		list.add(expected1);
		list.add(expected2);
		list.add(expected3);
		cacheService.add(list);	
		Assert.assertEquals(3,cacheService.getCacheDataSize());
	}
}


