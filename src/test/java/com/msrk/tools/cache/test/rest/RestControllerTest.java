package com.msrk.tools.cache.test.rest;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.test.BaseTest;
import com.msrk.tools.cache.util.Constant;
public class RestControllerTest extends BaseTest{

	TestRestTemplate restTemplate = new TestRestTemplate();
	
	HttpHeaders headers = new HttpHeaders();
	
	@Test
	public void testSetKey(){
		String key="hello";
		String value="world";
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(value, headers);

		restTemplate.exchange(
				createURLWithPort("/set/"+key),
				HttpMethod.POST,entity , String.class);
		
		entity = new HttpEntity<String>(null, headers);

		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/get/"+key),
				HttpMethod.GET, entity, String.class);
		
		Assert.assertEquals(value, response.getBody());

		
	}
	
	@Test
	public void testCacheItem(){
		
		String key="good";
		String value="morning";
		long time = new Date().getTime();
		
		CacheItem<String,Data> expected = new CacheItem<String, Data>(key, new Data(value, time));
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CacheItem<String,Data>> entity = new HttpEntity<>(expected, headers);

		restTemplate.exchange(
				createURLWithPort(Constant.CLUSTER_ITEM_REPLICATION_URL),
				HttpMethod.POST,entity , String.class);
		
		HttpEntity<String> entity1 = new HttpEntity<String>(null, headers);

		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/get/"+key),
				HttpMethod.GET, entity1, String.class);
		
		Assert.assertEquals(expected.getValue().getValue(), response.getBody());

		
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
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<CacheItem<String,Data>>> entity = new HttpEntity<>(list, headers);

		restTemplate.exchange(
				createURLWithPort(Constant.CLUSTER_DATA_REPLICATION_URL),
				HttpMethod.POST,entity , String.class);
		
		HttpEntity<String> entity1 = new HttpEntity<String>(null, headers);

		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/get/"+expected1.getKey()),
				HttpMethod.GET, entity1, String.class);
		
		Assert.assertEquals(expected1.getValue().getValue(), response.getBody());
		
		response = restTemplate.exchange(
				createURLWithPort("/get/"+expected2.getKey()),
				HttpMethod.GET, entity1, String.class);
		
		Assert.assertEquals(expected2.getValue().getValue(), response.getBody());
		
		response = restTemplate.exchange(
				createURLWithPort("/get/"+expected3.getKey()),
				HttpMethod.GET, entity1, String.class);
		
		Assert.assertEquals(expected3.getValue().getValue(), response.getBody());

		
	}
}
