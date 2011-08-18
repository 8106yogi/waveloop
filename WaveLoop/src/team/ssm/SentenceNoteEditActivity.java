package team.ssm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SentenceNoteEditActivity extends Activity {

	TextView mTitleView;
	TextView mTimeView;
	EditText mNoteView;
	RatingBar mRatingBar;
	
	DbAdapter dba;
	long mSentenceRowIndex;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.sentence_note_edit_activity);
    	
    	mTitleView = (TextView)findViewById(R.id.sne_title);
    	mTimeView = (TextView)findViewById(R.id.sne_time);
    	mNoteView = (EditText)findViewById(R.id.sne_note);
    	mRatingBar = (RatingBar)findViewById(R.id.sne_ratingBar);
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성. 
    	
    	
    	Intent intent = getIntent();
        if (intent != null)
        {
        	mSentenceRowIndex = intent.getIntExtra("sentence_row_id", 0);
        	
        	dba.open();
        	Cursor cursor = dba.fetchBook2(mSentenceRowIndex);
        	cursor.moveToPosition(0);
        	
        	long mediaDBIndex = cursor.getLong(cursor.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID));
        	String strStartTime = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_START_TIME));
        	String strFinishTime = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_END_TIME));
        	String strNote = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEMO));
        	int rate = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_STAR_RATE));
        	int color = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_COLOR));
        	dba.close();
        	
        	String title = "";
			
			Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;

	    	String selection = "( (_ID LIKE ?) )";
	    	String[] selectionArgs = { String.valueOf(mediaDBIndex) };
	    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
	    	 
	    	
	    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
	    	Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
	    	if(curMedia.getCount() == 1)
	    	{
	    		curMedia.moveToPosition(0);
		    	title = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.TITLE));
	    	}

        	
        	mTitleView.setText(title);
        	mTimeView.setText( "[" + strStartTime + " - " + strFinishTime + "]" );
        	mNoteView.setText(strNote);
        	
        	
        	mRatingBar.setRating(rate/2.f);
        	
        	
        	

        }
    	
    	
	}
	
	
	public void mOnClickSaveCancel(View v) {
    	
    	switch(v.getId()){   	
    	case R.id.sne_save:
	    	
    		// 여기다가 저장좀 하고.
    		dba.open();
    		dba.updateBook2(mSentenceRowIndex, mNoteView.getText().toString(), String.valueOf((int)(mRatingBar.getRating()*2.f)) , "0");
    		dba.close();
    		
    		
    		Toast.makeText(getApplicationContext(), "저장 되었습니다.", Toast.LENGTH_SHORT ).show();
    		finish();
	    	break;
	    	
    	case R.id.sne_cancel:
    		
    		
    		finish();
    		break;
    	}
    	
    }


	
}

