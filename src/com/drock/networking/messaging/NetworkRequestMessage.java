package com.drock.networking.messaging;


public interface NetworkRequestMessage
{
	public void deserializeFromWrapperMessage(WrapperMessage wrapper);
}
