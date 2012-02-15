package com.hakyn.srv.service;

import java.io.IOException;
import java.util.Arrays;

import com.hakyn.config.HakynConfig;
import com.hakyn.srv.protocol.HKMessage;
import com.hakyn.srv.protocol.HKMessageHeader;
import com.snow.IO.SnowTcpServer;
import com.snow.IO.SnowTcpClient;
import com.snow.IO.EventCallback.IOConnectEventCallback;
import com.snow.IO.EventCallback.IOReadEventCallback;

public class HKAuthService implements Runnable {
	
	private static SnowTcpServer authServer;

	@Override
	public void run() {
		
		// Create auth server
		authServer = new SnowTcpServer(HakynConfig.getAuthListenPort(), 10, 30, 100, 30);
		// Connect callback. For now just print that someone connected
		authServer.RegisterCallback(new IOConnectEventCallback() {
			public void Invoke(SnowTcpClient client) {
				System.out.println("Auth connection: "+client);
			}
		});
		// Read Callback - look for auth request
		authServer.RegisterCallback(new IOReadEventCallback() {
			public void Invoke(SnowTcpClient client, byte[] bytes) {
				try {
					HKMessageHeader header = new HKMessageHeader(Arrays.copyOfRange(bytes, 0, 9));
					// return if it's not an auth command, we shouldn't be getting it.
					if (header.command != 0x03) {
						return;
					}
					HKMessage msg = new HKMessage(header,Arrays.copyOfRange(bytes, 9, bytes.length), header.command);
					byte[] out = msg.handle();
					client.Write(out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void stop() throws IOException {
		authServer.Stop();
	}
	
	public static void AuthReply(SnowTcpClient client, String token, int error) {
		// Write out the auth return packet.
		
	}

}
