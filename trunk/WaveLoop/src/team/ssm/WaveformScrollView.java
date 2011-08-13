package team.ssm;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.SeekBar;

public class WaveformScrollView extends HorizontalScrollView {
	SeekBar mSeekBar;
	boolean mIsSeekbarTouched;
	MediaPlayer mPlayer;
	Scroller mScroller;// = new Scroller(this);
	//int mMaxScrollX;
	//int mMaxScrollY;
	LinearLayout mWaveformLinearLayout;
	
	long mLastScroll;
	
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
	
	public void setMediaPlayer( MediaPlayer player )
	{
		mPlayer = player;
		
		
	}
	
	
	// 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        	if (fromUser) {
            	WaveformScrollView.this.scrollTo(progress, 0);
            	 
            	double position = (double)progress/(double)mSeekBar.getMax()*(double)mPlayer.getDuration();
 				mPlayer.seekTo( (int)position );
        	}
        }

         public void onStartTrackingTouch(SeekBar seekBar) {
        	 mIsSeekbarTouched = true;
        	 
        	 forceStop();
        	 
        	 if( mPlayer != null )
        	 {
        		 mPlayer.pause();
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
		//mWaveformLinearLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
		
	}
	public WaveformScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mScroller = new Scroller(context);
		//mWaveformLinearLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
	}
	public WaveformScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mScroller = new Scroller(context);
		//mWaveformLinearLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
	}
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// 시크바를 직접 컨트롤중이 아니라면, 스크롤바 변화에 따라 시크바를 따라가게 한다.
		if( mSeekBar != null && mIsSeekbarTouched == false )
		{
			//LinearLayout waveformLinearLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
			mSeekBar.setMax( mWaveformLinearLayout.getMeasuredWidth() );
			mSeekBar.setProgress( this.getScrollX() );
			
			if( mPlayer != null && mPlayer.isPlaying() == false )
			{
				double position = (double)this.getScrollX()/(double)mWaveformLinearLayout.getMeasuredWidth()*(double)mPlayer.getDuration();
				mPlayer.seekTo( (int)position );
			}
				
		}
		
		

		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	
	@Override
	protected void onAnimationEnd() {
		
		super.onAnimationEnd();
	}
	@Override
	protected void onAnimationStart() {
		
		super.onAnimationStart();
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
		
		if (duration > 250) {
			//Log.i("", "getScroll : " + getScrollX() + ", " + getScrollY() );
			//Log.i("", "target : " + x + ", " + y );
			if(!mScroller.isFinished() ) { // is flinging
                mScroller.abortAnimation(); // to stop flinging on touch
        	}
			mScroller.startScroll(
					getScrollX(), 
					getScrollY(), 
					x-getScrollX(), 
					y-getScrollY() );
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
}
	
	
	


