package com.hakyn.srv.protocol;

import java.io.IOException;
import java.util.Arrays;
import com.hakyn.util.*;
public class HKMessageHeader {
	public static final String HEAD_TAG = "HAK";
	
	private byte[] bytes;
	private int length;
	private byte[] commandBytes;
	private byte command;
	
	
	// Message format
	// bytes 1-3 should always be the letters HAK - this helps denote the start of a message
	// bytes 4-7 should be a 32bit integer denoting the length of the packet minus the 9 byte header
	// bytes 8&9 are used to denote the command being given
	public HKMessageHeader(byte[] bytes) throws IOException {
		// Get only the header bytes
		this.bytes = Arrays.copyOfRange(bytes, 0, 8);
		// Make sure the message starts with the right header
		if (!Arrays.equals(HEAD_TAG.getBytes(), Arrays.copyOfRange(this.bytes, 0, 2))) {
			throw new IOException("Invalid message header");
		}
		
		// get the length of the message
		this.length = Converter.networkToInt(this.bytes, 3);
		
		this.commandBytes = Arrays.copyOfRange(this.bytes, 7, 8);
		
		
		// TODO: Fill in with commands as we create them.
//		switch (commandBytes[1]) {
//		case 0x01:
//			this.command = MessageCommands.UPDATE_POSITION;
//			break;
//		}
	}
	
	public HKMessageHeader(byte command, int bodyLength) {
		this.bytes = new byte[9];
		ArrayUtil.ArrayIntoArray(bytes, HEAD_TAG.getBytes(), 0);
		this.length = bodyLength;
		ArrayUtil.ArrayIntoArray(bytes, Converter.intToByteArray(bodyLength), 3);
		this.command = command;
		ArrayUtil.ArrayIntoArray(bytes, new byte[] { 0x00, command }, 7);
		this.length = bodyLength;
	}
	
	public byte[] getBytes() {
		return this.bytes;
	}
}
