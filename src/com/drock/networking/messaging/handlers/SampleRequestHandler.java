package com.drock.networking.messaging.handlers;

import com.drock.networking.messaging.messages.requests.SampleRequest;
import com.drock.networking.messaging.messages.responses.SampleResponse;

public class SampleRequestHandler extends BasicRequestHandler<SampleRequest, SampleResponse> 
{
	public SampleRequestHandler()
	{
		super(SampleRequest.class);
	}

	@Override
	public SampleResponse handleMessage(SampleRequest message) 
	{	
		return null;
	}
}
