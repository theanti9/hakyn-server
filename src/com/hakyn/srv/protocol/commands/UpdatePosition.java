/*
 * UpdatePosition.java
 * 
 * One of the command objects to be used by incoming packets and responses.
 * 
 */

package com.hakyn.srv.protocol.commands;

import java.io.IOException;
import java.util.Arrays;

import com.hakyn.srv.service.HKPositioningService;
import com.hakyn.util.Converter;
import com.hakyn.util.ArrayUtil;
import com.hakyn.game.HKCharacter;

public class UpdatePosition {
	
	private static final int DATA_LENGTH = 32;
	private static final byte COMMAND_BYTE = 0x01;
	private String characterId;
	private int x;
	private int y;
	
	// Construct object given a byte array (used when reading data in from the network)
	public UpdatePosition(byte[] bytes) throws IOException {
		// If the packet isn't the right length, throw an exception
		if (bytes.length != DATA_LENGTH) {
			throw new IOException("Invalid command length");
		}
		
		// Get the character ID. Should be a 24 character string - byte 0-23 in the array
		this.characterId = new String(Arrays.copyOfRange(bytes, 0, 23));
		
		// x and y coordinate are the remaining 8 bytes, each 32bit integers 
		this.x = Converter.networkToInt(bytes, 24);
		this.y = Converter.networkToInt(bytes, 28);
	}
	
	// Construct object given parameters (used when sending data out of the network)
	public UpdatePosition(String characterId, int x, int y) {
		// Set object attributes appropriately
		this.characterId = characterId;
		this.x = x;
		this.y = y;
	}
	
	public void run() throws IOException {
		// Call the position update method in the positioning service
		HKPositioningService.updateCharacterPosition(this.characterId, this.x, this.y);
	}
	
	// Get packet bytes for sending
	public byte[] GetMessage() {
		// packet should be 41 bytes - 9 for fixed header and 32 for data
		byte[] out = new byte[41];
		// HAK header
		out[0] = 0x48;
		out[1] = 0x41;
		out[2] = 0x4b;
		
		// Non-header data length, 32 bytes in this case
		ArrayUtil.ArrayIntoArray(out, Converter.intToByteArray(DATA_LENGTH), 3);
		ArrayUtil.ArrayIntoArray(out, new byte[] { 0x00, COMMAND_BYTE }, 7);
		
		// Start body
		ArrayUtil.ArrayIntoArray(out, characterId.getBytes(), 9);
		ArrayUtil.ArrayIntoArray(out, Converter.intToByteArray(x), 33);
		ArrayUtil.ArrayIntoArray(out, Converter.intToByteArray(y), 37);
		return out;
		
	}
	
	
}
