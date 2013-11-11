package com.drock.networking.messaging;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.drock.networking.messaging.handlers.BasicRequestHandler;

public class MessageHandlingSystem
{
	private Map<Short, BasicRequestHandler<? extends NetworkRequestMessage,? extends NetworkRequestMessage>> m_handlers;
	
	/**
	 * CTOR
	 */
	public MessageHandlingSystem()
	{
		m_handlers = new HashMap<Short, BasicRequestHandler<? extends NetworkRequestMessage,? extends NetworkRequestMessage>>();
	}
	
	/**
	 * Adds a set of message handlers to the system
	 */
	public void addDefaultHandlers()
	{
//		m_handlers.put(WrapperMessage.getMessageCode(), new SampleRequestHandler());
	}

	public WrapperMessage handleMessage(ByteBuffer rawData) 
	{
		WrapperMessage message = new WrapperMessage();
		message.deserializeFromByteBuffer(rawData);
		
		System.out.println("Version: " + message.version);
		System.out.println("Message Type: " + message.messageType);
		System.out.println("User ID: " + message.userID);
		System.out.println("Payload: " + message.payload);
		
		if (m_handlers.containsKey(message.messageType))
		{
			return m_handlers.get(message.messageType).handleMessageWrapper(message);
		}
		else
		{
			return null;
		}
	}
}
