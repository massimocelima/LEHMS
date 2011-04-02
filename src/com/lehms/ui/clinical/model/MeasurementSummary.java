package com.lehms.ui.clinical.model;

import java.io.Serializable;
import java.util.Date;

public class MeasurementSummary implements Serializable
{
    public String CreatedBy;
    public Date CreatedDate;
    public MeasurementTypeEnum Type;
    public String Description;
    public double PrimaryData;
    public Double SecondaryData;
}