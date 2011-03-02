package com.lehms.serviceInterface.implementation;

import com.lehms.IoC.ContainerFactory;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.ISerializer;
import com.lehms.serviceInterface.ProfileEnvironment;

public class HttpChannelFactory implements IChannelFactory {
	
	private ISerializer _serializer;
	private IIdentityProvider _identityProvider;
	private IProfileProvider _profileProvider;
	private IDepartmentProvider _departmentProvider;
	
	public HttpChannelFactory(ISerializer serializer, 
			IIdentityProvider identityProvider,  
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_serializer = serializer;
		_identityProvider = identityProvider;
		_profileProvider = profileProvider;
		_departmentProvider = departmentProvider;
	}
	
	@Override
	public IChannel Create(String endPoint) {
		if( _profileProvider.getProfile().GetProfileEnvironment() == ProfileEnvironment.Mock )
			return new MockChannel();
		else
			return new HttpRestChannel(endPoint, 
					_serializer, 
					_identityProvider, 
					_departmentProvider);

	}

}
