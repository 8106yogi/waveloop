package team.ssm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;

public class WaveformView extends View {

	public ShapeDrawable sd;
	private int[] mData;
	private int mBeginFrame;
	private int mEndFrame;
	private int mHeight;
	private Path path;
	private Paint mPaint;
	
	public WaveformView(Context context) {
		super(context);
		
		mPaint = new Paint();
		mPaint.setColor(0xff74AC23);
		
	}
	
	
	public void setData( int[] data, int beginFrame, int endFrame, int height )
	{
		mData = data;
		mBeginFrame = beginFrame;
		mEndFrame = endFrame;
		mHeight = height;
		

		if(data != null)
		{
			path = new Path();
	        path.moveTo( 0, 0 );
	        for(int i = 0; i + mBeginFrame < mEndFrame; ++i )
	        {
	        	path.lineTo( i*2, data[i+mBeginFrame] );
	        }
	        path.lineTo( mEndFrame*2, 0 );
	        path.lineTo( 0, 0 );
	        path.close();
		}
		
		/*else
		{
			path = new Path();
			path.moveTo( 0, 0 );
	        path.lineTo( mEndFrame*2, 0 );
	        path.moveTo( 0, 0 );
	        path.close();
		}*/
		

        //sd = new ShapeDrawable( new PathShape(path, mEndFrame-mBeginFrame, 300) );
        //sd.getPaint().setColor(0xff74AC23);

		
		
	}
	
	
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawLine(0, 0, 0, 300, mPaint);
		canvas.drawLine(0, mHeight/2, (mEndFrame-mBeginFrame)*2, mHeight/2, mPaint);
		
		if(null != path)
		{
			Matrix matrix = canvas.getMatrix();
			
			matrix.postTranslate(0, mHeight/2);

			matrix.preScale(1, 1);
			canvas.setMatrix(matrix);
			canvas.drawPath(path, mPaint);
			
			matrix.preScale(1, -1);
			canvas.setMatrix(matrix);
			canvas.drawPath(path, mPaint);

		}
		
		//canvas.
				
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension((mEndFrame-mBeginFrame)*2, mHeight);
	}

	
}

