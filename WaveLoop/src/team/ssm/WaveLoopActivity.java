package team.ssm;

import java.util.*;

import team.ssm.DbAdapter.DatabaseHelper;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
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
    DbAdapter dba;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    //int idx;
    ImportProgressDialog importDialog;
    
    public static final String WAVEPATH = "/data/data/com.androidhuman.app/files/";   
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
    	
    	
    	dba = new DbAdapter(this);
    	dbh = dba.new DatabaseHelper(this);
    	//dba.open();
    	
    	
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
        	dba.open();
        	//mCursor.moveToPosition(position);
            //String path = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DATA));
        	 
        	
        	
        
        	
        	int idx = 0;
        	Intent i = new Intent(WaveLoopActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", idx );
        	//i.putExtra("오디오파일경로", path );
        	startActivity(i);
        
            
        }
   };
   
	private void showLoadingResultMessage( ImportProgressDialog.EFinishResult result )
	{
		String message;
		switch(result)
		{
		case eFR_OK:
			message = "성공적으로 처리되었습니다.";
			break;
		case eFR_FILEERROR:
			message = "잘못된 파일입니다.";
			break;
		case eFR_FILENOTFOUNDERROR:
			message = "파일을 찾을 수 없스빈다.";
			break;
		case eFR_SUSPEND:
			message = "작업이 중단되었습니다.";
			break;
		case eFR_EXCEPTION:
			message = "알 수 없는 에러가 발생했습니다.";
			break;
		default:
			message = "알 수 없는 에러가 발생했습니다.";
			break;
		}
		Toast.makeText( getApplicationContext(), message, Toast.LENGTH_SHORT ).show();
   }

	public void refrashListFromDB()
	{
		// DB의 내용을 가져다가 Items에 새로 입력.
		Items.clear();
		dba.open();
		Cursor cur = dba.fetchAllBooks();
		for(int i = 0; i < cur.getCount(); ++i )
		{
			cur.moveToPosition(i);
			String strFilePath = cur.getString(cur.getColumnIndex(DbAdapter.KEY_FILEPATH));
			//String strWavePath = cur.getString(cur.getColumnIndex(DbAdapter.KEY_WAVEPATH));
			Items.add(strFilePath);// 여기 뭔가 수정되어야 할듯!?
		}
		dba.close();
		
		Adapter.notifyDataSetChanged();
	}
   
    public void mOnClick(View v) {
    	
    	/*
    	dba = new DbAdapter(this);
    	dbh = dba.new DatabaseHelper(this);
    	
    	SQLiteOpenHelper dbHelper = new DatabaseHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		*/
    	
    	ContentResolver mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 

    	String selection = "( (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) )";
    	String[] selectionArgs = {"%.aac", "%.arm", "%.mp3", "%.wav" };
    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
    	
    	Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
    	Cursor cursorExt = mCr.query(uriExternal, null, selection, selectionArgs, sortOrder);
    	
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
    			//  DB에 입력 전에 웨이브파일을 생성하고 DB에 입력해야 할 듯
    			
    			/*
    			dba.open();
    			for(int i = 0; i < mSelect.length; ++i )
    			{
    				if( mSelect[i] )
    				{
    					Items.add( (String)mFiles[i] );
    					mCursor.moveToPosition(i);	
    		            String path = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DATA));
    					dba.createBook(path,WAVEPATH + mFiles[i]);
    					
    				}
    			}
    			Adapter.notifyDataSetChanged();
    			dba.close();
    			*/
    			
    			// 선택된 음악파일 경로를 ArrayList에 담는다.
    			ArrayList<String> paths = new ArrayList<String>();
    			for(int i = 0; i < mSelect.length; ++i )
    			{
    				if( mSelect[i] )
    				{
    					mCursor.moveToPosition(i);	
    		            String path = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DATA));
    					paths.add(path);
    				}
    			}
    			
    			
    			// 로딩 다이얼로그 생성
    			importDialog = new ImportProgressDialog(WaveLoopActivity.this);
    			importDialog.setAudioPaths(paths);
    			importDialog.setFinishLoading( new ImportProgressDialog.FinishLoading() { 
    				private ImportProgressDialog.EFinishResult mResult;
    				public void finish( ImportProgressDialog.EFinishResult result ){// dialog가 dismiss 될 때 호출되는 함수.
    					mResult = result;
	    				runOnUiThread( new Runnable(){
	    					public void run()
	    					{
	    						showLoadingResultMessage(mResult);
	    						refrashListFromDB();
	    					}
	    				});

    				}
    			});
    			importDialog.show();
    			importDialog.beginThread();
    			
    			
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