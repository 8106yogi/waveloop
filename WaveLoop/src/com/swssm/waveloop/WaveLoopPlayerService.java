package com.swssm.waveloop;

import com.swssm.waveloop.audio.OSLESMediaPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class WaveLoopPlayerService extends Service {
	
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
        return START_STICKY;// service가 강제 종료 되면 자동으로 다시 시작
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
		
		
	}
	public void onDestroy()
	{
		super.onDestroy();
		mInstance = null;
		
		mPlayer.releaseAudioPlayer();
		mPlayer.releaseEngine();
		mPlayer = null;
		
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
	
	
	
	
	
	
}
