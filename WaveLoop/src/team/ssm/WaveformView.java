package team.ssm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;

public class WaveformView extends View {

	public ShapeDrawable sd;
	
	public WaveformView(Context context) {
		super(context);
		
		Path path = new Path();
        
        int[] testList = {10,15,23,12,15,28,46,18,19,21,19};
        
        path.moveTo( 0, 0 );
        for(int i = 0; i < testList.length; ++i )
        {
        	path.lineTo( i+1, testList[i] );
        }
        path.lineTo( testList.length, 0 );
        path.lineTo( 0, 0 );

        sd = new ShapeDrawable( new PathShape(path, 100, 100) );
        sd.getPaint().setColor(0xff74AC23);
        
	}
	
	
	protected void onDraw(Canvas canvas) {
		
		sd.draw(canvas);
		
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		canvas.setMatrix(matrix);
		
		sd.draw(canvas);
        
		super.onDraw(canvas);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(100, 100);
	}

	
}

