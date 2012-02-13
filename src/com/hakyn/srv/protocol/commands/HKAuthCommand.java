package com.hakyn.srv.protocol.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.bson.types.ObjectId;

import com.hakyn.auth.HKAuthToken;
import com.hakyn.db.MongoDbConnection;
import com.hakyn.db.MySqlDbConnection;
import com.hakyn.util.ArrayUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class HKAuthCommand {
	
	// Error Constants
	private static final int NO_ACCOUNT = 1;
	private static final int INCORRECT_PASSWORD = 2;
	private static final int NO_CHARACTER = 3;
	private static final int BLANK_USERNAME = 4;
	private static final int BLANK_PASSWORD = 5;
	// === Packet data ===
	private static final int DATA_LENGTH = 120;
	private static final byte COMMAND_BYTE = 0x03;

	// === User data ===
	// these should be looked up
	private int userid;
	private String charid;
	
	// these should be given in the packet
	private String username;
	private String password;
	
	// This should be generated
	private String token;
	private boolean hasError = false;
	private int error;
	
	public HKAuthCommand(byte[] bytes) {
		if (bytes.length < DATA_LENGTH);
		byte[] userbytes = Arrays.copyOfRange(bytes, 0, 60);
		username = new String(ArrayUtil.GetToFirstNull(userbytes));
		byte[] passbytes = Arrays.copyOfRange(bytes, 60, 120);
		password = new String(ArrayUtil.GetToFirstNull(passbytes));
		
		if (username.isEmpty()) {
			hasError = true;
			error = BLANK_USERNAME;
		} else if (password.isEmpty()) {
			hasError = true;
			error = BLANK_PASSWORD;
		}
	}
	
	public void run() throws SQLException {
		// look up info
		Statement stmt = MySqlDbConnection.getInstance().getNewStatement();
		
		stmt.execute("SELECT AccountID, AccountPassowrd FROM accounts WHERE AccountName = '" + this.username + "'");
		ResultSet rs = stmt.getResultSet();
		
		if (!rs.first()) {
			// Account not found
			// Need to return an error
			hasError = true;
			error = NO_ACCOUNT;
			return;
		}
		
		userid = rs.getInt("AccountID");
		// TODO: bcrypt password
		if (password != rs.getString("AccountPassword")) {
			hasError = true;
			error = INCORRECT_PASSWORD;
			return;
		}
		
		DBCollection coll = MongoDbConnection.getInstance().getCollection("characters");
		DBObject find = new BasicDBObject();
		// Find the character for this account
		find.put("account_id", userid);
		DBObject found = coll.findOne(find);
		// Make sure it actually got an object
		if (found == null) {
			hasError = true;
			error = NO_CHARACTER;
			return;
		}
		// Grab and set the character ID
		charid = ((ObjectId)found.get("_id")).toString();
		
		// set token
		token = HKAuthToken.generateToken();
		
		// add running info to db
		DBObject update = new BasicDBObject();
		update.put("token", token);
		coll.update(found, update);
		//HKAuthService.AuthReply(token, getError());
	}
	
	
	public String getToken() {
		return this.token;
	}
	
	// this will return 0 if there's no error, otherwise it will return the error code
	public int getError() {
		int ret = 0;
		if (hasError) {
			ret = this.error;
		}
		return ret;
	}
	
}
