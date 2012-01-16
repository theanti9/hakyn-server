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

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import com.hakyn.db.MongoDbConnection;
import com.hakyn.db.MySqlDbConnection;
import com.hakyn.srv.HKServiceConnection;
import com.hakyn.srv.HKTCPService;
import com.hakyn.srv.protocol.HKMessageHeader;
import com.hakyn.srv.protocol.HKMessage;
import com.hakyn.util.ArrayUtil;
import com.hakyn.util.Converter;

public class HKZone {
	
	public int ZoneId;
	
	// Map of character database objects to their ObjectIds
	private HashMap<ObjectId,DBObject> CharacterMap;
	
	// Create a zone given it's id
	public HKZone(int id) {
		this.ZoneId = id;
		// Load the characters currently in the zone
		this.loadCharacters();
	}
	
	
	private void loadCharacters() {
		// Create a query object and look for characters with current_zone set to the id
		BasicDBObject doc = new BasicDBObject();
		doc.put("current_zone", this.ZoneId);
		
		// Do the query
		DBCursor cur = MongoDbConnection.getInstance().getCollection("characters").find(doc);
		
		// While there's characters returned, add their objected ID's to the character mape
		while (cur.hasNext()) {
			DBObject n = cur.next();
			CharacterMap.put(((ObjectId)n.get("_id")),n);
		}
		
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
			sendUpdate(obj);
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
			boolean result = stmt.execute("SELECT zone_id FROM zones");
			
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
	
	// Send the position update to everyone in the zone
	private void sendUpdate(DBObject ch) {
		// Get a list of the service connections for the players in the zone
		List<HKServiceConnection> svcCons = HKTCPService.svcCons.getConnectionsForZone(ZoneId);
		
		// Loop over the list
		for (HKServiceConnection con : svcCons) {
			// Create the position update header
			HKMessageHeader header = new HKMessageHeader((byte)0x01, 32);
			
			// Create body and copy data to it
			byte[] body = new byte[32];
			ArrayUtil.ArrayIntoArray(body, ch.get("_id").toString().getBytes(), 0);
			ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)ch.get("x_coord")), 24);
			ArrayUtil.ArrayIntoArray(body, Converter.intToByteArray((Integer)ch.get("y_coord")), 28);
			
			// Send out the data
			con.Send(new HKMessage(header,body,(byte)0x01).getBytes());
		}
	}
	
}
