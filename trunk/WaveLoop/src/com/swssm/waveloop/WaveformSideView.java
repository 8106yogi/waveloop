package com.swssm.waveloop;

import android.content.Context;
import android.view.View;

public class WaveformSideView extends View {
	
	public interface SizeCallback {
        int getWidth();
        int getHeight();
    }
	
	private SizeCallback mSizeCallback;
	
	public WaveformSideView(Context context) {
		super(context);
		
	}

	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if(mSizeCallback != null)
			setMeasuredDimension(mSizeCallback.getWidth()/2, mSizeCallback.getHeight());
	}


	public void setSizeCallback(SizeCallback mSizeCallback) {
		this.mSizeCallback = mSizeCallback;
	}


	
}
