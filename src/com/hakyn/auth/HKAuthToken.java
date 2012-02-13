package com.hakyn.auth;

import java.math.BigInteger;
import java.security.SecureRandom;

public class HKAuthToken {
	
	private static SecureRandom random = new SecureRandom();
	
	public static String generateToken() {
		return new BigInteger(130, random).toString(32);
	}

}
