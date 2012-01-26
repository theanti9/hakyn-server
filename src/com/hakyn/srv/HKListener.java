/*
 * HKListener.java
 * 
 * Server main entry point.
 * starts services and listeners
 * 
 */

package com.hakyn.srv;
import com.hakyn.db.*;
import com.hakyn.srv.service.*;

public class HKListener {
	
	public static final String HOST = "localhost";
	public static final int PORT = 9098;
	
	public static HKPositioningService posService = new HKPositioningService();
	public static HKServiceListener svcListener = new HKServiceListener();
	public static HKMonsterSpawn spwnService = new HKMonsterSpawn();
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting...");
		
		try {
			// Initialize databases
			new MongoDbConnection();
			MongoDbConnection.getInstance().selectDb("hakyn");
			new MySqlDbConnection("localhost", 3306, "root", "", "hakyn");
			
			
			// Start the Position Service in a new thread
			System.out.println("Starting Position Service...");
			Thread posThread = new Thread(posService);
			posThread.start();
			
			// Start Spawning Service
			System.out.println("Starting Monster Spawning service...");
			Thread spwnThread = new Thread(spwnService);
			spwnThread.start();
			
			// Start service listener
			System.out.println("Starting service listener...");
			Thread svcThread = new Thread(svcListener);
			svcThread.start();
			
			System.out.println("Running!");
			System.out.println("Press <enter> to exit...");
			System.in.read();
		} finally {
			posService = null;
			svcListener = null;
			spwnService = null;
		}
		
	}

}
