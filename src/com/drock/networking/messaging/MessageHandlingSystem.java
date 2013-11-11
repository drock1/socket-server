package com.drock.networking.messaging;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drock.networking.messaging.handlers.BasicRequestHandler;

public class MessageHandlingSystem
{
	static Logger s_logger = LogManager.getLogger(MessageHandlingSystem.class);
	
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
			return null;
		}
	}
}
