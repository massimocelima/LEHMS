package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.JobEndActionRequest;
import com.lehms.messages.JobEndActionResponse;
import com.lehms.messages.JobStartActionRequest;
import com.lehms.messages.JobStartActionResponse;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IJobResource;
import com.lehms.serviceInterface.IProfileProvider;

public class JobResource implements IJobResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private JobResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	@Override
	public JobStartActionResponse Start(JobStartActionRequest request) throws Exception {
		return GetStartChannel().ExecuteCommand(request, JobStartActionResponse.class);
	}

	@Override
	public JobEndActionResponse End(JobEndActionRequest request) throws Exception  {
		return GetEndChannel().ExecuteCommand(request, JobEndActionResponse.class);
	}

	private IChannel GetStartChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetJobStartActionResourceEndPoint() + "/");
	}

	private IChannel GetEndChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetJobEndActionResourceEndPoint() + "/");
	}


}
