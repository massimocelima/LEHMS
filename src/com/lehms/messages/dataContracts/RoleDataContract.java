package com.lehms.messages.dataContracts;

import java.util.ArrayList;
import java.util.List;

public class RoleDataContract {

	public RoleDataContract()
	{
		Permissions = new ArrayList<Permission>();
	}
	
	public String Name;
	public List<Permission> Permissions;
	
}
