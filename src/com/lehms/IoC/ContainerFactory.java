package com.lehms.IoC;

import com.lehms.LehmsApplication;

public class ContainerFactory {

	private ContainerFactory() {}
	
	public static IContainer Create(LehmsApplication context)
	{
		return new Container(context);
	}
	
}
