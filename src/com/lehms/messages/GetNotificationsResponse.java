package com.lehms.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ConsumableCostItem;
import com.lehms.messages.dataContracts.NotificationDataContract;

public class GetNotificationsResponse implements Serializable {

	public GetNotificationsResponse()
	{
		Notifications = new ArrayList<NotificationDataContract>();
	}

    public List<NotificationDataContract> Notifications;
}
