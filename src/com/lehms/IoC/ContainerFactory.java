package com.lehms.IoC;

import com.lehms.LehmsApplication;

public class ContainerFactory {

	private static IContainer _container;
	
	private ContainerFactory() {}
	
	public static IContainer Create()
	{
		return _container;
	}
	
	public static void SetContainer(IContainer container)
	{
		_container = container;
	}
	
}
