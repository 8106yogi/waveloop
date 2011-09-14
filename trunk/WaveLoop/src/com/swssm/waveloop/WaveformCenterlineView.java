package com.swssm.waveloop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class WaveformCenterlineView extends View {

	private Paint mPaint;
	
	
	public WaveformCenterlineView(Context context) {
		super(context);
		
		init();
	}
	
	
	public WaveformCenterlineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
		
	}
	public WaveformCenterlineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}
	
	private void init()
	{
		mPaint = new Paint();
		mPaint.setColor(0xffff2222);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		canvas.drawLine(getMeasuredWidth()/2, 0, 
						getMeasuredWidth()/2, getMeasuredHeight(), 
						mPaint);
		
	
	}

	

}
