package com.lehms.serviceInterface;

import com.lehms.messages.dataContracts.Permission;

public interface IAuthorisationProvider {

	Boolean isAuthorised(Permission permission);
	Boolean isInRole(String role);
	
}
