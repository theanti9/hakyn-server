package com.hakyn.srv.protocol;

import java.io.IOException;
import java.util.Arrays;

import com.hakyn.util.ArrayUtil;
import com.hakyn.util.Converter;
public class HKMessageHeader {
	public static final String HEAD_TAG = "HAK";
	public int length;
	public byte command;
	public byte error = 0x00;
	private byte[] bytes;
	private byte[] commandBytes;
	
	// Message format
	// bytes 1-3 should always be the letters HAK - this helps denote the start of a message
	// bytes 4-7 should be a 32bit integer denoting the length of the packet minus the 9 byte header
	// bytes 8&9 are used to denote the command being given
	public HKMessageHeader(byte[] bytes) throws IOException {
		// Get only the header bytes
		this.bytes = Arrays.copyOfRange(bytes, 0, 9);
		// Make sure the message starts with the right header
		if (!Arrays.equals(HEAD_TAG.getBytes(), Arrays.copyOfRange(this.bytes, 0, 3))) {
			throw new IOException("Invalid message header");
		}
		
		// get the length of the message
		this.length = Converter.networkToInt(this.bytes, 3);
		this.commandBytes = Arrays.copyOfRange(this.bytes, 7, 9);
		this.command = this.commandBytes[1];
	}
	
	public HKMessageHeader(byte command, byte error, int bodyLength) {
		this.bytes = new byte[9];
		bytes = ArrayUtil.ArrayIntoArray(bytes, HEAD_TAG.getBytes(), 0);
		this.length = bodyLength;
		bytes = ArrayUtil.ArrayIntoArray(bytes, Converter.intToByteArray(bodyLength), 3);
		this.command = command;
		this.error = error;
		bytes = ArrayUtil.ArrayIntoArray(bytes, new byte[] { error, command }, 7);
		this.length = bodyLength;
	}
	
	public void setError(byte err) {
		this.error = err;
	}
	
	public byte[] getBytes() {
		return this.bytes;
	}
}
