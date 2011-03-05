package com.lehms.adapters;

import java.util.List;

import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;

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

public class ProgressNoteAdapter extends ArrayAdapter<ProgressNoteDataContract> {

	private int _resource;
	
	public ProgressNoteAdapter(Context context, int resource, List<ProgressNoteDataContract> items) {
		super(context, resource, items);
		_resource = resource;
	}
	
	@Override
	public long getItemId(int position) {
		//return getItem(position).Id.getMostSignificantBits();
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

        ProgressNoteDataContract note = getItem(position);

		TextView createdByTextView = (TextView)view.findViewById(R.id.progress_note_item_created_by);
		TextView createdDateTextView = (TextView)view.findViewById(R.id.progress_note_item_created_date);
		TextView subjectTextView = (TextView)view.findViewById(R.id.progress_note_item_subject);
		TextView noteTextView = (TextView)view.findViewById(R.id.progress_note_item_note);
		ImageView soundImageView = (ImageView)view.findViewById(R.id.progress_note_item_ic_sound);
				
		createdByTextView.setText(note.CreatedBy);
		createdDateTextView.setText(UIHelper.FormatShortDate(note.CreatedDate));
		subjectTextView.setText(note.Subject);
		noteTextView.setText(note.Note);
		
		if(note.VoiceMemoFileName != null && ! note.VoiceMemoFileName.equals(""))
			soundImageView.setVisibility(View.VISIBLE);
		else
			soundImageView.setVisibility(View.GONE);
		
		return view;
	}
}
