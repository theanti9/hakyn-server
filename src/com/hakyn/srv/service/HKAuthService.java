package com.hakyn.srv.service;

import java.io.IOException;

import com.hakyn.config.HakynConfig;
import com.snow.IO.SnowTcpServer;
import com.snow.IO.SnowTcpClient;
import com.snow.IO.EventCallback.IOConnectEventCallback;
import com.snow.IO.EventCallback.IOReadEventCallback;

public class HKAuthService implements Runnable {
	
	private static SnowTcpServer authServer;

	@Override
	public void run() {
		
		// Create auth server
		authServer = new SnowTcpServer(HakynConfig.getAuthListenPort(), 30, 70, 50, 30);
		// Connect callback. For now just print that someone connected
		authServer.RegisterCallback(new IOConnectEventCallback() {
			public void Invoke(SnowTcpClient client) {
				System.out.println("Auth connection: "+client);
			}
		});
		// Read Callback - look for auth request
		authServer.RegisterCallback(new IOReadEventCallback() {
			public void Invoke(SnowTcpClient client, byte[] bytes) {
				
			}
		});
	}
	
	public void stop() throws IOException {
		authServer.Stop();
	}
	
	public static void AuthReply(String token, int error) {
		// Write out the auth return packet.
		
	}

}
