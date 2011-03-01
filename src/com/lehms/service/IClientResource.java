package com.lehms.service;

import java.util.List;

import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;

public interface IClientResource {

	List<ClientSummaryDataContract> GetClientSummaries() throws Exception;
	GetClientDetailsResponse GetClientDetails(long _clientId) throws Exception;
	
}
