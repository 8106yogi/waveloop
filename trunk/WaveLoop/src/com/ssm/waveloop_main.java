package com.ssm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.widget.TabHost;
 
public class waveloop_main extends TabActivity {
    /** Called when the activity is first created. */
    
    Dialog dialog;
    AlertDialog.Builder builder;
    boolean[] mSelect;
    CharSequence[] mFiles;
	

    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main);
    	TabHost mTabHost = getTabHost();
    	  	 
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
    	  	.setIndicator("재생목록", getResources().getDrawable(R.drawable.ic_tab_playlist ))
    	    .setContent(R.id.view1)
    	);
    	
    	
    	//Button launch = (Button)findViewById(R.id.AddButton);
    	//launch.setOnClickListener(new Button.OnClickListener(){
    	
    	
    	
    	//android.R.drawable.ic_menu_crop
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("문장노트", getResources().getDrawable(R.drawable.ic_tab_sentencenote ))
    	    .setContent(R.id.view2)
    	);
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("옵션", getResources().getDrawable(R.drawable.ic_tab_option ))
        	  	.setContent(R.id.view3)
        );
    	 
    	
    }
    
    public void mOnClick(View v) {
    	
    	////////////////////////////////////////////////////////////////////
    	// db를 검색하여 오디오 파일을 전부 나열하기
    	ContentResolver mCr;
    	mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 
    	
    	Cursor cursorInt = mCr.query(uriInternal, null, null, null, null);
    	Cursor cursorExt = mCr.query(uriExternal, null, null, null, null);// sd카드가 없으면 cursorExt == null
    	
    	
    	int nCountInt = (null == cursorInt)?0:cursorInt.getCount();
    	int nCountExt = (null == cursorExt)?0:cursorExt.getCount();
    	int nCountTotal = nCountInt + nCountExt;
    	
    	mFiles = new CharSequence[nCountTotal];
    	mSelect = new boolean[nCountTotal];
    	
    	for (int i = 0; i < nCountInt; i++) {

        	cursorInt.moveToPosition(i);
            mFiles[i] = cursorInt.getString(cursorInt.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME));
        }
    	
    	
        for (int i = 0; i < nCountExt; i++) {

            cursorExt.moveToPosition(i);
            mFiles[i + nCountInt] = cursorExt.getString(cursorExt.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME));
        }
        
        ////////////////////////////////////////////////////////////////////
    	
    	
    	
    	// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setTitle("추가할 파일을 선택하세요")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(mFiles, mSelect, 
    			new DialogInterface.OnMultiChoiceClickListener(){
    		public void onClick(DialogInterface dialog, int which, 
    				boolean isChecked){
    			mSelect[which] = isChecked;
    		}
    	})
    	
    	.setPositiveButton("추가", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton){
    			
    		}
    	})
    	.setNegativeButton("취소", null)
    	.show();
    	
    
    	
    	/*
    	AlertDialog.Builder builder = new AlertDialog.Builder(waveloop_main.this);
    		builder.setTitle("추가할 파일을 선택하세요");
    		builder.setMultiChoiceItems(files, mSelect, new DialogInterface.OnMultiChoiceClickListener() {
		
    		    
    		    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    		      
    		    	Toast.makeText( getApplicationContext(), 
    		                  items[which] + "  Checked-" + Boolean.toString(isChecked), 
    		                  Toast.LENGTH_SHORT).show();
    		      
    		      }
    		    })
    		.setPositiveButton("추가", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	             Toast.makeText(getApplicationContext(),"재생목록에 추가되었습니다.",Toast.LENGTH_SHORT).show();
    	           }
    	       })
    	       .setNegativeButton("취소", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	             dialog.cancel();
    	           }
    	       });
    	*/


    	}
    	

}