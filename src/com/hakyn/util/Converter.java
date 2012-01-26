package com.hakyn.util;

public class Converter {
	
	// Convert first four network bytes to integer
	public static final int networkToInt(byte[] bytes) {
		int converted = 0x0;
		for (int i = 0; i < 4; i++) {
			converted = converted << 8;
			converted |= bytes[i];
		}
		return converted;
	}
	public static final int networkToInt(Byte[] bytes) {
		int converted = 0x0;
		for (int i = 0; i < 4; i++) {
			converted = converted << 8;
			converted |= bytes[i];
		}
		return converted;
	}
	// Convert first four network bytes to integer starting at the given offset
	public static final int networkToInt(byte[] bytes, int offset) {
		int converted = 0x0;
		for (int i = 0; i < 4; i++) {
			converted = converted << 8;
			converted |= bytes[i+offset];
		}
		return converted;
	}
	
	public static final byte[] intToByteArray(int value) {
        return new byte[] { (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value };
	}
	
}
