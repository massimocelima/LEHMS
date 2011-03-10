package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleDataContract implements Serializable {

	public RoleDataContract()
	{
		Permissions = new ArrayList<Permission>();
	}
	
	public String Name;
	public List<Permission> Permissions;
	
}
