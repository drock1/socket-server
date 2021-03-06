package com.drock.networking.messaging.handlers;

import org.apache.log4j.Logger;

import com.drock.networking.messaging.NetworkRequestMessage;
import com.drock.networking.messaging.NetworkResponseMessage;
import com.drock.networking.messaging.WrapperMessage;

public abstract class BasicRequestHandler<T extends NetworkRequestMessage, T2 extends NetworkResponseMessage>
{
	static Logger s_logger = Logger.getLogger(BasicRequestHandler.class);
	
	Class<T> m_messageClass;
	public BasicRequestHandler(Class<T> messageClass)
	{
		m_messageClass = messageClass;
	}
	
	public WrapperMessage handleMessageWrapper(WrapperMessage wrapper)
	{
		try
		{
			T message = m_messageClass.newInstance();
			message.deserializeFromWrapperMessage(wrapper);
			T2 response = handleMessage(message);
			
			if (response == null)
				return null;
			return response.serializeToWrapperMessage();
		}
		catch (Exception e)
		{
			s_logger.error("Error instantiating request message:", e);
		}
		
		return null;
	}
	
	public abstract T2 handleMessage(T message);
}
