package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.JobDataContract;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JobAdapter extends ArrayAdapter<JobDataContract> {

	private int _resource;
	
	public JobAdapter(Context context, int resource, List<JobDataContract> items) {
		super(context, resource, items);
		_resource = resource;
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

		JobDataContract job = getItem(position);
		
		TextView titleTextView = (TextView)view.findViewById(R.id.JobDataContract_ScheduledStartTime);
		TextView descriptionTextView = (TextView)view.findViewById(R.id.JobDataContract_Description);
		TextView typeTextView = (TextView)view.findViewById(R.id.JobDataContract_Type);
		
		titleTextView.setText(DateFormat.format("h:mmaa", job.ScheduledStartTime) + " - " + job.Client.FirstName + " " + job.Client.LastName);
		descriptionTextView.setText(job.Client.Address.StreetNumber + " " + 
				job.Client.Address.Street + " " + 
				job.Client.Address.Suburb + ", " + 
				job.Client.Address.State + " " +
				job.Client.Address.Postcode);
		typeTextView.setText(job.Type + " : " + job.ExtendOfService + " " + job.UnitOfMeasure);
		
		return view;
		
	}
}
