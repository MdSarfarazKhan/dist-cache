package com.msrk.tools.cache.network.replicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.domain.Cache;
import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.util.Constant;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class ClusterDataReplicatorWorker implements Runnable{

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	
	private RestTemplate restTemplate;
	private String host;
	private Cache<String, Data> cache;
	private int batchSize;
	
	
	
	public ClusterDataReplicatorWorker(RestTemplate restTemplate, String host, Cache<String, Data> cache,
			int batchSize) {
		super();
		this.restTemplate = restTemplate;
		this.host = host;
		this.cache = cache;
		this.batchSize = batchSize;
	}

	@Override
	public void run() {
		
		Iterator<Entry<String, Data>> it= cache.iterator();
		LOG.info("Replication started for host "+host);
		while(it.hasNext()){
			int count=0;
			List<CacheItem<String,Data>> list = new ArrayList<>();
			while(count++<batchSize && it.hasNext()){
				Entry<String,Data> entry = it.next();
				list.add(new CacheItem<String, Data>(entry.getKey(), entry.getValue()));
			}
			try {
				if(list.size()>0){
					restTemplate.postForLocation(Constant.HTTP_PREFIX+host + Constant.CLUSTER_DATA_REPLICATION_URL, list);
					LOG.info("Replicated "+list.size()+" on host "+host);
				}
			} catch (Exception e) {
				LOG.error("Error replicating data of size "+list.size()+" to "+host+". Error mesage:"+e.getMessage());
			}
		}
	}

}
