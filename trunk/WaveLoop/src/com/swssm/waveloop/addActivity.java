package com.swssm.waveloop;

import java.util.*;

import com.swssm.waveloop.R;
import com.swssm.waveloop.DbAdapter.DatabaseHelper;
import com.swssm.waveloop.ImportProgressDialog.FinishLoading;
import com.swssm.waveloop.WaveLoopActivity.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class addActivity extends Activity {
		Dialog dialog;
	    AlertDialog.Builder builder;
	    boolean[] mSelect;
	    
	 
	    Cursor mCursor;
	    ListView list2;
	   	    
	    ArrayList<Sound> mAllItems;
	    private ArrayList<Sound> mFilteredItems;
	    
	    ArrayAdapter<Sound> m_adapter2;
	    ArrayList<String> MediaDBIndex;
	    ArrayList<String> repetMDBIdx;
	    String abc;
	    DbAdapter dba;
	    DatabaseHelper dbh;
	    SQLiteDatabase db;
	    
	    ImportProgressDialog importDialog;
	    int mPosition;
	    Button btn;
	    CheckBox cb;
	    
	    private EditText mFilterText;
	    
	    private boolean isChecked[] ; //체크박스의 체크유무를 저장하는 boolean 배열.(체크되면 true)
	    private boolean isImported[];	//파일의 중복유무를 저장하는 boolean 배열.(중복되면 true)
	    private boolean isDismiss=false;
	    ArrayList<String> mFiles;
	    //public static final String WAVEPATH = "/data/data/com.androidhuman.app/files/";   
	    //private FinishLoading mFinishLoading;
	    //public static Activity add_activity;
	    
	    public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.add_activity);
		
		//team.ssm.WaveLoopActivity.m_orders = m_orders;
		//add_activity = this;		
		dba = new DbAdapter(this); //어댑터 객체 생성.
    	dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
		
    	btn = (Button)findViewById(R.id.add);
		btn.setEnabled(false);
		
		mFilterText = (EditText)findViewById(R.id.search_box);
		mFilterText.addTextChangedListener(filterTextWatcher);
		
		
    	mAllItems = new ArrayList<Sound>();	//리스트뷰에 출력할 항목(sound 객체)들을 저장하는 ArraylList 생성.
    	mFilteredItems = mAllItems;
    	
        list2 = (ListView)findViewById(R.id.list2);
        m_adapter2 = new SoundAdapter2(this, R.layout.row2, mAllItems); // 어댑터를 생성.
        list2.setAdapter(m_adapter2);
        list2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list2.setOnItemClickListener(mItemClickListener2);
        list2.setItemsCanFocus(false);
        
       // CheckBox chk = (CheckBox)findViewById(R.id.chk);
        //chk.setOnCheckedChangeListener(mCheckChange);
        //cb=(CheckBox)findViewById(R.id.chk);
		//cb.setOnCheckedChangeListener(null);
		
		        
    	
        
    	ContentResolver mCr = getContentResolver();

    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	//Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 

    	String selection = "( ( (_DATA LIKE ?) OR (_DATA LIKE ?) ) AND ( (IS_MUSIC = 1) OR (IS_PODCAST = 1) ) )";
    	String[] selectionArgs = {"%.mp3", "%.wav" };
    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
    	
    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
    	mCursor = mCr.query(uriExternal, null, selection, selectionArgs, sortOrder);
    	
    	//mCursor = new MergeCursor( new Cursor[] { cursorInt, cursorExt} );
    	//mCursor = cursorExt;
    	
    	if(mCursor != null)
    	{
    		int nCurCount = mCursor.getCount();
        	if(nCurCount > 0){
        		isChecked = new boolean[nCurCount];
        		isImported = new boolean[nCurCount];
        		
        		for (int i = 0; i < nCurCount; i++) {
    	    	
    	    		mCursor.moveToPosition(i);
    		    	String artist = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ARTIST));
    				String album = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ALBUM));
    				String title = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.TITLE));
    				int time = mCursor.getInt(mCursor.getColumnIndex(Audio.AudioColumns.DURATION))/1000;
    				
    				Sound s = new Sound(artist, album, title, time, mAllItems.size() );
    				mAllItems.add(s);
    	    		
    	    	}
    	    	startManagingCursor(mCursor);
        	}
        	
        	compare();
    	}
    	
	}
	    
	    private TextWatcher filterTextWatcher = new TextWatcher() {

	    	@Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	        }
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	            //adapter.getFilter().filter(s);
	        	m_adapter2.getFilter().filter(s);
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub

	        }

	    };
	    
	    public void onDestroy(){
	    	super.onDestroy();
	    	finish();
	    }
	    
		 public void compare(){ 	/**db의 내용을 가져와서 테이블의 DbAdpter.KEY_MEDIA_DB_ID와 
			  							모든 오디오목록의 Audio.AudioColumns._ID와 비교하여 
			  							중복되는 것이 있으면 리스트뷰에서 그 항목을 클릭 받지 못하게 함.**/  
		  	dba.open();
		  	MediaDBIndex = new ArrayList<String>();	//db에서 DbAdpter.KEY_MEDIA_DB_ID를 저장하는 ArrayList
		  	repetMDBIdx = new ArrayList<String>(); //중복된 id를 저장하는 ArrayList.
			Cursor cur = dba.fetchAllBooks();
			for(int i = 0; i < cur.getCount(); ++i )
			{
				cur.moveToPosition(i);
				MediaDBIndex.add(cur.getString(cur.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID)));
				
				//Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;

		    	//String selection = "( (_ID LIKE ?) )";
		    	//String[] selectionArgs = { strMediaDBIndex };
		    	//String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
		    	
		    	
		    	//Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
			}
			dba.close();
			
			
			for (int j = 0; j < mCursor.getCount(); j++)
			{
				mCursor.moveToPosition(j);	
	            String id = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns._ID)); //모든 오디오목록의 Audio.AudioColumns._ID를 저장하는 string 변수.
				if(MediaDBIndex.contains(id))
				{
					repetMDBIdx.add(id);
					isImported[j] = true;
				}
			}
	  }
	    
	
	
	AdapterView.OnItemClickListener mItemClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
        	Sound s = mFilteredItems.get(position);
        	isChecked[s.getIndex()] = ((ListView)parent).isItemChecked(position);
        	
        	updateAddButton();
        }
        
        private void updateAddButton()
        {
        	// 갯수를 세서 추가 버튼을 켜고끄는 작업을 하는듯.
        	boolean hasChecked = false;
        	for(int i = 0; i < isChecked.length; i++){
	        	if( isChecked[i] )
	        	{
	        		hasChecked = true;
	        		break;
	        	}
        	}

    		btn.setEnabled( hasChecked );

        }
	};
   
	public void OnClick_FilterDelete(View v) {
		mFilterText.setText("");
		// 키보드 감추기
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mFilterText.getWindowToken(), 0);

	}
	
    public void mOnClick2(View v) {
	   switch(v.getId()){
	   case R.id.add:
		   
		 
		// 선택된 음악파일 경로를 ArrayList에 담는다.
			ArrayList<Integer> paths = new ArrayList<Integer>();
			for(int i = 0; i < isChecked.length; ++i )
			{
				if( isChecked[i] )
				{
					mCursor.moveToPosition(i);
		            int id = mCursor.getInt(mCursor.getColumnIndex(Audio.AudioColumns._ID));
					paths.add(id);
				}
			}
			
			
			
			// 로딩 다이얼로그 생성
			importDialog = new ImportProgressDialog(addActivity.this);
			importDialog.setAudioIDs(paths);
			importDialog.setContentResolver(getContentResolver());
			importDialog.show();
			importDialog.beginThread();
			importDialog.setFinishLoading( new ImportProgressDialog.FinishLoading() { 
				private ImportProgressDialog.EFinishResult mResult;
				public void finish( ImportProgressDialog.EFinishResult result ){// dialog가 dismiss 될 때 호출되는 함수.
					mResult = result;
					runOnUiThread( new Runnable(){
	   					public void run()
	   					{
	   						showLoadingResultMessage(mResult);
	   						//refreshListFromDB();
	   						//isDismiss=true;
	   						//Intent it = new Intent(addActivity.this, WaveLoopActivity.class);
	   		   		        //startActivity(it);
	   		   		        addActivity.this.finish();
	   		   		        
	   					}
	   					
					});
					
				}
				
			});
			
			//finish();
			
		   //list2.clearChoices();
           //m_adapter2.notifyDataSetChanged();
           
			
		   break;
	   case R.id.cancel:
		   		//Intent it = new Intent(addActivity.this, WaveLoopActivity.class);
 		        //startActivity(it);
 		        finish();
 		}
	   
   }
   
   /*
   public void onBackPressed() //이 액티비티에서 back키를 눌렀을때 무조건 WaveLoopActivity로 넘어가게 인텐트함. 
   { 
	   //Intent it = new Intent(addActivity.this, WaveLoopActivity.class);
       //startActivity(it);
       finish();
   }
   */
   	
	   private void showLoadingResultMessage( ImportProgressDialog.EFinishResult result )
		{
			String message;
			switch(result)
			{
			case eFR_OK:
				message = "성공적으로 추가되었습니다.";
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

	
	private class SoundAdapter2 extends ArrayAdapter<Sound> implements Filterable {		

		private SoundFilter filter;
		private ArrayList<Sound> allItems;
        

        public SoundAdapter2(Context context, int textViewResourceId, ArrayList<Sound> items) {
                super(context, textViewResourceId, items);
                this.allItems = items;
        }
        /*
        public boolean areAllItemsEnabled() {
            return false;
        }
        */
        public boolean isEnabled(int position) {
        	Sound s = mFilteredItems.get(position);
        	if ( s != null && isImported[s.getIndex()] )	//	position에 따른 조건을 주어 활성화/비활성화가 되게끔 코딩해야함. 
        		return false;	// isRepet가 true이면, 즉 중복되었으면 비활성.
        	else
        		return true;	// 그렇지않으면, 즉 중복되지 않았으면 활성.
        	
        } 
        
        private void setTextViewAlpha( TextView v, boolean isRepeat )
        {
        	int alpha = (isRepeat)?60:255;
        	v.setTextColor(v.getTextColors().withAlpha(alpha));
        }
        public int getCount()
        {
        	return mFilteredItems.size();
        }
        
        // 각 항목의 뷰 생성
        public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row2, null);
                }
                Sound p = mFilteredItems.get(position);
                
                if (p != null) {
	        		TextView tar = (TextView) v.findViewById(R.id.top_artist2);
	                TextView tal = (TextView) v.findViewById(R.id.top_album2);
	                TextView bt = (TextView) v.findViewById(R.id.bottom_title2);
	                TextView rtt = (TextView) v.findViewById(R.id.right_total_time2);
	                
	                CheckBox cb=(CheckBox)v.findViewById(R.id.chk);
	                if (tar != null){
	                	tar.setText(p.getArtist());
	                	setTextViewAlpha(tar, isImported[p.getIndex()]);
	                	
	                }
	                if (tal != null){
	                	tal.setText(p.getAlbum());
	                	setTextViewAlpha(tal, isImported[p.getIndex()]);
	                }
	                if(bt != null){
	                	bt.setText(p.getTitle());
	                	setTextViewAlpha(bt, isImported[p.getIndex()]);
	                    
	                }
	                if(rtt != null){
                		int tot_time_sec = p.getTotalTime();
                		int tMin = tot_time_sec / 60;
        				int tSec = tot_time_sec % 60;
        			    String strTime = String.format("%02d:%02d" , tMin, tSec);
            			rtt.setText(strTime);
            			setTextViewAlpha(rtt, isImported[p.getIndex()]);
                }
	                
	                if(cb != null){
	                	//CheckBox cbx = (CheckBox) v.findViewById(R.id.chk);
	                    cb.setChecked(isChecked[p.getIndex()]);
	                    cb.setEnabled(!isImported[p.getIndex()]);
	                	
	                    
	                }
                     
                }
                
                return v;
        }
        
        @Override
        public Filter getFilter()
        {
            if (filter == null)
            	filter  = new SoundFilter();
            
            return filter;
        }
        
        

        
        private class SoundFilter extends Filter {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence prefix,
                                          FilterResults results) {
                // NOTE: this function is *always* called from the UI thread.
                mFilteredItems = (ArrayList<Sound>)results.values;

                notifyDataSetChanged();
            }

            @SuppressWarnings("unchecked")
            protected FilterResults performFiltering(CharSequence prefix) {
                // NOTE: this function is *always* called from a background thread, and
                // not the UI thread. 
            	
            	String filterExp = ".*" + prefix.toString() + ".*";
                FilterResults results = new FilterResults();

                if ( prefix != null && prefix.toString().length() > 0 ) 
                {
                	ArrayList<Sound> filteredList = new ArrayList<Sound>();
                    for (int i = 0; i < allItems.size(); i++)
                    {
                    	Sound s = allItems.get(i);
                    	String title = s.getTitle().toLowerCase();
                    	String album = s.getAlbum().toLowerCase();
                    	String artist = s.getArtist().toLowerCase();
                    	
                    	if( title.matches(filterExp) ||
                    		album.matches(filterExp) ||
                    		artist.matches(filterExp) )
                        {
                            filteredList.add(s);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                else
                {
                    synchronized (allItems)
                    {
                        results.values = allItems;
                        results.count = allItems.size();
                    }
                }

                return results;
            }
        }   

		
    }
	
    
    // 리스트 뷰에 출력할 항목
    class Sound {	
        
        private String Artist;
        private String Album;
        private String Title;
        private int Time;
        private int Index;
        
        
        public Sound(String _Artist, String _Album, String _Title, int _Time, int _Index) {
        	this.Artist = _Artist;
        	this.Album = _Album;
        	this.Title = _Title;
        	this.Time = _Time;
        	this.Index = _Index;
        }
        
        public String getArtist() {
            return Artist;
        }
        
        public String getAlbum() {
        	return Album;
        }
        public String getTitle() {
            return Title;
        }
        public int getTotalTime(){
    	    return Time;
    	}
        
        public int getIndex(){
    	    return Index;
    	}
        

    }
	
    
    
  
}





