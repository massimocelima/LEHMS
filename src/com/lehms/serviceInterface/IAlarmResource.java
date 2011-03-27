package com.lehms.serviceInterface;

import com.lehms.messages.RaiseAlarmRequest;
import com.lehms.messages.RaiseAlarmResponse;

public interface IAlarmResource {

	RaiseAlarmResponse Raise(RaiseAlarmRequest request) throws Exception;
	
}
