package team.ssm;

import java.util.*;

import team.ssm.DbAdapter.DatabaseHelper;
import team.ssm.ImportProgressDialog.FinishLoading;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.view.*;
import android.widget.*;

public class addActivity extends Activity {
		Dialog dialog;
	    AlertDialog.Builder builder;
	    boolean[] mSelect;
	    
	 
	    Cursor mCursor;
	    ListView list2;
	   	    
	    ArrayList<sound> m_orders2;
	    
	    ArrayAdapter<sound> m_adapter2;
	    String abc;
	    DbAdapter dba;
	    DatabaseHelper dbh;
	    SQLiteDatabase db;
	    
	    ImportProgressDialog importDialog;
	    int mPosition;
	    Button btn;
	    CheckBox cb;
	    
	    private boolean isClick[] ; //체크박스의 체크유무를 저장하는 boolean 배열.
	    ArrayList<String> mFiles;
	    public static final String WAVEPATH = "/data/data/com.androidhuman.app/files/";   
	    private FinishLoading mFinishLoading;
	
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
        list2.setItemsCanFocus(false);
        
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
    		isClick= new boolean[nCurCount];
    		
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
	    
	    
	    
	
	
	AdapterView.OnItemClickListener mItemClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
        	
        	isClick[position] = ((ListView)parent).isItemChecked(position);
        	int j=0;
        	for(int i=0; i<isClick.length; i++){
	        	if(isClick[i]){
	        		j=j+1;
	        		//btn.setEnabled(true);
	        	}
	        	
        	}
        	if(j>0){
        		btn.setEnabled(true);
        	}
        	else{
        		btn.setEnabled(false);
        	}
        	//CheckBox chk=(CheckBox)findViewById(R.id.chk);
        	//isCheck[position] = chk.isChecked();
        
        }
	};
   
	
   public void mOnClick2(View v) {
	   switch(v.getId()){
	   case R.id.add:
		   
		 
		// 선택된 음악파일 경로를 ArrayList에 담는다.
			ArrayList<Integer> paths = new ArrayList<Integer>();
			for(int i = 0; i < isClick.length; ++i )
			{
				if( isClick[i] )
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
			importDialog.setFinishLoading( new ImportProgressDialog.FinishLoading() { 
				private ImportProgressDialog.EFinishResult mResult;
				public void finish( ImportProgressDialog.EFinishResult result ){// dialog가 dismiss 될 때 호출되는 함수.
					mResult = result;
   				runOnUiThread( new Runnable(){
   					public void run()
   					{
   						showLoadingResultMessage(mResult);
   						//refreshListFromDB();
   						Intent i = new Intent(addActivity.this, WaveLoopActivity.class);
   			           startActivity(i);
   					}
   				});

				}
			});
			importDialog.show();
			importDialog.beginThread();
		   
		   //list2.clearChoices();
           //m_adapter2.notifyDataSetChanged();
           //Intent i = new Intent(addActivity.this, WaveLoopActivity.class);
           //startActivity(i);
  
		   break;
	   case R.id.cancel:
		   	
	   		finish();
	   }
	   
   }
   
  
   
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

   
	private class SoundAdapter2 extends ArrayAdapter<sound>{		

        private ArrayList<sound> items;

        public SoundAdapter2(Context context, int textViewResourceId, ArrayList<sound> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        // 각 항목의 뷰 생성
        public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row2, null);
                }
                sound p = items.get(position);
                //final int pos = position; 
                if (p != null) {
                        
                	
                		TextView tar = (TextView) v.findViewById(R.id.top_artist2);
                        TextView tal = (TextView) v.findViewById(R.id.top_album2);
                        TextView bt = (TextView) v.findViewById(R.id.bottom_title2);
                        CheckBox cb=(CheckBox)v.findViewById(R.id.chk);
                        if (tar != null){
                        	tar.setText(p.getArtist());                            
                        }
                        if (tal != null){
                        	tal.setText(p.getAlbum());                            
                        }
                        if(bt != null){
                        		bt.setText(p.getTitle());
                        }
                        if(cb != null){
                        	//CheckBox cbx = (CheckBox) v.findViewById(R.id.chk);
                            cb.setChecked(isClick[position]);
                        	
                            
                        }
                        
                }
            
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
	
    
    
  
}





