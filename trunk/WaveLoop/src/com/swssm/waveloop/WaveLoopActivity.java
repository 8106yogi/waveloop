package com.swssm.waveloop;

import java.io.File;

import com.swssm.waveloop.R;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.View;
import android.widget.*;
 
public class WaveLoopActivity extends TabActivity {
    

    static {
    	System.loadLibrary("audio-tools");
    }
    
    
    LinearLayout mWaveLoopMain;
    LinearLayout mNowPlayingMain;
    ImageButton mNowPlayingBtn;
    TextView mNowPlayingTitle;
    TextView mNowPlayingArtist;
    TextView mNowPlayingAlbum;
    ImageButton mNowPlayingPlayBtn;
	
    
    ComponentName mService = null;
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main);
    	TabHost tabHost = getTabHost();

    	tabHost.addTab(tabHost.newTabSpec("tab_test1")
    	  	.setIndicator(
    	  			getResources().getString(R.string.tab_playlist), 
    	  			getResources().getDrawable(R.drawable.ic_tab_playlist))    	  	
    	    //.setContent(R.id.view1)
    	  	.setContent( new Intent(this, PlaylistActivity.class) )
    	    
    	);
    	
    	tabHost.addTab(tabHost.newTabSpec("tab_test2")
    	   	.setIndicator(
    	   			getResources().getString(R.string.tab_favorites),
    	   			getResources().getDrawable(R.drawable.ic_tab_sentencenote))
    	    .setContent( new Intent(this, SentenceNoteActivity.class) )
    	);
    	
    	
    	tabHost.addTab(tabHost.newTabSpec("tab_test3")
        	   	.setIndicator(
        	   			getResources().getString(R.string.tab_option),
        	   			getResources().getDrawable(R.drawable.ic_tab_info))
        	  	.setContent( new Intent(this, OptionActivity.class) )
        	  	
        );
    	
    	mWaveLoopMain = (LinearLayout)findViewById(R.id.waveloop_main);
    	
    	mNowPlayingMain = (LinearLayout)findViewById(R.id.now_playing_main);
    	mNowPlayingBtn = (ImageButton)findViewById(R.id.now_playing_btn);
    	
    	mNowPlayingArtist = (TextView)findViewById(R.id.now_playing_artist);
    	mNowPlayingAlbum = (TextView)findViewById(R.id.now_playing_album);
    	mNowPlayingTitle = (TextView)findViewById(R.id.now_playing_title);
    	
    	mNowPlayingPlayBtn = (ImageButton)findViewById(R.id.now_playing_playbtn);
    	
    	if(mService == null)
    	{
    		Intent i = new Intent( this, WaveLoopPlayerService.class);
    		mService = startService(i);
    	}
    	
    }
    
    public void onResume() {
    	super.onResume();
    	
    	//Log.i("WaveLoop", "onStart");
    	
    	if(PlayerProxy.instance() == null) {
    		//mNowPlayingMain.setEnabled(false);
    		//mNowPlayingMain.setVisibility(View.INVISIBLE);
    		
    		mWaveLoopMain.removeView(mNowPlayingMain);
    		return;
    	}
    	
    	final AudioInfo audioInfo = PlayerProxy.getAudioInfo();
    	if( audioInfo != null )
    	{
    		mWaveLoopMain.removeView(mNowPlayingMain);
    		mWaveLoopMain.addView(mNowPlayingMain);
    		
    		//mNowPlayingMain.setEnabled(true);
    		//mNowPlayingMain.setVisibility(View.VISIBLE);
    		
    		mNowPlayingArtist.setText(audioInfo.artist);
    		mNowPlayingAlbum.setText(audioInfo.album);
    		mNowPlayingTitle.setText(audioInfo.title);
    		
    		mNowPlayingPlayBtn.setImageResource( (PlayerProxy.isPlaying())?
					R.drawable.pause_bkgnd : R.drawable.play_bkgnd);
		
    		mNowPlayingPlayBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(PlayerProxy.isPlaying())
						PlayerProxy.pause();
					else
						PlayerProxy.play();
					
					mNowPlayingPlayBtn.setImageResource( (PlayerProxy.isPlaying())?
							R.drawable.pause_bkgnd : R.drawable.play_bkgnd);
				
				}
			});
    		
    		mNowPlayingBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
	        	   	Intent i = new Intent(WaveLoopActivity.this, player_main.class); 
	        		i.putExtra("오디오파일경로", (int)audioInfo.dataRowId );
	        		startActivity(i);
				}
			});
    	}
    	else
    	{
    		mWaveLoopMain.removeView(mNowPlayingMain);
    		//mNowPlayingMain.setEnabled(false);
    		//mNowPlayingMain.setVisibility(View.INVISIBLE);
    	}
    	
    	
    }

    
   
   //액티비티 종료시 재생 강제 종료
   public void onDestroy() {
		super.onDestroy();
		/*
		if (player_main.mPlayer != null) {
			player_main.mPlayer.release();
			player_main.mPlayer = null;
	    }
	    */
		//if( player_main.mOSLESPlayer != null )
		{
//			player_main.mOSLESPlayer.releaseAudioPlayer();
//			player_main.mOSLESPlayer.releaseEngine();
//			player_main.mOSLESPlayer = null;
		}
		
	}

	public void onBackPressed()
	{
		
		new AlertDialog.Builder(this)
    	.setTitle( getResources().getString(R.string.alert_quit_message) )
    	.setIcon(android.R.drawable.ic_dialog_alert)
    	//.setCancelable(false)
    	.setPositiveButton( getResources().getString(R.string.quit_button)
    			, new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int which){
    			Intent intent = new Intent();
    			intent.setComponent(mService);
    		    stopService(intent);
    		    mService = null;
    		    
    			finish();
    		}
    	} )
    	.setNegativeButton(getResources().getString(R.string.cancel_button), null)
    	.show();
	}
    
   
    
    
    
    

}