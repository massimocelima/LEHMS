package com.lehms.serviceInterface;

import java.io.Serializable;
import java.util.Date;

public class CacheItem implements Serializable {

	public String Name;
	public Date CachedDate;
	public Serializable Item;
	public Date AbsoluteExpiry;
	
	public boolean hasExpired()
	{
		return AbsoluteExpiry.getTime() < (new Date()).getTime();
	}
}
