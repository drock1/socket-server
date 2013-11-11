package com.drock.networking.messaging;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class WrapperMessage
{
	public byte   version;
	public short  messageType;
	public int    userID;
	public String payload;
	
	public void deserializeFromByteBuffer(ByteBuffer buffer) 
	{
		version = buffer.get();
		messageType = buffer.getShort();
		userID = buffer.getInt();
		
		byte[] payloadBytes = new byte[buffer.remaining()];
		buffer.get(payloadBytes);
		try
		{
			payload = new String(payloadBytes, "US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			payload = null;
		}
	}

	public byte[] serializeToBytes() 
	{
		byte[] payloadBytes = payload.getBytes();
		
		ByteBuffer buffer = ByteBuffer.allocate(7 + payloadBytes.length);
		buffer.put(version);
		buffer.putShort(messageType);
		buffer.putInt(userID);
		buffer.put(payloadBytes);
		
		return buffer.array();
	}
}
