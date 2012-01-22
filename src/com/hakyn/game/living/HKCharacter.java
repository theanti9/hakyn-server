/*
 * HKCharacter.java
 * 
 * Collection of static functions for dealing with characters
 * 
 */

package com.hakyn.game.living;

import java.util.HashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import com.hakyn.db.MongoDbConnection;

public class HKCharacter {
	
	// Get the database object for a character
	public static DBObject get(ObjectId id) {
		// Do a query by id
		BasicDBObject doc = new BasicDBObject();
		doc.put("_id", id);
		DBCursor cur = MongoDbConnection.getInstance().getCollection("characters").find(doc);
		// If the cursor actually found something, put it into out and return it
		DBObject out = null;
		if (cur.hasNext()) {
			out = cur.next();
		}
		return out;
	}
	
	// Create a new character
	public static void create(HashMap<String, Object> attrMap) {
		// Create the database object and put the all the attributes on it
		BasicDBObject doc = new BasicDBObject();
		for (String key : attrMap.keySet()) {
			doc.put(key, attrMap.get(key));
		}
		// Insert into the database
		MongoDbConnection.getInstance().getCollection("characters").insert(doc);
	}
	
	// Set a characters zone
	public static void setZone(String characterId, int zoneId) {
		// Get a character object by it's id
		DBObject character = get(new ObjectId(characterId));
		BasicDBObject update = new BasicDBObject();
		// Set current zone
		update.put("current_zone", zoneId);
		// Update
		MongoDbConnection.getInstance().getCollection("characters").findAndModify(character, update);
	}
	
	// Set character position
	public static void setPosition(String characterId, int x, int y) {
		// Get character object
		DBObject character = get(new ObjectId(characterId));
		BasicDBObject update = new BasicDBObject();
		// Set x and y coordinates
		update.put("x_coord", x);
		update.put("y_coord", y);
		MongoDbConnection.getInstance().getCollection("characters").findAndModify(character, update);
	}
	
}
