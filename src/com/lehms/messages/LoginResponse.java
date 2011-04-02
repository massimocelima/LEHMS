package com.lehms.messages;

import java.io.Serializable;

import com.lehms.messages.dataContracts.UserDataContract;

public class LoginResponse implements Serializable  {

	public Boolean IsAuthenticated;
	public UserDataContract User;
}
