package com.hakyn.util;

import java.util.Arrays;

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
	
	public static final byte[] GetToFirstNull(byte[] from) {
		int i = 0;
		int count = 0;
		while (from[i] != 0x00) {
			count++;
		}
		return Arrays.copyOfRange(from, 0, count);
	}
	
	public static final byte[] FillExtraWithNull(byte[] into, byte[] from, int width, int offset) throws IndexOutOfBoundsException {
		if (offset+width > into.length) {
			throw new IndexOutOfBoundsException();
		}
		
		int i;
		for (i = 0; i < from.length; i++) {
			into[i+offset] = from[i];
		}
		for (/* use current i */;i<width; i++) {
			into[i+offset] = 0x00;
		}
		return into;
	}
	
}
