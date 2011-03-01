package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClientSummaryAdapter extends ArrayAdapter<ClientSummaryDataContract> {

	private int _resource;
	
	public ClientSummaryAdapter(Context context, int resource, List<ClientSummaryDataContract> items) {
		super(context, resource, items);
		_resource = resource;
	}
	
	@Override
	public long getItemId(int position) {
		return Long.parseLong(getItem(position).ClientId);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout view;
		
        if( convertView == null )         
        {             
    		view = new LinearLayout(getContext());   

    		LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(this.getContext().LAYOUT_INFLATER_SERVICE);
    		layoutInflater.inflate(_resource, view, true);
        }
        else
        {
        	view = (LinearLayout)convertView; 
        }

        ClientSummaryDataContract client = getItem(position);
		
		TextView firstNameTextView = (TextView)view.findViewById(R.id.client_item_first_name);
		TextView lastNameTextView = (TextView)view.findViewById(R.id.client_item_last_name);
		TextView clientIdTextView = (TextView)view.findViewById(R.id.client_item_client_id);
		
		firstNameTextView.setText(client.FirstName);
		lastNameTextView.setText(client.LastName);
		clientIdTextView.setText(client.ClientId);
		
		return view;
		
	}
}
