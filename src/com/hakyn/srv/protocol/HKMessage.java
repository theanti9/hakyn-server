package com.hakyn.srv.protocol;

import com.hakyn.util.ArrayUtil;

public class HKMessage {
	
	public int length;
	public byte command;
	public byte[] body;
	
	private HKMessageHeader header;
	
	public HKMessage(HKMessageHeader header, byte[] bytes, byte command) {
		this.body = bytes;
		this.command = command;
		this.header = header;
		int length = bytes.length + 9;
	}
	
	public byte[] getBytes() {
		byte[] out = new byte[length];
		ArrayUtil.ArrayIntoArray(out, header.getBytes(), 0);
		ArrayUtil.ArrayIntoArray(out, body, 9);
		return out;
	}

}
