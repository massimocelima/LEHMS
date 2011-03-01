package com.lehms.messages;

import java.util.List;

import com.lehms.messages.dataContracts.*;

public class GetClientDetailsResponse {
    public ClientDataContract Client;
    public DoctorDataContract Doctor;
    public PharmacyDataContract Pharmcy;
    public List<MedicalConditionDataContract> MedicalConditions;
    public List<AllergyDataContract> Allergies;
    public List<MedicationDataContract> Medication;
}
