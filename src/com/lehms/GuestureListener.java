package com.lehms;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GuestureListener implements View.OnTouchListener {

	private GestureDetector _gestureDetector;
	
	public GuestureListener(GestureDetector gestureDetector)
	{
		_gestureDetector = gestureDetector;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (_gestureDetector.onTouchEvent(event)) 
			return true; 
		return false;             
	}

}
