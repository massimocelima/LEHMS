package com.lehms.graph;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.layout.AnchorPosition;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.lehms.UIHelper;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;
import com.lehms.util.MeasurmentReportProvider;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GraphActivity extends RoboActivity implements OnTouchListener {
	
	public final static String EXTRA_MEASURMENT_SUMMERIES = "measurement_summaries";
	
	@InjectExtra(EXTRA_MEASURMENT_SUMMERIES) private Object [] _measurementList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try
		{
			String title = MeasurmentReportProvider.getMeasurementTitle(((MeasurementSummary)_measurementList[0]).Type );
			
			ArrayList<Double> values = new ArrayList<Double>();
			//ArrayList<Double> yValues = new ArrayList<Double>();

			for(int i = _measurementList.length - 1; i >= 0; i--)
			{
				MeasurementSummary measurment = (MeasurementSummary)_measurementList[i];
				values.add(measurment.PrimaryData);
				//yValues.add((double)measurment.CreatedDate.getTime());
			}
	
			setTitle(((MeasurementSummary)_measurementList[0]).Type.toString());
			
			_plot = new XYPlot(this, title);

	        // Turn the above arrays into XYSeries':
			_series = new SimpleXYSeries(
	                values,          // SimpleXYSeries takes a List so turn our array into a List
	                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
	                title);                             // Set the display title of the series
			
	        // Create a formatter to use for drawing a series using LineAndPointRenderer:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter(
	                Color.rgb(0, 200, 0),                   // line color
	                Color.rgb(0, 100, 0));                  // point color
	        
	        _plot.addSeries(_series, series1Format);

	        //XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
	        //mySimpleXYPlot.addSeries(series2, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100)));
	 
	        // reduce the number of range labels
	        _plot.setTicksPerRangeLabel(2);
	        
	        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
	        // To get rid of them call disableAllMarkup():
	        _plot.disableAllMarkup();
	        
	        // reposition the domain label to look a little cleaner:
	        Widget domainLabelWidget = _plot.getDomainLabelWidget();
	        _plot.position(domainLabelWidget,                     // the widget to position
                    45,                                    // x position value, in this case 45 pixels
                    XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                    0,                                     // y position value
                    YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                    AnchorPosition.LEFT_BOTTOM);    

	        _plot.setDomainLabel("");

	        setContentView(_plot);

			//Set of internal variables for keeping track of the boundaries
	        _plot.calculateMinMaxVals();
			minXY=new PointF(_plot.getCalculatedMinX().floatValue(),_plot.getCalculatedMinY().floatValue());
			maxXY=new PointF(_plot.getCalculatedMaxX().floatValue(),_plot.getCalculatedMaxY().floatValue());
			//_plot.setOnTouchListener(this);
			
			/*
			GraphView graphView = new GraphView(this, 
					values, 
					((MeasurementSummary)_measurementList[0]).Type.toString(), 
					horizontalLabels, 
					vertiacalLabels, 
					GraphView.LINE);
			setContentView(graphView);
			*/
		}
		catch(Exception ex)
		{
		}
	}
	

	private XYPlot _plot;
	private SimpleXYSeries _series;
	private PointF minXY;
	private PointF maxXY;
	
	// Definition of the touch states
	static final int NONE = 0;
	static final int ONE_FINGER_DRAG = 1;
	static final int TWO_FINGERS_DRAG = 2;
	int mode = NONE;
 
	PointF firstFinger;
	float lastScrolling;
	float distBetweenFingers;
	float lastZooming;
	 
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // Start gesture
			firstFinger = new PointF(event.getX(), event.getY());
			mode = ONE_FINGER_DRAG;
			break;
		case MotionEvent.ACTION_UP: 
		case MotionEvent.ACTION_POINTER_UP:
			//When the gesture ends, a thread is created to give inertia to the scrolling and zoom 
			Timer t = new Timer();
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						while(Math.abs(lastScrolling)>1f || Math.abs(lastZooming-1)<1.01){ 
						lastScrolling*=.8;
						scroll(lastScrolling);
						lastZooming+=(1-lastZooming)*.2;
						zoom(lastZooming);
						_plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
						try {
							_plot.postRedraw();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// the thread lives until the scrolling and zooming are imperceptible
					}
					}
				}, 0);
 
		case MotionEvent.ACTION_POINTER_DOWN: // second finger
			distBetweenFingers = spacing(event);
			// the distance check is done to avoid false alarms
			if (distBetweenFingers > 5f) {
				mode = TWO_FINGERS_DRAG;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ONE_FINGER_DRAG) {
				PointF oldFirstFinger=firstFinger;
				firstFinger=new PointF(event.getX(), event.getY());
				lastScrolling=oldFirstFinger.x-firstFinger.x;
				scroll(lastScrolling);
				lastZooming=(firstFinger.y-oldFirstFinger.y)/_plot.getHeight();
				if (lastZooming<0)
					lastZooming=1/(1-lastZooming);
				else
					lastZooming+=1;
				zoom(lastZooming);
				_plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
				_plot.redraw();
 
			} else if (mode == TWO_FINGERS_DRAG) {
				float oldDist =distBetweenFingers; 
				distBetweenFingers=spacing(event);
				lastZooming=oldDist/distBetweenFingers;
				zoom(lastZooming);
				_plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
				_plot.redraw();
			}
			break;
		}
		return true;
	}
 
	private void zoom(float scale) {
		float domainSpan = maxXY.x	- minXY.x;
		float domainMidPoint = maxXY.x		- domainSpan / 2.0f;
		float offset = domainSpan * scale / 2.0f;
		minXY.x=domainMidPoint- offset;
		maxXY.x=domainMidPoint+offset;
	}
 
	private void scroll(float pan) {
		float domainSpan = maxXY.x	- minXY.x;
		float step = domainSpan / _plot.getWidth();
		float offset = pan * step;
		minXY.x+= offset;
		maxXY.x+= offset;
	}
 
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
}