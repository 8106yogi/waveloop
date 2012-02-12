package com.swssm.waveloop;

import java.io.File;

import com.swssm.waveloop.R;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.view.View;
import android.widget.*;
 
public class WaveLoopActivity extends TabActivity {
    

    static {
    	System.loadLibrary("audio-tools");
    }
    
    ComponentName mService;
    
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
    	
    	mService = startService(new Intent( this, WaveLoopPlayerService.class));

    	
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
    		    
    			finish();
    		}
    	} )
    	.setNegativeButton(getResources().getString(R.string.cancel_button), null)
    	.show();
	}
    
   
    
    
    
    

}