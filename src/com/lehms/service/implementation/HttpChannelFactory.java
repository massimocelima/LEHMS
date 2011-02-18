package com.lehms.service.implementation;

import com.lehms.IoC.ContainerFactory;
import com.lehms.service.IChannel;
import com.lehms.service.IChannelFactory;
import com.lehms.service.IIdentityProvider;
import com.lehms.service.IProfileProvider;
import com.lehms.service.ISerializer;
import com.lehms.service.ProfileEnvironment;

public class HttpChannelFactory implements IChannelFactory {
	
	private ISerializer _serializer;
	private IIdentityProvider _identityProvider;
	private IProfileProvider _profileProvider;
	
	public HttpChannelFactory(ISerializer serializer, 
			IIdentityProvider identityProvider,  
			IProfileProvider profileProvider)
	{
		_serializer = serializer;
		_identityProvider = identityProvider;
		_profileProvider = profileProvider;
	}
	
	@Override
	public IChannel Create(String endPoint) {
		if( _profileProvider.getProfile().GetProfileEnvironment() == ProfileEnvironment.Mock )
			return new MockChannel();
		else
			return new HttpRestChannel(endPoint, _serializer, _identityProvider);

	}

}
