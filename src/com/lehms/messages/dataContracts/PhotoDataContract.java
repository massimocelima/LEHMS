package com.lehms.messages.dataContracts;

import java.util.Date;

public class PhotoDataContract {

	public String Name;
	public Date CreatedDate;
	public PhotoType Type;
	public String ClientId;

	@Override
	public String toString() {
		return Name;
	}
}
