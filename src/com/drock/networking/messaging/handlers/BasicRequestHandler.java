package com.drock.networking.messaging.handlers;

import com.drock.networking.messaging.NetworkRequestMessage;
import com.drock.networking.messaging.NetworkResponseMessage;
import com.drock.networking.messaging.WrapperMessage;

public abstract class BasicRequestHandler<T extends NetworkRequestMessage, T2 extends NetworkResponseMessage>
{
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
			return handleMessage(message).serializeToWrapperMessage();
		}
		catch (Exception e)
		{
			
		}
		
		return null;
	}
	
	public abstract T2 handleMessage(T message);
}
