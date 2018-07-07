package com.msrk.tools.cache.network.replicator;

import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.util.Constant;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class ClusterDataItemReplicatorWorker implements Runnable {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private Set<String> peers;
	private Queue<CacheItem<String, Data>> queue;
	private RestTemplate restTemplate;

	public ClusterDataItemReplicatorWorker(Set<String> peers, Queue<CacheItem<String, Data>> queue,
			RestTemplate restTemplate) {
		super();
		this.peers = peers;
		this.queue = queue;
		this.restTemplate = restTemplate;
	}

	@Override
	public void run() {
		while (true) {
			/**
			 * Logic used only to efficiently use the thread. Queue
			 * implementation is ConnecurrentLinkedQueue, So need to have sync
			 * block in fetching element.
			 */
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						LOG.info("Queue is Empty. Going to wait mode. Notify when work available");
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			CacheItem<String, Data> item = queue.poll();
			LOG.info("Replicating " + item.getKey() + " across " + peers.size() + " Nodes");
			if (item != null) {
				for (String add : peers) {
					try {
						restTemplate.postForLocation(Constant.HTTP_PREFIX + add + Constant.CLUSTER_ITEM_REPLICATION_URL,
								item);
					} catch (Exception e) {
						LOG.error("Error replicating " + item.getKey() + " to " + add + ". Error mesage:"
								+ e.getMessage());
					}
				}
			}
		}

	}

}
