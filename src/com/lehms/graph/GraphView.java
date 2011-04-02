package com.lehms.graph;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;


public class GraphView extends View {

	public static boolean BAR = true;
	public static boolean LINE = false;

	private Paint paint;
	private List<Double> values;
	private List<String> horlabels;
	private List<String> verlabels;
	private String title;
	private boolean type;

	public GraphView(Context context, List<Double> values, String title, 
			List<String> horlabels, List<String> verlabels, boolean type) {
		super(context);
		if (values == null)
		{
			values = new ArrayList<Double>();
		}
		else
			this.values = values;
		if (title == null)
			title = "";
		else
			this.title = title;
		if (horlabels == null)
		{
			this.horlabels = new ArrayList<String>();
		}
		else
			this.horlabels = horlabels;
		if (verlabels == null)
			this.verlabels = new ArrayList<String>();
		else
			this.verlabels = verlabels;
		this.type = type;
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;
		float max = getMax();
		float min = getMin();
		float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);

		paint.setTextAlign(Align.LEFT);
		int vers = verlabels.size() - 1;
		for (int i = 0; i < verlabels.size(); i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(horstart, y, width, y, paint);
			paint.setColor(Color.WHITE);
			canvas.drawText(verlabels.get(i), 0, y, paint);
		}
		int hors = horlabels.size() - 1;
		for (int i = 0; i < horlabels.size(); i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + horstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Align.CENTER);
			if (i==horlabels.size()-1)
				paint.setTextAlign(Align.RIGHT);
			if (i==0)
				paint.setTextAlign(Align.LEFT);
			paint.setColor(Color.WHITE);
			canvas.drawText(horlabels.get(i), x, height - 4, paint);
		}

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (max != min) {
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = values.size();
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < values.size(); i++) {
					float val = (float)(values.get(i) - min);
					float rat = val / diff;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = values.size();
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < values.size(); i++) {
					float val = (float)(values.get(i) - min);
					float rat = val / diff;
					float h = graphheight * rat;
					if (i > 0)
						canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}
	}

	private float getMax() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < values.size(); i++)
			if (values.get(i) > largest)
				largest = values.get(i).floatValue();
		return largest;
	}

	private float getMin() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.size(); i++)
			if (values.get(i) < smallest)
				smallest = (float)(values.get(i) + 0);
		return smallest;
	}

}
