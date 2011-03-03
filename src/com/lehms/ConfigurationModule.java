package com.lehms;

import android.content.Context;

import com.lehms.persistence.*;
import com.lehms.serviceInterface.*;
import com.lehms.serviceInterface.implementation.*;

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
		bind(IAuthenticationProvider.class).to(AuthenticationProvider.class);
		bind(IRosterResource.class).to(RosterResource.class);
		bind(IClientResource.class).to(ClientResource.class);
		bind(IProgressNoteResource.class).to(ProgressNoteResource.class);
		
		bind(IRosterRepository.class).toInstance(new RosterRepository(new JsonSerializer(), _context, _context));
		bind(IEventRepository.class).toInstance(new EventRepository(_context, new JsonSerializer(),_context));
				
		bind(IDepartmentProvider.class).toInstance(_context);
		bind(IDeviceIdentifierProvider.class).toInstance(_context);
		bind(IIdentityProvider.class).toInstance(_context);
		bind(IProfileProvider.class).toInstance(_context);
		bind(IOfficeContactProvider.class).toInstance(_context);
		bind(IAuthorisationProvider.class).toInstance(_context);
		
		bind(IChannelFactory.class).toInstance(
				new HttpChannelFactory(new JsonSerializer(), 
				_context, 
				_context, 
				_context));
	}
	
}
