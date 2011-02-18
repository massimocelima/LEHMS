package com.lehms;

import com.lehms.service.*;
import com.lehms.service.implementation.*;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

public class ConfigurationModule extends AbstractAndroidModule {

	private LehmsApplication _context;
	
	public ConfigurationModule(LehmsApplication context)
	{
		_context = context;
	}
	
	@Override
	protected void configure() {
		
		bind(ISerializer.class).to(JsonSerializer.class);
		bind(IAuthenticationService.class).to(AuthenticationService.class);
		
		bind(IIdentityProvider.class).toInstance(_context);
		bind(IProfileProvider.class).toInstance(_context);
		bind(IChannelFactory.class).toInstance(
				new HttpChannelFactory(new JsonSerializer(), 
				_context, 
				_context));
	}
	
}
