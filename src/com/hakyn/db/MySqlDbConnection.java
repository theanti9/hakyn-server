package com.hakyn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDbConnection {
	
	public static MySqlDbConnection instance = null;
	private boolean instantiated = false;
	private Connection con = null;
	
	// Creates object and connects;
	public MySqlDbConnection(String host, int port, String username, String password, String database) throws SQLException {
		if (instantiated) {
			return;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Error loading mysql driver");
			System.exit(-1);
		}
		con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?user="+username+"&password="+password);
		instance = this;
		instantiated = true;
	}
	
	public static MySqlDbConnection getInstance() throws SQLException {
		if (instance == null) {
			instance = new MySqlDbConnection("localhost", 3306, "root", "", "database");
		}
		return instance;
	}
	
	// get a new statement from the connection
	public Statement getNewStatement() throws SQLException {
		return con.createStatement();
	}
	
	public void close() throws SQLException {
		con.close();
	}
	
}
