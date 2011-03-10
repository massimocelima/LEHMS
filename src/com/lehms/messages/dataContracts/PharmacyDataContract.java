package com.lehms.messages.dataContracts;

import java.io.Serializable;

public class PharmacyDataContract implements Serializable {
     public long PharmacyId;
     public AddressDataContract Address;
     public String Email;
     public String FaxNumber;
     public String Name;
     public String PhoneNumber;
}
