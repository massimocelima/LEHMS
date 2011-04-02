package com.lehms.serviceInterface;

import java.lang.reflect.Type;


public interface ICache {

	CacheItem get(String name) throws Exception;
	void put(String name, CacheItem item) throws Exception;
	
}
