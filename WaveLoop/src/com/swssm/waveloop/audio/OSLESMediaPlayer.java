package com.swssm.waveloop.audio;

public class OSLESMediaPlayer {
	public native void createEngine();
	public native void releaseEngine();
	public native boolean createAudioPlayer(String uri);
	public native void releaseAudioPlayer();
	public native void play();
	public native void stop();
	public native void pause();
	public native boolean isPlaying();
	
	public native void seekTo(int position);
	public native int getDuration();
	public native int getPosition();
	
	public native void setRate(int rate);
	public native int getRate();
	
	public native void setLoop( int startPos, int endPos );
	public native void setNoLoop();
	
	
	public interface OnCompletionListener {
	    public void OnCompletion();// 플레이가 정상적으로 끝나서 종료되었을 때 호출되는 함수 
	}
	
	private OnCompletionListener mCompletionListener;
	public void SetOnCompletionListener( OnCompletionListener listener )
	{
		mCompletionListener = listener;
	}
	
	
	private void OnCompletion()
	{
		mCompletionListener.OnCompletion();
		
		int position = getPosition();
		int duration = getDuration();
		if( position != duration )
		{
			int a = 0;
			// 뭣이 다르다고!?
		}
		else
		{
			int c = 0;
			// 정상!
		}
	}
}


