package com.hakyn.srv.service;

import com.hakyn.game.HKZone;


public class HKMonsterSpawn implements Runnable {
	
	
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(180000);
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
