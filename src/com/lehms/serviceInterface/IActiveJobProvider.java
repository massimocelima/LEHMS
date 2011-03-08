package com.lehms.serviceInterface;

import com.lehms.messages.dataContracts.JobDetailsDataContract;

public interface IActiveJobProvider {

	JobDetailsDataContract get();
	void set(JobDetailsDataContract job);
	
}
