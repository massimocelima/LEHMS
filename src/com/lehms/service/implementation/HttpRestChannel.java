package com.lehms.service.implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Base64;

import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.service.IChannel;
import com.lehms.service.IIdentityProvider;
import com.lehms.service.ISerializer;

public class HttpRestChannel implements IChannel {

	private String _url;
	private ISerializer _serializer;
	private IIdentityProvider _identityProvider;
	
	public HttpRestChannel(String url, 
			ISerializer serializer,
			IIdentityProvider identityProvider)
	{
		_url = url;
		_serializer = serializer;
		_identityProvider = identityProvider;
	}

	@Override
	public <T> T Get(Class<T> responseType) throws Exception {
				
		// Send GET request to <service>/GetPlates         
    	HttpGet request = new HttpGet(_url);         
    	request.setHeader("Accept", "application/json");         
    	request.setHeader("Content-type", "application/json");
		AddBasicAuthenticationHeader(request);

    	DefaultHttpClient httpClient = new DefaultHttpClient();         
    	HttpResponse response;
		response = httpClient.execute(request);
    	HttpEntity responseEntity = response.getEntity();
    	
    	// Read response data into buffer         
    	char[] buffer = new char[(int)responseEntity.getContentLength()];         
    	InputStream stream = responseEntity.getContent();         
    	InputStreamReader reader = new InputStreamReader(stream);         
    	reader.read(buffer);         
    	stream.close();          
    	
    	return _serializer.Deserializer(new String(buffer), responseType);
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
    	
    	// Read response data into buffer         
    	char[] buffer = new char[(int)responseEntity.getContentLength()];         
    	InputStream stream = responseEntity.getContent();         
    	InputStreamReader reader = new InputStreamReader(stream);         
    	reader.read(buffer);         
    	stream.close();          
    	
    	return _serializer.Deserializer(new String(buffer), responseType);
    	
        //List<SampleItem> items = gson.fromJson(new String(buffer), new TypeToken<List<SampleItem>>(){}.getType());
	}

	@Override
	public <T> T Post(Object request, Class<T> responseType) throws Exception {
		
		HttpPost httpRequest = new HttpPost(_url); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);

		String jsonString = _serializer.serializer(request);
		   
		StringEntity entity = new StringEntity(jsonString); 
		httpRequest.setEntity(entity); 
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		   
		HttpEntity responseEntity =  response.getEntity();
		   
		char[] buffer = new char[(int)responseEntity.getContentLength()];         
		InputStream stream = responseEntity.getContent();         
		InputStreamReader reader = new InputStreamReader(stream);         
		reader.read(buffer);         
		stream.close();          
		
		T result = _serializer.Deserializer(new String(buffer), responseType);
		
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
	public <T> T Put(String id, Object request, Class<T> responseType) throws Exception {
		
		HttpPut httpRequest = new HttpPut(_url + "/" + id); 
		httpRequest.setHeader("Accept", "application/json");             
		httpRequest.setHeader("Content-type", "application/json"); 
		AddBasicAuthenticationHeader(httpRequest);
		
		String jsonString = _serializer.serializer(request);
		   
		StringEntity entity = new StringEntity(jsonString); 
		httpRequest.setEntity(entity); 
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		   
		HttpEntity responseEntity =  response.getEntity();
		   
		char[] buffer = new char[(int)responseEntity.getContentLength()];         
		InputStream stream = responseEntity.getContent();         
		InputStreamReader reader = new InputStreamReader(stream);         
		reader.read(buffer);         
		stream.close();          
		
		T result = _serializer.Deserializer(new String(buffer), responseType);
		
		return result;
	}
	
	private void AddBasicAuthenticationHeader(HttpUriRequest request) throws Exception
	{
		if( _identityProvider.isAuthenticated() )
		{
			UserDataContract user = _identityProvider.getCurrent();
			request.addHeader("Authorization", 
					"basic " + Base64.encode( (user.Username + ":" + user.Password).getBytes(), Base64.DEFAULT ));
		}
	}

}
