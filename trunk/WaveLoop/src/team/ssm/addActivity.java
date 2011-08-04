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
import android.util.*;
import android.view.*;
import android.widget.*;

public class addActivity extends Activity {
		Dialog dialog;
	    AlertDialog.Builder builder;
	    boolean[] mSelect;
	    CharSequence[] mFiles;
	 
	    Cursor mCursor;
	    ListView list2;
	    //ArrayList<String> items;
	    
	    //ArrayList<sound> m_orders;
	    ArrayList<sound> m_orders2;
	    
	    ArrayAdapter<sound> m_adapter2;
	    String abc;
	    DbAdapter dba;
	    DatabaseHelper dbh;
	    SQLiteDatabase db;
	    //int idx;
	    ImportProgressDialog importDialog;
	    int mPosition;
	    Button btn;
	    //CheckBox cb;
	    
	    public static final String WAVEPATH = "/data/data/com.androidhuman.app/files/";   
	
	
	    public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.add_activity);
		
		//team.ssm.WaveLoopActivity.m_orders = m_orders;
				
		dba = new DbAdapter(this); //어댑터 객체 생성.
    	dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
		
    	btn = (Button)findViewById(R.id.add);
		btn.setEnabled(false);
		
        
    	m_orders2 = new ArrayList<sound>();	//리스트뷰에 출력할 항목(sound 객체)들을 저장하는 ArraylList 생성.
        list2 = (ListView)findViewById(R.id.list2);
        m_adapter2 = new SoundAdapter2(this, R.layout.row2, m_orders2); // 어댑터를 생성.
        list2.setAdapter(m_adapter2);
        list2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list2.setOnItemClickListener(mItemClickListener2);
        
        
       // CheckBox chk = (CheckBox)findViewById(R.id.chk);
        //chk.setOnCheckedChangeListener(mCheckChange);
        //cb=(CheckBox)findViewById(R.id.chk);
		//cb.setOnCheckedChangeListener(null);
		
		        
    	
        
    	ContentResolver mCr = getContentResolver();

    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
    	//Uri uriInternal = Audio.Media.INTERNAL_CONTENT_URI;
    	 

    	String selection = "( (_DATA LIKE ?) OR (_DATA LIKE ?) )";
    	String[] selectionArgs = {"%.mp3", "%.wav" };
    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
    	
    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
    	mCursor = mCr.query(uriExternal, null, selection, selectionArgs, sortOrder);
    	
    	//mCursor = new MergeCursor( new Cursor[] { cursorInt, cursorExt} );
    	//mCursor = cursorExt;
    	int nCurCount = (mCursor == null)?0:mCursor.getCount();
    	if(nCurCount > 0){
	    	for (int i = 0; i < nCurCount; i++) {
	    	
	    		mCursor.moveToPosition(i);
		    	String artist = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ARTIST));
				String album = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.ALBUM));
				String title = mCursor.getString(mCursor.getColumnIndex(Audio.AudioColumns.TITLE));
				
				sound s = new sound(artist, album, title);
				m_orders2.add(s);
	    		
	    	}
	    	startManagingCursor(mCursor);
    	}
    	
    	
	}
	    /*
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    		if (isChecked) {
    			btn.setEnabled(true);
    		}
    		else {
    			
    		}
	    }
	     */
	    /*
	 // 어댑터뷰의 클릭리스너
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /****************
			리스트뷰에 나타나는 각각의 오디오 파일을 클릭했을 때 일어나는 동작을 정의하는 부분.
            "파일을 클릭하면 재생 화면으로 넘어간다." 
        	
        	dba.open();
        	//int idx = 0;
        	Intent i = new Intent(addActivity.this, WaveLoopActivity.class); 
        	//i.putExtra("오디오파일경로", position );
        	//i.putExtra("오디오파일경로", path );
        	
        	
        	startActivity(i);
        }
   };
	*/
	
	
	
	
	AdapterView.OnItemClickListener mItemClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
        	//Button btn = (Button)findViewById(R.id.add);
			//btn.setEnabled(true);
        	CheckBox chk = (CheckBox)findViewById(R.id.chk);
        	chk.toggle();   
        	
        }
   };
   
	
   public void mOnClick2(View v) {
	   switch(v.getId()){
	   case R.id.add:
		   
		 /*  
		   SparseBooleanArray sb = list2.getCheckedItemPositions();
		   
			   for (int j =0;  j< list2.getCount();  j++) {
                      
                    	  sound s = m_orders2.get(j);
                    	  String a = s.getArtist();
                    	  Toast.makeText(this,"추가할 파일:", Toast.LENGTH_SHORT).show();
                      
               }
		   
		   */
		   list2.clearChoices();
           m_adapter2.notifyDataSetChanged();
           Intent i = new Intent(addActivity.this, WaveLoopActivity.class);
           startActivity(i);
  
		   
			     		//Intent i = new Intent(addActivity.this, WaveLoopActivity.class); 
				   		//i.putExtra("체크된 항목 인덱스", idx);
				   		//startActivity(i);
				   		
					  // Toast.makeText(v.getContext(), "체크됨", Toast.LENGTH_SHORT).show();
					   
				
		   break;
	   case R.id.cancel:
		   	
	   		finish();
	   }
	   
   }
   
   
   
	private class SoundAdapter2 extends ArrayAdapter<sound>{		

        private ArrayList<sound> items;

        public SoundAdapter2(Context context, int textViewResourceId, ArrayList<sound> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        // 각 항목의 뷰 생성
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row2, null);
                }
                sound p = items.get(position);
                if (p != null) {
                        
                	
                		TextView tar = (TextView) v.findViewById(R.id.top_artist2);
                        TextView tal = (TextView) v.findViewById(R.id.top_album2);
                        TextView bt = (TextView) v.findViewById(R.id.bottom_title2);
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
                
               /* 
               CheckBox cb = (CheckBox)v.findViewById(R.id.chk);
               cb.setOnClickListener(new CheckBox.OnClickListener(){
                	public void onClick(View v){
                		btn.setEnabled(true);
                	}
                });
                */
                
                
        		
        		CheckBox cb=(CheckBox)v.findViewById(R.id.chk);
        		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        		if (isChecked) {
		        			//cb.setText( "체크 상태" )
		        			btn.setEnabled(true);
		        			Toast.makeText(addActivity.this, "체크됨", Toast.LENGTH_SHORT).show();
		        		}
		        		else {
		        			//cb.setText( "체크되지 않은 상태" );
		        			Toast.makeText(addActivity.this, "해제됨", Toast.LENGTH_SHORT).show();
		        		}
        			}
        		});
				
                /*
				CheckBox cb = (CheckBox)v.findViewById(R.id.chk);
				
				if(cb != null){
				cb.setChecked(v.isChecked()); // 체크 유무를 확인하여 체크
				}
                */
                
                return v;
              
                
        }

		
    }
    
    
    // 리스트 뷰에 출력할 항목
    class sound {	
        
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
        
        public String getAlbum() {
        	return Album;
        }
        public String getTitle() {
            return Title;
        }
        

    }
	
    
    
    public void onDestroy(Bundle savedInstanceState) {
		super.onDestroy();
		finish();
	
	}
}
