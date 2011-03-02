package com.lehms.serviceInterface.implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.text.format.DateFormat;
import android.util.Base64;

import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.ISerializer;

public class HttpRestChannel implements IChannel {

	private final String _url;
	private ISerializer _serializer;
	private IIdentityProvider _identityProvider;
	private IDepartmentProvider _departmentProvider;
	
	public HttpRestChannel(String url, 
			ISerializer serializer,
			IIdentityProvider identityProvider, 
			IDepartmentProvider departmentProvider)
	{
		_url = url;
		_serializer = serializer;
		_identityProvider = identityProvider;
		_departmentProvider = departmentProvider;
	}

	@Override
	public Object Get(Type responseType) throws Exception {

		// Send GET request to <service>/GetPlates         
    	HttpGet httpRequest = new HttpGet(_url);         
    	httpRequest.setHeader("Accept", "application/json");         
    	httpRequest.setHeader("Content-type", "application/json");
		AddBasicAuthenticationHeader(httpRequest);

    	DefaultHttpClient httpClient = new DefaultHttpClient();         
    	HttpResponse response;
		response = httpClient.execute(httpRequest);
    	HttpEntity responseEntity = response.getEntity();
    	
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
    	return _serializer.Deserializer(jsonStringResponse, responseType);
	}

	
	@Override
	public <T> T Get(Class<T> responseType) throws Exception {
		return Get(null, responseType);
	}

	@Override
	public <T> T Get(Object request, Class<T> responseType) throws Exception {
		
		String url = _url;
		
		if( request != null )
		{
			Field[] fields = request.getClass().getFields();
			for (int i = 0; i < fields.length; i++ )
			{
				url += i == 0 ? "?" : "&";
				Object value = fields[i].get(request);
			
				if( Date.class.equals(value.getClass()))
					url += fields[i].getName() + "=" + DateFormat.format("yyyy-MM-dd", (Date)value);
				else
					url += fields[i].getName().toLowerCase() + "=" + value.toString();
			}
		}
		
		// Send GET request to <service>/GetPlates         
    	HttpGet httpRequest = new HttpGet(url);         
    	httpRequest.setHeader("Accept", "application/json");         
    	httpRequest.setHeader("Content-type", "application/json");
		AddBasicAuthenticationHeader(httpRequest);

    	DefaultHttpClient httpClient = new DefaultHttpClient();         
    	HttpResponse response;
		response = httpClient.execute(httpRequest);
    	HttpEntity responseEntity = response.getEntity();
    	
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
    	return _serializer.Deserializer(jsonStringResponse, responseType);
	}
	
	@Override
	public <T> T Get(int pageIndex, int pageSize, String orderBy, String where, Class<T> responseType) throws Exception {
				
		// Send GET request to <service>/GetPlates         
    	HttpGet request = new HttpGet(_url);         
    	request.setHeader("Accept", "application/json");         
    	request.setHeader("Content-type", "application/json");
		AddBasicAuthenticationHeader(request);

    	DefaultHttpClient httpClient = new DefaultHttpClient();         
    	HttpResponse response;
		response = httpClient.execute(request);
    	HttpEntity responseEntity = response.getEntity();
    	
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
    	return _serializer.Deserializer(jsonStringResponse, responseType);
	}

	@Override
	public <T> T Get(String id, Class<T> responseType) throws Exception {
		
		// Send GET request to <service>/GetPlates         
    	HttpGet request = new HttpGet(_url + "/" + id);
    	request.setHeader("Accept", "application/json");         
    	request.setHeader("Content-type", "application/json");
		AddBasicAuthenticationHeader(request);

    	DefaultHttpClient httpClient = new DefaultHttpClient();         
    	HttpResponse response;
		response = httpClient.execute(request);
    	HttpEntity responseEntity = response.getEntity();
    	
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
    	return _serializer.Deserializer(jsonStringResponse, responseType);
    	
        //List<SampleItem> items = gson.fromJson(new String(buffer), new TypeToken<List<SampleItem>>(){}.getType());
	}

	@Override
	public <T> T Create(Object request, Class<T> responseType) throws Exception {
		
		HttpPost httpRequest = new HttpPost(_url); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);

		String jsonString = _serializer.Serializer(request);
		   
		StringEntity entity = new StringEntity(jsonString); 
		httpRequest.setEntity(entity); 
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		   
		HttpEntity responseEntity =  response.getEntity();
		   
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
		T result = _serializer.Deserializer(jsonStringResponse, responseType);
		
		return result;
	}
	
	@Override
	public <T> T ExecuteCommand(Object request, Class<T> responseType) throws Exception {
		HttpPost httpRequest = new HttpPost(_url); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);

		String jsonString = _serializer.Serializer(request);
		   
		StringEntity entity = new StringEntity(jsonString); 
		httpRequest.setEntity(entity); 
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		   
		HttpEntity responseEntity =  response.getEntity();
		   
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
		T result = _serializer.Deserializer(jsonStringResponse, responseType);
		
		return result;
	}


	@Override
	public void Delete(String id) throws Exception {

		HttpDelete httpRequest = new HttpDelete(_url + "/" + id); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		
		// TODO What do we do with the response???
		
		//HttpEntity responseEntity =  response.getEntity();
		   
		//char[] buffer = new char[(int)responseEntity.getContentLength()];         
		//InputStream stream = responseEntity.getContent();         
		//InputStreamReader reader = new InputStreamReader(stream);         
		//reader.read(buffer);         
		//stream.close();          
		
		//T result = _serializer.Deserializer(new String(buffer), responseType);
		//return result;
	}

	@Override
	public <T> T Update(String id, Object request, Class<T> responseType) throws Exception {
		
		HttpPut httpRequest = new HttpPut(_url + "/" + id); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);
		
		String jsonString = _serializer.Serializer(request);
		   
		StringEntity entity = new StringEntity(jsonString); 
		httpRequest.setEntity(entity); 
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		   
		HttpEntity responseEntity =  response.getEntity();
		   
    	String jsonStringResponse = GetJsonStringFromStream(responseEntity);
		T result = _serializer.Deserializer(jsonStringResponse, responseType);
		
		return result;
	}
	
	private void AddBasicAuthenticationHeader(HttpUriRequest request) throws Exception
	{
		if( _identityProvider.isAuthenticated() )
		{
			UserDataContract user = _identityProvider.getCurrent();
			request.addHeader("Authorization", 
					//"basic " + Base64.encode( (_departmentProvider.getDepartment() + "\\" + user.Username + ":" + user.Password).getBytes(), Base64.DEFAULT ));
					"basic " + _departmentProvider.getDepartment() + "\\" + user.Username + ":" + user.Password);
		}
	}

	
	private String GetJsonStringFromStream(HttpEntity responseEntity) throws Exception
	{
		char[] buffer = new char[(int)responseEntity.getContentLength()];
		InputStream stream = responseEntity.getContent();         
		InputStreamReader reader = new InputStreamReader(stream);         
    	int bytesRead = reader.read(buffer);         
    	while(bytesRead < buffer.length)
        	bytesRead += reader.read(buffer, bytesRead, buffer.length - bytesRead);
		stream.close();
		
		return new String(buffer);
	}
	
}
