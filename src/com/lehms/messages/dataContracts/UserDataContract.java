package com.lehms.messages.dataContracts;

import java.util.ArrayList;
import java.util.List;

public class UserDataContract {

	public UserDataContract()
	{
		Roles = new ArrayList<String>();
	}
	
	public String Username;
	public String Password;
	public List<String> Roles;
	public String Department;
	
	public Boolean IsInRole(String role)
	{
		return Roles.contains(role);
	}
	
}
