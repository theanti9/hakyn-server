/*
 * HKZone.java
 * 
 * Used to keep track of characters inside a given zone
 * Also contains static methods for retrieving information about contents
 * 
 */


package com.hakyn.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import com.hakyn.db.MongoDbConnection;
import com.hakyn.db.MySqlDbConnection;
import com.hakyn.game.living.HKCharacter;
import com.hakyn.game.living.HKMonster;
import com.hakyn.srv.HKServiceConnection;
import com.hakyn.srv.HKTCPService;
import com.hakyn.srv.protocol.HKMessageHeader;
import com.hakyn.srv.protocol.HKMessage;
import com.hakyn.util.ArrayUtil;
import com.hakyn.util.Converter;

public class HKZone {
	
	public int ZoneId;
	public String ZoneName;
	public String ZoneIP;
	public short ZoneType;
	
	
	private int maxMonsterCount;
	private int currentMonsterCount;
	private List<HKMonster> spawnableMonsters;
	private List<Integer> monsterTypeCount;
	// Map of character database objects to their ObjectIds
	private HashMap<ObjectId,DBObject> CharacterMap;
	private HashMap<ObjectId,DBObject> MonsterMap;
	
	// Create a zone given it's id
	public HKZone(int id, int maxMonsters) {
		this.ZoneId = id;
		this.maxMonsterCount = maxMonsters;
		this.monsterTypeCount = new ArrayList<Integer>();
		this.MonsterMap = new HashMap<ObjectId,DBObject>();
		this.CharacterMap = new HashMap<ObjectId,DBObject>();
		// Load the characters currently in the zone
		this.loadCharacters();
		
		// Load the list of possibly spawnable monsters
		this.loadMonsterList();
		System.out.println("Loaded zone "+id);
	}
	
	// is a character in this zone?
	public boolean characterInZone(String objid) {
		// Check to see if the character map contains the appropriate ObjectId
		return (CharacterMap.containsKey(new ObjectId(objid))) ? true : false;
	}
	
	// Remove a character from the character map
	public void removeCharacter(String objid) {
		ObjectId charobjid = new ObjectId(objid);
		// Make sure it exists in the map first
		if (CharacterMap.containsKey(charobjid)) {
			CharacterMap.remove(charobjid);
		}
	}
	
	// Add a character to the map given it's ID
	public void addCharacter(String objid) {
		// Create the ObjectId object
		ObjectId charobjid = new ObjectId(objid);
		// Do the query for the character
		BasicDBObject doc = new BasicDBObject();
		doc.put("_id", charobjid);
		DBCursor cur = MongoDbConnection.getInstance().getCollection("characters").find(doc);
		// Grab the first character
		DBObject ch = cur.next();
		// Add it to the map by its ID
		CharacterMap.put(charobjid, ch);
	}
	
	// Set the coordinates of a character on the map
	public void setCharacterCoord(String objid, int x, int y) {
		ObjectId charobjid = new ObjectId(objid);
		// Make sure the map has the character
		if (CharacterMap.containsKey(charobjid)) {
			// Get the character object
			DBObject obj = CharacterMap.get(charobjid);
			// Set coordinates
			obj.put("x_coord", x);
			obj.put("y_coord", y);
			CharacterMap.put(charobjid, obj);
			sendPositionUpdate(obj);
		}
	}
	
	// Save all the character positions on the map to the database.
	public void save() {
		// Loop over the objects
		for (DBObject c : CharacterMap.values()) {
			// Use HKCharacter to save values
			HKCharacter.setZone(c.get("_id").toString(), ZoneId);
			HKCharacter.setPosition(c.get("_id").toString(), ((Integer)c.get("x_coord")), ((Integer)c.get("y_coord")));
		}
	}
	
