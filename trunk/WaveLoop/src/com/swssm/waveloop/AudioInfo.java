package com.swssm.waveloop;

public class AudioInfo {
	public long mediaId;
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

	public AudioInfo() {
		
	}
}