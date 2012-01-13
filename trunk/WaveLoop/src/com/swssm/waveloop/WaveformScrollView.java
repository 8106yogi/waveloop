package com.swssm.waveloop;

import com.swssm.waveloop.R;
import com.swssm.waveloop.audio.OSLESMediaPlayer;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class WaveformScrollView extends HorizontalScrollView {
	SeekBar mSeekBar;
	boolean mIsSeekbarTouched;
	OSLESMediaPlayer mOSLESPlayer;
	//MediaPlayer mPlayer;
	Scroller mScroller;// = new Scroller(this);
	//int mMaxScrollX;
	//int mMaxScrollY;
	LinearLayout mWaveformLinearLayout;
	
	SentenceSegmentList mSentenceSegmentList;
	
	long mLastScroll;
	
	
	public void setSentenceSegmentList(SentenceSegmentList sentenceSegmentList) {
		mSentenceSegmentList = sentenceSegmentList;
	}
	
	public void setInnerLayout( LinearLayout layout )
	{
		mWaveformLinearLayout = layout;
	}
	
	public void setSeekBar( SeekBar seekBar )
	{
		mSeekBar = seekBar;
		mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		//mSeekBar.setMax(max);
		
	}
	
	public void setOSLESPlayer(OSLESMediaPlayer OSLESPlayer)
	{
		mOSLESPlayer = OSLESPlayer;
		
		
		

	}
	public void setMediaPlayer( MediaPlayer player )
	{
		//mPlayer = player;
		
		
	}
	
	
	// 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        	
        	//if(mPlayer == null)
        	//	return;
        	
        	if(mOSLESPlayer == null)
        		return;
        	
        	if (fromUser) {
            	WaveformScrollView.this.scrollTo(progress, 0);
            	 
            	//double position = (double)progress/(double)mSeekBar.getMax()*(double)mPlayer.getDuration();
 				//mPlayer.seekTo( (int)position );
            	double position = (double)progress/(double)mSeekBar.getMax()*(double)mOSLESPlayer.getDuration();
            	mOSLESPlayer.seekTo((int)position);
        	}
        	
        	//int cur =  mPlayer.getCurrentPosition()/1000;
        	int cur = mOSLESPlayer.getPosition()/1000;
       	 	int cMin = cur/60;
            int cSec = cur%60;
            String strTime = String.format("%02d:%02d" , cMin, cSec);
            TextView mCur = player_main.mCurtime;
            mCur.setText(strTime);
        	
        }

         public void onStartTrackingTouch(SeekBar seekBar) {
        	 mIsSeekbarTouched = true;
        	 
        	 forceStop();
        	 
        	 //if( mPlayer != null )
        	 if(mOSLESPlayer != null)
        	 {
        		 //mPlayer.pause();
        		 mOSLESPlayer.pause();
        		 player_main.mPlayBtn.setImageResource(R.drawable.play);
        	 }
         }

         public void onStopTrackingTouch(SeekBar seekBar) {
        	 mIsSeekbarTouched = false;
         }
    };
	
	GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
    	
		
		@Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2,
	                                float distanceX, float distanceY) {
	        // beware, it can scroll to infinity
	        scrollBy((int)distanceX, (int)distanceY);
	        return true;
	    }

	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
	    	Log.d("gd", "onFling");
	    	
	        mScroller.fling(getScrollX(), getScrollY(),
	                -(int)vX, -(int)vY, 0, 
	                (int)mWaveformLinearLayout.getMeasuredWidth(), 0, (int)mWaveformLinearLayout.getMeasuredHeight());
	        invalidate(); // don't remember if it's needed
	        return true;
	    }

	    @Override
	    public boolean onDown(MotionEvent e) {
	        if(!mScroller.isFinished() ) { // is flinging
	            mScroller.forceFinished(true); // to stop flinging on touch
	        }
	        return true; // else won't work
	    }
	    
	    
    	
    	
    });
	
	public WaveformScrollView(Context context) {
		super(context);
		
		mScroller = new Scroller(context);
		
	}
	public WaveformScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mScroller = new Scroller(context);
	}
	public WaveformScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mScroller = new Scroller(context);
	}
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// 시크바를 직접 컨트롤중이 아니라면, 스크롤바 변화에 따라 시크바를 따라가게 한다.
		if( mIsSeekbarTouched == false )
		{
			if( mSeekBar != null)
				mSeekBar.setProgress( this.getScrollX() );
			
			// 플레이어가 플레이중이 아니면, 플레이어도 스크롤바의 변화에 따라가게 한다.
			if( mOSLESPlayer != null && mOSLESPlayer.isPlaying() == false )
			{
				double position = (double)this.getScrollX()/(double)(mWaveformLinearLayout.getMeasuredWidth()-getMeasuredWidth())*(double)mOSLESPlayer.getDuration();
				mOSLESPlayer.seekTo((int)position);
			}
				
		}
		
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	private int mCurrentSegmentIndex;
	private View[]	mWaveformSemgnets;
	
	private void updateCurrentSegmentColor() {
		int segIndex = mSentenceSegmentList.getCurrentSentenceIndex(getScrollX()/2);
		
		
			if(mCurrentSegmentIndex != segIndex)
			{
				getmWaveformSemgnets()[mCurrentSegmentIndex].setBackgroundColor(0x00ffffff);
				if( mSentenceSegmentList.getSegments()[segIndex].isSilence == false )
					getmWaveformSemgnets()[segIndex].setBackgroundColor(0x33ffffff);
				mCurrentSegmentIndex = segIndex;
				
				
			}
		
		
	}
	
	@Override
	protected void onAnimationEnd() {
		
		super.onAnimationEnd();
		//Log.d("animation", "onAnimationEnd()");
	}
	@Override
	protected void onAnimationStart() {
		
		super.onAnimationStart();
		//Log.d("animation", "onAnimationStart()");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    return mGestureDetector.onTouchEvent(event);
	}
	
	/*
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mGestureDetector.onTouchEvent(ev)){
    		//onFling에서 return true가 들어오면 touch 이벤트 흡수
    		return true;
    	}
    	return super.dispatchTouchEvent(ev);
	}
	*/
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()) {
	        scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		}
	}
	
	
	public void scrollSmoothTo( int x, int y )
	{
		
		long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
		
		if (duration > 200) {
			Log.i("", "getScroll : " + getScrollX() + ", " + getScrollY() );
			Log.i("", "target : " + x + ", " + y );
			if(!mScroller.isFinished() ) { // is flinging
                mScroller.abortAnimation(); // to stop flinging on touch
        	}
			
			mScroller.startScroll(
					getScrollX()+1, 
					getScrollY(), 
					x-getScrollX(), 
					y-getScrollY(),
					200 );
			
			invalidate();
        } else {
        	if(!mScroller.isFinished() ) { // is flinging
                mScroller.abortAnimation(); // to stop flinging on touch
        	}
        	   scrollTo(x,y);
        }
		

		
		mLastScroll = AnimationUtils.currentAnimationTimeMillis();
		
		
	}
	
	
	public void forceStop()
	{
		//smoothScrollTo(0, 0)
		if(!mScroller.isFinished() ) { // is flinging
            mScroller.forceFinished(true); // to stop flinging on touch
        }
		
	}

	public boolean isFling()
	{
		return !mScroller.isFinished();
	}

	public void setmWaveformSemgnets(View[] mWaveformSemgnets) {
		this.mWaveformSemgnets = mWaveformSemgnets;
	}


	public View[] getmWaveformSemgnets() {
		return mWaveformSemgnets;
	}

	
}
	
	
	


