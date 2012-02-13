package com.hakyn.srv.service;

import com.hakyn.config.HakynConfig;
import com.hakyn.db.MongoDbConnection;
import com.hakyn.game.HKZone;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class HKMonsterSpawn implements Runnable {
	
	
	
	public void run() {
		System.out.println("Clearing old monster spawns");
		DBObject dbobj = new BasicDBObject();
		// clear all the spawned monsters
		MongoDbConnection.getInstance().getCollection("spawned_monsters").remove(dbobj);
		while (true) {
			try {
				Thread.sleep(HakynConfig.getSpawnInterval());
				System.out.println("Starting spawn...");
				for (HKZone z : HKPositioningService.ZoneMap.values()) {
					System.out.println("Spawning in zone "+z.ZoneId);
					z.spawn();
				}
				System.out.println("Finished spawn");
			} catch (InterruptedException e) {
				// print stack trace
				e.printStackTrace();
			}
			
		}
	}
	
	
}
