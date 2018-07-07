package com.msrk.tools.cache.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterNodeRecieiver implements Runnable {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private String ip;
	private int port;
	private MulticastSocket socket;
	private byte[] buf = new byte[1000];
	private Set<String> peers;
	private boolean run=true;
	private int nodeId;

	public ClusterNodeRecieiver(int nodeId,Set<String> peers,String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.peers=peers;
		this.nodeId=nodeId;
	}

	public void addPeer(String peer){
		if(peers.contains(peer))
			return;
		LOG.info("Adding peer:"+peer);
		peers.add(peer);
	}
	
	public void run() {
		try {
			socket = new MulticastSocket(port);
			InetAddress group = InetAddress.getByName(ip);
			socket.joinGroup(group);
			LOG.info(nodeId+" joined the cluster");
			while (run) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				String []tmp=received.split(":",2);
				String nodeId = tmp[0].trim();
				LOG.debug("Peer info received:"+received+" with node id "+nodeId);
				if(Integer.parseInt(nodeId) == this.nodeId)
					continue;
				received = tmp[1];
				addPeer(packet.getAddress().getHostAddress()+":"+received);
				//Thread.yield();
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			LOG.error("Error starting node receiver :"+e.getMessage());
		}
	}

	public void setRun(boolean run) {
		this.run = run;
	}
	
	
}
