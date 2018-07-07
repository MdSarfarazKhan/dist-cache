package com.msrk.tools.cache.test.cluster;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.network.replicator.ClusterDataItemReplicatorWorker;
import com.msrk.tools.cache.network.replicator.ClusterDataReplicationRequestWorker;
import com.msrk.tools.cache.network.replicator.ClusterDataReplicatorWorker;
import com.msrk.tools.cache.util.Constant;

import org.junit.Assert;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ClusterDataReplicatorsTest {

	
	RestTemplate restTemplate=new RestTemplate();
	MockRestServiceServer mockServer;
	ObjectMapper mapper= new ObjectMapper();
	
	@Test(timeout=5000)
	public void testDataReplicatorRequestWorker() throws InterruptedException, ExecutionException, UnknownHostException{
		MockRestServiceServer mockServer =
				  MockRestServiceServer.bindTo(restTemplate).build();
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+"10.10.3.52:8901"+Constant.CLUSTER_DATA_REPLICATION_REQUEST_URL+"?host="+InetAddress.getLocalHost().getHostAddress()+":9901"))
		  .andRespond(withSuccess());
		Set<String> set= new HashSet<>();
		set.add("10.10.3.52:8901");
		ClusterDataReplicationRequestWorker worker = new ClusterDataReplicationRequestWorker(set,restTemplate,9901);
		Future f = Executors.newSingleThreadExecutor().submit(worker);
		f.get();
		mockServer.verify();
	}
	
	@Test(timeout=5000)
	public void testDataItemReplicatorWorker() throws InterruptedException, ExecutionException, UnknownHostException, JsonProcessingException{
		
		Set<String> set= new HashSet<>();
		set.add("10.10.3.52:8901");
		set.add("10.10.3.51:8901");
		
		Queue<CacheItem<String,Data>> queue = new ConcurrentLinkedQueue<CacheItem<String,Data>>();
		
		String key="good";
		String value="morning";
		long time = new Date().getTime();		
		CacheItem<String,Data> expected1 = new CacheItem<String, Data>(key, new Data(value, time));
		
		key="good1";
		value="morning1";
		CacheItem<String,Data> expected2 = new CacheItem<String, Data>(key, new Data(value, time));
				
		MockRestServiceServer mockServer =
				  MockRestServiceServer.bindTo(restTemplate).build();
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+"10.10.3.52:8901"+Constant.CLUSTER_ITEM_REPLICATION_URL))
		.andExpect(method(HttpMethod.POST))
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))                
		   .andExpect(content().string(mapper.writeValueAsString(expected1)))
		   .andRespond(withSuccess());
				  
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+"10.10.3.51:8901"+Constant.CLUSTER_ITEM_REPLICATION_URL))
		.andExpect(method(HttpMethod.POST))
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))                
		   .andExpect(content().string(mapper.writeValueAsString(expected1)))
		   .andRespond(withSuccess());
		
		ClusterDataItemReplicatorWorker worker = new ClusterDataItemReplicatorWorker(set,queue,restTemplate);
		Future f = Executors.newSingleThreadExecutor().submit(worker);
		Assert.assertEquals(0, queue.size());
		queue.add(expected1);
		Assert.assertEquals(1, queue.size());
		synchronized (queue) {
			queue.notify();
		}
		Thread.sleep(1000);
		Assert.assertEquals(0, queue.size());
		mockServer =
				  MockRestServiceServer.bindTo(restTemplate).build();
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+"10.10.3.52:8901"+Constant.CLUSTER_ITEM_REPLICATION_URL))
		.andExpect(method(HttpMethod.POST))
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))                
		   .andExpect(content().string(mapper.writeValueAsString(expected2)))
		   .andRespond(withSuccess());
		
		  
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+"10.10.3.51:8901"+Constant.CLUSTER_ITEM_REPLICATION_URL))
		.andExpect(method(HttpMethod.POST))
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))                
		   .andExpect(content().string(mapper.writeValueAsString(expected2)))
		   .andRespond(withSuccess());
		queue.add(expected2);
		synchronized (queue) {
			queue.notify();
		}
		Thread.sleep(1000);
		Assert.assertEquals(0, queue.size());
		mockServer.verify();
	}
	
	@Test
	public void testClusterDataReplicator() throws JsonProcessingException, InterruptedException, ExecutionException{
		
		String key="good";
		String value="morning";
		long time = new Date().getTime();		
		CacheItem<String,Data> expected1 = new CacheItem<String, Data>(key, new Data(value, time));
		
		Cache<String,Data> cache =  new Cache<String,Data>();
		cache.set(key, new Data(value, time));
				
		List<CacheItem<String,Data>> list = new ArrayList<>();
		list.add(expected1);
		String host = "10.10.3.52:8901";
		
		MockRestServiceServer mockServer =
				  MockRestServiceServer.bindTo(restTemplate).build();
		mockServer.expect(requestTo(Constant.HTTP_PREFIX+host+Constant.CLUSTER_DATA_REPLICATION_URL))
		.andExpect(method(HttpMethod.POST))
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))                
		   .andExpect(content().string(mapper.writeValueAsString(list)))
		   .andRespond(withSuccess());
		ClusterDataReplicatorWorker worker = new ClusterDataReplicatorWorker(restTemplate, host, cache, 10);
		Future f = Executors.newSingleThreadExecutor().submit(worker);
		f.get();
		mockServer.verify();
		
	}
}
