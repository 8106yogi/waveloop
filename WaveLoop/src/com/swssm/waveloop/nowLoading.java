package com.swssm.waveloop;

import com.swssm.waveloop.R;

import android.app.*;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.*;
import android.view.*;
import android.widget.TextView;

public class nowLoading extends Activity {
	private TextView mVersion;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowloading);
        
        Context context = getApplicationContext();
        String version = null;
        try {
        	PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        	version = i.versionName;
        } catch(NameNotFoundException e) { }
        
        mVersion = (TextView)findViewById(R.id.version);
        mVersion.setText(version);
        
        
        GlobalOptions.load(this);
        
        mHandler.sendEmptyMessage(0);
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return true;
		
		return true;
	}
	
	/*
	public boolean dispatchTouchEvent(MotionEvent event) {
		return true;
	}
	*/
	
	
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
