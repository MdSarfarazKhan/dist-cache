package com.msrk.tools.cache.network;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@Component
public class ClusterNodeHandler {
	
	@Value("${server.port:8080}")
	private int serverPort;
	
	@Value("${cluster.join.refresh.interval:4000}")
	private int reNotifyPeriod;
	
	@Value("${cluster.join.multicast.ip:230.0.0.0}")
	private String ip;
	
	@Value("${cluster.join.multicast.port:4445}")
	private int port;
	
	@Autowired
	private Set<String> peers;
	@Value("${cluster.node.id:0}")
	private int nodeId;
	
	ExecutorService receiver = Executors.newSingleThreadExecutor();
	ExecutorService sender = Executors.newSingleThreadExecutor();
	
	@PostConstruct
	protected void init(){
		if(nodeId==0){
			nodeId=(int)Math.round(Math.random() * 100000) ;
		}
		ClusterNodeNotifier cnn = new ClusterNodeNotifier(nodeId,serverPort, reNotifyPeriod, ip, port);
		ClusterNodeRecieiver cnr = new ClusterNodeRecieiver(nodeId,peers, ip, port);
		sender.submit(new Thread(cnn));
		receiver.submit(new Thread(cnr));
		
	}
	
	@PreDestroy
	protected void cleanUp(){
		sender.shutdown();
		receiver.shutdown();
	}
	

}
