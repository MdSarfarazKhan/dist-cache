package com.msrk.tools.cache.test.cluster;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.msrk.tools.cache.network.ClusterNodeNotifier;
import com.msrk.tools.cache.network.ClusterNodeRecieiver;


public class ClusterTest {
	

	
	@Test(timeout=5000)
	public void clusterMessageTest() throws InterruptedException, UnknownHostException{
		
		Set<String> peers= new HashSet<>();
		
		ClusterNodeRecieiver cnr = new ClusterNodeRecieiver(3,peers, "230.0.0.0", 5000);
		new Thread(cnr).start();
		
		ClusterNodeNotifier cnn = new ClusterNodeNotifier(4,8080, 1000, "230.0.0.0", 5000);
		new Thread(cnn).start();
		
		Thread.sleep(500);
		cnn.setRun(false);
		cnr.setRun(false);
		Assert.assertEquals(1, peers.size());
		Assert.assertEquals(true, peers.contains(InetAddress.getLocalHost().getHostAddress()+":"+8080));
	}
}
