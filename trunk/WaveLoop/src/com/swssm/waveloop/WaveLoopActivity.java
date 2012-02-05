package com.swssm.waveloop;

import com.swssm.waveloop.R;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
 
public class WaveLoopActivity extends TabActivity {
    

    static {
    	System.loadLibrary("audio-tools");
    }
    
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
    	
    	

    	
    }

     
   public void onResume(){
	   super.onResume();
	   //refreshListFromDB();
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

    
    
   
    
    
    
    

}