package com.lehms.service;

import com.lehms.messages.LoginResponse;

public interface IAuthenticationService {

	LoginResponse Login(String username, String password) throws Exception;
}
