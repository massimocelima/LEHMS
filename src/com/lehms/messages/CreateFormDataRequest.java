package com.lehms.messages;

import java.io.Serializable;
import java.util.UUID;

import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.formDefinition.FormData;

public class CreateFormDataRequest implements Serializable  {

     public FormData Data;
     
     public String JobId;

     public UUID AttachmentId;
     
     /// <summary>
     /// TODO: remove this when new gateway is created
     /// </summary>
     public UUID DocumentId;

}
