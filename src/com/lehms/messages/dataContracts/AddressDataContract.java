package com.lehms.messages.dataContracts;

import java.io.Serializable;

public class AddressDataContract implements Serializable {

     public String Appartment;
     public LocationDataContract Location;
     public String Street;
     public String Suburb;
     public String State;
     public String Postcode;
     public String StreetNumber;

     public String getAddressNameForGeocoding()
     {
    	 String result = "";
    	 if(Appartment != "")
    	 {
    		 int appartmentNo = Integer.getInteger(Appartment, 0);
    		 if( appartmentNo > 0 )
    			 result = Appartment + "/";
    	 }
    	 
    	 result += StreetNumber + " " + Street + ", " + Suburb + ", " + Postcode + ", " + State;
    	 
    	 return result;
     }
}
