package com.lehms.IoC;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lehms.ConfigurationModule;
import com.lehms.LehmsApplication;

public class Container implements IContainer {

	private Injector _injector;
	
	public Container(LehmsApplication context)
	{
		_injector = Guice.createInjector(new ConfigurationModule(context));
	}

	@Override
	public <T> T resolve(Class<T> annotationType) {
		return _injector.getInstance(annotationType);
	}
	
}
