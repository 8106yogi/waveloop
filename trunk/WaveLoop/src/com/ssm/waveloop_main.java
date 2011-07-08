package com.ssm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
 
public class waveloop_main extends TabActivity {
    /** Called when the activity is first created. */
    
    Dialog dialog;
    AlertDialog.Builder builder;
    boolean[] mSelect = { false, false, false, false };
    final CharSequence[] files = {"a.mp3", "b.mp3", "c.mp3"};
	

    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main);
    	TabHost mTabHost = getTabHost();
    	  	 
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
    	  	.setIndicator("재생목록")
    	    .setContent(R.id.view1)
    	);
    	
    	//Button launch = (Button)findViewById(R.id.AddButton);
    	//launch.setOnClickListener(new Button.OnClickListener(){
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("문장노트")
    	    .setContent(R.id.view2)
    	);
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("옵션")
        	  	.setContent(R.id.view3)
        );
    	 
    	
    }
    
    public void mOnClick(View v) {
    	
    	// TODO 여기서 전체 mp3 파일 목록을 스캔하고 files에 채워줘야 한다.
    	// mSelect를 초기화 시켜줘야 한다.
    	
    	
    	
    	// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setTitle("추가할 파일을 선택하세요")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(files, mSelect, 
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