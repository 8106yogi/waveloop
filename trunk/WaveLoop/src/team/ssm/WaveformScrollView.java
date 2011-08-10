package team.ssm;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class WaveformScrollView extends HorizontalScrollView {
	SeekBar mSeekBar;
	boolean mIsSeekbarTouched;
	MediaPlayer mPlayer;
	
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
            	WaveformScrollView.this.smoothScrollTo(progress, 0);
            	 
            	double position = (double)progress/(double)mSeekBar.getMax()*(double)mPlayer.getDuration();
 				mPlayer.seekTo( (int)position );
        	}
        }

         public void onStartTrackingTouch(SeekBar seekBar) {
        	 mIsSeekbarTouched = true;
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
    	public boolean onFling(MotionEvent e1, MotionEvent e2,
    			float velocityX, float velocityY) {
    		/*if(Math.abs(velocityX) > 1000 && Math.abs(velocityY) < 500){//민감도 조정
    			Log.d(TAG, "onFling, consumed");
    			if(velocityX < 0){//왼쪽에서 오른쪽으로 플링
    				if(mButtonNext.isEnabled()){
    					mButtonNext.performClick();
    					return true;//touch 이벤트 흡수
    				}
    			}else{
    				if(mButtonPrev.isEnabled()){
    					mButtonPrev.performClick();
    					return true;//touch 이벤트 흡수
    				}
    			}
    		}*/
    		return false;
    	}
    	
    	
    });
	
	public WaveformScrollView(Context context) {
		super(context);
	}
	public WaveformScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public WaveformScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// 시크바를 직접 컨트롤중이 아니라면, 스크롤바 변화에 따라 시크바를 따라가게 한다.
		if( mSeekBar != null && mIsSeekbarTouched == false )
		{
			LinearLayout waveformLinearLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
			mSeekBar.setMax( waveformLinearLayout.getMeasuredWidth() );
			mSeekBar.setProgress( this.getScrollX() );
			
			if( mPlayer != null && mPlayer.isPlaying() == false )
			{
				double position = (double)this.getScrollX()/(double)waveformLinearLayout.getMeasuredWidth()*(double)mPlayer.getDuration();
				mPlayer.seekTo( (int)position );
			}
				
		}
		
		

		super.onScrollChanged(l, t, oldl, oldt);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		return super.onTouchEvent(ev);
	}
	
	/*
	@Override
	protected void onAnimationEnd() {
		
		super.onAnimationEnd();
	}
	@Override
	protected void onAnimationStart() {
		
		super.onAnimationStart();
	}
	*/
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mGestureDetector.onTouchEvent(ev)){
    		//onFling에서 return true가 들어오면 touch 이벤트 흡수
    		return true;
    	}
    	return super.dispatchTouchEvent(ev);
	}
	
	
	
	

}
