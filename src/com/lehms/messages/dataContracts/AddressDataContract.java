package com.lehms.messages.dataContracts;

public class AddressDataContract {

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
    		 result = Appartment + "/";
    	 
    	 result += StreetNumber + " " + Street + ", " + Suburb + ", " + Postcode + ", " + State;
    	 
    	 return result;
     }
}
