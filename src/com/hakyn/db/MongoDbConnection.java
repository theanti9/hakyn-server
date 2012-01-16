package com.hakyn.db;

import java.net.UnknownHostException;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.DB;


public class MongoDbConnection {
	private Mongo mongo;
	private DB db;
	private static boolean instantiated = false;
	public static MongoDbConnection instance = null;
	
	public static MongoDbConnection getInstance() {
		
		if (instance == null) {
			try {
				instance = new MongoDbConnection();
			} catch (UnknownHostException e) {
				System.err.println("Unkown Mongo Host");
				System.exit(-1);
			}
		}
		return instance;
	}
	
	public MongoDbConnection() throws UnknownHostException {
		if (!instantiated) {
			this.mongo = new Mongo();
			instantiated = true;
		}
		instance = this;
	}
	
	public MongoDbConnection(String host) throws UnknownHostException {
		if (!instantiated) {
			this.mongo = new Mongo(host);
			instantiated = true;
		}
		instance = this;
	}
	
	public MongoDbConnection(String host, int port) throws UnknownHostException {
		if (!instantiated) {
			this.mongo = new Mongo(host, port);
			instantiated = true;
		}
		instance = this;
	}
	
	public boolean auth(String username, String password) {
		if (!instantiated) {
			return false;
		}
		return this.db.authenticate(username, password.toCharArray());
	}
	
	public void selectDb(String dbname) {
		if (instantiated) {
			this.db = this.mongo.getDB(dbname);
		}
	}
	
	public DBCollection getCollection(String collname) {
		return this.db.getCollection(collname);
	}
}
