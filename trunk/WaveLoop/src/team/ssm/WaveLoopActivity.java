package team.ssm;

import java.util.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.view.*;
import android.widget.*;
 
public class WaveLoopActivity extends TabActivity {
    /** Called when the activity is first created. */
    
    Dialog dialog;
    AlertDialog.Builder builder;
    boolean[] mSelect;
    CharSequence[] mFiles;
    Cursor cursorExt;
    Cursor cursorInt;
    ListView list;
    ArrayList<String> Items;
    ArrayAdapter<String> Adapter;
    String abc;
    
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main);
    	TabHost mTabHost = getTabHost();
    	  	 
    	
    	/*
    	SimpleCursorAdapter Adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                mCursor, new String[] { MediaColumns.DISPLAY_NAME },
                new int[] { android.R.id.text1});
    	*/
    	Items = new ArrayList<String>();
    	
        Adapter = new ArrayAdapter<String>(this, android.R.layout.
        		 simple_list_item_1, Items);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(Adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_NONE);
        list.setOnItemClickListener(mItemClickListener);
    	
        
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
    	  	.setIndicator("재생목록")    	  	
    	    .setContent(R.id.view1)
    	);
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("문장노트")
    	    .setContent(R.id.view2)
    	);
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("옵션")
        	  	.setContent(R.id.view3)
        );
    	
    	
    }
    
    
    /*
    public void additems(){
    	SimpleCursorAdapter Adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                mCursor, new String[] { MediaColumns.DISPLAY_NAME },
                new int[] { android.R.id.text1});
    	list.setAdapter(Adapter);
    	
    }
    */
    
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /****************
			리스트뷰에 나타나는 각각의 오디오 파일을 클릭했을 때 일어나는 동작을 정의하는 부분.
            "파일을 클릭하면 재생 화면으로 넘어간다." 
        	*****************/
        	
        	cursorInt.moveToPosition(position);
            String path = cursorInt.getString(cursorInt.getColumnIndex(Audio.AudioColumns.DATA));
        	Intent i = new Intent(WaveLoopActivity.this, player_main.class); 
            i.putExtra("오디오파일경로", path );
        	startActivity(i);
        
            
        }
   };
   
  
    
    public void mOnClick(View v) {
    	
    	////////////////////////////////////////////////////////////////////
    	// db????????????????????????
    	ContentResolver mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 
    	
    	cursorInt = mCr.query(uriInternal, null, null, null, null);
    	cursorExt = mCr.query(uriExternal, null, null, null, null);
    	
    	
    	int nCountInt = (cursorInt == null)?0:cursorInt.getCount();
    	int nCountExt = (cursorExt == null)?0:cursorExt.getCount();
    	int nCountTotal = nCountInt + nCountExt;
    	
    	mFiles = new CharSequence[nCountTotal];
    	mSelect = new boolean[nCountTotal];
    	//mFiles = new CharSequence[nCountExt];
    	//mSelect = new boolean[nCountExt];
    	
    	for (int i = 0; i < nCountExt; i++) {

            cursorExt.moveToPosition(i);
            mFiles[i] = cursorExt.getString(cursorExt.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME));
        }
    	
    	
    	for (int i = 0; i < nCountInt; i++) {

        	cursorInt.moveToPosition(i);
            mFiles[i+nCountExt] = cursorInt.getString(cursorInt.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME));
        }
    	
    	
    	
       
        
        ////////////////////////////////////////////////////////////////////
        startManagingCursor(cursorExt);
    	startManagingCursor(cursorInt);
    	
    	
    	// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setTitle("추가할 파일을 선택해주세요")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(mFiles, mSelect, 
    			new DialogInterface.OnMultiChoiceClickListener(){
    		public void onClick(DialogInterface dialog, int which, boolean isChecked){
    			//mSelect[which] = isChecked;
    			if(mSelect[which] = isChecked){
    				abc = (String)mFiles[which];
    				
    			}
    			//전역변수를 하나 두어서, 현재 클릭한 항목의 인덱스인 which변수값과 체크여부 변수인 isChecked를 이용하여 저장해 두었다가,
                //밑에 추가 버튼 클릭시, 혹은 cancel 버튼 클릭시에 이용.
    		}
    	})
    	
    	.setPositiveButton("추가", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int which){
    			/*************
    			 추가버튼을 눌렀을때 선택한 아이템이 리스트뷰에 보여져야 함.
    			 추가 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
    			**************/
    			Items.add(abc);
    			Adapter.notifyDataSetChanged();
    		}
    	})
    	.setNegativeButton("취소", null)
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
    	             Toast.makeText(getApplicationContext(),"����Ͽ� �߰��Ǿ���ϴ�.",Toast.LENGTH_SHORT).show();
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