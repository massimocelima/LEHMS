package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;

public class ClientDataContract implements Serializable {
	
     public String ClientId;
     public String FirstName;
     public String LastName;
     public AddressDataContract Address;

     public Date DateOfBirth;
     public String Phone;
     public String Sex;
     public String EmergencyContact;
     
     public CarerDataContract Carer;
}
