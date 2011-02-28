package com.lehms.messages.dataContracts;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum JobStatusDataContract 
{
     Pending(0),
     Started(1),
     Finished(2);

     private static final Map<Integer,JobStatusDataContract> _lookup 
          = new HashMap<Integer,JobStatusDataContract>();

     static {
          for(JobStatusDataContract s : EnumSet.allOf(JobStatusDataContract.class))
               _lookup.put(s.getCode(), s);
     }

     private int code;

     private JobStatusDataContract(int code) {
          this.code = code;
     }

     public int getCode() { return code; }

     public static JobStatusDataContract get(int code) { 
          return _lookup.get(code); 
     }
}