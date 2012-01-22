/*
 * HKPositioningService.java
 * 
 * Keeps track of and contains services for updating character
 * positions in each zone
 * 
 */

package com.hakyn.srv.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.hakyn.game.HKZone;

public class HKPositioningService implements Runnable {

	// Map of zone ID's to their zone objects
	public static HashMap<Integer,HKZone> ZoneMap;
	// is the Positioning Service ready to accept commands
	private static boolean ready = false;
	
	@Override
	public void run() {
		// Setup functions
		createZoneMap();
		
		// mark as ready
		ready = true;
		System.out.println("Positioning service ready.");
		// This process is run in a thread
		while (true) {
			try {
				// Save the character positions ever 60 seconds
				Thread.sleep(60000);
				save();
			} catch (InterruptedException e) {
				// Don't do anything
			}
		}
		
	}
	
	
	// This creates the map of zone object to their IDs
	private static void createZoneMap() {
		ZoneMap = new HashMap<Integer, HKZone>();
		
		// Grab the ID list from HKZone
		List<Integer> l = HKZone.getZoneIdList();
		
		// Fill map
		for (Integer i : l) {
			// TODO: make this reflect the actual monster limits of each map
			System.out.println("Loading zone "+ i);
			ZoneMap.put(i, new HKZone(i.intValue(), 20));
		}
		System.out.println("Finished loading zone map");
	}
	
	// Used to grab the current zone ID given a character's ID
	private static int getZoneIdForCharacter(String charid) throws IOException {
		// Make sure the service is ready
		if (ready) {
			int id = -1;
			// Loop through the zones in the map and check to see
			// if the given character is in that zone
			for (HKZone zone : ZoneMap.values()) {
				if (zone.characterInZone(charid)) {
					// if it is get that id and return
					id = zone.ZoneId; 
					break;
				}
			}
			return id;
		} else {
			throw new IOException("Positioning Service not ready yet");
		}
		
	}
	
	// Sets the character position in the appropriate map
	public static void updateCharacterPosition(String charid, int x, int y) throws IOException {
		// Make sure the service is ready
		if (ready) {
			// Grab the correct zone object and set the Coordinates for that character
			ZoneMap.get(getZoneIdForCharacter(charid)).setCharacterCoord(charid, x, y);
		} else {
			throw new IOException("Positioning Service not ready yet.");
		}
	}
	
	// On stopping the service, try to save the map. This might fail
	public void stop() {
		save();		
	}

	// loop over the zones and save them.
	private static void save() {
		System.out.println("Saving zones");
		for(HKZone zone : ZoneMap.values()) {
			zone.save();
			System.out.println("Saved zone "+zone.ZoneId);
		}
	}
}
