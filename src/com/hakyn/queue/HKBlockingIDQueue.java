package com.hakyn.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hakyn.util.ArrayUtil;
import com.hakyn.util.Converter;

public class HKBlockingIDQueue {
	private static List<Byte> queue = Collections.synchronizedList(new ArrayList<Byte>());
	private static int idLength;
	
	public HKBlockingIDQueue(int idLen) {
		idLength = idLen;
	}
	
	public void EnqueueData(String clientId, byte[] bytes) {
		int len = idLength + 4 + bytes.length;
		Byte[] n = new Byte[len];
		n = ArrayUtil.ArrayIntoArray(n, clientId.getBytes(), 0);
		n = ArrayUtil.ArrayIntoArray(n, Converter.intToByteArray(bytes.length), idLength);
		n = ArrayUtil.ArrayIntoArray(n, bytes, idLength+4);
		// Block the queue while we add the bytes
		synchronized(queue) {
			for (int i = 0; i < n.length; i++) {
				queue.add((Byte)n[i]);
			}
		}
	}
	
	public HKIDQueueBlock dequeueChunk() {
		// block while we pull stuff off
		synchronized(queue) {
			// get the id Byte array and then put it into a string so we 
			// can pass it to our HKIDQueueBlock
			Byte[] id = (Byte[])queue.subList(0, idLength).toArray(); 
			byte[] strid = new byte[id.length];
			strid = ArrayUtil.ArrayIntoArray(strid, id, 0);
			int len = Converter.networkToInt((Byte[])queue.subList(idLength, idLength+5).toArray());
			Byte[] data = (Byte[])queue.subList(idLength+5, idLength+len+6).toArray();
			queue.removeAll(queue.subList(0, idLength+len+5));
			return new HKIDQueueBlock(new String(strid), len, data);
		}
	}
	
	public boolean isEmpty() {
		return (queue.size() == 0);
	}
	
}
