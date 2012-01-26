package com.hakyn.queue;

public class HKIDQueueBlock {
	
	public String ID;
	public int Length;
	public Byte[] Chunk;
	
	public HKIDQueueBlock(String id, int len, Byte[] bytes) {
		this.ID = id;
		this.Length = len;
		this.Chunk = bytes;
	}

}
