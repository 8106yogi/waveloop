package team.ssm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class WaveformRulerView extends View {
	
	private Paint mPaint;
	private int mWidth;
	
	public WaveformRulerView(Context context, int width) {
		super(context);
		
		mPaint = new Paint();
		mPaint.setColor(0xff74AC23);
		
		mWidth = width;
		
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		canvas.drawLine(0, 0, mWidth*2, 0, mPaint);
		for(int i = 0; i < mWidth*2/20; ++i ){
			canvas.drawLine(20*i, 0, 20*i, (i%5==0)?20:12, mPaint);
		}

	
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(mWidth*2, 20);
	}


	
	
}
