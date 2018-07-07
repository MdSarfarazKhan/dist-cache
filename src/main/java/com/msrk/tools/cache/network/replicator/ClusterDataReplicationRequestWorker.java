package com.msrk.tools.cache.network.replicator;

import java.net.InetAddress;
import java.net.URI;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.msrk.tools.cache.util.Constant;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class ClusterDataReplicationRequestWorker implements Runnable{

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private Set<String> peers;
	private int retryCount=3;
	private int retryInterval=5000;
	private RestTemplate restTemplate;
	private int port;
	
	
	public ClusterDataReplicationRequestWorker(Set<String> peers,
			RestTemplate restTemplate,int port) {
		super();
		this.peers = peers;
		this.restTemplate = restTemplate;
		this.port=port;
	}
	

	public ClusterDataReplicationRequestWorker(Set<String> peers, int retryCount, int retryInterval,
			RestTemplate restTemplate) {
		super();
		this.peers = peers;
		this.retryCount = retryCount;
		this.retryInterval = retryInterval;
		this.restTemplate = restTemplate;
	}




	@Override
	public void run() {
		
		while(peers.isEmpty()&& retryCount-->0){
			try {
				Thread.yield();
				Thread.sleep(retryInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!peers.isEmpty()){
			for(String peer:peers){
				try {
				    StringBuilder builder = new StringBuilder( Constant.HTTP_PREFIX);
					builder.append(peer);
					builder.append(Constant.CLUSTER_DATA_REPLICATION_REQUEST_URL);
					builder.append("?host=");
					builder.append(InetAddress.getLocalHost().getHostAddress());
					builder.append(":");
					builder.append(port);
				    Integer size = restTemplate.getForObject(URI.create(builder.toString()), Integer.class);
					if(size > 0){
						LOG.info("Replication request send to peer "+peer+". "+size +" data to be replicated.");
						break;
					}else{
						LOG.info("Replication request send to peer "+peer+". "+size +" in adequate. checking with another peer.");
					}
				} catch (Exception e) {
					LOG.error("Error replication request from "+peer+". Message: "+e.getMessage());
				}
			}
		}else{
			LOG.warn("peer size.No replication node found");
		}

	}

}
