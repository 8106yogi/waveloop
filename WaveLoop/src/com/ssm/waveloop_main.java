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
    	  	.setIndicator("������", getResources().getDrawable(R.drawable.ic_tab_playlist ))
    	    .setContent(R.id.view1)
    	);
    	
    	
    	//Button launch = (Button)findViewById(R.id.AddButton);
    	//launch.setOnClickListener(new Button.OnClickListener(){
    	
    	
    	
    	//android.R.drawable.ic_menu_crop
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("�����Ʈ", getResources().getDrawable(R.drawable.ic_tab_sentencenote ))
    	    .setContent(R.id.view2)
    	);
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("�ɼ�", getResources().getDrawable(R.drawable.ic_tab_option ))
        	  	.setContent(R.id.view3)
        );
    	 
    	
    }
    
    public void mOnClick(View v) {
    	
    	////////////////////////////////////////////////////////////////////
    	// db�� �˻��Ͽ� ����� ������ ���� �����ϱ�
    	ContentResolver mCr;
    	mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 
    	
    	Cursor cursorInt = mCr.query(uriInternal, null, null, null, null);
    	Cursor cursorExt = mCr.query(uriExternal, null, null, null, null);// sdī�尡 ������ cursorExt == null
    	
    	
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
    	.setTitle("�߰��� ������ �����ϼ���")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(mFiles, mSelect, 
    			new DialogInterface.OnMultiChoiceClickListener(){
    		public void onClick(DialogInterface dialog, int which, 
    				boolean isChecked){
    			mSelect[which] = isChecked;
    		}
    	})
    	
    	.setPositiveButton("�߰�", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton){
    			
    		}
    	})
    	.setNegativeButton("���", null)
    	.show();
    	
    
    	
    	/*
    	AlertDialog.Builder builder = new AlertDialog.Builder(waveloop_main.this);
    		builder.setTitle("�߰��� ������ �����ϼ���");
    		builder.setMultiChoiceItems(files, mSelect, new DialogInterface.OnMultiChoiceClickListener() {
		
    		    
    		    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    		      
    		    	Toast.makeText( getApplicationContext(), 
    		                  items[which] + "  Checked-" + Boolean.toString(isChecked), 
    		                  Toast.LENGTH_SHORT).show();
    		      
    		      }
    		    })
    		.setPositiveButton("�߰�", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	             Toast.makeText(getApplicationContext(),"�����Ͽ� �߰��Ǿ����ϴ�.",Toast.LENGTH_SHORT).show();
    	           }
    	       })
    	       .setNegativeButton("���", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	             dialog.cancel();
    	           }
    	       });
    	*/


    	}
    	

}