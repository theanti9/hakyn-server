/*
 * HKServiceConnectionCollection.java
 * 
 * This class is used to hold the collection of connections to the ServiceListener
 * 
 */

package com.hakyn.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hakyn.srv.HKServiceConnection;

public class HKServiceConnectionCollection {
	// master list
	private List<HKServiceConnection> conns;
	
	// indexes
	private HashMap<Integer, List<Integer>> zoneIndex; // <zoneid, listindex>
	private HashMap<String, Integer> characterIdIndex; // <characterid, listindex>
	
	public HKServiceConnectionCollection() {
		// create the connection list and the two indexes
		this.conns = new ArrayList<HKServiceConnection>();
		this.zoneIndex = new HashMap<Integer,List<Integer>>();
		this.characterIdIndex = new HashMap<String, Integer>();
	}
	
	public void add(HKServiceConnection newCon) {
		// add the new connection
		conns.add(newCon);
		// get it's position
		int i = conns.indexOf(newCon);
		// put in the index info
		if (zoneIndex.containsKey(newCon.ZoneId)) {
			List<Integer> l = zoneIndex.get(newCon.ZoneId);
			l.add(i);
			zoneIndex.put(new Integer(newCon.ZoneId), l);
		} else {
			List<Integer> ints = new ArrayList<Integer>();
			ints.add(i);
			zoneIndex.put(newCon.ZoneId, ints);
		}
		characterIdIndex.put(newCon.CharacterId, i);
	}
	
	// Get a list of connections in a particular zone
	public List<HKServiceConnection> getConnectionsForZone(int zoneId){
		List<HKServiceConnection> out = new ArrayList<HKServiceConnection>();
		// make sure we have the zone id
		if (zoneIndex.containsKey(zoneId)){
			// loop over the list that we grab from the zone index
			for (Integer i : zoneIndex.get(zoneId)) {
				HKServiceConnection c = conns.get(i);
				// Make sure the connection isn't null (which mean's it's
				// been removed
				if (c != null) {
					out.add(c);
				}
			}
		}
		return out;
	}
	
	// Get the connection for a particular character. This could possibly return null
	public HKServiceConnection getConnectionForCharacter(String characterId) {
		return conns.get(characterIdIndex.get(characterId));
	}
	
	// remove a connection from the map
	public void remove(HKServiceConnection svcCon) {
		// set the connection to null
		conns.set(conns.indexOf(svcCon), null);
	}
	
	// Clean up the list. This should run periodically to remove connections that have been
	// set to null by the remove method
	public synchronized void Clean() {
		// Recreate the indexes
		this.zoneIndex = new HashMap<Integer,List<Integer>>();
		this.characterIdIndex = new HashMap<String, Integer>();
		//List<HKServiceConnection> newConns = new ArrayList<HKServiceConnection>();
		for (HKServiceConnection svcCon : conns) {
			// Skip connections that have been nulled
			if (svcCon == null) {
				conns.remove(svcCon);
				continue;
			}
			// Grab the index of the current connection
			int i = conns.indexOf(svcCon);
			// If we already have entries for this zone
			if (zoneIndex.containsKey(svcCon.ZoneId)) {
				// Grab the list
				List<Integer> l = zoneIndex.get(svcCon.ZoneId);
				// Add the new integer
				l.add(i);
				// put it back in the index
				zoneIndex.put(new Integer(svcCon.ZoneId), l);
			} else {
				// Otherwise, create the list and add it
				List<Integer> ints = new ArrayList<Integer>();
				ints.add(i);
				zoneIndex.put(svcCon.ZoneId, ints);
			}
			// Put the character id in the index
			characterIdIndex.put(svcCon.CharacterId, i);
		}
	}
}
