package com.hakyn.srv.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import com.hakyn.db.MongoDbConnection;
import com.hakyn.game.HKZone;


public class HKMonsterSpawn implements Runnable {
	
	
	
	public void run() {
		System.out.println("Clearing old monster spawns");
		DBObject dbobj = new BasicDBObject();
		// clear all the spawned monsters
		MongoDbConnection.getInstance().getCollection("spawned_monsters").remove(dbobj);
		while (true) {
			try {
				Thread.sleep(60000);
				System.out.println("Starting spawn...");
				for (HKZone z : HKPositioningService.ZoneMap.values()) {
					System.out.println("Spawning in zone "+z.ZoneId);
					z.spawn();
				}
				System.out.println("Finished spawn");
			} catch (InterruptedException e) {
				// Do nothing
			}
			
		}
	}
	
	
}