	// Grab a list of all existing zone IDs. Used when starting positioning service
	public static List<Integer> getZoneIdList() {
		List<Integer> zonelist = new ArrayList<Integer>();
		try {
			// Get a new statement object from the MySql connection
			Statement stmt = MySqlDbConnection.getInstance().getNewStatement();
			// Run a query to get all the zone id's
			boolean result = stmt.execute("SELECT ZoneID FROM zones");
			
			// Make sure it worked
			if (!result) {
				throw new SQLException();
			}
			
			// Grab the result set and loop over it, adding ID's to the id list
			ResultSet resset = stmt.getResultSet();
			while (resset.next()) {
				zonelist.add(new Integer(resset.getInt(1)));
			}
			
		} catch (SQLException e) {
			// Don't do anything, just return an empty list
		}
		// Return the list
		return zonelist;
	}
	
	
	public void spawn() {
		HashMap<ObjectId,DBObject> newSpawns = newSpawns();
		sendSpawnUpdate(newSpawns);
		MonsterMap.putAll(newSpawns);
	}
	
	
	private HashMap<ObjectId,DBObject> newSpawns() {
		Random gen = new Random();
		int i,j;
		j = 0;
		HashMap<ObjectId,DBObject> out = new HashMap<ObjectId,DBObject>();
		// While we haven't filled the map
		while (currentMonsterCount < maxMonsterCount) {
			// gen a random number between 1 and 1000
			i = gen.nextInt(1001);
			// loop through each spawnable monster probably a bunch of times through
			// comparing the spawn chance to see if we spawn a new one
			HKMonster monster = spawnableMonsters.get(j);
			// Also make sure that we haven't spawned too many of that type
			if (new Float(i) * monster.SpawnChance < 100F && monsterTypeCount.get(j) < monster.MaxSpawn) {
				// Create a new spawned monster object
				DBObject dbobj = new BasicDBObject();
				// Add the right attributes
				dbobj.put("MonsterID", monster.MonsterID);
				dbobj.put("spawned_zone", this.ZoneId);
				dbobj.put("x_coord", gen.nextInt(500));
				dbobj.put("y_coord", gen.nextInt(500));
				// Add it to the spawned monsters collection
				MongoDbConnection.getInstance().getCollection("spawned_monsters").insert(dbobj);
				// Add it to the monster map
				out.put(((ObjectId)dbobj.get("_id")), dbobj);
				// Set the number spawned for the type that we spawned up one more
				monsterTypeCount.set(j, monsterTypeCount.get(j)+1);
				// increment the total we have spawned on the map
				currentMonsterCount++;
				System.out.println("Spawned Monster with ID "+monster.MonsterID+" in zone "+this.ZoneId);
			}
			j++;
			// If we've gone all the way through the spawnable list without
			// reaching the max number on the map, start over on the spawnables
			if (j >= spawnableMonsters.size()) {
				j = 0;
			}
		}
		return out;
	}
	
	// Send out the new spawn info to people in the zone
	private void sendSpawnUpdate(HashMap<ObjectId,DBObject> newSpawns) {
		// Get a list of the service connections for the players in the zone
		List<HKServiceConnection> svcCons = HKTCPService.svcCons.getConnectionsForZone(ZoneId);
		
		// Send all the spawn updates to each connection
		for (HKServiceConnection con : svcCons) {
			for (DBObject dbobj : newSpawns.values()) {
				// Create the fixed header
				HKMessageHeader header = new HKMessageHeader((byte)0x02, 36);
				
				byte[] body = new byte[36];
				body = ArrayUtil.ArrayIntoArray(body, dbobj.get("_id").toString().getBytes(), 0);
				body = ArrayUtil.ArrayIntoArray(body, dbobj.get("MonsterID").toString().getBytes(), 24);
				body = ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)dbobj.get("x_coord")), 28);
				body = ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)dbobj.get("y_coord")), 32);
				con.Send(new HKMessage(header, body, (byte)0x02).getBytes());
			}
		}
				
	}
	
	// Send the position update to everyone in the zone
	private void sendPositionUpdate(DBObject ch) {
		// Get a list of the service connections for the players in the zone
		List<HKServiceConnection> svcCons = HKTCPService.svcCons.getConnectionsForZone(ZoneId);
		
		// Loop over the list
		for (HKServiceConnection con : svcCons) {
			// Create the position update header
			HKMessageHeader header = new HKMessageHeader((byte)0x01, 32);
			
			// Create body and copy data to it
			byte[] body = new byte[32];
			body = ArrayUtil.ArrayIntoArray(body, ch.get("_id").toString().getBytes(), 0);
			body = ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)ch.get("x_coord")), 24);
			body = ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)ch.get("y_coord")), 28);
			
			// Send out the data
			con.Send(new HKMessage(header,body,(byte)0x01).getBytes());
		}
	}
	
	private void loadCharacters() {
		// Create a query object and look for characters with current_zone set to the id
		BasicDBObject doc = new BasicDBObject();
		doc.put("current_zone", this.ZoneId);
		doc.put("online", true);
		// Do the query
		DBCursor cur = MongoDbConnection.getInstance().getCollection("characters").find(doc);
		
		// While there's characters returned, add their objected ID's to the character map
		while (cur.hasNext()) {
			DBObject n = cur.next();
			CharacterMap.put(((ObjectId)n.get("_id")),n);
		}
		
	}
	
	private void loadMonsterList() {
		// Get the list of spawnable monsters for this zone.
		this.spawnableMonsters = HKMonster.getZoneSpawnableMonsters(this.ZoneId);
		// set the current count of each type of monster in this zone to 0
		for (int i = 0; i < this.spawnableMonsters.size(); i++) {
			monsterTypeCount.add(0);
		}
		
		
		
	}
	
}
