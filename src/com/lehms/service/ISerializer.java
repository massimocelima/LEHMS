package com.lehms.service;

public interface ISerializer {

	String serializer(Object o) throws Exception;
	<T> T Deserializer(String data, Class<T> type) throws Exception;
	
}
