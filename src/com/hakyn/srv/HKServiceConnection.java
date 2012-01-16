/*
 * HKServiceConnection.java
 * 
 * This class represents a connection to the service listener
 * Used for sending update data
 */

package com.hakyn.srv;

import Extasys.Network.TCP.Server.Listener.TCPClientConnection;
import Extasys.Network.TCP.Server.Listener.Exceptions.ClientIsDisconnectedException;
import Extasys.Network.TCP.Server.Listener.Exceptions.OutgoingPacketFailedException;

public class HKServiceConnection {
	
	public String CharacterId;
	public int ZoneId;
	
	private TCPClientConnection tcpClient;
	
	// Create the object with a given TCPClientConnection object
	public HKServiceConnection(TCPClientConnection tcpClient) {
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
			tcpClient.SendData(bytes, 0, bytes.length);
		} catch (ClientIsDisconnectedException e) {
			// TODO: do something
		} catch (OutgoingPacketFailedException e) {
			// TODO: do something
		}
	}
	
}
