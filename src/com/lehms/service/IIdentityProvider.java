package com.lehms.service;

import com.lehms.messages.dataContracts.UserDataContract;

public interface IIdentityProvider {
	
	 UserDataContract getCurrent() throws Exception;
	 void setCurrent(UserDataContract user) throws Exception;
	 void logout() throws Exception;
	 Boolean isAuthenticated();
}
