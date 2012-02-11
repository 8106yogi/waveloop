package com.swssm.waveloop;

import com.swssm.waveloop.audio.OSLESMediaPlayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class WaveLoopPlayerService extends Service {
	
	
	
	public class AudioInfo {
		public int mediaId;
		public String title;
		public String artist;
		public String album;
		public String path;
		
		public AudioInfo(int mediaId, String title, String artist, String album, String path)
		{
			this.mediaId = mediaId;
			this.title = title;
			this.artist = artist;
			this.album = album;
			this.path = path;
		}
	}
	
	
	static {
		System.loadLibrary("audio-tools");
	}
	
	private static WaveLoopPlayerService mInstance = null;
	public static WaveLoopPlayerService getInstance() {
		return mInstance;
	}
	
	private OSLESMediaPlayer mPlayer;
	
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
		mPlayer = new OSLESMediaPlayer();
		mPlayer.createEngine();
		
		//registerCallListener();
		
	}
	public void onDestroy()
	{
		super.onDestroy();
		
		//unregisterCallListener();
		
		mPlayer.releaseAudioPlayer();
		mPlayer.releaseEngine();
		mPlayer = null;
		
		mInstance = null;
		
		
	}
	
	public void createPlayer(String path) {
		mPlayer.createAudioPlayer(path);
	}
	
	public void releasePlayer() {
		mPlayer.releaseAudioPlayer();
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
	
	
	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		private boolean isPlaying = false;
		private int prevState = TelephonyManager.CALL_STATE_IDLE;
	    @Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	    	if(state == prevState)
	    		return;
	    	prevState = state;
	    	
	        if (state == TelephonyManager.CALL_STATE_RINGING) {
	            //Incoming call: Pause music
	        	isPlaying = isPlaying(); 
	        	if( isPlaying )
	        		pause();
	        	
	        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
	            //Not in call: Play music
	        	if( isPlaying ) {
	        		play();
	        		isPlaying = false;
	        	}
	        		
	        } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
	            //A call is dialing, active or on hold
	        	// ?
	        	
	        }
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
	
	
	public class OutgoingCallReceiver extends BroadcastReceiver {
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
	            //Log.i("CallTest", "Outgoing Call");
	        }
	    }
	}
	
	
}
