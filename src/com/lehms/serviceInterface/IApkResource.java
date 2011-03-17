package com.lehms.serviceInterface;

import java.io.InputStream;

import com.lehms.messages.dataContracts.ApkVersionInfo;

public interface IApkResource {

	ApkVersionInfo GetCurrentVersion() throws Exception;
	ContentInputStream GetUpdate(String currentVersion) throws Exception;
	
}
