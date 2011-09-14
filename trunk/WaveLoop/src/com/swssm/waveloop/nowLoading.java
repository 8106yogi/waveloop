package com.swssm.waveloop;

import com.swssm.waveloop.R;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class nowLoading extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowloading);
         
        mHandler.sendEmptyMessage(0);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			return true;
		}
		
	return true;

	}
	
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			
			
						  
	   	 	mHandler.postDelayed(new Runnable() {  
	   	   		public void run() {  
	   	 			
	   	 				 				
		   	 				Intent intent = new Intent(nowLoading.this, WaveLoopActivity.class);        		
		   	 				startActivity(intent);
		   	 				finish();
	   	 	
	   	 		} 
	   	 	}, 1000);
						
		}
	};
}
