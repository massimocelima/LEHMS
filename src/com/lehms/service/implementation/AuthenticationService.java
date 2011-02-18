package com.lehms.service.implementation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.lehms.messages.*;
import com.lehms.service.*;

public class AuthenticationService implements IAuthenticationService {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
   
	@Inject
	private AuthenticationService(IChannelFactory channelFactory, IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	@Override
	public LoginResponse Login(String username, String password) throws Exception {
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.Username = username;
		loginRequest.Password = password;
		
		//LoginResponse r = _channel.Get(LoginResponse.class);
		return GetChannel().Post(loginRequest, LoginResponse.class);
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetAuthenticationEndPoint());
	}

}
