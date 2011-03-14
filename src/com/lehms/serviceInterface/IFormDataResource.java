package com.lehms.serviceInterface;

import java.util.UUID;

import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.CreateFormDataResponse;
import com.lehms.messages.GetFormDataListResponse;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.formDefinition.FormData;

public interface IFormDataResource {

	GetFormDataListResponse Get(long clientId, UUID formDefinitionId, int skip, int take) throws Exception;
	CreateFormDataResponse Create(CreateFormDataRequest data) throws Exception;
	CreateFormDataResponse Create(CreateFormDataRequest request, AttachmentDataContract attachment) throws Exception;
	FormData Get(UUID formDataId) throws Exception;
	
}
