package com.hakyn.srv;

import java.net.InetAddress;


public class HKServiceListener implements Runnable {
	
	private static final int PORT = 9099;
	private static final int MAX_CONNECTIONS = 1000;
	private static final int CONNECTION_TIMEOUT = 120;
	private static final int CORE_POOL_SIZE = 30;
	private static final int MAX_POOL_SIZE = 70;
	@Override
	public void run() {
		try {
			HKTCPService tcpService = new HKTCPService("PositionService","Keeps track of character positions", InetAddress.getLocalHost(),PORT,MAX_CONNECTIONS,CONNECTION_TIMEOUT,CORE_POOL_SIZE,MAX_POOL_SIZE);
			tcpService.Start();
		} catch (Exception e) {
			System.err.println("Position Service Exception" + e.getMessage());
			System.exit(-1);
		}
		
	}
}
