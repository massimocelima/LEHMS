package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.JobDetailsDataContract;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JobAdapter extends ArrayAdapter<JobDetailsDataContract> {

	private int _resource;
	
	public JobAdapter(Context context, int resource, List<JobDetailsDataContract> items) {
		super(context, resource, items);
		_resource = resource;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return Long.parseLong(getItem(position).JobId);
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

        JobDetailsDataContract job = getItem(position);
		
		TextView titleTextView = (TextView)view.findViewById(R.id.JobDataContract_ScheduledStartTime);
		TextView descriptionTextView = (TextView)view.findViewById(R.id.JobDataContract_Description);
		TextView typeTextView = (TextView)view.findViewById(R.id.JobDataContract_Type);
		TextView statusTextView = (TextView)view.findViewById(R.id.JobDataContract_Status);
		
		
		titleTextView.setText(DateFormat.format("h:mmaa", job.ScheduledStartTime) + " - " + job.Client.FirstName + " " + job.Client.LastName);
		descriptionTextView.setText(job.Client.Address.StreetNumber + " " + 
				job.Client.Address.Street + " " + 
				job.Client.Address.Suburb + ", " + 
				job.Client.Address.State + " " +
				job.Client.Address.Postcode);
		typeTextView.setText(job.Type + " : " + job.ExtendOfService + " " + job.UnitOfMeasure);
		statusTextView.setText( job.StatusEnum().name() );
		switch(job.StatusEnum())
		{
		case Finished:
			statusTextView.setTextColor(Color.rgb(110,23,0));
			break;
		case Pending:
			statusTextView.setTextColor(Color.rgb(0,88,167));
			break;
		case Started:
			statusTextView.setTextColor(Color.rgb(0,110,40));
			break;
		}
		
		return view;
		
	}
}
