package com.lehms.serviceInterface;

import com.lehms.messages.*;

public interface INotificationsResource {

	GetNotificationsResponse Get(GetNotificationsRequest request) throws Exception;
	
}
