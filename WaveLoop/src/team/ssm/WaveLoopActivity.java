package team.ssm;

import java.util.*;

import team.ssm.DbAdapter.DatabaseHelper;
import team.ssm.ImportProgressDialog.*;
import android.*;
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
    //ArrayList<String> items;
    ArrayList<sound> m_orders;
    //ArrayAdapter<String> Adapter;
    ArrayAdapter<sound> m_adapter;
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
    static {
    	System.loadLibrary("audio-tools");
    }
    
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	
    	
    	setContentView(R.layout.main);
    	TabHost mTabHost = getTabHost();
    	
    	
    	  
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
    	  	.setIndicator("재생목록", getResources().getDrawable(R.drawable.ic_tab_playlist))    	  	
    	    .setContent(R.id.view1)
    	    
    	);
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
    	   	.setIndicator("문장노트", getResources().getDrawable(R.drawable.ic_tab_sentencenote))
    	    .setContent(R.id.view2)
    	);
    	
    	
    	mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        	   	.setIndicator("옵션", getResources().getDrawable(R.drawable.ic_tab_option))
        	  	.setContent(R.id.view3)
        );
    	
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성.
    	dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
    	//Items = new ArrayList<String>();
    	
    	
        /***** 20100725_동진: Custom ArrayAdapter를 이용한 ListView*****/
        
    	m_orders = new ArrayList<sound>();	// 리스트뷰에 출력할 내용의 원본data를 저장하는 arrayList.        
    	list = (ListView)findViewById(R.id.list);		// 사용자가 추가한 오디오 파일을 보여주는 리스트뷰
      
        m_adapter = new SoundAdapter(this, R.layout.row, m_orders); // 원본; m_orders의 내용을 리스트뷰; list에 연결해주는 어댑터.
        list.setAdapter(m_adapter);	// 어댑터와 리스트뷰를 연결
        list.setChoiceMode(ListView.CHOICE_MODE_NONE);
        list.setOnItemClickListener(mItemClickListener);	//리스트뷰의 클릭리스너 설정.
        
        
    	refreshListFromDB();
    }
    
    
    public void onDestroy(Bundle savedInstanceState) {
		super.onDestroy();
	
	}

    // 어댑터뷰의 클릭리스너
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /****************
			리스트뷰에 나타나는 각각의 오디오 파일을 클릭했을 때 일어나는 동작을 정의하는 부분.
            "파일을 클릭하면 재생 화면으로 넘어간다." 
        	*****************/
        	//dba.open();
        	//int idx = 0;
        	Intent i = new Intent(WaveLoopActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", position );
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
			message = "파일을 찾을 수 없습니다.";
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
	
	public void refreshListFromDB()	// 음악 리스트의 내용을 새로고침.
	{
		// DB의 내용을 가져다가 m_orders에 새로 입력.
		m_orders.clear();
			
		
		
		/*********************  디버깅용. db 테이블(data) 삭제 코드 ************************/
		/*
		db = dbh.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS data");
		db.execSQL("create table data (_id integer primary key autoincrement,"+
		"filepath text not null, wavepath text not null, media_db_id text not null)");
		*/
		
		dba.open();
		
		Cursor cur = dba.fetchAllBooks();
		for(int i = 0; i < cur.getCount(); ++i )
		{
			cur.moveToPosition(i);
			String strMediaDBIndex = cur.getString(cur.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID));
			
			Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;

	    	String selection = "( (_ID LIKE ?) )";
	    	String[] selectionArgs = { strMediaDBIndex };
	    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
	    	
	    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
	    	Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
	    	if(curMedia.getCount() == 1)
	    	{
	    		curMedia.moveToPosition(0);
		    	String artist = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ARTIST));
				String album = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ALBUM));
				String title = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.TITLE));
				
				sound s = new sound(artist, album, title);
				m_orders.add(s);
	    		
	    	}
	    	else
	    	{
	    		// 오디오 파일을 삭제하면 이곳에 들어올 것임.
	    		// 자동으로 삭제할지, 수동으로 삭제할지 생각해보자.
	    	}
	    	
			//String strFilePath = cur.getString(cur.getColumnIndex(DbAdapter.KEY_FILEPATH));
			//String strWavePath = cur.getString(cur.getColumnIndex(DbAdapter.KEY_WAVEPATH));
			//m_orders.add(strFilePath);// 여기 뭔가 수정되어야 할듯!?
			/*
			mCursor.moveToPosition(i);
			String artist = cur.getString(mCursor.getColumnIndex(Audio.AudioColumns.ARTIST));
			String album = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ALBUM));
			String title = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.TITLE));
			
			sound s1 = new sound(artist, album, title);
			m_orders.add(s1);
			*/
		}
		dba.close();
		
		m_adapter.notifyDataSetChanged();
	}
   
    public void mOnClick(View v) {
    	
    	/*    	
    	ContentResolver mCr = getContentResolver();
    	
    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	//Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 

    	String selection = "( (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) OR (_DATA LIKE ?) )";
    	String[] selectionArgs = {"%.aac", "%.arm", "%.mp3", "%.wav" };
    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
    	
    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
    	Cursor cursorExt = mCr.query(uriExternal, null, selection, selectionArgs, sortOrder);
    	
    	//mCursor = new MergeCursor( new Cursor[] { cursorInt, cursorExt} );
    	mCursor = cursorExt;
    	
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
    	*/
    	//int idx = 0;
    	
    	//i.putExtra("오디오파일경로", position );
    	//i.putExtra("오디오파일경로", path );
    	
		
    	
    	
    	Intent i = new Intent(WaveLoopActivity.this, addActivity.class); 
    	startActivity(i);
    	
    	/*
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
    					
    				    //Person p1 = new Person("안드로이드", "011-123-4567");
    			        //Person p2 = new Person("구글", "02-123-4567");
    			        //m_orders.add(p1);
    			        //m_orders.add(p2);
    					
    					mCursor.moveToPosition(i);
    					String artist = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ARTIST));
    					String album = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ALBUM));
    					String title = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.TITLE));
    					
    					sound s1 = new sound(artist, album, title);
    					m_orders.add(s1);
    					//m_orders.add( (String)mFiles[i] );
    					//mCursor.moveToPosition(i);	
    		            
    					String path = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.DATA));
    					dba.createBook(path,WAVEPATH + mFiles[i],0);
    					
    				}
    			}
    			m_adapter.notifyDataSetChanged();
    			dba.close();
    			
    			
    			
    			// 선택된 음악파일 경로를 ArrayList에 담는다.
    			ArrayList<Integer> paths = new ArrayList<Integer>();
    			for(int i = 0; i < mSelect.length; ++i )
    			{
    				if( mSelect[i] )
    				{
    					mCursor.moveToPosition(i);	
    		            int id = mCursor.getInt(mCursor.getColumnIndex(Audio.AudioColumns._ID));
    					paths.add(id);
    				}
    			}
    			
    			
    			
    			// 로딩 다이얼로그 생성
    			importDialog = new ImportProgressDialog(WaveLoopActivity.this);
    			importDialog.setAudioIDs(paths);
    			importDialog.setContentResolver(getContentResolver());
    			importDialog.setFinishLoading( new ImportProgressDialog.FinishLoading() { 
    				private ImportProgressDialog.EFinishResult mResult;
    				public void finish( ImportProgressDialog.EFinishResult result ){// dialog가 dismiss 될 때 호출되는 함수.
    					mResult = result;
	    				runOnUiThread( new Runnable(){
	    					public void run()
	    					{
	    						showLoadingResultMessage(mResult);
	    						refreshListFromDB();
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
    	*/
    
    }
    
    /*
    private class CustomFinishLoading implements FinishLoading{
    	
           

			private ImportProgressDialog.EFinishResult mResult;
			public void finish( ImportProgressDialog.EFinishResult result ){// dialog가 dismiss 될 때 호출되는 함수.
				mResult = result;
				runOnUiThread( new Runnable(){
					public void run()
					{
						showLoadingResultMessage(mResult);
						refreshListFromDB();
					}
				});

			
			}
    }
    */
    //	어댑터 클래스
    private class SoundAdapter extends ArrayAdapter<sound> {		

        private ArrayList<sound> items;

        public SoundAdapter(Context context, int textViewResourceId, ArrayList<sound> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        // 각 항목의 뷰 생성
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }
                sound p = items.get(position);
                if (p != null) {
                        TextView tar = (TextView) v.findViewById(R.id.top_artist);
                        TextView tal = (TextView) v.findViewById(R.id.top_album);
                        TextView bt = (TextView) v.findViewById(R.id.bottom_title);
                        if (tar != null){
                        	tar.setText(p.getArtist());                            
                        }
                        if (tal != null){
                        	tal.setText(p.getAlbum());                            
                        }
                        if(bt != null){
                        		bt.setText(p.getTitle());
                        }
                }
                return v;
        }
    }
    

    
    // 리스트 뷰에 출력할 항목
    public class sound {	
        
        private String Artist;
        private String Album;
        private String Title;
        
        
        public sound(String _Artist, String _Album, String _Title) {
        	this.Artist = _Artist;
        	this.Album = _Album;
        	this.Title = _Title;
        }
        
        public String getArtist() {
            return Artist;
        }
        
        public String getAlbum(){
        	return Album;
        }
        public String getTitle() {
            return Title;
        }

    }
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item=menu.add(0,1,0,"도움말");
        item.setIcon(R.drawable.ic_tab_example_selected);
        menu.add(0,2,0,"추가").setIcon(R.drawable.ic_tab_example_selected);
        menu.add(0,3,0,"전체삭제").setIcon(R.drawable.ic_tab_example_selected);
        
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
              Toast.makeText(this,"도움말입니다^^;",Toast.LENGTH_SHORT).show();
              return true;
        case 2:
              Toast.makeText(this,"추가합니다^^;",Toast.LENGTH_SHORT).show();
              return true;
        case 3:
        	{
	        
	        	new AlertDialog.Builder(WaveLoopActivity.this)
	        	.setTitle("모든 목록을 삭제하시겠습니까?")
	        	.setIcon(android.R.drawable.ic_dialog_alert)
	        	.setCancelable(false)
	        	.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
	        		public void onClick(DialogInterface dialog, int which){
	        			// 조낸 삭제 코드 작성.
	        			// 모든 파일을 삭제하고
	        			// DB를 초기화
	        			
	        			Toast.makeText(WaveLoopActivity.this,"모두 삭제되었습니다.",Toast.LENGTH_SHORT).show();
	        		}
	        	} )
	        	.setNegativeButton("취소", null)
	        	.show();
	        	
	        	
	        }
	        return true;
        }
        return false;
 }
    
    
    

}