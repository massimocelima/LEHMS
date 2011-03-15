package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.PhotoDataContract;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhotoAdapter extends ArrayAdapter<PhotoDataContract> {

	private int _resource;
	
	public PhotoAdapter(Context context, int resource, List<PhotoDataContract> items) {
		super(context, resource, items);
		_resource = resource;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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

        PhotoDataContract photo = getItem(position);
		
		TextView createdTextView = (TextView)view.findViewById(R.id.photo_item_created_date);
		TextView nameTextView = (TextView)view.findViewById(R.id.photo_item_name);
		TextView typeTextView = (TextView)view.findViewById(R.id.photo_item_type);
	
		createdTextView.setText( UIHelper.FormatLongDateTime(photo.CreatedDate ));
		nameTextView.setText( photo.Name);
		typeTextView.setText( photo.Type.GetDescription() );
		
		return view;
		
	}
}
