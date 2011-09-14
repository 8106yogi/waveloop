package com.swssm.waveloop;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class HFlingGestureListener extends SimpleOnGestureListener {
	private HFling hf = null;
	
	public HFlingGestureListener(HFling hf) {
		this.hf = hf;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		hf.onClick();
		return super.onSingleTapConfirmed(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(true == isLeft(e1,e2))
			hf.onFlingLeft();
		else
			hf.onFlingRight();
//		Log.i("GSL", "velocityX : " + velocityX);
		return super.onFling(e1, e2, velocityX, velocityY);
	}

	// or you can check it by velocityX
	private boolean isLeft(MotionEvent e1, MotionEvent e2) {
		float result = e1.getX() - e2.getX(); 
		if(0 < result)
			return true;
		return false;
	}

}
