package com.lehms.ui.clinical;

import java.util.List;

import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeasurementSummaryAdapter extends ArrayAdapter<MeasurementSummary> {

	private int _resource;
	
	public MeasurementSummaryAdapter(Context context, int resource, List<MeasurementSummary> items) {
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

        MeasurementSummary measurement = getItem(position);

		TextView createdTextView = (TextView)view.findViewById(R.id.measurement_summary_item_created);
		TextView descriptionTextView  = (TextView)view.findViewById(R.id.measurement_summary_item_description);
		
		TextView primaryTextView = (TextView)view.findViewById(R.id.measurement_summary_item_primary_value);
		TextView primaryLabelTextView = (TextView)view.findViewById(R.id.measurement_summary_item_primary_label);
		
		View secondaryContainer = view.findViewById(R.id.measurement_summary_item_secondary_container);
		TextView secondaryTextView = (TextView)view.findViewById(R.id.measurement_summary_item_secondary_value);
		TextView secondaryLabelTextView = (TextView)view.findViewById(R.id.measurement_summary_item_secondary_label);
				
		createdTextView.setText( UIHelper.FormatLongDateTime(measurement.CreatedDate));
		primaryTextView.setText( measurement.PrimaryData + "");
		
		if( measurement.SecondaryData == null)
			secondaryContainer.setVisibility(View.GONE);
		else
			secondaryTextView.setText( measurement.SecondaryData + "");
		
		descriptionTextView.setText( measurement.Description);
		
		SetupLabels(measurement.Type, primaryLabelTextView, secondaryLabelTextView );
		
		return view;
	}
	
	private void SetupLabels(MeasurementTypeEnum type, TextView primaryLabelTextView, TextView secondaryLabelTextView)
	{
		switch(type)
		{
		case BP:
			primaryLabelTextView.setText("Sysyolic (mmHg):" );
			secondaryLabelTextView.setText("Diastolic (mmHg):" );
			break;
		case BSL:
			primaryLabelTextView.setText("BSL (mmol/l):" );
			secondaryLabelTextView.setText("Insulin (UI/ml):" );
			break;
		case ECG:
			primaryLabelTextView.setText("Puls (bpm):" );
			break;
		case INR:
			primaryLabelTextView.setText("INR:" );
			break;
		case SPO2:
			primaryLabelTextView.setText("SPO2 (%):" );
			secondaryLabelTextView.setText("Pulse (bpm):" );
			break;
		case Temp:
			primaryLabelTextView.setText("Temperature (c):" );
			break;
		case Urine:
			primaryLabelTextView.setText("Protein:" );
			secondaryLabelTextView.setText("Glucose:" );
			break;
		case Weight:
			primaryLabelTextView.setText("Weight (kg):" );
			break;
		}
	}
}
