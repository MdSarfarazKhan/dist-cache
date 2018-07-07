package com.msrk.tools.cache.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public class ClusterNodeNotifier implements Runnable {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private int serverPort;
	private int reNotifyPeriod;
	private int nodeId;
	private String ip;
	private int port;

	private DatagramSocket socket;
	private InetAddress group;
	private byte[] buf;
	private boolean run=true;
	

	
	public ClusterNodeNotifier(int nodeId,int serverPort, int reNotifyPeriod, String ip, int port) {
		super();
		this.serverPort = serverPort;
		this.reNotifyPeriod = reNotifyPeriod;
		this.ip = ip;
		this.port = port;
		this.nodeId=nodeId;
	}

	public void notifyCluster() throws IOException {
		String nodeInfo=nodeId+":"+serverPort;
		LOG.info("Sending node info:"+nodeInfo);
		while (run) {
			LOG.debug("Sending node info:"+nodeInfo);
			buf = nodeInfo.getBytes();
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
			socket.send(packet);
						
			try {
				Thread.yield();
				Thread.sleep(reNotifyPeriod);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void setRun(boolean run) {
		this.run = run;
	}

	@Override
	public void run() {

		try {
			socket = new DatagramSocket();
			group = InetAddress.getByName(ip);
			notifyCluster();
		} catch (IOException e) {
			LOG.error("Error starting the Cluster Notifier "+e.getMessage());
		}
		socket.close();

	}
}
