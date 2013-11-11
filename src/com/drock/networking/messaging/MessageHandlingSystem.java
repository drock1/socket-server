package com.drock.networking.messaging;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.drock.networking.messaging.handlers.BasicRequestHandler;
import com.drock.networking.messaging.handlers.SampleRequestHandler;

public class MessageHandlingSystem
{
	static Logger s_logger = Logger.getLogger(MessageHandlingSystem.class);
	
	private Map<Short, BasicRequestHandler<? extends NetworkRequestMessage,? extends NetworkResponseMessage>> m_handlers;
	
	/**
	 * CTOR
	 */
	public MessageHandlingSystem()
	{
		m_handlers = new HashMap<Short, BasicRequestHandler<? extends NetworkRequestMessage,? extends NetworkResponseMessage>>();
	}
	
	/**
	 * Adds a set of message handlers to the system
	 */
	public void addDefaultHandlers()
	{
		m_handlers.put(SampleRequestHandler.getMessageCode(), new SampleRequestHandler());
	}

	public WrapperMessage handleMessage(ByteBuffer rawData) 
	{
		WrapperMessage message = new WrapperMessage();
		message.deserializeFromByteBuffer(rawData);
		
		s_logger.debug("Version: " + message.version);
		s_logger.debug("Message Type: " + message.messageType);
		s_logger.debug("User ID: " + message.userID);
		s_logger.debug("Payload: " + message.payload);
		
		if (m_handlers.containsKey(message.messageType))
		{
			return m_handlers.get(message.messageType).handleMessageWrapper(message);
		}
		else
		{
			s_logger.warn("Unknown message type: " + message.messageType + " consider adding a handler for it");
			return null;
		}
	}
}
