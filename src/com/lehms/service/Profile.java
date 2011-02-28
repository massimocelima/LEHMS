package com.lehms.service;

public class Profile {

	private ProfileEnvironment _profileEnvironment;
	
	public Profile()
	{
		_profileEnvironment = com.lehms.service.ProfileEnvironment.Development;
	}
	
	public Profile(com.lehms.service.ProfileEnvironment profileEnvironment)
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
			result = "http://192.168.1.3/INS.Gateway.WASHost";
			//result = "http://10.0.2.2:7856";
			break;
		case Testing:
			result = "http://lehms.theinsgroup.com.au:82/INSGateway";
			//result = "https://lehms.theinsgroup.com.au:83/INSGateway";
			break;
		case Production:
			result = "http://lehms.theinsgroup.com.au:84/INSGateway";
			//result = "https://lehms.theinsgroup.com.au:85/INSGateway";
			break;
		}
		return result;
	}
	
	public ProfileEnvironment GetProfileEnvironment()
	{
		return _profileEnvironment;
	}
	
}
