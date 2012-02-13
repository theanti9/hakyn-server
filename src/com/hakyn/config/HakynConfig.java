package com.hakyn.config;

import java.util.HashMap;

public class HakynConfig {
	private static int GameListenPort;
	private static int AuthListenPort;
	// TODO: Make Auth listener available as another host
	
	// Mongo database
	private static String ActiveGameDBHost;
	private static int ActiveGameDBPort;
	private static String ActiveGameDBUser;
	private static String ActiveGameDBPass;
	private static String ActiveGameDBName;
	
	// MySQL Database
	private static String StaticDBHost;
	private static int StaticDBPort;
	private static String StaticDBUser;
	private static String StaticDBPass;
	private static String StaticDBName;
	
	// Game Settings
	private static int SpawnInterval;
	private static int MapSaveInterval;
	
	public static void Setup(HashMap<String, Object> configAttributes) {
		GameListenPort = ((Integer)configAttributes.get("GameListenPort")).intValue();
		AuthListenPort = ((Integer)configAttributes.get("AuthListenPort")).intValue();
		
		ActiveGameDBHost = ((String)configAttributes.get("ActiveGameDBHost"));
		ActiveGameDBPort = ((Integer)configAttributes.get("ActiveGameDBPort")).intValue();
		ActiveGameDBUser = ((String)configAttributes.get("ActiveGameDBUser"));
		ActiveGameDBPass = ((String)configAttributes.get("ActiveGameDBPass"));
		ActiveGameDBName = ((String)configAttributes.get("ActiveGameDBName"));
		
		StaticDBHost = ((String)configAttributes.get("StaticDBHost"));
		StaticDBPort = ((Integer)configAttributes.get("StaticDBPort")).intValue();
		StaticDBUser = ((String)configAttributes.get("StaticDBUser"));
		StaticDBPass = ((String)configAttributes.get("StaticDBPass"));
		StaticDBName = ((String)configAttributes.get("StaticDBName"));
		
		SpawnInterval = ((Integer)configAttributes.get("SpawnInterval")).intValue();
		MapSaveInterval = ((Integer)configAttributes.get("MapSaveInterval")).intValue();
		
	}
	
	public static int getGameListenPort() {
		return GameListenPort;
	}
	
	public static int getAuthListenPort() {
		return AuthListenPort;
	}
	
	public static String getActiveGameDBHost() {
		return ActiveGameDBHost;
	}
	
	public static int getActiveGameDBPort() {
		return ActiveGameDBPort;
	}
	
	public static String getActiveGameDBUser() {
		return ActiveGameDBUser;
	}
	
	public static String getActiveDBPass() {
		return ActiveGameDBPass;
	}
	
	public static String getActiveDBName() {
		return ActiveGameDBName;
	}
	
	public static String getStaticDBHost() {
		return StaticDBHost;
	}
	
	public static int getStaticDBPort() {
		return StaticDBPort;
	}
	
	public static String getStaticDBUser() {
		return StaticDBUser;
	}
	
	public static String getStaticDBPass() {
		return StaticDBPass;
	}
	
	public static String getStaticDBName() {
		return StaticDBName;
	}
	
	public static int getSpawnInterval() {
		return SpawnInterval;
	}
	
	public static int getMapSaveInterval() {
		return MapSaveInterval;
	}
}
