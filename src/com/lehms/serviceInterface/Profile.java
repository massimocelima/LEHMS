package com.lehms.serviceInterface;

public class Profile {

	private ProfileEnvironment _profileEnvironment;
	
	public Profile()
	{
		_profileEnvironment = com.lehms.serviceInterface.ProfileEnvironment.Development;
	}
	
	public Profile(com.lehms.serviceInterface.ProfileEnvironment profileEnvironment)
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
	
	public String GetClientResourceEndPoint()
	{
		return GetBaseUrl() + "/Client";
	}

	public String GetClientsResourceEndPoint()
	{
		return GetBaseUrl() + "/Clients";
	}

	public String GetProgressNoteResourceEndPoint()
	{
		return GetBaseUrl() + "/ProgressNote";
	}

	public String GetProgressNoteRecordingResourceEndPoint()
	{
		return GetBaseUrl() + "/ProgressNoteRecording";
	}

	public String GetProgressNotesResourceEndPoint()
	{
		return GetBaseUrl() + "/ProgressNotes";
	}
	
	public String GetFormDefinitionsResourceEndPoint()
	{
		return GetBaseUrl() + "/FormDefinitions";
	}
	
	public String GetFormDataResourceEndPoint()
	{
		return GetBaseUrl() + "/FormData";
	}

	public String GetFormDataListResourceEndPoint()
	{
		return GetBaseUrl() + "/FormDataList";
	}

	public String GetBaseUrl()
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
			result = "http://lehms2.theinsgroup.com.au:82/INSGateway";
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
