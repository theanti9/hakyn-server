package com.hakyn.util;

public class ArrayUtil {
	public static final byte[] ArrayIntoArray(byte[] into, byte[] from, int start) {
		int stop = from.length;
		if (from.length + start > into.length-1) {
			stop = into.length-start-1;
		}
		
		for (int i = 0; i < stop; i++) {
			into[i+start] = from[i];
		}
		return into;
	}
	
	public static final Byte[] ArrayIntoArray(Byte[] into, Byte[] from, int start) {
		int stop = from.length;
		if (from.length + start > into.length-1) {
			stop = into.length-start-1;
		}
		
		for (int i = 0; i < stop; i++) {
			into[i+start] = from[i];
		}
		return into;
	}
	
	public static final byte[] ArrayIntoArray(byte[] into, Byte[] from, int start) {
		int stop = from.length;
		if (from.length + start > into.length-1) {
			stop = into.length-start-1;
		}
		
		for (int i = 0; i < stop; i++) {
			into[i+start] = from[i].byteValue();
		}
		return into;
	}
	
	public static final Byte[] ArrayIntoArray(Byte[] into, byte[] from, int start) {
		int stop = from.length;
		if (from.length + start > into.length-1) {
			stop = into.length-start-1;
		}
		
		for (int i = 0; i < stop; i++) {
			into[i+start] = (Byte)from[i];
		}
		return into;
	}
}
