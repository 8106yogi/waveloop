package team.ssm;

import java.util.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore;
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
 
    Cursor mCursor;
    ListView list;
    ArrayList<String> Items;
    ArrayAdapter<String> Adapter;
    String abc;
    
    /*
    // 로딩 progress 관련
    private long mLoadingStartTime;
    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private ProgressDialog mProgressDialog;
    */
    
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
        	
        	mCursor.moveToPosition(position);
            String path = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DATA));
        	Intent i = new Intent(WaveLoopActivity.this, player_main.class); 
            i.putExtra("오디오파일경로", path );
        	startActivity(i);
        
            
        }
   };
   

   
    public void mOnClick(View v) {
    	
    	////////////////////////////////////////////////////////////////////
    	
    	ContentResolver mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 

    	String selection = "( (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) )";
    	String[] selectionArgs = {"%.aac", "%.arm", "%.mp3", "%.wav" };
    	
    	Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    	Cursor cursorExt = mCr.query(uriExternal, null, selection, selectionArgs, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    	
    	mCursor = new MergeCursor( new Cursor[] { cursorInt, cursorExt} );
    	
    	int nCurCount = (mCursor == null)?0:mCursor.getCount();
    	
    	if(nCurCount > 0)
    	{
    		mFiles = new CharSequence[nCurCount];
        	mSelect = new boolean[nCurCount];
        	
        	for (int i = 0; i < nCurCount; i++) {
        		mCursor.moveToPosition(i);
                mFiles[i] = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME));
                mSelect[i] = false;
            }

            startManagingCursor(mCursor);
    	}
    	
 
    	
    	// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setTitle("추가할 파일을 선택해주세요")
    	.setIcon(R.drawable.icon)
    	.setMultiChoiceItems(mFiles, mSelect, 
    			new DialogInterface.OnMultiChoiceClickListener(){
    		public void onClick(DialogInterface dialog, int which, boolean isChecked){
    			mSelect[which] = isChecked;
    		}
    	})
    	
    	.setPositiveButton("추가", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int which){
    			/*************
    			 추가버튼을 눌렀을때 선택한 아이템이 리스트뷰에 보여져야 함.
    			 추가 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
    			**************/
    			
    			for(int i = 0; i < mSelect.length; ++i )
    			{
    				if( mSelect[i] )
    				{
    					Items.add( (String)mFiles[i] );
    				}
    				
    			}
    			Adapter.notifyDataSetChanged();
    			
    			// 로딩 화면으로 전환 필요
    			/*
    			mLoadingStartTime = System.currentTimeMillis();
    	        mLoadingLastUpdateTime = System.currentTimeMillis();
    	        mLoadingKeepGoing = true;
    	        mProgressDialog = new ProgressDialog(WaveLoopActivity.this);
    	        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	        mProgressDialog.setTitle("로딩중이시다!");
    	        mProgressDialog.setCancelable(true);
    	        mProgressDialog.setOnCancelListener(
    	            new DialogInterface.OnCancelListener() {
    	                public void onCancel(DialogInterface dialog) {
    	                    mLoadingKeepGoing = false;
    	                }
    	            });
    	        mProgressDialog.show();

    	        */
    			
    			// ProgressDialog 
    	        
    			// 여기서 사운드 파일의 변환 과정을 거치고
    			// 정상 처리된 파일을 DB에 추가하고
    			// DB 목록을 기반으로 메인 Activity 를 업데이트 한다.
    			
    			// DB 파일을 Activity 에 추가시, 해당 경로에 실제 mp3 파일이 있는지 매번 확인이 필요하다.
    			
    			
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