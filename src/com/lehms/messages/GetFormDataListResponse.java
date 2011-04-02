package com.lehms.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lehms.messages.dataContracts.FormDataSummaryDataContract;

public class GetFormDataListResponse implements Serializable  {
	
    public GetFormDataListResponse()
    {
        FormDataSummaries = new ArrayList<FormDataSummaryDataContract>();
    }

    public List<FormDataSummaryDataContract> FormDataSummaries;
}
