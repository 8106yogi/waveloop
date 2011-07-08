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
    	  	.setIndicator("������")
    	    .setContent(R.id.view1)
    	);
    	
    	//Button launch = (Button)findViewById(R.id.AddButton);
    	//launch.setOnClickListener(new Button.OnClickListener(){
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("�����Ʈ")
    	    .setContent(R.id.view2)
    	);
    	
    	
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("�ɼ�")
        	  	.setContent(R.id.view3)
        );
    	 
    	
    }
    
    public void mOnClick(View v) {
    	
    	// TODO ���⼭ ��ü mp3 ���� ����� ��ĵ�ϰ� files�� ä����� �Ѵ�.
    	// mSelect�� �ʱ�ȭ ������� �Ѵ�.
    	
    	
    	
    	// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setTitle("�߰��� ������ �����ϼ���")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(files, mSelect, 
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