package com.lehms.messages;

import java.util.UUID;

import com.lehms.messages.formDefinition.FormData;

public class CreateFormDataRequest {

     public FormData Data;

     public String JobId;

     /// <summary>
     /// TODO: remove this when new gateway is created
     /// </summary>
     public UUID DocumentId;

}
