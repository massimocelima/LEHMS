package com.lehms.messages.dataContracts;

import java.util.ArrayList;
import java.util.List;

public class UserDataContract {

	public UserDataContract()
	{
		Roles = new ArrayList<RoleDataContract>();
	}
	
    public String UserId;
	public String Username;
	public String Password;
	public List<RoleDataContract> Roles;
	public String Department;
	
    public String FirstName;
    public String LastName;
    public String Email;
    public String Phone;
    public String Mobile;
	
	public Boolean IsInRole(String role)
	{
		Boolean result = false;
		for(int i = 0; i < Roles.size(); i++)
		{
			if(Roles.get(i).Name.equals(role))
			{
				result = true;
				break;
			}
		}
		return result;
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

		return result;
	}
	
	public String GetFullUsername()
	{
		String fullUsername = Department + "\\" + Username;
		return fullUsername;
	}
	
}
