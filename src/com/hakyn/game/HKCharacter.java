package com.hakyn.game;

import java.util.HashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import com.hakyn.db.MongoDbConnection;

public class HKCharacter {
	
	public static DBObject get(ObjectId id) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("_id", id);
		DBCursor cur = MongoDbConnection.getInstance().getCollection("characters").find(doc);
		DBObject out = null;
		if (cur.hasNext()) {
			out = cur.next();
		}
		return out;
	}
	
	public static void create(HashMap<String, Object> attrMap) {
		BasicDBObject doc = new BasicDBObject();
		for (String key : attrMap.keySet()) {
			doc.put(key, attrMap.get(key));
		}
		MongoDbConnection.getInstance().getCollection("characters").insert(doc);
	}
	
	public static void setZone(String characterId, int zoneId) {
		DBObject character = get(new ObjectId(characterId));
		BasicDBObject update = new BasicDBObject();
		update.put("zone_id", zoneId);
		MongoDbConnection.getInstance().getCollection("characters").findAndModify(character, update);
	}
	
	public static void setPosition(String characterId, int x, int y) {
		DBObject character = get(new ObjectId(characterId));
		BasicDBObject update = new BasicDBObject();
		update.put("x_coord", x);
		update.put("y_coord", y);
		MongoDbConnection.getInstance().getCollection("characters").findAndModify(character, update);
	}
	
}
