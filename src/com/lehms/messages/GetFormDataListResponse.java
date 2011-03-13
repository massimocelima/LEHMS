package com.lehms.messages;

import java.util.ArrayList;
import java.util.List;

import com.lehms.messages.dataContracts.FormDataSummary;

public class GetFormDataListResponse {
	
    public GetFormDataListResponse()
    {
        FormDataSummaries = new ArrayList<FormDataSummary>();
    }

    public List<FormDataSummary> FormDataSummaries;
}
