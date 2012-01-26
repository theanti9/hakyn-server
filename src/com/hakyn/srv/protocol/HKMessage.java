package com.hakyn.srv.protocol;

import java.io.IOException;

import com.hakyn.srv.protocol.commands.UpdatePosition;
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
	}
	
	public byte[] getBytes() {
		byte[] out = new byte[length];
		out = ArrayUtil.ArrayIntoArray(out, header.getBytes(), 0);
		out = ArrayUtil.ArrayIntoArray(out, body, 9);
		return out;
	}
	
	public byte[] handle() {
		byte[] out;
		
		// Switch on the command. Initializing out[] to a 0-byte array
		// means there's no return data to send.
		switch(command) {
		case 0x01:
			try {
				UpdatePosition up = new UpdatePosition(body);
				up.run();
			} catch (IOException e) {
				// Probably a bad packet. Ignore
			}
			out = new byte[0];
			break;
		case 0x02:
			// Shouldn't ever receive this packet.
			out = new byte[0];
			break;
		default:
			out = new byte[0];
		}
		
		return out;
	}

}
