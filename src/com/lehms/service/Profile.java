package com.lehms.service;

public class Profile {

	private ProfileEnvironment _profileEnvironment;
	
	public Profile()
	{
		_profileEnvironment = ProfileEnvironment.Development;
	}
	
	public Profile(ProfileEnvironment profileEnvironment)
	{
		_profileEnvironment = profileEnvironment;
	}
	
	public String GetAuthenticationEndPoint()
	{
		return GetBaseUrl() + "/Authentication/Login";
	}
	
	public String GetRosterResourceEndPoint()
	{
		return GetBaseUrl() + "/Roster";
	}
	
	private String GetBaseUrl()
	{
		String result = "http://10.0.2.2:7856";
		switch(_profileEnvironment)
		{
		case Development:
			result = "http://192.168.0.112/INS.Gateway.WASHost";
			break;
		case DevelopmentEmulation:
			result = "http://10.0.2.2:7856";
			break;
		case Testing:
			result = "https://lehms.theinsgroup.com.au:29687";
			break;
		case Production:
			result = "https://lehms.theinsgroup.com.au:29688";
			break;
		}
		return result;
	}
	
	public ProfileEnvironment GetProfileEnvironment()
	{
		return _profileEnvironment;
	}
	
}
