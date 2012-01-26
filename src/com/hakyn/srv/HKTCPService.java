/*
 * HKTCPService.java
 * 
 * This is the Connection listener for outgoing service updates
 * 
 * TODO: Check character and incoming connection information against 
 * the main service once authentication is implemented. 
 * 
 */

package com.hakyn.srv;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hakyn.queue.HKBlockingIDQueue;
import com.hakyn.queue.HKQueueRunner;
import com.hakyn.util.HKServiceConnectionCollection;

import Extasys.DataFrame;
import Extasys.Network.TCP.Server.Listener.TCPClientConnection;

public class HKTCPService extends Extasys.Network.TCP.Server.ExtasysTCPServer {
	
	public static HKServiceConnectionCollection svcCons;
	public static HKBlockingIDQueue dataQueue;
	public static HKQueueRunner queueRunner;
	private ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> threadQueue;
	// Create the object with lots of parameters
	public HKTCPService(String name, String description, InetAddress listenerIP, int port, int maxConnections, int connectionTimeOut, int corePoolSize, int maximumPoolSize) {
		super(name, description, corePoolSize, maximumPoolSize);
		try {
			// Add the listener
			this.AddListener("ServiceListener", listenerIP, port, maxConnections, 65535, connectionTimeOut, 100, ((char)2));
		} catch (Exception e) {
			System.err.println("Error starting Service Listener: " + e.getMessage());
			System.exit(-1);
		}
		// Create the collection of service connections
		svcCons = new HKServiceConnectionCollection();
		// Initialize the data queue
		dataQueue = new HKBlockingIDQueue(new Date().toString().replace(" ","").length());
		threadQueue = new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS, threadQueue);
	}
	
	@Override
	public void OnClientConnect(TCPClientConnection client) {
		// When someone connects add create an HKServiceConnection object
		// and add it to the list
		HKServiceConnection svcCon = new HKServiceConnection(client);
		// Set the name to the date string with no spaces.
		client.setName(client.getConnectionStartUpDateTime().toString().replace(" ", ""));
		svcCons.add(svcCon);
		
		System.out.println("Service connection added from "+client.getIPAddress());
	}
	
	@Override
	public void OnClientDisconnect(TCPClientConnection client) {
		// Remove the connection from the collection given the name
		svcCons.remove(svcCons.getConnectionForCharacter(client.getName()));
		System.out.println(client.getIPAddress()+" disconnected from services");
	}
	
	@Override
	public void OnDataReceive(TCPClientConnection client, DataFrame data) {
		try {
			dataQueue.EnqueueData(client.getName(), data.getBytes());
			threadPool.execute(new Thread(new HKQueueRunner()));
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}	
	}
}
