package com.lehms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

public class GestureDetectorListView extends ListView
{
	private GestureDetector _gestureDetector = null;
	
	public GestureDetectorListView(Context context) {
		super(context);
	}

	public GestureDetectorListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GestureDetectorListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if( _gestureDetector != null && _gestureDetector.onTouchEvent(ev) )
			return true;
		return super.onInterceptTouchEvent(ev);
	}
	
	public void setGestureDetector(GestureDetector gestureDetector)
	{
		_gestureDetector = gestureDetector;
	}
}
