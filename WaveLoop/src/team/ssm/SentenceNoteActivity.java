package team.ssm;

import java.util.ArrayList;

import team.ssm.DbAdapter.DatabaseHelper;
import team.ssm.PlaylistActivity.sound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SentenceNoteActivity extends Activity {

	ArrayList<sentence> mArrSentences;
	ArrayAdapter<sentence> mAdapter;
	ListView mListView;
	
	DbAdapter dba;
	DatabaseHelper dbh;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.sentence_note_activity);
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성.
    	dbh = dba.new DatabaseHelper(this);	//오픈헬퍼 객체 생성.
    	
    	
    	mArrSentences = new ArrayList<sentence>();	// 리스트뷰에 출력할 내용의 원본data를 저장하는 arrayList.        
    	
    	mListView = (ListView)findViewById(R.id.sentence_note_list);		// 사용자가 추가한 오디오 파일을 보여주는 리스트뷰
    	mAdapter = new SentencesAdapter(this, R.layout.sentence_note_row, mArrSentences); // 원본; m_orders의 내용을 리스트뷰; list에 연결해주는 어댑터.
    	
    	mListView.setAdapter(mAdapter);	// 어댑터와 리스트뷰를 연결
    	mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    	mListView.setOnItemClickListener(mItemClickListener);	//리스트뷰의 클릭리스너 설정.
        
    	refreshListFromDB();
    	
	}
	
	
	
	public void refreshListFromDB()	// 문장노트 리스트의 내용을 새로고침.
	{
		/*
		mArrSentences.clear();

		dba.open();
		Cursor cur = dba.fetchAllBooks2();
		for(int i = 0; i < cur.getCount(); ++i )
		{
			cur.moveToPosition(i);
			String strMemo = cur.getString(cur.getColumnIndex(DbAdapter.KEY_MEMO));
			String strStartTime = cur.getString(cur.getColumnIndex(DbAdapter.KEY_START_TIME));
			String strFinishTime = cur.getString(cur.getColumnIndex(DbAdapter.KEY_END_TIME));
			
			String strAudioInfo = "[" + strStartTime + ":" + strFinishTime + "]";
			
			sentence s = new sentence(strMemo, strAudioInfo, 0, 0);
			mArrSentences.add(s);
			
		}
		dba.close();
		
       mAdapter.notifyDataSetChanged();
       
       */
	}
  
	public void onResume(){
    	super.onResume();
    	refreshListFromDB();
    }
	
	
	// 어댑터뷰의 클릭리스너
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
        	//dba.open();
        	//int idx = 0;
        	//mPos = position;
        	Intent i = new Intent(PlaylistActivity.this, player_main.class); 
        	i.putExtra("오디오파일경로", position );
        	//i.putExtra("오디오파일경로", path );
        	startActivity(i);
        	*/
            
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
                sentence s = items.get(position);
                
                if (s != null) {
                        TextView memo = (TextView) v.findViewById(R.id.sentence_row_note);
                        TextView audioInfo = (TextView) v.findViewById(R.id.sentence_row_audio_info);
                        //TextView bt = (TextView) v.findViewById(R.id.bottom_title);
                        if (memo != null){
                        	memo.setText(s.getNote());                            
                        }
                        if (audioInfo != null){
                        	audioInfo.setText(s.getAudioInfo());                     
                        }
                        
                }
                return v;
        }
    }

	// 리스트 뷰에 출력할 항목
    public class sentence {	
        
        private String note;
        private String audioInfo;
        private int starRate;
        private int color;
        
        
        public sentence( String note, String audioInfo, int starRate, int color ) {
        	this.note = note;
        	this.audioInfo = audioInfo;
        	this.starRate = starRate;
        	this.color = color;
        }


		public void setNote(String note) {
			this.note = note;
		}


		public String getNote() {
			return note;
		}


		public void setAudioInfo(String audioInfo) {
			this.audioInfo = audioInfo;
		}


		public String getAudioInfo() {
			return audioInfo;
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
        

    }

}
