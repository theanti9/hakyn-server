/*
 * HKCommandFilter.java
 * 
 * Asynchronous stream filter for main listener.
 * Handles incoming command packets.
 * 
 */

package com.hakyn.srv;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;

import com.hakyn.srv.protocol.HKMessage;
import com.hakyn.srv.protocol.HKMessageHeader;
import com.hakyn.srv.protocol.commands.*;


public class HKCommandFilter extends BaseFilter {

	// Fixed packet header size
	private static final int HEADER_SIZE = 9;
	
	// Handle commands as they come in
	@Override
	public NextAction handleRead(final FilterChainContext ctx) throws IOException {
		// Get incoming buffer
		final Buffer sourceBuffer = ctx.getMessage();
		final int sourceBufferLength = sourceBuffer.remaining();
		
		// If there's not enough data, try again
		if (sourceBufferLength < HEADER_SIZE) {
			return ctx.getStopAction(sourceBuffer);
		}
		
		// Get section sizes
		final int bodyLength = sourceBuffer.getInt(3);
		final int completeMessageLength = HEADER_SIZE + bodyLength;
		
		// Make sure we have enough data
		if (sourceBufferLength < completeMessageLength) {
			return ctx.getStopAction(sourceBuffer);
		}
		
		// any extra data we have will get processed later
		final Buffer remainder = sourceBufferLength > completeMessageLength ? sourceBuffer.split(completeMessageLength) : null;
		
		// Get header data out of the buffer
		byte[] headerArray = new byte[9];
		sourceBuffer.get(headerArray, 0, 9);
		
		// Create header object
		HKMessageHeader header = new HKMessageHeader(headerArray);
		
		// Get command byte
		byte[] command = new byte[] { sourceBuffer.get(7), sourceBuffer.get(8) };
		
		// Get the body from the buffer
		final byte[] body = new byte[bodyLength];
		sourceBuffer.get(body,HEADER_SIZE,bodyLength);
		
		// Determine what to do based on the command byte
		switch(command[1]) {
		
		// Position update
		case 0x01:
			// Call the UpdatePosition 
			UpdatePosition uc = new UpdatePosition(body);
			uc.run();
			// Do something like this if there's a response
			/*
			 * byte responseCommand = 0x00;
			 * byte[] msgout = uc.run();
			 * ctx.setMessage(new HKMessage(new HKMessageHeader(responseCommand, msgout.length), msgout, command));
			 * 
			 */
			break;
		}
		// Get rid of the buffer
		sourceBuffer.tryDispose();
		
		// Do stuff with whatever extra data was in the buffer
		return ctx.getInvokeAction(remainder);
	}
	
	// Write information back up the filter. this happens if we change the message inside handleRead()
	public NextAction handleWrite(final FilterChainContext ctx) throws IOException {
		// Get the message and it's size
		final HKMessage hkmessage = ctx.getMessage();
		final int size = hkmessage.length;
		
		// Grab the memory manager
		@SuppressWarnings("rawtypes")
		final MemoryManager memoryManager = ctx.getConnection().getTransport().getMemoryManager();
		
		// Create the buffer
		final Buffer output = memoryManager.allocate(size);
		output.allowBufferDispose(true);
		
		// Put the message into the buffer
		output.put(hkmessage.getBytes());
		
		// send it on it's way
		ctx.setMessage(output.flip());
		
		// Do whatever's next
		return ctx.getInvokeAction();
		
	}
	
}
