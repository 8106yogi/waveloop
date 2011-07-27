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
		
		/*
		int numFrames = mData.length;
        int[] frameGains = mData;
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double)(
                (frameGains[0] / 2.0) +
                (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double)(
                    (frameGains[i - 1] / 3.0) +
                    (frameGains[i    ] / 3.0) +
                    (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double)(
                (frameGains[numFrames - 2] / 2.0) +
                (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }        

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int)(smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

     // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int)minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int)maxGain];
            maxGain--;
        }

        // Compute the heights
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
            if(heights[i] > 0.95f)
            	heights[i] = 0.0f;
            	
        }
        
        

        */
		path = new Path();
        path.moveTo( 0, 0 );
        for(int i = 0; i + mBeginFrame < mEndFrame; ++i )
        {
        	//path.lineTo( i*2, (float)(heights[i+mBeginFrame]*150.f) );
        	path.lineTo( i*2, data[i+mBeginFrame] );
        	
        }
        path.lineTo( mEndFrame, 0 );
        path.lineTo( 0, 0 );
        path.close();

        //sd = new ShapeDrawable( new PathShape(path, mEndFrame-mBeginFrame, 300) );
        //sd.getPaint().setColor(0xff74AC23);

		
		
	}
	
	
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

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
				
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(mEndFrame-mBeginFrame, mHeight);
	}

	
}

