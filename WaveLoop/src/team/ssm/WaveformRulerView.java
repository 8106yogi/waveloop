package team.ssm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;

public class WaveformRulerView extends View {
	
	
	private int mWidth;
	private static Path path;
	private static Paint mPaint;
	
	public WaveformRulerView(Context context, int width) {
		super(context);
		
		mPaint = new Paint();
		mPaint.setColor(0xffffffff);
		mPaint.setStyle(Style.STROKE);
		
		mWidth = width;
		
		if( path == null )
		{
			path = new Path();
			path.moveTo(0, 0);
			path.lineTo(mWidth*2, 0);
			for(int i = 0; i < 10; ++i ){
				path.moveTo(10*i, 0);
				path.lineTo(10*i, (i%10==0)?15:10);
			}
		}
		
		
		
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		//canvas.drawLine(0, 0, mWidth*2, 0, mPaint);
		//canvas.drawLine(0, 0, 0, 15, mPaint);
		/*
		for(int i = 0; i < 20; ++i ){
			canvas.drawLine(5*i, 0, 5*i, (i%10==0)?15:10, mPaint);
		}
		*/
		
		canvas.drawPath(path, mPaint);

	
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(mWidth*2, 15);
	}


	
	
}
