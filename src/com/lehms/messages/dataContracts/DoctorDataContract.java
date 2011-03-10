package com.lehms.messages.dataContracts;

import java.io.Serializable;

public class DoctorDataContract implements Serializable {
     public long DoctorId;
     public AddressDataContract Address;
     public String FirstName;
     public String LastName;
     public String PhoneNumber;
     public String FaxNumber;
     public String Email;
}
