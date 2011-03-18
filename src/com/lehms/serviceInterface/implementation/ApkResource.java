package com.lehms.serviceInterface.implementation;

import java.io.InputStream;

import com.google.inject.Inject;
import com.lehms.messages.CreateFormDataResponse;
import com.lehms.messages.dataContracts.ApkVersionInfo;
import com.lehms.serviceInterface.ContentInputStream;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IApkResource;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IProfileProvider;

public class ApkResource implements IApkResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;

	@Inject
	public ApkResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_channelFactory = channelFactory;
		_profileProvider = profileProvider;
	}
	
	@Override
	public ApkVersionInfo GetCurrentVersion() throws Exception {
		return GetVersionInfoChannel().Get(ApkVersionInfo.class);
	}

	@Override
	public ContentInputStream GetUpdate(String currentVersion) throws Exception {
		currentVersion = currentVersion.replace(".", "_");
		return GetChannel().GetStream(currentVersion);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetApkResourceEndPoint());
	}

	private IChannel GetVersionInfoChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetApkVersionInfoResourceEndPoint());
	}
}
