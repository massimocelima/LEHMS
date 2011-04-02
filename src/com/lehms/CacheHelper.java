package com.lehms;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import com.lehms.serviceInterface.CacheItem;
import com.lehms.serviceInterface.ICache;

public class CacheHelper {

	public static void put( ICache cache, Serializable item, String name, int minutes) throws Exception
	{	
		CacheItem cacheItem = new CacheItem();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minutes);
		
		cacheItem.CachedDate = new Date();
		cacheItem.AbsoluteExpiry = calendar.getTime();
		cacheItem.Item = item;
		cacheItem.Name = "form_definitions";
		
		cache.put(name, cacheItem);
	}
}
