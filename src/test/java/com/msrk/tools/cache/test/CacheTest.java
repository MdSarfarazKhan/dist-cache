package com.msrk.tools.cache.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.Data;

public class CacheTest extends BaseTest{
	
	@Autowired
	Cache<String,Data> cache;
	
	@Test
	public void testCacheSet(){
		long time = new Date().getTime();
		String key="Hello";
		String value = "World";
		Data expected = new Data(value, time);
		cache.set(key, expected);
		Data actual = cache.get(key);
		Assert.assertEquals(expected.getValue(),actual.getValue());
		Assert.assertEquals(expected.getCreatedOn(),actual.getCreatedOn());
	}
	
	@Test
	public void testCacheReSet(){
		String key="Hello";
		cache.set(key, new Data("test", new Date().getTime()));
		
		long time = new Date().getTime();
		String value = "World";
		Data expected = new Data(value, time);
		cache.set(key, expected);
		Data actual = cache.get(key);
		Assert.assertEquals(expected.getValue(),actual.getValue());
		Assert.assertEquals(expected.getCreatedOn(),actual.getCreatedOn());
	}

}
