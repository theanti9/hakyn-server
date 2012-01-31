/*
 * HKServiceConnection.java
 * 
 * This class represents a connection to the service listener
 * Used for sending update data
 */

package com.hakyn.srv;

import com.snow.IO.SnowTcpClient;

public class HKServiceConnection {
	
	public String CharacterId;
	public int ZoneId;
	
	private SnowTcpClient tcpClient;
	
	// Create the object with a given TCPClientConnection object
	public HKServiceConnection(SnowTcpClient tcpClient) {
		this.tcpClient = tcpClient;
	}
	
	// Assign a character id to the connection
	public void setCharacterId(String objid) {
		this.CharacterId = objid;
	}
	
	// assign a zone id
	public void setZoneId(int zid) {
		this.ZoneId = zid;
	}
	
	// Send bytes to the client
	public void Send(byte[] bytes) {
		try {
			tcpClient.Write(bytes);
		} catch (Exception e) {
			// TODO: do something
		}
	}
	
}
