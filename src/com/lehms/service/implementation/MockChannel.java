package com.lehms.service.implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

import com.lehms.IoC.ContainerFactory;
import com.lehms.messages.LoginResponse;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.service.IChannel;
import com.lehms.service.IIdentityProvider;
import com.lehms.service.ISerializer;

public class MockChannel implements IChannel {

	public MockChannel()
	{
	}

	@Override
	public <T> T Get(int pageIndex, int pageSize, String orderBy, String where, Class<T> responseType) throws Exception {
		T result = ContainerFactory.Create().resolve(responseType);
		return result;
	}

	@Override
	public <T> T Get(String id, Class<T> responseType) throws Exception {
		T result = ContainerFactory.Create().resolve(responseType);
		return result;
	}

	@Override
	public <T> T Create(Object request, Class<T> responseType) throws Exception {
		if( LoginResponse.class.equals(responseType) )
		{
			return (T)CreateLoginResponse(); 
		}
		T result = ContainerFactory.Create().resolve(responseType);
		return result;
	}
	
	@Override
	public <T> T ExecuteCommand(Object request, Class<T> responseType) throws Exception {
		return Create(request, responseType);
	}

	@Override
	public void Delete(String id) throws Exception {
	}

	@Override
	public <T> T Update(String id, Object request, Class<T> responseType) throws Exception {

		T result = ContainerFactory.Create().resolve(responseType);
		return result;
	}
	
	private LoginResponse CreateLoginResponse()
	{
		LoginResponse result = new LoginResponse();
		result.IsAuthenticated = true;
		result.User = new UserDataContract();
		result.User.Department = "LEHMS";
		result.User.Password = "none";
		result.User.Username = "none";
		result.User.Roles.add("Administrator");
		return result;
	}

	@Override
	public <T> T Get(Object request, Class<T> responseType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T Get(Class<T> responseType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Get(Type type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

