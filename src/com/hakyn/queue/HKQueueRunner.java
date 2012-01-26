package com.hakyn.queue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.hakyn.srv.HKTCPService;
import com.hakyn.srv.protocol.HKMessage;
import com.hakyn.srv.protocol.HKMessageHeader;
import com.hakyn.util.ArrayUtil;

public class HKQueueRunner implements Runnable{

	public static HashMap<String, byte[]> bufferMap = new HashMap<String, byte[]>();

	@Override
	public void run() {
		HKIDQueueBlock block = HKTCPService.dataQueue.dequeueChunk();
		byte[] cur = bufferMap.get(block.ID);
		byte[] nw = new byte[cur.length+block.Length];
		nw = ArrayUtil.ArrayIntoArray(nw, cur, 0);
		nw = ArrayUtil.ArrayIntoArray(nw, block.Chunk, cur.length);
		bufferMap.put(block.ID,nw);
		
		if (nw.length > 10) {
			try {
				HKMessageHeader header = new HKMessageHeader(nw);
				// If we don't have the whole message in the buffer
				// Skip for now
				if (nw.length < header.length+9) {
					return;
				}
				HKMessage msg = new HKMessage(header, Arrays.copyOfRange(nw, 9, header.length+9), header.command);
				msg.handle();
			} catch (IOException e) {
				// TODO: something....
			}
		}
		
	}
	
	
}
