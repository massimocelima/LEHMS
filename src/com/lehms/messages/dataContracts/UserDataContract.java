package com.lehms.messages.dataContracts;

import java.util.ArrayList;
import java.util.List;

public class UserDataContract {

	public UserDataContract()
	{
		Roles = new ArrayList<String>();
	}
	
    public String UserId;
	public String Username;
	public String Password;
	public List<String> Roles;
	public String Department;
	
    public String FirstName;
    public String LastName;
    public String Email;
    public String Phone;
    public String Mobile;
	
	public Boolean IsInRole(String role)
	{
		return Roles.contains(role);
	}
	
	public String getUserDetails()
	{
		String result = "";
		if( FirstName.equals( "" ) )
			result = "None";
		else 
			result = FirstName;

		result += " ";
		
		if( LastName.equals( "" ) )
			result += "None";
		else 
			result += LastName;

		result += " (";

		if( Email.equals( "" ) )
			result += "none@there.com";
		else 
			result += Email;

		result += ")";

		return result;
	}
	
}
