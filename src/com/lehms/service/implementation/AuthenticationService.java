package com.lehms.service.implementation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.lehms.messages.*;
import com.lehms.service.*;

public class AuthenticationService implements IAuthenticationService {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	private IDepartmentProvider _departmentProvider;
	
	@Inject
	private AuthenticationService(IChannelFactory channelFactory, 
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
		_departmentProvider = departmentProvider;
	}
	
	@Override
	public LoginResponse Login(String username, String password) throws Exception {
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.Username = username;
		loginRequest.Password = password;
		loginRequest.Department = _departmentProvider.getDepartment();
		
		return GetChannel().ExecuteCommand(loginRequest, LoginResponse.class);
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetAuthenticationEndPoint());
	}

}
