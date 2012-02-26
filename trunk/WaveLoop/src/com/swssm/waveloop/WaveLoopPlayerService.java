package com.swssm.waveloop;

import com.swssm.waveloop.audio.OSLESMediaPlayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;



public class WaveLoopPlayerService extends Service {
	
	
	private OSLESMediaPlayer mPlayer;
	private AudioInfo mAudioInfo;
	private boolean mIsPlaying = false;

	private PlayerServiceListener mPlayerListener;
	
	private boolean mIsRepeat;
	private int mLoopCount;
	private int mRepeatStartPos;// ms
	private int mRepeatFinishPos;// ms
	
	
	public interface PlayerServiceListener {
		public void onRepeatCount(int count);
	}
	
	
	
	
	
	public AudioInfo getAudioInfo() {
		return mAudioInfo;
	}
	
	
	static {
		System.loadLibrary("audio-tools");
	}
	
	private static WaveLoopPlayerService mInstance = null;
	public static WaveLoopPlayerService getInstance() {
		return mInstance;
	}
	
	
	
	/*
	public class LocalBinder extends Binder {
		WaveLoopPlayerService getService() {
			return WaveLoopPlayerService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();
	*/
	@Override
	public IBinder onBind(Intent arg0) {
		return null;//mBinder
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;// service가 강제 종료 되면 자동으로 다시 시작
    }
	
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		
	}
	
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		mPlayerListener = null;
		mPlayer = new OSLESMediaPlayer();
		mPlayer.createEngine();
		
		registerCallListener();
		
	}
	public void onDestroy()
	{
		super.onDestroy();
		
		unregisterCallListener();
		
		mPlayer.releaseAudioPlayer();
		mPlayer.releaseEngine();
		mPlayer = null;
		mPlayerListener = null;
		mInstance = null;
		
		
	}
	
	public void createPlayer(AudioInfo audioInfo) {
		mAudioInfo = audioInfo;
		mPlayer.createAudioPlayer(mAudioInfo.path);
		
		mIsRepeat = false;
		mRepeatStartPos = 0;
		mRepeatFinishPos = 0;
		mPlayerListener = null;
	}
	
	public void releasePlayer() {
		mPlayer.releaseAudioPlayer();
		mAudioInfo = null;
		
		mPlayerListener = null;
	}
	
	public void play() {
		if(mPlayer != null)
			mPlayer.play();
	}
	
	public void pause() {
		if(mPlayer != null)
			mPlayer.pause();
	}
	
	public void stop() {
		if(mPlayer != null)
			mPlayer.stop();
	}

	public int getDuration() {
		if(mPlayer != null)
			return mPlayer.getDuration();
		
		return 0;
	}
	
	public void seekTo(int pos) {
		if(mPlayer != null)
			mPlayer.seekTo(pos);
	}
	
	public int getPosition() {
		if(mPlayer != null)
			return mPlayer.getPosition();
		
		return 0;
	}
	
	public void setRate(int rate) {
		if(mPlayer != null)
			mPlayer.setRate(rate);
	}
	
	
	public int getRate() {
		if(mPlayer != null)
			return mPlayer.getRate();
		
		return 1000;
	}
	
	public boolean isPlaying() {
		if(mPlayer != null)
			return mPlayer.isPlaying();
		
		return false;
	}
	
	public boolean isRepeat() {
		return mIsRepeat;
	}
	
	public void setPlayerListener(PlayerServiceListener listener) {
		mPlayerListener = listener;
	}
	
	
	
	
	public void setRepeat( boolean isRepeat, int startPos, int endPos )
	{
		mIsRepeat = isRepeat;
		//mLoopCount = Integer.MAX_VALUE;
		
		mRepeatStartPos = startPos;
		mRepeatFinishPos = endPos;
		
		mLoopCount = (GlobalOptions.repeatCount == 0)?
				Integer.MAX_VALUE:GlobalOptions.repeatCount;
		
		
		if(mIsRepeat)
			mRepeatHandler.sendEmptyMessage(0);
		else
		{
			mRepeatDelayHandler.removeMessages(0);
			mRepeatHandler.removeMessages(0);
		}
		
	}
	
	
	
	// 0.016초에 한번꼴로 재생 위치 갱신
    Handler mRepeatHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		//Log.i("curRate", "test" );

    		
			//updateCurrentSegmentColor();
			if( mIsRepeat == true &&
				PlayerProxy.isPlaying() == true )
			{
				int currentPosition = PlayerProxy.getPosition();
				if(currentPosition < mRepeatStartPos || currentPosition > mRepeatFinishPos )
				{
					Log.i("player", "Loop");
					PlayerProxy.seekTo(mRepeatStartPos+1);
					
					if(mLoopCount != Integer.MAX_VALUE)
						mLoopCount--;

					if(mLoopCount < 0)
						setRepeat(false, 0, 0);
					
					if(mPlayerListener != null)
						mPlayerListener.onRepeatCount(mLoopCount);
					
					PlayerProxy.pause();
					mRepeatDelayHandler.sendEmptyMessageDelayed(0, GlobalOptions.repeatDelayTime);
					
					
				}
			}
              
    		
			mRepeatHandler.sendEmptyMessageDelayed(0,16);
		}
		
    };
    
    Handler mRepeatDelayHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		PlayerProxy.play();
    	}
    };
    
    
    
    
	
	
	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		
		private int prevState = TelephonyManager.CALL_STATE_IDLE;
	    @Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	    	if(state == prevState)
	    		return;
	    	
	    	switch(state)
	    	{
	    	case TelephonyManager.CALL_STATE_RINGING:
	    		//Incoming call: Pause music
	        	//Log.i("CallTest", "CALL_STATE_RINGING : Incoming Call");
	        	mIsPlaying = isPlaying(); 
	        	if( mIsPlaying )
	        		pause();
	    		break;
	    	case TelephonyManager.CALL_STATE_IDLE:
	    		//Not in call: Play music
	        	//Log.i("CallTest", "CALL_STATE_IDLE");
	        	if( mIsPlaying ) {
	        		play();
	        		mIsPlaying = false;
	        	}
	    		break;
	    	case TelephonyManager.CALL_STATE_OFFHOOK:
	    		//A call is dialing, active or on hold
	        	//Log.i("CallTest", "CALL_STATE_OFFHOOK");
	        	if(prevState == TelephonyManager.CALL_STATE_IDLE)// 울리다 받은게 아니라면. : 내가 거는 거라면.
	        	{
	        		mIsPlaying = isPlaying(); 
		        	if( mIsPlaying )
		        		pause();
	        	}
	    		break;
	    	}
	        
	        prevState = state;
	        super.onCallStateChanged(state, incomingNumber);
	    }
	};
	
	private void registerCallListener()
	{
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	
	private void unregisterCallListener()
	{
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}
	
	/*
	public class OutgoingCallReceiver extends BroadcastReceiver {
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
	            Log.i("CallTest", "Outgoing Call");
	           
	        	
	        }
	    }
	}*/
	
	
}
