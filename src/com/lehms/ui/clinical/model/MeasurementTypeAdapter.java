package com.lehms.ui.clinical.model;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeasurementTypeAdapter extends ArrayAdapter<MeasurementType> {

	private int _resource;
	
	public MeasurementTypeAdapter(Context context, int resource, List<MeasurementType> items) {
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

        MeasurementType item = getItem(position);
		
		TextView nameTextView = (TextView)view.findViewById(R.id.clinical_measurment_type_item_name);
		TextView descriptionTextView = (TextView)view.findViewById(R.id.clinical_measurment_type_item_description);
		ImageView image = (ImageView)view.findViewById(R.id.clinical_measurment_type_item_image);
		
		nameTextView.setText(item.Name);
		descriptionTextView.setText(item.Description);

		switch(item.Type)
		{
		case BP:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_blood_pressure);
			break;
		case ECG:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_heart);
			break;
		case BSL:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_device);
			break;
		case INR:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_needle);
			break;
		case SPO2:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_o2);
			break;
		case Temp:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_temp);
			break;
		case Urine:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_default);
			break;
		case Weight:
			image.setImageResource(R.drawable.dashboard_btn_clinical_details_weight);
			break;
		}
		
		return view;
		
	}
}
