package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.UUID;

public class AttachmentDataContract implements Serializable  {

	public UUID Id;
	public String Name;
	public byte[] Data;
	
}
