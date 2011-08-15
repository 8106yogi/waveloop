package team.ssm;

import java.io.File;
import java.util.ArrayList;

import team.ssm.DbAdapter.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PlaylistActivity extends Activity {


    Dialog dialog;
    AlertDialog.Builder builder;
    boolean[] mSelect;
    CharSequence[] mFiles;
 
    Cursor mCursor;
    ListView list;
    //ArrayList<String> items;
    ArrayList<sound> m_orders;
    //ArrayList<sound> m_ReverseOrders;	//비어있는 sort용 ArrayList
    //ArrayAdapter<String> Adapter;
    ArrayAdapter<sound> m_adapter;
    String abc;
    DbAdapter dba;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    //int idx;
    ImportProgressDialog importDialog;
    int pos;
    
    public static final String WAVEPATH = "/data/data/com.androidhuman.app/files/";   
    //public static final String ORDER_ASC = "title_key asc";
    
    
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	
    	
    	setContentView(R.layout.playlist_activity);
    	
    	
    	
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성.
    	dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
    	//Items = new ArrayList<String>();
    	
    	
        /***** 20100725_동진: Custom ArrayAdapter를 이용한 ListView*****/
        
    	m_orders = new ArrayList<sound>();	// 리스트뷰에 출력할 내용의 원본data를 저장하는 arrayList.        
    	//m_ReverseOrders = new ArrayList<sound>(); //m_orders의 역순으로 데이터를 저장할 ArrayList
    	list = (ListView)findViewById(R.id.list);		// 사용자가 추가한 오디오 파일을 보여주는 리스트뷰
      
    	m_adapter = new SoundAdapter(this, R.layout.row, m_orders); // 원본; m_orders의 내용을 리스트뷰; list에 연결해주는 어댑터.
    	//m_adapter = new SoundAdapter(this, R.layout.row, m_ReverseOrders);
        
        list.setAdapter(m_adapter);	// 어댑터와 리스트뷰를 연결
        list.setChoiceMode(ListView.CHOICE_MODE_NONE);
        list.setOnItemClickListener(mItemClickListener);	//리스트뷰의 클릭리스너 설정.
        //list.setItemsCanFocus(true);
      
        
      //Comparator 를 만든다.
       /*
        final Comparator<sound> myComparator= new Comparator<sound>() {
            private final Collator   collator = Collator.getInstance();
      public int compare(sound object1,sound object2) {
       return collator.compare(object1.getData(), object2.getData());
      }
     };
        */
        registerForContextMenu(list);
        
        
    	refreshListFromDB();
    }
    
    
    
    
    
    
    public void onCreateContextMenu (ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
    		super.onCreateContextMenu(menu, v, menuInfo);
    		
    		AdapterContextMenuInfo info =(AdapterContextMenuInfo) menuInfo;
	      	pos  = info.position; 
	    	sound p = m_orders.get(pos);
	    	String HeaderTitle=p.getTitle();
	    	menu.setHeaderTitle(HeaderTitle);
	        menu.add(0,1,0,"재생");
	        menu.add(0,2,0,"목록에서 제거");
	        
	      

    }
    
    public boolean onContextItemSelected (MenuItem item) {
        switch (item.getItemId()) {
        case 1:	//재생
        	   	Intent i = new Intent(PlaylistActivity.this, player_main.class); 
        		i.putExtra("오디오파일경로", pos );
        		startActivity(i);
              return true;
        case 2:	//목록에서 제거        	
        	sound p = m_orders.get(pos);
	    	String HeaderTitle2=p.getTitle();
        	new AlertDialog.Builder(PlaylistActivity.this)
        	.setTitle(HeaderTitle2 +" 파일을 목록에서 제거하시겠습니까?")
        	.setIcon(android.R.drawable.ic_dialog_alert)
        	.setCancelable(false)
        	.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
        		public void onClick(DialogInterface dialog, int which){
        			// DB에 접근하여 파일패스를 얻어내고
        			dba.open();
        			Cursor cursor = dba.fetchAllBooks();
                	cursor.moveToPosition(pos);
                	String mWavepath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_WAVEPATH));
        			String m_db_id = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID));     
        			File file = new File(mWavepath);
        			
        			if(file.delete()){
        			
        				Toast.makeText(PlaylistActivity.this,"파일이 제거되었습니다.",Toast.LENGTH_SHORT).show();
        			
	        			
	        			dba.deleteBook(m_db_id);
	        			//dba.dropTable();
	        			//dba.createTable();
	        			
	        		}
        			dba.close();
        			refreshListFromDB();
        			
        		}
        	} )
        	.setNegativeButton("취소", null)
        	.show();
        	
        		
        	
              return true;
        
        }
       
        return true;
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
        	//mPos = position;
        	Intent i = new Intent(PlaylistActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", position );
        	//i.putExtra("오디오파일경로", path );
        	startActivity(i);
        	
            
        }
   };
   
   
	public void refreshListFromDB()	// 음악 리스트의 내용을 새로고침.
	{
		// DB의 내용을 가져다가 m_orders에 새로 입력.
		m_orders.clear();
		//m_ReverseOrders.clear();
		
		dba.open();
		//dba.exe
		
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
  
//	어댑터 클래스
    public class SoundAdapter extends ArrayAdapter<sound> {		

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
        item.setIcon(android.R.drawable.ic_menu_help);
        menu.add(0,2,0,"추가").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0,3,0,"전체삭제").setIcon(android.R.drawable.ic_menu_delete);
        
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
	        
	        	new AlertDialog.Builder(PlaylistActivity.this)
	        	.setTitle("모든 목록을 삭제하시겠습니까?")
	        	.setIcon(android.R.drawable.ic_dialog_alert)
	        	.setCancelable(false)
	        	.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
	        		public void onClick(DialogInterface dialog, int which){
	        			// 조낸 삭제 코드 작성.
	        			// 모든 파일을 삭제하고
	        			File[] fileList = getFilesDir().listFiles();
	        			for ( int i = 0; i < fileList.length; ++i )
	        			{
	        	            if (fileList[i].isFile())
	        	                fileList[i].delete();
	        	        }
	        			
	        			// DB를 초기화
	        			dba.open();
	        			dba.dropTable();
	        			dba.createTable();
	        			dba.close();
	        			
	        			refreshListFromDB();
	        			
	        			Toast.makeText(PlaylistActivity.this,"모두 삭제되었습니다.",Toast.LENGTH_SHORT).show();
	        			
	        		}
	        	} )
	        	.setNegativeButton("취소", null)
	        	.show();
	        	
	        	
	        }
	        return true;
        }
        return false;
    }
    
    public void onResume(){
    	super.onResume();
    	refreshListFromDB();
    }
    
    public void mOnClick(View v) {
    	
    	switch(v.getId()){   	
    	case R.id.AddButton:
    	
    	Intent i = new Intent(PlaylistActivity.this, addActivity.class); 
    	startActivity(i);
    	break;
    	}
    	
    }
   
}
