package com.msrk.tools.cache.network.replicator;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@Component
public class ClusterDataReplicator {

	@Autowired
	private Set<String> peers;
	@Autowired
	private Queue<CacheItem<String, Data>> queue;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Cache<String, Data> cache;
	@Value("${server.port}")
	private int port;
	
	@Value("${cluster.replicator.threadpool.size:5}")
	private int poolSize; 

	private ExecutorService executor;

	@PostConstruct
	public void initReplication() {
		executor = Executors.newFixedThreadPool(poolSize);
		executor.execute(new ClusterDataItemReplicatorWorker(peers, queue, restTemplate));
		for (int i = 0; i < poolSize-2; i++) {
			executor.execute(new ClusterDataItemReplicatorWorker(peers, queue, restTemplate));
		}
		executor.execute(new ClusterDataReplicationRequestWorker(peers, restTemplate,port));
	}
	
	public void processReplicationRequest(String host) {
		if (peers.size() != 0) {
			Executors.newSingleThreadExecutor().execute(new ClusterDataReplicatorWorker(restTemplate, host, cache, 10));
		}
	}

	@PreDestroy
	public void cleanUp() {
		executor.shutdown();
	}

}
