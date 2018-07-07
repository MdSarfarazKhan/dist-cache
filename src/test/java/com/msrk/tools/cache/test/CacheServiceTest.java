package com.msrk.tools.cache.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.service.CacheService;

public class CacheServiceTest extends BaseTest{

	@Autowired
	CacheService<String,Data> cacheService;
	
	
	
	@Test
	public void testCacheServiceWithoutCluster(){
		long time = new Date().getTime();
		String key="Hello";
		String value = "World";
		Data expected = new Data(value, time);
		cacheService.add(key, expected);
		Data actual = cacheService.get(key);
		Assert.assertEquals(expected.getValue(),actual.getValue());
		Assert.assertEquals(expected.getCreatedOn(),actual.getCreatedOn());
	}
	
	@Test
	public void testCacheReSetWithoutCluster(){
		String key="Hello";
		cacheService.add(key, new Data("test", new Date().getTime()));
		
		long time = new Date().getTime();
		String value = "World";
		Data expected = new Data(value, time);
		cacheService.add(key, expected);
		Data actual = cacheService.get(key);
		Assert.assertEquals(expected.getValue(),actual.getValue());
		Assert.assertEquals(expected.getCreatedOn(),actual.getCreatedOn());
	}
	
	
}
