package com.lehms.ui.clinical;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.device.IMeasurementDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeasurementDeviceAdapter extends ArrayAdapter<IMeasurementDevice> {

	private int _resource;
	
	public MeasurementDeviceAdapter(Context context, int resource, List<IMeasurementDevice> items) {
		super(context, resource, items);
		_resource = resource;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
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

        IMeasurementDevice device = getItem(position);
		
		TextView nameTextView = (TextView)view.findViewById(R.id.device_item_name);
		TextView addressTextView = (TextView)view.findViewById(R.id.device_item_address);
		
		nameTextView.setText(device.getName());
		addressTextView.setText(device.getAddress());
		
		return view;
		
	}
}
