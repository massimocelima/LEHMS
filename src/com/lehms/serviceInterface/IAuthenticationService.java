package com.lehms.serviceInterface;

import com.lehms.messages.LoginResponse;

public interface IAuthenticationService {

	LoginResponse Login(String username, String password) throws Exception;
}
