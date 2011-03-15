package com.lehms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageDrawableView extends View {
    
    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
    
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private Paint   mPaint;
    private Boolean _isDrawing = false;

    public ImageDrawableView(Context c, AttributeSet set, int defStyle) {
        super(c, set, defStyle);
        init();
    }

    public ImageDrawableView(Context c, AttributeSet set) {
        super(c, set);
        init();
    }

    public ImageDrawableView(Context c) {
        super(c);
        init();
    }
    
    public Boolean getIsDrawing()
    {
    	return _isDrawing;
    }
    
    private void init()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(4);

        //mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.observation_male_female);
        //mCanvas = new Canvas(mBitmap);
        
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        
        //this.setBackgroundResource(R.drawable.observation_male_female);
    }
    
    public void open(Context context, int imageResourceId)
    {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), imageResourceId); //R.drawable.observation_male_female);
        mCanvas = new Canvas(mBitmap);
    }

    public void open(String filename) throws FileNotFoundException
    {
        FileInputStream in = new FileInputStream(filename); //context.openFileInput(filename);
        mBitmap = BitmapFactory.decodeStream(in);
        mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
    }

    public void save(String filename) throws IOException
    {
	    // make sure the directory we plan to store the recording in exists
	    File directory = new File(filename).getParentFile();
	    if (!directory.exists())
	    	directory.mkdirs();
    	
    	File file = new File(filename);
    	if(file.exists())
    		file.delete();
   		file.createNewFile();
    	
        FileOutputStream out = new FileOutputStream(filename); // context.openFileOutput(filename, Context.MODE_PRIVATE);
        mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);
        
        if( mBitmap != null )
        {
        	canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        	canvas.drawPath(mPath, mPaint);
        }
    }
    
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    
    private void touch_start(float x, float y) {
    	
    	_isDrawing = true;
    	
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        //float dx = Math.abs(x - mX);
        //float dy = Math.abs(y - mY);
        //if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
    	mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
        mX = x;
        mY = y;
        //}
    }
    private void touch_up() {
    	
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        
    	_isDrawing = false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}