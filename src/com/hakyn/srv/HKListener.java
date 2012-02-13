/*
 * HKListener.java
 * 
 * Server main entry point.
 * starts services and listeners
 * 
 */

package com.hakyn.srv;
import com.hakyn.config.HakynConfig;
import com.hakyn.config.HakynConfigLoader;
import com.hakyn.db.MongoDbConnection;
import com.hakyn.db.MySqlDbConnection;
import com.hakyn.srv.service.HKMonsterSpawn;
import com.hakyn.srv.service.HKPositioningService;

public class HKListener {
	
	public static final String HOST = "localhost";
	//public static final int PORT = 9098;
	
	public static HKPositioningService posService = new HKPositioningService();
	public static HKServiceListener svcListener = new HKServiceListener();
	public static HKMonsterSpawn spwnService = new HKMonsterSpawn();
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting...");
		
		try {
			
			// initialize config
			HakynConfigLoader.LoadConfig();
			
			// Initialize databases
			new MongoDbConnection(HakynConfig.getActiveGameDBHost(), HakynConfig.getActiveGameDBPort());
			if (HakynConfig.getActiveGameDBUser() != null) {
				MongoDbConnection.getInstance().auth(HakynConfig.getActiveGameDBUser(), HakynConfig.getActiveDBPass());
			}
			
			MongoDbConnection.getInstance().selectDb(HakynConfig.getActiveDBName());
			new MySqlDbConnection(HakynConfig.getStaticDBHost(), HakynConfig.getStaticDBPort(), HakynConfig.getStaticDBUser(), HakynConfig.getStaticDBPass(), HakynConfig.getStaticDBName());
			
			
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
			svcListener.stop();
			posService = null;
			svcListener = null;
			spwnService = null;
			MongoDbConnection.getInstance().close();
			MySqlDbConnection.getInstance().close();
			
		}
		
	}

}
