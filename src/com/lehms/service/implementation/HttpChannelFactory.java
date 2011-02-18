package com.lehms.service.implementation;

import com.lehms.IoC.ContainerFactory;
import com.lehms.service.IChannel;
import com.lehms.service.IChannelFactory;
import com.lehms.service.IIdentityProvider;
import com.lehms.service.ISerializer;

public class HttpChannelFactory implements IChannelFactory {
	
	private ISerializer _serializer;
	private IIdentityProvider _identityProvider;
	
	public HttpChannelFactory(ISerializer serializer, 
			IIdentityProvider identityProvider)
	{
		_serializer = serializer;
		identityProvider = _identityProvider;
	}
	
	@Override
	public IChannel Create(String endPoint) {
		return new HttpRestChannel(endPoint, _serializer, _identityProvider);
	}

}
