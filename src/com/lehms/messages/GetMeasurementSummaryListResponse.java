package com.lehms.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lehms.ui.clinical.model.MeasurementSummary;

 public class GetMeasurementSummaryListResponse implements Serializable 
 {
     public GetMeasurementSummaryListResponse()
     {
         Measurements = new ArrayList<MeasurementSummary>();
     }

     public List<MeasurementSummary> Measurements;
 }
