package com.lehms.serviceInterface;

import com.lehms.messages.JobEndActionRequest;
import com.lehms.messages.JobEndActionResponse;
import com.lehms.messages.JobStartActionRequest;
import com.lehms.messages.JobStartActionResponse;
import com.lehms.messages.dataContracts.JobDetailsDataContract;

public interface IJobResource {

	JobStartActionResponse Start(JobStartActionRequest request) throws Exception ;
	JobEndActionResponse End(JobEndActionRequest request) throws Exception ;
	
}
