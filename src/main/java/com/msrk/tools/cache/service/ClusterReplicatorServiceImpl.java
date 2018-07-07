package com.msrk.tools.cache.service;

import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.network.replicator.ClusterDataReplicator;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@Component
public class ClusterReplicatorServiceImpl implements ClusterReplicatorService {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private Set<String> peers;

	@Autowired
	private Queue<CacheItem<String,Data>> queue;
	
	@Autowired
	private ClusterDataReplicator clusterDataReplicator;
	
	@Override
	public void replicateAcrossNodes(CacheItem<String, Data> cacheItem) {
		LOG.info("Replication request for key "+cacheItem.getKey()+" across "+peers.size()+" Nodes");
		if(peers.size()>0){
			synchronized(queue){
				queue.add(cacheItem);
				if(queue.size()==1)
					queue.notify();
			}
		}
	}

	@Override
	public void requestSync(String host) {
		clusterDataReplicator.processReplicationRequest(host);
	}

}
