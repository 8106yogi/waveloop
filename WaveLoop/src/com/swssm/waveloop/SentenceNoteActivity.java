package com.swssm.waveloop;

import java.io.File;
import java.util.ArrayList;

import com.swssm.waveloop.R;
import com.swssm.waveloop.DbAdapter.DatabaseHelper;
import com.swssm.waveloop.PlaylistActivity.sound;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SentenceNoteActivity extends Activity {

	ArrayList<sentence> mArrSentences;
	ArrayAdapter<sentence> mAdapter;
	ListView mListView;
	int mSelectedRowID;
	//int pos;
	DbAdapter dba;
	//DatabaseHelper dbh;
	//int mSelectedRowID;
	sentence mSelectedSentence;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.sentence_note_activity);
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성.
    	//dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
    	
    	
    	mArrSentences = new ArrayList<sentence>();	// 리스트뷰에 출력할 내용의 원본data를 저장하는 arrayList.        
    	
    	mListView = (ListView)findViewById(R.id.sentence_note_list);		// 사용자가 추가한 오디오 파일을 보여주는 리스트뷰
    	mAdapter = new SentencesAdapter(this, R.layout.sentence_note_row, mArrSentences); // 원본; m_orders의 내용을 리스트뷰; list에 연결해주는 어댑터.
    	
    	mListView.setAdapter(mAdapter);	// 어댑터와 리스트뷰를 연결
    	mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    	mListView.setOnItemClickListener(mItemClickListener);	//리스트뷰의 클릭리스너 설정.
    	mListView.setItemsCanFocus(true);
    	
    	registerForContextMenu(mListView);
    	
    	refreshListFromDB();
    	
	}
	
	
	
	public void onCreateContextMenu (ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
		
		
    		super.onCreateContextMenu(menu, v, menuInfo);
    		
    		AdapterContextMenuInfo info =(AdapterContextMenuInfo) menuInfo;
    		int pos = info.position; 
	    	//sound p = m_orders.get(mSelectedRowID);
    		mSelectedSentence = mArrSentences.get(pos);
	    	//mSelectedRowID = mSelectedSentence.getId();
	      	String HeaderTitle = mSelectedSentence.getTitle() + "\n" + mSelectedSentence.getTime();
	    	menu.setHeaderTitle(HeaderTitle);
	        
	    	menu.add(0,1,0,"재생");
	        menu.add(0,2,0,"편집");
	        menu.add(0,3,0,"삭제");
     

    }
	
	
    public boolean onContextItemSelected (MenuItem item) {
        switch (item.getItemId()) {
        case 1:	//재생
        {
        	//int rowID = mArrSentences.get(pos).id;
        	int mediaDBIndex = getMediaDBIndex( mSelectedSentence.getId() );
        	int dataRowID = getDataIndexFromMediaIndex(mediaDBIndex);
        	
        	Intent i = new Intent(SentenceNoteActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", dataRowID );
        	i.putExtra("start_segment_index", mSelectedSentence.getStartIndex() );
        	
        	startActivity(i);
        }
        	
        	break;
        case 2:	//편집
        {
        	// 문장노트 수정 액티비티를 시작한다.
        	Intent i = new Intent(SentenceNoteActivity.this, SentenceNoteEditActivity.class); 
    		i.putExtra("sentence_row_id", mSelectedSentence.getId() );
    		startActivity(i);
        }
        	
        	break;
        case 3:	//삭제   
        	
        	//sentence se =mArrSentences.get(pos);
        	String HeaderTitle2 = mSelectedSentence.getTitle() + "\n" + mSelectedSentence.getTime();
        	new AlertDialog.Builder(SentenceNoteActivity.this)
        	.setTitle("문장노트를 목록에서 삭제하시겠습니까?")
        	.setMessage(HeaderTitle2)
        	.setIcon(android.R.drawable.ic_dialog_alert)
        	.setCancelable(false)
        	.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
        		public void onClick(DialogInterface dialog, int which){
        			// DB에 접근하여 파일패스를 얻어내고
        			dba.open();
        			//Cursor cursor = dba.fetchAllBooks2();
                	//cursor.moveToPosition(pos);
                	
        			//int rowId = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID2));     
        				
	        		//dba.deleteBook2(rowId);
        			dba.deleteBook2( mSelectedSentence.getId() );
	        			
	        		Toast.makeText(SentenceNoteActivity.this,"문장노트가 삭제되었습니다.",Toast.LENGTH_SHORT).show();	
	        		
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
	
	
	public void refreshListFromDB()	// 문장노트 리스트의 내용을 새로고침.
	{
		
		mArrSentences.clear();

		dba.open();
		//dba.dropTable2();
		//dba.createTable2();
		
		Cursor cur2= dba.fetchAllBooks2();	// sentence 테이블의 커서
		for(int i = 0; i < cur2.getCount(); ++i )
		{
			cur2.moveToPosition(i);
			int rowIndex = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_ROWID2));
			String strMemo = cur2.getString(cur2.getColumnIndex(DbAdapter.KEY_MEMO));
			String strStartTime = cur2.getString(cur2.getColumnIndex(DbAdapter.KEY_START_TIME));
			String strFinishTime = cur2.getString(cur2.getColumnIndex(DbAdapter.KEY_END_TIME));
			long sentence_mdb_id = cur2.getLong(cur2.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID));
			int rate = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_STAR_RATE));
			int startIndex = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_START_ID));
			int finishIndex = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_END_ID));
			int noteColor= cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_COLOR));
			
			String title = "";
			
			
			Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
			String selection = "( (_ID LIKE ?) )";
	    	String[] selectionArgs = { String.valueOf(sentence_mdb_id) };
	    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
	    		    	
	    	Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
	    	if(curMedia != null)
	    	{
	    		if(curMedia.getCount() == 1)
		    	{
		    		curMedia.moveToPosition(0);
			    	title = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.TITLE));
		    	}
	    		curMedia.close();
	    	}
	    	
			
			String time ="[" + strStartTime + " - " + strFinishTime + "]";
			
			sentence s = new sentence(rowIndex, strMemo, title,
					startIndex, finishIndex, time, rate, noteColor);
			mArrSentences.add(s);
			
			
		}
		
		
		dba.close();
		
       mAdapter.notifyDataSetChanged();
       
       
	}
  
	public void onResume() {
    	super.onResume();
    	refreshListFromDB();
    }
	
	public void onDestroy() {
		super.onDestroy();
		dba.close();
	}
	 
	@Override 
	public void onBackPressed()
	{
		this.getParent().onBackPressed();
	}
	
	private int getMediaDBIndex( int sentenceRowId )
 	{
 		dba.open();
     	Cursor cursor = dba.fetchBook2( sentenceRowId );
     	cursor.moveToPosition(0);
     	int mediaDBIndex = cursor.getInt( cursor.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID) );
     	dba.close();
     	return mediaDBIndex;
 	}
 	
 	private int getDataIndexFromMediaIndex( int mediaIndex )
 	{
 		dba.open();
     	Cursor cursor = dba.fetchBookFromMediaID( mediaIndex );
     	cursor.moveToPosition(0);
     	int rowID = cursor.getInt( cursor.getColumnIndex(DbAdapter.KEY_ROWID) );
     	dba.close();
     	return rowID;
 	}
 	
 	
	// 어댑터뷰의 클릭리스너
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
        	int rowID = mArrSentences.get(position).id;
        	int mediaDBIndex = getMediaDBIndex(rowID);
        	int dataRowID = getDataIndexFromMediaIndex(mediaDBIndex);
        	
        	Intent i = new Intent(SentenceNoteActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", dataRowID );
        	startActivity(i);
        	*/
        	// 문장노트 수정 액티비티를 시작한다.
        	int rowID = mArrSentences.get(position).id;
        	Intent i = new Intent(SentenceNoteActivity.this, SentenceNoteEditActivity.class); 
    		i.putExtra("sentence_row_id", rowID );
    		startActivity(i);
        	
        }
   };
	
	
	//	어댑터 클래스
    public class SentencesAdapter extends ArrayAdapter<sentence> {		

        private ArrayList<sentence> items;

        public SentencesAdapter(Context context, int textViewResourceId, ArrayList<sentence> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        // 각 항목의 뷰 생성
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.sentence_note_row, null);
                }
                final sentence s = items.get(position);
                
                if (s != null) {
                    TextView memo = (TextView) v.findViewById(R.id.sentence_row_note);
                    TextView title = (TextView) v.findViewById(R.id.sentence_row_audio_title);
                    TextView time = (TextView) v.findViewById(R.id.sentence_row_audio_time);
                    ImageButton editButton = (ImageButton) v.findViewById(R.id.edit_sentence_note_button);
                    RatingBar rate = (RatingBar) v.findViewById(R.id.sn_ratingBar);
                    LinearLayout ll = (LinearLayout) v.findViewById(R.id.leftside_color);  
                    
                    if(s.getNote().equals("")){
                    	memo.setText("문장노트메모");
                    }
                    else{
                    	memo.setText(s.getNote());
                    }
                    title.setText(s.getTitle());
                    time.setText(s.getTime());
                    rate.setRating(s.starRate/2.f);
                    int color = s.getColor(); //0xffffff63
                    int a = color & 0x00ffffff; 
                    int b = a | 0x77000000;
                    ll.setBackgroundColor(color);
                    v.setBackgroundColor(b);
                    
                    
                    editButton.setFocusable(false);
                    editButton.setOnClickListener( new View.OnClickListener() {
						public void onClick(View v) {
							//mSelectedRowID = s.getId();
							//mSelectedSentence = s;
							//openContextMenu(v);
							
				        	int mediaDBIndex = getMediaDBIndex( s.getId() );
				        	int dataRowID = getDataIndexFromMediaIndex(mediaDBIndex);
				        	
				        	Intent i = new Intent(SentenceNoteActivity.this, player_main.class); 
				        	i.putExtra("오디오파일경로", dataRowID );
				        	i.putExtra("play_from_note", true);
				        	i.putExtra("start_segment_index", s.getStartIndex() );
				        	i.putExtra("end_segment_index", s.getStartIndex() );
				        	startActivity(i);
							
						}
                    });
                        
                }
                return v;
        }
    }

	// 리스트 뷰에 출력할 항목
    public class sentence {	
        
    	private int id;
        private String note;
        private String title;
        private String time;
        private int starRate;
        private int color;
        private int startIndex;
        private int finishIndex;
        
        
        public sentence(
        		int id, String note, String title, int startIndex, int finishIndex, 
        		String time, int starRate, int color ) {
        	this.setId(id);
        	this.note = note;
        	this.setTitle(title);
        	this.setTime(time);
        	this.starRate = starRate;
        	this.color = color;
        	this.startIndex = startIndex;
        	this.finishIndex = finishIndex;
        }


		public void setNote(String note) {
			this.note = note;
		}


		public String getNote() {
			return note;
		}


		

		public void setStarRate(int starRate) {
			this.starRate = starRate;
		}


		public int getStarRate() {
			return starRate;
		}


		public void setColor(int color) {
			this.color = color;
		}


		public int getColor() {
			return color;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public String getTitle() {
			return title;
		}


		public void setTime(String time) {
			this.time = time;
		}


		public String getTime() {
			return time;
		}


		public void setId(int id) {
			this.id = id;
		}


		public int getId() {
			return id;
		}


		public void setStartIndex(int startIndex) {
			this.startIndex = startIndex;
		}


		public int getStartIndex() {
			return startIndex;
		}


		public void setFinishIndex(int finishIndex) {
			this.finishIndex = finishIndex;
		}


		public int getFinishIndex() {
			return finishIndex;
		}
        

    }

}
