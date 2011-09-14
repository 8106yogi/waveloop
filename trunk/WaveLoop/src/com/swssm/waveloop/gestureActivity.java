package com.swssm.waveloop;
import java.util.*;

import com.swssm.waveloop.*;

import android.app.*;
import android.gesture.*;
import android.gesture.GestureOverlayView.*;
import android.os.*;
import android.widget.*;


public class gestureActivity extends Activity implements OnGesturePerformedListener{
	private GestureLibrary mLibrary;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_main);
		
		
        //  기정의된 제스쳐를 앱에서 반드시 로드하여 사용. GestureLibraries 클래스를 사용..
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            finish();
        }
        
        
        // 앱에서 제스쳐를 인식하기 위해서 XML layout에 GestureOverlayView을 추가. 제스쳐리스너 등록.
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);
        
        
	}

	 
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		// We want at least one prediction
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			// We want at least some confidence in the result
			if (prediction.score > 1.0) {
				// Show the spell
				Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
    public void onResume(){
    	super.onResume();
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	finish();
    }
	  
}
