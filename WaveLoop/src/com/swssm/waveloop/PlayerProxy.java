package com.swssm.waveloop;


public class PlayerProxy {
	

	static WaveLoopPlayerService instance()
	{
		return WaveLoopPlayerService.getInstance();
	}
	static void createPlayer(String path) {
		instance().createPlayer(path);
	}
	static void releasePlayer() {
		instance().releasePlayer();
	}
	
	static void play() {
		instance().play();
	}
	static void pause() {
		instance().pause();
	}
	static void stop() {
		instance().stop();
	}
	
	static boolean isPlaying() {
		return instance().isPlaying();
	}
	
	static void seekTo(int pos) {
		instance().seekTo(pos);
	}
	
	static int getPosition() {
		return instance().getPosition();
	}
	
	static int getDuration() {
		return instance().getDuration();
	}
	
	static void setRate(int rate) {
		instance().setRate(rate);
	}
	
	static int getRate() {
		return instance().getRate();
	}
	
}

