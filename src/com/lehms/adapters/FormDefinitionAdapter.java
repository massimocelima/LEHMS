package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.formDefinition.FormDefinition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FormDefinitionAdapter extends ArrayAdapter<FormDefinition> {

	private int _resource;
	
	public FormDefinitionAdapter(Context context, int resource, List<FormDefinition> items) {
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

        FormDefinition form = getItem(position);
		
		TextView nameTextView = (TextView)view.findViewById(R.id.form_definition_item_name);
		TextView descriptionTextView = (TextView)view.findViewById(R.id.form_definition_item_description);
		
		nameTextView.setText(form.Name);
		descriptionTextView.setText(form.Description);
		
		return view;
		
	}
}
