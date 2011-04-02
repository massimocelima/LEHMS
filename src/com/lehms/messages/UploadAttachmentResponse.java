package com.lehms.messages;

import java.io.Serializable;
import java.util.UUID;

public class UploadAttachmentResponse implements Serializable  {

	public UUID AttachmentId;

	/// <summary>
	/// TODO: remove this when new gateway is created
	/// </summary>
	public UUID DocumentId;
	
}
