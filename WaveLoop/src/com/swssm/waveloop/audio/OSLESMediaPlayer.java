package com.swssm.waveloop.audio;

public class OSLESMediaPlayer {
	public native void createEngine();
	public native boolean createAudioPlayer(String uri);
	public native void play();
	public native void stop();
	public native void pause();
	public native boolean isPlaying();
	
	public native void seekTo(int position);
	public native int getDuration();
	public native int getPosition();
	
	public native void setLoop( int startPos, int endPos );
	public native void setNoLoop();
	
}


