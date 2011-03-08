package com.lehms.serviceInterface;

import java.lang.reflect.Type;

public interface ISerializer {

	String Serializer(Object o) throws Exception;
	<T> T Deserializer(String data, Class<T> type) throws Exception;
	Object Deserializer(String data, Type type) throws Exception;
	
	String GetSerializerContentType();
	String GetDeserializerContentType();
}
