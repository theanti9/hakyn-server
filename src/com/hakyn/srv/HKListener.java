/*
 * HKListener.java
 * 
 * Server main entry point.
 * starts services and listeners
 * 
 */

package com.hakyn.srv;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import com.hakyn.srv.service.*;

public class HKListener {
	
	public static final String HOST = "localhost";
	public static final int PORT = 9098;
	
	public static HKPositioningService posService = new HKPositioningService();
	public static HKServiceListener svcListener = new HKServiceListener();
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting...");
		// Create and add Main listener filter chain
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
		filterChainBuilder.add(new TransportFilter());
		filterChainBuilder.add(new HKCommandFilter());
		
		// build filter chain
		final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
		transport.setProcessor(filterChainBuilder.build());
		
		try {
			transport.bind(PORT);
			System.out.println("Starting Transport...");
			transport.start();
			
			// Start the Position Service in a new thread
			System.out.println("Starting Position Service...");
			Thread posThread = new Thread(posService);
			posThread.start();
			
			// Start service listener
			System.out.println("Starting service listener...");
			Thread svcThread = new Thread(svcListener);
			svcThread.start();
			
			System.out.println("Running!");
			System.out.println("Press <enter> to exit...");
			System.in.read();
		} finally {
			transport.stop();
			// This stops the threads
			posService = null;
			svcListener = null;
		}
		
	}

}
