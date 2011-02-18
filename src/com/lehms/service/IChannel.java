package com.lehms.service;

public interface IChannel {

	<T> T Get(Class<T> responseType) throws Exception;
	<T> T Get(String id, Class<T> responseType) throws Exception;
	<T> T Post(Object request, Class<T> responseType) throws Exception;
	void Delete(String id) throws Exception;
	<T> T Put(String id, Object request, Class<T> responseType) throws Exception; 
}
