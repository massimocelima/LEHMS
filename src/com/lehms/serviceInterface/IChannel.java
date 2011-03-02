package com.lehms.serviceInterface;

import java.lang.reflect.Type;


public interface IChannel {

	<T> T Get(int pageIndex, int pageSize, String orderBy, String where, Class<T> responseType) throws Exception;
	<T> T Get(Class<T> responseType) throws Exception;
	<T> T Get(Object request, Class<T> responseType) throws Exception;
	<T> T Get(String id, Class<T> responseType) throws Exception;
	<T> T ExecuteCommand(Object request, Class<T> responseType) throws Exception;
	<T> T Create(Object request, Class<T> responseType) throws Exception;
	void Delete(String id) throws Exception;
	<T> T Update(String id, Object request, Class<T> responseType) throws Exception;
	
	Object Get(Type type) throws Exception; 
}
