package team.ssm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SentenceNoteEditActivity extends Activity {

	TextView mTitleView;
	TextView mTimeView;
	EditText mNoteView;
	
	DbAdapter dba;
	int mSentenceRowID;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.sentence_note_edit_activity);
    	
    	mTitleView = (TextView)findViewById(R.id.sne_title);
    	mTimeView = (TextView)findViewById(R.id.sne_time);
    	mNoteView = (EditText)findViewById(R.id.sne_note);
    	
    	dba = new DbAdapter(this); //어댑터 객체 생성. 
    	
    	
    	Intent intent = getIntent();
        if (intent != null)
        {
        	mSentenceRowID = intent.getIntExtra("sentence_row_id", 0);
        	
        	dba.open();
        	Cursor cursor = dba.fetchBook2(mSentenceRowID);
        	cursor.moveToPosition(0);
        	String strMediaDBIndex = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID));
        	String strStartTime = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_START_TIME));
        	String strFinishTime = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_END_TIME));
        	String strNote = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEMO));
        	dba.close();
        	
        	
        	mTimeView.setText( "[" + strStartTime + " - " + strFinishTime + "]" );
        	mNoteView.setText(strNote);
        	
        	

        }
    	
    	
	}
	
	
	public void mOnClickSaveCancel(View v) {
    	
    	switch(v.getId()){   	
    	case R.id.sne_save:
	    	
    		// 여기다가 저장좀 하고.
    		
    		Toast.makeText(getApplicationContext(), "저장 되었습니다.", Toast.LENGTH_SHORT ).show();
    		finish();
	    	break;
	    	
    	case R.id.sne_cancel:
    		
    		
    		finish();
    		break;
    	}
    	
    }


	
}

