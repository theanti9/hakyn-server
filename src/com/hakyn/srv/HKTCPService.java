/*
 * HKTCPService.java
 * 
 * This is the Connection listener for outgoing service updates
 * 
 */

package com.hakyn.srv;

import java.net.InetAddress;

import com.hakyn.util.HKServiceConnectionCollection;

import Extasys.DataFrame;
import Extasys.Network.TCP.Server.Listener.TCPClientConnection;

public class HKTCPService extends Extasys.Network.TCP.Server.ExtasysTCPServer {
	
	public static HKServiceConnectionCollection svcCons;
	
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
	}
	
	@Override
	public void OnClientConnect(TCPClientConnection client) {
		// When someone connects add create an HKServiceConnection object
		// and add it to the list
		HKServiceConnection svcCon = new HKServiceConnection(client);
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
		// The only data we should receive is a connections character ID
		if (data.getLength() != 24) {
			return;
		}
		// Set the connection name to the character ID
		client.setName(new String(data.getBytes()));
	}
}
