package com.swssm.waveloop;

import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class HFling extends HorizontalScrollView {
	//private final String TAG = "HFling";
	private LinearLayout mainLayout = null;
	private int cntView = 0; // count of views
	private int currentIndex = 0;
	private GestureDetector gd = null;
	private float modify = 0; // modifier from FLING
	private Observer observer = null;

	public HFling(Context context) {
		super(context);
		init(context);
	}
	
	public HFling(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public HFling(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mainLayout = new LinearLayout(getContext());
		this.addView(mainLayout);
		cntView = 0;
		gd = new GestureDetector(getContext(), new HFlingGestureListener(this));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		if(MotionEvent.ACTION_UP == ev.getAction()) {
			magneticX(getScrollX() + modify);
			
			return true; // prevent to move!!
		} else if(MotionEvent.ACTION_DOWN == ev.getAction()) {
			modify = 0; // reset modifier everytime you click!
			return super.onTouchEvent(ev); // let it go
		}
		return super.onTouchEvent(ev);
	}

	public void addChildView(View v) {
		ViewGroup.LayoutParams lpParent = this.getLayoutParams();
		ViewGroup.LayoutParams lpChild = new ViewGroup.LayoutParams(lpParent);
		v.setLayoutParams(lpChild);
		mainLayout.addView(v);
		cntView++;
	}
	
	/**
	 * Get start X along with view index
	 * @param index - starts from 0
	 * @return int as X value of view index
	 */
	private int getXWithIndex(int index) {
		// assertion
		if(cntView <= index || 0 == index) {
			return 0;
		}
		
		// 0 < index < cntView
		// get the width per view
		ViewGroup.LayoutParams lpParent = this.getLayoutParams();
		int width = lpParent.width;

		return width * index;
	}
	
	// reverse
	private int getIndexWithX(int x) {
		ViewGroup.LayoutParams lpParent = this.getLayoutParams();
		int width = lpParent.width;
		int quotient = (int)x / width;
		
		return quotient;
	}

	// internal method
	private void _smoothScrollTo(int x) {
		currentIndex = getIndexWithX(x);
		this.smoothScrollTo(x, getScrollY());
	}
	
	/**
	 * Scroll to certain indexed view
	 * @param index : index of view
	 * @return true if success, otherwise false
	 */
	public boolean ScrollToIndex(int index) {
		if(cntView <= index)
			return false;
		
		if(currentIndex == index) {
			return false;
		}
		
		int xToScroll = getXWithIndex(index);
		_smoothScrollTo(xToScroll);
		//Log.i("HFling", "Scroll to index " + index);
		currentIndex = index;
		return true;
	}

	/**
	 * Clear all child views
	 */
	public void clearChild() {
		mainLayout.removeAllViews();
		cntView = 0;
	}
	
	private void magneticX(float x) {
		int newX = getMagneticX(x);
		
		_smoothScrollTo(newX);
	}
	
	private int getMagneticX(float x) {
		// setting up variable
		ViewGroup.LayoutParams lpParent = this.getLayoutParams();
		int full = lpParent.width;
		int half = full / 2;

		int remainder = (int)x % full; // 0 to width
		int quotient = (int)x / full;
		if(remainder > half)
			return (quotient + 1) * full;
		else 
			return quotient * full;
	}
	
	public void onFlingLeft() {
		int half = this.getLayoutParams().width / 2;
		modify = modify + half;
	}
	
	public void onFlingRight() {
		int half = this.getLayoutParams().width / 2;
		modify = modify - half;
	}
	
	public void onClick() {
		try{
			notifyObserver(mainLayout.getChildAt(currentIndex));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// pretend to be Observable
	public void setObserver(Observer o) { 
		this.observer = o;
	}

	private void notifyObserver(View v) {
		if(null != observer)
			observer.update(null, v);
	}
}
