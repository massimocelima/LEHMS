package com.lehms.serviceInterface;

import java.util.Date;

import com.lehms.messages.GetFormDefinitionResponse;
import com.lehms.messages.dataContracts.RosterDataContract;

public interface IFormDefinitionResource {

	GetFormDefinitionResponse Get() throws Exception;
	
}
