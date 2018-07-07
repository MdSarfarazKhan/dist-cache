package com.msrk.tools.cache.config;


import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.util.Constant;


/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@Configuration
public class CacheConfig {
	
	@Value("${cache.size:100}")
	private int size;
	
	@Bean
	public Cache<String,Data> cache(){
		return new Cache<String,Data>(size);
	}
	
	@Bean
	public Set<String> peers(){
		return new ConcurrentHashMap<String,String>().newKeySet();
	}
	@Bean
	public Queue<CacheItem<String,Data>> concurrentLinkedQueue()
	{
		return new ConcurrentLinkedQueue<CacheItem<String,Data>>();
	}
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
}
